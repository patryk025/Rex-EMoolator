package pl.genschu.bloomooemulator.engine;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.GdxRuntimeException;
import org.ini4j.Ini;
import pl.genschu.bloomooemulator.BlooMooEngine;
import pl.genschu.bloomooemulator.engine.context.EngineVariable;
import pl.genschu.bloomooemulator.engine.context.GameContext;
import pl.genschu.bloomooemulator.engine.filesystem.AssetSourceDispatcher;
import pl.genschu.bloomooemulator.engine.filesystem.IFileSystem;
import pl.genschu.bloomooemulator.engine.input.InputManager;
import pl.genschu.bloomooemulator.interpreter.context.Context;
import pl.genschu.bloomooemulator.interpreter.runtime.ExecutionContext;
import pl.genschu.bloomooemulator.interpreter.runtime.ASTInterpreter;
import pl.genschu.bloomooemulator.interpreter.variable.*;
import pl.genschu.bloomooemulator.interpreter.values.StringValue;
import pl.genschu.bloomooemulator.loader.CNVParser;
import pl.genschu.bloomooemulator.loader.ImageLoader;
import pl.genschu.bloomooemulator.logic.GameEntry;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

import com.badlogic.gdx.Gdx;
import pl.genschu.bloomooemulator.engine.filesystem.LocalFileSystem;
import pl.genschu.bloomooemulator.engine.filesystem.VFS;
import pl.genschu.bloomooemulator.engine.ini.INIManager;
import pl.genschu.bloomooemulator.objects.Image;
import pl.genschu.bloomooemulator.geometry.spartial.QuadTree;
import pl.genschu.bloomooemulator.geometry.shapes.Box2D;
import pl.genschu.bloomooemulator.utils.FileUtils;

public class Game {
    private static final String DANE_ROOT = "DANE";

    private final VFS vfs = new VFS();
    private Context definitionContext;
    private GameEntry game;
    private String currentEpisode = "";
    private String currentScene = "";
    private String currentApplicationFile = null; // VFS-relative path to .cnv
    private String currentEpisodeFile = null;     // VFS-relative directory
    private String currentSceneFile = null;       // VFS-relative directory
    private String currentResourceDirectory = DANE_ROOT;
    private String previousScene = "";

    private InputManager inputManager;

    private INIManager gameINI = null;
    private String relativeIniPath = null;

    private final QuadTree quadTree;
    private final Set<EngineVariable> collisionMonitoredVariables = new HashSet<>();
    private final Set<EngineVariable> dirtyCollisionObjects = Collections.newSetFromMap(new IdentityHashMap<>());
    private final Map<EngineVariable, Set<EngineVariable>> collisionMap = new IdentityHashMap<>();

    private SceneVariable currentSceneVariable;
    private SceneVariable previousSceneVariable;

    private ApplicationVariable applicationVariable;
    private EpisodeVariable currentEpisodeVariable;

    private final CNVParser cnvParser = new CNVParser();

    // Resolved VFS-relative directories for definition variables (Application, Episode, Scene)
    private final Map<String, String> variablePaths = new HashMap<>();

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

        try {
            IFileSystem fs = AssetSourceDispatcher.openAssets(folder);
            vfs.mountAssets(fs);
            if(fs instanceof LocalFileSystem) {
                vfs.setStorage(new LocalFileSystem(folder)); // use the same folder as assets
            }
            else {
                vfs.setStorage(new LocalFileSystem(resolveStorageDir()));
            }
        } catch (IOException e) {
            showErrorWrongMedia();
            return;
        }

        if (!vfs.isDirectory(DANE_ROOT)) {
            Gdx.app.error("Game loader", "Folder dane not found");
            return;
        }

        // find application.def in DANE/ (case-insensitive resolve handles disk casing)
        String applicationDefPath = DANE_ROOT + "/application.def";
        if (!vfs.exists(applicationDefPath)) {
            Gdx.app.error("Game loader", "Application.def not found");
            return;
        }
        try (InputStream is = vfs.openRead(applicationDefPath)) {
            cnvParser.parse(is, "application.def", definitionContext);
        } catch (IOException e) {
            Gdx.app.error("Game loader", e.getMessage());
            return;
        }

