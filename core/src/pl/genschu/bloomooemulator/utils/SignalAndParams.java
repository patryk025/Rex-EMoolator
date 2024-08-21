package pl.genschu.bloomooemulator.utils;

import pl.genschu.bloomooemulator.interpreter.variable.types.BehaviourVariable;

// Helper class to store the behaviour variable and its parameters
public class SignalAndParams {
    public BehaviourVariable behaviourVariable;
    public String[] params;

    public SignalAndParams(BehaviourVariable behaviourVariable, String[] params) {
        this.behaviourVariable = behaviourVariable;
        this.params = params;
    }
}