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
     * @param arguments Optional arguments passed with the signal
     */
    void handle(Variable variable, String signalName, Value... arguments);
}
