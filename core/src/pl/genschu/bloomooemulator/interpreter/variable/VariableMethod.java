package pl.genschu.bloomooemulator.interpreter.variable;

import pl.genschu.bloomooemulator.interpreter.values.Value;

import java.util.List;

/**
 * Represents a method that can be called on a variable.
 *
 * Example:
 *   IntVariable.ADD(IntValue) -> MethodResult(newVar, value)
 *   StringVariable.GET(IntValue, IntValue) -> MethodResult(null, value)
 */
@FunctionalInterface
public interface VariableMethod {
    /**
     * Executes this method on the given variable with arguments.
     *
     * @param self The variable this method is called on
     * @param arguments The arguments passed to the method
     * @return MethodResult containing new state and return value
     */
    MethodResult execute(Variable self, List<Value> arguments);
}
