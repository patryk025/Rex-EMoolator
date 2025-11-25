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
import pl.genschu.bloomooemulator.geometry.points.Point3D;
import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.interpreter.variable.types.*;
import pl.genschu.bloomooemulator.geometry.shapes.Box2D;
import pl.genschu.bloomooemulator.world.GameObject;
import pl.genschu.bloomooemulator.world.Mesh;
import pl.genschu.bloomooemulator.world.MeshTriangle;
import pl.genschu.bloomooemulator.world.TriangleVertex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

public class DebugManager implements Disposable {
    private static final float VIRTUAL_HEIGHT = 600;

    private final SpriteBatch batch;
    private final OrthographicCamera camera;
    private final Game game;
    private final EngineConfig config;

    private final ShapeRenderer shapeRenderer;
    private final BitmapFont font;

    private String tooltipText = "";
    private final Vector2 tooltipPosition = new Vector2();
    private boolean showTooltip = false;

    private String debugVariablesValues = "";
    private Box2D debugRect;

    private boolean showSceneSelector = false;
    private List<String> sceneList = new ArrayList<>();
    private int selectedScene = -1;
    private Vector2 selectorPosition = new Vector2(10, 30);
    private int scrollPosition = 0;
    private static final int MAX_VISIBLE_SCENES = 15;

    private float keyRepeatTimer = 0;
    private boolean keyIsDown = false;
    private final float KEY_INITIAL_DELAY = 0.4f;
    private final float KEY_REPEAT_INTERVAL = 0.1f;


    public DebugManager(SpriteBatch batch, OrthographicCamera camera, Game game, EngineConfig config) {
        this.batch = batch;
        this.camera = camera;
        this.game = game;
        this.config = config;

        this.shapeRenderer = new ShapeRenderer();
        this.font = new BitmapFont();
    }

    public void render(float deltaTime) {
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
        for (Variable matrixVariable : game.getCurrentSceneContext().getVariables().values()) {
            if (matrixVariable instanceof MatrixVariable) {
                debugMatrix((MatrixVariable) matrixVariable);
            }
        }
    }

