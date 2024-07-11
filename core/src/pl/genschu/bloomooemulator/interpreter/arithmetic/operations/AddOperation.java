package pl.genschu.bloomooemulator.interpreter.arithmetic.operations;

import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.interpreter.arithmetic.ArithmeticOperation;
import pl.genschu.bloomooemulator.interpreter.factories.VariableFactory;
import pl.genschu.bloomooemulator.interpreter.variable.types.BoolVariable;
import pl.genschu.bloomooemulator.interpreter.variable.types.DoubleVariable;
import pl.genschu.bloomooemulator.interpreter.variable.types.IntegerVariable;
import pl.genschu.bloomooemulator.interpreter.variable.types.StringVariable;

public class AddOperation extends ArithmeticOperation {
    @Override
    public Variable performOperation(StringVariable var1, StringVariable var2) {
        return VariableFactory.createVariable("STRING", null, var1.GET() + var2.GET(), var1.getContext());
    }

    @Override
    public Variable performOperation(IntegerVariable var1, StringVariable var2) {
        return VariableFactory.createVariable("INTEGER", null, var1.GET() + var2.toInt(), var1.getContext()); // STRING się rzutuje do INT, jeśli się nie da, daje 0
    }

    @Override
    public Variable performOperation(StringVariable var1, IntegerVariable var2) {
        return VariableFactory.createVariable("STRING", null, var1.GET() + var2.toString(), var1.getContext());
    }

    @Override
    public Variable performOperation(DoubleVariable var1, StringVariable var2) {
        return VariableFactory.createVariable("DOUBLE", null, var1.GET() + var2.toDouble(), var1.getContext()); // STRING się rzutuje do DOUBLE, jeśli się nie da, daje 0
    }

    @Override
    public Variable performOperation(StringVariable var1, DoubleVariable var2) {
        return VariableFactory.createVariable("STRING", null, var1.GET() + var2.toString(), var1.getContext());
    }

    @Override
    public Variable performOperation(IntegerVariable var1, IntegerVariable var2) {
        return VariableFactory.createVariable("INTEGER", null, var1.GET() + var2.GET(), var1.getContext());
    }

    @Override
    public Variable performOperation(DoubleVariable var1, IntegerVariable var2) {
        return VariableFactory.createVariable("DOUBLE", null, var1.GET() + var2.GET(), var1.getContext());
    }

    @Override
    public Variable performOperation(IntegerVariable var1, DoubleVariable var2) {
        return VariableFactory.createVariable("INTEGER", null, var1.GET() + var2.toInt(), var1.getContext()); // castuje do inta i zaokrągla
    }

    @Override
    public Variable performOperation(DoubleVariable var1, DoubleVariable var2) {
        return VariableFactory.createVariable("DOUBLE", null, var1.GET() + var2.GET(), var1.getContext());
    }

    @Override
    public Variable performOperation(BoolVariable var1, StringVariable var2) {
        return VariableFactory.createVariable("BOOL", null, false, var1.getContext()); // cokolwiek bym nie dodawał, dostaje FALSE
    }

    @Override
    public Variable performOperation(StringVariable var1, BoolVariable var2) {
        return VariableFactory.createVariable("STRING", null, var1.GET()+var2.toString(), var1.getContext()); // dodaje literalnie TRUE lub FALSE
    }

    @Override
    public Variable performOperation(BoolVariable var1, BoolVariable var2) {
        return VariableFactory.createVariable("BOOL", null, var1.GET() && var2.GET(), var1.getContext()); // wygląda to dziwnie, ale jakby silnik zwracał iloczyn logiczny zamiast sumy, tj. i lewa strona i prawa muszą być TRUE, żeby dać TRUE (do sprawdzenia)
    }

    @Override
    public Variable performOperation(BoolVariable var1, IntegerVariable var2) {
        return var1; // tu wygląda, że zwraca lewy operand
    }

    @Override
    public Variable performOperation(IntegerVariable var1, BoolVariable var2) {
        return VariableFactory.createVariable("INTEGER", null, var1.GET() + var2.toInt(), var1.getContext());
    }

    @Override
    public Variable performOperation(BoolVariable var1, DoubleVariable var2) {
        return var1; // tu wygląda, że zwraca lewy operand
    }

    @Override
    public Variable performOperation(DoubleVariable var1, BoolVariable var2) {
        return VariableFactory.createVariable("DOUBLE", null, var1.GET() + var2.toDouble(), var1.getContext());
    }
}
