package pl.genschu.bloomooemulator.loader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import pl.genschu.bloomooemulator.interpreter.variable.types.FontVariable;
import pl.genschu.bloomooemulator.objects.FontKerning;
import pl.genschu.bloomooemulator.objects.Image;
import pl.genschu.bloomooemulator.utils.FileUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

public class FontLoader {

    public static void loadFont(FontVariable fontVariable, String filePath) {
        filePath = FileUtils.resolveRelativePath(fontVariable, filePath);

        try (FileInputStream fileInputStream = new FileInputStream(filePath)) {
            readFont(fontVariable, fileInputStream);
        } catch (IOException e) {
            Gdx.app.error("FontLoader", "Error while loading font: " + e.getMessage());
        }
    }

    private static void readFont(FontVariable fontVariable, FileInputStream fileInputStream) throws IOException {
        byte[] headerBuffer = new byte[4];
        fileInputStream.read(headerBuffer);
        String magicId = new String(headerBuffer, StandardCharsets.UTF_8);

        if (!"FNT\0".equals(magicId)) {
            throw new IOException("Invalid FNT file format");
        }

        ByteBuffer buffer = ByteBuffer.allocate(16).order(ByteOrder.LITTLE_ENDIAN);
        fileInputStream.read(buffer.array());
        int lineLength = buffer.getInt();
        int charHeight = buffer.getInt();
        int charWidth = buffer.getInt();
        int numChars = buffer.getInt();

        fontVariable.setCharHeight(charHeight);
        fontVariable.setCharWidth(charWidth);

        for (int i = 0; i < numChars; i++) {
            int byteValue = fileInputStream.read();

            String charString = new String(new byte[] {(byte) byteValue}, "CP1250");
            char character = charString.charAt(0);

            fontVariable.setCharTexture(character, null);
        }

        // Skip weird matrix
        fileInputStream.skip(numChars * numChars);

        for (int i = 0; i < numChars; i++) {
            int kerningLeft = fileInputStream.read();
            int kerningRight = fileInputStream.read();
            char character = fontVariable.getCharTextureKeys().get(i);
            fontVariable.setCharKerning(character, new FontKerning(kerningLeft, kerningRight));
        }

        byte[] imageData = new byte[lineLength * charHeight * 2];
        fileInputStream.read(imageData);

        // DEBUG: invert all colors
        for (int i = 0; i < imageData.length; i += 2) {
            imageData[i] = (byte) (0xFF - imageData[i]);
            imageData[i + 1] = (byte) (0xFF - imageData[i + 1]);
        }

        byte[] alphaData = new byte[lineLength * charHeight];
        fileInputStream.read(alphaData);
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
