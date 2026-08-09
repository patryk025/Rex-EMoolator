package pl.genschu.bloomooemulator.engine.metrics;

import pl.genschu.bloomooemulator.engine.time.LegacyPulseGate;
import pl.genschu.bloomooemulator.platform.GcMetricsSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.LongSupplier;

/**
 * Allocation-free runtime metric collection for the host render loop and the
 * independently gated legacy-engine pulse.
 *
 * <p>The hot path stores primitive samples only. Sorting, maps and immutable
 * records are created lazily when an overlay requests a snapshot.</p>
 */
public final class EngineMetrics {
    private static final long NANOS_PER_SECOND = 1_000_000_000L;
    private static final long WINDOW_NANOS = 5L * NANOS_PER_SECOND;
    private static final long SNAPSHOT_INTERVAL_NANOS = NANOS_PER_SECOND / 2L;
    private static final long UNSET = Long.MIN_VALUE;
    private static final int MAX_SAMPLES = 2048;
    private static final TimingStats EMPTY_TIMING = new TimingStats(0, 0, 0, 0, 0, 0);

    public enum Level {
        OFF,
        BASIC,
        DETAILED
    }

    public enum Phase {
        FRAME("Frame CPU"),
        HOST_INPUT("Host input"),
        LEGACY_INPUT("Legacy input"),
        MANAGERS("Managers"),
        RENDERING("Rendering"),
        DEBUG("Debug");

        private final String displayName;

        Phase(String displayName) {
            this.displayName = displayName;
        }

        public String displayName() {
            return displayName;
        }
    }

    private final LongSupplier nanoTimeSource;
    private final int targetPulseHz;
    private final long targetPulsePeriodNanos;

    private final TimedSeries frameIntervals = new TimedSeries(MAX_SAMPLES);
    private final TimedSeries pulseEvents = new TimedSeries(MAX_SAMPLES);
    private final TimedSeries pulseJitter = new TimedSeries(MAX_SAMPLES);
    private final TimedSeries pulseLateness = new TimedSeries(MAX_SAMPLES);
    private final TimedSeries gcCollections = new TimedSeries(MAX_SAMPLES);
    private final TimedSeries gcCollectionTimeMillis = new TimedSeries(MAX_SAMPLES);
    private final TimedSeries gcAllocatedBytes = new TimedSeries(MAX_SAMPLES);
    private final TimedSeries gcReclaimedBytes = new TimedSeries(MAX_SAMPLES);
    private final TimedSeries blockingGcCollections = new TimedSeries(MAX_SAMPLES);
    private final TimedSeries blockingGcTimeMillis = new TimedSeries(MAX_SAMPLES);
    private final TimedSeries[] phaseDurations = new TimedSeries[Phase.values().length];
    private final long[] phaseStartedAt = new long[Phase.values().length];

    private Level level = Level.BASIC;
    private long lastFrameStartedAt = UNSET;
    private long lastPulseAt = UNSET;
    private long missedPulsePeriods;
    private boolean paused;
    private boolean suppressNextMissedPeriods;

    private int glCalls;
    private int glDrawCalls;
    private int glTextureBindings;
    private int glShaderSwitches;
    private long glSubmittedVertices;

    private int drawableObjects;
    private int visibleSpriteObjects;
    private int visibleTextObjects;
    private int pastedGraphics;
    private int filteredSprites;
    private int clippedSprites;
    private int maskedSprites;

    private long javaHeapUsedBytes;
    private long javaHeapCommittedBytes;
    private long javaHeapMaxBytes;
    private long nativeHeapUsedBytes;

