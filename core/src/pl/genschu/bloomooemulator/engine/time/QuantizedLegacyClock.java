package pl.genschu.bloomooemulator.engine.time;

import java.util.Objects;
import java.util.function.LongSupplier;

/**
 * {@link LegacyClock} backed by a nanosecond source and quantized like a
 * coarse legacy system clock.
 */
public final class QuantizedLegacyClock implements LegacyClock {
    private static final long NANOS_PER_MILLISECOND = 1_000_000L;

    private final LongSupplier nanoTimeSource;
    private final long quantumNanos;
    private final long quantizedOriginNanos;

    public QuantizedLegacyClock(LegacyClockProfile profile) {
        this(System::nanoTime, profile);
    }

    public QuantizedLegacyClock(LongSupplier nanoTimeSource, LegacyClockProfile profile) {
        this.nanoTimeSource = Objects.requireNonNull(nanoTimeSource, "nanoTimeSource");
        LegacyClockProfile effectiveProfile = profile != null
                ? profile
                : LegacyClockProfile.defaultProfile();
        this.quantumNanos = effectiveProfile.quantumNanos();
        this.quantizedOriginNanos = quantize(nanoTimeSource.getAsLong());
    }

    @Override
    public long nowMillis() {
        long elapsedNanos = quantize(nanoTimeSource.getAsLong()) - quantizedOriginNanos;
        if (elapsedNanos <= 0L) {
            return 0L;
        }
        return elapsedNanos / NANOS_PER_MILLISECOND;
    }

    private long quantize(long nanos) {
        return Math.floorDiv(nanos, quantumNanos) * quantumNanos;
    }
}
