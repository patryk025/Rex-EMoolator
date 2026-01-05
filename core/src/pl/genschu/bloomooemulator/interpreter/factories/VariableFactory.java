package pl.genschu.bloomooemulator.interpreter.factories;

import pl.genschu.bloomooemulator.interpreter.v2.ast.ASTNode;
import pl.genschu.bloomooemulator.interpreter.v2.values.*;
import pl.genschu.bloomooemulator.interpreter.v2.variable.*;

import java.util.Locale;

/**
 * New factory dedicated to the v2 Value/Variable model.
 * <p>
 * Only primitive types are supported here for now:
 * INT/DOUBLE/STRING/BOOL.
 * Everything else should be created using their constructor while migration is in progress.
 */
public final class VariableFactory {
    private VariableFactory() {
    }

    /**
     * Creates a {@link Variable} based on the provided type/name/value trio.
     */
    public static Variable createVariable(String type, String name, Object rawValue) {
        Value value = createValueWithAutoType(name, rawValue);
        return createVariable(type, name, value);
    }

    /**
     * Creates a {@link Variable} using an already materialised {@link Value}.
     */
    public static Variable createVariable(String type, String name, Value value) {
        String normalized = normalize(type);

        return switch (normalized) {
            case "INT", "INTEGER" -> new IntegerVariable(name, valueAsInt(value));
            case "DOUBLE" -> new DoubleVariable(name, valueAsDouble(value));
            case "STRING" -> new StringVariable(name, value.toDisplayString());
            case "BOOL", "BOOLEAN" -> new BoolVariable(name, valueAsBool(value));
            default -> throw new IllegalArgumentException("Unsupported variable type: " + normalized);
        };
    }

    /**
     * Creates a {@link Value} from a raw object with a best-effort type guess.
     */
    public static Value createValueWithAutoType(String name, Object rawValue) {
        if (rawValue instanceof Value value) {
            return value;
        }

        if (rawValue instanceof Integer i) {
            return new IntValue(i);
        }
        if (rawValue instanceof Double d) {
            return new DoubleValue(d);
        }
        if (rawValue instanceof Float f) {
            return new DoubleValue(f.doubleValue());
        }
        if (rawValue instanceof Boolean b) {
            return BoolValue.of(b);
        }
        if (rawValue instanceof String s) {
            return new StringValue(s);
        }
        if (rawValue instanceof ASTNode astNode) {
            return new BehaviourValue(name, astNode);
        }

        return NullValue.INSTANCE;
    }

    private static String normalize(String type) {
        return type == null ? "" : type.trim().toUpperCase(Locale.ROOT);
    }

    private static int valueAsInt(Value value) {
        return switch (value) {
            case IntValue v -> v.value();
            case DoubleValue v -> (int) v.value();
            case StringValue v -> {
                IntValue parsed = v.tryParseInt();
                yield parsed != null ? parsed.value() : 0;
            }
            case BoolValue v -> v.value() ? 1 : 0;
            default -> 0;
        };
    }

    private static double valueAsDouble(Value value) {
        return switch (value) {
            case IntValue v -> v.value();
            case DoubleValue v -> v.value();
            case StringValue v -> {
                DoubleValue parsed = v.tryParseDouble();
                yield parsed != null ? parsed.value() : 0.0;
            }
            case BoolValue v -> v.value() ? 1.0 : 0.0;
            default -> 0.0;
        };
    }

    private static boolean valueAsBool(Value value) {
        return switch (value) {
            case BoolValue v -> v.value();
            case IntValue v -> v.value() != 0;
            case DoubleValue v -> v.value() != 0.0;
            case StringValue v -> v.value().equalsIgnoreCase("TRUE");
            default -> false;
        };
    }
}
