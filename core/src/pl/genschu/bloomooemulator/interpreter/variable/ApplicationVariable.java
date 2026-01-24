package pl.genschu.bloomooemulator.interpreter.variable;

import pl.genschu.bloomooemulator.interpreter.helpers.ArgumentHelper;
import pl.genschu.bloomooemulator.interpreter.values.*;

import java.util.*;

/**
 * ApplicationVariable represents the top-level application/game container.
 * Contains episodes and application-level settings.
 *
 * Most methods require access to Game context and should be handled by the interpreter.
 **/
public record ApplicationVariable(
    String name,
    String language,
    List<String> episodeNames,
    String startWith,
    Map<String, SignalHandler> signals
) implements Variable {

    public ApplicationVariable {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Variable name cannot be null or empty");
        }
        if (language == null) language = "POL";
        if (episodeNames == null) {
            episodeNames = List.of();
        } else {
            episodeNames = List.copyOf(episodeNames);
        }
        if (startWith == null) startWith = "";
        if (signals == null) {
            signals = Map.of();
        } else {
            signals = Map.copyOf(signals);
        }
    }

    public ApplicationVariable(String name) {
        this(name, "POL", List.of(), "", Map.of());
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
        return VariableType.APPLICATION;
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
        return new ApplicationVariable(name, language, episodeNames, startWith, newSignals);
    }

    // ========================================
    // HELPER METHODS
    // ========================================

    public ApplicationVariable withLanguage(String newLanguage) {
        return new ApplicationVariable(name, newLanguage, episodeNames, startWith, signals);
    }

    // ========================================
    // METHODS DEFINITION
    // ========================================

    private static final Map<String, VariableMethod> METHODS = Map.ofEntries(
        Map.entry("EXIT", (self, args) -> {
            // Actual exit handled by interpreter/game
            return MethodResult.noChange(NullValue.INSTANCE);
        }),

        Map.entry("GETLANGUAGE", (self, args) -> {
            ApplicationVariable thisVar = (ApplicationVariable) self;
            return MethodResult.noChange(new StringValue(thisVar.language));
        }),

        Map.entry("RUN", (self, args) -> {
            // TODO: maybe send signal to interpreter to run?
            // Requires context - handled by interpreter
            // Arguments: varName, methodName, param1...paramN
            return MethodResult.noChange(NullValue.INSTANCE);
        }),

        Map.entry("RUNENV", (self, args) -> {
            // TODO: maybe send signal to interpreter to run?
            // Requires context - handled by interpreter
            // Arguments: sceneName, behaviourName
            return MethodResult.noChange(NullValue.INSTANCE);
        }),

        Map.entry("SETLANGUAGE", (self, args) -> {
            ApplicationVariable thisVar = (ApplicationVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("SETLANGUAGE requires 1 argument");
            }

            String newLanguage = ArgumentHelper.getString(args.get(0));
            return MethodResult.sets(thisVar.withLanguage(newLanguage));
        })
    );

    @Override
    public String toString() {
        return "ApplicationVariable[" + name + ", lang=" + language + ", episodes=" + episodeNames.size() + "]";
    }
}
