package pl.genschu.bloomooemulator.objects;

import pl.genschu.bloomooemulator.interpreter.Context;

import java.io.File;

public class Game {
    private Context mainContext;
    private String gamePath;
    private String currentScene = "BRAKSCENY";

    public Game(String gamePath) {
        this.mainContext = new Context();
        this.gamePath = gamePath;

        scanGameDirectory();
    }

    private void scanGameDirectory() {
        File folder = new File(this.gamePath);
        File[] files = folder.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    System.out.println(file.getName());
                }
            }
        }
    }

    public Context getMainContext() {
        return mainContext;
    }

    public String getGamePath() {
        return gamePath;
    }

    public void setGamePath(String gamePath) {
        this.gamePath = gamePath;
    }

    public void setMainContext(Context mainContext) {
        this.mainContext = mainContext;
    }

    public String getCurrentScene() {
        return currentScene;
    }

    public void setCurrentScene(String currentScene) {
        this.currentScene = currentScene;
    }
}
