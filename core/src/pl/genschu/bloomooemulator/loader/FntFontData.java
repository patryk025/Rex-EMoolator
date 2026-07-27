package pl.genschu.bloomooemulator.loader;

import java.util.Arrays;

/**
 * Decoded, renderer-independent contents of a Piklib FNT file.
 */
public final class FntFontData {
    private final int atlasWidth;
    private final int atlasHeight;
    private final int pixelFormat;
    private final char[] characters;
    private final byte[] pairAdjustments;
    private final byte[] leftTrims;
    private final byte[] rightTrims;
    private final byte[] imageData;
    private final byte[] alphaData;

    FntFontData(
            int atlasWidth,
            int atlasHeight,
            int pixelFormat,
            char[] characters,
            byte[] pairAdjustments,
            byte[] leftTrims,
            byte[] rightTrims,
            byte[] imageData,
            byte[] alphaData
    ) {
        this.atlasWidth = atlasWidth;
        this.atlasHeight = atlasHeight;
        this.pixelFormat = pixelFormat;
        this.characters = characters.clone();
        this.pairAdjustments = pairAdjustments.clone();
        this.leftTrims = leftTrims.clone();
        this.rightTrims = rightTrims.clone();
        this.imageData = imageData.clone();
        this.alphaData = alphaData.clone();
    }

    public int atlasWidth() {
        return atlasWidth;
    }

    public int atlasHeight() {
        return atlasHeight;
    }

    public int pixelFormat() {
        return pixelFormat;
    }

    public int glyphCount() {
        return characters.length;
    }

    public int cellWidth() {
        return atlasWidth / glyphCount();
    }

    public char character(int index) {
        return characters[index];
    }

    public char[] characters() {
        return characters.clone();
    }

    public int pairAdjustment(int previousIndex, int currentIndex) {
        return pairAdjustments[previousIndex * glyphCount() + currentIndex];
    }

    public byte[] pairAdjustments() {
        return pairAdjustments.clone();
    }

    public int leftTrim(int index) {
        return leftTrims[index] & 0xFF;
    }

    public byte[] leftTrims() {
        return leftTrims.clone();
    }

    public int rightTrim(int index) {
        return rightTrims[index] & 0xFF;
    }

    public byte[] rightTrims() {
        return rightTrims.clone();
    }

    public byte[] imageData() {
        return imageData.clone();
    }

    public byte[] alphaData() {
        return alphaData.clone();
    }

    @Override
    public String toString() {
        return "FntFontData[" + atlasWidth + "x" + atlasHeight
                + ", pixelFormat=" + pixelFormat
                + ", glyphs=" + Arrays.toString(characters) + "]";
    }
}
