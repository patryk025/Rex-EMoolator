package pl.genschu.bloomooemulator.interpreter.variable;

import pl.genschu.bloomooemulator.interpreter.helpers.ArgumentHelper;
import pl.genschu.bloomooemulator.interpreter.values.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * BoolVariable holds boolean value
 **/
public record BoolVariable(
    String name,
    boolean boolValue,
    Map<String, SignalHandler> signals
) implements Variable {

    public BoolVariable {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Variable name cannot be null or empty");
        }
        if (signals == null) {
            signals = Map.of();
        } else {
            signals = Map.copyOf(signals);
        }
    }

    public BoolVariable(String name, boolean boolValue) {
        this(name, boolValue, Map.of());
    }

    // ========================================
    // INTERFACE IMPLEMENTATION
    // ========================================

    @Override
    public Value value() {
        return BoolValue.of(boolValue);
    }

    @Override
    public VariableType type() {
        return VariableType.BOOLEAN;
    }

    @Override
    public Variable withValue(Value newValue) {
        boolean newBool = ArgumentHelper.getBoolean(newValue);

        return new BoolVariable(name, newBool, signals);
    }

    @Override
    public Map<String, VariableMethod> methods() {
        return METHODS;
    }

    @Override
    public Variable withSignal(String signalName, SignalHandler handler) {
        Map<String, SignalHandler> newSignals = new HashMap<>(signals);
        newSignals.put(signalName, handler);
        return new BoolVariable(name, boolValue, newSignals);
    }

    // ========================================
    // METHODS DEFINITION
    // ========================================

    private static final Map<String, VariableMethod> METHODS = Map.ofEntries(
        Map.entry("GET", (self, args) -> MethodResult.noChange(self.value())),

        Map.entry("RESETINI", (self, args) -> {
            BoolVariable thisVar = (BoolVariable) self;
            // TODO: Get DEFAULT value from INI file
            return MethodResult.setsAndReturnsValue(
                    new BoolVariable(thisVar.name, thisVar.boolValue, thisVar.signals)
            );
        }),

        Map.entry("SET", (self, args) -> {
            BoolVariable thisVar = (BoolVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("SET requires 1 argument");
            }

            boolean newValue = ArgumentHelper.getBoolean(args.get(0));
            return MethodResult.setsAndReturnsValue(
                    new BoolVariable(thisVar.name, newValue, thisVar.signals)
            );
        }),

        Map.entry("SWITCH", (self, args) -> {
            BoolVariable thisVar = (BoolVariable) self;
            if (args.size() < 2) {
                throw new IllegalArgumentException("SWITCH requires 2 arguments");
            }

            boolean value1 = ArgumentHelper.getBoolean(args.get(0));
            boolean value2 = ArgumentHelper.getBoolean(args.get(1));
            // If current != value1 then use value2, else value1
            boolean result = (thisVar.boolValue != value1) ? value2 : value1;

            return MethodResult.setsAndReturnsValue(
                    new BoolVariable(thisVar.name, result, thisVar.signals)
            );
        })
    );

    // ========================================
    // CONVENIENT ACCESSORS
    // ========================================

    /**
     * Gets the boolean value directly.
     * Convenience method to avoid value().unwrap().
     */
    public boolean get() {
        return boolValue;
    }

    @Override
    public String toString() {
        return "BoolVariable[" + name + "=" + (boolValue ? "TRUE" : "FALSE") + "]";
    }

}
