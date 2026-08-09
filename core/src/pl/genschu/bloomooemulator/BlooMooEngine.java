package pl.genschu.bloomooemulator;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import pl.genschu.bloomooemulator.engine.config.EngineConfig;
import pl.genschu.bloomooemulator.engine.metrics.EngineMetrics;
import pl.genschu.bloomooemulator.logic.GameEntry;
import pl.genschu.bloomooemulator.engine.Game;
import pl.genschu.bloomooemulator.engine.input.InputManager;
import pl.genschu.bloomooemulator.engine.render.RenderManager;
import pl.genschu.bloomooemulator.engine.time.LegacyPulseGate;
import pl.genschu.bloomooemulator.engine.update.UpdateManager;
import pl.genschu.bloomooemulator.engine.debug.DebugManager;
import pl.genschu.bloomooemulator.platform.GcMetricsSource;
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
    private EngineMetrics engineMetrics;
    private GLProfiler glProfiler;

    private final GameEntry gameEntry;
    private final EngineConfig config;
    private final PrinterService printerService;
    private final GcMetricsSource gcMetricsSource;

    public BlooMooEngine(GameEntry gameEntry) {
        this(gameEntry, null, GcMetricsSource.unavailable());
    }

    public BlooMooEngine(GameEntry gameEntry, PrinterService printerService) {
        this(gameEntry, printerService, GcMetricsSource.unavailable());
    }

    public BlooMooEngine(GameEntry gameEntry, PrinterService printerService,
                         GcMetricsSource gcMetricsSource) {
        this.gameEntry = gameEntry;
        this.printerService = printerService;
        this.gcMetricsSource = gcMetricsSource;
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
        engineMetrics = new EngineMetrics(config.getLegacyPulseHz());
        glProfiler = new GLProfiler(Gdx.graphics);

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
        debugManager = new DebugManager(
                batch, camera, game, config, engineMetrics, gcMetricsSource);

        game.setInputManager(inputManager);

        game.loadGame();

        // set log level
        Gdx.app.setLogLevel(config.getLogLevel());
    }

    @Override
    public void render() {
        boolean detailedMetrics = config.isMonitorPerformance();
        engineMetrics.setLevel(detailedMetrics
                ? EngineMetrics.Level.DETAILED
                : EngineMetrics.Level.BASIC);
        setGlProfilingEnabled(detailedMetrics);
        engineMetrics.beginFrame(config.isPaused());

        try {
            renderMeasuredFrame();
        } finally {
            engineMetrics.endFrame();
        }
    }

    private void renderMeasuredFrame() {
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
            LegacyPulseGate.PulseDecision pulseDecision = legacyPulseGate.poll();
            engineMetrics.recordPulse(pulseDecision);
            runLegacyPulse = pulseDecision.admitted();
        }

        engineMetrics.beginPhase(EngineMetrics.Phase.HOST_INPUT);
        try {
            // Debug controls must remain responsive while paused.
            inputManager.processHostInput(deltaTime);
        } finally {
            engineMetrics.endPhase(EngineMetrics.Phase.HOST_INPUT);
        }

        // Script-visible input belongs to the gated legacy pump. The explicit
        // plan also preserves BlooMoo's render -> input -> managers order,
        // which cannot be represented by a simple render-before boolean.
        float renderDeltaTime = deltaTime;
        game.getCompatibilityProfile().engine().legacyFrameOrder().execute(
                runLegacyPulse,
                this::processLegacyInput,
                () -> renderFrame(renderDeltaTime),
                this::runLegacyPulse);
    }

    private void processLegacyInput() {
        engineMetrics.beginPhase(EngineMetrics.Phase.LEGACY_INPUT);
        try {
            // Polling this on every 90/120/144 Hz render would accelerate key
            // repeat, mouse-move handlers and button state machines.
            inputManager.processLegacyInput();
        } finally {
            engineMetrics.endPhase(EngineMetrics.Phase.LEGACY_INPUT);
        }
    }

    private void runLegacyPulse() {
        engineMetrics.beginPhase(EngineMetrics.Phase.MANAGERS);
        try {
            updateManager.pulse();
        } finally {
            engineMetrics.endPhase(EngineMetrics.Phase.MANAGERS);
        }
    }

    private void renderFrame(float deltaTime) {
        if (glProfiler.isEnabled()) {
            // Keep the GL figures scoped to game rendering. The debug overlay
            // itself would otherwise inflate every value it displays.
            glProfiler.reset();
        }

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        engineMetrics.beginPhase(EngineMetrics.Phase.RENDERING);
        try {
            // render objects
            renderManager.render(deltaTime);
        } finally {
            engineMetrics.endPhase(EngineMetrics.Phase.RENDERING);
            recordDetailedRenderMetrics();
        }

        engineMetrics.beginPhase(EngineMetrics.Phase.DEBUG);
        try {
            // render debug info
            debugManager.render(deltaTime);
        } finally {
            engineMetrics.endPhase(EngineMetrics.Phase.DEBUG);
        }
    }

    private void recordDetailedRenderMetrics() {
        if (!glProfiler.isEnabled()) {
            return;
        }

        RenderManager.RenderStats renderStats = renderManager.getLastRenderStats();
        engineMetrics.recordRenderWorkload(
                renderStats.drawableObjects(),
                renderStats.visibleSpriteObjects(),
                renderStats.visibleTextObjects(),
                renderStats.pastedGraphics(),
                renderStats.filteredSprites(),
                renderStats.clippedSprites(),
                renderStats.maskedSprites());
        engineMetrics.recordGlWorkload(
                glProfiler.getCalls(),
                glProfiler.getDrawCalls(),
                glProfiler.getTextureBindings(),
                glProfiler.getShaderSwitches(),
                Math.round(glProfiler.getVertexCount().total));
    }

    private void setGlProfilingEnabled(boolean enabled) {
        if (enabled && !glProfiler.isEnabled()) {
            glProfiler.enable();
        } else if (!enabled && glProfiler.isEnabled()) {
            glProfiler.disable();
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        inputManager.handleResize(width, height);
    }

    @Override
    public void dispose() {
        if (glProfiler != null && glProfiler.isEnabled()) {
            glProfiler.disable();
        }
        batch.dispose();
        game.dispose();
        renderManager.dispose();
        inputManager.dispose();
        updateManager.dispose();
        debugManager.dispose();
        gcMetricsSource.dispose();
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
