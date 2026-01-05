package pl.genschu.bloomooemulator.interpreter.values;

/**
 * Enumeration of all possible value types in the interpreter.
 * This makes type checking explicit and avoids string comparisons.
 */
public enum ValueType {
    INT,
    DOUBLE,
    STRING,
    BOOL,
    NULL,
    VARIABLE_REF,
    BEHAVIOUR;

    /**
     * Returns true if this is a primitive type (int, double, string, bool).
     */
    public boolean isPrimitive() {
        return this == INT || this == DOUBLE || this == STRING || this == BOOL;
    }

    /**
     * Returns true if this is a numeric type (int or double).
     */
    public boolean isNumeric() {
        return this == INT || this == DOUBLE;
    }
}
