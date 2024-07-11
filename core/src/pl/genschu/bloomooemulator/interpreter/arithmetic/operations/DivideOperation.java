package pl.genschu.bloomooemulator.interpreter.arithmetic.operations;

import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.interpreter.arithmetic.ArithmeticOperation;
import pl.genschu.bloomooemulator.interpreter.factories.VariableFactory;
import pl.genschu.bloomooemulator.interpreter.variable.types.BoolVariable;
import pl.genschu.bloomooemulator.interpreter.variable.types.DoubleVariable;
import pl.genschu.bloomooemulator.interpreter.variable.types.IntegerVariable;
import pl.genschu.bloomooemulator.interpreter.variable.types.StringVariable;

public class DivideOperation extends ArithmeticOperation {
    @Override
    public Variable performOperation(StringVariable var1, StringVariable var2) {
        double divisor = var2.toDouble();
        if (divisor == 0) { // odtwarzam zachowanie silnika, który się wywala nawet jak operacja jest nieprawidłowa
            throw new ArithmeticException("Division by zero");
        }
        return var1; // STRING się nie dzieli
    }

    @Override
    public Variable performOperation(IntegerVariable var1, StringVariable var2) {
        int divisor = var2.toInt();
        if (divisor == 0) {
            throw new ArithmeticException("Division by zero");
        }
        return VariableFactory.createVariable("INTEGER", null, var1.GET() / var2.toInt(), var1.getContext());
    }

    @Override
    public Variable performOperation(StringVariable var1, IntegerVariable var2) {
        int divisor = var2.GET();
        if (divisor == 0) {
            throw new ArithmeticException("Division by zero");
        }
        return var1; // STRING się nie dzieli
    }

    @Override
    public Variable performOperation(DoubleVariable var1, StringVariable var2) {
        double divisor = var2.toDouble();
        if (divisor == 0) {
            throw new ArithmeticException("Division by zero");
        }
        return VariableFactory.createVariable("DOUBLE", null, var1.GET() / var2.toDouble(), var1.getContext());
    }

    @Override
    public Variable performOperation(StringVariable var1, DoubleVariable var2) {
        double divisor = var2.GET();
        if (divisor == 0) {
            throw new ArithmeticException("Division by zero");
        }
        return var1; // STRING się nie dzieli
    }

    @Override
    public Variable performOperation(IntegerVariable var1, IntegerVariable var2) {
        int divisor = var2.GET();
        if (divisor == 0) {
            throw new ArithmeticException("Division by zero");
        }
        return VariableFactory.createVariable("INTEGER", null, var1.GET() / var2.GET(), var1.getContext());
    }

    @Override
    public Variable performOperation(DoubleVariable var1, IntegerVariable var2) {
        int divisor = var2.GET();
        if (divisor == 0) {
            throw new ArithmeticException("Division by zero");
        }
        return VariableFactory.createVariable("DOUBLE", null, var1.GET() / var2.GET(), var1.getContext());
    }

    @Override
    public Variable performOperation(IntegerVariable var1, DoubleVariable var2) {
        double divisor = var2.GET();
        if (divisor == 0) {
            throw new ArithmeticException("Division by zero");
        }
        return VariableFactory.createVariable("INTEGER", null, var1.GET() / var2.toInt(), var1.getContext());
    }

    @Override
    public Variable performOperation(DoubleVariable var1, DoubleVariable var2) {
        double divisor = var2.GET();
        if (divisor == 0) {
            throw new ArithmeticException("Division by zero");
        }
        return VariableFactory.createVariable("DOUBLE", null, var1.GET() / var2.GET(), var1.getContext());
    }

    @Override
    public Variable performOperation(BoolVariable var1, StringVariable var2) {
        double divisor = var2.toDouble();
        if (divisor == 0) {
            throw new ArithmeticException("Division by zero");
        }
        return var1;
    }

    @Override
    public Variable performOperation(StringVariable var1, BoolVariable var2) {
        int divisor = var2.toInt();
        if (divisor == 0) {
            throw new ArithmeticException("Division by zero");
        }
        return var1;
    }

    @Override
    public Variable performOperation(BoolVariable var1, BoolVariable var2) {
        int divisor = var2.toInt();
        if (divisor == 0) {
            throw new ArithmeticException("Division by zero");
        }
        return var1;
    }

    @Override
    public Variable performOperation(BoolVariable var1, IntegerVariable var2) {
        int divisor = var2.GET();
        if (divisor == 0) {
            throw new ArithmeticException("Division by zero");
        }
        return var1;
    }

    @Override
    public Variable performOperation(IntegerVariable var1, BoolVariable var2) {
        int divisor = var2.toInt();
        if (divisor == 0) {
            throw new ArithmeticException("Division by zero");
        }
        return VariableFactory.createVariable("INTEGER", null, var1.GET() / var2.toInt(), var1.getContext());
    }

    @Override
    public Variable performOperation(BoolVariable var1, DoubleVariable var2) {
        double divisor = var2.GET();
        if (divisor == 0) {
            throw new ArithmeticException("Division by zero");
        }
        return var1;
    }

    @Override
    public Variable performOperation(DoubleVariable var1, BoolVariable var2) {
        double divisor = var2.toDouble();
        if (divisor == 0) {
            throw new ArithmeticException("Division by zero");
        }
        return VariableFactory.createVariable("DOUBLE", null, var1.GET() / var2.toDouble(), var1.getContext());
    }
}
