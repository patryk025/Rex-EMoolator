package pl.genschu.bloomooemulator.builders;

import pl.genschu.bloomooemulator.interpreter.context.Context;
import pl.genschu.bloomooemulator.interpreter.runtime.ASTInterpreter;
import pl.genschu.bloomooemulator.interpreter.values.*;
import pl.genschu.bloomooemulator.interpreter.variable.*;

import java.util.List;

/**
 * Helper class for executing variable methods with context in tests.
 *
 * Creates a MethodContext from a Context and passes it to the variable's callMethod.
 */
public final class MethodHelper {

    private MethodHelper() {}

    /**
     * Creates a MethodContext adapter from a Context for test use.
     */
    public static MethodContext createMethodContext(Context context) {
        return new ASTInterpreter(context).getMethodContext();
    }

    /**
     * Calls a method on a variable with a MethodContext created from Context.
     */
    public static Value callWithContext(Context context, Variable variable, String methodName, Value... arguments) {
        MethodContext ctx = createMethodContext(context);
        MethodResult result = variable.callMethod(methodName,
                arguments != null ? List.of(arguments) : List.of(), ctx);
        return result.getReturnValue();
    }

    /**
     * Calls a method on a named variable with a MethodContext created from Context.
     */
    public static Value callWithContext(Context context, String variableName, String methodName, Value... arguments) {
        Variable variable = context.getVariable(variableName);
        if (variable == null) {
            throw new IllegalArgumentException("Variable not found: " + variableName);
        }
        return callWithContext(context, variable, methodName, arguments);
    }

}