    private long gcSamplingStartedAt = UNSET;
    private long previousGcSampleAt = UNSET;
    private long lastObservedCollectionAt = UNSET;
    private long previousGcCollectionCount = GcMetricsSource.Sample.UNAVAILABLE;
    private long previousGcCollectionTimeMillis = GcMetricsSource.Sample.UNAVAILABLE;
    private long previousGcAllocatedBytes = GcMetricsSource.Sample.UNAVAILABLE;
    private long previousGcFreedBytes = GcMetricsSource.Sample.UNAVAILABLE;
    private long previousBlockingGcCount = GcMetricsSource.Sample.UNAVAILABLE;
    private long previousBlockingGcTimeMillis = GcMetricsSource.Sample.UNAVAILABLE;
    private long previousGcJavaHeapUsedBytes = GcMetricsSource.Sample.UNAVAILABLE;
    private long totalGcCollections = GcMetricsSource.Sample.UNAVAILABLE;
    private long totalGcCollectionTimeMillis = GcMetricsSource.Sample.UNAVAILABLE;
    private long totalGcAllocatedBytes = GcMetricsSource.Sample.UNAVAILABLE;
    private long totalGcFreedBytes = GcMetricsSource.Sample.UNAVAILABLE;
    private long totalBlockingGcCollections = GcMetricsSource.Sample.UNAVAILABLE;
    private long totalBlockingGcTimeMillis = GcMetricsSource.Sample.UNAVAILABLE;

    private long lastSnapshotAt = UNSET;
    private Snapshot cachedSnapshot;

    public EngineMetrics(int targetPulseHz) {
        this(System::nanoTime, targetPulseHz);
    }

    EngineMetrics(LongSupplier nanoTimeSource, int targetPulseHz) {
        this.nanoTimeSource = Objects.requireNonNull(nanoTimeSource, "nanoTimeSource");
        this.targetPulseHz = Math.max(0, targetPulseHz);
        this.targetPulsePeriodNanos = targetPulseHz > 0
                ? Math.max(1L, NANOS_PER_SECOND / targetPulseHz)
                : 0L;
        for (int i = 0; i < phaseDurations.length; i++) {
            phaseDurations[i] = new TimedSeries(MAX_SAMPLES);
        }
        Arrays.fill(phaseStartedAt, UNSET);
        cachedSnapshot = emptySnapshot(false);
    }

    public Level level() {
        return level;
    }

    public void setLevel(Level newLevel) {
        Objects.requireNonNull(newLevel, "newLevel");
        if (level == newLevel) {
            return;
        }

        Level previousLevel = level;
        level = newLevel;
        if (newLevel == Level.OFF || previousLevel == Level.OFF) {
            resetAll();
        } else if (newLevel == Level.DETAILED) {
            resetDetailed();
        } else {
            Arrays.fill(phaseStartedAt, UNSET);
        }
        invalidateSnapshot();
    }

    /** Records one host-frame boundary and starts the optional CPU-frame timer. */
    public void beginFrame(boolean paused) {
        if (this.paused != paused) {
            this.paused = paused;
            resetPulseWindow();
            if (!paused) {
                // The gate is intentionally not polled while paused. Its first
                // post-resume decision therefore contains pause time, not a
                // real emulation deadline miss.
                suppressNextMissedPeriods = true;
            }
            invalidateSnapshot();
        }
        if (level == Level.OFF) {
            return;
        }

        long now = nanoTimeSource.getAsLong();
        if (lastFrameStartedAt != UNSET && now >= lastFrameStartedAt) {
            frameIntervals.add(now, now - lastFrameStartedAt);
        }
        lastFrameStartedAt = now;

        if (level == Level.DETAILED) {
            phaseStartedAt[Phase.FRAME.ordinal()] = now;
        }
    }

    public void endFrame() {
        endPhase(Phase.FRAME);
    }

    public void beginPhase(Phase phase) {
        if (level == Level.DETAILED) {
            phaseStartedAt[phase.ordinal()] = nanoTimeSource.getAsLong();
        }
    }

    public void endPhase(Phase phase) {
        if (level != Level.DETAILED) {
            return;
        }

        int index = phase.ordinal();
        long startedAt = phaseStartedAt[index];
        if (startedAt == UNSET) {
            return;
        }

        long now = nanoTimeSource.getAsLong();
        phaseStartedAt[index] = UNSET;
        if (now >= startedAt) {
            phaseDurations[index].add(now, now - startedAt);
        }
    }

