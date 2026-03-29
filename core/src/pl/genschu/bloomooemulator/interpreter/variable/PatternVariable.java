package pl.genschu.bloomooemulator.interpreter.variable;

import pl.genschu.bloomooemulator.interpreter.values.*;

import java.util.*;

/**
 * PatternVariable represents a tile pattern/grid layout.
 * Contains grid configuration and element placement.
 *
 * Methods are stubs — not yet implemented in the original engine.
 */
public record PatternVariable(
    String name,
    Map<String, SignalHandler> signals
) implements Variable {

    public PatternVariable {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Variable name cannot be null or empty");
        }
        if (signals == null) {
            signals = Map.of();
        } else {
            signals = Map.copyOf(signals);
        }
    }

    public PatternVariable(String name) {
        this(name, Map.of());
    }

    @Override
    public Value value() {
        return NullValue.INSTANCE;
    }

    @Override
    public VariableType type() {
        return VariableType.PATTERN;
    }

    @Override
    public Variable withValue(Value newValue) {
        return this;
    }

    @Override
    public Map<String, MethodSpec> methods() {
        return METHODS;
    }

    @Override
    public Variable withSignal(String signalName, SignalHandler handler) {
        Map<String, SignalHandler> newSignals = new HashMap<>(signals);
        if (handler != null) {
            newSignals.put(signalName, handler);
        } else {
            newSignals.remove(signalName);
        }
        return new PatternVariable(name, newSignals);
    }

    private static final Map<String, MethodSpec> METHODS = Map.ofEntries(
        Map.entry("ADD", MethodSpec.of((self, args, ctx) -> {
            // TODO: ADD(unknown, posX, posY, animoName, layer?)
            throw new UnsupportedOperationException("PATTERN.ADD not implemented");
        }))
    );

    @Override
    public String toString() {
        return "PatternVariable[" + name + "]";
    }
}
