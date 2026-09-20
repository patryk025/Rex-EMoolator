package pl.genschu.bloomooemulator.engine.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import pl.genschu.bloomooemulator.TestEnvironment;
import pl.genschu.bloomooemulator.builders.ContextBuilder;
import pl.genschu.bloomooemulator.builders.MethodHelper;
import pl.genschu.bloomooemulator.engine.Game;
import pl.genschu.bloomooemulator.engine.config.EngineConfig;
import pl.genschu.bloomooemulator.interpreter.values.IntValue;
import pl.genschu.bloomooemulator.interpreter.variable.MouseVariable;
import pl.genschu.bloomooemulator.interpreter.variable.SceneVariable;
import pl.genschu.bloomooemulator.logic.GameEntry;
import pl.genschu.bloomooemulator.logic.MouseMode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MouseRecenterIntegrationTest {
    @BeforeAll static void boot() { TestEnvironment.init(); }

    @Test void magicCountsEachQueuedMovementOnceAndStaysStillBetweenPulses() {
        exercise(MouseMode.PHYSICAL);
    }

    @Test void touchRecenteringPreservesMovementWithoutNativeWarp() {
        exercise(MouseMode.TOUCH);
    }

    private void exercise(MouseMode mode) {
        Input previousInput = Gdx.input;
        Graphics previousGraphics = Gdx.graphics;
        Input host = mock(Input.class);
        Graphics graphics = mock(Graphics.class);
        AtomicReference<InputProcessor> installed = new AtomicReference<>();
        doAnswer(call -> { installed.set(call.getArgument(0)); return null; })
                .when(host).setInputProcessor(any());
        when(host.getInputProcessor()).thenAnswer(call -> installed.get());
        when(graphics.getWidth()).thenReturn(1600);
        when(graphics.getHeight()).thenReturn(900);
        Gdx.input = host;
        Gdx.graphics = graphics;
        InputManager input = null;
        try {
            var context = new ContextBuilder().build();
            var entry = new GameEntry();
            entry.setMouseMode(mode);
            var game = new Game(entry, null);
            game.setCurrentSceneContext(context);
            game.setCurrentSceneVariable(new SceneVariable("MAGIC"));
            context.setGame(game);
            var viewport = new FitViewport(800, 600, new OrthographicCamera());
            viewport.update(1600, 900, true);
            input = new InputManager(viewport, game, new EngineConfig());
            game.setInputManager(input);
            // MAGIC integrates (GETPOSX - 400, GETPOSY - 300), then recentres.
            List<String> deltas = new ArrayList<>();
            List<String> clicks = new ArrayList<>();
            MouseVariable mouse = (MouseVariable) new MouseVariable("MOUSE").withSignal("ONMOVE", (v, s, args) -> {
                var m = (MouseVariable) v;
                int dx = m.getPosX() - 400;
                int dy = m.getPosY() - 300;
                if (dx != 0 || dy != 0) deltas.add(dx + "," + dy);
                MethodHelper.callWithContext(context, m, "SETPOSITION", new IntValue(400), new IntValue(300));
            });
            mouse = (MouseVariable) mouse.withSignal("ONCLICK", (v, s, args) -> clicks.add("down"));
            mouse = (MouseVariable) mouse.withSignal("ONRELEASE", (v, s, args) -> clicks.add("up"));
            context.setVariable("MOUSE", mouse);
            // Position host at center before installing the MAGIC movement handler.
            mouse.state().posX = 400;
            mouse.state().posY = 300;
            when(host.getX()).thenReturn(800);
            when(host.getY()).thenReturn(450);
            MethodHelper.callWithContext(context, mouse, "SETPOSITION", new IntValue(400), new IntValue(300));
            clearInvocations(host);
            var processor = installed.get();
            processor.touchDown(800, 450, 0, Input.Buttons.LEFT);
            processor.touchDragged(806, 453, 0);
            processor.touchDragged(815, 459, 0);
            processor.touchUp(815, 459, 0, Input.Buttons.LEFT);
            input.processLegacyInput();
            assertEquals(List.of("4,2", "6,4"), deltas);
            assertEquals(List.of("down", "up"), clicks);
            assertEquals(400, mouse.getPosX());
            assertEquals(300, mouse.getPosY());
            for (int i = 0; i < 10; i++) input.processLegacyInput();
            assertEquals(List.of("4,2", "6,4"), deltas, "idle pulses must not repeat movement");
            if (mode == MouseMode.PHYSICAL) {
                verify(host, times(1)).setCursorPosition(800, 450);
                processor.mouseMoved(800, 450); // Native warp notification.
                processor.mouseMoved(803, 447); // Next real movement.
                input.processLegacyInput();
                assertEquals(List.of("4,2", "6,4", "2,-2"), deltas);
                // An ordinary script warp also honors scaling, bars and Y direction.
                mouse.state().emitSignals = false;
                MethodHelper.callWithContext(context, mouse, "SETPOSITION", new IntValue(100), new IntValue(150));
                verify(host).setCursorPosition(350, 225);
                input.processLegacyInput();
                assertEquals(100, mouse.getPosX());
                assertEquals(150, mouse.getPosY());
            } else {
                // Later touch callbacks keep the virtual origin across pulses.
                processor.touchDown(815, 459, 0, Input.Buttons.LEFT);
                processor.touchDragged(818, 456, 0);
                input.processLegacyInput();
                assertEquals(List.of("4,2", "6,4", "2,-2"), deltas);
                verify(host, never()).setCursorPosition(anyInt(), anyInt());
            }
        } finally {
            if (input != null) input.dispose();
            Gdx.input = previousInput;
            Gdx.graphics = previousGraphics;
        }
    }
}
