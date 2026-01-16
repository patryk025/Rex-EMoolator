package pl.genschu.bloomooemulator.interpreter.variable;

/**
 * Enumeration of variable types.
 * Replaces string comparisons in old interpreter.
 */
public enum VariableType {
    ANIMO,
    ARRAY,
    BEHAVIOUR,
    BOOLEAN,
    BUTTON,
    CANVAS_OBSERVER,
    CLASS,
    CNVLOADER,
    CONDITION,
    COMPLEXCONDITION,
    DATABASE,
    DATABASE_CURSOR,
    DOUBLE,
    EXPRESSION,
    FILTER,
    FONT,
    GROUP,
    IMAGE,
    INSTANCE, // artificial type for class instances
    INTEGER,
    JOYSTICK,
    KEYBOARD,
    MOUSE,
    MULTIARRAY,
    PATTERN,
    SEQUENCE,
    SOUND,
    STATICFILTER,
    STRING,
    STRUCT,
    TEXT,
    TIMER,
    VECTOR,
    VIRTUALGRAPHICSOBJECT,
    WORLD;

    /**
     * Returns true if this is a primitive type.
     */
    public boolean isPrimitive() {
        return this == INTEGER || this == DOUBLE || this == STRING || this == BOOLEAN;
    }
}
