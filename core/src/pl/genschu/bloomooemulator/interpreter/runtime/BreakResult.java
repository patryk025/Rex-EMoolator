package pl.genschu.bloomooemulator.interpreter.runtime;

import pl.genschu.bloomooemulator.interpreter.values.Value;
import pl.genschu.bloomooemulator.interpreter.values.NullValue;

/**
 * Represents a @BREAK - breaks out of ALL enclosing loops.
 * Must be propagated up the call stack until all loops are exited.
 */
public final class BreakResult implements ExecutionResult {

    /** Singleton instance to avoid allocations. */
    public static final BreakResult INSTANCE = new BreakResult();

    private BreakResult() {}

    @Override
    public Value getValue() {
        return NullValue.INSTANCE;
    }

    @Override
    public boolean shouldContinue() {
        return false;
    }

    @Override
    public boolean isBreakAll() {
        return true;
    }

    @Override
    public boolean isReturn() {
        return false;
    }

    @Override
    public String toString() {
        return "BreakResult";
    }
}
