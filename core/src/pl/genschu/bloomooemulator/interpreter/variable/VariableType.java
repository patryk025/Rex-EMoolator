package pl.genschu.bloomooemulator.interpreter.variable;

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
    WORLD,
    DATABASE,
    STRUCT,
    DATABASE_CURSOR,
    CLASS,
    INSTANCE,
    CNVLOADER;

    /**
     * Returns true if this is a primitive type.
     */
    public boolean isPrimitive() {
        return this == INTEGER || this == DOUBLE || this == STRING || this == BOOLEAN;
    }
}
