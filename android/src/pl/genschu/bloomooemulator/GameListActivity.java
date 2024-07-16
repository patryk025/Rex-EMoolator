package pl.genschu.bloomooemulator;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_list);

        gameManager = new GameManager(getFolderPath());

        gamesRecyclerView = findViewById(R.id.gamesRecyclerView);
        gamesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new GameListAdapter(this, gameManager.getGames());
        gamesRecyclerView.setAdapter(adapter);

        Button addGameButton = findViewById(R.id.addGameButton);
        addGameButton.setOnClickListener(v -> showGameDialog());
    }

    private String getFolderPath() {
        return getFilesDir().getAbsolutePath();
    }

    private void showGameDialog() {
        showGameDialog(null);
    }

    private void showGameDialog(GameEntry game) {
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
            } else {
                game.setName(nameField.getText().toString());
                game.setPath(pathField.getText().toString());
                game.setMouseMode(mouseModeSelectBox.getSelectedItem().toString());
                game.setMouseVirtualJoystick(joystickCheckbox.isChecked());
                game.setSkipLicenceCode(skipPoliceCheckbox.isChecked());
                game.setMaintainAspectRatio(fullscreenCheckbox.isChecked());
                gameManager.updateGame(game);
            }
        });

        builder.setNegativeButton("Anuluj", null);
        builder.show();
    }

    private void showDeleteDialog(GameEntry game) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Usuwanie gry");
        builder.setMessage("Czy aby na pewno chcesz usunąć " + game.getName() + "?");
        builder.setPositiveButton("Tak", (dialog, which) -> {
            gameManager.removeGame(game);
        });
        builder.setNegativeButton("Nie", null);
        builder.show();
    }
}
