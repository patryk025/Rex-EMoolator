package pl.genschu.bloomooemulator.interpreter.ast.statements;

import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.ast.Statement;
import pl.genschu.bloomooemulator.interpreter.exceptions.OneBreakException;

public class OneBreakStatement extends Statement {
    @Override
    public void execute(Context context) {
        throw new OneBreakException("OneBreak statement encountered");
    }
}
