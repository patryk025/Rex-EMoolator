package pl.genschu.bloomooemulator.loader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import pl.genschu.bloomooemulator.objects.FontCropping;
import pl.genschu.bloomooemulator.objects.Image;
import pl.genschu.bloomooemulator.loader.helpers.BinaryReader;
import pl.genschu.bloomooemulator.loader.helpers.InputStreamBinaryReader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class FontLoader {

    public static void loadFont(FontLoadable fontVariable, InputStream stream) {
        try {
            readFont(fontVariable, stream);
        } catch (IOException e) {
            Gdx.app.error("FontLoader", "Error while loading font: " + e.getMessage());
        }
    }

    private static void readFont(FontLoadable fontVariable, InputStream fileInputStream) throws IOException {
        BinaryReader reader = new InputStreamBinaryReader(fileInputStream);
        String magicId = reader.readFixedString(4, StandardCharsets.UTF_8, false);

        if (!"FNT\0".equals(magicId)) {
            throw new IOException("Invalid FNT file format");
        }

        int lineLength = reader.readI32LE();
        int charHeight = reader.readI32LE();
        int charWidth = reader.readI32LE();
        int numChars = reader.readI32LE();

        fontVariable.setCharHeight(charHeight);
        fontVariable.setCharWidth(charWidth);

        for (int i = 0; i < numChars; i++) {
            int byteValue = reader.readU8();
            String charString = new String(new byte[] {(byte) byteValue}, java.nio.charset.Charset.forName("windows-1250"));
            char character = charString.charAt(0);

            fontVariable.setCharTexture(character, null);
        }

        byte[] kerningData = reader.readBytes(Math.multiplyExact(numChars, numChars));

        for (int i = 0; i < numChars; i++) {
            fontVariable.setCharKerning(i, new int[numChars]);
            for (int j = 0; j < numChars; j++) {
                fontVariable.setCharKerning(i, j, kerningData[i * numChars + j]);
            }
        }

        for (int i = 0; i < numChars; i++) {
            int kerningLeft = reader.readU8();
            fontVariable.setCharCropping(fontVariable.getCharTextureKeys().get(i), new FontCropping(kerningLeft, 0));
        }

        for (int i = 0; i < numChars; i++) {
            int kerningRight = reader.readU8();
            FontCropping cropping = fontVariable.getCharCropping(fontVariable.getCharTextureKeys().get(i));
            cropping.setRight(kerningRight);
        }

        byte[] imageData = reader.readBytes(Math.multiplyExact(Math.multiplyExact(lineLength, charHeight), 2));

        // DEBUG: invert all colors
        for (int i = 0; i < imageData.length; i += 2) {
            imageData[i] = (byte) (0xFF - imageData[i]);
            imageData[i + 1] = (byte) (0xFF - imageData[i + 1]);
        }

        byte[] alphaData = reader.readBytes(Math.multiplyExact(lineLength, charHeight));
        // DEBUG: fill alpha data with 0xFF
        /*for (int i = 0; i < alphaData.length; i++) {
            alphaData[i] = (byte) 0xFF;
        }*/

        Image fontImage = new Image(lineLength, charHeight, 0, 0, 16, imageData, alphaData, 0);

        for (int i = 0; i < numChars; i++) {
            char character = fontVariable.getCharTextureKeys().get(i);
            TextureRegion charRegion = new TextureRegion(fontImage.getImageTexture(), i * charWidth + i * 2 + 1, 0, charWidth, charHeight);
            fontVariable.setCharTexture(character, charRegion);
        }

        //fontVariable.exportCharactersToFiles("Desktop\\debug_font");
    }
}
