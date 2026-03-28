package pl.genschu.bloomooemulator.interpreter.variable;

import com.badlogic.gdx.Gdx;
import pl.genschu.bloomooemulator.interpreter.context.Context;
import pl.genschu.bloomooemulator.interpreter.helpers.ArgumentHelper;
import pl.genschu.bloomooemulator.interpreter.runtime.ExecutionContext;
import pl.genschu.bloomooemulator.interpreter.values.NullValue;
import pl.genschu.bloomooemulator.interpreter.values.StringValue;
import pl.genschu.bloomooemulator.interpreter.values.Value;
import pl.genschu.bloomooemulator.loader.CNVParser;
import pl.genschu.bloomooemulator.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * CNVLoaderVariable dynamically loads .cnv files at runtime.
 *
 * Unlike CLASS (which keeps instance variables isolated), CNVLoader
 * merges loaded variables into the parent context.
 */
public record CNVLoaderVariable(
        String name,
        Map<String, Context> loadedContexts,  // Maps cnvFile path -> loaded Context
        Map<String, SignalHandler> signals
) implements Variable {

    public CNVLoaderVariable {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Variable name cannot be null or empty");
        }
        if (loadedContexts == null) {
            loadedContexts = Map.of();
        } else {
            loadedContexts = Map.copyOf(loadedContexts);
        }
        if (signals == null) {
            signals = Map.of();
        } else {
            signals = Map.copyOf(signals);
        }
    }

    // Convenience constructor - empty loader
    public CNVLoaderVariable(String name) {
        this(name, Map.of(), Map.of());
    }

    // ========================================
    // INTERFACE IMPLEMENTATION
    // ========================================

    @Override
    public Value value() {
        // Value is the count of loaded contexts
        return new StringValue("CNVLOADER[" + loadedContexts.size() + " loaded]");
    }

    @Override
    public VariableType type() {
        return VariableType.CNVLOADER;
    }

    @Override
    public Variable withValue(Value newValue) {
        // CNVLoader doesn't support direct value assignment
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
        return new CNVLoaderVariable(name, loadedContexts, newSignals);
    }

    // ========================================
    // CNVLOADER-SPECIFIC OPERATIONS
    // ========================================

    /**
     * Records that a context has been loaded.
     *
     * @param cnvFile File path
     * @param context Loaded context
     * @return New CNVLoaderVariable with updated state
     */
    public CNVLoaderVariable withLoadedContext(String cnvFile, Context context) {
        Map<String, Context> newLoaded = new HashMap<>(loadedContexts);
        newLoaded.put(cnvFile, context);
        return new CNVLoaderVariable(name, newLoaded, signals);
    }

    /**
     * Records that a context has been unloaded.
     *
     * @param cnvFile File path
     * @return New CNVLoaderVariable without that context
     */
    public CNVLoaderVariable withoutLoadedContext(String cnvFile) {
        Map<String, Context> newLoaded = new HashMap<>(loadedContexts);
        newLoaded.remove(cnvFile);
        return new CNVLoaderVariable(name, newLoaded, signals);
    }

    /**
     * Checks if a file is already loaded.
     *
     * @param cnvFile File path
     * @return True if loaded
     */
    public boolean isLoaded(String cnvFile) {
        return loadedContexts.containsKey(cnvFile);
    }

    /**
     * Gets a loaded context by file path.
     *
     * @param cnvFile File path
     * @return Context or null
     */
    public Context getLoadedContext(String cnvFile) {
        return loadedContexts.get(cnvFile);
    }

    // ========================================
    // METHODS DEFINITION
    // ========================================

    private static final Map<String, MethodSpec> METHODS = Map.ofEntries(
        Map.entry("LOAD", MethodSpec.of((self, args, ctx) -> {
            CNVLoaderVariable thisVar = (CNVLoaderVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("LOAD requires 1 argument (cnvFile)");
            }

            String cnvFile = ArgumentHelper.getString(args.get(0));

            if (thisVar.isLoaded(cnvFile)) {
                Gdx.app.log("CNVLoaderVariable", "CNV already loaded: " + cnvFile);
                return MethodResult.noReturn();
            }

            Context parentContext = ctx.context();
            Context cnvContext = new Context(new ExecutionContext(), parentContext);
            cnvContext.setGame(ctx.getGame());

            String cnvPath = FileUtils.resolveRelativePath(ctx.getGame(), cnvFile);

            CNVParser cnvParser = new CNVParser();
            try {
                Gdx.app.log("CNVLoaderVariable", "Loading " + cnvFile);
                cnvParser.parseFile(new File(cnvPath), cnvContext);

                for (Map.Entry<String, Variable> entry : cnvContext.getVariables(false).entrySet()) {
                    parentContext.setVariable(entry.getKey(), entry.getValue());
                }

                CNVLoaderVariable updated = thisVar.withLoadedContext(cnvFile, cnvContext);
                ctx.updateVariable(thisVar.name(), updated);

                parentContext.addAdditionalContext(cnvContext);
                Gdx.app.log("CNVLoaderVariable", "Loaded " + cnvFile);
            } catch (IOException e) {
                Gdx.app.error("CNVLoaderVariable", "Error loading " + cnvFile, e);
                throw new RuntimeException(e);
            }

            return MethodResult.noReturn();
        })),

        Map.entry("RELEASE", MethodSpec.of((self, args, ctx) -> {
            CNVLoaderVariable thisVar = (CNVLoaderVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("RELEASE requires 1 argument (cnvFile)");
            }

            String cnvFile = ArgumentHelper.getString(args.get(0));

            if (!thisVar.isLoaded(cnvFile)) {
                Gdx.app.log("CNVLoaderVariable", "CNV not loaded: " + cnvFile);
                return MethodResult.noReturn();
            }

            Context parentContext = ctx.context();
            Context cnvContext = thisVar.getLoadedContext(cnvFile);

            Gdx.app.log("CNVLoaderVariable", "Unloading " + cnvFile);

            for (String variableName : cnvContext.getVariables(false).keySet()) {
                parentContext.removeVariable(variableName);
            }

            parentContext.removeAdditionalContext(cnvContext);

            CNVLoaderVariable updated = thisVar.withoutLoadedContext(cnvFile);
            ctx.updateVariable(thisVar.name(), updated);

            Gdx.app.log("CNVLoaderVariable", "Unloaded " + cnvFile);
            return MethodResult.noReturn();
        }))
    );

    @Override
    public String toString() {
        return "CNVLoader[" + name + " with " + loadedContexts.size() + " loaded contexts]";
    }
}
