package pl.genschu.bloomooemulator.interpreter.variable;

import pl.genschu.bloomooemulator.interpreter.runtime.effects.Effect;
import pl.genschu.bloomooemulator.interpreter.values.NullValue;
import pl.genschu.bloomooemulator.interpreter.values.Value;

import java.util.List;

/**
 * Result of a method call on a Variable.
 *
 * Separates two concerns:
 * - newSelf: the new state of the variable (null = no change)
 * - returnValue: the value returned to the expression (null => NullValue.INSTANCE)
 */
public record MethodResult(
    Variable newSelf,    // null = no change to variable
    Value returnValue,   // null => NullValue.INSTANCE
    List<Effect> effects // side effects requested by method
) {
    public MethodResult {
        if (effects == null) {
            effects = List.of();
        }
    }
    /**
     * Method doesn't change the variable, just returns a value.
     * Example: GET()
     */
    public static MethodResult noChange(Value v) {
        return new MethodResult(null, v, List.of());
    }

    /**
     * Method changes the variable but doesn't return anything meaningful.
     * Example: CLEAR()
     */
    public static MethodResult sets(Variable v) {
        return new MethodResult(v, NullValue.INSTANCE, List.of());
    }

    /**
     * Method changes the variable AND returns a value.
     * Example: ADD(5) - changes X to X+5 and returns X+5
     */
    public static MethodResult setsAndReturns(Variable v, Value ret) {
        return new MethodResult(v, ret, List.of());
    }

    /**
     * Convenience: sets variable and returns its value.
     * Most common case for arithmetic operations.
     */
    public static MethodResult setsAndReturnsValue(Variable v) {
        return new MethodResult(v, v.value(), List.of());
    }

    /**
     * Method doesn't change the variable, just requests side effects.
     */
    public static MethodResult effects(List<Effect> effects) {
        return new MethodResult(null, NullValue.INSTANCE, effects);
    }

    /**
     * Gets the return value, converting null to NullValue.INSTANCE.
     */
    public Value getReturnValue() {
        return returnValue != null ? returnValue : NullValue.INSTANCE;
    }

    /**
     * Returns true if the variable should be updated in context.
     */
    public boolean hasNewState() {
        return newSelf != null;
    }
}
