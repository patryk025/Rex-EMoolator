package pl.genschu.bloomooemulator.interpreter.variable;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import pl.genschu.bloomooemulator.TestEnvironment;
import pl.genschu.bloomooemulator.builders.ContextBuilder;
import pl.genschu.bloomooemulator.engine.Game;
import pl.genschu.bloomooemulator.engine.compatibility.CompatibilityProfile;
import pl.genschu.bloomooemulator.engine.compatibility.EngineVariant;
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
import pl.genschu.bloomooemulator.interpreter.values.VariableValue;
import pl.genschu.bloomooemulator.logic.GameFamilies;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
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

        // Six arguments: the seventh defaults to TRUE, i.e. a target seen through the camera.
        world.callMethod("FINDPATH",
                new IntValue(7), new IntValue(9),
                new IntValue(450), new IntValue(275), new IntValue(8),
                new BoolValue(false));

        verify(physics).findPath(7, 9, new PhysicsPoint(80.0, 45.0, 8.0), false);
    }

    @Test
    void findPathWithViewportFlagOffUsesFixedCanvasAndIgnoresCameraScroll() {
        when(physics.getCanvasScroll()).thenReturn(new CanvasScroll(30.0, -20.0));

        world.callMethod("FINDPATH",
                new IntValue(7), new IntValue(9),
                new DoubleValue(450.5), new IntValue(275), new IntValue(8),
                new BoolValue(true), new BoolValue(false));

        verify(physics).findPath(7, 9, new PhysicsPoint(50.5, 25.0, 8.0), true);
    }

    @Test
    void findPathAppendsExactTargetByDefault() {
        when(physics.getCanvasScroll()).thenReturn(CanvasScroll.NONE);

        world.callMethod("FINDPATH",
                new IntValue(7), new IntValue(9),
                new IntValue(400), new IntValue(300), new IntValue(0));

        verify(physics).findPath(7, 9, new PhysicsPoint(0.0, 0.0, 0.0), true);
    }

    @Test
    void setActiveWithIntegerSecondArgumentSwitchesPathNodes() {
        world.callMethod("SETACTIVE",
                new IntValue(9000), new IntValue(10), new BoolValue(false));

        verify(physics).setActivePath(9000, 10, false);
        verify(physics, never()).setActive(anyInt(), anyBoolean(), anyBoolean());
    }

    @Test
    void setActiveWithIntegerVariableAsSecondArgumentSwitchesPathNodes() {
        IntegerVariable tag = new IntegerVariable("VARITER", 20);

        world.callMethod("SETACTIVE",
                new IntValue(9000), new VariableValue(tag), new BoolValue(true));

        verify(physics).setActivePath(9000, 20, true);
    }

    @Test
    void setActiveWithBoolArgumentsSwitchesTheBody() {
        world.callMethod("SETACTIVE",
                new IntValue(7), new BoolValue(false), new BoolValue(false));
        verify(physics).setActive(7, false, false);

        // Two arguments leave collision reporting on.
        world.callMethod("SETACTIVE", new IntValue(8), new BoolValue(false));
        verify(physics).setActive(8, false, true);
    }

    @Test
    void reksioICzarodziejeFallsThroughToEnablingTheObjectAfterThreeArgumentSetActive() {
        Game game = mock(Game.class);
        when(game.getCompatibilityProfile()).thenReturn(
                new CompatibilityProfile(EngineVariant.PIKLIB_8, GameFamilies.REKSIO_CZARODZIEJE));
        world.state().gameRef = game;

        world.callMethod("SETACTIVE",
                new IntValue(9000), new IntValue(10), new BoolValue(false));

        InOrder order = inOrder(physics);
        order.verify(physics).setActivePath(9000, 10, false);
        order.verify(physics).setActive(9000, true, true);
    }

    @Test
    void getAngleReturnsTruncatedIntegerDegreesAndKeepsTheSideOfAStoppedBody() {
        when(physics.getAngle(7)).thenReturn(Math.toRadians(44.9));
        assertEquals(new IntValue(44), world.callMethod("GETANGLE", new IntValue(7)).returnValue());

        when(physics.getAngle(7)).thenReturn(Math.toRadians(-90.0));
        assertEquals(new IntValue(270), world.callMethod("GETANGLE", new IntValue(7)).returnValue());

        // Velocity scaled to zero keeps its signs: atan2(±0, -0) = ±pi, atan2(-0, +0) = -0.
        // +pi reads 179 because Sekai's 32-bit 180/pi is slightly short of the real value.
        when(physics.getAngle(7)).thenReturn(Math.atan2(0.0, -0.0));
        assertEquals(new IntValue(179), world.callMethod("GETANGLE", new IntValue(7)).returnValue());

        when(physics.getAngle(7)).thenReturn(Math.atan2(-0.0, -0.0));
        assertEquals(new IntValue(180), world.callMethod("GETANGLE", new IntValue(7)).returnValue());

        when(physics.getAngle(7)).thenReturn(Math.atan2(-0.0, 0.0));
        assertEquals(new IntValue(0), world.callMethod("GETANGLE", new IntValue(7)).returnValue());
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
