package pl.genschu.bloomooemulator.interpreter.v1.arithmetic.operations;

import pl.genschu.bloomooemulator.interpreter.v1.variable.Variable;
import pl.genschu.bloomooemulator.interpreter.v1.arithmetic.ArithmeticOperation;
import pl.genschu.bloomooemulator.interpreter.factories.LegacyVariableFactory;
import pl.genschu.bloomooemulator.interpreter.v1.variable.types.BoolVariable;
import pl.genschu.bloomooemulator.interpreter.v1.variable.types.DoubleVariable;
import pl.genschu.bloomooemulator.interpreter.v1.variable.types.IntegerVariable;
import pl.genschu.bloomooemulator.interpreter.v1.variable.types.StringVariable;

public class ModuloOperation extends ArithmeticOperation {
    @Override
    public Variable performOperation(StringVariable var1, StringVariable var2) {
        double divisor = var2.toDouble();
        if (divisor == 0) { // odtwarzam zachowanie silnika, który się wywala nawet jak operacja jest nieprawidłowa
            throw new ArithmeticException("Division by zero");
        }
        return var1; // STRING się nie dzieli i zwraca siebie jako resztę
    }

    @Override
    public Variable performOperation(IntegerVariable var1, StringVariable var2) {
        int divisor = var2.toInt();
        if (divisor == 0) {
            throw new ArithmeticException("Division by zero");
        }
        return LegacyVariableFactory.createVariable("INTEGER", null, var1.GET() % var2.toInt(), var1.getContext());
    }

    @Override
    public Variable performOperation(StringVariable var1, IntegerVariable var2) {
        int divisor = var2.GET();
        if (divisor == 0) {
            throw new ArithmeticException("Division by zero");
        }
        return var1; // STRING się nie dzieli i zwraca siebie jako resztę
    }

    @Override
    public Variable performOperation(DoubleVariable var1, StringVariable var2) {
        double divisor = var2.toDouble();
        if (divisor == 0) {
            throw new ArithmeticException("Division by zero");
        }
        return LegacyVariableFactory.createVariable("DOUBLE", null, (int) (var1.GET() % var2.toDouble()), var1.getContext()); //wygląda, że zwraca resztę bez części po przecinku
    }

    @Override
    public Variable performOperation(StringVariable var1, DoubleVariable var2) {
        double divisor = var2.GET();
        if (divisor == 0) {
            throw new ArithmeticException("Division by zero");
        }
        return var1; // STRING się nie dzieli i zwraca siebie jako resztę
    }

    @Override
    public Variable performOperation(IntegerVariable var1, IntegerVariable var2) {
        int divisor = var2.GET();
        if (divisor == 0) {
            throw new ArithmeticException("Division by zero");
        }
        return LegacyVariableFactory.createVariable("INTEGER", null, var1.GET() % var2.GET(), var1.getContext());
    }

    @Override
    public Variable performOperation(DoubleVariable var1, IntegerVariable var2) {
        int divisor = var2.GET();
        if (divisor == 0) {
            throw new ArithmeticException("Division by zero");
        }
        return LegacyVariableFactory.createVariable("DOUBLE", null, (int) (var1.GET() % var2.GET()), var1.getContext());
    }

    @Override
    public Variable performOperation(IntegerVariable var1, DoubleVariable var2) {
        double divisor = var2.GET();
        if (divisor == 0) {
            throw new ArithmeticException("Division by zero");
        }
        return LegacyVariableFactory.createVariable("INTEGER", null, var1.GET() % var2.toInt(), var1.getContext());
    }

    @Override
    public Variable performOperation(DoubleVariable var1, DoubleVariable var2) {
        double divisor = var2.GET();
        if (divisor == 0) {
            throw new ArithmeticException("Division by zero");
        }
        return LegacyVariableFactory.createVariable("DOUBLE", null, (int) (var1.GET() % var2.GET()), var1.getContext());
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
        return LegacyVariableFactory.createVariable("INTEGER", null, var1.GET() % var2.toInt(), var1.getContext());
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
        return LegacyVariableFactory.createVariable("DOUBLE", null, (int) (var1.GET() % var2.toDouble()), var1.getContext());
    }
}
