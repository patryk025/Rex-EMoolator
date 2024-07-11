package pl.genschu.bloomooemulator.interpreter.factories;

import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.variable.types.*;
import pl.genschu.bloomooemulator.interpreter.util.TypeGuesser;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;

import java.util.Objects;

public class VariableFactory
{
    public static Variable createVariableWithAutoType(String name, Object value, Context context) {
        return createVariable(TypeGuesser.guessType(""+value), name, value, context);
    }

    public static Variable createVariable(String type, String name, Context context) {
        return createVariable(Objects.requireNonNullElse(type, "VOID"), name, null, context);
    }

    public static Variable createVariable(String type, String name, Object value, Context context) {
        // System.out.println("VariableFactory, type: "+type+", valie: "+value);
		switch (type.toUpperCase()) {
            case "ANIMO":
                return new AnimoVariable(name, context);
            case "APPLICATION":
                return new ApplicationVariable(name, context);
            case "ARRAY":
                return new ArrayVariable(name, context);
            case "BEHAVIOUR":
                return new BehaviourVariable(name, context);
            case "BOOL":
                if (value instanceof Boolean) {
                    return new BoolVariable(name, (Boolean) value, context);
                } else if (value instanceof Integer) {
                    return new BoolVariable(name, (Integer) value != 0, context);
                } else if (value instanceof Double) {
                    return new BoolVariable(name, (Double) value != 0, context);
                }
                else if (value instanceof String) {
                    try {
                        return new BoolVariable(name, Boolean.parseBoolean((String) value), context);
                    } catch (NumberFormatException e) {
                        return new StringVariable(name, (String) value, context);
                    }
                } else {
                    return new StringVariable(name, value.toString(), context);
                }
            case "BUTTON":
                return new ButtonVariable(name, context);
            case "CANVASOBSERVER":
                return new CanvasObserverVariable(name, context);
            case "CLASS":
                return new ClassVariable(name, context);
            case "CNVLOADER":
                return new CNVLoaderVariable(name, context);
            case "COMPLEXCONDITION":
                return new ComplexConditionVariable(name, context);
            case "CONDITION":
                return new ConditionVariable(name, context);
            case "DATABASE":
                return new DatabaseVariable(name, context);
            case "DOUBLE":
                if (value instanceof Double) {
                    return new DoubleVariable(name, (Double) value, context);
                } else if (value instanceof Integer) {
                    return new DoubleVariable(name, ((Integer) value).doubleValue(), context);
                } else if (value instanceof String) {
                    try {
                        return new DoubleVariable(name, Double.parseDouble((String) value), context);
                    } catch (NumberFormatException e) {
                        return new StringVariable(name, (String) value, context);
                    }
                } else if(value instanceof Boolean) {
                    return new DoubleVariable(name, ((Boolean) value) ? 1.0 : 0.0, context);
                } else {
                    return new StringVariable(name, value.toString(), context);
                }
            case "EPISODE":
                return new EpisodeVariable(name, context);
            case "FONT":
                return new FontVariable(name, context);
            case "GROUP":
                return new GroupVariable(name, context);
            case "IMAGE":
                return new ImageVariable(name, context);
            case "INERTIA":
                return new InertiaVariable(name, context);
            case "INTEGER":
            case "INT":
                if (value instanceof Integer) {
                    return new IntegerVariable(name, (Integer) value, context);
                } else if (value instanceof Double) {
                    return new IntegerVariable(name, ((Double) value).intValue(), context);
                } else if (value instanceof Boolean) {
                    return new IntegerVariable(name, ((Boolean) value) ? 1 : 0, context);
                } else if (value instanceof String) {
                    try {
                        return new IntegerVariable(name, Integer.parseInt((String) value), context);
                    } catch (NumberFormatException e) {
                        return new StringVariable(name, (String) value, context);
                    }
                } else {
                    return new StringVariable(name, value.toString(), context);
                }
            case "KEYBOARD":
                return new KeyboardVariable(name, context);
            case "MATRIX":
                return new MatrixVariable(name, context);
            case "MOUSE":
                return new MouseVariable(name, context);
            case "MULTIARRAY":
                return new MultiArrayVariable(name, context);
            case "PATTERN":
                return new PatternVariable(name, context);
            case "SCENE":
                return new SceneVariable(name, context);
            case "SEQUENCE":
                return new SequenceVariable(name, context);
            /*case "SIMPLE": //TODO: implement this class
                return new SimpleVariable(name, context);*/
            case "SOUND":
                return new SoundVariable(name, context);
            /*case "SPEAKING": //TODO: implement this class
                return new SpeakingVariable(name, context);*/
            case "STATICFILTER":
                return new StaticFilterVariable(name, context);
            case "STRING":
                return new StringVariable(name, (String) value, context);
            case "STRUCT":
                return new StructVariable(name, context);
            case "TEXT":
                return new TextVariable(name, context);
            case "TIMER":
                return new TimerVariable(name, context);
            case "VECTOR":
                return new VectorVariable(name, context);
            case "WORLD":
                return new WorldVariable(name, context);
            default:
                throw new IllegalArgumentException("Unknown variable type: " + type);
        }
    }
}
