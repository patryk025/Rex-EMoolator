package pl.genschu.bloomooemulator.interpreter.variable.types;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.GdxRuntimeException;
import pl.genschu.bloomooemulator.interpreter.exceptions.ClassMethodNotImplementedException;
import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.variable.Attribute;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.loader.FontLoader;
import pl.genschu.bloomooemulator.objects.FontKerning;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FontVariable extends Variable {
	private Map<Character, TextureRegion> charTextures = new HashMap<>();
	private Map<Character, FontKerning> charKernings = new HashMap<>();
	private int charHeight;
	private int charWidth;

	public FontVariable(String name, Context context) {
		super(name, context);
	}

	public void setCharTexture(char c, TextureRegion texture) {
		charTextures.put(c, texture);
	}

	public TextureRegion getCharTexture(char c) {
		return charTextures.getOrDefault(c, null);
	}

	public void setCharKerning(char c, FontKerning kerning) {
		charKernings.put(c, kerning);
	}

	public FontKerning getCharKerning(char c) {
		return charKernings.getOrDefault(c, new FontKerning(0, 0));
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

	public Map<Character, FontKerning> getCharKernings() {
		return charKernings;
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

					PixmapIO.writePNG(Gdx.files.external(outputDirectory + "/" + ((int) character) + ".png"), regionPixmap);

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
