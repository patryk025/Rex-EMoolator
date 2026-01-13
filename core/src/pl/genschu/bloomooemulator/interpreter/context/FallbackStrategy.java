package pl.genschu.bloomooemulator.interpreter.context;

import pl.genschu.bloomooemulator.interpreter.variable.StringVariable;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;

/**
 * Strategy for creating fallback variables when not found.
 */
@FunctionalInterface
public interface FallbackStrategy {
    /**
     * Creates a fallback variable when lookup fails.
     *
     * @param name Variable name that was not found
     * @param context Context where lookup failed
     * @return Fallback variable, or null to indicate "not found"
     */
    Variable createFallback(String name, ContextV2 context);

    /**
     * No fallback - returns null when variable not found.
     */
    FallbackStrategy NONE = (name, context) -> null;

    /**
     * Creates STRING(name)=name when not found.
     */
    FallbackStrategy NAME_AS_STRING_VALUE = (name, context) -> new StringVariable("temp", name);
}
