package pl.genschu.bloomooemulator.tests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import pl.genschu.bloomooemulator.TestEnvironment;
import pl.genschu.bloomooemulator.builders.ContextBuilder;
import pl.genschu.bloomooemulator.builders.MethodHelper;
import pl.genschu.bloomooemulator.geometry.coordinates.CanvasRect;
import pl.genschu.bloomooemulator.interpreter.context.Context;
import pl.genschu.bloomooemulator.interpreter.values.BoolValue;
import pl.genschu.bloomooemulator.interpreter.values.IntValue;
import pl.genschu.bloomooemulator.interpreter.values.StringValue;
import pl.genschu.bloomooemulator.interpreter.values.Value;
import pl.genschu.bloomooemulator.interpreter.variable.ArrayVariable;
import pl.genschu.bloomooemulator.interpreter.variable.ImageVariable;
import pl.genschu.bloomooemulator.interpreter.variable.PatternVariable;
import pl.genschu.bloomooemulator.objects.Image;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

/**
 * Expected values are what the real PIKLIB8 interpreter returned for the same calls
 * (Olimpiada Letnia), on a 100x100 board with a 10x10 grid and 128x98 graphics.
 */
class PatternVariableTest {
    private static final int FILE_X = 575;

    private Context context;
    private PatternVariable pattern;
    private ArrayVariable info;

    @BeforeAll
    static void boot() {
        TestEnvironment.init();
    }

    @BeforeEach
    void setUp() {
        context = new ContextBuilder().build();
        info = new ArrayVariable("T");
        context.setVariable("T", info);
        graphic("A", true);
        graphic("B", true);
        graphic("C", false);
        pattern = board("3");
    }

    private ImageVariable graphic(String name, boolean visible) {
        ImageVariable image = new ImageVariable(name);
        Image pixels = mock(Image.class);
        pixels.width = 128;
        pixels.height = 98;
        image.state().image = pixels;
        image.state().posX = FILE_X;
        image.state().updateRect();
        image.state().visible = visible;
        context.setVariable(name, image);
        return image;
    }

    private PatternVariable board(String layers) {
        PatternVariable board = new PatternVariable("P");
        context.setAttribute("P", "WIDTH", "100");
        context.setAttribute("P", "HEIGHT", "100");
        context.setAttribute("P", "GRIDX", "10");
        context.setAttribute("P", "GRIDY", "10");
        context.setAttribute("P", "LAYERS", layers);
        board.init(context);
        context.setVariable("P", board);
        return board;
    }

    private Value call(String method, Object... arguments) {
        Value[] values = new Value[arguments.length];
        for (int i = 0; i < arguments.length; i++) {
            values[i] = switch (arguments[i]) {
                case Integer number -> new IntValue(number);
                case Boolean flag -> BoolValue.of(flag);
                default -> new StringValue(arguments[i].toString());
            };
        }
        return MethodHelper.callWithContext(context, pattern, method, values);
    }

    private String text(String method, Object... arguments) {
        return call(method, arguments).toDisplayString();
    }

    private String alias(int column, int row) {
        return text("GETALIASATCELL", column, row);
    }

    private List<String> array() {
        List<String> values = new ArrayList<>();
        info.elements().forEach(value -> values.add(value.toDisplayString()));
        return values;
    }

    @Test
    void sixthArgumentOfAddIsTheLayer() {
        call("ADD", "low", 1, 2, "A", 0);
        call("ADD", "top", 1, 2, "B", 0, 1);
        call("ADD", "hid", 5, 5, "C", 7, 2);

        assertEquals("low", text("GETALIASATCELL", 1, 2));
        assertEquals("top", text("GETALIASATCELL", 1, 2, 1));
        assertEquals("hid", text("GETALIASATCELL", 5, 5, 2));
    }

    @Test
    void fifthArgumentOfAddIsNotTheLayer() {
        call("ADD", "low", 1, 2, "A", 0);
        call("ADD", "top", 1, 2, "B", 1);

        assertEquals("top", text("GETALIASATCELL", 1, 2));
        assertEquals("NULL", text("GETALIASATCELL", 1, 2, 1));
    }

