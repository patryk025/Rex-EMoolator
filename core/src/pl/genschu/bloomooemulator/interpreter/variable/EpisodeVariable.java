package pl.genschu.bloomooemulator.interpreter.variable;

import pl.genschu.bloomooemulator.interpreter.helpers.ArgumentHelper;
import pl.genschu.bloomooemulator.interpreter.runtime.effects.BackSceneEffect;
import pl.genschu.bloomooemulator.interpreter.runtime.effects.GetCurrentSceneEffect;
import pl.genschu.bloomooemulator.interpreter.runtime.effects.GetPreviousSceneEffect;
import pl.genschu.bloomooemulator.interpreter.runtime.effects.GotoSceneEffect;
import pl.genschu.bloomooemulator.interpreter.values.*;

import java.util.*;

/**
 * EpisodeVariable represents an episode in the game.
 * Contains scenes and episode-level settings.
 **/
public record EpisodeVariable(
    String name,
    List<String> sceneNames,
    String startWith,
    Map<String, SignalHandler> signals
) implements Variable {

    public EpisodeVariable {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Variable name cannot be null or empty");
        }
        if (sceneNames == null) {
            sceneNames = List.of();
        } else {
            sceneNames = List.copyOf(sceneNames);
        }
        if (startWith == null) startWith = "";
        if (signals == null) {
            signals = Map.of();
        } else {
            signals = Map.copyOf(signals);
        }
    }

    public EpisodeVariable(String name) {
        this(name, List.of(), "", Map.of());
    }

    // ========================================
    // INTERFACE IMPLEMENTATION
    // ========================================

    @Override
    public Value value() {
        return new StringValue(name);
    }

    @Override
    public VariableType type() {
        return VariableType.EPISODE;
    }

    @Override
    public Variable withValue(Value newValue) {
        return this;
    }

    @Override
    public Map<String, MethodSpec> methods() {
        return METHODS;
    }

    @Override
    public Variable withSignal(String signalName, SignalHandler handler) {
        Map<String, SignalHandler> newSignals = new HashMap<>(signals);
        newSignals.put(signalName, handler);
        return new EpisodeVariable(name, sceneNames, startWith, newSignals);
    }

    // ========================================
    // METHODS DEFINITION
    // ========================================

    private static final Map<String, MethodSpec> METHODS = Map.ofEntries(
        Map.entry("BACK", MethodSpec.of((self, args) -> {
            return MethodResult.effects(List.of(new BackSceneEffect()));
        })),

        Map.entry("GETCURRENTSCENE", MethodSpec.of((self, args) -> {
            return new MethodResult(null, NullValue.INSTANCE, List.of(new GetCurrentSceneEffect()));
        })),

        Map.entry("GETLATESTSCENE", MethodSpec.of((self, args) -> {
            return new MethodResult(null, NullValue.INSTANCE, List.of(new GetPreviousSceneEffect()));
        })),

        Map.entry("GOTO", MethodSpec.of((self, args) -> {
            if (args.isEmpty()) {
                throw new IllegalArgumentException("GOTO requires 1 argument");
            }
            String sceneName = ArgumentHelper.getString(args.get(0));
            return MethodResult.effects(List.of(new GotoSceneEffect(sceneName)));
        })
    ));

    // ========================================
    // CONVENIENT ACCESSORS
    // ========================================

    @Override
    public String toString() {
        return "EpisodeVariable[" + name + ", scenes=" + sceneNames.size() + "]";
    }
}
