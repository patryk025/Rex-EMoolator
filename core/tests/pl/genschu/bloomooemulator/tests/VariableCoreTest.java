package pl.genschu.bloomooemulator.tests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import pl.genschu.bloomooemulator.TestEnvironment;
import pl.genschu.bloomooemulator.builders.ContextBuilder;
import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.exceptions.ClassMethodNotImplementedException;
import pl.genschu.bloomooemulator.interpreter.exceptions.VariableUnsupportedOperationException;
import pl.genschu.bloomooemulator.interpreter.factories.VariableFactory;
import pl.genschu.bloomooemulator.interpreter.variable.Signal;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.interpreter.variable.types.BehaviourVariable;
import pl.genschu.bloomooemulator.interpreter.variable.types.BoolVariable;
import pl.genschu.bloomooemulator.interpreter.variable.types.IntegerVariable;
import pl.genschu.bloomooemulator.interpreter.variable.types.StringVariable;

import static org.junit.jupiter.api.Assertions.*;

class VariableCoreTest {
    private Context ctx;

    @BeforeAll
    static void boot() {
        TestEnvironment.init();
    }

    @BeforeEach
    void setUp() {
        ctx = new ContextBuilder().build();
    }

    @Test
    void testConvertPrimitives() {
        Variable intVar = VariableFactory.createVariable("INTEGER", "I", 42, ctx);

        Variable strVar = intVar.convertTo("STRING");
        assertEquals("42", strVar.getValue());
        assertEquals("STRING", strVar.getType());

        Variable dblVar = intVar.convertTo("DOUBLE");
        assertEquals(42.0, dblVar.getValue());
        assertEquals("DOUBLE", dblVar.getType());

        Variable boolVar = intVar.convertTo("BOOL");
        assertEquals(true, boolVar.getValue());
        assertEquals("BOOL", boolVar.getType());
    }

    @Test
    void testConvertZeroToBool() {
        Variable zeroInt = VariableFactory.createVariable("INTEGER", "ZERO", 0, ctx);
        Variable boolVar = zeroInt.convertTo("BOOL");
        assertEquals(false, boolVar.getValue());
    }

    @Test
    void testConvertStringTypes() {
        Variable strVar = VariableFactory.createVariable("STRING", "STR", "123.45", ctx);

        Variable intVar = strVar.convertTo("INTEGER");
        assertEquals(123, intVar.getValue());

        Variable dblVar = strVar.convertTo("DOUBLE");
        assertEquals(123.45, dblVar.getValue());

        Variable boolVar = strVar.convertTo("BOOL");
        assertEquals(false, boolVar.getValue());
    }

    @Test
    void testConvertStringTypes2() {
        Variable strVar = VariableFactory.createVariable("STRING", "STR", "123.55", ctx);

        Variable intVar = strVar.convertTo("INTEGER");
        assertEquals(123, intVar.getValue());

        Variable dblVar = strVar.convertTo("DOUBLE");
        assertEquals(123.55, dblVar.getValue());

        Variable boolVar = strVar.convertTo("BOOL");
        assertEquals(false, boolVar.getValue());
    }

    @Test
    void testConvertStringTypesWithBrokenValues() {
        Variable strVar = VariableFactory.createVariable("STRING", "STR", "123.55.55", ctx);

        Variable intVar = strVar.convertTo("INTEGER");
        assertEquals(123, intVar.getValue());

        Variable dblVar = strVar.convertTo("DOUBLE");
        assertEquals(123.55, dblVar.getValue());

        Variable boolVar = strVar.convertTo("BOOL");
        assertEquals(false, boolVar.getValue());
    }

    @Test
    void testConvertStringTypesWithScientificNotation() {
        Variable strVar = VariableFactory.createVariable("STRING", "STR", "123.55e12", ctx);

        Variable intVar = strVar.convertTo("INTEGER");
        assertEquals(123, intVar.getValue());

        Variable dblVar = strVar.convertTo("DOUBLE");
        assertEquals(123550000000000.00000, dblVar.getValue());

        Variable boolVar = strVar.convertTo("BOOL");
        assertEquals(false, boolVar.getValue());
    }

    @Test
    void testAddBehaviour() {
        Context testCtx = new ContextBuilder()
                .withFactory("STRING", "HOST", "A")
                .withFactory("BEHAVIOUR", "B", "{}")
                .build();

        Variable host = testCtx.getVariable("HOST");

        assertNull(host.getSignal("ONCLICK"));

        host.fireMethod("ADDBEHAVIOUR",
                new StringVariable("", "ONCLICK", testCtx),
                new StringVariable("", "B", testCtx));

        assertNotNull(host.getSignal("ONCLICK"));
    }

