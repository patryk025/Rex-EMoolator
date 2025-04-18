package pl.genschu.bloomooemulator.interpreter.util;

import com.badlogic.gdx.Input;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class KeyboardsKeysMapper {
    private static final Map<Integer, String> KEY_MAPPING = new HashMap<>();
    private static final Map<String, Integer> KEY_REVERSE_MAPPING = new HashMap<>();
    static {
        // Function keys
        KEY_MAPPING.put(Input.Keys.F1, "F1");
        KEY_MAPPING.put(Input.Keys.F2, "F2");
        KEY_MAPPING.put(Input.Keys.F3, "F3");
        KEY_MAPPING.put(Input.Keys.F4, "F4");
        KEY_MAPPING.put(Input.Keys.F5, "F5");
        KEY_MAPPING.put(Input.Keys.F6, "F6");
        KEY_MAPPING.put(Input.Keys.F7, "F7");
        KEY_MAPPING.put(Input.Keys.F8, "F8");
        KEY_MAPPING.put(Input.Keys.F9, "F9");
        KEY_MAPPING.put(Input.Keys.F10, "F10");
        KEY_MAPPING.put(Input.Keys.F11, "F11");
        KEY_MAPPING.put(Input.Keys.F12, "F12");

        // Special keys
        KEY_MAPPING.put(Input.Keys.ESCAPE, "ESC");
        KEY_MAPPING.put(Input.Keys.INSERT, "INSERT");
        KEY_MAPPING.put(Input.Keys.PAGE_UP, "PGUP");
        KEY_MAPPING.put(Input.Keys.PAGE_DOWN, "PGDN");
        KEY_MAPPING.put(Input.Keys.HOME, "HOME");
        KEY_MAPPING.put(Input.Keys.CAPS_LOCK, "CAPSLOCK");
        KEY_MAPPING.put(Input.Keys.SHIFT_LEFT, "LSHIFT");
        KEY_MAPPING.put(Input.Keys.SHIFT_RIGHT, "RSHIFT");
        KEY_MAPPING.put(Input.Keys.CONTROL_LEFT, "LCTRL");
        KEY_MAPPING.put(Input.Keys.CONTROL_RIGHT, "RCTRL");
        KEY_MAPPING.put(Input.Keys.ALT_LEFT, "LALT");
        KEY_MAPPING.put(Input.Keys.ALT_RIGHT, "RALT");
        KEY_MAPPING.put(Input.Keys.ENTER, "ENTER");
        KEY_MAPPING.put(Input.Keys.SPACE, "SPACE");
        KEY_MAPPING.put(Input.Keys.TAB, "TAB");

        // Letters
        KEY_MAPPING.put(Input.Keys.Q, "Q");
        KEY_MAPPING.put(Input.Keys.W, "W");
        KEY_MAPPING.put(Input.Keys.E, "E");
        KEY_MAPPING.put(Input.Keys.R, "R");
        KEY_MAPPING.put(Input.Keys.T, "T");
        KEY_MAPPING.put(Input.Keys.Y, "Y");
        KEY_MAPPING.put(Input.Keys.U, "U");
        KEY_MAPPING.put(Input.Keys.I, "I");
        KEY_MAPPING.put(Input.Keys.O, "O");
        KEY_MAPPING.put(Input.Keys.P, "P");
        KEY_MAPPING.put(Input.Keys.A, "A");
        KEY_MAPPING.put(Input.Keys.S, "S");
        KEY_MAPPING.put(Input.Keys.D, "D");
        KEY_MAPPING.put(Input.Keys.F, "F");
        KEY_MAPPING.put(Input.Keys.G, "G");
        KEY_MAPPING.put(Input.Keys.H, "H");
        KEY_MAPPING.put(Input.Keys.J, "J");
        KEY_MAPPING.put(Input.Keys.K, "K");
        KEY_MAPPING.put(Input.Keys.L, "L");
        KEY_MAPPING.put(Input.Keys.Z, "Z");
        KEY_MAPPING.put(Input.Keys.X, "X");
        KEY_MAPPING.put(Input.Keys.C, "C");
        KEY_MAPPING.put(Input.Keys.V, "V");
        KEY_MAPPING.put(Input.Keys.B, "B");
        KEY_MAPPING.put(Input.Keys.N, "N");
        KEY_MAPPING.put(Input.Keys.M, "M");

        // Numbers
        KEY_MAPPING.put(Input.Keys.NUM_0, "0");
        KEY_MAPPING.put(Input.Keys.NUM_1, "1");
        KEY_MAPPING.put(Input.Keys.NUM_2, "2");
        KEY_MAPPING.put(Input.Keys.NUM_3, "3");
        KEY_MAPPING.put(Input.Keys.NUM_4, "4");
        KEY_MAPPING.put(Input.Keys.NUM_5, "5");
        KEY_MAPPING.put(Input.Keys.NUM_6, "6");
        KEY_MAPPING.put(Input.Keys.NUM_7, "7");
        KEY_MAPPING.put(Input.Keys.NUM_8, "8");
        KEY_MAPPING.put(Input.Keys.NUM_9, "9");

        // Arrow keys
        KEY_MAPPING.put(Input.Keys.LEFT, "LEFT");
        KEY_MAPPING.put(Input.Keys.RIGHT, "RIGHT");
        KEY_MAPPING.put(Input.Keys.UP, "UP");
        KEY_MAPPING.put(Input.Keys.DOWN, "DOWN");

        for (Map.Entry<Integer, String> entry : KEY_MAPPING.entrySet()) {
            KEY_REVERSE_MAPPING.put(entry.getValue(), entry.getKey());
        }
    }

    public static String getMappedKey(int key) {
        return KEY_MAPPING.get(key);
    }

    public static Set<Integer> getKeySet() {
        return KEY_MAPPING.keySet();
    }

    public static int getKeyCode(String key) {
        Integer keyCode = KEY_REVERSE_MAPPING.get(key);
        return (keyCode != null) ? keyCode : -1;
    }
}
