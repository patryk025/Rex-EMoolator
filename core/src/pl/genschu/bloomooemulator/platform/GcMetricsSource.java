package pl.genschu.bloomooemulator.platform;

/**
 * Platform-specific source of monotonically increasing garbage-collection
 * counters. Unsupported counters use {@link Sample#UNAVAILABLE}.
 */
public interface GcMetricsSource {
    Sample sample();

    default void dispose() {
    }

    static GcMetricsSource unavailable() {
        return () -> Sample.EMPTY;
    }

    record Sample(
            long collectionCount,
            long collectionTimeMillis,
            long allocatedBytes,
            long freedBytes,
            long blockingCollectionCount,
            long blockingCollectionTimeMillis
    ) {
        public static final long UNAVAILABLE = -1L;
        public static final Sample EMPTY = new Sample(
                UNAVAILABLE,
                UNAVAILABLE,
                UNAVAILABLE,
                UNAVAILABLE,
                UNAVAILABLE,
                UNAVAILABLE);
    }
}
