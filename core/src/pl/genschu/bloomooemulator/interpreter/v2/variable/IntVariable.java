package pl.genschu.bloomooemulator.interpreter.v2.variable;

import pl.genschu.bloomooemulator.interpreter.v2.values.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * IntVariable - SZABLON dla innych typów!
 *
 * Pokaże Ci wzorzec, który możesz replikować dla:
 * - DoubleVariable
 * - StringVariable
 * - BoolVariable
 * - ArrayVariable
 * - etc.
 *
 * Key points:
 * 1. Record = immutable automatycznie
 * 2. Metody zdefiniowane w METHODS map
 * 3. withValue() tworzy NOWĄ instancję
 * 4. callMethod() deleguje do METHODS
 * 5. Signals przechowywane w Map
 */
public record IntVariable(
    String name,
    int intValue,  // Primitive value stored directly!
    Map<String, SignalHandler> signals
) implements Variable {

    // Compact constructor - validation
    public IntVariable {
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
    public IntVariable(String name, int intValue) {
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

        // Return NEW instance (immutable!)
        return new IntVariable(name, newInt, signals);
    }

    @Override
    public Variable callMethod(String methodName, List<Value> arguments) {
        VariableMethod method = METHODS.get(methodName.toUpperCase());

        if (method == null) {
            throw new IllegalArgumentException(
                "Method not found: " + methodName + " on INTEGER variable"
            );
        }

        return method.execute(this, arguments);
    }

    @Override
    public Map<String, VariableMethod> methods() {
        return METHODS;
    }

    @Override
    public Variable withSignal(String signalName, SignalHandler handler) {
        Map<String, SignalHandler> newSignals = new HashMap<>(signals);
        newSignals.put(signalName, handler);
        return new IntVariable(name, intValue, newSignals);
    }

    @Override
    public void emitSignal(String signalName, Value argument) {
        SignalHandler handler = signals.get(signalName);
        if (handler != null) {
            handler.handle(this, signalName, argument);
        }
    }

    // ========================================
    // METHODS DEFINITION
    // ========================================

    /**
     * All methods available on IntVariable.
     *
     * Pattern: method name -> lambda implementation
     *
     * To replikuj dla innych typów!
     */
    private static final Map<String, VariableMethod> METHODS = Map.ofEntries(
        // ADD(value) - dodaj wartość
        Map.entry("ADD", (self, args) -> {
            IntVariable thisVar = (IntVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("ADD requires 1 argument");
            }

            int addend = toInt(args.get(0));
            return new IntVariable(thisVar.name, thisVar.intValue + addend, thisVar.signals);
        }),

        // SUB(value) - odejmij wartość
        Map.entry("SUB", (self, args) -> {
            IntVariable thisVar = (IntVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("SUB requires 1 argument");
            }

            int subtrahend = toInt(args.get(0));
            return new IntVariable(thisVar.name, thisVar.intValue - subtrahend, thisVar.signals);
        }),

        // MUL(value) - pomnóż przez wartość
        Map.entry("MUL", (self, args) -> {
            IntVariable thisVar = (IntVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("MUL requires 1 argument");
            }

            int multiplier = toInt(args.get(0));
            return new IntVariable(thisVar.name, thisVar.intValue * multiplier, thisVar.signals);
        }),

        // DIV(value) - podziel przez wartość
        Map.entry("DIV", (self, args) -> {
            IntVariable thisVar = (IntVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("DIV requires 1 argument");
            }

            int divisor = toInt(args.get(0));
            if (divisor == 0) {
                return thisVar;  // Division by zero = no change
            }
            return new IntVariable(thisVar.name, thisVar.intValue / divisor, thisVar.signals);
        }),

        // MOD(value) - modulo
        Map.entry("MOD", (self, args) -> {
            IntVariable thisVar = (IntVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("MOD requires 1 argument");
            }

            int divisor = toInt(args.get(0));
            if (divisor == 0) {
                return thisVar;
            }
            return new IntVariable(thisVar.name, thisVar.intValue % divisor, thisVar.signals);
        }),

        // SET(value) - ustaw wartość
        Map.entry("SET", (self, args) -> {
            IntVariable thisVar = (IntVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("SET requires 1 argument");
            }

            int newValue = toInt(args.get(0));
            return new IntVariable(thisVar.name, newValue, thisVar.signals);
        }),

        // GET() - pobierz wartość (zwraca siebie)
        Map.entry("GET", (self, args) -> self),

        // INC() - inkrementuj
        Map.entry("INC", (self, args) -> {
            IntVariable thisVar = (IntVariable) self;
            return new IntVariable(thisVar.name, thisVar.intValue + 1, thisVar.signals);
        }),

        // DEC() - dekrementuj
        Map.entry("DEC", (self, args) -> {
            IntVariable thisVar = (IntVariable) self;
            return new IntVariable(thisVar.name, thisVar.intValue - 1, thisVar.signals);
        }),

        // ABS() - wartość bezwzględna
        Map.entry("ABS", (self, args) -> {
            IntVariable thisVar = (IntVariable) self;
            return new IntVariable(thisVar.name, Math.abs(thisVar.intValue), thisVar.signals);
        }),

        // NEG() - negacja
        Map.entry("NEG", (self, args) -> {
            IntVariable thisVar = (IntVariable) self;
            return new IntVariable(thisVar.name, -thisVar.intValue, thisVar.signals);
        }),

        // MIN(value) - minimum z dwóch wartości
        Map.entry("MIN", (self, args) -> {
            IntVariable thisVar = (IntVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("MIN requires 1 argument");
            }

            int other = toInt(args.get(0));
            return new IntVariable(thisVar.name, Math.min(thisVar.intValue, other), thisVar.signals);
        }),

        // MAX(value) - maksimum z dwóch wartości
        Map.entry("MAX", (self, args) -> {
            IntVariable thisVar = (IntVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("MAX requires 1 argument");
            }

            int other = toInt(args.get(0));
            return new IntVariable(thisVar.name, Math.max(thisVar.intValue, other), thisVar.signals);
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
