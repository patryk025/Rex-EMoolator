package pl.genschu.bloomooemulator.loader;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import pl.genschu.bloomooemulator.objects.FontCropping;

import java.util.List;

/**
 * Variables loadable by {@link FontLoader}.
 */
public interface FontLoadable {
    void setCharHeight(int charHeight);
    void setCharWidth(int charWidth);
    void setCharTexture(char c, TextureRegion texture);
    void setCharKerning(int i, int[] kernings);
    void setCharKerning(int i, int j, int kerning);
    void setCharCropping(char c, FontCropping cropping);
    FontCropping getCharCropping(char c);
    List<Character> getCharTextureKeys();
}
