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
    @InternalMutable
    List<Value> elements,
    Map<String, SignalHandler> signals
) implements Variable {

    public ArrayVariable {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Variable name cannot be null or empty");
        }
        if (elements == null) {
            elements = new ArrayList<>();
        } else if (!(elements instanceof ArrayList)) {
            elements = new ArrayList<>(elements);  // Ensure mutable
        }
        if (signals == null) {
            signals = Map.of();
        } else {
            signals = Map.copyOf(signals);
        }
    }

    public ArrayVariable(String name) {
        this(name, new ArrayList<>(), Map.of());
    }

    public ArrayVariable(String name, List<Value> elements) {
        this(name, elements, Map.of());
    }

    // ========================================
    // INTERFACE IMPLEMENTATION
    // ========================================

    @Override
    public Value value() {
        return new IntValue(elements.size());
    }

    @Override
    public VariableType type() {
        return VariableType.ARRAY;
    }

    @Override
    public Variable withValue(Value newValue) {
        elements.clear();
        elements.add(newValue);
        return this;
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
    // METHODS DEFINITION
    // ========================================

    private static final Map<String, MethodSpec> METHODS_SPECS = Map.ofEntries(
        Map.entry("ADD", MethodSpec.of((self, args) -> {
            ArrayVariable thisVar = (ArrayVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("ADD requires at least 1 argument");
            }
            thisVar.elements.addAll(args);
            return MethodResult.noReturn();
        })),

        Map.entry("ADDAT", MethodSpec.of((self, args) -> {
            ArrayVariable thisVar = (ArrayVariable) self;
            if (args.size() < 2) {
                throw new IllegalArgumentException("ADDAT requires 2 arguments");
            }
            int index = ArgumentHelper.getInt(args.get(0));
            if (index < 0 || index >= thisVar.elements.size()) {
                return MethodResult.returns(NullValue.INSTANCE);
            }
            double addend = ArgumentHelper.getDouble(args.get(1));
            double current = ArgumentHelper.getDouble(thisVar.elements.get(index));
            thisVar.elements.set(index, new DoubleValue(current + addend));
            return MethodResult.noReturn();
        })),

        Map.entry("CHANGEAT", MethodSpec.of((self, args) -> {
            ArrayVariable thisVar = (ArrayVariable) self;
            if (args.size() < 2) {
                throw new IllegalArgumentException("CHANGEAT requires 2 arguments");
            }
            int index = ArgumentHelper.getInt(args.get(0));
            Value newValue = args.get(1);
            if (index < 0 || index >= thisVar.elements.size()) {
                return MethodResult.returns(NullValue.INSTANCE);
            }
            thisVar.elements.set(index, newValue);
            return MethodResult.noReturn();
        })),

        Map.entry("CLAMPAT", MethodSpec.of((self, args) -> {
            ArrayVariable thisVar = (ArrayVariable) self;
            if (args.size() < 3) {
                throw new IllegalArgumentException("CLAMPAT requires 3 arguments");
            }
            int index = ArgumentHelper.getInt(args.get(0));
            if (index < 0 || index >= thisVar.elements.size()) {
                return MethodResult.returns(NullValue.INSTANCE);
            }
            Value current = thisVar.elements.get(index);
            switch (current) {
                case IntValue iv -> {
                    int rangeMin = ArgumentHelper.getInt(args.get(1));
                    int rangeMax = ArgumentHelper.getInt(args.get(2));
                    int clamped = Math.max(rangeMin, Math.min(rangeMax, iv.value()));
                    thisVar.elements.set(index, new IntValue(clamped));
                    return MethodResult.noReturn();
                }
                case DoubleValue dv -> {
                    double rangeMin = ArgumentHelper.getDouble(args.get(1));
                    double rangeMax = ArgumentHelper.getDouble(args.get(2));
                    double clamped = Math.max(rangeMin, Math.min(rangeMax, dv.value()));
                    thisVar.elements.set(index, new DoubleValue(clamped));
                    return MethodResult.noReturn();
                }
                default -> {
                    return MethodResult.returns(NullValue.INSTANCE);
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
                    return MethodResult.returns(BoolValue.TRUE);
                }
            }
            return MethodResult.returns(BoolValue.FALSE);
        })),

        Map.entry("FIND", MethodSpec.of((self, args) -> {
            ArrayVariable thisVar = (ArrayVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("FIND requires 1 argument");
            }
            Value needle = args.get(0);
            for (int i = 0; i < thisVar.elements.size(); i++) {
                if (ValueOps.compare(thisVar.elements.get(i), needle, ComparisonNode.ComparisonOp.EQUAL).value()) {
                    return MethodResult.returns(new IntValue(i));
                }
            }
            return MethodResult.returns(new IntValue(-1));
        })),

        Map.entry("GET", MethodSpec.of((self, args) -> {
            ArrayVariable thisVar = (ArrayVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("GET requires at least 1 argument");
            }
            int index = ArgumentHelper.getInt(args.get(0));
            if (index < 0 || index >= thisVar.elements.size()) {
                return MethodResult.returns(NullValue.INSTANCE);
            }
            return MethodResult.returns(thisVar.elements.get(index));
        })),

        Map.entry("GETSIZE", MethodSpec.of((self, args) -> {
            ArrayVariable thisVar = (ArrayVariable) self;
            return MethodResult.returns(new IntValue(thisVar.elements.size()));
        })),

        Map.entry("GETSUMVALUE", MethodSpec.of((self, args) -> {
            ArrayVariable thisVar = (ArrayVariable) self;
            double sum = 0.0;
            for (Value element : thisVar.elements) {
                sum += ArgumentHelper.getDouble(element);
            }
            return MethodResult.returns(new DoubleValue(sum));
        })),

        Map.entry("INSERTAT", MethodSpec.of((self, args) -> {
            ArrayVariable thisVar = (ArrayVariable) self;
            if (args.size() < 2) {
                throw new IllegalArgumentException("INSERTAT requires 2 arguments");
            }
            int index = ArgumentHelper.getInt(args.get(0));
            Value value = args.get(1);
            if (index < 0 || index > thisVar.elements.size()) {
                return MethodResult.returns(NullValue.INSTANCE);
            }
            thisVar.elements.add(index, value);
            return MethodResult.noReturn();
        })),

        Map.entry("MODAT", MethodSpec.of((self, args) -> {
            ArrayVariable thisVar = (ArrayVariable) self;
            if (args.size() < 2) {
                throw new IllegalArgumentException("MODAT requires 2 arguments");
            }
            int index = ArgumentHelper.getInt(args.get(0));
            if (index < 0 || index >= thisVar.elements.size()) {
                return MethodResult.returns(NullValue.INSTANCE);
            }
            double divisor = ArgumentHelper.getDouble(args.get(1));
            if (divisor == 0.0) {
                return MethodResult.returns(NullValue.INSTANCE);
            }
            double current = ArgumentHelper.getDouble(thisVar.elements.get(index));
            thisVar.elements.set(index, new DoubleValue(current % divisor));
            return MethodResult.noReturn();
        })),

        Map.entry("MULAT", MethodSpec.of((self, args) -> {
            ArrayVariable thisVar = (ArrayVariable) self;
            if (args.size() < 2) {
                throw new IllegalArgumentException("MULAT requires 2 arguments");
            }
            int index = ArgumentHelper.getInt(args.get(0));
            if (index < 0 || index >= thisVar.elements.size()) {
                return MethodResult.returns(NullValue.INSTANCE);
            }
            double multiplier = ArgumentHelper.getDouble(args.get(1));
            double current = ArgumentHelper.getDouble(thisVar.elements.get(index));
            thisVar.elements.set(index, new DoubleValue(current * multiplier));
            return MethodResult.noReturn();
        })),

        Map.entry("REMOVEALL", MethodSpec.of((self, args) -> {
            ArrayVariable thisVar = (ArrayVariable) self;
            thisVar.elements.clear();
            return MethodResult.noReturn();
        })),

        Map.entry("REMOVEAT", MethodSpec.of((self, args) -> {
            ArrayVariable thisVar = (ArrayVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("REMOVEAT requires 1 argument");
            }
            int index = ArgumentHelper.getInt(args.get(0));
            if (index < 0 || index >= thisVar.elements.size()) {
                return MethodResult.returns(NullValue.INSTANCE);
            }
            thisVar.elements.remove(index);
            return MethodResult.noReturn();
        })),

        Map.entry("REVERSEFIND", MethodSpec.of((self, args) -> {
            ArrayVariable thisVar = (ArrayVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("REVERSEFIND requires 1 argument");
            }
            Value needle = args.get(0);
            for (int i = thisVar.elements.size() - 1; i >= 0; i--) {
                if (ValueOps.compare(thisVar.elements.get(i), needle, ComparisonNode.ComparisonOp.EQUAL).value()) {
                    return MethodResult.returns(new IntValue(i));
                }
            }
            return MethodResult.returns(new IntValue(-1));
        })),

        Map.entry("SUB", MethodSpec.of((self, args) -> {
            ArrayVariable thisVar = (ArrayVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("SUB requires 1 argument");
            }
            double subtrahend = ArgumentHelper.getDouble(args.get(0));
            for (int i = 0; i < thisVar.elements.size(); i++) {
                double result = ArgumentHelper.getDouble(thisVar.elements.get(i)) - subtrahend;
                thisVar.elements.set(i, new DoubleValue(result));
            }
            return MethodResult.noReturn();
        })),

        Map.entry("SUBAT", MethodSpec.of((self, args) -> {
            ArrayVariable thisVar = (ArrayVariable) self;
            if (args.size() < 2) {
                throw new IllegalArgumentException("SUBAT requires 2 arguments");
            }
            int index = ArgumentHelper.getInt(args.get(0));
            if (index < 0 || index >= thisVar.elements.size()) {
                return MethodResult.returns(NullValue.INSTANCE);
            }
            double subtrahend = ArgumentHelper.getDouble(args.get(1));
            double current = ArgumentHelper.getDouble(thisVar.elements.get(index));
            thisVar.elements.set(index, new DoubleValue(current - subtrahend));
            return MethodResult.noReturn();
        })),

        Map.entry("SUM", MethodSpec.of((self, args) -> {
            ArrayVariable thisVar = (ArrayVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("SUM requires 1 argument");
            }
            double addend = ArgumentHelper.getDouble(args.get(0));
            for (int i = 0; i < thisVar.elements.size(); i++) {
                double result = ArgumentHelper.getDouble(thisVar.elements.get(i)) + addend;
                thisVar.elements.set(i, new DoubleValue(result));
            }
            return MethodResult.noReturn();
        }))
    );

    // ========================================
    // CONVENIENT ACCESSORS
    // ========================================

    public int size() {
        return elements.size();
    }

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
