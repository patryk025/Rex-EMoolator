package pl.genschu.bloomooemulator;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import pl.genschu.bloomooemulator.adapters.PatchListAdapter;
import pl.genschu.bloomooemulator.logic.AppPaths;
import pl.genschu.bloomooemulator.logic.GameEntry;
import pl.genschu.bloomooemulator.logic.GameManager;
import pl.genschu.bloomooemulator.patch.PatchCatalog;
import pl.genschu.bloomooemulator.patch.PatchIssue;
import pl.genschu.bloomooemulator.patch.PatchManagerController;
import pl.genschu.bloomooemulator.patch.PatchRowVM;

/**
 * Native per-game patch manager — the Android counterpart of the Swing
 * {@code Dialogs.showPatchesDialog}. The UI-agnostic logic lives in
 * {@link PatchManagerController}; this activity only renders rows and forwards
 * user actions.
 *
 * <p>Runs in a plain {@link AppCompatActivity} with no initialised libGDX, so the
 * bundled catalog is read straight from the APK assets via {@code AssetManager}
 * (the classpath/{@code Gdx.files} path {@link PatchCatalog#loadBundled()} relies
 * on is unavailable here). Patches install under {@code getFilesDir()/patches},
 * the same location {@code Game.mountPatches} reads on Android. Network and disk
 * work runs on a background {@link ExecutorService}; results post back via
 * {@link #runOnUiThread}.
 */
public class PatchManagerActivity extends AppCompatActivity {

