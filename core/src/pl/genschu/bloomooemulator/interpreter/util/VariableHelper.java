package pl.genschu.bloomooemulator.interpreter.util;

import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.interpreter.factories.VariableFactory;
import pl.genschu.bloomooemulator.interpreter.ast.expressions.*;

public class VariableHelper {
    public static Variable getVariableFromObject(Object value, Context context) {
        if (value instanceof MethodCallExpression) {
            return (Variable) ((MethodCallExpression) value).evaluate(context);
        }
        else if(value instanceof ArithmeticExpression) {
            return VariableFactory.createVariableWithAutoType("",  ((ConstantExpression) ((ArithmeticExpression) value).evaluate(context)).evaluate(null).toString(), context);
        }
        else if(value instanceof ConstantExpression) {
            Object valueString = ((ConstantExpression) value).evaluate(null);

            if(context.getVariable(valueString.toString()) == null) {
                return VariableFactory.createVariableWithAutoType("", valueString, context);
            }

            return context.getVariable(valueString.toString());
        }
        else if(value instanceof VariableExpression) {
            return (Variable) ((VariableExpression) value).evaluate(context);
        }
        else if(value instanceof PointerExpression) {
            return context.getVariable(((PointerExpression) value).evaluate(context).toString());
        }

        return null;
    }
}
