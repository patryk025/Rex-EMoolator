package pl.genschu.bloomooemulator.tests;

import org.junit.jupiter.api.Test;
import pl.genschu.bloomooemulator.engine.physics.IPhysicsEngine;
import pl.genschu.bloomooemulator.world.GameObject;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GameObjectDampingTest {
    @Test
    void postStepDampingUsesLinearOnlyVelocityWrite() {
        IPhysicsEngine physics = mock(IPhysicsEngine.class);
        when(physics.getPosition(7)).thenReturn(new double[]{0.0, 0.0, 0.0});
        when(physics.getSpeed(7)).thenReturn(new double[]{10.0, 0.0, 0.0});

        GameObject object = GameObject.builder()
                .id(7)
                .friction(2.0)
                .maxVelocity(100.0)
                .physicsEngine(physics)
                .build();
        object.setStepsize(0.5);

        object.updateObject();

        verify(physics).setDampedSpeed(7, 9.0, 0.0, 0.0);
        verify(physics, never()).setSpeed(7, 9.0, 0.0, 0.0);
    }
}
