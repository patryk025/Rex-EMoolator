package pl.genschu.bloomooemulator.tests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.genschu.bloomooemulator.TestEnvironment;
import pl.genschu.bloomooemulator.builders.ContextBuilder;
import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.interpreter.variable.types.*;

import static org.junit.jupiter.api.Assertions.*;

class ArrayTest {
    private Context ctx;
    private ArrayVariable arrayVar;

    @BeforeAll
    static void boot() {
        TestEnvironment.init();
    }

    @BeforeEach
    void setUp() {
        ctx = new ContextBuilder().build();
        arrayVar = new ArrayVariable("TEST_ARRAY", ctx);
    }

    @Test
    void testAdd() {
        arrayVar.fireMethod("ADD",
                new IntegerVariable("", 1, ctx),
                new DoubleVariable("", 2.5, ctx),
                new StringVariable("", "text", ctx),
                new BoolVariable("", true, ctx));

        assertEquals(4, arrayVar.getElements().size());

        assertEquals("INTEGER", arrayVar.getElements().get(0).getType());
        assertEquals(1, ((IntegerVariable) arrayVar.getElements().get(0)).GET());

        assertEquals("DOUBLE", arrayVar.getElements().get(1).getType());
        assertEquals(2.5, ((DoubleVariable) arrayVar.getElements().get(1)).GET());

        assertEquals("STRING", arrayVar.getElements().get(2).getType());
        assertEquals("text", ((StringVariable) arrayVar.getElements().get(2)).GET());

        assertEquals("BOOL", arrayVar.getElements().get(3).getType());
        assertTrue(((BoolVariable) arrayVar.getElements().get(3)).GET());
    }

    @Test
    void testGet() {
        arrayVar.fireMethod("ADD",
                new IntegerVariable("", 10, ctx),
                new StringVariable("", "Hello", ctx),
                new DoubleVariable("", 3.14, ctx));

        Variable element0 = arrayVar.fireMethod("GET", new IntegerVariable("", 0, ctx));
        Variable element1 = arrayVar.fireMethod("GET", new IntegerVariable("", 1, ctx));
        Variable element2 = arrayVar.fireMethod("GET", new IntegerVariable("", 2, ctx));
        Variable element3 = arrayVar.fireMethod("GET", new IntegerVariable("", 3, ctx));

        assertEquals("INTEGER", element0.getType());
        assertEquals(10, ((IntegerVariable) element0).GET());

        assertEquals("STRING", element1.getType());
        assertEquals("Hello", ((StringVariable) element1).GET());

        assertEquals("DOUBLE", element2.getType());
        assertEquals(3.14, ((DoubleVariable) element2).GET());

        assertEquals("STRING", element3.getType());
        assertEquals("NULL", ((StringVariable) element3).GET());
    }

    @Test
    void testGetWithUnknownField() {
        arrayVar.fireMethod("ADD", new IntegerVariable("", 10, ctx));

        Variable result = arrayVar.fireMethod("GET",
                new IntegerVariable("", 5, ctx),
                new IntegerVariable("", 0, ctx));

        assertEquals("STRING", result.getType());
        assertEquals("NULL", ((StringVariable) result).GET());
    }

    @Test
    void testChangeAt() {
        arrayVar.fireMethod("ADD",
                new IntegerVariable("", 1, ctx),
                new StringVariable("", "old", ctx));

        arrayVar.fireMethod("CHANGEAT",
                new IntegerVariable("", 1, ctx),
                new StringVariable("", "new", ctx));

        assertEquals("new", ((StringVariable) arrayVar.getElements().get(1)).GET());
    }

    @Test
    void testInsertAt() {
        arrayVar.fireMethod("ADD",
                new IntegerVariable("", 1, ctx),
                new IntegerVariable("", 3, ctx));

        arrayVar.fireMethod("INSERTAT",
                new IntegerVariable("", 1, ctx),
                new IntegerVariable("", 2, ctx));

        assertEquals(3, arrayVar.getElements().size());
        assertEquals(2, ((IntegerVariable) arrayVar.getElements().get(1)).GET());
        assertEquals(3, ((IntegerVariable) arrayVar.getElements().get(2)).GET());
    }

    @Test
    void testRemoveAt() {
        arrayVar.fireMethod("ADD",
                new IntegerVariable("", 1, ctx),
                new IntegerVariable("", 2, ctx),
                new IntegerVariable("", 3, ctx));

        arrayVar.fireMethod("REMOVEAT", new IntegerVariable("", 1, ctx));

        assertEquals(2, arrayVar.getElements().size());
        assertEquals(1, ((IntegerVariable) arrayVar.getElements().get(0)).GET());
        assertEquals(3, ((IntegerVariable) arrayVar.getElements().get(1)).GET());
    }

