package pl.genschu.bloomooemulator.engine.physics;

import java.util.Objects;
import java.util.function.LongSupplier;

public class ODEPhysicsTimer {
    private boolean pausedFlag;
    private boolean useHighPrecision;

    private static final long NANO_FREQUENCY = 1_000_000_000L;
    private final LongSupplier nanoClock;
    private final LongSupplier milliClock;
    private long nanoPrevStamp;
    private long prevTime;

    public ODEPhysicsTimer() {
        this(System::nanoTime, System::currentTimeMillis);
    }

    ODEPhysicsTimer(LongSupplier nanoClock, LongSupplier milliClock) {
        this.nanoClock = Objects.requireNonNull(nanoClock);
        this.milliClock = Objects.requireNonNull(milliClock);
        initializeTimer();
    }

    public void initializeTimer() {
        this.pausedFlag = true;

        this.useHighPrecision = true;

        this.nanoPrevStamp = 0L;
        this.prevTime = 0L;
    }

    public double calculateStepSize() {
        if (this.pausedFlag) {
            return 0.0;
        }

        if (this.useHighPrecision) {
            return calculateHighPrecisionStep();
        } else {
            return calculateFallbackStep();
        }
    }

    private double calculateHighPrecisionStep() {
        long currentStamp = nanoClock.getAsLong();

        long prevStamp = this.nanoPrevStamp;
        this.nanoPrevStamp = currentStamp;

        if (prevStamp == 0L) {
            return 0.0;
        }

        long deltaTime = currentStamp - prevStamp;

        return (double) deltaTime / NANO_FREQUENCY;
    }

    private double calculateFallbackStep() {
        long currentTime = milliClock.getAsLong();

        double deltaTime = (currentTime - this.prevTime) / 1000.0;
        this.prevTime = currentTime;

        return deltaTime;
    }

    public void pause() {
        this.pausedFlag = true;
    }

    public void resume() {
        if (!this.pausedFlag) {
            return;
        }
        this.pausedFlag = false;
        // Sekai snapshots QueryPerformanceCounter/timeGetTime while resuming. The first
        // MoveObjects call therefore consumes forces with a small positive step instead
        // of returning zero and leaving ODE's one-shot force accumulator armed.
        this.nanoPrevStamp = nanoClock.getAsLong();
        this.prevTime = milliClock.getAsLong();
    }

    public boolean isPaused() {
        return this.pausedFlag;
    }

    public boolean isUsingHighPrecision() {
        return this.useHighPrecision;
    }

    public void setUseHighPrecision(boolean useHighPrecision) {
        this.useHighPrecision = useHighPrecision;
        initializeTimer();
    }
}
