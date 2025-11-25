package pl.genschu.bloomooemulator.engine.physics;

public class ODEPhysicsTimer {
    private boolean pausedFlag;
    private boolean useHighPrecision;

    private long nanoFrequency = 1_000_000_000L;
    private long nanoPrevStamp;
    private long prevTime;

    public ODEPhysicsTimer() {
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
        long currentStamp = System.nanoTime();

        long prevStamp = this.nanoPrevStamp;
        this.nanoPrevStamp = currentStamp;

        if (prevStamp == 0L) {
            return 0.0;
        }

        long deltaTime = currentStamp - prevStamp;

        return (double) deltaTime / this.nanoFrequency;
    }

    private double calculateFallbackStep() {
        long currentTime = System.currentTimeMillis();

        double deltaTime = (currentTime - this.prevTime) / 1000.0;
        this.prevTime = currentTime;

        return deltaTime;
    }

    public void pause() {
        this.pausedFlag = true;
    }

    public void resume() {
        this.pausedFlag = false;
        this.nanoPrevStamp = 0L;
        this.prevTime = 0L;
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
