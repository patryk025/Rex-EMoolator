package pl.genschu.bloomooemulator;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import pl.genschu.bloomooemulator.BlooMooEmulator;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setForegroundFPS(60);
		config.setTitle("Rex EMoolator");

		String testScene = "D:\\Program Files\\AidemMedia\\Reksio i Wehikuł Czasu\\dane\\game\\przygoda\\CS_ZANURZENIEKRETA";

		new Lwjgl3Application(new BlooMooEmulator(testScene), config);
	}
}
