package pl.genschu.bloomooemulator.tests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.genschu.bloomooemulator.TestEnvironment;
import pl.genschu.bloomooemulator.builders.ContextBuilder;
import pl.genschu.bloomooemulator.builders.MethodHelper;
import pl.genschu.bloomooemulator.engine.decision.events.ButtonEvent;
import pl.genschu.bloomooemulator.engine.decision.states.ButtonState;
import pl.genschu.bloomooemulator.geometry.coordinates.CanvasRect;
import pl.genschu.bloomooemulator.interpreter.context.Context;
import pl.genschu.bloomooemulator.interpreter.values.StringValue;
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

    @Test
    void sharedStandardAndClickGraphicsStayVisibleInTheirActiveStates() {
        ImageVariable standardAndClick = standardImage();
        ImageVariable hovered = new ImageVariable("HOVERED");
        ButtonVariable button = new ButtonVariable("BTN");
        ctx.setVariable("STD_CLICK", standardAndClick);
        ctx.setVariable("HOVERED", hovered);
        ctx.setVariable("BTN", button);
        ctx.setAttribute("BTN", "GFXSTANDARD", "STD_CLICK");
        ctx.setAttribute("BTN", "GFXONCLICK", "STD_CLICK");
        ctx.setAttribute("BTN", "GFXONMOVE", "HOVERED");

        button.init(ctx);

        assertTrue(standardAndClick.isVisible());
        assertFalse(hovered.isVisible());

        button.changeState(ButtonEvent.FOCUS_ON, ctx);
        assertFalse(standardAndClick.isVisible());
        assertTrue(hovered.isVisible());

        button.changeState(ButtonEvent.PRESSED, ctx);
        assertTrue(standardAndClick.isVisible());
        assertFalse(hovered.isVisible());

        button.changeState(ButtonEvent.RELEASED, ctx);
        assertFalse(standardAndClick.isVisible());
        assertTrue(hovered.isVisible());

        button.changeState(ButtonEvent.FOCUS_OFF, ctx);
        assertTrue(standardAndClick.isVisible());
        assertFalse(hovered.isVisible());
    }

    @Test
    void initReadsDraggingConfigurationAndMethodsToggleIt() {
        ButtonVariable button = new ButtonVariable("BTN");
        ctx.setVariable("BTN", button);
        ctx.setAttribute("BTN", "DRAG", "DRAG_IMAGE");
        ctx.setAttribute("BTN", "DRAGGABLE", "TRUE");

        button.init(ctx);

        assertTrue(button.isDraggable());
        assertEquals("DRAG_IMAGE", button.getDragName());

        button.callMethod("DISABLEDRAGGING");
        assertFalse(button.isDraggable());

        button.callMethod("ENABLEDRAGGING");
        assertTrue(button.isDraggable());
    }

    @Test
    void explicitRectUsesDirectDrawEdgeOrderWithoutYAxisCompensation() {
        ButtonVariable button = new ButtonVariable("BTN");
        ctx.setVariable("BTN", button);
        ctx.setAttribute("BTN", "RECT", "10,20,30,40");

        button.init(ctx);

        assertEquals(new CanvasRect(10, 20, 30, 40), button.getRect());
        assertTrue(button.getRect().contains(10, 20));
        assertFalse(button.getRect().contains(30, 20));
        assertFalse(button.getRect().contains(10, 40));
    }

    @Test
    void gfxBackedRectTracksImmutableBoundsReplacement() {
        ImageVariable standard = standardImage();
        ButtonVariable button = new ButtonVariable("BTN");
        ctx.setVariable("STD", standard);
        ctx.setVariable("BTN", button);
        ctx.setAttribute("BTN", "GFXSTANDARD", "STD");
        button.init(ctx);

        standard.state().rect = new CanvasRect(100, 120, 140, 160);

        assertEquals(new CanvasRect(100, 120, 140, 160), button.getRect());
    }

    @Test
    void setRectVariableKeepsUsingLiveCanonicalBounds() {
        ImageVariable standard = standardImage();
        ButtonVariable button = new ButtonVariable("BTN");
        ctx.setVariable("STD", standard);
        ctx.setVariable("BTN", button);

        MethodHelper.callWithContext(ctx, button, "SETRECT", new StringValue("STD"));
        standard.state().rect = new CanvasRect(5, 6, 25, 36);

        assertEquals(new CanvasRect(5, 6, 25, 36), button.getRect());
    }

    private static ImageVariable standardImage() {
        ImageVariable image = new ImageVariable("STD");
        image.state().rect = new CanvasRect(0, 0, 10, 10);
        image.state().visible = true;
        return image;
    }
}
