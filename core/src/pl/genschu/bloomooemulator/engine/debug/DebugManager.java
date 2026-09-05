package pl.genschu.bloomooemulator.engine.debug;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.TimeUtils;
import pl.genschu.bloomooemulator.engine.Game;
import pl.genschu.bloomooemulator.engine.config.EngineConfig;
import pl.genschu.bloomooemulator.engine.context.CanvasBoundsProvider;
import pl.genschu.bloomooemulator.engine.context.EngineVariable;
import pl.genschu.bloomooemulator.engine.context.GameContext;
import pl.genschu.bloomooemulator.engine.metrics.EngineMetrics;
import pl.genschu.bloomooemulator.geometry.coordinates.CanvasCoordinateSystem;
import pl.genschu.bloomooemulator.geometry.coordinates.CanvasPoint;
import pl.genschu.bloomooemulator.geometry.coordinates.CanvasRect;
import pl.genschu.bloomooemulator.geometry.coordinates.CanvasScroll;
import pl.genschu.bloomooemulator.geometry.coordinates.OpenGlPoint;
import pl.genschu.bloomooemulator.geometry.coordinates.OpenGlRect;
import pl.genschu.bloomooemulator.geometry.coordinates.PhysicsPoint;
import pl.genschu.bloomooemulator.geometry.points.Point3D;
import pl.genschu.bloomooemulator.interpreter.context.Context;
import pl.genschu.bloomooemulator.interpreter.runtime.ASTInterpreter;
import pl.genschu.bloomooemulator.interpreter.values.StringValue;
import pl.genschu.bloomooemulator.interpreter.variable.*;
import pl.genschu.bloomooemulator.platform.GcMetricsSource;
import pl.genschu.bloomooemulator.world.GameObject;
import pl.genschu.bloomooemulator.world.Mesh;
import pl.genschu.bloomooemulator.world.MeshTriangle;
import pl.genschu.bloomooemulator.world.TriangleVertex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

public class DebugManager implements Disposable {
    private static final long RUNTIME_METRICS_INTERVAL_NANOS = 500_000_000L;

    private final SpriteBatch batch;
    private final OrthographicCamera camera;
    private final Game game;
    private final EngineConfig config;
    private final EngineMetrics engineMetrics;
    private final GcMetricsSource gcMetricsSource;

    private final ShapeRenderer shapeRenderer;
    private final BitmapFont font;
    private long lastRuntimeMetricsSampleAt = Long.MIN_VALUE;
    private boolean runtimeMetricsSampling;

    private String tooltipText = "";
    private final Vector2 tooltipPosition = new Vector2();
    private boolean showTooltip = false;

    private String debugVariablesValues = "";
    private CanvasRect debugRect;

    private final Set<String> loggedCollisionMismatches = new HashSet<>();

    private enum SelectorMode { SCENES, ARCADE, CUTSCENE }

    private boolean showSceneSelector = false;
    private SelectorMode selectorMode = SelectorMode.SCENES;
    private final StringBuilder sceneNameInput = new StringBuilder();   // shared filter / name field
    private final Vector2 selectorPosition = new Vector2(10, 30);
    private static final int MAX_VISIBLE_SCENES = 15;

    // SCENES mode: master list + type-to-filter view.
    private final List<String> sceneList = new ArrayList<>();
    private final List<String> filteredScenes = new ArrayList<>();
    private int selectedScene = -1;   // index into filteredScenes
    private int scrollPosition = 0;

    // ARCADE / CUTSCENE name catalog + type-to-filter pick list.
    private final SceneNameCatalog sceneCatalog = new SceneNameCatalog();
    private final List<String> filteredNames = new ArrayList<>();
    private int filteredSelection = -1;   // -1 = load the typed text, not a list item
    private int filteredScroll = 0;
    private static final int MAX_VISIBLE_LOADER = 10;

    // Cached filter state so the view is rebuilt only when the query/mode change.
    private String appliedFilter = null;
    private SelectorMode appliedMode = null;

    private float keyRepeatTimer = 0;
    private boolean keyIsDown = false;
    private final float KEY_INITIAL_DELAY = 0.4f;
    private final float KEY_REPEAT_INTERVAL = 0.1f;

    /**
     * Captures typed characters for the shared filter / name field (scene
     * filter in SCENES mode, scene name in ARCADE / CUTSCENE modes). Installed
     * while the selector is open so it also swallows gameplay input. We capture
     * inline rather than via {@code Gdx.input.getTextInput} because the LWJGL3
     * AWT dialog deadlocks on macOS under {@code -XstartOnFirstThread}.
     */
    private final InputAdapter selectorInputProcessor = new InputAdapter() {
        @Override
        public boolean keyTyped(char character) {
            if (!showSceneSelector) {
                return false;
            }
            if (character == '\b') {
                if (sceneNameInput.length() > 0) {
                    sceneNameInput.deleteCharAt(sceneNameInput.length() - 1);
                }
            } else if (character >= 32 && character != 127) {
                sceneNameInput.append(character);
            }
            // The change is picked up by ensureFilter() next frame, which rebuilds
            // the filtered view and resets the selection.
            return true;
        }
    };


    public DebugManager(SpriteBatch batch, OrthographicCamera camera, Game game, EngineConfig config,
                        EngineMetrics engineMetrics, GcMetricsSource gcMetricsSource) {
        this.batch = batch;
        this.camera = camera;
        this.game = game;
        this.config = config;
        this.engineMetrics = engineMetrics;
        this.gcMetricsSource = gcMetricsSource;

        this.shapeRenderer = new ShapeRenderer();
        this.font = new BitmapFont();
    }

    public void render(float deltaTime) {
        EngineMetrics.Snapshot metricsSnapshot = null;
        if (config.isMonitorPerformance() || config.isShowFpsCounter()) {
            if (config.isMonitorPerformance()) {
                recordRuntimeMetricsIfDue();
            }
            metricsSnapshot = engineMetrics.snapshot();
        }
        if (!config.isMonitorPerformance()) {
            runtimeMetricsSampling = false;
        }

        // render if in debug mode
        if (config.isDebugGraphics()) {
            renderGraphicsDebug();
        }

        if (config.isDebugGraphicsBounds()) {
            renderObjectBoundingBoxes();
        }

        if (config.isDebugVariables()) {
            renderVariablesDebug();
        }

        if (config.isMonitorPerformance()) {
            renderMonitorPerformance(metricsSnapshot);
        }

        if (config.isDebugWorld()) {
            renderMeshDebug();
        }

        if (config.isDebugButtons()) {
            renderButtonBorders();
        }

        if (config.isDebugCollisions()) {
            renderCollisionDebug();
        }

        if (debugRect != null) {
            renderDebugRectangle();
        }

        if (showTooltip && !tooltipText.isEmpty()) {
            renderTooltip();
        }
        else if(!showTooltip) {
            tooltipText = "";
        }

        if (showSceneSelector) {
            renderSceneSelector();
        }

        if (config.isDebugMatrix()) {
            for (EngineVariable ev : game.getCurrentSceneContext().getVariables().values()) {
                if (ev instanceof MatrixVariable mv) {
                    debugMatrix(mv);
                }
            }
        }

        if (config.isShowFpsCounter()) {
            renderFpsCounter(metricsSnapshot);
        }
    }

