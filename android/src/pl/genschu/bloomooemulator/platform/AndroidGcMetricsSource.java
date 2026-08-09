package pl.genschu.bloomooemulator.platform;

import android.os.Debug;

import java.util.Map;

/** Reads the cumulative ART runtime counters available since Android API 23. */
public final class AndroidGcMetricsSource implements GcMetricsSource {
    private static final String GC_COUNT = "art.gc.gc-count";
    private static final String GC_TIME = "art.gc.gc-time";
    private static final String BYTES_ALLOCATED = "art.gc.bytes-allocated";
    private static final String BYTES_FREED = "art.gc.bytes-freed";
    private static final String BLOCKING_GC_COUNT = "art.gc.blocking-gc-count";
    private static final String BLOCKING_GC_TIME = "art.gc.blocking-gc-time";

    @Override
    public Sample sample() {
        Map<String, String> stats = Debug.getRuntimeStats();
        return new Sample(
                parseCounter(stats.get(GC_COUNT)),
                parseCounter(stats.get(GC_TIME)),
                parseCounter(stats.get(BYTES_ALLOCATED)),
                parseCounter(stats.get(BYTES_FREED)),
                parseCounter(stats.get(BLOCKING_GC_COUNT)),
                parseCounter(stats.get(BLOCKING_GC_TIME)));
    }

    private static long parseCounter(String value) {
        if (value == null) {
            return Sample.UNAVAILABLE;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException ignored) {
            return Sample.UNAVAILABLE;
        }
    }
}
