package pl.genschu.bloomooemulator.interpreter.context;

import pl.genschu.bloomooemulator.interpreter.variable.Variable;

import java.util.*;

/**
 * Storage for variables in a single context.
 */
public class VariableStore {
    private final Map<String, Variable> variables = new LinkedHashMap<>();
    private final VariableCacheIndex cacheIndex = new VariableCacheIndex();

    /**
     * Gets a variable by name from this store only.
     * Does not check parent/additional contexts.
     *
     * @return Variable or null if not found
     */
    public Variable get(String name) {
        return variables.get(name);
    }

    /**
     * Sets/updates a variable in this store.
     * Automatically updates cache index.
     */
    public void set(String name, Variable var) {
        Variable oldVar = variables.get(name);
        // Only evict the cache entry when the variable is new or its type changed.
        // For same-type updates, rely on LinkedHashMap.put() to update the value
        // in place — this preserves insertion order, which load-order-sensitive
        // consumers (e.g. priority-sorted render lists) depend on.
        if (oldVar != null && oldVar.type() != var.type()) {
            cacheIndex.remove(name, oldVar);
        }

        variables.put(name, var);
        cacheIndex.index(name, var);
    }

    /**
     * Checks if variable exists in this store only.
     */
    public boolean has(String name) {
        return variables.containsKey(name);
    }

    /**
     * Removes a variable from this store.
     */
    public void remove(String name) {
        Variable oldVar = variables.remove(name);
        if (oldVar != null) {
            cacheIndex.remove(name, oldVar);
        }
    }

    /**
     * Returns all variables in this store (unmodifiable).
     */
    public Map<String, Variable> getAll() {
        return Collections.unmodifiableMap(variables);
    }

    /**
     * Returns cache index (for accessing cached views).
     */
    public VariableCacheIndex getCacheIndex() {
        return cacheIndex;
    }

    /**
     * Clears all variables and cache.
     */
    public void clear() {
        variables.clear();
        cacheIndex.clear();
    }

    /**
     * Returns number of variables in this store.
     */
    public int size() {
        return variables.size();
    }
}
