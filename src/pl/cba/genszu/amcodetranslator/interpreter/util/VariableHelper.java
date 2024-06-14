package pl.cba.genszu.amcodetranslator.interpreter.util;

import pl.cba.genszu.amcodetranslator.interpreter.Context;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Variable;
import pl.cba.genszu.amcodetranslator.interpreter.ast.expressions.*;
import pl.cba.genszu.amcodetranslator.interpreter.factories.VariableFactory;

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
