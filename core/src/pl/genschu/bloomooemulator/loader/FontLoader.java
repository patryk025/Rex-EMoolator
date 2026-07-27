package pl.genschu.bloomooemulator.loader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import pl.genschu.bloomooemulator.objects.FontCropping;
import pl.genschu.bloomooemulator.objects.Image;
import pl.genschu.bloomooemulator.loader.helpers.BinaryReader;
import pl.genschu.bloomooemulator.loader.helpers.InputStreamBinaryReader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

public class FontLoader {
    private static final Charset DEFAULT_CHARACTER_SET = Charset.forName("windows-1250");
    static final int DEFAULT_FONT_COLOR = 0xFFFF;

    public static void loadFont(FontLoadable fontVariable, InputStream stream) {
        try {
            readFont(fontVariable, stream);
        } catch (IOException e) {
            Gdx.app.error("FontLoader", "Error while loading font: " + e.getMessage());
        }
    }

    private static void readFont(FontLoadable fontVariable, InputStream fileInputStream) throws IOException {
        FntFontData data = readFontData(fileInputStream);

        fontVariable.clearFontData();
        fontVariable.setCharHeight(data.atlasHeight());
        fontVariable.setCharWidth(data.cellWidth());
        fontVariable.setPixelFormat(data.pixelFormat());

        for (char character : data.characters()) {
            fontVariable.setCharTexture(character, null);
        }

        int glyphCount = data.glyphCount();
        for (int previous = 0; previous < glyphCount; previous++) {
            fontVariable.setCharKerning(previous, new int[glyphCount]);
            for (int current = 0; current < glyphCount; current++) {
                fontVariable.setCharKerning(
                        previous,
                        current,
                        data.pairAdjustment(previous, current)
                );
            }
        }

        for (int i = 0; i < glyphCount; i++) {
            char character = data.character(i);
            fontVariable.setCharCropping(
                    character,
                    new FontCropping(data.leftTrim(i), data.rightTrim(i))
            );
        }

        Image fontImage = new Image(
                data.atlasWidth(),
                data.atlasHeight(),
                0,
                0,
                data.pixelFormat(),
                createSolidColorImageData(data, DEFAULT_FONT_COLOR),
                data.alphaData(),
                0
        );

        for (int i = 0; i < glyphCount; i++) {
            char character = data.character(i);
            int regionX = i * data.cellWidth() + data.leftTrim(i);
            int regionWidth = data.cellWidth() - data.leftTrim(i) - data.rightTrim(i);
            TextureRegion charRegion = new TextureRegion(
                    fontImage.getImageTexture(),
                    regionX,
                    0,
                    regionWidth,
                    data.atlasHeight()
            );
            fontVariable.setCharTexture(character, charRegion);
        }
    }

    /**
     * Overrides color: the glyph alpha mask is retained,
     * while every 16-bit pixel in the colour plane receives the selected color.
     *
     * <p>Piklib fonts and text objects default to {@code 0xFFFF} (white).
     * arial14.fnt, store black RGB glyph pixels and
     * rely on this runtime recolouring step.</p>
     */
    static byte[] createSolidColorImageData(FntFontData data, int color) {
        byte[] colorData = data.imageData();
        byte low = (byte) color;
        byte high = (byte) (color >>> 8);

        for (int offset = 0; offset < colorData.length; offset += 2) {
            colorData[offset] = low;
            colorData[offset + 1] = high;
        }
        return colorData;
    }

    /**
     * Parses an FNT stream without creating any OpenGL resources.
     */
    public static FntFontData readFontData(InputStream fileInputStream) throws IOException {
        BinaryReader reader = new InputStreamBinaryReader(fileInputStream);
        String magicId = reader.readFixedString(4, StandardCharsets.UTF_8, false);

        if (!"FNT\0".equals(magicId)) {
            throw new IOException("Invalid FNT file format");
        }

        int atlasWidth = reader.readI32LE();
        int atlasHeight = reader.readI32LE();
        int pixelFormat = reader.readI32LE();
        int glyphCount = reader.readI32LE();

        if (atlasWidth <= 0 || atlasHeight <= 0) {
            throw new IOException("Invalid FNT atlas dimensions: " + atlasWidth + "x" + atlasHeight);
        }
        if (pixelFormat != 15 && pixelFormat != 16) {
            throw new IOException("Unsupported FNT pixel format: " + pixelFormat);
        }
        if (glyphCount <= 0) {
            throw new IOException("Invalid FNT glyph count: " + glyphCount);
        }
        if (atlasWidth % glyphCount != 0) {
            throw new IOException(
                    "FNT atlas width " + atlasWidth
                            + " is not divisible by glyph count " + glyphCount
            );
        }

        int pairCount;
        int pixelCount;
        int imageByteCount;
        try {
            pairCount = Math.multiplyExact(glyphCount, glyphCount);
            pixelCount = Math.multiplyExact(atlasWidth, atlasHeight);
            imageByteCount = Math.multiplyExact(pixelCount, 2);
        } catch (ArithmeticException e) {
            throw new IOException("FNT dimensions overflow supported array sizes", e);
        }

        byte[] characterBytes = reader.readBytes(glyphCount);
        char[] characters = new char[glyphCount];
        Set<Character> uniqueCharacters = new HashSet<>();
        for (int i = 0; i < glyphCount; i++) {
            characters[i] = new String(
                    new byte[]{characterBytes[i]},
                    DEFAULT_CHARACTER_SET
            ).charAt(0);
            if (!uniqueCharacters.add(characters[i])) {
                throw new IOException(
                        "Duplicate FNT character at glyph " + i + ": " + characters[i]
                );
            }
        }

        byte[] pairAdjustments = reader.readBytes(pairCount);
        byte[] leftTrims = reader.readBytes(glyphCount);
        byte[] rightTrims = reader.readBytes(glyphCount);

        int cellWidth = atlasWidth / glyphCount;
        for (int i = 0; i < glyphCount; i++) {
            int left = leftTrims[i] & 0xFF;
            int right = rightTrims[i] & 0xFF;
            if (left + right > cellWidth) {
                throw new IOException(
                        "Invalid FNT trims for glyph " + i + ": "
                                + left + " + " + right + " > " + cellWidth
                );
            }
        }

        byte[] imageData = reader.readBytes(imageByteCount);
        byte[] alphaData = reader.readBytes(pixelCount);
        if (fileInputStream.read() != -1) {
            throw new IOException("Unexpected trailing data after FNT alpha plane");
        }

        return new FntFontData(
                atlasWidth,
                atlasHeight,
                pixelFormat,
                characters,
                pairAdjustments,
                leftTrims,
                rightTrims,
                imageData,
                alphaData
        );
    }
}
