package pl.cba.genszu.amcodetranslator.interpreter.ast.statements;

import pl.cba.genszu.amcodetranslator.interpreter.*;
import pl.cba.genszu.amcodetranslator.interpreter.ast.*;
import pl.cba.genszu.amcodetranslator.interpreter.exceptions.*;

public class OneBreakStatement extends Statement {
    @Override
    public void execute(Context context) {
        throw new OneBreakException("OneBreak statement encountered");
    }
}
