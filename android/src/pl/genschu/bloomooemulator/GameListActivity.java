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
import pl.genschu.bloomooemulator.logic.GameEntry;
import pl.genschu.bloomooemulator.logic.GameManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

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
        
        Toast.makeText(getApplicationContext(), "Załadowano " + gameManager.getGames().size + " gier", Toast.LENGTH_LONG).show();

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
                Toast.makeText(this, "Uprawnienia przyznane!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Uprawnienia odmówione!", Toast.LENGTH_SHORT).show();
            }
        }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    Toast.makeText(this, "Uprawnienia do zarządzania plikami przyznane!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Uprawnienia do zarządzania plikami odmówione!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    
    private String getFolderPath() {
        return getFilesDir().getAbsolutePath();
    }

    public void showGameDialog() {
        showGameDialog(null);
    }

    public void showGameDialog(GameEntry game) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(game == null ? "Dodaj grę" : "Edytuj grę");

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_game, null);
        builder.setView(dialogView);

        EditText nameField = dialogView.findViewById(R.id.nameField);
        EditText pathField = dialogView.findViewById(R.id.pathField);
        Button chooseFolderButton = dialogView.findViewById(R.id.chooseFolderButton);
        Button chooseIsoButton = dialogView.findViewById(R.id.chooseIsoButton);
        Spinner mouseModeSelectBox = dialogView.findViewById(R.id.mouseModeSelectBox);
        CheckBox joystickCheckbox = dialogView.findViewById(R.id.joystickCheckbox);
        CheckBox skipPoliceCheckbox = dialogView.findViewById(R.id.skipPoliceCheckbox);
        CheckBox fullscreenCheckbox = dialogView.findViewById(R.id.fullscreenCheckbox);
        CheckBox fpsCounterCheckbox = dialogView.findViewById(R.id.fpsCounterCheckbox);

        chooseFolderButton.setOnClickListener(v -> showPathPicker(pathField, true));
        chooseIsoButton.setOnClickListener(v -> showPathPicker(pathField, false));

        if (game != null) {
            nameField.setText(game.getName());
            pathField.setText(game.getPath());
            mouseModeSelectBox.setSelection(game.getMouseMode().equals("Dotykowo") ? 0 : 1);
            joystickCheckbox.setChecked(game.isMouseVirtualJoystick());
            skipPoliceCheckbox.setChecked(game.isSkipLicenceCode());
            fullscreenCheckbox.setChecked(!game.isMaintainAspectRatio());
            fpsCounterCheckbox.setChecked(game.isShowFpsCounter());
        }

        builder.setPositiveButton("Zapisz", (dialog, which) -> {
            if (game == null) {
                GameEntry newGame = new GameEntry(
                        nameField.getText().toString(),
                        pathField.getText().toString(),
                        mouseModeSelectBox.getSelectedItem().toString(),
                        joystickCheckbox.isChecked(),
                        skipPoliceCheckbox.isChecked(),
                        !fullscreenCheckbox.isChecked());
                newGame.setShowFpsCounter(fpsCounterCheckbox.isChecked());
                gameManager.addGame(newGame);

                adapter.notifyItemInserted(gameManager.getGames().indexOf(newGame, true));
            } else {
                game.setName(nameField.getText().toString());
                game.setPath(pathField.getText().toString());
                game.setMouseMode(mouseModeSelectBox.getSelectedItem().toString());
                game.setMouseVirtualJoystick(joystickCheckbox.isChecked());
                game.setSkipLicenceCode(skipPoliceCheckbox.isChecked());
                game.setMaintainAspectRatio(!fullscreenCheckbox.isChecked());
                game.setShowFpsCounter(fpsCounterCheckbox.isChecked());
                gameManager.updateGame(game);

                adapter.notifyItemChanged(gameManager.getGames().indexOf(game, true));
            }
        });

        builder.setNegativeButton("Anuluj", null);
        builder.show();
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
            builder.setPositiveButton("Wybierz ten folder", (dialog, which) -> pathField.setText(directory.getAbsolutePath()));
        }
        builder.setNegativeButton("Anuluj", null);
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
        builder.setTitle("Usuwanie gry");
        builder.setMessage("Czy aby na pewno chcesz usunąć " + game.getName() + "?");
        builder.setPositiveButton("Tak", (dialog, which) -> {
            int index = gameManager.getGames().indexOf(game, true);
            gameManager.removeGame(game);
            adapter.notifyItemRemoved(index);
        });
        builder.setNegativeButton("Nie", null);
        builder.show();
    }

    /** Lazily resolves and persists the INI path before a game is launched. */
    public void persistIniPath(GameEntry game) {
        if (game.ensureIniPath()) {
            gameManager.updateGame(game);
        }
    }
}
