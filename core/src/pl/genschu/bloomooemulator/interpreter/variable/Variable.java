package pl.genschu.bloomooemulator.interpreter.variable;

import pl.genschu.bloomooemulator.interpreter.values.Value;

import java.util.List;
import java.util.Map;

public sealed interface Variable permits
        IntegerVariable,
        DoubleVariable,
        StringVariable,
        BoolVariable,
        BehaviourVariable,
        ArrayVariable {

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
     *   Variable result = x.callMethod("ADD", List.of(new IntValue(5)));
     *   // result is IntVariable("x", 15, ...)
     */
    default Variable callMethod(String methodName, List<Value> arguments) {
        Map<String, VariableMethod> availableMethods = methods();
        VariableMethod method = availableMethods != null
                ? availableMethods.get(methodName.toUpperCase())
                : null;

        if (method == null) {
            throw new IllegalArgumentException("Method not found: " + methodName + " on " + type() + " variable");
        }

        return method.execute(this, arguments == null ? List.of() : arguments);
    }

    /**
     * Returns available methods for this variable type.
     */
    Map<String, VariableMethod> methods();

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
}
