package pl.genschu.bloomooemulator.objects;

import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.interpreter.variable.types.AnimoVariable;
import pl.genschu.bloomooemulator.interpreter.variable.types.ApplicationVariable;
import pl.genschu.bloomooemulator.interpreter.variable.types.EpisodeVariable;
import pl.genschu.bloomooemulator.interpreter.variable.types.ImageVariable;
import pl.genschu.bloomooemulator.interpreter.variable.types.SceneVariable;
import pl.genschu.bloomooemulator.interpreter.variable.types.SequenceVariable;
import pl.genschu.bloomooemulator.loader.CNVParser;
import pl.genschu.bloomooemulator.logic.GameEntry;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
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

    private SceneVariable currentSceneVariable;

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

        definitionContext.setGame(this);

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
            try {
                episode.setPath(FileUtils.findRelativeFileIgnoreCase(daneFolder, episode.getAttribute("PATH").getValue().toString()));
            } catch(NullPointerException e) {
                episode.setPath(null); // in Reksio i Skarb Piratów PATH is missing
            }

            for(SceneVariable scene : episode.getScenes()) {
                scene.setPath(FileUtils.findRelativeFileIgnoreCase(daneFolder, scene.getAttribute("PATH").getValue().toString()));
            }
        }

        Gdx.app.log("Game loader", "Application.def loaded");

        Gdx.app.log("Game loader", "Loading application variables...");

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
        Variable variable = definitionContext.getVariable(name);

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
            if(episode.getPath() == null) {
                currentEpisodeContext = new Context();
                currentEpisodeContext.setParentContext(currentApplicationContext);
                Gdx.app.log("Game", "Episode " + episode.getName() + " doesn't have PATH attribute. Skipping...");
                loadScene(episode.getFirstScene());
                return;
            }
            Gdx.app.log("Game", "Loading episode " + episode.getName());
            try {
                currentEpisodeContext = new Context();
                currentEpisodeContext.setParentContext(currentApplicationContext);
                File episodeFile = FileUtils.findRelativeFileIgnoreCase(episode.getPath(), episode.getName() + ".cnv");

                if(episodeFile != null) {
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
            currentSceneFile = scene.getPath();
            currentScene = scene.getName();

            currentSceneVariable = scene;

            cnvParser.parseFile(sceneFile, currentSceneContext);

            runInit(currentSceneContext);

            if(scene.getMusic() != null) {
                scene.getMusic().play();
            }
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
            context.getVariable("__INIT__").getMethod("RUN", Collections.singletonList("mixed")).execute(null);
        } catch (Exception e) {
            Gdx.app.error("Game", "Error while running __INIT__ BEHAVIOUR: " + e.getMessage(), e);
        }
    }

    // method for release data from memory
    public void dispose() {
        for(String key : definitionContext.getVariables().keySet()) {
            Variable variable = definitionContext.getVariable(key);
            if(variable instanceof SceneVariable) {
                SceneVariable scene = (SceneVariable) variable;
                if(scene.isBackgroundLoaded()) {
                    scene.getBackground().getImage().getImageTexture().dispose();
                }
                for(String varKey : scene.getContext().getGraphicsVariables().keySet()) {
                    Variable graphic = scene.getContext().getVariable(varKey);
                    if(graphic instanceof AnimoVariable) {
                        List<Image> images = ((AnimoVariable) graphic).getImages();
                        for(Image image : images) {
                            if(image.isLoaded()) {
                                image.getImageTexture().dispose();
                            }
                        }
                    }
                    if(graphic instanceof ImageVariable) {
                        if(((ImageVariable) graphic).getImage().isLoaded()) {
                            ((ImageVariable) graphic).getImage().getImageTexture().dispose();
                        }
                    }
                    if(graphic instanceof SequenceVariable) {
                        SequenceVariable sequence = (SequenceVariable) graphic;
                        
                        Map<String, AnimoVariable> animos = sequence.getAnimoCache();
                        
                        for(String animoName : animos.keySet()) {
                            AnimoVariable animo = animos.get(animoName);
                            List<Image> images = animo.getImages();
                            for(Image image : images) {
                                if(image.isLoaded()) {
                                    image.getImageTexture().dispose();
                                }
                            }
                        }
                    }
                }
            }
            variable.setContext(null);
        }
    }

    public SceneVariable getCurrentSceneVariable() {
        return currentSceneVariable;
    }

    public void setCurrentSceneVariable(SceneVariable currentSceneVariable) {
        this.currentSceneVariable = currentSceneVariable;
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
