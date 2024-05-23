package pl.cba.genszu.amcodetranslator.interpreter.logic;

import pl.cba.genszu.amcodetranslator.interpreter.Variable;
import pl.cba.genszu.amcodetranslator.interpreter.logic.operations.AndOperation;
import pl.cba.genszu.amcodetranslator.interpreter.logic.operations.OrOperation;

public class LogicSolver {
    static AndOperation andOperation = new AndOperation();
    static OrOperation orOperation = new OrOperation();

    public static Variable and(Variable var1, Variable var2) {
        return andOperation.performOperation(var1, var2);
    }

    public static Variable or(Variable var1, Variable var2) {
        return orOperation.performOperation(var1, var2);
    }
}
