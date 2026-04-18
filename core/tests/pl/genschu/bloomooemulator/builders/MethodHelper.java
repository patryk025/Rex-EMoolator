package pl.genschu.bloomooemulator.builders;

import pl.genschu.bloomooemulator.interpreter.context.Context;
import pl.genschu.bloomooemulator.interpreter.runtime.ASTInterpreter;
import pl.genschu.bloomooemulator.interpreter.runtime.ExecutionContext;
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
        return new MethodContext() {
            @Override
            public Variable getVariable(String name) {
                return context.getVariable(name);
            }

            @Override
            public void setVariable(String name, Variable variable) {
                context.setVariable(name, variable);
            }

            @Override
            public boolean removeVariable(String name) {
                return context.removeVariableInHierarchy(name);
            }

            @Override
            public boolean updateVariable(String name, Variable variable) {
                return context.updateVariableInHierarchy(name, variable);
            }

            @Override
            public pl.genschu.bloomooemulator.engine.Game getGame() {
                return context.getGame();
            }

            @Override
            public Value runBehaviour(String frameName, Variable thisVar, BehaviourVariable behaviour, List<Value> args) {
                ExecutionContext exec = context.exec();
                exec.pushFrame(frameName, behaviour.name(), null);
                try {
                    if (args != null && !args.isEmpty()) {
                        for (int i = 0; i < args.size(); i++) {
                            exec.setLocal("$" + (i + 1), valueToVariable("$" + (i + 1), args.get(i)));
                        }
                    }
                    if (thisVar != null) {
                        exec.setThis(thisVar);
                    }
                    ASTInterpreter interpreter = new ASTInterpreter(context);
                    interpreter.execute(behaviour.ast());
                    if (interpreter.getPendingReturnValue() != null) {
                        return interpreter.getPendingReturnValue();
                    }
                    return NullValue.INSTANCE;
                } finally {
                    exec.popFrame();
                }
            }

            @Override
            public pl.genschu.bloomooemulator.interpreter.context.CloneRegistry clones() {
                return context.clones();
            }

            @Override
            public Context context() {
                return context;
            }
        };
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

    private static Variable valueToVariable(String name, Value value) {
        return switch (value) {
            case IntValue v    -> new IntegerVariable(name, v.value());
            case DoubleValue v -> new DoubleVariable(name, v.value());
            case StringValue v -> new StringVariable(name, v.value());
            case BoolValue v   -> new BoolVariable(name, v.value());
            default            -> new StringVariable(name, value.toDisplayString());
        };
    }

    public static Value callWithEffects(Context context, Variable variable, String methodName, Value... arguments) {
        return callWithContext(context, variable, methodName, arguments);
    }

    public static Value callWithEffects(Context context, String variableName, String methodName, Value... arguments) {
        return callWithContext(context, variableName, methodName, arguments);
    }
}