    /** Records an admitted pulse and the deadline information produced by its gate. */
    public void recordPulse(LegacyPulseGate.PulseDecision decision) {
        if (level == Level.OFF || !decision.admitted()) {
            return;
        }

        long now = decision.timestampNanos();
        long decisionMissedPeriods = Math.max(0L, decision.missedPeriods());
        long decisionLatenessNanos = Math.max(0L, decision.latenessNanos());
        if (suppressNextMissedPeriods) {
            decisionMissedPeriods = 0L;
            decisionLatenessNanos = 0L;
            suppressNextMissedPeriods = false;
        }

        pulseEvents.add(now, 1L);
        pulseLateness.add(now, decisionLatenessNanos);
        missedPulsePeriods += decisionMissedPeriods;

        if (lastPulseAt != UNSET && now >= lastPulseAt && targetPulsePeriodNanos > 0L) {
            long interval = now - lastPulseAt;
            long expectedPeriods = Math.max(1L, decision.missedPeriods() + 1L);
            long expectedInterval = expectedPeriods * targetPulsePeriodNanos;
            pulseJitter.add(now, absoluteDifference(interval, expectedInterval));
        }
        lastPulseAt = now;
    }

    /** Stores the latest GL workload. Collection is meaningful only in detailed mode. */
    public void recordGlWorkload(int calls, int drawCalls, int textureBindings,
                                 int shaderSwitches, long submittedVertices) {
        if (level != Level.DETAILED) {
            return;
        }
        this.glCalls = Math.max(0, calls);
        this.glDrawCalls = Math.max(0, drawCalls);
        this.glTextureBindings = Math.max(0, textureBindings);
        this.glShaderSwitches = Math.max(0, shaderSwitches);
        this.glSubmittedVertices = Math.max(0L, submittedVertices);
    }

    /** Stores logical scene workload from the most recently rendered canvas. */
    public void recordRenderWorkload(int drawableObjects, int visibleSpriteObjects,
                                     int visibleTextObjects, int pastedGraphics,
                                     int filteredSprites, int clippedSprites,
                                     int maskedSprites) {
        if (level != Level.DETAILED) {
            return;
        }
        this.drawableObjects = Math.max(0, drawableObjects);
        this.visibleSpriteObjects = Math.max(0, visibleSpriteObjects);
        this.visibleTextObjects = Math.max(0, visibleTextObjects);
        this.pastedGraphics = Math.max(0, pastedGraphics);
        this.filteredSprites = Math.max(0, filteredSprites);
        this.clippedSprites = Math.max(0, clippedSprites);
        this.maskedSprites = Math.max(0, maskedSprites);
    }

    /** Stores current process-memory figures without retaining historical samples. */
    public void recordMemory(long javaUsedBytes, long javaCommittedBytes,
                             long javaMaxBytes, long nativeUsedBytes) {
        if (level != Level.DETAILED) {
            return;
        }
        this.javaHeapUsedBytes = Math.max(0L, javaUsedBytes);
        this.javaHeapCommittedBytes = Math.max(0L, javaCommittedBytes);
        this.javaHeapMaxBytes = Math.max(0L, javaMaxBytes);
        this.nativeHeapUsedBytes = Math.max(0L, nativeUsedBytes);
    }

