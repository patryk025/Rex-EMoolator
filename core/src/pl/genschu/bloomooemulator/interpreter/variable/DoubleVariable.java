package pl.genschu.bloomooemulator.interpreter.variable;

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
    double doubleValue,
    Map<String, SignalHandler> signals
) implements Variable {

    public DoubleVariable {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Variable name cannot be null or empty");
        }
        if (signals == null) {
            signals = Map.of();
        } else {
            signals = Map.copyOf(signals);
        }
    }

    public DoubleVariable(String name, double doubleValue) {
        this(name, doubleValue, Map.of());
    }

    // ========================================
    // INTERFACE IMPLEMENTATION
    // ========================================

    @Override
    public Value value() {
        return new DoubleValue(doubleValue);
    }

    @Override
    public VariableType type() {
        return VariableType.DOUBLE;
    }

    @Override
    public Variable withValue(Value newValue) {
        double newDouble = ArgumentHelper.getDouble(newValue);

        return new DoubleVariable(name, newDouble, signals);
    }

    @Override
    public Map<String, VariableMethod> methods() {
        return METHODS;
    }

    @Override
    public Variable withSignal(String signalName, SignalHandler handler) {
        Map<String, SignalHandler> newSignals = new HashMap<>(signals);
        newSignals.put(signalName, handler);
        return new DoubleVariable(name, doubleValue, newSignals);
    }

    // ========================================
    // METHODS DEFINITION
    // ========================================

    private static final Map<String, VariableMethod> METHODS = Map.ofEntries(
        Map.entry("ABS", (self, args) -> {
            DoubleVariable thisVar = (DoubleVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("ABS requires 1 argument");
            }

            double value = ArgumentHelper.getDouble(args.get(0));
            return MethodResult.setsAndReturnsValue(
                    new DoubleVariable(thisVar.name, Math.abs(value), thisVar.signals)
            );
        }),

        Map.entry("ADD", (self, args) -> {
            DoubleVariable thisVar = (DoubleVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("ADD requires 1 argument");
            }

            double addend = ArgumentHelper.getDouble(args.get(0));
            return MethodResult.setsAndReturnsValue(
                    new DoubleVariable(thisVar.name, thisVar.doubleValue + addend, thisVar.signals)
            );
        }),

        Map.entry("ARCTAN", (self, args) -> {
            DoubleVariable thisVar = (DoubleVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("ARCTAN requires 1 argument");
            }

            double angle = ArgumentHelper.getDouble(args.get(0));
            double atanRadians = Math.atan(angle);
            double atanDegrees = Math.toDegrees(atanRadians);

            return MethodResult.setsAndReturnsValue(
                    new DoubleVariable(thisVar.name, atanDegrees, thisVar.signals)
            );
        }),

        Map.entry("ARCTANEX", (self, args) -> {
            DoubleVariable thisVar = (DoubleVariable) self;
            if (args.size() < 2) {
                throw new IllegalArgumentException("ARCTANEX requires 2 arguments");
            }

            double y = ArgumentHelper.getDouble(args.get(0));
            double x = ArgumentHelper.getDouble(args.get(1));
            double atanRadians = Math.atan2(y, x);
            double atanDegrees = Math.toDegrees(atanRadians);

            return MethodResult.setsAndReturnsValue(
                    new DoubleVariable(thisVar.name, atanDegrees, thisVar.signals)
            );
        }),

        Map.entry("CLAMP", (self, args) -> {
            DoubleVariable thisVar = (DoubleVariable) self;
            if (args.size() < 2) {
                throw new IllegalArgumentException("CLAMP requires 2 arguments");
            }

            double rangeMin = ArgumentHelper.getDouble(args.get(0));
            double rangeMax = ArgumentHelper.getDouble(args.get(1));
            double clampedValue = Math.max(rangeMin, Math.min(rangeMax, thisVar.doubleValue));

            return MethodResult.setsAndReturnsValue(
                    new DoubleVariable(thisVar.name, clampedValue, thisVar.signals)
            );
        }),

        Map.entry("CLEAR", (self, args) -> {
            DoubleVariable thisVar = (DoubleVariable) self;
            return MethodResult.setsAndReturnsValue(
                    new DoubleVariable(thisVar.name, 0.0, thisVar.signals)
            );
        }),

        Map.entry("COSINUS", (self, args) -> {
            DoubleVariable thisVar = (DoubleVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("COSINUS requires 1 argument");
            }

            double angle = ArgumentHelper.getDouble(args.get(0));
            double cosValue = Math.cos(Math.toRadians(angle));

            return MethodResult.setsAndReturnsValue(
                    new DoubleVariable(thisVar.name, cosValue, thisVar.signals)
            );
        }),

        Map.entry("DEC", (self, args) -> {
            DoubleVariable thisVar = (DoubleVariable) self;
            return MethodResult.setsAndReturnsValue(
                    new DoubleVariable(thisVar.name, thisVar.doubleValue - 1.0, thisVar.signals)
            );
        }),

        Map.entry("DIV", (self, args) -> {
            DoubleVariable thisVar = (DoubleVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("DIV requires 1 argument");
            }

            double divisor = ArgumentHelper.getDouble(args.get(0));
            if (divisor == 0.0) {
                return MethodResult.noChange(thisVar.value());  // Division by zero = no change
            }
            return MethodResult.setsAndReturnsValue(
                    new DoubleVariable(thisVar.name, thisVar.doubleValue / divisor, thisVar.signals)
            );
        }),

        Map.entry("GET", (self, args) -> MethodResult.noChange(self.value())),

        Map.entry("INC", (self, args) -> {
            DoubleVariable thisVar = (DoubleVariable) self;
            return MethodResult.setsAndReturnsValue(
                    new DoubleVariable(thisVar.name, thisVar.doubleValue + 1.0, thisVar.signals)
            );
        }),

        Map.entry("LENGTH", (self, args) -> {
            DoubleVariable thisVar = (DoubleVariable) self;
            if (args.size() < 2) {
                throw new IllegalArgumentException("LENGTH requires 2 arguments");
            }

            double x = ArgumentHelper.getDouble(args.get(0));
            double y = ArgumentHelper.getDouble(args.get(1));
            double length = Math.sqrt(x * x + y * y);

            return MethodResult.setsAndReturnsValue(
                    new DoubleVariable(thisVar.name, length, thisVar.signals)
            );
        }),

        Map.entry("LOG", (self, args) -> {
            DoubleVariable thisVar = (DoubleVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("LOG requires 1 argument");
            }

            double value = ArgumentHelper.getDouble(args.get(0));
            return MethodResult.setsAndReturnsValue(
                    new DoubleVariable(thisVar.name, Math.log(value), thisVar.signals)
            );
        }),

        Map.entry("MOD", (self, args) -> {
            DoubleVariable thisVar = (DoubleVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("MOD requires 1 argument");
            }

            double divisor = ArgumentHelper.getDouble(args.get(0));
            if (divisor == 0.0) {
                return MethodResult.noChange(thisVar.value());
            }
            // Modulo cuts off decimal part in original engine
            double result = (int) (thisVar.doubleValue % divisor);
            return MethodResult.setsAndReturnsValue(
                    new DoubleVariable(thisVar.name, result, thisVar.signals)
            );
        }),

        Map.entry("MAXA", (self, args) -> {
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

            return MethodResult.setsAndReturnsValue(
                    new DoubleVariable(thisVar.name, max, thisVar.signals)
            );
        }),

        Map.entry("MINA", (self, args) -> {
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

            return MethodResult.setsAndReturnsValue(
                    new DoubleVariable(thisVar.name, min, thisVar.signals)
            );
        }),

        Map.entry("MUL", (self, args) -> {
            DoubleVariable thisVar = (DoubleVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("MUL requires 1 argument");
            }

            double multiplier = ArgumentHelper.getDouble(args.get(0));
            return MethodResult.setsAndReturnsValue(
                    new DoubleVariable(thisVar.name, thisVar.doubleValue * multiplier, thisVar.signals)
            );
        }),

        Map.entry("RESETINI", (self, args) -> {
            DoubleVariable thisVar = (DoubleVariable) self;
            // TODO: Get DEFAULT value from INI file
            return MethodResult.setsAndReturnsValue(
                    new DoubleVariable(thisVar.name, thisVar.doubleValue, thisVar.signals)
            );
        }),

        Map.entry("SET", (self, args) -> {
            DoubleVariable thisVar = (DoubleVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("SET requires 1 argument");
            }

            double newValue = ArgumentHelper.getDouble(args.get(0));
            return MethodResult.setsAndReturnsValue(
                    new DoubleVariable(thisVar.name, newValue, thisVar.signals)
            );
        }),

        Map.entry("SGN", (self, args) -> {
            DoubleVariable thisVar = (DoubleVariable) self;
            int sgn = 0;
            if (thisVar.doubleValue > 0) {
                sgn = 1;
            } else if (thisVar.doubleValue < 0) {
                sgn = -1;
            }
            // SGN returns int, does not change the variable
            return MethodResult.noChange(new IntValue(sgn));
        }),

        Map.entry("SINUS", (self, args) -> {
            DoubleVariable thisVar = (DoubleVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("SINUS requires 1 argument");
            }

            double angle = ArgumentHelper.getDouble(args.get(0));
            double sinValue = Math.sin(Math.toRadians(angle));

            return MethodResult.setsAndReturnsValue(
                    new DoubleVariable(thisVar.name, sinValue, thisVar.signals)
            );
        }),

        Map.entry("SQRT", (self, args) -> {
            DoubleVariable thisVar = (DoubleVariable) self;
            double value = args.isEmpty() ? thisVar.doubleValue : ArgumentHelper.getDouble(args.get(0));
            return MethodResult.setsAndReturnsValue(
                    new DoubleVariable(thisVar.name, Math.sqrt(value), thisVar.signals)
            );
        }),

        Map.entry("SUB", (self, args) -> {
            DoubleVariable thisVar = (DoubleVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("SUB requires 1 argument");
            }

            double subtrahend = ArgumentHelper.getDouble(args.get(0));
            return MethodResult.setsAndReturnsValue(
                    new DoubleVariable(thisVar.name, thisVar.doubleValue - subtrahend, thisVar.signals)
            );
        }),

        Map.entry("SWITCH", (self, args) -> {
            DoubleVariable thisVar = (DoubleVariable) self;
            if (args.size() < 2) {
                throw new IllegalArgumentException("SWITCH requires 2 arguments");
            }

            double valueA = ArgumentHelper.getDouble(args.get(0));
            double valueB = ArgumentHelper.getDouble(args.get(1));
            double result = (thisVar.doubleValue == valueA) ? valueB : valueA;

            return MethodResult.setsAndReturnsValue(
                    new DoubleVariable(thisVar.name, result, thisVar.signals)
            );
        })
    );

    // ========================================
    // CONVENIENT ACCESSORS
    // ========================================

    /**
     * Gets the double value directly.
     * Convenience method to avoid value().unwrap().
     */
    public double get() {
        return doubleValue;
    }

    @Override
    public String toString() {
        return "DoubleVariable[" + name + "=" + doubleValue + "]";
    }

}
