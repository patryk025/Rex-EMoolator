package pl.genschu.bloomooemulator.interpreter.variable;

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
    List<Double> components,
    Map<String, SignalHandler> signals
) implements Variable {

    public VectorVariable {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Variable name cannot be null or empty");
        }
        if (components == null) {
            components = List.of();
        } else {
            components = List.copyOf(components);  // Immutable
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
        // Return length as the "value" of the vector
        return new DoubleValue(length());
    }

    @Override
    public VariableType type() {
        return VariableType.VECTOR;
    }

    @Override
    public Variable withValue(Value newValue) {
        // Setting value on vector sets all components to that value
        double val = ArgumentHelper.getDouble(newValue);
        List<Double> newComponents = new ArrayList<>();
        for (int i = 0; i < components.size(); i++) {
            newComponents.add(val);
        }
        return new VectorVariable(name, size, newComponents, signals);
    }

    public Variable withSize(int newSize) {
        List<Double> newComponents = new ArrayList<>(components);
        if (newSize > components.size()) {
            // Extend with zeros
            for (int i = components.size(); i < newSize; i++) {
                newComponents.add(0.0);
            }
        } else if (newSize < components.size()) {
            // Truncate
            newComponents = newComponents.subList(0, newSize);
        }
        return new VectorVariable(name, newSize, newComponents, signals);
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

    private VectorVariable withComponents(List<Double> newComponents) {
        return new VectorVariable(name, size, newComponents, signals);
    }

    /**
     * Calculates the length (magnitude) of this vector.
     */
    public double length() {
        double sum = 0;
        for (double component : components) {
            sum += component * component;
        }
        return Math.sqrt(sum);
    }

    /**
     * Gets component at index or 0.0 if out of bounds.
     */
    public double getComponent(int index) {
        if (index >= 0 && index < components.size()) {
            return components.get(index);
        }
        return 0.0;
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

            List<Double> newComponents = new ArrayList<>(thisVar.components);
            while (newComponents.size() < thisVar.size) {
                newComponents.add(0.0);
            }

            for (int i = 0; i < thisVar.size; i++) {
                double a = newComponents.get(i);
                double b = i < other.components.size() ? other.components.get(i) : 0.0;
                newComponents.set(i, a + b);
            }

            return MethodResult.sets(thisVar.withComponents(newComponents));
        }, ArgKind.VAR_REF)),

        Map.entry("ASSIGN", MethodSpec.of((self, args) -> {
            VectorVariable thisVar = (VectorVariable) self;
            List<Double> newComponents = new ArrayList<>(thisVar.components);
            int targetSize = Math.max(newComponents.size(), args.size());
            while (newComponents.size() < targetSize) {
                newComponents.add(0.0);
            }
            for (int i = 0; i < args.size(); i++) {
                newComponents.set(i, ArgumentHelper.getDouble(args.get(i)));
            }
            return MethodResult.sets(thisVar.withComponents(newComponents));
        })),

        Map.entry("GET", MethodSpec.of((self, args) -> {
            if (args.isEmpty()) {
                throw new IllegalArgumentException("GET requires 1 argument");
            }
            VectorVariable thisVar = (VectorVariable) self;
            int index = ArgumentHelper.getInt(args.get(0));
            if (index >= 0 && index < thisVar.components.size()) {
                return MethodResult.noChange(new DoubleValue(thisVar.components.get(index)));
            }
            return MethodResult.noChange(new DoubleValue(0.0));
        })),

        Map.entry("MUL", MethodSpec.of((self, args) -> {
            if (args.isEmpty()) {
                throw new IllegalArgumentException("MUL requires 1 argument");
            }
            VectorVariable thisVar = (VectorVariable) self;
            double scalar = ArgumentHelper.getDouble(args.get(0));
            List<Double> newComponents = new ArrayList<>(thisVar.components);
            while (newComponents.size() < thisVar.size) {
                newComponents.add(0.0);
            }
            for (int i = 0; i < thisVar.size; i++) {
                newComponents.set(i, newComponents.get(i) * scalar);
            }
            return MethodResult.sets(thisVar.withComponents(newComponents));
        })),

        Map.entry("NORMALIZE", MethodSpec.of((self, args) -> {
            VectorVariable thisVar = (VectorVariable) self;
            double length = thisVar.length();
            if (length <= 0.0) {
                return MethodResult.noChange(NullValue.INSTANCE);
            }
            List<Double> newComponents = new ArrayList<>(thisVar.components);
            while (newComponents.size() < thisVar.size) {
                newComponents.add(0.0);
            }
            for (int i = 0; i < thisVar.size; i++) {
                newComponents.set(i, newComponents.get(i) / length);
            }
            return MethodResult.sets(thisVar.withComponents(newComponents));
        })),

        Map.entry("LEN", MethodSpec.of((self, args) -> {
            VectorVariable thisVar = (VectorVariable) self;
            return MethodResult.noChange(new DoubleValue(thisVar.length()));
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
            List<Double> resultComponents = new ArrayList<>(resultVector.components);

            VectorVariable newResult = new VectorVariable(
                resultVector.name(),
                resultVector.size,
                resultComponents,
                resultVector.signals()
            );
            for (int i = 0; i < thisVar.size; i++) {
                double a = i < thisVar.components.size() ? thisVar.components.get(i) : 0.0;
                double b = i < normalVector.components.size() ? normalVector.components.get(i) : 0.0;
                if (i < resultComponents.size()) {
                    resultComponents.set(i, 2 * dotProduct * b - a);
                }
            }
            return MethodResult.effects(List.of(new UpdateVariableEffect(resultVector.name(), newResult)));
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

    /**
     * Gets X component (index 0) or 0.0 if not set.
     */
    public double x() {
        return getComponent(0);
    }

    /**
     * Gets Y component (index 1) or 0.0 if not set.
     */
    public double y() {
        return getComponent(1);
    }

    /**
     * Gets Z component (index 2) or 0.0 if not set.
     */
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
