package pl.genschu.bloomooemulator.interpreter.variable;

import pl.genschu.bloomooemulator.annotations.InternalMutable;
import pl.genschu.bloomooemulator.interpreter.helpers.ArgumentHelper;
import pl.genschu.bloomooemulator.interpreter.values.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * StructVariable represents a struct with named, typed fields.
 * Used for defining database row schemas and accessing field values.
 */
public record StructVariable(
    String name,
    List<String> fields,    // field names (e.g., "NAME", "AGE", "SCORE")
    List<String> types,     // field types (e.g., "STRING", "INTEGER", "DOUBLE")
    @InternalMutable
    List<Value> values      // field values (mutable)
) implements Variable {

    public StructVariable(String name, List<String> fields, List<String> types, List<Value> values) {
        this.name = name;
        this.fields = fields != null ? List.copyOf(fields) : List.of();
        this.types = types != null ? List.copyOf(types) : List.of();
        this.values = values != null ? new ArrayList<>(values) : new ArrayList<>();
    }

    public static StructVariable withSchema(String name, List<String> fields, List<String> types) {
        List<Value> emptyValues = new ArrayList<>(fields.size());
        for (int i = 0; i < fields.size(); i++) {
            emptyValues.add(NullValue.INSTANCE);
        }
        return new StructVariable(name, fields, types, emptyValues);
    }

    @Override public String name() { return name; }
    @Override public Value value() { return NullValue.INSTANCE; }
    @Override public VariableType type() { return VariableType.STRUCT; }
    @Override public Variable withValue(Value newValue) { return this; }

    @Override
    public Map<String, MethodSpec> methods() {
        return METHODS;
    }

    @Override public Map<String, SignalHandler> signals() { return Map.of(); }
    @Override public Variable withSignal(String signalName, SignalHandler handler) { return this; }

    public Value getFieldByIndex(int index) {
        if (index >= 0 && index < values.size()) {
            return values.get(index);
        }
        return NullValue.INSTANCE;
    }

    public Value getFieldByName(String fieldName) {
        int idx = getFieldIndex(fieldName);
        return idx >= 0 ? values.get(idx) : NullValue.INSTANCE;
    }

    public int getFieldIndex(String fieldName) {
        if (fieldName == null) return -1;
        for (int i = 0; i < fields.size(); i++) {
            if (fields.get(i).equalsIgnoreCase(fieldName)) {
                return i;
            }
        }
        return -1;
    }

    public StructVariable withValueAt(int index, Value newValue) {
        if (index < 0 || index >= values.size()) {
            return this;
        }
        values.set(index, newValue);
        return this;
    }

    public StructVariable withValues(List<Value> newValues) {
        values.clear();
        values.addAll(newValues);
        return this;
    }

    public List<String> toRowStrings() {
        List<String> out = new ArrayList<>(values.size());
        for (Value v : values) {
            out.add(v == null || v == NullValue.INSTANCE ? "" : v.toDisplayString());
        }
        return out;
    }

    private static final Map<String, MethodSpec> METHODS = Map.ofEntries(
        Map.entry("GETFIELD", MethodSpec.of((self, args, ctx) -> {
            StructVariable thisVar = (StructVariable) self;
            int idx = ArgumentHelper.getInt(args, 0, 0);
            return MethodResult.returns(thisVar.getFieldByIndex(idx));
        })),

        Map.entry("SET", MethodSpec.of((self, args, ctx) -> {
            StructVariable thisVar = (StructVariable) self;
            if (args == null || args.isEmpty()) {
                return MethodResult.noReturn();
            }
            Value cursorRef = args.get(0);

            DatabaseCursorVariable cursor = resolveCursor(cursorRef, ctx);
            if (cursor != null) {
                DatabaseVariable dbVar = cursor.db();
                List<String> currentRow = dbVar.state().currentRow();
                List<String> types = thisVar.types();
                List<Value> newValues = new ArrayList<>(thisVar.fields().size());

                for (int i = 0; i < thisVar.fields().size(); i++) {
                    Value converted = getValue(i, currentRow, types);
                    newValues.add(converted);
                }

                thisVar.withValues(newValues);
                return MethodResult.noReturn();
            }

            return MethodResult.noReturn();
        }))
    );

    private static Value getValue(int i, List<String> currentRow, List<String> types) {
        String rawValue = (i < currentRow.size()) ? currentRow.get(i) : "";
        String type = (i < types.size()) ? types.get(i).toUpperCase() : "STRING";

        StringValue rawStringValue = new StringValue(rawValue);
        Value converted;
        switch (type) {
            case "INTEGER" -> converted = rawStringValue.toInt();
            case "DOUBLE" -> converted = rawStringValue.toDouble();
            case "BOOLEAN" -> converted = rawStringValue.toBool();
            default -> converted = rawStringValue;
        }
        return converted;
    }

    private static DatabaseCursorVariable resolveCursor(Value value, MethodContext ctx) {
        if (value instanceof VariableValue(Variable variable) && variable instanceof DatabaseCursorVariable cursor) {
            return cursor;
        }
        if (value instanceof StringValue(String value1) && ctx != null) {
            Variable resolved = ctx.getVariable(value1);
            if (resolved instanceof DatabaseCursorVariable cursor) return cursor;
        }
        return null;
    }
}
