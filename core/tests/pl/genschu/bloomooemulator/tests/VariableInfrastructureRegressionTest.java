package pl.genschu.bloomooemulator.tests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import pl.genschu.bloomooemulator.TestEnvironment;
import pl.genschu.bloomooemulator.builders.ContextBuilder;
import pl.genschu.bloomooemulator.builders.MethodHelper;
import pl.genschu.bloomooemulator.interpreter.context.Context;
import pl.genschu.bloomooemulator.interpreter.runtime.ExecutionContext;
import pl.genschu.bloomooemulator.interpreter.values.*;
import pl.genschu.bloomooemulator.interpreter.variable.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class VariableInfrastructureRegressionTest {

    @BeforeAll
    static void boot() {
        TestEnvironment.init();
    }

    @Test
    void testBuiltinsAreResolvedByDefaultResolver() {
        Context ctx = new ContextBuilder().build();

        Variable mouse = ctx.getVariable("MOUSE");
        Variable keyboard = ctx.getVariable("KEYBOARD");
        Variable random = ctx.getVariable("RANDOM");
        Variable system = ctx.getVariable("SYSTEM");

        assertInstanceOf(MouseVariable.class, mouse);
        assertInstanceOf(KeyboardVariable.class, keyboard);
        assertInstanceOf(RandVariable.class, random);
        assertInstanceOf(SystemVariable.class, system);

        assertSame(mouse, ctx.getMouseVariable());
        assertSame(keyboard, ctx.getKeyboardVariable());
        assertSame(mouse, ctx.getVariable("MOUSE"));
        assertSame(random, ctx.getVariable("RAND"));
    }

    @Test
    void testClassVariableLoadsConstructorAndDestructor() throws IOException {
        Context ctx = new ContextBuilder()
                .withVariable("STRING", "FLAG", "")
                .build();

        Path classFile = Files.createTempFile("class-variable", ".cnv");
        Files.writeString(classFile, """
                OBJECT = VALUE
                VALUE:TYPE = INTEGER
                VALUE:VALUE = 1
                OBJECT = CONSTRUCTOR
                CONSTRUCTOR:TYPE = BEHAVIOUR
                CONSTRUCTOR:CODE = {VALUE^SET(5);}
                OBJECT = DESTRUCTOR
                DESTRUCTOR:TYPE = BEHAVIOUR
                DESTRUCTOR:CODE = {FLAG^SET("deleted");}
                """);

        ctx.setVariable("MYCLASS", new ClassVariable("MYCLASS", classFile.toAbsolutePath().toString()));

        MethodHelper.callWithContext(ctx, "MYCLASS", "NEW", new StringValue("OBJ"));

        Variable created = ctx.store().get("OBJ");
        assertInstanceOf(InstanceVariable.class, created);

        InstanceVariable instance = (InstanceVariable) created;
        Variable valueVar = instance.instanceContext().store().get("VALUE");
        assertNotNull(valueVar);
        assertEquals(5, valueVar.value().toInt().value());

        MethodHelper.callWithContext(ctx, "MYCLASS", "DELETE", new StringValue("OBJ"));

        assertFalse(ctx.hasLocalVariable("OBJ"));
        assertEquals("deleted", ctx.getVariable("FLAG").value().toDisplayString());
    }

    @Test
    void testGroupVariableBroadcastsMethodsAndReturnsVariableRef() {
        Context ctx = new ContextBuilder()
                .withVariable("INTEGER", "A", 1)
                .withVariable("INTEGER", "B", 2)
                .build();

        GroupVariable group = new GroupVariable(
                "G",
                new ArrayList<>(List.of("A", "B")),
                -1,
                Map.of()
        );
        ctx.setVariable("G", group);

        MethodHelper.callWithContext(ctx, "G", "INC");

        assertEquals(2, ctx.getVariable("A").value().toInt().value());
        assertEquals(3, ctx.getVariable("B").value().toInt().value());

        Value first = MethodHelper.callWithContext(ctx, "G", "NEXT");
        assertInstanceOf(VariableRef.class, first);
        assertEquals("A", first.toDisplayString());
    }

    @Test
    void testCloneFallsBackToGenericRecordCopy() {
        ArrayVariable array = new ArrayVariable("ARR");
        array.callMethod("ADD", List.of(new IntValue(1), new IntValue(2)));

        Context ctx = new ContextBuilder()
                .withVariable(array)
                .build();

        MethodHelper.callWithContext(ctx, "ARR", "CLONE");

        Variable cloned = ctx.store().get("ARR_1");
        assertInstanceOf(ArrayVariable.class, cloned);
        assertNotSame(array, cloned);
        assertEquals(2, ((ArrayVariable) cloned).elements().size());

        ((ArrayVariable) cloned).elements().add(new IntValue(3));
        assertEquals(2, array.elements().size());
        assertEquals(3, ((ArrayVariable) cloned).elements().size());
    }

    @Test
    void testResetIniRestoresValuesFromContextAttributes() {
        Context ctx = new ContextBuilder().build();

        ctx.setVariable("TXT", new StringVariable("TXT", "changed"));
        ctx.setVariable("FLAG", new BoolVariable("FLAG", false));
        ctx.setVariable("COUNT", new IntegerVariable("COUNT", 99));
        ctx.setVariable("RATIO", new DoubleVariable("RATIO", 9.5));

        ctx.setAttribute("TXT", "VALUE", "original");
        ctx.setAttribute("FLAG", "DEFAULT", "TRUE");
        ctx.setAttribute("COUNT", "VALUE", "13");
        ctx.setAttribute("COUNT", "INIT_VALUE", "7");
        ctx.setAttribute("RATIO", "VALUE", "1.25");

        MethodHelper.callWithContext(ctx, "TXT", "RESETINI");
        MethodHelper.callWithContext(ctx, "FLAG", "RESETINI");
        MethodHelper.callWithContext(ctx, "COUNT", "RESETINI");
        MethodHelper.callWithContext(ctx, "RATIO", "RESETINI");

        assertEquals("original", ctx.getVariable("TXT").value().toDisplayString());
        assertTrue(ctx.getVariable("FLAG").value().toBool().value());
        assertEquals(7, ctx.getVariable("COUNT").value().toInt().value());
        assertEquals(1.25, ctx.getVariable("RATIO").value().toDouble().value(), 0.0001);
    }

    @Test
    void testAdditionalContextVariablesAreResolvableAndResetIniSeesTheirAttributes() {
        Context root = new ContextBuilder().build();
        Context additional = new Context(new ExecutionContext(), root);

        IntegerVariable inner = new IntegerVariable("INNER", 42);
        additional.setVariable("INNER", inner);
        additional.setAttribute("INNER", "INIT_VALUE", "7");
        root.addAdditionalContext(additional);

        assertSame(inner, root.getVariable("INNER"));

        MethodHelper.callWithContext(root, "INNER", "RESETINI");

        assertEquals(7, inner.getInt());
    }

    @Test
    void testStringCopyFileCopiesContents() throws IOException {
        Context ctx = new ContextBuilder()
                .withVariable(new StringVariable("FS", ""))
                .build();

        Path source = Files.createTempFile("copyfile-source", ".txt");
        Path destination = Files.createTempFile("copyfile-destination", ".txt");
        Files.writeString(source, "payload");

        Value result = MethodHelper.callWithContext(
                ctx,
                "FS",
                "COPYFILE",
                new StringValue(source.toString()),
                new StringValue(destination.toString())
        );

        assertTrue(result.toBool().value());
        assertEquals("payload", Files.readString(destination));
    }

    @Test
    void testRandGetPlentyAppendsUniqueValuesToArray() {
        Context ctx = new ContextBuilder()
                .withVariable(new ArrayVariable("ARR"))
                .build();

        ArrayVariable array = (ArrayVariable) ctx.getVariable("ARR");
        array.elements().add(new IntValue(99));

        MethodHelper.callWithContext(
                ctx,
                "RAND",
                "GETPLENTY",
                new VariableRef("ARR"),
                new IntValue(3),
                new IntValue(10),
                new IntValue(5),
                BoolValue.TRUE
        );

        assertEquals(4, array.elements().size());
        assertEquals(99, array.elements().getFirst().toInt().value());

        List<Integer> generated = array.elements().subList(1, array.elements().size()).stream()
                .map(Value::toInt)
                .map(IntValue::value)
                .toList();

        assertEquals(3, generated.size());
        assertEquals(3, generated.stream().distinct().count());
        assertTrue(generated.stream().allMatch(value -> value >= 10 && value < 15));
    }
}
