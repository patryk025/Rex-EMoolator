package pl.genschu.bloomooemulator.interpreter.context;

import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.interpreter.variable.*;

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
     */
    BuiltinVariableProvider DEFAULT = (name, context) -> {
        String normalized = name == null ? "" : name.trim().toUpperCase();
        String canonicalName = switch (normalized) {
            case "RANDOM" -> "RAND";
            case "MOUSE", "KEYBOARD", "RAND", "SYSTEM" -> normalized;
            default -> null;
        };

        if (canonicalName == null) {
            return null;
        }

        Variable existing = findInHierarchy(context, canonicalName);
        if (existing != null) {
            return existing;
        }

        Context root = getRootContext(context);
        Variable created = switch (canonicalName) {
            case "MOUSE" -> new MouseVariable("MOUSE");
            case "KEYBOARD" -> new KeyboardVariable("KEYBOARD");
            case "RAND" -> new RandVariable("RAND");
            case "SYSTEM" -> new SystemVariable("SYSTEM");
            default -> null;
        };

        if (created != null) {
            root.setVariable(created.name(), created);
        }
        return created;
    };

    private static Context getRootContext(Context context) {
        Context current = context;
        while (current.getParent() != null) {
            current = current.getParent();
        }
        return current;
    }

    private static Variable findInHierarchy(Context context, String canonicalName) {
        for (Context current = context; current != null; current = current.getParent()) {
            if (current.store().has(canonicalName)) {
                return current.store().get(canonicalName);
            }
        }
        return null;
    }
}
