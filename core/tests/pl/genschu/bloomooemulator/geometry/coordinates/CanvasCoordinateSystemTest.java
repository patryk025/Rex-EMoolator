package pl.genschu.bloomooemulator.geometry.coordinates;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CanvasCoordinateSystemTest {
    @Test
    void convertsCanvasPointsToBottomLeftOpenGlSpace() {
        assertEquals(new OpenGlPoint(0, 600),
                CanvasCoordinateSystem.toOpenGl(new CanvasPoint(0, 0)));
        assertEquals(new OpenGlPoint(800, 0),
                CanvasCoordinateSystem.toOpenGl(new CanvasPoint(800, 600)));
    }

    @Test
    void convertsWholeRectInsteadOfMakingCallersFlipYAxis() {
        CanvasRect canvas = new CanvasRect(10, 20, 110, 70);

        assertEquals(new OpenGlRect(10, 530, 100, 50),
                CanvasCoordinateSystem.toOpenGl(canvas));
    }

    @Test
    void convertsClockwiseCanvasRotationAtTheRendererBoundary() {
        assertEquals(-90.0, CanvasCoordinateSystem.toOpenGlRotationDegrees(90.0));
    }

    @Test
    void canvasCentreIsTheNativePhysicsOrigin() {
        assertEquals(new PhysicsPoint(0, 0, 7),
                CanvasCoordinateSystem.toPhysics(new CanvasPoint(400, 300), 7));
        assertEquals(new CanvasPoint(400, 300),
                CanvasCoordinateSystem.fromPhysics(new PhysicsPoint(0, 0, 7)));
    }

    @Test
    void physicsCanvasRoundTripIncludesCameraScrollExactlyOnce() {
        CanvasScroll scroll = new CanvasScroll(25, -15);
        PhysicsPoint world = new PhysicsPoint(120, -40, 3);
        CanvasPoint canvas = CanvasCoordinateSystem.fromPhysics(world, scroll);

        assertEquals(world, CanvasCoordinateSystem.toPhysics(canvas, 3, scroll));
    }

    @Test
    void canvasBoundsBecomeNormalizedYUpPhysicsBounds() {
        PhysicsBox box = CanvasCoordinateSystem.toPhysics(
                new CanvasRect(100, 150, 300, 250), -2, 4);

        assertEquals(new PhysicsPoint(-300, 50, -2), box.min());
        assertEquals(new PhysicsPoint(-100, 150, 4), box.max());
    }

    @Test
    void continuousCanvasBoundsKeepFractionalWorldLimits() {
        PhysicsBox box = CanvasCoordinateSystem.toPhysics(
                100.25, 150.5, 300.75, 250.125, -2.5, 4.75);

        assertEquals(new PhysicsPoint(-299.75, 49.875, -2.5), box.min());
        assertEquals(new PhysicsPoint(-99.25, 149.5, 4.75), box.max());
    }
}
