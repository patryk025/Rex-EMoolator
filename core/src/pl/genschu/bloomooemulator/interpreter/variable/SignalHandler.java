package pl.genschu.bloomooemulator.interpreter.variable;

import pl.genschu.bloomooemulator.interpreter.values.Value;

/**
 * Handler for variable signals (events).
 *
 * Signals in AidemMedia:
 * - ONINIT - when variable is initialized
 * - ONCHANGED - when value changes
 * - ONBRUTALCHANGED - when value is set (even if same)
 * - ONCOLLISION - when collision detected
 * - etc.
 */
@FunctionalInterface
public interface SignalHandler {
    /**
     * Handles the signal.
     *
     * @param variable The variable that emitted the signal
     * @param signalName The name of the signal
     * @param arguments Explicit payload passed with the signal; qualifiers are
     *                  represented only in {@code signalName}
     */
    void handle(Variable variable, String signalName, Value... arguments);

    /**
     * Structured dispatch entry point. Legacy/test lambdas continue to work via
     * the three-argument functional method, while named handlers can inspect the
     * payload policy and selected binding.
     */
    default void handle(Variable variable, SignalEmission emission) {
        handle(variable, emission.emittedName(), emission.argumentArray());
    }
}
