package pl.genschu.bloomooemulator.engine.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
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

/**
 * Class responsible for rendering text.
 */
class TextRenderer implements Disposable {
    private final SpriteBatch batch;
    private final OrthographicCamera camera;
    private final BitmapFont defaultFont;

    public TextRenderer(SpriteBatch batch, OrthographicCamera camera) {
        this.batch = batch;
        this.camera = camera;
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
                rect,
                textVariable.getHJustify(),
                textVariable.getVJustify()
        );

        boolean clippingPushed = false;
        if (rect.getWidth() > 0 && rect.getHeight() > 0) {
            Rectangle bounds = new Rectangle(
                    rect.getXLeft(),
                    GraphicsRenderer.VIRTUAL_HEIGHT - rect.getYTop(),
                    rect.getWidth(),
                    rect.getHeight()
            );
            Rectangle scissors = new Rectangle();
            ScissorStack.calculateScissors(camera, batch.getTransformMatrix(), bounds, scissors);
            clippingPushed = ScissorStack.pushScissors(scissors);
            if (!clippingPushed) {
                return;
            }
        }

        batch.setColor(1, 1, 1, 1);
        try {
            for (PiklibTextLayout.Line line : layout.lines()) {
                for (PiklibTextLayout.GlyphPlacement glyph : line.glyphs()) {
                    TextureRegion region = font.getCharTexture(glyph.character());
                    if (region == null || region.getRegionWidth() <= 0) {
                        continue;
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
                batch.flush();
                ScissorStack.popScissors();
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

        int xLeft = clippingRect.getXLeft();
        int yTop = (int) (GraphicsRenderer.VIRTUAL_HEIGHT - clippingRect.getYTop());
        int xRight = clippingRect.getXRight();
        int yBottom = (int) (GraphicsRenderer.VIRTUAL_HEIGHT - clippingRect.getYBottom());

        batch.flush();
        Gdx.gl.glEnable(GL20.GL_SCISSOR_TEST);

        // Calculate clipping area coordinates in screen pixels
        Vector2 projectedCoordsLeftTop = cameraToWindowCoordinates(xLeft, yTop);
        Vector2 projectedCoordsRightBottom = cameraToWindowCoordinates(xRight, yBottom);

        int scissorX = (int) projectedCoordsLeftTop.x;
        int scissorY = (int) projectedCoordsLeftTop.y;
        int scissorWidth = (int) (projectedCoordsRightBottom.x - projectedCoordsLeftTop.x);
        int scissorHeight = (int) (projectedCoordsRightBottom.y - projectedCoordsLeftTop.y);

        Gdx.gl.glScissor(scissorX, scissorY, scissorWidth, scissorHeight);

        batch.draw(image.getImageTexture(),
                rect.getXLeft(),
                GraphicsRenderer.VIRTUAL_HEIGHT - rect.getYTop() - image.height,
                image.width,
                image.height);

        batch.flush();
        Gdx.gl.glDisable(GL20.GL_SCISSOR_TEST);
    }

    private Vector2 cameraToWindowCoordinates(float x, float y) {
        Vector3 worldCoordinates = new Vector3(x, y, 0);
        Vector3 windowCoordinates = new Vector3();

        return new Vector2(windowCoordinates.x, windowCoordinates.y);
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
        int xLeft = clippingRect.getXLeft();
        int yTop = (int) (VIRTUAL_HEIGHT - clippingRect.getYTop());
        int xRight = clippingRect.getXRight();
        int yBottom = (int) (VIRTUAL_HEIGHT - clippingRect.getYBottom());

        batch.flush();
        Gdx.gl.glEnable(GL20.GL_SCISSOR_TEST);

        // Calculate clipping area coordinates in screen pixels
        Vector2 projectedCoordsLeftTop = new Vector2(xLeft, yTop);  // Temporary, need proper implementation
        Vector2 projectedCoordsRightBottom = new Vector2(xRight, yBottom);  // Temporary

        int scissorX = (int) projectedCoordsLeftTop.x;
        int scissorY = (int) projectedCoordsLeftTop.y;
        int scissorWidth = (int) (projectedCoordsRightBottom.x - projectedCoordsLeftTop.x);
        int scissorHeight = (int) (projectedCoordsRightBottom.y - projectedCoordsLeftTop.y);

        Gdx.gl.glScissor(scissorX, scissorY, scissorWidth, scissorHeight);

        batch.draw(image.getImageTexture(),
                rect.getXLeft(),
                VIRTUAL_HEIGHT - rect.getYTop() - image.height,
                image.width,
                image.height);

        batch.flush();
        Gdx.gl.glDisable(GL20.GL_SCISSOR_TEST);
    }

    @Override
    public void dispose() {
    }
}