    @Test
    void testRemoveAll() {
        arrayVar.fireMethod("ADD",
                new IntegerVariable("", 1, ctx),
                new IntegerVariable("", 2, ctx));

        arrayVar.fireMethod("REMOVEALL");

        assertEquals(0, arrayVar.getElements().size());
    }

    @Test
    void testGetSize() {
        arrayVar.fireMethod("ADD",
                new IntegerVariable("", 1, ctx),
                new IntegerVariable("", 2, ctx),
                new IntegerVariable("", 3, ctx));

        IntegerVariable result = (IntegerVariable) arrayVar.fireMethod("GETSIZE");

        assertEquals(3, result.GET());
    }

    @Test
    void testFindExactMatch() {
        arrayVar.fireMethod("ADD",
                new IntegerVariable("", 1, ctx),
                new StringVariable("", "test", ctx),
                new DoubleVariable("", 3.14, ctx));

        IntegerVariable result1 = (IntegerVariable) arrayVar.fireMethod("FIND",
                new IntegerVariable("", 1, ctx));

        IntegerVariable result2 = (IntegerVariable) arrayVar.fireMethod("FIND",
                new StringVariable("", "test", ctx));

        IntegerVariable result3 = (IntegerVariable) arrayVar.fireMethod("FIND",
                new DoubleVariable("", 3.14, ctx));

        assertEquals(0, result1.GET());
        assertEquals(1, result2.GET());
        assertEquals(2, result3.GET());
    }

    @Test
    void testFindNoMatch() {
        arrayVar.fireMethod("ADD",
                new IntegerVariable("", 1, ctx),
                new StringVariable("", "test", ctx));

        IntegerVariable result = (IntegerVariable) arrayVar.fireMethod("FIND",
                new DoubleVariable("", 3.14, ctx));

        assertEquals(-1, result.GET());
    }

    @Test
    void testFindSimilarValues() {
        arrayVar.fireMethod("ADD",
                new IntegerVariable("", 4, ctx),
                new DoubleVariable("", 4.0, ctx),
                new StringVariable("", "4", ctx));

        IntegerVariable result1 = (IntegerVariable) arrayVar.fireMethod("FIND",
                new IntegerVariable("", 4, ctx));

        IntegerVariable result2 = (IntegerVariable) arrayVar.fireMethod("FIND",
                new DoubleVariable("", 4.0, ctx));

        IntegerVariable result3 = (IntegerVariable) arrayVar.fireMethod("FIND",
                new StringVariable("", "4", ctx));

        // it returns 0, no matter what type it is, argument is cast to value type
        assertEquals(0, result1.GET());
        assertEquals(0, result2.GET());
        assertEquals(0, result3.GET());
    }

    @Test
    void testContains() {
        arrayVar.fireMethod("ADD",
                new IntegerVariable("", 1, ctx),
                new StringVariable("", "test", ctx));

        BoolVariable result1 = (BoolVariable) arrayVar.fireMethod("CONTAINS",
                new StringVariable("", "test", ctx));

        BoolVariable result2 = (BoolVariable) arrayVar.fireMethod("CONTAINS",
                new StringVariable("", "missing", ctx));

        assertTrue(result1.GET());
        assertFalse(result2.GET());
    }

    @Test
    void testReverseFind() {
        arrayVar.fireMethod("ADD",
                new IntegerVariable("", 5, ctx),
                new StringVariable("", "test", ctx),
                new IntegerVariable("", 5, ctx));

        IntegerVariable result = (IntegerVariable) arrayVar.fireMethod("REVERSEFIND",
                new IntegerVariable("", 5, ctx));

        assertEquals(2, result.GET());
    }

    @Test
    void testClone() {
        arrayVar.fireMethod("ADD",
                new IntegerVariable("", 1, ctx),
                new StringVariable("", "test", ctx));

        ArrayVariable clone = arrayVar.clone();

        assertNotSame(arrayVar, clone);
        assertEquals(2, clone.getElements().size());
        assertEquals(1, ((IntegerVariable) clone.getElements().get(0)).GET());
        assertEquals("test", ((StringVariable) clone.getElements().get(1)).GET());

        clone.getElements().add(new IntegerVariable("", 3, ctx));
        assertEquals(2, arrayVar.getElements().size());
        assertEquals(3, clone.getElements().size());
    }
}