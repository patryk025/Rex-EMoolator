package pl.genschu.bloomooemulator.interpreter.variable;

import pl.genschu.bloomooemulator.interpreter.v2.values.*;
import pl.genschu.bloomooemulator.interpreter.values.*;

import java.util.Map;

/**
 * StringVariable holds text value
 **/
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
        return new StringValue(stringValue);
    }

    @Override
    public VariableType type() {
        return VariableType.STRING;
    }

    @Override
    public Variable withValue(Value newValue) {
        String newString = switch (newValue) {
            case StringValue v -> v.value();
            case IntValue v -> String.valueOf(v.value());
            case DoubleValue v -> String.valueOf(v.value());
            case BoolValue v -> v.toDisplayString();
            case NullValue __ -> "NULL";
            default -> newValue.toDisplayString();
        };

        return new StringVariable(name, newString, signals);
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
