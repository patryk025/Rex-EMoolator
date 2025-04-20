package pl.genschu.bloomooemulator.engine.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.Viewport;
import pl.genschu.bloomooemulator.engine.Game;
import pl.genschu.bloomooemulator.engine.config.EngineConfig;
import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.variable.Signal;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.interpreter.variable.types.*;

import java.util.*;

public class InputManager implements Disposable {
    // Camera and viewport references
    private final OrthographicCamera camera;
    private final Viewport viewport;
    private final Game game;
    private final EngineConfig config;

    // Mouse state
    private final Vector2 mousePosition = new Vector2();
    private boolean mousePressed = false;
    private boolean mousePrevPressed = false;
    private Context lastMouseClickContext = null;

    // Keyboard state
    private final Set<Integer> pressedKeys = new HashSet<>();
    private final Set<Integer> previouslyPressedKeys = new HashSet<>();

    // Active button
    private Variable activeButton = null;

    // Input handlers
    private final ButtonHandler buttonHandler;
    private final KeyboardHandler keyboardHandler;

    public InputManager(OrthographicCamera camera, Viewport viewport, Game game, EngineConfig config) {
        this.camera = camera;
        this.viewport = viewport;
        this.game = game;
        this.config = config;

        this.buttonHandler = new ButtonHandler(game, this);
        this.keyboardHandler = new KeyboardHandler(game);
    }

    public void processInput(float deltaTime) {
        Context context = game.getCurrentSceneContext();

        // Get mouse and keyboard variables from the current context
        MouseVariable mouseVariable = context.getMouseVariable();
        KeyboardVariable keyboardVariable = context.getKeyboardVariable();

        // Handle mouse input
        processMouseInput(mouseVariable);

        // Handle keyboard input
        processKeyboardInput(keyboardVariable);

        // Handle debugging (F1, F2 keys)
        processDebugInput();
    }

    private void processMouseInput(MouseVariable mouseVariable) {
        int x = Gdx.input.getX();
        int y = Gdx.input.getY();

        if (x < 0 || y < 0 || x >= Gdx.graphics.getWidth() || y >= Gdx.graphics.getHeight()) {
            return;
        }

        // Mouse coordinates translation
        Vector2 correctedCoords = getCorrectedMouseCoords(x, y);
        int correctedX = (int) correctedCoords.x;
        int correctedY = (int) correctedCoords.y;

        // Update mouse position
        mousePosition.set(correctedX, correctedY);

        // Update mouse buttons state
        boolean isPressed = Gdx.input.isButtonPressed(Input.Buttons.LEFT);
        boolean justPressed = isPressed && !mousePrevPressed;
        boolean justReleased = !isPressed && mousePrevPressed;

        // Set last mouse click context
        if (justPressed) {
            lastMouseClickContext = game.getCurrentSceneContext();
        }

        // Check if the last mouse click was in the same context
        if (justReleased && lastMouseClickContext != game.getCurrentSceneContext()) {
            justReleased = false;
        }

        // Update mouse variable
        if (mouseVariable != null) {
            mouseVariable.update(correctedX, correctedY);
        }

        // Process button interactions
        buttonHandler.handleMouseInput(correctedX, correctedY, isPressed, justPressed, justReleased, mouseVariable);

        // Emit mouse signals
        if (mouseVariable != null && mouseVariable.isEmitSignals()) {
            if (justPressed) {
                mouseVariable.emitSignal("ONCLICK", "LEFT");
            } else if (justReleased) {
                mouseVariable.emitSignal("ONRELEASE", "LEFT");
            }
        }

        // Update mouse state
        mousePrevPressed = isPressed;
    }

    private void processKeyboardInput(KeyboardVariable keyboardVariable) {
        if (keyboardVariable == null || !keyboardVariable.isEnabled()) {
            return;
        }

        // Handle keyboard buttons input
        keyboardHandler.handleKeyboardInput(keyboardVariable, pressedKeys, previouslyPressedKeys);

        // Update keyboard buttons state
        previouslyPressedKeys.clear();
        previouslyPressedKeys.addAll(pressedKeys);
    }

    private void processDebugInput() {
        // Toggle debug variables
        if (Gdx.input.isKeyJustPressed(Input.Keys.F1)) {
            config.toggleDebugVariables();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.F2)) {
            config.toggleDebugGraphics();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.F3)) {
            config.toggleDebugButtons();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.F4)) {
            config.toggleDebugGraphicsBounds();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.F5)) {
            config.toggleDebugMatrix();
        }
    }

    // Method converting screen coordinates to world coordinates
    public Vector2 getCorrectedMouseCoords(int screenX, int screenY) {
        Vector2 result = new Vector2();

        float windowWidth = Gdx.graphics.getWidth();
        float windowHeight = Gdx.graphics.getHeight();
        float virtualWidth = viewport.getWorldWidth();
        float virtualHeight = viewport.getWorldHeight();

        // Calculate scales
        float aspectRatio = virtualWidth / virtualHeight;
        float windowRatio = windowWidth / windowHeight;

        float scale = windowRatio > aspectRatio ?
                windowHeight / virtualHeight :
                windowWidth / virtualWidth;

        float correctX = (windowWidth - virtualWidth * scale) / 2;
        float correctY = (windowHeight - virtualHeight * scale) / 2;

        // Coordinate correction
        result.x = (screenX - correctX) / scale;
        result.y = (screenY - correctY) / scale;

        return result;
    }

    // Handle window resize
    public void handleResize(int width, int height) {
        // Reset mouse state on resize
        mousePressed = false;
        mousePrevPressed = false;
    }

    // Helper method to trigger a signal
    public void triggerSignal(Variable variable, String signalName) {
        Signal signal = variable.getSignal(signalName);
        if (signal != null) {
            signal.execute(null);
        }
    }

    public Variable getActiveButton() {
        return activeButton;
    }

    public void setActiveButton(Variable activeButton) {
        this.activeButton = activeButton;
    }

    public void clearActiveButton(Variable button) {
        if (this.activeButton == button) {
            this.activeButton = null;
        }
    }

    public Vector2 getMousePosition() {
        return mousePosition;
    }

    public boolean isMousePressed() {
        return mousePressed;
    }

    @Override
    public void dispose() {
    }
}