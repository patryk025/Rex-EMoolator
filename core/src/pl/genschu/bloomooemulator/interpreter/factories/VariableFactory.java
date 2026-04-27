package pl.genschu.bloomooemulator.interpreter.factories;

import pl.genschu.bloomooemulator.interpreter.ast.ASTNode;
import pl.genschu.bloomooemulator.interpreter.helpers.ArgumentHelper;
import pl.genschu.bloomooemulator.interpreter.values.*;
import pl.genschu.bloomooemulator.interpreter.variable.*;

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
            case "INT", "INTEGER" -> new IntegerVariable(name, ArgumentHelper.getInt(value));
            case "DOUBLE" -> new DoubleVariable(name, ArgumentHelper.getDouble(value));
            case "STRING" -> new StringVariable(name, ArgumentHelper.getString(value));
            case "BOOL", "BOOLEAN" -> new BoolVariable(name, ArgumentHelper.getBoolean(value));
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
}
