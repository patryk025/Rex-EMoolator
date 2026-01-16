package pl.genschu.bloomooemulator.interpreter.context;

import java.util.HashMap;
import java.util.Map;

/**
 * AttributeStore holds variable attributes loaded from CNV files.
 */
public class AttributeStore {
    // varName -> attributeName -> attributeValue
    private final Map<String, Map<String, String>> attributes = new HashMap<>();

    /**
     * Sets an attribute for a variable.
     *
     * @param varName Variable name
     * @param attributeName Attribute name (e.g., "DEF", "MODEL")
     * @param attributeValue Attribute value
     */
    public void set(String varName, String attributeName, String attributeValue) {
        attributes
            .computeIfAbsent(varName, k -> new HashMap<>())
            .put(attributeName, attributeValue);
    }

    /**
     * Gets an attribute for a variable.
     *
     * @param varName Variable name
     * @param attributeName Attribute name
     * @return Attribute value or null
     */
    public String get(String varName, String attributeName) {
        Map<String, String> varAttrs = attributes.get(varName);
        if (varAttrs == null) {
            return null;
        }
        return varAttrs.get(attributeName);
    }

    /**
     * Gets all attributes for a variable.
     *
     * @param varName Variable name
     * @return Map of attributes (unmodifiable) or empty map
     */
    public Map<String, String> getAll(String varName) {
        Map<String, String> varAttrs = attributes.get(varName);
        if (varAttrs == null) {
            return Map.of();
        }
        return Map.copyOf(varAttrs);
    }

    /**
     * Removes all attributes for a variable.
     *
     * @param varName Variable name
     */
    public void remove(String varName) {
        attributes.remove(varName);
    }

    /**
     * Checks if a variable has any attributes.
     *
     * @param varName Variable name
     * @return True if variable has attributes
     */
    public boolean has(String varName) {
        return attributes.containsKey(varName);
    }

    /**
     * Clears all attributes.
     */
    public void clear() {
        attributes.clear();
    }
}
