package pl.genschu.bloomooemulator;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.Interpreter;
import pl.genschu.bloomooemulator.interpreter.antlr.AidemMediaLexer;
import org.antlr.v4.runtime.CharStreams;
import pl.genschu.bloomooemulator.interpreter.antlr.AidemMediaParser;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import pl.genschu.bloomooemulator.interpreter.ast.ASTBuilderVisitor;
import pl.genschu.bloomooemulator.interpreter.ast.Node;
import pl.genschu.bloomooemulator.interpreter.util.Point;
import pl.genschu.bloomooemulator.interpreter.variable.Attribute;
import pl.genschu.bloomooemulator.interpreter.variable.Signal;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.interpreter.variable.types.*;
import pl.genschu.bloomooemulator.loader.ImageLoader;
import pl.genschu.bloomooemulator.logic.GameEntry;
import pl.genschu.bloomooemulator.objects.*;
import pl.genschu.bloomooemulator.utils.CoordinatesHelper;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class BlooMooEmulator extends ApplicationAdapter {
    private static final float VIRTUAL_WIDTH = 800;
    private static final float VIRTUAL_HEIGHT = 600;

    SpriteBatch batch;
    Context context;
    OrthographicCamera camera;
    Viewport viewport;

    GameEntry gameEntry;
    Game game;

    private boolean debugButtons = false;

    private Texture cursorTexture;

    private ButtonVariable activeButton = null;
    private boolean prevPressed = false;
    private ShapeRenderer shape;

    public BlooMooEmulator(GameEntry gameEntry) {
        this.gameEntry = gameEntry;
    }
    
    @Override
    public void create () {
        batch = new SpriteBatch();

        this.game = new Game(this.gameEntry);

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

        batch.begin();
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        ImageVariable background = game.getCurrentSceneVariable().getBackground();
        if(background != null) {
            Image image = background.getImage();
            batch.setColor(1, 1, 1, background.getOpacity());
            batch.draw(image.getImageTexture(), image.offsetX, VIRTUAL_HEIGHT-image.offsetY-image.height, image.width, image.height);
        }

        for (Variable variable : drawList) {
            if(variable instanceof ImageVariable) {
                ImageVariable imageVariable = (ImageVariable) variable;
                Image image = imageVariable.getImage();
                if(imageVariable.isVisible()) {

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

        // update timers
        for (Variable variable : new ArrayList<>(context.getTimerVariables().values())) {
            TimerVariable timer = (TimerVariable) variable;
            timer.update();
        }

        // Handle mouse events
        int x = Gdx.input.getX();
        int y = Gdx.input.getY();
        boolean isPressed = Gdx.input.isButtonPressed(Input.Buttons.LEFT);
        boolean justPressed = !prevPressed && isPressed;
        boolean justReleased = prevPressed && !isPressed;
        
        //Gdx.app.log("MouseData", "Mouse coords: ("+x+", "+y+")");
        
        // correct coordinates according to window size
        Vector2 correctedVector = getCorrectedMouseCoords(x, y, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), (int) VIRTUAL_WIDTH, (int) VIRTUAL_HEIGHT);
        x = (int) correctedVector.x;
        y = (int) correctedVector.y;
        
        //Gdx.app.log("MouseData", "Corrected mouse coords: ("+x+", "+y+")");

        // TODO: implement
        //MouseVariable mouse = context.getMouseVariable();
        //mouse.update(x, y, isPressed);

        handleMouseInput(x, y, isPressed, justPressed, justReleased);

        prevPressed = isPressed;
        
        // batch.draw(cursorTexture, x - 25, VIRTUAL_HEIGHT - y - 25, 50, 50);
        
        batch.end();
    }

    public void handleMouseInput(int x, int y, boolean isPressed, boolean justPressed, boolean justReleased) {
        for (Variable variable : new ArrayList<>(context.getButtonsVariables().values())) {
            ButtonVariable button = (ButtonVariable) variable;
            if (button.getRect() != null && button.getRect().contains(x, y)) {
                if(debugButtons) {
                    // draw rect
                    drawRectangle(button.getRect(), Color.GREEN );
                }
                if (justPressed) {
                    if (activeButton == null) {
                        activeButton = button;
                        triggerSignal(button, "ONCLICKED");
                    }
                }
                if (button == activeButton) {
                    if (isPressed) {
                        // Dragging goes here
                    } else if (justReleased) {
                        triggerSignal(button, "ONRELEASED");
                        activeButton = null;
                    }
                }

                if (!button.isFocused() && !isPressed) {
                    button.setFocused(true);
                    Signal onFocusSignal = button.getSignal("ONFOCUSON");
                    if (onFocusSignal != null) {
                        onFocusSignal.execute(null);
                    }
                }
            }
            else {
                if(debugButtons && button.getRect() != null) {
                    // draw rect
                    drawRectangle(button.getRect(), Color.RED);
                }

                if (button.isFocused() && !isPressed) {
                    button.setFocused(false);
                    Signal onFocusLossSignal = button.getSignal("ONFOCUSOFF");
                    if (onFocusLossSignal != null) {
                        onFocusLossSignal.execute(null);
                    }
                }
            }
        }

        if (justReleased && activeButton != null) {
            triggerSignal(activeButton, "ONRELEASED");
            activeButton = null;
        }
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

    private void triggerSignal(ButtonVariable button, String signalName) {
        Signal signal = button.getSignal(signalName);
        if (signal != null) {
            signal.execute(null);
        }
    }

    private List<Variable> getGraphicsVariables() {
        List<Variable> drawList = new ArrayList<>(context.getGraphicsVariables().values());
        Comparator<Variable> comparator = (o1, o2) -> {
            Attribute priorityAttr1 = o1.getAttribute("PRIORITY");
            Attribute priorityAttr2 = o2.getAttribute("PRIORITY");
            int priority1 = priorityAttr1 != null ? Integer.parseInt(priorityAttr1.getValue().toString()) : 0;
            int priority2 = priorityAttr2 != null ? Integer.parseInt(priorityAttr2.getValue().toString()) : 0;
            return Integer.compare(priority1, priority2);
        };
        Collections.sort(drawList, comparator);
        return drawList;
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
}
