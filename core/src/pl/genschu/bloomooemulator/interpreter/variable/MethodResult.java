package pl.genschu.bloomooemulator.interpreter.variable;

import pl.genschu.bloomooemulator.interpreter.values.NullValue;
import pl.genschu.bloomooemulator.interpreter.values.Value;

/**
 * Result of a method call on a Variable.
 *
 * Separates two concerns:
 * - newSelf: the new state of the variable (null = no change)
 * - returnValue: the value returned to the expression (null => NullValue.INSTANCE)
 */
public record MethodResult(
    Variable newSelf,    // null = no change to variable
    Value returnValue    // null => NullValue.INSTANCE
) {
    /**
     * Method doesn't change the variable, just returns a value.
     * Example: GET()
     */
    public static MethodResult noChange(Value v) {
        return new MethodResult(null, v);
    }

    /**
     * Method changes the variable but doesn't return anything meaningful.
     * Example: CLEAR()
     */
    public static MethodResult sets(Variable v) {
        return new MethodResult(v, NullValue.INSTANCE);
    }

    /**
     * Method changes the variable AND returns a value.
     * Example: ADD(5) - changes X to X+5 and returns X+5
     */
    public static MethodResult setsAndReturns(Variable v, Value ret) {
        return new MethodResult(v, ret);
    }

    /**
     * Convenience: sets variable and returns its value.
     * Most common case for arithmetic operations.
     */
    public static MethodResult setsAndReturnsValue(Variable v) {
        return new MethodResult(v, v.value());
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
