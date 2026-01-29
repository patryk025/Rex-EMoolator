package pl.genschu.bloomooemulator.interpreter.runtime.effects;

import pl.genschu.bloomooemulator.interpreter.context.Context;
import pl.genschu.bloomooemulator.interpreter.values.Value;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;

import java.util.List;

/**
 * Effect: clone a variable N times.
 */
public final class CloneEffect implements Effect {
    private final int amount;

    public CloneEffect(int amount) {
        this.amount = amount;
    }

    @Override
    public Value apply(Context context, Variable self, List<Value> arguments) {
        int count = Math.max(0, amount);
        if (count == 0) {
            return null;
        }

        String baseName = self.name();
        int existing = context.clones().getCloneNames(baseName).size();

        for (int i = 0; i < count; i++) {
            String cloneName = baseName + "_" + (existing + i + 1);
            Variable clone = self.copyAs(cloneName);
            context.setVariable(cloneName, clone);
            context.clones().registerClone(baseName, cloneName);
        }

        return null;
    }
}
