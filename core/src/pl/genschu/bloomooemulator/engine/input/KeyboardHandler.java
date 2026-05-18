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
                currentlyPressedKeys, previouslyPressedKeys);
    }

    public void handleKeyboardInput(Collection<KeyboardVariable> keyboardVariables,
                                    Set<Integer> currentlyPressedKeys,
                                    Set<Integer> previouslyPressedKeys) {
        if (keyboardVariables == null || keyboardVariables.isEmpty()) {
            return;
        }

        // Clear the current state of the keys
        currentlyPressedKeys.clear();

        // Check all keys defined in the mapper
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
                        keyboardVariable.emitSignal("ONKEYDOWN", new StringValue(keyName));
                    }
                }

                // Send the ONCHAR signal only if the key is pressed for the first time
                if (!wasPressed) {
                    for (KeyboardVariable keyboardVariable : keyboardVariables) {
                        if (keyboardVariable.isEnabled()) {
                            keyboardVariable.emitSignal("ONCHAR", new StringValue(keyName));
                        }
                    }
                }
            } else if (wasPressed) {
                // Key was pressed and now it is released
                for (KeyboardVariable keyboardVariable : keyboardVariables) {
                    if (keyboardVariable.isEnabled()) {
                        keyboardVariable.emitSignal("ONKEYUP", new StringValue(keyName));
                    }
                }
            }
        }
    }
}
