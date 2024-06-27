package pl.cba.genszu.amcodetranslator.interpreter.arithmetic;

import pl.cba.genszu.amcodetranslator.interpreter.factories.VariableFactory;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Variable;
import pl.cba.genszu.amcodetranslator.interpreter.arithmetic.operations.*;

public class ArithmeticSolver {
    static AddOperation addOperation = new AddOperation();
    static SubtractOperation subtractOperation = new SubtractOperation();
    static MultiplyOperation multiplyOperation = new MultiplyOperation();
    static DivideOperation divideOperation = new DivideOperation();
    static ModuloOperation moduloOperation = new ModuloOperation();

    public static Variable add(Variable var1, Variable var2) {
        return addOperation.performOperation(var1, var2);
    }

    public static Variable subtract(Variable var1, Variable var2) {
        return subtractOperation.performOperation(var1, var2);
    }

    public static Variable multiply(Variable var1, Variable var2) {
        return multiplyOperation.performOperation(var1, var2);
    }

    public static Variable divide(Variable var1, Variable var2) {
        try {
            return divideOperation.performOperation(var1, var2);
        } catch (ArithmeticException e) {
            return VariableFactory.createVariable("STRING", null, "NULL", var1.getContext());
        }
    }

    public static Variable modulo(Variable var1, Variable var2) {
        try {
            return moduloOperation.performOperation(var1, var2);
        } catch (ArithmeticException e) {
            return VariableFactory.createVariable("STRING", null, "NULL", var1.getContext());
        }
    }
}
