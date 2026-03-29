package pl.genschu.bloomooemulator.engine.render;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import pl.genschu.bloomooemulator.engine.Game;
import pl.genschu.bloomooemulator.engine.config.EngineConfig;
import pl.genschu.bloomooemulator.engine.context.EngineVariable;
import pl.genschu.bloomooemulator.engine.context.GameContext;
import pl.genschu.bloomooemulator.interpreter.variable.*;
import pl.genschu.bloomooemulator.objects.Image;
import pl.genschu.bloomooemulator.geometry.shapes.Box2D;

import java.util.*;

public class RenderManager implements Disposable {
    private static final float VIRTUAL_HEIGHT = 600;

    private final SpriteBatch batch;
    private final OrthographicCamera camera;
    private final Game game;
    private final EngineConfig config;

    private final GraphicsRenderer graphicsRenderer;
    private final TextRenderer textRenderer;
    private final MaskRenderer maskRenderer;
    private final AlphaMaskRenderer alphaMaskRenderer;

    // TODO: move
    public RenderManager(SpriteBatch batch, OrthographicCamera camera, Game game, EngineConfig config) {
        this.batch = batch;
        this.camera = camera;
        this.game = game;
        this.config = config;

        this.graphicsRenderer = new GraphicsRenderer(batch, camera);
        this.textRenderer = new TextRenderer(batch);
        this.maskRenderer = new MaskRenderer(batch);
        this.alphaMaskRenderer = new AlphaMaskRenderer(batch);
    }

    public void render(float deltaTime) {
        GameContext context = game.getCurrentSceneContext();

        // start rendering
        batch.begin();
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        // Render background
        renderBackground();

        // Obtain draw list and sort by priority
        List<Variable> drawList = getGraphicsVariables();
        sortByPriority(drawList);

        // TODO: technically text is also graphics so it should be rendered along with graphics
        // Render graphics
        renderGraphics(drawList);

        // Render text
        renderTexts(context);

        batch.end();
    }

    private void renderBackground() {
        ImageVariable background = game.getCurrentBackgroundImage();
        if (background != null && background.getImage() != null) {
            Image image = background.getImage();
            if (image.getImageTexture() != null) {
                batch.setColor(1, 1, 1, background.getOpacity());
                batch.draw(image.getImageTexture(),
                        image.offsetX,
                        VIRTUAL_HEIGHT - image.offsetY - image.height,
                        image.width,
                        image.height);
            }
        }
    }

    private void renderGraphics(List<Variable> drawList) {
        for (Variable variable : drawList) {
            if (variable instanceof ImageVariable img) {
                renderImage(img);
            } else if (variable instanceof AnimoVariable animo) {
                renderAnimo(animo);
            }
        }
    }

    private void renderImage(ImageVariable imageVariable) {
        Image image = imageVariable.getImage();
        if (image == null || image.getImageTexture() == null) {
            return;
        }

        Box2D rect = imageVariable.getRect();
        Box2D clippingRect = imageVariable.getClippingRect();
        Map<String, Box2D> alphaMasks = imageVariable.getAlphaMasks();

        if (alphaMasks.isEmpty()) {
            batch.setColor(1, 1, 1, imageVariable.getOpacity());

            if (clippingRect != null) {
                maskRenderer.renderWithClipping(image, rect, clippingRect);
            } else {
                graphicsRenderer.renderImage(imageVariable);
            }
        } else {
            alphaMaskRenderer.renderWithAlphaMask(imageVariable, rect, clippingRect, alphaMasks);
        }
    }

    private void renderAnimo(AnimoVariable animoVariable) {
        graphicsRenderer.renderAnimo(animoVariable);
    }

    private void renderTexts(GameContext context) {
        for (EngineVariable variable : new ArrayList<>(context.getTextVariables().values())) {
            if (variable instanceof TextVariable textVar) {
                textRenderer.renderText(textVar);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private List<Variable> getGraphicsVariables() {
        GameContext context = game.getCurrentSceneContext();
        return new ArrayList<>((Collection<? extends Variable>) context.getGraphicsVariables().values());
    }

    private void sortByPriority(List<Variable> drawList) {
        drawList.sort((v1, v2) -> {
            int priority1 = getPriority(v1);
            int priority2 = getPriority(v2);
            return Integer.compare(priority1, priority2);
        });
    }

    private int getPriority(Variable variable) {
        if (variable instanceof ImageVariable img) {
            return img.state().priority;
        } else if (variable instanceof AnimoVariable animo) {
            return animo.getPriority();
        }
        return 0;
    }

    @Override
    public void dispose() {
        // Disposing resources
        graphicsRenderer.dispose();
        textRenderer.dispose();
        maskRenderer.dispose();
        alphaMaskRenderer.dispose();
    }

    // Helper method for debugging
    private String getDrawListAsString(List<Variable> drawList) {
        StringBuilder sb = new StringBuilder();
        for (Variable variable : drawList) {
            boolean visible = false;
            if (variable instanceof ImageVariable img) {
                visible = img.isVisible();
            } else if (variable instanceof AnimoVariable animo) {
                visible = animo.isVisible();
            }

            if (!visible) continue;

            sb.append(variable.name());
            sb.append(" (").append(getPriority(variable)).append(")");
            sb.append(", ");
        }

        if (sb.length() > 2) {
            sb.setLength(sb.length() - 2);
        }
        return sb.toString();
    }
}
