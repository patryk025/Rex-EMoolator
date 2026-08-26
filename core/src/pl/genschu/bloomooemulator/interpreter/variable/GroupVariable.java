package pl.genschu.bloomooemulator.interpreter.variable;

import pl.genschu.bloomooemulator.annotations.InternalMutable;
import pl.genschu.bloomooemulator.interpreter.helpers.ArgumentHelper;
import pl.genschu.bloomooemulator.interpreter.values.*;

import java.util.*;

/**
 * GroupVariable stores a collection of variable references (by name).
 * Supports iteration with a marker and can delegate method calls to all members.
 **/
public record GroupVariable(
    String name,
    @InternalMutable
    List<String> variableNames,
    @InternalMutable
    int[] markerHolder,  // single-element mutable marker holder
    Map<String, SignalHandler> signals
) implements Variable {

    public GroupVariable {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Variable name cannot be null or empty");
        }
        if (variableNames == null) {
            variableNames = new ArrayList<>();
        } else if (!(variableNames instanceof ArrayList)) {
            variableNames = new ArrayList<>(variableNames);
        }
        if (markerHolder == null) {
            markerHolder = new int[]{-1};
        }
        if (signals == null) {
            signals = Map.of();
        } else {
            signals = Map.copyOf(signals);
        }
    }

    public GroupVariable(String name) {
        this(name, new ArrayList<>(), new int[]{-1}, Map.of());
    }

    public GroupVariable(String name, List<String> variableNames) {
        this(name, variableNames, new int[]{-1}, Map.of());
    }

    // Legacy compat: marker as int
    public GroupVariable(String name, List<String> variableNames, int marker, Map<String, SignalHandler> signals) {
        this(name, variableNames, new int[]{marker}, signals);
    }

    // ========================================
    // INTERFACE IMPLEMENTATION
    // ========================================

    public int marker() { return markerHolder[0]; }

    @Override
    public Value value() {
        return new IntValue(variableNames.size());
    }

    @Override
    public VariableType type() {
        return VariableType.GROUP;
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
        return new GroupVariable(name, variableNames, markerHolder, newSignals);
    }

    // ========================================
    // METHODS DEFINITION
    // ========================================

    private static final Map<String, MethodSpec> METHODS = Map.ofEntries(
        Map.entry("ADD", MethodSpec.of((self, args, ctx) -> {
            GroupVariable thisVar = (GroupVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("ADD requires at least 1 argument");
            }
            for (Value arg : args) {
                String varName = ArgumentHelper.getString(arg);
                if (!thisVar.variableNames.contains(varName)) {
                    thisVar.variableNames.add(varName);
                }
            }
            return MethodResult.noReturn();
        })),

        Map.entry("GETSIZE", MethodSpec.of((self, args, ctx) -> {
            GroupVariable thisVar = (GroupVariable) self;
            return MethodResult.returns(new IntValue(thisVar.variableNames.size()));
        })),

        Map.entry("GETNAMEATMARKER", MethodSpec.of((self, args, ctx) -> {
            GroupVariable thisVar = (GroupVariable) self;
            int marker = thisVar.markerHolder[0];
            if (marker < 0 || marker >= thisVar.variableNames.size()) {
                return MethodResult.returns(new StringValue("NULL"));
            }
            return MethodResult.returns(new StringValue(thisVar.variableNames.get(marker)));
        })),

        Map.entry("GETMARKERPOS", MethodSpec.of((self, args, ctx) -> {
            GroupVariable thisVar = (GroupVariable) self;
            return MethodResult.returns(new IntValue(thisVar.markerHolder[0]));
        })),

        Map.entry("NEXT", MethodSpec.of((self, args, ctx) -> {
            GroupVariable thisVar = (GroupVariable) self;
            if (thisVar.variableNames.isEmpty()) {
                return MethodResult.noReturn();
            }
            int step = args.isEmpty() ? 1 : ArgumentHelper.getInt(args.get(0));
            int newMarker = Math.floorMod(thisVar.markerHolder[0] + step, thisVar.variableNames.size());
            thisVar.markerHolder[0] = newMarker;
            String varName = thisVar.variableNames.get(newMarker);
            return MethodResult.returns(new StringValue(varName));
        })),

        Map.entry("PREV", MethodSpec.of((self, args, ctx) -> {
            GroupVariable thisVar = (GroupVariable) self;
            if (thisVar.variableNames.isEmpty()) {
                return MethodResult.noReturn();
            }
            int step = args.isEmpty() ? 1 : ArgumentHelper.getInt(args.get(0));
            int newMarker = Math.floorMod(thisVar.markerHolder[0] - step, thisVar.variableNames.size());
            thisVar.markerHolder[0] = newMarker;
            String varName = thisVar.variableNames.get(newMarker);
            return MethodResult.returns(new StringValue(varName));
        })),

        Map.entry("REMOVE", MethodSpec.of((self, args, ctx) -> {
            GroupVariable thisVar = (GroupVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("REMOVE requires 1 argument");
            }
            String varName = ArgumentHelper.getString(args.get(0));
            thisVar.variableNames.remove(varName);
            return MethodResult.noReturn();
        })),

        Map.entry("REMOVEALL", MethodSpec.of((self, args, ctx) -> {
            GroupVariable thisVar = (GroupVariable) self;
            thisVar.variableNames.clear();
            return MethodResult.noReturn();
        })),

        Map.entry("RESETMARKER", MethodSpec.of((self, args, ctx) -> {
            GroupVariable thisVar = (GroupVariable) self;
            thisVar.markerHolder[0] = -1;
            return MethodResult.noReturn();
        })),

        Map.entry("SETMARKERPOS", MethodSpec.of((self, args, ctx) -> {
            GroupVariable thisVar = (GroupVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("SETMARKERPOS requires 1 argument");
            }
            int requested = ArgumentHelper.getInt(args.get(0));
            thisVar.markerHolder[0] = Math.clamp(requested, -1, thisVar.variableNames.size() - 1);
            return MethodResult.noReturn();
        }))
    );

    // ========================================
    // CONVENIENT ACCESSORS
    // ========================================

    public int size() {
        return variableNames.size();
    }

    @Override
    public MethodResult callMethod(String methodName, List<Value> arguments, MethodContext ctx) {
        try {
            return Variable.super.callMethod(methodName, arguments, ctx);
        } catch (IllegalArgumentException e) {
            if (ctx == null) {
                throw e;
            }

            for (String varName : variableNames) {
                Variable variable = ctx.getVariable(varName);
                if (variable == null) {
                    continue;
                }

                try {
                    variable.callMethod(methodName, arguments, ctx);
                } catch (IllegalArgumentException | UnsupportedOperationException ignored) {
                    // Ignore members that do not implement the delegated method.
                }
            }
            return MethodResult.noReturn();
        }
    }

    @Override
    public String toString() {
        return "GroupVariable[" + name + ", size=" + variableNames.size() + ", marker=" + markerHolder[0] + "]";
    }
}
