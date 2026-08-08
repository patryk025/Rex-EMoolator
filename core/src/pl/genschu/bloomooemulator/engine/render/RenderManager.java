package pl.genschu.bloomooemulator.engine.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.Viewport;
import pl.genschu.bloomooemulator.engine.Game;
import pl.genschu.bloomooemulator.engine.config.EngineConfig;
import pl.genschu.bloomooemulator.engine.context.EngineVariable;
import pl.genschu.bloomooemulator.engine.context.GameContext;
import pl.genschu.bloomooemulator.interpreter.variable.*;
import pl.genschu.bloomooemulator.objects.Image;
import pl.genschu.bloomooemulator.geometry.shapes.Box2D;

import java.util.*;

public class RenderManager implements Disposable {
    private static final int VIRTUAL_WIDTH = 800;
    private static final float VIRTUAL_HEIGHT = 600;

    private final SpriteBatch batch;
    private final OrthographicCamera camera;
    private final Viewport viewport;
    private final Game game;
    private final EngineConfig config;

    /**
     * Persistent 800x600 logical canvas. Keeping the fixed-size surface here
     * makes the displayed frame and CANVAS_OBSERVER.SAVE use the same complete
     * image. RGBA8888 is intentional: renderers using destination alpha need an
     * alpha channel; SAVE converts its on-demand snapshot to legacy RGB565.
     */
    private final FrameBuffer canvasBuffer;
    private final TextureRegion canvasRegion;

    private final GraphicsRenderer graphicsRenderer;
    private final TextRenderer textRenderer;
    private final MaskRenderer maskRenderer;
    private final AlphaMaskRenderer alphaMaskRenderer;

    public RenderManager(SpriteBatch batch, OrthographicCamera camera, Viewport viewport,
                         Game game, EngineConfig config) {
        this.batch = batch;
        this.camera = camera;
        this.viewport = viewport;
        this.game = game;
        this.config = config;

        this.canvasBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, VIRTUAL_WIDTH, (int) VIRTUAL_HEIGHT, false);
        this.canvasRegion = new TextureRegion(canvasBuffer.getColorBufferTexture());
        // Frame-buffer textures use OpenGL's bottom-left origin. Flip only the
        // presentation view; readback retains its native orientation and SAVE
        // converts it exactly once.
        this.canvasRegion.flip(false, true);
        canvasBuffer.getColorBufferTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        this.graphicsRenderer = new GraphicsRenderer(batch, camera);
        this.textRenderer = new TextRenderer(batch);
        this.maskRenderer = new MaskRenderer(batch);
        this.alphaMaskRenderer = new AlphaMaskRenderer(batch);

