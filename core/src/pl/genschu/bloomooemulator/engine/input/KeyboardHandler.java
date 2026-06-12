package pl.genschu.bloomooemulator.engine.input;

import com.badlogic.gdx.Gdx;
import pl.genschu.bloomooemulator.engine.Game;
import pl.genschu.bloomooemulator.interpreter.values.StringValue;
import pl.genschu.bloomooemulator.interpreter.variable.KeyboardVariable;
import pl.genschu.bloomooemulator.utils.KeyboardKeysMapper;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public class KeyboardHandler {
    private final Game game;

    public KeyboardHandler(Game game) {
        this.game = game;
    }

    public void handleKeyboardInput(KeyboardVariable keyboardVariable,
                                    Set<Integer> currentlyPressedKeys,
                                    Set<Integer> previouslyPressedKeys) {
        handleKeyboardInput(keyboardVariable != null ? List.of(keyboardVariable) : List.of(),
                currentlyPressedKeys, previouslyPressedKeys, List.of());
    }

    public void handleKeyboardInput(Collection<KeyboardVariable> keyboardVariables,
                                    Set<Integer> currentlyPressedKeys,
                                    Set<Integer> previouslyPressedKeys) {
        handleKeyboardInput(keyboardVariables, currentlyPressedKeys, previouslyPressedKeys, List.of());
    }

    /**
     * Processes one frame of keyboard input across two channels:
     * <ul>
     *   <li><b>Named-key channel</b> (polling over {@link KeyboardKeysMapper}):
     *       emits ONKEYDOWN/ONKEYUP. This matches {@code getKeySignal}, so digits
     *       and punctuation are absent here by design.</li>
     *   <li><b>Character channel</b> ({@code typedChars} from {@link KeyboardCharInput}):
     *       emits ONCHAR. This matches {@code onChar} (WM_CHAR) and is the source of
     *       digits, punctuation and case-sensitive letters.</li>
     * </ul>
     * All three events update the latest key (GETLATESTKEY), last writer wins.
     */
    public void handleKeyboardInput(Collection<KeyboardVariable> keyboardVariables,
                                    Set<Integer> currentlyPressedKeys,
                                    Set<Integer> previouslyPressedKeys,
                                    List<Character> typedChars) {
        if (keyboardVariables == null || keyboardVariables.isEmpty()) {
            return;
        }

        // Clear the current state of the keys
        currentlyPressedKeys.clear();

        // Named-key channel: ONKEYDOWN / ONKEYUP.
        for (int keyCode : KeyboardKeysMapper.getKeySet()) {
            String keyName = KeyboardKeysMapper.getMappedKey(keyCode);

            boolean isPressed = Gdx.input.isKeyPressed(keyCode);
            boolean wasPressed = previouslyPressedKeys.contains(keyCode);

            if (isPressed) {
                // Add key to the set of currently pressed keys
                currentlyPressedKeys.add(keyCode);

                // Send the signal only if the key is pressed for the first time
                // or auto-repeat is enabled
                for (KeyboardVariable keyboardVariable : keyboardVariables) {
                    if (!keyboardVariable.isEnabled()) {
                        continue;
                    }
                    if (!wasPressed || keyboardVariable.isAutoRepeat()) {
                        keyboardVariable.setLatestKey(keyName);
                        keyboardVariable.emitSignal("ONKEYDOWN", new StringValue(keyName));
                    }
                }
            } else if (wasPressed) {
                // Key was pressed and now it is released
                for (KeyboardVariable keyboardVariable : keyboardVariables) {
                    if (keyboardVariable.isEnabled()) {
                        keyboardVariable.setLatestKey(keyName);
                        keyboardVariable.emitSignal("ONKEYUP", new StringValue(keyName));
                    }
                }
            }
        }

        // Character channel: ONCHAR.
        if (typedChars != null) {
            for (char ch : typedChars) {
                String charStr = String.valueOf(ch);
                for (KeyboardVariable keyboardVariable : keyboardVariables) {
                    if (keyboardVariable.isEnabled()) {
                        keyboardVariable.setLatestKey(charStr);
                        keyboardVariable.emitSignal("ONCHAR", new StringValue(charStr));
                    }
                }
            }
        }
    }
}
