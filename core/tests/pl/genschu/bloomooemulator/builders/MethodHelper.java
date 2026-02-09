package pl.genschu.bloomooemulator.builders;

import pl.genschu.bloomooemulator.interpreter.context.Context;
import pl.genschu.bloomooemulator.interpreter.runtime.effects.Effect;
import pl.genschu.bloomooemulator.interpreter.values.Value;
import pl.genschu.bloomooemulator.interpreter.variable.MethodResult;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;

import java.util.List;

/**
 * Helper class for executing methods with effects in tests.
 *
 * Since Variable.callMethod() returns a MethodResult with effects that are normally
 * executed by ASTInterpreter, this helper provides a way to execute those effects
 * in unit tests.
 */
public final class MethodHelper {

    private MethodHelper() {}

    /**
     * Calls a method on a variable and executes all resulting effects.
     *
     * @param context The context to execute effects in
     * @param variable The variable to call the method on
     * @param methodName The method name
     * @param arguments The method arguments
     * @return The return value from the method
     */
    public static Value callWithEffects(Context context, Variable variable, String methodName, Value... arguments) {
        MethodResult result = variable.callMethod(methodName, arguments);

        // Variable is mutated in-place via MutableValue holders, no need to update context.

        // Execute effects
        Value returnValue = result.getReturnValue();
        for (Effect effect : result.effects()) {
            Value effectValue = effect.apply(context, variable, arguments != null ? List.of(arguments) : List.of());
            if (effectValue != null) {
                returnValue = effectValue;
            }
        }

        return returnValue;
    }

    /**
     * Calls a method on a variable and executes all resulting effects.
     *
     * @param context The context to execute effects in
     * @param variableName The name of the variable in context
     * @param methodName The method name
     * @param arguments The method arguments
     * @return The return value from the method
     */
    public static Value callWithEffects(Context context, String variableName, String methodName, Value... arguments) {
        Variable variable = context.getVariable(variableName);
        if (variable == null) {
            throw new IllegalArgumentException("Variable not found: " + variableName);
        }
        return callWithEffects(context, variable, methodName, arguments);
    }
}
