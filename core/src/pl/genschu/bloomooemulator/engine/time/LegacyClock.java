package pl.genschu.bloomooemulator.engine.time;

/**
 * Monotonic clock exposed to legacy engine schedulers.
 *
 * <p>The original engines sampled {@code GetTickCount}; callers therefore see
 * integer milliseconds that may advance in coarse, platform-dependent jumps.
 * Rendering cadence must not be used as a substitute for this clock.</p>
 */
@FunctionalInterface
public interface LegacyClock {
    long nowMillis();
}
