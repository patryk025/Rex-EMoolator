package pl.genschu.bloomooemulator.interpreter.variable;

import pl.genschu.bloomooemulator.annotations.InternalMutable;
import pl.genschu.bloomooemulator.interpreter.values.Value;
import pl.genschu.bloomooemulator.interpreter.variable.capabilities.CloneableVar;

import java.util.List;
import java.util.Map;

/**
 * ArrayVariable stores a list of Values.
 **/
public record ArrayVariable(
    String name,
    @InternalMutable
    List<Value> elements,
    Map<String, SignalHandler> signals
) implements Variable, CloneableVar {

    public ArrayVariable {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Variable name cannot be null or empty");
        }
        if (elements == null) {
            elements = List.of();
        } else {
            elements = List.copyOf(elements);  // Immutable
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
    public Map<String, VariableMethod> methods() {
        throw new UnsupportedOperationException("TODO: Implement");
    }

    @Override
    public Variable withSignal(String signalName, SignalHandler handler) {
        throw new UnsupportedOperationException("TODO: Implement");
    }

    @Override
    public List<Variable> getClones() {
        return List.of();
    }

    @Override
    public Variable withAddedClone(Variable clone) {
        return null;
    }
}
