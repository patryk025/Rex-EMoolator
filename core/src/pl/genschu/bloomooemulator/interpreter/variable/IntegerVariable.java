package pl.genschu.bloomooemulator.interpreter.variable;

import pl.genschu.bloomooemulator.interpreter.helpers.ArgumentHelper;
import pl.genschu.bloomooemulator.interpreter.values.*;
import pl.genschu.bloomooemulator.interpreter.variable.capabilities.CloneableVar;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * IntegerVariable holds int value
 **/
public record IntegerVariable(
    String name,
    int intValue,
    Map<String, SignalHandler> signals
) implements Variable, CloneableVar {

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
        Map.entry("ABS", (self, args) -> {
            IntegerVariable thisVar = (IntegerVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("ABS requires 1 argument");
            }

            int value = ArgumentHelper.getInt(args.get(0));
            return MethodResult.setsAndReturnsValue(
                    new IntegerVariable(thisVar.name, Math.abs(value), thisVar.signals)
            );
        }),

        Map.entry("ADD", (self, args) -> {
            IntegerVariable thisVar = (IntegerVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("ADD requires 1 argument");
            }

            int addend = ArgumentHelper.getInt(args.get(0));
            return MethodResult.setsAndReturnsValue(
                    new IntegerVariable(thisVar.name, thisVar.intValue + addend, thisVar.signals)
            );
        }),

        Map.entry("AND", (self, args) -> {
            IntegerVariable thisVar = (IntegerVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("AND requires 1 argument");
            }

            int value = ArgumentHelper.getInt(args.get(0));
            return MethodResult.setsAndReturnsValue(
                    new IntegerVariable(thisVar.name, thisVar.intValue & value, thisVar.signals)
            );
        }),

        Map.entry("CLAMP", (self, args) -> {
            IntegerVariable thisVar = (IntegerVariable) self;
            if (args.size() < 2) {
                throw new IllegalArgumentException("AND requires 2 argument");
            }

            int rangeMin = ArgumentHelper.getInt(args.get(0));
            int rangeMax = ArgumentHelper.getInt(args.get(1));
            int clampedValue = Math.max(rangeMin, Math.min(rangeMax, thisVar.intValue));
            return MethodResult.setsAndReturnsValue(
                    new IntegerVariable(thisVar.name, clampedValue, thisVar.signals)
            );
        }),

        Map.entry("CLEAR", (self, args) -> {
            IntegerVariable thisVar = (IntegerVariable) self;
            return MethodResult.setsAndReturnsValue(
                    new IntegerVariable(thisVar.name, 0, thisVar.signals)
            );
        }),

        Map.entry("DEC", (self, args) -> {
            IntegerVariable thisVar = (IntegerVariable) self;
            return MethodResult.setsAndReturnsValue(
                    new IntegerVariable(thisVar.name, thisVar.intValue - 1, thisVar.signals)
            );
        }),

        Map.entry("DIV", (self, args) -> {
            IntegerVariable thisVar = (IntegerVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("DIV requires 1 argument");
            }

            int divisor = ArgumentHelper.getInt(args.get(0));
            if (divisor == 0) {
                return MethodResult.noChange(thisVar.value());  // Division by zero = no change, in original code crashes engine
            }
            return MethodResult.setsAndReturnsValue(
                    new IntegerVariable(thisVar.name, thisVar.intValue / divisor, thisVar.signals)
            );
        }),

        Map.entry("GET", (self, args) -> MethodResult.noChange(self.value())),

        Map.entry("INC", (self, args) -> {
            IntegerVariable thisVar = (IntegerVariable) self;
            return MethodResult.setsAndReturnsValue(
                    new IntegerVariable(thisVar.name, thisVar.intValue + 1, thisVar.signals)
            );
        }),

        Map.entry("LENGTH", (self, args) -> {
            IntegerVariable thisVar = (IntegerVariable) self;
            if (args.size() < 2) {
                throw new IllegalArgumentException("LENGTH requires 2 argument");
            }

            int x = ArgumentHelper.getInt(args.get(0));
            int y = ArgumentHelper.getInt(args.get(1));
            int length = (int) Math.sqrt(x * x + y * y);
            return MethodResult.setsAndReturnsValue(
                    new IntegerVariable(thisVar.name, length, thisVar.signals)
            );
        }),

        Map.entry("MOD", (self, args) -> {
            IntegerVariable thisVar = (IntegerVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("MOD requires 1 argument");
            }

            int divisor = ArgumentHelper.getInt(args.get(0));
            if (divisor == 0) {
                return MethodResult.noChange(thisVar.value());
            }
            return MethodResult.setsAndReturnsValue(
                    new IntegerVariable(thisVar.name, thisVar.intValue % divisor, thisVar.signals)
            );
        }),

        Map.entry("MUL", (self, args) -> {
            IntegerVariable thisVar = (IntegerVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("MUL requires 1 argument");
            }

            int multiplier = ArgumentHelper.getInt(args.get(0));
            return MethodResult.setsAndReturnsValue(
                    new IntegerVariable(thisVar.name, thisVar.intValue * multiplier, thisVar.signals)
            );
        }),

        Map.entry("NOT", (self, args) -> {
            IntegerVariable thisVar = (IntegerVariable) self;
            return MethodResult.setsAndReturnsValue(
                    new IntegerVariable(thisVar.name, ~thisVar.intValue, thisVar.signals)
            );
        }),

        Map.entry("OR", (self, args) -> {
            IntegerVariable thisVar = (IntegerVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("OR requires 1 argument");
            }

            int value = ArgumentHelper.getInt(args.get(0));
            return MethodResult.setsAndReturnsValue(
                    new IntegerVariable(thisVar.name, thisVar.intValue | value, thisVar.signals)
            );
        }),

        Map.entry("POWER", (self, args) -> {
            IntegerVariable thisVar = (IntegerVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("POWER requires 1 argument");
            }

            int power = ArgumentHelper.getInt(args.get(0));
            double result = Math.pow(thisVar.intValue, power);
            int value = 0;
            if (result > 0) {
                value = (int) Math.round(result);
            } else {
                value = (int) Math.ceil(result - 0.5);
            }
            return MethodResult.setsAndReturnsValue(
                    new IntegerVariable(thisVar.name, value, thisVar.signals)
            );
        }),

        Map.entry("RANDOM", (self, args) -> {
            IntegerVariable thisVar = (IntegerVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("RANDOM requires 1 or 2 arguments");
            }

            int result = 0;
            if(args.size() == 1) {
                Random random = new Random();
                int bound = ArgumentHelper.getInt(args.get(0));
                result = random.nextInt(bound);
            } else {
                int min = ArgumentHelper.getInt(args.get(0));
                int max = ArgumentHelper.getInt(args.get(1));
                Random random = new Random();
                result = min + random.nextInt(max - min + 1);
            }
            return MethodResult.setsAndReturnsValue(
                    new IntegerVariable(thisVar.name, result, thisVar.signals)
            );
        }),

        Map.entry("RESETINI", (self, args) -> {
            IntegerVariable thisVar = (IntegerVariable) self;

            // TODO: Get DEFAULT value from INI file

            return MethodResult.setsAndReturnsValue(
                    new IntegerVariable(thisVar.name, thisVar.intValue(), thisVar.signals)
            );
        }),

        Map.entry("SET", (self, args) -> {
            IntegerVariable thisVar = (IntegerVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("SET requires 1 argument");
            }

            int newValue = ArgumentHelper.getInt(args.get(0));
            return MethodResult.setsAndReturnsValue(
                    new IntegerVariable(thisVar.name, newValue, thisVar.signals)
            );
        }),

        Map.entry("SUB", (self, args) -> {
            IntegerVariable thisVar = (IntegerVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("SUB requires 1 argument");
            }

            int subtrahend = ArgumentHelper.getInt(args.get(0));
            return MethodResult.setsAndReturnsValue(
                    new IntegerVariable(thisVar.name, thisVar.intValue - subtrahend, thisVar.signals)
            );
        }),

        Map.entry("SWITCH", (self, args) -> {
            IntegerVariable thisVar = (IntegerVariable) self;
            if (args.size() < 2) {
                throw new IllegalArgumentException("SWITCH requires 2 argument");
            }

            int valueA = ArgumentHelper.getInt(args.get(0));
            int valueB = ArgumentHelper.getInt(args.get(1));
            int result = (thisVar.intValue == valueA) ? valueB : valueA;
            return MethodResult.setsAndReturnsValue(
                    new IntegerVariable(thisVar.name, result, thisVar.signals)
            );
        }),

        Map.entry("XOR", (self, args) -> {
            IntegerVariable thisVar = (IntegerVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("XOR requires 1 argument");
            }

            int value = ArgumentHelper.getInt(args.get(0));
            return MethodResult.setsAndReturnsValue(
                    new IntegerVariable(thisVar.name, thisVar.intValue ^ value, thisVar.signals)
            );
        })
    );

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

    @Override
    public List<Variable> getClones() {
        return List.of();
    }

    @Override
    public Variable withAddedClone(Variable clone) {
        return null;
    }
}
