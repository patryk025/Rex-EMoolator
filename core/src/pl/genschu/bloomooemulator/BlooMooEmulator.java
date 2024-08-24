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

    private boolean debugGraphics = true;

    private String tooltipText = "";
    private Vector2 tooltipPosition = new Vector2();
    private boolean showTooltip = false;
    private BitmapFont font;

    public BlooMooEmulator(GameEntry gameEntry) {
        this.gameEntry = gameEntry;
    }
    
    @Override
    public void create () {
        batch = new SpriteBatch();
        font = new BitmapFont();

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

        MouseVariable mouseVariable = context.getMouseVariable();
        /*if(mouseVariable != null) { // to w przyszłości
            drawList.add(mouseVariable);
        }*/

        batch.begin();
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        // Handle mouse events
        int x = Gdx.input.getX();
        int y = Gdx.input.getY();

        if(x > 0 && y > 0 && x < Gdx.graphics.getWidth() && y < Gdx.graphics.getHeight()) {
            boolean isPressed = Gdx.input.isButtonPressed(Input.Buttons.LEFT);
            boolean justPressed = !prevPressed && isPressed;
            boolean justReleased = prevPressed && !isPressed;

            // correct coordinates according to window size
            Vector2 correctedVector = getCorrectedMouseCoords(x, y, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), (int) VIRTUAL_WIDTH, (int) VIRTUAL_HEIGHT);
            x = (int) correctedVector.x;
            y = (int) correctedVector.y;

            if (mouseVariable != null)
                mouseVariable.update(x, y);

            handleMouseInput(x, y, isPressed, justPressed, justReleased, mouseVariable, new ArrayList<>(drawList));

            prevPressed = isPressed;
        }

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

        // update timers
        for (Variable variable : new ArrayList<>(context.getTimerVariables().values())) {
            TimerVariable timer = (TimerVariable) variable;
            try {
                timer.update();
            } catch (BreakException ignored) {
                // simple break, nothing special here
            }
        }

        if(debugButtons) {
            List<Variable> buttons = new ArrayList<>(context.getButtonsVariables().values());

            Collections.sort(buttons, (o1, o2) -> {
                Variable image1 = ((ButtonVariable) o1).getCurrentImage();
                Variable image2 = ((ButtonVariable) o2).getCurrentImage();

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
                ButtonVariable button = (ButtonVariable) variable;

                if (!button.isVisible()) continue;

                if (!button.isEnabled()) continue;

                if (button.getRect() != null && button.getRect().contains(x, y)) {
                    drawRectangle(button.getRect(), Color.GREEN);
                }
                else if(button.getRect() != null) {
                    drawRectangle(button.getRect(), Color.RED);
                }
            }
        }

        batch.end();

        game.takeScreenshot();
        //dumpVariablesToLog();

        renderTooltip();
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

        Collections.sort(buttons, (o1, o2) -> {
            Variable image1 = ((ButtonVariable) o1).getCurrentImage();
            Variable image2 = ((ButtonVariable) o2).getCurrentImage();

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
            ButtonVariable button = (ButtonVariable) variable;

            if(!button.isVisible()) {
                Gdx.app.log("BlooMooEmulator", button.getName() + " is not visible, hiding images");
                button.hideImages();
            }

            if(!button.isEnabled()) continue;

            if (button.getRect() != null && button.getRect().contains(x, y)) {
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
                        button.setPressed(true);
                    } else if (justReleased) {
                        triggerSignal(button, "ONRELEASED");
                        button.setPressed(false);
                        triggerSignal(button, "GFXONCLICK");
                        activeButton = null;
                    }
                }

                if (!button.isFocused() && !isPressed) {
                    button.setFocused(true);
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
                    Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
                    Signal onFocusLossSignal = button.getSignal("ONFOCUSOFF");
                    if (onFocusLossSignal != null) {
                        onFocusLossSignal.execute(null);
                    }
                } else {
                    button.setFocused(false);
                }
            }
        }

        if (justReleased && activeButton != null) {
            triggerSignal(activeButton, "ONRELEASED");
            activeButton.setPressed(false);
            triggerSignal(activeButton, "GFXONCLICK");
            activeButton = null;
        }

        //Gdx.app.log("Mouse", "mouseVariable != null: " + (mouseVariable != null) + " isPressed: " + isPressed + " mouseVariable.isEnabled(): " + (mouseVariable != null && mouseVariable.isEnabled()));
        if(mouseVariable != null && isPressed && mouseVariable.isEnabled()) {
            mouseVariable.emitSignal("ONCLICK", "LEFT"); // right is not used at all

            Variable graphics = getGraphicsAt(x, y, drawList);
            if(graphics != null) {
                graphics.emitSignal("ONCLICK", "LEFT");
            }
        }

        if(debugGraphics) {
            Variable hoveredVariable = getGraphicsAt(x, y, drawList);
            if (hoveredVariable != null) {
                tooltipText = hoveredVariable.getName() + " (" + hoveredVariable.getType() + ")\nPriority: " + hoveredVariable.getAttribute("PRIORITY").getValue();
                if(hoveredVariable instanceof AnimoVariable) {
                    tooltipText += "\nCurrent event: " + ((AnimoVariable) hoveredVariable).getCurrentEvent().getName();
                    tooltipText += "\nIs playing: " + ((AnimoVariable) hoveredVariable).isPlaying();
                }
                else if(hoveredVariable instanceof SequenceVariable) {
                    tooltipText += "\nCurrent event: " + ((SequenceVariable) hoveredVariable).getCurrentEventName();
                    tooltipText += "\nCurrent animo event: " + ((SequenceVariable) hoveredVariable).getCurrentAnimo().getCurrentEvent().getName();
                    tooltipText += "\nIs playing: " + ((SequenceVariable) hoveredVariable).getCurrentAnimo().isPlaying();
                }
                tooltipPosition.set(x + 20, VIRTUAL_HEIGHT - y - 20);
                showTooltip = true;
            } else {
                showTooltip = false;
            }
        }
    }

    private void renderTooltip() {
        if (showTooltip && !tooltipText.isEmpty()) {
            GlyphLayout layout = new GlyphLayout(font, tooltipText);
            float width = layout.width + 10;
            float height = layout.height + 10;

            float x = tooltipPosition.x;
            float y = tooltipPosition.y;

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

    private Variable getGraphicsAt(int x, int y, List<Variable> drawList) {
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
                    return variable;
                }
            }
        } catch (NullPointerException ignored) {}

        return null;
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
        //Gdx.app.log("Draw list before sort", getDrawListAsString(drawList));
        Comparator<Variable> comparator = (o1, o2) -> {
            Attribute priorityAttr1 = o1.getAttribute("PRIORITY");
            Attribute priorityAttr2 = o2.getAttribute("PRIORITY");
            int priority1 = priorityAttr1 != null ? Integer.parseInt(priorityAttr1.getValue().toString()) : 0;
            int priority2 = priorityAttr2 != null ? Integer.parseInt(priorityAttr2.getValue().toString()) : 0;
            return Integer.compare(priority1, priority2);
        };
        Collections.sort(drawList, comparator);
        Gdx.app.log("Draw list after sort", getDrawListAsString(drawList));
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
}
