package pl.genschu.bloomooemulator.tests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import pl.genschu.bloomooemulator.TestEnvironment;
import pl.genschu.bloomooemulator.builders.ContextBuilder;
import pl.genschu.bloomooemulator.geometry.coordinates.CanvasRect;
import pl.genschu.bloomooemulator.interpreter.context.Context;
import pl.genschu.bloomooemulator.interpreter.values.IntValue;
import pl.genschu.bloomooemulator.interpreter.values.StringValue;
import pl.genschu.bloomooemulator.interpreter.variable.ImageVariable;
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

    @Test
    void setJustifyStoresCanonicalDirectDrawRect() {
        TextVariable text = new TextVariable("TEXT");

        text.callMethod(
                "SETJUSTIFY",
                new IntValue(10), new IntValue(20), new IntValue(110), new IntValue(120),
                new StringValue("CENTER"), new StringValue("BOTTOM"));

        assertEquals(new CanvasRect(10, 20, 110, 120), text.getRect());
    }

    @Test
    void variableBackedRectTracksGraphicBoundsReplacement() {
        Context context = new ContextBuilder().build();
        ImageVariable image = new ImageVariable("IMAGE");
        image.state().rect = new CanvasRect(0, 0, 10, 10);
        TextVariable text = new TextVariable("TEXT");
        context.setVariable("IMAGE", image);
        context.setVariable("TEXT", text);
        context.setAttribute("TEXT", "RECT", "IMAGE");

        text.init(context);
        image.state().rect = new CanvasRect(100, 200, 300, 400);

        assertEquals(new CanvasRect(100, 200, 300, 400), text.getRect());
    }
}
