package pl.genschu.bloomooemulator.tests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.genschu.bloomooemulator.TestEnvironment;
import pl.genschu.bloomooemulator.builders.ContextBuilder;
import pl.genschu.bloomooemulator.builders.MethodHelper;
import pl.genschu.bloomooemulator.geometry.shapes.Box2D;
import pl.genschu.bloomooemulator.interpreter.context.Context;
import pl.genschu.bloomooemulator.interpreter.values.BoolValue;
import pl.genschu.bloomooemulator.interpreter.values.IntValue;
import pl.genschu.bloomooemulator.interpreter.values.Value;
import pl.genschu.bloomooemulator.interpreter.variable.ImageVariable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Regression test for the broken {@code ImageVariable.isAt} / script {@code ISAT}.
 *
 * <p>The GFX rect is stored posY-anchored: {@code yTop = posY}, {@code yBottom = posY - height}
 * (see {@code updateRect}). Click detection ({@code Box2D.contains}) compensates for that with a
 * {@code +getHeight()} shift, so the real screen footprint of an image at posY=0/height=10 is the
 * band [0,10]. {@code isAt} however reads the raw getters ({@code y >= yTop && y <= yBottom}),
 * which for any positive height is {@code y >= 0 && y <= -10} — an empty range — so it always
 * returns false. This test pins {@code ISAT} to the same footprint as {@code contains}.</p>
 */
class ImageVariableIsAtTest {
    private Context ctx;

    @BeforeAll
    static void boot() {
        TestEnvironment.init();
    }

    @BeforeEach
    void setUp() {
        ctx = new ContextBuilder().build();
    }

    /** Image at posY=0, height=10 → screen footprint x[0,10], y[0,10]. */
    private static ImageVariable footprintImage() {
        ImageVariable image = new ImageVariable("IMG");
        image.state().rect = new Box2D(0, -10, 10, 0);
        image.state().visible = true;
        return image;
    }

    @Test
    void isAtReturnsTrueForPointInsideFootprint() {
        ImageVariable image = footprintImage();
        ctx.setVariable("IMG", image);

        Value result = MethodHelper.callWithContext(ctx, image, "ISAT",
                new IntValue(5), new IntValue(5));

        assertTrue(((BoolValue) result).value(),
                "ISAT(5,5) should hit an image whose footprint is x[0,10] y[0,10]");
    }

    @Test
    void isAtAgreesWithContains() {
        ImageVariable image = footprintImage();
        Box2D rect = image.state().rect;

        // isAt must report the same hit region that click detection (Box2D.contains) uses.
        int[][] probes = {{5, 5}, {0, 0}, {10, 10}, {5, 50}, {-5, 5}, {5, -50}};
        for (int[] p : probes) {
            assertEquals(rect.contains(p[0], p[1]), image.isAt(p[0], p[1]),
                    "isAt must agree with Box2D.contains at (" + p[0] + "," + p[1] + ")");
        }
    }
}
