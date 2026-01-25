package pl.genschu.bloomooemulator.interpreter.runtime.effects;

import pl.genschu.bloomooemulator.engine.Game;
import pl.genschu.bloomooemulator.interpreter.context.Context;
import pl.genschu.bloomooemulator.interpreter.values.StringValue;
import pl.genschu.bloomooemulator.interpreter.values.Value;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;

import java.util.List;

/**
 * Effect: get previous scene name.
 */
public final class GetPreviousSceneEffect implements Effect {
    @Override
    public Value apply(Context context, Variable self, List<Value> arguments) {
        Game game = context.getGame();
        if (game == null) {
            throw new IllegalStateException("Game not available in context");
        }
        return new StringValue(game.getPreviousScene());
    }
}
