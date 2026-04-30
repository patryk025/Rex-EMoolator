package pl.genschu.bloomooemulator.interpreter.variable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import pl.genschu.bloomooemulator.annotations.InternalMutable;
import pl.genschu.bloomooemulator.interpreter.values.*;
import pl.genschu.bloomooemulator.loader.FontLoadable;
import pl.genschu.bloomooemulator.loader.FontLoader;
import pl.genschu.bloomooemulator.objects.FontCropping;

import java.util.*;

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
) implements Variable, FontLoadable {

    public static final class FontState {
        public final Map<Character, TextureRegion> charTextures = new LinkedHashMap<>();
        public final Map<Character, FontCropping> charCroppings = new LinkedHashMap<>();
        public final Map<Character, Map<Character, Integer>> charKerningsMap = new HashMap<>();
        public int charHeight;
        public int charWidth;

        public FontState() {}

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
            return copy;
        }

        public void dispose() {
            charTextures.clear();
            charCroppings.clear();
            charKerningsMap.clear();
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

    @Override public void setCharHeight(int charHeight) { state.charHeight = charHeight; }
    @Override public void setCharWidth(int charWidth) { state.charWidth = charWidth; }
    @Override public void setCharTexture(char c, TextureRegion texture) { state.setCharTexture(c, texture); }
    @Override public void setCharKerning(int i, int[] kernings) { state.setCharKerning(i, kernings); }
    @Override public void setCharKerning(int i, int j, int kerning) { state.setCharKerning(i, j, kerning); }
    @Override public void setCharCropping(char c, FontCropping cropping) { state.setCharCropping(c, cropping); }
    @Override public FontCropping getCharCropping(char c) { return state.getCharCropping(c); }
    @Override public java.util.List<Character> getCharTextureKeys() { return state.getCharTextureKeys(); }

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
