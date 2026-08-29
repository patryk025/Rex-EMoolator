package pl.genschu.bloomooemulator.tests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import pl.genschu.bloomooemulator.TestEnvironment;
import pl.genschu.bloomooemulator.geometry.coordinates.CanvasRect;
import pl.genschu.bloomooemulator.interpreter.values.BoolValue;
import pl.genschu.bloomooemulator.interpreter.values.IntValue;
import pl.genschu.bloomooemulator.interpreter.values.StringValue;
import pl.genschu.bloomooemulator.interpreter.variable.ImageVariable;
import pl.genschu.bloomooemulator.objects.Image;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ImageVariableTest {

    @BeforeAll
    static void boot() {
        TestEnvironment.init();
    }

    @Test
    void setOpacityWaitsForInvalidateBeforeChangingRenderedOpacity_s65ZamekRegression() {
        // Reksio i Ufo, S65_ZAMEK: SETOPACITY is staged and INVALIDATE applies it.
        ImageVariable image = new ImageVariable("IMGZAMEK");

        assertEquals(1.0f, image.getOpacity(), 0.0001f);

        image.callMethod("SETOPACITY", new IntValue(64));

        assertEquals(1.0f, image.getOpacity(), 0.0001f);

        image.callMethod("INVALIDATE");

        assertEquals(64.0f / 255.0f, image.getOpacity(), 0.0001f);
    }

    @Test
    void centerAnchorMakesSetPositionReferToImageCenter_footballMatchRegression() {
        ImageVariable image = new ImageVariable("BLUEBALL");
        image.state().rect = new CanvasRect(0, 0, 24, 24);

        image.callMethod("SETANCHOR", new StringValue("CENTER"));
        image.callMethod("SETPOSITION", new IntValue(250), new IntValue(124));

        assertEquals(238, image.getPosX());
        assertEquals(112, image.getPosY());
    }

    @Test
    void isAtUsesTopLeftScreenCoordinatesForImageBounds_wpzrMaskRegression() {
        ImageVariable image = new ImageVariable("MASKA_PRZESZKODY");
        image.state().rect = new CanvasRect(10, 20, 30, 40);

        assertTrue(image.callMethod("ISAT", new IntValue(15), new IntValue(25), new BoolValue(false))
                .returnValue().toBool().value());
        assertFalse(image.callMethod("ISAT", new IntValue(15), new IntValue(19), new BoolValue(false))
                .returnValue().toBool().value());
        assertFalse(image.callMethod("ISAT", new IntValue(15), new IntValue(41), new BoolValue(false))
                .returnValue().toBool().value());
        assertFalse(image.callMethod("ISAT", new IntValue(30), new IntValue(25), BoolValue.FALSE)
                .returnValue().toBool().value(), "right edge is exclusive");
        assertFalse(image.callMethod("ISAT", new IntValue(15), new IntValue(40), BoolValue.FALSE)
                .returnValue().toBool().value(), "bottom edge is exclusive");
    }

    @Test
    void getCenterUsesCanonicalTopDownBounds() {
        ImageVariable image = new ImageVariable("IMAGE");
        image.state().rect = new CanvasRect(10, 20, 30, 60);

        assertEquals(20, image.callMethod("GETCENTERX").returnValue().toInt().value());
        assertEquals(40, image.callMethod("GETCENTERY").returnValue().toInt().value());
    }

    @Test
    void isAtCanRejectTransparentPixels_wpzrObstacleMaskRegression() {
        Image pixels = new Image(2, 1, 0, 0, 16,
                new byte[]{0, 0, 0, 0}, new byte[]{0, (byte) 255}, 0);
        try {
            ImageVariable image = new ImageVariable("MASKA_PRZESZKODY");
            image.state().image = pixels;
            image.state().posX = 10;
            image.state().posY = 20;
            image.state().updateRect();

            assertTrue(image.callMethod("ISAT", new IntValue(10), new IntValue(20),
                    BoolValue.FALSE)
                    .returnValue().toBool().value());
            assertFalse(image.callMethod("ISAT", new IntValue(10), new IntValue(20),
                    BoolValue.TRUE)
                    .returnValue().toBool().value());
            assertTrue(image.callMethod("ISAT", new IntValue(11), new IntValue(20),
                    BoolValue.TRUE)
                    .returnValue().toBool().value());
        } finally {
            pixels.getImageTexture().dispose();
        }
    }
}
