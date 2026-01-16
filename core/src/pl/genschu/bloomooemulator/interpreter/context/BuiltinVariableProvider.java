package pl.genschu.bloomooemulator.interpreter.context;

import pl.genschu.bloomooemulator.interpreter.variable.Variable;

/**
 * Provider for built-in global variables (MOUSE, KEYBOARD, etc.).
 */
@FunctionalInterface
public interface BuiltinVariableProvider {
    /**
     * Gets a builtin variable by name.
     *
     * @param name Variable name (e.g., "MOUSE", "KEYBOARD")
     * @param context Context requesting the builtin
     * @return Builtin variable, or null if not a builtin
     */
    Variable get(String name, Context context);

    /**
     * No builtins - always returns null.
     */
    BuiltinVariableProvider NONE = (name, context) -> null;

    /**
     * Default builtin provider - creates MOUSE, KEYBOARD, etc.
     *
     * TODO: Implement this when we have Variable factories and refactored variables.
     * For now, returns null.
     */
    BuiltinVariableProvider DEFAULT = (name, context) -> {
        // TODO: Implement builtin creation
        // - MOUSE → MouseVariable
        // - KEYBOARD → KeyboardVariable
        // - etc.
        return null;
    };
}
