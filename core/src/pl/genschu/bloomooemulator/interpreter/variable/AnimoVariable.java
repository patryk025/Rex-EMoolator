package pl.genschu.bloomooemulator.interpreter.variable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import pl.genschu.bloomooemulator.annotations.InternalMutable;
import pl.genschu.bloomooemulator.engine.Game;
import pl.genschu.bloomooemulator.engine.decision.events.AnimoEvent;
import pl.genschu.bloomooemulator.engine.decision.events.ButtonEvent;
import pl.genschu.bloomooemulator.engine.decision.states.AnimoState;
import pl.genschu.bloomooemulator.engine.decision.states.ButtonState;
import pl.genschu.bloomooemulator.engine.filters.Filter;
import pl.genschu.bloomooemulator.engine.render.RenderOrder;
import pl.genschu.bloomooemulator.geometry.shapes.Box2D;
import pl.genschu.bloomooemulator.interpreter.context.Context;
import pl.genschu.bloomooemulator.interpreter.helpers.ArgumentHelper;
import pl.genschu.bloomooemulator.interpreter.values.*;
import pl.genschu.bloomooemulator.interpreter.variable.capabilities.Initializable;
import pl.genschu.bloomooemulator.loader.AnimoLoader;
import pl.genschu.bloomooemulator.utils.FileUtils;
import pl.genschu.bloomooemulator.objects.Event;
import pl.genschu.bloomooemulator.objects.FrameData;
import pl.genschu.bloomooemulator.objects.Image;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;

import java.util.*;

/**
 * AnimoVariable represents an animation object in the game.
 * Uses mutable AnimoPlaybackState for animation state to avoid record recreation on every frame.
 */
