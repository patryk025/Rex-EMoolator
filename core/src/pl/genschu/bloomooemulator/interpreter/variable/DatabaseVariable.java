package pl.genschu.bloomooemulator.interpreter.variable;

import com.badlogic.gdx.Gdx;
import pl.genschu.bloomooemulator.annotations.InternalMutable;
import pl.genschu.bloomooemulator.interpreter.context.Context;
import pl.genschu.bloomooemulator.interpreter.helpers.ArgumentHelper;
import pl.genschu.bloomooemulator.interpreter.values.*;
import pl.genschu.bloomooemulator.interpreter.variable.capabilities.HasCursor;
import pl.genschu.bloomooemulator.interpreter.variable.capabilities.Initializable;
import pl.genschu.bloomooemulator.interpreter.variable.db.DatabaseState;

import java.util.Map;

/**
 * DatabaseVariable represents a database with rows and columns.
 * Linked to a STRUCT via modelName for schema definition.
 */
public record DatabaseVariable(
    String name,
    String modelName, // nullable - name of STRUCT defining schema (columns/types)
    @InternalMutable
    DatabaseState state, // DatabaseState is mutable for performance reasons
    DatabaseCursorVariable cursor
) implements Variable, HasCursor, Initializable {

    public DatabaseVariable(String name, String modelName, DatabaseState state, DatabaseCursorVariable cursor) {
        this.name = name;
        this.modelName = modelName;
        this.state = (state != null) ? state : new DatabaseState();
        this.cursor = cursor;
    }

    public DatabaseVariable(String name, String modelName, DatabaseState state) {
        this(name, modelName, state, null);
    }

    public DatabaseVariable(String name, DatabaseState state) {
        this(name, null, state, null);
    }

    public DatabaseVariable(String name) {
        this(name, null, new DatabaseState(), null);
    }

    @Override public String name() { return name; }
    @Override public Value value() { return NullValue.INSTANCE; }
    @Override public VariableType type() { return VariableType.DATABASE; }
    @Override public Variable withValue(Value newValue) { return this; }

    @Override
    public Map<String, MethodSpec> methods() {
        return METHODS;
    }

    @Override public Map<String, SignalHandler> signals() { return Map.of(); }
    @Override public Variable withSignal(String signalName, SignalHandler handler) { return this; }

    @Override
    public Variable getCursor() {
        if (cursor == null) {
            return new DatabaseCursorVariable(this);
        }
        return cursor;
    }

    /**
     * Initialize database by resolving MODEL -> STRUCT and copying column schema.
     */
    @Override
    public void init(Context context) {
        if (modelName == null || modelName.isEmpty()) {
            Gdx.app.debug("DatabaseVariable", name + ": No MODEL defined, skipping schema init");
            return;
        }

        Variable modelVar = context.getVariable(modelName);
        if (modelVar == null) {
            Gdx.app.error("DatabaseVariable", name + ": MODEL '" + modelName + "' not found in context");
            return;
        }

        if (!(modelVar instanceof StructVariable struct)) {
            Gdx.app.error("DatabaseVariable", name + ": MODEL '" + modelName + "' is not a STRUCT (got " + modelVar.type() + ")");
            return;
        }

        // Copy column names from STRUCT fields to DatabaseState
        state.setColumns(struct.fields());
        Gdx.app.debug("DatabaseVariable", name + ": Initialized with " + struct.fields().size() + " columns from " + modelName);
    }

    private static final Map<String, MethodSpec> METHODS = Map.ofEntries(
        Map.entry("GETROWSNO", MethodSpec.of((self, args, ctx) -> {
            DatabaseVariable thisVar = (DatabaseVariable) self;
            return MethodResult.returns(new IntValue(thisVar.state.rowsNo()));
        })),

        Map.entry("NEXT", MethodSpec.of((self, args, ctx) -> {
            DatabaseVariable thisVar = (DatabaseVariable) self;
            thisVar.state.next();
            return MethodResult.noReturn();
        })),

        Map.entry("REMOVEALL", MethodSpec.of((self, args, ctx) -> {
            DatabaseVariable thisVar = (DatabaseVariable) self;
            thisVar.state.removeAll();
            return MethodResult.noReturn();
        })),

        Map.entry("SELECT", MethodSpec.of((self, args, ctx) -> {
            DatabaseVariable thisVar = (DatabaseVariable) self;
            int idx = ArgumentHelper.getInt(args, 0, 0);
            thisVar.state.select(idx);
            return MethodResult.noReturn();
        })),

        Map.entry("FIND", MethodSpec.of((self, args, ctx) -> {
            DatabaseVariable thisVar = (DatabaseVariable) self;
            String colName = ArgumentHelper.getString(args, 0, "");
            String colValue = ArgumentHelper.getString(args, 1, "");
            int def = ArgumentHelper.getInt(args, 2, 0);
            int found = thisVar.state.find(colName, colValue, def);
            return MethodResult.returns(new IntValue(found));
        })),

        // TODO: IMPLEMENT LOAD/SAVE
        Map.entry("LOAD", MethodSpec.of((self, args, ctx) -> MethodResult.noReturn())),
        Map.entry("SAVE", MethodSpec.of((self, args, ctx) -> MethodResult.noReturn()))
    );
}
