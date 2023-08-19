package pl.cba.genszu.amcodetranslator.interpreter.factories;

import pl.cba.genszu.amcodetranslator.interpreter.*;
import pl.cba.genszu.amcodetranslator.interpreter.variabletypes.*;
import pl.cba.genszu.amcodetranslator.utils.TypeGuesser;

public class VariableFactory
{
    public static Variable createVariable(String name, Object value) {
        return createVariable(TypeGuesser.guessNumber(""+value), name, value);
    }

    public static Variable createVariable(String type, String name, Object value) {
        switch (type.toUpperCase()) {
            case "ANIMO":
                return new AnimoVariable(name, value);
            case "APPLICATION":
                return new ApplicationVariable(name, value);
            case "ARRAY":
                return new ArrayVariable(name, value);
            case "BEHAVIOUR":
                return new BehaviourVariable(name, value);
            case "BOOL":
                return new BoolVariable(name, value);
            case "BUTTON":
                return new ButtonVariable(name, value);
            case "CANVASOBSERVER":
                return new CanvasObserverVariable(name, value);
            case "CLASS":
                return new ClassVariable(name, value);
            case "CNVLOADER":
                return new CNVLoaderVariable(name, value);
            case "COMPLEXCONDITION":
                return new ComplexConditionVariable(name, value);
            case "CONDITION":
                return new ConditionVariable(name, value);
            case "DATABASE":
                return new DatabaseVariable(name, value);
            case "DOUBLE":
                return new DoubleVariable(name, value);
            case "EPISODE":
                return new EpisodeVariable(name, value);
            case "EXPRESSION":
                return new ExpressionVariable(name, value);
            case "FILTER":
                return new FilterVariable(name, value);
            case "FONT":
                return new FontVariable(name, value);
            case "GROUP":
                return new GroupVariable(name, value);
            case "IMAGE":
                return new ImageVariable(name, value);
            case "INERTIA":
                return new InertiaVariable(name, value);
            case "INTEGER":
                return new IntegerVariable(name, value);
            case "KEYBOARD":
                return new KeyboardVariable(name, value);
            case "MATRIX":
                return new MatrixVariable(name, value);
            case "MOUSE":
                return new MouseVariable(name, value);
            case "MULTIARRAY":
                return new MultiArrayVariable(name, value);
            case "MUSIC":
                return new MusicVariable(name, value);
            case "PATTERN":
                return new PatternVariable(name, value);
            case "RAND":
                return new RandVariable(name, value);
            case "SCENE":
                return new SceneVariable(name, value);
            case "SEQUENCE":
                return new SequenceVariable(name, value);
            case "SIMPLE":
                return new SimpleVariable(name, value);
            case "SOUND":
                return new SoundVariable(name, value);
            case "SPEAKING":
                return new SpeakingVariable(name, value);
            case "STATICFILTER":
                return new StaticFilterVariable(name, value);
            case "STRING":
                return new StringVariable(name, value);
            case "STRUCT":
                return new StructVariable(name, value);
            case "TEXT":
                return new TextVariable(name, value);
            case "TIMER":
                return new TimerVariable(name, value);
            case "VECTOR":
                return new VectorVariable(name, value);
            case "WORLD":
                return new WorldVariable(name, value);
            case "VOID":
                return new Variable(null); //void
            case "OPCODE":
                return new Variable(name); //zmienna na operacje (break, continue, onebreak)
            default:
                throw new IllegalArgumentException("Unknown variable type: " + type);
        }
    }
}
