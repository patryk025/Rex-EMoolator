package pl.genschu.bloomooemulator.interpreter.variable;

import pl.genschu.bloomooemulator.interpreter.helpers.ArgumentHelper;
import pl.genschu.bloomooemulator.interpreter.values.*;

import java.util.*;

/**
 * EpisodeVariable represents an episode in the game.
 * Contains scenes and episode-level settings.
 *
 * Most methods require access to game context and should be handled by the interpreter.
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
    public Map<String, VariableMethod> methods() {
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

    private static final Map<String, VariableMethod> METHODS = Map.ofEntries(
        Map.entry("BACK", (self, args) -> {
            // Go to previous scene - handled by interpreter/game
            return MethodResult.noChange(NullValue.INSTANCE);
        }),

        Map.entry("GETCURRENTSCENE", (self, args) -> {
            // Requires game context - handled by interpreter
            // Returns placeholder - interpreter should override
            return MethodResult.noChange(new StringValue(""));
        }),

        Map.entry("GETLATESTSCENE", (self, args) -> {
            // Requires game context - handled by interpreter
            // Returns placeholder - interpreter should override
            return MethodResult.noChange(new StringValue(""));
        }),

        Map.entry("GOTO", (self, args) -> {
            // Go to specified scene - handled by interpreter/game
            if (args.isEmpty()) {
                throw new IllegalArgumentException("GOTO requires 1 argument");
            }
            // The actual navigation is handled by interpreter
            return MethodResult.noChange(NullValue.INSTANCE);
        })
    );

    // ========================================
    // CONVENIENT ACCESSORS
    // ========================================

    @Override
    public String toString() {
        return "EpisodeVariable[" + name + ", scenes=" + sceneNames.size() + "]";
    }
}
