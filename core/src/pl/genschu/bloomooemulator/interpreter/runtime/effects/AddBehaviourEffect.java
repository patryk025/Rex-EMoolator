package pl.genschu.bloomooemulator.interpreter.runtime.effects;

import pl.genschu.bloomooemulator.interpreter.context.Context;
import pl.genschu.bloomooemulator.interpreter.values.Value;
import pl.genschu.bloomooemulator.interpreter.variable.BehaviourVariable;
import pl.genschu.bloomooemulator.interpreter.variable.SignalHandler;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;

import java.util.List;

/**
 * Effect: add a behaviour (signal handler) to a variable.
 *
 * Parses behaviourName which can be:
 * - Simple name: "MYBEHAVIOUR"
 * - Name with params: "MYBEHAVIOUR(\"param1\", $VAR)"
 */
public final class AddBehaviourEffect implements Effect {
    private final String signalName;
    private final String behaviourSpec;

    public AddBehaviourEffect(String signalName, String behaviourSpec) {
        this.signalName = normalizeSignalName(signalName);
        this.behaviourSpec = behaviourSpec;
    }

    private static String normalizeSignalName(String name) {
        if (name == null) return "";
        return name.replace("$", "^");
    }

    @Override
    public Value apply(Context context, Variable self, List<Value> arguments) {
        // Parse behaviour spec - may have parameters
        String behaviourName = behaviourSpec;
        String[] params = null;

        if (behaviourSpec.contains("(") && behaviourSpec.endsWith(")")) {
            int parenStart = behaviourSpec.indexOf('(');
            behaviourName = behaviourSpec.substring(0, parenStart);
            String paramStr = behaviourSpec.substring(parenStart + 1, behaviourSpec.length() - 1);
            if (!paramStr.isEmpty()) {
                params = paramStr.split(",");
                for (int i = 0; i < params.length; i++) {
                    params[i] = params[i].trim();
                    // Remove quotes from string params
                    if ((params[i].startsWith("\"") && params[i].endsWith("\"")) ||
                        (params[i].startsWith("'") && params[i].endsWith("'"))) {
                        params[i] = params[i].substring(1, params[i].length() - 1);
                    }
                }
            }
        }

        // Get behaviour variable
        String finalBehaviourName = behaviourName.trim();
        Variable behaviourVar = context.getVariable(finalBehaviourName);
        if (!(behaviourVar instanceof BehaviourVariable behaviour)) {
            // TODO: Log error - behaviour not found
            return null;
        }

        // Create signal handler that executes the behaviour
        String[] finalParams = params;
        SignalHandler handler = (variable, signal, args) -> {
            // Execute behaviour with parameters
            // Pass params as $1, $2, etc.
            context.exec().pushFrame("Signal:" + signal, finalBehaviourName, null);
            try {
                if (finalParams != null) {
                    for (int i = 0; i < finalParams.length; i++) {
                        String paramValue = finalParams[i];
                        // Resolve variable references
                        if (paramValue.startsWith("$") && paramValue.length() > 1) {
                            Variable paramVar = context.getVariable(paramValue.substring(1));
                            if (paramVar != null) {
                                context.exec().setLocal("$" + (i + 1), paramVar.value());
                                continue;
                            }
                        }
                        context.exec().setLocal("$" + (i + 1), new pl.genschu.bloomooemulator.interpreter.values.StringValue(paramValue));
                    }
                }

                // Set THIS to the signal source variable
                context.exec().setThis(variable);

                // Execute behaviour
                new RunBehaviourEffect(false).apply(context, behaviour, List.of());
            } finally {
                context.exec().popFrame();
            }
        };

        // Add signal to variable
        Variable updated = self.withSignal(signalName, handler);
        context.updateVariableInHierarchy(self.name(), updated);

        return null;
    }
}
