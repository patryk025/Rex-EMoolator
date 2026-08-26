package pl.genschu.bloomooemulator.engine.physics;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import pl.genschu.bloomooemulator.TestEnvironment;
import pl.genschu.bloomooemulator.geometry.coordinates.PhysicsPoint;
import pl.genschu.bloomooemulator.interpreter.variable.ImageVariable;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ODEPhysicsLinkTest {
    @BeforeAll
    static void boot() {
        TestEnvironment.init();
    }

    @Test
    void linkedPositionUpdateInvokesSpatialInvalidationCallback() {
        ODEPhysicsEngine physics = new ODEPhysicsEngine();
        physics.init();
        try {
            physics.setGravity(0.0, 0.0, 0.0);
            physics.createBody(
                    7, 1.0, 0.0, 0.0, 0.0, 0.0, 100.0,
                    1, 2, 1.0, 1.0, 1.0);
            physics.setPosition(7, new PhysicsPoint(50.0, -25.0, 0.0));

            ImageVariable image = new ImageVariable("BALL");
            AtomicInteger invalidations = new AtomicInteger();
            physics.linkVariable(image, 7, invalidations::incrementAndGet);

            physics.stepSimulation(0.001);

            assertEquals(450, image.getPosX());
            assertEquals(325, image.getPosY());
            assertEquals(1, invalidations.get());
        } finally {
            physics.shutdown();
        }
    }
}
