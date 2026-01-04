package pl.genschu.bloomooemulator.loader.v2;

import pl.genschu.bloomooemulator.interpreter.variable.types.BehaviourVariable;

/**
 * Structured representation of a signal definition from CNV file.
 *
 * Replaces the messy closure pattern from v1 with clean data structure.
 *
 * Examples:
 * - ONINIT=MyBehaviour(param1, param2)  → behaviour=MyBehaviour, params=[param1, param2]
 * - ONCLICK={@PRINT("hello");}          → behaviour=anonymous, params=null
 */
public record SignalDefinition(
    String signalName,
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
        if (behaviourVariable == null) {
            throw new IllegalArgumentException("Behaviour variable cannot be null");
        }
        // params can be null - that's fine
    }

    /**
     * Convenience constructor without params.
     */
    public SignalDefinition(String signalName, BehaviourVariable behaviourVariable) {
        this(signalName, behaviourVariable, null);
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
