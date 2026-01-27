package pl.genschu.bloomooemulator.interpreter.context;

import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.interpreter.variable.VariableType;
import pl.genschu.bloomooemulator.interpreter.runtime.ExecutionContext;
import pl.genschu.bloomooemulator.interpreter.variable.capabilities.HasCursor;

/**
 * Handles original engine quirks using capability interfaces.
 *
 * Quirks:
 * 1. "THIS" - resolves to current execution context's this value
 * 2. "_CURSOR" suffix - resolves to database cursor
 * 3. "_\d+" suffix - resolves to clones (e.g., SPRITE_0, SPRITE_1)
 */
public class OriginalEngineQuirksHandler {

    /**
     * Checks if name matches a quirk and handles it.
     *
     * @return Variable if quirk handled, null otherwise
     */
    public Variable handle(String name, Context context) {
        // 1. Handle THIS
        if ("THIS".equals(name)) {
            return resolveThis(context);
        }

        // 2. Handle _CURSOR suffix
        if (name.endsWith("_CURSOR")) {
            return resolveCursor(name, context);
        }

        // 3. Handle clone suffix _\d+
        if (name.matches(".*_\\d+$")) {
            return resolveClone(name, context);
        }

        return null;
    }

    /**
     * Resolves THIS from ExecutionContext.
     */
    private Variable resolveThis(Context context) {
        // Get from ExecutionContext (injected!)
        ExecutionContext exec = context.exec();
        if (exec == null) {
            return null;
        }

        return exec.getThis();
    }

    /**
     * Resolves _CURSOR using HasCursor capability.
     *
     * Example: DATABASE_CURSOR -> DATABASE.getCursor()
     */
    private Variable resolveCursor(String name, Context context) {
        String baseName = name.substring(0, name.length() - "_CURSOR".length());
        Variable baseVar = context.getVariable(baseName);

        if (baseVar == null) {
            return null;
        }

        if (baseVar instanceof HasCursor hc) return hc.getCursor();
        return null;
    }

    /**
     * Resolves clone using the context clone registry.
     *
     * Logic (from v1):
     * - SPRITE_0: First check if "SPRITE_0" exists as variable.
     *             If not, return SPRITE itself (unless SPRITE is STRING("SPRITE")).
     * - SPRITE_1+: Find in SPRITE.getClones()
     */
    private Variable resolveClone(String name, Context context) {
        int lastUnderscore = name.lastIndexOf('_');
        String baseName = name.substring(0, lastUnderscore);
        int cloneNumber;

        try {
            cloneNumber = Integer.parseInt(name.substring(lastUnderscore + 1));
        } catch (NumberFormatException e) {
            // Not a valid clone number
            return null;
        }

        if (cloneNumber == 0) {
            // Special case: _0 can be the variable itself

            // First check if full name exists
            if (context.store().has(name)) {
                return context.store().get(name);
            }

            // Then check base variable
            Variable baseVar = context.getVariable(baseName);
            if (baseVar == null) {
                return null;
            }

            // v1 quirk: if base is STRING and its value equals its name,
            // don't return it as _0 (it's a fallback string, not real variable)
            if (baseVar.type() == VariableType.STRING) {
                // TODO: Check if value().toDisplayString().equals(baseName)
                // For now, return it anyway
            }

            return baseVar;
        } else {
            // _1+: Find in clones list (using capability)
            for (String cloneName : context.clones().getCloneNames(baseName)) {
                if (cloneName.equals(name)) {
                    return context.getVariable(cloneName);
                }
            }

            return null;
        }
    }
}
