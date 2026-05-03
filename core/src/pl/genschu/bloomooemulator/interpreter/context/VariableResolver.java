package pl.genschu.bloomooemulator.interpreter.context;

import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.interpreter.variable.capabilities.HasInstanceContext;

import java.util.*;
import java.util.function.Function;

/**
 * Resolves variables
 */
public class VariableResolver {
    private final OriginalEngineQuirksHandler quirksHandler;
    private final BuiltinVariableProvider builtinProvider;
    private final FallbackStrategy fallbackStrategy;

    public VariableResolver(
        OriginalEngineQuirksHandler quirksHandler,
        BuiltinVariableProvider builtinProvider,
        FallbackStrategy fallbackStrategy
    ) {
        this.quirksHandler = quirksHandler;
        this.builtinProvider = builtinProvider;
        this.fallbackStrategy = fallbackStrategy;
    }

    /**
     * Default resolver with legacy compatibility.
     */
    public static VariableResolver createDefault() {
        return new VariableResolver(
            new OriginalEngineQuirksHandler(),
            BuiltinVariableProvider.DEFAULT,
            FallbackStrategy.NAME_AS_STRING_VALUE
        );
    }

    /**
     * Strict resolver without fallback.
     */
    public static VariableResolver createStrict() {
        return new VariableResolver(
            new OriginalEngineQuirksHandler(),
            BuiltinVariableProvider.NONE,
            FallbackStrategy.NONE
        );
    }

    /**
     * Resolves a variable by name (v1-compatible lookup chain).
     *
     * @param name Variable name
     * @param context Context to search from
     * @return Variable or null (depending on fallback strategy)
     */
    public Variable resolve(String name, Context context) {
        // 1. Legacy quirks (THIS, _CURSOR, _\d+)
        Variable quirk = quirksHandler.handle(name, context);
        if (quirk != null) {
            return quirk;
        }

        // 2. Builtins (MOUSE, KEYBOARD)
        Variable builtin = builtinProvider.get(name, context);
        if (builtin != null) {
            return builtin;
        }

        // 3. Context graph (current -> additional contexts -> parent)
        Variable resolved = findInContextGraph(name, context, new HashSet<>());
        if (resolved != null) {
            return resolved;
        }

        // 4. Fallback strategy
        if (fallbackStrategy != null) {
            return fallbackStrategy.createFallback(name, context);
        }

        return null;
    }

    private Variable findInContextGraph(String name, Context context, Set<Context> visited) {
        if (context == null || !visited.add(context)) {
            return null;
        }

        if (context.store().has(name)) {
            return context.store().get(name);
        }

        for (Context additional : context.getAdditionalContexts()) {
            Variable additionalVariable = findInContextGraph(name, additional, visited);
            if (additionalVariable != null) {
                return additionalVariable;
            }
        }

        if (context.getParent() != null) {
            return findInContextGraph(name, context.getParent(), visited);
        }

        return null;
    }

    /**
     * Collects graphics from context hierarchy including additionalContexts and class instances.
     *
     * Order: class instances → additional contexts → current → parent
     *
     * @param context Context to collect from
     * @return Map of graphics variables (unmodifiable)
     */
    public Map<String, Variable> collectGraphics(Context context) {
        return collectByType(context, ctx -> ctx.store().getCacheIndex().getGraphics());
    }

    /**
     * Collects buttons from context hierarchy including additionalContexts and class instances.
     */
    public Map<String, Variable> collectButtons(Context context) {
        return collectByType(context, ctx -> ctx.store().getCacheIndex().getButtons());
    }

    /**
     * Collects timers from context hierarchy including additionalContexts and class instances.
     */
    public Map<String, Variable> collectTimers(Context context) {
        return collectByType(context, ctx -> ctx.store().getCacheIndex().getTimers());
    }

    /**
     * Collects texts from context hierarchy including additionalContexts and class instances.
     */
    public Map<String, Variable> collectTexts(Context context) {
        return collectByType(context, ctx -> ctx.store().getCacheIndex().getTexts());
    }

    /**
     * Collects sounds from context hierarchy including additionalContexts and class instances.
     */
    public Map<String, Variable> collectSounds(Context context) {
        return collectByType(context, ctx -> ctx.store().getCacheIndex().getSounds());
    }

    /**
     * Generic collector for cached variable types.
     *
     * Order: class instances → additional contexts → current → parent
     */
    private Map<String, Variable> collectByType(
        Context context,
        Function<Context, Map<String, Variable>> extractor
    ) {
        return collectByType(context, extractor, new HashSet<>());
    }

    private Map<String, Variable> collectByType(
        Context context,
        Function<Context, Map<String, Variable>> extractor,
        Set<Context> visited
    ) {
        Map<String, Variable> result = new LinkedHashMap<>();
        if (context == null || !visited.add(context)) {
            return result;
        }

        // From class instances (getInstanceContext() on Variable)
        for (Variable var : context.store().getAll().values()) {
            if(var instanceof HasInstanceContext hic) {
                Context instanceCtx = hic.getInstanceContext();
                if (instanceCtx != null) {
                    result.putAll(collectByType(instanceCtx, extractor, visited));
                }
            }
        }

        // From additional contexts
        for (Context additional : context.getAdditionalContexts()) {
            result.putAll(collectByType(additional, extractor, visited));
        }

        // From current
        result.putAll(extractor.apply(context));

        // From parent (recursive)
        if (context.getParent() != null) {
            result.putAll(collectByType(context.getParent(), extractor, visited));
        }

        return result;
    }

    /**
     * Collects all variables from context hierarchy.
     *
     * @param context Context to collect from
     * @param includeParent Whether to include parent contexts
     * @return Map of all variables (unmodifiable)
     */
    public Map<String, Variable> collectAllVariables(Context context, boolean includeParent) {
        return collectAllVariables(context, includeParent, new HashSet<>());
    }

    private Map<String, Variable> collectAllVariables(Context context, boolean includeParent, Set<Context> visited) {
        Map<String, Variable> result = new LinkedHashMap<>();
        if (context == null || !visited.add(context)) {
            return result;
        }

        // From class instances
        for (Variable var : context.store().getAll().values()) {
            if(var instanceof HasInstanceContext hic) {
                Context instanceCtx = hic.getInstanceContext();
                if (instanceCtx != null) {
                    result.putAll(collectAllVariables(instanceCtx, false, visited));
                }
            }
        }

        // From additional contexts
        for (Context additional : context.getAdditionalContexts()) {
            result.putAll(collectAllVariables(additional, includeParent, visited));
        }

        // From parent
        if (includeParent && context.getParent() != null) {
            result.putAll(collectAllVariables(context.getParent(), true, visited));
        }

        // From current (last = highest priority)
        result.putAll(context.store().getAll());

        return result;
    }
}
