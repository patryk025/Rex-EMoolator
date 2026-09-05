package pl.genschu.bloomooemulator.engine.input;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import pl.genschu.bloomooemulator.TestEnvironment;
import pl.genschu.bloomooemulator.builders.MethodHelper;
import pl.genschu.bloomooemulator.engine.Game;
import pl.genschu.bloomooemulator.engine.config.EngineConfig;
import pl.genschu.bloomooemulator.geometry.coordinates.CanvasRect;
import pl.genschu.bloomooemulator.interpreter.context.Context;
import pl.genschu.bloomooemulator.interpreter.runtime.ExecutionContext;
import pl.genschu.bloomooemulator.interpreter.values.IntValue;
import pl.genschu.bloomooemulator.interpreter.variable.*;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SceneHotspotPriorityTest {
    @BeforeAll static void boot() { TestEnvironment.init(); }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void parentSceneSettersBlockUnderlyingMenuButKeepDialogInteractive(boolean menuHasGraphic) {
        Game game = new Game(null, null);
        Context episode = new Context(new ExecutionContext(), null, game);
        Context scene = new Context(new ExecutionContext(), episode, game);
        SceneVariable original = new SceneVariable("MENUGLOWNE");
        episode.setVariable(original.name(), original);
        game.setCurrentSceneContext(scene);
        game.setCurrentSceneVariable(original);
        InputManager input = new InputManager(null, game, new EngineConfig());
        game.setInputManager(input);
        ButtonHandler handler = new ButtonHandler(game, input, false);
        AtomicInteger menuFocus = new AtomicInteger();
        AtomicInteger menuClicks = new AtomicInteger();
        AtomicInteger dialogClicks = new AtomicInteger();
        AtomicInteger pauseClicks = new AtomicInteger();
        addButton(scene, "MENU", 100, new CanvasRect(10, 10, 100, 100), menuFocus, menuClicks);
        if (!menuHasGraphic) {
            ((ButtonVariable) scene.getVariable("MENU")).state().gfxStandardName = null;
        }
        addButton(episode, "DIALOG", 5000, new CanvasRect(200, 10, 300, 100), new AtomicInteger(), dialogClicks);
        addButton(episode, "B_GLOBAL_PAUSE", 0, new CanvasRect(400, 10, 500, 100), new AtomicInteger(), pauseClicks);
        ButtonVariable pauseButton = (ButtonVariable) episode.getVariable("B_GLOBAL_PAUSE");
        pauseButton.state().gfxStandardName = null;
        MethodHelper.callWithContext(episode, pauseButton, "SETPRIORITY", new IntValue(4001));
        assertEquals(4001, pauseButton.state().copy().hotspotPriority);

        handler.handleMouseInput(50, 50, false, false, false, null, true);
        assertEquals(1, menuFocus.get());
        handler.handleMouseInput(700, 500, false, false, false, null, true);
        MethodHelper.callWithContext(episode, original.name(), "SETMINHSPRIORITY", new IntValue(4000));
        assertEquals(4000, game.getCurrentSceneVariable().minHotSpotZ());
        handler.handleMouseInput(50, 50, false, false, false, null, true);
        handler.handleMouseInput(50, 50, true, true, false, null, true);
        assertEquals(1, menuFocus.get(), "menu hover must not restart its animation under the dialog");
        assertEquals(0, menuClicks.get());
        handler.handleMouseInput(250, 50, false, false, true, null, true);
        handler.handleMouseInput(250, 50, true, true, false, null, true);
        assertEquals(1, dialogClicks.get());
        handler.handleMouseInput(700, 500, false, false, true, null, true);
        handler.handleMouseInput(450, 50, false, false, false, null, true);
        handler.handleMouseInput(450, 50, true, true, false, null, true);
        assertEquals(1, pauseClicks.get(), "RECT-only pause overlay must honor SETPRIORITY");
        handler.handleMouseInput(700, 500, false, false, true, null, true);

        MethodHelper.callWithContext(episode, original.name(), "SETMAXHSPRIORITY", new IntValue(4500));
        handler.handleMouseInput(250, 50, true, true, false, null, true);
        assertEquals(1, dialogClicks.get(), "upper bound must also use the updated scene record");
        handler.handleMouseInput(700, 500, false, false, true, null, true);
        MethodHelper.callWithContext(episode, original.name(), "SETMINHSPRIORITY", new IntValue(0));
        MethodHelper.callWithContext(episode, original.name(), "SETMAXHSPRIORITY", new IntValue(99999));
        handler.handleMouseInput(50, 50, false, false, false, null, true);
        handler.handleMouseInput(50, 50, true, true, false, null, true);
        assertEquals(2, menuFocus.get());
        assertEquals(1, menuClicks.get());
    }

    private static void addButton(Context owner, String name, int priority, CanvasRect rect,
                                  AtomicInteger focus, AtomicInteger clicks) {
        ImageVariable gfx = new ImageVariable(name + "_GFX");
        gfx.state().priority = priority;
        gfx.state().rect = rect;
        owner.setVariable(gfx.name(), gfx);
        ButtonVariable button = (ButtonVariable) new ButtonVariable(name)
                .withSignal("ONFOCUSON", (v, s, a) -> focus.incrementAndGet())
                .withSignal("ONCLICKED", (v, s, a) -> clicks.incrementAndGet());
        button.init(owner);
        button.state().gfxStandardName = gfx.name();
        button.state().rect = rect;
        button.state().rectFollowsStandard = false;
        owner.setVariable(name, button);
    }
}
