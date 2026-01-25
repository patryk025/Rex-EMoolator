package pl.genschu.bloomooemulator.interpreter.variable;

import pl.genschu.bloomooemulator.interpreter.helpers.ArgumentHelper;
import pl.genschu.bloomooemulator.interpreter.runtime.effects.ExitGameEffect;
import pl.genschu.bloomooemulator.interpreter.runtime.effects.RunEnvEffect;
import pl.genschu.bloomooemulator.interpreter.runtime.effects.RunMethodEffect;
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
    public Map<String, MethodSpec> methodSpecs() {
        return METHOD_SPECS;
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
        Map.entry("EXIT", (self, args) -> MethodResult.effects(List.of(new ExitGameEffect()))),

        Map.entry("GETLANGUAGE", (self, args) -> {
            ApplicationVariable thisVar = (ApplicationVariable) self;
            return MethodResult.noChange(new StringValue(thisVar.language));
        }),

        Map.entry("RUN", (self, args) -> {
            if (args.size() < 2) {
                throw new IllegalArgumentException("RUN requires at least 2 arguments");
            }
            String varName = ArgumentHelper.getString(args.get(0));
            String methodName = ArgumentHelper.getString(args.get(1));
            List<Value> params = args.size() > 2 ? args.subList(2, args.size()) : List.of();
            return MethodResult.effects(List.of(new RunMethodEffect(varName, methodName, params)));
        }),

        Map.entry("RUNENV", (self, args) -> {
            if (args.size() < 2) {
                throw new IllegalArgumentException("RUNENV requires 2 arguments");
            }
            String sceneName = ArgumentHelper.getString(args.get(0));
            String behaviourName = ArgumentHelper.getString(args.get(1));
            return MethodResult.effects(List.of(new RunEnvEffect(sceneName, behaviourName)));
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

    private static final Map<String, MethodSpec> METHOD_SPECS = Map.ofEntries(
        Map.entry("EXIT", MethodSpec.of(METHODS.get("EXIT"))),
        Map.entry("GETLANGUAGE", MethodSpec.of(METHODS.get("GETLANGUAGE"))),
        Map.entry("RUN", MethodSpec.of(METHODS.get("RUN"), ArgKind.VALUE, ArgKind.VALUE)),
        Map.entry("RUNENV", MethodSpec.of(METHODS.get("RUNENV"), ArgKind.VALUE, ArgKind.VALUE)),
        Map.entry("SETLANGUAGE", MethodSpec.of(METHODS.get("SETLANGUAGE"), ArgKind.VALUE))
    );

    @Override
    public String toString() {
        return "ApplicationVariable[" + name + ", lang=" + language + ", episodes=" + episodeNames.size() + "]";
    }
}
