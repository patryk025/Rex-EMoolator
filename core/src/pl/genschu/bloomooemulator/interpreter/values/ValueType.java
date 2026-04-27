package pl.genschu.bloomooemulator.interpreter.values;

/**
 * Enumeration of all possible value types in the interpreter.
 * This makes type checking explicit and avoids string comparisons.
 */
public enum ValueType {
    INTEGER,
    DOUBLE,
    STRING,
    BOOL,
    NULL,
    VARIABLE_REF,
    VARIABLE,
    BEHAVIOUR;

    /**
     * Returns true if this is a primitive type (int, double, string, bool).
     */
    public boolean isPrimitive() {
        return this == INTEGER || this == DOUBLE || this == STRING || this == BOOL;
    }

    /**
     * Returns true if this is a numeric type (int or double).
     */
    public boolean isNumeric() {
        return this == INTEGER || this == DOUBLE;
    }
}
