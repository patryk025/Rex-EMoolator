package pl.genschu.bloomooemulator.tests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.genschu.bloomooemulator.TestEnvironment;
import pl.genschu.bloomooemulator.builders.ContextBuilder;
import pl.genschu.bloomooemulator.engine.Game;
import pl.genschu.bloomooemulator.engine.input.DragManager;
import pl.genschu.bloomooemulator.interpreter.context.Context;
import pl.genschu.bloomooemulator.interpreter.variable.AnimoVariable;
import pl.genschu.bloomooemulator.interpreter.variable.ButtonVariable;
import pl.genschu.bloomooemulator.interpreter.variable.ImageVariable;
import pl.genschu.bloomooemulator.interpreter.variable.IntegerVariable;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DragManagerTest {
    private Context context;
    private Game game;
    private DragManager manager;

    @BeforeAll
    static void boot() {
        TestEnvironment.init();
    }

    @BeforeEach
    void setUp() {
        context = new ContextBuilder().build();
        game = mock(Game.class);
        when(game.getCurrentSceneContext()).thenReturn(context);
        manager = new DragManager(game);
    }

    @Test
    void dragsExplicitGraphicsAndRestoresPriorityWithSignalsInOrder() {
        ImageVariable standard = image("STD", 10, 20, 15);
        ImageVariable dragged = image("DRAG_IMAGE", 100, 200, 37);
        List<String> signals = new ArrayList<>();
        ButtonVariable button = draggingButton("BTN", "DRAG_IMAGE", "STD", null);
        button = (ButtonVariable) button
                .withSignal("ONSTARTDRAGGING", (variable, signal, args) -> signals.add(signal))
                .withSignal("ONDRAGGING", (variable, signal, args) -> signals.add(signal))
                .withSignal("ONENDDRAGGING", (variable, signal, args) -> signals.add(signal));
        context.setVariable("STD", standard);
        context.setVariable("DRAG_IMAGE", dragged);
        context.setVariable("BTN", button);

        assertTrue(manager.start(button, context, 20, 30));
        assertEquals("DRAG_IMAGE", manager.getDraggedName());
        assertEquals(10000, dragged.getPriority());

        manager.update(27, 34);

        assertEquals(107, dragged.getPosX());
        assertEquals(204, dragged.getPosY());

        manager.end(27, 34);

        assertFalse(manager.isDragging());
        assertEquals("", manager.getDraggedName());
        assertEquals(37, dragged.getPriority());
        assertEquals(List.of("ONSTARTDRAGGING", "ONDRAGGING", "ONENDDRAGGING"), signals);
    }

    @Test
    void fallsBackFromInvalidDragToStandardThenOnMove() {
        ImageVariable standard = image("STD", 0, 0, 5);
        ImageVariable hover = image("HOVER", 50, 60, 6);
        ButtonVariable button = draggingButton("BTN", "NOT_GRAPHICS", "STD", "HOVER");
        context.setVariable("NOT_GRAPHICS", new IntegerVariable("NOT_GRAPHICS", 1));
        context.setVariable("STD", standard);
        context.setVariable("HOVER", hover);

        assertTrue(manager.start(button, context, 0, 0));
        assertEquals("STD", manager.getDraggedName());
        manager.cancel();

        button.state().gfxStandardName = "MISSING";
        assertTrue(manager.start(button, context, 0, 0));
        assertEquals("HOVER", manager.getDraggedName());
    }

    @Test
    void sceneChangeCancelsWithoutEndSignalAndRestoresPriority() {
        ImageVariable dragged = image("STD", 0, 0, 42);
        List<String> signals = new ArrayList<>();
        ButtonVariable button = draggingButton("BTN", null, "STD", null);
        button = (ButtonVariable) button.withSignal(
                "ONENDDRAGGING", (variable, signal, args) -> signals.add(signal));
        context.setVariable("STD", dragged);

        assertTrue(manager.start(button, context, 0, 0));
        when(game.getCurrentSceneContext()).thenReturn(new ContextBuilder().build());

        manager.update(5, 5);

        assertFalse(manager.isDragging());
        assertEquals(42, dragged.getPriority());
        assertTrue(signals.isEmpty());
    }

    @Test
    void acceptsAnimoAsButtonDragTarget() {
        AnimoVariable animo = new AnimoVariable("ANIMO");
        animo.state().posX = 30;
        animo.state().posY = 40;
        animo.state().priority = 9;
        ButtonVariable button = draggingButton("BTN", "ANIMO", null, null);
        context.setVariable("ANIMO", animo);

        assertTrue(manager.start(button, context, 5, 5));
        manager.update(8, 11);
        manager.end(8, 11);

        assertEquals(33, animo.getPosX());
        assertEquals(46, animo.getPosY());
        assertEquals(9, animo.getPriority());
    }

    @Test
    void startSignalMayCancelDragSafely() {
        ImageVariable dragged = image("STD", 0, 0, 42);
        ButtonVariable button = draggingButton("BTN", null, "STD", null);
        button = (ButtonVariable) button.withSignal(
                "ONSTARTDRAGGING", (variable, signal, args) -> manager.cancel());
        context.setVariable("STD", dragged);

        assertFalse(manager.start(button, context, 0, 0));
        assertFalse(manager.isDragging());
        assertEquals(42, dragged.getPriority());
    }

    private static ButtonVariable draggingButton(String name, String drag, String standard, String hover) {
        ButtonVariable button = new ButtonVariable(name);
        button.state().draggable = true;
        button.state().dragName = drag;
        button.state().gfxStandardName = standard;
        button.state().gfxOnMoveName = hover;
        return button;
    }

    private static ImageVariable image(String name, int x, int y, int priority) {
        ImageVariable image = new ImageVariable(name);
        image.state().posX = x;
        image.state().posY = y;
        image.state().priority = priority;
        return image;
    }
}
