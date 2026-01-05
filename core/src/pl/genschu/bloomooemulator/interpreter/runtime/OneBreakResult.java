package pl.genschu.bloomooemulator.interpreter.runtime;

import pl.genschu.bloomooemulator.interpreter.values.Value;
import pl.genschu.bloomooemulator.interpreter.values.NullValue;

/**
 * Represents a @ONEBREAK - breaks out of the immediately enclosing loop only.
 * Should NOT be propagated beyond the current loop.
 */
public final class OneBreakResult implements ExecutionResult {

    /** Singleton instance to avoid allocations. */
    public static final OneBreakResult INSTANCE = new OneBreakResult();

    private OneBreakResult() {}

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
        return false;
    }

    @Override
    public boolean isReturn() {
        return false;
    }

    @Override
    public String toString() {
        return "OneBreakResult";
    }
}
