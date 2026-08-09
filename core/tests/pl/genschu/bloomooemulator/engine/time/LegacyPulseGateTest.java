package pl.genschu.bloomooemulator.engine.time;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LegacyPulseGateTest {
    @Test
    void capsHighRefreshRenderLoopsAtSixtyPulsesPerSecond() {
        assertEquals(60, countPulses(60));
        assertEquals(60, countPulses(120));
        assertEquals(60, countPulses(144));
    }

    @Test
    void dropsAStallInsteadOfReplayingItsBacklog() {
        AtomicLong nanos = new AtomicLong();
        LegacyPulseGate gate = new LegacyPulseGate(nanos::get, 60);

        assertTrue(gate.tryAcquirePulse());
        nanos.set(500_000_000L);
        LegacyPulseGate.PulseDecision stalled = gate.poll();
        assertTrue(stalled.admitted());
        assertEquals(29, stalled.missedPeriods());
        assertEquals(483_333_334L, stalled.latenessNanos());
        nanos.set(501_000_000L);
        assertFalse(gate.tryAcquirePulse());
        nanos.set(516_666_667L);
        assertTrue(gate.tryAcquirePulse());
    }

    private static int countPulses(int renderHz) {
        AtomicLong nanos = new AtomicLong();
        LegacyPulseGate gate = new LegacyPulseGate(nanos::get, 60);
        int pulses = 0;
        for (int frame = 0; frame < renderHz; frame++) {
            nanos.set(frame * 1_000_000_000L / renderHz);
            if (gate.tryAcquirePulse()) {
                pulses++;
            }
        }
        return pulses;
    }
}
