package pl.genschu.bloomooemulator.interpreter.runtime.effects;

import pl.genschu.bloomooemulator.interpreter.context.Context;
import pl.genschu.bloomooemulator.interpreter.variable.ArgKind;
import pl.genschu.bloomooemulator.interpreter.variable.MethodResult;
import pl.genschu.bloomooemulator.interpreter.variable.MethodSpec;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.interpreter.values.StringValue;
import pl.genschu.bloomooemulator.interpreter.values.Value;
import pl.genschu.bloomooemulator.interpreter.values.VariableRef;
import pl.genschu.bloomooemulator.interpreter.values.VariableValue;

import java.util.ArrayList;
import java.util.List;

/**
 * Effect: run a method on a variable in the current context.
 */
public final class RunMethodEffect implements Effect {
    private final String variableName;
    private final String methodName;
    private final List<Value> arguments;

    public RunMethodEffect(String variableName, String methodName, List<Value> arguments) {
        this.variableName = variableName;
        this.methodName = methodName;
        this.arguments = arguments != null ? List.copyOf(arguments) : List.of();
    }

    @Override
    public Value apply(Context context, Variable self, List<Value> originalArguments) {
        Variable target = context.getVariable(variableName);
        if (target == null) {
            throw new IllegalArgumentException("Variable not found: " + variableName);
        }

        MethodSpec spec = target.methodSpecs().get(methodName.toUpperCase());
        if (spec == null || spec.method() == null) {
            throw new IllegalArgumentException("Method not found: " + methodName + " on " + target.type());
        }

        List<Value> resolvedArgs = resolveArguments(context, spec, arguments);
        MethodResult result = spec.method().execute(target, resolvedArgs);

        if (result.hasNewState()) {
            boolean updated = context.updateVariableInHierarchy(target.name(), result.newSelf());
            if (!updated) {
                throw new IllegalStateException("Failed to update variable after method call: " + target.name());
            }
        }

        Value returnValue = result.getReturnValue();
        for (Effect effect : result.effects()) {
            Value effectValue = effect.apply(context, target, resolvedArgs);
            if (effectValue != null) {
                returnValue = effectValue;
            }
        }

        return returnValue;
    }

    private List<Value> resolveArguments(Context context, MethodSpec spec, List<Value> args) {
        List<Value> resolved = new ArrayList<>(args.size());
        for (int i = 0; i < args.size(); i++) {
            Value arg = args.get(i);
            ArgKind kind = spec.kindAt(i);
            if (kind == ArgKind.VAR || kind == ArgKind.VAR_REF) {
                resolved.add(resolveVariableArgument(context, arg, kind));
            } else {
                resolved.add(arg);
            }
        }
        return resolved;
    }

    private Value resolveVariableArgument(Context context, Value argValue, ArgKind argKind) {
        if (argValue instanceof VariableValue) {
            return argValue;
        }
        if (argValue instanceof VariableRef ref) {
            Variable resolved = context.getVariable(ref.name());
            if (resolved == null) {
                throw new IllegalArgumentException("Variable not found: " + ref.name());
            }
            return new VariableValue(resolved);
        }
        if (argValue instanceof StringValue str) {
            if (argKind == ArgKind.VAR_REF) {
                Variable resolved = context.getVariable(str.value());
                if (resolved == null) {
                    throw new IllegalArgumentException("Variable not found: " + str.value());
                }
                return new VariableValue(resolved);
            }
        }
        if (argKind == ArgKind.VAR) {
            throw new IllegalArgumentException("Expected variable reference argument");
        }
        return argValue;
    }
}
