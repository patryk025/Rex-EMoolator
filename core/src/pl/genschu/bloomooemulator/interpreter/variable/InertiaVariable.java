package pl.genschu.bloomooemulator.interpreter.variable;

import pl.genschu.bloomooemulator.interpreter.values.*;

import java.util.*;

/**
 * InertiaVariable represents a 2D physics simulation engine.
 * Manages rigid bodies with forces, gravity, velocity, damping.
 *
 * All methods are stubs — not yet implemented in the original engine.
 */
public record InertiaVariable(
    String name,
    Map<String, SignalHandler> signals
) implements Variable {

    public InertiaVariable {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Variable name cannot be null or empty");
        }
        if (signals == null) {
            signals = Map.of();
        } else {
            signals = Map.copyOf(signals);
        }
    }

    public InertiaVariable(String name) {
        this(name, Map.of());
    }

    @Override
    public Value value() {
        return NullValue.INSTANCE;
    }

    @Override
    public VariableType type() {
        return VariableType.INERTIA;
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
        return new InertiaVariable(name, newSignals);
    }

    private static final Map<String, MethodSpec> METHODS = Map.ofEntries(
        Map.entry("ADDFORCE", MethodSpec.of((self, args, ctx) -> {
            throw new UnsupportedOperationException("INERTIA.ADDFORCE not implemented");
        })),
        Map.entry("CREATESPHERE", MethodSpec.of((self, args, ctx) -> {
            throw new UnsupportedOperationException("INERTIA.CREATESPHERE not implemented");
        })),
        Map.entry("DELETEBODY", MethodSpec.of((self, args, ctx) -> {
            throw new UnsupportedOperationException("INERTIA.DELETEBODY not implemented");
        })),
        Map.entry("GETPOSITIONX", MethodSpec.of((self, args, ctx) -> {
            throw new UnsupportedOperationException("INERTIA.GETPOSITIONX not implemented");
        })),
        Map.entry("GETPOSITIONY", MethodSpec.of((self, args, ctx) -> {
            throw new UnsupportedOperationException("INERTIA.GETPOSITIONY not implemented");
        })),
        Map.entry("GETSPEED", MethodSpec.of((self, args, ctx) -> {
            throw new UnsupportedOperationException("INERTIA.GETSPEED not implemented");
        })),
        Map.entry("LINK", MethodSpec.of((self, args, ctx) -> {
            throw new UnsupportedOperationException("INERTIA.LINK not implemented");
        })),
        Map.entry("LOAD", MethodSpec.of((self, args, ctx) -> {
            throw new UnsupportedOperationException("INERTIA.LOAD not implemented");
        })),
        Map.entry("RESETTIMER", MethodSpec.of((self, args, ctx) -> {
            throw new UnsupportedOperationException("INERTIA.RESETTIMER not implemented");
        })),
        Map.entry("SETGRAVITY", MethodSpec.of((self, args, ctx) -> {
            throw new UnsupportedOperationException("INERTIA.SETGRAVITY not implemented");
        })),
        Map.entry("SETLINEARDAMPING", MethodSpec.of((self, args, ctx) -> {
            throw new UnsupportedOperationException("INERTIA.SETLINEARDAMPING not implemented");
        })),
        Map.entry("SETMATERIAL", MethodSpec.of((self, args, ctx) -> {
            throw new UnsupportedOperationException("INERTIA.SETMATERIAL not implemented");
        })),
        Map.entry("SETPOSITION", MethodSpec.of((self, args, ctx) -> {
            throw new UnsupportedOperationException("INERTIA.SETPOSITION not implemented");
        })),
        Map.entry("SETVELOCITY", MethodSpec.of((self, args, ctx) -> {
            throw new UnsupportedOperationException("INERTIA.SETVELOCITY not implemented");
        })),
        Map.entry("TICK", MethodSpec.of((self, args, ctx) -> {
            throw new UnsupportedOperationException("INERTIA.TICK not implemented");
        })),
        Map.entry("UNLINK", MethodSpec.of((self, args, ctx) -> {
            throw new UnsupportedOperationException("INERTIA.UNLINK not implemented");
        }))
    );

    @Override
    public String toString() {
        return "InertiaVariable[" + name + "]";
    }
}
