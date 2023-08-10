package pl.cba.genszu.amcodetranslator.interpreter.arithmetic;

import pl.cba.genszu.amcodetranslator.interpreter.Variable;
import pl.cba.genszu.amcodetranslator.interpreter.exceptions.VariableUnsupportedOperationException;
import pl.cba.genszu.amcodetranslator.interpreter.variabletypes.DoubleVariable;
import pl.cba.genszu.amcodetranslator.interpreter.variabletypes.IntegerVariable;
import pl.cba.genszu.amcodetranslator.interpreter.variabletypes.StringVariable;

public abstract class ArithmeticOperation {
    public Variable performOperation(Variable var1, Variable var2) {
        switch (var1.getType()) {
            case "STRING":
                if (var2.getType().equals("STRING")) {
                    return performOperation((StringVariable) var1, (StringVariable) var2);
                } else if (var2.getType().equals("INTEGER")) {
                    return performOperation((StringVariable) var1, (IntegerVariable) var2);
                } else if (var2.getType().equals("DOUBLE")) {
                    return performOperation((StringVariable) var1, (DoubleVariable) var2);
                } else {
                    throw new VariableUnsupportedOperationException(var1, var2, this.getOperation());
                }
            case "INTEGER":
                if (var2.getType().equals("STRING")) {
                    return performOperation((IntegerVariable) var1, (StringVariable) var2);
                } else if (var2.getType().equals("INTEGER")) {
                    return performOperation((IntegerVariable) var1, (IntegerVariable) var2);
                } else if (var2.getType().equals("DOUBLE")) {
                    return performOperation((IntegerVariable) var1, (DoubleVariable) var2);
                } else {
                    throw new VariableUnsupportedOperationException(var1, var2, this.getOperation());
                }
            case "DOUBLE":
                if (var2.getType().equals("STRING")) {
                    return performOperation((DoubleVariable) var1, (StringVariable) var2);
                } else if (var2.getType().equals("INTEGER")) {
                    return performOperation((DoubleVariable) var1, (IntegerVariable) var2);
                } else if (var2.getType().equals("DOUBLE")) {
                    return performOperation((DoubleVariable) var1, (DoubleVariable) var2);
                } else {
                    throw new VariableUnsupportedOperationException(var1, var2, this.getOperation());
                }
            default:
                throw new VariableUnsupportedOperationException(var1, var2, this.getOperation());
        }
    }

    abstract Variable performOperation(StringVariable var1, StringVariable var2);

    abstract Variable performOperation(IntegerVariable var1, StringVariable var2);

    abstract Variable performOperation(StringVariable var1, IntegerVariable var2);

    abstract Variable performOperation(DoubleVariable var1, StringVariable var2);

    abstract Variable performOperation(StringVariable var1, DoubleVariable var2);

    abstract Variable performOperation(IntegerVariable var1, IntegerVariable var2);

    abstract Variable performOperation(DoubleVariable var1, IntegerVariable var2);

    abstract Variable performOperation(IntegerVariable var1, DoubleVariable var2);

    abstract Variable performOperation(DoubleVariable var1, DoubleVariable var2);

    public String getOperation() {
        return this.getClass().getSimpleName().split("Operation")[0].toLowerCase();
    }
}