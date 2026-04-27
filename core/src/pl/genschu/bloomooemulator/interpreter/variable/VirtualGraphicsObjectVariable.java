package pl.genschu.bloomooemulator.interpreter.variable;

import pl.genschu.bloomooemulator.interpreter.values.*;

import java.util.*;

/**
 * VirtualGraphicsObjectVariable represents a virtual graphics object.
 * Acts as a proxy/composite for graphical elements with position, size, and mask support.
 *
 * All methods are stubs — not yet implemented in the original engine.
 */
public record VirtualGraphicsObjectVariable(
    String name,
    Map<String, SignalHandler> signals
) implements Variable {

    public VirtualGraphicsObjectVariable {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Variable name cannot be null or empty");
        }
        if (signals == null) {
            signals = Map.of();
        } else {
            signals = Map.copyOf(signals);
        }
    }

    public VirtualGraphicsObjectVariable(String name) {
        this(name, Map.of());
    }

    @Override
    public Value value() {
        return NullValue.INSTANCE;
    }

    @Override
    public VariableType type() {
        return VariableType.VIRTUALGRAPHICSOBJECT;
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
        return new VirtualGraphicsObjectVariable(name, newSignals);
    }

    private static final Map<String, MethodSpec> METHODS = Map.ofEntries(
        Map.entry("GETHEIGHT", MethodSpec.of((self, args, ctx) -> {
            throw new UnsupportedOperationException("VIRTUALGRAPHICSOBJECT.GETHEIGHT not implemented");
        })),
        Map.entry("GETPOSITIONX", MethodSpec.of((self, args, ctx) -> {
            throw new UnsupportedOperationException("VIRTUALGRAPHICSOBJECT.GETPOSITIONX not implemented");
        })),
        Map.entry("GETPOSITIONY", MethodSpec.of((self, args, ctx) -> {
            throw new UnsupportedOperationException("VIRTUALGRAPHICSOBJECT.GETPOSITIONY not implemented");
        })),
        Map.entry("GETWIDTH", MethodSpec.of((self, args, ctx) -> {
            throw new UnsupportedOperationException("VIRTUALGRAPHICSOBJECT.GETWIDTH not implemented");
        })),
        Map.entry("MOVE", MethodSpec.of((self, args, ctx) -> {
            throw new UnsupportedOperationException("VIRTUALGRAPHICSOBJECT.MOVE not implemented");
        })),
        Map.entry("SETMASK", MethodSpec.of((self, args, ctx) -> {
            throw new UnsupportedOperationException("VIRTUALGRAPHICSOBJECT.SETMASK not implemented");
        })),
        Map.entry("SETPOSITION", MethodSpec.of((self, args, ctx) -> {
            throw new UnsupportedOperationException("VIRTUALGRAPHICSOBJECT.SETPOSITION not implemented");
        })),
        Map.entry("SETPRIORITY", MethodSpec.of((self, args, ctx) -> {
            throw new UnsupportedOperationException("VIRTUALGRAPHICSOBJECT.SETPRIORITY not implemented");
        })),
        Map.entry("SETSOURCE", MethodSpec.of((self, args, ctx) -> {
            throw new UnsupportedOperationException("VIRTUALGRAPHICSOBJECT.SETSOURCE not implemented");
        }))
    );

    @Override
    public String toString() {
        return "VirtualGraphicsObjectVariable[" + name + "]";
    }
}
