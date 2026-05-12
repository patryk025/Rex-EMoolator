package pl.genschu.bloomooemulator.tests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import pl.genschu.bloomooemulator.TestEnvironment;
import pl.genschu.bloomooemulator.interpreter.values.IntValue;
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
}
