package pl.genschu.bloomooemulator.engine.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import pl.genschu.bloomooemulator.TestEnvironment;
import pl.genschu.bloomooemulator.builders.ContextBuilder;
import pl.genschu.bloomooemulator.engine.Game;
import pl.genschu.bloomooemulator.engine.config.EngineConfig;
import pl.genschu.bloomooemulator.engine.decision.states.ButtonState;
import pl.genschu.bloomooemulator.geometry.coordinates.CanvasRect;
import pl.genschu.bloomooemulator.interpreter.variable.ButtonVariable;
import pl.genschu.bloomooemulator.interpreter.variable.SceneVariable;
import pl.genschu.bloomooemulator.logic.GameEntry;
import pl.genschu.bloomooemulator.logic.MouseMode;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class QueuedPointerIntegrationTest {
    @BeforeAll static void boot() { TestEnvironment.init(); }

    @Test void fastTouchActivatesOnceAndClearsHover() { exercise(MouseMode.TOUCH, false, false); }
    @Test void mouseKeepsHoverAfterRelease() { exercise(MouseMode.PHYSICAL, false, false); }
    @Test void releaseOutsideDoesNotActivate() { exercise(MouseMode.TOUCH, true, false); }
    @Test void cancelledTouchDoesNotActivate() { exercise(MouseMode.TOUCH, false, true); }

    private void exercise(MouseMode mode, boolean outside, boolean cancel) {
        var previousInput = Gdx.input;
        var installed = new java.util.concurrent.atomic.AtomicReference<com.badlogic.gdx.InputProcessor>();
        Gdx.input = org.mockito.Mockito.mock(Input.class);
        org.mockito.Mockito.doAnswer(call -> {
            installed.set(call.getArgument(0));
            return null;
        }).when(Gdx.input).setInputProcessor(org.mockito.ArgumentMatchers.any());
        org.mockito.Mockito.when(Gdx.input.getInputProcessor()).thenAnswer(call -> installed.get());
        var previousGraphics = Gdx.graphics;
        var graphics = org.mockito.Mockito.mock(com.badlogic.gdx.Graphics.class);
        org.mockito.Mockito.when(graphics.getWidth()).thenReturn(800);
        org.mockito.Mockito.when(graphics.getHeight()).thenReturn(600);
        org.mockito.Mockito.when(graphics.getBackBufferWidth()).thenReturn(800);
        org.mockito.Mockito.when(graphics.getBackBufferHeight()).thenReturn(600);
        Gdx.graphics = graphics;
        GameEntry entry = new GameEntry();
        entry.setMouseMode(mode);
        var context = new ContextBuilder().build();
        Game game = new Game(entry, null);
        game.setCurrentSceneContext(context);
        game.setCurrentSceneVariable(new SceneVariable("SCENE"));
        context.setGame(game);
        var viewport = new StretchViewport(800, 600, new OrthographicCamera());
        viewport.update(800, 600, true);
        InputManager input = new InputManager(viewport, game, new EngineConfig());
        game.setInputManager(input);
        try {
            List<String> signals = new ArrayList<>();
            ButtonVariable button = new ButtonVariable("BTN");
            for (String signal : List.of("ONFOCUSON", "ONCLICKED", "ONRELEASED", "ONACTION", "ONFOCUSOFF")) {
                button = (ButtonVariable) button.withSignal(signal, (v, s, args) -> signals.add(signal));
            }
            context.setVariable("BTN", button);
            button.init(context);
            button.state().rect = new CanvasRect(10, 20, 110, 120);
            assertTrue(input.getCorrectedMouseCoords(50, 60).isPresent(), "coordinate conversion");
            var processor = Gdx.input.getInputProcessor();
            assertInstanceOf(com.badlogic.gdx.InputMultiplexer.class, processor);
            processor.touchDown(50, 60, 0, Input.Buttons.LEFT);
            if (cancel) processor.touchCancelled(50, 60, 0, Input.Buttons.LEFT);
            else processor.touchUp(outside ? 900 : 50, 60, 0, Input.Buttons.LEFT);
            input.processLegacyInput();
            assertEquals(1, signals.stream().filter("ONCLICKED"::equals).count(), "signals=" + signals + " state=" + button.getButtonState() + " pos=" + input.getMousePosition() + " rect=" + button.getRect());
            assertEquals(outside || cancel ? 0 : 1, signals.stream().filter("ONACTION"::equals).count());
            assertEquals(mode == MouseMode.PHYSICAL ? ButtonState.HOVERED : ButtonState.STANDARD, button.getButtonState());
            assertNull(input.getActiveButton());
            assertFalse(input.isMousePressed());
            int count = signals.size();
            input.processLegacyInput();
            assertEquals(count, signals.size());
        } finally {
            input.dispose();
            Gdx.input = previousInput;
            Gdx.graphics = previousGraphics;
        }
    }
}
