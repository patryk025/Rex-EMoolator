package pl.genschu.bloomooemulator.interpreter.values;

/**
 * Immutable string value.
 */
public record StringValue(String value) implements Value {

    public StringValue {
        // Compact constructor - null safety
        if (value == null) {
            value = "";
        }
    }

    @Override
    public ValueType getType() {
        return ValueType.STRING;
    }

    @Override
    public Object unwrap() {
        return value;
    }

    @Override
    public String toDisplayString() {
        return value;
    }

    /**
     * Returns the length of this string.
     */
    public int length() {
        return value.length();
    }

    /**
     * Returns true if this string is empty.
     */
    public boolean isEmpty() {
        return value.isEmpty();
    }

    /**
     * Attempts to convert this string to an integer.
     * Returns null if the string is not a valid integer.
     */
    public IntValue tryParseInt() {
        try {
            return new IntValue(Integer.parseInt(value));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Attempts to convert this string to a double.
     * Returns null if the string is not a valid double.
     */
    public DoubleValue tryParseDouble() {
        try {
            return new DoubleValue(Double.parseDouble(value));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Converts this string to a boolean.
     * "TRUE" (case-insensitive) = true, everything else = false.
     */
    public BoolValue toBool() {
        return new BoolValue(value.equalsIgnoreCase("TRUE"));
    }
}