    private void recordRuntimeMetricsIfDue() {
        long now = TimeUtils.nanoTime();
        if (!runtimeMetricsSampling) {
            runtimeMetricsSampling = true;
            lastRuntimeMetricsSampleAt = Long.MIN_VALUE;
        }
        if (lastRuntimeMetricsSampleAt != Long.MIN_VALUE
                && now >= lastRuntimeMetricsSampleAt
                && now - lastRuntimeMetricsSampleAt < RUNTIME_METRICS_INTERVAL_NANOS) {
            return;
        }
        lastRuntimeMetricsSampleAt = now;

        Runtime runtime = Runtime.getRuntime();
        long nativeHeap = Gdx.app.getType() == Application.ApplicationType.Android
                ? Gdx.app.getNativeHeap()
                : 0L;
        engineMetrics.recordMemory(
                Gdx.app.getJavaHeap(),
                runtime.totalMemory(),
                runtime.maxMemory(),
                nativeHeap);
        engineMetrics.recordGarbageCollection(gcMetricsSource.sample());
    }

    private void renderObjectBoundingBoxes() {
        GameContext context = game.getCurrentSceneContext();
        List<EngineVariable> graphics = new ArrayList<>(context.getGraphicsVariables().values());

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        batch.begin();
        font.setColor(Color.WHITE);

        for (EngineVariable variable : graphics) {
            CanvasRect rect = canvasBoundsOf(variable);
            if (rect == null) continue;

            boolean visible = false;
            boolean inQuadTree = false;
            if (variable instanceof ImageVariable img) {
                visible = img.isVisible();
                inQuadTree = img.state().monitorCollision;
            } else if (variable instanceof AnimoVariable animo) {
                visible = animo.isVisible();
                inQuadTree = animo.isMonitorCollision();
            }

            if (visible) {
                if(inQuadTree) {
                    shapeRenderer.setColor(Color.MAGENTA);
                } else {
                    shapeRenderer.setColor(Color.RED);
                }
            } else {
                shapeRenderer.setColor(Color.GRAY);
            }
            drawCanvasRect(shapeRenderer, rect);

            String name = variable.getName();
            OpenGlPoint topLeft = CanvasCoordinateSystem.toOpenGl(rect.topLeft());
            font.draw(
                    batch,
                    name,
                    asFloat(topLeft.x()),
                    asFloat(topLeft.y()) + 15
            );
        }

        batch.end();
        shapeRenderer.end();
    }

    private void renderGraphicsDebug() {
        // Get info about graphics under cursor
        Optional<CanvasPoint> mousePosition = getMousePosition();
        if (mousePosition.isEmpty()) {
            showTooltip = false;
            return;
        }

        CanvasPoint mousePos = mousePosition.get();
        int mouseX = (int) Math.floor(mousePos.x());
        int mouseY = (int) Math.floor(mousePos.y());
        EngineVariable graphicsUnderCursor = getGraphicsAt(mouseX, mouseY);
        EngineVariable buttonUnderCursor = getButtonAt(mouseX, mouseY);

        if (buttonUnderCursor != null) {
            if(buttonUnderCursor instanceof ButtonVariable btn) {
                generateTooltipForButton(btn);
            }
            else {
                generateTooltipForGraphics(buttonUnderCursor);
            }
            setTooltipPosition(mousePos);
            showTooltip = true;
        }
        else if (graphicsUnderCursor != null) {
            generateTooltipForGraphics(graphicsUnderCursor);
            setTooltipPosition(mousePos);
            showTooltip = true;
        } else {
            showTooltip = false;
        }
    }

    private void renderVariablesDebug() {
        generateDebugVariablesText();

        batch.begin();
        font.setColor(Color.WHITE);
        font.draw(batch, debugVariablesValues, 5,
                asFloat(CanvasCoordinateSystem.toOpenGlY(5)));
        batch.end();
    }

    private void renderMonitorPerformance(EngineMetrics.Snapshot snapshot) {
        StringBuilder performanceMetrics = new StringBuilder("=== Runtime Metrics [F6] ===\n");
        performanceMetrics.append(String.format(Locale.ROOT,
                "Host: %.1f FPS | avg %.2f ms | p95 %.2f | p99 %.2f | max %.2f\n",
                snapshot.hostFps(),
                snapshot.frameTimes().averageMs(),
                snapshot.frameTimes().p95Ms(),
                snapshot.frameTimes().p99Ms(),
                snapshot.frameTimes().maxMs()));

        if (snapshot.paused()) {
            performanceMetrics.append("Pulse: PAUSED\n");
        } else if (snapshot.targetPulseHz() > 0) {
            performanceMetrics.append(String.format(Locale.ROOT,
                    "Pulse: %.1f/%d Hz | missed %d\n",
                    snapshot.pulseHz(),
                    snapshot.targetPulseHz(),
                    snapshot.missedPulsePeriods()));
            performanceMetrics.append(String.format(Locale.ROOT,
                    "Cadence: jitter p99 %.2f ms | late p99 %.2f | max %.2f ms\n",
                    snapshot.pulseJitterP99Ms(),
                    snapshot.pulseLatenessP99Ms(),
                    snapshot.pulseLatenessMaxMs()));
        } else {
            performanceMetrics.append(String.format(Locale.ROOT,
                    "Pulse: %.1f Hz | missed %d\n",
                    snapshot.pulseHz(), snapshot.missedPulsePeriods()));
        }

        performanceMetrics.append("Stalls: >33 ms ")
                .append(snapshot.stallsOver33Ms())
                .append(" | >50 ms ")
                .append(snapshot.stallsOver50Ms())
                .append('\n');

        EngineMetrics.RenderStats render = snapshot.render();
        performanceMetrics.append(String.format(Locale.ROOT,
                "Scene: drawables %d | sprites %d | text %d | pasted %d\n",
                render.drawableObjects(), render.visibleSpriteObjects(),
                render.visibleTextObjects(), render.pastedGraphics()));
        performanceMetrics.append(String.format(Locale.ROOT,
                "Paths: filters %d | clips %d | alpha masks %d\n",
                render.filteredSprites(), render.clippedSprites(), render.maskedSprites()));

        EngineMetrics.GlStats gl = snapshot.gl();
        performanceMetrics.append(String.format(Locale.ROOT,
                "GL: draw %d | calls %d | binds %d | shaders %d | submitted %d\n",
                gl.drawCalls(), gl.calls(), gl.textureBindings(),
                gl.shaderSwitches(), gl.submittedVertices()));

        EngineMetrics.MemoryStats memory = snapshot.memory();
        performanceMetrics.append(String.format(Locale.ROOT,
                "Heap: %.1f / %.1f / %.1f MiB (used/committed/max)",
                toMebibytes(memory.javaHeapUsedBytes()),
                toMebibytes(memory.javaHeapCommittedBytes()),
                toMebibytes(memory.javaHeapMaxBytes())));
        if (memory.nativeHeapUsedBytes() > 0L) {
            performanceMetrics.append(String.format(Locale.ROOT,
                    " | native %.1f MiB", toMebibytes(memory.nativeHeapUsedBytes())));
        }
        performanceMetrics.append('\n');

        appendGarbageCollectionMetrics(performanceMetrics, snapshot.gc());
        performanceMetrics.append("CPU phase: avg | p95 | max ms\n");

        for (EngineMetrics.Phase phase : EngineMetrics.Phase.values()) {
            EngineMetrics.TimingStats timing = snapshot.phase(phase);
            performanceMetrics.append(String.format(Locale.ROOT,
                    "%-12s %6.2f | %6.2f | %6.2f\n",
                    phase.displayName(), timing.averageMs(), timing.p95Ms(), timing.maxMs()));
        }

        batch.begin();
        font.setColor(Color.WHITE);
        font.draw(batch, performanceMetrics, 310,
                asFloat(CanvasCoordinateSystem.toOpenGlY(5)));
        batch.end();
    }

