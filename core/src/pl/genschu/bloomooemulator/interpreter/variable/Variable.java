package pl.genschu.bloomooemulator.interpreter.variable;

import pl.genschu.bloomooemulator.interpreter.helpers.ArgumentHelper;
import pl.genschu.bloomooemulator.interpreter.runtime.effects.CloneEffect;
import pl.genschu.bloomooemulator.interpreter.values.IntValue;
import pl.genschu.bloomooemulator.interpreter.values.Value;

import java.util.List;
import java.util.Map;

public sealed interface Variable permits
        IntegerVariable,
        DoubleVariable,
        StringVariable,
        BoolVariable,
        BehaviourVariable,
        ArrayVariable,
        DatabaseVariable,
        StructVariable,
        DatabaseCursorVariable,
        ClassVariable,
        InstanceVariable,
        CNVLoaderVariable,
        TimerVariable,
        VectorVariable,
        RandVariable,
        GroupVariable,
        ExpressionVariable,
        ConditionVariable,
        ComplexConditionVariable,
        ApplicationVariable,
        EpisodeVariable,
        SceneVariable {

    /**
     * Returns the name of this variable.
     */
    String name();

    /**
     * Returns the current value as a v2 Value.
     */
    Value value();

    /**
     * Returns the type of this variable.
     */
    VariableType type();

    /**
     * Creates a new Variable with the given value.
     * This is the PRIMARY way to "change" a variable (returns new instance).
     *
     * Example:
     *   IntVariable x = new IntVariable("x", 10, ...);
     *   IntVariable x2 = (IntVariable) x.withValue(new IntValue(20));
     *   // x still has value 10, x2 has value 20!
     */
    Variable withValue(Value newValue);

    /**
     * Calls a method on this variable and returns the result.
     *
     * Example:
     *   IntVariable x = new IntVariable("x", 10, ...);
     *   MethodResult result = x.callMethod("ADD", List.of(new IntValue(5)));
     *   // result.newSelf() is IntVariable("x", 15, ...)
     *   // result.returnValue() is IntValue(15)
     */
    default MethodResult callMethod(String methodName, List<Value> arguments) {
        Map<String, MethodSpec> availableMethods = methods();
        MethodSpec spec = availableMethods != null
                ? availableMethods.get(methodName.toUpperCase())
                : null;

        if (spec == null || spec.method() == null) {
            MethodSpec global = globalMethods().get(methodName);
            if (global == null || global.method() == null) {
                throw new IllegalArgumentException("Method not found: " + methodName + " on " + type() + " variable");
            }
            return global.method().execute(this, arguments == null ? List.of() : arguments);
        }

        return spec.method().execute(this, arguments == null ? List.of() : arguments);
    }

    /**
     * Global methods available on all variable types.
     */
    Map<String, MethodSpec> GLOBAL_METHODS = Map.ofEntries(
            Map.entry("CLONE", MethodSpec.of((self, args) -> {
                int amount = 1;
                if (args != null && !args.isEmpty()) {
                    amount = ArgumentHelper.getInt(args.get(0));
                }
                return MethodResult.effects(List.of(new CloneEffect(amount)));
            })),

            Map.entry("GETCLONEINDEX", MethodSpec.of((self, args) -> {
                String name = self.name();
                int idx = 0;
                int pos = name.lastIndexOf('_');
                if (pos > -1 && pos < name.length() - 1) {
                    try {
                        idx = Integer.parseInt(name.substring(pos + 1));
                    } catch (NumberFormatException ignored) {
                        idx = 0;
                    }
                }
                return MethodResult.noChange(new IntValue(idx));
            }))
    );

    /**
     * Returns available methods for this variable type.
     */
    Map<String, MethodSpec> methods();

    /**
     * Returns available global methods for all variable types.
     */
    default Map<String, MethodSpec> globalMethods() {
        return GLOBAL_METHODS;
    }

    /**
     * Returns signal handlers attached to this variable.
     */
    Map<String, SignalHandler> signals();

    /**
     * Creates a new Variable with an additional signal handler.
     */
    Variable withSignal(String signalName, SignalHandler handler);

    /**
     * Emits a signal on this variable.
     * This executes the signal handler if one is registered.
     */
    default void emitSignal(String signalName, Value... arguments) {
        Map<String, SignalHandler> registeredSignals = signals();
        SignalHandler handler = registeredSignals != null ? registeredSignals.get(signalName) : null;
        if (handler != null) {
            handler.handle(this, signalName, arguments);
        }
    }

    /**
     * Creates a copy of this variable with a new name.
     */
    default Variable copyAs(String newName) {
        throw new UnsupportedOperationException("copyAs not implemented for " + type() + " variable");
    }
}
