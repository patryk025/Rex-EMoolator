package pl.genschu.bloomooemulator.engine.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.interpreter.variable.types.AnimoVariable;
import pl.genschu.bloomooemulator.interpreter.variable.types.ImageVariable;
import pl.genschu.bloomooemulator.interpreter.variable.types.TextVariable;
import pl.genschu.bloomooemulator.objects.Image;
import pl.genschu.bloomooemulator.objects.Rectangle;

import java.util.Map;

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
        if (!imageVariable.isVisible() || !imageVariable.isRenderedOnCanvas()) {
            return;
        }

        Image image = imageVariable.getImage();
        if (image == null || image.getImageTexture() == null) {
            return;
        }

        Rectangle rect = imageVariable.getRect();
        batch.setColor(1, 1, 1, imageVariable.getOpacity());
        batch.draw(image.getImageTexture(),
                rect.getXLeft(),
                VIRTUAL_HEIGHT - rect.getYTop() - image.height,
                image.width,
                image.height);
    }

    /**
     * Renders an object of type AnimoVariable.
     */
    public void renderAnimo(AnimoVariable animoVariable) {
        if (!animoVariable.isVisible() && !animoVariable.isPlaying()) {
            return;
        }

        Image image = animoVariable.getCurrentImage();
        if (image == null || image.getImageTexture() == null) {
            return;
        }

        Rectangle rect = animoVariable.getRect();
        batch.setColor(1, 1, 1, animoVariable.getOpacity());
        batch.draw(image.getImageTexture(),
                rect.getXLeft(),
                VIRTUAL_HEIGHT - rect.getYTop() - image.height,
                image.width,
                image.height);
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
    private BitmapFont defaultFont;

    public TextRenderer(SpriteBatch batch) {
        this.batch = batch;
        this.defaultFont = new BitmapFont();
    }

    /**
     * Renders an object of type TextVariable.
     */
    public void renderText(TextVariable textVariable) {
        if (!textVariable.isVisible()) {
            return;
        }

        textVariable.renderText(batch);
    }

    @Override
    public void dispose() {
        if (defaultFont != null) {
            defaultFont.dispose();
        }
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
    public void renderWithClipping(Image image, Rectangle rect, Rectangle clippingRect) {
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
    public void renderWithAlphaMask(ImageVariable imageVariable, Rectangle rect, Rectangle clippingRect, Map<String, Rectangle> alphaMasks) {
        Image image = imageVariable.getImage();
        if (image == null || image.getImageTexture() == null) {
            return;
        }

        // End the current batch
        batch.end();

        // Configuring alpha mask
        batch.begin();
        Gdx.gl.glColorMask(false, false, false, true);
        batch.setBlendFunction(GL20.GL_ONE, GL20.GL_ZERO);

        // Render alpha masks
        for (Map.Entry<String, Rectangle> entry : alphaMasks.entrySet()) {
            String maskName = entry.getKey();
            Rectangle maskRect = entry.getValue();

            Variable maskVar = imageVariable.getContext().getVariable(maskName);
            if (!(maskVar instanceof ImageVariable)) continue;

            ImageVariable mask = (ImageVariable) maskVar;
            if (mask.getImage() == null) continue;

            Texture maskTexture = mask.getImage().getImageTexture();
            if (maskTexture == null) continue;

            batch.draw(maskTexture,
                    maskRect.getXLeft(),
                    VIRTUAL_HEIGHT - maskRect.getYTop() - mask.getImage().height);
        }

        batch.flush();

        // Setting batch for rendering with alpha
        Gdx.gl.glColorMask(true, true, true, true);
        batch.setBlendFunction(GL20.GL_DST_ALPHA, GL20.GL_ONE_MINUS_DST_ALPHA);

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
        batch.end();
        batch.begin();
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    }

    private void renderWithClipping(Image image, Rectangle rect, Rectangle clippingRect) {
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