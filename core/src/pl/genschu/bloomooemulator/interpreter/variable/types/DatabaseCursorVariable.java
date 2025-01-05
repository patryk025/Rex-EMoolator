package pl.genschu.bloomooemulator.interpreter.variable.types;

import com.badlogic.gdx.Gdx;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.variable.Attribute;
import pl.genschu.bloomooemulator.interpreter.variable.Method;
import pl.genschu.bloomooemulator.interpreter.variable.Parameter;
import pl.genschu.bloomooemulator.utils.ArgumentsHelper;

import java.util.List;

public class DatabaseCursorVariable extends Variable {
    private final DatabaseVariable database;

    public DatabaseCursorVariable(DatabaseVariable database, Context context) {
        super(database.getName() + "_CURSOR", context);
        this.database = database;
    }

    @Override
    public void setAttribute(String name, Attribute attribute) {
        // Nie ma atrybutów
    }

    @Override
    public Method getMethod(String name, List<String> paramTypes) {
        if (name.equals("SET")) {
            return new Method(
                    List.of(new Parameter("STRUCT", "struct", true)),
                    "void"
            ) {
                @Override
                public Variable execute(List<Object> arguments) {
                    Variable struct = getContext().getVariable(ArgumentsHelper.getString(arguments.get(0)));
                    if(struct != null) {
                        if(struct instanceof StructVariable) {
                            StructVariable structVariable = (StructVariable) struct;
                            database.updateCurrentRow(structVariable);
                        }
                        else {
                            Gdx.app.error("DatabaseCursorVariable", "Cannot set cursor to non-struct variable");
                        }
                    }
                    return null;
                }
            };
        }
        return super.getMethod(name, paramTypes);
    }

    @Override
    public String getType() {
        return "DATABASE_CURSOR";
    }
}