    @Test
    void graphicsSearchRunsFromTheTopLayerAndAliasSearchFromTheBottom() {
        call("ADD", "low", 1, 2, "A", 0);
        call("ADD", "top", 1, 2, "B", 0, 1);

        assertEquals("B", text("GETGRAPHICSAT", 15, 25, true, true));
        assertEquals("low", text("GETALIASAT", 15, 25, true, true));
    }

    @ParameterizedTest
    @CsvSource({"0,A", "1,B", "2,B"})
    void layerArgumentLimitsTheSearchUnlessItIsTheTopLayer(int layer, String expected) {
        call("ADD", "low", 1, 2, "A", 0);
        call("ADD", "top", 1, 2, "B", 0, 1);

        assertEquals(expected, text("GETGRAPHICSAT", 15, 25, true, true, layer));
    }

    @Test
    void thirdHitTestArgumentIncludesHiddenTiles() {
        call("ADD", "low", 1, 2, "A", 0);
        call("ADD", "top", 1, 2, "B", 0, 1);
        call("ADD", "hid", 5, 5, "C", 7, 2);

        assertEquals("C", text("GETGRAPHICSAT", 55, 55, true, true));
        assertEquals("B", text("GETGRAPHICSAT", 55, 55, false, true));
        assertEquals("low", text("GETALIASAT", 55, 55, false, true));
    }

    @Test
    void missesHaveDifferentTextForGraphicsAndAliases() {
        assertEquals("UNKNOWN", text("GETGRAPHICSAT", 5, 5, true, true));
        assertEquals("NULL", text("GETALIASAT", 5, 5, true, true));
        assertEquals("NULL", text("GETALIASATCELL", 0, 0));
    }

    @Test
    void infoHoldsAliasCellSizeAndCentre() {
        call("ADD", "low", 1, 2, "A", 0);
        call("ADD", "top", 1, 2, "B", 0, 1);

        call("GETINFOAT", 15, 25, true, true, "T");
        assertEquals(List.of("low", "1", "2", "128", "98", "74", "69"), array());

        info.elements().clear();
        call("GETALIASINFO", "top", "T");
        assertEquals(List.of("top", "1", "2", "128", "98", "74", "69"), array());
    }

    @Test
    void infoIsLeftAloneOnAMiss() {
        call("GETINFOAT", 0, 0, true, true, "T");
        call("GETALIASINFO", "nothing", "T");

        assertTrue(info.elements().isEmpty());
    }

    @Test
    void moveAndSetPositionShiftTheBoardNotTheCells() {
        call("ADD", "low", 1, 2, "A", 0);

        call("MOVE", 100, 50);
        assertEquals("UNKNOWN", text("GETGRAPHICSAT", 5, 5, true, true));
        assertEquals("A", text("GETGRAPHICSAT", 115, 75, true, true));
        assertEquals("low", alias(1, 2));

        call("SETPOSITION", -5, -5);
        assertEquals("A", text("GETGRAPHICSAT", 6, 16, true, true));
    }

    @Test
    void cellCoordinatesIncludeTheFarEdgesAndAreWrittenEvenOutside() {
        call("MOVE", 100, 50);

        assertEquals("TRUE", text("GETCELLCORD", 115, 75, "T"));
        assertEquals("TRUE", text("GETCELLCORD", 200, 150, "T"));
        assertEquals("FALSE", text("GETCELLCORD", 201, 150, "T"));
        assertEquals(List.of("1", "2", "10", "10", "10", "10"), array());
    }

    @Test
    void cellCoordinatesTruncateTowardsZero() {
        assertEquals("FALSE", text("GETCELLCORD", -5, -15, "T"));
        assertEquals(List.of("0", "-1"), array());
    }

