package pl.genschu.bloomooemulator.tests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import pl.genschu.bloomooemulator.TestEnvironment;
import pl.genschu.bloomooemulator.builders.ContextBuilder;
import pl.genschu.bloomooemulator.builders.MethodHelper;
import pl.genschu.bloomooemulator.interpreter.context.Context;
import pl.genschu.bloomooemulator.interpreter.factories.VariableFactory;
import pl.genschu.bloomooemulator.interpreter.variable.ApplicationVariable;
import pl.genschu.bloomooemulator.interpreter.values.*;
import pl.genschu.bloomooemulator.interpreter.variable.RandVariable;
import pl.genschu.bloomooemulator.interpreter.variable.SignalHandler;
import pl.genschu.bloomooemulator.interpreter.variable.TimerVariable;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

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
    void testAddBehaviourResolvesParameterVariableNameWithoutTruncation() {
        Context testCtx = new ContextBuilder()
                .withVariable("STRING", "HOST", "test")
                .withVariable("STRING", "PARAM", "resolved")
                .withVariable("STRING", "TARGET", "")
                .withVariable("BEHAVIOUR", "B_WITH_PARAMS", "{TARGET^SET($1);}")
                .build();

        MethodHelper.callWithEffects(testCtx, "HOST", "ADDBEHAVIOUR",
                new StringValue("ONTEST"),
                new StringValue("B_WITH_PARAMS(PARAM)"));

        Variable host = testCtx.getVariable("HOST");
        host.emitSignal("ONTEST");

        Variable target = testCtx.getVariable("TARGET");
        assertEquals("resolved", target.value().toDisplayString());
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
    void testWithoutSignalRemovesSignalViaWithSignalContract() {
        SignalHandler handler = (variable, signalName, arguments) -> {};
        Variable app = new ApplicationVariable("APP").withSignal("ONTEST", handler);

        assertNotNull(app.getSignal("ONTEST"));

        Variable updated = app.withoutSignal("ONTEST");

        assertNull(updated.getSignal("ONTEST"));
    }

    @Test
    void testCopyAsThrowsWhenVariableDoesNotOverrideIt() {
        Variable app = new ApplicationVariable("APP");

        UnsupportedOperationException exception = assertThrows(
                UnsupportedOperationException.class,
                () -> app.copyAs("APP_1")
        );

        assertEquals("copyAs not implemented for APPLICATION variable", exception.getMessage());
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
        flag = testCtx.getVariable("FLAG");
        flag.callMethod("SET", BoolValue.TRUE);

        assertTrue(changedToTrue.get());
    }

    @Test
    void testTimerVariableEmitsOnTick() {
        AtomicBoolean ticked = new AtomicBoolean(false);

        TimerVariable timer = new TimerVariable("TIMER", 10);
        timer = (TimerVariable) timer.withSignal("ONTICK^1", (var, signal, args) -> ticked.set(true));

        timer.update(timer.lastTickTime() + timer.elapse());

        assertTrue(ticked.get());
        assertEquals(1, timer.currentTickCount());
    }

    // ========================================
    // ONCHANGED / ONBRUTALCHANGED SIGNAL TESTS
    // ========================================

    @Test
    void testIntegerVariableSignalsOnSet() {
        AtomicInteger brutalChangedCount = new AtomicInteger(0);
        AtomicInteger changedCount = new AtomicInteger(0);
        List<String> brutalValues = new ArrayList<>();
        List<String> changedValues = new ArrayList<>();

        Context testCtx = new ContextBuilder()
                .withVariable("INTEGER", "NUM", 10)
                .build();

        Variable num = testCtx.getVariable("NUM");
        num = num.withSignal("ONBRUTALCHANGED", (var, signal, args) -> {
            brutalChangedCount.incrementAndGet();
        });
        num = num.withSignal("ONCHANGED", (var, signal, args) -> {
            changedCount.incrementAndGet();
        });
        testCtx.setVariable("NUM", num);

        // SET to different value - both signals should fire
        num = testCtx.getVariable("NUM");
        num.callMethod("SET", List.of(new IntValue(20)));

        assertEquals(1, brutalChangedCount.get());
        assertEquals(1, changedCount.get());

        // SET to same value - only ONBRUTALCHANGED should fire
        num.callMethod("SET", List.of(new IntValue(20)));

        assertEquals(2, brutalChangedCount.get());
        assertEquals(1, changedCount.get()); // unchanged!
    }

    @Test
    void testIntegerVariableSignalsWithParamOnSet() {
        AtomicInteger brutalChangedCount = new AtomicInteger(0);
        AtomicInteger changedCount = new AtomicInteger(0);

        Context testCtx = new ContextBuilder()
                .withVariable("INTEGER", "NUM", 10)
                .build();

        Variable num = testCtx.getVariable("NUM");
        num = num.withSignal("ONBRUTALCHANGED", (var, signal, args) -> {
            brutalChangedCount.incrementAndGet();
        }).withSignal("ONBRUTALCHANGED^20", (var, signal, args) -> {
            brutalChangedCount.addAndGet(2); // count this signal as 2 changes to distinguish it
        });
        num = num.withSignal("ONCHANGED", (var, signal, args) -> {
            changedCount.incrementAndGet();
        });
        testCtx.setVariable("NUM", num);

        // SET to different value - both signals should fire
        num = testCtx.getVariable("NUM");
        num.callMethod("SET", List.of(new IntValue(20)));

        assertEquals(2, brutalChangedCount.get());
        assertEquals(1, changedCount.get());

        // SET to same value - only ONBRUTALCHANGED should fire
        num.callMethod("SET", List.of(new IntValue(20)));

        assertEquals(4, brutalChangedCount.get());
        assertEquals(1, changedCount.get()); // unchanged!
    }

    @Test
    void testIntegerVariableSignalsOnAdd() {
        AtomicInteger brutalChangedCount = new AtomicInteger(0);
        AtomicInteger changedCount = new AtomicInteger(0);

        Context testCtx = new ContextBuilder()
                .withVariable("INTEGER", "NUM", 10)
                .build();

        Variable num = testCtx.getVariable("NUM");
        num = num.withSignal("ONBRUTALCHANGED", (var, signal, args) -> brutalChangedCount.incrementAndGet());
        num = num.withSignal("ONCHANGED", (var, signal, args) -> changedCount.incrementAndGet());
        testCtx.setVariable("NUM", num);

        // ADD 5 - value changes from 10 to 15
        num = testCtx.getVariable("NUM");
        num.callMethod("ADD", List.of(new IntValue(5)));

        assertEquals(1, brutalChangedCount.get());
        assertEquals(1, changedCount.get());
        assertEquals(15, num.value().toInt().value());

        // ADD 0 - value stays 15, only ONBRUTALCHANGED fires
        num.callMethod("ADD", List.of(new IntValue(0)));

        assertEquals(2, brutalChangedCount.get());
        assertEquals(1, changedCount.get()); // unchanged!
    }

    @Test
    void testDoubleVariableSignalsOnSet() {
        AtomicInteger brutalChangedCount = new AtomicInteger(0);
        AtomicInteger changedCount = new AtomicInteger(0);

        Context testCtx = new ContextBuilder()
                .withVariable("DOUBLE", "NUM", 10.5)
                .build();

        Variable num = testCtx.getVariable("NUM");
        num = num.withSignal("ONBRUTALCHANGED", (var, signal, args) -> brutalChangedCount.incrementAndGet());
        num = num.withSignal("ONCHANGED", (var, signal, args) -> changedCount.incrementAndGet());
        testCtx.setVariable("NUM", num);

        // SET to different value
        num = testCtx.getVariable("NUM");
        num.callMethod("SET", List.of(new DoubleValue(20.5)));

        assertEquals(1, brutalChangedCount.get());
        assertEquals(1, changedCount.get());

        // SET to same value - only ONBRUTALCHANGED should fire
        num.callMethod("SET", List.of(new DoubleValue(20.5)));

        assertEquals(2, brutalChangedCount.get());
        assertEquals(1, changedCount.get()); // unchanged!
    }

    @Test
    void testStringVariableSignalsOnSet() {
        AtomicInteger brutalChangedCount = new AtomicInteger(0);
        AtomicInteger changedCount = new AtomicInteger(0);

        Context testCtx = new ContextBuilder()
                .withVariable("STRING", "TEXT", "hello")
                .build();

        Variable text = testCtx.getVariable("TEXT");
        text = text.withSignal("ONBRUTALCHANGED", (var, signal, args) -> brutalChangedCount.incrementAndGet());
        text = text.withSignal("ONCHANGED", (var, signal, args) -> {
            changedCount.incrementAndGet();
        });
        testCtx.setVariable("TEXT", text);

        // SET to different value
        text = testCtx.getVariable("TEXT");
        text.callMethod("SET", List.of(new StringValue("world")));

        assertEquals(1, brutalChangedCount.get());
        assertEquals(1, changedCount.get());

        // SET to same value - only ONBRUTALCHANGED should fire
        text.callMethod("SET", List.of(new StringValue("world")));

        assertEquals(2, brutalChangedCount.get());
    }

    @Test
    void testStringVariableSignalsOnAdd() {
        AtomicInteger brutalChangedCount = new AtomicInteger(0);
        AtomicInteger changedCount = new AtomicInteger(0);

        Context testCtx = new ContextBuilder()
                .withVariable("STRING", "TEXT", "hello")
                .build();

        Variable text = testCtx.getVariable("TEXT");
        text = text.withSignal("ONBRUTALCHANGED", (var, signal, args) -> brutalChangedCount.incrementAndGet());
        text = text.withSignal("ONCHANGED", (var, signal, args) -> changedCount.incrementAndGet());
        testCtx.setVariable("TEXT", text);

        // ADD " world" - value changes
        text = testCtx.getVariable("TEXT");
        text.callMethod("ADD", List.of(new StringValue(" world")));

        assertEquals(1, brutalChangedCount.get());
        assertEquals(1, changedCount.get());
        assertEquals("hello world", text.value().toDisplayString());

        // ADD "" (empty string) - value stays the same, only ONBRUTALCHANGED fires
        text.callMethod("ADD", List.of(new StringValue("")));

        assertEquals(2, brutalChangedCount.get());
        assertEquals(1, changedCount.get()); // unchanged!
    }

    @Test
    void testBoolVariableSignalsOnSet() {
        AtomicInteger brutalChangedCount = new AtomicInteger(0);
        AtomicInteger changedCount = new AtomicInteger(0);

        Context testCtx = new ContextBuilder()
                .withVariable("BOOLEAN", "FLAG", false)
                .build();

        Variable flag = testCtx.getVariable("FLAG");
        flag = flag.withSignal("ONBRUTALCHANGED", (var, signal, args) -> brutalChangedCount.incrementAndGet());
        flag = flag.withSignal("ONCHANGED", (var, signal, args) -> {
            changedCount.incrementAndGet();
        });
        testCtx.setVariable("FLAG", flag);

        // SET to true - value changes
        flag = testCtx.getVariable("FLAG");
        flag.callMethod("SET", List.of(new BoolValue(true)));

        assertEquals(1, brutalChangedCount.get());
        assertEquals(1, changedCount.get());

        // SET to true again - only ONBRUTALCHANGED should fire
        flag.callMethod("SET", List.of(new BoolValue(true)));

        assertEquals(2, brutalChangedCount.get());
        assertEquals(1, changedCount.get()); // unchanged!
    }

    @Test
    void testBoolVariableSignalsOnSwitch() {
        AtomicInteger brutalChangedCount = new AtomicInteger(0);
        AtomicInteger changedCount = new AtomicInteger(0);

        Context testCtx = new ContextBuilder()
                .withVariable("BOOLEAN", "FLAG", true)
                .build();

        Variable flag = testCtx.getVariable("FLAG");
        flag = flag.withSignal("ONBRUTALCHANGED", (var, signal, args) -> brutalChangedCount.incrementAndGet());
        flag = flag.withSignal("ONCHANGED", (var, signal, args) -> changedCount.incrementAndGet());
        testCtx.setVariable("FLAG", flag);

        flag = testCtx.getVariable("FLAG");
        flag.callMethod("SWITCH", List.of(
                new BoolValue(false),
                new BoolValue(false)
        ));

        assertEquals(1, brutalChangedCount.get());
        assertEquals(1, changedCount.get());
        assertFalse(flag.value().toBool().value());
    }

    @Test
    void testIntegerVariableSignalsOnIncDec() {
        AtomicInteger brutalChangedCount = new AtomicInteger(0);
        AtomicInteger changedCount = new AtomicInteger(0);

        Context testCtx = new ContextBuilder()
                .withVariable("INTEGER", "NUM", 10)
                .build();

        Variable num = testCtx.getVariable("NUM");
        num = num.withSignal("ONBRUTALCHANGED", (var, signal, args) -> brutalChangedCount.incrementAndGet());
        num = num.withSignal("ONCHANGED", (var, signal, args) -> changedCount.incrementAndGet());
        testCtx.setVariable("NUM", num);

        // INC - value changes from 10 to 11
        num = testCtx.getVariable("NUM");
        num.callMethod("INC", List.of());

        assertEquals(1, brutalChangedCount.get());
        assertEquals(1, changedCount.get());
        assertEquals(11, num.value().toInt().value());

        // DEC - value changes from 11 to 10
        num.callMethod("DEC", List.of());

        assertEquals(2, brutalChangedCount.get());
        assertEquals(2, changedCount.get());
        assertEquals(10, num.value().toInt().value());
    }
}
