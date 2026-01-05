package pl.genschu.bloomooemulator.interpreter.v1.ast.statements;

import pl.genschu.bloomooemulator.interpreter.v1.Context;
import pl.genschu.bloomooemulator.interpreter.v1.ast.Statement;
import pl.genschu.bloomooemulator.interpreter.v1.exceptions.BreakException;

public class BreakStatement extends Statement {
    @Override
    public void execute(Context context) {
        throw new BreakException("Break statement encountered");
    }
}
