package pl.genschu.bloomooemulator.interpreter.values;

/**
 * Represents a reference to a variable by name.
 * This is NOT the variable itself, but a reference that needs to be resolved
 * at runtime using the execution context.
 *
 * Example: In code like "x^ADD(5)", the "x" is parsed as a VariableRef("x").
 */
public record VariableRef(String name) implements Value {

    public VariableRef {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Variable name cannot be null or empty");
        }
    }

    @Override
    public ValueType getType() {
        return ValueType.VARIABLE_REF;
    }

    @Override
    public Object unwrap() {
        return name;
    }

    @Override
    public String toDisplayString() {
        return name;
    }
}
