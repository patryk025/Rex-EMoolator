package pl.genschu.bloomooemulator.interpreter.variable;

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
    List<Value> values      // field values
) implements Variable {

    public StructVariable(String name, List<String> fields, List<String> types, List<Value> values) {
        this.name = name;
        this.fields = fields != null ? List.copyOf(fields) : List.of();
        this.types = types != null ? List.copyOf(types) : List.of();
        this.values = values != null ? List.copyOf(values) : List.of();
    }

    /**
     * Creates a struct with fields/types but empty (null) values.
     */
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

    /**
     * Returns the value at given field index.
     */
    public Value getFieldByIndex(int index) {
        if (index >= 0 && index < values.size()) {
            return values.get(index);
        }
        return NullValue.INSTANCE;
    }

    /**
     * Returns the value for given field name (case-insensitive).
     */
    public Value getFieldByName(String fieldName) {
        int idx = getFieldIndex(fieldName);
        return idx >= 0 ? values.get(idx) : NullValue.INSTANCE;
    }

    /**
     * Returns the index of a field by name (case-insensitive), or -1 if not found.
     */
    public int getFieldIndex(String fieldName) {
        if (fieldName == null) return -1;
        for (int i = 0; i < fields.size(); i++) {
            if (fields.get(i).equalsIgnoreCase(fieldName)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns a new StructVariable with updated value at given index.
     */
    public StructVariable withValueAt(int index, Value newValue) {
        if (index < 0 || index >= values.size()) {
            return this;
        }
        List<Value> newValues = new ArrayList<>(values);
        newValues.set(index, newValue);
        return new StructVariable(name, fields, types, newValues);
    }

    /**
     * Returns a new StructVariable with all values replaced.
     */
    public StructVariable withValues(List<Value> newValues) {
        return new StructVariable(name, fields, types, newValues);
    }

    /**
     * Converts values to a list of strings (for database row updates).
     */
    public List<String> toRowStrings() {
        List<String> out = new ArrayList<>(values.size());
        for (Value v : values) {
            out.add(v == null || v == NullValue.INSTANCE ? "" : v.toDisplayString());
        }
        return out;
    }

    private static final Map<String, MethodSpec> METHODS = Map.ofEntries(
        Map.entry("GETFIELD", MethodSpec.of((self, args) -> {
            StructVariable thisVar = (StructVariable) self;
            int idx = ArgumentHelper.getInt(args, 0, 0);
            return MethodResult.noChange(thisVar.getFieldByIndex(idx));
        })),

        Map.entry("SET", MethodSpec.of((self, args) -> {
            StructVariable thisVar = (StructVariable) self;
            if (args == null || args.isEmpty()) {
                return MethodResult.noChange(NullValue.INSTANCE);
            }
            Value cursorRef = args.get(0);

            if (cursorRef instanceof VariableValue(Variable variable) && variable instanceof DatabaseCursorVariable(DatabaseVariable dbVar)) {
                List<String> currentRow = dbVar.state().currentRow();
                List<String> types = thisVar.types();
                List<Value> newValues = new ArrayList<>(thisVar.fields().size());

                for (int i = 0; i < thisVar.fields().size(); i++) {
                    String rawValue = (i < currentRow.size()) ? currentRow.get(i) : "";
                    String type = (i < types.size()) ? types.get(i).toUpperCase() : "STRING";

                    StringValue rawStringValue = new StringValue(rawValue);
                    Value converted;
                    switch (type) {
                        case "INTEGER" -> converted = rawStringValue.toInt();
                        case "DOUBLE" -> converted = rawStringValue.toDouble();
                        case "BOOLEAN" -> converted = rawStringValue.toBool();
                        default -> converted = rawStringValue; // Fallback to string
                    }
                    newValues.add(converted);
                }

                // Update the struct in context with new values
                StructVariable updatedStruct = thisVar.withValues(newValues);
                return MethodResult.sets(updatedStruct);
            }

            //return MethodResult.effects(List.of(new StructSetFromCursorEffect(thisVar, cursorRef)));
            return MethodResult.noChange(NullValue.INSTANCE);
        }, ArgKind.VAR_REF))
    );
}
