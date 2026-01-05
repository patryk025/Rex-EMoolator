package pl.genschu.bloomooemulator.interpreter.v2.variable;

import pl.genschu.bloomooemulator.interpreter.v2.values.Value;

import java.util.List;
import java.util.Map;

/**
 * TODO: Implement based on IntVariable template!
 *
 * String-specific methods to implement:
 * - GET(index) - get character at index
 * - GET(index, length) - substring
 * - LEN() - string length
 * - CONCAT(string) - concatenate
 * - UPPER() - to uppercase
 * - LOWER() - to lowercase
 * - TRIM() - trim whitespace
 * - REPLACE(old, new) - replace substring
 * etc.
 */
public record StringVariable(
    String name,
    String stringValue,
    Map<String, SignalHandler> signals
) implements Variable {

    public StringVariable {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Variable name cannot be null or empty");
        }
        if (stringValue == null) {
            stringValue = "";
        }
        if (signals == null) {
            signals = Map.of();
        } else {
            signals = Map.copyOf(signals);
        }
    }

    public StringVariable(String name, String stringValue) {
        this(name, stringValue, Map.of());
    }

    @Override
    public Value value() {
        throw new UnsupportedOperationException("TODO: Implement based on IntVariable");
    }

    @Override
    public VariableType type() {
        return VariableType.STRING;
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
