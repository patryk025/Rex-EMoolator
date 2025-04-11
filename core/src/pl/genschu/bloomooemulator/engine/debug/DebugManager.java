package pl.genschu.bloomooemulator.engine.debug;

import com.badlogic.gdx.Gdx;
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
import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.interpreter.variable.types.*;
import pl.genschu.bloomooemulator.objects.Rectangle;

import java.util.ArrayList;
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
    private Rectangle debugRect;

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

        if (config.isDebugVariables()) {
            renderVariablesDebug();
        }

        if (debugRect != null) {
            renderDebugRectangle();
        }

        if (showTooltip && !tooltipText.isEmpty()) {
            renderTooltip();
        }
    }

    private void renderGraphicsDebug() {
        // Get info about graphics under cursor
        Vector2 mousePos = getMousePosition();
        Variable graphicsUnderCursor = getGraphicsAt((int) mousePos.x, (int) mousePos.y);

        if (graphicsUnderCursor != null) {
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

    private void renderDebugRectangle() {
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.RED);
        Rectangle rect = debugRect;
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
            Rectangle rect = null;
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

    private void generateTooltipForGraphics(Variable graphics) {
        StringBuilder sb = new StringBuilder();
        sb.append(graphics.getName()).append(" (").append(graphics.getType()).append(")\n");
        sb.append("Priority: ").append(graphics.getAttribute("PRIORITY") != null ?
                graphics.getAttribute("PRIORITY").getValue() : 0);

        Rectangle rect = getRect(graphics);
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
            } else if (variable instanceof SequenceVariable) {
                visible = ((SequenceVariable) variable).isVisible();
            }

            if (!visible) continue;

            Rectangle rect = getRect(variable);
            if (rect != null && rect.contains(x, y)) {
                return variable;
            }
        }

        return null;
    }

    private Rectangle getRect(Variable variable) {
        if (variable instanceof ImageVariable) {
            return ((ImageVariable) variable).getRect();
        } else if (variable instanceof AnimoVariable) {
            return ((AnimoVariable) variable).getRect();
        }
        return null;
    }

    private boolean isGraphicsButton(Variable variable) {
        Rectangle rect = getRect(variable);
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

    public void setDebugRect(Rectangle rect) {
        this.debugRect = rect;
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        font.dispose();
    }
}