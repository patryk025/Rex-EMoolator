package pl.genschu.bloomooemulator.interpreter.variable;

import pl.genschu.bloomooemulator.annotations.InternalMutable;
import pl.genschu.bloomooemulator.interpreter.helpers.ArgumentHelper;
import pl.genschu.bloomooemulator.interpreter.values.*;

import java.util.HashMap;
import java.util.Map;

/**
 * TimerVariable handles time-based events.
 * Emits ONTICK signal at regular intervals.
 *
 * <p>Time fields are expressed in engine-clock milliseconds (see
 * {@link pl.genschu.bloomooemulator.engine.Game#getEngineTimeMs()}), not
 * wall-clock time. Callers must pass the engine time to {@link #update(long)};
 * methods that mutate {@code lastTickTime} also read it from the engine.
 *
 * Uses a mutable TimerState to avoid recreating the record on every tick.
 **/
public record TimerVariable(
    String name,
    @InternalMutable TimerState state,
    Map<String, SignalHandler> signals
) implements Variable {

    /**
     * Mutable internal state for timer.
     */
    public static final class TimerState {
        public long elapse;
        public boolean enabled;
        public int ticks;
        public long lastTickTime;
        public int currentTickCount;

        public TimerState(long elapse, boolean enabled, int ticks, long lastTickTime, int currentTickCount) {
            this.elapse = elapse;
            this.enabled = enabled;
            this.ticks = ticks;
            this.lastTickTime = lastTickTime;
            this.currentTickCount = currentTickCount;
        }

        public TimerState() {
            this(0L, true, 0, 0L, 0);
        }

        public TimerState copy() {
            return new TimerState(elapse, enabled, ticks, lastTickTime, currentTickCount);
        }
    }

    public TimerVariable {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Variable name cannot be null or empty");
        }
        if (state == null) {
            state = new TimerState();
        }
        if (signals == null) {
            signals = Map.of();
        } else {
            signals = Map.copyOf(signals);
        }
    }

    public TimerVariable(String name) {
        this(name, new TimerState(), Map.of());
    }

    public TimerVariable(String name, long elapse) {
        this(name, new TimerState(elapse, true, 0, 0L, 0), Map.of());
    }

    // Legacy-compatible constructor
    public TimerVariable(String name, long elapse, boolean enabled, int ticks,
                         long lastTickTime, int currentTickCount, Map<String, SignalHandler> signals) {
        this(name, new TimerState(elapse, enabled, ticks, lastTickTime, currentTickCount), signals);
    }

    // ========================================
    // INTERFACE IMPLEMENTATION
    // ========================================

    @Override
    public Value value() {
        return new IntValue(state.currentTickCount);
    }

    @Override
    public VariableType type() {
        return VariableType.TIMER;
    }

    @Override
    public Variable withValue(Value newValue) {
        state.currentTickCount = ArgumentHelper.getInt(newValue);
        return this;
    }

    @Override
    public Map<String, MethodSpec> methods() {
        return METHODS;
    }

    @Override
    public Variable withSignal(String signalName, SignalHandler handler) {
        Map<String, SignalHandler> newSignals = new HashMap<>(signals);
        if (handler == null) {
            newSignals.remove(signalName);
        } else {
            newSignals.put(signalName, handler);
        }
        return new TimerVariable(name, state, newSignals);
    }

    // ========================================
    // TIMER-SPECIFIC METHODS
    // ========================================

    // Accessors for common fields
    public long elapse() { return state.elapse; }
    public boolean enabled() { return state.enabled; }
    public int ticks() { return state.ticks; }
    public long lastTickTime() { return state.lastTickTime; }
    public int currentTickCount() { return state.currentTickCount; }

    public UpdateResult update(long currentTime) {
        if (!state.enabled || state.elapse <= 0) {
            return new UpdateResult(this, false);
        }

        if (currentTime - state.lastTickTime >= state.elapse) {
            state.currentTickCount++;
            state.lastTickTime = currentTime;
            emitSignal("ONTICK", new IntValue(state.currentTickCount));

            if (state.ticks != 0 && state.currentTickCount >= state.ticks) {
                state.enabled = false;
            }
            return new UpdateResult(this, true);
        }

        return new UpdateResult(this, false);
    }

    public record UpdateResult(TimerVariable timer, boolean tickOccurred) {}

    public int getTimeFromLastTick(long engineTimeMs) {
        return (int) (engineTimeMs - state.lastTickTime);
    }

    // ========================================
    // METHODS DEFINITION
    // ========================================

    private static final Map<String, MethodSpec> METHODS = Map.ofEntries(
        Map.entry("DISABLE", MethodSpec.of((self, args, ctx) -> {
            TimerVariable thisVar = (TimerVariable) self;
            thisVar.state.enabled = false;
            return MethodResult.noReturn();
        })),

        Map.entry("ENABLE", MethodSpec.of((self, args, ctx) -> {
            TimerVariable thisVar = (TimerVariable) self;
            if (thisVar.state.enabled) {
                return MethodResult.noReturn(); // BlooMooDLL: ENABLE on already-enabled timer is a no-op
            }
            thisVar.state.currentTickCount = 0;
            thisVar.state.enabled = true;
            thisVar.state.lastTickTime = engineTimeFrom(ctx);
            return MethodResult.noReturn();
        })),

        Map.entry("GETTICKS", MethodSpec.of((self, args, ctx) -> {
            TimerVariable thisVar = (TimerVariable) self;
            return MethodResult.returns(new IntValue(thisVar.state.currentTickCount));
        })),

        Map.entry("RESET", MethodSpec.of((self, args, ctx) -> {
            TimerVariable thisVar = (TimerVariable) self;
            thisVar.state.currentTickCount = 0;
            thisVar.state.lastTickTime = engineTimeFrom(ctx);
            return MethodResult.noReturn();
        })),

        Map.entry("SET", MethodSpec.of((self, args, ctx) -> {
            TimerVariable thisVar = (TimerVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("SET requires 1 argument");
            }
            // BlooMooDLL: SET also resets the slot (zeros currentTickCount and lastFire).
            thisVar.state.ticks = ArgumentHelper.getInt(args.get(0));
            thisVar.state.currentTickCount = 0;
            thisVar.state.lastTickTime = engineTimeFrom(ctx);
            return MethodResult.noReturn();
        })),

        Map.entry("SETELAPSE", MethodSpec.of((self, args, ctx) -> {
            TimerVariable thisVar = (TimerVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("SETELAPSE requires 1 argument");
            }
            // BlooMooDLL: only the period changes; the accumulator (lastTickTime) is preserved
            // so scripts that retune ELAPSE mid-flight keep the elapsed budget intact.
            thisVar.state.elapse = ArgumentHelper.getInt(args.get(0));
            return MethodResult.noReturn();
        }))
    );

    private static long engineTimeFrom(MethodContext ctx) {
        if (ctx == null) return 0L;
        var game = ctx.getGame();
        return game == null ? 0L : game.getEngineTimeMs();
    }

    @Override
    public String toString() {
        return "TimerVariable[" + name + ", elapse=" + state.elapse + "ms, enabled=" + state.enabled +
               ", ticks=" + state.currentTickCount + "/" + (state.ticks == 0 ? "\u221e" : state.ticks) + "]";
    }
}
