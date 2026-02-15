package pl.genschu.bloomooemulator.interpreter.variable;

import pl.genschu.bloomooemulator.annotations.InternalMutable;
import pl.genschu.bloomooemulator.interpreter.helpers.ArgumentHelper;
import pl.genschu.bloomooemulator.interpreter.values.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * IntegerVariable holds int value
 **/
public record IntegerVariable(
    String name,
    @InternalMutable MutableValue holder,
    Map<String, SignalHandler> signals
) implements Variable {

    public IntegerVariable {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Variable name cannot be null or empty");
        }
        if (holder == null) {
            holder = new MutableValue(new IntValue(0));
        }
        if (signals == null) {
            signals = Map.of();
        } else {
            signals = Map.copyOf(signals);
        }
    }

    // Convenience constructors
    public IntegerVariable(String name, int intValue) {
        this(name, new MutableValue(new IntValue(intValue)), Map.of());
    }

    public IntegerVariable(String name, int intValue, Map<String, SignalHandler> signals) {
        this(name, new MutableValue(new IntValue(intValue)), signals);
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
        return VariableType.INTEGER;
    }

    @Override
    public Variable withValue(Value newValue) {
        int newInt = switch (newValue) {
            case IntValue v -> v.value();
            case DoubleValue v -> (int) v.value();
            case StringValue v -> {
                IntValue parsed = v.toInt();
                yield parsed.value();
            }
            case BoolValue v -> v.value() ? 1 : 0;
            default -> 0;
        };
        setValue(new IntValue(newInt));
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
        return new IntegerVariable(name, holder, newSignals);
    }

    // ========================================
    // CONVENIENT ACCESSORS
    // ========================================

    public int getInt() {
        return ((IntValue) holder.get()).value();
    }

    /**
     * Gets the int value directly.
     */
    public int get() {
        return getInt();
    }

    /**
     * Sets the value and emits appropriate signals.
     * Always emits ONBRUTALCHANGED, emits ONCHANGED only if value actually changed.
     */
    public void setValue(IntValue newValue) {
        int oldVal = getInt();
        int newVal = newValue.value();
        holder.set(newValue);

        // Always emit ONBRUTALCHANGED
        emitSignal("ONBRUTALCHANGED", new IntValue(newVal));

        // Emit ONCHANGED only if value actually changed
        if (oldVal != newVal) {
            emitSignal("ONCHANGED", new IntValue(newVal));
        }
    }

    // ========================================
    // METHODS DEFINITION
    // ========================================

    private static final Map<String, MethodSpec> METHODS = Map.ofEntries(
        Map.entry("ABS", MethodSpec.of((self, args, ctx) -> {
            IntegerVariable thisVar = (IntegerVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("ABS requires 1 argument");
            }
            int value = ArgumentHelper.getInt(args.get(0));
            IntValue result = new IntValue(Math.abs(value));
            thisVar.setValue(result);
            return MethodResult.returns(result);
        })),

        Map.entry("ADD", MethodSpec.of((self, args, ctx) -> {
            IntegerVariable thisVar = (IntegerVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("ADD requires 1 argument");
            }
            int addend = ArgumentHelper.getInt(args.get(0));
            IntValue result = new IntValue(thisVar.getInt() + addend);
            thisVar.setValue(result);
            return MethodResult.returns(result);
        })),

        Map.entry("AND", MethodSpec.of((self, args, ctx) -> {
            IntegerVariable thisVar = (IntegerVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("AND requires 1 argument");
            }
            int value = ArgumentHelper.getInt(args.get(0));
            IntValue result = new IntValue(thisVar.getInt() & value);
            thisVar.setValue(result);
            return MethodResult.returns(result);
        })),

        Map.entry("CLAMP", MethodSpec.of((self, args, ctx) -> {
            IntegerVariable thisVar = (IntegerVariable) self;
            if (args.size() < 2) {
                throw new IllegalArgumentException("CLAMP requires 2 arguments");
            }
            int rangeMin = ArgumentHelper.getInt(args.get(0));
            int rangeMax = ArgumentHelper.getInt(args.get(1));
            int clampedValue = Math.max(rangeMin, Math.min(rangeMax, thisVar.getInt()));
            IntValue result = new IntValue(clampedValue);
            thisVar.setValue(result);
            return MethodResult.returns(result);
        })),

        Map.entry("CLEAR", MethodSpec.of((self, args, ctx) -> {
            IntegerVariable thisVar = (IntegerVariable) self;
            IntValue result = new IntValue(0);
            thisVar.setValue(result);
            return MethodResult.returns(result);
        })),

        Map.entry("DEC", MethodSpec.of((self, args, ctx) -> {
            IntegerVariable thisVar = (IntegerVariable) self;
            IntValue result = new IntValue(thisVar.getInt() - 1);
            thisVar.setValue(result);
            return MethodResult.returns(result);
        })),

        Map.entry("DIV", MethodSpec.of((self, args, ctx) -> {
            IntegerVariable thisVar = (IntegerVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("DIV requires 1 argument");
            }
            int divisor = ArgumentHelper.getInt(args.get(0));
            if (divisor == 0) {
                return MethodResult.returns(thisVar.value());  // Division by zero = no change
            }
            IntValue result = new IntValue(thisVar.getInt() / divisor);
            thisVar.setValue(result);
            return MethodResult.returns(result);
        })),

        Map.entry("GET", MethodSpec.of((self, args, ctx) -> MethodResult.returns(self.value()))),

        Map.entry("INC", MethodSpec.of((self, args, ctx) -> {
            IntegerVariable thisVar = (IntegerVariable) self;
            IntValue result = new IntValue(thisVar.getInt() + 1);
            thisVar.setValue(result);
            return MethodResult.returns(result);
        })),

        Map.entry("LENGTH", MethodSpec.of((self, args, ctx) -> {
            IntegerVariable thisVar = (IntegerVariable) self;
            if (args.size() < 2) {
                throw new IllegalArgumentException("LENGTH requires 2 arguments");
            }
            int x = ArgumentHelper.getInt(args.get(0));
            int y = ArgumentHelper.getInt(args.get(1));
            int length = (int) Math.sqrt(x * x + y * y);
            IntValue result = new IntValue(length);
            thisVar.setValue(result);
            return MethodResult.returns(result);
        })),

        Map.entry("MOD", MethodSpec.of((self, args, ctx) -> {
            IntegerVariable thisVar = (IntegerVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("MOD requires 1 argument");
            }
            int divisor = ArgumentHelper.getInt(args.get(0));
            if (divisor == 0) {
                return MethodResult.returns(thisVar.value());
            }
            IntValue result = new IntValue(thisVar.getInt() % divisor);
            thisVar.setValue(result);
            return MethodResult.returns(result);
        })),

        Map.entry("MUL", MethodSpec.of((self, args, ctx) -> {
            IntegerVariable thisVar = (IntegerVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("MUL requires 1 argument");
            }
            int multiplier = ArgumentHelper.getInt(args.get(0));
            IntValue result = new IntValue(thisVar.getInt() * multiplier);
            thisVar.setValue(result);
            return MethodResult.returns(result);
        })),

        Map.entry("NOT", MethodSpec.of((self, args, ctx) -> {
            IntegerVariable thisVar = (IntegerVariable) self;
            IntValue result = new IntValue(~thisVar.getInt());
            thisVar.setValue(result);
            return MethodResult.returns(result);
        })),

        Map.entry("OR", MethodSpec.of((self, args, ctx) -> {
            IntegerVariable thisVar = (IntegerVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("OR requires 1 argument");
            }
            int value = ArgumentHelper.getInt(args.get(0));
            IntValue result = new IntValue(thisVar.getInt() | value);
            thisVar.setValue(result);
            return MethodResult.returns(result);
        })),

        Map.entry("POWER", MethodSpec.of((self, args, ctx) -> {
            IntegerVariable thisVar = (IntegerVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("POWER requires 1 argument");
            }
            int power = ArgumentHelper.getInt(args.get(0));
            double pow = Math.pow(thisVar.getInt(), power);
            int value;
            if (pow > 0) {
                value = (int) Math.round(pow);
            } else {
                value = (int) Math.ceil(pow - 0.5);
            }
            IntValue result = new IntValue(value);
            thisVar.setValue(result);
            return MethodResult.returns(result);
        })),

        Map.entry("RANDOM", MethodSpec.of((self, args, ctx) -> {
            IntegerVariable thisVar = (IntegerVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("RANDOM requires 1 or 2 arguments");
            }
            int rand;
            if (args.size() == 1) {
                Random random = new Random();
                int bound = ArgumentHelper.getInt(args.get(0));
                rand = random.nextInt(bound);
            } else {
                int min = ArgumentHelper.getInt(args.get(0));
                int max = ArgumentHelper.getInt(args.get(1));
                Random random = new Random();
                rand = min + random.nextInt(max - min + 1);
            }
            IntValue result = new IntValue(rand);
            thisVar.setValue(result);
            return MethodResult.returns(result);
        })),

        Map.entry("RESETINI", MethodSpec.of((self, args, ctx) -> {
            IntegerVariable thisVar = (IntegerVariable) self;
            // TODO: Get DEFAULT value from INI file
            return MethodResult.returns(thisVar.value());
        })),

        Map.entry("SET", MethodSpec.of((self, args, ctx) -> {
            IntegerVariable thisVar = (IntegerVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("SET requires 1 argument");
            }
            int newValue = ArgumentHelper.getInt(args.get(0));
            IntValue result = new IntValue(newValue);
            thisVar.setValue(result);
            return MethodResult.returns(result);
        })),

        Map.entry("SUB", MethodSpec.of((self, args, ctx) -> {
            IntegerVariable thisVar = (IntegerVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("SUB requires 1 argument");
            }
            int subtrahend = ArgumentHelper.getInt(args.get(0));
            IntValue result = new IntValue(thisVar.getInt() - subtrahend);
            thisVar.setValue(result);
            return MethodResult.returns(result);
        })),

        Map.entry("SWITCH", MethodSpec.of((self, args, ctx) -> {
            IntegerVariable thisVar = (IntegerVariable) self;
            if (args.size() < 2) {
                throw new IllegalArgumentException("SWITCH requires 2 arguments");
            }
            int valueA = ArgumentHelper.getInt(args.get(0));
            int valueB = ArgumentHelper.getInt(args.get(1));
            int switched = (thisVar.getInt() == valueA) ? valueB : valueA;
            IntValue result = new IntValue(switched);
            thisVar.setValue(result);
            return MethodResult.returns(result);
        })),

        Map.entry("XOR", MethodSpec.of((self, args, ctx) -> {
            IntegerVariable thisVar = (IntegerVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("XOR requires 1 argument");
            }
            int value = ArgumentHelper.getInt(args.get(0));
            IntValue result = new IntValue(thisVar.getInt() ^ value);
            thisVar.setValue(result);
            return MethodResult.returns(result);
        }))
    );

    @Override
    public String toString() {
        return "IntVariable[" + name + "=" + getInt() + "]";
    }

    @Override
    public Variable copyAs(String newName) {
        return new IntegerVariable(newName, this.getInt(), this.signals);
    }
}
