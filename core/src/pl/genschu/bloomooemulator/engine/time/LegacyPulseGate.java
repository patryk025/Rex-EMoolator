package pl.genschu.bloomooemulator.engine.time;

import java.util.Objects;
import java.util.function.LongSupplier;

/**
 * Limits legacy-manager polling independently of the graphics backend.
 *
 * <p>Some backends ignore {@code Graphics#setForegroundFPS}. This gate still
 * allows at most one manager pulse per host frame and deliberately discards
 * missed deadlines after a stall; it never replays a backlog.</p>
 */
public final class LegacyPulseGate {
    private static final long NANOS_PER_SECOND = 1_000_000_000L;

    private final LongSupplier nanoTimeSource;
    private final long periodNanos;
    private boolean initialized;
    private long nextPulseAtNanos;

    public LegacyPulseGate(int pulsesPerSecond) {
        this(System::nanoTime, pulsesPerSecond);
    }

    public LegacyPulseGate(LongSupplier nanoTimeSource, int pulsesPerSecond) {
        this.nanoTimeSource = Objects.requireNonNull(nanoTimeSource, "nanoTimeSource");
        this.periodNanos = pulsesPerSecond > 0
                ? Math.max(1L, NANOS_PER_SECOND / pulsesPerSecond)
                : 0L;
    }

    /**
     * Returns true for the first poll and then at most at the configured rate.
     * A late poll moves the deadline past the current time in one operation.
     */
    public boolean tryAcquirePulse() {
        if (periodNanos == 0L) {
            return true;
        }

        long now = nanoTimeSource.getAsLong();
        if (!initialized) {
            initialized = true;
            nextPulseAtNanos = now + periodNanos;
            return true;
        }

        long overdueNanos = now - nextPulseAtNanos;
        if (overdueNanos < 0L) {
            return false;
        }

        long elapsedPeriods = overdueNanos / periodNanos;
        nextPulseAtNanos += (elapsedPeriods + 1L) * periodNanos;
        return true;
    }
}
