package pl.genschu.bloomooemulator.engine;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.GdxRuntimeException;
import org.ini4j.Ini;
import pl.genschu.bloomooemulator.BlooMooEngine;
import pl.genschu.bloomooemulator.engine.context.EngineVariable;
import pl.genschu.bloomooemulator.engine.context.GameContext;
import pl.genschu.bloomooemulator.engine.input.InputManager;
import pl.genschu.bloomooemulator.interpreter.context.Context;
import pl.genschu.bloomooemulator.interpreter.runtime.ExecutionContext;
import pl.genschu.bloomooemulator.interpreter.runtime.ASTInterpreter;
import pl.genschu.bloomooemulator.interpreter.variable.*;
import pl.genschu.bloomooemulator.interpreter.values.StringValue;
import pl.genschu.bloomooemulator.loader.CNVParser;
import pl.genschu.bloomooemulator.loader.ImageLoader;
import pl.genschu.bloomooemulator.logic.GameEntry;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

import com.badlogic.gdx.Gdx;
import pl.genschu.bloomooemulator.engine.ini.INIManager;
import pl.genschu.bloomooemulator.objects.Image;
import pl.genschu.bloomooemulator.geometry.spartial.QuadTree;
import pl.genschu.bloomooemulator.geometry.shapes.Box2D;
import pl.genschu.bloomooemulator.utils.FileUtils;

public class Game {
    private Context definitionContext;
    private GameEntry game;
    private String currentEpisode = "";
    private String currentScene = "";
    private File currentApplicationFile = null;
    private File currentEpisodeFile = null;
    private File currentSceneFile = null;
    private String previousScene = "";

    private InputManager inputManager;

    private INIManager gameINI = null;
    private String iniPath = null;

    private final QuadTree quadTree;
    private final Set<EngineVariable> collisionMonitoredVariables = new HashSet<>();
    private final Set<EngineVariable> dirtyCollisionObjects = Collections.newSetFromMap(new IdentityHashMap<>());
    private final Map<EngineVariable, Set<EngineVariable>> collisionMap = new IdentityHashMap<>();

    private SceneVariable currentSceneVariable;
    private SceneVariable previousSceneVariable;

    private ApplicationVariable applicationVariable;
    private EpisodeVariable currentEpisodeVariable;

    private final CNVParser cnvParser = new CNVParser();
    private File daneFolder = null; // $
    private File commonFolder = null; // $COMMON
    private File wavsFolder = null; // $WAVS

    // Resolved paths for definition variables (Application, Episode, Scene)
    private final Map<String, File> variablePaths = new HashMap<>();

    private String currentLanguage = "POL"; // Default language (Polish)
    private Context currentApplicationContext;
    private Context currentEpisodeContext;
    private Context currentSceneContext;

    private List<EngineVariable> playingAudios = new ArrayList<>();

    private Map<String, Music> musicCache;
    private Music currentSceneMusic = null;

    // Background image for current scene (loaded on scene change)
    private ImageVariable currentBackgroundImage = null;

    private Pixmap lastFrame;

    private final BlooMooEngine emulator;

    public Game(GameEntry game, BlooMooEngine emulator) {
        this.definitionContext = new Context(new ExecutionContext());
        this.game = game;
        this.quadTree = new QuadTree(0, new Box2D(0, 0, 800, 600));
        this.emulator = emulator;

        musicCache = Collections.synchronizedMap(new HashMap<>());

        definitionContext.setGame(this);
    }

