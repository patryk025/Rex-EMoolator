package pl.genschu.bloomooemulator.interpreter.variable;

import pl.genschu.bloomooemulator.interpreter.values.Value;

import java.util.List;
import java.util.Map;

/**
 * BoolVariable holds boolean value
 **/
public record BoolVariable(
    String name,
    boolean boolValue,
    Map<String, SignalHandler> signals
) implements Variable {

    public BoolVariable {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Variable name cannot be null or empty");
        }
        if (signals == null) {
            signals = Map.of();
        } else {
            signals = Map.copyOf(signals);
        }
    }

    public BoolVariable(String name, boolean boolValue) {
        this(name, boolValue, Map.of());
    }

    @Override
    public Value value() {
        throw new UnsupportedOperationException("TODO: Implement based on IntVariable");
    }

    @Override
    public VariableType type() {
        return VariableType.BOOLEAN;
    }

    @Override
    public Variable withValue(Value newValue) {
        throw new UnsupportedOperationException("TODO: Implement based on IntVariable");
    }

    @Override
    public Variable callMethod(String methodName, List<Value> arguments) {
        throw new UnsupportedOperationException("TODO: Implement based on IntVariable");
    }

    @Override
    public Map<String, VariableMethod> methods() {
        throw new UnsupportedOperationException("TODO: Implement based on IntVariable");
    }

    @Override
    public Variable withSignal(String signalName, SignalHandler handler) {
        throw new UnsupportedOperationException("TODO: Implement based on IntVariable");
    }

    @Override
    public void emitSignal(String signalName, Value argument) {
        throw new UnsupportedOperationException("TODO: Implement based on IntVariable");
    }
}
