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
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

import com.badlogic.gdx.Gdx;
import pl.genschu.bloomooemulator.utils.FileUtils;

public class Game {
    private Context definitionContext;
    private GameEntry game;
    private String currentEpisode = "BRAKEPIZODU";
    private String currentScene = "BRAKSCENY";
    private File currentSceneFile = null;

    private ApplicationVariable applicationVariable;

    private CNVParser cnvParser = new CNVParser();
    private File daneFolder = null; // $
    private File commonFolder = null; // $COMMON
    private File wavsFolder = null; // $WAVS
    private Context currentApplicationContext;
    private Context currentEpisodeContext;
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
        applicationVariable.setPath(FileUtils.findRelativeFileIgnoreCase(daneFolder, applicationVariable.getAttribute("PATH").getValue().toString()));

        for(EpisodeVariable episode : applicationVariable.getEpisodes()) {
            episode.reloadScenes();
            episode.setPath(FileUtils.findRelativeFileIgnoreCase(daneFolder, episode.getAttribute("PATH").getValue().toString()));

            for(SceneVariable scene : episode.getScenes()) {
                scene.setPath(FileUtils.findRelativeFileIgnoreCase(daneFolder, scene.getAttribute("PATH").getValue().toString()));
            }
        }

        Gdx.app.log("Game loader", "Application.def loaded");

        Gdx.app.log("Game loader", "Loading application variables...");


        definitionContext.setGame(this);

        try {
            currentApplicationContext = new Context();
            currentApplicationContext.setParentContext(definitionContext);

            cnvParser.parseFile(FileUtils.findRelativeFileIgnoreCase(applicationVariable.getPath(), applicationVariable.getName()+".cnv"), currentApplicationContext);

            Gdx.app.log("Game loader", "Application variables loaded");

            runInit(currentApplicationContext);

            goTo(applicationVariable.getFirstEpisode().getFirstScene().getName());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void goTo(String name) {
        Variable variable = definitionContext.getVariable(name, null);

        if (variable instanceof EpisodeVariable) {
            loadEpisode((EpisodeVariable) variable);
        } else if (variable instanceof SceneVariable) {
            if(currentEpisodeContext == null) {
                // find episode name containing this scene
                for(EpisodeVariable episode : applicationVariable.getEpisodes()) {
                    if(episode.getScenes().contains((SceneVariable) variable)) {
                        loadEpisode(episode);
                        break;
                    }
                }
            }
            if(variable.getName().equals(currentScene)) {
                return;
            }
            loadScene((SceneVariable) variable);
        }
    }

    private void loadEpisode(EpisodeVariable episode) {
        if (!Objects.equals(currentEpisode, episode.getName())) {
            Gdx.app.log("Game", "Loading episode " + episode.getName());
            try {
                currentEpisodeContext = new Context();
                currentEpisodeContext.setParentContext(currentApplicationContext);
                File episodeFile = FileUtils.findRelativeFileIgnoreCase(episode.getPath(), episode.getName() + ".cnv");

                if(episodeFile.exists()) {
                    try {
                        cnvParser.parseFile(episodeFile, currentEpisodeContext);
                    } catch (NullPointerException e) {
                        Gdx.app.error("Game", "Error while loading episode " + episode.getName() + ":\n" + e.getMessage());
                    }
                }
                else {
                    Gdx.app.error("Game", "Episode " + episode.getName() + " doesn't have preload scripts, but it's okey. Continue without it.");
                }

                currentEpisode = episode.getName();
                currentScene = episode.getFirstScene().getName();

                runInit(currentEpisodeContext);

                loadScene(episode.getFirstScene());

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void loadScene(SceneVariable scene) {
        Gdx.app.log("Game", "Loading scene " + scene.getName());
        try {
            currentSceneContext = new Context();
            currentSceneContext.setParentContext(currentEpisodeContext);

            File sceneFile = FileUtils.findRelativeFileIgnoreCase(scene.getPath(), scene.getName() + ".cnv");
            cnvParser.parseFile(sceneFile, currentSceneContext);
            currentSceneFile = scene.getPath();
            currentScene = scene.getName();

            runInit(currentSceneContext);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void runInit(Context context) {
        if(!context.hasVariable("__INIT__")) {
            Gdx.app.log("Game", "__INIT__ BEHAVIOUR not found. Continue without it.");
            return;
        }

        try {
            context.getVariable("__INIT__", null).getMethod("RUN", Collections.singletonList("mixed")).execute(null);
        } catch (Exception e) {
            Gdx.app.error("Game", "Error while running __INIT__ BEHAVIOUR: " + e.getMessage(), e);
        }
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

    public File getDaneFolder() {
        return daneFolder;
    }

    public void setDaneFolder(File daneFolder) {
        this.daneFolder = daneFolder;
    }

    public File getCommonFolder() {
        return commonFolder;
    }

    public void setCommonFolder(File commonFolder) {
        this.commonFolder = commonFolder;
    }

    public File getWavsFolder() {
        return wavsFolder;
    }

    public void setWavsFolder(File wavsFolder) {
        this.wavsFolder = wavsFolder;
    }

    public File getCurrentSceneFile() {
        return currentSceneFile;
    }

    public void setCurrentSceneFile(File currentSceneFile) {
        this.currentSceneFile = currentSceneFile;
    }
}