        // find APPLICATION variable
        Map<String, Variable> variables = definitionContext.getVariables();
        for (Map.Entry<String, Variable> entry : variables.entrySet()) {
            Variable variable = entry.getValue();
            if (variable instanceof ApplicationVariable app) {
                applicationVariable = app;
                break;
            }
        }

        if (applicationVariable == null) {
            Gdx.app.error("Game loader", "APPLICATION variable not found");
            return;
        }

        relativeIniPath = findGameINI();
        if (relativeIniPath == null) {
            gameINI = null;
        } else {
            gameINI = new INIManager();
            try (InputStream is = vfs.openRead(relativeIniPath)) {
                gameINI.load(is);
            } catch (IOException e) {
                Gdx.app.error("Game loader", "Failed to load INI via VFS: " + e.getMessage());
            }
        }

        // Resolve VFS path for ApplicationVariable
        String appPath = definitionContext.getAttribute(applicationVariable.name(), "PATH");
        if (appPath != null) {
            variablePaths.put(applicationVariable.name(), composeUnderDane(appPath));
        }

        // Sync language from ApplicationVariable to Game
        this.setLanguage(applicationVariable.language());

        // Resolve episode paths and scene paths
        for (String episodeName : applicationVariable.episodeNames()) {
            Variable epVar = definitionContext.getVariable(episodeName);
            if (epVar instanceof EpisodeVariable episode) {
                String epPath = definitionContext.getAttribute(episodeName, "PATH");
                if (epPath != null) {
                    variablePaths.put(episodeName, composeUnderDane(epPath));
                }

                for (String sceneName : episode.sceneNames()) {
                    String scenePath = definitionContext.getAttribute(sceneName, "PATH");
                    if (scenePath != null) {
                        variablePaths.put(sceneName, composeUnderDane(scenePath));
                    }
                }
            }
        }

        Gdx.app.log("Game loader", "Application.def loaded");

        Gdx.app.log("Game loader", "Loading application variables...");

