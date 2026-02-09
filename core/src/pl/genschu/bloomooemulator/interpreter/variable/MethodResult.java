package pl.genschu.bloomooemulator.interpreter.variable;

import pl.genschu.bloomooemulator.interpreter.runtime.effects.Effect;
import pl.genschu.bloomooemulator.interpreter.values.NullValue;
import pl.genschu.bloomooemulator.interpreter.values.Value;

import java.util.List;

/**
 * Result of a method call on a Variable.
 */
public record MethodResult(
    Value returnValue,   // null => NullValue.INSTANCE
    List<Effect> effects // side effects requested by method
) {
    public MethodResult {
        if (effects == null) {
            effects = List.of();
        }
    }

    /**
     * Method returns a value (most common case).
     * Use for both read-only methods (GET) and mutating methods (ADD, SET, etc.).
     */
    public static MethodResult returns(Value v) {
        return new MethodResult(v, List.of());
    }

    /**
     * Method doesn't return anything meaningful.
     * Use for void-style mutating methods.
     */
    public static MethodResult noReturn() {
        return new MethodResult(NullValue.INSTANCE, List.of());
    }

    /**
     * Method doesn't change the variable, just requests side effects.
     */
    public static MethodResult effects(List<Effect> effects) {
        return new MethodResult(NullValue.INSTANCE, effects);
    }

    /**
     * Gets the return value, converting null to NullValue.INSTANCE.
     */
    public Value getReturnValue() {
        return returnValue != null ? returnValue : NullValue.INSTANCE;
    }
}
