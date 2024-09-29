package pl.genschu.bloomooemulator.interpreter.util;

import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.variable.types.KeyboardVariable;
import pl.genschu.bloomooemulator.interpreter.variable.types.MouseVariable;
import pl.genschu.bloomooemulator.interpreter.variable.types.RandVariable;
import pl.genschu.bloomooemulator.interpreter.variable.types.SystemVariable;

// This class holds global variables
public class GlobalVariables {
    private static MouseVariable globalMouse;
    private static KeyboardVariable globalKeyboard;
    private static RandVariable globalRandom;
    private static SystemVariable globalSystem;

    public static MouseVariable getMouseVariable(Context context) {
        if (globalMouse == null) {
            globalMouse = new MouseVariable("MOUSE", context.getGame().getCurrentApplicationContext());
        }
        return globalMouse;
    }

    public static KeyboardVariable getKeyboardVariable(Context context) {
        if (globalKeyboard == null) {
            globalKeyboard = new KeyboardVariable("KEYBOARD", context.getGame().getCurrentApplicationContext());
        }
        return globalKeyboard;
    }

    public static RandVariable getRandomVariable(Context context) {
        if (globalRandom == null) {
            globalRandom = new RandVariable("RAND", context.getGame().getCurrentApplicationContext());
        }
        return globalRandom;
    }

    public static SystemVariable getSystemVariable(Context context) {
        if (globalSystem == null) {
            globalSystem = new SystemVariable("SYSTEM", context.getGame().getCurrentApplicationContext());
        }
        return globalSystem;
    }
}

