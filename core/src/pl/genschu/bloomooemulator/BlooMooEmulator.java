package pl.genschu.bloomooemulator;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
import com.badlogic.gdx.graphics.OrthographicCamera;
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



    public BlooMooEmulator(GameEntry gameEntry) {
        this.gameEntry = gameEntry;
    }

    @Override
    public void create () {
        batch = new SpriteBatch();

        this.game = new Game(this.gameEntry);

        context = this.game.getCurrentSceneContext();

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

        List<Variable> drawList = getGraphicsVariables();

        batch.begin();
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        ImageVariable background = game.getCurrentSceneVariable().getBackground();
        if(background != null) {
            Image image = background.getImage();
            batch.draw(image.getImageTexture(), image.offsetX, VIRTUAL_HEIGHT-image.offsetY-image.height, image.width, image.height);
        }

        for (Variable variable : drawList) {
            if(variable instanceof ImageVariable) {
                Image image = ((ImageVariable) variable).getImage();
                if(variable.getAttribute("VISIBLE").getValue().toString().equals("TRUE")
                        &&  variable.getAttribute("TOCANVAS").getValue().toString().equals("TRUE")) {
                    try {
                        batch.setColor(1, 1, 1, ((int) variable.getAttribute("OPACITY").getValue()) / 255f);
                    } catch(NullPointerException e) {
                        batch.setColor(1, 1, 1, 1);
                    }
                    Rectangle rect = ((ImageVariable) variable).getRect();
                    try {
                        batch.draw(image.getImageTexture(), rect.getXLeft(), VIRTUAL_HEIGHT-rect.getYTop()-image.height, image.width, image.height);
                    } catch(NullPointerException ignored) {
                        // skip for now
                    }
                }
            } else if(variable instanceof AnimoVariable) {
                AnimoVariable animoVariable = (AnimoVariable) variable;
                if(variable.getAttribute("VISIBLE").getValue().toString().equals("TRUE")
                        &&  variable.getAttribute("TOCANVAS").getValue().toString().equals("TRUE")) {
                    try {
                        Image image = animoVariable.getCurrentImage();
                        Event event = animoVariable.getCurrentEvent();
                        if (event == null) continue;
                        Rectangle rect = animoVariable.getRect();
                        try {
                            batch.draw(image.getImageTexture(), rect.getXLeft(), VIRTUAL_HEIGHT - rect.getYTop() - image.height, image.width, image.height);
                        } catch (NullPointerException ignored) {}
                        animoVariable.updateAnimation(deltaTime);
                    } catch(NullPointerException ignored) {
                        Gdx.app.log("AnimoVariable", "Image not found");
                    }
                }
            } else if(variable instanceof SequenceVariable) {
                SequenceVariable sequenceVariable = (SequenceVariable) variable;
                try {
                    Image image = sequenceVariable.getCurrentAnimo().getCurrentImage();
                    Event event = sequenceVariable.getCurrentAnimo().getCurrentEvent();
                    if (event == null) continue;
                    Rectangle rect = sequenceVariable.getCurrentAnimo().getRect();
                    try {
                        batch.draw(image.getImageTexture(), rect.getXLeft(), VIRTUAL_HEIGHT - rect.getYTop() - image.height, image.width, image.height);
                    } catch (NullPointerException ignored) {}
                    if(sequenceVariable.isPlaying()) {
                        sequenceVariable.updateAnimation(deltaTime);
                    }
                } catch(NullPointerException e) {
                    Gdx.app.log("SequenceVariable", "Image not found");
                }
            }
        }

        batch.end();

        // Handle mouse events
        int x = Gdx.input.getX();
        int y = Gdx.input.getY();
        boolean isPressed = Gdx.input.isButtonPressed(Input.Buttons.LEFT);

        // TODO: implement
        //MouseVariable mouse = context.getMouseVariable();
        //mouse.update(x, y, isPressed);

        for (Variable variable : new ArrayList<>(context.getButtonsVariables().values())) {
            ButtonVariable button = (ButtonVariable) variable;
            //Gdx.app.log("ButtonDebug", button.getName());
            //Gdx.app.log("ButtonDebug", button.getRect() != null ? button.getRect().toString() : null);
            //Gdx.app.log("ButtonDebug", "x: " + x + " y: " + y);
            //Gdx.app.log("ButtonDebug", "contains: " + (button.getRect() != null && button.getRect().contains(x, y)) + " pressed: " + button.isPressed());
            if (button.getRect() != null) {
                if (button.getRect().contains(x, y)) {
                    if (isPressed && !button.isPressed()) {
                        Signal onClickSignal = button.getSignal("ONCLICKED");
                        if (onClickSignal != null) {
                            onClickSignal.execute(null);
                        }
                        button.setPressed(true);
                    } else if (!isPressed && button.isPressed()) {
                        button.setPressed(false);
                        Signal onReleasedSignal = button.getSignal("ONRELEASED");
                        if (onReleasedSignal != null) {
                            onReleasedSignal.execute(null);
                        }
                    }
                    if (!button.isFocused()) {
                        button.setFocused(true);
                        Signal onFocusSignal = button.getSignal("ONFOCUSON");
                        if (onFocusSignal != null) {
                            onFocusSignal.execute(null);
                        }
                    }
                } else {
                    if (button.isPressed() && !isPressed) {
                        button.setPressed(false);
                        Signal onReleasedSignal = button.getSignal("ONRELEASED");
                        if (onReleasedSignal != null) {
                            onReleasedSignal.execute(null);
                        }
                    }
                    if (button.isFocused()) {
                        button.setFocused(false);
                        Signal onFocusLossSignal = button.getSignal("ONFOCUSOFF");
                        if (onFocusLossSignal != null) {
                            onFocusLossSignal.execute(null);
                        }
                    }
                }
            }
        }
    }

    private List<Variable> getGraphicsVariables() {
        List<Variable> drawList = new ArrayList<>(context.getGraphicsVariables().values());
        Comparator<Variable> comparator = new Comparator<Variable>() {
            @Override
            public int compare(Variable o1, Variable o2) {
                Attribute priorityAttr1 = o1.getAttribute("PRIORITY");
                Attribute priorityAttr2 = o2.getAttribute("PRIORITY");
                int priority1 = priorityAttr1 != null ? Integer.parseInt(priorityAttr1.getValue().toString()) : 0;
                int priority2 = priorityAttr2 != null ? Integer.parseInt(priorityAttr2.getValue().toString()) : 0;
                return Integer.compare(priority1, priority2);
            }
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
}
