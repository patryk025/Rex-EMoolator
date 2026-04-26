package pl.genschu.bloomooemulator.interpreter.variable;

import com.badlogic.gdx.Gdx;
import pl.genschu.bloomooemulator.interpreter.helpers.ArgumentHelper;
import pl.genschu.bloomooemulator.interpreter.runtime.ExecutionResult;
import pl.genschu.bloomooemulator.interpreter.values.*;

import java.util.*;

/**
 * ApplicationVariable represents the top-level application/game container.
 * Contains episodes and application-level settings.
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
    public Map<String, MethodSpec> methods() {
        return METHODS;
    }

    @Override
    public Variable withSignal(String signalName, SignalHandler handler) {
        Map<String, SignalHandler> newSignals = new HashMap<>(signals);
        if (handler == null) {
            newSignals.remove(signalName);
        } else {
            newSignals.put(signalName, handler);
        }
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

    private static final Map<String, MethodSpec> METHODS = Map.ofEntries(
        Map.entry("EXIT", MethodSpec.of((self, args, ctx) -> {
            Gdx.app.exit();
            return MethodResult.noReturn();
        })),

        Map.entry("GETLANGUAGE", MethodSpec.of((self, args, ctx) -> {
            ApplicationVariable thisVar = (ApplicationVariable) self;
            return MethodResult.returns(new StringValue(thisVar.language));
        })),

        Map.entry("RUN", MethodSpec.of((self, args, ctx) -> {
            if (args.size() < 2) {
                throw new IllegalArgumentException("RUN requires at least 2 arguments");
            }
            String varName = ArgumentHelper.getString(args.get(0));
            String methodName = ArgumentHelper.getString(args.get(1));
            List<Value> params = args.size() > 2 ? args.subList(2, args.size()) : List.of();
            Variable target = ctx.getVariable(varName);
            return target.callMethod(methodName, params, ctx);
        })),

        Map.entry("RUNENV", MethodSpec.of((self, args, ctx) -> {
            if (args.size() < 2) {
                throw new IllegalArgumentException("RUNENV requires 2 arguments");
            }
            String sceneName = ArgumentHelper.getString(args.get(0));
            String behaviourName = ArgumentHelper.getString(args.get(1));
            String currentScene = ctx.getGame().getCurrentScene();
            if (!sceneName.equalsIgnoreCase(currentScene)) {
                return MethodResult.noReturn();
            }
            Variable behaviourVar = ctx.getVariable(behaviourName);
            if (behaviourVar instanceof BehaviourVariable behaviour) {
                ExecutionResult result = ctx.runBehaviour("RUNENV:" + behaviourName, null, behaviour, List.of());
                return MethodResult.fromExecution(result);
            }
            return MethodResult.noReturn();
        })),

        Map.entry("SETLANGUAGE", MethodSpec.of((self, args, ctx) -> {
            ApplicationVariable thisVar = (ApplicationVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("SETLANGUAGE requires 1 argument");
            }
            String newLanguage = ArgumentHelper.getString(args.get(0));
            ctx.updateVariable(thisVar.name(), thisVar.withLanguage(newLanguage));
            return MethodResult.noReturn();
        }))
    );

    @Override
    public String toString() {
        return "ApplicationVariable[" + name + ", lang=" + language + ", episodes=" + episodeNames.size() + "]";
    }
}
