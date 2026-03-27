package pl.genschu.bloomooemulator.interpreter.v1.util;

import pl.genschu.bloomooemulator.interpreter.v1.Context;
import pl.genschu.bloomooemulator.interpreter.v1.variable.GlobalVariable;
import pl.genschu.bloomooemulator.interpreter.v1.variable.types.KeyboardVariable;
import pl.genschu.bloomooemulator.interpreter.v1.variable.types.MouseVariable;
import pl.genschu.bloomooemulator.interpreter.v1.variable.types.RandVariable;
import pl.genschu.bloomooemulator.interpreter.v1.variable.types.SystemVariable;

// This class holds global variables
public class GlobalVariables {
    private static MouseVariable globalMouse;
    private static KeyboardVariable globalKeyboard;
    private static RandVariable globalRandom;
    private static SystemVariable globalSystem;

    private static MouseVariable getMouseVariable(Context context) {
        if (globalMouse == null) {
            globalMouse = new MouseVariable("MOUSE", (Context) context.getGame().getCurrentApplicationContext());
        }
        return globalMouse;
    }

    private static KeyboardVariable getKeyboardVariable(Context context) {
        if (globalKeyboard == null) {
            globalKeyboard = new KeyboardVariable("KEYBOARD", (Context) context.getGame().getCurrentApplicationContext());
        }
        return globalKeyboard;
    }

    private static RandVariable getRandomVariable(Context context) {
        if (globalRandom == null) {
            globalRandom = new RandVariable("RAND", (Context) context.getGame().getCurrentApplicationContext());
        }
        return globalRandom;
    }

    private static SystemVariable getSystemVariable(Context context) {
        if (globalSystem == null) {
            globalSystem = new SystemVariable("SYSTEM", (Context) context.getGame().getCurrentApplicationContext());
        }
        return globalSystem;
    }

    public static GlobalVariable getVariable(String name, Context context) {
        switch (name) {
            case "MOUSE":
                return getMouseVariable(context);
            case "KEYBOARD":
                return getKeyboardVariable(context);
            case "RAND":
            case "RANDOM":
                return getRandomVariable(context);
            case "SYSTEM":
                return getSystemVariable(context);
            default:
                return null;
        }
    }

    public static void reset() {
        globalMouse = null;
        globalKeyboard = null;
        globalRandom = null;
        globalSystem = null;
    }
}

