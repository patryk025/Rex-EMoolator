package pl.genschu.bloomooemulator.engine.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;
import pl.genschu.bloomooemulator.engine.context.EngineVariable;
import pl.genschu.bloomooemulator.engine.context.GameContext;
import pl.genschu.bloomooemulator.engine.filters.Filter;
import pl.genschu.bloomooemulator.interpreter.variable.AnimoVariable;
import pl.genschu.bloomooemulator.interpreter.variable.FontVariable;
import pl.genschu.bloomooemulator.interpreter.variable.ImageVariable;
import pl.genschu.bloomooemulator.interpreter.variable.TextVariable;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.objects.Image;
import pl.genschu.bloomooemulator.geometry.shapes.Box2D;

/**
 * Class responsible for rendering graphics.
 */
public class GraphicsRenderer implements Disposable {
    protected static final float VIRTUAL_WIDTH = 800;
    protected static final float VIRTUAL_HEIGHT = 600;

    private final SpriteBatch batch;
    private final OrthographicCamera camera;

    public GraphicsRenderer(SpriteBatch batch, OrthographicCamera camera) {
        this.batch = batch;
        this.camera = camera;
    }

    /**
     * Renders an object of type ImageVariable.
     */
    public void renderImage(ImageVariable imageVariable) {
        if (!imageVariable.isVisible()) {
            return;
        }

        Image image = imageVariable.getImage();
        if (image == null || image.getImageTexture() == null) {
            return;
        }

        Box2D rect = imageVariable.getRect();
        batch.setColor(1, 1, 1, imageVariable.getOpacity());
        if (imageVariable.hasFilters()) {
            // get first filter (not sure if there can be more at once)
            Filter filter = imageVariable.getFilters().get(0);
            filter.apply(batch, image.getImageTexture(), rect.getXLeft(), VIRTUAL_HEIGHT - rect.getYTop() - image.height, image.width, image.height);
        } else {
            batch.draw(image.getImageTexture(),
                    rect.getXLeft(),
                    VIRTUAL_HEIGHT - rect.getYTop() - image.height,
                    image.width,
                    image.height);
        }
    }

    /**
     * Renders an object of type AnimoVariable.
     */
    public void renderAnimo(AnimoVariable animoVariable) {
        if (!animoVariable.isVisible()) {
            return;
        }

        if(!animoVariable.isRenderedOnCanvas()) {
            return;
        }

        Image image = animoVariable.getCurrentImage();
        if (image == null || image.getImageTexture() == null) {
            return;
        }

        Box2D rect = animoVariable.getRect();
        batch.setColor(1, 1, 1, animoVariable.getCalculatedOpacity());

        if (animoVariable.hasFilters()) {
            // get first filter (not sure if there can be more at once)
            Filter filter = animoVariable.getFilters().get(0);
            filter.apply(batch, image.getImageTexture(), rect.getXLeft(), VIRTUAL_HEIGHT - rect.getYTop() - image.height, image.width, image.height);
        } else {
            batch.draw(image.getImageTexture(),
                    rect.getXLeft(),
                    VIRTUAL_HEIGHT - rect.getYTop() - image.height,
                    image.width,
                    image.height);
        }
    }

    @Override
    public void dispose() {
        // Zwolnienie zasobów, jeśli są potrzebne
    }
}

/** Applies clipping in fixed FBO pixels, bypassing backbuffer/HiDPI scaling. */
final class LogicalScissor {
    private LogicalScissor() {}

    static boolean enable(SpriteBatch batch, Box2D rect) {
        int x = Math.max(0, rect.getXLeft());
        int right = Math.min((int) GraphicsRenderer.VIRTUAL_WIDTH, rect.getXRight());
        int y = Math.max(0, (int) GraphicsRenderer.VIRTUAL_HEIGHT - rect.getYTop());
        int top = Math.min((int) GraphicsRenderer.VIRTUAL_HEIGHT,
                (int) GraphicsRenderer.VIRTUAL_HEIGHT - rect.getYBottom());
        int width = right - x;
        int height = top - y;
        if (width <= 0 || height <= 0) {
            return false;
        }

        batch.flush();
        Gdx.gl.glEnable(GL20.GL_SCISSOR_TEST);
        Gdx.gl.glScissor(x, y, width, height);
        return true;
    }

    static void disable(SpriteBatch batch) {
        batch.flush();
        Gdx.gl.glDisable(GL20.GL_SCISSOR_TEST);
    }
}

/**
 * Class responsible for rendering text.
 */
class TextRenderer implements Disposable {
    private final SpriteBatch batch;
    private final BitmapFont defaultFont;

    public TextRenderer(SpriteBatch batch) {
        this.batch = batch;
        this.defaultFont = new BitmapFont();
    }

    /**
     * Renders a TextVariable using its script-selected Piklib FONT.
     */
    public void renderText(TextVariable textVariable, GameContext context) {
        if (!textVariable.isVisible()) {
            return;
        }

        String text = textVariable.getText();
        if (text == null || text.isEmpty()) return;

        Box2D rect = textVariable.getRect();
        if (rect == null) return;

        EngineVariable selectedFont = textVariable.getFontName() == null
                ? null
                : context.getVariable(textVariable.getFontName());
        if (selectedFont instanceof FontVariable font && font.isLoaded()) {
            renderPiklibText(textVariable, font, rect);
            return;
        }

        renderFallbackText(text, rect);
    }

