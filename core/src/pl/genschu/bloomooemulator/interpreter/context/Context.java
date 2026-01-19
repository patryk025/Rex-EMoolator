package pl.genschu.bloomooemulator.interpreter.context;

import pl.genschu.bloomooemulator.engine.Game;
import pl.genschu.bloomooemulator.interpreter.values.Value;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.interpreter.runtime.ExecutionContext;

import java.util.*;

/**
 * Context v2 - clean implementation using composition.
 */
public class Context {
    private final ExecutionContext executionContext;
    private final VariableStore store;
    private final VariableResolver resolver;
    private final AttributeStore attributes;

    private Context parent;
    private final List<Context> additionalContexts = new ArrayList<>();

    private Game game;

    /**
     * Creates context with injected ExecutionContext.
     *
     * ExecutionContext comes from interpreter/runtime
     *
     * @param executionContext Runtime context (must not be null)
     */
    public Context(ExecutionContext executionContext) {
        this(executionContext, null, null, VariableResolver.createDefault());
    }

    /**
     * Creates context with parent.
     *
     * @param executionContext Runtime context (must not be null)
     * @param parent Parent context (can be null)
     */
    public Context(ExecutionContext executionContext, Context parent) {
        this(executionContext, parent, null, VariableResolver.createDefault());
    }

    /**
     * Creates context with parent and game.
     *
     * @param executionContext Runtime context (must not be null)
     * @param parent Parent context (can be null)
     * @param game Game instance (can be null)
     */
    public Context(ExecutionContext executionContext, Context parent, Game game) {
        this(executionContext, parent, game, VariableResolver.createDefault());
    }

    /**
     * Full constructor with custom resolver.
     *
     * @param executionContext Runtime context (must not be null)
     * @param parent Parent context (can be null)
     * @param game Game instance (can be null)
     * @param resolver Variable resolver (must not be null)
     */
    public Context(
        ExecutionContext executionContext,
        Context parent,
        Game game,
        VariableResolver resolver
    ) {
        if (executionContext == null) {
            throw new IllegalArgumentException("ExecutionContext cannot be null");
        }
        if (resolver == null) {
            throw new IllegalArgumentException("VariableResolver cannot be null");
        }

        this.executionContext = executionContext;
        this.store = new VariableStore();
        this.resolver = resolver;
        this.attributes = new AttributeStore();
        this.parent = parent;
        this.game = game;
    }

    // ============================================================
    // Delegation to ExecutionContext (runtime/call stack)
    // ============================================================

    /**
     * Returns the execution context (for stack trace, locals, etc.).
     */
    public ExecutionContext exec() {
        return executionContext;
    }

    /**
     * Resolves a Value by name, checking execution locals first, then variables.
     *
     * @param name Variable name
     * @return Value or null if not found
     */
    public Value resolveValue(String name) {
        Value local = executionContext.getLocal(name);
        if (local != null) {
            return local;
        }

        Variable variable = resolver.resolve(name, this);
        return variable != null ? variable.value() : null;
    }

    // ============================================================
    // Delegation to VariableStore (storage)
    // ============================================================

    /**
     * Returns the variable store (for direct access if needed).
     */
    public VariableStore store() {
        return store;
    }

    // ============================================================
    // Delegation to VariableResolver (lookup logic)
    // ============================================================

    /**
     * Gets a variable by name (full lookup chain).
     *
     * This is the main entry point for variable access.
     *
     * @param name Variable name
     * @return Variable or null (depending on fallback strategy)
     */
    public Variable getVariable(String name) {
        return resolver.resolve(name, this);
    }

    /**
     * Sets a variable in this context only.
     *
     * @param name Variable name
     * @param var Variable to set
     */
    public void setVariable(String name, Variable var) {
        store.set(name, var);
    }

    /**
     * Checks if variable exists in this context only.
     *
     * @param name Variable name
     * @return true if exists in this context
     */
    public boolean hasVariable(String name) {
        return store.has(name);
    }

    /**
     * Checks if variable exists in this context or any parent context.
     *
     * @param name Variable name
     * @return true if exists in hierarchy
     */
    public boolean hasVariableInHierarchy(String name) {
        if (store.has(name)) {
            return true;
        }

        if (parent != null) {
            return parent.hasVariableInHierarchy(name);
        }

        return false;
    }

    /**
     * Updates a variable in the context hierarchy.
     *
     * @param name Variable name
     * @param var New variable instance
     * @return true if variable was updated in any context
     */
    public boolean updateVariableInHierarchy(String name, Variable var) {
        if (store.has(name)) {
            store.set(name, var);
            return true;
        }

        if (parent != null) {
            return parent.updateVariableInHierarchy(name, var);
        }

        return false;
    }

    /**
     * Removes a variable from this context only.
     *
     * @param name Variable name
     */
    public void removeVariable(String name) {
        store.remove(name);
    }

    /**
     * Gets all variables from context hierarchy (not including parent).
     *
     * @return Map of all variables (unmodifiable)
     */
    public Map<String, Variable> getVariables() {
        return resolver.collectAllVariables(this, false);
    }

