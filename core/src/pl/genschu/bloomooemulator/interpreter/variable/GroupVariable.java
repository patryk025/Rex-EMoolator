package pl.genschu.bloomooemulator.interpreter.variable;

import pl.genschu.bloomooemulator.interpreter.helpers.ArgumentHelper;
import pl.genschu.bloomooemulator.interpreter.values.*;

import java.util.*;

/**
 * GroupVariable stores a collection of variable references (by name).
 * Supports iteration with a marker and can delegate method calls to all members.
 **/
public record GroupVariable(
    String name,
    List<String> variableNames,
    int marker,
    Map<String, SignalHandler> signals
) implements Variable {

    public GroupVariable {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Variable name cannot be null or empty");
        }
        if (variableNames == null) {
            variableNames = List.of();
        } else {
            variableNames = List.copyOf(variableNames);
        }
        if (signals == null) {
            signals = Map.of();
        } else {
            signals = Map.copyOf(signals);
        }
    }

    public GroupVariable(String name) {
        this(name, List.of(), -1, Map.of());
    }

    public GroupVariable(String name, List<String> variableNames) {
        this(name, variableNames, variableNames.isEmpty() ? -1 : 0, Map.of());
    }

    // ========================================
    // INTERFACE IMPLEMENTATION
    // ========================================

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
        // Cannot set value directly on group
        return this;
    }

    @Override
    public Map<String, MethodSpec> methods() {
        return METHODS;
    }

    @Override
    public Variable withSignal(String signalName, SignalHandler handler) {
        Map<String, SignalHandler> newSignals = new HashMap<>(signals);
        newSignals.put(signalName, handler);
        return new GroupVariable(name, variableNames, marker, newSignals);
    }

    // ========================================
    // HELPER METHODS
    // ========================================

    private GroupVariable withVariableNames(List<String> newNames) {
        int newMarker = newNames.isEmpty() ? -1 : Math.min(marker, newNames.size() - 1);
        if (newMarker < 0 && !newNames.isEmpty()) newMarker = 0;
        return new GroupVariable(name, newNames, newMarker, signals);
    }

    private GroupVariable withMarker(int newMarker) {
        return new GroupVariable(name, variableNames, newMarker, signals);
    }

    // ========================================
    // METHODS DEFINITION
    // ========================================

    private static final Map<String, MethodSpec> METHODS = Map.ofEntries(
        Map.entry("ADD", MethodSpec.of((self, args) -> {
            GroupVariable thisVar = (GroupVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("ADD requires at least 1 argument");
            }

            List<String> newNames = new ArrayList<>(thisVar.variableNames);
            for (Value arg : args) {
                String varName = ArgumentHelper.getString(arg);
                if (!newNames.contains(varName)) {
                    newNames.add(varName);
                }
            }
            return MethodResult.sets(thisVar.withVariableNames(newNames));
        })),

        // NOTE: ADDCLONES requires context access to resolve variable clones
        // and should be handled at a higher level

        Map.entry("GETSIZE", MethodSpec.of((self, args) -> {
            GroupVariable thisVar = (GroupVariable) self;
            return MethodResult.noChange(new IntValue(thisVar.variableNames.size()));
        })),

        Map.entry("NEXT", MethodSpec.of((self, args) -> {
            GroupVariable thisVar = (GroupVariable) self;
            if (thisVar.variableNames.isEmpty()) {
                return MethodResult.noChange(NullValue.INSTANCE);
            }

            int newMarker = Math.min(thisVar.marker + 1, thisVar.variableNames.size() - 1);
            String varName = thisVar.variableNames.get(newMarker);
            return MethodResult.setsAndReturns(
                    thisVar.withMarker(newMarker),
                    new StringValue(varName)
            );
        })),

        Map.entry("PREV", MethodSpec.of((self, args) -> {
            GroupVariable thisVar = (GroupVariable) self;
            if (thisVar.variableNames.isEmpty()) {
                return MethodResult.noChange(NullValue.INSTANCE);
            }

            int newMarker = Math.max(thisVar.marker - 1, 0);
            String varName = thisVar.variableNames.get(newMarker);
            return MethodResult.setsAndReturns(
                    thisVar.withMarker(newMarker),
                    new StringValue(varName)
            );
        })),

        Map.entry("REMOVE", MethodSpec.of((self, args) -> {
            GroupVariable thisVar = (GroupVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("REMOVE requires 1 argument");
            }

            String varName = ArgumentHelper.getString(args.get(0));
            List<String> newNames = new ArrayList<>(thisVar.variableNames);
            newNames.remove(varName);
            return MethodResult.sets(thisVar.withVariableNames(newNames));
        })),

        Map.entry("REMOVEALL", MethodSpec.of((self, args) -> {
            GroupVariable thisVar = (GroupVariable) self;
            return MethodResult.sets(new GroupVariable(thisVar.name, List.of(), -1, thisVar.signals));
        })),

        Map.entry("RESETMARKER", MethodSpec.of((self, args) -> {
            GroupVariable thisVar = (GroupVariable) self;
            int newMarker = thisVar.variableNames.isEmpty() ? -1 : 0;
            return MethodResult.sets(thisVar.withMarker(newMarker));
        }))
    );

    // ========================================
    // CONVENIENT ACCESSORS
    // ========================================

    /**
     * Gets the size of the group.
     */
    public int size() {
        return variableNames.size();
    }

    @Override
    public String toString() {
        return "GroupVariable[" + name + ", size=" + variableNames.size() + ", marker=" + marker + "]";
    }
}