    /**
     * Records cumulative platform GC counters and derives rolling rates from
     * their deltas. Call this after {@link #recordMemory(long, long, long, long)}
     * so runtimes without a freed-bytes counter can estimate reclaimed memory.
     */
    public void recordGarbageCollection(GcMetricsSource.Sample sample) {
        if (level != Level.DETAILED) {
            return;
        }
        Objects.requireNonNull(sample, "sample");

        long now = nanoTimeSource.getAsLong();
        if (gcSamplingStartedAt == UNSET) {
            gcSamplingStartedAt = now;
        }

        if (previousGcSampleAt != UNSET && now >= previousGcSampleAt) {
            long collectionDelta = recordCounterDelta(
                    gcCollections, now, sample.collectionCount(), previousGcCollectionCount);
            recordCounterDelta(gcCollectionTimeMillis, now,
                    sample.collectionTimeMillis(), previousGcCollectionTimeMillis);
            long allocatedDelta = recordCounterDelta(
                    gcAllocatedBytes, now, sample.allocatedBytes(), previousGcAllocatedBytes);
            long freedDelta = recordCounterDelta(
                    gcReclaimedBytes, now, sample.freedBytes(), previousGcFreedBytes);
            recordCounterDelta(blockingGcCollections, now,
                    sample.blockingCollectionCount(), previousBlockingGcCount);
            recordCounterDelta(blockingGcTimeMillis, now,
                    sample.blockingCollectionTimeMillis(), previousBlockingGcTimeMillis);

            if (freedDelta == GcMetricsSource.Sample.UNAVAILABLE
                    && allocatedDelta != GcMetricsSource.Sample.UNAVAILABLE
                    && previousGcJavaHeapUsedBytes != GcMetricsSource.Sample.UNAVAILABLE) {
                long heapGrowth = javaHeapUsedBytes - previousGcJavaHeapUsedBytes;
                long estimatedReclaimed = Math.max(0L, allocatedDelta - heapGrowth);
                if (estimatedReclaimed > 0L) {
                    gcReclaimedBytes.add(now, estimatedReclaimed);
                }
            }

            if (collectionDelta > 0L) {
                lastObservedCollectionAt = now;
            }
        }

        previousGcSampleAt = now;
        previousGcCollectionCount = sample.collectionCount();
        previousGcCollectionTimeMillis = sample.collectionTimeMillis();
        previousGcAllocatedBytes = sample.allocatedBytes();
        previousGcFreedBytes = sample.freedBytes();
        previousBlockingGcCount = sample.blockingCollectionCount();
        previousBlockingGcTimeMillis = sample.blockingCollectionTimeMillis();
        previousGcJavaHeapUsedBytes = javaHeapUsedBytes;

        totalGcCollections = sample.collectionCount();
        totalGcCollectionTimeMillis = sample.collectionTimeMillis();
        totalGcAllocatedBytes = sample.allocatedBytes();
        totalGcFreedBytes = sample.freedBytes();
        totalBlockingGcCollections = sample.blockingCollectionCount();
        totalBlockingGcTimeMillis = sample.blockingCollectionTimeMillis();
    }

    /** Returns a cached half-second snapshot, recalculating it only when needed. */
    public Snapshot snapshot() {
        if (level == Level.OFF) {
            return emptySnapshot(paused);
        }

        long now = nanoTimeSource.getAsLong();
        if (lastSnapshotAt != UNSET
                && now >= lastSnapshotAt
                && now - lastSnapshotAt < SNAPSHOT_INTERVAL_NANOS) {
            return cachedSnapshot;
        }

        long cutoff = now - WINDOW_NANOS;
        long[] frames = frameIntervals.valuesSince(cutoff);
        TimingStats frameStats = calculateTimingStats(frames);
        double hostFps = frameStats.averageMs() > 0.0
                ? 1000.0 / frameStats.averageMs()
                : 0.0;

        long[] pulseTimes = pulseEvents.timestampsSince(cutoff);
        double pulseHz = calculatePulseRate(pulseTimes, now);
        TimingStats jitterStats = calculateTimingStats(pulseJitter.valuesSince(cutoff));
        TimingStats latenessStats = calculateTimingStats(pulseLateness.valuesSince(cutoff));

        EnumMap<Phase, TimingStats> phases = new EnumMap<>(Phase.class);
        for (Phase phase : Phase.values()) {
            phases.put(phase, calculateTimingStats(
                    phaseDurations[phase.ordinal()].valuesSince(cutoff)));
        }
        GcStats gcStats = calculateGcStats(now, cutoff);

        cachedSnapshot = new Snapshot(
                hostFps,
                frameStats,
                countAbove(frames, 33_000_000L),
                countAbove(frames, 50_000_000L),
                pulseHz,
                targetPulseHz,
                jitterStats.p99Ms(),
                latenessStats.p99Ms(),
                latenessStats.maxMs(),
                missedPulsePeriods,
                paused,
                Collections.unmodifiableMap(phases),
                new GlStats(glCalls, glDrawCalls, glTextureBindings,
                        glShaderSwitches, glSubmittedVertices),
                new RenderStats(drawableObjects, visibleSpriteObjects, visibleTextObjects,
                        pastedGraphics, filteredSprites, clippedSprites, maskedSprites),
                new MemoryStats(javaHeapUsedBytes, javaHeapCommittedBytes,
                        javaHeapMaxBytes, nativeHeapUsedBytes),
                gcStats);
        lastSnapshotAt = now;
        return cachedSnapshot;
    }

