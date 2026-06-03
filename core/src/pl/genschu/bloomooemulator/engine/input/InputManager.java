package pl.genschu.bloomooemulator.engine.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Cursor;
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
import pl.genschu.bloomooemulator.interpreter.context.Context;
import pl.genschu.bloomooemulator.interpreter.values.StringValue;
import pl.genschu.bloomooemulator.interpreter.variable.*;
import pl.genschu.bloomooemulator.objects.Image;
import pl.genschu.bloomooemulator.geometry.shapes.Box2D;

import java.util.*;

import static pl.genschu.bloomooemulator.utils.CollisionChecker.getImage;
import static pl.genschu.bloomooemulator.utils.CollisionChecker.getRect;

public class InputManager implements Disposable {
    public enum MouseCursor {
        ARROW(Cursor.SystemCursor.Arrow),
        WAIT(Cursor.SystemCursor.NotAllowed), // For now, I don't have a wait cursor, so I'm using not allowed as a substitute
        ACTIVE(Cursor.SystemCursor.Hand);

        private final Cursor.SystemCursor systemCursor;

        MouseCursor(Cursor.SystemCursor systemCursor) {
            this.systemCursor = systemCursor;
        }

        public Cursor.SystemCursor systemCursor() {
            return systemCursor;
        }

        public static MouseCursor fromName(String cursorName) {
            if (cursorName == null) return null;

            return switch (cursorName.toUpperCase(Locale.ROOT)) {
                case "ARROW" -> ARROW;
                case "WAIT" -> WAIT;
                case "ACTIVE" -> ACTIVE;
                default -> null;
            };
        }
    }

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
    private MouseCursor mouseCursor = MouseCursor.ARROW;
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

        List<MouseVariable> mouseVariables = getMouseListeners(context);
        List<KeyboardVariable> keyboardVariables = getKeyboardListeners(context);

        game.getEmulator().getDebugManager().handleSceneSelectorInput(deltaTime);

        // Handle mouse input
        processMouseInput(mouseVariables);

        // Handle keyboard input
        processKeyboardInput(keyboardVariables);

        // Handle debugging (F1, F2 keys)
        processDebugInput();
    }

    private List<MouseVariable> getMouseListeners(GameContext context) {
        if (context instanceof Context interpreterContext) {
            return interpreterContext.getMouseVariables();
        }

        EngineVariable mouseEV = context.getMouseVariable();
        return mouseEV instanceof MouseVariable mouse ? List.of(mouse) : List.of();
    }

    private List<KeyboardVariable> getKeyboardListeners(GameContext context) {
        if (context instanceof Context interpreterContext) {
            return interpreterContext.getKeyboardVariables();
        }

        EngineVariable keyboardEV = context.getKeyboardVariable();
        return keyboardEV instanceof KeyboardVariable keyboard ? List.of(keyboard) : List.of();
    }

    private void processMouseInput(List<MouseVariable> mouseVariables) {
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

        // Update mouse listener variables
        for (MouseVariable mouseVariable : mouseVariables) {
            mouseVariable.update(correctedX, correctedY);
        }

        // Process button interactions
        MouseVariable primaryMouse = mouseVariables.isEmpty() ? null : mouseVariables.get(mouseVariables.size() - 1);
        // MOUSE^DISABLE() must also block button handling, not just mouse signals.
        // With no mouse variable in context, default to enabled (legacy behaviour).
        boolean mouseEnabled = primaryMouse == null || primaryMouse.isEnabled();
        buttonHandler.handleMouseInput(correctedX, correctedY, isPressed, justPressed, justReleased, primaryMouse, mouseEnabled);

        // Emit mouse signals
        if (justPressed) {
            emitMouseSignal(mouseVariables, "ONCLICK", new StringValue("LEFT"));
        } else if (justReleased) {
            emitMouseSignal(mouseVariables, "ONRELEASE", new StringValue("LEFT"));
        }

        // Update mouse state
        mousePressed = isPressed;
        mousePrevPressed = isPressed;
    }

    private void emitMouseSignal(List<MouseVariable> mouseVariables, String signalName, StringValue buttonName) {
        for (MouseVariable mouseVariable : mouseVariables) {
            if (mouseVariable.isEnabled() && mouseVariable.isEmitSignals()) {
                mouseVariable.emitSignal(signalName, buttonName);
            }
        }
    }

    private void processKeyboardInput(List<KeyboardVariable> keyboardVariables) {
        if (keyboardVariables.isEmpty()) {
            return;
        }

        // Handle keyboard buttons input
        keyboardHandler.handleKeyboardInput(keyboardVariables, pressedKeys, previouslyPressedKeys);

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
            config.toggleDebugCollisions();
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

    // Helper method to trigger a signal on an interpreter variable.
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
        applyMouseCursor(null);
    }

    public MouseCursor getMouseCursor() {
        return mouseCursor;
    }

    public void setMouseCursor(MouseCursor mouseCursor) {
        if (mouseCursor == null) {
            return;
        }

        this.mouseCursor = mouseCursor;
        applyMouseCursor(null);
    }

    public void applyMouseCursor(Cursor.SystemCursor overrideCursor) {
        Cursor.SystemCursor cursor = !mouseVisible
                ? Cursor.SystemCursor.None
                : overrideCursor != null ? overrideCursor : mouseCursor.systemCursor();
        Gdx.graphics.setSystemCursor(cursor);
    }

    @Override
    public void dispose() {
    }
}
