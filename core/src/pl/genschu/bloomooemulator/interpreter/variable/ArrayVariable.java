package pl.genschu.bloomooemulator.interpreter.variable;

import pl.genschu.bloomooemulator.annotations.InternalMutable;
import pl.genschu.bloomooemulator.interpreter.ast.ComparisonNode;
import pl.genschu.bloomooemulator.interpreter.helpers.ArgumentHelper;
import pl.genschu.bloomooemulator.interpreter.ops.ValueOps;
import pl.genschu.bloomooemulator.interpreter.values.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ArrayVariable stores a list of Values.
 **/
public record ArrayVariable(
    String name,
    //@InternalMutable (testing performance impact of copy-on-write)
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
            elements = List.copyOf(elements);  // Immutable
        }
        if (signals == null) {
            signals = Map.of();
        } else {
            signals = Map.copyOf(signals);
        }
    }

    public ArrayVariable(String name) {
        this(name, List.of(), Map.of());
    }

    public ArrayVariable(String name, List<Value> elements) {
        this(name, elements, Map.of());
    }

    // ========================================
    // INTERFACE IMPLEMENTATION
    // ========================================

    @Override
    public Value value() {
        // Return size as the "value" of the array (useful for conditions)
        return new IntValue(elements.size());
    }

    @Override
    public VariableType type() {
        return VariableType.ARRAY;
    }

    @Override
    public Variable withValue(Value newValue) {
        // Setting value on array clears it and adds the single value
        return new ArrayVariable(name, List.of(newValue), signals);
    }

    @Override
    public Map<String, MethodSpec> methods() {
        return METHODS_SPECS;
    }

    @Override
    public Variable withSignal(String signalName, SignalHandler handler) {
        Map<String, SignalHandler> newSignals = new HashMap<>(signals);
        newSignals.put(signalName, handler);
        return new ArrayVariable(name, elements, newSignals);
    }

    // ========================================
    // HELPER METHODS
    // ========================================

    private ArrayVariable withElements(List<Value> newElements) {
        return new ArrayVariable(name, newElements, signals);
    }

    // ========================================
    // METHODS DEFINITION
    // ========================================

    private static final Map<String, MethodSpec> METHODS_SPECS = Map.ofEntries(
        Map.entry("ADD", MethodSpec.of((self, args) -> {
            ArrayVariable thisVar = (ArrayVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("ADD requires at least 1 argument");
            }

            List<Value> newElements = new ArrayList<>(thisVar.elements);
            newElements.addAll(args);
            return MethodResult.sets(thisVar.withElements(newElements));
        })),

        Map.entry("ADDAT", MethodSpec.of((self, args) -> {
            ArrayVariable thisVar = (ArrayVariable) self;
            if (args.size() < 2) {
                throw new IllegalArgumentException("ADDAT requires 2 arguments");
            }

            int index = ArgumentHelper.getInt(args.get(0));
            if (index < 0 || index >= thisVar.elements.size()) {
                return MethodResult.noChange(NullValue.INSTANCE);
            }

            double addend = ArgumentHelper.getDouble(args.get(1));
            double current = ArgumentHelper.getDouble(thisVar.elements.get(index));

            List<Value> newElements = new ArrayList<>(thisVar.elements);
            newElements.set(index, new DoubleValue(current + addend));
            return MethodResult.sets(thisVar.withElements(newElements));
        })),

        Map.entry("CHANGEAT", MethodSpec.of((self, args) -> {
            ArrayVariable thisVar = (ArrayVariable) self;
            if (args.size() < 2) {
                throw new IllegalArgumentException("CHANGEAT requires 2 arguments");
            }

            int index = ArgumentHelper.getInt(args.get(0));
            Value newValue = args.get(1);

            if (index < 0 || index >= thisVar.elements.size()) {
                return MethodResult.noChange(NullValue.INSTANCE);
            }

            List<Value> newElements = new ArrayList<>(thisVar.elements);
            newElements.set(index, newValue);
            return MethodResult.sets(thisVar.withElements(newElements));
        })),

        Map.entry("CLAMPAT", MethodSpec.of((self, args) -> {
            ArrayVariable thisVar = (ArrayVariable) self;
            if (args.size() < 3) {
                throw new IllegalArgumentException("CLAMPAT requires 3 arguments");
            }

            int index = ArgumentHelper.getInt(args.get(0));
            if (index < 0 || index >= thisVar.elements.size()) {
                return MethodResult.noChange(NullValue.INSTANCE);
            }

            Value current = thisVar.elements.get(index);

            switch(current) {
                case IntValue iv -> {
                    int rangeMin = ArgumentHelper.getInt(args.get(1));
                    int rangeMax = ArgumentHelper.getInt(args.get(2));
                    int clamped = Math.max(rangeMin, Math.min(rangeMax, iv.value()));

                    List<Value> newElements = new ArrayList<>(thisVar.elements);
                    newElements.set(index, new IntValue(clamped));
                    return MethodResult.sets(thisVar.withElements(newElements));
                }
                case DoubleValue dv -> {
                    double rangeMin = ArgumentHelper.getDouble(args.get(1));
                    double rangeMax = ArgumentHelper.getDouble(args.get(2));
                    double clamped = Math.max(rangeMin, Math.min(rangeMax, dv.value()));

                    List<Value> newElements = new ArrayList<>(thisVar.elements);
                    newElements.set(index, new DoubleValue(clamped));
                    return MethodResult.sets(thisVar.withElements(newElements));
                }
                default -> {
                    /* no adjustment possible */
                    return MethodResult.noChange(NullValue.INSTANCE);
                }
            }
        })),

        Map.entry("CONTAINS", MethodSpec.of((self, args) -> {
            ArrayVariable thisVar = (ArrayVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("CONTAINS requires 1 argument");
            }

            String needle = ArgumentHelper.getString(args.get(0));
            for (Value element : thisVar.elements) {
                if (element.toDisplayString().equals(needle)) {
                    return MethodResult.noChange(BoolValue.TRUE);
                }
            }
            return MethodResult.noChange(BoolValue.FALSE);
        })),

        // TODO: implement COPYTO (needs context?)

        Map.entry("FIND", MethodSpec.of((self, args) -> {
            ArrayVariable thisVar = (ArrayVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("FIND requires 1 argument");
            }

            Value needle = args.get(0);
            for (int i = 0; i < thisVar.elements.size(); i++) {
                if(ValueOps.compare(thisVar.elements().get(i), needle, ComparisonNode.ComparisonOp.EQUAL).value()) {
                    return MethodResult.noChange(new IntValue(i));
                }
            }
            return MethodResult.noChange(new IntValue(-1));
        })),

        Map.entry("GET", MethodSpec.of((self, args) -> {
            ArrayVariable thisVar = (ArrayVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("GET requires at least 1 argument");
            }

            int index = ArgumentHelper.getInt(args.get(0));
            if (index < 0 || index >= thisVar.elements.size()) {
                return MethodResult.noChange(NullValue.INSTANCE);
            }
            return MethodResult.noChange(thisVar.elements.get(index));
        })),

        Map.entry("GETSIZE", MethodSpec.of((self, args) -> {
            ArrayVariable thisVar = (ArrayVariable) self;
            return MethodResult.noChange(new IntValue(thisVar.elements.size()));
        })),

        Map.entry("GETSUMVALUE", MethodSpec.of((self, args) -> {
            ArrayVariable thisVar = (ArrayVariable) self;
            double sum = 0.0;
            for (Value element : thisVar.elements) {
                sum += ArgumentHelper.getDouble(element);
            }
            return MethodResult.noChange(new DoubleValue(sum));
        })),

        Map.entry("INSERTAT", MethodSpec.of((self, args) -> {
            ArrayVariable thisVar = (ArrayVariable) self;
            if (args.size() < 2) {
                throw new IllegalArgumentException("INSERTAT requires 2 arguments");
            }

            int index = ArgumentHelper.getInt(args.get(0));
            Value value = args.get(1);

            if (index < 0 || index > thisVar.elements.size()) {
                return MethodResult.noChange(NullValue.INSTANCE);
            }

            List<Value> newElements = new ArrayList<>(thisVar.elements);
            newElements.add(index, value);
            return MethodResult.sets(thisVar.withElements(newElements));
        })),

        // TODO: implement LOAD and LOADINI

        Map.entry("MODAT", MethodSpec.of((self, args) -> {
            ArrayVariable thisVar = (ArrayVariable) self;
            if (args.size() < 2) {
                throw new IllegalArgumentException("MODAT requires 2 arguments");
            }

            int index = ArgumentHelper.getInt(args.get(0));
            if (index < 0 || index >= thisVar.elements.size()) {
                return MethodResult.noChange(NullValue.INSTANCE);
            }

            double divisor = ArgumentHelper.getDouble(args.get(1));
            if (divisor == 0.0) {
                return MethodResult.noChange(NullValue.INSTANCE);
            }

            double current = ArgumentHelper.getDouble(thisVar.elements.get(index));

            List<Value> newElements = new ArrayList<>(thisVar.elements);
            newElements.set(index, new DoubleValue(current % divisor));
            return MethodResult.sets(thisVar.withElements(newElements));
        })),

        Map.entry("MULAT", MethodSpec.of((self, args) -> {
            ArrayVariable thisVar = (ArrayVariable) self;
            if (args.size() < 2) {
                throw new IllegalArgumentException("MULAT requires 2 arguments");
            }

            int index = ArgumentHelper.getInt(args.get(0));
            if (index < 0 || index >= thisVar.elements.size()) {
                return MethodResult.noChange(NullValue.INSTANCE);
            }

            double multiplier = ArgumentHelper.getDouble(args.get(1));
            double current = ArgumentHelper.getDouble(thisVar.elements.get(index));

            List<Value> newElements = new ArrayList<>(thisVar.elements);
            newElements.set(index, new DoubleValue(current * multiplier));
            return MethodResult.sets(thisVar.withElements(newElements));
        })),

        Map.entry("REMOVEALL", MethodSpec.of((self, args) -> {
            ArrayVariable thisVar = (ArrayVariable) self;
            return MethodResult.sets(thisVar.withElements(List.of()));
        })),

        Map.entry("REMOVEAT", MethodSpec.of((self, args) -> {
            ArrayVariable thisVar = (ArrayVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("REMOVEAT requires 1 argument");
            }

            int index = ArgumentHelper.getInt(args.get(0));
            if (index < 0 || index >= thisVar.elements.size()) {
                return MethodResult.noChange(NullValue.INSTANCE);
            }

            List<Value> newElements = new ArrayList<>(thisVar.elements);
            newElements.remove(index);
            return MethodResult.sets(thisVar.withElements(newElements));
        })),

        Map.entry("REVERSEFIND", MethodSpec.of((self, args) -> {
            ArrayVariable thisVar = (ArrayVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("REVERSEFIND requires 1 argument");
            }

            Value needle = args.get(0);
            for (int i = thisVar.elements.size() - 1; i >= 0; i--) {
                if(ValueOps.compare(thisVar.elements().get(i), needle, ComparisonNode.ComparisonOp.EQUAL).value()) {
                    return MethodResult.noChange(new IntValue(i));
                }
            }
            return MethodResult.noChange(new IntValue(-1));
        })),

        // TODO: implement SAVE and SAVEINI

        Map.entry("SUB", MethodSpec.of((self, args) -> {
            ArrayVariable thisVar = (ArrayVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("SUB requires 1 argument");
            }

            double subtrahend = ArgumentHelper.getDouble(args.get(0));
            List<Value> newElements = new ArrayList<>();
            for (Value element : thisVar.elements) {
                double result = ArgumentHelper.getDouble(element) - subtrahend;
                newElements.add(new DoubleValue(result));
            }
            return MethodResult.sets(thisVar.withElements(newElements));
        })),

        Map.entry("SUBAT", MethodSpec.of((self, args) -> {
            ArrayVariable thisVar = (ArrayVariable) self;
            if (args.size() < 2) {
                throw new IllegalArgumentException("SUBAT requires 2 arguments");
            }

            int index = ArgumentHelper.getInt(args.get(0));
            if (index < 0 || index >= thisVar.elements.size()) {
                return MethodResult.noChange(NullValue.INSTANCE);
            }

            double subtrahend = ArgumentHelper.getDouble(args.get(1));
            double current = ArgumentHelper.getDouble(thisVar.elements.get(index));

            List<Value> newElements = new ArrayList<>(thisVar.elements);
            newElements.set(index, new DoubleValue(current - subtrahend));
            return MethodResult.sets(thisVar.withElements(newElements));
        })),

        Map.entry("SUM", MethodSpec.of((self, args) -> {
            ArrayVariable thisVar = (ArrayVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("SUM requires 1 argument");
            }

            double addend = ArgumentHelper.getDouble(args.get(0));
            List<Value> newElements = new ArrayList<>();
            for (Value element : thisVar.elements) {
                double result = ArgumentHelper.getDouble(element) + addend;
                newElements.add(new DoubleValue(result));
            }
            return MethodResult.sets(thisVar.withElements(newElements));
        }))
    );

    // ========================================
    // CONVENIENT ACCESSORS
    // ========================================

    /**
     * Gets the size of the array.
     */
    public int size() {
        return elements.size();
    }

    /**
     * Gets element at index or null if out of bounds.
     */
    public Value get(int index) {
        if (index < 0 || index >= elements.size()) {
            return null;
        }
        return elements.get(index);
    }

    @Override
    public String toString() {
        return "ArrayVariable[" + name + ", size=" + elements.size() + "]";
    }

}
