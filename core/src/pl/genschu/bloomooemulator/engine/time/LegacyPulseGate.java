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
    private final PulseDecision pulseDecision = new PulseDecision();
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
        return poll().admitted();
    }

    /**
     * Polls the gate and exposes deadline details for runtime diagnostics.
     * {@code missedPeriods} counts scheduled pulses discarded before the one
     * admitted by this poll; ordinary high-refresh polls return zero. The
     * returned object is reused by the gate and is valid until the next poll.
     */
    public PulseDecision poll() {
        long now = nanoTimeSource.getAsLong();
        if (periodNanos == 0L) {
            return pulseDecision.set(true, now, 0L, 0L);
        }

        if (!initialized) {
            initialized = true;
            nextPulseAtNanos = now + periodNanos;
            return pulseDecision.set(true, now, 0L, 0L);
        }

        long overdueNanos = now - nextPulseAtNanos;
        if (overdueNanos < 0L) {
            return pulseDecision.set(false, now, 0L, 0L);
        }

        long elapsedPeriods = overdueNanos / periodNanos;
        nextPulseAtNanos += (elapsedPeriods + 1L) * periodNanos;
        return pulseDecision.set(true, now, overdueNanos, elapsedPeriods);
    }

    public static final class PulseDecision {
        private boolean admitted;
        private long timestampNanos;
        private long latenessNanos;
        private long missedPeriods;

        private PulseDecision set(boolean admitted, long timestampNanos,
                                  long latenessNanos, long missedPeriods) {
            this.admitted = admitted;
            this.timestampNanos = timestampNanos;
            this.latenessNanos = latenessNanos;
            this.missedPeriods = missedPeriods;
            return this;
        }

        public boolean admitted() {
            return admitted;
        }

        public long timestampNanos() {
            return timestampNanos;
        }

        public long latenessNanos() {
            return latenessNanos;
        }

        public long missedPeriods() {
            return missedPeriods;
        }
    }
}
