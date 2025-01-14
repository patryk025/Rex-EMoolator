package pl.genschu.bloomooemulator;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.exceptions.BreakException;
import pl.genschu.bloomooemulator.interpreter.variable.Attribute;
import pl.genschu.bloomooemulator.interpreter.variable.Signal;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.interpreter.variable.types.*;
import pl.genschu.bloomooemulator.logic.GameEntry;
import pl.genschu.bloomooemulator.objects.*;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import pl.genschu.bloomooemulator.utils.CollisionChecker;

import java.util.*;
import java.util.stream.Collectors;

public class BlooMooEmulator extends ApplicationAdapter {
    private static final float VIRTUAL_WIDTH = 800;
    private static final float VIRTUAL_HEIGHT = 600;

    SpriteBatch batch;
    Context context;
    OrthographicCamera camera;
    Viewport viewport;

    GameEntry gameEntry;
    Game game;

    private final boolean debugButtons = false;

    private Texture cursorTexture;

    private Variable activeButton = null;
    private boolean prevPressed = false;
    private ShapeRenderer shape;

    private boolean debugGraphics = true;

    private String tooltipText = "";
    private final Vector2 tooltipPosition = new Vector2();
    private boolean showTooltip = false;
    private BitmapFont font;

    private boolean showDebugVariables = false;
    private String debugVariablesValues = "";

    public BlooMooEmulator(GameEntry gameEntry) {
        this.gameEntry = gameEntry;
    }
    
    @Override
    public void create () {
        batch = new SpriteBatch();
        font = new BitmapFont();

        this.game = new Game(this.gameEntry, this);

        context = this.game.getCurrentSceneContext();

        shape = new ShapeRenderer();
        camera = new OrthographicCamera();
        if(gameEntry.isMaintainAspectRatio())
            viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
        else
            viewport = new StretchViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
        viewport.apply();
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
        
        cursorTexture = new Texture(Gdx.files.internal("kropka.png"));

        Gdx.app.setLogLevel(Application.LOG_DEBUG);
    }

