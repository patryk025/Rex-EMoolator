package pl.genschu.bloomooemulator.interpreter.variable;

import pl.genschu.bloomooemulator.interpreter.values.NullValue;
import pl.genschu.bloomooemulator.interpreter.values.Value;
import pl.genschu.bloomooemulator.interpreter.values.VariableRef;

import java.util.HashMap;
import java.util.Map;

public final class DatabaseCursorVariable implements Variable {
    private final DatabaseVariable db;
    private final Map<String, VariableMethod> methods;

    public DatabaseCursorVariable(DatabaseVariable db) {
        this.db = db;
        this.methods = buildMethods();
    }

    @Override
    public String name() {
        return db.name() + "_CURSOR";
    }

    @Override
    public Value value() {
        return NullValue.INSTANCE;
    }

    @Override
    public VariableType type() {
        return VariableType.DATABASE_CURSOR;
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

    private Map<String, VariableMethod> buildMethods() {
        Map<String, VariableMethod> m = new HashMap<>();

        m.put("SET", (self, args) -> {
            if (args == null || args.isEmpty()) return MethodResult.noChange(NullValue.INSTANCE);

            Value v = args.get(0);

            // TODO: implement setting from StructVariable
            return MethodResult.noChange(NullValue.INSTANCE);
        });

        return Map.copyOf(m);
    }
    public void setFromStruct(StructVariable struct) {
        if (struct == null) return;
        db.state().updateCurrentRow(struct.toRowStrings());
    }
}
