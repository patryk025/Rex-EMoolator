package pl.genschu.bloomooemulator.interpreter.runtime.effects;

import com.badlogic.gdx.Gdx;
import pl.genschu.bloomooemulator.interpreter.context.Context;
import pl.genschu.bloomooemulator.interpreter.values.Value;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;

import java.util.List;

/**
 * Effect: exit the application.
 */
public final class ExitGameEffect implements Effect {
    @Override
    public Value apply(Context context, Variable self, List<Value> arguments) {
        Gdx.app.exit();
        return null;
    }
}
