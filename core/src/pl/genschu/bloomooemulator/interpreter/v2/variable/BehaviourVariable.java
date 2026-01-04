package pl.genschu.bloomooemulator.interpreter.v2.variable;

import pl.genschu.bloomooemulator.interpreter.v2.ast.ASTNode;
import pl.genschu.bloomooemulator.interpreter.v2.values.Value;

import java.util.List;
import java.util.Map;

/**
 * TODO: Implement!
 *
 * BehaviourVariable stores executable code (AST).
 *
 * Key difference from primitives:
 * - Stores ASTNode instead of primitive value
 * - RUN() method executes the AST
 * - RUNC() runs with condition check
 * - RUNLOOPED() runs in loop
 */
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
    public Variable callMethod(String methodName, List<Value> arguments) {
        throw new UnsupportedOperationException("TODO: Implement RUN, RUNC, RUNLOOPED");
    }

    @Override
    public Map<String, VariableMethod> methods() {
        throw new UnsupportedOperationException("TODO: Implement");
    }

    @Override
    public Variable withSignal(String signalName, SignalHandler handler) {
        throw new UnsupportedOperationException("TODO: Implement");
    }

    @Override
    public void emitSignal(String signalName, Value argument) {
        throw new UnsupportedOperationException("TODO: Implement");
    }
}
