package pl.genschu.bloomooemulator.interpreter.variable;

import pl.genschu.bloomooemulator.interpreter.context.Context;
import pl.genschu.bloomooemulator.interpreter.values.NullValue;
import pl.genschu.bloomooemulator.interpreter.values.StringValue;
import pl.genschu.bloomooemulator.interpreter.values.Value;

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
        Map.entry("LOAD", MethodSpec.of((self, args) -> {
            CNVLoaderVariable thisVar = (CNVLoaderVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("LOAD requires 1 argument (cnvFile)");
            }

            String cnvFile = args.get(0).toDisplayString();

            // Check if already loaded
            if (thisVar.isLoaded(cnvFile)) {
                // TODO: Log warning? For now, just return self
                return MethodResult.noChange(NullValue.INSTANCE);
            }

            // TODO: Implementation requires:
            // 1. Get parent context reference
            // 2. Create new Context with parent
            // 3. Resolve cnvFile path using FileUtils
            // 4. Parse .cnv file into new context using CNVParser
            // 5. Copy variables from loaded context to parent context
            // 6. Add loaded context as additional context to parent
            // 7. Return new CNVLoaderVariable with updated loadedContexts

            // For now, throw exception indicating this needs integration
            throw new UnsupportedOperationException(
                "CNVLOADER.LOAD not yet integrated with CNVParser and Context. " +
                "Implementation requires: " +
                "1. Context reference to merge variables, " +
                "2. CNVParser to load file, " +
                "3. FileUtils to resolve path"
            );
        })),

        Map.entry("RELEASE", MethodSpec.of((self, args) -> {
            CNVLoaderVariable thisVar = (CNVLoaderVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("RELEASE requires 1 argument (cnvFile)");
            }

            String cnvFile = args.get(0).toDisplayString();

            // Check if loaded
            if (!thisVar.isLoaded(cnvFile)) {
                // TODO: Log warning? For now, just return self
                return MethodResult.noChange(NullValue.INSTANCE);
            }

            // TODO: Implementation requires:
            // 1. Get parent context reference
            // 2. Get loaded context by cnvFile
            // 3. Remove all variables from parent context that came from loaded context
            // 4. Remove loaded context from additional contexts
            // 5. Return new CNVLoaderVariable without this context

            // For now, throw exception indicating this needs integration
            throw new UnsupportedOperationException(
                "CNVLOADER.RELEASE not yet integrated with Context. " +
                "Implementation requires: " +
                "1. Context reference to remove variables, " +
                "2. Loaded context tracking"
            );
        }))
    );

    @Override
    public String toString() {
        return "CNVLoader[" + name + " with " + loadedContexts.size() + " loaded contexts]";
    }
}
