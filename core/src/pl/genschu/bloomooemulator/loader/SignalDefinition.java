package pl.genschu.bloomooemulator.loader;

import pl.genschu.bloomooemulator.interpreter.v1.variable.types.BehaviourVariable;

/**
 * Structured representation of a signal definition from CNV file.
 *
 * Replaces the messy closure pattern from v1 with clean data structure.
 *
 * Examples:
 * - ONINIT=MyBehaviour(param1, param2)  → behaviour=MyBehaviour, params=[param1, param2]
 * - ONCLICK={TEST^SET("hello");}          → behaviour=anonymous, params=null
 */
public record SignalDefinition(
    String signalName,
    String[] signalParams,
    BehaviourVariable behaviourVariable,
    String[] params
) {
    /**
     * Constructor with validation.
     */
    public SignalDefinition {
        if (signalName == null || signalName.isEmpty()) {
            throw new IllegalArgumentException("Signal name cannot be null or empty");
        }
        String[] parts = signalName.split("\\^", 2);
        signalName = parts[0];
        signalParams = (parts.length > 1 && !parts[1].isEmpty())
                ? parts[1].split("\\^")
                : null;
        signalParams = (signalParams != null) ? signalParams.clone() : null; // ensure immutability
        if (behaviourVariable == null) {
            throw new IllegalArgumentException("Behaviour variable cannot be null");
        }
        // params can be null - that's fine
    }

    /**
     * Convenience constructor without params.
     */
    public SignalDefinition(String signalName, BehaviourVariable behaviourVariable) {
        this(signalName, null, behaviourVariable, null);
    }

    public SignalDefinition(String signalName, BehaviourVariable behaviourVariable, String[] params) {
        this(signalName, null, behaviourVariable, params);
    }

    /**
     * Returns true if this signal has parameters.
     */
    public boolean hasParams() {
        return params != null && params.length > 0;
    }

    @Override
    public String toString() {
        return "SignalDefinition[" +
               "signal=" + signalName +
               ", behaviour=" + behaviourVariable.getName() +
               ", params=" + (hasParams() ? String.join(",", params) : "none") +
               "]";
    }
}
