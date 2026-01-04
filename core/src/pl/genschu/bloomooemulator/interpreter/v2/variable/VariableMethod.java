package pl.genschu.bloomooemulator.interpreter.v2.variable;

import pl.genschu.bloomooemulator.interpreter.v2.values.Value;

import java.util.List;

/**
 * Represents a method that can be called on a variable.
 *
 * Example:
 *   IntVariable.ADD(IntValue) -> IntVariable
 *   StringVariable.GET(IntValue, IntValue) -> StringVariable
 */
@FunctionalInterface
public interface VariableMethod {
    /**
     * Executes this method on the given variable with arguments.
     *
     * @param self The variable this method is called on
     * @param arguments The arguments passed to the method
     * @return The result (usually a new Variable)
     */
    Variable execute(Variable self, List<Value> arguments);
}
