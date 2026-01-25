package pl.genschu.bloomooemulator.interpreter.helpers;

import pl.genschu.bloomooemulator.interpreter.values.*;

public class ArgumentHelper {
    /**
     * Converts Value to int with type coercion.
     * Helper for method implementations.
     */
    public static int getInt(Value value) {
        return switch (value) {
            case IntValue v -> v.value();
            case DoubleValue v -> v.toInt().value();
            case StringValue v -> v.tryParseInt().value();
            case BoolValue v -> v.toInt().value();
            case VariableValue v -> getInt(v.variable().value());
            default -> 0;
        };
    }

    /**
     * Converts Value to boolean with type coercion.
     * Helper for method implementations.
     */
    public static boolean getBoolean(Value value) {
        return switch (value) {
            case IntValue v -> v.toBool().value();
            case DoubleValue v -> v.toBool().value();
            case StringValue v -> v.toBool().value();
            case BoolValue v -> v.value();
            case VariableValue v -> getBoolean(v.variable().value());
            default -> false;
        };
    }

    /**
     * Converts Value to double with type coercion.
     * Helper for method implementations.
     */
    public static double getDouble(Value value) {
        return switch (value) {
            case IntValue v -> v.toDouble().value();
            case DoubleValue v -> v.value();
            case StringValue v -> v.tryParseDouble().value();
            case BoolValue v -> v.toDouble().value();
            case VariableValue v -> getDouble(v.variable().value());
            default -> 0.0;
        };
    }

    /**
     * Converts Value to String with type coercion.
     * Helper for method implementations.
     */
    public static String getString(Value value) {
        return switch (value) {
            case IntValue v -> v.toStringValue().value();
            case DoubleValue v -> v.toStringValue().value();
            case StringValue v -> v.value();
            case BoolValue v -> v.toStringValue().value();
            case VariableValue v -> getString(v.variable().value());
            default -> "";
        };
    }
}