        clearLogicalCanvas();
    }

    public void render(float deltaTime) {
        renderLogicalCanvas();
        presentLogicalCanvas();
    }

    private void renderLogicalCanvas() {
        canvasBuffer.begin();
        try {
            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            camera.update();
            batch.setProjectionMatrix(camera.combined);

            GameContext context = game.getCurrentSceneContext();
            batch.begin();
            batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

            // Render background
            renderBackground();

            // Render graphics pasted onto background by CANVAS_OBSERVER.PASTE
            renderPastedGraphics();

            // Obtain draw list and sort by priority
            List<Variable> drawList = getDrawableVariables(context);
            sortByPriority(drawList);

            renderDrawList(drawList, context);
        } finally {
            if (batch.isDrawing()) {
                batch.end();
            }
            canvasBuffer.end();
            restorePresentationViewport();
        }
    }

    private void presentLogicalCanvas() {
        restorePresentationViewport();

        batch.begin();
        batch.setColor(1, 1, 1, 1);
        // Destination alpha is working state for AlphaMaskRenderer. The
        // original DirectDraw canvas is opaque at presentation time, so using
        // SRC_ALPHA here would apply the mask a second time.
        batch.disableBlending();
        batch.draw(canvasRegion, 0, 0, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        batch.enableBlending();
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        batch.end();
    }

    private void clearLogicalCanvas() {
        canvasBuffer.begin();
        try {
            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        } finally {
            canvasBuffer.end();
            restorePresentationViewport();
        }
    }

    private void restorePresentationViewport() {
        viewport.apply();
        camera.update();
        batch.setProjectionMatrix(camera.combined);
    }

    /**
     * Reads the current logical canvas on demand. The returned pixmap is an
     * independent, caller-owned RGB565 snapshot in OpenGL (bottom-up) order.
     * Reading RGBA/UNSIGNED_BYTE first uses the portable GLES2 readback path;
     * the CPU-side conversion retains the 16-bpp contract exposed to games.
     */
    public Pixmap captureLogicalCanvas() {
        Pixmap readback = new Pixmap(VIRTUAL_WIDTH, (int) VIRTUAL_HEIGHT, Pixmap.Format.RGBA8888);
        Pixmap snapshot = null;
        boolean success = false;
        boolean bufferBound = false;
        try {
            canvasBuffer.begin();
            bufferBound = true;
            readback.getPixels().position(0);
            Gdx.gl.glReadPixels(
                    0,
                    0,
                    VIRTUAL_WIDTH,
                    (int) VIRTUAL_HEIGHT,
                    GL20.GL_RGBA,
                    GL20.GL_UNSIGNED_BYTE,
                    readback.getPixels());

            snapshot = new Pixmap(VIRTUAL_WIDTH, (int) VIRTUAL_HEIGHT, Pixmap.Format.RGB565);
            snapshot.setBlending(Pixmap.Blending.None);
            snapshot.drawPixmap(readback, 0, 0);
            success = true;
            return snapshot;
        } finally {
            if (bufferBound) {
                try {
                    canvasBuffer.end();
                } finally {
                    restorePresentationViewport();
                }
            }
            readback.dispose();
            if (!success && snapshot != null) {
                snapshot.dispose();
            }
        }
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

    private void renderPastedGraphics() {
        for (PastedGraphic p : game.getPastedGraphics()) {
            if (p.texture() == null) continue;
            batch.setColor(1, 1, 1, p.opacity());
            batch.draw(p.texture(),
                    p.x(),
                    VIRTUAL_HEIGHT - p.y() - p.height(),
                    p.width(),
                    p.height());
        }
    }

    private void renderDrawList(List<Variable> drawList, GameContext context) {
        for (Variable variable : drawList) {
            if (variable instanceof ImageVariable img) {
                renderImage(img);
            } else if (variable instanceof AnimoVariable animo) {
                renderAnimo(animo);
            } else if (variable instanceof KolorowankaVariable klr) {
                renderKolorowanka(klr);
            } else if (variable instanceof TextVariable text) {
                textRenderer.renderText(text, context);
            }
        }
    }

    private void renderKolorowanka(KolorowankaVariable klr) {
        if (!klr.isVisible()) {
            return;
        }
        var texture = klr.getTexture();
        if (texture == null) {
            return;
        }
        batch.setColor(1, 1, 1, 1);
        batch.draw(texture,
                klr.getPosX(),
                VIRTUAL_HEIGHT - klr.getPosY() - klr.getHeight(),
                klr.getWidth(),
                klr.getHeight());
    }

    private void renderImage(ImageVariable imageVariable) {
        Image image = imageVariable.getImage();
        if (image == null || image.getImageTexture() == null) {
            return;
        }

        Box2D rect = imageVariable.getRect();
        Box2D clippingRect = imageVariable.getClippingRect();
        ImageVariable.AlphaMaskBinding alphaMask = imageVariable.getAlphaMask();

        if (alphaMask == null) {
            batch.setColor(1, 1, 1, imageVariable.getOpacity());

            if (clippingRect != null) {
                maskRenderer.renderWithClipping(image, rect, clippingRect);
            } else {
                graphicsRenderer.renderImage(imageVariable);
            }
        } else {
            alphaMaskRenderer.renderWithAlphaMask(imageVariable, rect, clippingRect, alphaMask);
        }
    }

    private void renderAnimo(AnimoVariable animoVariable) {
        graphicsRenderer.renderAnimo(animoVariable);
    }

    private List<Variable> getDrawableVariables(GameContext context) {
        Set<Variable> variables = new LinkedHashSet<>();
        for (EngineVariable variable : context.getGraphicsVariables().values()) {
            if (variable instanceof Variable drawable) {
                variables.add(drawable);
            }
        }
        for (EngineVariable variable : context.getTextVariables().values()) {
            if (variable instanceof Variable drawable) {
                variables.add(drawable);
            }
        }
        return new ArrayList<>(variables);
    }

    private void sortByPriority(List<Variable> drawList) {
        drawList.sort((v1, v2) -> {
            int priority1 = getPriority(v1);
            int priority2 = getPriority(v2);
            int priorityComparison = Integer.compare(priority1, priority2);
            if (priorityComparison != 0) {
                return priorityComparison;
            }
            return Long.compare(getRenderOrder(v1), getRenderOrder(v2));
        });
    }

    private int getPriority(Variable variable) {
        if (variable instanceof ImageVariable img) {
            return img.state().priority;
        } else if (variable instanceof AnimoVariable animo) {
            return animo.getPriority();
        } else if (variable instanceof KolorowankaVariable klr) {
            return klr.getPriority();
        } else if (variable instanceof TextVariable text) {
            return text.getPriority();
        }
        return 0;
    }

    private long getRenderOrder(Variable variable) {
        if (variable instanceof ImageVariable img) {
            return img.getRenderOrder();
        } else if (variable instanceof AnimoVariable animo) {
            return animo.getRenderOrder();
        } else if (variable instanceof KolorowankaVariable klr) {
            return klr.getRenderOrder();
        } else if (variable instanceof TextVariable text) {
            return text.getRenderOrder();
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
        canvasBuffer.dispose();
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
