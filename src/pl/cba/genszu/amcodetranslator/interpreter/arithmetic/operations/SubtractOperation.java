package pl.cba.genszu.amcodetranslator.interpreter.arithmetic;

import pl.cba.genszu.amcodetranslator.interpreter.Variable;
import pl.cba.genszu.amcodetranslator.interpreter.exceptions.VariableUnsupportedOperationException;
import pl.cba.genszu.amcodetranslator.interpreter.factories.VariableFactory;
import pl.cba.genszu.amcodetranslator.interpreter.types.DoubleVariable;
import pl.cba.genszu.amcodetranslator.interpreter.types.IntegerVariable;
import pl.cba.genszu.amcodetranslator.interpreter.types.StringVariable;

public class SubtractOperation extends ArithmeticOperation {
    @Override
    Variable performOperation(StringVariable var1, StringVariable var2) {
        throw new VariableUnsupportedOperationException(var1, var2, this.getOperation());
    }

    @Override
    Variable performOperation(IntegerVariable var1, StringVariable var2) {
        throw new VariableUnsupportedOperationException(var1, var2, this.getOperation());
    }

    @Override
    Variable performOperation(StringVariable var1, IntegerVariable var2) {
        throw new VariableUnsupportedOperationException(var1, var2, this.getOperation());
    }

    @Override
    Variable performOperation(DoubleVariable var1, StringVariable var2) {
        throw new VariableUnsupportedOperationException(var1, var2, this.getOperation());
    }

    @Override
    Variable performOperation(StringVariable var1, DoubleVariable var2) {
        throw new VariableUnsupportedOperationException(var1, var2, this.getOperation());
    }

    @Override
    Variable performOperation(IntegerVariable var1, IntegerVariable var2) {
        return VariableFactory.createVariable("INTEGER", null, var1.GET() - var2.GET());
    }

    @Override
    Variable performOperation(DoubleVariable var1, IntegerVariable var2) {
        return VariableFactory.createVariable("DOUBLE", null, var1.GET() - var2.GET());
    }

    @Override
    Variable performOperation(IntegerVariable var1, DoubleVariable var2) {
        return VariableFactory.createVariable("DOUBLE", null, var1.GET() - var2.GET());
    }

    @Override
    Variable performOperation(DoubleVariable var1, DoubleVariable var2) {
        return VariableFactory.createVariable("DOUBLE", null, var1.GET() - var2.GET());
    }
}
