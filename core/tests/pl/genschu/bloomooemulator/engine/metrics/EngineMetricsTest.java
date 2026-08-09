package pl.genschu.bloomooemulator.engine.metrics;

import org.junit.jupiter.api.Test;
import pl.genschu.bloomooemulator.engine.time.LegacyPulseGate;
import pl.genschu.bloomooemulator.platform.GcMetricsSource;

import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EngineMetricsTest {
    private static final long SIXTY_HZ_NANOS = 1_000_000_000L / 60L;

    @Test
    void reportsHostFpsAndFrameTimePercentilesFromStartToStartSamples() {
        AtomicLong nanos = new AtomicLong();
        EngineMetrics metrics = new EngineMetrics(nanos::get, 60);

        for (int frame = 0; frame <= 60; frame++) {
            nanos.set(frame * SIXTY_HZ_NANOS);
            metrics.beginFrame(false);
        }

        EngineMetrics.Snapshot snapshot = metrics.snapshot();

        assertEquals(60.0, snapshot.hostFps(), 0.01);
        assertEquals(16.666666, snapshot.frameTimes().averageMs(), 0.001);
        assertEquals(16.666666, snapshot.frameTimes().p99Ms(), 0.001);
        assertEquals(0, snapshot.stallsOver33Ms());
        assertEquals(0, snapshot.stallsOver50Ms());
    }

    @Test
    void keepsLongHostStallsVisibleInsteadOfClippingThem() {
        AtomicLong nanos = new AtomicLong();
        EngineMetrics metrics = new EngineMetrics(nanos::get, 60);

        metrics.beginFrame(false);
        nanos.set(2_000_000_000L);
        metrics.beginFrame(false);

        EngineMetrics.Snapshot snapshot = metrics.snapshot();
        assertEquals(2_000.0, snapshot.frameTimes().p99Ms(), 0.001);
        assertEquals(1, snapshot.stallsOver33Ms());
        assertEquals(1, snapshot.stallsOver50Ms());
    }

    @Test
    void reportsStableSixtyHertzPulseCadence() {
        AtomicLong nanos = new AtomicLong();
        LegacyPulseGate gate = new LegacyPulseGate(nanos::get, 60);
        EngineMetrics metrics = new EngineMetrics(nanos::get, 60);

        for (int pulse = 0; pulse <= 60; pulse++) {
            nanos.set(pulse * SIXTY_HZ_NANOS);
            metrics.recordPulse(gate.poll());
        }

        EngineMetrics.Snapshot snapshot = metrics.snapshot();
        assertEquals(60.0, snapshot.pulseHz(), 0.01);
        assertEquals(0, snapshot.missedPulsePeriods());
    }

    @Test
    void reportsPulseRateLatenessAndDiscardedPeriods() {
        AtomicLong nanos = new AtomicLong();
        LegacyPulseGate gate = new LegacyPulseGate(nanos::get, 60);
        EngineMetrics metrics = new EngineMetrics(nanos::get, 60);

        metrics.recordPulse(gate.poll());
        nanos.set(SIXTY_HZ_NANOS);
        metrics.recordPulse(gate.poll());
        nanos.set(500_000_000L);
        metrics.recordPulse(gate.poll());

        EngineMetrics.Snapshot snapshot = metrics.snapshot();

        assertEquals(28, snapshot.missedPulsePeriods());
        assertTrue(snapshot.pulseLatenessMaxMs() > 460.0);
        assertTrue(snapshot.pulseJitterP99Ms() < 0.001);
    }

    @Test
    void detailedPhaseTimersAreDisabledInBasicMode() {
        AtomicLong nanos = new AtomicLong();
        EngineMetrics metrics = new EngineMetrics(nanos::get, 60);

        metrics.beginPhase(EngineMetrics.Phase.RENDERING);
        nanos.set(5_000_000L);
        metrics.endPhase(EngineMetrics.Phase.RENDERING);
        assertEquals(0, metrics.snapshot().phase(EngineMetrics.Phase.RENDERING).samples());

        metrics.setLevel(EngineMetrics.Level.DETAILED);
        metrics.beginPhase(EngineMetrics.Phase.RENDERING);
        nanos.set(8_000_000L);
        metrics.endPhase(EngineMetrics.Phase.RENDERING);

        EngineMetrics.TimingStats rendering = metrics.snapshot()
                .phase(EngineMetrics.Phase.RENDERING);
        assertEquals(1, rendering.samples());
        assertEquals(3.0, rendering.averageMs(), 0.001);
    }

    @Test
    void exposesLatestDetailedGlRenderAndMemoryWorkload() {
        AtomicLong nanos = new AtomicLong();
        EngineMetrics metrics = new EngineMetrics(nanos::get, 60);
        metrics.setLevel(EngineMetrics.Level.DETAILED);

        metrics.recordGlWorkload(80, 12, 9, 2, 540);
        metrics.recordRenderWorkload(45, 31, 4, 2, 3, 1, 2);
        metrics.recordMemory(128L << 20, 256L << 20, 1024L << 20, 64L << 20);

        EngineMetrics.Snapshot snapshot = metrics.snapshot();
        assertEquals(12, snapshot.gl().drawCalls());
        assertEquals(9, snapshot.gl().textureBindings());
        assertEquals(540, snapshot.gl().submittedVertices());
        assertEquals(45, snapshot.render().drawableObjects());
        assertEquals(31, snapshot.render().visibleSpriteObjects());
        assertEquals(2, snapshot.render().maskedSprites());
        assertEquals(128L << 20, snapshot.memory().javaHeapUsedBytes());
        assertEquals(64L << 20, snapshot.memory().nativeHeapUsedBytes());
    }

    @Test
    void derivesRollingGcAllocationAndReclamationRatesFromRuntimeCounters() {
        AtomicLong nanos = new AtomicLong();
        EngineMetrics metrics = new EngineMetrics(nanos::get, 60);
        metrics.setLevel(EngineMetrics.Level.DETAILED);

        metrics.recordMemory(100L << 20, 256L << 20, 1024L << 20, 0L);
        metrics.recordGarbageCollection(new GcMetricsSource.Sample(
                10, 100, 500L << 20, 400L << 20, 2, 20));

        nanos.set(1_000_000_000L);
        metrics.recordMemory(110L << 20, 256L << 20, 1024L << 20, 0L);
        metrics.recordGarbageCollection(new GcMetricsSource.Sample(
                12, 120, 550L << 20, 440L << 20, 3, 25));

        EngineMetrics.GcStats gc = metrics.snapshot().gc();
        assertTrue(gc.available());
        assertEquals(120.0, gc.collectionsPerMinute(), 0.001);
        assertEquals(2.0, gc.recentTimePercent(), 0.001);
        assertEquals(50L << 20, gc.allocatedBytesPerSecond(), 0.001);
        assertEquals(40L << 20, gc.reclaimedBytesPerSecond(), 0.001);
        assertFalse(gc.reclaimedEstimated());
        assertEquals(60.0, gc.blockingCollectionsPerMinute(), 0.001);
        assertEquals(0.5, gc.recentBlockingTimePercent(), 0.001);
        assertEquals(0L, gc.lastCollectionAgeMillis());
    }

    @Test
    void estimatesReclaimedBytesWhenTheJvmOnlyExposesAllocationCounters() {
        AtomicLong nanos = new AtomicLong();
        EngineMetrics metrics = new EngineMetrics(nanos::get, 60);
        metrics.setLevel(EngineMetrics.Level.DETAILED);

        metrics.recordMemory(100L << 20, 256L << 20, 1024L << 20, 0L);
        metrics.recordGarbageCollection(new GcMetricsSource.Sample(
                5, 50, 200L << 20, -1L, -1L, -1L));

        nanos.set(1_000_000_000L);
        metrics.recordMemory(110L << 20, 256L << 20, 1024L << 20, 0L);
        metrics.recordGarbageCollection(new GcMetricsSource.Sample(
                6, 55, 250L << 20, -1L, -1L, -1L));

        EngineMetrics.GcStats gc = metrics.snapshot().gc();
        assertEquals(50L << 20, gc.allocatedBytesPerSecond(), 0.001);
        assertEquals(40L << 20, gc.reclaimedBytesPerSecond(), 0.001);
        assertTrue(gc.reclaimedEstimated());
        assertFalse(gc.blockingStatsAvailable());
    }

    @Test
    void pauseDoesNotTurnIntoMissedEmulationDeadlinesOnResume() {
        AtomicLong nanos = new AtomicLong();
        LegacyPulseGate gate = new LegacyPulseGate(nanos::get, 60);
        EngineMetrics metrics = new EngineMetrics(nanos::get, 60);

        metrics.beginFrame(false);
        metrics.recordPulse(gate.poll());
        nanos.set(SIXTY_HZ_NANOS);
        metrics.beginFrame(true);

        nanos.set(1_000_000_000L);
        metrics.beginFrame(false);
        metrics.recordPulse(gate.poll());

        EngineMetrics.Snapshot snapshot = metrics.snapshot();
        assertEquals(0, snapshot.missedPulsePeriods());
        assertEquals(0.0, snapshot.pulseLatenessMaxMs(), 0.001);
    }
}
