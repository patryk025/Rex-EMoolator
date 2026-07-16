package pl.genschu.bloomooemulator.engine.physics;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ODEPhysicsTimerTest {
    @Test
    void resumeSnapshotsClockForFirstPhysicsStep() {
        AtomicLong nanos = new AtomicLong(1_000_000_000L);
        AtomicLong millis = new AtomicLong(1_000L);
        ODEPhysicsTimer timer = new ODEPhysicsTimer(nanos::get, millis::get);

        timer.resume();
        nanos.addAndGet(34_100L);

        assertEquals(0.0000341, timer.calculateStepSize(), 1e-12);
    }

    @Test
    void repeatedResumeDoesNotDiscardElapsedTime() {
        AtomicLong nanos = new AtomicLong(1_000_000_000L);
        AtomicLong millis = new AtomicLong(1_000L);
        ODEPhysicsTimer timer = new ODEPhysicsTimer(nanos::get, millis::get);

        timer.resume();
        nanos.addAndGet(5_000_000L);
        timer.resume();

        assertEquals(0.005, timer.calculateStepSize(), 1e-12);
    }
}
