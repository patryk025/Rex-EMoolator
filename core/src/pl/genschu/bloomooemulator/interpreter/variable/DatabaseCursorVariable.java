package pl.genschu.bloomooemulator.interpreter.variable;

import pl.genschu.bloomooemulator.interpreter.values.NullValue;
import pl.genschu.bloomooemulator.interpreter.values.Value;
import pl.genschu.bloomooemulator.interpreter.values.VariableValue;

import java.util.Map;

/**
 * DatabaseCursorVariable represents a cursor for iterating over database query results.
 * Provides SET method to update the current row from a StructVariable.
 */
public record DatabaseCursorVariable(
    DatabaseVariable db
) implements Variable {

    @Override
    public String name() {
        return db.name() + "_CURSOR";
    }

    @Override public Value value() { return NullValue.INSTANCE; }
    @Override public VariableType type() { return VariableType.DATABASE_CURSOR; }
    @Override public Variable withValue(Value newValue) { return this; }

    @Override
    public Map<String, MethodSpec> methods() {
        return METHODS;
    }

    @Override public Map<String, SignalHandler> signals() { return Map.of(); }
    @Override public Variable withSignal(String signalName, SignalHandler handler) { return this; }

    private static final Map<String, MethodSpec> METHODS = Map.ofEntries(
        Map.entry("SET", MethodSpec.of((self, args) -> {
            DatabaseCursorVariable thisVar = (DatabaseCursorVariable) self;
            if (args == null || args.isEmpty()) {
                return MethodResult.noChange(NullValue.INSTANCE);
            }

            Value v = args.get(0);

            // If the argument is a VariableValue wrapping a StructVariable
            if (v instanceof VariableValue(Variable variable) && variable instanceof StructVariable struct) {
                thisVar.setFromStruct(struct);
                return MethodResult.noChange(NullValue.INSTANCE);
            }

            // Otherwise, this is an incorrect situation, as variable should be already .
            throw new IllegalArgumentException("SET method requires a StructVariable argument");
        }, ArgKind.VAR_REF))
    );

    /**
     * Updates the current database row with values from the struct.
     */
    public void setFromStruct(StructVariable struct) {
        if (struct == null) return;
        db.state().updateCurrentRow(struct.toRowStrings());
    }
}
