package pl.genschu.bloomooemulator.interpreter.variable;

import pl.genschu.bloomooemulator.interpreter.helpers.ArgumentHelper;
import pl.genschu.bloomooemulator.interpreter.values.*;

import java.util.HashMap;
import java.util.Map;

/**
 * TimerVariable handles time-based events.
 * Emits ONTICK signal at regular intervals.
 *
 * Fields:
 * - elapse: interval in milliseconds between ticks
 * - enabled: whether the timer is active
 * - ticks: maximum number of ticks (0 = unlimited)
 * - lastTickTime: timestamp of last tick
 * - currentTickCount: number of ticks since reset
 **/
public record TimerVariable(
    String name,
    long elapse,
    boolean enabled,
    int ticks,
    long lastTickTime,
    int currentTickCount,
    Map<String, SignalHandler> signals
) implements Variable {

    public TimerVariable {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Variable name cannot be null or empty");
        }
        if (signals == null) {
            signals = Map.of();
        } else {
            signals = Map.copyOf(signals);
        }
    }

    public TimerVariable(String name) {
        this(name, 0L, true, 0, System.currentTimeMillis(), 0, Map.of());
    }

    public TimerVariable(String name, long elapse) {
        this(name, elapse, true, 0, System.currentTimeMillis(), 0, Map.of());
    }

    // ========================================
    // INTERFACE IMPLEMENTATION
    // ========================================

    @Override
    public Value value() {
        return new IntValue(currentTickCount);
    }

    @Override
    public VariableType type() {
        return VariableType.TIMER;
    }

    @Override
    public Variable withValue(Value newValue) {
        int newTickCount = ArgumentHelper.getInt(newValue);
        return new TimerVariable(name, elapse, enabled, ticks, lastTickTime, newTickCount, signals);
    }

    @Override
    public Map<String, VariableMethod> methods() {
        return METHODS;
    }

    @Override
    public Variable withSignal(String signalName, SignalHandler handler) {
        Map<String, SignalHandler> newSignals = new HashMap<>(signals);
        newSignals.put(signalName, handler);
        return new TimerVariable(name, elapse, enabled, ticks, lastTickTime, currentTickCount, newSignals);
    }

    // ========================================
    // TIMER-SPECIFIC METHODS
    // ========================================

    /**
     * Updates the timer based on current time.
     * Returns a new TimerVariable with updated state.
     * Call this from your game loop.
     *
     * @return UpdateResult containing new timer state and whether a tick occurred
     */
    public UpdateResult update() {
        return update(System.currentTimeMillis());
    }

    /**
     * Updates the timer with a specific timestamp (useful for testing).
     *
     * @param currentTime the current timestamp in milliseconds
     * @return UpdateResult containing new timer state and whether a tick occurred
     */
    public UpdateResult update(long currentTime) {
        if (!enabled || elapse <= 0) {
            return new UpdateResult(this, false);
        }

        if (currentTime - lastTickTime >= elapse) {
            int newTickCount = currentTickCount + 1;
            boolean shouldDisable = (ticks != 0 && newTickCount >= ticks);

            TimerVariable newTimer = new TimerVariable(
                name, elapse, !shouldDisable, ticks,
                currentTime, newTickCount, signals
            );
            return new UpdateResult(newTimer, true);
        }

        return new UpdateResult(this, false);
    }

    /**
     * Result of timer update operation.
     */
    public record UpdateResult(TimerVariable timer, boolean tickOccurred) {}

    /**
     * Gets time elapsed since last tick in milliseconds.
     */
    public int getTimeFromLastTick() {
        return (int) (System.currentTimeMillis() - lastTickTime);
    }

    // ========================================
    // METHODS DEFINITION
    // ========================================

    private static final Map<String, VariableMethod> METHODS = Map.ofEntries(
        Map.entry("DISABLE", (self, args) -> {
            TimerVariable thisVar = (TimerVariable) self;
            return MethodResult.sets(new TimerVariable(
                thisVar.name, thisVar.elapse, false, thisVar.ticks,
                thisVar.lastTickTime, thisVar.currentTickCount, thisVar.signals
            ));
        }),

        Map.entry("ENABLE", (self, args) -> {
            TimerVariable thisVar = (TimerVariable) self;
            return MethodResult.sets(new TimerVariable(
                thisVar.name, thisVar.elapse, true, thisVar.ticks,
                System.currentTimeMillis(), thisVar.currentTickCount, thisVar.signals
            ));
        }),

        Map.entry("GETTICKS", (self, args) -> {
            TimerVariable thisVar = (TimerVariable) self;
            return MethodResult.noChange(new IntValue(thisVar.currentTickCount));
        }),

        Map.entry("RESET", (self, args) -> {
            TimerVariable thisVar = (TimerVariable) self;
            return MethodResult.sets(new TimerVariable(
                thisVar.name, thisVar.elapse, thisVar.enabled, thisVar.ticks,
                System.currentTimeMillis(), 0, thisVar.signals
            ));
        }),

        Map.entry("SET", (self, args) -> {
            TimerVariable thisVar = (TimerVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("SET requires 1 argument");
            }

            int newTicks = ArgumentHelper.getInt(args.get(0));
            return MethodResult.sets(new TimerVariable(
                thisVar.name, thisVar.elapse, thisVar.enabled, newTicks,
                thisVar.lastTickTime, thisVar.currentTickCount, thisVar.signals
            ));
        }),

        Map.entry("SETELAPSE", (self, args) -> {
            TimerVariable thisVar = (TimerVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("SETELAPSE requires 1 argument");
            }

            long newElapse = ArgumentHelper.getInt(args.get(0));
            return MethodResult.sets(new TimerVariable(
                thisVar.name, newElapse, thisVar.enabled, thisVar.ticks,
                System.currentTimeMillis(), thisVar.currentTickCount, thisVar.signals
            ));
        })
    );

    // ========================================
    // CONVENIENT ACCESSORS
    // ========================================

    @Override
    public String toString() {
        return "TimerVariable[" + name + ", elapse=" + elapse + "ms, enabled=" + enabled +
               ", ticks=" + currentTickCount + "/" + (ticks == 0 ? "∞" : ticks) + "]";
    }
}
