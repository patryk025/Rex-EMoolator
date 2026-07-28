package pl.genschu.bloomooemulator.loader;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import pl.genschu.bloomooemulator.TestEnvironment;
import pl.genschu.bloomooemulator.builders.ContextBuilder;
import pl.genschu.bloomooemulator.engine.Game;
import pl.genschu.bloomooemulator.engine.filesystem.LocalFileSystem;
import pl.genschu.bloomooemulator.interpreter.context.Context;
import pl.genschu.bloomooemulator.interpreter.variable.FontVariable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FontLoaderTest {
    private static final int RGB_555 = 15;
    private static final int RGB_565 = 16;

    @TempDir
    Path tempDir;

    @BeforeAll
    static void boot() {
        TestEnvironment.init();
    }

    @Test
    void decodesHeaderAndPerGlyphTables() throws IOException {
        FntFontData data = read(fnt()
                .atlas(8, 1)
                .glyphs('A', 'V')
                .pairAdjustments(
                        -2, 1,                          // A followed by A / V
                        Byte.MIN_VALUE, Byte.MAX_VALUE  // V followed by A / V
                )
                .leftTrims(1, 0)
                .rightTrims(1, 1)
                .build());

        assertEquals(8, data.atlasWidth());
        assertEquals(1, data.atlasHeight());
        assertEquals(RGB_565, data.pixelFormat());
        assertEquals(2, data.glyphCount());
        assertEquals(4, data.cellWidth()); // 8 px atlas split between 2 glyphs
        assertArrayEquals(new char[]{'A', 'V'}, data.characters());

        // Pair adjustments are signed bytes, so the full -128..127 range must survive.
        assertEquals(-2, data.pairAdjustment(0, 0));
        assertEquals(1, data.pairAdjustment(0, 1));
        assertEquals(-128, data.pairAdjustment(1, 0));
        assertEquals(127, data.pairAdjustment(1, 1));

        assertEquals(1, data.leftTrim(0));
        assertEquals(1, data.rightTrim(1));

        assertEquals(16, data.imageData().length); // 8 * 1 pixels, 2 bytes each
        assertEquals(8, data.alphaData().length);  // 8 * 1 pixels, 1 byte each
    }

    @ParameterizedTest
    @ValueSource(ints = {RGB_555, RGB_565})
    void acceptsBothSupportedPixelFormats(int pixelFormat) throws IOException {
        assertEquals(pixelFormat, read(fnt().pixelFormat(pixelFormat).build()).pixelFormat());
    }

    @Test
    void colorizationRecolorsACopyAndKeepsTheDecodedAtlasBlack() throws IOException {
        // A single-pixel-wide glyph cell; the builder zero-fills both pixel planes.
        FntFontData data = read(fnt().atlas(2, 1).glyphs('A').build());

        byte[] decodedAtlas = data.imageData();
        byte[] recolored = FontLoader.createSolidColorImageData(data, FontLoader.DEFAULT_FONT_COLOR);

        assertArrayEquals(new byte[]{0, 0, 0, 0}, decodedAtlas);
        assertArrayEquals(
                new byte[]{(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF},
                recolored
        );
        assertArrayEquals(decodedAtlas, data.imageData());
    }

    @Test
    void rejectsForeignMagic() {
        byte[] file = fnt().build();
        file[0] = 'X';

        assertEquals("Invalid FNT file format", failureFor(file).getMessage());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 8, 24, 32})
    void rejectsUnsupportedPixelFormat(int pixelFormat) {
        byte[] file = fnt().pixelFormat(pixelFormat).build();

        assertEquals(
                "Unsupported FNT pixel format: " + pixelFormat,
                failureFor(file).getMessage()
        );
    }

    @Test
    void rejectsAtlasWidthThatCannotBeSplitIntoEqualCells() {
        byte[] file = fnt().atlas(7, 1).glyphs('A', 'V').build();

        assertEquals(
                "FNT atlas width 7 is not divisible by glyph count 2",
                failureFor(file).getMessage()
        );
    }

    @Test
    void rejectsTrimsWiderThanTheGlyphCell() {
        // Cell is 8 / 2 = 4 px wide, but the first glyph trims 3 + 2 px away.
        byte[] file = fnt().atlas(8, 1).glyphs('A', 'V')
                .leftTrims(3, 0)
                .rightTrims(2, 0)
                .build();

        assertEquals(
                "Invalid FNT trims for glyph 0: 3 + 2 > 4",
                failureFor(file).getMessage()
        );
    }

    @Test
    void rejectsDuplicateCharacters() {
        byte[] file = fnt().glyphs('A', 'A').build();

        assertEquals(
                "Duplicate FNT character at glyph 1: A",
                failureFor(file).getMessage()
        );
    }

    @Test
    void rejectsTrailingDataAfterTheAlphaPlane() {
        byte[] file = fnt().build();
        byte[] withExtraByte = Arrays.copyOf(file, file.length + 1);

        assertEquals(
                "Unexpected trailing data after FNT alpha plane",
                failureFor(withExtraByte).getMessage()
        );
    }

    @Test
    void rejectsTruncatedPixelPlanes() {
        byte[] file = fnt().build();
        byte[] truncated = Arrays.copyOf(file, file.length - 1);

        assertThrows(
                EOFException.class,
                () -> FontLoader.readFontData(new ByteArrayInputStream(truncated))
        );
    }

    @Test
    void cnvInitializationLoadsTheBaseDefFontThroughVfs() throws IOException {
        Path common = Files.createDirectories(tempDir.resolve("COMMON"));
        Files.write(common.resolve("TEST.FNT"), fnt()
                .atlas(8, 1)
                .glyphs('A', 'V')
                .pairAdjustments(
                        0, 1,   // A followed by A / V
                        -2, 0   // V followed by A / V
                )
                .leftTrims(1, 0)
                .rightTrims(1, 1)
                .build());

        Game game = new Game(null, null);
        game.getVfs().mountAssets(new LocalFileSystem(tempDir.toFile()));
        Context context = new ContextBuilder().build();
        context.setGame(game);

        String script = """
                OBJECT=TESTFONT
                TESTFONT:TYPE=FONT
                TESTFONT:DEF_ARIAL_STANDARD_14=$COMMON\\TEST.FNT
                """;
        new CNVParser().parse(
                new ByteArrayInputStream(script.getBytes(StandardCharsets.UTF_8)),
                "font-test.cnv",
                context
        );

        FontVariable font = (FontVariable) context.getVariable("TESTFONT");
        assertTrue(font.isLoaded());
        assertEquals(4, font.getCharWidth());
        assertEquals(1, font.getCharHeight());
        assertEquals(RGB_565, font.getPixelFormat());
        assertEquals(1, font.getCharKerning('A', 'V'));
        // 'A' occupies cell 0 minus its 1 px left and 1 px right trim.
        assertEquals(1, font.getCharTexture('A').getRegionX());
        assertEquals(2, font.getCharTexture('A').getRegionWidth());
    }

    private static FntFontData read(byte[] file) throws IOException {
        return FontLoader.readFontData(new ByteArrayInputStream(file));
    }

    private static IOException failureFor(byte[] file) {
        return assertThrows(
                IOException.class,
                () -> FontLoader.readFontData(new ByteArrayInputStream(file))
        );
    }

    private static FntBuilder fnt() {
        return new FntBuilder();
    }

    /**
     * Builds a minimal, well-formed FNT file. Defaults describe an 8x1 atlas holding
     * the two glyphs {@code A} and {@code V} with no kerning and no trims; each test
     * overrides only the part it is about.
     */
    private static final class FntBuilder {
        private static final byte[] MAGIC = {'F', 'N', 'T', 0};

        private int atlasWidth = 8;
        private int atlasHeight = 1;
        private int pixelFormat = RGB_565;
        private char[] glyphs = {'A', 'V'};
        private int[] pairAdjustments;
        private int[] leftTrims;
        private int[] rightTrims;

        FntBuilder atlas(int width, int height) {
            this.atlasWidth = width;
            this.atlasHeight = height;
            return this;
        }

        FntBuilder pixelFormat(int pixelFormat) {
            this.pixelFormat = pixelFormat;
            return this;
        }

        FntBuilder glyphs(char... glyphs) {
            this.glyphs = glyphs;
            return this;
        }

        /** Row-major {@code glyphCount x glyphCount} table indexed by {@code [previous][current]}. */
        FntBuilder pairAdjustments(int... pairAdjustments) {
            this.pairAdjustments = pairAdjustments;
            return this;
        }

        FntBuilder leftTrims(int... leftTrims) {
            this.leftTrims = leftTrims;
            return this;
        }

        FntBuilder rightTrims(int... rightTrims) {
            this.rightTrims = rightTrims;
            return this;
        }

        byte[] build() {
            int glyphCount = glyphs.length;
            int pixelCount = atlasWidth * atlasHeight;

            ByteArrayOutputStream file = new ByteArrayOutputStream();
            file.writeBytes(MAGIC);
            file.writeBytes(int32LE(atlasWidth));
            file.writeBytes(int32LE(atlasHeight));
            file.writeBytes(int32LE(pixelFormat));
            file.writeBytes(int32LE(glyphCount));
            file.writeBytes(toBytes(glyphs));
            file.writeBytes(toBytes(pairAdjustments, glyphCount * glyphCount));
            file.writeBytes(toBytes(leftTrims, glyphCount));
            file.writeBytes(toBytes(rightTrims, glyphCount));
            file.writeBytes(new byte[pixelCount * 2]); // colour plane
            file.writeBytes(new byte[pixelCount]);     // alpha plane
            return file.toByteArray();
        }

        private static byte[] int32LE(int value) {
            return ByteBuffer.allocate(Integer.BYTES)
                    .order(ByteOrder.LITTLE_ENDIAN)
                    .putInt(value)
                    .array();
        }

        private static byte[] toBytes(char[] characters) {
            byte[] bytes = new byte[characters.length];
            for (int i = 0; i < characters.length; i++) {
                bytes[i] = (byte) characters[i];
            }
            return bytes;
        }

        private static byte[] toBytes(int[] values, int expectedLength) {
            if (values == null) {
                return new byte[expectedLength];
            }
            if (values.length != expectedLength) {
                throw new IllegalArgumentException(
                        "Expected " + expectedLength + " values but got " + values.length
                );
            }
            byte[] bytes = new byte[expectedLength];
            for (int i = 0; i < expectedLength; i++) {
                bytes[i] = (byte) values[i];
            }
            return bytes;
        }
    }
}
