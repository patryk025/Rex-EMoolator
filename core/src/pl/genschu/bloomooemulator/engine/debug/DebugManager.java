package pl.genschu.bloomooemulator.engine.debug;

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
import pl.genschu.bloomooemulator.engine.Game;
import pl.genschu.bloomooemulator.engine.config.EngineConfig;
import pl.genschu.bloomooemulator.engine.context.EngineVariable;
import pl.genschu.bloomooemulator.engine.context.GameContext;
import pl.genschu.bloomooemulator.geometry.points.Point3D;
import pl.genschu.bloomooemulator.interpreter.context.Context;
import pl.genschu.bloomooemulator.interpreter.variable.*;
import pl.genschu.bloomooemulator.geometry.shapes.Box2D;
import pl.genschu.bloomooemulator.world.GameObject;
import pl.genschu.bloomooemulator.world.Mesh;
import pl.genschu.bloomooemulator.world.MeshTriangle;
import pl.genschu.bloomooemulator.world.TriangleVertex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class DebugManager implements Disposable {
    private static final float VIRTUAL_HEIGHT = 600;

    private final SpriteBatch batch;
    private final OrthographicCamera camera;
    private final Game game;
    private final EngineConfig config;

    private final ShapeRenderer shapeRenderer;
    private final BitmapFont font;
    private final FpsCounter fpsCounter = new FpsCounter();

    private String tooltipText = "";
    private final Vector2 tooltipPosition = new Vector2();
    private boolean showTooltip = false;

    private String debugVariablesValues = "";
    private Box2D debugRect;

    private final Set<String> loggedCollisionMismatches = new HashSet<>();

    private enum SelectorMode { SCENES, ARCADE, CUTSCENE }

    private boolean showSceneSelector = false;
    private SelectorMode selectorMode = SelectorMode.SCENES;
    private final StringBuilder sceneNameInput = new StringBuilder();   // shared filter / name field
    private Vector2 selectorPosition = new Vector2(10, 30);
    private static final int MAX_VISIBLE_SCENES = 15;

    // SCENES mode: master list + type-to-filter view.
    private List<String> sceneList = new ArrayList<>();
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


    public DebugManager(SpriteBatch batch, OrthographicCamera camera, Game game, EngineConfig config) {
        this.batch = batch;
        this.camera = camera;
        this.game = game;
        this.config = config;

        this.shapeRenderer = new ShapeRenderer();
        this.font = new BitmapFont();
    }

    public void render(float deltaTime) {
        fpsCounter.update(Gdx.graphics.getDeltaTime());

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
            renderMonitorPerformance();
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

        // get matrix variables
        for (EngineVariable ev : game.getCurrentSceneContext().getVariables().values()) {
            if (ev instanceof MatrixVariable mv) {
                debugMatrix(mv);
            }
        }

        if (config.isShowFpsCounter()) {
            renderFpsCounter();
        }
    }

    private void renderObjectBoundingBoxes() {
        GameContext context = game.getCurrentSceneContext();
        List<EngineVariable> graphics = new ArrayList<>(context.getGraphicsVariables().values());

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        batch.begin();
        font.setColor(Color.WHITE);

        for (EngineVariable variable : graphics) {
            Box2D rect = getRect(variable);
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
            shapeRenderer.rect(
                    rect.getXLeft(),
                    VIRTUAL_HEIGHT - rect.getYTop() - rect.getHeight(),
                    rect.getWidth(),
                    rect.getHeight()
            );

            String name = variable.getName();
            font.draw(
                    batch,
                    name,
                    rect.getXLeft(),
                    VIRTUAL_HEIGHT - rect.getYTop() + 15
            );
        }

        batch.end();
        shapeRenderer.end();
    }

    private void renderGraphicsDebug() {
        // Get info about graphics under cursor
        Vector2 mousePos = getMousePosition();
        EngineVariable graphicsUnderCursor = getGraphicsAt((int) mousePos.x, (int) mousePos.y);
        EngineVariable buttonUnderCursor = getButtonAt((int) mousePos.x, (int) mousePos.y);

        if (buttonUnderCursor != null) {
            if(buttonUnderCursor instanceof ButtonVariable btn) {
                generateTooltipForButton(btn);
            }
            else {
                generateTooltipForGraphics(buttonUnderCursor);
            }
            tooltipPosition.set(mousePos.x + 20, VIRTUAL_HEIGHT - mousePos.y - 20);
            showTooltip = true;
        }
        else if (graphicsUnderCursor != null) {
            generateTooltipForGraphics(graphicsUnderCursor);
            tooltipPosition.set(mousePos.x + 20, VIRTUAL_HEIGHT - mousePos.y - 20);
            showTooltip = true;
        } else {
            showTooltip = false;
        }
    }

    private void renderVariablesDebug() {
        generateDebugVariablesText();

        batch.begin();
        font.setColor(Color.WHITE);
        font.draw(batch, debugVariablesValues, 5, VIRTUAL_HEIGHT - 5);
        batch.end();
    }

    private void renderMonitorPerformance() {
        String performanceMetrics = PerformanceMonitor.printStats();

        batch.begin();
        font.setColor(Color.WHITE);
        font.draw(batch, performanceMetrics, 350, VIRTUAL_HEIGHT - 5);
        batch.end();
    }

    private void renderFpsCounter() {
        FpsCounter.Snapshot snapshot = fpsCounter.getSnapshot();
        String text = String.format(Locale.ROOT, "%.0f FPS | low %.0f", snapshot.fps, snapshot.lowOnePercentFps);
        GlyphLayout layout = new GlyphLayout(font, text);
        float x = 8;
        float y = VIRTUAL_HEIGHT - 8;
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
        font.setColor(fpsColor(snapshot.lowOnePercentFps));
        font.draw(batch, text, x, y);
        batch.end();
    }

    private Color fpsColor(float lowOnePercentFps) {
        if (lowOnePercentFps >= 55f) return Color.GREEN;
        if (lowOnePercentFps >= 30f) return Color.YELLOW;
        return Color.RED;
    }

    private void renderDebugRectangle() {
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.RED);
        Box2D rect = debugRect;
        shapeRenderer.rect(rect.getXLeft(), VIRTUAL_HEIGHT - rect.getYBottom() - rect.getHeight(),
                rect.getWidth(), rect.getHeight());
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
        List<EngineVariable> buttons = new ArrayList<>(game.getCurrentSceneContext().getButtonsVariables().values());
        Vector2 mousePos = getMousePosition();

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        for (EngineVariable variable : buttons) {
            Box2D rect = null;
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
                else if (rect.contains((int) mousePos.x, (int) mousePos.y)) {
                    shapeRenderer.setColor(Color.GREEN);
                } else {
                    shapeRenderer.setColor(Color.RED);
                }

                int height = rect.getHeight();
                int width = rect.getWidth();
                shapeRenderer.rect(rect.getXLeft(), VIRTUAL_HEIGHT - rect.getYTop() - height, width, height);
            }
        }

        shapeRenderer.end();

        // Labels next to buttons
        batch.begin();
        font.setColor(Color.WHITE);
        for (EngineVariable variable : buttons) {
            Box2D rect = null;
            if (variable instanceof ButtonVariable btn) {
                rect = btn.getRect();
            } else if (variable instanceof AnimoVariable animo) {
                rect = animo.getRect();
            }
            if (rect == null) continue;
            font.draw(batch, variable.getName(),
                    rect.getXLeft(),
                    VIRTUAL_HEIGHT - rect.getYTop() - rect.getHeight() - 3);
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

        // QuadTree root bounds (matches Game.java: new Box2D(0, 0, 800, 600))
        shapeRenderer.setColor(Color.DARK_GRAY);
        shapeRenderer.rect(0, 0, 800, VIRTUAL_HEIGHT);

        // Rect per monitored variable, colored by status
        int collidingCount = 0;
        for (EngineVariable variable : snapshot) {
            Box2D rect = rectOf(variable);
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

            shapeRenderer.rect(
                    rect.getXLeft(),
                    VIRTUAL_HEIGHT - rect.getYTop() - rect.getHeight(),
                    rect.getWidth(),
                    rect.getHeight()
            );
        }

        // Lines between currently-registered colliding pairs
        shapeRenderer.setColor(Color.GREEN);
        Set<String> drawnPairs = new HashSet<>();
        int activePairs = 0;
        for (EngineVariable variable : snapshot) {
            Box2D a = rectOf(variable);
            if (a == null) continue;
            for (EngineVariable other : game.getCollidingWith(variable)) {
                String key = pairKey(variable, other);
                if (!drawnPairs.add(key)) continue;
                Box2D b = rectOf(other);
                if (b == null) continue;
                shapeRenderer.line(centerX(a), centerY(a), centerX(b), centerY(b));
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
            Box2D a = rectOf(vi);
            if (a == null) continue;
            Set<EngineVariable> retrieved = null;
            for (int j = i + 1; j < snapshot.size(); j++) {
                EngineVariable vj = snapshot.get(j);
                Box2D b = rectOf(vj);
                if (b == null) continue;
                if (!a.intersects(b)) continue;

                if (retrieved == null) {
                    retrieved = new HashSet<>(
                            game.getQuadTree().retrieve(new ArrayList<>(), vi));
                }
                if (!retrieved.contains(vj)) {
                    hiddenPairs++;
                    shapeRenderer.line(centerX(a), centerY(a), centerX(b), centerY(b));
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
            Box2D rect = rectOf(variable);
            if (rect == null) continue;
            font.draw(batch, variable.getName(),
                    rect.getXLeft(),
                    VIRTUAL_HEIGHT - rect.getYTop() - rect.getHeight() - 3);
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
        font.draw(batch, stats, 5, VIRTUAL_HEIGHT - 120);
        batch.end();
    }

    private static String pairKey(EngineVariable a, EngineVariable b) {
        String na = a.getName();
        String nb = b.getName();
        return na.compareTo(nb) <= 0 ? na + "|" + nb : nb + "|" + na;
    }

    private Box2D rectOf(EngineVariable variable) {
        if (variable instanceof ImageVariable img) return img.getRect();
        if (variable instanceof AnimoVariable animo) return animo.getRect();
        return null;
    }

    private static float centerX(Box2D r) {
        return r.getXLeft() + r.getWidth() / 2f;
    }

    private static float centerY(Box2D r) {
        return VIRTUAL_HEIGHT - r.getYTop() - r.getHeight() / 2f;
    }

    private void debugMatrix(MatrixVariable matrixVariable) {
        if (!config.isDebugMatrix()) return;

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

                shapeRenderer.rect(x, VIRTUAL_HEIGHT - y - cellHeight, cellWidth - 1, cellHeight - 1);
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
                float y = VIRTUAL_HEIGHT - (startY + i * cellHeight + 15);

                font.draw(batch, value, x, y);
            }
        }

        batch.end();
    }

    private void drawVelocityArrow(ShapeRenderer sr, GameObject go, float scrollX, float scrollY) {
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

        final float x0 = go.getX()+400-scrollX;
        final float y0 = go.getY()+300+scrollY;
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

    private void drawPath(ShapeRenderer sr, GameObject go, float scrollX, float scrollY) {
        Deque<Point3D> path = go.getPath();
        if (path == null || path.size() < 2) return;

        sr.setColor(Color.RED);
        Point3D prev = null;
        for (Point3D p : path) {
            if (prev != null) {
                sr.line((float) prev.x+400-scrollX, (float) prev.y+300+scrollY,
                        (float) p.x+400-scrollX, (float) p.y+300+scrollY);
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

        // Camera scroll offset (CameraAnchor): screen space subtracts scrollX and adds
        // scrollY relative to the unscrolled +400/+300 origin shift used below.
        final float scrollX = world.getPhysicsEngine().getBkgPosX();
        final float scrollY = world.getPhysicsEngine().getBkgPosY();

        for (GameObject go : objects) {
            Mesh mesh = go.getMesh();
            final float x = go.getX();
            final float y = go.getY();
            final float theta = go.getRotationZ();
            final float c = (float)Math.cos(theta);
            final float s = (float)Math.sin(theta);

            if (mesh == null) {
                final float[] d = go.getDimensions();

                final float correctedX = x + 400 - scrollX;
                final float correctedY = y + 300 + scrollY;

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
                final float dx = 400f - scrollX, dy = 300f + scrollY;

                for (MeshTriangle tri : mesh.getTriangles()) {
                    TriangleVertex[] vs = tri.getVertices();

                    // TODO: DRY, currently duplicated code for each vertex
                    float x0 = (float) vs[0].getPoint().x;
                    float y0 = (float) vs[0].getPoint().y;
                    float[] r0 = rot2D(x0, y0, c, s);
                    float wx0 = (go.isRigidBody() ? x + r0[0] : r0[0]) + dx;
                    float wy0 = (go.isRigidBody() ? y + r0[1] : r0[1]) + dy;

                    float x1 = (float) vs[1].getPoint().x;
                    float y1 = (float) vs[1].getPoint().y;
                    float[] r1 = rot2D(x1, y1, c, s);
                    float wx1 = (go.isRigidBody() ? x + r1[0] : r1[0]) + dx;
                    float wy1 = (go.isRigidBody() ? y + r1[1] : r1[1]) + dy;

                    float x2 = (float) vs[2].getPoint().x;
                    float y2 = (float) vs[2].getPoint().y;
                    float[] r2 = rot2D(x2, y2, c, s);
                    float wx2 = (go.isRigidBody() ? x + r2[0] : r2[0]) + dx;
                    float wy2 = (go.isRigidBody() ? y + r2[1] : r2[1]) + dy;

                    shapeRenderer.line(wx0, wy0, wx1, wy1);
                    shapeRenderer.line(wx1, wy1, wx2, wy2);
                    shapeRenderer.line(wx2, wy2, wx0, wy0);
                }
            }
            drawVelocityArrow(shapeRenderer, go, scrollX, scrollY);
            drawPath(shapeRenderer, go, scrollX, scrollY);
        }

        shapeRenderer.end();
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

        Box2D rect = button.getRect();
        String rectText = "\nRect: " + (rect != null ?
                ("\n    left upper corner: (" + rect.getXLeft() + ", " + rect.getYTop() + ")" +
                        "\n    width: " + rect.getWidth() +
                        "\n    height: " + rect.getHeight()) :
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

        Box2D rect = getRect(graphics);
        String rectText = "\nRect: " + (rect != null ?
                ("\n    left upper corner: (" + rect.getXLeft() + ", " + rect.getYTop() + ")" +
                        "\n    width: " + rect.getWidth() +
                        "\n    height: " + rect.getHeight()) :
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

        sb.append("Scena: ").append(game.getCurrentScene()).append("\n\n");

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
                            .append("(").append(timer.getTimeFromLastTick(game.getEngineTimeMs())).append("/").append(timer.elapse()).append("ms)")
                            .append("\n");
                }

                default -> {}
            }
        }

        debugVariablesValues = sb.toString();
    }

    private Vector2 getMousePosition() {
        return new Vector2(Gdx.input.getX(), Gdx.input.getY());
    }

    private int getPriority(EngineVariable variable) {
        if (variable instanceof ImageVariable img) return img.state().priority;
        if (variable instanceof AnimoVariable animo) return animo.getPriority();
        return 0;
    }

    private EngineVariable getButtonAt(int x, int y) {
        GameContext context = game.getCurrentSceneContext();
        List<EngineVariable> buttons = new ArrayList<>(context.getButtonsVariables().values());

        int minHSPriority = game.getCurrentSceneVariable().minHotSpotZ();
        int maxHSPriority = game.getCurrentSceneVariable().maxHotSpotZ();

        for (EngineVariable variable : buttons) {
            if (variable instanceof ButtonVariable btn) {
                String gfxName = btn.getCurrentGfxName();
                if (gfxName != null) {
                    EngineVariable gfx = context.getVariable(gfxName);
                    if (gfx != null) {
                        int priority = getPriority(gfx);
                        if (priority < minHSPriority || priority > maxHSPriority) continue;
                    }
                }

                Box2D btnRect = btn.getRect();
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

            Box2D rect = getRect(variable);
            if (rect != null && rect.contains(x, y)) {
                return variable;
            }
        }

        return null;
    }

    private Box2D getRect(EngineVariable variable) {
        if (variable instanceof ImageVariable img) return img.getRect();
        if (variable instanceof AnimoVariable animo) return animo.getRect();
        return null;
    }

    private boolean isGraphicsButton(EngineVariable variable) {
        Box2D rect = getRect(variable);
        GameContext context = game.getCurrentSceneContext();

        for (EngineVariable ev : context.getVariables().values()) {
            if (!(ev instanceof ButtonVariable btn)) continue;

            String gfxStdName = btn.state().gfxStandardName;
            if (gfxStdName != null && gfxStdName.equals(variable.getName())) {
                return true;
            }

            if (rect == null) continue;

            Box2D btnRect = btn.getRect();
            if (btnRect != null && btnRect.intersects(rect)) {
                return true;
            }
        }

        if (variable instanceof AnimoVariable animo) {
            return animo.isAsButton();
        }

        return false;
    }

    public void setDebugRect(Box2D rect) {
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
        switch (mode) {
            case ARCADE:   return "ARCADE";
            case CUTSCENE: return "CUTSCENKI";
            default:       return "SCENY";
        }
    }

    private void renderSceneSelector() {
        if (!showSceneSelector) return;

        List<SelectorMode> modes = availableModes();
        boolean hasTabs = modes.size() > 1;
        int topPad = hasTabs ? 52 : 32;   // header (+ tabs) area above content

        int width = 300;
        int height;
        if (selectorMode == SelectorMode.SCENES) {
            int rows = Math.min(Math.max(filteredScenes.size(), 1), MAX_VISIBLE_SCENES);
            height = topPad + rows * 20 + 36;   // filter line + list + footer
        } else {
            int rows = Math.max(1, Math.min(filteredNames.size(), MAX_VISIBLE_LOADER));
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

    private static final class FpsCounter {
        private static final int MAX_SAMPLES = 600;
        private static final float WINDOW_SECONDS = 5.0f;
        private static final float REFRESH_SECONDS = 0.5f;

        private final float[] frameTimes = new float[MAX_SAMPLES];
        private int nextIndex = 0;
        private int sampleCount = 0;
        private float totalSeconds = 0;
        private float refreshTimer = 0;
        private Snapshot snapshot = new Snapshot(0, 0);

        void update(float deltaTime) {
            if (deltaTime <= 0) return;

            float frameTime = Math.min(deltaTime, 1.0f);
            if (sampleCount < MAX_SAMPLES) {
                sampleCount++;
            } else {
                totalSeconds -= frameTimes[nextIndex];
            }

            frameTimes[nextIndex] = frameTime;
            totalSeconds += frameTime;
            nextIndex = (nextIndex + 1) % MAX_SAMPLES;

            while (sampleCount > 1 && totalSeconds > WINDOW_SECONDS) {
                int oldestIndex = (nextIndex - sampleCount + MAX_SAMPLES) % MAX_SAMPLES;
                totalSeconds -= frameTimes[oldestIndex];
                sampleCount--;
            }

            refreshTimer += frameTime;
            if (refreshTimer >= REFRESH_SECONDS || snapshot.fps == 0) {
                refreshTimer = 0;
                snapshot = calculateSnapshot();
            }
        }

        Snapshot getSnapshot() {
            return snapshot;
        }

        private Snapshot calculateSnapshot() {
            if (sampleCount == 0 || totalSeconds <= 0) {
                return new Snapshot(0, 0);
            }

            float fps = sampleCount / totalSeconds;
            float[] sorted = new float[sampleCount];
            for (int i = 0; i < sampleCount; i++) {
                int index = (nextIndex - sampleCount + i + MAX_SAMPLES) % MAX_SAMPLES;
                sorted[i] = frameTimes[index];
            }
            Arrays.sort(sorted);

            int percentile99Index = Math.min(sorted.length - 1, (int) Math.ceil(sorted.length * 0.99f) - 1);
            float lowOnePercentFps = 1.0f / sorted[percentile99Index];
            return new Snapshot(fps, lowOnePercentFps);
        }

        static final class Snapshot {
            final float fps;
            final float lowOnePercentFps;

            Snapshot(float fps, float lowOnePercentFps) {
                this.fps = fps;
                this.lowOnePercentFps = lowOnePercentFps;
            }
        }
    }
}
