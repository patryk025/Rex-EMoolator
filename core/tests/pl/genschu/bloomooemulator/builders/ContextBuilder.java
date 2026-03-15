package pl.genschu.bloomooemulator.builders;

import pl.genschu.bloomooemulator.interpreter.ast.ASTNode;
import pl.genschu.bloomooemulator.interpreter.context.Context;
import pl.genschu.bloomooemulator.interpreter.parser.CodeParser;
import pl.genschu.bloomooemulator.interpreter.runtime.ExecutionContext;
import pl.genschu.bloomooemulator.interpreter.variable.*;

/**
 * Builder for creating test contexts with v2 infrastructure.
 *
 * Example usage:
 *   Context ctx = new ContextBuilder()
 *       .withVariable("INTEGER", "x", 10)
 *       .withVariable("STRING", "name", "test")
 *       .build();
 */
public final class ContextBuilder {
    private final ExecutionContext execContext = new ExecutionContext();
    private final Context ctx = new Context(execContext);

    /**
     * Adds a variable using type string and value.
     *
     * @param type Variable type (INTEGER, DOUBLE, STRING, BOOLEAN)
     * @param name Variable name
     * @param value Variable value
     * @return this builder
     */
    public ContextBuilder withVariable(String type, String name, Object value) {
        Variable v = createVariable(type, name, value);
        ctx.setVariable(name, v);
        return this;
    }

    /**
     * Adds a variable directly.
     *
     * @param v Variable to add
     * @return this builder
     */
    public ContextBuilder withVariable(Variable v) {
        ctx.setVariable(v.name(), v);
        return this;
    }

    /**
     * DEPRECATED: Alias for withVariable() for backwards compatibility.
     * Use withVariable() instead.
     *
     * @deprecated Use {@link #withVariable(String, String, Object)} instead
     */
    @Deprecated(since = "0.2", forRemoval = true)
    public ContextBuilder withFactory(String type, String name, Object value) {
        return withVariable(type, name, value);
    }

    /**
     * Builds the context.
     *
     * @return the configured context
     */
    public Context build() {
        return ctx;
    }

    /**
     * Creates a new Variable from type string and value.
     * Supports basic types: INTEGER, DOUBLE, STRING, BOOLEAN, BOOL, BEHAVIOUR
     */
    private Variable createVariable(String type, String name, Object value) {
        return switch (type.toUpperCase()) {
            case "INTEGER" -> new IntegerVariable(name, ((Number) value).intValue());
            case "DOUBLE" -> new DoubleVariable(name, ((Number) value).doubleValue());
            case "STRING" -> new StringVariable(name, value.toString());
            case "BOOLEAN", "BOOL" -> new BoolVariable(name, (Boolean) value);
            case "BEHAVIOUR" -> {
                String code = value.toString();
                ASTNode ast = CodeParser.parseCode(code);
                yield new BehaviourVariable(name, ast, java.util.Map.of());
            }
            default -> throw new IllegalArgumentException("Unsupported type: " + type);
        };
    }
}