    @Override
    public void render () {
        float deltaTime = Gdx.graphics.getDeltaTime();
        ScreenUtils.clear(0, 0, 0, 1);
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        context = this.game.getCurrentSceneContext();

        List<Variable> drawList = getGraphicsVariables();

        MouseVariable mouseVariable = context.getMouseVariable();

        batch.begin();
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        // Handle mouse events
        int x = Gdx.input.getX();
        int y = Gdx.input.getY();

        if(x >= 0 && y >= 0 && x < Gdx.graphics.getWidth() && y < Gdx.graphics.getHeight()) {
            boolean isPressed = Gdx.input.isButtonPressed(Input.Buttons.LEFT);
            boolean justPressed = !prevPressed && isPressed;
            boolean justReleased = prevPressed && !isPressed;

            // correct coordinates according to window size
            Vector2 correctedVector = getCorrectedMouseCoords(x, y, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), (int) VIRTUAL_WIDTH, (int) VIRTUAL_HEIGHT);
            x = (int) correctedVector.x;
            y = (int) correctedVector.y;

            if(mouseVariable != null) {
                mouseVariable.update(x, y);

                handleMouseInput(x, y, isPressed, justPressed, justReleased, mouseVariable, new ArrayList<>(drawList));
            }

            prevPressed = isPressed;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.F1)) {
            showDebugVariables = !showDebugVariables;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.F2)) {
            debugGraphics = !debugGraphics;
            showTooltip = false;
        }

        ImageVariable background = game.getCurrentSceneVariable().getBackground();
        if(background != null) {
            Image image = background.getImage();
            if(image.getImageTexture() != null) {
                batch.setColor(1, 1, 1, background.getOpacity());
                batch.draw(image.getImageTexture(), image.offsetX, VIRTUAL_HEIGHT-image.offsetY-image.height, image.width, image.height);
            }
        }

        for (Variable variable : drawList) {
            if(variable instanceof ImageVariable) {
                ImageVariable imageVariable = (ImageVariable) variable;
                Image image = imageVariable.getImage();

                if(imageVariable.isVisible()) {
                    //Gdx.app.log(variable.getName(), ((ImageVariable) variable).getRect().toString());

                    batch.setColor(1, 1, 1, imageVariable.getOpacity());

                    Rectangle rect = imageVariable.getRect();
                    Rectangle clippingRect = imageVariable.getClippingRect();
                    if(clippingRect != null) {
                        int xLeft = clippingRect.getXLeft();
                        int yTop = (int) (VIRTUAL_HEIGHT - clippingRect.getYTop());

                        int xRight = clippingRect.getXRight();
                        int yBottom = (int) (VIRTUAL_HEIGHT - clippingRect.getYBottom());

                        Vector2 projectedCoordsLeftTop = cameraToWindowCoordinates(camera, xLeft, yTop);
                        Vector2 projectedCoordsRightBottom = cameraToWindowCoordinates(camera, xRight, yBottom);

                        int scissorX = (int) projectedCoordsLeftTop.x;
                        int scissorY = (int) projectedCoordsLeftTop.y;
                        int scissorWidth = (int) (projectedCoordsRightBottom.x - projectedCoordsLeftTop.x);
                        int scissorHeight = (int) (projectedCoordsRightBottom.y - projectedCoordsLeftTop.y);

                        batch.flush();

                        Gdx.gl.glEnable(GL20.GL_SCISSOR_TEST);
                        Gdx.gl.glScissor(scissorX, scissorY, scissorWidth, scissorHeight);

                        try {
                            batch.draw(image.getImageTexture(), rect.getXLeft(), VIRTUAL_HEIGHT - rect.getYTop() - image.height, image.width, image.height);
                        } catch(NullPointerException e) {
                            Gdx.app.error("Render", e.getMessage());
                        }

                        batch.flush();

                        Gdx.gl.glDisable(GL20.GL_SCISSOR_TEST);
                    }
                    else {
                        try {
                            batch.draw(image.getImageTexture(), rect.getXLeft(), VIRTUAL_HEIGHT - rect.getYTop() - image.height, image.width, image.height);
                        } catch(NullPointerException ignored) {
                        }
                    }
                }
            } else if(variable instanceof AnimoVariable) {
                AnimoVariable animoVariable = (AnimoVariable) variable;
                if(animoVariable.isVisible()) {
                    //Gdx.app.log(variable.getName(), ((AnimoVariable) variable).getRect().toString());
                    try {
                        Image image = animoVariable.getCurrentImage();
                        if(image == null) continue;
                        Event event = animoVariable.getCurrentEvent();
                        if (event == null) continue;

                        batch.setColor(1, 1, 1, animoVariable.getOpacity());

                        Rectangle rect = animoVariable.getRect();
                        try {
                            batch.draw(image.getImageTexture(), rect.getXLeft(), VIRTUAL_HEIGHT - rect.getYTop() - image.height, image.width, image.height);
                        } catch (NullPointerException ignored) {}
                        animoVariable.updateAnimation(deltaTime);
                    } catch(NullPointerException ignored) {
                        Gdx.app.log("AnimoVariable", "Image not found in Animo "+animoVariable.getName());
                    }
                }
            } else if(variable instanceof SequenceVariable) {
                SequenceVariable sequenceVariable = (SequenceVariable) variable;

                if(!sequenceVariable.isVisible()) continue;
                
                if(drawList.contains(sequenceVariable.getCurrentAnimo())) continue;

                if(sequenceVariable.getCurrentAnimo() == null) continue;
                if(!sequenceVariable.getCurrentAnimo().isVisible()) continue;

                //Gdx.app.log(variable.getName(), ((SequenceVariable) variable).getCurrentAnimo().getRect().toString());
                try {
                    Image image = sequenceVariable.getCurrentAnimo().getCurrentImage();
                    Event event = sequenceVariable.getCurrentAnimo().getCurrentEvent();
                    if (event == null) continue;

                    batch.setColor(1, 1, 1, sequenceVariable.getCurrentAnimo().getOpacity());

                    Rectangle rect = sequenceVariable.getCurrentAnimo().getRect();
                    try {
                        batch.draw(image.getImageTexture(), rect.getXLeft(), VIRTUAL_HEIGHT - rect.getYTop() - image.height, image.width, image.height);
                    } catch (NullPointerException ignored) {}
                    if(sequenceVariable.isPlaying()) {
                        sequenceVariable.updateAnimation(deltaTime);
                    }
                } catch(NullPointerException e) {
                    //Gdx.app.log("SequenceVariable", "Image not found in Sequence "+sequenceVariable.getName());
                }
            }
        }

        // debug font
        for(Variable variable : new ArrayList<>(context.getTextVariables().values())) {
            TextVariable textVariable = (TextVariable) variable;
            if(textVariable.isVisible()) {
                textVariable.renderText(batch);
            }
        }

        // update timers
        for (Variable variable : new ArrayList<>(context.getTimerVariables().values())) {
            TimerVariable timer = (TimerVariable) variable;
            try {
                timer.update();
            } catch (BreakException ignored) {
                // simple break, nothing special here
            }
        }

        checkForCollisions();

        if(debugButtons) {
            List<Variable> buttons = new ArrayList<>(context.getButtonsVariables().values());

            Collections.sort(buttons, (o1, o2) -> {
                Variable image1 = null;
                if(o1 instanceof ButtonVariable) {
                    image1 = ((ButtonVariable) o1).getCurrentImage();
                }
                else if(o1 instanceof AnimoVariable) {
                    image1 = o1;
                }
                Variable image2 = null;
                if(o2 instanceof ButtonVariable) {
                    image2 = ((ButtonVariable) o2).getCurrentImage();
                }
                else if(o2 instanceof AnimoVariable) {
                    image2 = o2;
                }

                int priority1 = 0;
                int priority2 = 0;

                if(image1 != null) {
                    priority1 = image1.getAttribute("PRIORITY") != null ? Integer.parseInt(image1.getAttribute("PRIORITY").getValue().toString()) : 0;
                }
                if(image2 != null) {
                    priority2 = image2.getAttribute("PRIORITY") != null ? Integer.parseInt(image2.getAttribute("PRIORITY").getValue().toString()) : 0;
                }

                return Integer.compare(priority2, priority1);
            });

            for (Variable variable : buttons) {
                if(variable instanceof ButtonVariable) {
                    ButtonVariable button = (ButtonVariable) variable;

                    if (!button.isEnabled()) continue;

                    if (button.getRect() != null && button.getRect().contains(x, y)) {
                        drawRectangle(button.getRect(), Color.GREEN);
                    } else if (button.getRect() != null) {
                        drawRectangle(button.getRect(), Color.RED);
                    }
                }
                else if(variable instanceof AnimoVariable) {
                    AnimoVariable animoVariable = (AnimoVariable) variable;
                    if (animoVariable.getRect() != null && animoVariable.getRect().contains(x, y)) {
                        drawRectangle(animoVariable.getRect(), Color.GREEN);
                    } else if (animoVariable.getRect() != null) {
                        drawRectangle(animoVariable.getRect(), Color.RED);
                    }
                }
            }
        }

        batch.end();

        game.takeScreenshot();
        //dumpVariablesToLog();

        if (showDebugVariables) {
            generateDebugVariables();

        }
        renderTooltip();
    }

    private void checkForCollisions() {
        List<Variable> objects = new ArrayList<>(game.getCollisionMonitoredVariables());
        //Gdx.app.log("CollisionList", getDrawListAsString(objects));
        for (Variable object : objects) {
            List<Variable> potentialCollisions = game.getQuadTree().retrieve(new ArrayList<>(), object);
            for (Variable other : potentialCollisions) {
                if (other != object && CollisionChecker.checkCollision(object, other)) {
                    if (!object.isColliding(other)) {
                        object.setColliding(other);
                    }
                } else if (object.isColliding(other)) {
                    object.releaseColliding(other);
                }
            }
        }
    }

    private Rectangle getRect(Variable variable) {
        if(variable instanceof ImageVariable) {
            return ((ImageVariable) variable).getRect();
        }
        if(variable instanceof AnimoVariable) {
            return ((AnimoVariable) variable).getRect();
        }
        if(variable instanceof SequenceVariable) {
            return ((SequenceVariable) variable).getCurrentAnimo().getRect();
        }
        return null;
    }

    private Image getImage(Variable variable) {
        if(variable instanceof ImageVariable) {
            return ((ImageVariable) variable).getImage();
        }
        if(variable instanceof AnimoVariable) {
            return ((AnimoVariable) variable).getCurrentImage();
        }
        if(variable instanceof SequenceVariable) {
            return ((SequenceVariable) variable).getCurrentAnimo().getCurrentImage();
        }
        return null;
    }

    public void handleMouseInput(int x, int y, boolean isPressed, boolean justPressed, boolean justReleased, MouseVariable mouseVariable, List<Variable> drawList) {
        //Gdx.app.log("Mouse", "x: " + x + " y: " + y);
        List<Variable> buttons = new ArrayList<>(context.getButtonsVariables().values());

        Map<String, Variable> classInstances = context.getClassInstances();
        for (Variable variable : classInstances.values()) {
            List<Variable> classButtons = new ArrayList<>(variable.getContext().getButtonsVariables(false).values());

            buttons.addAll(classButtons);
        }

        int minHSPriority = game.getCurrentSceneVariable().getMinHotSpotZ();
        int maxHSPriority = game.getCurrentSceneVariable().getMaxHotSpotZ();

        Collections.sort(buttons, (o1, o2) -> {
            Variable image1 = null;
            if(o1 instanceof ButtonVariable) {
                image1 = ((ButtonVariable) o1).getCurrentImage();
            }
            else if(o1 instanceof AnimoVariable) {
                image1 = o1;
            }
            Variable image2 = null;
            if(o2 instanceof ButtonVariable) {
                image2 = ((ButtonVariable) o2).getCurrentImage();
            }
            else if(o2 instanceof AnimoVariable) {
                image2 = o2;
            }

            int priority1 = 0;
            int priority2 = 0;

            if(image1 != null) {
                priority1 = image1.getAttribute("PRIORITY") != null ? Integer.parseInt(image1.getAttribute("PRIORITY").getValue().toString()) : 0;
            }
            if(image2 != null) {
                priority2 = image2.getAttribute("PRIORITY") != null ? Integer.parseInt(image2.getAttribute("PRIORITY").getValue().toString()) : 0;
            }

            return Integer.compare(priority2, priority1);
        });

        for (Variable variable : buttons) {
            if(variable instanceof ButtonVariable) {
                ButtonVariable button = (ButtonVariable) variable;
                /*if(!button.isLoaded()) {
                    button.loadImages();
                }*/

                Variable image = button.getCurrentImage();
                if(image != null) {
                    int priority = image.getAttribute("PRIORITY") != null ? Integer.parseInt(image.getAttribute("PRIORITY").getValue().toString()) : 0;
                    if(priority < minHSPriority || priority > maxHSPriority) continue;
                }

                if(!button.isEnabled()) continue;

                if (button.getRect() != null && button.getRect().contains(x, y)) {
                    if(mouseVariable == null || mouseVariable.isVisible())
                        Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Hand);
                    if (justPressed) {
                        if (activeButton == null) {
                            activeButton = button;
                            triggerSignal(button, "ONCLICKED");
                            triggerSignal(button, "ONACTION");
                        }
                    }
                    if (button == activeButton) {
                        if (isPressed) {
                            if (button.isEnabled()) {
                                button.setPressed(true);
                            }
                        } else if (justReleased) {
                            if (button.isEnabled()) {
                                triggerSignal(button, "ONRELEASED");
                                button.setPressed(false);
                                triggerSignal(button, "GFXONCLICK");
                            }
                            activeButton = null;
                        }
                    }

                    if (!button.isFocused() && !isPressed && button.isEnabled()) {
                        button.setFocused(true);
                        if(mouseVariable == null || mouseVariable.isVisible())
                            Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Hand);
                        Signal onFocusSignal = button.getSignal("ONFOCUSON");
                        if (onFocusSignal != null) {
                            onFocusSignal.execute(null);
                        }
                    }
                }
                else {
                    if (button.isFocused() && !isPressed) {
                        button.setFocused(false);
                        if(mouseVariable == null || mouseVariable.isVisible())
                            Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
                        Signal onFocusLossSignal = button.getSignal("ONFOCUSOFF");
                        if (onFocusLossSignal != null) {
                            onFocusLossSignal.execute(null);
                        }
                    } else {
                        button.setFocused(false);
                    }
                }
            } else if(variable instanceof AnimoVariable) {
                AnimoVariable animo = (AnimoVariable) variable;

                int priority = variable.getAttribute("PRIORITY") != null ? Integer.parseInt(variable.getAttribute("PRIORITY").getValue().toString()) : 0;
                if(priority < minHSPriority || priority > maxHSPriority) continue;

                if (animo.getRect() != null && animo.getRect().contains(x, y)) {
                    if(animo.isChangeCursor() && (mouseVariable == null || mouseVariable.isVisible()))
                        Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Hand);
                    if (justPressed) {
                        if (activeButton == null) {
                            activeButton = animo;
                            triggerSignal(animo, "ONCLICK");
                            animo.fireMethod("PLAY", new StringVariable("", "ONCLICK", context));
                            animo.setPlaying(false);
                        }
                    }
                    if (animo == activeButton) {
                        if (isPressed) {
                            animo.setPressed(true);
                        } else if (justReleased) {
                            animo.setPressed(false);
                            activeButton = null;
                        }
                    }

                    if (!animo.isFocused() && !isPressed) {
                        animo.setFocused(true);
                        if(animo.isChangeCursor() && (mouseVariable == null || mouseVariable.isVisible()))
                            Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Hand);
                        Signal onFocusSignal = animo.getSignal("ONFOCUSON");
                        if (onFocusSignal != null) {
                            onFocusSignal.execute(null);
                        }
                        animo.fireMethod("PLAY", new StringVariable("", "ONFOCUSON", context));
                        animo.setPlaying(false);
                    }
                }
                else {
                    if (animo.isFocused() && !isPressed) {
                        animo.setFocused(false);
                        if(mouseVariable == null || mouseVariable.isVisible())
                            Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
                        Signal onFocusLossSignal = animo.getSignal("ONFOCUSOFF");
                        if (onFocusLossSignal != null) {
                            onFocusLossSignal.execute(null);
                        }
                        animo.fireMethod("PLAY", new StringVariable("", "ONFOCUSOFF", context));
                        animo.setPlaying(false);
                    }
                }
            }
        }

        if (justReleased && activeButton != null) {
            if(activeButton instanceof ButtonVariable) {
                triggerSignal(activeButton, "ONRELEASED");
                ((ButtonVariable) activeButton).setPressed(false);
                triggerSignal(activeButton, "GFXONCLICK");
            }
            else if(activeButton instanceof AnimoVariable) {
                ((AnimoVariable) activeButton).setPressed(false);
            }
            activeButton = null;
        }

        Variable graphics = getGraphicsAt(x, y, drawList, false);

        //Gdx.app.log("Mouse", "mouseVariable != null: " + (mouseVariable != null) + " isPressed: " + isPressed + " mouseVariable.isEnabled(): " + (mouseVariable != null && mouseVariable.isEnabled()));
        if(mouseVariable != null) {
            if (justPressed) {
                mouseVariable.emitSignal("ONCLICK", "LEFT"); // right is not used at all
            } else if (justReleased) {
                mouseVariable.emitSignal("ONRELEASE", "LEFT"); // right is not used at all
            }
        }

        if(debugGraphics) {
            if (graphics != null) {
                tooltipText = graphics.getName() + " (" + graphics.getType() + ")\nPriority: " + (graphics.getAttribute("PRIORITY") != null ? graphics.getAttribute("PRIORITY").getValue() : 0);
                Rectangle rect = getRect(graphics);
                String rectText = "\nRect: " + (rect != null ? ("\n    left upper corner: (" + rect.getXLeft() + ", " + rect.getYTop() + ")\n    width: " + rect.getWidth() + "\n    height: " + rect.getHeight()) : "no defined");
                if(graphics instanceof AnimoVariable) {
                    tooltipText += "\nCurrent FPS: " + ((AnimoVariable) graphics).getFps();
                    tooltipText += "\nCurrent event: " + ((AnimoVariable) graphics).getCurrentEvent().getName();
                    tooltipText += "\nCurrent frame number: " + ((AnimoVariable) graphics).getCurrentFrameNumber();
                    tooltipText += "\nCurrent image number: " + ((AnimoVariable) graphics).getCurrentImageNumber();
                    tooltipText += "\nIs playing: " + ((AnimoVariable) graphics).isPlaying();
                    tooltipText += "\nCollision monitoring: " + (graphics.getAttribute("MONITORCOLLISION") != null ? graphics.getAttribute("MONITORCOLLISION").getValue() : "FALSE");
                    tooltipText += rectText;
                }
                else if(graphics instanceof SequenceVariable) {
                    tooltipText += "\nCurrent FPS: " + ((SequenceVariable) graphics).getCurrentAnimo().getFps();
                    tooltipText += "\nCurrent event: " + ((SequenceVariable) graphics).getCurrentEventName();
                    tooltipText += "\nCurrent animo event: " + ((SequenceVariable) graphics).getCurrentAnimo().getCurrentEvent().getName();
                    tooltipText += "\nCurrent frame number: " + ((SequenceVariable) graphics).getCurrentAnimo().getCurrentFrameNumber();
                    tooltipText += "\nCurrent image number: " + ((SequenceVariable) graphics).getCurrentAnimo().getCurrentImageNumber();
                    tooltipText += "\nIs playing: " + ((SequenceVariable) graphics).getCurrentAnimo().isPlaying();
                    tooltipText += rectText;
                }
                else if(graphics instanceof ImageVariable) {
                    tooltipText += "\nCollision monitoring: " + (graphics.getAttribute("MONITORCOLLISION") != null ? graphics.getAttribute("MONITORCOLLISION").getValue() : "FALSE");
                    tooltipText += rectText;
                }
                else {
                    tooltipText += rectText;
                }
                tooltipPosition.set(x + 20, VIRTUAL_HEIGHT - y - 20);
                showTooltip = true;
            } else {
                showTooltip = false;
            }
        }
    }

    private void generateDebugVariables() {
        StringBuilder sb = new StringBuilder();
        sb.append("Scena: ").append(game.getCurrentScene()).append("\n").append("\n");
        for(Variable variable : game.getCurrentSceneContext().getVariables().values()) {
            switch(variable.getType()) {
                case "INTEGER":
                case "DOUBLE":
                case "BOOL":
                case "STRING":
                    sb.append(variable.getName()).append(" (").append(variable.getType()).append(") = ").append(variable.getValue()).append("\n");
                    break;
            }
        }
        debugVariablesValues = sb.toString();

        batch.begin();
        font.setColor(Color.WHITE);
        font.draw(batch, debugVariablesValues, 5, VIRTUAL_HEIGHT - 5);
        batch.end();
    }

    private void renderTooltip() {
        if (showTooltip && !tooltipText.isEmpty()) {
            GlyphLayout layout = new GlyphLayout(font, tooltipText);
            float width = layout.width + 10;
            float height = layout.height + 10;

            float x = tooltipPosition.x;
            float y = tooltipPosition.y;

            // sprawdzanie, czy tooltip nie wyjeżdża z prawej strony ekranu i z dołu
            if(x + width > VIRTUAL_WIDTH) {
                x = VIRTUAL_WIDTH - width;
            }

            if(y - height < 0) {
                y = height;
            }

            // Rysowanie tła i obramowania
            shape.begin(ShapeRenderer.ShapeType.Filled);
            shape.setColor(new Color(0, 0, 0, 0.75f));
            shape.rect(x, y - height, width, height);
            shape.end();

            shape.begin(ShapeRenderer.ShapeType.Line);
            shape.setColor(Color.WHITE);
            shape.rect(x, y - height, width, height);
            shape.end();

            batch.begin();
            font.setColor(Color.WHITE);
            font.draw(batch, tooltipText, x + 5, y - 5);
            batch.end();
        }
    }

    private Variable getGraphicsAt(int x, int y, List<Variable> drawList, boolean useAlpha) {
        try {
            Collections.reverse(drawList);
            for (Variable variable : drawList) {
                boolean visible = false;
                if (variable instanceof ImageVariable) {
                    visible = ((ImageVariable) variable).isVisible();
                }
                if (variable instanceof AnimoVariable) {
                    visible = ((AnimoVariable) variable).isVisible();
                }
                if (variable instanceof SequenceVariable) {
                    visible = ((SequenceVariable) variable).isVisible();
                }
                if (!visible) {
                    continue;
                }

                Rectangle rect = getRect(variable);
                if (rect.contains(x, y)) {
                    if(useAlpha) {
                        return variable;
                    }
                    Image image = getImage(variable);
                    int relativeX = x - rect.getXLeft();
                    int relativeY = y - rect.getYTop();
                    int alpha = getAlpha(image, relativeX, relativeY);

                    //Gdx.app.log(variable.getName(), "relativeX: " + relativeX + ", relativeY: " + relativeY + ", alpha: " + alpha);

                    if (alpha > 0) {
                        return variable;
                    }
                }
            }
        } catch (NullPointerException ignored) {}

        return null;
    }

    private static int getAlpha(Image image, int relativeX, int relativeY) {
        int alpha = 255;

        if(image.getImageTexture() != null) {
            TextureData textureData = image.getImageTexture().getTextureData();
            if (!textureData.isPrepared()) {
                textureData.prepare();
            }
            Pixmap pixmap = textureData.consumePixmap();
            int pixel = pixmap.getPixel(relativeX, relativeY);
            alpha = (pixel & 0xFF);
        }
        return alpha;
    }

    private void drawRectangle(Rectangle rect, Color color) {
        shape.setProjectionMatrix(camera.combined);
        shape.begin(ShapeRenderer.ShapeType.Line);
        shape.setColor(color);
        int height = rect.getYTop() - rect.getYBottom();
        int width = rect.getXRight() - rect.getXLeft();
        shape.rect(rect.getXLeft(), VIRTUAL_HEIGHT - rect.getYTop() - height, width, height);
        shape.end();
    }

    private void triggerSignal(Variable variable, String signalName) {
        Signal signal = variable.getSignal(signalName);
        if (signal != null) {
            signal.execute(null);
        }
    }

    private List<Variable> getGraphicsVariables() {
        List<Variable> drawList = new ArrayList<>(context.getGraphicsVariables().values());

        Map<String, Variable> classInstances = context.getClassInstances();
        for (Variable variable : classInstances.values()) {
            List<Variable> classDrawList = new ArrayList<>(variable.getContext().getGraphicsVariables(false).values());

            drawList.addAll(classDrawList);
        }

        //Gdx.app.log("Draw list before sort", getDrawListAsString(drawList));
        Comparator<Variable> comparator = (o1, o2) -> {
            Attribute priorityAttr1 = o1.getAttribute("PRIORITY");
            Attribute priorityAttr2 = o2.getAttribute("PRIORITY");
            int priority1 = priorityAttr1 != null ? Integer.parseInt(priorityAttr1.getValue().toString()) : 0;
            int priority2 = priorityAttr2 != null ? Integer.parseInt(priorityAttr2.getValue().toString()) : 0;
            return Integer.compare(priority1, priority2);
        };
        Collections.sort(drawList, comparator);
        //Gdx.app.log("Draw list after sort", getDrawListAsString(drawList));
        return drawList;
    }

    private String getDrawListAsString(List<Variable> drawList) {
        StringBuilder sb = new StringBuilder();
        for (Variable variable : drawList) {
            boolean visible = false;
            if (variable instanceof ImageVariable) {
                visible = ((ImageVariable) variable).isVisible();
            }
            if (variable instanceof AnimoVariable) {
                visible = ((AnimoVariable) variable).isVisible();
            }
            if (variable instanceof SequenceVariable) {
                visible = ((SequenceVariable) variable).isVisible();
            }
            if (!visible) {
                continue;
            }

            if (variable instanceof SequenceVariable) {
                /*SequenceVariable seqVar = (SequenceVariable) variable;
                if(seqVar.getCurrentEventName() != null) {
                    if (seqVar.getCurrentEventName().isEmpty()) {
                        sb.append("__").append(seqVar.getName()).append("_ANIMO_");
                    }
                }*/
            } else {
                sb.append(variable.getName());
                Attribute priority = variable.getAttribute("PRIORITY");
                if (priority != null) {
                    sb.append(" (").append(priority.getValue()).append(")");
                }
                sb.append(", ");
            }
        }
        if (sb.length() > 2) {
            sb.setLength(sb.length() - 2);
        }
        return sb.toString();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void dispose () {
        batch.dispose();
        game.dispose();
    }

    private void dumpVariablesToLog() {
        FileHandle file = Gdx.files.local("log.txt");
        List<Variable> variables = new ArrayList<>(context.getVariables().values());
        //clear file
        file.writeString("", false);
        for (Variable variable : variables) {
            if(variable instanceof StringVariable || variable instanceof DoubleVariable || variable instanceof IntegerVariable || variable instanceof BoolVariable) {
                file.writeString(variable.getName() + "=" + variable.getValue() + "\n", true);
            }
        }
    }
    
    public Vector2 getCorrectedMouseCoords(int screenX, int screenY, int windowWidth, int windowHeight, int virtualWidth, int virtualHeight) {
        // calculate ratios and scales
        float aspectRatio = (float) virtualWidth / virtualHeight;
        float windowRatio = (float) windowWidth / windowHeight;
    
        float scale = windowRatio > aspectRatio ? (float) windowHeight / virtualHeight : (float) windowWidth / virtualWidth;
    
        float correctX = (windowWidth - virtualWidth * scale) / 2;
        float correctY = (windowHeight - virtualHeight * scale) / 2;
    
        // correct coordinates
        float x = (screenX - correctX) / scale;
        float y = (screenY - correctY) / scale;
    
        return new Vector2(x, y);
    }

    public Vector2 cameraToWindowCoordinates(Camera camera, float x, float y) {
        Vector3 worldCoordinates = new Vector3(x, y, 0);
        Vector3 windowCoordinates = camera.project(worldCoordinates);

        return new Vector2(windowCoordinates.x, windowCoordinates.y);
    }

    public Variable getActiveButton() {
        return activeButton;
    }

    public void setActiveButton(Variable activeButton) {
        this.activeButton = activeButton;
    }
}
