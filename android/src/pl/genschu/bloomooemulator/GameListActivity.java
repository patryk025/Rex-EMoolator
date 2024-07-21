package pl.genschu.bloomooemulator;

import android.Manifest;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
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

public class GameListActivity extends AppCompatActivity {
    private RecyclerView gamesRecyclerView;
    private GameListAdapter adapter;
    private GameManager gameManager;
    
    private static final int REQUEST_CODE_PERMISSIONS = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_list);

        gameManager = new GameManager(getFolderPath());

        gamesRecyclerView = findViewById(R.id.gamesRecyclerView);
        gamesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new GameListAdapter(this, gameManager.getGames());
        gamesRecyclerView.setAdapter(adapter);
        
        Toast.makeText(getApplicationContext(), "Załadowano "+gameManager.getGames().size+" gier", Toast.LENGTH_LONG).show();

        Button addGameButton = findViewById(R.id.addGameButton);
        addGameButton.setOnClickListener(v -> showGameDialog());
    
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
        Spinner mouseModeSelectBox = dialogView.findViewById(R.id.mouseModeSelectBox);
        CheckBox joystickCheckbox = dialogView.findViewById(R.id.joystickCheckbox);
        CheckBox skipPoliceCheckbox = dialogView.findViewById(R.id.skipPoliceCheckbox);
        CheckBox fullscreenCheckbox = dialogView.findViewById(R.id.fullscreenCheckbox);

        if (game != null) {
            nameField.setText(game.getName());
            pathField.setText(game.getPath());
            mouseModeSelectBox.setSelection(game.getMouseMode().equals("Dotykowo") ? 0 : 1);
            joystickCheckbox.setChecked(game.isMouseVirtualJoystick());
            skipPoliceCheckbox.setChecked(game.isSkipLicenceCode());
            fullscreenCheckbox.setChecked(game.isMaintainAspectRatio());
        }

        builder.setPositiveButton("Zapisz", (dialog, which) -> {
            if (game == null) {
                gameManager.addGame(new GameEntry(
                        nameField.getText().toString(),
                        pathField.getText().toString(),
                        mouseModeSelectBox.getSelectedItem().toString(),
                        joystickCheckbox.isChecked(),
                        skipPoliceCheckbox.isChecked(),
                        fullscreenCheckbox.isChecked()));

                adapter.notifyItemInserted(gameManager.getGames().indexOf(game, true));
            } else {
                game.setName(nameField.getText().toString());
                game.setPath(pathField.getText().toString());
                game.setMouseMode(mouseModeSelectBox.getSelectedItem().toString());
                game.setMouseVirtualJoystick(joystickCheckbox.isChecked());
                game.setSkipLicenceCode(skipPoliceCheckbox.isChecked());
                game.setMaintainAspectRatio(fullscreenCheckbox.isChecked());
                gameManager.updateGame(game);

                adapter.notifyItemChanged(gameManager.getGames().indexOf(game, true));
            }
        });

        builder.setNegativeButton("Anuluj", null);
        builder.show();
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
}
