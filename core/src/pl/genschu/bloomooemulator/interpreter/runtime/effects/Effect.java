package pl.genschu.bloomooemulator.interpreter.runtime.effects;

import pl.genschu.bloomooemulator.interpreter.context.Context;
import pl.genschu.bloomooemulator.interpreter.values.Value;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;

import java.util.List;

/**
 * Side-effect requested by a variable method.
 *
 * Returning null means "no return value override".
 */
public interface Effect {
    Value apply(Context context, Variable self, List<Value> arguments);
}
