package pl.genschu.bloomooemulator.interpreter.v2.runtime;

import pl.genschu.bloomooemulator.interpreter.v2.values.Value;
import pl.genschu.bloomooemulator.interpreter.v2.values.NullValue;

/**
 * Represents a @RETURN - returns from the current behaviour.
 * Should be propagated up the call stack until the behaviour boundary is reached.
 */
public record ReturnResult(Value value) implements ExecutionResult {

    public ReturnResult {
        if (value == null) {
            value = NullValue.INSTANCE;
        }
    }

    @Override
    public Value getValue() {
        return value;
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
        return true;
    }

    @Override
    public String toString() {
        return "ReturnResult[" + value.toDisplayString() + "]";
    }
}
