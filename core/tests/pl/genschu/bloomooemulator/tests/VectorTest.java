package pl.genschu.bloomooemulator.tests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.Arguments;

import pl.genschu.bloomooemulator.TestEnvironment;
import pl.genschu.bloomooemulator.builders.ContextBuilder;
import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.variable.Attribute;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.interpreter.variable.types.*;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class VectorTest {

    private Context ctx;
    private VectorVariable vector;

    @BeforeAll
    static void boot() { TestEnvironment.init(); }

    @BeforeEach
    void setUp() {
        ctx    = new ContextBuilder().build();
        vector = new VectorVariable("TEST_VECTOR", ctx);
        vector.setAttribute("SIZE", new Attribute("INTEGER", 2));
    }

    private static Stream<Arguments> assignProvider() {
        return Stream.of(
                Arguments.of(  3.0,   4.0),
                Arguments.of(  0.0,   0.0),
                Arguments.of( -7.5,  12.25),
                Arguments.of(1e9,  -1e9)
        );
    }

    private static Stream<Arguments> addProvider() {
        return Stream.of(
                //   x1   y1    x2    y2
                Arguments.of(  2.0,  2.0,  1.0,  -3.0),
                Arguments.of( -5.0,  0.0,  5.0,   5.0),
                Arguments.of(  0.1, -0.1, -0.1,   0.1)
        );
    }

    private static Stream<Arguments> mulProvider() {
        return Stream.of(
                //    x     y     scalar
                Arguments.of( -1.5,  4.0,   2.0),
                Arguments.of(  3.0, -3.0,  -1.0),
                Arguments.of(  0.5,  0.5,   0.0)
        );
    }

    private static Stream<Arguments> lenProvider() {
        return Stream.of(
                Arguments.of( 0.0, 0.0, 0.0),
                Arguments.of( 3.0, 4.0, 5.0),
                Arguments.of( 1.0, 1.0, Math.sqrt(2)),
                Arguments.of( 1e6, 0.0, 1e6)
        );
    }

    private static Stream<Arguments> normalizeProvider() {
        return Stream.of(
        //                                x    y  expectedRx  expectedRy
                Arguments.of( 0.0, 5.0, 0.0, 1.0),        // wektor osi Y
                Arguments.of( 3.0, 4.0, 0.6, 0.8),        // klasyczne (3,4)
                Arguments.of( 1e-3, -1e-3, 0.70711, -0.70711)      // bardzo mały wektor skośny
        );
    }

    private static Stream<Arguments> reflectProvider() {
        // reversed results... okey...
        return Stream.of(
            //                           vx    vy    nx    ny  expectedRx  expectedRy
                Arguments.of( 1.0, -1.0,  0.0,  1.0,   -1.0,  -1.0),   // odbicie od osi X (normal (0,1))
                Arguments.of( 1.0,  2.0,  1.0,  0.0,  1.0,  -2.0),   // odbicie od osi Y (normal (1,0))
                Arguments.of( 2.0,  2.0,  1/Math.sqrt(2), 1/Math.sqrt(2),  2.0, 2.0) // normal (45°)
        );
    }

    @ParameterizedTest(name = "assign({0}, {1}) → get" )
    @MethodSource("assignProvider")
    void testAssignAndGet(double x, double y) {
        vector.fireMethod("ASSIGN", new DoubleVariable("", x, ctx), new DoubleVariable("", y, ctx));
        assertEquals(x, ((DoubleVariable) vector.fireMethod("GET", new IntegerVariable("", 0, ctx))).GET());
        assertEquals(y, ((DoubleVariable) vector.fireMethod("GET", new IntegerVariable("", 1, ctx))).GET());
    }

    @ParameterizedTest(name = "({0}, {1}) + ({2}, {3})")
    @MethodSource("addProvider")
    void testAdd(double x1, double y1, double x2, double y2) {
        vector.fireMethod("ASSIGN", new DoubleVariable("", x1, ctx), new DoubleVariable("", y1, ctx));
        VectorVariable other = new VectorVariable("OTHER", ctx);
        other.fireMethod("ASSIGN", new DoubleVariable("", x2, ctx), new DoubleVariable("", y2, ctx));

        vector.fireMethod("ADD", other);

        assertEquals(x1 + x2, ((DoubleVariable) vector.fireMethod("GET", new IntegerVariable("", 0, ctx))).GET());
        assertEquals(y1 + y2, ((DoubleVariable) vector.fireMethod("GET", new IntegerVariable("", 1, ctx))).GET());
    }

    @ParameterizedTest(name = "({0}, {1}) * {2}")
    @MethodSource("mulProvider")
    void testMul(double x, double y, double scalar) {
        vector.fireMethod("ASSIGN", new DoubleVariable("", x, ctx), new DoubleVariable("", y, ctx));
        vector.fireMethod("MUL", new DoubleVariable("", scalar, ctx));

        assertEquals(x * scalar, ((DoubleVariable) vector.fireMethod("GET", new IntegerVariable("", 0, ctx))).GET());
        assertEquals(y * scalar, ((DoubleVariable) vector.fireMethod("GET", new IntegerVariable("", 1, ctx))).GET());
    }

    @ParameterizedTest(name = "len(({0}, {1})) ≈ {2}")
    @MethodSource("lenProvider")
    void testLen(double x, double y, double expected) {
        vector.fireMethod("ASSIGN", new DoubleVariable("", x, ctx), new DoubleVariable("", y, ctx));
        double len = ((DoubleVariable) vector.fireMethod("LEN")).GET();
        assertEquals(expected, len, 1e-5);
    }

    @ParameterizedTest(name = "normalize({0}, {1}) → ({2}, {3})")
    @MethodSource("normalizeProvider")
    void testNormalize(double x, double y, double expectedRx, double expectedRy) {
        vector.fireMethod("ASSIGN", new DoubleVariable("", x, ctx), new DoubleVariable("", y, ctx));
        vector.fireMethod("NORMALIZE");

        double len = ((DoubleVariable) vector.fireMethod("LEN")).GET();
        assertEquals(1.0, len, 1e-5);
        assertEquals(expectedRx, ((DoubleVariable) vector.fireMethod("GET", new IntegerVariable("", 0, ctx))).GET(), 1e-5);
        assertEquals(expectedRy, ((DoubleVariable) vector.fireMethod("GET", new IntegerVariable("", 1, ctx))).GET(), 1e-5);
    }

    @ParameterizedTest(name = "reflect(({0}, {1})) by ({2}, {3}) → ({4}, {5})")
    @MethodSource("reflectProvider")
    void testReflect(double vx, double vy, double nx, double ny, double expectedRx, double expectedRy) {
        vector.fireMethod("ASSIGN", new DoubleVariable("", vx, ctx), new DoubleVariable("", vy, ctx));

        VectorVariable normal = new VectorVariable("N", ctx);
        normal.setAttribute("SIZE", new Attribute("INTEGER", 2));
        normal.fireMethod("ASSIGN", new DoubleVariable("", nx, ctx), new DoubleVariable("", ny, ctx));

        VectorVariable result = new VectorVariable("R", ctx);
        result.setAttribute("SIZE", new Attribute("INTEGER", 2));
        vector.fireMethod("REFLECT", normal, result);

        assertEquals(expectedRx, ((DoubleVariable) result.fireMethod("GET", new IntegerVariable("", 0, ctx))).GET(), 1e-5);
        assertEquals(expectedRy, ((DoubleVariable) result.fireMethod("GET", new IntegerVariable("", 1, ctx))).GET(), 1e-5);
    }
}
