package pl.genschu.bloomooemulator;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import pl.genschu.bloomooemulator.engine.config.EngineConfig;
import pl.genschu.bloomooemulator.engine.debug.PerformanceMonitor;
import pl.genschu.bloomooemulator.logic.GameEntry;
import pl.genschu.bloomooemulator.engine.Game;
import pl.genschu.bloomooemulator.engine.input.InputManager;
import pl.genschu.bloomooemulator.engine.render.RenderManager;
import pl.genschu.bloomooemulator.engine.update.UpdateManager;
import pl.genschu.bloomooemulator.engine.debug.DebugManager;

public class BlooMooEngine extends ApplicationAdapter {
    private static final float VIRTUAL_WIDTH = 800;
    private static final float VIRTUAL_HEIGHT = 600;

    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Viewport viewport;

    private Game game;
    private InputManager inputManager;
    private RenderManager renderManager;
    private UpdateManager updateManager;
    private DebugManager debugManager;

    private final GameEntry gameEntry;
    private final EngineConfig config;

    public BlooMooEngine(GameEntry gameEntry) {
        this.gameEntry = gameEntry;
        this.config = new EngineConfig();
    }

    @Override
    public void create() {
        // initialise LibGDX
        batch = new SpriteBatch();
        camera = new OrthographicCamera();

        // select viewport
        if (gameEntry.isMaintainAspectRatio()) {
            viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
        } else {
            viewport = new StretchViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
        }

        viewport.apply();
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);

        // initialise emulator components
        game = new Game(gameEntry, this);
        renderManager = new RenderManager(batch, camera, game, config);
        inputManager = new InputManager(camera, viewport, game, config);
        updateManager = new UpdateManager(game, config);
        debugManager = new DebugManager(batch, camera, game, config);

        game.setInputManager(inputManager);

        game.loadGame();

        // set log level
        Gdx.app.setLogLevel(config.getLogLevel());
    }

    @Override
    public void render() {
        float deltaTime = Gdx.graphics.getDeltaTime();

        if(config.isPaused() && !config.isStepFrame()) {
            deltaTime = 0;
        }
        if(config.isStepFrame()) {
            config.toggleStepFrame();
            deltaTime = 1.0f / 15.0f; // assume 15 FPS for a single step
        }

        PerformanceMonitor.startOperation("Render - frame time");

        // clear screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // update camera and set projection matrix
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        PerformanceMonitor.startOperation("Render - processing input");
        // handle input
        inputManager.processInput(deltaTime);
        PerformanceMonitor.endOperation("Render - processing input");

        PerformanceMonitor.startOperation("Render - updating game state");
        // update objects
        updateManager.update(deltaTime);
        PerformanceMonitor.endOperation("Render - updating game state");

        PerformanceMonitor.startOperation("Render - rendering");
        // render objects
        renderManager.render(deltaTime);
        PerformanceMonitor.endOperation("Render - rendering");

        PerformanceMonitor.startOperation("Render - rendering debug info");
        // render debug info
        debugManager.render(deltaTime);
        PerformanceMonitor.endOperation("Render - rendering debug info");

        // take screenshot for CanvasObserver
        game.takeScreenshot();

        PerformanceMonitor.endOperation("Render - frame time");
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        inputManager.handleResize(width, height);
    }

    @Override
    public void dispose() {
        batch.dispose();
        game.dispose();
        renderManager.dispose();
        inputManager.dispose();
        updateManager.dispose();
        debugManager.dispose();
    }

    public SpriteBatch getBatch() {
        return batch;
    }

    public OrthographicCamera getCamera() {
        return camera;
    }

    public Viewport getViewport() {
        return viewport;
    }

    public Game getGame() {
        return game;
    }

    public EngineConfig getConfig() {
        return config;
    }

    public DebugManager getDebugManager() {
        return debugManager;
    }

    public UpdateManager getUpdateManager() {
        return updateManager;
    }
}