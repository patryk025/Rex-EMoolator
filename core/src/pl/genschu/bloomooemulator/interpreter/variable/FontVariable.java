package pl.genschu.bloomooemulator.interpreter.variable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.GdxRuntimeException;
import pl.genschu.bloomooemulator.annotations.InternalMutable;
import pl.genschu.bloomooemulator.interpreter.context.Context;
import pl.genschu.bloomooemulator.interpreter.values.*;
import pl.genschu.bloomooemulator.interpreter.variable.capabilities.Initializable;
import pl.genschu.bloomooemulator.loader.FontLoadable;
import pl.genschu.bloomooemulator.loader.FontLoader;
import pl.genschu.bloomooemulator.objects.FontCropping;

import java.util.*;
import java.util.stream.Collectors;

/**
 * FontVariable represents a bitmap font loaded from game resources.
 * Contains character textures, croppings (spacing), and kerning data.
 *
 * Uses mutable FontState because font data is loaded incrementally by FontLoader.
 */
public record FontVariable(
    String name,
    @InternalMutable FontState state,
    Map<String, SignalHandler> signals
) implements Variable, FontLoadable, Initializable {

    public static final class FontState {
        public final Map<Character, TextureRegion> charTextures = new LinkedHashMap<>();
        public final Map<Character, FontCropping> charCroppings = new LinkedHashMap<>();
        public final Map<Character, Map<Character, Integer>> charKerningsMap = new HashMap<>();
        public int charHeight;
        public int charWidth;
        public int pixelFormat;

        public FontState() {}

        public void clear() {
            charTextures.clear();
            charCroppings.clear();
            charKerningsMap.clear();
            charHeight = 0;
            charWidth = 0;
            pixelFormat = 0;
        }

        public void setCharTexture(char c, TextureRegion texture) {
            charTextures.put(c, texture);
        }

        public TextureRegion getCharTexture(char c) {
            return charTextures.get(c);
        }

        public void setCharCropping(char c, FontCropping cropping) {
            charCroppings.put(c, cropping);
        }

        public FontCropping getCharCropping(char c) {
            if (!charCroppings.containsKey(c)) {
                return new FontCropping(0, 0);
            }
            return charCroppings.get(c);
        }

        public int getCharKerning(char c, char k) {
            if (charKerningsMap.containsKey(c) && charKerningsMap.get(c).containsKey(k)) {
                return charKerningsMap.get(c).get(k);
            }
            return 0;
        }

        public List<Character> getCharTextureKeys() {
            return List.copyOf(charTextures.keySet());
        }

        public void setCharKerning(int i, int[] kernings) {
            char character = getCharTextureKeys().get(i);
            charKerningsMap.put(character, new HashMap<>());
        }

        public void setCharKerning(int i, int i2, int kerning) {
            char character = getCharTextureKeys().get(i);
            char character2 = getCharTextureKeys().get(i2);
            charKerningsMap.putIfAbsent(character, new HashMap<>());
            charKerningsMap.get(character).put(character2, kerning);
        }

        public FontState copy() {
            FontState copy = new FontState();
            copy.charTextures.putAll(this.charTextures);
            copy.charCroppings.putAll(this.charCroppings);
            for (var entry : this.charKerningsMap.entrySet()) {
                copy.charKerningsMap.put(entry.getKey(), new HashMap<>(entry.getValue()));
            }
            copy.charHeight = this.charHeight;
            copy.charWidth = this.charWidth;
            copy.pixelFormat = this.pixelFormat;
            return copy;
        }

        public void dispose() {
            clear();
        }

        // Debug: export character textures to files
        public void exportCharactersToFiles(String outputDirectory) {
            if (charTextures.isEmpty()) return;

            TextureRegion firstRegion = charTextures.values().iterator().next();
            Texture texture = firstRegion.getTexture();
            if (!texture.getTextureData().isPrepared()) texture.getTextureData().prepare();
            Pixmap fullPixmap = texture.getTextureData().consumePixmap();
            PixmapIO.writePNG(Gdx.files.external(outputDirectory + "/full.png"), fullPixmap);

            for (Map.Entry<Character, TextureRegion> entry : charTextures.entrySet()) {
                Character character = entry.getKey();
                TextureRegion region = entry.getValue();
                if (region != null) {
                    try {
                        Pixmap regionPixmap = new Pixmap(region.getRegionWidth(), region.getRegionHeight(), Pixmap.Format.RGBA8888);
                        regionPixmap.drawPixmap(fullPixmap, 0, 0, region.getRegionX(), region.getRegionY(), region.getRegionWidth(), region.getRegionHeight());
                        PixmapIO.writePNG(Gdx.files.external(outputDirectory + "/" + Character.toString(character).toLowerCase() + (Character.isUpperCase(character) ? "_U" : "") + ".png"), regionPixmap);
                        regionPixmap.dispose();
                    } catch (GdxRuntimeException e) {
                        System.out.println("Error processing character: " + character);
                        e.printStackTrace();
                    }
                }
            }
            fullPixmap.dispose();
        }
    }

    public FontVariable {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Variable name cannot be null or empty");
        }
        if (state == null) {
            state = new FontState();
        }
        if (signals == null) {
            signals = Map.of();
        } else {
            signals = Map.copyOf(signals);
        }
    }

    public FontVariable(String name) {
        this(name, new FontState(), Map.of());
    }

    @Override
    public Value value() {
        return NullValue.INSTANCE;
    }

    @Override
    public VariableType type() {
        return VariableType.FONT;
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
        return new FontVariable(name, state, newSignals);
    }

    @Override
    public Variable copyAs(String newName) {
        return new FontVariable(newName, state.copy(), new HashMap<>(signals));
    }

    // ========================================
    // FontLoadable IMPLEMENTATION
    // ========================================

    @Override public void clearFontData() { state.clear(); }
    @Override public void setCharHeight(int charHeight) { state.charHeight = charHeight; }
    @Override public void setCharWidth(int charWidth) { state.charWidth = charWidth; }
    @Override public void setPixelFormat(int pixelFormat) { state.pixelFormat = pixelFormat; }
    @Override public void setCharTexture(char c, TextureRegion texture) { state.setCharTexture(c, texture); }
    @Override public void setCharKerning(int i, int[] kernings) { state.setCharKerning(i, kernings); }
    @Override public void setCharKerning(int i, int j, int kerning) { state.setCharKerning(i, j, kerning); }
    @Override public void setCharCropping(char c, FontCropping cropping) { state.setCharCropping(c, cropping); }
    @Override public FontCropping getCharCropping(char c) { return state.getCharCropping(c); }
    @Override public java.util.List<Character> getCharTextureKeys() { return state.getCharTextureKeys(); }

    public TextureRegion getCharTexture(char character) {
        return state.getCharTexture(character);
    }

    public boolean hasCharacter(char character) {
        return state.charTextures.containsKey(character);
    }

    public int getCharKerning(char previous, char current) {
        return state.getCharKerning(previous, current);
    }

    public int getCharHeight() {
        return state.charHeight;
    }

    public int getCharWidth() {
        return state.charWidth;
    }

    public int getPixelFormat() {
        return state.pixelFormat;
    }

    public boolean isLoaded() {
        return state.charHeight > 0 && !state.charTextures.isEmpty();
    }

    /**
     * Piklib's getLetterWidth result, before CText6 adds its fixed 2 px spacing.
     */
    public int getLetterWidth(char previous, char current) {
        if (current == ' ') {
            return getLetterWidth('\0', 'l');
        }
        if (current == '~') {
            return 1;
        }
        if (!state.charTextures.containsKey(current)) {
            return 0;
        }

        FontCropping cropping = state.getCharCropping(current);
        int inkWidth = state.charWidth - cropping.getLeft() - cropping.getRight();
        if (!state.charTextures.containsKey(previous)) {
            return inkWidth;
        }
        return inkWidth - state.getCharKerning(previous, current);
    }

    public int getAdvance(char previous, char current) {
        return getLetterWidth(previous, current) + 2;
    }

    // ========================================
    // INITIALIZABLE
    // ========================================

    @Override
    public void init(Context context) {
        if (context.getGame() == null) {
            Gdx.app.error("FontVariable", "Cannot initialize " + name + " without a Game");
            return;
        }

        List<Map.Entry<String, String>> definitions = context.attributes()
                .getAll(name)
                .entrySet()
                .stream()
                .filter(entry -> entry.getKey().toUpperCase(Locale.ROOT).startsWith("DEF_"))
                .sorted(Comparator
                        .comparing((Map.Entry<String, String> entry) ->
                                !entry.getKey().toUpperCase(Locale.ROOT).contains("_STANDARD_"))
                        .thenComparing(Map.Entry::getKey, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());

        if (definitions.isEmpty()) {
            Gdx.app.error("FontVariable", "No DEF_* font definition for " + name);
            return;
        }

        // FontState currently represents the base face. Prefer STANDARD when a
        // collection contains style variants; inline style switching can be
        // layered on top without changing the FNT decoder or base renderer.
        loadFromDefinition(context.getGame(), definitions.get(0).getValue());
        if (definitions.size() > 1) {
            Gdx.app.debug(
                    "FontVariable",
                    name + ": loaded base face " + definitions.get(0).getKey()
                            + "; " + (definitions.size() - 1)
                            + " additional style face(s) are not selected yet"
            );
        }
    }

    /**
     * Called during attribute processing to load font data from a DEF_ attribute.
     */
    public void loadFromDefinition(pl.genschu.bloomooemulator.engine.Game game, String fontDefinition) {
        String vfsPath = pl.genschu.bloomooemulator.utils.FileUtils.resolveVfsPath(game, fontDefinition);
        try (java.io.InputStream is = game.getVfs().openRead(vfsPath)) {
            FontLoader.loadFont(this, is);
        } catch (java.io.IOException e) {
            com.badlogic.gdx.Gdx.app.error("FontVariable", "Failed to open font via VFS: " + vfsPath, e);
        }
    }

    // No script-callable methods — FontVariable is used by TextVariable for rendering
    private static final Map<String, MethodSpec> METHODS = Map.of();

    @Override
    public String toString() {
        return "FontVariable[" + name + ", chars=" + state.charTextures.size() + "]";
    }
}
