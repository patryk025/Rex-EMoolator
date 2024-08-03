package pl.genschu.bloomooemulator.utils;

import com.sun.jdi.BooleanValue;
import pl.genschu.bloomooemulator.interpreter.variable.types.BoolVariable;
import pl.genschu.bloomooemulator.interpreter.variable.types.DoubleVariable;
import pl.genschu.bloomooemulator.interpreter.variable.types.IntegerVariable;
import pl.genschu.bloomooemulator.interpreter.variable.types.StringVariable;

public class ArgumentsHelper {
    public static int getInteger(Object object) {
        if(object instanceof IntegerVariable) {
            return ((IntegerVariable) object).GET();
        }
        else if(object instanceof StringVariable) {
            return ((StringVariable) object).toInt();
        }
        else if(object instanceof DoubleVariable) {
            return ((DoubleVariable) object).toInt();
        }
        else if(object instanceof BoolVariable) {
            return ((BoolVariable) object).toInt();
        }
        else {
            return 0;
        }
    }

    public static boolean getBoolean(Object object) {
        if(object instanceof IntegerVariable) {
            return ((IntegerVariable) object).toBool();
        }
        else if(object instanceof StringVariable) {
            return ((StringVariable) object).toBool();
        }
        else if(object instanceof DoubleVariable) {
            return ((DoubleVariable) object).toBool();
        }
        else if(object instanceof BoolVariable) {
            return ((BoolVariable) object).GET();
        }
        else {
            return false;
        }
    }

    public static double getDouble(Object object) {
        if(object instanceof IntegerVariable) {
            return ((IntegerVariable) object).toDouble();
        }
        else if(object instanceof StringVariable) {
            return ((StringVariable) object).toDouble();
        }
        else if(object instanceof DoubleVariable) {
            return ((DoubleVariable) object).GET();
        }
        else if(object instanceof BoolVariable) {
            return ((BoolVariable) object).toDouble();
        }
        else {
            return 0.0;
        }
    }

    public static String getString(Object object) {
        if(object instanceof IntegerVariable) {
            return ((IntegerVariable) object).toStringVariable();
        }
        else if(object instanceof StringVariable) {
            return ((StringVariable) object).GET();
        }
        else if(object instanceof DoubleVariable) {
            return ((DoubleVariable) object).toStringVariable();
        }
        else if(object instanceof BoolVariable) {
            return ((BoolVariable) object).toStringVariable();
        }
        else {
            return object.toString();
        }
    }
}