public record AnimoVariable(
    String name,
    @InternalMutable AnimoPlaybackState state,
    AnimoData data,
    Map<String, SignalHandler> signals
) implements Variable, Initializable {

    // Event flags from animation files
    public static final int FLAG_PLAY_NEXT_EVENT = 0x800000;
    public static final int FLAG_WAIT_FOR_SFX = 0x100000;
    public static final int FLAG_PING_PONG = 0x20000;

    /**
     * Mutable playback state for animation.
     */
    public static final class AnimoPlaybackState {
        // Animation state
        public AnimoState animationState = AnimoState.INIT;
        public ButtonState buttonState = ButtonState.DISABLED;

        // Current playback
        public Event currentEvent;
        public int currentFrameNumber = 0;
        public int currentImageNumber = 0;
        public Image currentImage;
        public float elapsedTime = 0f;
        public int direction = 1;

        // Geometry
        public int posX = 0;
        public int posY = 0;
        public int anchorX = 0;
        public int anchorY = 0;
        public Box2D rect = new Box2D(0, 0, 0, 0);

        // Rendering
        public boolean visible = true;
        public boolean toCanvas = true;
        public int opacity = 255;
        public int priority = 0;
        public long renderOrder = RenderOrder.next();
        public List<Filter> filters = new ArrayList<>();

        // Button
        public boolean changeCursor = false;

        // Collision
        public boolean monitorCollision = false;
        public boolean monitorCollisionAlpha = false;
        public Music currentSfx;

        // Playback observers
        public List<PlaybackObserver> observers = new ArrayList<>();

        // File attributes
        public String filename = "";
        public int fps = 15;
        public float frameDuration = 1f / 15f;

        // Discard the first updateAnimation() after PLAY — the first libGDX deltaTime
        // includes time from *before* PLAY was called (render-loop smear), which would
        // shorten the first displayed frame by up to one 60fps tick (~16 ms).
        public boolean skipNextAccumulation = false;

        public AnimoPlaybackState() {}

        public AnimoPlaybackState copy() {
            AnimoPlaybackState copy = new AnimoPlaybackState();
            copy.animationState = this.animationState;
            copy.buttonState = this.buttonState;
            copy.currentEvent = this.currentEvent;
            copy.currentFrameNumber = this.currentFrameNumber;
            copy.currentImageNumber = this.currentImageNumber;
            copy.currentImage = this.currentImage;
            copy.elapsedTime = this.elapsedTime;
            copy.direction = this.direction;
            copy.posX = this.posX;
            copy.posY = this.posY;
            copy.anchorX = this.anchorX;
            copy.anchorY = this.anchorY;
            copy.rect = new Box2D(this.rect.getXLeft(), this.rect.getYBottom(), this.rect.getXRight(), this.rect.getYTop());
            copy.visible = this.visible;
            copy.toCanvas = this.toCanvas;
            copy.opacity = this.opacity;
            copy.priority = this.priority;
            copy.renderOrder = this.renderOrder;
            copy.filters = new ArrayList<>(this.filters);
            copy.changeCursor = this.changeCursor;
            copy.monitorCollision = this.monitorCollision;
            copy.monitorCollisionAlpha = this.monitorCollisionAlpha;
            copy.currentSfx = this.currentSfx;
            copy.filename = this.filename;
            copy.fps = this.fps;
            copy.frameDuration = this.frameDuration;
            return copy;
        }
    }

    /**
     * Immutable animation data loaded from file.
     */
    public record AnimoData(
        List<Event> events,
        List<Image> images,
        int imagesCount,
        int eventsCount,
        int colorDepth,
        int fps,
        int opacity,
        int maxWidth,
        int maxHeight,
        String description,
        String signature
    ) {
        public static final AnimoData EMPTY = new AnimoData(
            List.of(), List.of(), 0, 0, 16, 15, 255, 0, 0, "", ""
        );

        public AnimoData {
            if (events == null) events = List.of();
            if (images == null) images = List.of();
            if (description == null) description = "";
            if (signature == null) signature = "";
        }
    }

    /**
     * Result of animation update.
     */
    public record AnimationUpdateResult(
        boolean frameChanged,
        boolean animationEnded,
        String signalToEmit,
        String signalArgument
    ) {}

    // Compact constructor
    public AnimoVariable {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Variable name cannot be null or empty");
        }
        if (state == null) {
            state = new AnimoPlaybackState();
        }
        if (data == null) {
            data = AnimoData.EMPTY;
        }
        if (signals == null) {
            signals = Map.of();
        } else {
            signals = Map.copyOf(signals);
        }
    }

    public AnimoVariable(String name) {
        this(name, new AnimoPlaybackState(), AnimoData.EMPTY, Map.of());
    }

    public AnimoVariable(String name, String filename) {
        this(name, new AnimoPlaybackState(), AnimoData.EMPTY, Map.of());
        this.state.filename = filename;
    }

    // ========================================
    // INTERFACE IMPLEMENTATION
    // ========================================

    @Override
    public Value value() {
        return new StringValue(name);
    }

    @Override
    public VariableType type() {
        return VariableType.ANIMO;
    }

    @Override
    public Variable withValue(Value newValue) {
        return this;
    }

    @Override
    public Map<String, MethodSpec> methods() {
        return METHODS;
    }

    @Override
    public Variable withSignal(String signalName, SignalHandler handler) {
        Map<String, SignalHandler> newSignals = new HashMap<>(signals);
        if (handler == null) {
            newSignals.remove(signalName);
        } else {
            newSignals.put(signalName, handler);
        }
        return new AnimoVariable(name, state, data, newSignals);
    }

    @Override
    public Variable copyAs(String newName) {
        return copyFromTemplate(this, newName, new HashMap<>(signals));
    }

    @Override
    public void init(Context context) {
        // Read attributes from context (like v1's initAttributes)
        initAttributesFromContext(context);

        String filename = normalizeFilename(state.filename);
        state.filename = filename;
        if (filename == null || filename.isEmpty()) {
            Gdx.app.debug("AnimoVariable", name + ": No FILENAME, skipping load");
            return;
        }

        Game game = context.getGame();
        String vfsPath = game != null ? FileUtils.resolveVfsPath(game, filename) : filename;

        AnimoVariable aliased = aliasFromPreviouslyLoadedVariable(context, vfsPath);
        if (aliased != null) {
            context.setVariable(name, aliased);
            Gdx.app.log("AnimoVariable", name + ": Reused ANIMO from " + vfsPath + " via alias copy");
            return;
        }

        try (java.io.InputStream is = game.getVfs().openRead(vfsPath)) {
            AnimoData loadedData = AnimoLoader.load(is);
            AnimoVariable updated = this.withData(loadedData);
            // FPS: attribute override > file value > default
            if (state.fps != 15) {
                // Attribute already set a custom FPS, keep it
                updated.state.fps = state.fps;
                updated.state.frameDuration = 1f / state.fps;
            } else if (loadedData.fps() > 0) {
                updated.state.fps = loadedData.fps();
                updated.state.frameDuration = 1f / loadedData.fps();
            }
            updated.state.opacity = loadedData.opacity();
            // Carry over attribute-initialized state
            updated.state.visible = state.visible;
            updated.state.toCanvas = state.toCanvas;
            updated.state.priority = state.priority;
            updated.state.monitorCollision = state.monitorCollision;
            updated.state.buttonState = state.buttonState;
            // Load SFX audio files from frame descriptions
            loadSfxAudio(loadedData, game);
            context.setVariable(name, updated);
            Gdx.app.log("AnimoVariable", name + ": Loaded ANIMO from " + vfsPath);
        } catch (Exception e) {
            Gdx.app.error("AnimoVariable", name + ": Failed to load ANIMO: " + e.getMessage(), e);
        }
    }

    private AnimoVariable aliasFromPreviouslyLoadedVariable(Context context, String vfsPath) {
        Game game = context.getGame();
        for (Variable variable : context.getVariables(false).values()) {
            if (!(variable instanceof AnimoVariable source) || source.name.equals(name) || source.data == AnimoData.EMPTY) {
                continue;
            }

            String sourceFilename = normalizeFilename(source.state.filename);
            if (sourceFilename == null || sourceFilename.isEmpty()) {
                continue;
            }

            String sourceVfsPath = game != null ? FileUtils.resolveVfsPath(game, sourceFilename) : sourceFilename;
            if (sourceVfsPath != null && sourceVfsPath.equalsIgnoreCase(vfsPath)) {
                return copyFromTemplate(source, name, signals);
            }
        }
        return null;
    }

    private static String normalizeFilename(String filename) {
        if (filename == null || filename.isEmpty()) {
            return filename;
        }
        return filename.toUpperCase().endsWith(".ANN") ? filename : filename + ".ANN";
    }

    private static AnimoVariable copyFromTemplate(AnimoVariable template, String newName, Map<String, SignalHandler> newSignals) {
        AnimoPlaybackState copiedState = template.state.copy();
        AnimoData copiedData = copyData(template.data);
        rebindStateToCopiedData(template, copiedState, copiedData);
        return new AnimoVariable(newName, copiedState, copiedData, newSignals);
    }

    private static AnimoData copyData(AnimoData source) {
        if (source == null || source == AnimoData.EMPTY) {
            return AnimoData.EMPTY;
        }

        List<Image> images = new ArrayList<>(source.images());
        List<Event> events = new ArrayList<>(source.events().size());

        for (Event sourceEvent : source.events()) {
            Event copiedEvent = new Event();
            copiedEvent.setName(sourceEvent.getName());
            copiedEvent.setFramesCount(sourceEvent.getFramesCount());
            copiedEvent.setLoopStart(sourceEvent.getLoopStart());
            copiedEvent.setLoopEnd(sourceEvent.getLoopEnd());
            copiedEvent.setRepeatCount(sourceEvent.getRepeatCount());
            copiedEvent.setRepeatCounter(sourceEvent.getRepeatCounter());
            copiedEvent.setOpacity(sourceEvent.getOpacity());
            copiedEvent.setFlags(sourceEvent.getFlags());
            copiedEvent.setFramesNumbers(new ArrayList<>(sourceEvent.getFramesNumbers()));

            List<FrameData> copiedFrameData = new ArrayList<>();
            if (sourceEvent.getFrameData() != null) {
                for (FrameData sourceFrame : sourceEvent.getFrameData()) {
                    FrameData copiedFrame = new FrameData();
                    copiedFrame.setStartingBytes(sourceFrame.getStartingBytes() != null ? sourceFrame.getStartingBytes().clone() : null);
                    copiedFrame.setOffsetX(sourceFrame.getOffsetX());
                    copiedFrame.setOffsetY(sourceFrame.getOffsetY());
                    copiedFrame.setSfxRandomSeed(sourceFrame.getSfxRandomSeed());
                    copiedFrame.setOpacity(sourceFrame.getOpacity());
                    copiedFrame.setName(sourceFrame.getName());
                    copiedFrame.setSfxDescription(sourceFrame.getSfxDescription());
                    copiedFrame.setSfxAudio(sourceFrame.getSfxAudio() != null ? new ArrayList<>(sourceFrame.getSfxAudio()) : new ArrayList<>());
                    copiedFrameData.add(copiedFrame);
                }
            }
            copiedEvent.setFrameData(copiedFrameData);

            List<Image> copiedFrames = new ArrayList<>();
            if (sourceEvent.getFramesNumbers() != null) {
                for (Integer frameNumber : sourceEvent.getFramesNumbers()) {
                    copiedFrames.add(images.get(frameNumber));
                }
            }
            copiedEvent.setFrames(copiedFrames);
            events.add(copiedEvent);
        }

        return new AnimoData(
            events,
            images,
            source.imagesCount(),
            source.eventsCount(),
            source.colorDepth(),
            source.fps(),
            source.opacity(),
            source.maxWidth(),
            source.maxHeight(),
            source.description(),
            source.signature()
        );
    }

    private static void rebindStateToCopiedData(AnimoVariable template, AnimoPlaybackState copiedState, AnimoData copiedData) {
        copiedState.currentEvent = null;
        if (template.state.currentEvent != null) {
            int eventIndex = template.data.events().indexOf(template.state.currentEvent);
            if (eventIndex >= 0 && eventIndex < copiedData.events().size()) {
                copiedState.currentEvent = copiedData.events().get(eventIndex);
            }
        }

        copiedState.currentImage = null;
        if (copiedState.currentImageNumber >= 0 && copiedState.currentImageNumber < copiedData.images().size()) {
            copiedState.currentImage = copiedData.images().get(copiedState.currentImageNumber);
        }
    }

    /**
     * Loads SFX audio files from frame data descriptions.
     */
    private void loadSfxAudio(AnimoData loadedData, Game game) {
        if (loadedData.events() == null) return;
        for (Event event : loadedData.events()) {
            if (event.getFrameData() == null) continue;
            for (FrameData frame : event.getFrameData()) {
                String desc = frame.getSfxDescription();
                if (desc == null || desc.isEmpty()) continue;
                String[] sfxFiles = desc.split(";");
                List<Music> sfxAudioList = new ArrayList<>();
                for (String sfxFile : sfxFiles) {
                    if (sfxFile.isEmpty()) continue;
                    if (!sfxFile.toLowerCase().startsWith("sfx\\")) {
                        sfxFile = "sfx\\" + sfxFile;
                    }
                    if (!sfxFile.startsWith("$")) {
                        sfxFile = "$WAVS\\" + sfxFile;
                    }
                    try {
                        String vfsPath = FileUtils.resolveVfsPath(game, sfxFile);
                        sfxAudioList.add(Gdx.audio.newMusic(game.getAudioFileHandle(vfsPath)));
                    } catch (Exception e) {
                        Gdx.app.error("AnimoVariable", "Error loading SFX: " + e.getMessage());
                    }
                }
                frame.setSfxAudio(sfxAudioList);
            }
        }
    }

    /**
     * Reads attributes from context and applies them to state.
     * Mirrors v1's initMissingAttributes() + initAttributes().
     */
    private void initAttributesFromContext(Context context) {
        // FILENAME
        if (state.filename == null || state.filename.isEmpty()) {
            String attr = context.getAttribute(name, "FILENAME");
            if (attr != null && !attr.isEmpty()) {
                state.filename = attr;
            }
        }

        // VISIBLE (default: true)
        String visibleAttr = context.getAttribute(name, "VISIBLE");
        if (visibleAttr != null) {
            state.visible = visibleAttr.equalsIgnoreCase("TRUE");
        }

        // TOCANVAS (default: true)
        String toCanvasAttr = context.getAttribute(name, "TOCANVAS");
        if (toCanvasAttr != null) {
            state.toCanvas = toCanvasAttr.equalsIgnoreCase("TRUE");
        }

        // PRIORITY (default: 0)
        String priorityAttr = context.getAttribute(name, "PRIORITY");
        if (priorityAttr != null) {
            try { state.priority = Integer.parseInt(priorityAttr); }
            catch (NumberFormatException ignored) {}
        }

        // FPS (default: 15)
        String fpsAttr = context.getAttribute(name, "FPS");
        if (fpsAttr != null) {
            try {
                int fps = Integer.parseInt(fpsAttr);
                if (fps > 0) {
                    state.fps = fps;
                    state.frameDuration = 1f / fps;
                }
            } catch (NumberFormatException ignored) {}
        }

        // ASBUTTON
        String asButtonAttr = context.getAttribute(name, "ASBUTTON");
        if (asButtonAttr != null && asButtonAttr.equalsIgnoreCase("TRUE")) {
            changeButtonState(ButtonEvent.ENABLE);
        }

        // MONITORCOLLISION
        String monitorAttr = context.getAttribute(name, "MONITORCOLLISION");
        if (monitorAttr != null && monitorAttr.equalsIgnoreCase("TRUE")) {
            state.monitorCollision = true;
        }

        // MONITORCOLLISIONALPHA
        String monitorAlphaAttr = context.getAttribute(name, "MONITORCOLLISIONALPHA");
        if (monitorAlphaAttr != null && monitorAlphaAttr.equalsIgnoreCase("TRUE")) {
            state.monitorCollisionAlpha = true;
        }
    }

    // ========================================
    // ANIMATION DATA MANAGEMENT
    // ========================================

    /**
     * Returns a new AnimoVariable with loaded animation data.
     */
    public AnimoVariable withData(AnimoData newData) {
        AnimoVariable newVar = new AnimoVariable(name, state, newData, signals);
        // Initialize current event to first event with frames
        if (newData.events() != null && !newData.events().isEmpty()) {
            for (Event event : newData.events()) {
                if (!event.getFrames().isEmpty()) {
                    newVar.state.currentEvent = event;
                    break;
                }
            }
            if (newVar.state.currentEvent != null && !newVar.state.currentEvent.getFramesNumbers().isEmpty()) {
                newVar.state.currentFrameNumber = 0;
                newVar.state.currentImageNumber = newVar.state.currentEvent.getFramesNumbers().get(0);
                if (newData.images() != null && newVar.state.currentImageNumber < newData.images().size()) {
                    newVar.state.currentImage = newData.images().get(newVar.state.currentImageNumber);
                }
                newVar.updateRect();
            }
        }
        return newVar;
    }

    // ========================================
    // ACCESSORS
    // ========================================

    public int getPosX() { return state.posX; }
    public int getPosY() { return state.posY; }
    public int getAnchorX() { return state.anchorX; }
    public int getAnchorY() { return state.anchorY; }
    public Box2D getRect() { return state.rect; }
    public boolean isVisible() { return state.visible; }
    public int getOpacity() { return state.opacity; }
    public int getPriority() { return state.priority; }
    public long getRenderOrder() { return state.renderOrder; }
    public int getFps() { return state.fps; }
    public float getFrameDuration() { return state.frameDuration; }
    public int getDirection() { return state.direction; }
    public Event getCurrentEvent() { return state.currentEvent; }
    public int getCurrentFrameNumber() { return state.currentFrameNumber; }
    public int getCurrentImageNumber() { return state.currentImageNumber; }
    public Image getCurrentImage() { return state.currentImage; }
    public AnimoState getAnimationState() { return state.animationState; }
    public ButtonState getButtonState() { return state.buttonState; }
    public String getFilename() { return state.filename; }
    public boolean isMonitorCollision() { return state.monitorCollision; }
    public boolean isMonitorCollisionAlpha() { return state.monitorCollisionAlpha; }
    public boolean isChangeCursor() { return state.changeCursor; }
    public List<Filter> getFilters() { return state.filters; }
    public boolean hasFilters() { return !state.filters.isEmpty(); }

    public List<Event> getEvents() { return data.events(); }
    public List<Image> getImages() { return data.images(); }
    public int getImagesCount() { return data.imagesCount(); }
    public int getEventsCount() { return data.eventsCount(); }
    public int getMaxWidth() { return data.maxWidth(); }
    public int getMaxHeight() { return data.maxHeight(); }

    public boolean isPlaying() {
        return state.animationState == AnimoState.PLAYING;
    }

    public boolean isAsButton() {
        return state.buttonState != ButtonState.DISABLED;
    }

    public int getAlpha(int x, int y) {
        if (state.currentImage == null || state.currentImage.getImageTexture() == null) return 0;
        return getAlpha(state.currentImage, x, y);
    }

    public int getAlpha(Image image, int x, int y) {
        if (image == null || image.getImageTexture() == null) return 0;
        Pixmap pixmap = image.getImageTexture().getTextureData().consumePixmap();
        Color color = new Color(pixmap.getPixel(x, y));
        return (int) (color.a * 255f);
    }

    public boolean isRenderedOnCanvas() {
        return state.toCanvas;
    }

    // ========================================
    // STATE MODIFICATION
    // ========================================

    public void setFps(int fps) {
        state.fps = fps;
        state.frameDuration = 1f / fps;
    }

    public void setVisible(boolean visible) {
        state.visible = visible;
    }

    public void setOpacity(int opacity) {
        state.opacity = opacity;
    }

    public void setPriority(int priority) {
        state.priority = priority;
        state.renderOrder = RenderOrder.next();
    }

    public void setDirection(int direction) {
        state.direction = direction;
    }

    public void setChangeCursor(boolean changeCursor) {
        state.changeCursor = changeCursor;
    }

    public void setFilename(String filename) {
        state.filename = filename;
    }

    public void setPosX(int posX) {
        state.posX = posX;
    }

    public void setPosY(int posY) {
        state.posY = posY;
    }

    // ========================================
    // PLAYBACK OBSERVERS
    // ========================================

    public void addObserver(PlaybackObserver observer) {
        if (!state.observers.contains(observer)) {
            state.observers.add(observer);
        }
    }

    public void removeObserver(PlaybackObserver observer) {
        state.observers.remove(observer);
    }

    private void notifyObserversFinished(String eventName) {
        for (int i = state.observers.size() - 1; i >= 0; i--) {
            state.observers.get(i).onPlaybackFinished(this, eventName);
        }
    }

    private void notifyObserversStarted(String eventName) {
        for (int i = state.observers.size() - 1; i >= 0; i--) {
            state.observers.get(i).onPlaybackStarted(this, eventName);
        }
    }

    // ========================================
    // ANIMATION STATE MACHINE
    // ========================================

    public void changeAnimoState(AnimoEvent event) {
        changeAnimoState(event, true);
    }

    public void changeAnimoState(AnimoEvent event, boolean emitSignal) {
        AnimoState oldState = state.animationState;
        state.animationState = evaluateAnimoState(oldState, event);

        if (state.animationState != oldState) {
            switch (state.animationState) {
                case STOPPED:
                    if (emitSignal && oldState == AnimoState.PLAYING && state.currentEvent != null) {
                        emitSignal("ONFINISHED", new StringValue(state.currentEvent.getName()));
                        notifyObserversFinished(state.currentEvent.getName());
                    }
                    break;
                case IDLE:
                    if (oldState == AnimoState.PLAYING && state.currentEvent != null) {
                        emitSignal("ONFINISHED", new StringValue(state.currentEvent.getName()));
                        notifyObserversFinished(state.currentEvent.getName());
                    }
                    break;
            }
            Gdx.app.debug("AnimoVariable", name + " state: " + oldState + " -> " + state.animationState + " (event: " + event + ")");
        }
    }

    /**
     * Evaluate next animation state based on current state and event.
     */
    private AnimoState evaluateAnimoState(AnimoState currentState, AnimoEvent event) {
        return switch (event) {
            case PLAY -> AnimoState.PLAYING;
            case PAUSE -> currentState == AnimoState.PLAYING ? AnimoState.PAUSED : currentState;
            case STOP -> (currentState == AnimoState.PLAYING || currentState == AnimoState.PAUSED)
                ? AnimoState.STOPPED : currentState;
            case END -> currentState == AnimoState.PLAYING ? AnimoState.IDLE : currentState;
            case PREV_FRAME, NEXT_FRAME, SET_FRAME -> currentState;
        };
    }

    public void changeButtonState(ButtonEvent event) {
        changeButtonState(event, null);
    }

    public void changeButtonState(ButtonEvent event, Context context) {
        ButtonState oldState = state.buttonState;
        state.buttonState = evaluateButtonState(oldState, event);

        if (state.buttonState != oldState) {
            switch (state.buttonState) {
                case STANDARD -> {
                    if (oldState == ButtonState.HOVERED) {
                        callMethod("PLAY", new StringValue("ONFOCUSOFF"));
                        emitSignal("ONFOCUSOFF");
                    }
                    if (oldState == ButtonState.DISABLED) {
                        state.toCanvas = true;
                        callMethod("PLAY", new StringValue("ONNOEVENT"));
                        if (context != null) {
                            context.addButtonVariable(this);
                        }
                    }
                }
                case HOVERED -> {
                    callMethod("PLAY", new StringValue("ONFOCUSON"));
                    if (oldState == ButtonState.STANDARD) {
                        emitSignal("ONFOCUSON");
                    }
                    if (oldState == ButtonState.PRESSED) {
                        emitSignal("ONRELEASE");
                    }
                }
                case PRESSED -> {
                    callMethod("PLAY", new StringValue("ONCLICK"));
                    emitSignal("ONCLICK");
                }
                case DISABLED -> {
                    if (context != null) {
                        context.removeButtonVariable(this);
                    }
                    if (oldState == ButtonState.PRESSED) {
                        Game game = context != null ? context.getGame() : null;
                        if (game != null && game.getInputManager() != null
                                && game.getInputManager().getActiveButton() == this) {
                            game.getInputManager().clearActiveButton(this);
                        }
                    }
                }
                default -> {}
            }
            Gdx.app.debug("AnimoVariable", name + " button state: " + oldState + " -> " + state.buttonState);
        }
    }

    /**
     * Evaluate next button state based on current state and event.
     */
    private ButtonState evaluateButtonState(ButtonState currentState, ButtonEvent event) {
        return switch (event) {
            case ENABLE -> ButtonState.STANDARD;
            case DISABLE -> ButtonState.DISABLED;
            case DISABLE_BUT_VISIBLE -> ButtonState.DISABLED_BUT_VISIBLE;
            case PRESSED -> currentState == ButtonState.HOVERED ? ButtonState.PRESSED : currentState;
            case RELEASED -> currentState == ButtonState.PRESSED ? ButtonState.HOVERED : currentState;
            case FOCUS_ON -> currentState == ButtonState.STANDARD ? ButtonState.HOVERED : currentState;
            case FOCUS_OFF -> (currentState == ButtonState.HOVERED || currentState == ButtonState.PRESSED)
                ? ButtonState.STANDARD : currentState;
        };
    }

    // ========================================
    // FRAME MANAGEMENT
    // ========================================

    public void setCurrentFrameNumber(int frameNumber, boolean emitEvent) {
        if (state.currentEvent == null || state.currentEvent.getFramesNumbers() == null) return;
        if (frameNumber < 0 || frameNumber >= state.currentEvent.getFramesNumbers().size()) {
            Gdx.app.error("AnimoVariable", "Trying to set frame " + frameNumber + " out of bounds");
            return;
        }
        state.currentFrameNumber = frameNumber;
        int imageNumber = state.currentEvent.getFramesNumbers().get(frameNumber);
        setCurrentImageNumber(imageNumber, emitEvent);
        playSfx();
    }

    public void setCurrentImageNumber(int imageNumber, boolean emitFrameChanged) {
        state.currentImageNumber = imageNumber;
        if (data.images() != null && imageNumber < data.images().size()) {
            state.currentImage = data.images().get(imageNumber);
        }
        updateRect();
        if (emitFrameChanged && state.currentEvent != null) {
            emitSignal("ONFRAMECHANGED", new StringValue(state.currentEvent.getName()));
        }
    }

    private void playSfx() {
        if (state.currentEvent == null) return;
        List<FrameData> frameData = state.currentEvent.getFrameData();
        if (frameData == null || frameData.isEmpty() || state.currentFrameNumber >= frameData.size()) return;

        FrameData fd = frameData.get(state.currentFrameNumber);
        if (fd.getSfxAudio() != null && !fd.getSfxAudio().isEmpty()) {
            Gdx.app.log("AnimoVariable", "Playing sfx in animo " + state.currentEvent.getName());
            int randomIndex = new Random().nextInt(fd.getSfxAudio().size());
            Music music = fd.getSfxAudio().get(randomIndex);
            if (state.currentSfx != null) state.currentSfx.stop();
            state.currentSfx = music;
            state.currentSfx.play();
        }
    }

    // ========================================
    // RECT MANAGEMENT
    // ========================================

    public void updateRect() {
        if (state.currentImage == null) {
            state.rect.setXLeft(state.posX);
            state.rect.setYTop(state.posY);
            state.rect.setXRight(state.posX + 1);
            state.rect.setYBottom(state.posY - 1);
        } else {
            try {
                FrameData frameData = (state.currentEvent != null && state.currentEvent.getFrameData() != null && !state.currentEvent.getFrameData().isEmpty())
                    ? state.currentEvent.getFrameData().get(state.currentFrameNumber)
                    : null;

                int frameOffsetX = frameData != null ? frameData.getOffsetX() : 0;
                int frameOffsetY = frameData != null ? frameData.getOffsetY() : 0;

                state.rect.setXLeft(state.posX + frameOffsetX + state.currentImage.offsetX);
                state.rect.setYTop(state.posY + frameOffsetY + state.currentImage.offsetY);
                state.rect.setXRight(state.rect.getXLeft() + state.currentImage.width);
                state.rect.setYBottom(state.rect.getYTop() - state.currentImage.height);
            } catch (Exception e) {
                Gdx.app.error("AnimoVariable", "Error updating rect: " + e.getMessage());
                state.rect.setXLeft(state.posX);
                state.rect.setYTop(state.posY);
                state.rect.setXRight(state.posX + 1);
                state.rect.setYBottom(state.posY - 1);
            }
        }
    }

    // ========================================
    // ANIMATION UPDATE
    // ========================================

    public void updateAnimation(float deltaTime) {
        if (state.skipNextAccumulation) {
            state.skipNextAccumulation = false;
            return;
        }
        state.elapsedTime += deltaTime;
        if (state.elapsedTime <= state.frameDuration) return;

        state.elapsedTime -= state.frameDuration;
        if (state.elapsedTime > state.frameDuration) {
            state.elapsedTime = 0; // Prevent fast-forward on lag
        }

        if (state.currentEvent == null || !isPlaying()) return;

        if (state.currentEvent.getFramesCount() == 0) {
            changeAnimoState(AnimoEvent.STOP);
            return;
        }

        if (!isPlaying()) return; // Re-check in case ONFRAMECHANGED stopped it

        int nextFrameNumber = state.currentFrameNumber + state.direction;

        // Handle loop end
        if (state.currentEvent.getLoopEnd() != 0 && nextFrameNumber >= state.currentEvent.getLoopEnd()) {
            setCurrentFrameNumber(state.currentEvent.getLoopStart(), true);
            state.currentEvent.setRepeatCounter(state.currentEvent.getRepeatCounter() + 1);
            if (state.currentEvent.getRepeatCounter() >= state.currentEvent.getRepeatCount()) {
                changeAnimoState(AnimoEvent.END);
            }
        }
        // Handle overflow/underflow
        else if (nextFrameNumber >= state.currentEvent.getFrames().size() || nextFrameNumber < 0) {
            if (nextFrameNumber < 0) {
                // Backward past first frame
                if ((state.currentEvent.getFlags() & FLAG_PING_PONG) != 0) {
                    state.direction *= -1;
                } else {
                    changeAnimoState(AnimoEvent.END);
                }
            } else {
                // Forward past last frame
                if ((state.currentEvent.getFlags() & FLAG_PING_PONG) != 0) {
                    state.direction *= -1;
                } else if ((state.currentEvent.getFlags() & FLAG_PLAY_NEXT_EVENT) != 0) {
                    int currentIdx = data.events().indexOf(state.currentEvent);
                    if (currentIdx < data.events().size() - 1) {
                        state.currentEvent = data.events().get(currentIdx + 1);
                        setCurrentFrameNumber(0, true);
                        state.currentEvent.setRepeatCounter(0);
                    } else {
                        changeAnimoState(AnimoEvent.END);
                    }
                } else {
                    changeAnimoState(AnimoEvent.END);
                }
            }
        } else {
            setCurrentFrameNumber(nextFrameNumber, true);
        }
    }

    // ========================================
    // HELPER METHODS
    // ========================================

    public boolean hasEvent(String eventName) {
        if (data.events() == null) return false;
        for (Event event : data.events()) {
            if (event.getName().equalsIgnoreCase(eventName)) {
                return true;
            }
        }
        return false;
    }

    public Event getEvent(String eventName) {
        if (data.events() == null) return null;
        for (Event event : data.events()) {
            if (event.getName().equalsIgnoreCase(eventName)) {
                return event;
            }
        }
        return null;
    }

    public List<Event> getEventsWithPrefix(String prefix) {
        List<Event> result = new ArrayList<>();
        if (data.events() == null) return result;
        for (Event event : data.events()) {
            if (event.getName().startsWith(prefix + "_")) {
                result.add(event);
            }
        }
        return result;
    }

    public boolean isAt(int x, int y) {
        Box2D r = state.rect;
        return x >= r.getXLeft() && x <= r.getXRight() && y >= r.getYTop() && y <= r.getYBottom();
    }

    public boolean isNear(Box2D rect1, Box2D rect2, int iouThreshold) {
        int intersectionX = Math.max(rect1.getXLeft(), rect2.getXLeft());
        int intersectionY = Math.max(rect1.getYBottom(), rect2.getYBottom());
        int intersectionWidth = Math.min(rect1.getXRight(), rect2.getXRight()) - intersectionX;
        int intersectionHeight = Math.min(rect1.getYTop(), rect2.getYTop()) - intersectionY;

        if (intersectionWidth <= 0 || intersectionHeight <= 0) {
            return false;
        }

        int intersectionArea = intersectionWidth * intersectionHeight;
        int unionArea = rect1.area() + rect2.area() - intersectionArea;
        double iou = (double) intersectionArea / unionArea * 100;

        return iou >= iouThreshold;
    }

    public void addFilter(Filter filter) {
        state.filters.add(filter);
    }

    public void removeFilter(Filter filter) {
        state.filters.remove(filter);
    }

    /**
     * Calculate opacity considering event and frame opacity.
     */
    public float getCalculatedOpacity() {
        int opacity = state.opacity;
        if (state.currentEvent != null) {
            opacity = opacity * state.currentEvent.getOpacity() / 255;
            if (state.currentImage != null && state.currentEvent.getFrameData() != null && !state.currentEvent.getFrameData().isEmpty()) {
                opacity = opacity * state.currentEvent.getFrameData().get(state.currentFrameNumber).getOpacity() / 255;
            }
        }
        return opacity / 255f;
    }

    // ========================================
    // METHODS DEFINITION
    // ========================================

    private static final Map<String, MethodSpec> METHODS = Map.ofEntries(
        // PLAYBACK METHODS
        Map.entry("PLAY", MethodSpec.of((self, args, ctx) -> {
            AnimoVariable thisVar = (AnimoVariable) self;
            if (args.isEmpty()) {
                // PLAY() - play current event
                Event currentEvent = thisVar.state.currentEvent;
                if (currentEvent != null && currentEvent.getFramesCount() == 0) {
                    thisVar.changeAnimoState(AnimoEvent.END);
                    return MethodResult.noReturn();
                }
                thisVar.state.elapsedTime = 0f;
                thisVar.state.skipNextAccumulation = true;
                thisVar.setVisible(true);
                thisVar.changeAnimoState(AnimoEvent.PLAY);
                thisVar.setCurrentFrameNumber(0, true);
                if (currentEvent != null) {
                    currentEvent.setRepeatCounter(0);
                    thisVar.emitSignal("ONSTARTED", new StringValue(currentEvent.getName()));
                    thisVar.notifyObserversStarted(currentEvent.getName());
                }
            } else {
                // PLAY(eventName) - play specific event
                String eventName = ArgumentHelper.getString(args.get(0));
                Event event = thisVar.getEvent(eventName);
                if (event != null) {
                    thisVar.state.currentEvent = event;
                    if (event.getFramesCount() == 0) {
                        thisVar.changeAnimoState(AnimoEvent.END);
                        return MethodResult.noReturn();
                    }
                    thisVar.state.elapsedTime = 0f;
                    thisVar.state.skipNextAccumulation = true;
                    thisVar.setVisible(true);
                    thisVar.changeAnimoState(AnimoEvent.PLAY);
                    thisVar.setCurrentFrameNumber(0, true);
                    event.setRepeatCounter(0);
                    thisVar.emitSignal("ONSTARTED", new StringValue(event.getName()));
                    thisVar.notifyObserversStarted(event.getName());
                }
            }
            return MethodResult.noReturn();
        })),

        Map.entry("NPLAY", MethodSpec.of((self, args, ctx) -> {
            AnimoVariable thisVar = (AnimoVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("NPLAY requires 1 argument");
            }
            int eventId = ArgumentHelper.getInt(args.get(0));
            List<Event> events = thisVar.data.events();
            if (eventId >= 0 && eventId < events.size()) {
                Event event = events.get(eventId);
                thisVar.state.currentEvent = event;
                if (event.getFramesCount() == 0) {
                    thisVar.changeAnimoState(AnimoEvent.END);
                    return MethodResult.noReturn();
                }
                thisVar.state.elapsedTime = 0f;
                thisVar.state.skipNextAccumulation = true;
                thisVar.setVisible(true);
                thisVar.changeAnimoState(AnimoEvent.PLAY);
                thisVar.setCurrentFrameNumber(0, true);
                event.setRepeatCounter(0);
                thisVar.emitSignal("ONSTARTED", new StringValue(event.getName()));
                thisVar.notifyObserversStarted(event.getName());
            } else {
                Gdx.app.error("AnimoVariable", "Event with id " + eventId + " not found");
            }
            return MethodResult.noReturn();
        })),

        Map.entry("PAUSE", MethodSpec.of((self, args, ctx) -> {
            AnimoVariable thisVar = (AnimoVariable) self;
            thisVar.changeAnimoState(AnimoEvent.PAUSE);
            return MethodResult.noReturn();
        })),

        Map.entry("RESUME", MethodSpec.of((self, args, ctx) -> {
            AnimoVariable thisVar = (AnimoVariable) self;
            thisVar.changeAnimoState(AnimoEvent.PLAY);
            return MethodResult.noReturn();
        })),

        Map.entry("STOP", MethodSpec.of((self, args, ctx) -> {
            AnimoVariable thisVar = (AnimoVariable) self;
            boolean emitSignal = args.isEmpty() || ArgumentHelper.getBoolean(args.get(0));
            thisVar.changeAnimoState(AnimoEvent.STOP, emitSignal);
            return MethodResult.noReturn();
        })),

        Map.entry("NEXTFRAME", MethodSpec.of((self, args, ctx) -> {
            AnimoVariable thisVar = (AnimoVariable) self;
            if (thisVar.state.currentEvent == null) return MethodResult.noReturn();
            int nextFrame = thisVar.state.currentFrameNumber + 1;
            if (nextFrame >= thisVar.state.currentEvent.getFramesNumbers().size()) {
                nextFrame = 0;
            }
            thisVar.setCurrentFrameNumber(nextFrame, true);
            thisVar.changeAnimoState(AnimoEvent.NEXT_FRAME);
            return MethodResult.noReturn();
        })),

        Map.entry("PREVFRAME", MethodSpec.of((self, args, ctx) -> {
            AnimoVariable thisVar = (AnimoVariable) self;
            if (thisVar.state.currentEvent == null) return MethodResult.noReturn();
            int prevFrame = thisVar.state.currentFrameNumber - 1;
            if (prevFrame < 0) {
                prevFrame = thisVar.state.currentEvent.getFramesNumbers().size() - 1;
            }
            thisVar.setCurrentFrameNumber(prevFrame, true);
            thisVar.changeAnimoState(AnimoEvent.PREV_FRAME);
            return MethodResult.noReturn();
        })),

        // FRAME METHODS
        Map.entry("SETFRAME", MethodSpec.of((self, args, ctx) -> {
            AnimoVariable thisVar = (AnimoVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("SETFRAME requires at least 1 argument");
            }
            if (args.size() == 1) {
                // SETFRAME(imageNumber)
                int imageNumber = ArgumentHelper.getInt(args.get(0));
                if (imageNumber >= thisVar.data.imagesCount()) {
                    Gdx.app.error("AnimoVariable", "SETFRAME image number out of range");
                    return MethodResult.noReturn();
                }
                thisVar.state.currentFrameNumber = 0;
                thisVar.setCurrentImageNumber(imageNumber, true);
            } else {
                // SETFRAME(eventName, frameNumber)
                String eventName = ArgumentHelper.getString(args.get(0));
                int frameNumber = ArgumentHelper.getInt(args.get(1));
                Event event = thisVar.getEvent(eventName);
                if (event != null && !event.getFrames().isEmpty()) {
                    thisVar.state.currentEvent = event;
                    thisVar.setCurrentFrameNumber(frameNumber, true);
                } else {
                    Gdx.app.error("AnimoVariable", "Event " + eventName + " not found or has no frames");
                }
            }
            thisVar.changeAnimoState(AnimoEvent.SET_FRAME);
            return MethodResult.noReturn();
        })),

        Map.entry("GETFRAME", MethodSpec.of((self, args, ctx) -> {
            AnimoVariable thisVar = (AnimoVariable) self;
            return MethodResult.returns(new IntValue(thisVar.state.currentImageNumber));
        })),

        Map.entry("GETCFRAMEINEVENT", MethodSpec.of((self, args, ctx) -> {
            AnimoVariable thisVar = (AnimoVariable) self;
            return MethodResult.returns(new IntValue(thisVar.state.currentFrameNumber));
        })),

        Map.entry("GETNOFINEVENT", MethodSpec.of((self, args, ctx) -> {
            AnimoVariable thisVar = (AnimoVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("GETNOFINEVENT requires 1 argument");
            }
            String eventName = ArgumentHelper.getString(args.get(0));
            try {
                int eventNumber = Integer.parseInt(eventName);
                List<Event> events = thisVar.data.events();
                if (eventNumber >= 0 && eventNumber < events.size()) {
                    return MethodResult.returns(new IntValue(events.get(eventNumber).getFramesCount()));
                }
            } catch (NumberFormatException ignored) {
                Event event = thisVar.getEvent(eventName);
                if (event != null) {
                    return MethodResult.returns(new IntValue(event.getFramesCount()));
                }
            }
            return MethodResult.returns(new IntValue(0));
        })),

        Map.entry("GETFRAMENAME", MethodSpec.of((self, args, ctx) -> {
            AnimoVariable thisVar = (AnimoVariable) self;
            Event currentEvent = thisVar.state.currentEvent;
            if (currentEvent != null && !currentEvent.getFrameData().isEmpty()) {
                return MethodResult.returns(new StringValue(
                    currentEvent.getFrameData().get(thisVar.state.currentFrameNumber).getName().toUpperCase()));
            }
            return MethodResult.returns(new StringValue("NULL"));
        })),

        // POSITION METHODS
        Map.entry("SETPOSITION", MethodSpec.of((self, args, ctx) -> {
            AnimoVariable thisVar = (AnimoVariable) self;
            if (args.size() < 2) {
                throw new IllegalArgumentException("SETPOSITION requires 2 arguments");
            }
            thisVar.state.posX = ArgumentHelper.getInt(args.get(0)) - thisVar.state.anchorX;
            thisVar.state.posY = ArgumentHelper.getInt(args.get(1)) - thisVar.state.anchorY;
            thisVar.updateRect();
            if (ctx != null && ctx.getGame() != null) {
                ctx.getGame().markCollisionDirty(thisVar);
            }
            return MethodResult.noReturn();
        })),

        Map.entry("GETPOSITIONX", MethodSpec.of((self, args, ctx) -> {
            AnimoVariable thisVar = (AnimoVariable) self;
            boolean absolute = !args.isEmpty();
            if (absolute) {
                return MethodResult.returns(new IntValue(thisVar.state.posX));
            }
            return MethodResult.returns(new IntValue(thisVar.state.rect.getXLeft()));
        })),

        Map.entry("GETPOSITIONY", MethodSpec.of((self, args, ctx) -> {
            AnimoVariable thisVar = (AnimoVariable) self;
            boolean absolute = !args.isEmpty();
            if (absolute) {
                return MethodResult.returns(new IntValue(thisVar.state.posY));
            }
            return MethodResult.returns(new IntValue(thisVar.state.rect.getYTop()));
        })),

        Map.entry("MOVE", MethodSpec.of((self, args, ctx) -> {
            AnimoVariable thisVar = (AnimoVariable) self;
            if (args.size() < 2) {
                throw new IllegalArgumentException("MOVE requires 2 arguments");
            }
            thisVar.state.posX += ArgumentHelper.getInt(args.get(0));
            thisVar.state.posY += ArgumentHelper.getInt(args.get(1));
            thisVar.updateRect();
            if (ctx != null && ctx.getGame() != null) {
                ctx.getGame().markCollisionDirty(thisVar);
            }
            return MethodResult.noReturn();
        })),

        Map.entry("SETANCHOR", MethodSpec.of((self, args, ctx) -> {
            AnimoVariable thisVar = (AnimoVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("SETANCHOR requires arguments");
            }
            if (args.size() == 1) {
                // SETANCHOR(anchorName)
                String anchor = ArgumentHelper.getString(args.get(0)).toUpperCase();
                Box2D rect = thisVar.state.rect;
                switch (anchor) {
                    case "CENTER" -> {
                        thisVar.state.anchorX = rect.getXLeft() + rect.getWidth() / 2;
                        thisVar.state.anchorY = rect.getYTop() + rect.getHeight() / 2;
                    }
                    case "LEFTUPPER" -> {
                        thisVar.state.anchorX = rect.getXLeft();
                        thisVar.state.anchorY = rect.getYTop();
                    }
                    case "RIGHTUPPER" -> {
                        thisVar.state.anchorX = rect.getXRight();
                        thisVar.state.anchorY = rect.getYTop();
                    }
                    case "LEFTLOWER" -> {
                        thisVar.state.anchorX = rect.getXLeft();
                        thisVar.state.anchorY = rect.getYBottom();
                    }
                    case "RIGHTLOWER" -> {
                        thisVar.state.anchorX = rect.getXRight();
                        thisVar.state.anchorY = rect.getYBottom();
                    }
                    case "LEFT" -> {
                        thisVar.state.anchorX = rect.getXLeft();
                        thisVar.state.anchorY = rect.getYTop() + rect.getHeight() / 2;
                    }
                    case "RIGHT" -> {
                        thisVar.state.anchorX = rect.getXRight();
                        thisVar.state.anchorY = rect.getYTop() + rect.getHeight() / 2;
                    }
                    case "TOP" -> {
                        thisVar.state.anchorX = rect.getXLeft() + rect.getWidth() / 2;
                        thisVar.state.anchorY = rect.getYTop();
                    }
                    case "BOTTOM" -> {
                        thisVar.state.anchorX = rect.getXLeft() + rect.getWidth() / 2;
                        thisVar.state.anchorY = rect.getYBottom();
                    }
                    default -> Gdx.app.log("AnimoVariable", "Unknown anchor: " + anchor);
                }
            } else {
                // SETANCHOR(x, y)
                thisVar.state.anchorX = ArgumentHelper.getInt(args.get(0));
                thisVar.state.anchorY = ArgumentHelper.getInt(args.get(1));
            }
            return MethodResult.noReturn();
        })),

        Map.entry("GETANCHOR", MethodSpec.of((self, args, ctx) -> {
            AnimoVariable thisVar = (AnimoVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("GETANCHOR requires 1 argument: arrayName");
            }
            String arrayName = ArgumentHelper.getString(args.get(0));
            Variable arrayVar = ctx.getVariable(arrayName);
            if (arrayVar instanceof ArrayVariable array) {
                array.elements().add(new IntValue(thisVar.state.anchorX));
                array.elements().add(new IntValue(thisVar.state.anchorY));
            }
            return MethodResult.noReturn();
        })),

        // DIMENSION METHODS
        Map.entry("GETWIDTH", MethodSpec.of((self, args, ctx) -> {
            AnimoVariable thisVar = (AnimoVariable) self;
            if (thisVar.state.currentImage != null) {
                return MethodResult.returns(new IntValue(thisVar.state.currentImage.width));
            }
            return MethodResult.returns(new IntValue(0));
        })),

        Map.entry("GETHEIGHT", MethodSpec.of((self, args, ctx) -> {
            AnimoVariable thisVar = (AnimoVariable) self;
            if (thisVar.state.currentImage != null) {
                return MethodResult.returns(new IntValue(thisVar.state.currentImage.height));
            }
            return MethodResult.returns(new IntValue(0));
        })),

        Map.entry("GETMAXWIDTH", MethodSpec.of((self, args, ctx) -> {
            AnimoVariable thisVar = (AnimoVariable) self;
            return MethodResult.returns(new IntValue(thisVar.data.maxWidth()));
        })),

        Map.entry("GETMAXHEIGHT", MethodSpec.of((self, args, ctx) -> {
            AnimoVariable thisVar = (AnimoVariable) self;
            return MethodResult.returns(new IntValue(thisVar.data.maxHeight()));
        })),

        Map.entry("GETCENTERX", MethodSpec.of((self, args, ctx) -> {
            AnimoVariable thisVar = (AnimoVariable) self;
            Box2D rect = thisVar.state.rect;
            return MethodResult.returns(new IntValue(rect.getXLeft() + rect.getWidth() / 2));
        })),

        Map.entry("GETCENTERY", MethodSpec.of((self, args, ctx) -> {
            AnimoVariable thisVar = (AnimoVariable) self;
            Box2D rect = thisVar.state.rect;
            return MethodResult.returns(new IntValue(rect.getYTop() + rect.getHeight() / 2));
        })),

        Map.entry("GETENDX", MethodSpec.of((self, args, ctx) -> {
            AnimoVariable thisVar = (AnimoVariable) self;
            Box2D rect = thisVar.state.rect;
            return MethodResult.returns(new IntValue(rect.getXLeft() + rect.getWidth()));
        })),

        Map.entry("GETENDY", MethodSpec.of((self, args, ctx) -> {
            AnimoVariable thisVar = (AnimoVariable) self;
            Box2D rect = thisVar.state.rect;
            return MethodResult.returns(new IntValue(rect.getYTop() + rect.getHeight()));
        })),

        Map.entry("GETCURRFRAMEPOSX", MethodSpec.of((self, args, ctx) -> {
            AnimoVariable thisVar = (AnimoVariable) self;
            if (thisVar.state.currentImage != null) {
                return MethodResult.returns(new IntValue(thisVar.state.currentImage.offsetX));
            }
            return MethodResult.returns(new IntValue(0));
        })),

        Map.entry("GETCURRFRAMEPOSY", MethodSpec.of((self, args, ctx) -> {
            AnimoVariable thisVar = (AnimoVariable) self;
            if (thisVar.state.currentImage != null) {
                return MethodResult.returns(new IntValue(thisVar.state.currentImage.offsetY));
            }
            return MethodResult.returns(new IntValue(0));
        })),

        // VISIBILITY METHODS
        Map.entry("SHOW", MethodSpec.of((self, args, ctx) -> {
            AnimoVariable thisVar = (AnimoVariable) self;
            thisVar.setVisible(true);
            return MethodResult.noReturn();
        })),

        Map.entry("HIDE", MethodSpec.of((self, args, ctx) -> {
            AnimoVariable thisVar = (AnimoVariable) self;
            thisVar.setVisible(false);
            return MethodResult.noReturn();
        })),

        Map.entry("ISVISIBLE", MethodSpec.of((self, args, ctx) -> {
            AnimoVariable thisVar = (AnimoVariable) self;
            return MethodResult.returns(new BoolValue(thisVar.state.visible));
        })),

        Map.entry("SETOPACITY", MethodSpec.of((self, args, ctx) -> {
            AnimoVariable thisVar = (AnimoVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("SETOPACITY requires 1 argument");
            }
            thisVar.setOpacity(ArgumentHelper.getInt(args.get(0)));
            return MethodResult.noReturn();
        })),

        // OTHER METHODS
        Map.entry("SETFPS", MethodSpec.of((self, args, ctx) -> {
            AnimoVariable thisVar = (AnimoVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("SETFPS requires 1 argument");
            }
            thisVar.setFps(ArgumentHelper.getInt(args.get(0)));
            return MethodResult.noReturn();
        })),

        Map.entry("SETPRIORITY", MethodSpec.of((self, args, ctx) -> {
            AnimoVariable thisVar = (AnimoVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("SETPRIORITY requires 1 argument");
            }
            thisVar.setPriority(ArgumentHelper.getInt(args.get(0)));
            return MethodResult.noReturn();
        })),

        Map.entry("GETPRIORITY", MethodSpec.of((self, args, ctx) -> {
            AnimoVariable thisVar = (AnimoVariable) self;
            return MethodResult.returns(new IntValue(thisVar.state.priority));
        })),

        Map.entry("SETASBUTTON", MethodSpec.of((self, args, ctx) -> {
            AnimoVariable thisVar = (AnimoVariable) self;
            if (args.size() < 2) {
                throw new IllegalArgumentException("SETASBUTTON requires 2 arguments");
            }
            boolean enabled = ArgumentHelper.getBoolean(args.get(0));
            boolean changeCursor = ArgumentHelper.getBoolean(args.get(1));
            thisVar.setChangeCursor(changeCursor);
            Context context = ctx != null ? ctx.context() : null;
            if (!enabled) {
                thisVar.changeButtonState(ButtonEvent.DISABLE, context);
            } else {
                thisVar.changeButtonState(ButtonEvent.ENABLE, context);
            }
            return MethodResult.noReturn();
        })),

        Map.entry("SETFORWARD", MethodSpec.of((self, args, ctx) -> {
            AnimoVariable thisVar = (AnimoVariable) self;
            thisVar.setDirection(1);
            return MethodResult.noReturn();
        })),

        Map.entry("SETBACKWARD", MethodSpec.of((self, args, ctx) -> {
            AnimoVariable thisVar = (AnimoVariable) self;
            thisVar.setDirection(-1);
            return MethodResult.noReturn();
        })),

        Map.entry("ISAT", MethodSpec.of((self, args, ctx) -> {
            AnimoVariable thisVar = (AnimoVariable) self;
            if (args.size() < 3) {
                throw new IllegalArgumentException("ISAT requires 3 arguments");
            }
            int posX = ArgumentHelper.getInt(args.get(0));
            int posY = ArgumentHelper.getInt(args.get(1));
            // boolean checkAlpha = ArgumentHelper.getBoolean(args.get(2)); // TODO: implement alpha check
            return MethodResult.returns(new BoolValue(thisVar.isAt(posX, posY)));
        })),

        Map.entry("ISNEAR", MethodSpec.of((self, args, ctx) -> {
            AnimoVariable thisVar = (AnimoVariable) self;
            if (args.size() < 2) {
                throw new IllegalArgumentException("ISNEAR requires 2 arguments: varName, iouThreshold");
            }
            String otherName = ArgumentHelper.getString(args.get(0));
            int iouThreshold = ArgumentHelper.getInt(args.get(1));
            Variable otherVar = ctx != null ? ctx.getVariable(otherName) : null;
            if (otherVar instanceof AnimoVariable otherAnimo) {
                return MethodResult.returns(new BoolValue(
                    thisVar.isNear(thisVar.getRect(), otherAnimo.getRect(), iouThreshold)));
            }
            return MethodResult.returns(new BoolValue(false));
        })),

        Map.entry("ISPLAYING", MethodSpec.of((self, args, ctx) -> {
            AnimoVariable thisVar = (AnimoVariable) self;
            if (args.isEmpty()) {
                return MethodResult.returns(new BoolValue(thisVar.isPlaying()));
            }
            String eventName = ArgumentHelper.getString(args.get(0));
            Event currentEvent = thisVar.state.currentEvent;
            return MethodResult.returns(new BoolValue(
                currentEvent != null && currentEvent.getName().equalsIgnoreCase(eventName) && thisVar.isPlaying()));
        })),

        Map.entry("MONITORCOLLISION", MethodSpec.of((self, args, ctx) -> {
            AnimoVariable thisVar = (AnimoVariable) self;
            thisVar.state.monitorCollision = true;
            if (ctx != null && ctx.getGame() != null) {
                ctx.getGame().addCollisionMonitor(thisVar);
            }
            return MethodResult.noReturn();
        })),

        Map.entry("REMOVEMONITORCOLLISION", MethodSpec.of((self, args, ctx) -> {
            AnimoVariable thisVar = (AnimoVariable) self;
            thisVar.state.monitorCollision = false;
            if (ctx != null && ctx.getGame() != null) {
                ctx.getGame().removeCollisionMonitor(thisVar);
            }
            return MethodResult.noReturn();
        })),

        Map.entry("MONITORCOLLISIONALPHA", MethodSpec.of((self, args, ctx) -> {
            AnimoVariable thisVar = (AnimoVariable) self;
            thisVar.state.monitorCollisionAlpha = true;
            return MethodResult.noReturn();
        })),

        Map.entry("REMOVEMONITORCOLLISIONALPHA", MethodSpec.of((self, args, ctx) -> {
            AnimoVariable thisVar = (AnimoVariable) self;
            thisVar.state.monitorCollisionAlpha = false;
            return MethodResult.noReturn();
        })),

        Map.entry("LOAD", MethodSpec.of((self, args, ctx) -> {
            AnimoVariable thisVar = (AnimoVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("LOAD requires 1 argument");
            }
            thisVar.state.filename = ArgumentHelper.getString(args.get(0));
            // Actual loading is done by the interpreter using AnimoLoader
            return MethodResult.noReturn();
        })),

        Map.entry("GETNAME", MethodSpec.of((self, args, ctx) -> {
            AnimoVariable thisVar = (AnimoVariable) self;
            return MethodResult.returns(new StringValue(thisVar.name()));
        })),

        Map.entry("GETNOE", MethodSpec.of((self, args, ctx) -> {
            AnimoVariable thisVar = (AnimoVariable) self;
            return MethodResult.returns(new IntValue(thisVar.data.eventsCount()));
        })),

        Map.entry("GETNOF", MethodSpec.of((self, args, ctx) -> {
            AnimoVariable thisVar = (AnimoVariable) self;
            return MethodResult.returns(new IntValue(thisVar.data.imagesCount()));
        })),

        Map.entry("GETEVENTNAME", MethodSpec.of((self, args, ctx) -> {
            AnimoVariable thisVar = (AnimoVariable) self;
            if (args.isEmpty()) {
                Event currentEvent = thisVar.state.currentEvent;
                if (currentEvent != null) {
                    return MethodResult.returns(new StringValue(currentEvent.getName().toUpperCase()));
                }
                return MethodResult.returns(new StringValue(""));
            }
            int eventIndex = ArgumentHelper.getInt(args.get(0));
            List<Event> events = thisVar.data.events();
            if (eventIndex >= 0 && eventIndex < events.size()) {
                return MethodResult.returns(new StringValue(events.get(eventIndex).getName().toUpperCase()));
            }
            Event currentEvent = thisVar.state.currentEvent;
            return MethodResult.returns(new StringValue(currentEvent != null ? currentEvent.getName().toUpperCase() : ""));
        })),

        Map.entry("TOP", MethodSpec.of((self, args, ctx) -> {
            // TODO: implement this method
            return MethodResult.noReturn();
        })),

        Map.entry("SETFRAMENAME", MethodSpec.of((self, args, ctx) -> {
            // TODO: implement this method
            throw new UnsupportedOperationException("SETFRAMENAME is not implemented yet");
        }))
    );

    @Override
    public String toString() {
        return "AnimoVariable[" + name + ", event=" + (state.currentEvent != null ? state.currentEvent.getName() : "none") +
               ", frame=" + state.currentFrameNumber + ", state=" + state.animationState + "]";
    }
}
