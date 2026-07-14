package pl.genschu.bloomooemulator.tests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import pl.genschu.bloomooemulator.TestEnvironment;
import pl.genschu.bloomooemulator.interpreter.values.IntValue;
import pl.genschu.bloomooemulator.interpreter.values.StringValue;
import pl.genschu.bloomooemulator.interpreter.variable.ImageVariable;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        image.state().rect.setXRight(24);
        image.state().rect.setYBottom(-24);

        image.callMethod("SETANCHOR", new StringValue("CENTER"));
        image.callMethod("SETPOSITION", new IntValue(250), new IntValue(124));

        assertEquals(238, image.getPosX());
        assertEquals(112, image.getPosY());
    }
}
