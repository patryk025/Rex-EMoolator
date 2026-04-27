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
     * Returns 0 if the string is not a valid integer.
     */
    @Override
    public IntValue toInt() {
        try {
            String numericPrefix = value().replaceFirst("^([-+]?\\d*\\.?\\d+).*", "$1");
            double value = Double.parseDouble(numericPrefix);
            return new IntValue((int) value);
        }
        catch(NumberFormatException e) {
            return new IntValue(0);
        }
    }

    /**
     * Attempts to convert this string to a double.
     * Supports scientific notation with e/E/d/D (yeah, for nobody knows reason "d" is also correct).
     * Examples: "123.55e12", "123.55D12", "1.5d-3"
     * Returns 0 if the string is not a valid double.
     */
    @Override
    public DoubleValue toDouble() {
        try {
            // Match number with optional scientific notation (e/E/d/D)
            String numericPrefix = value().replaceFirst(
                "^([-+]?\\d*\\.?\\d+([eEdD][-+]?\\d+)?).*", "$1");
            // Replace 'd' or 'D' with 'e' for Java's parseDouble
            String normalized = numericPrefix.replaceAll("[dD]", "e");
            double result = Double.parseDouble(normalized);
            return new DoubleValue(result);
        }
        catch(NumberFormatException e) {
            return new DoubleValue(0);
        }
    }

    /**
     * Converts this string to a boolean.
     * "TRUE" (case-insensitive) = true, everything else = false.
     */
    public BoolValue toBool() {
        return new BoolValue(value.equalsIgnoreCase("TRUE"));
    }

    /**
     * Converts this string to a string value (no-op).
     */
    public StringValue toStringValue() {
        return this;
    }
}
