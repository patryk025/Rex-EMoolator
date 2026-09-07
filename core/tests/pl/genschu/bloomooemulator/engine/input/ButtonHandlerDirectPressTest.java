package pl.genschu.bloomooemulator.engine.input;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import pl.genschu.bloomooemulator.TestEnvironment;
import pl.genschu.bloomooemulator.builders.ContextBuilder;
import pl.genschu.bloomooemulator.engine.Game;
import pl.genschu.bloomooemulator.engine.config.EngineConfig;
import pl.genschu.bloomooemulator.engine.decision.states.ButtonState;
import pl.genschu.bloomooemulator.geometry.coordinates.CanvasRect;
import pl.genschu.bloomooemulator.interpreter.context.Context;
import pl.genschu.bloomooemulator.interpreter.variable.ButtonVariable;
import pl.genschu.bloomooemulator.interpreter.variable.SceneVariable;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class ButtonHandlerDirectPressTest {
    @BeforeAll
    static void boot() {
        TestEnvironment.init();
    }

    @Test
    void firstPressOverStandardButtonFocusesAndClicksWithoutPriorHoverPass() {
        Context context = new ContextBuilder().build();

        Game game = new Game(null, null);
        game.setCurrentSceneContext(context);
        game.setCurrentSceneVariable(new SceneVariable("SCENE"));
        context.setGame(game);

        InputManager inputManager =
                new InputManager(null, game, new EngineConfig());
        game.setInputManager(inputManager);

        ButtonHandler handler =
                new ButtonHandler(game, inputManager, false);

        List<String> signals = new ArrayList<>();

        ButtonVariable button =
                (ButtonVariable) new ButtonVariable("BTN")
                        .withSignal("ONFOCUSON",
                                (variable, signal, args) ->
                                        signals.add("ONFOCUSON"))
                        .withSignal("ONCLICKED",
                                (variable, signal, args) ->
                                        signals.add("ONCLICKED"))
                        .withSignal("ONRELEASED",
                                (variable, signal, args) ->
                                        signals.add("ONRELEASED"))
                        .withSignal("ONACTION",
                                (variable, signal, args) ->
                                        signals.add("ONACTION"));

        context.setVariable("BTN", button);
        button.init(context);
        button.state().rect = new CanvasRect(10, 20, 110, 120);

        assertEquals(ButtonState.STANDARD, button.getButtonState());

        // Pierwszym zdarzeniem nad przyciskiem jest od razu DOWN.
        // Celowo brak wcześniejszego wywołania symulującego hover.
        handler.handleMouseInput(
                50, 60,
                true,   // isPressed
                true,   // justPressed
                false,  // justReleased
                null,
                true
        );

        assertEquals(
                List.of("ONFOCUSON", "ONCLICKED"),
                signals
        );
        assertEquals(ButtonState.PRESSED, button.getButtonState());
        assertSame(button, inputManager.getActiveButton());

        handler.handleMouseInput(
                50, 60,
                false,  // isPressed
                false,  // justPressed
                true,   // justReleased
                null,
                true
        );

        assertEquals(
                List.of(
                        "ONFOCUSON",
                        "ONCLICKED",
                        "ONRELEASED",
                        "ONACTION"
                ),
                signals
        );
        assertEquals(ButtonState.HOVERED, button.getButtonState());
        assertNull(inputManager.getActiveButton());
    }
}