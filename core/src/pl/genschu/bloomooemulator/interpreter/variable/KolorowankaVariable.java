package pl.genschu.bloomooemulator.interpreter.variable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import pl.genschu.bloomooemulator.annotations.InternalMutable;
import pl.genschu.bloomooemulator.engine.render.RenderOrder;
import pl.genschu.bloomooemulator.interpreter.context.Context;
import pl.genschu.bloomooemulator.interpreter.helpers.ArgumentHelper;
import pl.genschu.bloomooemulator.interpreter.values.*;
import pl.genschu.bloomooemulator.interpreter.variable.capabilities.Initializable;
import pl.genschu.bloomooemulator.loader.PtrLoader;
import pl.genschu.bloomooemulator.loader.SoundLoader;
import pl.genschu.bloomooemulator.objects.Image;
import pl.genschu.bloomooemulator.utils.FileUtils;

import java.util.*;

/**
 * KolorowankaVariable — interactive coloring picture. Loads a PTR file
 * (outline bitmap + closed fields as span regions + palette) and lets the player
 * fill fields with the selected palette color or a tiled pattern.
 */
public record KolorowankaVariable(
    String name,
    @InternalMutable KolorowankaState state,
    Map<String, SignalHandler> signals
) implements Variable, Initializable {

    /** Sentinel screen color for "uncolored". */
    public static final int COLOR_NONE = 0x10000;

    /** Fade animation: 10 steps × 10 ms. */
    private static final int FADE_STEPS = 10;
    private static final int FADE_STEP_MS = 10;

    private static final Random RANDOM = new Random();

    /** Runtime state of a single PTR field. */
    public static final class Field {
        public PtrLoader.Field def;
        public boolean locked;
        public boolean disabled;
        /** Current palette index (−1 = uncolored / transient fade color). */
        public int colorId = -1;
        /** Current screen color (RGB565) or {@link #COLOR_NONE}. */
        public int color16 = COLOR_NONE;
        /** Field ever received a color — its fill region is drawn. */
        public boolean hasColour;
    }

    public static final class KolorowankaState {
        public Context context;

        // Object properties (CNV)
        public boolean toCanvas = true;
        public int priority = 0;
        public long renderOrder = RenderOrder.next();
        public boolean visible = true;
        public boolean enabled = true;
        public String seqAnnName = "";
        public String seqSndName = "";

        // Loaded PTR data
        public PtrLoader.PtrFile ptr;
        public int[] palette16 = new int[0];
        public final List<Field> fields = new ArrayList<>();

        // Painter
        public int posX = 0;
        public int posY = 0;
        public Pixmap pixmap;
        public Texture texture;
        public boolean textureDirty;

        // Colouring
        public int currentColorId = 0;
        public int fadeFrom16 = 0;
        public int fadeTo16 = 0;
        public int fadeStep = 0;
        public final List<Field> pendingFields = new ArrayList<>();
        public int defColor16 = -1;
        public int lastClickX = 0;
        public int lastClickY = 0;
        public final Map<Integer, Pixmap> patterns = new HashMap<>();

        // Sound
        public String latestWav = "";
        public SoundVariable wavPlayer;

        public long lastFadeTimeMs = -1;

        public boolean isLoaded() {
            return ptr != null;
        }

        public KolorowankaState copy() {
            // Shared mutable state, like other engine-object variables (painter,
            // pending animation and loaded PTR cannot be meaningfully cloned).
            return this;
        }

        public void dispose() {
            if (texture != null) {
                texture.dispose();
                texture = null;
            }
            if (pixmap != null) {
                pixmap.dispose();
                pixmap = null;
            }
            for (Pixmap p : patterns.values()) {
                p.dispose();
            }
            patterns.clear();
        }
    }

    public KolorowankaVariable {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Variable name cannot be null or empty");
        }
        if (state == null) {
            state = new KolorowankaState();
        }
        if (signals == null) {
            signals = Map.of();
        } else {
            signals = Map.copyOf(signals);
        }
    }

    public KolorowankaVariable(String name) {
        this(name, new KolorowankaState(), Map.of());
    }

    @Override
    public Value value() {
        return NullValue.INSTANCE;
    }

    @Override
    public VariableType type() {
        return VariableType.KOLOROWANKA;
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
        if (handler != null) {
            newSignals.put(signalName, handler);
        } else {
            newSignals.remove(signalName);
        }
        return new KolorowankaVariable(name, state, newSignals);
    }

    @Override
    public Variable copyAs(String newName) {
        return new KolorowankaVariable(newName, state.copy(), new HashMap<>(signals));
    }

    // ========================================
    // INITIALIZABLE
    // ========================================

    @Override
    public void init(Context context) {
        state.context = context;

        String toCanvas = context.getAttribute(name, "TOCANVAS");
        if (toCanvas != null) {
            state.toCanvas = toCanvas.equalsIgnoreCase("TRUE");
        }

        String priority = context.getAttribute(name, "PRIORITY");
        if (priority != null) {
            try { state.priority = Integer.parseInt(priority.trim()); } catch (NumberFormatException ignored) {}
        }

        String visible = context.getAttribute(name, "VISIBLE");
        if (visible != null) {
            state.visible = visible.equalsIgnoreCase("TRUE");
        }

        String seqAnn = context.getAttribute(name, "SEQANN");
        if (seqAnn != null) {
            state.seqAnnName = seqAnn.trim();
        }

        String seqSnd = context.getAttribute(name, "SEQSND");
        if (seqSnd != null) {
            state.seqSndName = seqSnd.trim();
        }
    }

    // ========================================
    // ACCESSORS (for managers)
    // ========================================

    public boolean isVisible() { return state.visible && state.toCanvas && state.isLoaded(); }
    public int getPriority() { return state.priority; }
    public long getRenderOrder() { return state.renderOrder; }
    public int getPosX() { return state.posX; }
    public int getPosY() { return state.posY; }
    public int getWidth() { return state.ptr != null ? state.ptr.width : 0; }
    public int getHeight() { return state.ptr != null ? state.ptr.height : 0; }

    /** Returns the up-to-date texture (rebuilds it from the pixmap when dirty). Call on the GL thread. */
    public Texture getTexture() {
        if (state.pixmap == null) return null;
        if (state.texture == null) {
            state.texture = new Texture(state.pixmap);
            state.textureDirty = false;
        } else if (state.textureDirty) {
            state.texture.draw(state.pixmap, 0, 0);
            state.textureDirty = false;
        }
        return state.texture;
    }

    // ========================================
    // INPUT
    // ========================================

    /** Handles a left-button press at scene coordinates. */
    public void handleClick(int sceneX, int sceneY) {
        KolorowankaState s = state;
        if (!s.enabled || !s.isLoaded() || !s.visible) return;

        int x = sceneX - s.posX;
        int y = sceneY - s.posY;
        if (x < 0 || y < 0 || x >= s.ptr.width || y >= s.ptr.height) return;
        s.lastClickX = x;
        s.lastClickY = y;

        boolean hasPattern = s.patterns.containsKey(s.currentColorId);
        Field field = getFieldAt(x, y);
        if (field == null) return;

        // Already painted with the selected pattern/color → ignore
        if (hasPattern) {
            if (field.colorId == s.currentColorId) return;
        } else if (field.color16 == paletteColor16(s.currentColorId)) {
            return;
        }

        // Fade animation in progress → ignore clicks
        if (!s.pendingFields.isEmpty()) return;

        if (field.disabled) {
            emitSignal("ONDISABLED", new StringValue(field.def.name));
            return;
        }
        if (field.locked && (field.def.requiredColorId == -1
                ? field.colorId == -1
                : field.colorId == field.def.requiredColorId)) {
            emitSignal("ONLOCKED", new StringValue(field.def.name));
            return;
        }

        emitSignal("ONCLICKED", new StringValue(field.def.name));
        s.pendingFields.add(field);

        // Patterns (either as the crayon or already on the field) are applied instantly;
        // plain colors run through the 10-step fade in the update loop.
        boolean immediate = hasPattern
                || (field.colorId != -1 && s.patterns.containsKey(field.colorId));
        if (immediate) {
            s.fadeStep = 0;
            colorPending(paletteColor16(s.currentColorId), s.currentColorId);
            s.pendingFields.clear();
            playReactions(field);
            emitSignal("ONCOLOURED", new StringValue(field.def.name));
        } else {
            s.fadeFrom16 = field.color16;
        }

        // Group coloring: fields sharing the group id follow the clicked field
        if (field.def.groupId != -1 && isColorable(field)) {
            for (Field other : s.fields) {
                if (other == field || other.def.groupId != field.def.groupId) continue;
                playReactions(other);
                s.pendingFields.add(other);
                if (immediate) {
                    s.fadeStep = 0;
                    colorPending(paletteColor16(s.currentColorId), s.currentColorId);
                    s.pendingFields.clear();
                    emitSignal("ONCOLOURED", new StringValue(field.def.name));
                }
                emitSignal("ONCLICKED", new StringValue(other.def.name));
            }
        }

        if (checkIfFinished()) {
            emitSignal("ONFINISHED");
        }

        // Original quirk: fade of an uncolored field starts from SETDEFCOLOR (or white)
        if (s.fadeFrom16 == COLOR_NONE) {
            s.fadeFrom16 = s.defColor16 != -1 ? s.defColor16 : 0xFFFF;
        }
    }

    // ========================================
    // UPDATE
    // ========================================

    /** Advances the color fade animation; driven by the engine clock. */
    public void update(long engineTimeMs) {
        KolorowankaState s = state;
        if (s.lastFadeTimeMs < 0) {
            s.lastFadeTimeMs = engineTimeMs;
            return;
        }
        while (engineTimeMs - s.lastFadeTimeMs >= FADE_STEP_MS) {
            s.lastFadeTimeMs += FADE_STEP_MS;
            fadeTick();
        }
    }

    private void fadeTick() {
        KolorowankaState s = state;
        if (!s.enabled || s.pendingFields.isEmpty() || !s.isLoaded()) return;
        if (s.fadeFrom16 == s.fadeTo16) {
            s.fadeStep = 0;
            return;
        }
        s.fadeStep++;
        if (s.fadeStep == FADE_STEPS) {
            colorPending(paletteColor16(s.currentColorId), s.currentColorId);
            s.pendingFields.clear();
            s.fadeStep = 0;
            Field field = getFieldAt(s.lastClickX, s.lastClickY);
            if (field != null) {
                playReactions(field);
                emitSignal("ONCOLOURED", new StringValue(field.def.name));
            }
            if (checkIfFinished()) {
                emitSignal("ONFINISHED");
            }
        } else {
            colorPending(lerp565(s.fadeFrom16, s.fadeTo16, s.fadeStep), -1);
        }
    }

    // ========================================
    // CORE LOGIC
    // ========================================

    private Field getFieldAt(int x, int y) {
        for (Field field : state.fields) {
            if (field.def.fillRegion != null && field.def.fillRegion.contains(x, y)) {
                return field;
            }
        }
        return null;
    }

    /** Field reacts to clicks (not disabled, not locked-and-complete). */
    private boolean isColorable(Field field) {
        if (field.disabled) return false;
        return !field.locked
                || field.def.requiredColorId == -1
                || field.def.requiredColorId != field.colorId;
    }

    private void setFieldColor(Field field, int color16, int colorId) {
        field.hasColour = true;
        field.color16 = color16;
        field.colorId = colorId;
        if (color16 != -1 && color16 == colorId) {
            field.disabled = true;
        }
    }

    private void colorPending(int color16, int colorId) {
        for (Field field : state.pendingFields) {
            setFieldColor(field, color16, colorId);
        }
        repaint();
    }

    /** checkIfFished: all fields with a required color match it, and at least one exists. */
    private boolean checkIfFinished() {
        int required = 0;
        int matching = 0;
        for (Field field : state.fields) {
            if (field.def.requiredColorId == -1) continue;
            required++;
            if (field.colorId == field.def.requiredColorId) matching++;
        }
        return required > 0 && required == matching;
    }

    private int paletteColor16(int colorId) {
        if (colorId < 0 || colorId >= state.palette16.length) return COLOR_NONE;
        return state.palette16[colorId];
    }

    private static int rgb565(int r, int g, int b) {
        return ((r & 0xF8) << 8) | ((g & 0xFC) << 3) | ((b & 0xF8) >> 3);
    }

    /** Channel-wise linear step in RGB565 space, like the original 16 bpp path. */
    private static int lerp565(int from, int to, int step) {
        int r = ((from >> 11) & 0x1F) + (((to >> 11 & 0x1F) - ((from >> 11) & 0x1F)) * step) / FADE_STEPS;
        int g = ((from >> 5) & 0x3F) + (((to >> 5 & 0x3F) - ((from >> 5) & 0x3F)) * step) / FADE_STEPS;
        int b = (from & 0x1F) + (((to & 0x1F) - (from & 0x1F)) * step) / FADE_STEPS;
        return ((r & 0x1F) << 11) | ((g & 0x3F) << 5) | (b & 0x1F);
    }

    // ========================================
    // REACTIONS
    // ========================================

    private void playReactions(Field field) {
        for (PtrLoader.Reaction reaction : field.def.reactions) {
            if (!reactionMatches(reaction, field)) continue;

            if (!reaction.annEventName.isEmpty()) {
                playAnnEvent(reaction.annEventName);
            }
            if (!reaction.wavNames.isEmpty()) {
                String[] tokens = reaction.wavNames.split("; ");
                String wav = tokens[RANDOM.nextInt(tokens.length)].trim();
                if (!wav.isEmpty()) {
                    playWav(wav);
                    setLatestWav(wav);
                }
            }
        }
    }

    /** Conditions compare the SCREEN color of listed fields. */
    private boolean reactionMatches(PtrLoader.Reaction reaction, Field colouredField) {
        int[] conditions = reaction.conditions;
        for (int i = 0; i + 1 < conditions.length; i += 2) {
            int expected = conditions[i];
            int fieldIndex = conditions[i + 1];
            if (fieldIndex < 0 || fieldIndex >= state.fields.size()) return false;
            if (state.fields.get(fieldIndex).color16 != expected) return false;
        }
        int painted = colouredField.colorId;
        return painted == -1 || painted == reaction.triggerColorId;
    }

    private void playAnnEvent(String eventName) {
        if (state.seqAnnName.isEmpty() || state.context == null) return;
        Variable seq = state.context.getVariable(state.seqAnnName);
        if (seq instanceof SequenceVariable sequence) {
            sequence.playEvent(eventName, state.context);
        } else {
            Gdx.app.error("KolorowankaVariable", name + ": SEQANN \"" + state.seqAnnName + "\" is not a SEQUENCE");
        }
    }

    /** Plays a WAV from the sound FX directory. */
    private void playWav(String wavName) {
        if (state.context == null || state.context.getGame() == null) return;
        try {
            if (state.wavPlayer == null) {
                state.wavPlayer = new SoundVariable(name + "_KLRWAV");
            } else {
                state.wavPlayer.stop(false);
            }
            String path = wavName;
            if (!path.contains(".")) {
                path += ".WAV";
            }
            if (!path.startsWith("$")) {
                path = "$WAVS\\" + path;
            }
            String vfsPath = FileUtils.resolveVfsPath(state.context.getGame(), path);
            SoundLoader.loadSound(state.wavPlayer, state.context.getGame().getAudioFileHandle(vfsPath));
            state.wavPlayer.play();
        } catch (Exception e) {
            Gdx.app.error("KolorowankaVariable", name + ": error playing WAV " + wavName, e);
        }
    }

    private void setLatestWav(String wav) {
        state.latestWav = wav;
        emitSignal("ONSOUND");
    }

    /** Resolves "ANIMO!n" pattern references: 1-based frame of the first event (else current image). */
    private static Image resolveAnimoFrame(AnimoVariable animo, String frameRef) {
        if (frameRef != null && !animo.getEvents().isEmpty()) {
            try {
                int frame = Integer.parseInt(frameRef) - 1;
                var event = animo.getEvents().get(0);
                if (event.getFrames() != null && frame >= 0 && frame < event.getFrames().size()) {
                    return event.getFrames().get(frame);
                }
                if (event.getFramesNumbers() != null && frame >= 0 && frame < event.getFramesNumbers().size()) {
                    int imageNumber = event.getFramesNumbers().get(frame);
                    if (imageNumber >= 0 && imageNumber < animo.getImages().size()) {
                        return animo.getImages().get(imageNumber);
                    }
                }
            } catch (NumberFormatException ignored) {}
        }
        return animo.getCurrentImage();
    }

    // ========================================
    // LOADING
    // ========================================

    private void loadPtr(String filename, MethodContext ctx) {
        flushData();
        String path = filename;
        if (!path.contains(".")) {
            path += ".ptr";
        }
        PtrLoader.PtrFile ptr;
        try {
            String vfsPath = FileUtils.resolveVfsPath(ctx.getGame(), path);
            try (java.io.InputStream is = ctx.getGame().getVfs().openRead(vfsPath)) {
                ptr = PtrLoader.load(is);
            }
        } catch (Exception e) {
            Gdx.app.error("KolorowankaVariable", name + ": error loading PTR: " + path, e);
            return;
        }
        applyPtr(ptr);
    }

    /** Installs parsed PTR data (exposed for tests; LOAD goes through the VFS). */
    public void applyPtr(PtrLoader.PtrFile ptr) {
        flushData();
        state.ptr = ptr;

        state.palette16 = new int[state.ptr.palette.length];
        for (int i = 0; i < state.palette16.length; i++) {
            // Palette entries are Windows COLORREF: 0x00BBGGRR
            int bgr = state.ptr.palette[i];
            state.palette16[i] = rgb565(bgr & 0xFF, (bgr >> 8) & 0xFF, (bgr >> 16) & 0xFF);
        }

        state.fields.clear();
        for (PtrLoader.Field def : state.ptr.fields) {
            Field field = new Field();
            field.def = def;
            field.locked = def.locked;
            field.disabled = def.disabled;
            if (def.initialColorId == -1) {
                field.colorId = -1;
                field.color16 = COLOR_NONE;
                field.hasColour = false;
            } else {
                field.colorId = def.initialColorId;
                field.color16 = paletteColor16(def.initialColorId);
                field.hasColour = true;
                // Original auto-lock: field already has its required color
                if (def.initialColorId == def.requiredColorId) {
                    field.locked = true;
                }
            }
            state.fields.add(field);
        }

        state.currentColorId = 0;
        state.fadeTo16 = paletteColor16(0);
        state.fadeStep = 0;
        state.pendingFields.clear();

        if (state.pixmap != null) {
            state.pixmap.dispose();
        }
        if (state.texture != null) {
            state.texture.dispose();
            state.texture = null;
        }
        state.pixmap = new Pixmap(state.ptr.width, state.ptr.height, Pixmap.Format.RGBA8888);
        state.pixmap.setBlending(Pixmap.Blending.None);
        repaint();
    }

    private void flushData() {
        state.ptr = null;
        state.palette16 = new int[0];
        state.fields.clear();
        state.pendingFields.clear();
        state.currentColorId = 0;
        state.fadeStep = 0;
    }

    // ========================================
    // RENDERING
    // ========================================

    /**
     * Repaints the whole picture into the pixmap. Per pixel (a = outline brightness,
     * 255 = paper, 0 = black line): a colored field pixel is the fill color scaled
     * towards black by a/255 (opaque); an uncolored pixel is black with alpha 255−a
     * (the scene background shows through the paper).
     */
    private void repaint() {
        KolorowankaState s = state;
        if (s.pixmap == null || s.ptr == null) return;

        s.pixmap.setColor(0, 0, 0, 0);
        s.pixmap.fill();

        // Pass 1: fill regions of fields that ever received a color
        for (Field field : s.fields) {
            if (field.hasColour) {
                paintRegion(field.def.fillRegion, field);
            }
        }
        // Pass 2: outline regions (skipped for blank fields with the hide flag)
        for (Field field : s.fields) {
            if (field.color16 == COLOR_NONE && field.def.hideOutlineWhenBlank) {
                continue;
            }
            paintRegion(field.def.outlineRegion, field);
        }
        s.textureDirty = true;
    }

    private void paintRegion(PtrLoader.Region region, Field field) {
        KolorowankaState s = state;
        if (region == null || region.rows == null) return;

        Pixmap pattern = field.colorId != -1 ? s.patterns.get(field.colorId) : null;
        int color16 = field.color16;

        for (int r = 0; r < region.rows.length; r++) {
            int y = region.top + r;
            if (y < 0 || y >= s.ptr.height) continue;
            int[] spans = region.rows[r];
            for (int i = 0; i + 1 < spans.length; i += 2) {
                int start = spans[i];
                int end = start + spans[i + 1];
                for (int x = Math.max(0, start); x < Math.min(end, s.ptr.width); x++) {
                    int a = s.ptr.bitmap[y * s.ptr.width + x] & 0xFF;
                    int rgba;
                    if (pattern != null) {
                        int px = pattern.getPixel(x % pattern.getWidth(), y % pattern.getHeight());
                        rgba = darken(px >>> 24, (px >> 16) & 0xFF, (px >> 8) & 0xFF, a);
                    } else if (color16 == COLOR_NONE) {
                        // Uncolored: transparent paper + anti-aliased black line
                        rgba = 255 - a;
                    } else {
                        int c = color16 & 0xFFFF;
                        rgba = darken(
                                ((c >> 11) & 0x1F) * 255 / 31,
                                ((c >> 5) & 0x3F) * 255 / 63,
                                (c & 0x1F) * 255 / 31,
                                a);
                    }
                    s.pixmap.drawPixel(x, y, rgba);
                }
            }
        }
    }

    /** Opaque pixel: color scaled towards black by the outline brightness. */
    private static int darken(int r, int g, int b, int a) {
        return ((r * a / 255) << 24) | ((g * a / 255) << 16) | ((b * a / 255) << 8) | 0xFF;
    }

    // ========================================
    // METHODS
    // ========================================

    private static final Map<String, MethodSpec> METHODS = Map.ofEntries(
        Map.entry("LOAD", MethodSpec.of((self, args, ctx) -> {
            KolorowankaVariable klr = (KolorowankaVariable) self;
            klr.loadPtr(ArgumentHelper.getString(args.get(0)), ctx);
            return MethodResult.noReturn();
        })),

        Map.entry("SETPOSITION", MethodSpec.of((self, args, ctx) -> {
            KolorowankaVariable klr = (KolorowankaVariable) self;
            klr.state.posX = ArgumentHelper.getInt(args.get(0));
            klr.state.posY = ArgumentHelper.getInt(args.get(1));
            return MethodResult.noReturn();
        })),

        Map.entry("MOVE", MethodSpec.of((self, args, ctx) -> {
            KolorowankaVariable klr = (KolorowankaVariable) self;
            klr.state.posX += ArgumentHelper.getInt(args.get(0));
            klr.state.posY += ArgumentHelper.getInt(args.get(1));
            return MethodResult.noReturn();
        })),

        Map.entry("SETCOLOR", MethodSpec.of((self, args, ctx) -> {
            KolorowankaVariable klr = (KolorowankaVariable) self;
            // Ignored while the fade animation is running
            if (klr.state.pendingFields.isEmpty()) {
                klr.state.currentColorId = ArgumentHelper.getInt(args.get(0));
                klr.state.fadeTo16 = klr.paletteColor16(klr.state.currentColorId);
            }
            return MethodResult.noReturn();
        })),

        Map.entry("FLUSH", MethodSpec.of((self, args, ctx) -> {
            ((KolorowankaVariable) self).flushData();
            return MethodResult.noReturn();
        })),

        Map.entry("SHOW", MethodSpec.of((self, args, ctx) -> {
            ((KolorowankaVariable) self).state.visible = true;
            return MethodResult.noReturn();
        })),

        Map.entry("HIDE", MethodSpec.of((self, args, ctx) -> {
            ((KolorowankaVariable) self).state.visible = false;
            return MethodResult.noReturn();
        })),

        Map.entry("ENABLE", MethodSpec.of((self, args, ctx) -> {
            ((KolorowankaVariable) self).state.enabled = true;
            return MethodResult.noReturn();
        })),

        Map.entry("DISABLE", MethodSpec.of((self, args, ctx) -> {
            ((KolorowankaVariable) self).state.enabled = false;
            return MethodResult.noReturn();
        })),

        Map.entry("CLEAR", MethodSpec.of((self, args, ctx) -> {
            KolorowankaVariable klr = (KolorowankaVariable) self;
            // Restores initial colors; does NOT restore locked/disabled flags (original quirk)
            for (Field field : klr.state.fields) {
                int initial = field.def.initialColorId;
                klr.setFieldColor(field,
                        initial == -1 ? COLOR_NONE : klr.paletteColor16(initial),
                        initial);
            }
            klr.repaint();
            return MethodResult.noReturn();
        })),

        Map.entry("APPLYANNSEQ", MethodSpec.of((self, args, ctx) -> {
            KolorowankaVariable klr = (KolorowankaVariable) self;
            klr.state.seqAnnName = args.isEmpty() ? "" : ArgumentHelper.getString(args.get(0));
            return MethodResult.noReturn();
        })),

        Map.entry("APPLYSNDSEQ", MethodSpec.of((self, args, ctx) -> {
            KolorowankaVariable klr = (KolorowankaVariable) self;
            klr.state.seqSndName = args.isEmpty() ? "" : ArgumentHelper.getString(args.get(0));
            return MethodResult.noReturn();
        })),

        Map.entry("STOPWAV", MethodSpec.of((self, args, ctx) -> {
            KolorowankaVariable klr = (KolorowankaVariable) self;
            if (klr.state.wavPlayer != null) {
                klr.state.wavPlayer.stop(false);
            }
            return MethodResult.noReturn();
        })),

        Map.entry("PRINT", MethodSpec.of((self, args, ctx) -> {
            // Original prints the window on the system printer — intentionally a no-op here
            Gdx.app.log("KolorowankaVariable", ((KolorowankaVariable) self).name + ": PRINT ignored (no printer support for now)");
            return MethodResult.noReturn();
        })),

        Map.entry("GETLATESTWAV", MethodSpec.of((self, args, ctx) ->
            MethodResult.returns(new StringValue(((KolorowankaVariable) self).state.latestWav))
        )),

        Map.entry("GETFIELDNAME", MethodSpec.of((self, args, ctx) -> {
            KolorowankaVariable klr = (KolorowankaVariable) self;
            int x = ArgumentHelper.getInt(args.get(0));
            int y = ArgumentHelper.getInt(args.get(1));
            Field field = klr.getFieldAt(x, y);
            return MethodResult.returns(new StringValue(field != null ? field.def.name : "NULL"));
        })),

        Map.entry("LOADSTATE", MethodSpec.of((self, args, ctx) -> {
            KolorowankaVariable klr = (KolorowankaVariable) self;
            Variable arrayVar = ctx.getVariable(ArgumentHelper.getString(args.get(0)));
            if (arrayVar instanceof ArrayVariable array) {
                List<Field> fields = klr.state.fields;
                for (int i = 0; i < fields.size() && i < array.size(); i++) {
                    Value value = array.get(i);
                    int colorId = value != null ? value.toInt().value() : -1;
                    if (colorId != -1) {
                        klr.setFieldColor(fields.get(i), klr.paletteColor16(colorId), colorId);
                    }
                }
                klr.repaint();
            }
            return MethodResult.noReturn();
        })),

        Map.entry("SAVESTATE", MethodSpec.of((self, args, ctx) -> {
            KolorowankaVariable klr = (KolorowankaVariable) self;
            Variable arrayVar = ctx.getVariable(ArgumentHelper.getString(args.get(0)));
            if (arrayVar instanceof ArrayVariable array) {
                array.elements().clear();
                for (Field field : klr.state.fields) {
                    array.elements().add(new IntValue(field.colorId));
                }
            }
            return MethodResult.noReturn();
        })),

        Map.entry("DRAW", MethodSpec.of((self, args, ctx) -> {
            KolorowankaVariable klr = (KolorowankaVariable) self;
            // Reveal the solution: every field gets its required color. Original quirk:
            // requiredColorId == -1 yields screen color −1 (≈ white), not "uncolored".
            for (Field field : klr.state.fields) {
                int required = field.def.requiredColorId;
                klr.setFieldColor(field,
                        required == -1 ? -1 : klr.paletteColor16(required),
                        required);
            }
            klr.repaint();
            return MethodResult.noReturn();
        })),

        Map.entry("GETCURRENTCOLOUR", MethodSpec.of((self, args, ctx) -> {
            KolorowankaVariable klr = (KolorowankaVariable) self;
            // Despite the name: returns the palette index of a FIELD (−1 = uncolored)
            if (args.size() == 1) {
                String fieldName = ArgumentHelper.getString(args.get(0));
                for (Field field : klr.state.fields) {
                    if (field.def.name.equalsIgnoreCase(fieldName)) {
                        return MethodResult.returns(new IntValue(field.colorId));
                    }
                }
                return MethodResult.returns(new IntValue(-1));
            }
            int x = ArgumentHelper.getInt(args.get(0));
            int y = ArgumentHelper.getInt(args.get(1));
            Field field = klr.getFieldAt(x, y);
            return MethodResult.returns(new IntValue(field != null ? field.colorId : -1));
        })),

        Map.entry("SETPATTERN", MethodSpec.of((self, args, ctx) -> {
            KolorowankaVariable klr = (KolorowankaVariable) self;
            int colorId = ArgumentHelper.getInt(args.get(0));
            String objectName = ArgumentHelper.getString(args.get(1));

            // "Wyprawa Po Złote Runo" references ANIMO frames as "ANNPATTERNS!n" (1-based frame of the first event)
            String frameRef = null;
            int bang = objectName.indexOf('!');
            if (bang >= 0) {
                frameRef = objectName.substring(bang + 1).trim();
                objectName = objectName.substring(0, bang);
            }

            Variable gfx = ctx.getVariable(objectName);
            pl.genschu.bloomooemulator.objects.Image image = null;
            if (gfx instanceof ImageVariable img) {
                image = img.getImage();
            } else if (gfx instanceof AnimoVariable animo) {
                image = resolveAnimoFrame(animo, frameRef);
            }

            if (image != null && image.getImageTexture() != null) {
                TextureData data = image.getImageTexture().getTextureData();
                if (!data.isPrepared()) data.prepare();
                Pixmap old = klr.state.patterns.put(colorId, data.consumePixmap());
                if (old != null) old.dispose();
            } else {
                Gdx.app.error("KolorowankaVariable",
                        klr.name + ": SETPATTERN — unsupported or unloaded graphics object: "
                                + objectName + (frameRef != null ? "!" + frameRef : ""));
            }
            return MethodResult.noReturn();
        })),

        Map.entry("SETDEFCOLOR", MethodSpec.of((self, args, ctx) -> {
            KolorowankaVariable klr = (KolorowankaVariable) self;
            int r = ArgumentHelper.getInt(args.get(0));
            int g = ArgumentHelper.getInt(args.get(1));
            int b = ArgumentHelper.getInt(args.get(2));
            klr.state.defColor16 = rgb565(r, g, b);
            return MethodResult.noReturn();
        }))
    );

    @Override
    public String toString() {
        return "KolorowankaVariable[" + name + ", loaded=" + state.isLoaded()
                + ", fields=" + state.fields.size() + ", visible=" + state.visible + "]";
    }
}
