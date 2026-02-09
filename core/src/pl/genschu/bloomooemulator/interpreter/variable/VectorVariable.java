package pl.genschu.bloomooemulator.interpreter.variable;

import pl.genschu.bloomooemulator.annotations.InternalMutable;
import pl.genschu.bloomooemulator.interpreter.helpers.ArgumentHelper;
import pl.genschu.bloomooemulator.interpreter.runtime.effects.UpdateVariableEffect;
import pl.genschu.bloomooemulator.interpreter.values.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * VectorVariable stores an N-dimensional vector of double values.
 * Commonly used for 2D (x, y) or 3D (x, y, z) coordinates.
 **/
public record VectorVariable(
    String name,
    int size,
    @InternalMutable
    List<Double> components,
    Map<String, SignalHandler> signals
) implements Variable {

    public VectorVariable {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Variable name cannot be null or empty");
        }
        if (components == null) {
            components = new ArrayList<>();
        } else if (!(components instanceof ArrayList)) {
            components = new ArrayList<>(components);  // Ensure mutable
        }
        if (signals == null) {
            signals = Map.of();
        } else {
            signals = Map.copyOf(signals);
        }
    }

    public VectorVariable(String name, int size) {
        this(name, size, createZeroComponents(size), Map.of());
    }

    public VectorVariable(String name, double... values) {
        this(name, 0, toList(values), Map.of());
    }

    public VectorVariable(String name, List<Double> components) {
        this(name, 0, components, Map.of());
    }

    private static List<Double> createZeroComponents(int size) {
        List<Double> list = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            list.add(0.0);
        }
        return list;
    }

    private static List<Double> toList(double... values) {
        List<Double> list = new ArrayList<>(values.length);
        for (double v : values) {
            list.add(v);
        }
        return list;
    }

    // ========================================
    // INTERFACE IMPLEMENTATION
    // ========================================

    @Override
    public Value value() {
        return new DoubleValue(length());
    }

    @Override
    public VariableType type() {
        return VariableType.VECTOR;
    }

    @Override
    public Variable withValue(Value newValue) {
        double val = ArgumentHelper.getDouble(newValue);
        for (int i = 0; i < components.size(); i++) {
            components.set(i, val);
        }
        return this;
    }

    public Variable withSize(int newSize) {
        if (newSize > components.size()) {
            for (int i = components.size(); i < newSize; i++) {
                components.add(0.0);
            }
        } else if (newSize < components.size()) {
            while (components.size() > newSize) {
                components.remove(components.size() - 1);
            }
        }
        return new VectorVariable(name, newSize, components, signals);
    }

    @Override
    public Map<String, MethodSpec> methods() {
        return METHOD;
    }

    @Override
    public Variable withSignal(String signalName, SignalHandler handler) {
        Map<String, SignalHandler> newSignals = new HashMap<>(signals);
        newSignals.put(signalName, handler);
        return new VectorVariable(name, size, components, newSignals);
    }

    // ========================================
    // HELPER METHODS
    // ========================================

    public double length() {
        double sum = 0;
        for (double component : components) {
            sum += component * component;
        }
        return Math.sqrt(sum);
    }

    public double getComponent(int index) {
        if (index >= 0 && index < components.size()) {
            return components.get(index);
        }
        return 0.0;
    }

    private void ensureSize() {
        while (components.size() < size) {
            components.add(0.0);
        }
    }

    // ========================================
    // METHODS DEFINITION
    // ========================================

    private static final Map<String, MethodSpec> METHOD = Map.ofEntries(
        Map.entry("ADD", MethodSpec.of((self, args) -> {
            if (args.isEmpty()) {
                throw new IllegalArgumentException("ADD requires 1 argument");
            }
            VectorVariable thisVar = (VectorVariable) self;
            VectorVariable other = requireVector(args.get(0), "ADD");

            thisVar.ensureSize();
            for (int i = 0; i < thisVar.size; i++) {
                double a = thisVar.components.get(i);
                double b = i < other.components.size() ? other.components.get(i) : 0.0;
                thisVar.components.set(i, a + b);
            }
            return MethodResult.noReturn();
        }, ArgKind.VAR_REF)),

        Map.entry("ASSIGN", MethodSpec.of((self, args) -> {
            VectorVariable thisVar = (VectorVariable) self;
            int targetSize = Math.max(thisVar.components.size(), args.size());
            while (thisVar.components.size() < targetSize) {
                thisVar.components.add(0.0);
            }
            for (int i = 0; i < args.size(); i++) {
                thisVar.components.set(i, ArgumentHelper.getDouble(args.get(i)));
            }
            return MethodResult.noReturn();
        })),

        Map.entry("GET", MethodSpec.of((self, args) -> {
            if (args.isEmpty()) {
                throw new IllegalArgumentException("GET requires 1 argument");
            }
            VectorVariable thisVar = (VectorVariable) self;
            int index = ArgumentHelper.getInt(args.get(0));
            if (index >= 0 && index < thisVar.components.size()) {
                return MethodResult.returns(new DoubleValue(thisVar.components.get(index)));
            }
            return MethodResult.returns(new DoubleValue(0.0));
        })),

        Map.entry("MUL", MethodSpec.of((self, args) -> {
            if (args.isEmpty()) {
                throw new IllegalArgumentException("MUL requires 1 argument");
            }
            VectorVariable thisVar = (VectorVariable) self;
            double scalar = ArgumentHelper.getDouble(args.get(0));
            thisVar.ensureSize();
            for (int i = 0; i < thisVar.size; i++) {
                thisVar.components.set(i, thisVar.components.get(i) * scalar);
            }
            return MethodResult.noReturn();
        })),

        Map.entry("NORMALIZE", MethodSpec.of((self, args) -> {
            VectorVariable thisVar = (VectorVariable) self;
            double len = thisVar.length();
            if (len <= 0.0) {
                return MethodResult.returns(NullValue.INSTANCE);
            }
            thisVar.ensureSize();
            for (int i = 0; i < thisVar.size; i++) {
                thisVar.components.set(i, thisVar.components.get(i) / len);
            }
            return MethodResult.noReturn();
        })),

        Map.entry("LEN", MethodSpec.of((self, args) -> {
            VectorVariable thisVar = (VectorVariable) self;
            return MethodResult.returns(new DoubleValue(thisVar.length()));
        })),

        Map.entry("REFLECT", MethodSpec.of((self, args) -> {
            if (args.size() < 2) {
                throw new IllegalArgumentException("REFLECT requires 2 arguments");
            }
            VectorVariable thisVar = (VectorVariable) self;
            VectorVariable normalVector = requireVector(args.get(0), "REFLECT");
            VectorVariable resultVector = requireVector(args.get(1), "REFLECT");

            double dotProduct = 0.0;
            for (int i = 0; i < thisVar.size; i++) {
                double a = i < thisVar.components.size() ? thisVar.components.get(i) : 0.0;
                double b = i < normalVector.components.size() ? normalVector.components.get(i) : 0.0;
                dotProduct += a * b;
            }
            for (int i = 0; i < thisVar.size; i++) {
                double a = i < thisVar.components.size() ? thisVar.components.get(i) : 0.0;
                double b = i < normalVector.components.size() ? normalVector.components.get(i) : 0.0;
                if (i < resultVector.components.size()) {
                    resultVector.components.set(i, 2 * dotProduct * b - a);
                }
            }

            return MethodResult.effects(List.of(new UpdateVariableEffect(resultVector.name(), resultVector)));
        }, ArgKind.VAR_REF, ArgKind.VAR_REF))
    );

    private static VectorVariable requireVector(Value value, String methodName) {
        if (value instanceof VariableValue(Variable variable) && variable instanceof VectorVariable v) {
            return v;
        }
        throw new IllegalArgumentException(methodName + " requires VECTOR argument");
    }

    // ========================================
    // CONVENIENT ACCESSORS
    // ========================================

    public double x() {
        return getComponent(0);
    }

    public double y() {
        return getComponent(1);
    }

    public double z() {
        return getComponent(2);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("VectorVariable[" + name + "=(");
        for (int i = 0; i < components.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(components.get(i));
        }
        sb.append(")]");
        return sb.toString();
    }
}
