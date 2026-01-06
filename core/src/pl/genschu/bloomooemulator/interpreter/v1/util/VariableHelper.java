package pl.genschu.bloomooemulator.interpreter.v1.util;

import pl.genschu.bloomooemulator.interpreter.v1.Context;
import pl.genschu.bloomooemulator.interpreter.v1.variable.Variable;
import pl.genschu.bloomooemulator.interpreter.factories.LegacyVariableFactory;
import pl.genschu.bloomooemulator.interpreter.v1.ast.expressions.*;
import pl.genschu.bloomooemulator.interpreter.v1.variable.types.*;

@Deprecated(forRemoval = true, since = "0.2.0-beta")
public class VariableHelper {
    public static Variable getVariableFromObject(Object value, Context context) {
        if (value instanceof MethodCallExpression) {
            return (Variable) ((MethodCallExpression) value).evaluate(context);
        }
        else if(value instanceof ArithmeticExpression) {
            return LegacyVariableFactory.createVariableWithAutoType("",  ((ConstantExpression) ((ArithmeticExpression) value).evaluate(context)).evaluate(context).toString(), context);
        }
        else if(value instanceof ConstantExpression) {
            Object valueString = ((ConstantExpression) value).evaluate(context);

            if(valueString.toString().equals("_I_")) {
                return context.getVariable(valueString.toString());
            }
            if(valueString instanceof Integer) {
                return new IntegerVariable("", (Integer) valueString, context);
            }
            else if(valueString instanceof Double) {
                return new DoubleVariable("", (Double) valueString, context);
            }
            return new StringVariable("", valueString.toString(), context);
        }
        else if(value instanceof VariableExpression) {
            return (Variable) ((VariableExpression) value).evaluate(context);
        }
        else if(value instanceof PointerExpression) {
            return context.getVariable(((PointerExpression) value).evaluate(context).toString());
        }
        else if(value instanceof StructExpression) {
            return (Variable) ((StructExpression) value).evaluate(context);
        }
        else if(value instanceof ConditionExpression) {
            return (Variable) ((ConstantExpression) ((ConditionExpression) value).evaluate(context)).evaluate(context);
        }
        if (value instanceof String) {
            String tmp  = value.toString();
            if(tmp.startsWith("\"") && tmp.endsWith("\"")) {
                return new StringVariable("", tmp.substring(1, tmp.length() - 1), context);
            }
            return context.getVariable(tmp);
        }

        return null;
    }
}
