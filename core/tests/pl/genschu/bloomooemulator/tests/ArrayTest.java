package pl.genschu.bloomooemulator.tests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.genschu.bloomooemulator.TestEnvironment;
import pl.genschu.bloomooemulator.interpreter.values.*;
import pl.genschu.bloomooemulator.interpreter.variable.ArrayVariable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ArrayTest {
    private ArrayVariable arrayVar;

    @BeforeAll
    static void boot() {
        TestEnvironment.init();
    }

    @BeforeEach
    void setUp() {
        arrayVar = new ArrayVariable("TEST_ARRAY");
    }

    @Test
    void testAdd() {
        arrayVar = (ArrayVariable) arrayVar.callMethod("ADD",
                List.of(new IntValue(1),
                new DoubleValue(2.5),
                new StringValue("text"),
                new BoolValue(true))).newSelf();

        assertEquals(4, arrayVar.elements().size());

        assertEquals("INT", arrayVar.elements().get(0).getType().name());
        assertEquals(1, ((IntValue) arrayVar.elements().get(0)).value());

        assertEquals("DOUBLE", arrayVar.elements().get(1).getType().name());
        assertEquals(2.5, ((DoubleValue) arrayVar.elements().get(1)).value());

        assertEquals("STRING", arrayVar.elements().get(2).getType().name());
        assertEquals("text", ((StringValue) arrayVar.elements().get(2)).value());

        assertEquals("BOOL", arrayVar.elements().get(3).getType().name());
        assertTrue(((BoolValue) arrayVar.elements().get(3)).value());
    }

    @Test
    void testGet() {
        arrayVar = (ArrayVariable) arrayVar.callMethod("ADD",
                List.of(new IntValue(10),
                new StringValue("Hello"),
                new DoubleValue(3.14))).newSelf();

        Value element0 = arrayVar.callMethod("GET", List.of(new IntValue(0))).getReturnValue();
        Value element1 = arrayVar.callMethod("GET", List.of(new IntValue(1))).getReturnValue();
        Value element2 = arrayVar.callMethod("GET", List.of(new IntValue(2))).getReturnValue();
        Value element3 = arrayVar.callMethod("GET", List.of(new IntValue(3))).getReturnValue();

        assertEquals("INT", element0.getType().name());
        assertEquals(10, ((IntValue) element0).value());

        assertEquals("STRING", element1.getType().name());
        assertEquals("Hello", ((StringValue) element1).value());

        assertEquals("DOUBLE", element2.getType().name());
        assertEquals(3.14, ((DoubleValue) element2).value());

        assertEquals("NULL", element3.getType().name());
        assertEquals("NULL", ((NullValue) element3).toDisplayString());
    }

    @Test
    void testGetWithUnknownField() {
        arrayVar = (ArrayVariable) arrayVar.callMethod("ADD", List.of(new IntValue(10))).newSelf();

        Value result = arrayVar.callMethod("GET",
                List.of(new IntValue(5),
                new IntValue(0))).getReturnValue();

        assertEquals("NULL", result.getType().name());
        assertEquals("NULL", ((NullValue) result).toDisplayString());
    }

    @Test
    void testChangeAt() {
        arrayVar = (ArrayVariable) arrayVar.callMethod("ADD",
                List.of(new IntValue(1),
                new StringValue("old"))).newSelf();

        arrayVar = (ArrayVariable) arrayVar.callMethod("CHANGEAT",
                List.of(new IntValue(1),
                new StringValue("new"))).newSelf();

        assertEquals("new", ((StringValue) arrayVar.elements().get(1)).value());
    }

    @Test
    void testInsertAt() {
        arrayVar = (ArrayVariable) arrayVar.callMethod("ADD",
                List.of(new IntValue(1),
                new IntValue(3))).newSelf();

        arrayVar = (ArrayVariable) arrayVar.callMethod("INSERTAT",
                List.of(new IntValue(1),
                new IntValue(2))).newSelf();

        assertEquals(3, arrayVar.elements().size());
        assertEquals(2, ((IntValue) arrayVar.elements().get(1)).value());
        assertEquals(3, ((IntValue) arrayVar.elements().get(2)).value());
    }

    @Test
    void testRemoveAt() {
        arrayVar = (ArrayVariable) arrayVar.callMethod("ADD",
                List.of(new IntValue(1),
                new IntValue(2),
                new IntValue(3))).newSelf();

        arrayVar = (ArrayVariable) arrayVar.callMethod("REMOVEAT", List.of(new IntValue(1))).newSelf();

        assertEquals(2, arrayVar.elements().size());
        assertEquals(1, ((IntValue) arrayVar.elements().get(0)).value());
        assertEquals(3, ((IntValue) arrayVar.elements().get(1)).value());
    }

    @Test
    void testRemoveAll() {
        arrayVar = (ArrayVariable) arrayVar.callMethod("ADD",
                List.of(new IntValue(1),
                new IntValue(2))).newSelf();

        arrayVar = (ArrayVariable) arrayVar.callMethod("REMOVEALL", List.of()).newSelf();

        assertEquals(0, arrayVar.elements().size());
    }

    @Test
    void testGetSize() {
        arrayVar = (ArrayVariable) arrayVar.callMethod("ADD",
                List.of(new IntValue(1),
                new IntValue(2),
                new IntValue(3))).newSelf();

        IntValue result = (IntValue) arrayVar.callMethod("GETSIZE", List.of()).getReturnValue();

        assertEquals(3, result.value());
    }

    @Test
    void testFindExactMatch() {
        arrayVar = (ArrayVariable) arrayVar.callMethod("ADD",
                List.of(new IntValue(1),
                new StringValue("test"),
                new DoubleValue(3.14))).newSelf();

        IntValue result1 = (IntValue) arrayVar.callMethod("FIND",
                List.of(new IntValue(1))).getReturnValue();

        IntValue result2 = (IntValue) arrayVar.callMethod("FIND",
                List.of(new StringValue("test"))).getReturnValue();

        IntValue result3 = (IntValue) arrayVar.callMethod("FIND",
                List.of(new DoubleValue(3.14))).getReturnValue();

        assertEquals(0, result1.value());
        assertEquals(1, result2.value());
        assertEquals(2, result3.value());
    }

    @Test
    void testFindNoMatch() {
        arrayVar = (ArrayVariable) arrayVar.callMethod("ADD",
                List.of(new IntValue(1),
                new StringValue("test"))).newSelf();

        IntValue result = (IntValue) arrayVar.callMethod("FIND",
                List.of(new DoubleValue(3.14))).getReturnValue();

        assertEquals(-1, result.value());
    }

    @Test
    void testFindSimilarValues() {
        arrayVar = (ArrayVariable) arrayVar.callMethod("ADD",
                List.of(new IntValue(4),
                new DoubleValue(4.0),
                new StringValue("4"))).newSelf();

        IntValue result1 = (IntValue) arrayVar.callMethod("FIND",
                List.of(new IntValue(4))).getReturnValue();

        IntValue result2 = (IntValue) arrayVar.callMethod("FIND",
                List.of(new DoubleValue(4.0))).getReturnValue();

        IntValue result3 = (IntValue) arrayVar.callMethod("FIND",
                List.of(new StringValue("4"))).getReturnValue();

        // it returns 0, no matter what type it is, argument is cast to value type
        assertEquals(0, result1.value());
        assertEquals(0, result2.value());
        assertEquals(0, result3.value());
    }

    @Test
    void testContains() {
        arrayVar = (ArrayVariable) arrayVar.callMethod("ADD",
                List.of(new IntValue(1),
                new StringValue("test"))).newSelf();

        BoolValue result1 = (BoolValue) arrayVar.callMethod("CONTAINS",
                List.of(new StringValue("test"))).getReturnValue();

        BoolValue result2 = (BoolValue) arrayVar.callMethod("CONTAINS",
                List.of(new StringValue("missing"))).getReturnValue();

        assertTrue(result1.value());
        assertFalse(result2.value());
    }

    @Test
    void testReverseFind() {
        arrayVar = (ArrayVariable) arrayVar.callMethod("ADD",
                List.of(new IntValue(5),
                new StringValue("test"),
                new IntValue(5))).newSelf();

        IntValue result = (IntValue) arrayVar.callMethod("REVERSEFIND",
                List.of(new IntValue(5))).getReturnValue();

        assertEquals(2, result.value());
    }

    /*@Test
    void testClone() {
        arrayVar.callMethod("ADD",
                List.of(new IntValue(1),
                new StringValue("test")));

        ArrayVariable clone = arrayVar.clone();

        assertNotSame(arrayVar, clone);
        assertEquals(2, clone.elements().size());
        assertEquals(1, ((IntValue) clone.elements().get(0)).value());
        assertEquals("test", ((StringValue) clone.elements().get(1)).value());

        clone.elements().add(new IntValue(3));
        assertEquals(2, arrayVar.elements().size());
        assertEquals(3, clone.elements().size());
    }*/
}