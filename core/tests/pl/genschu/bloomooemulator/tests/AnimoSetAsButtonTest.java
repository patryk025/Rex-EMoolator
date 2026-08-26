package pl.genschu.bloomooemulator.tests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.genschu.bloomooemulator.TestEnvironment;
import pl.genschu.bloomooemulator.builders.ContextBuilder;
import pl.genschu.bloomooemulator.builders.MethodHelper;
import pl.genschu.bloomooemulator.geometry.coordinates.CanvasRect;
import pl.genschu.bloomooemulator.interpreter.context.Context;
import pl.genschu.bloomooemulator.interpreter.values.BoolValue;
import pl.genschu.bloomooemulator.interpreter.variable.AnimoVariable;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Regression guard: ANIMO buttons (SETASBUTTON) must use the live state.rect for hit
 * testing, not a snapshot taken at SETASBUTTON time. Scenes like S38_KALIBRACJA
 * depend on animated buttons whose bounds shift over time.
 */
class AnimoSetAsButtonTest {
    private Context ctx;

    @BeforeAll
    static void boot() {
        TestEnvironment.init();
    }

    @BeforeEach
    void setUp() {
        ctx = new ContextBuilder().build();
    }

    @Test
    void getRectReflectsLiveStateAfterSetAsButton() {
        AnimoVariable animo = new AnimoVariable("ANNCOMET0");
        animo.state().rect = new CanvasRect(0, 0, 100, 50);
        ctx.setVariable("ANNCOMET0", animo);

        MethodHelper.callWithContext(ctx, animo, "SETASBUTTON",
                BoolValue.TRUE, BoolValue.TRUE);

        // Animation frame moves the sprite; getRect() must reflect the new position
        // so the hit-test rect in ButtonHandler follows it.
        animo.state().rect = new CanvasRect(200, 150, 250, 200);

        CanvasRect live = animo.getRect();
        assertEquals(200, live.left());
        assertEquals(250, live.right());
        assertEquals(150, live.top());
        assertEquals(200, live.bottom());
    }
}
