package pl.genschu.bloomooemulator.interpreter.runtime;

import pl.genschu.bloomooemulator.interpreter.v1.Context;
import pl.genschu.bloomooemulator.interpreter.v1.variable.Variable;
import pl.genschu.bloomooemulator.interpreter.v1.variable.types.*;
import pl.genschu.bloomooemulator.interpreter.values.*;

/**
 * Bridge between v1 Variable system and v2 Value system.
 *
 * This is a TEMPORARY solution during migration.
 * Eventually, we'll rewrite Variable to use Value internally.
 *
 * Why a bridge?
 * - v1 Variables are mutable objects with methods
 * - v2 Values are immutable data
 * - We need to convert between them during transition
 */
public class VariableBridge {

    /**
     * Converts v1 Variable to v2 Value.
     *
     * This extracts the primitive value from the Variable.
     * For complex types (BEHAVIOUR, etc), we return a reference.
     */
    public static Value toValue(Variable variable) {
        if (variable == null) {
            return NullValue.INSTANCE;
        }

        // Pattern matching would be nice here, but Variable doesn't have sealed hierarchy
        // So we use string comparison on type
        String type = variable.getType();

        return switch (type) {
            case "INTEGER" -> {
                IntegerVariable intVar = (IntegerVariable) variable;
                yield new IntValue(intVar.GET());
            }

            case "DOUBLE" -> {
                DoubleVariable doubleVar = (DoubleVariable) variable;
                yield new DoubleValue(doubleVar.GET());
            }

            case "STRING" -> {
                StringVariable strVar = (StringVariable) variable;
                yield new StringValue(strVar.GET());
            }

            case "BOOLEAN" -> {
                BoolVariable boolVar = (BoolVariable) variable;
                yield BoolValue.of(boolVar.GET());
            }

            case "BEHAVIOUR" -> {
                // For now, just return a reference
                // TODO: Convert BEHAVIOUR code to BehaviourValue with AST
                yield new VariableRef(variable.getName());
            }

            default -> {
                // For other types (ANIMO, IMAGE, etc), return a reference
                // The interpreter will look up by name when needed
                yield new VariableRef(variable.getName());
            }
        };
    }

    /**
     * Converts v2 Value to v1 Variable.
     *
     * This creates a new Variable or updates an existing one.
     * Note: This is tricky because Variables are mutable!
     */
    public static Variable toVariable(
        Value value,
        String name,
        Context context
    ) {
        if (value == null || value instanceof NullValue) {
            return null;
        }

        return switch (value) {
            case IntValue v ->
                new IntegerVariable(name, v.value(), context);

            case DoubleValue v ->
                new DoubleVariable(name, v.value(), context);

            case StringValue v ->
                new StringVariable(name, v.value(), context);

            case BoolValue v ->
                new BoolVariable(name, v.value(), context);

            case VariableRef ref -> {
                // This is a reference - look it up in context
                Variable var = context.getVariable(ref.name());
                if (var == null) {
                    throw new RuntimeException("Variable not found: " + ref.name());
                }
                yield var;
            }

            case BehaviourValue bv -> {
                // TODO: Create BehaviourVariable from AST
                throw new UnsupportedOperationException(
                    "Converting BehaviourValue to Variable not yet implemented"
                );
            }

            default ->
                throw new UnsupportedOperationException(
                    "Cannot convert " + value.getClass().getSimpleName() + " to Variable"
                );
        };
    }

    /**
     * Updates an existing Variable with a new Value.
     *
     * This is used when assigning to variables.
     * It mutates the Variable in place (because v1 Variables are mutable).
     */
    public static void updateVariable(Variable variable, Value newValue) {
        if (variable == null) {
            throw new IllegalArgumentException("Cannot update null variable");
        }

        // Based on the variable type, update its value
        String type = variable.getType();

        switch (type) {
            case "INTEGER" -> {
                int newIntValue = switch (newValue) {
                    case IntValue v -> v.value();
                    case DoubleValue v -> (int) v.value();
                    case StringValue v -> {
                        IntValue parsed = v.tryParseInt();
                        yield parsed != null ? parsed.value() : 0;
                    }
                    case BoolValue v -> v.value() ? 1 : 0;
                    default -> 0;
                };
                variable.getAttribute("VALUE").setValue(newIntValue);
            }

            case "DOUBLE" -> {
                double newDoubleValue = switch (newValue) {
                    case IntValue v -> (double) v.value();
                    case DoubleValue v -> v.value();
                    case StringValue v -> {
                        DoubleValue parsed = v.tryParseDouble();
                        yield parsed != null ? parsed.value() : 0.0;
                    }
                    case BoolValue v -> v.value() ? 1.0 : 0.0;
                    default -> 0.0;
                };
                variable.getAttribute("VALUE").setValue(newDoubleValue);
            }

            case "STRING" -> {
                String newStrValue = newValue.toDisplayString();
                variable.getAttribute("VALUE").setValue(newStrValue);
            }

            case "BOOLEAN" -> {
                boolean newBoolValue = switch (newValue) {
                    case BoolValue v -> v.value();
                    case IntValue v -> v.value() != 0;
                    case DoubleValue v -> v.value() != 0.0;
                    case StringValue v -> v.value().equalsIgnoreCase("TRUE");
                    default -> false;
                };
                variable.getAttribute("VALUE").setValue(newBoolValue);
            }

            default -> {
                throw new UnsupportedOperationException(
                    "Updating " + type + " variables not yet implemented"
                );
            }
        }
    }
}