        try {
            currentApplicationContext = new Context(new ExecutionContext(), definitionContext);
            currentApplicationContext.setGame(this);

            String appDir = variablePaths.get(applicationVariable.name());
            currentApplicationFile = appDir + "/" + applicationVariable.name() + ".cnv";
            currentResourceDirectory = appDir;

            try (InputStream is = vfs.openRead(currentApplicationFile)) {
                cnvParser.parse(is, applicationVariable.name() + ".cnv", currentApplicationContext);
            }

            Gdx.app.log("Game loader", "Application variables loaded");

            runInit(currentApplicationContext);

            // Navigate: first episode → first scene
            String firstEpisodeName;
            if (!applicationVariable.startWith().isEmpty()) {
                if (!applicationVariable.episodeNames().contains(applicationVariable.startWith())) {
                    Gdx.app.error("Game", "APPLICATION.STARTWITH references unknown episode: '"
                            + applicationVariable.startWith() + "'. Available episodes: " + applicationVariable.episodeNames());
                    firstEpisodeName = null;
                } else {
                    firstEpisodeName = applicationVariable.startWith();
                }
            } else {
                firstEpisodeName = applicationVariable.episodeNames().isEmpty() ? null : applicationVariable.episodeNames().get(0);
            }

            String firstSceneName = null;
            if (firstEpisodeName != null) {
                Variable firstEpVar = definitionContext.getVariable(firstEpisodeName);
                if (firstEpVar instanceof EpisodeVariable firstEpisode) {
                    firstSceneName = resolveStartScene(firstEpisode);
                }
            }

            if (firstSceneName != null) {
                goTo(firstSceneName);
            } else {
                showErrorInApplicationDef();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /** Joins a DANE-relative segment with the DANE root, normalizing separators. */
    private static String composeUnderDane(String relPath) {
        return DANE_ROOT + "/" + relPath.replace('\\', '/').replaceFirst("^/+", "");
    }

    /** Reads a file's bytes via VFS (caller-provided path). */
    private byte[] readAllBytes(String vfsPath) throws IOException {
        try (InputStream is = vfs.openRead(vfsPath)) {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte[] data = new byte[8192];
            int nRead;
            while ((nRead = is.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            buffer.flush();
            return buffer.toByteArray();
        }
    }

    private String findIniInExe(String exeVfsPath) {
        try {
            byte[] exeBytes = readAllBytes(exeVfsPath);
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


    /**
     * Locates the game's INI file. Returns a path relative to the VFS root
     * (suitable for {@code vfs.openRead}), or {@code null} if none is found.
     */
    public String findGameINI() {
        // bloomoo.ini points at the real INI via [MAIN].INI
        if (vfs.exists("bloomoo.ini")) {
            Gdx.app.log("findGameINI", "Found bloomoo.ini, looking for INI with variables...");
            try (InputStream is = vfs.openRead("bloomoo.ini")) {
                Ini bloomooIniFile = new Ini();
                bloomooIniFile.load(is);
                String mainIni = bloomooIniFile.get("MAIN", "INI");
                if (mainIni != null) {
                    Gdx.app.log("findGameINI", "Found INI: " + mainIni);
                    return mainIni;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        String[] rootEntries = vfs.list("");
        if (rootEntries == null) rootEntries = new String[0];

        List<String> exeFiles = new ArrayList<>();
        for (String name : rootEntries) {
            String lower = name.toLowerCase();
            if (lower.endsWith(".exe")
                    && !lower.equals("setup.exe")
                    && !lower.equals("uninstall.exe")
                    && !lower.equals("install.exe")) {
                exeFiles.add(name);
            }
        }

        if (exeFiles.isEmpty()) {
            Gdx.app.error("findGameINI", "No .exe file found, falling back to first .ini in folder");
            for (String name : rootEntries) {
                String lower = name.toLowerCase();
                if (lower.endsWith(".ini")
                        && !lower.equals("setup.ini")
                        && !lower.equals("uninstall.ini")
                        && !lower.equals("install.ini")) {
                    Gdx.app.log("findGameINI", "Using first found file: " + name);
                    return name;
                }
            }
            Gdx.app.error("findGameINI", "No .ini files found in the parent folder");
            return null;
        }

        for (String exeName : exeFiles) {
            Gdx.app.log("findGameINI", "Searching for ini file associated with executable: " + exeName);

            String baseName = exeName.substring(0, exeName.lastIndexOf('.'));
            String iniFileName = baseName + ".ini";

            if (vfs.exists(iniFileName)) {
                Gdx.app.log("findGameINI", "Found ini file: " + iniFileName);
                return iniFileName;
            }

            iniFileName = findIniInExe(exeName);
            if (iniFileName != null && vfs.exists(iniFileName)) {
                Gdx.app.log("findGameINI", "Found ini file: " + iniFileName);
                return iniFileName;
            }
        }

        Gdx.app.error("findGameINI", "No .ini file found in the parent folder");
        return null;
    }

    /**
     * Picks a directory for per-game writable storage. Uses libGDX's local
     * dir when available so it works on non-desktop backends; falls back to
     * a folder under the user home for tooling/CLI contexts.
     *
     * The game folder name acts as the per-game key. It's not perfect (two
     * games installed under the same folder name would collide), but it
     * avoids depending on a stable id from {@code GameEntry} that we don't
     * have yet.
     */
    private File resolveStorageDir() {
        String key = new File(this.game.getPath()).getName();
        try {
            if (Gdx.files != null) {
                return Gdx.files.local("storage/" + key).file();
            }
        } catch (Exception ignored) {
            // fallthrough to user.home
        }
        return new File(System.getProperty("user.home"), ".bloomooemulator/" + key);
    }

    public void goTo(String name) {
        persistGameINI();

        Variable variable = definitionContext.getVariable(name);

        if (variable instanceof EpisodeVariable episode) {
            loadEpisode(episode);
        } else if (variable instanceof SceneVariable scene) {
            // Find which episode contains this scene using episode.sceneNames()
            EpisodeVariable ownerEpisode = findEpisodeForScene(name);
            if (ownerEpisode != null) {
                loadEpisode(ownerEpisode);
            } else {
                Gdx.app.error("Game", "Scene '" + name + "' not found in any episode's SCENES list");
            }

            previousSceneVariable = currentSceneVariable;
            if (previousSceneVariable != null) {
                previousScene = previousSceneVariable.name();
            }
            loadScene(scene);
        } else {
            Gdx.app.error("Game", "GOTO target '" + name + "' is not a known Episode or Scene");
        }
    }

    public void goToPreviousScene() {
        persistGameINI();

        loadScene(previousSceneVariable);
    }

    private EpisodeVariable findEpisodeForScene(String sceneName) {
        for (String episodeName : applicationVariable.episodeNames()) {
            Variable epVar = definitionContext.getVariable(episodeName);
            if (epVar instanceof EpisodeVariable episode && episode.sceneNames().contains(sceneName)) {
                return episode;
            }
        }
        return null;
    }

    private String resolveStartScene(EpisodeVariable episode) {
        if (!episode.startWith().isEmpty()) {
            if (!episode.sceneNames().contains(episode.startWith())) {
                Gdx.app.error("Game", "EPISODE '" + episode.name() + "' STARTWITH references unknown scene: '"
                        + episode.startWith() + "'. Available scenes: " + episode.sceneNames());
                return null;
            }
            return episode.startWith();
        }
        return episode.sceneNames().isEmpty() ? null : episode.sceneNames().get(0);
    }

    private void showErrorInApplicationDef() {
        showStartupError("Cannot determine starting scene. Check Application.def for invalid STARTWITH values.", "Invalid startup parameters. Check Application.def or press F9 to change scene.");
    }

    private void showErrorWrongMedia() {
        showStartupError("Wrong media type used.", "Invalid startup parameters. The input data format is invalid. Currently, only directories and ISO images are supported. Please select a different source.");
    }

    private void showStartupError(String logMessage, String title) {
        Gdx.app.error("Game", logMessage);

        SceneVariable errorScene = new SceneVariable("__ERROR__");
        currentSceneVariable = errorScene;
        currentScene = errorScene.name();

        currentEpisodeVariable = new EpisodeVariable("__ERROR_EPISODE__", List.of(errorScene.name()), "", Map.of());
        currentEpisode = currentEpisodeVariable.name();

        currentEpisodeContext = new Context(new ExecutionContext(), currentApplicationContext != null ? currentApplicationContext : definitionContext);
        currentEpisodeContext.setGame(this);

        currentSceneContext = new Context(new ExecutionContext(), currentEpisodeContext);
        currentSceneContext.setGame(this);

        TextVariable errorText = new TextVariable("__STARTUP_ERROR__");
        errorText.state().text = title;
        errorText.state().visible = true;
        errorText.state().toCanvas = true;
        errorText.state().priority = Integer.MAX_VALUE;
        errorText.state().rect = new Box2D(100, 50, 700, 150);
        errorText.state().hJustify = "CENTER";
        errorText.state().vJustify = "CENTER";
        currentSceneContext.setVariable("__STARTUP_ERROR__", errorText);
    }

    private void loadEpisode(EpisodeVariable episode) {
        if (!Objects.equals(currentEpisode, episode.name())) {
            String epPath = variablePaths.get(episode.name());
            if(epPath == null) {
                currentEpisodeContext = new Context(new ExecutionContext(), currentApplicationContext);
                currentEpisodeContext.setGame(this);
                currentEpisodeVariable = episode;
                currentResourceDirectory = currentApplicationContext != null ? directoryOf(currentApplicationFile) : DANE_ROOT;
                Gdx.app.log("Game", "Episode " + episode.name() + " doesn't have PATH attribute. Skipping...");
                return;
            }
            Gdx.app.log("Game", "Loading episode " + episode.name());
            try {
                currentEpisode = episode.name();
                currentEpisodeContext = new Context(new ExecutionContext(), currentApplicationContext);
                currentEpisodeContext.setGame(this);
                currentEpisodeFile = epPath;
                currentResourceDirectory = epPath;
                String episodeFile = epPath + "/" + episode.name() + ".cnv";

                if(vfs.exists(episodeFile)) {
                    try (InputStream is = vfs.openRead(episodeFile)) {
                        cnvParser.parse(is, episode.name() + ".cnv", currentEpisodeContext);
                    } catch (NullPointerException e) {
                        Gdx.app.error("Game", "Error while loading episode " + episode.name() + ":\n" + e.getMessage());
                    }
                }
                else {
                    Gdx.app.error("Game", "Episode " + episode.name() + " doesn't have preload scripts. Continue without it.");
                }

                // Get first scene name
                String resolved = resolveStartScene(episode);
                if (resolved == null) {
                    Gdx.app.error("Game", "Episode '" + episode.name() + "' has no valid starting scene. Aborting episode load.");
                    return;
                }
                currentScene = resolved;

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

            String scenePath = variablePaths.get(scene.name());
            String sceneFile = scenePath == null ? null : scenePath + "/" + scene.name() + ".cnv";
            currentSceneFile = scenePath;
            currentResourceDirectory = scenePath != null ? scenePath : (currentEpisodeFile != null ? currentEpisodeFile : DANE_ROOT);
            currentScene = scene.name();

            // Handle music transition
            if(currentSceneMusic != null && currentSceneMusic.isPlaying()) {
                // Stop previous music if different from new scene's music
                if (!scene.music().equals(currentSceneVariable != null ? currentSceneVariable.music() : "")) {
                    currentSceneMusic.stop();
                }
            }

            currentSceneVariable = scene;

            if(sceneFile != null && vfs.exists(sceneFile)) {
                try (InputStream is = vfs.openRead(sceneFile)) {
                    cnvParser.parse(is, scene.name() + ".cnv", currentSceneContext);
                } catch (NullPointerException e) {
                    Gdx.app.error("Game", "Error while loading scene " + scene.name() + ":\n" + e.getMessage());
                }
            }
            else {
                Gdx.app.error("Game", "Scene " + scene.name() + " doesn't have preload scripts. Continue without it.");
            }

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
                String vfsPath = FileUtils.resolveVfsPath(this, musicFile);
                Music music = Gdx.audio.newMusic(vfs.getFileHandle(vfsPath));
                if (music != null) {
                    music.setLooping(true);
                }
                return music;
            } catch (Exception e) {
                Gdx.app.error("Game", "Error loading music via VFS: " + musicFile, e);
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
            String vfsPath = FileUtils.resolveVfsPath(this, bgFile);
            try (InputStream is = vfs.openRead(vfsPath)) {
                ImageLoader.loadImage(currentBackgroundImage, is);
            }
            currentBackgroundImage.state().updateRect();
        } catch (Exception e) {
            Gdx.app.error("Game", "Error loading background for scene " + scene.name() + " via VFS", e);
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

    private static String directoryOf(String vfsPath) {
        if (vfsPath == null || vfsPath.isEmpty()) return DANE_ROOT;
        int slash = vfsPath.replace('\\', '/').lastIndexOf('/');
        return slash >= 0 ? vfsPath.substring(0, slash) : DANE_ROOT;
    }

    private void persistGameINI() {
        if (gameINI == null || relativeIniPath == null) return;
        try (OutputStream os = vfs.openWrite(relativeIniPath)) {
            gameINI.store(os);
        } catch (IOException e) {
            Gdx.app.error("Game", "Failed to save INI via VFS: " + e.getMessage());
        }
    }

    // method for release data from memory
    public void dispose() {
        persistGameINI();

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
        vfs.setLanguage(language);

        // Sync with ApplicationVariable — create updated record if needed
        if (applicationVariable != null && currentApplicationContext != null) {
            ApplicationVariable updated = applicationVariable.withLanguage(language);
            currentApplicationContext.setVariable(applicationVariable.name(), updated);
            applicationVariable = updated;
        }

        Gdx.app.log("Game", "Language set to: " + language);
    }

    public GameContext getCurrentApplicationContext() {
        return currentApplicationContext;
    }

    public GameContext getCurrentEpisodeContext() {
        return currentEpisodeContext;
    }

    public String getCurrentResourceDirectory() {
        if (currentResourceDirectory != null && !currentResourceDirectory.isEmpty()) {
            return currentResourceDirectory;
        }
        if (currentSceneFile != null && !currentSceneFile.isEmpty()) {
            return currentSceneFile;
        }
        if (currentEpisodeFile != null && !currentEpisodeFile.isEmpty()) {
            return currentEpisodeFile;
        }
        if (currentApplicationFile != null && !currentApplicationFile.isEmpty()) {
            return directoryOf(currentApplicationFile);
        }
        return DANE_ROOT;
    }

    public String getPreviousScene() {
        return previousScene;
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

    public VFS getVfs() {
        return vfs;
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
