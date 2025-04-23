package pl.genschu.bloomooemulator.engine.filters;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.Texture;
import pl.genschu.bloomooemulator.utils.ArgumentsHelper;

public class ScaleFilter extends Filter {
    @Override
    public void apply(Batch batch, Texture texture, float x, float y, float width, float height) {
        float scaleX = 1f;
        float scaleY = 1f;

        if (properties.containsKey("FACTORX")) {
            Object scaleObj = getProperty("FACTORX");
            scaleX = (float) ArgumentsHelper.getDouble(scaleObj);
            if(scaleX <= 0) scaleX = 1f;
        }

        if (properties.containsKey("FACTORY")) {
            Object scaleObj = getProperty("FACTORY");
            scaleY = (float) ArgumentsHelper.getDouble(scaleObj);
            if(scaleY <= 0) scaleY = 1f;
        }

        // TODO: support for BYCENTER = FALSE
        float centerX = x + width / 2;
        float centerY = y + height / 2;

        float newWidth = width * scaleX;
        float newHeight = height * scaleY;
        float newX = centerX - newWidth / 2;
        float newY = centerY - newHeight / 2;

        batch.draw(
                texture,
                newX, newY,             // Position
                newWidth / 2, newHeight / 2, // Origin point
                newWidth, newHeight,    // New dimensions
                1, 1,                   // We don't need to scale here, as we recalculated dimensions
                0,                      // No rotation
                0, 0,                   // Texture region coordinates
                texture.getWidth(),     // Texture region width
                texture.getHeight(),    // Texture region height
                false, false            // Don't flip
        );
    }
}