package pl.genschu.bloomooemulator.tests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.genschu.bloomooemulator.TestEnvironment;
import pl.genschu.bloomooemulator.builders.ContextBuilder;
import pl.genschu.bloomooemulator.builders.MethodHelper;
import pl.genschu.bloomooemulator.engine.Game;
import pl.genschu.bloomooemulator.geometry.coordinates.CanvasRect;
import pl.genschu.bloomooemulator.interpreter.context.Context;
import pl.genschu.bloomooemulator.interpreter.values.BoolValue;
import pl.genschu.bloomooemulator.interpreter.values.IntValue;
import pl.genschu.bloomooemulator.interpreter.values.NullValue;
import pl.genschu.bloomooemulator.interpreter.values.Value;
import pl.genschu.bloomooemulator.interpreter.variable.CanvasObserverVariable;
import pl.genschu.bloomooemulator.interpreter.variable.ImageVariable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CanvasObserverVariableTest {
    private Context context;
    private CanvasObserverVariable observer;

    @BeforeAll
    static void boot() {
        TestEnvironment.init();
    }

    @BeforeEach
    void setUp() {
        context = new ContextBuilder().build();
        Game game = mock(Game.class);
        context.setGame(game);
        when(game.getCurrentSceneContext()).thenReturn(context);
        observer = new CanvasObserverVariable("CANVAS");
        context.setVariable("CANVAS", observer);
    }

    @Test
    void getGraphicsAtUsesCanonicalHalfOpenBounds() {
        ImageVariable image = new ImageVariable("IMAGE");
        image.state().rect = new CanvasRect(10, 20, 30, 40);
        context.setVariable("IMAGE", image);

        assertEquals("IMAGE", graphicsAt(10, 20).toStringValue().value());
        assertSame(NullValue.INSTANCE, graphicsAt(30, 20));
        assertSame(NullValue.INSTANCE, graphicsAt(10, 40));
    }

    private Value graphicsAt(int x, int y) {
        return MethodHelper.callWithContext(
                context,
                observer,
                "GETGRAPHICSAT",
                new IntValue(x),
                new IntValue(y),
                BoolValue.FALSE,
                new IntValue(Integer.MIN_VALUE),
                new IntValue(Integer.MAX_VALUE),
                BoolValue.TRUE);
    }
}
