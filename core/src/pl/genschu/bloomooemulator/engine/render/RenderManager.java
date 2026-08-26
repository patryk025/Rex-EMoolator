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
import pl.genschu.bloomooemulator.geometry.coordinates.CanvasCoordinateSystem;
import pl.genschu.bloomooemulator.geometry.coordinates.CanvasRect;
import pl.genschu.bloomooemulator.geometry.coordinates.OpenGlRect;

import java.util.*;

public class RenderManager implements Disposable {
    private final SpriteBatch batch;
    private final OrthographicCamera camera;
    private final Viewport viewport;
    private final Game game;
    private final EngineConfig config;

    /**
     * Persistent fixed-size logical canvas. Keeping the surface here
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
    private final RenderStats lastRenderStats = new RenderStats();

    public RenderManager(SpriteBatch batch, OrthographicCamera camera, Viewport viewport,
                         Game game, EngineConfig config) {
        this.batch = batch;
        this.camera = camera;
        this.viewport = viewport;
        this.game = game;
        this.config = config;

        this.canvasBuffer = new FrameBuffer(
                Pixmap.Format.RGBA8888,
                CanvasCoordinateSystem.WIDTH,
                CanvasCoordinateSystem.HEIGHT,
                false);
        this.canvasRegion = new TextureRegion(canvasBuffer.getColorBufferTexture());
        // Frame-buffer textures use OpenGL's bottom-left origin. Flip only the
        // presentation view; captureLogicalCanvas normalizes readback orientation
        // separately at the OpenGL-to-canvas boundary.
        this.canvasRegion.flip(false, true);
        canvasBuffer.getColorBufferTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        this.graphicsRenderer = new GraphicsRenderer(batch);
        this.textRenderer = new TextRenderer(batch);
        this.maskRenderer = new MaskRenderer(batch);
        this.alphaMaskRenderer = new AlphaMaskRenderer(batch);

        clearLogicalCanvas();
    }

    public void render(float deltaTime) {
        lastRenderStats.reset();
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
            lastRenderStats.drawableObjects = drawList.size();
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
        batch.draw(
                canvasRegion,
                0,
                0,
                CanvasCoordinateSystem.WIDTH,
                CanvasCoordinateSystem.HEIGHT);
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
     * independent, caller-owned RGB565 snapshot in canvas (top-down) order.
     * Reading RGBA/UNSIGNED_BYTE first uses the portable GLES2 readback path;
     * the CPU-side conversion retains the 16-bpp contract exposed to games and
     * normalizes OpenGL's bottom-up row order at this boundary.
     */
    public Pixmap captureLogicalCanvas() {
        Pixmap readback = new Pixmap(
                CanvasCoordinateSystem.WIDTH,
                CanvasCoordinateSystem.HEIGHT,
                Pixmap.Format.RGBA8888);
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
                    CanvasCoordinateSystem.WIDTH,
                    CanvasCoordinateSystem.HEIGHT,
                    GL20.GL_RGBA,
                    GL20.GL_UNSIGNED_BYTE,
                    readback.getPixels());

            snapshot = new Pixmap(
                    CanvasCoordinateSystem.WIDTH,
                    CanvasCoordinateSystem.HEIGHT,
                    Pixmap.Format.RGB565);
            copyOpenGlReadbackToCanvas(readback, snapshot);
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

    /** Copies bottom-up OpenGL readback rows into a top-down canvas pixmap. */
    static void copyOpenGlReadbackToCanvas(Pixmap readback, Pixmap canvas) {
        if (readback.getWidth() != canvas.getWidth()
                || readback.getHeight() != canvas.getHeight()) {
            throw new IllegalArgumentException("Readback and canvas dimensions must match");
        }

        int width = readback.getWidth();
        int height = readback.getHeight();
        canvas.setBlending(Pixmap.Blending.None);
        for (int canvasY = 0; canvasY < height; canvasY++) {
            int openGlY = height - canvasY - 1;
            canvas.drawPixmap(readback, 0, canvasY, 0, openGlY, width, 1);
        }
    }

    private void renderBackground() {
        ImageVariable background = game.getCurrentBackgroundImage();
        if (background != null && background.getImage() != null) {
            Image image = background.getImage();
            if (image.getImageTexture() != null) {
                OpenGlRect destination = backgroundDestination(
                        image.offsetX, image.offsetY, image.width, image.height,
                        game.getBackgroundPositionX(), game.getBackgroundPositionY());
                batch.setColor(1, 1, 1, background.getOpacity());
                batch.draw(image.getImageTexture(),
                        GraphicsRenderer.asFloat(destination.x()),
                        GraphicsRenderer.asFloat(destination.y()),
                        GraphicsRenderer.asFloat(destination.width()),
                        GraphicsRenderer.asFloat(destination.height()));
                lastRenderStats.visibleSpriteObjects++;
            }
        }
    }

    /** IMG offsets place the bitmap; CANVAS_OBSERVER position selects its visible viewport. */
    static float backgroundDrawX(int imageOffsetX, int backgroundPositionX) {
        return GraphicsRenderer.asFloat(backgroundDestination(
                imageOffsetX, 0, 0, 0, backgroundPositionX, 0).x());
    }

