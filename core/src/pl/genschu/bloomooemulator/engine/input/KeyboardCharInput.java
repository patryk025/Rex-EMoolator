package pl.genschu.bloomooemulator.engine.input;

import com.badlogic.gdx.InputAdapter;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * Buffers characters typed during a frame so the polling-based {@link InputManager}
 * can drain them and emit ONCHAR signals.
 *
 * <p>This mirrors the original engine's {@code CMC_Keyboard::onChar} channel
 * (WM_CHAR), which is keyboard-layout and Shift aware and is the only source of
 * digits, punctuation and case-sensitive letters. The named-key channel
 * ({@code getKeySignal} -> ONKEYDOWN/ONKEYUP) deliberately does not carry those.
 */
public class KeyboardCharInput extends InputAdapter {
    private final Deque<Character> typed = new ArrayDeque<>();

    @Override
    public boolean keyTyped(char character) {
        // WM_CHAR also delivers control codes (backspace, enter, tab, escape...).
        // Scripts only react to printable characters, so drop control chars to
        // avoid emitting ONCHAR signals with control-character names.
        if (!Character.isISOControl(character)) {
            typed.add(character);
        }
        return false;
    }

    /** Removes and returns all characters buffered since the last drain. */
    public List<Character> drain() {
        if (typed.isEmpty()) {
            return List.of();
        }
        List<Character> out = new ArrayList<>(typed);
        typed.clear();
        return out;
    }
}
