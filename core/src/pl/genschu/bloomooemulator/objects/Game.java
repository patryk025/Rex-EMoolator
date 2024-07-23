package pl.genschu.bloomooemulator.objects;

import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.interpreter.variable.types.ApplicationVariable;
import pl.genschu.bloomooemulator.interpreter.variable.types.EpisodeVariable;
import pl.genschu.bloomooemulator.interpreter.variable.types.SceneVariable;
import pl.genschu.bloomooemulator.loader.CNVParser;
import pl.genschu.bloomooemulator.logic.GameEntry;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import pl.genschu.bloomooemulator.utils.FileUtils;

public class Game {
    private Context definitionContext;
    private GameEntry game;
    private String currentScene = "BRAKSCENY";

    private ApplicationVariable applicationVariable;

    private CNVParser cnvParser = new CNVParser();
    private File daneFolder = null;
    private File commonFolder = null;
    private File wavsFolder = null;
    private Context currentSceneContext;

    public Game(GameEntry game) {
        this.definitionContext = new Context();
        this.game = game;

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

        if(daneFolder == null) {
            Gdx.app.error("Game loader", "Folder dane not found");
            return;
        }
        files = daneFolder.listFiles();
        
        // find application.def
        boolean applicationDefFound = false;
        if (files != null) {
            for (File file : files) {
                if(file.getName().toLowerCase().matches("application.def")) {
                    try {
                        cnvParser.parseFile(file, definitionContext);
                        applicationDefFound = true;
                        break;
                    } catch(IOException e) {
                        Gdx.app.error("Game loader", e.getMessage());
                    }
                }
            }
        }

        if(!applicationDefFound) {
            Gdx.app.error("Game loader", "Application.def not found");
            return;
        }

        // find APPLICATION variable
        Map<String, Variable> variables = definitionContext.getVariables();
        for(Map.Entry<String, Variable> entry : variables.entrySet()) {
            Variable variable = entry.getValue();
            if(variable instanceof ApplicationVariable) {
                applicationVariable = (ApplicationVariable) variable;
                break;
            }
        }

        if(applicationVariable == null) {
            Gdx.app.error("Game loader", "APPLICATION variable not found");
            return;
        }

        applicationVariable.reloadEpisodes();
        applicationVariable.setPath(daneFolder);

        for(EpisodeVariable episode : applicationVariable.getEpisodes()) {
            episode.reloadScenes();
            episode.setPath(FileUtils.findRelativeFileIgnoreCase(daneFolder, episode.getAttribute("PATH").getValue().toString()));

            for(SceneVariable scene : episode.getScenes()) {
                scene.setPath(FileUtils.findRelativeFileIgnoreCase(daneFolder, scene.getAttribute("PATH").getValue().toString()));
            }
        }

        Gdx.app.log("Game loader", "Application.def loaded");
    }

    public Context getDefinitionContext() {
        return definitionContext;
    }

    public GameEntry getGame() {
        return game;
    }

    public void setGamePath(GameEntry game) {
        this.game = game;
    }

    public void setDefinitionContext(Context definitionContext) {
        this.definitionContext = definitionContext;
    }

    public String getCurrentScene() {
        return currentScene;
    }

    public void setCurrentScene(String currentScene) {
        this.currentScene = currentScene;
    }

    public Context getCurrentSceneContext() {
        return currentSceneContext;
    }

    public void setCurrentSceneContext(Context currentSceneContext) {
        this.currentSceneContext = currentSceneContext;
    }
}
