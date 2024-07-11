package pl.genschu.bloomooemulator.interpreter.arithmetic;

import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.interpreter.exceptions.VariableUnsupportedOperationException;
import pl.genschu.bloomooemulator.interpreter.variable.types.BoolVariable;
import pl.genschu.bloomooemulator.interpreter.variable.types.DoubleVariable;
import pl.genschu.bloomooemulator.interpreter.variable.types.IntegerVariable;
import pl.genschu.bloomooemulator.interpreter.variable.types.StringVariable;

public abstract class ArithmeticOperation {
    public Variable performOperation(Variable var1, Variable var2) {
		// System.out.println("("+var1.getType()+") "+var1.getValue()+", ("+var2.getType()+") "+var2.getValue());
        switch (var1.getType()) {
            case "STRING":
                if (var2.getType().equals("STRING")) {
                    return performOperation((StringVariable) var1, (StringVariable) var2);
                } else if (var2.getType().equals("INTEGER")) {
                    return performOperation((StringVariable) var1, (IntegerVariable) var2);
                } else if (var2.getType().equals("DOUBLE")) {
                    return performOperation((StringVariable) var1, (DoubleVariable) var2);
                } else if (var2.getType().equals("BOOL")) {
                    return performOperation((StringVariable) var1, (BoolVariable) var2);
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
                } else if (var2.getType().equals("BOOL")) {
                    return performOperation((IntegerVariable) var1, (BoolVariable) var2);
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
                } else if (var2.getType().equals("BOOL")) {
                    return performOperation((DoubleVariable) var1, (BoolVariable) var2);
                } else {
                    throw new VariableUnsupportedOperationException(var1, var2, this.getOperation());
                }
			case "BOOL":
                if (var2.getType().equals("STRING")) {
                    return performOperation((BoolVariable) var1, (StringVariable) var2);
                } else if (var2.getType().equals("INTEGER")) {
                    return performOperation((BoolVariable) var1, (IntegerVariable) var2);
                } else if (var2.getType().equals("DOUBLE")) {
                    return performOperation((BoolVariable) var1, (DoubleVariable) var2);
                } else if (var2.getType().equals("BOOL")) {
                    return performOperation((BoolVariable) var1, (BoolVariable) var2);
                } else {
                    throw new VariableUnsupportedOperationException(var1, var2, this.getOperation());
                }
            default:
                throw new VariableUnsupportedOperationException(var1, var2, this.getOperation());
        }
    }

    public abstract Variable performOperation(StringVariable var1, StringVariable var2);

    public abstract Variable performOperation(IntegerVariable var1, StringVariable var2);

    public abstract Variable performOperation(StringVariable var1, IntegerVariable var2);

    public abstract Variable performOperation(DoubleVariable var1, StringVariable var2);

    public abstract Variable performOperation(StringVariable var1, DoubleVariable var2);

    public abstract Variable performOperation(IntegerVariable var1, IntegerVariable var2);

    public abstract Variable performOperation(DoubleVariable var1, IntegerVariable var2);

    public abstract Variable performOperation(IntegerVariable var1, DoubleVariable var2);

    public abstract Variable performOperation(DoubleVariable var1, DoubleVariable var2);

    public abstract Variable performOperation(BoolVariable var1, StringVariable var2);

    public abstract Variable performOperation(StringVariable var1, BoolVariable var2);

    public abstract Variable performOperation(BoolVariable var1, BoolVariable var2);

    public abstract Variable performOperation(BoolVariable var1, IntegerVariable var2);

    public abstract Variable performOperation(IntegerVariable var1, BoolVariable var2);

    public abstract Variable performOperation(BoolVariable var1, DoubleVariable var2);

    public abstract Variable performOperation(DoubleVariable var1, BoolVariable var2);

    public String getOperation() {
        return this.getClass().getSimpleName().split("Operation")[0].toLowerCase();
    }
}