    static float backgroundDrawY(int imageOffsetY, int imageHeight, int backgroundPositionY) {
        return GraphicsRenderer.asFloat(backgroundDestination(
                0, imageOffsetY, 0, imageHeight, 0, backgroundPositionY).y());
    }

    static OpenGlRect backgroundDestination(
            int imageOffsetX,
            int imageOffsetY,
            int imageWidth,
            int imageHeight,
            int backgroundPositionX,
            int backgroundPositionY
    ) {
        CanvasRect bounds = CanvasRect.fromPositionAndSize(
                imageOffsetX - backgroundPositionX,
                imageOffsetY - backgroundPositionY,
                imageWidth,
                imageHeight);
        return CanvasCoordinateSystem.toOpenGl(bounds);
    }

    private void renderPastedGraphics() {
        for (PastedGraphic p : game.getPastedGraphics()) {
            if (p.texture() == null) continue;
            batch.setColor(1, 1, 1, p.opacity());
            OpenGlRect destination = CanvasCoordinateSystem.toOpenGl(p.bounds());
            batch.draw(p.texture(),
                    GraphicsRenderer.asFloat(destination.x()),
                    GraphicsRenderer.asFloat(destination.y()),
                    GraphicsRenderer.asFloat(destination.width()),
                    GraphicsRenderer.asFloat(destination.height()));
            lastRenderStats.visibleSpriteObjects++;
            lastRenderStats.pastedGraphics++;
        }
    }

    private void renderDrawList(List<Variable> drawList, GameContext context) {
        for (Variable variable : drawList) {
            if (variable instanceof ImageVariable img) {
                recordImageWorkload(img);
                renderImage(img);
            } else if (variable instanceof AnimoVariable animo) {
                recordAnimoWorkload(animo);
                renderAnimo(animo);
            } else if (variable instanceof KolorowankaVariable klr) {
                renderKolorowanka(klr);
            } else if (variable instanceof TextVariable text) {
                recordTextWorkload(text);
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
        lastRenderStats.visibleSpriteObjects++;
        batch.setColor(1, 1, 1, 1);
        OpenGlRect destination = CanvasCoordinateSystem.toOpenGl(klr.getCanvasBounds());
        batch.draw(texture,
                GraphicsRenderer.asFloat(destination.x()),
                GraphicsRenderer.asFloat(destination.y()),
                GraphicsRenderer.asFloat(destination.width()),
                GraphicsRenderer.asFloat(destination.height()));
    }

    private void renderImage(ImageVariable imageVariable) {
        Image image = imageVariable.getImage();
        if (image == null || image.getImageTexture() == null) {
            return;
        }

        CanvasRect rect = imageVariable.getRect();
        CanvasRect clippingRect = imageVariable.getClippingRect();
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

    private void recordImageWorkload(ImageVariable imageVariable) {
        Image image = imageVariable.getImage();
        if (!imageVariable.isVisible() || image == null || image.getImageTexture() == null) {
            return;
        }
        lastRenderStats.visibleSpriteObjects++;
        if (imageVariable.hasFilters()) {
            lastRenderStats.filteredSprites++;
        }
        if (imageVariable.getClippingRect() != null) {
            lastRenderStats.clippedSprites++;
        }
        if (imageVariable.getAlphaMask() != null) {
            lastRenderStats.maskedSprites++;
        }
    }

    private void recordAnimoWorkload(AnimoVariable animoVariable) {
        Image image = animoVariable.getCurrentImage();
        if (!animoVariable.isVisible() || !animoVariable.isRenderedOnCanvas()
                || image == null || image.getImageTexture() == null) {
            return;
        }
        lastRenderStats.visibleSpriteObjects++;
        if (animoVariable.hasFilters()) {
            lastRenderStats.filteredSprites++;
        }
    }

    private void recordTextWorkload(TextVariable textVariable) {
        String text = textVariable.getText();
        if (textVariable.isVisible() && text != null && !text.isEmpty()
                && textVariable.getRect() != null) {
            lastRenderStats.visibleTextObjects++;
        }
    }

    public RenderStats getLastRenderStats() {
        return lastRenderStats;
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

    /** Primitive, reused workload snapshot for the most recently rendered scene. */
    public static final class RenderStats {
        private int drawableObjects;
        private int visibleSpriteObjects;
        private int visibleTextObjects;
        private int pastedGraphics;
        private int filteredSprites;
        private int clippedSprites;
        private int maskedSprites;

        private void reset() {
            drawableObjects = 0;
            visibleSpriteObjects = 0;
            visibleTextObjects = 0;
            pastedGraphics = 0;
            filteredSprites = 0;
            clippedSprites = 0;
            maskedSprites = 0;
        }

        public int drawableObjects() {
            return drawableObjects;
        }

        public int visibleSpriteObjects() {
            return visibleSpriteObjects;
        }

        public int visibleTextObjects() {
            return visibleTextObjects;
        }

        public int pastedGraphics() {
            return pastedGraphics;
        }

        public int filteredSprites() {
            return filteredSprites;
        }

        public int clippedSprites() {
            return clippedSprites;
        }

        public int maskedSprites() {
            return maskedSprites;
        }
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
