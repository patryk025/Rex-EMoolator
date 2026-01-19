package pl.genschu.bloomooemulator.interpreter.variable;

import pl.genschu.bloomooemulator.interpreter.values.*;
import pl.genschu.bloomooemulator.interpreter.variable.capabilities.HasCursor;
import pl.genschu.bloomooemulator.interpreter.variable.db.DatabaseState;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class DatabaseVariable implements Variable, HasCursor {
    private final String name;
    private final DatabaseState state;

    private DatabaseCursorVariable cursor;

    private final Map<String, VariableMethod> methods;

    public DatabaseVariable(String name, DatabaseState state) {
        this.name = name;
        this.state = (state != null) ? state : new DatabaseState();
        this.methods = buildMethods();
    }

    public DatabaseState state() {
        return state;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Value value() {
        return NullValue.INSTANCE;
    }

    @Override
    public VariableType type() {
        return VariableType.DATABASE;
    }

    @Override
    public Variable withValue(Value newValue) {
        return this;
    }

    @Override
    public Map<String, VariableMethod> methods() {
        return methods;
    }

    @Override
    public Map<String, SignalHandler> signals() {
        return Map.of();
    }

    @Override
    public Variable withSignal(String signalName, SignalHandler handler) {
        return this;
    }

    // ===== Legacy capability: _CURSOR =====
    @Override
    public Variable getCursor() {
        if (cursor == null) {
            cursor = new DatabaseCursorVariable(this);
        }
        return cursor;
    }

    private Map<String, VariableMethod> buildMethods() {
        Map<String, VariableMethod> m = new HashMap<>();

        m.put("GETROWSNO", (self, args) ->
                MethodResult.noChange(new IntValue(state.rowsNo()))
        );

        m.put("NEXT", (self, args) -> {
            state.next();
            return MethodResult.noChange(NullValue.INSTANCE);
        });

        m.put("REMOVEALL", (self, args) -> {
            state.removeAll();
            return MethodResult.noChange(NullValue.INSTANCE);
        });

        m.put("SELECT", (self, args) -> {
            int idx = asInt(args, 0, 0);
            state.select(idx);
            return MethodResult.noChange(NullValue.INSTANCE);
        });

        m.put("FIND", (self, args) -> {
            String colName = asString(args, 0, "");
            String colValue = asString(args, 1, "");
            int def = asInt(args, 2, 0);
            int found = state.find(colName, colValue, def);
            return MethodResult.noChange(new IntValue(found));
        });

        // TODO: IMPLEMENT LOAD/SAVE
        m.put("LOAD", (self, args) -> MethodResult.noChange(NullValue.INSTANCE));
        m.put("SAVE", (self, args) -> MethodResult.noChange(NullValue.INSTANCE));

        return Map.copyOf(m);
    }

    private static int asInt(List<Value> args, int index, int def) {
        if (args == null || index >= args.size() || args.get(index) == null) return def;
        Value v = args.get(index);
        return switch (v) {
            case IntValue iv -> iv.value();
            case StringValue sv -> {
                IntValue parsed = sv.tryParseInt();
                yield parsed != null ? parsed.value() : def;
            }
            default -> def;
        };
    }

    private static String asString(List<Value> args, int index, String def) {
        if (args == null || index >= args.size() || args.get(index) == null) return def;
        Value v = args.get(index);
        return switch (v) {
            case StringValue sv -> sv.value();
            default -> v.toDisplayString();
        };
    }
}
