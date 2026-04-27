package pl.genschu.bloomooemulator.interpreter.values;

import pl.genschu.bloomooemulator.interpreter.variable.Variable;

/**
 * Wraps a Variable as a Value for method argument passing.
 */
public record VariableValue(Variable variable) implements Value {
    public VariableValue {
        if (variable == null) {
            throw new IllegalArgumentException("Variable cannot be null");
        }
    }

    @Override
    public ValueType getType() {
        return ValueType.VARIABLE;
    }

    @Override
    public Object unwrap() {
        return variable;
    }

    @Override
    public String toDisplayString() {
        return variable.name();
    }
}
