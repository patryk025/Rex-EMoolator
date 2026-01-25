package pl.genschu.bloomooemulator.interpreter.runtime.effects;

import pl.genschu.bloomooemulator.engine.Game;
import pl.genschu.bloomooemulator.interpreter.context.Context;
import pl.genschu.bloomooemulator.interpreter.values.StringValue;
import pl.genschu.bloomooemulator.interpreter.values.Value;
import pl.genschu.bloomooemulator.interpreter.variable.MethodResult;
import pl.genschu.bloomooemulator.interpreter.variable.MethodSpec;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;

import java.util.List;

/**
 * Effect: run a behaviour in the current scene context (legacy hack).
 */
public final class RunEnvEffect implements Effect {
    private final String sceneName;
    private final String behaviourName;

    public RunEnvEffect(String sceneName, String behaviourName) {
        this.sceneName = sceneName;
        this.behaviourName = behaviourName;
    }

    @Override
    public Value apply(Context context, Variable self, List<Value> arguments) {
        Game game = context.getGame();
        if (game == null) {
            return null;
        }
        if (!sceneName.equals(game.getCurrentScene())) {
            return null;
        }

        Variable behaviour = context.getVariable(behaviourName);
        if (behaviour == null) {
            return null;
        }

        MethodSpec spec = behaviour.methodSpecs().get("RUN");
        if (spec == null || spec.method() == null) {
            return null;
        }

        MethodResult result = spec.method().execute(behaviour, List.of(new StringValue(behaviourName)));
        Value returnValue = result.getReturnValue();
        for (Effect effect : result.effects()) {
            Value effectValue = effect.apply(context, behaviour, List.of(new StringValue(behaviourName)));
            if (effectValue != null) {
                returnValue = effectValue;
            }
        }
        return returnValue;
    }
}