    private static void appendGarbageCollectionMetrics(
            StringBuilder target, EngineMetrics.GcStats gc) {
        if (!gc.available()) {
            target.append("GC: unavailable\n");
            return;
        }

        target.append(String.format(Locale.ROOT,
                "GC: %.1f/min | time %.2f%% | total %d / %d ms",
                gc.collectionsPerMinute(), gc.recentTimePercent(),
                gc.collectionCount(), gc.collectionTimeMillis()));
        if (gc.lastCollectionAgeMillis() >= 0L) {
            target.append(String.format(Locale.ROOT,
                    " | last %.1f s", gc.lastCollectionAgeMillis() / 1000.0));
        }
        target.append('\n');

        if (gc.allocatedBytesPerSecond() >= 0.0) {
            target.append(String.format(Locale.ROOT,
                    "Alloc: %.1f MiB/s", toMebibytes(gc.allocatedBytesPerSecond())));
            if (gc.reclaimedBytesPerSecond() >= 0.0) {
                target.append(String.format(Locale.ROOT,
                        " | reclaim%s %.1f MiB/s",
                        gc.reclaimedEstimated() ? "~" : "",
                        toMebibytes(gc.reclaimedBytesPerSecond())));
            }
            target.append('\n');
        }

        if (gc.blockingStatsAvailable()) {
            target.append(String.format(Locale.ROOT,
                    "Blocking GC: %.1f/min | time %.2f%% | total %d / %d ms\n",
                    gc.blockingCollectionsPerMinute(), gc.recentBlockingTimePercent(),
                    gc.blockingCollectionCount(), gc.blockingCollectionTimeMillis()));
        }
    }

    private static double toMebibytes(long bytes) {
        return bytes / (1024.0 * 1024.0);
    }

    private static double toMebibytes(double bytes) {
        return bytes / (1024.0 * 1024.0);
    }

    private void renderFpsCounter(EngineMetrics.Snapshot snapshot) {
        String text;
        if (snapshot.paused()) {
            text = String.format(Locale.ROOT,
                    "%.0f FPS | pulse PAUSED | p99 %.1f ms",
                    snapshot.hostFps(), snapshot.frameTimes().p99Ms());
        } else if (snapshot.targetPulseHz() > 0) {
            text = String.format(Locale.ROOT,
                    "%.0f FPS | pulse %.1f/%d | p99 %.1f ms | missed %d",
                    snapshot.hostFps(), snapshot.pulseHz(), snapshot.targetPulseHz(),
                    snapshot.frameTimes().p99Ms(), snapshot.missedPulsePeriods());
        } else {
            text = String.format(Locale.ROOT,
                    "%.0f FPS | pulse %.1f Hz | p99 %.1f ms",
                    snapshot.hostFps(), snapshot.pulseHz(), snapshot.frameTimes().p99Ms());
        }
        GlyphLayout layout = new GlyphLayout(font, text);
        float x = 8;
        float y = asFloat(CanvasCoordinateSystem.toOpenGlY(8));
        float paddingX = 6;
        float paddingY = 4;

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(new Color(0, 0, 0, 0.65f));
        shapeRenderer.rect(
                x - paddingX,
                y - layout.height - paddingY,
                layout.width + paddingX * 2,
                layout.height + paddingY * 2
        );
        shapeRenderer.end();

        batch.begin();
        font.setColor(metricsColor(snapshot));
        font.draw(batch, text, x, y);
        batch.end();
    }

    private Color metricsColor(EngineMetrics.Snapshot snapshot) {
        if (snapshot.hostFps() <= 0.0 || snapshot.paused()) {
            return Color.WHITE;
        }
        if (snapshot.missedPulsePeriods() > 0) {
            return Color.RED;
        }
        if (snapshot.targetPulseHz() > 0 && snapshot.pulseHz() > 0.0
                && snapshot.pulseHz() < snapshot.targetPulseHz() * 0.95) {
            return Color.YELLOW;
        }

        int targetFps = config.getTargetFPS();
        if (targetFps <= 0 || snapshot.frameTimes().p99Ms() <= 0.0) {
            return Color.GREEN;
        }
        double frameBudgetMs = 1000.0 / targetFps;
        if (snapshot.frameTimes().p99Ms() <= frameBudgetMs * 1.10) return Color.GREEN;
        if (snapshot.frameTimes().p99Ms() <= frameBudgetMs * 2.0) return Color.YELLOW;
        return Color.RED;
    }

