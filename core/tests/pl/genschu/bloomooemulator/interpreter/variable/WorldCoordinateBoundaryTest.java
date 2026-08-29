package pl.genschu.bloomooemulator.interpreter.variable;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import pl.genschu.bloomooemulator.TestEnvironment;
import pl.genschu.bloomooemulator.builders.ContextBuilder;
import pl.genschu.bloomooemulator.engine.Game;
import pl.genschu.bloomooemulator.engine.physics.IPhysicsEngine;
import pl.genschu.bloomooemulator.geometry.coordinates.CanvasScroll;
import pl.genschu.bloomooemulator.geometry.coordinates.PhysicsBox;
import pl.genschu.bloomooemulator.geometry.coordinates.PhysicsPoint;
import pl.genschu.bloomooemulator.interpreter.context.Context;
import pl.genschu.bloomooemulator.interpreter.runtime.ASTInterpreter;
import pl.genschu.bloomooemulator.interpreter.values.BoolValue;
import pl.genschu.bloomooemulator.interpreter.values.DoubleValue;
import pl.genschu.bloomooemulator.interpreter.values.IntValue;
import pl.genschu.bloomooemulator.interpreter.values.StringValue;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class WorldCoordinateBoundaryTest {
    private IPhysicsEngine physics;
    private WorldVariable world;

    @BeforeAll
    static void boot() {
        TestEnvironment.init();
    }

    @BeforeEach
    void setUp() {
        physics = mock(IPhysicsEngine.class);
        world = new WorldVariable(
                "WORLD",
                new WorldVariable.WorldState(physics),
                Map.of());
    }

    @Test
    void positionSettersUseFixedCanvasAndIgnoreCameraScroll() {
        when(physics.getCanvasScroll()).thenReturn(new CanvasScroll(90.0, -30.0));

        world.callMethod("SETPOSITION",
                new IntValue(7),
                new DoubleValue(125.5),
                new DoubleValue(450.25),
                new DoubleValue(9.0));
        world.callMethod("ADDOBJECT",
                new IntValue(8),
                new DoubleValue(700.0), new DoubleValue(50.0), new DoubleValue(-2.0),
                new DoubleValue(1.0), new DoubleValue(2.0), new DoubleValue(3.0),
                new DoubleValue(4.0), new IntValue(0), new DoubleValue(5.0));

        verify(physics).setPosition(7, new PhysicsPoint(-274.5, -150.25, 9.0));
        verify(physics).setPosition(8, new PhysicsPoint(300.0, 250.0, -2.0));
    }

    @Test
    void setPositionCoordChangesOneFixedCanvasAxisOnly() {
        when(physics.getPhysicsPosition(7)).thenReturn(new PhysicsPoint(10.0, 20.0, 30.0));

        world.callMethod("SETPOSITIONCOORD",
                new IntValue(7), new IntValue(0), new DoubleValue(125.0));
        world.callMethod("SETPOSITIONCOORD",
                new IntValue(7), new IntValue(1), new DoubleValue(455.0));

        verify(physics).setPosition(7, new PhysicsPoint(-275.0, 20.0, 30.0));
        verify(physics).setPosition(7, new PhysicsPoint(10.0, -155.0, 30.0));
    }

    @Test
    void gettersProjectPhysicsThroughCurrentCameraScroll() {
        when(physics.getPhysicsPosition(7)).thenReturn(new PhysicsPoint(20.0, -10.0, 5.0));
        when(physics.getCanvasScroll()).thenReturn(new CanvasScroll(30.0, -25.0));

        assertEquals(390.0, returnedDouble("GETPOSITIONX", 7));
        assertEquals(335.0, returnedDouble("GETPOSITIONY", 7));
        assertEquals(5.0, returnedDouble("GETPOSITIONZ", 7));
    }

    @Test
    void setLimitNormalizesCanvasYIntoTypedPhysicsBoundsWithoutTruncation() {
        world.callMethod("SETLIMIT",
                new IntValue(7),
                new DoubleValue(10.5), new DoubleValue(20.25), new DoubleValue(-2.0),
                new DoubleValue(700.75), new DoubleValue(580.5), new DoubleValue(4.0));

        verify(physics).setLimit(7, new PhysicsBox(
                new PhysicsPoint(-389.5, -280.5, -2.0),
                new PhysicsPoint(300.75, 279.75, 4.0)));
    }

    @Test
    void joinUsesFixedCanvasButJoin2AndVectorsStayInWorldSpace() {
        world.callMethod("JOIN",
                new IntValue(1), new IntValue(2),
                new DoubleValue(425.5), new DoubleValue(310.25), new DoubleValue(7.5),
                new DoubleValue(100.0));

        ArgumentCaptor<PhysicsPoint> anchor = ArgumentCaptor.forClass(PhysicsPoint.class);
        verify(physics).addJoint(
                eq(1), eq(2), anchor.capture(),
                eq(100.0), anyDouble(), anyDouble(),
                eq(0.0), eq(1.0), eq(0.0));
        assertEquals(new PhysicsPoint(25.5, -10.25, 7.5), anchor.getValue());

        world.callMethod("JOIN2", List.of(
                new IntValue(1), new IntValue(2),
                new DoubleValue(1), new DoubleValue(2), new DoubleValue(3),
                new DoubleValue(4), new DoubleValue(5), new DoubleValue(6),
                new DoubleValue(7), new DoubleValue(8), new DoubleValue(9)));
        verify(physics).addJoint2(1, 2, 1, 2, 3, 4, 6, 5, 7, 9, 8);

        world.callMethod("SETVELOCITY",
                new IntValue(7), new DoubleValue(3), new DoubleValue(-4), new DoubleValue(5));
        world.callMethod("ADDFORCE",
                new IntValue(7), new DoubleValue(6), new DoubleValue(-7), new DoubleValue(8));
        verify(physics).setSpeed(7, 3, -4, 5);
        verify(physics).addForce(7, 6, -7, 8);
    }

    @Test
    void findPathConvertsViewportTargetWithCurrentScrollBeforeCallingPhysics() {
        when(physics.getCanvasScroll()).thenReturn(new CanvasScroll(30.0, -20.0));

        world.callMethod("FINDPATH",
                new IntValue(7), new IntValue(9),
                new IntValue(450), new IntValue(275), new IntValue(8),
                new BoolValue(true), new BoolValue(false));

        verify(physics).findPath(
                7, 9, new PhysicsPoint(80.0, 45.0, 8.0), true, false);
    }

    @Test
    void linkRegistersCollisionInvalidationAfterPhysicsDrivenPositionUpdate() {
        Game game = mock(Game.class);
        ImageVariable image = new ImageVariable("BALL");
        Context context = new ContextBuilder().withVariable(image).build();
        context.setGame(game);
        MethodContext methodContext = new ASTInterpreter(context).getMethodContext();

        world.callMethod("LINK", List.of(new IntValue(7), new StringValue("BALL")), methodContext);

        ArgumentCaptor<Runnable> invalidation = ArgumentCaptor.forClass(Runnable.class);
        verify(physics).linkVariable(eq(image), eq(7), invalidation.capture());
        invalidation.getValue().run();
        verify(game).markCollisionDirty(image);
    }

    private double returnedDouble(String method, int objectId) {
        return world.callMethod(method, new IntValue(objectId))
                .returnValue()
                .toDouble()
                .value();
    }
}
