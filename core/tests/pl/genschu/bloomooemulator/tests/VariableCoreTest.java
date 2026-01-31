package pl.genschu.bloomooemulator.tests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import pl.genschu.bloomooemulator.TestEnvironment;
import pl.genschu.bloomooemulator.builders.ContextBuilder;
import pl.genschu.bloomooemulator.builders.MethodHelper;
import pl.genschu.bloomooemulator.interpreter.context.Context;
import pl.genschu.bloomooemulator.interpreter.v1.exceptions.VariableUnsupportedOperationException;
import pl.genschu.bloomooemulator.interpreter.factories.VariableFactory;
import pl.genschu.bloomooemulator.interpreter.values.IntValue;
import pl.genschu.bloomooemulator.interpreter.values.StringValue;
import pl.genschu.bloomooemulator.interpreter.values.Value;
import pl.genschu.bloomooemulator.interpreter.variable.MethodResult;
import pl.genschu.bloomooemulator.interpreter.variable.RandVariable;
import pl.genschu.bloomooemulator.interpreter.variable.SignalHandler;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

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
        Variable intVar = VariableFactory.createVariable("INTEGER", "I", 42);

        Variable strVar = intVar.convertTo("STRING");
        assertEquals("STRING", strVar.type().name());
        assertEquals("42", strVar.value().toDisplayString());

        Variable dblVar = intVar.convertTo("DOUBLE");
        assertEquals("DOUBLE", dblVar.type().name());
        assertEquals(42.0, dblVar.value().toDouble().value());

        Variable boolVar = intVar.convertTo("BOOLEAN");
        assertEquals("BOOLEAN", boolVar.type().name());
        assertTrue(boolVar.value().toBool().value());
    }

    @Test
    void testConvertZeroToBool() {
        Variable zeroInt = VariableFactory.createVariable("INTEGER", "ZERO", 0);
        Variable boolVar = zeroInt.convertTo("BOOLEAN");
        assertFalse(boolVar.value().toBool().value());
    }

    @Test
    void testConvertStringTypes() {
        Variable strVar = VariableFactory.createVariable("STRING", "STR", "123.45");

        Variable intVar = strVar.convertTo("INTEGER");
        assertEquals(123, intVar.value().toInt().value());

        Variable dblVar = strVar.convertTo("DOUBLE");
        assertEquals(123.45, dblVar.value().toDouble().value());

        Variable boolVar = strVar.convertTo("BOOLEAN");
        assertFalse(boolVar.value().toBool().value());
    }

    @Test
    void testConvertStringTypes2() {
        Variable strVar = VariableFactory.createVariable("STRING", "STR", "123.55");

        Variable intVar = strVar.convertTo("INTEGER");
        assertEquals(123, intVar.value().toInt().value());

        Variable dblVar = strVar.convertTo("DOUBLE");
        assertEquals(123.55, dblVar.value().toDouble().value());

        Variable boolVar = strVar.convertTo("BOOLEAN");
        assertFalse(boolVar.value().toBool().value());
    }

    @Test
    void testConvertStringTypesWithBrokenValues() {
        Variable strVar = VariableFactory.createVariable("STRING", "STR", "123.55.55");

        Variable intVar = strVar.convertTo("INTEGER");
        assertEquals(123, intVar.value().toInt().value());

        Variable dblVar = strVar.convertTo("DOUBLE");
        assertEquals(123.55, dblVar.value().toDouble().value());

        Variable boolVar = strVar.convertTo("BOOLEAN");
        assertFalse(boolVar.value().toBool().value());
    }

    @Test
    void testConvertStringTypesWithScientificNotation() {
        Variable strVar = VariableFactory.createVariable("STRING", "STR", "123.55e12");

        Variable intVar = strVar.convertTo("INTEGER");
        assertEquals(123, intVar.value().toInt().value());

        Variable dblVar = strVar.convertTo("DOUBLE");
        assertEquals(123550000000000.00000, dblVar.value().toDouble().value());

        Variable boolVar = strVar.convertTo("BOOLEAN");
        assertFalse(boolVar.value().toBool().value());
    }

    @Test
    void testAddBehaviour() {
        Context testCtx = new ContextBuilder()
                .withVariable("STRING", "HOST", "A")
                .withVariable("BEHAVIOUR", "B", "{}")
                .build();

        Variable host = testCtx.getVariable("HOST");
        assertNull(host.getSignal("ONCLICK"));

        // Use MethodHelper to execute method with effects
        MethodHelper.callWithEffects(testCtx, "HOST", "ADDBEHAVIOUR",
                new StringValue("ONCLICK"),
                new StringValue("B"));

        // Get updated variable from context (immutable)
        host = testCtx.getVariable("HOST");
        assertNotNull(host.getSignal("ONCLICK"));
    }

    @Test
    void testAddBehaviourWithParameters() {
        Context testCtx = new ContextBuilder()
                .withVariable("STRING", "HOST", "test")
                .withVariable("BEHAVIOUR", "B_WITH_PARAMS", "{@RETURN($1);}")
                .build();

        Variable host = testCtx.getVariable("HOST");

        MethodHelper.callWithEffects(testCtx, "HOST", "ADDBEHAVIOUR",
                new StringValue("ONTEST"),
                new StringValue("B_WITH_PARAMS(\"param1\")"));

        // Get updated variable from context
        host = testCtx.getVariable("HOST");
        assertNotNull(host.getSignal("ONTEST"));
    }

    @Test
    void testRemoveBehaviour() {
        Context testCtx = new ContextBuilder()
                .withVariable("STRING", "HOST", "test")
                .withVariable("BEHAVIOUR", "B", "{}")
                .build();

        // Add behaviour first
        MethodHelper.callWithEffects(testCtx, "HOST", "ADDBEHAVIOUR",
                new StringValue("ONTEST"),
                new StringValue("B"));

        Variable host = testCtx.getVariable("HOST");
        assertNotNull(host.getSignal("ONTEST"));

        // Remove behaviour
        MethodHelper.callWithEffects(testCtx, "HOST", "REMOVEBEHAVIOUR",
                new StringValue("ONTEST"));

        // Get updated variable
        host = testCtx.getVariable("HOST");
        assertNull(host.getSignal("ONTEST"));
    }

    @Test
    void testClone() {
        ctx = new ContextBuilder().withVariable("INTEGER", "ORIG", 42).build();

        Variable intVar = ctx.getVariable("ORIG");

        // Clone once
        MethodHelper.callWithEffects(ctx, "ORIG", "CLONE");

        Variable clone1 = ctx.getVariable("ORIG_1");
        assertEquals("INTEGER", clone1.type().name());
        assertEquals("ORIG_1", clone1.name());
        assertEquals(42, clone1.value().toInt().value());

        // Clone 3 more times
        MethodHelper.callWithEffects(ctx, "ORIG", "CLONE", new IntValue(3));

        Variable clone2 = ctx.getVariable("ORIG_2");
        Variable clone3 = ctx.getVariable("ORIG_3");
        Variable clone4 = ctx.getVariable("ORIG_4");

        assertEquals("INTEGER", clone2.type().name());
        assertEquals("INTEGER", clone3.type().name());
        assertEquals("INTEGER", clone4.type().name());

        assertEquals(4, ctx.clones().getCloneNames("ORIG").size());
    }

    @Test
    void testGetCloneIndex() {
        ctx = new ContextBuilder().withVariable("INTEGER", "ORIG", 42).build();

        Variable intVar = ctx.getVariable("ORIG");

        Value index = intVar.callMethod("GETCLONEINDEX", List.of()).getReturnValue();
        assertEquals(0, index.toInt().value());

        // Clone 2 times - use MethodHelper to execute effects
        MethodHelper.callWithEffects(ctx, "ORIG", "CLONE", new IntValue(2));

        Variable clone1 = ctx.getVariable("ORIG_1");
        Variable clone2 = ctx.getVariable("ORIG_2");

        Value index1 = clone1.callMethod("GETCLONEINDEX", List.of()).getReturnValue();
        Value index2 = clone2.callMethod("GETCLONEINDEX", List.of()).getReturnValue();

        assertEquals(1, index1.toInt().value());
        assertEquals(2, index2.toInt().value());
    }

    @Test
    void testConvertToSameType() {
        Variable intVar = VariableFactory.createVariable("INTEGER", "I", 42);
        Variable converted = intVar.convertTo("INTEGER");

        assertSame(intVar, converted);
    }

    @Test
    void testConvertNonPrimitiveTypes() {
        Variable animoVar = new RandVariable("A");

        assertThrows(UnsupportedOperationException.class, () -> {
            animoVar.convertTo("STRING");
        });
    }

    @Test
    void testEmitSignal() {
        // Use atomic boolean to track signal execution
        AtomicBoolean signalExecuted = new AtomicBoolean(false);

        // Create context with variable
        Context testCtx = new ContextBuilder()
                .withVariable("STRING", "TEST", "value")
                .build();

        // Add signal handler using withSignal
        Variable var = testCtx.getVariable("TEST");
        SignalHandler handler = (variable, signalName, args) -> {
            signalExecuted.set(true);
        };
        Variable varWithSignal = var.withSignal("ONSIGNAL^TEST", handler);
        testCtx.setVariable("TEST", varWithSignal);

        assertFalse(signalExecuted.get());

        // Send signal - this should trigger the handler
        MethodHelper.callWithEffects(testCtx, "TEST", "SEND", new StringValue("TEST"));

        assertTrue(signalExecuted.get());
    }

    @Test
    void testEmitSignalWithBoolVariable() {
        // Test signal on BoolVariable with ONBRUTALCHANGED
        AtomicBoolean changedToTrue = new AtomicBoolean(false);

        Context testCtx = new ContextBuilder()
                .withVariable("BOOLEAN", "FLAG", false)
                .build();

        // Add ONBRUTALCHANGED^TRUE signal
        Variable flag = testCtx.getVariable("FLAG");
        SignalHandler handler = (variable, signalName, args) -> {
            changedToTrue.set(true);
        };
        Variable flagWithSignal = flag.withSignal("ONBRUTALCHANGED^TRUE", handler);
        testCtx.setVariable("FLAG", flagWithSignal);

        // Change value to true - should trigger signal
        // Note: In full implementation, SET would emit ONBRUTALCHANGED
        // For this test, we manually emit the signal
        flag = testCtx.getVariable("FLAG");
        flag.emitSignal("ONBRUTALCHANGED^TRUE");

        assertTrue(changedToTrue.get());
    }
}