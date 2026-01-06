package pl.genschu.bloomooemulator.interpreter.variable;

import pl.genschu.bloomooemulator.interpreter.values.*;

import java.util.HashMap;
import java.util.Map;

/**
 * IntegerVariable holds int value
 **/
public record IntegerVariable(
    String name,
    int intValue,
    Map<String, SignalHandler> signals
) implements Variable {

    public IntegerVariable {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Variable name cannot be null or empty");
        }
        if (signals == null) {
            signals = Map.of();  // Empty immutable map
        } else {
            signals = Map.copyOf(signals);  // Defensive copy
        }
    }

    // Convenience constructor without signals
    public IntegerVariable(String name, int intValue) {
        this(name, intValue, Map.of());
    }

    // ========================================
    // INTERFACE IMPLEMENTATION
    // ========================================

    @Override
    public Value value() {
        return new IntValue(intValue);
    }

    @Override
    public VariableType type() {
        return VariableType.INTEGER;
    }

    @Override
    public Variable withValue(Value newValue) {
        // Convert Value to int with type coercion
        int newInt = switch (newValue) {
            case IntValue v -> v.value();
            case DoubleValue v -> (int) v.value();
            case StringValue v -> {
                IntValue parsed = v.tryParseInt();
                yield parsed != null ? parsed.value() : 0;
            }
            case BoolValue v -> v.value() ? 1 : 0;
            default -> 0;
        };

        // Return NEW instance
        return new IntegerVariable(name, newInt, signals);
    }

    @Override
    public Map<String, VariableMethod> methods() {
        return METHODS;
    }

    @Override
    public Variable withSignal(String signalName, SignalHandler handler) {
        Map<String, SignalHandler> newSignals = new HashMap<>(signals);
        newSignals.put(signalName, handler);
        return new IntegerVariable(name, intValue, newSignals);
    }

    // ========================================
    // METHODS DEFINITION
    // ========================================

    private static final Map<String, VariableMethod> METHODS = Map.ofEntries(
        Map.entry("ADD", (self, args) -> {
            IntegerVariable thisVar = (IntegerVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("ADD requires 1 argument");
            }

            int addend = toInt(args.get(0));
            return new IntegerVariable(thisVar.name, thisVar.intValue + addend, thisVar.signals);
        }),

        Map.entry("SUB", (self, args) -> {
            IntegerVariable thisVar = (IntegerVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("SUB requires 1 argument");
            }

            int subtrahend = toInt(args.get(0));
            return new IntegerVariable(thisVar.name, thisVar.intValue - subtrahend, thisVar.signals);
        }),

        Map.entry("MUL", (self, args) -> {
            IntegerVariable thisVar = (IntegerVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("MUL requires 1 argument");
            }

            int multiplier = toInt(args.get(0));
            return new IntegerVariable(thisVar.name, thisVar.intValue * multiplier, thisVar.signals);
        }),

        Map.entry("DIV", (self, args) -> {
            IntegerVariable thisVar = (IntegerVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("DIV requires 1 argument");
            }

            int divisor = toInt(args.get(0));
            if (divisor == 0) {
                return thisVar;  // Division by zero = no change
            }
            return new IntegerVariable(thisVar.name, thisVar.intValue / divisor, thisVar.signals);
        }),

        Map.entry("MOD", (self, args) -> {
            IntegerVariable thisVar = (IntegerVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("MOD requires 1 argument");
            }

            int divisor = toInt(args.get(0));
            if (divisor == 0) {
                return thisVar;
            }
            return new IntegerVariable(thisVar.name, thisVar.intValue % divisor, thisVar.signals);
        }),

        Map.entry("SET", (self, args) -> {
            IntegerVariable thisVar = (IntegerVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("SET requires 1 argument");
            }

            int newValue = toInt(args.get(0));
            return new IntegerVariable(thisVar.name, newValue, thisVar.signals);
        }),

        Map.entry("GET", (self, args) -> self),

        Map.entry("INC", (self, args) -> {
            IntegerVariable thisVar = (IntegerVariable) self;
            return new IntegerVariable(thisVar.name, thisVar.intValue + 1, thisVar.signals);
        }),

        Map.entry("DEC", (self, args) -> {
            IntegerVariable thisVar = (IntegerVariable) self;
            return new IntegerVariable(thisVar.name, thisVar.intValue - 1, thisVar.signals);
        }),

        Map.entry("ABS", (self, args) -> {
            IntegerVariable thisVar = (IntegerVariable) self;
            return new IntegerVariable(thisVar.name, Math.abs(thisVar.intValue), thisVar.signals);
        }),

        Map.entry("NEG", (self, args) -> {
            IntegerVariable thisVar = (IntegerVariable) self;
            return new IntegerVariable(thisVar.name, -thisVar.intValue, thisVar.signals);
        }),

        Map.entry("MIN", (self, args) -> {
            IntegerVariable thisVar = (IntegerVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("MIN requires 1 argument");
            }

            int other = toInt(args.get(0));
            return new IntegerVariable(thisVar.name, Math.min(thisVar.intValue, other), thisVar.signals);
        }),

        Map.entry("MAX", (self, args) -> {
            IntegerVariable thisVar = (IntegerVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("MAX requires 1 argument");
            }

            int other = toInt(args.get(0));
            return new IntegerVariable(thisVar.name, Math.max(thisVar.intValue, other), thisVar.signals);
        })
    );

    // ========================================
    // HELPER METHODS
    // ========================================

    /**
     * Converts Value to int with type coercion.
     * Helper for method implementations.
     */
    private static int toInt(Value value) {
        return switch (value) {
            case IntValue v -> v.value();
            case DoubleValue v -> (int) v.value();
            case StringValue v -> {
                IntValue parsed = v.tryParseInt();
                yield parsed != null ? parsed.value() : 0;
            }
            case BoolValue v -> v.value() ? 1 : 0;
            default -> 0;
        };
    }

    // ========================================
    // CONVENIENT ACCESSORS
    // ========================================

    /**
     * Gets the int value directly.
     * Convenience method to avoid value().unwrap().
     */
    public int get() {
        return intValue;
    }

    @Override
    public String toString() {
        return "IntVariable[" + name + "=" + intValue + "]";
    }
}
