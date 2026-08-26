package pl.genschu.bloomooemulator.engine.physics.camera;

import org.junit.jupiter.api.Test;
import pl.genschu.bloomooemulator.geometry.coordinates.CanvasPoint;
import pl.genschu.bloomooemulator.geometry.coordinates.CanvasScroll;
import pl.genschu.bloomooemulator.geometry.coordinates.PhysicsPoint;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CameraAnchorTest {
    @Test
    void typedFixedCanvasAndPhysicsConversionsRoundTrip() {
        CameraAnchor camera = new CameraAnchor();
        PhysicsPoint physicsPoint = new PhysicsPoint(-125.5, 80.25, 7.0);

        CanvasPoint canvasPoint = camera.physicsToCanvas(physicsPoint);

        assertEquals(new CanvasPoint(274.5, 219.75), canvasPoint);
        assertEquals(physicsPoint, camera.canvasToPhysics(canvasPoint, physicsPoint.z()));
        assertEquals(CanvasScroll.NONE, camera.canvasScroll());
    }

    @Test
    void typedConversionsApplyCameraScrollExactlyOnce() {
        CameraAnchor camera = new CameraAnchor();
        camera.setLimits(-200.0f, 1_000.0f, -150.0f, 750.0f);
        camera.updateCameraAnchor(new PhysicsPoint(75.0, -40.0, 3.0));

        PhysicsPoint physicsPoint = new PhysicsPoint(-20.0, 30.0, 9.0);
        CanvasPoint canvasPoint = camera.physicsToCanvas(physicsPoint);

        assertEquals(new CanvasScroll(75.0, 40.0), camera.canvasScroll());
        assertEquals(new CanvasPoint(305.0, 230.0), canvasPoint);
        assertEquals(physicsPoint, camera.canvasToPhysics(canvasPoint, physicsPoint.z()));
    }
}
