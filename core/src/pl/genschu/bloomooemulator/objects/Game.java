package pl.genschu.bloomooemulator.objects;

import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.loader.CNVParser;
import pl.genschu.bloomooemulator.logic.GameEntry;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;

public class Game {
    private Context mainContext;
    private GameEntry game;
    private String currentScene = "BRAKSCENY";
    private List<Episode> episodes;
    private CNVParser cnvParser = new CNVParser();
    private File daneFolder = null;
    private File commonFolder = null;
    private File wavsFolder = null;

    public Game(GameEntry game) {
        this.mainContext = new Context();
        this.game = game;
        this.episodes = new ArrayList<>();

        scanGameDirectory();
    }

    private void scanGameDirectory() {
        File folder = new File(this.game.getPath());
        File[] files = folder.listFiles();
        
        daneFolder = null;
        commonFolder = null;
        wavsFolder = null;

        // let's find all needed folders
        if (files != null) {
            for (File file : files) {
                if(file.isDirectory()) {
                    if(file.getName().equalsIgnoreCase("dane")) {
                        daneFolder = file;
                    }
                    else if(file.getName().equalsIgnoreCase("common")) {
                        commonFolder = file;
                    }
                    else if(file.getName().equalsIgnoreCase("wavs")) {
                        wavsFolder = file;
                    }
                }
            }
        }
        
        files = daneFolder.listFiles();
        
        // find application.def
        boolean applicationDefFound = false;
        if (files != null) {
            for (File file : files) {
                if(file.getName().toLowerCase().matches("application.def")) {
                    try {
                        cnvParser.parseFile(file, mainContext);
                        break;
                    } catch(IOException e) {
                        Gdx.app.error("Game loader", e.getMessage());
                    }
                    applicationDefFound = true;
                }
            }
        }
    }

    public Context getMainContext() {
        return mainContext;
    }

    public GameEntry getGame() {
        return game;
    }

    public void setGamePath(GameEntry game) {
        this.game = game;
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
    
    class Episode {
        private List<Scene> scenes;
        private Scene firstScene;
        private File episodePath;
        private String name;
        
        public Episode(File episodePath) {
            this.episodePath = episodePath;
        }
    
        public List<Scene> getScenes() {
            return this.scenes;
        }
        
        public void addScene(String name) {
            
        }
        
        public Scene getFirstScene() {
            return this.firstScene;
        }
        
        public void setFirstScene(Scene firstScene) {
            this.firstScene = firstScene;
        }
        
        public File getEpisodePath() {
            return this.episodePath;
        }
        
        public void setEpisodePath(File episodePath) {
            this.episodePath = episodePath;
        }
    }
    
    class Scene {
        private String name;
        private File scenePath;
        
        public File getScenePath() {
            return scenePath;
        }
    }
}
