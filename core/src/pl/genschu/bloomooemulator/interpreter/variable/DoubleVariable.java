package pl.genschu.bloomooemulator.interpreter.variable;

import pl.genschu.bloomooemulator.annotations.InternalMutable;
import pl.genschu.bloomooemulator.interpreter.helpers.ArgumentHelper;
import pl.genschu.bloomooemulator.interpreter.values.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DoubleVariable holds double value
 **/
public record DoubleVariable(
    String name,
    @InternalMutable MutableValue holder,
    Map<String, SignalHandler> signals
) implements Variable {

    public DoubleVariable {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Variable name cannot be null or empty");
        }
        if (holder == null) {
            holder = new MutableValue(new DoubleValue(0.0));
        }
        if (signals == null) {
            signals = Map.of();
        } else {
            signals = Map.copyOf(signals);
        }
    }

    // Convenience constructors
    public DoubleVariable(String name, double doubleValue) {
        this(name, new MutableValue(new DoubleValue(doubleValue)), Map.of());
    }

    public DoubleVariable(String name, double doubleValue, Map<String, SignalHandler> signals) {
        this(name, new MutableValue(new DoubleValue(doubleValue)), signals);
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
        return VariableType.DOUBLE;
    }

    @Override
    public Variable withValue(Value newValue) {
        double newDouble = ArgumentHelper.getDouble(newValue);
        setValue(new DoubleValue(newDouble));
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
        return new DoubleVariable(name, holder, newSignals);
    }

    // ========================================
    // CONVENIENT ACCESSORS
    // ========================================

    public double getDouble() {
        return ((DoubleValue) holder.get()).value();
    }

    public double get() {
        return getDouble();
    }

    /**
     * Sets the value and emits appropriate signals.
     * Always emits ONBRUTALCHANGED, emits ONCHANGED only if value actually changed.
     */
    public void setValue(DoubleValue newValue) {
        double oldVal = getDouble();
        double newVal = newValue.value();
        holder.set(newValue);

        // Always emit ONBRUTALCHANGED
        emitSignal("ONBRUTALCHANGED", new DoubleValue(newVal));

        // Emit ONCHANGED only if value actually changed
        if (oldVal != newVal) {
            emitSignal("ONCHANGED", new DoubleValue(newVal));
        }
    }

    // ========================================
    // METHODS DEFINITION
    // ========================================

    private static final Map<String, MethodSpec> METHODS = Map.ofEntries(
        Map.entry("ABS", MethodSpec.of((self, args) -> {
            DoubleVariable thisVar = (DoubleVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("ABS requires 1 argument");
            }
            double value = ArgumentHelper.getDouble(args.get(0));
            DoubleValue result = new DoubleValue(Math.abs(value));
            thisVar.setValue(result);
            return MethodResult.returns(result);
        })),

        Map.entry("ADD", MethodSpec.of((self, args) -> {
            DoubleVariable thisVar = (DoubleVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("ADD requires 1 argument");
            }
            double addend = ArgumentHelper.getDouble(args.get(0));
            DoubleValue result = new DoubleValue(thisVar.getDouble() + addend);
            thisVar.setValue(result);
            return MethodResult.returns(result);
        })),

        Map.entry("ARCTAN", MethodSpec.of((self, args) -> {
            DoubleVariable thisVar = (DoubleVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("ARCTAN requires 1 argument");
            }
            double angle = ArgumentHelper.getDouble(args.get(0));
            double atanDegrees = Math.toDegrees(Math.atan(angle));
            DoubleValue result = new DoubleValue(atanDegrees);
            thisVar.setValue(result);
            return MethodResult.returns(result);
        })),

        Map.entry("ARCTANEX", MethodSpec.of((self, args) -> {
            DoubleVariable thisVar = (DoubleVariable) self;
            if (args.size() < 2) {
                throw new IllegalArgumentException("ARCTANEX requires 2 arguments");
            }
            double y = ArgumentHelper.getDouble(args.get(0));
            double x = ArgumentHelper.getDouble(args.get(1));
            double atanDegrees = Math.toDegrees(Math.atan2(y, x));
            DoubleValue result = new DoubleValue(atanDegrees);
            thisVar.setValue(result);
            return MethodResult.returns(result);
        })),

        Map.entry("CLAMP", MethodSpec.of((self, args) -> {
            DoubleVariable thisVar = (DoubleVariable) self;
            if (args.size() < 2) {
                throw new IllegalArgumentException("CLAMP requires 2 arguments");
            }
            double rangeMin = ArgumentHelper.getDouble(args.get(0));
            double rangeMax = ArgumentHelper.getDouble(args.get(1));
            double clampedValue = Math.max(rangeMin, Math.min(rangeMax, thisVar.getDouble()));
            DoubleValue result = new DoubleValue(clampedValue);
            thisVar.setValue(result);
            return MethodResult.returns(result);
        })),

        Map.entry("CLEAR", MethodSpec.of((self, args) -> {
            DoubleVariable thisVar = (DoubleVariable) self;
            DoubleValue result = new DoubleValue(0.0);
            thisVar.setValue(result);
            return MethodResult.returns(result);
        })),

        Map.entry("COSINUS", MethodSpec.of((self, args) -> {
            DoubleVariable thisVar = (DoubleVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("COSINUS requires 1 argument");
            }
            double angle = ArgumentHelper.getDouble(args.get(0));
            double cosValue = Math.cos(Math.toRadians(angle));
            DoubleValue result = new DoubleValue(cosValue);
            thisVar.setValue(result);
            return MethodResult.returns(result);
        })),

        Map.entry("DEC", MethodSpec.of((self, args) -> {
            DoubleVariable thisVar = (DoubleVariable) self;
            DoubleValue result = new DoubleValue(thisVar.getDouble() - 1.0);
            thisVar.setValue(result);
            return MethodResult.returns(result);
        })),

        Map.entry("DIV", MethodSpec.of((self, args) -> {
            DoubleVariable thisVar = (DoubleVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("DIV requires 1 argument");
            }
            double divisor = ArgumentHelper.getDouble(args.get(0));
            if (divisor == 0.0) {
                return MethodResult.returns(thisVar.value());  // Division by zero = no change
            }
            DoubleValue result = new DoubleValue(thisVar.getDouble() / divisor);
            thisVar.setValue(result);
            return MethodResult.returns(result);
        })),

        Map.entry("GET", MethodSpec.of((self, args) -> MethodResult.returns(self.value()))),

        Map.entry("INC", MethodSpec.of((self, args) -> {
            DoubleVariable thisVar = (DoubleVariable) self;
            DoubleValue result = new DoubleValue(thisVar.getDouble() + 1.0);
            thisVar.setValue(result);
            return MethodResult.returns(result);
        })),

        Map.entry("LENGTH", MethodSpec.of((self, args) -> {
            DoubleVariable thisVar = (DoubleVariable) self;
            if (args.size() < 2) {
                throw new IllegalArgumentException("LENGTH requires 2 arguments");
            }
            double x = ArgumentHelper.getDouble(args.get(0));
            double y = ArgumentHelper.getDouble(args.get(1));
            double length = Math.sqrt(x * x + y * y);
            DoubleValue result = new DoubleValue(length);
            thisVar.setValue(result);
            return MethodResult.returns(result);
        })),

        Map.entry("LOG", MethodSpec.of((self, args) -> {
            DoubleVariable thisVar = (DoubleVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("LOG requires 1 argument");
            }
            double value = ArgumentHelper.getDouble(args.get(0));
            DoubleValue result = new DoubleValue(Math.log(value));
            thisVar.setValue(result);
            return MethodResult.returns(result);
        })),

        Map.entry("MOD", MethodSpec.of((self, args) -> {
            DoubleVariable thisVar = (DoubleVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("MOD requires 1 argument");
            }
            double divisor = ArgumentHelper.getDouble(args.get(0));
            if (divisor == 0.0) {
                return MethodResult.returns(thisVar.value());
            }
            // Modulo cuts off decimal part in original engine
            double modResult = (int) (thisVar.getDouble() % divisor);
            DoubleValue result = new DoubleValue(modResult);
            thisVar.setValue(result);
            return MethodResult.returns(result);
        })),

        Map.entry("MAXA", MethodSpec.of((self, args) -> {
            DoubleVariable thisVar = (DoubleVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("MAXA requires at least 1 argument");
            }
            double max = Double.MIN_VALUE;
            for (Value arg : args) {
                double value = ArgumentHelper.getDouble(arg);
                if (value > max) {
                    max = value;
                }
            }
            DoubleValue result = new DoubleValue(max);
            thisVar.setValue(result);
            return MethodResult.returns(result);
        })),

        Map.entry("MINA", MethodSpec.of((self, args) -> {
            DoubleVariable thisVar = (DoubleVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("MINA requires at least 1 argument");
            }
            double min = Double.MAX_VALUE;
            for (Value arg : args) {
                double value = ArgumentHelper.getDouble(arg);
                if (value < min) {
                    min = value;
                }
            }
            DoubleValue result = new DoubleValue(min);
            thisVar.setValue(result);
            return MethodResult.returns(result);
        })),

        Map.entry("MUL", MethodSpec.of((self, args) -> {
            DoubleVariable thisVar = (DoubleVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("MUL requires 1 argument");
            }
            double multiplier = ArgumentHelper.getDouble(args.get(0));
            DoubleValue result = new DoubleValue(thisVar.getDouble() * multiplier);
            thisVar.setValue(result);
            return MethodResult.returns(result);
        })),

        Map.entry("RESETINI", MethodSpec.of((self, args) -> {
            DoubleVariable thisVar = (DoubleVariable) self;
            // TODO: Get DEFAULT value from INI file
            return MethodResult.returns(thisVar.value());
        })),

        Map.entry("SET", MethodSpec.of((self, args) -> {
            DoubleVariable thisVar = (DoubleVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("SET requires 1 argument");
            }
            double newValue = ArgumentHelper.getDouble(args.get(0));
            DoubleValue result = new DoubleValue(newValue);
            thisVar.setValue(result);
            return MethodResult.returns(result);
        })),

        Map.entry("SGN", MethodSpec.of((self, args) -> {
            DoubleVariable thisVar = (DoubleVariable) self;
            int sgn = 0;
            if (thisVar.getDouble() > 0) {
                sgn = 1;
            } else if (thisVar.getDouble() < 0) {
                sgn = -1;
            }
            // SGN returns int, does not change the variable
            return MethodResult.returns(new IntValue(sgn));
        })),

        Map.entry("SINUS", MethodSpec.of((self, args) -> {
            DoubleVariable thisVar = (DoubleVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("SINUS requires 1 argument");
            }
            double angle = ArgumentHelper.getDouble(args.get(0));
            double sinValue = Math.sin(Math.toRadians(angle));
            DoubleValue result = new DoubleValue(sinValue);
            thisVar.setValue(result);
            return MethodResult.returns(result);
        })),

        Map.entry("SQRT", MethodSpec.of((self, args) -> {
            DoubleVariable thisVar = (DoubleVariable) self;
            double value = args.isEmpty() ? thisVar.getDouble() : ArgumentHelper.getDouble(args.get(0));
            DoubleValue result = new DoubleValue(Math.sqrt(value));
            thisVar.setValue(result);
            return MethodResult.returns(result);
        })),

        Map.entry("SUB", MethodSpec.of((self, args) -> {
            DoubleVariable thisVar = (DoubleVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("SUB requires 1 argument");
            }
            double subtrahend = ArgumentHelper.getDouble(args.get(0));
            DoubleValue result = new DoubleValue(thisVar.getDouble() - subtrahend);
            thisVar.setValue(result);
            return MethodResult.returns(result);
        })),

        Map.entry("SWITCH", MethodSpec.of((self, args) -> {
            DoubleVariable thisVar = (DoubleVariable) self;
            if (args.size() < 2) {
                throw new IllegalArgumentException("SWITCH requires 2 arguments");
            }
            double valueA = ArgumentHelper.getDouble(args.get(0));
            double valueB = ArgumentHelper.getDouble(args.get(1));
            double switched = (thisVar.getDouble() == valueA) ? valueB : valueA;
            DoubleValue result = new DoubleValue(switched);
            thisVar.setValue(result);
            return MethodResult.returns(result);
        }))
    );

    @Override
    public String toString() {
        return "DoubleVariable[" + name + "=" + getDouble() + "]";
    }

    @Override
    public Variable copyAs(String newName) {
        return new DoubleVariable(newName, this.getDouble(), this.signals);
    }
}
