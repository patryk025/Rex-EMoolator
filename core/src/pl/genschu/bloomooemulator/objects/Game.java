package pl.genschu.bloomooemulator.objects;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.GdxRuntimeException;
import org.ini4j.Ini;
import org.w3c.dom.Attr;
import pl.genschu.bloomooemulator.BlooMooEmulator;
import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.util.GlobalVariables;
import pl.genschu.bloomooemulator.interpreter.variable.Attribute;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.interpreter.variable.types.*;
import pl.genschu.bloomooemulator.loader.CNVParser;
import pl.genschu.bloomooemulator.logic.GameEntry;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

import com.badlogic.gdx.Gdx;
import pl.genschu.bloomooemulator.utils.FileUtils;

public class Game {
    private Context definitionContext;
    private GameEntry game;
    private String currentEpisode = "BRAKEPIZODU";
    private String currentScene = "BRAKSCENY";
    private File currentApplicationFile = null;
    private File currentEpisodeFile = null;
    private File currentSceneFile = null;
    private File previousSceneFile = null;

    private INIManager gameINI = null;
    private String iniPath = null;

    private QuadTree quadTree;
    private final Set<Variable> collisionMonitoredVariables = new HashSet<>();

    private SceneVariable currentSceneVariable;
    private SceneVariable previousSceneVariable;

    private ApplicationVariable applicationVariable;
    private EpisodeVariable currentEpisodeVariable;

    private CNVParser cnvParser = new CNVParser();
    private File daneFolder = null; // $
    private File commonFolder = null; // $COMMON
    private File wavsFolder = null; // $WAVS
    private Context currentApplicationContext;
    private Context currentEpisodeContext;
    private Context currentSceneContext;

    private List<Music> playingAudios = new ArrayList<>();

    private Map<String, Music> musicCache;

    private Pixmap lastFrame;

    private BlooMooEmulator emulator;

