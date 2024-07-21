package pl.genschu.bloomooemulator;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
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
import pl.genschu.bloomooemulator.interpreter.variable.types.ImageVariable;
import pl.genschu.bloomooemulator.loader.ImageLoader;
import pl.genschu.bloomooemulator.objects.Image;
import pl.genschu.bloomooemulator.utils.CoordinatesHelper;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class BlooMooEmulator extends ApplicationAdapter {
    private static final float VIRTUAL_WIDTH = 800;
    private static final float VIRTUAL_HEIGHT = 600;

    SpriteBatch batch;
    BitmapFont font;
    Context context;
    OrthographicCamera camera;
    Viewport viewport;

    String gamePath;

    ImageVariable imageVariable;

    public BlooMooEmulator(String gamePath) {
        this.gamePath = gamePath;
    }

    @Override
    public void create () {
        batch = new SpriteBatch();
        context = new Context();

        imageVariable = new ImageVariable("TEST", null);
        imageVariable.setAttribute("FILENAME", new Attribute("FILENAME", "D:\\Program Files\\AidemMedia\\Reksio i Wehikuł Czasu\\dane\\game\\przygoda\\CS_ZANURZENIEKRETA\\cpl_bkg.img"));

        ImageLoader.loadImage(imageVariable);

        camera = new OrthographicCamera();
        viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
        viewport.apply();
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);

        Gdx.app.setLogLevel(Application.LOG_DEBUG);
    }

    @Override
    public void render () {
        ScreenUtils.clear(0, 0, 0, 1);
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();

        Image image = imageVariable.getImage();
        batch.draw(image.getImageTexture(), 0, 0, image.width, image.height);

        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void dispose () {
        batch.dispose();
        font.dispose();
    }
}
