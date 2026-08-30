package pl.genschu.bloomooemulator.engine.input;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import pl.genschu.bloomooemulator.TestEnvironment;
import pl.genschu.bloomooemulator.builders.ContextBuilder;
import pl.genschu.bloomooemulator.builders.MethodHelper;
import pl.genschu.bloomooemulator.engine.Game;
import pl.genschu.bloomooemulator.engine.config.EngineConfig;
import pl.genschu.bloomooemulator.geometry.coordinates.CanvasRect;
import pl.genschu.bloomooemulator.interpreter.context.Context;
import pl.genschu.bloomooemulator.interpreter.values.BoolValue;
import pl.genschu.bloomooemulator.interpreter.values.StringValue;
import pl.genschu.bloomooemulator.interpreter.variable.AnimoVariable;
import pl.genschu.bloomooemulator.interpreter.variable.ButtonVariable;
import pl.genschu.bloomooemulator.interpreter.variable.SceneVariable;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class ButtonHandlerSetStdTest {
    @BeforeAll
    static void boot() {
        TestEnvironment.init();
    }

    @Test
    void setAsButtonAnimoCanDelegateItsClickToButtonThroughSetStd() {
        Context context = new ContextBuilder().build();
        Game game = new Game(null, null);
        game.setCurrentSceneContext(context);
        game.setCurrentSceneVariable(new SceneVariable("SCENE"));
        context.setGame(game);

        InputManager inputManager = new InputManager(null, game, new EngineConfig());
        game.setInputManager(inputManager);
        ButtonHandler handler = new ButtonHandler(game, inputManager, false);

        AtomicInteger clicks = new AtomicInteger();
        ButtonVariable button = (ButtonVariable) new ButtonVariable("BTNDUMMY")
                .withSignal("ONCLICKED", (variable, signal, args) -> clicks.incrementAndGet());
        button.init(context);

        AnimoVariable mask = (AnimoVariable) new AnimoVariable("MASK")
                .withSignal("ONFOCUSON", (variable, signal, args) ->
                        MethodHelper.callWithContext(
                                context, button, "SETSTD",
                                new StringValue("MASK"), BoolValue.FALSE));
        mask.state().rect = new CanvasRect(10, 20, 110, 120);

        // Preserve the scene's declaration/runtime order: the ANIMO exists first,
        // BTNDUMMY is a regular BUTTON, and SETASBUTTON registers MASK afterwards.
        context.setVariable("MASK", mask);
        context.setVariable("BTNDUMMY", button);
        MethodHelper.callWithContext(
                context, mask, "SETASBUTTON", BoolValue.TRUE, BoolValue.TRUE);

        // Hovering MASK assigns it to BTNDUMMY through SETSTD.
        handler.handleMouseInput(50, 60, false, false, false, null, true);
        assertEquals(new CanvasRect(10, 20, 110, 120), button.getRect());

        // On the next input pass both objects share the same hit graphics and
        // BTNDUMMY takes focus, matching the original CHotSpot dispatch order.
        handler.handleMouseInput(50, 60, false, false, false, null, true);

        // The CButton-compatible hotspot must own the click, not the ANIMO listener.
        handler.handleMouseInput(50, 60, true, true, false, null, true);

        assertSame(button, inputManager.getActiveButton());
        assertEquals(1, clicks.get());
    }
}
