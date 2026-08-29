package pl.genschu.bloomooemulator.engine.filters;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import pl.genschu.bloomooemulator.geometry.coordinates.CanvasCoordinateSystem;
import pl.genschu.bloomooemulator.geometry.coordinates.OpenGlRect;

public class RotateFilter extends Filter {
    @Override
    public void apply(Batch batch, Texture texture, OpenGlRect destination) {
        float x = (float) destination.x();
        float y = (float) destination.y();
        float width = (float) destination.width();
        float height = (float) destination.height();
        float angle = 0f;
        if (properties.containsKey("ANGLE")) {
            Object rotationAngle = getProperty("ANGLE");
            angle = coerceFloat(rotationAngle);
        }

        // TODO: support for CURRENTFRAME = TRUE

        batch.draw(
                texture,
                x, y, // Position
                width/2, height/2, // Origin (set to center for now)
                width, height, // Size
                1, 1, // Scale
                (float) CanvasCoordinateSystem.toOpenGlRotationDegrees(angle),
                0, 0, texture.getWidth(), texture.getHeight(), // Texture region
                false, false // Flip
        );
    }

    private static float coerceFloat(Object value) {
        if (value instanceof Number number) {
            return number.floatValue();
        }
        if (value instanceof Boolean bool) {
            return bool ? 1f : 0f;
        }
        if (value instanceof String string) {
            try {
                return Float.parseFloat(string);
            } catch (NumberFormatException ignored) {
                return 0f;
            }
        }
        return 0f;
    }
}
