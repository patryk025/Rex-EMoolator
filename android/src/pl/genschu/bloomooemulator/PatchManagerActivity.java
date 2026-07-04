package pl.genschu.bloomooemulator;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import pl.genschu.bloomooemulator.adapters.PatchListAdapter;
import pl.genschu.bloomooemulator.logic.AppPaths;
import pl.genschu.bloomooemulator.logic.GameEntry;
import pl.genschu.bloomooemulator.logic.GameManager;
import pl.genschu.bloomooemulator.patch.InstalledPatch;
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
    private ActivityResultLauncher<String[]> patchArchivePicker;
    private ActivityResultLauncher<Uri> patchFolderPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patch_manager);
        patchArchivePicker = registerForActivityResult(
                new ActivityResultContracts.OpenDocument(), this::importLocalArchive);
        patchFolderPicker = registerForActivityResult(
                new ActivityResultContracts.OpenDocumentTree(), this::linkLocalFolder);

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
            Toast.makeText(this, getString(R.string.patch_engine_hash_failed), Toast.LENGTH_LONG).show();
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
        String subHeader = getString(R.string.patch_engine_label, shortHash(hash));
        if (family != null) {
            subHeader += getString(R.string.patch_family_suffix, family);
        }
        subHeaderText.setText(subHeader);

        Button importPatchButton = findViewById(R.id.importPatchButton);
        importPatchButton.setOnClickListener(v -> patchArchivePicker.launch(new String[]{
                "application/zip",
                "application/x-zip-compressed",
                "application/x-rar-compressed",
                "application/vnd.rar",
                "application/octet-stream",
                "*/*"
        }));

        Button linkPatchFolderButton = findViewById(R.id.linkPatchFolderButton);
        linkPatchFolderButton.setOnClickListener(v -> patchFolderPicker.launch(null));

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

    private void importLocalArchive(Uri uri) {
        if (uri == null) {
            return;
        }
        showIndeterminate();
        executor.execute(() -> {
            String error = null;
            File archive = null;
            try {
                archive = copyUriToTempFile(uri);
                controller.importLocalArchive(archive);
            } catch (Exception ex) {
                error = ex.getMessage();
            } finally {
                if (archive != null) {
                    //noinspection ResultOfMethodCallIgnored
                    archive.delete();
                }
            }
            final String err = error;
            runOnUiThread(() -> {
                hideProgress();
                if (err != null) {
                    Toast.makeText(this, getString(R.string.patch_import_failed, err), Toast.LENGTH_LONG).show();
                }
                rebuild();
            });
        });
    }

    private void linkLocalFolder(Uri uri) {
        if (uri == null) {
            return;
        }
        showIndeterminate();
        executor.execute(() -> {
            String error = null;
            try {
                try {
                    getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                } catch (SecurityException ignored) {
                    // The picker grant is enough for the immediate copy pass.
                }
                File mirror = mirrorDirFor(uri);
                deleteRecursively(mirror);
                if (!mirror.mkdirs() && !mirror.isDirectory()) {
                    throw new IOException("Cannot create " + mirror);
                }
                copyTreeUriToDirectory(uri, mirror);
                InstalledPatch linked = controller.linkLocalFolder(mirror);
                if (linked.getManifest().getId() == null || linked.getManifest().getId().trim().isEmpty()) {
                    throw new IOException("Linked patch has no id");
                }
            } catch (Exception ex) {
                error = ex.getMessage();
            }
            final String err = error;
            runOnUiThread(() -> {
                hideProgress();
                if (err != null) {
                    Toast.makeText(this, getString(R.string.patch_link_failed, err), Toast.LENGTH_LONG).show();
                }
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
                menu.add(0, MENU_INSTALL, 0, getString(R.string.patch_action_install));
            }
        } else {
            menu.add(0, MENU_TOGGLE, 0, getString(row.isEnabled()
                    ? R.string.patch_action_disable : R.string.patch_action_enable));
            menu.add(0, MENU_UP, 1, getString(R.string.patch_action_move_up));
            menu.add(0, MENU_DOWN, 2, getString(R.string.patch_action_move_down));
            menu.add(0, MENU_UNINSTALL, 3, getString(R.string.patch_action_uninstall));
        }
        if (menu.size() == 0) {
            Toast.makeText(this, getString(R.string.patch_no_actions), Toast.LENGTH_SHORT).show();
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
                .setTitle(getString(R.string.patch_action_uninstall))
                .setMessage(getString(row.isLinkedLocal()
                        ? R.string.patch_unlink_message
                        : R.string.patch_uninstall_message, row.getDisplayName()))
                .setPositiveButton(getString(R.string.common_yes), (dialog, which) -> {
                    controller.uninstall(row.getId());
                    rebuild();
                })
                .setNegativeButton(getString(R.string.common_no), null)
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
                    Toast.makeText(this, getString(R.string.patch_install_failed, err), Toast.LENGTH_LONG).show();
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
            sb.append(getString(R.string.patch_none_relevant));
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

    private File copyUriToTempFile(Uri uri) throws IOException {
        File archive = File.createTempFile("patch-import-", ".archive", getCacheDir());
        try (InputStream in = getContentResolver().openInputStream(uri);
             OutputStream out = new FileOutputStream(archive)) {
            if (in == null) {
                throw new IOException("Cannot open selected file");
            }
            copy(in, out);
        }
        return archive;
    }

    private File mirrorDirFor(Uri uri) {
        File root = new File(getFilesDir(), "patch-work");
        String gameHash = controller.getGameHash();
        String hashPrefix = gameHash.length() > 8
                ? gameHash.substring(0, 8).toLowerCase(Locale.ROOT)
                : gameHash.toLowerCase(Locale.ROOT);
        String uriKey = Integer.toUnsignedString(uri.toString().hashCode(), 36);
        return new File(new File(root, hashPrefix), uriKey);
    }

    private void copyTreeUriToDirectory(Uri treeUri, File targetDir) throws IOException {
        String rootDocumentId = DocumentsContract.getTreeDocumentId(treeUri);
        copyDocumentChildren(treeUri, rootDocumentId, targetDir);
    }

    private void copyDocumentChildren(Uri treeUri, String documentId, File targetDir) throws IOException {
        Uri childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(treeUri, documentId);
        String[] projection = {
                DocumentsContract.Document.COLUMN_DOCUMENT_ID,
                DocumentsContract.Document.COLUMN_DISPLAY_NAME,
                DocumentsContract.Document.COLUMN_MIME_TYPE
        };
        try (Cursor cursor = getContentResolver().query(childrenUri, projection, null, null, null)) {
            if (cursor == null) {
                throw new IOException("Cannot list selected folder");
            }
            int idColumn = cursor.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_DOCUMENT_ID);
            int nameColumn = cursor.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_DISPLAY_NAME);
            int typeColumn = cursor.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_MIME_TYPE);
            while (cursor.moveToNext()) {
                String childId = cursor.getString(idColumn);
                String name = safeFileName(cursor.getString(nameColumn));
                String mimeType = cursor.getString(typeColumn);
                if (name == null || name.isBlank()) {
                    continue;
                }
                File target = new File(targetDir, name);
                if (DocumentsContract.Document.MIME_TYPE_DIR.equals(mimeType)) {
                    if (!target.mkdirs() && !target.isDirectory()) {
                        throw new IOException("Cannot create " + target);
                    }
                    copyDocumentChildren(treeUri, childId, target);
                } else {
                    Uri documentUri = DocumentsContract.buildDocumentUriUsingTree(treeUri, childId);
                    try (InputStream in = getContentResolver().openInputStream(documentUri);
                         OutputStream out = new FileOutputStream(target)) {
                        if (in == null) {
                            throw new IOException("Cannot open " + name);
                        }
                        copy(in, out);
                    }
                }
            }
        }
    }

    private static String safeFileName(String name) {
        if (name == null) {
            return null;
        }
        return name.replace('/', '_').replace('\\', '_');
    }

    private static void deleteRecursively(File file) throws IOException {
        if (file == null || !file.exists()) {
            return;
        }
        File[] children = file.listFiles();
        if (children != null) {
            for (File child : children) {
                deleteRecursively(child);
            }
        }
        if (!file.delete()) {
            throw new IOException("Cannot delete " + file);
        }
    }

    private static void copy(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[8192];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }
}
