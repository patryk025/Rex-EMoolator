package pl.genschu.bloomooemulator.interpreter.variable;

import pl.genschu.bloomooemulator.interpreter.values.NullValue;
import pl.genschu.bloomooemulator.interpreter.values.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class StructVariable implements Variable {
    private final String name;
    private final List<Variable> valuesInOrder; // or maybe List<Value> will be better option?

    public StructVariable(String name, List<Variable> valuesInOrder) {
        this.name = name;
        this.valuesInOrder = valuesInOrder != null ? List.copyOf(valuesInOrder) : List.of();
    }

    @Override public String name() { return name; }
    @Override public Value value() { return NullValue.INSTANCE; }
    @Override public VariableType type() { return VariableType.STRUCT; }
    @Override public Variable withValue(Value newValue) { return this; }

    @Override public Map<String, VariableMethod> methods() { return Map.of(); }
    @Override public Map<String, SignalHandler> signals() { return Map.of(); }
    @Override public Variable withSignal(String signalName, SignalHandler handler) { return this; }

    public List<String> toRowStrings() {
        List<String> out = new ArrayList<>(valuesInOrder.size());
        for (Variable v : valuesInOrder) {
            out.add(v == null ? "" : v.value().toDisplayString());
        }
        return out;
    }
}
