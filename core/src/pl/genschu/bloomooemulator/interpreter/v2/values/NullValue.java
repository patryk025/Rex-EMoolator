package pl.genschu.bloomooemulator.interpreter.v2.values;

/**
 * Represents a null/void value.
 * Singleton instance to avoid allocations.
 */
public final class NullValue implements Value {

    /** The single instance of NullValue. */
    public static final NullValue INSTANCE = new NullValue();

    // Private constructor to enforce singleton
    private NullValue() {}

    @Override
    public ValueType getType() {
        return ValueType.NULL;
    }

    @Override
    public Object unwrap() {
        return null;
    }

    @Override
    public String toDisplayString() {
        return "NULL";
    }

    @Override
    public String toString() {
        return "NullValue";
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof NullValue;
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
