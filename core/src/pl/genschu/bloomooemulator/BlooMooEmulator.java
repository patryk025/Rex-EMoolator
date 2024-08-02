package pl.genschu.bloomooemulator;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
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
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.interpreter.variable.types.AnimoVariable;
import pl.genschu.bloomooemulator.interpreter.variable.types.ImageVariable;
import pl.genschu.bloomooemulator.interpreter.variable.types.SequenceVariable;
import pl.genschu.bloomooemulator.loader.ImageLoader;
import pl.genschu.bloomooemulator.logic.GameEntry;
import pl.genschu.bloomooemulator.objects.Event;
import pl.genschu.bloomooemulator.objects.FrameData;
import pl.genschu.bloomooemulator.objects.Game;
import pl.genschu.bloomooemulator.objects.Image;
import pl.genschu.bloomooemulator.utils.CoordinatesHelper;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

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

        batch.begin();
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        ImageVariable background = game.getCurrentSceneVariable().getBackground();
        if(background != null) {
            Image image = background.getImage();
            batch.draw(image.getImageTexture(), image.offsetX, VIRTUAL_HEIGHT-image.offsetY-image.height, image.width, image.height);
        }

        for(String key : context.getGraphicsVariables().keySet()) {
            Variable variable = context.getGraphicsVariables().get(key);
            if(variable instanceof ImageVariable) {
                Image image = ((ImageVariable) variable).getImage();
                if(
                        variable.getAttribute("VISIBLE").getValue().toString().equals("TRUE")
                    &&  variable.getAttribute("TOCANVAS").getValue().toString().equals("TRUE")
                ) {
                    try {
                        batch.setColor(1, 1, 1, ((int) variable.getAttribute("OPACITY").getValue()) / 255f);
                    } catch(NullPointerException e) {
                        batch.setColor(1, 1, 1, 1);
                    }
                    try {
                        batch.draw(image.getImageTexture(), image.offsetX, VIRTUAL_HEIGHT-image.offsetY-image.height, image.width, image.height);
                    } catch(NullPointerException ignored) {
                        // skip for now
                    }
                }
            }
            // TODO: system animacji
            if(variable instanceof AnimoVariable) {
                AnimoVariable animoVariable = (AnimoVariable) variable;
                if(
                    variable.getAttribute("VISIBLE").getValue().toString().equals("TRUE")
                &&  variable.getAttribute("TOCANVAS").getValue().toString().equals("TRUE")
                ) {
                    try {
                        Image image = animoVariable.getCurrentImage();
                        Event event = animoVariable.getCurrentEvent();
                        if (event == null) continue;
                        FrameData frameData = event.getFrameData().get(animoVariable.getCurrentFrameNumber());
                        try {
                            batch.draw(image.getImageTexture(), animoVariable.getPosX() + frameData.getOffsetX() + image.offsetX, VIRTUAL_HEIGHT - animoVariable.getPosY() - frameData.getOffsetY() - image.offsetY - image.height, image.width, image.height);
                        } catch (NullPointerException ignored) {
                        }
                        animoVariable.updateAnimation(deltaTime);
                    } catch(NullPointerException ignored) {
                        Gdx.app.log("AnimoVariable", "Image not found");
                    }
                }
            }
            if(variable instanceof SequenceVariable) {
                SequenceVariable sequenceVariable = (SequenceVariable) variable;

                try {
                    Gdx.app.log("SequenceVariable", "Is ANIMO available? " + (sequenceVariable.getCurrentAnimo() != null));
                    Gdx.app.log("SequenceVariable", "Frame number: " + (sequenceVariable.getCurrentAnimo().getCurrentFrameNumber()));
                    Image image = sequenceVariable.getCurrentAnimo().getCurrentImage();
                    Event event = sequenceVariable.getCurrentAnimo().getCurrentEvent();
                    if (event == null) continue;
                    FrameData frameData = event.getFrameData().get(sequenceVariable.getCurrentAnimo().getCurrentFrameNumber());
                    try {
                        batch.draw(image.getImageTexture(), frameData.getOffsetX() + image.offsetX, VIRTUAL_HEIGHT - frameData.getOffsetY() - image.offsetY - image.height, image.width, image.height);
                    } catch (NullPointerException ignored) {
                    }
                    if(sequenceVariable.isPlaying()) {
                        sequenceVariable.updateAnimation(deltaTime);
                    }
                } catch(NullPointerException e) {
                    Gdx.app.log("SequenceVariable", "Image not found");
                }
            }
        }

        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void dispose () {
        batch.dispose();
    }
}