    @Test
    void setGridChangesSpacingAndKeepsTheTable() {
        call("SETGRID", 20, 5);

        assertEquals("TRUE", text("GETCELLCORD", 50, 50, "T"));
        assertEquals(List.of("2", "10"), array());

        call("ADD", "g", 3, 4, "A", 0);
        assertEquals("A", text("GETGRAPHICSAT", 65, 25, true, true));
    }

    @Test
    void addOverACellMovesTheNewTileToTheEndOfTheLayer() {
        call("ADD", "one", 1, 2, "A", 0);
        call("ADD", "two", 1, 2, "B", 0);
        call("ADD", "three", 2, 2, "C", 0);
        assertEquals("two", text("GETALIASAT", 25, 25, true, true));

        call("ADD", "one2", 1, 2, "A", 0);
        assertEquals("three", text("GETALIASAT", 25, 25, true, true));
        assertEquals("C", text("GETGRAPHICSAT", 25, 25, true, true));
    }

    @Test
    void removeTakesACellWithLayerZeroByDefault() {
        call("ADD", "low", 1, 2, "A", 0);
        call("ADD", "top", 1, 2, "B", 0, 1);

        call("REMOVE", 1, 2, 1);
        assertEquals("NULL", text("GETALIASATCELL", 1, 2, 1));
        assertEquals("A", text("GETGRAPHICSAT", 15, 25, true, true));

        call("REMOVE", 1, 2);
        assertEquals("NULL", alias(1, 2));
    }

    @Test
    void removeOfAnEmptyCellOrUnknownAliasDoesNothing() {
        call("ADD", "low", 1, 2, "A", 0);

        call("REMOVE", 9, 9);
        call("REMOVE", "nothing");

        assertEquals("low", alias(1, 2));
    }

    @ParameterizedTest
    @CsvSource({
        "AA,      ''",
        "AAA,     1",
        "AAAA,    1",
        "AAAAA,   1 3",
        "AAAAAA,  1 3",
        "AAAAAAA, 1 3 5",
        "ABAB,    1 3",
        "AABB,    1 2 3",
        "ABBA,    1 2",
        "BAAAB,   0 2 4",
        "AAAB,    1 3",
        "BAAA,    0 2",
        "ABAAB,   1 3 4",
    })
    void removeByAliasSkipsTheTileAfterEachRemovedOne(String aliases, String expected) {
        for (int i = 0; i < aliases.length(); i++) {
            call("ADD", aliases.substring(i, i + 1), i, 0, "A", 0);
        }

        call("REMOVE", "A");

        List<String> survivors = new ArrayList<>();
        for (int i = 0; i < aliases.length(); i++) {
            if (!alias(i, 0).equals("NULL")) {
                survivors.add(Integer.toString(i));
            }
        }
        assertEquals(expected, String.join(" ", survivors));
    }

    @Test
    void removeAllDoesNothing() {
        call("ADD", "low", 1, 2, "A", 0);

        call("REMOVEALL");

        assertEquals("low", alias(1, 2));
    }

    @Test
    void ignoringAlphaLeavesTheGraphicAtItsLocalCell() {
        ImageVariable image = (ImageVariable) context.getVariable("A");
        call("ADD", "low", 3, 2, "A", 0);
        assertEquals(FILE_X, image.getPosX());

        assertEquals("UNKNOWN", text("GETGRAPHICSAT", 35, 25, true, false));
        assertEquals(FILE_X, image.getPosX());

        assertEquals("low", text("GETALIASAT", 35, 25, true, true));
        assertEquals(30, image.getPosX());
    }

    @Test
    void tileBoundsFollowTheBoardAndKeepTheGraphicWhereItWas() {
        ImageVariable image = (ImageVariable) context.getVariable("A");
        call("ADD", "low", 3, 2, "A", 0);
        PatternVariable.Tile tile = pattern.state().tiles(0).get(0);

        assertEquals(CanvasRect.fromPositionAndSize(30, 20, 128, 98), pattern.tileBounds(tile));

        call("MOVE", 100, -50);
        assertEquals(CanvasRect.fromPositionAndSize(130, -30, 128, 98), pattern.tileBounds(tile));
        assertEquals(FILE_X, image.getPosX());
    }

