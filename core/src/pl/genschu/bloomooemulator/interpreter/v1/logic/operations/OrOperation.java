package pl.genschu.bloomooemulator.interpreter.v1.logic.operations;

import pl.genschu.bloomooemulator.interpreter.v1.variable.Variable;
import pl.genschu.bloomooemulator.interpreter.v1.arithmetic.ArithmeticOperation;
import pl.genschu.bloomooemulator.interpreter.factories.LegacyVariableFactory;
import pl.genschu.bloomooemulator.interpreter.v1.variable.types.BoolVariable;
import pl.genschu.bloomooemulator.interpreter.v1.variable.types.DoubleVariable;
import pl.genschu.bloomooemulator.interpreter.v1.variable.types.IntegerVariable;
import pl.genschu.bloomooemulator.interpreter.v1.variable.types.StringVariable;

public class OrOperation extends ArithmeticOperation {
    @Override
    public Variable performOperation(StringVariable var1, StringVariable var2) {
        throw new UnsupportedOperationException(String.format("Nieobsługiwana operacja OR dla zmiennych typu (%s, %s)", var1.getType(), var2.getType()));
    }

    @Override
    public Variable performOperation(IntegerVariable var1, StringVariable var2) {
        throw new UnsupportedOperationException(String.format("Nieobsługiwana operacja OR dla zmiennych typu (%s, %s)", var1.getType(), var2.getType()));
    }

    @Override
    public Variable performOperation(StringVariable var1, IntegerVariable var2) {
        throw new UnsupportedOperationException(String.format("Nieobsługiwana operacja OR dla zmiennych typu (%s, %s)", var1.getType(), var2.getType()));
    }

    @Override
    public Variable performOperation(DoubleVariable var1, StringVariable var2) {
        throw new UnsupportedOperationException(String.format("Nieobsługiwana operacja OR dla zmiennych typu (%s, %s)", var1.getType(), var2.getType()));
    }

    @Override
    public Variable performOperation(StringVariable var1, DoubleVariable var2) {
        throw new UnsupportedOperationException(String.format("Nieobsługiwana operacja OR dla zmiennych typu (%s, %s)", var1.getType(), var2.getType()));
    }

    @Override
    public Variable performOperation(IntegerVariable var1, IntegerVariable var2) {
        throw new UnsupportedOperationException(String.format("Nieobsługiwana operacja OR dla zmiennych typu (%s, %s)", var1.getType(), var2.getType()));
    }

    @Override
    public Variable performOperation(DoubleVariable var1, IntegerVariable var2) {
        throw new UnsupportedOperationException(String.format("Nieobsługiwana operacja OR dla zmiennych typu (%s, %s)", var1.getType(), var2.getType()));
    }

    @Override
    public Variable performOperation(IntegerVariable var1, DoubleVariable var2) {
        throw new UnsupportedOperationException(String.format("Nieobsługiwana operacja OR dla zmiennych typu (%s, %s)", var1.getType(), var2.getType()));
    }

    @Override
    public Variable performOperation(DoubleVariable var1, DoubleVariable var2) {
        throw new UnsupportedOperationException(String.format("Nieobsługiwana operacja OR dla zmiennych typu (%s, %s)", var1.getType(), var2.getType()));
    }

    @Override
    public Variable performOperation(BoolVariable var1, StringVariable var2) {
        throw new UnsupportedOperationException(String.format("Nieobsługiwana operacja OR dla zmiennych typu (%s, %s)", var1.getType(), var2.getType()));
    }

    @Override
    public Variable performOperation(StringVariable var1, BoolVariable var2) {
        throw new UnsupportedOperationException(String.format("Nieobsługiwana operacja OR dla zmiennych typu (%s, %s)", var1.getType(), var2.getType()));
    }

    @Override
    public Variable performOperation(BoolVariable var1, BoolVariable var2) {
        return LegacyVariableFactory.createVariable("BOOL", "", var1.GET() || var2.GET(), var1.getContext());
    }

    @Override
    public Variable performOperation(BoolVariable var1, IntegerVariable var2) {
        throw new UnsupportedOperationException(String.format("Nieobsługiwana operacja OR dla zmiennych typu (%s, %s)", var1.getType(), var2.getType()));
    }

    @Override
    public Variable performOperation(IntegerVariable var1, BoolVariable var2) {
        throw new UnsupportedOperationException(String.format("Nieobsługiwana operacja OR dla zmiennych typu (%s, %s)", var1.getType(), var2.getType()));
    }

    @Override
    public Variable performOperation(BoolVariable var1, DoubleVariable var2) {
        throw new UnsupportedOperationException(String.format("Nieobsługiwana operacja OR dla zmiennych typu (%s, %s)", var1.getType(), var2.getType()));
    }

    @Override
    public Variable performOperation(DoubleVariable var1, BoolVariable var2) {
        throw new UnsupportedOperationException(String.format("Nieobsługiwana operacja OR dla zmiennych typu (%s, %s)", var1.getType(), var2.getType()));
    }
}
