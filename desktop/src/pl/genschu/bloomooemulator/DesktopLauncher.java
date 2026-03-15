package pl.genschu.bloomooemulator;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import pl.genschu.bloomooemulator.logic.GameEntry;
import pl.genschu.bloomooemulator.logic.GameManager;

import java.io.File;

// On macOS application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main (String[] args) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setForegroundFPS(60);
		config.setTitle("Rex EMoolator");
		config.setWindowedMode(800, 600);

		GameEntry game = null;
		if (args.length > 0) {
			int gameIndex = Integer.parseInt(args[0]);
			String folderPath = System.getProperty("user.home") + File.separator + ".rexemoolator";
			GameManager gm = new GameManager(folderPath);
			game = gm.getGames().get(gameIndex);
		}

		new Lwjgl3Application(new BlooMooEngine(game), config);
	}
}
