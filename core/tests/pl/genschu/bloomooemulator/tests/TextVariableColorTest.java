package pl.genschu.bloomooemulator.tests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import pl.genschu.bloomooemulator.TestEnvironment;
import pl.genschu.bloomooemulator.interpreter.values.IntValue;
import pl.genschu.bloomooemulator.interpreter.variable.TextVariable;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TextVariableColorTest {
    @BeforeAll
    static void boot() {
        TestEnvironment.init();
    }

    @Test
    void setColorStoresRgbAndCopiedTextKeepsIt() {
        TextVariable text = new TextVariable("TEXT");

        text.callMethod(
                "SETCOLOR",
                new IntValue(255),
                new IntValue(0),
                new IntValue(128)
        );

        assertEquals(0xFF0080, text.getColor());
        assertEquals(0xFF0080, ((TextVariable) text.copyAs("COPY")).getColor());
    }
}
