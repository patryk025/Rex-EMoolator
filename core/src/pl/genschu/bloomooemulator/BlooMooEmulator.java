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
import pl.genschu.bloomooemulator.interpreter.util.KeyboardsKeysMapper;
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

    private Variable activeButton = null;
    private boolean prevPressed = false;
    private ShapeRenderer shape;
    private Context lastMouseClickContext = null;

    private final Set<Integer> previouslyPressedKeys = new HashSet<>();

    private boolean debugGraphics = true;

    private String tooltipText = "";
    private final Vector2 tooltipPosition = new Vector2();
    private boolean showTooltip = false;
    private BitmapFont font;

    private boolean showDebugVariables = false;
    private String debugVariablesValues = "";

    private ShapeRenderer shapeRenderer;
    private Rectangle debugRect;

    public BlooMooEmulator(GameEntry gameEntry) {
        this.gameEntry = gameEntry;
    }
    
    @Override
    public void create () {
        batch = new SpriteBatch();
        font = new BitmapFont();
        shapeRenderer = new ShapeRenderer();

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
        KeyboardVariable keyboardVariable = context.getKeyboardVariable();

        batch.begin();
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        // Handle mouse events
        int x = Gdx.input.getX();
        int y = Gdx.input.getY();

        if(x >= 0 && y >= 0 && x < Gdx.graphics.getWidth() && y < Gdx.graphics.getHeight()) {
            boolean isPressed = Gdx.input.isButtonPressed(Input.Buttons.LEFT);
            boolean justPressed = !prevPressed && isPressed;
            boolean justReleased = prevPressed && !isPressed;

            if(justPressed) {
                lastMouseClickContext = game.getCurrentSceneContext();
            }
            if(justReleased && lastMouseClickContext != game.getCurrentSceneContext()) {
                justReleased = false;
            }

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

        handleKeyboard(keyboardVariable);

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

                if (imageVariable.isVisible() && imageVariable.isRenderedOnCanvas()) {
                    Rectangle rect = imageVariable.getRect();
                    Rectangle clippingRect = imageVariable.getClippingRect();

                    Map<String, Rectangle> alphaMasks = imageVariable.getAlphaMasks();
                    if (alphaMasks.isEmpty()) {
                        batch.setColor(1, 1, 1, imageVariable.getOpacity());

                        if (clippingRect != null) {
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
                            } catch (NullPointerException e) {
                                Gdx.app.error("Render", e.getMessage());
                            }

                            batch.flush();
                            Gdx.gl.glDisable(GL20.GL_SCISSOR_TEST);
                        } else {
                            try {
                                batch.draw(image.getImageTexture(), rect.getXLeft(), VIRTUAL_HEIGHT - rect.getYTop() - image.height, image.width, image.height);
                            } catch (NullPointerException ignored) {}
                        }
                    } else {
                        batch.end();

                        batch.begin();

                        Gdx.gl.glColorMask(false, false, false, true);
                        batch.setBlendFunction(GL20.GL_ONE, GL20.GL_ZERO);

                        for (Map.Entry<String, Rectangle> entry : alphaMasks.entrySet()) {
                            String maskName = entry.getKey();
                            Rectangle maskRect = entry.getValue();

                            Variable maskVar = imageVariable.getContext().getVariable(maskName);
                            if (!(maskVar instanceof ImageVariable)) continue;
                            ImageVariable mask = (ImageVariable) maskVar;
                            if (mask.getImage() == null) continue;

                            Texture maskTexture = mask.getImage().getImageTexture();
                            if (maskTexture == null) continue;

                            batch.draw(maskTexture, maskRect.getXLeft(), VIRTUAL_HEIGHT - maskRect.getYTop() - mask.getImage().height);

                            batch.flush();
                        }

                        Gdx.gl.glColorMask(true, true, true, true);

                        batch.setBlendFunction(GL20.GL_DST_ALPHA, GL20.GL_ONE_MINUS_DST_ALPHA);

                        if (clippingRect != null) {
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
                            } catch (NullPointerException e) {
                                Gdx.app.error("Render", e.getMessage());
                            }

                            batch.flush();
                            Gdx.gl.glDisable(GL20.GL_SCISSOR_TEST);
                        } else {
                            try {
                                batch.draw(image.getImageTexture(), rect.getXLeft(), VIRTUAL_HEIGHT - rect.getYTop() - image.height, image.width, image.height);
                            } catch (NullPointerException ignored) {}
                        }

                        batch.flush();
                        batch.end();

                        batch.begin();

                        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
                    }
                }
            } else if(variable instanceof AnimoVariable) {
                AnimoVariable animoVariable = (AnimoVariable) variable;
                if(animoVariable.isVisible() || animoVariable.isPlaying()) {
                    //Gdx.app.log(variable.getName(), ((AnimoVariable) variable).getRect().toString());
                    try {
                        Image image = animoVariable.getCurrentImage();
                        if(image == null) continue;
                        Event event = animoVariable.getCurrentEvent();
                        if (event == null) continue;

                        Rectangle rect = animoVariable.getRect();

                        if(animoVariable.isRenderedOnCanvas() && animoVariable.isVisible()) {
                            batch.setColor(1, 1, 1, animoVariable.getOpacity());
                            try {
                                batch.draw(image.getImageTexture(), rect.getXLeft(), VIRTUAL_HEIGHT - rect.getYTop() - image.height, image.width, image.height);
                            } catch (NullPointerException ignored) {}
                        }
                        animoVariable.updateAnimation(deltaTime);
                    } catch(NullPointerException ignored) {
                        Gdx.app.log("AnimoVariable", "Image not found in Animo "+animoVariable.getName());
                    }
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

            buttons.sort((o1, o2) -> {
                Variable image1 = null;
                if (o1 instanceof ButtonVariable) {
                    image1 = ((ButtonVariable) o1).getCurrentImage();
                } else if (o1 instanceof AnimoVariable) {
                    image1 = o1;
                }
                Variable image2 = null;
                if (o2 instanceof ButtonVariable) {
                    image2 = ((ButtonVariable) o2).getCurrentImage();
                } else if (o2 instanceof AnimoVariable) {
                    image2 = o2;
                }

                int priority1 = 0;
                int priority2 = 0;

                if (image1 != null) {
                    priority1 = image1.getAttribute("PRIORITY") != null ? Integer.parseInt(image1.getAttribute("PRIORITY").getValue().toString()) : 0;
                }
                if (image2 != null) {
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

        if (getDebugRect() != null) {
            shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(Color.RED); // Czerwony prostokąt
            Rectangle rect = getDebugRect();
            shapeRenderer.rect(rect.getXLeft(), VIRTUAL_HEIGHT - rect.getYBottom() - rect.getHeight(),
                    rect.getWidth(), rect.getHeight());
            shapeRenderer.end();
        }

        game.takeScreenshot();
        //dumpVariablesToLog();

        if (showDebugVariables) {
            generateDebugVariables();

        }
        renderTooltip();
    }

    public Rectangle getDebugRect() {
        return debugRect;
    }

    public void setDebugRect(Rectangle debugRect) {
        this.debugRect = debugRect;
    }

    public ShapeRenderer getShapeRenderer() {
        return shapeRenderer;
    }

    public void setShapeRenderer(ShapeRenderer shapeRenderer) {
        this.shapeRenderer = shapeRenderer;
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
        return null;
    }

    private Image getImage(Variable variable) {
        if(variable instanceof ImageVariable) {
            return ((ImageVariable) variable).getImage();
        }
        if(variable instanceof AnimoVariable) {
            return ((AnimoVariable) variable).getCurrentImage();
        }
        return null;
    }

    private void handleKeyboard(KeyboardVariable keyboardVariable) {
        if(keyboardVariable == null || !keyboardVariable.isEnabled()) {
            return;
        }

        Set<Integer> currentlyPressedKeys = new HashSet<>();

        for(int keyCode : KeyboardsKeysMapper.getKeySet()) {
            String keyName = KeyboardsKeysMapper.getMappedKey(keyCode);

            boolean isPressed = Gdx.input.isKeyPressed(keyCode);
            boolean wasPressed = previouslyPressedKeys.contains(keyCode);

            if(isPressed) {
                currentlyPressedKeys.add(keyCode);

                if(!wasPressed || keyboardVariable.isAutoRepeat()) {
                    keyboardVariable.emitSignal("ONKEYDOWN", keyName);
                }

                if(!wasPressed) {
                    keyboardVariable.emitSignal("ONCHAR", keyName);
                }
            }
            else if(wasPressed) {
                keyboardVariable.emitSignal("ONKEYUP", keyName);
            }
        }

        previouslyPressedKeys.clear();
        previouslyPressedKeys.addAll(currentlyPressedKeys);
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

        buttons.sort((o1, o2) -> {
            Variable image1 = null;
            if (o1 instanceof ButtonVariable) {
                image1 = ((ButtonVariable) o1).getCurrentImage();
            } else if (o1 instanceof AnimoVariable) {
                image1 = o1;
            }
            Variable image2 = null;
            if (o2 instanceof ButtonVariable) {
                image2 = ((ButtonVariable) o2).getCurrentImage();
            } else if (o2 instanceof AnimoVariable) {
                image2 = o2;
            }

            int priority1 = 0;
            int priority2 = 0;

            if (image1 != null) {
                priority1 = image1.getAttribute("PRIORITY") != null ? Integer.parseInt(image1.getAttribute("PRIORITY").getValue().toString()) : 0;
            }
            if (image2 != null) {
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
                    tooltipText += "\nIs button: " + isGraphicsButton(graphics);
                    tooltipText += "\nCollision monitoring: " + (graphics.getAttribute("MONITORCOLLISION") != null ? graphics.getAttribute("MONITORCOLLISION").getValue() : "FALSE");
                    tooltipText += rectText;
                }
                else if(graphics instanceof ImageVariable) {
                    tooltipText += "\nIs button: " + isGraphicsButton(graphics);
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

    private boolean isGraphicsButton(Variable variable) {
        // get rect (no offence)
        Rectangle rect = getRect(variable);

        // find Button with GFXSTANDARD
        for (Variable v : game.getCurrentSceneContext().getVariables().values()) {
            if(!v.getType().equals("BUTTON")) continue;

            if (v.getAttribute("GFXSTANDARD") != null && v.getAttribute("GFXSTANDARD").getValue().equals(variable.getName())) {
                return true;
            }

            if(rect == null) continue;

            // check rect
            ButtonVariable buttonVariable = (ButtonVariable) v;
            if (buttonVariable.getRect() != null && buttonVariable.getRect().intersects(rect)) {
                return true;
            }
        }

        if(variable instanceof AnimoVariable) {
            return ((AnimoVariable) variable).isAsButton();
        }

        return false;
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
                case "TIMER":
                    TimerVariable timerVariable = (TimerVariable) variable;
                    sb.append(variable.getName()).append(" (").append(variable.getType()).append(") = ").append(timerVariable.getCurrentTickCount()).append("/").append(timerVariable.getTicks()).append("(").append(timerVariable.getTimeFromLastTick()).append("/").append(timerVariable.getElapse()).append("ms)").append("\n");
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
        drawList.sort(comparator);
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

            sb.append(variable.getName());
            Attribute priority = variable.getAttribute("PRIORITY");
            if (priority != null) {
                sb.append(" (").append(priority.getValue()).append(")");
            }
            sb.append(", ");
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
