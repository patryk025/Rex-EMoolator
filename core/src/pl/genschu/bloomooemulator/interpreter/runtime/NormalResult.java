package pl.genschu.bloomooemulator.interpreter.runtime;

import pl.genschu.bloomooemulator.interpreter.values.Value;
import pl.genschu.bloomooemulator.interpreter.values.NullValue;

/**
 * Represents normal execution - no control flow change.
 * Execution continues to the next statement.
 */
public record NormalResult(Value value) implements ExecutionResult {

    public NormalResult {
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
        return true;
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
        return "NormalResult[" + value.toDisplayString() + "]";
    }
}
