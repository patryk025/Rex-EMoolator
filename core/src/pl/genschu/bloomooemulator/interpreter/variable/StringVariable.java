package pl.genschu.bloomooemulator.interpreter.variable;

import pl.genschu.bloomooemulator.interpreter.helpers.ArgumentHelper;
import pl.genschu.bloomooemulator.interpreter.values.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * StringVariable holds text value
 **/
public record StringVariable(
    String name,
    String stringValue,
    Map<String, SignalHandler> signals
) implements Variable {

    public StringVariable {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Variable name cannot be null or empty");
        }
        if (stringValue == null) {
            stringValue = "";
        }
        if (signals == null) {
            signals = Map.of();
        } else {
            signals = Map.copyOf(signals);
        }
    }

    public StringVariable(String name, String stringValue) {
        this(name, stringValue, Map.of());
    }

    // ========================================
    // INTERFACE IMPLEMENTATION
    // ========================================

    @Override
    public Value value() {
        return new StringValue(stringValue);
    }

    @Override
    public VariableType type() {
        return VariableType.STRING;
    }

    @Override
    public Variable withValue(Value newValue) {
        String newString = ArgumentHelper.getString(newValue);

        return new StringVariable(name, newString, signals);
    }

    @Override
    public Map<String, VariableMethod> methods() {
        return METHODS;
    }

    @Override
    public Variable withSignal(String signalName, SignalHandler handler) {
        Map<String, SignalHandler> newSignals = new HashMap<>(signals);
        newSignals.put(signalName, handler);
        return new StringVariable(name, stringValue, newSignals);
    }

    // ========================================
    // METHODS DEFINITION
    // ========================================

    private static final Map<String, VariableMethod> METHODS = Map.ofEntries(
        Map.entry("ADD", (self, args) -> {
            StringVariable thisVar = (StringVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("ADD requires 1 argument");
            }

            String addition = ArgumentHelper.getString(args.get(0));
            return MethodResult.setsAndReturnsValue(
                    new StringVariable(thisVar.name, thisVar.stringValue + addition, thisVar.signals)
            );
        }),

        // TODO: Implement COPYFILE method if file system access is available

        Map.entry("CHANGEAT", (self, args) -> {
            StringVariable thisVar = (StringVariable) self;
            if (args.size() < 2) {
                throw new IllegalArgumentException("CHANGEAT requires 2 arguments");
            }

            int index = ArgumentHelper.getInt(args.get(0));
            String replacement = ArgumentHelper.getString(args.get(1));
            String value = thisVar.stringValue;

            if (index < 0 || index >= value.length()) {
                return MethodResult.noChange(thisVar.value());
            }

            String newValue = value.substring(0, index) + replacement + value.substring(index + 1);
            return MethodResult.setsAndReturnsValue(
                    new StringVariable(thisVar.name, newValue, thisVar.signals)
            );
        }),

        Map.entry("CUT", (self, args) -> {
            StringVariable thisVar = (StringVariable) self;
            if (args.size() < 2) {
                throw new IllegalArgumentException("CUT requires 2 arguments");
            }

            int index = ArgumentHelper.getInt(args.get(0));
            int length = ArgumentHelper.getInt(args.get(1));
            String value = thisVar.stringValue;

            if (index < 0 || index >= value.length()) {
                return MethodResult.setsAndReturnsValue(
                        new StringVariable(thisVar.name, "", thisVar.signals)
                );
            }

            int endIndex = Math.min(index + length, value.length());
            String newValue = value.substring(index, endIndex);
            return MethodResult.setsAndReturnsValue(
                    new StringVariable(thisVar.name, newValue, thisVar.signals)
            );
        }),

        Map.entry("FIND", (self, args) -> {
            StringVariable thisVar = (StringVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("FIND requires at least 1 argument");
            }

            String needle = ArgumentHelper.getString(args.get(0));
            int offset = args.size() > 1 ? ArgumentHelper.getInt(args.get(1)) : 0;
            int index = thisVar.stringValue.indexOf(needle, offset);

            return MethodResult.noChange(new IntValue(index));
        }),

        Map.entry("GET", (self, args) -> {
            StringVariable thisVar = (StringVariable) self;
            if (args.isEmpty()) {
                return MethodResult.noChange(self.value());
            }
            // GET(index) or GET(index, length)
            int index = ArgumentHelper.getInt(args.get(0));
            int length = args.size() >= 2 ? ArgumentHelper.getInt(args.get(1)) : 1;

            String value = thisVar.stringValue;
            if (index < 0 || index >= value.length()) {
                return MethodResult.noChange(new StringValue(""));
            }
            int endIndex = Math.min(index + length, value.length());
            return MethodResult.noChange(new StringValue(value.substring(index, endIndex)));
        }),

        Map.entry("LENGTH", (self, args) -> {
            StringVariable thisVar = (StringVariable) self;
            return MethodResult.noChange(new IntValue(thisVar.stringValue.length()));
        }),

        Map.entry("REPLACEAT", (self, args) -> {
            StringVariable thisVar = (StringVariable) self;
            if (args.size() < 3) {
                throw new IllegalArgumentException("REPLACEAT requires 3 arguments");
            }

            int index = ArgumentHelper.getInt(args.get(0));
            int length = ArgumentHelper.getInt(args.get(1));
            String replacement = ArgumentHelper.getString(args.get(2));
            String value = thisVar.stringValue;

            if (index < 0 || index > value.length()) {
                return MethodResult.noChange(thisVar.value());
            }

            int endIndex = Math.min(index + length, value.length());
            String newValue = value.substring(0, index) + replacement + value.substring(endIndex);
            return MethodResult.setsAndReturnsValue(
                    new StringVariable(thisVar.name, newValue, thisVar.signals)
            );
        }),

        Map.entry("RESETINI", (self, args) -> {
            StringVariable thisVar = (StringVariable) self;
            // TODO: Get DEFAULT value from INI file
            return MethodResult.setsAndReturnsValue(
                    new StringVariable(thisVar.name, thisVar.stringValue, thisVar.signals)
            );
        }),

        Map.entry("SET", (self, args) -> {
            StringVariable thisVar = (StringVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("SET requires 1 argument");
            }

            String newValue = ArgumentHelper.getString(args.get(0));
            return MethodResult.setsAndReturnsValue(
                    new StringVariable(thisVar.name, newValue, thisVar.signals)
            );
        }),

        Map.entry("SUB", (self, args) -> {
            StringVariable thisVar = (StringVariable) self;
            if (args.size() < 2) {
                throw new IllegalArgumentException("SUB requires 2 arguments");
            }

            int index = ArgumentHelper.getInt(args.get(0));
            int length = ArgumentHelper.getInt(args.get(1));
            String value = thisVar.stringValue;

            if (index < 0 || index > value.length()) {
                return MethodResult.noChange(thisVar.value());
            }

            int endIndex = Math.min(index + length, value.length());
            String newValue = value.substring(0, index) + value.substring(endIndex);
            return MethodResult.setsAndReturnsValue(
                    new StringVariable(thisVar.name, newValue, thisVar.signals)
            );
        }),

        Map.entry("UPPER", (self, args) -> {
            StringVariable thisVar = (StringVariable) self;
            return MethodResult.setsAndReturnsValue(
                    new StringVariable(thisVar.name, thisVar.stringValue.toUpperCase(), thisVar.signals)
            );
        }),

        Map.entry("LOWER", (self, args) -> {
            StringVariable thisVar = (StringVariable) self;
            return MethodResult.setsAndReturnsValue(
                    new StringVariable(thisVar.name, thisVar.stringValue.toLowerCase(), thisVar.signals)
            );
        })
    );

    // ========================================
    // CONVENIENT ACCESSORS
    // ========================================

    /**
     * Gets the string value directly.
     * Convenience method to avoid value().unwrap().
     */
    public String get() {
        return stringValue;
    }

    @Override
    public String toString() {
        return "StringVariable[" + name + "=\"" + stringValue + "\"]";
    }

}