    private Snapshot emptySnapshot(boolean paused) {
        return new Snapshot(
                0, EMPTY_TIMING, 0, 0, 0, targetPulseHz,
                0, 0, 0, 0, paused, Map.of(),
                new GlStats(0, 0, 0, 0, 0),
                new RenderStats(0, 0, 0, 0, 0, 0, 0),
                new MemoryStats(0, 0, 0, 0),
                GcStats.UNAVAILABLE);
    }

    private void resetAll() {
        frameIntervals.clear();
        pulseEvents.clear();
        pulseJitter.clear();
        pulseLateness.clear();
        lastFrameStartedAt = UNSET;
        lastPulseAt = UNSET;
        missedPulsePeriods = 0L;
        suppressNextMissedPeriods = false;
        resetDetailed();
    }

    private void resetPulseWindow() {
        pulseEvents.clear();
        pulseJitter.clear();
        pulseLateness.clear();
        lastPulseAt = UNSET;
    }

    private void resetDetailed() {
        for (TimedSeries durations : phaseDurations) {
            durations.clear();
        }
        Arrays.fill(phaseStartedAt, UNSET);
        glCalls = 0;
        glDrawCalls = 0;
        glTextureBindings = 0;
        glShaderSwitches = 0;
        glSubmittedVertices = 0L;
        drawableObjects = 0;
        visibleSpriteObjects = 0;
        visibleTextObjects = 0;
        pastedGraphics = 0;
        filteredSprites = 0;
        clippedSprites = 0;
        maskedSprites = 0;
        javaHeapUsedBytes = 0L;
        javaHeapCommittedBytes = 0L;
        javaHeapMaxBytes = 0L;
        nativeHeapUsedBytes = 0L;
        gcCollections.clear();
        gcCollectionTimeMillis.clear();
        gcAllocatedBytes.clear();
        gcReclaimedBytes.clear();
        blockingGcCollections.clear();
        blockingGcTimeMillis.clear();
        gcSamplingStartedAt = UNSET;
        previousGcSampleAt = UNSET;
        lastObservedCollectionAt = UNSET;
        previousGcCollectionCount = GcMetricsSource.Sample.UNAVAILABLE;
        previousGcCollectionTimeMillis = GcMetricsSource.Sample.UNAVAILABLE;
        previousGcAllocatedBytes = GcMetricsSource.Sample.UNAVAILABLE;
        previousGcFreedBytes = GcMetricsSource.Sample.UNAVAILABLE;
        previousBlockingGcCount = GcMetricsSource.Sample.UNAVAILABLE;
        previousBlockingGcTimeMillis = GcMetricsSource.Sample.UNAVAILABLE;
        previousGcJavaHeapUsedBytes = GcMetricsSource.Sample.UNAVAILABLE;
        totalGcCollections = GcMetricsSource.Sample.UNAVAILABLE;
        totalGcCollectionTimeMillis = GcMetricsSource.Sample.UNAVAILABLE;
        totalGcAllocatedBytes = GcMetricsSource.Sample.UNAVAILABLE;
        totalGcFreedBytes = GcMetricsSource.Sample.UNAVAILABLE;
        totalBlockingGcCollections = GcMetricsSource.Sample.UNAVAILABLE;
        totalBlockingGcTimeMillis = GcMetricsSource.Sample.UNAVAILABLE;
    }

    private void invalidateSnapshot() {
        lastSnapshotAt = UNSET;
    }

    private static long absoluteDifference(long first, long second) {
        return first >= second ? first - second : second - first;
    }

    private static long recordCounterDelta(TimedSeries series, long now,
                                           long current, long previous) {
        if (current < 0L || previous < 0L || current < previous) {
            return GcMetricsSource.Sample.UNAVAILABLE;
        }
        long delta = current - previous;
        if (delta > 0L) {
            series.add(now, delta);
        }
        return delta;
    }

