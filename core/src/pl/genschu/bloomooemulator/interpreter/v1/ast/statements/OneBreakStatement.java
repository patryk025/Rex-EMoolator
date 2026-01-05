package pl.genschu.bloomooemulator.interpreter.v1.ast.statements;

import pl.genschu.bloomooemulator.interpreter.v1.Context;
import pl.genschu.bloomooemulator.interpreter.v1.ast.Statement;
import pl.genschu.bloomooemulator.interpreter.v1.exceptions.OneBreakException;

public class OneBreakStatement extends Statement {
    @Override
    public void execute(Context context) {
        throw new OneBreakException("OneBreak statement encountered");
    }
}
