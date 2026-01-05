package pl.genschu.bloomooemulator.interpreter.v2.variable;

import pl.genschu.bloomooemulator.interpreter.v2.values.Value;

import java.util.List;
import java.util.Map;

/**
 * V2 Variable - Clean, immutable, type-safe!
 *
 * Key differences from v1:
 * - Immutable (returns new instance on change)
 * - Type-safe (sealed interface)
 * - Clean API (no getAttribute("VALUE"))
 * - Built-in signal support
 *
 * Why immutable?
 * - No side effects
 * - Thread-safe automatically
 * - Easy to test
 * - Easy to reason about
 *
 * Why sealed?
 * - Exhaustive pattern matching
 * - Compiler enforces handling all types
 * - No unknown subtypes
 */
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
    Variable callMethod(String methodName, List<Value> arguments);

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
    void emitSignal(String signalName, Value argument);
}
