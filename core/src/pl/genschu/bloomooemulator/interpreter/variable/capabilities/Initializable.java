package pl.genschu.bloomooemulator.interpreter.variable.capabilities;

import pl.genschu.bloomooemulator.interpreter.context.Context;

/**
 * Capability interface for variables that need initialization after CNV parsing.
 *
 * Called after all variables are parsed but before ONINIT signals are emitted.
 * Use this for:
 * - DATABASE: resolve MODEL -> STRUCT and copy column schema
 * - ANIMO: load animation frames
 * - SOUND: prepare audio resources
 *
 * Variables implementing this interface should handle missing dependencies gracefully
 * (e.g., MODEL STRUCT not found -> log warning, continue with empty columns).
 */
public interface Initializable {
    /**
     * Initialize this variable using the given context.
     * Called once after CNV parsing is complete.
     *
     * @param context The context containing all parsed variables
     */
    void init(Context context);
}
