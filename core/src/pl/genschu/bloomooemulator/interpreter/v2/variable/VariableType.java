package pl.genschu.bloomooemulator.interpreter.v2.variable;

/**
 * Enumeration of variable types.
 * Replaces string comparisons in v1.
 */
public enum VariableType {
    INTEGER,
    DOUBLE,
    STRING,
    BOOLEAN,
    BEHAVIOUR,
    ARRAY,
    ANIMO,
    IMAGE,
    SOUND,
    VECTOR,
    FONT,
    BUTTON,
    TEXT,
    SEQUENCE,
    GROUP,
    TIMER,
    MOUSE,
    KEYBOARD,
    CONDITION,
    CANVAS_OBSERVER,
    WORLD;

    /**
     * Returns true if this is a primitive type.
     */
    public boolean isPrimitive() {
        return this == INTEGER || this == DOUBLE || this == STRING || this == BOOLEAN;
    }
}
