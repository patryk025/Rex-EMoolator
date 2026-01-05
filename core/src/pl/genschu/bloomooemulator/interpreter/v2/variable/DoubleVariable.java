package pl.genschu.bloomooemulator.interpreter.v2.variable;

import pl.genschu.bloomooemulator.interpreter.v2.values.Value;

import java.util.List;
import java.util.Map;

/**
 * TODO: Implement based on IntVariable template!
 *
 * Copy IntVariable, change:
 * 1. int -> double
 * 2. IntValue -> DoubleValue
 * 3. Adjust methods (ADD, SUB, etc) for double arithmetic
 * 4. Add DOUBLE-specific methods (FLOOR, CEIL, ROUND, etc)
 */
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