    private void renderPiklibText(TextVariable textVariable, FontVariable font, Box2D rect) {
        PiklibTextLayout.Layout layout = PiklibTextLayout.layout(
                font,
                textVariable.getText(),
                textVariable.getColor(),
                rect,
                textVariable.getHJustify(),
                textVariable.getVJustify()
        );

        boolean clippingPushed = false;
        if (rect.getWidth() > 0 && rect.getHeight() > 0) {
            clippingPushed = LogicalScissor.enable(batch, rect);
            if (!clippingPushed) {
                return;
            }
        }

        batch.setColor(1, 1, 1, 1);
        try {
            int activeColor = -1;
            for (PiklibTextLayout.Line line : layout.lines()) {
                for (PiklibTextLayout.GlyphPlacement glyph : line.glyphs()) {
                    TextureRegion region = font.getCharTexture(glyph.character());
                    if (region == null || region.getRegionWidth() <= 0) {
                        continue;
                    }
                    if (activeColor != glyph.color()) {
                        activeColor = glyph.color();
                        batch.setColor(
                                ((activeColor >>> 16) & 0xFF) / 255f,
                                ((activeColor >>> 8) & 0xFF) / 255f,
                                (activeColor & 0xFF) / 255f,
                                1f
                        );
                    }
                    batch.draw(
                            region,
                            glyph.x(),
                            GraphicsRenderer.VIRTUAL_HEIGHT - glyph.top() - font.getCharHeight()
                    );
                }
            }
        } finally {
            if (clippingPushed) {
                LogicalScissor.disable(batch);
            }
            batch.setColor(1, 1, 1, 1);
        }
    }

    private void renderFallbackText(String text, Box2D rect) {
        float startX = rect.getXLeft();
        float startY = GraphicsRenderer.VIRTUAL_HEIGHT - rect.getYBottom();

        String[] lines = text.replace("\n", "").replace('\r', '|').split("\\|", -1);
        float lineHeight = defaultFont.getLineHeight();

        for (int i = 0; i < lines.length; i++) {
            defaultFont.draw(batch, lines[i], startX, startY - (i * lineHeight));
        }
    }

    @Override
    public void dispose() {
        defaultFont.dispose();
    }
}

/**
 * Class responsible for rendering masks.
 */
class MaskRenderer implements Disposable {
    private final SpriteBatch batch;

    public MaskRenderer(SpriteBatch batch) {
        this.batch = batch;
    }

    /**
     * Renders a clipping mask for the graphic object.
     */
    public void renderWithClipping(Image image, Box2D rect, Box2D clippingRect) {
        if (clippingRect == null) {
            return;
        }

        if (!LogicalScissor.enable(batch, clippingRect)) {
            return;
        }
        try {
            batch.draw(image.getImageTexture(),
                    rect.getXLeft(),
                    GraphicsRenderer.VIRTUAL_HEIGHT - rect.getYTop() - image.height,
                    image.width,
                    image.height);
        } finally {
            LogicalScissor.disable(batch);
        }
    }

    @Override
    public void dispose() {
        //
    }
}

/**
 * Class responsible for rendering alpha masks.
 */
class AlphaMaskRenderer implements Disposable {
    private static final float VIRTUAL_HEIGHT = 600;

    private final SpriteBatch batch;

    public AlphaMaskRenderer(SpriteBatch batch) {
        this.batch = batch;
    }

    /**
     * Renders object with alpha mask
     */
    public void renderWithAlphaMask(ImageVariable imageVariable, Box2D rect, Box2D clippingRect, ImageVariable.AlphaMaskBinding alphaMask) {
        Image image = imageVariable.getImage();
        if (image == null || image.getImageTexture() == null) {
            return;
        }

        Image maskImage = alphaMask.mask().getImage();
        if (maskImage == null || maskImage.getImageTexture() == null) {
            batch.setColor(1, 1, 1, imageVariable.getOpacity());
            batch.draw(image.getImageTexture(),
                    rect.getXLeft(),
                    VIRTUAL_HEIGHT - rect.getYTop() - image.height,
                    image.width,
                    image.height);
            return;
        }

        batch.flush();

        // Write mask alpha into the destination alpha channel only
        Gdx.gl.glColorMask(false, false, false, true);
        batch.setBlendFunction(GL20.GL_ONE, GL20.GL_ZERO);
        batch.setColor(1, 1, 1, 1);
        batch.draw(maskImage.getImageTexture(),
                alphaMask.posX(),
                VIRTUAL_HEIGHT - alphaMask.posY() - maskImage.height,
                maskImage.width,
                maskImage.height);
        batch.flush();

        // Draw the image modulated by the destination alpha we just wrote
        Gdx.gl.glColorMask(true, true, true, true);
        batch.setBlendFunction(GL20.GL_DST_ALPHA, GL20.GL_ONE_MINUS_DST_ALPHA);
        batch.setColor(1, 1, 1, imageVariable.getOpacity());

        // Rendering image
        if (clippingRect != null) {
            renderWithClipping(image, rect, clippingRect);
        } else {
            batch.draw(image.getImageTexture(),
                    rect.getXLeft(),
                    VIRTUAL_HEIGHT - rect.getYTop() - image.height,
                    image.width,
                    image.height);
        }

        batch.flush();

        // Restoring default settings
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        batch.setColor(1, 1, 1, 1);
    }

    private void renderWithClipping(Image image, Box2D rect, Box2D clippingRect) {
        if (!LogicalScissor.enable(batch, clippingRect)) {
            return;
        }
        try {
            batch.draw(image.getImageTexture(),
                    rect.getXLeft(),
                    VIRTUAL_HEIGHT - rect.getYTop() - image.height,
                    image.width,
                    image.height);
        } finally {
            LogicalScissor.disable(batch);
        }
    }

    @Override
    public void dispose() {
    }
}
