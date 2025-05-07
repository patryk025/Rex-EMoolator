package pl.genschu.bloomooemulator.interpreter.logic;

import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.interpreter.logic.operations.*;
import pl.genschu.bloomooemulator.interpreter.variable.types.StringVariable;

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
        return andOperation.performOperation(ensurePrimitive(var1), ensurePrimitive(var2));
    }

    public static Variable or(Variable var1, Variable var2) {
        return orOperation.performOperation(ensurePrimitive(var1), ensurePrimitive(var2));
    }

    public static Variable equals(Variable var1, Variable var2) {
        return equalsOperation.performOperation(ensurePrimitive(var1), ensurePrimitive(var2));
    }

    public static Variable notEquals(Variable var1, Variable var2) {
        return notEqualsOperation.performOperation(ensurePrimitive(var1), ensurePrimitive(var2));
    }

    public static Variable greater(Variable var1, Variable var2) {
        return greaterOperation.performOperation(ensurePrimitive(var1), ensurePrimitive(var2));
    }

    public static Variable greaterOrEquals(Variable var1, Variable var2) {
        return greaterOrEqualsOperation.performOperation(ensurePrimitive(var1), ensurePrimitive(var2));
    }

    public static Variable less(Variable var1, Variable var2) {
        return lessOperation.performOperation(ensurePrimitive(var1), ensurePrimitive(var2));
    }

    public static Variable lessOrEquals(Variable var1, Variable var2) {
        return lessOrEqualsOperation.performOperation(ensurePrimitive(var1), ensurePrimitive(var2));
    }

    private static Variable ensurePrimitive(Variable var) {
        if(var.getType().matches("STRING|INTEGER|DOUBLE|BOOL"))
            return var;
        else
            return new StringVariable("", var.getName(), var.getContext());
    }
}