    @Test
    void tileBoundsHonourTheGraphicAnchor() {
        ImageVariable image = (ImageVariable) context.getVariable("A");
        image.state().anchorX = 64;
        image.state().anchorY = 49;
        call("ADD", "low", 3, 2, "A", 0);

        assertEquals(CanvasRect.fromPositionAndSize(-34, -29, 128, 98),
                pattern.tileBounds(pattern.state().tiles(0).get(0)));
    }

    @ParameterizedTest
    @CsvSource({"TRUE,TRUE,true", "TRUE,FALSE,false", "FALSE,TRUE,false"})
    void boardIsDrawnOnlyWhenVisibleAndOnCanvas(String visible, String toCanvas, boolean drawn) {
        context.setAttribute("P", "VISIBLE", visible);
        context.setAttribute("P", "TOCANVAS", toCanvas);
        context.setAttribute("P", "PRIORITY", "10");

        pattern.init(context);

        assertEquals(drawn, pattern.isDrawn());
        assertEquals(10, pattern.getPriority());
    }

    @Test
    void boardIsRegisteredAsAGraphicsVariable() {
        assertSame(pattern, context.getGraphicsVariables().get("P"));
    }

    @Test
    void hitBoundsAreHalfOpen() {
        call("ADD", "a", 0, 0, "A", 0);

        assertEquals("a", text("GETALIASAT", 127, 97, true, true));
        assertEquals("NULL", text("GETALIASAT", 128, 50, true, true));
        assertEquals("NULL", text("GETALIASAT", 50, 98, true, true));
        assertEquals("NULL", text("GETALIASAT", -1, 50, true, true));
    }

    /**
     * Comes from reading CPattern::getGraphicsObject (threshold &lt; alpha) and is the one
     * rule here not seen running: the host's fake window leaves images without pixels.
     */
    @Test
    void alphaTestComparesThePixelWithTheThreshold() {
        Image pixels = new Image(2, 1, 0, 0, 16,
                new byte[]{0, 0, 0, 0}, new byte[]{0, (byte) 200}, 0);
        try {
            ImageVariable image = new ImageVariable("MASK");
            image.state().image = pixels;
            image.state().updateRect();
            context.setVariable("MASK", image);
            call("ADD", "mask", 0, 0, "MASK", 0);

            assertEquals("UNKNOWN", text("GETGRAPHICSAT", 0, 0, true, false));
            assertEquals("MASK", text("GETGRAPHICSAT", 1, 0, true, false));
            assertEquals("MASK", text("GETGRAPHICSAT", 1, 0, true, false, -1, 199));
            assertEquals("UNKNOWN", text("GETGRAPHICSAT", 1, 0, true, false, -1, 200));
            assertEquals("mask", text("GETALIASAT", 1, 0, true, false));
            assertEquals(0, image.getPosX());
        } finally {
            pixels.getImageTexture().dispose();
        }
    }

    @ParameterizedTest
    @CsvSource({"''", "0", "1"})
    void emptyOrZeroLayersMeanOneLayer(String layers) {
        pattern = board(layers);

        assertEquals(1, pattern.state().layerCount());
        call("ADD", "x", 1, 2, "A", 0);
        assertEquals("x", alias(1, 2));
        assertEquals("A", text("GETGRAPHICSAT", 15, 25, true, true));
    }

    @Test
    void outOfRangeCellsAreIgnoredWhereTheOriginalCrashes() {
        pattern = board("1");

        call("ADD", "x", 1, 2, "A", 0, 1);
        call("ADD", "x", 10, 0, "A", 0);
        call("ADD", "x", 0, -1, "A", 0);
        call("ADD", "x", 0, 0, "MISSING", 0);

        assertEquals("NULL", alias(0, 0));
        assertEquals("NULL", text("GETALIASATCELL", 99, 99, 5));
        assertTrue(pattern.state().tiles(0).isEmpty());
    }
}
