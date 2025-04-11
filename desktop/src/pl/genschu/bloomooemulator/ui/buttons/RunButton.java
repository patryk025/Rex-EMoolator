package pl.genschu.bloomooemulator.ui.buttons;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import pl.genschu.bloomooemulator.BlooMooEngine;
import pl.genschu.bloomooemulator.logic.GameEntry;
import pl.genschu.bloomooemulator.logic.GameManager;
import pl.genschu.bloomooemulator.ui.ButtonColumn;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class RunButton extends ButtonColumn {
    GameManager gameManager;

    public RunButton(JTable table, int column, GameManager gameManager) {
        super(table, column);
        this.gameManager = gameManager;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // get selected row
        int row = table.getSelectedRow();
        GameEntry game = gameManager.getGames().get(row);

        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setForegroundFPS(60);
        config.setWindowedMode(800, 600); // temporary for testing
        config.setTitle("Rex EMoolator");

        new Lwjgl3Application(new BlooMooEngine(game), config);
    }
}
