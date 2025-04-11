package pl.genschu.bloomooemulator.engine.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import pl.genschu.bloomooemulator.engine.Game;
import pl.genschu.bloomooemulator.interpreter.util.KeyboardsKeysMapper;
import pl.genschu.bloomooemulator.interpreter.variable.types.KeyboardVariable;

import java.util.Set;

public class KeyboardHandler {
    private final Game game;

    public KeyboardHandler(Game game) {
        this.game = game;
    }

    public void handleKeyboardInput(KeyboardVariable keyboardVariable,
                                    Set<Integer> currentlyPressedKeys,
                                    Set<Integer> previouslyPressedKeys) {
        if (keyboardVariable == null || !keyboardVariable.isEnabled()) {
            return;
        }

        // Clear the current state of the keys
        currentlyPressedKeys.clear();

        // Check all keys defined in the mapper
        for (int keyCode : KeyboardsKeysMapper.getKeySet()) {
            String keyName = KeyboardsKeysMapper.getMappedKey(keyCode);

            boolean isPressed = Gdx.input.isKeyPressed(keyCode);
            boolean wasPressed = previouslyPressedKeys.contains(keyCode);

            if (isPressed) {
                // Add key to the set of currently pressed keys
                currentlyPressedKeys.add(keyCode);

                // Send the signal only if the key is pressed for the first time
                // or auto-repeat is enabled
                if (!wasPressed || keyboardVariable.isAutoRepeat()) {
                    keyboardVariable.emitSignal("ONKEYDOWN", keyName);
                }

                // Send the ONCHAR signal only if the key is pressed for the first time
                if (!wasPressed) {
                    keyboardVariable.emitSignal("ONCHAR", keyName);
                }
            } else if (wasPressed) {
                // Key was pressed and now it is released
                keyboardVariable.emitSignal("ONKEYUP", keyName);
            }
        }
    }
}