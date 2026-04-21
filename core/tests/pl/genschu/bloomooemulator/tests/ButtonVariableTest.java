package pl.genschu.bloomooemulator.tests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.genschu.bloomooemulator.TestEnvironment;
import pl.genschu.bloomooemulator.builders.ContextBuilder;
import pl.genschu.bloomooemulator.builders.MethodHelper;
import pl.genschu.bloomooemulator.engine.decision.states.ButtonState;
import pl.genschu.bloomooemulator.geometry.shapes.Box2D;
import pl.genschu.bloomooemulator.interpreter.context.Context;
import pl.genschu.bloomooemulator.interpreter.variable.ButtonVariable;
import pl.genschu.bloomooemulator.interpreter.variable.ImageVariable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ButtonVariableTest {
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
    void initWithEnableFalseDisablesAndHidesGraphics() {
        ImageVariable standard = standardImage();
        ButtonVariable button = new ButtonVariable("BTN");
        ctx.setVariable("STD", standard);
        ctx.setVariable("BTN", button);
        ctx.setAttribute("BTN", "GFXSTANDARD", "STD");
        ctx.setAttribute("BTN", "ENABLE", "FALSE");

        button.init(ctx);

        assertEquals(ButtonState.DISABLED, button.getButtonState());
        assertFalse(button.isEnabled());
        assertFalse(standard.isVisible());
    }

    @Test
    void disableButVisibleStillKeepsStandardGraphicsVisible() {
        ImageVariable standard = standardImage();
        ButtonVariable button = new ButtonVariable("BTN");
        ctx.setVariable("STD", standard);
        ctx.setVariable("BTN", button);
        ctx.setAttribute("BTN", "GFXSTANDARD", "STD");
        ctx.setAttribute("BTN", "ENABLE", "TRUE");
        button.init(ctx);

        MethodHelper.callWithContext(ctx, button, "DISABLEBUTVISIBLE");

        assertEquals(ButtonState.DISABLED_BUT_VISIBLE, button.getButtonState());
        assertFalse(button.isEnabled());
        assertTrue(standard.isVisible());
    }

    private static ImageVariable standardImage() {
        ImageVariable image = new ImageVariable("STD");
        image.state().rect = new Box2D(0, -10, 10, 0);
        image.state().visible = true;
        return image;
    }
}
