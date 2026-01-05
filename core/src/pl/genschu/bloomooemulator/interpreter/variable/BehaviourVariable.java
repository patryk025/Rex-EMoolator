package pl.genschu.bloomooemulator.interpreter.variable;

import pl.genschu.bloomooemulator.interpreter.ast.ASTNode;
import pl.genschu.bloomooemulator.interpreter.values.Value;

import java.util.Map;

/**
 * BehaviourVariable stores executable code (AST).
 **/
public record BehaviourVariable(
    String name,
    ASTNode ast,  // The compiled code!
    Map<String, SignalHandler> signals
) implements Variable {

    public BehaviourVariable {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Variable name cannot be null or empty");
        }
        if (ast == null) {
            throw new IllegalArgumentException("AST cannot be null for behaviour");
        }
        if (signals == null) {
            signals = Map.of();
        } else {
            signals = Map.copyOf(signals);
        }
    }

    @Override
    public Value value() {
        throw new UnsupportedOperationException("TODO: Implement");
    }

    @Override
    public VariableType type() {
        return VariableType.BEHAVIOUR;
    }

    @Override
    public Variable withValue(Value newValue) {
        throw new UnsupportedOperationException("Cannot set value on BEHAVIOUR");
    }

    @Override
    public Map<String, VariableMethod> methods() {
        throw new UnsupportedOperationException("TODO: Implement");
    }

    @Override
    public Variable withSignal(String signalName, SignalHandler handler) {
        throw new UnsupportedOperationException("TODO: Implement");
    }
}
