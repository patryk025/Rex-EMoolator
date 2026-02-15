package pl.genschu.bloomooemulator.interpreter.variable;

import pl.genschu.bloomooemulator.interpreter.values.NullValue;
import pl.genschu.bloomooemulator.interpreter.values.Value;

/**
 * Result of a method call on a Variable.
 */
public record MethodResult(
    Value returnValue   // null => NullValue.INSTANCE
) {
    /**
     * Method returns a value (most common case).
     * Use for both read-only methods (GET) and mutating methods (ADD, SET, etc.).
     */
    public static MethodResult returns(Value v) {
        return new MethodResult(v);
    }

    /**
     * Method doesn't return anything meaningful.
     * Use for void-style mutating methods.
     */
    public static MethodResult noReturn() {
        return new MethodResult(NullValue.INSTANCE);
    }

    /**
     * Gets the return value, converting null to NullValue.INSTANCE.
     */
    public Value getReturnValue() {
        return returnValue != null ? returnValue : NullValue.INSTANCE;
    }
}
