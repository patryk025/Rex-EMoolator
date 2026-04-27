package pl.genschu.bloomooemulator.interpreter.variable;

import pl.genschu.bloomooemulator.annotations.InternalMutable;
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
    @InternalMutable MutableValue holder,
    Map<String, SignalHandler> signals
) implements Variable {

    public BoolVariable {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Variable name cannot be null or empty");
        }
        if (holder == null) {
            holder = new MutableValue(BoolValue.FALSE);
        }
        if (signals == null) {
            signals = Map.of();
        } else {
            signals = Map.copyOf(signals);
        }
    }

    // Convenience constructors
    public BoolVariable(String name, boolean boolValue) {
        this(name, new MutableValue(BoolValue.of(boolValue)), Map.of());
    }

    public BoolVariable(String name, boolean boolValue, Map<String, SignalHandler> signals) {
        this(name, new MutableValue(BoolValue.of(boolValue)), signals);
    }

    // ========================================
    // INTERFACE IMPLEMENTATION
    // ========================================

    @Override
    public Value value() {
        return holder.get();
    }

    @Override
    public VariableType type() {
        return VariableType.BOOLEAN;
    }

    @Override
    public Variable withValue(Value newValue) {
        boolean newBool = ArgumentHelper.getBoolean(newValue);
        setValue(BoolValue.of(newBool));
        return this;
    }

    @Override
    public Map<String, MethodSpec> methods() {
        return METHODS;
    }

    @Override
    public Variable withSignal(String signalName, SignalHandler handler) {
        Map<String, SignalHandler> newSignals = new HashMap<>(signals);
        if (handler == null) {
            newSignals.remove(signalName);
        } else {
            newSignals.put(signalName, handler);
        }
        return new BoolVariable(name, holder, newSignals);
    }

    // ========================================
    // CONVENIENT ACCESSORS
    // ========================================

    public boolean getBool() {
        return ((BoolValue) holder.get()).value();
    }

    public boolean get() {
        return getBool();
    }

    /**
     * Sets the value and emits appropriate signals.
     * Always emits ONBRUTALCHANGED, emits ONCHANGED only if value actually changed.
     */
    public void setValue(BoolValue newValue) {
        boolean oldVal = getBool();
        boolean newVal = newValue.value();
        holder.set(newValue);

        // Always emit ONBRUTALCHANGED
        emitSignal("ONBRUTALCHANGED", new BoolValue(newVal));

        // Emit ONCHANGED only if value actually changed
        if (oldVal != newVal) {
            emitSignal("ONCHANGED", new BoolValue(newVal));
        }
    }

    // ========================================
    // METHODS DEFINITION
    // ========================================

    private static final Map<String, MethodSpec> METHODS = Map.ofEntries(
        Map.entry("GET", MethodSpec.of((self, args, ctx) -> MethodResult.returns(self.value()))),

        Map.entry("RESETINI", MethodSpec.of((self, args, ctx) -> {
            BoolVariable thisVar = (BoolVariable) self;
            String resetValue = self.getResetAttributeValue(ctx);
            BoolValue result = BoolValue.of(resetValue != null && ArgumentHelper.getBoolean(new StringValue(resetValue)));
            thisVar.setValue(result);
            return MethodResult.noReturn();
        })),

        Map.entry("SET", MethodSpec.of((self, args, ctx) -> {
            BoolVariable thisVar = (BoolVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("SET requires 1 argument");
            }
            boolean newValue = ArgumentHelper.getBoolean(args.get(0));
            BoolValue result = BoolValue.of(newValue);
            thisVar.setValue(result);
            return MethodResult.returns(result);
        })),

        Map.entry("SWITCH", MethodSpec.of((self, args, ctx) -> {
            BoolVariable thisVar = (BoolVariable) self;
            if (args.size() < 2) {
                throw new IllegalArgumentException("SWITCH requires 2 arguments");
            }
            boolean value1 = ArgumentHelper.getBoolean(args.get(0));
            boolean value2 = ArgumentHelper.getBoolean(args.get(1));
            boolean switched = (thisVar.getBool() != value1) ? value2 : value1;
            BoolValue result = BoolValue.of(switched);
            thisVar.setValue(result);
            return MethodResult.returns(result);
        }))
    );

    @Override
    public String toString() {
        return "BoolVariable[" + name + "=" + (getBool() ? "TRUE" : "FALSE") + "]";
    }

    @Override
    public Variable copyAs(String newName) {
        return new BoolVariable(newName, this.getBool(), this.signals);
    }
}
