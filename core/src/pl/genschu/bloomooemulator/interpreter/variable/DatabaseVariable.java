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

        Map.entry("LOAD", MethodSpec.of((self, args, ctx) -> {
            DatabaseVariable thisVar = (DatabaseVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("LOAD requires 1 argument: dtaName");
            }
            String dtaName = ArgumentHelper.getString(args.get(0));
            String filePath = pl.genschu.bloomooemulator.utils.FileUtils.resolveRelativePath(ctx.getGame(), dtaName);
            if (thisVar.state.columns().isEmpty()) {
                Gdx.app.error("DatabaseVariable", "Missing model in database " + thisVar.name() + ". Aborting LOAD.");
                return MethodResult.noReturn();
            }
            java.util.List<java.util.List<String>> data = new java.util.ArrayList<>();
            try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(filePath))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    data.add(new java.util.ArrayList<>(java.util.Arrays.asList(line.split("\\|"))));
                }
            } catch (java.io.IOException e) {
                Gdx.app.error("DatabaseVariable", "Error loading database: " + e.getMessage(), e);
            }
            thisVar.state.setData(data);
            return MethodResult.noReturn();
        })),

        Map.entry("SAVE", MethodSpec.of((self, args, ctx) -> {
            DatabaseVariable thisVar = (DatabaseVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("SAVE requires 1 argument: dtaName");
            }
            String dtaName = ArgumentHelper.getString(args.get(0));
            String filePath = pl.genschu.bloomooemulator.utils.FileUtils.resolveRelativePath(ctx.getGame(), dtaName);
            try (java.io.BufferedWriter writer = new java.io.BufferedWriter(new java.io.FileWriter(filePath))) {
                for (java.util.List<String> row : thisVar.state.data()) {
                    writer.write(String.join("|", row));
                    writer.newLine();
                }
            } catch (java.io.IOException e) {
                Gdx.app.error("DatabaseVariable", "Error saving database: " + e.getMessage(), e);
            }
            return MethodResult.noReturn();
        }))
    );
}