    private static double calculatePulseRate(long[] pulseTimes, long now) {
        if (pulseTimes.length < 2) {
            return 0.0;
        }
        long elapsed = now - pulseTimes[0];
        if (elapsed <= 0L) {
            return 0.0;
        }
        return (pulseTimes.length - 1L) * (double) NANOS_PER_SECOND / elapsed;
    }

    private GcStats calculateGcStats(long now, long cutoff) {
        if (gcSamplingStartedAt == UNSET) {
            return GcStats.UNAVAILABLE;
        }

        long windowStart = Math.max(cutoff, gcSamplingStartedAt);
        long elapsedNanos = Math.max(0L, now - windowStart);
        double elapsedSeconds = elapsedNanos / (double) NANOS_PER_SECOND;
        double elapsedMillis = elapsedNanos / 1_000_000.0;

        long collections = sum(gcCollections.valuesSince(cutoff));
        long collectionMillis = sum(gcCollectionTimeMillis.valuesSince(cutoff));
        long allocatedBytes = sum(gcAllocatedBytes.valuesSince(cutoff));
        long reclaimedBytes = sum(gcReclaimedBytes.valuesSince(cutoff));
        long blockingCollections = sum(blockingGcCollections.valuesSince(cutoff));
        long blockingMillis = sum(blockingGcTimeMillis.valuesSince(cutoff));

        boolean reclaimedSupported = totalGcFreedBytes >= 0L || totalGcAllocatedBytes >= 0L;
        long lastCollectionAgeMillis = lastObservedCollectionAt == UNSET
                ? GcMetricsSource.Sample.UNAVAILABLE
                : Math.max(0L, now - lastObservedCollectionAt) / 1_000_000L;

        return new GcStats(
                totalGcCollections,
                totalGcCollectionTimeMillis,
                ratePerMinute(collections, elapsedSeconds, totalGcCollections >= 0L),
                timePercent(collectionMillis, elapsedMillis, totalGcCollectionTimeMillis >= 0L),
                ratePerSecond(allocatedBytes, elapsedSeconds, totalGcAllocatedBytes >= 0L),
                ratePerSecond(reclaimedBytes, elapsedSeconds, reclaimedSupported),
                totalGcFreedBytes < 0L && reclaimedSupported,
                lastCollectionAgeMillis,
                totalBlockingGcCollections,
                totalBlockingGcTimeMillis,
                ratePerMinute(blockingCollections, elapsedSeconds,
                        totalBlockingGcCollections >= 0L),
                timePercent(blockingMillis, elapsedMillis,
                        totalBlockingGcTimeMillis >= 0L));
    }

    private static double ratePerMinute(long value, double elapsedSeconds, boolean supported) {
        if (!supported) {
            return -1.0;
        }
        return elapsedSeconds > 0.0 ? value * 60.0 / elapsedSeconds : 0.0;
    }

    private static double ratePerSecond(long value, double elapsedSeconds, boolean supported) {
        if (!supported) {
            return -1.0;
        }
        return elapsedSeconds > 0.0 ? value / elapsedSeconds : 0.0;
    }

    private static double timePercent(long timeMillis, double elapsedMillis, boolean supported) {
        if (!supported) {
            return -1.0;
        }
        return elapsedMillis > 0.0 ? timeMillis * 100.0 / elapsedMillis : 0.0;
    }

    private static long sum(long[] values) {
        long total = 0L;
        for (long value : values) {
            total += value;
        }
        return total;
    }

    private static int countAbove(long[] values, long threshold) {
        int count = 0;
        for (long value : values) {
            if (value > threshold) {
                count++;
            }
        }
        return count;
    }

    private static TimingStats calculateTimingStats(long[] values) {
        if (values.length == 0) {
            return EMPTY_TIMING;
        }

        long total = 0L;
        for (long value : values) {
            total += value;
        }
        Arrays.sort(values);

        return new TimingStats(
                values.length,
                total / (double) values.length / 1_000_000.0,
                percentile(values, 0.50) / 1_000_000.0,
                percentile(values, 0.95) / 1_000_000.0,
                percentile(values, 0.99) / 1_000_000.0,
                values[values.length - 1] / 1_000_000.0);
    }

