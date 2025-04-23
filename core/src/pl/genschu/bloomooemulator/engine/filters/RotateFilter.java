package pl.genschu.bloomooemulator.engine.filters;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import pl.genschu.bloomooemulator.utils.ArgumentsHelper;

public class RotateFilter extends Filter {
    @Override
    public void apply(Batch batch, Texture texture, float x, float y, float width, float height) {
        float angle = 0f;
        if (properties.containsKey("ANGLE")) {
            Object rotationAngle = getProperty("ANGLE");
            angle = (float) ArgumentsHelper.getDouble(rotationAngle);
        }

        // TODO: support for CURRENTFRAME = TRUE

        batch.draw(
                texture,
                x, y, // Position
                width/2, height/2, // Origin (set to center for now)
                width, height, // Size
                1, 1, // Scale
                -angle, // Rotation
                0, 0, texture.getWidth(), texture.getHeight(), // Texture region
                false, false // Flip
        );
    }
}