    public void loadGame() {
        if (game != null) {
            scanGameDirectory();
        }
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
            if(variable instanceof ApplicationVariable app) {
                applicationVariable = app;
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

        // Resolve path for ApplicationVariable
        String appPath = definitionContext.getAttribute(applicationVariable.name(), "PATH");
        if (appPath != null) {
            variablePaths.put(applicationVariable.name(),
                    FileUtils.findRelativeFileIgnoreCase(daneFolder, appPath));
        }

        // Sync language from ApplicationVariable to Game
        this.setLanguage(applicationVariable.language());

        // Resolve episode paths and scene paths
        for (String episodeName : applicationVariable.episodeNames()) {
            Variable epVar = definitionContext.getVariable(episodeName);
            if (epVar instanceof EpisodeVariable episode) {
                String epPath = definitionContext.getAttribute(episodeName, "PATH");
                if (epPath != null) {
                    variablePaths.put(episodeName,
                            FileUtils.findRelativeFileIgnoreCase(daneFolder, epPath));
                }

                for (String sceneName : episode.sceneNames()) {
                    String scenePath = definitionContext.getAttribute(sceneName, "PATH");
                    if (scenePath != null) {
                        variablePaths.put(sceneName,
                                FileUtils.findRelativeFileIgnoreCase(daneFolder, scenePath));
                    }
                }
            }
        }

        Gdx.app.log("Game loader", "Application.def loaded");

        Gdx.app.log("Game loader", "Loading application variables...");

        try {
            currentApplicationContext = new Context(new ExecutionContext(), definitionContext);
            currentApplicationContext.setGame(this);

            File appDir = variablePaths.get(applicationVariable.name());
            currentApplicationFile = FileUtils.findRelativeFileIgnoreCase(appDir, applicationVariable.name()+".cnv");

            cnvParser.parseFile(currentApplicationFile, currentApplicationContext);

            Gdx.app.log("Game loader", "Application variables loaded");

            runInit(currentApplicationContext);

            // Navigate: first episode → first scene
            String firstEpisodeName = !applicationVariable.startWith().isEmpty()
                    ? applicationVariable.startWith()
                    : applicationVariable.episodeNames().isEmpty() ? null : applicationVariable.episodeNames().get(0);

            if (firstEpisodeName != null) {
                Variable firstEpVar = definitionContext.getVariable(firstEpisodeName);
                if (firstEpVar instanceof EpisodeVariable firstEpisode) {
                    String firstSceneName = !firstEpisode.startWith().isEmpty()
                            ? firstEpisode.startWith()
                            : firstEpisode.sceneNames().isEmpty() ? null : firstEpisode.sceneNames().get(0);
                    if (firstSceneName != null) {
                        goTo(firstSceneName);
                    }
                }
            }
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

        if (variable instanceof EpisodeVariable episode) {
            loadEpisode(episode);
        } else if (variable instanceof SceneVariable scene) {
            if (currentEpisodeContext == null) {
                // Find which episode contains this scene
                for (String episodeName : applicationVariable.episodeNames()) {
                    Variable epVar = definitionContext.getVariable(episodeName);
                    if (epVar instanceof EpisodeVariable episode && episode.sceneNames().contains(name)) {
                        loadEpisode(episode);
                        break;
                    }
                }
            }

            previousSceneVariable = currentSceneVariable;
            try {
                previousScene = previousSceneVariable.name();
            }
            catch (NullPointerException ignored) {}
            loadScene(scene);
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
        if (!Objects.equals(currentEpisode, episode.name())) {
            File epPath = variablePaths.get(episode.name());
            if(epPath == null) {
                currentEpisodeContext = new Context(new ExecutionContext(), currentApplicationContext);
                currentEpisodeContext.setGame(this);
                currentEpisodeVariable = episode;
                Gdx.app.log("Game", "Episode " + episode.name() + " doesn't have PATH attribute. Skipping...");
                return;
            }
            Gdx.app.log("Game", "Loading episode " + episode.name());
            try {
                currentEpisode = episode.name();
                currentEpisodeContext = new Context(new ExecutionContext(), currentApplicationContext);
                currentEpisodeContext.setGame(this);
                currentEpisodeFile = epPath;
                File episodeFile = FileUtils.findRelativeFileIgnoreCase(currentEpisodeFile, episode.name() + ".cnv");

                if(episodeFile != null) {
                    try {
                        cnvParser.parseFile(episodeFile, currentEpisodeContext);
                    } catch (NullPointerException e) {
                        Gdx.app.error("Game", "Error while loading episode " + episode.name() + ":\n" + e.getMessage());
                    }
                }
                else {
                    Gdx.app.error("Game", "Episode " + episode.name() + " doesn't have preload scripts, but it's okey. Continue without it.");
                }

                // Get first scene name
                currentScene = !episode.startWith().isEmpty()
                        ? episode.startWith()
                        : episode.sceneNames().isEmpty() ? "" : episode.sceneNames().get(0);

                runInit(currentEpisodeContext);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void loadScene(SceneVariable scene) {
        stopAllSounds();
        quadTree.clear();
        collisionMonitoredVariables.clear();
        dirtyCollisionObjects.clear();
        collisionMap.clear();
        if(inputManager != null) {
            inputManager.setActiveButton(null);
            inputManager.setMouseVisible(true);
        }
        Gdx.app.log("Game", "Loading scene " + scene.name());
        try {
            currentSceneContext = new Context(new ExecutionContext(), currentEpisodeContext);
            currentSceneContext.setGame(this);

            File scenePath = variablePaths.get(scene.name());
            File sceneFile = FileUtils.findRelativeFileIgnoreCase(scenePath, scene.name() + ".cnv");
            currentSceneFile = scenePath;
            currentScene = scene.name();

            // Handle music transition
            if(currentSceneMusic != null && currentSceneMusic.isPlaying()) {
                // Stop previous music if different from new scene's music
                if (!scene.music().equals(currentSceneVariable != null ? currentSceneVariable.music() : "")) {
                    currentSceneMusic.stop();
                }
            }

            currentSceneVariable = scene;

            cnvParser.parseFile(sceneFile, currentSceneContext);

            // Load and play scene music
            try {
                if (!scene.music().isEmpty()) {
                    Music music = loadMusic("$\\"+scene.music());
                    if (music != null && !music.isPlaying()) {
                        music.setLooping(true);
                        music.setVolume(scene.musicVolume() / 1000.0f);
                        music.play();
                    }
                    currentSceneMusic = music;
                }
            } catch (GdxRuntimeException e) {
                Gdx.app.error("Game", "Error while loading music for scene " + scene.name() + ". Continue without it.");
            }

            // Load background image
            loadBackgroundImage(scene);

            populateQuadTree(currentSceneContext);

            runInit(currentSceneContext);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Music loadMusic(String musicFile) {
        return musicCache.computeIfAbsent(musicFile, key -> {
            try {
                String path = FileUtils.resolveRelativePath(this, musicFile);
                Music music = Gdx.audio.newMusic(Gdx.files.absolute(path));
                if (music != null) {
                    music.setLooping(true);
                }
                return music;
            } catch (Exception e) {
                Gdx.app.error("Game", "Error loading music: " + musicFile, e);
                return null;
            }
        });
    }

    private void loadBackgroundImage(SceneVariable scene) {
        if (scene.background().isEmpty()) {
            currentBackgroundImage = null;
            return;
        }
        try {
            String bgFile = scene.background();
            currentBackgroundImage = new ImageVariable("__BACKGROUND__", bgFile);
            String path = FileUtils.resolveRelativePath(this, bgFile);
            ImageLoader.loadImage(currentBackgroundImage, path);
            currentBackgroundImage.state().updateRect();
        } catch (Exception e) {
            Gdx.app.error("Game", "Error loading background for scene " + scene.name(), e);
            currentBackgroundImage = null;
        }
    }

    private void populateQuadTree(Context context) {
        for(Variable variable : context.getGraphicsVariables().values()) {
            if(variable instanceof ImageVariable img) {
                if (img.state().monitorCollision) {
                    addCollisionMonitor(variable);
                }
            } else if(variable instanceof AnimoVariable animo) {
                if (animo.isMonitorCollision()) {
                    addCollisionMonitor(variable);
                }
            }
        }
        Gdx.app.log("Game", "QuadTree populated, " + collisionMonitoredVariables.size() + " variables monitored");
    }

    private void runInit(Context context) {
        // v2 CNVParser already runs ONINIT signals during parseFile().
        // The __INIT__ behaviour is a v1 concept.
        // For v2, check if there's a __INIT__ behaviour and run it via ASTInterpreter.
        Variable initVar = context.getVariable("__INIT__");
        if (initVar == null) {
            return;
        }

        if (initVar instanceof BehaviourVariable initBehaviour) {
            try {
                ASTInterpreter interpreter = new ASTInterpreter(context);
                interpreter.runBehaviour("__INIT__", initBehaviour, initBehaviour, List.of());
            } catch (Exception e) {
                Gdx.app.error("Game", "Error while running __INIT__ BEHAVIOUR: " + e.getMessage(), e);
            }
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

        // Dispose background image
        if (currentBackgroundImage != null) {
            currentBackgroundImage.state().dispose();
        }

        // Dispose music
        for (Music music : musicCache.values()) {
            if (music != null) {
                music.dispose();
            }
        }
        musicCache.clear();

        // Dispose graphics variables
        for(String key : definitionContext.getVariables().keySet()) {
            Variable variable = definitionContext.getVariable(key);
            if(variable instanceof ImageVariable img) {
                img.state().dispose();
            } else if(variable instanceof AnimoVariable animo) {
                for(Image image : animo.getImages()) {
                    if(image.isLoaded() && image.getImageTexture() != null) {
                        image.getImageTexture().dispose();
                    }
                }
            }
        }
    }

    public String findINISectionForVariable(String variableName) {
        if (gameINI == null) {
            return currentScene.toUpperCase();
        }

        // Section hierarchy
        String[] sectionsToCheck = new String[] {
                currentScene.toUpperCase(),
                currentEpisode.toUpperCase(),
                applicationVariable.name().toUpperCase()
        };

        String existingSection = gameINI.findSectionForKey(sectionsToCheck, variableName);

        if(existingSection != null)
            return existingSection;

        return currentScene.toUpperCase();
    }

    public void takeScreenshot() {
        // save frame for CanvasObserver
        if(lastFrame != null && !lastFrame.isDisposed())
            lastFrame.dispose();
        lastFrame = new Pixmap(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), Pixmap.Format.RGB565);
        Gdx.gl.glReadPixels(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), GL20.GL_RGB, GL20.GL_UNSIGNED_SHORT_5_6_5, lastFrame.getPixels());
    }

    // ========================================
    // COLLISION TRACKING
    // ========================================

    public boolean isColliding(EngineVariable a, EngineVariable b) {
        Set<EngineVariable> set = collisionMap.get(a);
        return set != null && set.contains(b);
    }

    public void setColliding(EngineVariable a, EngineVariable b) {
        if (isColliding(a, b)) {
            return;
        }
        collisionMap.computeIfAbsent(a, k -> Collections.newSetFromMap(new IdentityHashMap<>())).add(b);
        emitCollisionSignal(a, "ONCOLLISION", b.getName());
    }

    public void releaseColliding(EngineVariable a, EngineVariable b) {
        if (!isColliding(a, b)) {
            return;
        }
        Set<EngineVariable> set = collisionMap.get(a);
        if (set != null) {
            set.remove(b);
            if (set.isEmpty()) {
                collisionMap.remove(a);
            }
        }

        emitCollisionSignal(a, "ONCOLLISIONFINISHED", b.getName());
    }

    public void markCollisionDirty(EngineVariable variable) {
        if (collisionMonitoredVariables.contains(variable)) {
            dirtyCollisionObjects.add(variable);
        }
    }

    public Set<EngineVariable> getDirtyCollisionObjects() {
        return dirtyCollisionObjects;
    }

    public void addCollisionMonitor(EngineVariable variable) {
        if (!collisionMonitoredVariables.add(variable)) {
            return;
        }
        quadTree.insert(variable);
    }

    public void removeCollisionMonitor(EngineVariable variable) {
        for (EngineVariable other : getCollidingWith(variable)) {
            releaseColliding(variable, other);
        }
        collisionMonitoredVariables.remove(variable);
        quadTree.remove(variable);
    }

    public void rebuildCollisionQuadTree() {
        quadTree.clear();
        for (EngineVariable variable : collisionMonitoredVariables) {
            quadTree.insert(variable);
        }
    }

    public Set<EngineVariable> getCollidingWith(EngineVariable variable) {
        Set<EngineVariable> set = collisionMap.get(variable);
        if (set == null || set.isEmpty()) {
            return Set.of();
        }
        Set<EngineVariable> snapshot = Collections.newSetFromMap(new IdentityHashMap<>());
        snapshot.addAll(set);
        return snapshot;
    }

    private void emitCollisionSignal(EngineVariable variable, String signalName, String otherName) {
        String normalizedName = otherName.toUpperCase(Locale.ROOT);
        if (variable instanceof Variable v2Var) {
            v2Var.emitSignal(signalName, new StringValue(normalizedName));
        }
    }

    // ========================================
    // GETTERS AND SETTERS
    // ========================================

    public SceneVariable getCurrentSceneVariable() {
        return currentSceneVariable;
    }

    public void setCurrentSceneVariable(SceneVariable currentSceneVariable) {
        this.currentSceneVariable = currentSceneVariable;
    }

    public ImageVariable getCurrentBackgroundImage() {
        return currentBackgroundImage;
    }

    public void setCurrentBackgroundImage(ImageVariable backgroundImage) {
        this.currentBackgroundImage = backgroundImage;
    }

    public Music getCurrentSceneMusic() {
        return currentSceneMusic;
    }

    public GameContext getDefinitionContext() {
        return definitionContext;
    }

    public GameEntry getGame() {
        return game;
    }

    public void setGamePath(GameEntry game) {
        this.game = game;
    }

    public void setDefinitionContext(GameContext definitionContext) {
        this.definitionContext = (Context) definitionContext;
    }

    public String getCurrentScene() {
        return currentScene;
    }

    public void setCurrentScene(String currentScene) {
        this.currentScene = currentScene;
    }

    public GameContext getCurrentSceneContext() {
        return currentSceneContext;
    }

    public void setCurrentSceneContext(GameContext currentSceneContext) {
        this.currentSceneContext = (Context) currentSceneContext;
    }

    public File getDaneFolder() {
        return daneFolder;
    }

    public void setDaneFolder(File daneFolder) {
        this.daneFolder = daneFolder;
    }

    /**
     * Gets the current language code (e.g., "POL", "HUN", "CZE").
     *
     * @return Current language code
     */
    public String getLanguage() {
        return currentLanguage;
    }

    /**
     * Sets the current language code.
     * Also updates ApplicationVariable if present.
     *
     * @param language Language code (e.g., "POL", "HUN", "CZE")
     */
    public void setLanguage(String language) {
        this.currentLanguage = language;

        // Sync with ApplicationVariable — create updated record if needed
        if (applicationVariable != null && currentApplicationContext != null) {
            ApplicationVariable updated = applicationVariable.withLanguage(language);
            currentApplicationContext.setVariable(applicationVariable.name(), updated);
            applicationVariable = updated;
        }

        Gdx.app.log("Game", "Language set to: " + language);
    }

    @Deprecated(forRemoval = true, since = "0.2.0-beta")
    public File getCommonFolder() {
        return commonFolder;
    }

    @Deprecated(forRemoval = true, since = "0.2.0-beta")
    public void setCommonFolder(File commonFolder) {
        this.commonFolder = commonFolder;
    }

    @Deprecated(forRemoval = true, since = "0.2.0-beta")
    public File getWavsFolder() {
        return wavsFolder;
    }

    @Deprecated(forRemoval = true, since = "0.2.0-beta")
    public void setWavsFolder(File wavsFolder) {
        this.wavsFolder = wavsFolder;
    }

    public File getCurrentApplicationFile() {
        return currentApplicationFile;
    }

    public GameContext getCurrentApplicationContext() {
        return currentApplicationContext;
    }

    public void setCurrentApplicationFile(File currentApplicationFile) {
        this.currentApplicationFile = currentApplicationFile;
    }

    public GameContext getCurrentEpisodeContext() {
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

    public String getPreviousScene() {
        return previousScene;
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

    public List<EngineVariable> getPlayingAudios() {
        return playingAudios;
    }

    public void setPlayingAudios(List<EngineVariable> playingAudios) {
        this.playingAudios = playingAudios;
    }

    public void stopAllSounds() {
        stopAllSounds(true);
    }

    public void stopAllSounds(boolean stopBackground) {
        for(EngineVariable ev : new ArrayList<>(playingAudios)) {
            if (ev instanceof SoundVariable sound) {
                if(stopBackground || sound.state().sound != currentSceneMusic) {
                    sound.stop(false);
                }
            }
        }
        playingAudios.clear();
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

    public Set<EngineVariable> getCollisionMonitoredVariables() {
        return collisionMonitoredVariables;
    }

    public BlooMooEngine getEmulator() {
        return emulator;
    }

    public void setInputManager(InputManager inputManager) {
        this.inputManager = inputManager;
    }

    public InputManager getInputManager() {
        return inputManager;
    }
}
