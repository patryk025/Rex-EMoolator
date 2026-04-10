package pl.genschu.bloomooemulator.engine.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.viewport.Viewport;
import pl.genschu.bloomooemulator.engine.Game;
import pl.genschu.bloomooemulator.engine.config.EngineConfig;
import pl.genschu.bloomooemulator.engine.context.EngineVariable;
import pl.genschu.bloomooemulator.engine.context.GameContext;
import pl.genschu.bloomooemulator.interpreter.values.StringValue;
import pl.genschu.bloomooemulator.interpreter.variable.*;
import pl.genschu.bloomooemulator.objects.Image;
import pl.genschu.bloomooemulator.geometry.shapes.Box2D;

import java.util.*;

import static pl.genschu.bloomooemulator.utils.CollisionChecker.getImage;
import static pl.genschu.bloomooemulator.utils.CollisionChecker.getRect;

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
    private GameContext lastMouseClickContext = null;
    private boolean mouseVisible = true;
    // LibGDX polling returns (0,0) until a real mouse event arrives. Without a
    // guard, the first tick processes buttons as if the cursor were at (0,0),
    // falsely focusing any button whose rect contains that point.
    private boolean mouseEverObserved = false;

    // Keyboard state
    private final Set<Integer> pressedKeys = new HashSet<>();
    private final Set<Integer> previouslyPressedKeys = new HashSet<>();

    // Active button
    private EngineVariable activeButton = null;

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
        GameContext context = game.getCurrentSceneContext();
        if (context == null) return;

        // Get mouse and keyboard variables from the current context
        EngineVariable mouseEV = context.getMouseVariable();
        EngineVariable keyboardEV = context.getKeyboardVariable();

        MouseVariable mouseVariable = mouseEV instanceof MouseVariable m ? m : null;
        KeyboardVariable keyboardVariable = keyboardEV instanceof KeyboardVariable k ? k : null;

        game.getEmulator().getDebugManager().handleSceneSelectorInput(deltaTime);

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

        boolean isPressed = Gdx.input.isButtonPressed(Input.Buttons.LEFT);

        // Skip processing until we've seen a real mouse event. LibGDX's polling
        // API returns (0,0) before any event arrives, which can falsely trigger
        // button focus on any button whose rect contains the top-left corner.
        if (!mouseEverObserved) {
            if (x == 0 && y == 0 && !isPressed) {
                return;
            }
            mouseEverObserved = true;
        }

        // Mouse coordinates translation
        Vector2 correctedCoords = getCorrectedMouseCoords(x, y);
        int correctedX = (int) correctedCoords.x;
        int correctedY = (int) correctedCoords.y;

        // Update mouse position
        mousePosition.set(correctedX, correctedY);
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
                mouseVariable.emitSignal("ONCLICK", new StringValue("LEFT"));
            } else if (justReleased) {
                mouseVariable.emitSignal("ONRELEASE", new StringValue("LEFT"));
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

        if (Gdx.input.isKeyJustPressed(Input.Keys.F6)) {
            config.toggleMonitorPerformance();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.F7)) {
            @SuppressWarnings("unchecked")
            Collection<Variable> vars = (Collection<Variable>) (Collection<?>) game.getCurrentSceneContext().getGraphicsVariables().values();
            exportGraphicsToFile(new ArrayList<>(vars));
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.F8)) {
            config.toggleDebugWorld();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.F9)) {
            game.getEmulator().getDebugManager().toggleSceneSelector();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.F10)) {
            // WorldVariable F10 debug — deferred until WorldVariable v2
            Gdx.app.log("InputManager", "F10 world debug not available in v2 yet");
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.F11)) {
            config.togglePaused();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.F12)) {
            config.toggleStepFrame();
        }
    }

    private void exportGraphicsToFile(List<Variable> drawList) {
        FileHandle exportDir = Gdx.files.local("exported_graphics");
        if (!exportDir.exists()) exportDir.mkdirs();

        FileHandle metaFile = exportDir.child("meta.json");
        Json json = new Json();
        Array<ObjectMap<String, Object>> metadata = new Array<>();

        for (Variable variable : drawList) {
            Image image = getImage(variable);
            Box2D rect = getRect(variable);
            if (image == null || image.getImageTexture() == null || rect == null) continue;

            boolean isVisible = false;
            if (variable instanceof ImageVariable img) {
                isVisible = img.isVisible();
            } else if (variable instanceof AnimoVariable animo) {
                isVisible = animo.isVisible();
            }

            Texture texture = image.getImageTexture();
            if (!texture.getTextureData().isPrepared()) {
                texture.getTextureData().prepare();
            }
            Pixmap pixmap = texture.getTextureData().consumePixmap();

            String filename = variable.name() + ".png";
            PixmapIO.writePNG(exportDir.child(filename), pixmap);

            ObjectMap<String, Object> entry = new ObjectMap<>();
            entry.put("name", variable.name());
            entry.put("file", filename);
            entry.put("visible", isVisible);
            entry.put("type", variable.type().name());
            entry.put("rect", Map.of(
                    "x", rect.getXLeft(),
                    "y", rect.getYTop(),
                    "w", rect.getWidth(),
                    "h", rect.getHeight()
            ));

            metadata.add(entry);
        }

        metaFile.writeString(json.prettyPrint(metadata), false);
        Gdx.app.log("Export", "Exported graphics to " + exportDir.file().getAbsolutePath());
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

    // Helper method to trigger a signal on a v2 Variable
    public void triggerSignal(Variable variable, String signalName) {
        variable.emitSignal(signalName);
    }

    public EngineVariable getActiveButton() {
        return activeButton;
    }

    public void setActiveButton(EngineVariable activeButton) {
        this.activeButton = activeButton;
    }

    public void clearActiveButton(EngineVariable button) {
        if (this.activeButton == button || button == null) {
            this.activeButton = null;
        }
    }

    public Vector2 getMousePosition() {
        return mousePosition;
    }

    public boolean isMousePressed() {
        return mousePressed;
    }

    public boolean isMouseVisible() {
        return mouseVisible;
    }

    public void setMouseVisible(boolean mouseVisible) {
        this.mouseVisible = mouseVisible;
    }

    @Override
    public void dispose() {
    }
}