    private void renderObjectBoundingBoxes() {
        Context context = game.getCurrentSceneContext();
        List<Variable> graphics = new ArrayList<>(context.getGraphicsVariables().values());

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        batch.begin();
        font.setColor(Color.WHITE);

        for (Variable variable : graphics) {
            Box2D rect = getRect(variable);
            if (rect == null) continue;

            boolean visible = false;
            if (variable instanceof ImageVariable) {
                visible = ((ImageVariable) variable).isVisible();
            } else if (variable instanceof AnimoVariable) {
                visible = ((AnimoVariable) variable).isVisible();
            }

            if (visible) {
                shapeRenderer.setColor(Color.RED);
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
        Variable graphicsUnderCursor = getGraphicsAt((int) mousePos.x, (int) mousePos.y);
        Variable buttonUnderCursor = getButtonAt((int) mousePos.x, (int) mousePos.y);

        if (buttonUnderCursor != null) {
            if(buttonUnderCursor instanceof ButtonVariable) {
                generateTooltipForButton((ButtonVariable) buttonUnderCursor);
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

        // Render button borders
        //renderButtonBorders();
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
        List<Variable> buttons = new ArrayList<>(game.getCurrentSceneContext().getButtonsVariables().values());
        Vector2 mousePos = getMousePosition();

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        for (Variable variable : buttons) {
            Box2D rect = null;
            boolean isEnabled = true;

            if (variable instanceof ButtonVariable) {
                ButtonVariable button = (ButtonVariable) variable;
                if (!button.isEnabled()) continue;
                rect = button.getRect();
            } else if (variable instanceof AnimoVariable) {
                AnimoVariable animoVariable = (AnimoVariable) variable;
                rect = animoVariable.getRect();
            }

            if (rect != null) {
                if (rect.contains((int) mousePos.x, (int) mousePos.y)) {
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
    }

    private void debugMatrix(MatrixVariable matrixVariable) {
        if (!config.isDebugMatrix()) return;

        // get array data
        ArrayVariable arrayVariable = matrixVariable.getData();
        if (arrayVariable == null) return;

        // get array elements
        List<Variable> elements = arrayVariable.getElements();
        if (elements == null) return;

        // get matrix dimensions
        int width = matrixVariable.getWidth();
        int height = matrixVariable.getHeight();
        int cellWidth = matrixVariable.getCellWidth();
        int cellHeight = matrixVariable.getCellHeight();

        // get base pos
        float startX = matrixVariable.getBasePosX();
        float startY = matrixVariable.getBasePosY();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int index = i * width + j;
                if (index >= elements.size()) continue;

                Variable element = elements.get(index);
                int value = 0;
                if (element instanceof IntegerVariable) {
                    value = ((IntegerVariable) element).GET();
                }

                float x = startX + j * cellWidth;
                float y = startY + i * cellHeight;

                switch (value) {
                    case 0: // EMPTY
                        shapeRenderer.setColor(0.1f, 0.1f, 0.1f, 1f); // Ciemnoszary
                        break;
                    case 1: // GROUND
                        shapeRenderer.setColor(0.5f, 0.35f, 0.05f, 1f); // Brązowy
                        break;
                    case 2: // STONE
                        shapeRenderer.setColor(0.6f, 0.6f, 0.6f, 1f); // Szary
                        break;
                    case 3: // DYNAMITE
                        shapeRenderer.setColor(1f, 0f, 0f, 1f); // Czerwony
                        break;
                    case 4: // WALL_WEAK
                        shapeRenderer.setColor(0.7f, 0.5f, 0.3f, 1f); // Jasnobrązowy
                        break;
                    case 5: // ENEMY
                        shapeRenderer.setColor(1f, 0.5f, 0f, 1f); // Pomarańczowy
                        break;
                    case 6: // WALL_STRONG
                        shapeRenderer.setColor(0.3f, 0.3f, 0.3f, 1f); // Ciemnoszary
                        break;
                    case 7: // DYNAMITE_FIRED
                        shapeRenderer.setColor(1f, 0.3f, 0.3f, 1f); // Jasnoczerwony
                        break;
                    case 8: // EXPLOSION
                        shapeRenderer.setColor(1f, 1f, 0f, 1f); // Żółty
                        break;
                    case 9: // EXIT
                        shapeRenderer.setColor(0f, 1f, 0f, 1f); // Zielony
                        break;
                    case 99: // MOLE
                        shapeRenderer.setColor(0f, 0f, 1f, 1f); // Niebieski
                        break;
                    default:
                        shapeRenderer.setColor(0.8f, 0.8f, 0.8f, 1f); // Biały dla nieznanych wartości
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
                if (index >= elements.size()) continue;

                Variable element = elements.get(index);
                String value = element.toString();

                float x = startX + j * cellWidth + 4; // Offset dla tekstu
                float y = VIRTUAL_HEIGHT - (startY + i * cellHeight + 15); // Offset dla tekstu

                font.draw(batch, value, x, y);
            }
        }

        batch.end();
    }

    private void drawVelocityArrow(ShapeRenderer sr, GameObject go) {
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

        final float x0 = go.getX()+400;
        final float y0 = go.getY()+300;
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

    private void drawPath(ShapeRenderer sr, GameObject go) {
        Deque<Point3D> path = go.getPath();
        if (path == null || path.size() < 2) return;

        sr.setColor(Color.RED);
        Point3D prev = null;
        for (Point3D p : path) {
            if (prev != null) {
                sr.line((float) prev.x+400, (float) prev.y+300,
                        (float) p.x+400, (float) p.y+300);
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

        WorldVariable world = game.getCurrentSceneContext().getWorldVariable();
        if( world == null ) {
            shapeRenderer.end();
            return;
        }
        List<GameObject> objects = world.getPhysicsEngine().getGameObjects();

        for (GameObject go : objects) {
            Mesh mesh = go.getMesh();
            final float x = go.getX();
            final float y = go.getY();
            final float theta = go.getRotationZ();
            final float c = (float)Math.cos(theta);
            final float s = (float)Math.sin(theta);

            if (mesh == null) {
                final float[] d = go.getDimensions();

                final float correctedX = x + 400;
                final float correctedY = y + 300;

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
                final float dx = 400f, dy = 300f;

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
            drawVelocityArrow(shapeRenderer, go);
            drawPath(shapeRenderer, go);
        }

        shapeRenderer.end();
    }



    private void generateTooltipForButton(ButtonVariable button) {
        StringBuilder sb = new StringBuilder();

        sb.append("Button: ").append(button.getName()).append("\n");
        sb.append("Active: ").append(button.isEnabled()).append("\n");

        Variable gfxStandard = button.getCurrentImage();
        if (gfxStandard != null) {
            sb.append("\nStandard GFX: ").append(gfxStandard.getName()).append("\n");
            sb.append("Priority: ").append(gfxStandard.getAttribute("PRIORITY") != null ?
                    gfxStandard.getAttribute("PRIORITY").getValue() : "brak").append("\n");
        } else {
            sb.append("\nNo standard GFX\n");
        }

        Box2D rect = button.getRect();
        String rectText = "\nRect: " + (rect != null ?
                ("\n    left upper corner: (" + rect.getXLeft() + ", " + rect.getYTop() + ")" +
                        "\n    width: " + rect.getWidth() +
                        "\n    height: " + rect.getHeight()) :
                "no defined");
        sb.append(rectText);

        if (button.getAttribute("SNDONMOVE") != null) {
            sb.append("\nSound on hover: ").append(button.getAttribute("SNDONMOVE").getValue()).append("\n");
        }

        if (button.getAttribute("SNDONCLICK") != null) {
            sb.append("\nSound on click: ").append(button.getAttribute("SNDONCLICK").getValue()).append("\n");
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

    private void generateTooltipForGraphics(Variable graphics) {
        StringBuilder sb = new StringBuilder();
        sb.append(graphics.getName()).append(" (").append(graphics.getType()).append(")\n");
        sb.append("Priority: ").append(graphics.getAttribute("PRIORITY") != null ?
                graphics.getAttribute("PRIORITY").getValue() : 0);

        Box2D rect = getRect(graphics);
        String rectText = "\nRect: " + (rect != null ?
                ("\n    left upper corner: (" + rect.getXLeft() + ", " + rect.getYTop() + ")" +
                        "\n    width: " + rect.getWidth() +
                        "\n    height: " + rect.getHeight()) :
                "no defined");

        if (graphics instanceof AnimoVariable) {
            AnimoVariable animo = (AnimoVariable) graphics;
            sb.append("\nCurrent FPS: ").append(animo.getFps());
            sb.append("\nCurrent event: ").append(animo.getCurrentEvent() != null ?
                    animo.getCurrentEvent().getName() : "none");
            sb.append("\nCurrent frame number: ").append(animo.getCurrentFrameNumber());
            sb.append("\nCurrent image number: ").append(animo.getCurrentImageNumber());
            sb.append("\nIs playing: ").append(animo.isPlaying());
            sb.append("\nIs button: ").append(isGraphicsButton(graphics));
            sb.append("\nCollision monitoring: ").append(graphics.getAttribute("MONITORCOLLISION") != null ?
                    graphics.getAttribute("MONITORCOLLISION").getValue() : "FALSE");
            sb.append(rectText);
        } else if (graphics instanceof ImageVariable) {
            sb.append("\nIs button: ").append(isGraphicsButton(graphics));
            sb.append("\nCollision monitoring: ").append(graphics.getAttribute("MONITORCOLLISION") != null ?
                    graphics.getAttribute("MONITORCOLLISION").getValue() : "FALSE");
            sb.append(rectText);
        } else {
            sb.append(rectText);
        }

        tooltipText = sb.toString();
    }

    private void generateDebugVariablesText() {
        StringBuilder sb = new StringBuilder();
        Context context = game.getCurrentSceneContext();

        sb.append("Scena: ").append(game.getCurrentScene()).append("\n\n");

        for (Variable variable : context.getVariables().values()) {
            switch (variable.getType()) {
                case "INTEGER":
                case "DOUBLE":
                case "BOOL":
                case "STRING":
                    sb.append(variable.getName())
                            .append(" (").append(variable.getType()).append(") = ")
                            .append(variable.getValue()).append("\n");
                    break;

                case "TIMER":
                    TimerVariable timer = (TimerVariable) variable;
                    sb.append(variable.getName())
                            .append(" (").append(variable.getType()).append(") = ")
                            .append(timer.getCurrentTickCount()).append("/").append(timer.getTicks())
                            .append("(").append(timer.getTimeFromLastTick()).append("/").append(timer.getElapse()).append("ms)")
                            .append("\n");
                    break;
            }
        }

        debugVariablesValues = sb.toString();
    }

    private Vector2 getMousePosition() {
        return new Vector2(Gdx.input.getX(), Gdx.input.getY());
    }

    private int getPriority(Variable image) {
        if (image != null && image.getAttribute("PRIORITY") != null) {
            return Integer.parseInt(image.getAttribute("PRIORITY").getValue().toString());
        }
        return 0;
    }

    private Variable getButtonAt(int x, int y) {
        Context context = game.getCurrentSceneContext();
        List<Variable> buttons = new ArrayList<>(context.getButtonsVariables().values());

        int minHSPriority = game.getCurrentSceneVariable().getMinHotSpotZ();
        int maxHSPriority = game.getCurrentSceneVariable().getMaxHotSpotZ();

        Variable focusedButton = null;

        for (Variable variable : buttons) {
            if (variable instanceof ButtonVariable) {
                ButtonVariable button = (ButtonVariable) variable;
                Variable image = button.getCurrentImage();

                // Filter by hotspot priority
                if (image != null) {
                    int priority = getPriority(image);
                    if (priority < minHSPriority || priority > maxHSPriority) continue;
                }

                // Check if button is enabled
                if (button.isEnabled() && button.getRect() != null && button.getRect().contains(x, y)) {
                    focusedButton = button;
                    break;
                }
            } else if (variable instanceof AnimoVariable) {
                AnimoVariable animo = (AnimoVariable) variable;

                // Filter by hotspot priority
                int priority = getPriority(animo);
                if (priority < minHSPriority || priority > maxHSPriority) continue;

                // Check if animo is under cursor
                if (animo.getRect() != null && animo.getRect().contains(x, y)) {
                    focusedButton = animo;
                    break;
                }
            }
        }

        return focusedButton;
    }

    private Variable getGraphicsAt(int x, int y) {
        Context context = game.getCurrentSceneContext();
        List<Variable> drawList = new ArrayList<>(context.getGraphicsVariables().values());

        // Reverse list
        List<Variable> reversedList = new ArrayList<>(drawList);
        java.util.Collections.reverse(reversedList);

        for (Variable variable : reversedList) {
            boolean visible = false;

            if (variable instanceof ImageVariable) {
                visible = ((ImageVariable) variable).isVisible();
            } else if (variable instanceof AnimoVariable) {
                visible = ((AnimoVariable) variable).isVisible();
            }

            if (!visible) continue;

            Box2D rect = getRect(variable);
            if (rect != null && rect.contains(x, y)) {
                return variable;
            }
        }

        return null;
    }

    private Box2D getRect(Variable variable) {
        if (variable instanceof ImageVariable) {
            return ((ImageVariable) variable).getRect();
        } else if (variable instanceof AnimoVariable) {
            return ((AnimoVariable) variable).getRect();
        }
        return null;
    }

    private boolean isGraphicsButton(Variable variable) {
        Box2D rect = getRect(variable);
        Context context = game.getCurrentSceneContext();

        // Check if graphics is a button
        for (Variable v : context.getVariables().values()) {
            if (!v.getType().equals("BUTTON")) continue;

            if (v.getAttribute("GFXSTANDARD") != null &&
                    v.getAttribute("GFXSTANDARD").getValue().equals(variable.getName())) {
                return true;
            }

            if (rect == null) continue;

            ButtonVariable buttonVariable = (ButtonVariable) v;
            if (buttonVariable.getRect() != null && buttonVariable.getRect().intersects(rect)) {
                return true;
            }
        }

        if (variable instanceof AnimoVariable) {
            return ((AnimoVariable) variable).isAsButton();
        }

        return false;
    }

    public void setDebugRect(Box2D rect) {
        this.debugRect = rect;
    }

    public void toggleSceneSelector() {
        showSceneSelector = !showSceneSelector;
        if (showSceneSelector) {
            updateSceneList();
        }
    }

    private void updateSceneList() {
        sceneList.clear();

        // Get every scene
        for (EpisodeVariable episode : game.getApplicationVariable().getEpisodes()) {
            for (SceneVariable scene : episode.getScenes()) {
                sceneList.add(scene.getName());
            }
        }

        // Sort
        Collections.sort(sceneList);

        // Set selected scene
        selectedScene = sceneList.indexOf(game.getCurrentScene());
    }

    private void renderSceneSelector() {
        if (!showSceneSelector) return;

        int width = 300;
        int height = Math.min(sceneList.size(), MAX_VISIBLE_SCENES) * 20 + 40;

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
        font.draw(batch, "Scene Selector (F9 to toggle)", selectorPosition.x + 10, selectorPosition.y + height - 10);

        // Draw scene list
        int visibleCount = Math.min(sceneList.size(), MAX_VISIBLE_SCENES);

        for (int i = 0; i < visibleCount; i++) {
            int index = scrollPosition + i;
            if (index >= sceneList.size()) break;

            if (index == selectedScene) {
                font.setColor(Color.GREEN);  // Selected scene
            } else if (sceneList.get(index).equals(game.getCurrentScene())) {
                font.setColor(Color.CYAN);   // Current scene
            } else {
                font.setColor(Color.WHITE);  // Rest of scenes
            }

            float itemY = selectorPosition.y + height - 35 - (i * 20);

            font.draw(batch, sceneList.get(index), selectorPosition.x + 15, itemY);
        }

        batch.end();

        if (sceneList.size() > MAX_VISIBLE_SCENES) {
            float scrollableArea = height - 40;
            float totalItems = sceneList.size();
            float visibleItems = MAX_VISIBLE_SCENES;

            float barHeight = (visibleItems / totalItems) * scrollableArea;

            float barPositionRatio = scrollPosition / (totalItems - visibleItems);
            float barY = selectorPosition.y + 10 + (scrollableArea - barHeight) * (1.0f - barPositionRatio);

            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(Color.GRAY);
            shapeRenderer.rect(selectorPosition.x + width - 15, selectorPosition.y + 10, 10, scrollableArea);
            shapeRenderer.setColor(Color.WHITE);
            shapeRenderer.rect(selectorPosition.x + width - 13, barY, 6, barHeight);
            shapeRenderer.end();
        }
    }

    public void handleSceneSelectorInput(float deltaTime) {
        if (!showSceneSelector) return;

        boolean upPressed = Gdx.input.isKeyPressed(Input.Keys.UP);
        boolean downPressed = Gdx.input.isKeyPressed(Input.Keys.DOWN);

        if (upPressed || downPressed) {
            if (!keyIsDown) {
                keyIsDown = true;
                keyRepeatTimer = 0;

                if (upPressed) {
                    moveSelectionUp();
                } else {
                    moveSelectionDown();
                }
            } else {
                keyRepeatTimer += deltaTime;

                if (keyRepeatTimer > KEY_INITIAL_DELAY) {
                    float timeSinceDelay = keyRepeatTimer - KEY_INITIAL_DELAY;
                    if (timeSinceDelay % KEY_REPEAT_INTERVAL < deltaTime) {
                        if (upPressed) {
                            moveSelectionUp();
                        } else {
                            moveSelectionDown();
                        }
                    }
                }
            }
        } else {
            keyIsDown = false;
            keyRepeatTimer = 0;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) && selectedScene >= 0) {
            // Goto scene
            game.goTo(sceneList.get(selectedScene));
            showSceneSelector = false;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            // Close selector
            showSceneSelector = false;
        }

        Gdx.input.setInputProcessor(new InputAdapter());
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
        if (selectedScene < sceneList.size() - 1) {
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