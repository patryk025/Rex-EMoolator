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
import pl.genschu.bloomooemulator.engine.compatibility.EngineVariant;
import pl.genschu.bloomooemulator.engine.debug.PerformanceMonitor;
import pl.genschu.bloomooemulator.logic.GameEntry;
import pl.genschu.bloomooemulator.engine.Game;
import pl.genschu.bloomooemulator.engine.input.InputManager;
import pl.genschu.bloomooemulator.engine.render.RenderManager;
import pl.genschu.bloomooemulator.engine.time.LegacyPulseGate;
import pl.genschu.bloomooemulator.engine.update.UpdateManager;
import pl.genschu.bloomooemulator.engine.debug.DebugManager;
import pl.genschu.bloomooemulator.platform.PrinterService;

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
    private LegacyPulseGate legacyPulseGate;

    private final GameEntry gameEntry;
    private final EngineConfig config;
    private final PrinterService printerService;

    public BlooMooEngine(GameEntry gameEntry) {
        this(gameEntry, null);
    }

    public BlooMooEngine(GameEntry gameEntry, PrinterService printerService) {
        this.gameEntry = gameEntry;
        this.printerService = printerService;
        this.config = new EngineConfig();
        if (gameEntry != null) {
            this.config.setShowFpsCounter(gameEntry.isShowFpsCounter());
            this.config.setLegacyClockProfile(gameEntry.getLegacyClockProfileEnum());
        }
    }

    /** Platform printing hook; null when the launcher provides none. */
    public PrinterService getPrinterService() {
        return printerService;
    }

    @Override
    public void create() {
        // initialise LibGDX

        // Legacy managers are polled once per host render. Apply the existing
        // cadence configuration on every backend (not only the desktop
        // launcher). It is only a scheduling hint: AndroidGraphics currently
        // ignores setForegroundFPS, so LegacyPulseGate below is authoritative.
        Gdx.graphics.setVSync(config.isVsync());
        Gdx.graphics.setForegroundFPS(config.getTargetFPS());
        legacyPulseGate = new LegacyPulseGate(config.getLegacyPulseHz());

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
        renderManager = new RenderManager(batch, camera, viewport, game, config);
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
        boolean stepFrame = config.isStepFrame();
        boolean runLegacyPulse = false;

        if (config.isPaused() && !stepFrame) {
            deltaTime = 0;
        }
        if (stepFrame) {
            config.toggleStepFrame();
            runLegacyPulse = true;
        } else if (!config.isPaused()) {
            runLegacyPulse = legacyPulseGate.tryAcquirePulse();
        }

        PerformanceMonitor.startOperation("Render - frame time");

        PerformanceMonitor.startOperation("Render - processing input");
        // Debug controls must remain responsive while paused.
        inputManager.processHostInput(deltaTime);
        PerformanceMonitor.endOperation("Render - processing input");

        // Script-visible input belongs to the gated legacy pump. The explicit
        // plan also preserves BlooMoo's render -> input -> managers order,
        // which cannot be represented by a simple render-before boolean.
        float renderDeltaTime = deltaTime;
        game.getCompatibilityProfile().engine().legacyFrameOrder().execute(
                runLegacyPulse,
                this::processLegacyInput,
                () -> renderFrame(renderDeltaTime),
                this::runLegacyPulse);

        PerformanceMonitor.endOperation("Render - frame time");
    }

    private void processLegacyInput() {
        PerformanceMonitor.startOperation("Render - processing legacy input");
        // Polling this on every 90/120/144 Hz render would accelerate key
        // repeat, mouse-move handlers and button state machines.
        inputManager.processLegacyInput();
        PerformanceMonitor.endOperation("Render - processing legacy input");
    }

    private void runLegacyPulse() {
        PerformanceMonitor.startOperation("Render - updating game state");
        updateManager.pulse();
        PerformanceMonitor.endOperation("Render - updating game state");
    }

    private void renderFrame(float deltaTime) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        PerformanceMonitor.startOperation("Render - rendering");
        // render objects
        renderManager.render(deltaTime);
        PerformanceMonitor.endOperation("Render - rendering");

        PerformanceMonitor.startOperation("Render - rendering debug info");
        // render debug info
        debugManager.render(deltaTime);
        PerformanceMonitor.endOperation("Render - rendering debug info");
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

    public RenderManager getRenderManager() {
        return renderManager;
    }
}
