package pl.cba.genszu.amcodetranslator.interpreter.logic;

import pl.cba.genszu.amcodetranslator.interpreter.Variable;
import pl.cba.genszu.amcodetranslator.interpreter.logic.operations.*;

public class LogicSolver {
    static AndOperation andOperation = new AndOperation();
    static OrOperation orOperation = new OrOperation();
    static EqualsOperation equalsOperation = new EqualsOperation();
    static NotEqualsOperation notEqualsOperation = new NotEqualsOperation();
    static GreaterThanOperation greaterOperation = new GreaterThanOperation();
    static GreaterOrEqualsOperation greaterOrEqualsOperation = new GreaterOrEqualsOperation();
    static LessThanOperation lessOperation = new LessThanOperation();
    static LessOrEqualsOperation lessOrEqualsOperation = new LessOrEqualsOperation();

    public static Variable and(Variable var1, Variable var2) {
        return andOperation.performOperation(var1, var2);
    }

    public static Variable or(Variable var1, Variable var2) {
        return orOperation.performOperation(var1, var2);
    }

    public static Variable equals(Variable var1, Variable var2) {
        return equalsOperation.performOperation(var1, var2);
    }

    public static Variable notEquals(Variable var1, Variable var2) {
        return notEqualsOperation.performOperation(var1, var2);
    }

    public static Variable greater(Variable var1, Variable var2) {
        return greaterOperation.performOperation(var1, var2);
    }

    public static Variable greaterOrEquals(Variable var1, Variable var2) {
        return greaterOrEqualsOperation.performOperation(var1, var2);
    }

    public static Variable less(Variable var1, Variable var2) {
        return lessOperation.performOperation(var1, var2);
    }

    public static Variable lessOrEquals(Variable var1, Variable var2) {
        return lessOrEqualsOperation.performOperation(var1, var2);
    }
}
