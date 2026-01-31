package pl.genschu.bloomooemulator.tests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.Arguments;

import pl.genschu.bloomooemulator.TestEnvironment;
import pl.genschu.bloomooemulator.builders.ContextBuilder;
import pl.genschu.bloomooemulator.interpreter.context.Context;
import pl.genschu.bloomooemulator.interpreter.values.DoubleValue;
import pl.genschu.bloomooemulator.interpreter.values.IntValue;
import pl.genschu.bloomooemulator.interpreter.values.VariableValue;
import pl.genschu.bloomooemulator.interpreter.variable.VectorVariable;

import java.util.List;
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
        vector = (VectorVariable) new VectorVariable("TEST_VECTOR").withSize(2);
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
        vector = (VectorVariable) vector.callMethod("ASSIGN", List.of(new DoubleValue(x), new DoubleValue(y))).newSelf();
        assertEquals(x, vector.callMethod("GET", new IntValue(0)).getReturnValue().toDouble().value());
        assertEquals(y, vector.callMethod("GET", new IntValue(1)).getReturnValue().toDouble().value());
    }

    @ParameterizedTest(name = "({0}, {1}) + ({2}, {3})")
    @MethodSource("addProvider")
    void testAdd(double x1, double y1, double x2, double y2) {
        vector = (VectorVariable) vector.callMethod("ASSIGN", new DoubleValue(x1), new DoubleValue(y1)).newSelf();
        VectorVariable other = new VectorVariable("OTHER");
        other.callMethod("ASSIGN", new DoubleValue(x2), new DoubleValue(y2));

        vector = (VectorVariable) vector.callMethod("ADD", new VariableValue(other)).newSelf();

        assertEquals(x1 + x2, vector.callMethod("GET", new IntValue(0)).getReturnValue().toDouble().value());
        assertEquals(y1 + y2, vector.callMethod("GET", new IntValue(1)).getReturnValue().toDouble().value());
    }

    @ParameterizedTest(name = "({0}, {1}) * {2}")
    @MethodSource("mulProvider")
    void testMul(double x, double y, double scalar) {
        vector = (VectorVariable) vector.callMethod("ASSIGN", new DoubleValue(x), new DoubleValue(y)).newSelf();
        vector = (VectorVariable) vector.callMethod("MUL", new DoubleValue(scalar)).newSelf();

        assertEquals(x * scalar, vector.callMethod("GET", new IntValue(0)).getReturnValue().toDouble().value());
        assertEquals(y * scalar, vector.callMethod("GET", new IntValue(1)).getReturnValue().toDouble().value());
    }

    @ParameterizedTest(name = "len(({0}, {1})) ≈ {2}")
    @MethodSource("lenProvider")
    void testLen(double x, double y, double expected) {
        vector = (VectorVariable) vector.callMethod("ASSIGN", new DoubleValue(x), new DoubleValue(y)).newSelf();
        double len = vector.callMethod("LEN").getReturnValue().toDouble().value();
        assertEquals(expected, len, 1e-5);
    }

    @ParameterizedTest(name = "normalize({0}, {1}) → ({2}, {3})")
    @MethodSource("normalizeProvider")
    void testNormalize(double x, double y, double expectedRx, double expectedRy) {
        vector = (VectorVariable) vector.callMethod("ASSIGN", new DoubleValue(x), new DoubleValue(y)).newSelf();
        vector = (VectorVariable) vector.callMethod("NORMALIZE").newSelf();

        double len = vector.callMethod("LEN").getReturnValue().toDouble().value();
        assertEquals(1.0, len, 1e-5);
        assertEquals(expectedRx, vector.callMethod("GET", new IntValue(0)).getReturnValue().toDouble().value(), 1e-5);
        assertEquals(expectedRy, vector.callMethod("GET", new IntValue(1)).getReturnValue().toDouble().value(), 1e-5);
    }

    @ParameterizedTest(name = "reflect(({0}, {1})) by ({2}, {3}) → ({4}, {5})")
    @MethodSource("reflectProvider")
    void testReflect(double vx, double vy, double nx, double ny, double expectedRx, double expectedRy) {
        vector = (VectorVariable) vector.callMethod("ASSIGN", new DoubleValue(vx), new DoubleValue(vy)).newSelf();

        VectorVariable normal = (VectorVariable) new VectorVariable("N").withSize(2);
        normal = (VectorVariable) normal.callMethod("ASSIGN", new DoubleValue(nx), new DoubleValue(ny)).newSelf();

        VectorVariable result = (VectorVariable) new VectorVariable("R").withSize(2);
        vector = (VectorVariable) vector.callMethod("REFLECT", new VariableValue(normal), new VariableValue(result)).newSelf();

        assertEquals(expectedRx, result.callMethod("GET", new IntValue(0)).getReturnValue().toDouble().value(), 1e-5);
        assertEquals(expectedRy, result.callMethod("GET", new IntValue(1)).getReturnValue().toDouble().value(), 1e-5);
    }
}
