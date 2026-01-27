package pl.genschu.bloomooemulator.interpreter.context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Stores clone relationships for variables within a context.
 */
public class CloneRegistry {
    private final Map<String, List<String>> clonesByBase = new HashMap<>();

    /**
     * Registers a clone name under a base variable name.
     */
    public void registerClone(String baseName, String cloneName) {
        if (baseName == null || baseName.isEmpty()) {
            throw new IllegalArgumentException("Base name cannot be null or empty");
        }
        if (cloneName == null || cloneName.isEmpty()) {
            throw new IllegalArgumentException("Clone name cannot be null or empty");
        }
        List<String> clones = clonesByBase.computeIfAbsent(baseName, k -> new ArrayList<>());
        if (!clones.contains(cloneName)) {
            clones.add(cloneName);
        }
    }

    /**
     * Returns clone names for a base variable.
     */
    public List<String> getCloneNames(String baseName) {
        List<String> clones = clonesByBase.get(baseName);
        if (clones == null) {
            return List.of();
        }
        return Collections.unmodifiableList(clones);
    }

    /**
     * Removes a clone entry.
     */
    public void removeClone(String baseName, String cloneName) {
        List<String> clones = clonesByBase.get(baseName);
        if (clones == null) {
            return;
        }
        clones.remove(cloneName);
        if (clones.isEmpty()) {
            clonesByBase.remove(baseName);
        }
    }

    /**
     * Clears all clone mappings.
     */
    public void clear() {
        clonesByBase.clear();
    }
}
