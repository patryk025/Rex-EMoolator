package pl.genschu.bloomooemulator.interpreter.variable;

import pl.genschu.bloomooemulator.annotations.InternalMutable;
import pl.genschu.bloomooemulator.interpreter.helpers.ArgumentHelper;
import pl.genschu.bloomooemulator.interpreter.values.*;
import pl.genschu.bloomooemulator.utils.FileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * StringVariable holds text value
 **/
public record StringVariable(
    String name,
    @InternalMutable MutableValue holder,
    Map<String, SignalHandler> signals
) implements Variable {

    public StringVariable {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Variable name cannot be null or empty");
        }
        if (holder == null) {
            holder = new MutableValue(new StringValue(""));
        }
        if (signals == null) {
            signals = Map.of();
        } else {
            signals = Map.copyOf(signals);
        }
    }

    // Convenience constructors
    public StringVariable(String name, String stringValue) {
        this(name, new MutableValue(new StringValue(stringValue != null ? stringValue : "")), Map.of());
    }

    public StringVariable(String name, String stringValue, Map<String, SignalHandler> signals) {
        this(name, new MutableValue(new StringValue(stringValue != null ? stringValue : "")), signals);
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
        return VariableType.STRING;
    }

    @Override
    public Variable withValue(Value newValue) {
        String newString = ArgumentHelper.getString(newValue);
        setValue(new StringValue(newString));
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
        return new StringVariable(name, holder, newSignals);
    }

    // ========================================
    // CONVENIENT ACCESSORS
    // ========================================

    public String getString() {
        return ((StringValue) holder.get()).value();
    }

    public String get() {
        return getString();
    }

    /**
     * Sets the value and emits appropriate signals.
     * Always emits ONBRUTALCHANGED, emits ONCHANGED only if value actually changed.
     */
    public void setValue(StringValue newValue) {
        String oldVal = getString();
        String newVal = newValue.value();
        holder.set(newValue);

        // Always emit ONBRUTALCHANGED
        emitSignal("ONBRUTALCHANGED", newValue);

        // Emit ONCHANGED only if value actually changed
        if (!oldVal.equals(newVal)) {
            emitSignal("ONCHANGED", newValue);
        }
    }

    // ========================================
    // METHODS DEFINITION
    // ========================================

    private static final Map<String, MethodSpec> METHODS = Map.ofEntries(
        Map.entry("ADD", MethodSpec.of((self, args, ctx) -> {
            StringVariable thisVar = (StringVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("ADD requires 1 argument");
            }
            String addition = ArgumentHelper.getString(args.get(0));
            StringValue result = new StringValue(thisVar.getString() + addition);
            thisVar.setValue(result);
            return MethodResult.returns(result);
        })),

        Map.entry("CHANGEAT", MethodSpec.of((self, args, ctx) -> {
            StringVariable thisVar = (StringVariable) self;
            if (args.size() < 2) {
                throw new IllegalArgumentException("CHANGEAT requires 2 arguments");
            }
            int index = ArgumentHelper.getInt(args.get(0));
            String replacement = ArgumentHelper.getString(args.get(1));
            String value = thisVar.getString();
            if (index < 0 || index >= value.length()) {
                return MethodResult.returns(thisVar.value());
            }
            String newValue = value.substring(0, index) + replacement + value.substring(index + 1);
            StringValue result = new StringValue(newValue);
            thisVar.setValue(result);
            return MethodResult.returns(result);
        })),

        Map.entry("COPYFILE", MethodSpec.of((self, args, ctx) -> {
            if (args.size() < 2) {
                throw new IllegalArgumentException("COPYFILE requires 2 arguments");
            }

            Path sourcePath = resolvePath(ctx, ArgumentHelper.getString(args.get(0)));
            Path destinationPath = resolvePath(ctx, ArgumentHelper.getString(args.get(1)));

            if (!Files.exists(sourcePath)) {
                return MethodResult.returns(BoolValue.FALSE);
            }

            try {
                Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
                return MethodResult.returns(BoolValue.TRUE);
            } catch (IOException e) {
                return MethodResult.returns(BoolValue.FALSE);
            }
        })),

        Map.entry("CUT", MethodSpec.of((self, args, ctx) -> {
            StringVariable thisVar = (StringVariable) self;
            if (args.size() < 2) {
                throw new IllegalArgumentException("CUT requires 2 arguments");
            }
            int index = ArgumentHelper.getInt(args.get(0));
            int length = ArgumentHelper.getInt(args.get(1));
            String value = thisVar.getString();
            if (index < 0 || index >= value.length()) {
                StringValue result = new StringValue("");
                thisVar.setValue(result);
                return MethodResult.returns(result);
            }
            int endIndex = Math.min(index + length, value.length());
            StringValue result = new StringValue(value.substring(index, endIndex));
            thisVar.setValue(result);
            return MethodResult.returns(result);
        })),

        Map.entry("FIND", MethodSpec.of((self, args, ctx) -> {
            StringVariable thisVar = (StringVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("FIND requires at least 1 argument");
            }
            String needle = ArgumentHelper.getString(args.get(0));
            int offset = args.size() > 1 ? ArgumentHelper.getInt(args.get(1)) : 0;
            int index = thisVar.getString().indexOf(needle, offset);
            return MethodResult.returns(new IntValue(index));
        })),

        Map.entry("GET", MethodSpec.of((self, args, ctx) -> {
            StringVariable thisVar = (StringVariable) self;
            if (args.isEmpty()) {
                return MethodResult.returns(self.value());
            }
            int index = ArgumentHelper.getInt(args.get(0));
            int length = args.size() >= 2 ? ArgumentHelper.getInt(args.get(1)) : 1;
            String value = thisVar.getString();
            if (index < 0 || index >= value.length()) {
                return MethodResult.returns(new StringValue(""));
            }
            int endIndex = Math.min(index + length, value.length());
            return MethodResult.returns(new StringValue(value.substring(index, endIndex)));
        })),

        Map.entry("LENGTH", MethodSpec.of((self, args, ctx) -> {
            StringVariable thisVar = (StringVariable) self;
            return MethodResult.returns(new IntValue(thisVar.getString().length()));
        })),

        Map.entry("REPLACEAT", MethodSpec.of((self, args, ctx) -> {
            StringVariable thisVar = (StringVariable) self;
            if (args.size() < 3) {
                throw new IllegalArgumentException("REPLACEAT requires 3 arguments");
            }
            int index = ArgumentHelper.getInt(args.get(0));
            int length = ArgumentHelper.getInt(args.get(1));
            String replacement = ArgumentHelper.getString(args.get(2));
            String value = thisVar.getString();
            if (index < 0 || index > value.length()) {
                return MethodResult.returns(thisVar.value());
            }
            int endIndex = Math.min(index + length, value.length());
            String newValue = value.substring(0, index) + replacement + value.substring(endIndex);
            StringValue result = new StringValue(newValue);
            thisVar.setValue(result);
            return MethodResult.returns(result);
        })),

        Map.entry("RESETINI", MethodSpec.of((self, args, ctx) -> {
            StringVariable thisVar = (StringVariable) self;
            String resetValue = self.getResetAttributeValue(ctx);
            StringValue result = new StringValue(resetValue != null ? resetValue : "");
            thisVar.setValue(result);
            return MethodResult.returns(result);
        })),

        Map.entry("SET", MethodSpec.of((self, args, ctx) -> {
            StringVariable thisVar = (StringVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("SET requires 1 argument");
            }
            String newValue = ArgumentHelper.getString(args.get(0));
            StringValue result = new StringValue(newValue);
            thisVar.setValue(result);
            return MethodResult.returns(result);
        })),

        Map.entry("SUB", MethodSpec.of((self, args, ctx) -> {
            StringVariable thisVar = (StringVariable) self;
            if (args.size() < 2) {
                throw new IllegalArgumentException("SUB requires 2 arguments");
            }
            int index = ArgumentHelper.getInt(args.get(0));
            int length = ArgumentHelper.getInt(args.get(1));
            String value = thisVar.getString();
            if (index < 0 || index > value.length()) {
                return MethodResult.returns(thisVar.value());
            }
            int endIndex = Math.min(index + length, value.length());
            String newValue = value.substring(0, index) + value.substring(endIndex);
            StringValue result = new StringValue(newValue);
            thisVar.setValue(result);
            return MethodResult.returns(result);
        })),

        Map.entry("UPPER", MethodSpec.of((self, args, ctx) -> {
            StringVariable thisVar = (StringVariable) self;
            StringValue result = new StringValue(thisVar.getString().toUpperCase());
            thisVar.setValue(result);
            return MethodResult.returns(result);
        })),

        Map.entry("LOWER", MethodSpec.of((self, args, ctx) -> {
            StringVariable thisVar = (StringVariable) self;
            StringValue result = new StringValue(thisVar.getString().toLowerCase());
            thisVar.setValue(result);
            return MethodResult.returns(result);
        }))
    );

    @Override
    public String toString() {
        return "StringVariable[" + name + "=\"" + getString() + "\"]";
    }

    private static Path resolvePath(MethodContext ctx, String rawPath) {
        if (ctx != null && ctx.getGame() != null) {
            return Path.of(FileUtils.resolveRelativePath(ctx.getGame(), rawPath));
        }

        String normalized = rawPath;
        if (normalized.startsWith("$")) {
            normalized = normalized.substring(1).replaceFirst("^[/\\\\]+", "");
        }

        Path path = Path.of(normalized);
        return path.isAbsolute() ? path.normalize() : path.toAbsolutePath().normalize();
    }

    @Override
    public Variable copyAs(String newName) {
        return new StringVariable(newName, this.getString(), this.signals);
    }
}
