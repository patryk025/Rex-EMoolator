package pl.genschu.bloomooemulator.interpreter.variable.types;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.GdxRuntimeException;
import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.variable.Attribute;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.loader.FontLoader;
import pl.genschu.bloomooemulator.objects.FontCropping;

import java.util.*;

public class FontVariable extends Variable {
	private final Map<Character, TextureRegion> charTextures = new LinkedHashMap<>();
	private final Map<Character, FontCropping> charCroppings = new LinkedHashMap<>();
	private final Map<Character, Map<Character, Integer>> charKerningsMap = new HashMap<>();
	private int charHeight;
	private int charWidth;

	public FontVariable(String name, Context context) {
		super(name, context);
	}

	public void setCharTexture(char c, TextureRegion texture) {
		charTextures.put(c, texture);
	}

	public TextureRegion getCharTexture(char c) {
		return charTextures.get(c);
	}

	public void setCharCropping(char c, FontCropping kerning) {
		charCroppings.put(c, kerning);
	}

	public FontCropping getCharCropping(char c) {
		if(!charCroppings.containsKey(c)) {
            return new FontCropping(0, 0);
		}
		return charCroppings.get(c);
	}

	public int getCharKerning(char c, char k) {
		if(charKerningsMap.containsKey(c) && charKerningsMap.get(c).containsKey(k)) {
			return charKerningsMap.get(c).get(k);
		} else {
			return 0;
		}
	}

	public int getCharHeight() {
		return charHeight;
	}

	public void setCharHeight(int height) {
		this.charHeight = height;
	}

	public int getCharWidth() {
		return charWidth;
	}

	public void setCharWidth(int width) {
		this.charWidth = width;
	}

	public Map<Character, TextureRegion> getCharTextures() {
		return charTextures;
	}

	public Map<Character, FontCropping> getCharCroppings() {
		return charCroppings;
	}

	public List<Character> getCharTextureKeys() {
		return List.copyOf(charTextures.keySet());
	}

	@Override
	public String getType() {
		return "FONT";
	}

	@Override
	public void setAttribute(String name, Attribute attribute) {
		if(name.startsWith("DEF_")) {
			// TODO: parse DEF_[FONT]_[STYLE]_[SIZE]
			FontLoader.loadFont(this, attribute.getValue().toString());
		}
	}

	// debug
	public void exportCharactersToFiles(String outputDirectory) {
		boolean exportedTexture = false;

		if(charTextures.isEmpty())
			return;

		TextureRegion firstRegion = charTextures.values().iterator().next();
		Texture texture = firstRegion.getTexture();
		if(!texture.getTextureData().isPrepared())
			texture.getTextureData().prepare();
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

	public Map<Character, Map<Character, Integer>> getCharKerningsMap() {
		return charKerningsMap;
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
}