    private static long percentile(long[] sortedValues, double percentile) {
        int index = Math.min(
                sortedValues.length - 1,
                Math.max(0, (int) Math.ceil(sortedValues.length * percentile) - 1));
        return sortedValues[index];
    }

    public record TimingStats(
            int samples,
            double averageMs,
            double p50Ms,
            double p95Ms,
            double p99Ms,
            double maxMs
    ) {}

    public record GlStats(
            int calls,
            int drawCalls,
            int textureBindings,
            int shaderSwitches,
            long submittedVertices
    ) {}

    public record RenderStats(
            int drawableObjects,
            int visibleSpriteObjects,
            int visibleTextObjects,
            int pastedGraphics,
            int filteredSprites,
            int clippedSprites,
            int maskedSprites
    ) {}

    public record MemoryStats(
            long javaHeapUsedBytes,
            long javaHeapCommittedBytes,
            long javaHeapMaxBytes,
            long nativeHeapUsedBytes
    ) {}

    public record GcStats(
            long collectionCount,
            long collectionTimeMillis,
            double collectionsPerMinute,
            double recentTimePercent,
            double allocatedBytesPerSecond,
            double reclaimedBytesPerSecond,
            boolean reclaimedEstimated,
            long lastCollectionAgeMillis,
            long blockingCollectionCount,
            long blockingCollectionTimeMillis,
            double blockingCollectionsPerMinute,
            double recentBlockingTimePercent
    ) {
        private static final GcStats UNAVAILABLE = new GcStats(
                -1L, -1L, -1.0, -1.0, -1.0, -1.0,
                false, -1L, -1L, -1L, -1.0, -1.0);

        public boolean available() {
            return collectionCount >= 0L;
        }

        public boolean blockingStatsAvailable() {
            return blockingCollectionCount >= 0L;
        }
    }

    public record Snapshot(
            double hostFps,
            TimingStats frameTimes,
            int stallsOver33Ms,
            int stallsOver50Ms,
            double pulseHz,
            int targetPulseHz,
            double pulseJitterP99Ms,
            double pulseLatenessP99Ms,
            double pulseLatenessMaxMs,
            long missedPulsePeriods,
            boolean paused,
            Map<Phase, TimingStats> phaseTimings,
            GlStats gl,
            RenderStats render,
            MemoryStats memory,
            GcStats gc
    ) {
        public TimingStats phase(Phase phase) {
            return phaseTimings.getOrDefault(phase, EMPTY_TIMING);
        }
    }

    private static final class TimedSeries {
        private final long[] timestamps;
        private final long[] values;
        private int nextIndex;
        private int count;

        private TimedSeries(int capacity) {
            timestamps = new long[capacity];
            values = new long[capacity];
        }

        private void add(long timestamp, long value) {
            timestamps[nextIndex] = timestamp;
            values[nextIndex] = value;
            nextIndex = (nextIndex + 1) % values.length;
            if (count < values.length) {
                count++;
            }
        }

        private long[] valuesSince(long cutoff) {
            int matching = countSince(cutoff);
            long[] result = new long[matching];
            int resultIndex = 0;
            for (int i = 0; i < count; i++) {
                int index = indexAt(i);
                if (timestamps[index] >= cutoff) {
                    result[resultIndex++] = values[index];
                }
            }
            return result;
        }

        private long[] timestampsSince(long cutoff) {
            int matching = countSince(cutoff);
            long[] result = new long[matching];
            int resultIndex = 0;
            for (int i = 0; i < count; i++) {
                int index = indexAt(i);
                if (timestamps[index] >= cutoff) {
                    result[resultIndex++] = timestamps[index];
                }
            }
            return result;
        }

        private int countSince(long cutoff) {
            int matching = 0;
            for (int i = 0; i < count; i++) {
                if (timestamps[indexAt(i)] >= cutoff) {
                    matching++;
                }
            }
            return matching;
        }

        private int indexAt(int chronologicalOffset) {
            return (nextIndex - count + chronologicalOffset + values.length) % values.length;
        }

        private void clear() {
            nextIndex = 0;
            count = 0;
        }
    }
}
