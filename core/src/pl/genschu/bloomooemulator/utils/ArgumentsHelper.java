package pl.genschu.bloomooemulator.utils;

import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.interpreter.variable.types.*;

public class ArgumentsHelper {
    public static int getInteger(Object object) {
        return getInteger(object, true);
    }

    public static int getInteger(Object object, boolean resolveString) {
        if(object instanceof IntegerVariable) {
            return ((IntegerVariable) object).GET();
        }
        else if(object instanceof StringVariable) {
            if(resolveString) {
                String value = ((StringVariable) object).GET();
                Variable var = ((StringVariable) object).getContext().getVariable(value);
                if(isPrimitive(var)) {
                    return getInteger(var, false);
                }
                return ((StringVariable) object).toInt();
            }
            return ((StringVariable) object).toInt();
        }
        else if(object instanceof DoubleVariable) {
            return ((DoubleVariable) object).toInt();
        }
        else if(object instanceof BoolVariable) {
            return ((BoolVariable) object).toInt();
        }
        else if(object instanceof ExpressionVariable) {
            return getInteger(((ExpressionVariable) object).getValue(), resolveString);
        }
        else {
            return 0;
        }
    }

    public static boolean getBoolean(Object object) {
        return getBoolean(object, true);
    }

    public static boolean getBoolean(Object object, boolean resolveString) {
        if(object instanceof IntegerVariable) {
            return ((IntegerVariable) object).toBool();
        }
        else if(object instanceof StringVariable) {
            if(resolveString) {
                String value = ((StringVariable) object).GET();
                Variable var = ((StringVariable) object).getContext().getVariable(value);
                if(isPrimitive(var)) {
                    return getBoolean(var, false);
                }
                return ((StringVariable) object).toBool();
            }
            return ((StringVariable) object).toBool();
        }
        else if(object instanceof DoubleVariable) {
            return ((DoubleVariable) object).toBool();
        }
        else if(object instanceof BoolVariable) {
            return ((BoolVariable) object).GET();
        }
        else if(object instanceof ExpressionVariable) {
            return getBoolean(((ExpressionVariable) object).getValue(), resolveString);
        }
        else {
            return false;
        }
    }

    public static double getDouble(Object object) {
        return getDouble(object, true);
    }

    public static double getDouble(Object object, boolean resolveString) {
        if(object instanceof IntegerVariable) {
            return ((IntegerVariable) object).toDouble();
        }
        else if(object instanceof StringVariable) {
            if(resolveString) {
                String value = ((StringVariable) object).GET();
                Variable var = ((StringVariable) object).getContext().getVariable(value);
                if(isPrimitive(var)) {
                    return getDouble(var, false);
                }
                return ((StringVariable) object).toDouble();
            }
            return ((StringVariable) object).toDouble();
        }
        else if(object instanceof DoubleVariable) {
            return ((DoubleVariable) object).GET();
        }
        else if(object instanceof BoolVariable) {
            return ((BoolVariable) object).toDouble();
        }
        else if(object instanceof ExpressionVariable) {
            return getDouble(((ExpressionVariable) object).getValue(), resolveString);
        }
        else {
            return 0.0;
        }
    }

    public static String getString(Object object) {
        return getString(object, false);
    }

    public static String getString(Object object, boolean resolveString) {
        if(object instanceof IntegerVariable) {
            return ((IntegerVariable) object).toStringVariable();
        }
        else if(object instanceof StringVariable) {
            if(resolveString) {
                String value = ((StringVariable) object).GET();
                Variable var = ((StringVariable) object).getContext().getVariable(value);
                if(isPrimitive(var)) {
                    return getString(var, false);
                }
                return ((StringVariable) object).GET();
            }
            return ((StringVariable) object).GET();
        }
        else if(object instanceof DoubleVariable) {
            return ((DoubleVariable) object).toStringVariable();
        }
        else if(object instanceof BoolVariable) {
            return ((BoolVariable) object).toStringVariable();
        }
        else if(object instanceof ExpressionVariable) {
            return getString(((ExpressionVariable) object).getValue(), resolveString);
        }
        else if(object instanceof Variable) {
            return ((Variable) object).getName();
        }
        else {
            return object.toString();
        }
    }

    private static boolean isPrimitive(Variable var) {
        return var instanceof IntegerVariable || var instanceof StringVariable || var instanceof DoubleVariable || var instanceof BoolVariable || var instanceof ExpressionVariable;
    }
}
