package pl.genschu.bloomooemulator.interpreter.v2.values;

/**
 * Immutable boolean value.
 */
public record BoolValue(boolean value) implements Value {

    /** Constant for TRUE value to avoid allocations. */
    public static final BoolValue TRUE = new BoolValue(true);

    /** Constant for FALSE value to avoid allocations. */
    public static final BoolValue FALSE = new BoolValue(false);

    /**
     * Returns TRUE or FALSE constant based on the boolean value.
     */
    public static BoolValue of(boolean value) {
        return value ? TRUE : FALSE;
    }

    @Override
    public ValueType getType() {
        return ValueType.BOOL;
    }

    @Override
    public Object unwrap() {
        return value;
    }

    @Override
    public String toDisplayString() {
        return value ? "TRUE" : "FALSE";
    }

    /**
     * Converts this boolean to an integer (true = 1, false = 0).
     */
    public IntValue toInt() {
        return new IntValue(value ? 1 : 0);
    }

    /**
     * Converts this boolean to a string ("TRUE" or "FALSE").
     */
    public StringValue toStringValue() {
        return new StringValue(toDisplayString());
    }

    /**
     * Logical AND operation.
     */
    public BoolValue and(BoolValue other) {
        return BoolValue.of(this.value && other.value);
    }

    /**
     * Logical OR operation.
     */
    public BoolValue or(BoolValue other) {
        return BoolValue.of(this.value || other.value);
    }

    /**
     * Logical NOT operation.
     */
    public BoolValue not() {
        return BoolValue.of(!this.value);
    }
}
