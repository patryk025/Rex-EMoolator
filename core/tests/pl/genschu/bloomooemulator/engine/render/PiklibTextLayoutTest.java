package pl.genschu.bloomooemulator.engine.render;

import org.junit.jupiter.api.Test;
import pl.genschu.bloomooemulator.geometry.shapes.Box2D;
import pl.genschu.bloomooemulator.interpreter.variable.FontVariable;
import pl.genschu.bloomooemulator.objects.FontCropping;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PiklibTextLayoutTest {
    private static final int RGB_565 = 16;

    /** Every glyph occupies a fixed 10 px cell; its ink is the cell minus the trims. */
    private static final int CELL_WIDTH = 10;
    private static final int LINE_HEIGHT = 20;

    // Piklib's getLetterWidth returns the ink width, and CText6 adds 2 px of spacing.
    private static final int ADVANCE_A = 7;         // ink 5 + 2
    private static final int ADVANCE_V = 9;         // ink 7 + 2
    private static final int ADVANCE_V_AFTER_A = 8; // kerned 1 px tighter
    private static final int ADVANCE_SPACE = 4;     // measured as 'l': ink 2 + 2
    private static final int WIDTH_OF_AV = ADVANCE_A + ADVANCE_V_AFTER_A; // 15

    private static final int WHITE = 0xFFFFFF;

    // Redraw advances consecutive lines by fontHeight - 4, while getHeight only
    // subtracts 2 px per line boundary.
    private static final int LINE_STEP = LINE_HEIGHT - 4;
    private static final int HEIGHT_OF_TWO_LINES = 2 * LINE_HEIGHT - 2;

    @Test
    void measuresInkWidthPlusFixedSpacingAndAppliesPairAdjustments() {
        FontVariable font = font();

        assertEquals(ADVANCE_A, PiklibTextLayout.measure(font, "A"));
        assertEquals(ADVANCE_V, PiklibTextLayout.measure(font, "V"));
        assertEquals(WIDTH_OF_AV, PiklibTextLayout.measure(font, "AV"));
    }

    @Test
    void measuresSpacesAsTheWidthOfTheLowercaseLGlyph() {
        // A space is not a glyph, so the 'A' after it gets no pair adjustment either.
        assertEquals(
                ADVANCE_A + ADVANCE_SPACE + ADVANCE_A,
                PiklibTextLayout.measure(font(), "A A")
        );
    }

    @Test
    void unknownCharactersContributeOnlyTheFixedSpacing() {
        assertEquals(ADVANCE_A + 2, PiklibTextLayout.measure(font(), "A?"));
    }

    @Test
    void measuresTildeAsAOnePixelHardSpace() {
        assertEquals(1 + 2, PiklibTextLayout.measure(font(), "~"));
    }

    @Test
    void wrapsAtSpacesWhenTheLineNoLongerFitsTheRect() {
        // "AV AV" measures 34 px, so it cannot fit into a 20 px wide rect.
        PiklibTextLayout.Layout layout = layout(font(), "AV AV", new Box2D(0, 0, 20, 100));

        assertEquals(List.of("AV", "AV"), textOf(layout));
        assertEquals(HEIGHT_OF_TWO_LINES, layout.height());
        assertEquals(0, layout.lines().get(0).top());
        assertEquals(LINE_STEP, layout.lines().get(1).top());
    }

    @Test
    void placesEachGlyphUsingThePairAdjustmentOfItsPredecessor() {
        PiklibTextLayout.Layout layout = layout(font(), "AV", new Box2D(0, 0, 100, 100));

        assertEquals(
                List.of(
                        new PiklibTextLayout.GlyphPlacement('A', 0, 0, WHITE),
                        // 'A' advances 7 px, then 'V' is pulled 1 px back by the kerning.
                        new PiklibTextLayout.GlyphPlacement('V', ADVANCE_A - 1, 0, WHITE)
                ),
                layout.lines().get(0).glyphs()
        );
    }

    @Test
    void centersTheTextBlockInsideTheRect() {
        // Script RECT syntax is (left, top, right, bottom) despite the Box2D accessor names.
        Box2D rect = new Box2D(10, 20, 110, 120);

        PiklibTextLayout.Line firstLine = PiklibTextLayout
                .layout(font(), "AV|AV", rect, "CENTER", "CENTER")
                .lines()
                .get(0);

        assertEquals(10 + (100 - WIDTH_OF_AV) / 2, firstLine.x());
        assertEquals(20 + (100 - HEIGHT_OF_TWO_LINES) / 2, firstLine.top());
    }

    @Test
    void alignsTheTextBlockToTheRightAndBottomEdgesOfTheRect() {
        Box2D rect = new Box2D(10, 20, 110, 120);

        PiklibTextLayout.Line firstLine = PiklibTextLayout
                .layout(font(), "AV", rect, "RIGHT", "BOTTOM")
                .lines()
                .get(0);

        assertEquals(110 - WIDTH_OF_AV, firstLine.x());
        assertEquals(120 - LINE_HEIGHT, firstLine.top());
    }

    @Test
    void breaksLinesOnPipeAndCarriageReturn() {
        assertEquals(
                List.of("A", "V", "A"),
                textOf(layout(font(), "A|V\rA", wideRect()))
        );
    }

    @Test
    void ignoresLineFeeds() {
        assertEquals(List.of("AV"), textOf(layout(font(), "A\nV", wideRect())));
    }

    @Test
    void stopsAtTheFirstNulCharacter() {
        assertEquals(List.of("AV"), textOf(layout(font(), "AV\0ignored", wideRect())));
    }

    @Test
    void dropsStyleMarkersFromTheLaidOutText() {
        assertEquals(List.of("AV"), textOf(layout(font(), "<B>A</B><I>V</I>", wideRect())));
    }

    @Test
    void appliesTheBaseColorAndInlineRgb565ColorWords() {
        PiklibTextLayout.Layout layout = PiklibTextLayout.layout(
                font(),
                "A<COLOR1151>V<COLOR63488>A",
                WHITE,
                wideRect(),
                "LEFT",
                "TOP"
        );

        assertEquals("AVA", layout.lines().get(0).text());
        assertEquals(
                List.of(
                        WHITE,
                        0x008EFF,  // 1151 = 0x047F: r5=0, g6=35, b5=31 expanded back to 8 bits
                        0xFF0000   // 63488 = 0xF800: pure red
                ),
                colorsOf(layout.lines().get(0))
        );
    }

    @Test
    void acceptsRgbTripletColorPayloadsThatTheScriptArgumentParserCannotHandle() {
        PiklibTextLayout.Layout layout = PiklibTextLayout.layout(
                font(),
                "A<COLOR,255,128,0>V",
                WHITE,
                wideRect(),
                "LEFT",
                "TOP"
        );

        // The triplet is quantized to RGB565 first, so green 128 comes back as 130.
        assertEquals(List.of(WHITE, 0xFF8200), colorsOf(layout.lines().get(0)));
    }

    private static Box2D wideRect() {
        return new Box2D(0, 0, 100, 100);
    }

    private static PiklibTextLayout.Layout layout(FontVariable font, String text, Box2D rect) {
        return PiklibTextLayout.layout(font, text, rect, "LEFT", "TOP");
    }

    private static List<String> textOf(PiklibTextLayout.Layout layout) {
        return layout.lines().stream().map(PiklibTextLayout.Line::text).toList();
    }

    private static List<Integer> colorsOf(PiklibTextLayout.Line line) {
        return line.glyphs().stream().map(PiklibTextLayout.GlyphPlacement::color).toList();
    }

    private static FontVariable font() {
        FontVariable font = new FontVariable("TEST_FONT");
        font.setCharHeight(LINE_HEIGHT);
        font.setCharWidth(CELL_WIDTH);
        font.setPixelFormat(RGB_565);

        defineGlyph(font, 'A', 2, 3); // ink 5
        defineGlyph(font, 'V', 1, 2); // ink 7
        defineGlyph(font, 'l', 4, 4); // ink 2 - Piklib measures spaces as this glyph

        setKerning(font, 'A', 'V', 1);
        return font;
    }

    /** Layout never touches the texture, so a null region is enough to declare a glyph. */
    private static void defineGlyph(FontVariable font, char character, int leftTrim, int rightTrim) {
        font.setCharTexture(character, null);
        font.setCharCropping(character, new FontCropping(leftTrim, rightTrim));
    }

    private static void setKerning(
            FontVariable font,
            char previous,
            char current,
            int adjustment
    ) {
        List<Character> glyphs = font.getCharTextureKeys();
        font.setCharKerning(glyphs.indexOf(previous), glyphs.indexOf(current), adjustment);
    }
}
