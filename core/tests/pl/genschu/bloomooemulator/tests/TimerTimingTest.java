package pl.genschu.bloomooemulator.tests;

import org.junit.jupiter.api.Test;
import pl.genschu.bloomooemulator.engine.time.LegacyClock;
import pl.genschu.bloomooemulator.interpreter.variable.TimerVariable;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TimerTimingTest {
    @Test
    void storesClockSampleTakenAfterCallback() {
        AtomicLong now = new AtomicLong(50L);
        LegacyClock clock = now::get;
        TimerVariable timer = new TimerVariable("TIMER", 50L, true, 0, 0L, 0, Map.of());
        timer = (TimerVariable) timer.withSignal("ONTICK^1", (variable, signal, args) -> now.set(83L));

        timer.update(clock);

        assertEquals(83L, timer.lastTickTime());
        assertEquals(1, timer.currentTickCount());
    }

    @Test
    void finiteTimerDisablesBeforeFinalHandlerAndCanBeEnabledAgain() {
        AtomicBoolean observedDisabled = new AtomicBoolean(false);
        TimerVariable timer = new TimerVariable("TIMER", 10L, true, 1, 0L, 0, Map.of());
        timer = (TimerVariable) timer.withSignal("ONTICK^1", (variable, signal, args) -> {
            TimerVariable firingTimer = (TimerVariable) variable;
            observedDisabled.set(!firingTimer.enabled());
            firingTimer.callMethod("ENABLE");
        });

        timer.update(() -> 10L);

        assertTrue(observedDisabled.get());
        assertTrue(timer.enabled());
        assertEquals(0, timer.currentTickCount(), "ENABLE restarts the finite timer");
        assertEquals(10L, timer.lastTickTime());
    }

    @Test
    void zeroIntervalFiresOncePerPulse() {
        TimerVariable timer = new TimerVariable("TIMER", 0L, true, 0, 0L, 0, Map.of());

        timer.update(() -> 0L);
        timer.update(() -> 0L);

        assertEquals(2, timer.currentTickCount());
    }

    @Test
    void longStallDoesNotReplayBacklogInsideOnePulse() {
        TimerVariable timer = new TimerVariable("TIMER", 10L, true, 0, 0L, 0, Map.of());

        TimerVariable.UpdateResult result = timer.update(() -> 500L);

        assertTrue(result.tickOccurred());
        assertEquals(1, timer.currentTickCount());
        assertFalse(timer.update(() -> 500L).tickOccurred());
    }
}