    @Test
    void testAddBehaviourWithParameters() {
        Context testCtx = new ContextBuilder()
                .withFactory("STRING", "HOST", "test")
                .withFactory("BEHAVIOUR", "B_WITH_PARAMS", "{@RETURN($1);}")
                .build();

        Variable host = testCtx.getVariable("HOST");

        host.fireMethod("ADDBEHAVIOUR",
                new StringVariable("", "ONTEST", testCtx),
                new StringVariable("", "B_WITH_PARAMS(\"param1\")", testCtx));

        assertNotNull(host.getSignal("ONTEST"));
    }

    @Test
    void testRemoveBehaviour() {
        Context testCtx = new ContextBuilder()
                .withFactory("STRING", "HOST", "test")
                .withFactory("BEHAVIOUR", "B", "{}")
                .build();

        Variable host = testCtx.getVariable("HOST");

        host.fireMethod("ADDBEHAVIOUR",
                new StringVariable("", "ONTEST", testCtx),
                new StringVariable("", "B", testCtx));

        assertNotNull(host.getSignal("ONTEST"));

        host.fireMethod("REMOVEBEHAVIOUR", new StringVariable("", "ONTEST", testCtx));

        assertNull(host.getSignal("ONTEST"));
    }

    @Test
    void testClone() {
        ctx = new ContextBuilder().withFactory("INTEGER", "ORIG", 42).build();

        Variable intVar = ctx.getVariable("ORIG");

        intVar.fireMethod("CLONE");

        Variable clone1 = ctx.getVariable("ORIG_1");
        assertEquals("INTEGER", clone1.getType());
        assertEquals("ORIG_1", clone1.getName());
        assertEquals(42, ((IntegerVariable) clone1).GET());

        intVar.fireMethod("CLONE", new IntegerVariable("", 3, ctx));

        Variable clone2 = ctx.getVariable("ORIG_2");
        Variable clone3 = ctx.getVariable("ORIG_3");
        Variable clone4 = ctx.getVariable("ORIG_4");

        assertEquals("INTEGER", clone2.getType());
        assertEquals("INTEGER", clone3.getType());
        assertEquals("INTEGER", clone4.getType());

        assertEquals(4, intVar.getClones().size());
    }

    @Test
    void testGetCloneIndex() {
        ctx = new ContextBuilder().withFactory("INTEGER", "ORIG", 42).build();

        Variable intVar = ctx.getVariable("ORIG");

        Variable index = intVar.fireMethod("GETCLONEINDEX");
        assertEquals(0, index.getValue());

        intVar.fireMethod("CLONE", new IntegerVariable("", 2, ctx));

        Variable clone1 = ctx.getVariable("ORIG_1");
        Variable clone2 = ctx.getVariable("ORIG_2");

        Variable index1 = clone1.fireMethod("GETCLONEINDEX");
        Variable index2 = clone2.fireMethod("GETCLONEINDEX");

        assertEquals(1, index1.getValue());
        assertEquals(2, index2.getValue());
    }

    @Test
    void testConvertToSameType() {
        Variable intVar = VariableFactory.createVariable("INTEGER", "I", 42, ctx);
        Variable converted = intVar.convertTo("INTEGER");

        assertSame(intVar, converted);
    }

    @Test
    void testConvertNonPrimitiveTypes() {
        Variable animoVar = VariableFactory.createVariable("ANIMO", "A", null, ctx);

        assertThrows(VariableUnsupportedOperationException.class, () -> {
            animoVar.convertTo("STRING");
        });
    }

    @Test
    void testEmitSignal() {
        Variable var = VariableFactory.createVariable("STRING", "TEST", "value", ctx);

        BoolVariable flag = new BoolVariable("FLAG", false, ctx);
        ctx.setVariable("FLAG", flag);

        var.setSignal("ONSIGNAL^TEST", new Signal() {
            @Override
            public void execute(Object argument) {
                flag.fireMethod("SET", new BoolVariable("", true, ctx));
            }
        });

        assertFalse(flag.GET());

        var.fireMethod("SEND", new StringVariable("", "TEST", ctx));

        assertTrue(flag.GET());

        flag.setSignal("ONBRUTALCHANGED^TRUE", new Signal() {
            @Override
            public void execute(Object argument) {
                flag.fireMethod("SET", new BoolVariable("", false, ctx));
            }
        });

        flag.fireMethod("SET", new BoolVariable("", true, ctx));

        assertFalse(flag.GET());
    }
}