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
import pl.genschu.bloomooemulator.interpreter.variable.AnimoVariable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

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
    void setAsButtonTrueFreezesCurrentRect() {
        AnimoVariable animo = new AnimoVariable("ANIMORESTART");
        animo.state().rect = new Box2D(10, 0, 50, -40);
        ctx.setVariable("ANIMORESTART", animo);

        MethodHelper.callWithContext(ctx, animo, "SETASBUTTON",
                BoolValue.TRUE, BoolValue.TRUE);

        Box2D frozen = animo.getButtonRect();
        assertNotNull(frozen);
        assertEquals(10, frozen.getXLeft());
        assertEquals(50, frozen.getXRight());
    }

    @Test
    void frozenRectIsIndependentOfLaterFrameRectChanges() {
        AnimoVariable animo = new AnimoVariable("ANIMORESTART");
        animo.state().rect = new Box2D(0, 0, 100, -50);
        ctx.setVariable("ANIMORESTART", animo);

        MethodHelper.callWithContext(ctx, animo, "SETASBUTTON",
                BoolValue.TRUE, BoolValue.TRUE);

        // Simulate a later animation frame moving / resizing the live rect.
        animo.state().rect = new Box2D(200, 200, 250, 150);

        Box2D frozen = animo.getButtonRect();
        assertEquals(0, frozen.getXLeft());
        assertEquals(100, frozen.getXRight());
        assertEquals(0, frozen.getYBottom());
        assertEquals(-50, frozen.getYTop());
    }

    @Test
    void setAsButtonFalseClearsButtonRect() {
        AnimoVariable animo = new AnimoVariable("ANIMORESTART");
        animo.state().rect = new Box2D(0, 0, 10, -10);
        ctx.setVariable("ANIMORESTART", animo);

        MethodHelper.callWithContext(ctx, animo, "SETASBUTTON",
                BoolValue.TRUE, BoolValue.TRUE);
        assertNotNull(animo.getButtonRect());

        MethodHelper.callWithContext(ctx, animo, "SETASBUTTON",
                BoolValue.FALSE, BoolValue.FALSE);
        assertNull(animo.getButtonRect());
    }
}
