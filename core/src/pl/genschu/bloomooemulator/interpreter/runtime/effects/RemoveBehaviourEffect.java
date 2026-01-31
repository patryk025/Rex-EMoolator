package pl.genschu.bloomooemulator.interpreter.runtime.effects;

import pl.genschu.bloomooemulator.interpreter.context.Context;
import pl.genschu.bloomooemulator.interpreter.values.Value;
import pl.genschu.bloomooemulator.interpreter.variable.SignalHandler;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Effect: remove a behaviour (signal handler) from a variable.
 */
public final class RemoveBehaviourEffect implements Effect {
    private final String signalName;

    public RemoveBehaviourEffect(String signalName) {
        this.signalName = normalizeSignalName(signalName);
    }

    private static String normalizeSignalName(String name) {
        if (name == null) return "";
        return name.replace("$", "^");
    }

    @Override
    public Value apply(Context context, Variable self, List<Value> arguments) {
        // Create new signals map without the removed signal
        Map<String, SignalHandler> currentSignals = self.signals();
        if (currentSignals == null || !currentSignals.containsKey(signalName)) {
            return null; // Signal doesn't exist, nothing to remove
        }

        Map<String, SignalHandler> newSignals = new HashMap<>(currentSignals);
        newSignals.remove(signalName);

        // We need to create a new variable without this signal
        // This is done by calling withSignal with a special "remove" pattern
        // But since Variable.withSignal only adds, we need a different approach

        // For now, we'll use a workaround: set a null handler
        // The updated variable should handle null handlers as "removed"
        // Actually, let's just update via context with modified signals

        // We need access to create a new variable instance with different signals
        // This depends on the variable type - each record type has its own constructor
        // For simplicity, we'll set a dummy handler that does nothing
        // TODO: Implement proper signal removal in Variable interface

        Variable updated = self.withSignal(signalName, null);
        context.updateVariableInHierarchy(self.name(), updated);

        return null;
    }
}
