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
    Interpreter interpreter;
    Point point;
    OrthographicCamera camera;
    Viewport viewport;

    @Override
    public void create () {
        batch = new SpriteBatch();
        context = new Context();

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Roboto-Regular.ttf"));
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();
        parameter.size = 72;
        font = generator.generateFont(parameter);
        generator.dispose();

        String testExpression = "{@RETURN([7+7@7+7*7-7]);}";
        AidemMediaLexer lexer = new AidemMediaLexer(CharStreams.fromString(testExpression));
        AidemMediaParser parser = new AidemMediaParser(new CommonTokenStream(lexer));
        ParseTree tree = parser.script();

        ASTBuilderVisitor astBuilder = new ASTBuilderVisitor(context);
        Node astRoot = astBuilder.visit(tree);
        interpreter = new Interpreter(astRoot, context);
        point = new Point(300, 400);
        Gdx.app.debug("POINT", point.toString());

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
        interpreter.interpret();
        font.draw(batch, "7+7/7+7*7-7=" + interpreter.getReturnValue(), point.getX(), point.getY());
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        CoordinatesHelper.updateScale(width, height);
    }

    @Override
    public void dispose () {
        batch.dispose();
        font.dispose();
    }
}
