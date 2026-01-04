package pl.genschu.bloomooemulator.interpreter.v2.variable;

import pl.genschu.bloomooemulator.interpreter.v2.values.Value;

import java.util.List;
import java.util.Map;

/**
 * TODO: Implement!
 *
 * ArrayVariable stores a list of Values.
 *
 * Methods to implement:
 * - GET(index) - get element at index
 * - SET(index, value) - set element at index
 * - ADD(value) - append element
 * - REMOVE(index) - remove element
 * - LEN() - array length
 * - CLEAR() - clear array
 */
public record ArrayVariable(
    String name,
    List<Value> elements,
    Map<String, SignalHandler> signals
) implements Variable {

    public ArrayVariable {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Variable name cannot be null or empty");
        }
        if (elements == null) {
            elements = List.of();
        } else {
            elements = List.copyOf(elements);  // Immutable!
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
        return VariableType.ARRAY;
    }

    @Override
    public Variable withValue(Value newValue) {
        throw new UnsupportedOperationException("TODO: Implement");
    }

    @Override
    public Variable callMethod(String methodName, List<Value> arguments) {
        throw new UnsupportedOperationException("TODO: Implement");
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
