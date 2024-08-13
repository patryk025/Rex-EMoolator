package pl.genschu.bloomooemulator.interpreter.util;

import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.interpreter.factories.VariableFactory;
import pl.genschu.bloomooemulator.interpreter.ast.expressions.*;
import pl.genschu.bloomooemulator.interpreter.variable.types.ApplicationVariable;
import pl.genschu.bloomooemulator.interpreter.variable.types.EpisodeVariable;
import pl.genschu.bloomooemulator.interpreter.variable.types.SceneVariable;
import pl.genschu.bloomooemulator.interpreter.variable.types.StringVariable;

public class VariableHelper {
    public static Variable getVariableFromObject(Object value, Context context) {
        return getVariableFromObject(value, context, false);
    }

    public static Variable getVariableFromObject(Object value, Context context, boolean resolveString) {
        if (value instanceof MethodCallExpression) {
            return (Variable) ((MethodCallExpression) value).evaluate(context);
        }
        else if(value instanceof ArithmeticExpression) {
            return VariableFactory.createVariableWithAutoType("",  ((ConstantExpression) ((ArithmeticExpression) value).evaluate(context)).evaluate(context).toString(), context);
        }
        else if(value instanceof ConstantExpression) {
            Object valueString = ((ConstantExpression) value).evaluate(context);

            if(resolveString) {
                if(!context.hasVariable(valueString.toString())) {
                    return VariableFactory.createVariableWithAutoType("", valueString, context);
                }

                Variable variable = context.getVariable(valueString.toString());

                if(variable instanceof ApplicationVariable || variable instanceof EpisodeVariable || variable instanceof SceneVariable) { // it should not be returned
                    return VariableFactory.createVariableWithAutoType("", valueString.toString(), context);
                }

                return context.getVariable(valueString.toString());
            }

            if(valueString.toString().equals("_I_")) {
                return context.getVariable(valueString.toString());
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
        if (value instanceof String) {
            if(resolveString) {
                return context.getVariable((String) value);
            }
            return new StringVariable("", (String) value, context);
        }

        return null;
    }

    public static int getValueFromString(Variable value) {
        try {
            return Integer.parseInt(value.getValue().toString());
        } catch (NumberFormatException e) {
            Variable variable = value.getContext().getVariable(value.getValue().toString());
            if(variable == null) {
                return 0;
            }
            return Integer.parseInt(variable.getValue().toString());
        }
    }
}
