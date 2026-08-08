package pl.genschu.bloomooemulator.engine.time;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;

class QuantizedLegacyClockTest {
    @Test
    void preservesGlobalQuantumPhaseAndProducesFifteenSixteenMillisecondPattern() {
        AtomicLong nanos = new AtomicLong(7_000_000L);
        QuantizedLegacyClock clock = new QuantizedLegacyClock(
                nanos::get,
                LegacyClockProfile.WINDOWS_64_HZ
        );

        assertEquals(0L, clock.nowMillis());

        nanos.set(15_624_999L);
        assertEquals(0L, clock.nowMillis());

        nanos.set(15_625_000L);
        assertEquals(15L, clock.nowMillis());

        nanos.set(31_250_000L);
        assertEquals(31L, clock.nowMillis());

        nanos.set(46_875_000L);
        assertEquals(46L, clock.nowMillis());

        nanos.set(62_500_000L);
        assertEquals(62L, clock.nowMillis());
    }

    @Test
    void exposesMeasuredCompatibilityPresets() {
        assertEquals(15_625_000L, LegacyClockProfile.WINDOWS_64_HZ.quantumNanos());
        assertEquals(10_000_000L, LegacyClockProfile.WINDOWS_100_HZ.quantumNanos());
        assertEquals(5_000_000L, LegacyClockProfile.EMULATOR_200_HZ.quantumNanos());
        assertEquals(54_925_400L, LegacyClockProfile.WINDOWS_18_2_HZ.quantumNanos());
        assertEquals(1_000_000L, LegacyClockProfile.ONE_MILLISECOND.quantumNanos());
    }

    @Test
    void unknownStoredProfileFallsBackToSixtyFourHertz() {
        assertEquals(LegacyClockProfile.WINDOWS_64_HZ,
                LegacyClockProfile.fromStored("not-a-profile"));
    }

    @Test
    void sixtyHertzHostPulseAndSixtyFourHertzClockProduceThresholdSpecificCadence() {
        assertEquals(36, countDuePulsesAtSixtyHertz(0L, 16L));
        assertEquals(20, countDuePulsesAtSixtyHertz(0L, 33L));
        assertEquals(12, countDuePulsesAtSixtyHertz(0L, 66L));

        // The 16 ms threshold phase-locks differently when construction falls
        // three milliseconds into the global clock quantum.
        assertEquals(40, countDuePulsesAtSixtyHertz(3_000_000L, 16L));
        assertEquals(20, countDuePulsesAtSixtyHertz(3_000_000L, 33L));
        assertEquals(12, countDuePulsesAtSixtyHertz(3_000_000L, 66L));
    }

    private static int countDuePulsesAtSixtyHertz(long startNanos, long thresholdMs) {
        AtomicLong nanos = new AtomicLong(startNanos);
        QuantizedLegacyClock clock = new QuantizedLegacyClock(
                nanos::get,
                LegacyClockProfile.WINDOWS_64_HZ);
        long lastTickMs = clock.nowMillis();
        int due = 0;

        for (int hostFrame = 1; hostFrame <= 60; hostFrame++) {
            nanos.set(startNanos + hostFrame * 1_000_000_000L / 60L);
            long observed = clock.nowMillis();
            if (observed - lastTickMs >= thresholdMs) {
                due++;
                lastTickMs = clock.nowMillis();
            }
        }
        return due;
    }
}
