package pl.genschu.bloomooemulator.interpreter.values;

/**
 * Base interface for all runtime values in the interpreter.
 * This is a sealed interface to ensure type safety and enable exhaustive pattern matching.
 *
 * Value objects are immutable and represent actual runtime values,
 * unlike the old system which mixed Objects, Expressions, and Variables chaotically.
 */
public sealed interface Value permits
    IntValue,
    DoubleValue,
    StringValue,
    BoolValue,
    NullValue,
    VariableRef,
    VariableValue,
    BehaviourValue {

    /**
     * Returns the type of this value.
     */
    ValueType getType();

    /**
     * Unwraps this value to a Java object.
     * Use sparingly - prefer pattern matching instead.
     */
    Object unwrap();

    /**
     * Returns a string representation suitable for display/debugging.
     */
    String toDisplayString();
}