    private static final int MENU_INSTALL = 1;
    private static final int MENU_TOGGLE = 2;
    private static final int MENU_UP = 3;
    private static final int MENU_DOWN = 4;
    private static final int MENU_UNINSTALL = 5;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private PatchManagerController controller;
    private PatchListAdapter adapter;
    private TextView notesText;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patch_manager);

        GameEntry game = (GameEntry) getIntent().getSerializableExtra("game");
        if (game == null) {
            finish();
            return;
        }

        // Backfill the engine hash for legacy entries and persist it, mirroring the desktop dialog.
        if (game.ensureDllHash()) {
            new GameManager(getFilesDir().getAbsolutePath()).updateGame(game);
        }
        String hash = game.getDllHash();
        if (hash == null || hash.trim().isEmpty()) {
            Toast.makeText(this, "Nie udało się ustalić wersji silnika gry.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        String family = game.resolveFamily();

        File patchesRoot = new File(getFilesDir(), "patches");
        controller = new PatchManagerController(hash, family, patchesRoot,
                AppPaths.patchesIndexFileIn(patchesRoot), loadBundledCatalog());

        setTitle(getString(R.string.patch_manager_title) + ": " + game.getName());

        TextView headerText = findViewById(R.id.headerText);
        TextView subHeaderText = findViewById(R.id.subHeaderText);
        notesText = findViewById(R.id.notesText);
        progressBar = findViewById(R.id.progressBar);

        headerText.setText(game.getName());
        subHeaderText.setText("Silnik: " + shortHash(hash)
                + (family != null ? "  •  Rodzina: " + family : ""));

        RecyclerView recyclerView = findViewById(R.id.patchRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PatchListAdapter(this::showMenu);
        recyclerView.setAdapter(adapter);

        rebuild();

        // Merge the remote catalog off the main thread, then re-render.
        showIndeterminate();
        executor.execute(() -> {
            controller.refreshRemote();
            runOnUiThread(() -> {
                hideProgress();
                rebuild();
            });
        });
    }

    @Override
    protected void onDestroy() {
        executor.shutdownNow();
        super.onDestroy();
    }

    private void showMenu(View anchor, PatchRowVM row) {
        PopupMenu popup = new PopupMenu(this, anchor);
        Menu menu = popup.getMenu();
        if (!row.isInstalled()) {
            if (row.isDownloadable()) {
                menu.add(0, MENU_INSTALL, 0, "Zainstaluj");
            }
        } else {
            menu.add(0, MENU_TOGGLE, 0, row.isEnabled() ? "Wyłącz" : "Włącz");
            menu.add(0, MENU_UP, 1, "Przesuń w górę");
            menu.add(0, MENU_DOWN, 2, "Przesuń w dół");
            menu.add(0, MENU_UNINSTALL, 3, "Odinstaluj");
        }
        if (menu.size() == 0) {
            Toast.makeText(this, "Brak dostępnych akcji dla tej łatki.", Toast.LENGTH_SHORT).show();
            return;
        }
        popup.setOnMenuItemClickListener(item -> handleMenu(row, item.getItemId()));
        popup.show();
    }

    private boolean handleMenu(PatchRowVM row, int itemId) {
        switch (itemId) {
            case MENU_INSTALL:
                install(row);
                return true;
            case MENU_TOGGLE:
                controller.toggle(row.getId());
                rebuild();
                return true;
            case MENU_UP:
                if (controller.move(row.getId(), -1)) {
                    rebuild();
                }
                return true;
            case MENU_DOWN:
                if (controller.move(row.getId(), 1)) {
                    rebuild();
                }
                return true;
            case MENU_UNINSTALL:
                confirmUninstall(row);
                return true;
            default:
                return false;
        }
    }

    private void confirmUninstall(PatchRowVM row) {
        new AlertDialog.Builder(this)
                .setTitle("Odinstaluj")
                .setMessage("Czy odinstalować łatkę „" + row.getDisplayName() + "”?")
                .setPositiveButton("Tak", (dialog, which) -> {
                    controller.uninstall(row.getId());
                    rebuild();
                })
                .setNegativeButton("Nie", null)
                .show();
    }

    private void install(PatchRowVM row) {
        showProgress(0);
        final String patchId = row.getId();
        // Throttle UI posts: only when the whole-percent changes, and a single switch
        // to indeterminate when the server omits Content-Length.
        final int[] lastPct = {-1};
        executor.execute(() -> {
            String error = null;
            try {
                controller.install(patchId, (bytesRead, totalBytes) -> {
                    if (totalBytes > 0) {
                        int pct = (int) (bytesRead * 100 / totalBytes);
                        if (pct != lastPct[0]) {
                            lastPct[0] = pct;
                            runOnUiThread(() -> showProgress(pct));
                        }
                    } else if (lastPct[0] != -2) {
                        lastPct[0] = -2;
                        runOnUiThread(this::showIndeterminate);
                    }
                });
            } catch (Exception ex) {
                error = ex.getMessage();
            }
            final String err = error;
            runOnUiThread(() -> {
                hideProgress();
                if (err != null) {
                    Toast.makeText(this, "Instalacja nie powiodła się: " + err, Toast.LENGTH_LONG).show();
                }
                rebuild();
            });
        });
    }

    /** Re-queries the controller and refreshes the list + validation notes. Call on the UI thread. */
    private void rebuild() {
        List<PatchRowVM> rows = controller.rows();
        adapter.setRows(rows);
        StringBuilder sb = new StringBuilder();
        if (rows.isEmpty()) {
            sb.append("Brak łatek pasujących do tej gry.");
        } else {
            for (PatchIssue issue : controller.issues()) {
                sb.append("• [").append(issue.getSeverity()).append("] ").append(issue.getMessage()).append('\n');
            }
        }
        notesText.setText(sb.toString());
    }

    /** Determinate download progress (0–100%). */
    private void showProgress(int percent) {
        progressBar.setIndeterminate(false);
        progressBar.setMax(100);
        progressBar.setProgress(percent);
        progressBar.setVisibility(View.VISIBLE);
    }

    /** Indeterminate spinner — for the remote refresh and downloads of unknown size. */
    private void showIndeterminate() {
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }

    private PatchCatalog loadBundledCatalog() {
        try (InputStream in = getAssets().open("patch_catalog.json")) {
            return PatchCatalog.fromJson(readAll(in));
        } catch (IOException e) {
            return new PatchCatalog();
        }
    }

    private static String shortHash(String hash) {
        return hash.length() > 8 ? hash.substring(0, 8) + "…" : hash;
    }

    private static String readAll(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
        return new String(out.toByteArray(), StandardCharsets.UTF_8);
    }
}
