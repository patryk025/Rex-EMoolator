package pl.genschu.bloomooemulator.interpreter.variable;

import pl.genschu.bloomooemulator.interpreter.values.NullValue;
import pl.genschu.bloomooemulator.interpreter.values.Value;
import pl.genschu.bloomooemulator.interpreter.values.VariableRef;

import java.util.HashMap;
import java.util.Map;

/**
 * DatabaseCursorVariable represents a cursor for iterating over database query results.
 */
public record DatabaseCursorVariable (
    DatabaseVariable db
) implements Variable {

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
    public Map<String, MethodSpec> methods() {
        return METHODS;
    }

    @Override
    public Map<String, SignalHandler> signals() {
        return Map.of();
    }

    @Override
    public Variable withSignal(String signalName, SignalHandler handler) {
        return this;
    }

    private static final Map<String, MethodSpec> METHODS = Map.ofEntries(
        Map.entry("SET", MethodSpec.of((self, args) -> {
            if (args == null || args.isEmpty()) return MethodResult.noChange(NullValue.INSTANCE);

            Value v = args.get(0);

            // TODO: implement setting from StructVariable
            return MethodResult.noChange(NullValue.INSTANCE);
        }))
    );

    public void setFromStruct(StructVariable struct) {
        if (struct == null) return;
        db.state().updateCurrentRow(struct.toRowStrings());
    }
}
