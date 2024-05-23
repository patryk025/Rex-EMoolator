package pl.cba.genszu.amcodetranslator.interpreter.arithmetic.operations;

import pl.cba.genszu.amcodetranslator.interpreter.Variable;
import pl.cba.genszu.amcodetranslator.interpreter.arithmetic.ArithmeticOperation;
import pl.cba.genszu.amcodetranslator.interpreter.factories.VariableFactory;
import pl.cba.genszu.amcodetranslator.interpreter.types.BoolVariable;
import pl.cba.genszu.amcodetranslator.interpreter.types.DoubleVariable;
import pl.cba.genszu.amcodetranslator.interpreter.types.IntegerVariable;
import pl.cba.genszu.amcodetranslator.interpreter.types.StringVariable;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class AddOperation extends ArithmeticOperation {
    @Override
    public Variable performOperation(StringVariable var1, StringVariable var2) {
        return VariableFactory.createVariable("STRING", null, var1.GET() + var2.GET());
    }

    @Override
    public Variable performOperation(IntegerVariable var1, StringVariable var2) {
        return var1; // wygląda, że silnik tak nie dodaje
    }

    @Override
    public Variable performOperation(StringVariable var1, IntegerVariable var2) {
        return VariableFactory.createVariable("STRING", null, var1.GET() + var2.GET());
    }

    @Override
    public Variable performOperation(DoubleVariable var1, StringVariable var2) {
        return var1; // wygląda, że silnik tak nie dodaje
    }

    @Override
    public Variable performOperation(StringVariable var1, DoubleVariable var2) {
        NumberFormat formatter = new DecimalFormat("#0.00000");
        return VariableFactory.createVariable("STRING", null, var1.GET() + formatter.format(var2.GET()));
    }

    @Override
    public Variable performOperation(IntegerVariable var1, IntegerVariable var2) {
        return VariableFactory.createVariable("INTEGER", null, var1.GET() + var2.GET());
    }

    @Override
    public Variable performOperation(DoubleVariable var1, IntegerVariable var2) {
        return VariableFactory.createVariable("DOUBLE", null, var1.GET() + var2.GET());
    }

    @Override
    public Variable performOperation(IntegerVariable var1, DoubleVariable var2) {
        return VariableFactory.createVariable("INTEGER", null, Math.round(var1.GET() + var2.GET())); // castuje do inta i zaokrągla
    }

    @Override
    public Variable performOperation(DoubleVariable var1, DoubleVariable var2) {
        return VariableFactory.createVariable("DOUBLE", null, var1.GET() + var2.GET());
    }

    @Override
    public Variable performOperation(BoolVariable var1, StringVariable var2) {
        return VariableFactory.createVariable("BOOL", null, false); // wygląda, że zwraca false
    }

    @Override
    public Variable performOperation(StringVariable var1, BoolVariable var2) {
        return VariableFactory.createVariable("STRING", null, var1.GET()+(var2.GET() ? "TRUE" : "FALSE")); // dodaje literalnie TRUE lub FALSE
    }

    @Override
    public Variable performOperation(BoolVariable var1, BoolVariable var2) {
        return VariableFactory.createVariable("BOOL", null, var1.GET() || var2.GET());
    }

    @Override
    public Variable performOperation(BoolVariable var1, IntegerVariable var2) {
        return VariableFactory.createVariable("BOOL", null, var1.GET() || (var2.GET() != 0));
    }

    @Override
    public Variable performOperation(IntegerVariable var1, BoolVariable var2) {
        return VariableFactory.createVariable("INTEGER", null, var1.GET() + (var2.GET() ? 1 : 0));
    }

    @Override
    public Variable performOperation(BoolVariable var1, DoubleVariable var2) {
        return VariableFactory.createVariable("BOOL", null, var1.GET() || (var2.GET() != 0));
    }

    @Override
    public Variable performOperation(DoubleVariable var1, BoolVariable var2) {
        return VariableFactory.createVariable("DOUBLE", null, var1.GET() + (var2.GET() ? 1 : 0));
    }
}
