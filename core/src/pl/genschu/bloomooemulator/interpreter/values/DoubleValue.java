package pl.genschu.bloomooemulator.interpreter.values;

/**
 * Immutable double value.
 */
public record DoubleValue(double value) implements Value {

    @Override
    public ValueType getType() {
        return ValueType.DOUBLE;
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
     * Converts this double to an int value (truncates).
     */
    public IntValue toInt() {
        return new IntValue((int) value);
    }

    /**
     * Converts this double to a string value.
     */
    public StringValue toStringValue() {
        return new StringValue(String.valueOf(value));
    }

    /**
     * Converts this double to a boolean value (0.0 = false, non-zero = true).
     */
    public BoolValue toBool() {
        return new BoolValue(value != 0.0);
    }
}
