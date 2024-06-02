package pl.cba.genszu.amcodetranslator.interpreter.logic.operations;

import pl.cba.genszu.amcodetranslator.interpreter.variable.Variable;
import pl.cba.genszu.amcodetranslator.interpreter.arithmetic.ArithmeticOperation;
import pl.cba.genszu.amcodetranslator.interpreter.factories.VariableFactory;
import pl.cba.genszu.amcodetranslator.interpreter.variable.types.BoolVariable;
import pl.cba.genszu.amcodetranslator.interpreter.variable.types.DoubleVariable;
import pl.cba.genszu.amcodetranslator.interpreter.variable.types.IntegerVariable;
import pl.cba.genszu.amcodetranslator.interpreter.variable.types.StringVariable;

public class OrOperation extends ArithmeticOperation {
    @Override
    public Variable performOperation(StringVariable var1, StringVariable var2) {
        return null;
    }

    @Override
    public Variable performOperation(IntegerVariable var1, StringVariable var2) {
        return null;
    }

    @Override
    public Variable performOperation(StringVariable var1, IntegerVariable var2) {
        return null;
    }

    @Override
    public Variable performOperation(DoubleVariable var1, StringVariable var2) {
        return null;
    }

    @Override
    public Variable performOperation(StringVariable var1, DoubleVariable var2) {
        return null;
    }

    @Override
    public Variable performOperation(IntegerVariable var1, IntegerVariable var2) {
        return null;
    }

    @Override
    public Variable performOperation(DoubleVariable var1, IntegerVariable var2) {
        return null;
    }

    @Override
    public Variable performOperation(IntegerVariable var1, DoubleVariable var2) {
        return null;
    }

    @Override
    public Variable performOperation(DoubleVariable var1, DoubleVariable var2) {
        return null;
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
        return VariableFactory.createVariable("BOOL", "", var1.GET() || var2.GET());
    }

    @Override
    public Variable performOperation(BoolVariable var1, IntegerVariable var2) {
        return VariableFactory.createVariable("BOOL", "", var1.GET() || (var2.GET() != 0));
    }

    @Override
    public Variable performOperation(IntegerVariable var1, BoolVariable var2) {
        return null;
    }

    @Override
    public Variable performOperation(BoolVariable var1, DoubleVariable var2) {
        return VariableFactory.createVariable("BOOL", "", var1.GET() || (var2.GET() != 0));
    }

    @Override
    public Variable performOperation(DoubleVariable var1, BoolVariable var2) {
        return null;
    }
}
