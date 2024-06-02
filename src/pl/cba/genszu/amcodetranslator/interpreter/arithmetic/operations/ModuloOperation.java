package pl.cba.genszu.amcodetranslator.interpreter.arithmetic.operations;

import pl.cba.genszu.amcodetranslator.interpreter.variable.Variable;
import pl.cba.genszu.amcodetranslator.interpreter.arithmetic.ArithmeticOperation;
import pl.cba.genszu.amcodetranslator.interpreter.exceptions.VariableUnsupportedOperationException;
import pl.cba.genszu.amcodetranslator.interpreter.factories.VariableFactory;
import pl.cba.genszu.amcodetranslator.interpreter.types.BoolVariable;
import pl.cba.genszu.amcodetranslator.interpreter.types.DoubleVariable;
import pl.cba.genszu.amcodetranslator.interpreter.types.IntegerVariable;
import pl.cba.genszu.amcodetranslator.interpreter.types.StringVariable;

public class ModuloOperation extends ArithmeticOperation {
    @Override
    public Variable performOperation(StringVariable var1, StringVariable var2) {
        throw new VariableUnsupportedOperationException(var1, var2, this.getOperation());
    }

    @Override
    public Variable performOperation(IntegerVariable var1, StringVariable var2) {
        throw new VariableUnsupportedOperationException(var1, var2, this.getOperation());
    }

    @Override
    public Variable performOperation(StringVariable var1, IntegerVariable var2) {
        throw new VariableUnsupportedOperationException(var1, var2, this.getOperation());
    }

    @Override
    public Variable performOperation(DoubleVariable var1, StringVariable var2) {
        throw new VariableUnsupportedOperationException(var1, var2, this.getOperation());
    }

    @Override
    public Variable performOperation(StringVariable var1, DoubleVariable var2) {
        throw new VariableUnsupportedOperationException(var1, var2, this.getOperation());
    }

    @Override
    public Variable performOperation(IntegerVariable var1, IntegerVariable var2) {
        return VariableFactory.createVariable("INTEGER", null, var1.GET() % var2.GET());
    }

    @Override
    public Variable performOperation(DoubleVariable var1, IntegerVariable var2) {
        return VariableFactory.createVariable("DOUBLE", null, var1.GET() % var2.GET());
    }

    @Override
    public Variable performOperation(IntegerVariable var1, DoubleVariable var2) {
        return VariableFactory.createVariable("DOUBLE", null, var1.GET() % var2.GET());
    }

    @Override
    public Variable performOperation(DoubleVariable var1, DoubleVariable var2) {
        return VariableFactory.createVariable("DOUBLE", null, var1.GET() % var2.GET());
    }

    @Override
    public Variable performOperation(BoolVariable var1, StringVariable var2) {
        return null;
    }

    @Override
    public Variable performOperation(StringVariable var1, BoolVariable var2) {
        return null;
    }

    @Override
    public Variable performOperation(BoolVariable var1, BoolVariable var2) {
        return null;
    }

    @Override
    public Variable performOperation(BoolVariable var1, IntegerVariable var2) {
        return null;
    }

    @Override
    public Variable performOperation(IntegerVariable var1, BoolVariable var2) {
        return null;
    }

    @Override
    public Variable performOperation(BoolVariable var1, DoubleVariable var2) {
        return null;
    }

    @Override
    public Variable performOperation(DoubleVariable var1, BoolVariable var2) {
        return null;
    }
}
