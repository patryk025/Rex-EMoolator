package pl.genschu.bloomooemulator.interpreter.v2.values;

/**
 * Immutable integer value.
 */
public record IntValue(int value) implements Value {

    @Override
    public ValueType getType() {
        return ValueType.INT;
    }

    @Override
    public Object unwrap() {
        return value;
    }

    @Override
    public String toDisplayString() {
        return String.valueOf(value);
    }

    /**
     * Converts this int to a double value.
     */
    public DoubleValue toDouble() {
        return new DoubleValue(value);
    }

    /**
     * Converts this int to a string value.
     */
    public StringValue toStringValue() {
        return new StringValue(String.valueOf(value));
    }

    /**
     * Converts this int to a boolean value (0 = false, non-zero = true).
     */
    public BoolValue toBool() {
        return new BoolValue(value != 0);
    }
}
