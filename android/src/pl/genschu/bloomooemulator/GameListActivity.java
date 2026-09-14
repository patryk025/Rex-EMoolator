package pl.genschu.bloomooemulator;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import pl.genschu.bloomooemulator.adapters.GameListAdapter;
import pl.genschu.bloomooemulator.engine.time.LegacyClockProfile;
import pl.genschu.bloomooemulator.logic.GameEntry;
import pl.genschu.bloomooemulator.logic.GameManager;
import pl.genschu.bloomooemulator.logic.GameSourceScanner;
import pl.genschu.bloomooemulator.logic.MouseMode;

import java.io.File;
import java.util.*;
import java.util.function.Consumer;

public class GameListActivity extends AppCompatActivity {
    private GameListAdapter adapter;
    private GameManager gameManager;

    private static final int REQUEST_CODE_PERMISSIONS = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_list);

        gameManager = new GameManager(getFolderPath());

        RecyclerView gamesRecyclerView = findViewById(R.id.gamesRecyclerView);
        gamesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new GameListAdapter(this, gameManager.getGames());
        gamesRecyclerView.setAdapter(adapter);
        
        int gameCount = gameManager.getGames().size;
        Toast.makeText(getApplicationContext(),
                getResources().getQuantityString(R.plurals.games_loaded, gameCount, gameCount),
                Toast.LENGTH_LONG).show();

        Button addGameButton = findViewById(R.id.addGameButton);
        addGameButton.setOnClickListener(v -> showGameDialog());

        if (savedInstanceState == null) {
            requestStoragePermissions();
        }
    }

    private void requestStoragePermissions() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            // On API 30+ legacy WRITE_EXTERNAL_STORAGE is a no-op; MANAGE_EXTERNAL_STORAGE supersedes it.
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse(String.format("package:%s", getApplicationContext().getPackageName())));
                startActivityForResult(intent, REQUEST_CODE_PERMISSIONS);
            }
            return;
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                REQUEST_CODE_PERMISSIONS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, getString(R.string.permissions_granted), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.permissions_denied), Toast.LENGTH_SHORT).show();
            }
        }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    Toast.makeText(this, getString(R.string.manage_files_granted), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, getString(R.string.manage_files_denied), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    
    private String getFolderPath() {
        return getFilesDir().getAbsolutePath();
    }

    private static int mouseModeLabelRes(MouseMode mode) {
        switch (mode) {
            case PHYSICAL:
                return R.string.mouse_mode_physical;
            case TOUCH:
            default:
                return R.string.mouse_mode_touch;
        }
    }

    public void showGameDialog() {
        showGameDialog(null);
    }

    public void showGameDialog(GameEntry game) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(game == null ? getString(R.string.add_game) : getString(R.string.edit_game));

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_game, null);
        builder.setView(dialogView);

        EditText nameField = dialogView.findViewById(R.id.nameField);
        EditText pathField = dialogView.findViewById(R.id.pathField);
        EditText rootField = dialogView.findViewById(R.id.rootField);
        Button chooseFolderButton = dialogView.findViewById(R.id.chooseFolderButton);
        Button chooseIsoButton = dialogView.findViewById(R.id.chooseIsoButton);
        Spinner mouseModeSelectBox = dialogView.findViewById(R.id.mouseModeSelectBox);
        Spinner legacyClockProfileSelectBox = dialogView.findViewById(R.id.legacyClockProfileSelectBox);
        CheckBox joystickCheckbox = dialogView.findViewById(R.id.joystickCheckbox);
        CheckBox licenceCodeHintCheckbox = dialogView.findViewById(R.id.licenceCodeHintCheckbox);
        CheckBox fullscreenCheckbox = dialogView.findViewById(R.id.fullscreenCheckbox);
        CheckBox fpsCounterCheckbox = dialogView.findViewById(R.id.fpsCounterCheckbox);
        EditText familyField = dialogView.findViewById(R.id.familyField);

        chooseFolderButton.setOnClickListener(v -> showPathPicker(pathField, true));
        chooseIsoButton.setOnClickListener(v -> showPathPicker(pathField, false));

        // Spinner shows localized labels but maps to a stable MouseMode (persisted by key).
        final MouseMode[] mouseModes = MouseMode.values();
        String[] mouseModeLabels = new String[mouseModes.length];
        for (int i = 0; i < mouseModes.length; i++) {
            mouseModeLabels[i] = getString(mouseModeLabelRes(mouseModes[i]));
        }
        ArrayAdapter<String> mouseModeAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, mouseModeLabels);
        mouseModeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mouseModeSelectBox.setAdapter(mouseModeAdapter);

        // Display names may evolve, while GameEntry persists the stable enum name.
        final LegacyClockProfile[] legacyClockProfiles = LegacyClockProfile.values();
        String[] legacyClockProfileLabels = new String[legacyClockProfiles.length];
        for (int i = 0; i < legacyClockProfiles.length; i++) {
            legacyClockProfileLabels[i] = legacyClockProfiles[i].displayName();
        }
        ArrayAdapter<String> legacyClockProfileAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, legacyClockProfileLabels);
        legacyClockProfileAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        legacyClockProfileSelectBox.setAdapter(legacyClockProfileAdapter);
        legacyClockProfileSelectBox.setSelection(LegacyClockProfile.defaultProfile().ordinal());

        if (game != null) {
            nameField.setText(game.getName());
            pathField.setText(game.getPath());
            rootField.setText(game.getRootPath());
            mouseModeSelectBox.setSelection(game.getMouseModeEnum().ordinal());
            legacyClockProfileSelectBox.setSelection(game.getLegacyClockProfileEnum().ordinal());
            joystickCheckbox.setChecked(game.isMouseVirtualJoystick());
            licenceCodeHintCheckbox.setChecked(game.isShowLicenceCodeHint());
            fullscreenCheckbox.setChecked(!game.isMaintainAspectRatio());
            fpsCounterCheckbox.setChecked(game.isShowFpsCounter());
            if (game.getFamilyOverride() != null) {
                familyField.setText(game.getFamilyOverride());
            }
        }

        builder.setPositiveButton(getString(R.string.save), null);
        builder.setNegativeButton(getString(R.string.cancel), null);
        AlertDialog editor = builder.create();
        editor.setOnShowListener(shown -> editor.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String sourcePath = pathField.getText().toString().trim();
            String rootPath = rootField.getText().toString().trim();
            editor.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
            new Thread(() -> {
                try {
                    List<GameSourceScanner.Candidate> candidates = game != null && sourcePath.equals(game.getPath()) && rootPath.equals(game.getRootPath())
                            ? Collections.singletonList(new GameSourceScanner.Candidate(rootPath, game.getName()))
                            : GameSourceScanner.scan(sourcePath, rootPath);
                    runOnUiThread(() -> {
                        if (isFinishing() || isDestroyed() || !editor.isShowing()) return;
                        editor.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                        if (candidates.isEmpty()) {
                            Toast.makeText(this, R.string.no_games_found, Toast.LENGTH_LONG).show();
                            return;
                        }
                        Consumer<List<GameSourceScanner.Candidate>> save = selected -> {
                            if (selected.isEmpty()) return;
                            try {
                                for (GameSourceScanner.Candidate candidate : selected) {
                                    if (game == null) {
                                        GameEntry newGame = new GameEntry(
                                                candidates.size() > 1 || nameField.getText().toString().isBlank() ? candidate.name() : nameField.getText().toString(),
                                                sourcePath,
                                                candidate.rootPath(),
                                                mouseModes[mouseModeSelectBox.getSelectedItemPosition()].key(),
                                                joystickCheckbox.isChecked(),
                                                licenceCodeHintCheckbox.isChecked(),
                                                !fullscreenCheckbox.isChecked());
                                        newGame.setShowFpsCounter(fpsCounterCheckbox.isChecked());
                                        newGame.setFamilyOverride(familyField.getText().toString());
                                        newGame.setLegacyClockProfile(legacyClockProfiles[legacyClockProfileSelectBox.getSelectedItemPosition()]);
                                        gameManager.addGame(newGame);

                                        adapter.notifyItemInserted(gameManager.getGames().indexOf(newGame, true));
                                    } else {
                                        game.setName(nameField.getText().toString());
                                        game.setSource(sourcePath, candidate.rootPath());
                                        game.setMouseMode(mouseModes[mouseModeSelectBox.getSelectedItemPosition()]);
                                        game.setMouseVirtualJoystick(joystickCheckbox.isChecked());
                                        game.setShowLicenceCodeHint(licenceCodeHintCheckbox.isChecked());
                                        game.setMaintainAspectRatio(!fullscreenCheckbox.isChecked());
                                        game.setShowFpsCounter(fpsCounterCheckbox.isChecked());
                                        game.setFamilyOverride(familyField.getText().toString());
                                        game.setLegacyClockProfile(legacyClockProfiles[legacyClockProfileSelectBox.getSelectedItemPosition()]);
                                        gameManager.updateGame(game);

                                        adapter.notifyItemChanged(gameManager.getGames().indexOf(game, true));
                                    }
                                }
                                editor.dismiss();
                            } catch (RuntimeException failure) {
                                Toast.makeText(this, getString(R.string.game_scan_failed) + "\n" + failure.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        };
                        if (candidates.size() == 1) {
                            save.accept(candidates);
                            return;
                        }
                        String[] labels = new String[candidates.size()];
                        boolean[] checked = new boolean[candidates.size()];
                        for (int i = 0; i < labels.length; i++) {
                            labels[i] = candidates.get(i).toString();
                            checked[i] = game == null || i == 0;
                        }
                        AlertDialog.Builder picker = new AlertDialog.Builder(this).setTitle(R.string.select_games);
                        if (game == null) {
                            picker.setMultiChoiceItems(labels, checked, (d, index, value) -> checked[index] = value);
                        } else {
                            picker.setSingleChoiceItems(labels, 0, (d, index) -> {
                                Arrays.fill(checked, false);
                                checked[index] = true;
                            });
                        }
                        picker.setPositiveButton(game == null ? R.string.add_selected : R.string.save, (d, which) -> {
                            List<GameSourceScanner.Candidate> selected = new ArrayList<>();
                            for (int i = 0; i < checked.length; i++) if (checked[i]) selected.add(candidates.get(i));
                            save.accept(selected);
                        }).setNegativeButton(R.string.cancel, null).show();
                    });
                } catch (Exception failure) {
                    runOnUiThread(() -> {
                        if (isFinishing() || isDestroyed() || !editor.isShowing()) return;
                        editor.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                        Toast.makeText(this, getString(R.string.game_scan_failed) + "\n" + failure.getMessage(), Toast.LENGTH_LONG).show();
                    });
                }
            }, "game-source-scan").start();
        }));
        editor.show();
    }

    private void showPathPicker(EditText pathField, boolean directoryMode) {
        File startDirectory = resolveStartDirectory(pathField.getText().toString());
        showPathPicker(pathField, directoryMode, startDirectory);
    }

    private void showPathPicker(EditText pathField, boolean directoryMode, File directory) {
        File[] files = directory.listFiles(file -> file.isDirectory() || (!directoryMode && isSupportedAssetFile(file)));
        List<File> entries = new ArrayList<>();
        if (directory.getParentFile() != null) {
            entries.add(directory.getParentFile());
        }
        if (files != null) {
            Arrays.sort(files, Comparator
                    .comparing((File file) -> !file.isDirectory())
                    .thenComparing(file -> file.getName().toLowerCase()));
            entries.addAll(Arrays.asList(files));
        }

        String[] labels = new String[entries.size()];
        for (int i = 0; i < entries.size(); i++) {
            File entry = entries.get(i);
            labels[i] = entry.equals(directory.getParentFile()) ? ".." : entry.getName() + (entry.isDirectory() ? "/" : "");
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(directory.getAbsolutePath());
        builder.setItems(labels, (dialog, which) -> {
            File selected = entries.get(which);
            if (selected.isDirectory()) {
                showPathPicker(pathField, directoryMode, selected);
            } else {
                pathField.setText(selected.getAbsolutePath());
            }
        });
        if (directoryMode) {
            builder.setPositiveButton(getString(R.string.choose_this_folder), (dialog, which) -> pathField.setText(directory.getAbsolutePath()));
        }
        builder.setNegativeButton(getString(R.string.cancel), null);
        builder.show();
    }

    private File resolveStartDirectory(String currentPath) {
        if (currentPath != null && !currentPath.isBlank()) {
            File currentFile = new File(currentPath);
            if (currentFile.isDirectory()) {
                return currentFile;
            }
            File parent = currentFile.getParentFile();
            if (parent != null && parent.isDirectory()) {
                return parent;
            }
        }

        File externalStorage = Environment.getExternalStorageDirectory();
        return externalStorage != null ? externalStorage : getFilesDir();
    }

    private static boolean isSupportedAssetFile(File file) {
        String name = file.getName().toLowerCase(Locale.ROOT);
        return name.endsWith(".iso") || name.endsWith(".zip");
    }

    public void showDeleteDialog(GameEntry game) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.delete_game_title));
        builder.setMessage(getString(R.string.delete_game_message, game.getName()));
        builder.setPositiveButton(getString(R.string.common_yes), (dialog, which) -> {
            int index = gameManager.getGames().indexOf(game, true);
            gameManager.removeGame(game);
            adapter.notifyItemRemoved(index);
        });
        builder.setNegativeButton(getString(R.string.common_no), null);
        builder.show();
    }

    /** Lazily resolves and persists the INI path before a game is launched. */
    public void persistIniPath(GameEntry game) {
        if (game.ensureIniPath()) {
            gameManager.updateGame(game);
        }
    }
}
