package pl.genschu.bloomooemulator.interpreter.variable;

import pl.genschu.bloomooemulator.interpreter.values.Value;

import java.util.Map;

/**
 * DoubleVariable holds double value
 **/
public record DoubleVariable(
    String name,
    double doubleValue,
    Map<String, SignalHandler> signals
) implements Variable {

    public DoubleVariable {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Variable name cannot be null or empty");
        }
        if (signals == null) {
            signals = Map.of();
        } else {
            signals = Map.copyOf(signals);
        }
    }

    public DoubleVariable(String name, double doubleValue) {
        this(name, doubleValue, Map.of());
    }

    @Override
    public Value value() {
        throw new UnsupportedOperationException("TODO: Implement based on IntVariable");
    }

    @Override
    public VariableType type() {
        return VariableType.DOUBLE;
    }

    @Override
    public Variable withValue(Value newValue) {
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
}
