package pl.genschu.bloomooemulator.interpreter.variable;

import com.badlogic.gdx.Gdx;
import pl.genschu.bloomooemulator.interpreter.context.Context;
import pl.genschu.bloomooemulator.interpreter.helpers.ArgumentHelper;
import pl.genschu.bloomooemulator.interpreter.runtime.ASTInterpreter;
import pl.genschu.bloomooemulator.interpreter.runtime.ExecutionResult;
import pl.genschu.bloomooemulator.interpreter.values.*;
import pl.genschu.bloomooemulator.interpreter.variable.capabilities.HasInstanceContext;
import pl.genschu.bloomooemulator.utils.LangCodeConverter;

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
            if (!ctx.context().hasVariable(varName)) {
                return MethodResult.noReturn();
            }
            Variable target = ctx.getVariable(varName);
            // A behaviour can't name its caller statically, so it stashes the
            // caller's name in a temp STRING and hands RUN the *name of that temp*
            // via $N (e.g. UFO^RUN($1,"GETCENTERX") with $1 naming VARSTEMP1, a
            // STRING holding "WALK0"). Whole-source parameter substitution turns
            // $1 into the bare VARSTEMP1 reference before this call is parsed, so
            // varName arrives here as the resolved value "WALK0".
            return target.callMethod(methodName, params, ctx);
        })),

        Map.entry("RUNENV", MethodSpec.of((self, args, ctx) -> {
            if (args.size() < 2) {
                throw new IllegalArgumentException("RUNENV requires 2 arguments");
            }
            String ownerName = ArgumentHelper.getString(args.get(0));
            String behaviourName = ArgumentHelper.getString(args.get(1));

            Context live = ctx.getGame() != null && ctx.getGame().getCurrentSceneContext() != null
                    ? (Context) ctx.getGame().getCurrentSceneContext()
                    : ctx.context();

            if (!live.hasVariable(ownerName)) {
                return MethodResult.noReturn();
            }
            Variable owner = live.getVariable(ownerName);

            if (owner instanceof HasInstanceContext hic) {
                Variable behaviour = hic.getInstanceContext().getVariable(behaviourName);
                if (behaviour instanceof BehaviourVariable) {
                    return owner.callMethod(behaviourName, List.of(), ctx);
                }
                return MethodResult.noReturn();
            }

            Variable behaviourVar = live.getVariable(behaviourName);
            if (behaviourVar instanceof BehaviourVariable behaviour) {
                // Run the behaviour in the context where it is DEFINED, not in the
                // deepest live context. The lookup above walks UP the chain, so a
                // behaviour declared at episode/application level (e.g. B_PAUSE_START
                // living in PRZYGODA) is resolved fine — but if we then executed it
                // rooted at the scene context, a bare name it references (e.g.
                // BFITMP3) would resolve against the SCENE store first and shadow the
                // parent's own version. A parent never sees its children's variables;
                // resolution must start from the behaviour's owning context upward.
                Context defining = live.findOwningContext(behaviour);
                Context runIn = defining != null ? defining : live;
                ExecutionResult result = new ASTInterpreter(runIn)
                        .runBehaviour("RUNENV:" + behaviourName, owner, behaviour, List.of());
                return MethodResult.fromExecution(result);
            }
            return MethodResult.noReturn();
        })),

        Map.entry("SETLANGUAGE", MethodSpec.of((self, args, ctx) -> {
            ApplicationVariable thisVar = (ApplicationVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("SETLANGUAGE requires 1 argument");
            }
            String newLanguageCode = ArgumentHelper.getString(args.get(0));
            String newLanguage = LangCodeConverter.lcidToIsoCode(newLanguageCode).toUpperCase();
            ctx.updateVariable(thisVar.name(), thisVar.withLanguage(newLanguage));
            return MethodResult.noReturn();
        }))
    );

    @Override
    public String toString() {
        return "ApplicationVariable[" + name + ", lang=" + language + ", episodes=" + episodeNames.size() + "]";
    }
}
