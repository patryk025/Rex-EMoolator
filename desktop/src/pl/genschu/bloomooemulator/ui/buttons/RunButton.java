package pl.genschu.bloomooemulator.ui.buttons;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import pl.genschu.bloomooemulator.BlooMooEngine;
import pl.genschu.bloomooemulator.logic.GameEntry;
import pl.genschu.bloomooemulator.logic.GameManager;
import pl.genschu.bloomooemulator.ui.ButtonColumn;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.IOException;

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

        boolean isMac = System.getProperty("os.name", "").toLowerCase().contains("mac");
        if (isMac) {
            // On macOS, Lwjgl3Application requires -XstartOnFirstThread on the JVM.
            launchGameSubprocess(row);
        } else {
            launchGameInProcess(game);
        }
    }

    private void launchGameInProcess(GameEntry game) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setForegroundFPS(60);
        config.setWindowedMode(800, 600); // temporary for testing
        config.setTitle("Rex EMoolator");

        new Lwjgl3Application(new BlooMooEngine(game), config);
    }

    private void launchGameSubprocess(int gameIndex) {
        try {
            String javaExec = ProcessHandle.current().info().command().orElse("java");
            String classpath = System.getProperty("java.class.path");
            ProcessBuilder pb = new ProcessBuilder(
                    javaExec,
                    "-XstartOnFirstThread",
                    "-cp", classpath,
                    "pl.genschu.bloomooemulator.DesktopLauncher",
                    String.valueOf(gameIndex)
            );
            pb.inheritIO();
            pb.start();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
