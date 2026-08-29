package pl.genschu.bloomooemulator.engine.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
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
import pl.genschu.bloomooemulator.engine.context.CanvasBoundsProvider;
import pl.genschu.bloomooemulator.engine.context.CurrentImageProvider;
import pl.genschu.bloomooemulator.engine.context.EngineVariable;
import pl.genschu.bloomooemulator.engine.context.GameContext;
import pl.genschu.bloomooemulator.interpreter.context.Context;
import pl.genschu.bloomooemulator.interpreter.values.StringValue;
import pl.genschu.bloomooemulator.interpreter.variable.*;
import pl.genschu.bloomooemulator.objects.Image;
import pl.genschu.bloomooemulator.geometry.coordinates.CanvasCoordinateSystem;
import pl.genschu.bloomooemulator.geometry.coordinates.CanvasPoint;
import pl.genschu.bloomooemulator.geometry.coordinates.CanvasRect;
import pl.genschu.bloomooemulator.geometry.coordinates.OpenGlPoint;

import java.util.*;

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

    // Viewport maps host-window pixels into the fixed logical OpenGL projection.
    private final Viewport viewport;
    private final Game game;
    private final EngineConfig config;

    // Mouse state
    private CanvasPoint mousePosition = new CanvasPoint(0, 0);
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
    private final DragManager dragManager;
    private final ButtonHandler buttonHandler;
    private final KeyboardHandler keyboardHandler;
    // Captures typed characters for the ONCHAR channel.
    private final KeyboardCharInput keyboardCharInput = new KeyboardCharInput();

    public InputManager(Viewport viewport, Game game, EngineConfig config) {
        this.viewport = viewport;
        this.game = game;
        this.config = config;

        this.dragManager = new DragManager(game);
        this.buttonHandler = new ButtonHandler(game, this);
        this.keyboardHandler = new KeyboardHandler(game);
    }

    public void processInput(float deltaTime) {
        processHostInput(deltaTime);
        processLegacyInput();
    }

    /** Processes emulator/debug controls once per rendered host frame. */
    public void processHostInput(float deltaTime) {
        GameContext context = game.getCurrentSceneContext();
        if (context == null) return;

        var debugManager = game.getEmulator().getDebugManager();
        debugManager.handleSceneSelectorInput(deltaTime);

        // Keep the character-input processor installed during gameplay so keyTyped
        // events feed the ONCHAR channel. The scene selector temporarily swaps in
        // its own processor and never restores it on close, so re-assert ours once
        // the selector is no longer active.
        if (!debugManager.isSceneSelectorActive()
                && Gdx.input.getInputProcessor() != keyboardCharInput) {
            Gdx.input.setInputProcessor(keyboardCharInput);
        }

        // Debug hotkeys are intentionally not gated by the legacy pulse: F11
        // and F12 must still be able to resume/step a paused game.
        processDebugInput();
    }

    /** Processes script-visible input exactly once for an admitted legacy pulse. */
    public void processLegacyInput() {
        GameContext context = game.getCurrentSceneContext();
        if (context == null) return;

        List<MouseVariable> mouseVariables = getMouseListeners(context);
        List<KeyboardVariable> keyboardVariables = getKeyboardListeners(context);

        // Handle mouse input
        processMouseInput(mouseVariables);

        // Handle keyboard input
        processKeyboardInput(keyboardVariables);
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
        Optional<CanvasPoint> correctedCoords = getCorrectedMouseCoords(x, y);
        if (correctedCoords.isEmpty()) {
            // Remember the physical button state even while the pointer is in a
            // FitViewport bar. Otherwise entering the canvas with the button held
            // would look like a fresh click. Dropping an active button also prevents
            // a drag from remaining captured after a release outside the canvas.
            if (!isPressed && mousePrevPressed) {
                setActiveButton(null);
            }
            mousePressed = isPressed;
            mousePrevPressed = isPressed;
            return;
        }

        CanvasPoint canvasPoint = correctedCoords.get();
        int correctedX = (int) Math.floor(canvasPoint.x());
        int correctedY = (int) Math.floor(canvasPoint.y());

        // Update mouse position
        mousePosition = new CanvasPoint(correctedX, correctedY);
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

        // KOLOROWANKA objects register a global mouse listener in the original engine.
        // Skip when a button handler above changed the scene: the press belongs to the
        // previous scene and must not colour a field in the freshly loaded one.
        if (justPressed && mouseEnabled && game.getCurrentSceneContext() == lastMouseClickContext) {
            dispatchKolorowankaClick(correctedX, correctedY);
        }

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

    private void dispatchKolorowankaClick(int x, int y) {
        GameContext context = game.getCurrentSceneContext();
        if (context == null) return;
        for (EngineVariable variable : new ArrayList<>(context.getGraphicsVariables().values())) {
            if (variable instanceof KolorowankaVariable kolorowanka) {
                kolorowanka.handleClick(x, y);
            }
        }
    }

    private void emitMouseSignal(List<MouseVariable> mouseVariables, String signalName, StringValue buttonName) {
        for (MouseVariable mouseVariable : mouseVariables) {
            if (mouseVariable.isEnabled() && mouseVariable.isEmitSignals()) {
                mouseVariable.emitSignal(signalName, buttonName);
            }
        }
    }

    private void processKeyboardInput(List<KeyboardVariable> keyboardVariables) {
        // Always drain so the buffer never grows unbounded, even with no listeners.
        List<Character> typedChars = keyboardCharInput.drain();

        if (keyboardVariables.isEmpty()) {
            return;
        }

        // Handle keyboard buttons input (ONKEYDOWN/ONKEYUP) and typed chars (ONCHAR)
        keyboardHandler.handleKeyboardInput(keyboardVariables, pressedKeys, previouslyPressedKeys, typedChars);

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
            if (!(variable instanceof CurrentImageProvider imageProvider)
                    || !(variable instanceof CanvasBoundsProvider boundsProvider)) {
                continue;
            }
            Image image = imageProvider.getCurrentImage();
            CanvasRect rect = boundsProvider.getCanvasBounds();
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
                    "x", rect.left(),
                    "y", rect.top(),
                    "w", rect.width(),
                    "h", rect.height()
            ));

            metadata.add(entry);
        }

        metaFile.writeString(json.prettyPrint(metadata), false);
        Gdx.app.log("Export", "Exported graphics to " + exportDir.file().getAbsolutePath());
    }

    /**
     * Maps a top-left-origin host-window pixel through the active LibGDX viewport
     * into the script-visible DirectDraw canvas. {@link Viewport#unproject(Vector2)}
     * is authoritative for both FitViewport and StretchViewport; points in FitViewport
     * bars map outside the logical canvas and are rejected.
     */
    public Optional<CanvasPoint> getCorrectedMouseCoords(int screenX, int screenY) {
        Vector2 openGl = viewport.unproject(new Vector2(screenX, screenY));
        CanvasPoint canvas = CanvasCoordinateSystem.fromOpenGl(
                new OpenGlPoint(openGl.x, openGl.y));
        return CanvasCoordinateSystem.BOUNDS.contains(canvas)
                ? Optional.of(canvas)
                : Optional.empty();
    }

    // Handle window resize
    public void handleResize(int width, int height) {
        // Reset mouse state on resize
        mousePressed = false;
        mousePrevPressed = false;
        dragManager.cancel();
        activeButton = null;
    }

    // Helper method to trigger a signal on an interpreter variable.
    public void triggerSignal(Variable variable, String signalName) {
        variable.emitSignal(signalName);
    }

    public EngineVariable getActiveButton() {
        return activeButton;
    }

    public void setActiveButton(EngineVariable activeButton) {
        if (activeButton == null) dragManager.cancel();
        this.activeButton = activeButton;
    }

    public void clearActiveButton(EngineVariable button) {
        if (this.activeButton == button || button == null) {
            dragManager.cancel();
            this.activeButton = null;
        }
    }

    public DragManager getDragManager() {
        return dragManager;
    }

    public CanvasPoint getMousePosition() {
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
        dragManager.cancel();
    }
}