    /**
     * Gets all variables from context hierarchy.
     *
     * @param includeParent Whether to include parent contexts
     * @return Map of all variables (unmodifiable)
     */
    public Map<String, Variable> getVariables(boolean includeParent) {
        return resolver.collectAllVariables(this, includeParent);
    }

    // ============================================================
    // Cached views (delegated to resolver)
    // ============================================================

    /**
     * Gets all graphics variables from context hierarchy.
     * includes additionalContexts and classInstances.
     *
     * @return Map of graphics variables (unmodifiable)
     */
    public Map<String, Variable> getGraphicsVariables() {
        return resolver.collectGraphics(this);
    }

    /**
     * Gets all button variables from context hierarchy.
     * includes additionalContexts and classInstances.
     *
     * @return Map of button variables (unmodifiable)
     */
    public Map<String, Variable> getButtonsVariables() {
        return resolver.collectButtons(this);
    }

    /**
     * Gets all timer variables from context hierarchy.
     * includes additionalContexts and classInstances.
     *
     * @return Map of timer variables (unmodifiable)
     */
    public Map<String, Variable> getTimerVariables() {
        return resolver.collectTimers(this);
    }

    /**
     * Gets all text variables from context hierarchy.
     * includes additionalContexts and classInstances.
     *
     * @return Map of text variables (unmodifiable)
     */
    public Map<String, Variable> getTextVariables() {
        return resolver.collectTexts(this);
    }

    /**
     * Gets all sound variables from context hierarchy.
     * includes additionalContexts and classInstances.
     *
     * @return Map of sound variables (unmodifiable)
     */
    public Map<String, Variable> getSoundVariables() {
        return resolver.collectSounds(this);
    }

    // ============================================================
    // Singleton variables (convenience methods)
    // ============================================================

    /**
     * Gets the MOUSE variable (searches up parent chain).
     *
     * @return MOUSE variable or null
     */
    public Variable getMouseVariable() {
        Variable mouse = store.getCacheIndex().getMouse();
        if (mouse != null) return mouse;

        if (parent != null) {
            return parent.getMouseVariable();
        }

        return null;
    }

    /**
     * Gets the KEYBOARD variable (searches up parent chain).
     *
     * @return KEYBOARD variable or null
     */
    public Variable getKeyboardVariable() {
        Variable keyboard = store.getCacheIndex().getKeyboard();
        if (keyboard != null) return keyboard;

        if (parent != null) {
            return parent.getKeyboardVariable();
        }

        return null;
    }

    /**
     * Gets the WORLD variable (searches up parent chain).
     *
     * @return WORLD variable or null
     */
    public Variable getWorldVariable() {
        Variable world = store.getCacheIndex().getWorld();
        if (world != null) return world;

        if (parent != null) {
            return parent.getWorldVariable();
        }

        return null;
    }

    // ============================================================
    // Context hierarchy
    // ============================================================

    /**
     * Gets the parent context.
     *
     * @return Parent context or null
     */
    public Context getParent() {
        return parent;
    }

    /**
     * Sets the parent context.
     *
     * @param parent Parent context
     */
    public void setParent(Context parent) {
        this.parent = parent;
    }

    /**
     * Gets additional contexts (unmodifiable list).
     * These are contexts loaded by CNVLoader.
     *
     * @return List of additional contexts
     */
    public List<Context> getAdditionalContexts() {
        return Collections.unmodifiableList(additionalContexts);
    }

    /**
     * Adds an additional context (from CNVLoader).
     *
     * @param context Additional context to add
     */
    public void addAdditionalContext(Context context) {
        additionalContexts.add(context);
    }

    /**
     * Removes an additional context.
     *
     * @param context Additional context to remove
     */
    public void removeAdditionalContext(Context context) {
        additionalContexts.remove(context);
    }

    // ============================================================
    // Game reference
    // ============================================================

    /**
     * Gets the game instance (searches up parent chain).
     *
     * @return Game instance or null
     */
    public Game getGame() {
        if (game != null) {
            return game;
        }

        if (parent != null) {
            return parent.getGame();
        }

        return null;
    }

    /**
     * Sets the game instance.
     *
     * @param game Game instance
     */
    public void setGame(Game game) {
        this.game = game;
    }

    // ============================================================
    // Attributes (for CNV parser)
    // ============================================================

    /**
     * Returns the attribute store (for CNV parser access).
     *
     * @return Attribute store
     */
    public AttributeStore attributes() {
        return attributes;
    }

    /**
     * Sets an attribute for a variable.
     *
     * @param varName Variable name
     * @param attributeName Attribute name
     * @param attributeValue Attribute value
     */
    public void setAttribute(String varName, String attributeName, String attributeValue) {
        attributes.set(varName, attributeName, attributeValue);
    }

    /**
     * Gets an attribute for a variable.
     *
     * @param varName Variable name
     * @param attributeName Attribute name
     * @return Attribute value or null
     */
    public String getAttribute(String varName, String attributeName) {
        return attributes.get(varName, attributeName);
    }

    // ============================================================
    // Utility methods
    // ============================================================

    /**
     * Clears all variables in this context.
     */
    public void clear() {
        store.clear();
        attributes.clear();
    }

    @Override
    public String toString() {
        return "Context[vars=" + store.size() +
               ", parent=" + (parent != null) +
               ", additional=" + additionalContexts.size() + "]";
    }
}
