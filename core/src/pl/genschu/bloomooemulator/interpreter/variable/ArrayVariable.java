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
        if (handler == null) {
            newSignals.remove(signalName);
        } else {
            newSignals.put(signalName, handler);
        }
        return new ArrayVariable(name, elements, newSignals);
    }

    @Override
    public Variable copyAs(String newName) {
        return new ArrayVariable(newName, new ArrayList<>(elements), signals);
    }

    // ========================================
    // METHODS DEFINITION
    // ========================================

    private static final Map<String, MethodSpec> METHODS_SPECS = Map.ofEntries(
        Map.entry("ADD", MethodSpec.of((self, args, ctx) -> {
            ArrayVariable thisVar = (ArrayVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("ADD requires at least 1 argument");
            }
            thisVar.elements.addAll(args);
            return MethodResult.noReturn();
        })),

        Map.entry("ADDAT", MethodSpec.of((self, args, ctx) -> {
            ArrayVariable thisVar = (ArrayVariable) self;
            if (args.size() < 2) {
                throw new IllegalArgumentException("ADDAT requires 2 arguments");
            }
            int index = ArgumentHelper.getInt(args.get(0));
            if (index < 0 || index >= thisVar.elements.size()) {
                return MethodResult.noReturn();
            }
            double addend = ArgumentHelper.getDouble(args.get(1));
            double current = ArgumentHelper.getDouble(thisVar.elements.get(index));
            thisVar.elements.set(index, new DoubleValue(current + addend));
            return MethodResult.noReturn();
        })),

        Map.entry("CHANGEAT", MethodSpec.of((self, args, ctx) -> {
            ArrayVariable thisVar = (ArrayVariable) self;
            if (args.size() < 2) {
                throw new IllegalArgumentException("CHANGEAT requires 2 arguments");
            }
            int index = ArgumentHelper.getInt(args.get(0));
            Value newValue = args.get(1);
            if (index < 0 || index >= thisVar.elements.size()) {
                return MethodResult.noReturn();
            }
            thisVar.elements.set(index, newValue);
            return MethodResult.noReturn();
        })),

        Map.entry("CLAMPAT", MethodSpec.of((self, args, ctx) -> {
            ArrayVariable thisVar = (ArrayVariable) self;
            if (args.size() < 3) {
                throw new IllegalArgumentException("CLAMPAT requires 3 arguments");
            }
            int index = ArgumentHelper.getInt(args.get(0));
            if (index < 0 || index >= thisVar.elements.size()) {
                return MethodResult.noReturn();
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
                    return MethodResult.noReturn();
                }
            }
        })),

        Map.entry("CONTAINS", MethodSpec.of((self, args, ctx) -> {
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

        Map.entry("FIND", MethodSpec.of((self, args, ctx) -> {
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

        Map.entry("GET", MethodSpec.of((self, args, ctx) -> {
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

        Map.entry("GETSIZE", MethodSpec.of((self, args, ctx) -> {
            ArrayVariable thisVar = (ArrayVariable) self;
            return MethodResult.returns(new IntValue(thisVar.elements.size()));
        })),

        Map.entry("GETSUMVALUE", MethodSpec.of((self, args, ctx) -> {
            ArrayVariable thisVar = (ArrayVariable) self;
            double sum = 0.0;
            for (Value element : thisVar.elements) {
                sum += ArgumentHelper.getDouble(element);
            }
            return MethodResult.returns(new DoubleValue(sum));
        })),

        Map.entry("INSERTAT", MethodSpec.of((self, args, ctx) -> {
            ArrayVariable thisVar = (ArrayVariable) self;
            if (args.size() < 2) {
                throw new IllegalArgumentException("INSERTAT requires 2 arguments");
            }
            int index = ArgumentHelper.getInt(args.get(0));
            Value value = args.get(1);
            if (index < 0 || index > thisVar.elements.size()) {
                return MethodResult.noReturn();
            }
            thisVar.elements.add(index, value);
            return MethodResult.noReturn();
        })),

        Map.entry("MODAT", MethodSpec.of((self, args, ctx) -> {
            ArrayVariable thisVar = (ArrayVariable) self;
            if (args.size() < 2) {
                throw new IllegalArgumentException("MODAT requires 2 arguments");
            }
            int index = ArgumentHelper.getInt(args.get(0));
            if (index < 0 || index >= thisVar.elements.size()) {
                return MethodResult.noReturn();
            }
            double divisor = ArgumentHelper.getDouble(args.get(1));
            if (divisor == 0.0) {
                return MethodResult.noReturn();
            }
            double current = ArgumentHelper.getDouble(thisVar.elements.get(index));
            thisVar.elements.set(index, new DoubleValue(current % divisor));
            return MethodResult.noReturn();
        })),

        Map.entry("MULAT", MethodSpec.of((self, args, ctx) -> {
            ArrayVariable thisVar = (ArrayVariable) self;
            if (args.size() < 2) {
                throw new IllegalArgumentException("MULAT requires 2 arguments");
            }
            int index = ArgumentHelper.getInt(args.get(0));
            if (index < 0 || index >= thisVar.elements.size()) {
                return MethodResult.noReturn();
            }
            double multiplier = ArgumentHelper.getDouble(args.get(1));
            double current = ArgumentHelper.getDouble(thisVar.elements.get(index));
            thisVar.elements.set(index, new DoubleValue(current * multiplier));
            return MethodResult.noReturn();
        })),

        Map.entry("REMOVEALL", MethodSpec.of((self, args, ctx) -> {
            ArrayVariable thisVar = (ArrayVariable) self;
            thisVar.elements.clear();
            return MethodResult.noReturn();
        })),

        Map.entry("REMOVEAT", MethodSpec.of((self, args, ctx) -> {
            ArrayVariable thisVar = (ArrayVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("REMOVEAT requires 1 argument");
            }
            int index = ArgumentHelper.getInt(args.get(0));
            if (index < 0 || index >= thisVar.elements.size()) {
                return MethodResult.noReturn();
            }
            thisVar.elements.remove(index);
            return MethodResult.noReturn();
        })),

        Map.entry("REVERSEFIND", MethodSpec.of((self, args, ctx) -> {
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

        Map.entry("SUB", MethodSpec.of((self, args, ctx) -> {
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

        Map.entry("SUBAT", MethodSpec.of((self, args, ctx) -> {
            ArrayVariable thisVar = (ArrayVariable) self;
            if (args.size() < 2) {
                throw new IllegalArgumentException("SUBAT requires 2 arguments");
            }
            int index = ArgumentHelper.getInt(args.get(0));
            if (index < 0 || index >= thisVar.elements.size()) {
                return MethodResult.noReturn();
            }
            double subtrahend = ArgumentHelper.getDouble(args.get(1));
            double current = ArgumentHelper.getDouble(thisVar.elements.get(index));
            thisVar.elements.set(index, new DoubleValue(current - subtrahend));
            return MethodResult.noReturn();
        })),

        Map.entry("COPYTO", MethodSpec.of((self, args, ctx) -> {
            ArrayVariable thisVar = (ArrayVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("COPYTO requires 1 argument: arrayVarName");
            }
            String targetName = ArgumentHelper.getString(args.get(0));
            Variable targetVar = ctx.getVariable(targetName);
            if (targetVar instanceof ArrayVariable targetArray) {
                targetArray.elements().addAll(thisVar.elements);
            }
            return MethodResult.noReturn();
        })),

        Map.entry("LOAD", MethodSpec.of((self, args, ctx) -> {
            ArrayVariable thisVar = (ArrayVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("LOAD requires 1 argument: path");
            }
            String path = ArgumentHelper.getString(args.get(0));
            String filePath = pl.genschu.bloomooemulator.utils.FileUtils.resolveRelativePath(ctx.getGame(), path);
            thisVar.elements.clear();
            try (java.io.FileInputStream f = new java.io.FileInputStream(filePath)) {
                java.nio.ByteOrder LE = java.nio.ByteOrder.LITTLE_ENDIAN;
                byte[] buf4 = new byte[4];
                f.read(buf4);
                int arrayLength = java.nio.ByteBuffer.wrap(buf4).order(LE).getInt();
                for (int i = 0; i < arrayLength; i++) {
                    f.read(buf4);
                    int dataType = java.nio.ByteBuffer.wrap(buf4).order(LE).getInt();
                    switch (dataType) {
                        case 1 -> { f.read(buf4); thisVar.elements.add(new IntValue(java.nio.ByteBuffer.wrap(buf4).order(LE).getInt())); }
                        case 2 -> { f.read(buf4); int len = java.nio.ByteBuffer.wrap(buf4).order(LE).getInt(); byte[] sb = new byte[len]; f.read(sb); thisVar.elements.add(new StringValue(new String(sb, java.nio.charset.StandardCharsets.UTF_8).split("\0")[0])); }
                        case 3 -> { byte[] b = new byte[1]; f.read(b); thisVar.elements.add(new BoolValue(b[0] != 0)); }
                        case 4 -> { f.read(buf4); thisVar.elements.add(new DoubleValue(java.nio.ByteBuffer.wrap(buf4).order(LE).getInt() / 10000.0)); }
                        default -> throw new IllegalArgumentException("Unknown array data type: " + dataType);
                    }
                }
            } catch (java.io.IOException e) {
                com.badlogic.gdx.Gdx.app.error("ArrayVariable", "Error loading array: " + e.getMessage());
            }
            return MethodResult.noReturn();
        })),

        Map.entry("LOADINI", MethodSpec.of((self, args, ctx) -> {
            ArrayVariable thisVar = (ArrayVariable) self;
            pl.genschu.bloomooemulator.engine.Game game = ctx.getGame();
            if (game == null || game.getGameINI() == null) return MethodResult.noReturn();
            String section = game.findINISectionForVariable(thisVar.name().toUpperCase());
            String serialized = game.getGameINI().get(section, thisVar.name().toUpperCase());
            thisVar.elements.clear();
            if (serialized != null && !serialized.isEmpty()) {
                for (String elem : serialized.split(",")) {
                    elem = elem.trim();
                    try { thisVar.elements.add(new IntValue(Integer.parseInt(elem))); continue; } catch (NumberFormatException ignored) {}
                    try { thisVar.elements.add(new DoubleValue(Double.parseDouble(elem))); continue; } catch (NumberFormatException ignored) {}
                    if ("TRUE".equalsIgnoreCase(elem) || "FALSE".equalsIgnoreCase(elem)) {
                        thisVar.elements.add(new BoolValue(Boolean.parseBoolean(elem)));
                    } else {
                        thisVar.elements.add(new StringValue(elem));
                    }
                }
            }
            return MethodResult.noReturn();
        })),

        Map.entry("SAVE", MethodSpec.of((self, args, ctx) -> {
            ArrayVariable thisVar = (ArrayVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("SAVE requires 1 argument: path");
            }
            String path = ArgumentHelper.getString(args.get(0));
            String filePath = pl.genschu.bloomooemulator.utils.FileUtils.resolveRelativePath(ctx.getGame(), path);
            try (java.io.FileOutputStream f = new java.io.FileOutputStream(filePath)) {
                java.nio.ByteOrder LE = java.nio.ByteOrder.LITTLE_ENDIAN;
                f.write(java.nio.ByteBuffer.allocate(4).order(LE).putInt(thisVar.elements.size()).array());
                for (Value element : thisVar.elements) {
                    switch (element) {
                        case IntValue iv -> { f.write(java.nio.ByteBuffer.allocate(4).order(LE).putInt(1).array()); f.write(java.nio.ByteBuffer.allocate(4).order(LE).putInt(iv.value()).array()); }
                        case DoubleValue dv -> { f.write(java.nio.ByteBuffer.allocate(4).order(LE).putInt(4).array()); f.write(java.nio.ByteBuffer.allocate(4).order(LE).putInt((int)(dv.value() * 10000)).array()); }
                        case StringValue sv -> { f.write(java.nio.ByteBuffer.allocate(4).order(LE).putInt(2).array()); byte[] sb = sv.value().getBytes(java.nio.charset.StandardCharsets.UTF_8); f.write(java.nio.ByteBuffer.allocate(4).order(LE).putInt(sb.length + 1).array()); f.write(sb); f.write(0); }
                        case BoolValue bv -> { f.write(java.nio.ByteBuffer.allocate(4).order(LE).putInt(3).array()); f.write(bv.value() ? 1 : 0); }
                        default -> throw new IllegalArgumentException("Unsupported array element type for save");
                    }
                }
            } catch (java.io.IOException e) {
                com.badlogic.gdx.Gdx.app.error("ArrayVariable", "Error saving array: " + e.getMessage());
            }
            return MethodResult.noReturn();
        })),

        Map.entry("SAVEINI", MethodSpec.of((self, args, ctx) -> {
            ArrayVariable thisVar = (ArrayVariable) self;
            pl.genschu.bloomooemulator.engine.Game game = ctx.getGame();
            if (game == null || game.getGameINI() == null) return MethodResult.noReturn();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < thisVar.elements.size(); i++) {
                sb.append(thisVar.elements.get(i).toDisplayString());
                if (i < thisVar.elements.size() - 1) sb.append(",");
            }
            String section = game.findINISectionForVariable(thisVar.name().toUpperCase());
            game.getGameINI().put(section, thisVar.name().toUpperCase(), sb.toString());
            return MethodResult.noReturn();
        })),

        Map.entry("SUM", MethodSpec.of((self, args, ctx) -> {
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