    private void renderDebugRectangle() {
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.RED);
        drawCanvasRect(shapeRenderer, debugRect);
        shapeRenderer.end();
    }

    private void renderTooltip() {
        GlyphLayout layout = new GlyphLayout(font, tooltipText);
        float width = layout.width + 10;
        float height = layout.height + 10;

        float x = tooltipPosition.x;
        float y = tooltipPosition.y;

        // Check if tooltip is out of bounds
        if (x + width > camera.viewportWidth) {
            x = camera.viewportWidth - width;
        }

        if (y - height < 0) {
            y = height;
        }

        // Render tooltip background
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(new Color(0, 0, 0, 0.75f));
        shapeRenderer.rect(x, y - height, width, height);
        shapeRenderer.end();

        // Render tooltip border
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(x, y - height, width, height);
        shapeRenderer.end();

        // Render tooltip text
        batch.begin();
        font.setColor(Color.WHITE);
        font.draw(batch, tooltipText, x + 5, y - 5);
        batch.end();
    }

    private void renderButtonBorders() {
        List<EngineVariable> buttons = new ArrayList<>(game.getCurrentSceneContext().getButtonVariablesForInput());
        Optional<CanvasPoint> mousePosition = getMousePosition();

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        for (EngineVariable variable : buttons) {
            CanvasRect rect = null;
            boolean buttonEnabled = true;

            if (variable instanceof ButtonVariable btn) {
                if (!btn.isEnabled()) buttonEnabled = false;
                rect = btn.getRect();
            } else if (variable instanceof AnimoVariable animo) {
                rect = animo.getRect();
            }

            if (rect != null) {
                if (!buttonEnabled) {
                    shapeRenderer.setColor(Color.GRAY);
                }
                else if (mousePosition.map(rect::contains).orElse(false)) {
                    shapeRenderer.setColor(Color.GREEN);
                } else {
                    shapeRenderer.setColor(Color.RED);
                }

                drawCanvasRect(shapeRenderer, rect);
            }
        }

        shapeRenderer.end();

        // Labels next to buttons
        batch.begin();
        font.setColor(Color.WHITE);
        for (EngineVariable variable : buttons) {
            CanvasRect rect = null;
            if (variable instanceof ButtonVariable btn) {
                rect = btn.getRect();
            } else if (variable instanceof AnimoVariable animo) {
                rect = animo.getRect();
            }
            if (rect == null) continue;
            OpenGlRect openGlRect = CanvasCoordinateSystem.toOpenGl(rect);
            font.draw(batch, variable.getName(),
                    asFloat(openGlRect.x()),
                    asFloat(openGlRect.y()) - 3);
        }
        batch.end();
    }

    private void renderCollisionDebug() {
        Set<EngineVariable> monitored = game.getCollisionMonitoredVariables();
        if (monitored.isEmpty()) {
            renderCollisionStats(0, 0, 0, 0);
            return;
        }

        List<EngineVariable> snapshot = new ArrayList<>(monitored);
        Set<EngineVariable> dirty = game.getDirtyCollisionObjects();

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        // QuadTree root bounds are the fixed DirectDraw canvas.
        shapeRenderer.setColor(Color.DARK_GRAY);
        drawCanvasRect(shapeRenderer, CanvasCoordinateSystem.BOUNDS);

        // Rect per monitored variable, colored by status
        int collidingCount = 0;
        for (EngineVariable variable : snapshot) {
            CanvasRect rect = canvasBoundsOf(variable);
            if (rect == null) continue;

            boolean isDirty = dirty.contains(variable);
            boolean isColliding = !game.getCollidingWith(variable).isEmpty();

            if (isColliding) {
                shapeRenderer.setColor(Color.YELLOW);
                collidingCount++;
            } else if (isDirty) {
                shapeRenderer.setColor(Color.CYAN);
            } else {
                shapeRenderer.setColor(Color.MAGENTA);
            }

            drawCanvasRect(shapeRenderer, rect);
        }

        // Lines between currently-registered colliding pairs
        shapeRenderer.setColor(Color.GREEN);
        Set<String> drawnPairs = new HashSet<>();
        int activePairs = 0;
        for (EngineVariable variable : snapshot) {
            CanvasRect a = canvasBoundsOf(variable);
            if (a == null) continue;
            for (EngineVariable other : game.getCollidingWith(variable)) {
                String key = pairKey(variable, other);
                if (!drawnPairs.add(key)) continue;
                CanvasRect b = canvasBoundsOf(other);
                if (b == null) continue;
                drawCanvasLine(shapeRenderer, a.center(), b.center());
                activePairs++;
            }
        }

        // Retrieval correctness: for every intersecting pair of monitored rects,
        // check whether the QuadTree actually surfaces it. Hidden pairs are the
        // real "why wasn't this collision detected" signal.
        int hiddenPairs = 0;
        shapeRenderer.setColor(Color.ORANGE);
        for (int i = 0; i < snapshot.size(); i++) {
            EngineVariable vi = snapshot.get(i);
            CanvasRect a = canvasBoundsOf(vi);
            if (a == null) continue;
            Set<EngineVariable> retrieved = null;
            for (int j = i + 1; j < snapshot.size(); j++) {
                EngineVariable vj = snapshot.get(j);
                CanvasRect b = canvasBoundsOf(vj);
                if (b == null) continue;
                if (!a.intersects(b)) continue;

                if (retrieved == null) {
                    retrieved = new HashSet<>(
                            game.getQuadTree().retrieve(new ArrayList<>(), vi));
                }
                if (!retrieved.contains(vj)) {
                    hiddenPairs++;
                    drawCanvasLine(shapeRenderer, a.center(), b.center());
                    String key = pairKey(vi, vj);
                    if (loggedCollisionMismatches.add(key)) {
                        Gdx.app.log("CollisionDebug",
                                "QuadTree missed overlap: " + vi.getName()
                                        + " vs " + vj.getName()
                                        + " | a=" + a + " b=" + b);
                    }
                }
            }
        }

        shapeRenderer.end();

        // Labels next to rects (separate batch pass)
        batch.begin();
        font.setColor(Color.WHITE);
        for (EngineVariable variable : snapshot) {
            CanvasRect rect = canvasBoundsOf(variable);
            if (rect == null) continue;
            OpenGlRect openGlRect = CanvasCoordinateSystem.toOpenGl(rect);
            font.draw(batch, variable.getName(),
                    asFloat(openGlRect.x()),
                    asFloat(openGlRect.y()) - 3);
        }
        batch.end();

        renderCollisionStats(snapshot.size(), dirty.size(), activePairs, hiddenPairs);
    }

    private void renderCollisionStats(int monitored, int dirty, int activePairs, int hiddenPairs) {
        String stats = "Collisions [F10]"
                + "\nMonitored: " + monitored
                + "\nDirty: " + dirty
                + "\nActive pairs: " + activePairs
                + "\nQuadTree misses: " + hiddenPairs;

        batch.begin();
        font.setColor(hiddenPairs > 0 ? Color.ORANGE : Color.WHITE);
        font.draw(batch, stats, 5,
                asFloat(CanvasCoordinateSystem.toOpenGlY(120)));
        batch.end();
    }

    private static String pairKey(EngineVariable a, EngineVariable b) {
        String na = a.getName();
        String nb = b.getName();
        return na.compareTo(nb) <= 0 ? na + "|" + nb : nb + "|" + na;
    }

    private static CanvasRect canvasBoundsOf(EngineVariable variable) {
        return variable instanceof CanvasBoundsProvider boundsProvider
                ? boundsProvider.getCanvasBounds()
                : null;
    }

    private static void drawCanvasRect(ShapeRenderer renderer, CanvasRect rect) {
        OpenGlRect openGlRect = CanvasCoordinateSystem.toOpenGl(rect);
        renderer.rect(
                asFloat(openGlRect.x()),
                asFloat(openGlRect.y()),
                asFloat(openGlRect.width()),
                asFloat(openGlRect.height()));
    }

    private static void drawCanvasLine(
            ShapeRenderer renderer,
            CanvasPoint from,
            CanvasPoint to
    ) {
        OpenGlPoint openGlFrom = CanvasCoordinateSystem.toOpenGl(from);
        OpenGlPoint openGlTo = CanvasCoordinateSystem.toOpenGl(to);
        renderer.line(
                asFloat(openGlFrom.x()), asFloat(openGlFrom.y()),
                asFloat(openGlTo.x()), asFloat(openGlTo.y()));
    }

    private static OpenGlPoint physicsToOpenGl(
            double worldX,
            double worldY,
            CanvasScroll scroll
    ) {
        return CanvasCoordinateSystem.physicsToOpenGl(
                new PhysicsPoint(worldX, worldY, 0), scroll);
    }

    private static float asFloat(double value) {
        return (float) value;
    }

    private void debugMatrix(MatrixVariable matrixVariable) {
        MatrixVariable.MatrixState ms = matrixVariable.state();
        if (ms.data == null) return;

        int width = ms.width;
        int height = ms.height;
        int cellWidth = ms.cellWidth;
        int cellHeight = ms.cellHeight;
        float startX = ms.basePosX;
        float startY = ms.basePosY;

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int index = i * width + j;
                if (index >= ms.data.length) continue;

                int value = ms.data[index];

                float x = startX + j * cellWidth;
                float y = startY + i * cellHeight;

                switch (value) {
                    case 0 -> shapeRenderer.setColor(0.1f, 0.1f, 0.1f, 1f);
                    case 1 -> shapeRenderer.setColor(0.5f, 0.35f, 0.05f, 1f);
                    case 2 -> shapeRenderer.setColor(0.6f, 0.6f, 0.6f, 1f);
                    case 3 -> shapeRenderer.setColor(1f, 0f, 0f, 1f);
                    case 4 -> shapeRenderer.setColor(0.7f, 0.5f, 0.3f, 1f);
                    case 5 -> shapeRenderer.setColor(1f, 0.5f, 0f, 1f);
                    case 6 -> shapeRenderer.setColor(0.3f, 0.3f, 0.3f, 1f);
                    case 7 -> shapeRenderer.setColor(1f, 0.3f, 0.3f, 1f);
                    case 8 -> shapeRenderer.setColor(1f, 1f, 0f, 1f);
                    case 9 -> shapeRenderer.setColor(0f, 1f, 0f, 1f);
                    case 99 -> shapeRenderer.setColor(0f, 0f, 1f, 1f);
                    default -> shapeRenderer.setColor(0.8f, 0.8f, 0.8f, 1f);
                }

                shapeRenderer.rect(
                        x,
                        asFloat(CanvasCoordinateSystem.toOpenGlBottom(y, cellHeight)),
                        cellWidth - 1,
                        cellHeight - 1);
            }
        }

        shapeRenderer.end();

        batch.begin();
        font.setColor(Color.WHITE);

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int index = i * width + j;
                if (index >= ms.data.length) continue;

                String value = String.valueOf(ms.data[index]);

                float x = startX + j * cellWidth + 4;
                float y = asFloat(CanvasCoordinateSystem.toOpenGlY(
                        startY + i * cellHeight + 15));

                font.draw(batch, value, x, y);
            }
        }

        batch.end();
    }

    private void drawVelocityArrow(ShapeRenderer sr, GameObject go, CanvasScroll scroll) {
        sr.setColor(Color.CYAN);
        final float vx = go.getVelX();
        final float vy = go.getVelY();

        final float speed = (float)Math.hypot(vx, vy);
        if (speed < 1e-3f) return;

        final float baseLen   = 12f;
        final float scaleLen  = 0.25f;
        final float maxLen    = 80f;
        final float headFrac  = 0.25f;
        final float headAngle = (float)Math.toRadians(25);

        final float dx = vx / speed;
        final float dy = vy / speed;

        float len = Math.min(maxLen, baseLen + scaleLen * speed);

        OpenGlPoint origin = physicsToOpenGl(go.getX(), go.getY(), scroll);
        final float x0 = asFloat(origin.x());
        final float y0 = asFloat(origin.y());
        final float x1 = x0 + dx * len;
        final float y1 = y0 + dy * len;

        sr.line(x0, y0, x1, y1);

        final float headLen = len * headFrac;
        final float cosA = (float)Math.cos(headAngle);
        final float sinA = (float)Math.sin(headAngle);

        final float pLx =  dx * cosA - dy * sinA;
        final float pLy =  dx * sinA + dy * cosA;
        final float pRx =  dx * cosA + dy * sinA;
        final float pRy = -dx * sinA + dy * cosA;

        sr.line(x1, y1, x1 - pLx * headLen, y1 - pLy * headLen);
        sr.line(x1, y1, x1 - pRx * headLen, y1 - pRy * headLen);
    }

    private void drawPath(ShapeRenderer sr, GameObject go, CanvasScroll scroll) {
        Deque<Point3D> path = go.getPath();
        if (path == null || path.size() < 2) return;

        sr.setColor(Color.RED);
        Point3D prev = null;
        for (Point3D p : path) {
            if (prev != null) {
                OpenGlPoint from = physicsToOpenGl(prev.x, prev.y, scroll);
                OpenGlPoint to = physicsToOpenGl(p.x, p.y, scroll);
                sr.line(
                        asFloat(from.x()), asFloat(from.y()),
                        asFloat(to.x()), asFloat(to.y()));
            }
            prev = p;
        }
    }

    private static float[] rot2D(float x, float y, float c, float s) {
        return new float[] { c*x - s*y, s*x + c*y };
    }

    private void renderMeshDebug() {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.GREEN);

        EngineVariable worldEV = game.getCurrentSceneContext().getWorldVariable();
        if (!(worldEV instanceof WorldVariable world)) {
            shapeRenderer.end();
            return;
        }
        List<GameObject> objects = world.getPhysicsEngine().getGameObjects();

        CanvasScroll scroll = world.getPhysicsEngine().getCanvasScroll();

        for (GameObject go : objects) {
            Mesh mesh = go.getMesh();
            final float x = go.getX();
            final float y = go.getY();
            final float theta = go.getRotationZ();
            final float c = (float)Math.cos(theta);
            final float s = (float)Math.sin(theta);

            if (mesh == null) {
                final float[] d = go.getDimensions();

                OpenGlPoint center = physicsToOpenGl(x, y, scroll);
                final float correctedX = asFloat(center.x());
                final float correctedY = asFloat(center.y());

                switch (go.getGeomType()) {
                    case 0: // box
                        float halfW = (d[0] > 0 ? d[0] : 1f) * 0.5f;
                        float halfH = (d[1] > 0 ? d[1] : 1f) * 0.5f;

                        float[][] corners = {
                                {-halfW, -halfH},
                                { halfW, -halfH},
                                { halfW,  halfH},
                                {-halfW,  halfH}
                        };

                        float[][] wc = new float[4][2];
                        for (int i=0;i<4;i++){
                            float[] r2 = rot2D(corners[i][0], corners[i][1], c, s);
                            wc[i][0] = correctedX + r2[0];
                            wc[i][1] = correctedY + r2[1];
                        }

                        shapeRenderer.line(wc[0][0], wc[0][1], wc[1][0], wc[1][1]);
                        shapeRenderer.line(wc[1][0], wc[1][1], wc[2][0], wc[2][1]);
                        shapeRenderer.line(wc[2][0], wc[2][1], wc[3][0], wc[3][1]);
                        shapeRenderer.line(wc[3][0], wc[3][1], wc[0][0], wc[0][1]);
                        break;
                    case 1: // cylinder
                    case 2: // box
                        float r = d[0] > 0 ? d[0] : 1f;
                        shapeRenderer.circle(correctedX, correctedY, r, 24);
                        break;
                    default:
                        shapeRenderer.line(correctedX-4, correctedY, correctedX+4, correctedY);
                        shapeRenderer.line(correctedX, correctedY-4, correctedX, correctedY+4);
                }
            }
            else {
                for (MeshTriangle tri : mesh.getTriangles()) {
                    TriangleVertex[] vs = tri.getVertices();

                    OpenGlPoint p0 = meshVertexToOpenGl(vs[0], go, x, y, c, s, scroll);
                    OpenGlPoint p1 = meshVertexToOpenGl(vs[1], go, x, y, c, s, scroll);
                    OpenGlPoint p2 = meshVertexToOpenGl(vs[2], go, x, y, c, s, scroll);

                    shapeRenderer.line(
                            asFloat(p0.x()), asFloat(p0.y()),
                            asFloat(p1.x()), asFloat(p1.y()));
                    shapeRenderer.line(
                            asFloat(p1.x()), asFloat(p1.y()),
                            asFloat(p2.x()), asFloat(p2.y()));
                    shapeRenderer.line(
                            asFloat(p2.x()), asFloat(p2.y()),
                            asFloat(p0.x()), asFloat(p0.y()));
                }
            }
            drawVelocityArrow(shapeRenderer, go, scroll);
            drawPath(shapeRenderer, go, scroll);
        }

        shapeRenderer.end();
    }

    private static OpenGlPoint meshVertexToOpenGl(
            TriangleVertex vertex,
            GameObject object,
            float objectX,
            float objectY,
            float rotationCos,
            float rotationSin,
            CanvasScroll scroll
    ) {
        float[] rotated = rot2D(
                (float) vertex.getPoint().x,
                (float) vertex.getPoint().y,
                rotationCos,
                rotationSin);
        double worldX = object.isRigidBody() ? objectX + rotated[0] : rotated[0];
        double worldY = object.isRigidBody() ? objectY + rotated[1] : rotated[1];
        return physicsToOpenGl(worldX, worldY, scroll);
    }



    private void generateTooltipForButton(ButtonVariable button) {
        StringBuilder sb = new StringBuilder();

        sb.append("Button: ").append(button.name()).append("\n");
        sb.append("Active: ").append(button.isEnabled()).append("\n");
        sb.append("State: ").append(button.getButtonState()).append("\n");

        String gfxName = button.getCurrentGfxName();
        if (gfxName != null) {
            sb.append("\nCurrent GFX: ").append(gfxName).append("\n");
        } else {
            sb.append("\nNo GFX\n");
        }

        CanvasRect rect = button.getRect();
        String rectText = "\nRect: " + (rect != null ?
                ("\n    left upper corner: (" + rect.left() + ", " + rect.top() + ")" +
                        "\n    width: " + rect.width() +
                        "\n    height: " + rect.height()) :
                "no defined");
        sb.append(rectText);

        ButtonVariable.ButtonVarState bs = button.state();
        if (bs.sndOnMoveName != null) {
            sb.append("\nSound on hover: ").append(bs.sndOnMoveName).append("\n");
        }
        if (bs.sndOnClickName != null) {
            sb.append("\nSound on click: ").append(bs.sndOnClickName).append("\n");
        }

        sb.append("\nSignals: ");
        boolean hasSignals = false;
        for (String signalName : List.of("ONCLICKED", "ONACTION", "ONFOCUSON", "ONFOCUSOFF", "ONRELEASED")) {
            if (button.getSignal(signalName) != null) {
                if (hasSignals) sb.append(", ");
                sb.append(signalName);
                hasSignals = true;
            }
        }
        if (!hasSignals) sb.append("no signals");

        tooltipText = sb.toString();
    }

    private void generateTooltipForGraphics(EngineVariable graphics) {
        StringBuilder sb = new StringBuilder();
        sb.append(graphics.getName()).append(" (").append(graphics.getTypeName()).append(")\n");

        CanvasRect rect = canvasBoundsOf(graphics);
        String rectText = "\nRect: " + (rect != null ?
                ("\n    left upper corner: (" + rect.left() + ", " + rect.top() + ")" +
                        "\n    width: " + rect.width() +
                        "\n    height: " + rect.height()) :
                "no defined");

        if (graphics instanceof AnimoVariable animo) {
            sb.append("Priority: ").append(animo.getPriority());
            sb.append("\nCurrent FPS: ").append(animo.getFps());
            sb.append("\nCurrent event: ").append(animo.getCurrentEvent() != null ?
                    animo.getCurrentEvent().getName() : "none");
            sb.append("\nCurrent frame number: ").append(animo.getCurrentFrameNumber());
            sb.append("\nCurrent image number: ").append(animo.getCurrentImageNumber());
            sb.append("\nIs playing: ").append(animo.isPlaying());
            sb.append("\nIs button: ").append(isGraphicsButton(graphics));
            sb.append("\nIs visible: ").append(animo.isVisible());
            sb.append("\nIs rendered on canvas: ").append(animo.isRenderedOnCanvas());
            sb.append("\nCollision monitoring: ").append(animo.isMonitorCollision());
            sb.append(rectText);
        } else if (graphics instanceof ImageVariable img) {
            sb.append("Priority: ").append(img.state().priority);
            sb.append("\nIs button: ").append(isGraphicsButton(graphics));
            sb.append("\nIs visible: ").append(img.isVisible());
            sb.append("\nCollision monitoring: ").append(img.state().monitorCollision);
            sb.append(rectText);
        } else {
            sb.append(rectText);
        }

        tooltipText = sb.toString();
    }

    private void generateDebugVariablesText() {
        StringBuilder sb = new StringBuilder();
        GameContext context = game.getCurrentSceneContext();
        MethodContext methodContext = new ASTInterpreter((Context) context).getMethodContext();

        sb.append("Scena: ").append(game.getCurrentScene()).append("\n");

        switch(game.getCurrentScene()) {
            case "ARCADE" -> {
                sb.append("Sekcja interaktywna: ");
                String arcadeScene = switch(SceneLoaderScripts.familyId(game.getGame().getGameName())) {
                    case "CZARODZIEJE" -> ((StringVariable) context.getVariable("G_SARCADEOBJECTS")).getString();
                    case "WEHIKUL" -> ((StringVariable) context.getVariable("G_SARCADESCENE")).getString();
                    case "NEMO" -> ((Variable) context.getVariable("GSAVE"))
                            .callMethod("GET", List.of(new StringValue("ARCADE_SCENE_NAME")), methodContext)
                            .getReturnValue()
                            .toDisplayString();
                    default -> "NULL";
                };
                sb.append(arcadeScene).append("\n");
            }
            case "CUTSCENKI" -> {
                sb.append("Cutscenka: ");
                String cutsceneScene = switch(SceneLoaderScripts.familyId(game.getGame().getGameName())) {
                    case "NEMO" -> ((Variable) context.getVariable("GSAVE"))
                            .callMethod("GET", List.of(new StringValue("CS_SCENE_NAME")), methodContext)
                            .getReturnValue()
                            .toDisplayString();
                    case "KRETES" -> ((Variable) context.getVariable("GSAVE"))
                            .callMethod("GET", List.of(new StringValue("CS_NAME")), methodContext)
                            .getReturnValue()
                            .toDisplayString();
                    default -> "NULL";
                };
                sb.append(cutsceneScene).append("\n");
            }
        }

        sb.append("\n");

        for (EngineVariable ev : context.getVariables().values()) {
            if (!(ev instanceof Variable variable)) continue;

            switch (variable.type()) {
                case INTEGER, DOUBLE, BOOLEAN, STRING ->
                    sb.append(variable.name())
                            .append(" (").append(variable.type()).append(") = ")
                            .append(variable.value()).append("\n");

                case TIMER -> {
                    TimerVariable timer = (TimerVariable) variable;
                    sb.append(variable.name())
                            .append(" (TIMER) = ")
                            .append(timer.currentTickCount()).append("/").append(timer.ticks())
                            .append("(").append(timer.getTimeFromLastTick(game.getTimerTimeMs())).append("/").append(timer.elapse()).append("ms)")
                            .append("\n");
                }

                default -> {}
            }
        }

        debugVariablesValues = sb.toString();
    }

    private Optional<CanvasPoint> getMousePosition() {
        if (game.getInputManager() == null) {
            return Optional.empty();
        }
        return game.getInputManager().getCorrectedMouseCoords(
                Gdx.input.getX(), Gdx.input.getY());
    }

    private void setTooltipPosition(CanvasPoint mousePosition) {
        OpenGlPoint position = CanvasCoordinateSystem.toOpenGl(
                mousePosition.translated(20, 20));
        tooltipPosition.set(asFloat(position.x()), asFloat(position.y()));
    }

    private int getPriority(EngineVariable variable) {
        if (variable instanceof ImageVariable img) return img.state().priority;
        if (variable instanceof AnimoVariable animo) return animo.getPriority();
        return 0;
    }

    private EngineVariable getButtonAt(int x, int y) {
        Context context = (Context) game.getCurrentSceneContext();
        List<EngineVariable> buttons = new ArrayList<>(context.getButtonVariablesForInput());

        int minHSPriority = game.getCurrentSceneVariable().minHotSpotZ();
        int maxHSPriority = game.getCurrentSceneVariable().maxHotSpotZ();

        for (EngineVariable variable : buttons) {
            if (variable instanceof ButtonVariable btn) {
                String gfxName = btn.getCurrentGfxName();
                if (gfxName != null) {
                    Context owner = context.findOwningContext(btn);
                    EngineVariable gfx = (owner != null ? owner : context).getVariable(gfxName);
                    if (gfx != null) {
                        int priority = getPriority(gfx);
                        if (priority < minHSPriority || priority > maxHSPriority) continue;
                    }
                }

                CanvasRect btnRect = btn.getRect();
                if (btn.isEnabled() && btnRect != null && btnRect.contains(x, y)) {
                    return btn;
                }
            } else if (variable instanceof AnimoVariable animo) {
                int priority = animo.getPriority();
                if (priority < minHSPriority || priority > maxHSPriority) continue;

                if (animo.getRect() != null && animo.getRect().contains(x, y)) {
                    return animo;
                }
            }
        }

        return null;
    }

    private EngineVariable getGraphicsAt(int x, int y) {
        GameContext context = game.getCurrentSceneContext();
        List<EngineVariable> drawList = new ArrayList<>(context.getGraphicsVariables().values());

        List<EngineVariable> reversedList = new ArrayList<>(drawList);
        java.util.Collections.reverse(reversedList);

        for (EngineVariable variable : reversedList) {
            boolean visible = false;

            if (variable instanceof ImageVariable img) {
                visible = img.isVisible();
            } else if (variable instanceof AnimoVariable animo) {
                visible = animo.isVisible();
            }

            if (!visible) continue;

            CanvasRect rect = canvasBoundsOf(variable);
            if (rect != null && rect.contains(x, y)) {
                return variable;
            }
        }

        return null;
    }

    private boolean isGraphicsButton(EngineVariable variable) {
        CanvasRect rect = canvasBoundsOf(variable);
        GameContext context = game.getCurrentSceneContext();

        for (EngineVariable ev : context.getVariables().values()) {
            if (!(ev instanceof ButtonVariable btn)) continue;

            String gfxStdName = btn.state().gfxStandardName;
            if (gfxStdName != null && gfxStdName.equals(variable.getName())) {
                return true;
            }

            if (rect == null) continue;

            CanvasRect btnRect = btn.getRect();
            if (btnRect != null && btnRect.intersects(rect)) {
                return true;
            }
        }

        if (variable instanceof AnimoVariable animo) {
            return animo.isAsButton();
        }

        return false;
    }

    public void setDebugRect(CanvasRect rect) {
        this.debugRect = rect;
    }

    public void toggleSceneSelector() {
        showSceneSelector = !showSceneSelector;
        if (showSceneSelector) {
            selectorMode = SelectorMode.SCENES;
            resetFilterInput();
            updateSceneList();
        }
    }

    /**
     * Whether the scene selector overlay is currently open. While open, it installs
     * its own input processor and swallows gameplay input.
     */
    public boolean isSceneSelectorActive() {
        return showSceneSelector;
    }

    private void updateSceneList() {
        sceneList.clear();

        // Resolve every scene through application episodes.
        for (String episodeName : game.getApplicationVariable().episodeNames()) {
            EngineVariable epVar = game.getDefinitionContext().getVariable(episodeName);
            if (epVar instanceof EpisodeVariable ep) {
                sceneList.addAll(ep.sceneNames());
            }
        }

        // Sort
        Collections.sort(sceneList);

        // Build the (initially unfiltered) view; selects & scrolls to the
        // current scene. Force a rebuild regardless of the previous filter state.
        appliedFilter = null;
        appliedMode = null;
        ensureFilter();
    }

    /**
     * Rebuilds the filtered view only when the typed query or the mode changed,
     * so list navigation (which doesn't touch the query) is preserved.
     */
    private void ensureFilter() {
        String query = sceneNameInput.toString();
        if (query.equals(appliedFilter) && selectorMode == appliedMode) {
            return;
        }
        appliedFilter = query;
        appliedMode = selectorMode;
        rebuildFilter();
    }

    private void rebuildFilter() {
        String query = sceneNameInput.toString().trim().toLowerCase(Locale.ROOT);
        if (selectorMode == SelectorMode.SCENES) {
            filteredScenes.clear();
            for (String scene : sceneList) {
                if (query.isEmpty() || scene.toLowerCase(Locale.ROOT).contains(query)) {
                    filteredScenes.add(scene);
                }
            }
            if (query.isEmpty()) {
                // On open / unfiltered, highlight the current scene.
                selectedScene = filteredScenes.indexOf(game.getCurrentScene());
                if (selectedScene < 0 && !filteredScenes.isEmpty()) {
                    selectedScene = 0;
                }
            } else {
                selectedScene = filteredScenes.isEmpty() ? -1 : 0;
            }
            clampScrollToSelection();
        } else {
            filteredNames.clear();
            String family = SceneLoaderScripts.familyId(game.getGame() != null ? game.getGame().getGameName() : null);
            boolean arcade = selectorMode == SelectorMode.ARCADE;
            for (String name : sceneCatalog.names(family, arcade)) {
                if (query.isEmpty() || name.toLowerCase(Locale.ROOT).contains(query)) {
                    filteredNames.add(name);
                }
            }
            filteredSelection = -1;   // a fresh query defaults focus to the typed text
            filteredScroll = 0;
        }
    }

    /** Scrolls the scene list so {@link #selectedScene} is visible (roughly centred). */
    private void clampScrollToSelection() {
        int maxScroll = Math.max(0, filteredScenes.size() - MAX_VISIBLE_SCENES);
        if (selectedScene < 0) {
            scrollPosition = 0;
        } else {
            int target = selectedScene - MAX_VISIBLE_SCENES / 2;
            scrollPosition = Math.max(0, Math.min(target, maxScroll));
        }
    }

    private List<SelectorMode> availableModes() {
        List<SelectorMode> modes = new ArrayList<>();
        modes.add(SelectorMode.SCENES);
        String gameName = game.getGame() != null ? game.getGame().getGameName() : null;
        if (SceneLoaderScripts.supportsArcade(gameName)) {
            modes.add(SelectorMode.ARCADE);
        }
        if (SceneLoaderScripts.supportsCutscene(gameName)) {
            modes.add(SelectorMode.CUTSCENE);
        }
        return modes;
    }

    private void cycleMode(int direction) {
        List<SelectorMode> modes = availableModes();
        int current = Math.max(0, modes.indexOf(selectorMode));
        int next = (current + direction + modes.size()) % modes.size();
        selectorMode = modes.get(next);
        resetFilterInput();
        keyIsDown = false;
        keyRepeatTimer = 0;
    }

    /** Clears the shared filter field and forces the view to rebuild. */
    private void resetFilterInput() {
        sceneNameInput.setLength(0);
        filteredNames.clear();
        filteredScenes.clear();
        filteredSelection = -1;
        filteredScroll = 0;
        appliedFilter = null;   // force ensureFilter() to rebuild for the new mode
    }

    private void clampLoaderScroll() {
        if (filteredSelection < 0) {
            filteredScroll = 0;
        } else if (filteredSelection < filteredScroll) {
            filteredScroll = filteredSelection;
        } else if (filteredSelection >= filteredScroll + MAX_VISIBLE_LOADER) {
            filteredScroll = filteredSelection - MAX_VISIBLE_LOADER + 1;
        }
    }

    private void moveLoaderSelectionDown() {
        if (filteredNames.isEmpty()) {
            return;
        }
        if (filteredSelection < filteredNames.size() - 1) {
            filteredSelection++;
        }
        clampLoaderScroll();
    }

    private void moveLoaderSelectionUp() {
        if (filteredSelection >= 0) {
            filteredSelection--;   // stepping above the top returns focus to the text field
        }
        clampLoaderScroll();
    }

    /**
     * Runs the game-specific loader script for the given scene name. Called on
     * the render thread (from input handling), so the scene transition it
     * triggers is safe.
     */
    private void loadCustomScene(SelectorMode mode, String name) {
        if (name == null || name.trim().isEmpty()) {
            return;
        }
        String gameName = game.getGame() != null ? game.getGame().getGameName() : null;
        String script = mode == SelectorMode.ARCADE
                ? SceneLoaderScripts.arcadeScript(gameName, name)
                : SceneLoaderScripts.cutsceneScript(gameName, name);
        if (script != null) {
            game.runScript(script);
        }
        showSceneSelector = false;
    }

    private static String modeLabel(SelectorMode mode) {
        return switch (mode) {
            case ARCADE -> "ARCADE";
            case CUTSCENE -> "CUTSCENKI";
            default -> "SCENY";
        };
    }

    private void renderSceneSelector() {
        if (!showSceneSelector) return;

        List<SelectorMode> modes = availableModes();
        boolean hasTabs = modes.size() > 1;
        int topPad = hasTabs ? 52 : 32;   // header (+ tabs) area above content

        int width = 300;
        int height;
        if (selectorMode == SelectorMode.SCENES) {
            int rows = Math.clamp(filteredScenes.size(), 1, MAX_VISIBLE_SCENES);
            height = topPad + rows * 20 + 36;   // filter line + list + footer
        } else {
            int rows = Math.clamp(filteredNames.size(), 1, MAX_VISIBLE_LOADER);
            height = topPad + 44 + rows * 20 + 14;
        }

        // Draw background
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(new Color(0, 0, 0, 0.8f));
        shapeRenderer.rect(selectorPosition.x, selectorPosition.y, width, height);
        shapeRenderer.end();

        // Draw border
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(selectorPosition.x, selectorPosition.y, width, height);
        shapeRenderer.end();

        batch.begin();

        // Draw header
        font.setColor(Color.YELLOW);
        font.draw(batch, "Scene Selector (F9)", selectorPosition.x + 10, selectorPosition.y + height - 10);

        // Draw mode tabs
        if (hasTabs) {
            float tabX = selectorPosition.x + 12;
            float tabY = selectorPosition.y + height - 30;
            for (SelectorMode mode : modes) {
                font.setColor(mode == selectorMode ? Color.GREEN : Color.GRAY);
                GlyphLayout layout = font.draw(batch, modeLabel(mode), tabX, tabY);
                tabX += layout.width + 14;
            }
        }

        float contentTop = selectorPosition.y + height - topPad;

        if (selectorMode == SelectorMode.SCENES) {
            // Filter field (type to narrow the list).
            font.setColor(Color.WHITE);
            font.draw(batch, "Filtr: " + sceneNameInput + "_", selectorPosition.x + 15, contentTop);

            float listTop = contentTop - 20;
            if (filteredScenes.isEmpty()) {
                font.setColor(Color.GRAY);
                font.draw(batch, "(brak dopasowania)", selectorPosition.x + 15, listTop);
            } else {
                int visibleCount = Math.min(filteredScenes.size(), MAX_VISIBLE_SCENES);
                for (int i = 0; i < visibleCount; i++) {
                    int index = scrollPosition + i;
                    if (index >= filteredScenes.size()) break;

                    if (index == selectedScene) {
                        font.setColor(Color.GREEN);  // Selected scene
                    } else if (filteredScenes.get(index).equals(game.getCurrentScene())) {
                        font.setColor(Color.CYAN);   // Current scene
                    } else {
                        font.setColor(Color.WHITE);  // Rest of scenes
                    }

                    float itemY = listTop - (i * 20);
                    font.draw(batch, filteredScenes.get(index), selectorPosition.x + 15, itemY);
                }
            }
        } else {
            font.setColor(Color.WHITE);
            font.draw(batch, "Nazwa " + modeLabel(selectorMode) + ":", selectorPosition.x + 15, contentTop);

            boolean textFocused = filteredSelection < 0;
            font.setColor(textFocused ? Color.GREEN : Color.LIGHT_GRAY);
            font.draw(batch, sceneNameInput + (textFocused ? "_" : ""), selectorPosition.x + 15, contentTop - 20);

            float listTop = contentTop - 44;
            if (filteredNames.isEmpty()) {
                font.setColor(Color.GRAY);
                font.draw(batch, "(brak na liscie - ENTER laduje wpisane)", selectorPosition.x + 15, listTop);
            } else {
                int visible = Math.min(filteredNames.size(), MAX_VISIBLE_LOADER);
                for (int i = 0; i < visible; i++) {
                    int index = filteredScroll + i;
                    if (index >= filteredNames.size()) break;
                    font.setColor(index == filteredSelection ? Color.GREEN : Color.WHITE);
                    font.draw(batch, filteredNames.get(index), selectorPosition.x + 15, listTop - i * 20);
                }
            }
        }

        // Footer hint
        font.setColor(Color.GRAY);
        font.draw(batch, hasTabs ? "<-/->: tryb   ESC: zamknij" : "ESC: zamknij",
                selectorPosition.x + 12, selectorPosition.y + 14);

        batch.end();

        if (selectorMode == SelectorMode.SCENES && filteredScenes.size() > MAX_VISIBLE_SCENES) {
            float scrollableArea = height - topPad - 44;
            float totalItems = filteredScenes.size();
            float visibleItems = MAX_VISIBLE_SCENES;

            float barHeight = (visibleItems / totalItems) * scrollableArea;

            float barPositionRatio = scrollPosition / (totalItems - visibleItems);
            float barY = selectorPosition.y + 24 + (scrollableArea - barHeight) * (1.0f - barPositionRatio);

            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(Color.GRAY);
            shapeRenderer.rect(selectorPosition.x + width - 15, selectorPosition.y + 24, 10, scrollableArea);
            shapeRenderer.setColor(Color.WHITE);
            shapeRenderer.rect(selectorPosition.x + width - 13, barY, 6, barHeight);
            shapeRenderer.end();
        }
    }

    public void handleSceneSelectorInput(float deltaTime) {
        if (!showSceneSelector) return;

        // Swallow gameplay input while the selector is open; the processor also
        // captures typed characters for the shared filter / name field.
        Gdx.input.setInputProcessor(selectorInputProcessor);

        // Switch modes (only meaningful when the game exposes ARCADE/CUTSCENE).
        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            cycleMode(-1);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            cycleMode(1);
        }

        // Re-filter when the typed query (or mode) changed.
        ensureFilter();

        // UP/DOWN drive the active list (filtered scenes, or filtered loader list).
        boolean upPressed = Gdx.input.isKeyPressed(Input.Keys.UP);
        boolean downPressed = Gdx.input.isKeyPressed(Input.Keys.DOWN);

        if (upPressed || downPressed) {
            if (!keyIsDown) {
                keyIsDown = true;
                keyRepeatTimer = 0;

                if (upPressed) {
                    moveUp();
                } else {
                    moveDown();
                }
            } else {
                keyRepeatTimer += deltaTime;

                if (keyRepeatTimer > KEY_INITIAL_DELAY) {
                    float timeSinceDelay = keyRepeatTimer - KEY_INITIAL_DELAY;
                    if (timeSinceDelay % KEY_REPEAT_INTERVAL < deltaTime) {
                        if (upPressed) {
                            moveUp();
                        } else {
                            moveDown();
                        }
                    }
                }
            }
        } else {
            keyIsDown = false;
            keyRepeatTimer = 0;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            if (selectorMode == SelectorMode.SCENES) {
                if (selectedScene >= 0 && selectedScene < filteredScenes.size()) {
                    game.goTo(filteredScenes.get(selectedScene));
                    showSceneSelector = false;
                }
            } else {
                // ARCADE / CUTSCENE: a highlighted list item wins, else the typed text.
                String name = (filteredSelection >= 0 && filteredSelection < filteredNames.size())
                        ? filteredNames.get(filteredSelection)
                        : sceneNameInput.toString();
                loadCustomScene(selectorMode, name);
            }
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            // Close selector
            showSceneSelector = false;
        }
    }

    private void moveUp() {
        if (selectorMode == SelectorMode.SCENES) {
            moveSelectionUp();
        } else {
            moveLoaderSelectionUp();
        }
    }

    private void moveDown() {
        if (selectorMode == SelectorMode.SCENES) {
            moveSelectionDown();
        } else {
            moveLoaderSelectionDown();
        }
    }

    private void moveSelectionUp() {
        if (selectedScene > 0) {
            selectedScene--;

            if (selectedScene < scrollPosition) {
                scrollPosition = selectedScene;
            }
        }
    }

    private void moveSelectionDown() {
        if (selectedScene < filteredScenes.size() - 1) {
            selectedScene++;

            if (selectedScene >= scrollPosition + MAX_VISIBLE_SCENES) {
                scrollPosition = selectedScene - MAX_VISIBLE_SCENES + 1;
            }
        }
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        font.dispose();
    }

}
