package pl.genschu.bloomooemulator.interpreter.runtime.effects;

import pl.genschu.bloomooemulator.interpreter.context.Context;
import pl.genschu.bloomooemulator.interpreter.values.Value;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;

import java.util.List;

/**
 * Effect: update a variable in the context hierarchy.
 */
public final class UpdateVariableEffect implements Effect {
    private final String variableName;
    private final Variable newValue;

    public UpdateVariableEffect(String variableName, Variable newValue) {
        this.variableName = variableName;
        this.newValue = newValue;
    }

    @Override
    public Value apply(Context context, Variable self, List<Value> arguments) {
        boolean updated = context.updateVariableInHierarchy(variableName, newValue);
        if (!updated) {
            throw new IllegalStateException("Failed to update variable: " + variableName);
        }
        return null;
    }
}