    public Game(GameEntry game, BlooMooEmulator emulator) {
        this.definitionContext = new Context();
        this.game = game;
        this.quadTree = new QuadTree(0, new Rectangle(0, 0, 800, 600));
        this.emulator = emulator;

        musicCache = Collections.synchronizedMap(new HashMap<>());

        definitionContext.setGame(this);

        GlobalVariables.reset();

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

        iniPath = findGameINI();
        if(iniPath == null) {
            gameINI = null;
        }
        else {
            gameINI = new INIManager();
            try {
                gameINI.loadFile(iniPath);
            } catch (IOException e) {
                Gdx.app.error("Game loader", "This should not happen but somehow file doesn't load");
            }
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

            currentApplicationFile = FileUtils.findRelativeFileIgnoreCase(applicationVariable.getPath(), applicationVariable.getName()+".cnv");

            cnvParser.parseFile(currentApplicationFile, currentApplicationContext);

            Gdx.app.log("Game loader", "Application variables loaded");

            runInit(currentApplicationContext);

            // FIXME: I still don't know, why RANDOM is working without defining it in scripts
            currentApplicationContext.setVariable("RANDOM", new RandVariable("RANDOM", currentApplicationContext));
            currentApplicationContext.setVariable("SYSTEM", new SystemVariable("RANDOM", currentApplicationContext));

            goTo(applicationVariable.getFirstEpisode().getFirstScene().getName());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String findIniInExe(File exeFile) {
        try {
            byte[] exeBytes = new byte[(int) exeFile.length()];
            FileInputStream fis = new FileInputStream(exeFile);
            fis.read(exeBytes);
            fis.close();

            String exeContent = new String(exeBytes, StandardCharsets.UTF_8);

            // Poszukaj ".ini" i weź kilka znaków wcześniej
            int iniIndex = exeContent.indexOf(".ini");
            if (iniIndex > 0) {
                int startIndex = exeContent.lastIndexOf("\0", iniIndex) + 1;
                return exeContent.substring(startIndex, iniIndex + 4);
            }
        } catch (IOException e) {
            Gdx.app.error("findIniInExe", "Error reading exe file: " + e.getMessage());
        }
        return null;
    }


    public String findGameINI() {
        File parentFolder = daneFolder.getParentFile();
        if (parentFolder == null || !parentFolder.exists()) {
            Gdx.app.error("findGameINI", "Parent folder is null or does not exist");
            return null;
        }

        // find bloomoo.ini
        File bloomooIni = new File(parentFolder, "bloomoo.ini");
        if (bloomooIni.exists()) {
            Gdx.app.log("findGameINI", "Found bloomoo.ini in the parent folder, looking for INI with variables...");
            try {
                Ini bloomooIniFile = new Ini();
                bloomooIniFile.load(bloomooIni);

                // read INI field
                String iniPath = bloomooIniFile.get("MAIN", "INI");

                if(iniPath != null) {
                    File iniFile = new File(parentFolder, iniPath);
                    Gdx.app.log("findGameINI", "Found INI: " + iniFile.getAbsolutePath());
                    return iniFile.getAbsolutePath();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        File[] files = parentFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(".exe") && !name.equalsIgnoreCase("setup.exe") && !name.equalsIgnoreCase("uninstall.exe") && !name.equalsIgnoreCase("install.exe"));
        if (files == null || files.length == 0) {
            Gdx.app.error("findGameINI", "No .exe file found in the parent folder. Switching to fallback method...");

            File[] iniFiles = parentFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(".ini") && !name.equalsIgnoreCase("setup.ini") && !name.equalsIgnoreCase("uninstall.ini") && !name.equalsIgnoreCase("install.ini"));

            if (iniFiles == null || iniFiles.length == 0) {
                Gdx.app.error("findGame", "No .ini files found in the parent folder");
                return null;
            }

            Gdx.app.log("findGameIni", "Using first found file: " + iniFiles[0].getAbsolutePath());
            return iniFiles[0].getAbsolutePath();
        }

        for(File file : files) {
            Gdx.app.log("findGameINI", "Searching for ini file associated with executable: " + file.getName());

            // let's try to use ini file with the same name as exe
            String exeFileName = files[0].getName();
            String baseName = exeFileName.substring(0, exeFileName.lastIndexOf('.'));

            String iniFileName = baseName + ".ini";

            File iniFile = new File(parentFolder, iniFileName);

            if(iniFile.exists()) {
                Gdx.app.log("findGameINI", "Found ini file: " + iniFile.getAbsolutePath());
                return iniFile.getAbsolutePath();
            }
            else {
                iniFileName = findIniInExe(file);

                if (iniFileName == null) {
                    continue;
                }

                iniFile = new File(parentFolder, iniFileName);
                if (iniFile.exists()) {
                    Gdx.app.log("findGameINI", "Found ini file: " + iniFile.getAbsolutePath());
                    return iniFile.getAbsolutePath();
                }
            }
        }

        Gdx.app.error("findGameINI", "No .ini file found in the parent folder");
        return null;
    }

    public void goTo(String name) {
        try {
            gameINI.saveFile(iniPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (NullPointerException ignored) {
            // gameINI can be null
        }

        Variable variable = definitionContext.getVariable(name);

        if (variable instanceof EpisodeVariable) {
            loadEpisode((EpisodeVariable) variable);
        } else if (variable instanceof SceneVariable) {
            if (currentEpisodeContext == null) {
                for (EpisodeVariable episode : applicationVariable.getEpisodes()) {
                    if (episode.getScenes().contains((SceneVariable) variable)) {
                        loadEpisode(episode);
                        break;
                    }
                }
            }

            previousSceneVariable = currentSceneVariable;
            previousSceneFile = currentSceneFile;
            loadScene((SceneVariable) variable);
        }
    }

    public void goToPreviousScene() {
        try {
            gameINI.saveFile(iniPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        loadScene(previousSceneVariable);
    }

    private void loadEpisode(EpisodeVariable episode) {
        if (!Objects.equals(currentEpisode, episode.getName())) {
            if(episode.getPath() == null) {
                currentEpisodeContext = new Context();
                currentEpisodeContext.setParentContext(currentApplicationContext);
                currentEpisodeVariable = episode;
                Gdx.app.log("Game", "Episode " + episode.getName() + " doesn't have PATH attribute. Skipping...");
                //loadScene(episode.getFirstScene());
                return;
            }
            Gdx.app.log("Game", "Loading episode " + episode.getName());
            try {
                currentEpisode = episode.getName();
                currentEpisodeContext = new Context();
                currentEpisodeContext.setParentContext(currentApplicationContext);
                currentEpisodeFile = episode.getPath();
                File episodeFile = FileUtils.findRelativeFileIgnoreCase(currentEpisodeFile, episode.getName() + ".cnv");

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

                currentScene = episode.getFirstScene().getName();

                runInit(currentEpisodeContext);

                //loadScene(episode.getFirstScene());

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void loadScene(SceneVariable scene) {
        stopAllSounds();
        quadTree.clear();
        collisionMonitoredVariables.clear();
        emulator.setActiveButton(null);
        Gdx.app.log("Game", "Loading scene " + scene.getName());
        try {
            currentSceneContext = new Context();
            currentSceneContext.setParentContext(currentEpisodeContext);

            File sceneFile = FileUtils.findRelativeFileIgnoreCase(scene.getPath(), scene.getName() + ".cnv");
            currentSceneFile = scene.getPath();
            currentScene = scene.getName();

            if(currentSceneVariable != null) {
                Music music = currentSceneVariable.getMusic();
                if(music != null && music.isPlaying() && music != scene.getMusic()) {
                    music.stop();
                }
            }

            currentSceneVariable = scene;

            cnvParser.parseFile(sceneFile, currentSceneContext);

            try {
                if(scene.getMusic() != null && !scene.getMusic().isPlaying()) {
                    scene.getMusic().play();
                }
            } catch (GdxRuntimeException e) {
                Gdx.app.error("Game", "Error while loading music for scene " + scene.getName() + ". Continue without it.");
            }

            populateQuadTree(currentSceneContext);

            runInit(currentSceneContext);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void populateQuadTree(Context context) {
        for(Variable variable : context.getGraphicsVariables(true).values()) {
            if(variable instanceof ImageVariable || variable instanceof AnimoVariable) {
                Attribute monitorCollision = variable.getAttribute("MONITORCOLLISION");

                if(monitorCollision != null && monitorCollision.getValue().equals("TRUE")) {
                    quadTree.insert(variable);
                    collisionMonitoredVariables.add(variable);
                }
            }
        }
        Gdx.app.log("Game", "QuadTree populated, " + collisionMonitoredVariables.size() + " variables monitored");
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
        try {
            gameINI.saveFile(iniPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (NullPointerException ignored) {
            // gameINI can be null
        }

        for(String key : definitionContext.getVariables().keySet()) {
            Variable variable = definitionContext.getVariable(key);
            if(variable instanceof SceneVariable) {
                SceneVariable scene = (SceneVariable) variable;
                if(scene.isBackgroundLoaded()) {
                    Texture background = scene.getBackground().getImage().getImageTexture();
                    if(background != null) {
                        background.dispose();
                    }
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

                        /*
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
                        */
                    }
                }
            }
            variable.setContext(null);
        }
    }

    public void takeScreenshot() {
        // save frame for CanvasObserver
        if(lastFrame != null && !lastFrame.isDisposed())
            lastFrame.dispose();
        lastFrame = new Pixmap(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), Pixmap.Format.RGB565);
        Gdx.gl.glReadPixels(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), GL20.GL_RGB, GL20.GL_UNSIGNED_SHORT_5_6_5, lastFrame.getPixels());
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

    public File getCurrentApplicationFile() {
        return currentApplicationFile;
    }

    public Context getCurrentApplicationContext() {
        return currentApplicationContext;
    }

    public void setCurrentApplicationFile(File currentApplicationFile) {
        this.currentApplicationFile = currentApplicationFile;
    }

    public Context getCurrentEpisodeContext() {
        return currentEpisodeContext;
    }

    public File getCurrentEpisodeFile() {
        return currentEpisodeFile;
    }

    public void setCurrentEpisodeFile(File currentEpisodeFile) {
        this.currentEpisodeFile = currentEpisodeFile;
    }

    public File getCurrentSceneFile() {
        return currentSceneFile;
    }

    public void setCurrentSceneFile(File currentSceneFile) {
        this.currentSceneFile = currentSceneFile;
    }

    public Pixmap getLastFrame() {
        return lastFrame;
    }

    public void setLastFrame(Pixmap lastFrame) {
        this.lastFrame = lastFrame;
    }

    public Map<String, Music> getMusicCache() {
        return musicCache;
    }

    public void setMusicCache(Map<String, Music> musicCache) {
        this.musicCache = musicCache;
    }

    public INIManager getGameINI() {
        return gameINI;
    }

    public void setGameINI(INIManager gameINI) {
        this.gameINI = gameINI;
    }

    public ApplicationVariable getApplicationVariable() {
        return applicationVariable;
    }

    public List<Music> getPlayingAudios() {
        return playingAudios;
    }

    public void setPlayingAudios(List<Music> playingAudios) {
        this.playingAudios = playingAudios;
    }

    public void stopAllSounds() {
        stopAllSounds(true);
    }

    public void stopAllSounds(boolean stopBackground) {
        for(Music music : playingAudios) {
            if(stopBackground || music != currentSceneVariable.getMusic()) {
                music.stop();
            }
        }
    }

    public EpisodeVariable getCurrentEpisodeVariable() {
        return currentEpisodeVariable;
    }

    public String getCurrentEpisode() {
        return currentEpisode;
    }

    public QuadTree getQuadTree() {
        return quadTree;
    }

    public Set<Variable> getCollisionMonitoredVariables() {
        return collisionMonitoredVariables;
    }

    public BlooMooEmulator getEmulator() {
        return emulator;
    }
}
