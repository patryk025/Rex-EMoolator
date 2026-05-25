package pl.genschu.bloomooemulator.tests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import pl.genschu.bloomooemulator.TestEnvironment;
import pl.genschu.bloomooemulator.builders.ContextBuilder;
import pl.genschu.bloomooemulator.builders.MethodHelper;
import pl.genschu.bloomooemulator.engine.Game;
import pl.genschu.bloomooemulator.engine.filesystem.LocalFileSystem;
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
        assertEquals(List.of(mouse), ctx.getMouseVariables());
        assertEquals(List.of(keyboard), ctx.getKeyboardVariables());
        assertSame(mouse, ctx.getVariable("MOUSE"));
        assertSame(random, ctx.getVariable("RAND"));
    }

    @Test
    void testInputListenersPreserveHierarchyOrderAndDuplicateNames() {
        Context definition = new ContextBuilder().build();
        Context application = new Context(new ExecutionContext(), definition);
        Context episode = new Context(new ExecutionContext(), application);
        Context scene = new Context(new ExecutionContext(), episode);

        MouseVariable applicationMouse = new MouseVariable("MOUSE");
        MouseVariable episodeMouse = new MouseVariable("MOUSE");
        MouseVariable sceneMouse = new MouseVariable("MOUSE");
        KeyboardVariable applicationKeyboard = new KeyboardVariable("KEYBOARD");
        KeyboardVariable episodeKeyboard = new KeyboardVariable("KEYBOARD");
        KeyboardVariable sceneKeyboard = new KeyboardVariable("KEYBOARD");

        application.setVariable("MOUSE", applicationMouse);
        episode.setVariable("MOUSE", episodeMouse);
        scene.setVariable("MOUSE", sceneMouse);
        application.setVariable("KEYBOARD", applicationKeyboard);
        episode.setVariable("KEYBOARD", episodeKeyboard);
        scene.setVariable("KEYBOARD", sceneKeyboard);

        assertEquals(List.of(applicationMouse, episodeMouse, sceneMouse), scene.getMouseVariables());
        assertEquals(List.of(applicationKeyboard, episodeKeyboard, sceneKeyboard), scene.getKeyboardVariables());
        assertSame(sceneMouse, scene.getMouseVariable());
        assertSame(sceneKeyboard, scene.getKeyboardVariable());
    }

    @Test
    void testClassVariableLoadsConstructorAndDestructor(@org.junit.jupiter.api.io.TempDir Path tempDir) throws IOException {
        Context ctx = new ContextBuilder()
                .withVariable("STRING", "FLAG", "")
                .build();

        Path commonClasses = tempDir.resolve("COMMON/classes");
        Files.createDirectories(commonClasses);
        Files.writeString(commonClasses.resolve("myclass.class"), """
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

        Game game = new Game(null, null);
        game.getVfs().mountAssets(new LocalFileSystem(tempDir.toFile()));
        ctx.setGame(game);

        ctx.setVariable("MYCLASS", new ClassVariable("MYCLASS", "myclass.class"));

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
    void testButtonInClassInstanceResolvesGraphicsViaOwningContext(@org.junit.jupiter.api.io.TempDir Path tempDir) throws IOException {
        // Regression guard: a BUTTON defined inside a class instance is collected by
        // getButtonsVariables (so the input handler iterates it), but its GFX/SND
        // siblings live in the instance context, invisible to a scene-level
        // getVariable(), which only walks up the parent chain, never down into
        // instances. ButtonHandler must resolve state changes against the owning
        // context, located via findOwningContext.
        Context scene = new ContextBuilder().build();

        Path commonClasses = tempDir.resolve("COMMON/classes");
        Files.createDirectories(commonClasses);
        Files.writeString(commonClasses.resolve("widget.class"), """
                OBJECT = MYGFX
                MYGFX:TYPE = INTEGER
                MYGFX:VALUE = 7
                OBJECT = MYBTN
                MYBTN:TYPE = BUTTON
                MYBTN:GFXSTANDARD = MYGFX
                MYBTN:RECT = 10,10,50,50
                """);

        Game game = new Game(null, null);
        game.getVfs().mountAssets(new LocalFileSystem(tempDir.toFile()));
        scene.setGame(game);

        scene.setVariable("WIDGET", new ClassVariable("WIDGET", "widget.class"));
        MethodHelper.callWithContext(scene, "WIDGET", "NEW", new StringValue("OBJ"));

        InstanceVariable instance = (InstanceVariable) scene.store().get("OBJ");
        Context instanceCtx = instance.instanceContext();

        // The class button is collected from the scene (input handler sees it).
        assertTrue(scene.getButtonsVariables().containsKey("MYBTN"),
                "class-instance button must appear in scene button collection");

        // The bug's root: a scene-level lookup never descends into instances, so the
        // default resolver hands back a STRING(name)=name fallback instead of the real
        // GFX. ButtonHandler then sees a non-image gfx, its alpha test returns 0, and
        // the button is skipped: no focus, no events.
        Variable sceneGfx = scene.getVariable("MYGFX");
        assertInstanceOf(StringVariable.class, sceneGfx);
        assertEquals("MYGFX", sceneGfx.value().toDisplayString());

        // The fix: the scene can locate the context that actually owns the button,
        // and from there the real sibling GFX resolves.
        assertSame(instanceCtx, scene.findOwningContext(scene.getButtonsVariables().get("MYBTN")));
        Variable ownerGfx = instanceCtx.getVariable("MYGFX");
        assertInstanceOf(IntegerVariable.class, ownerGfx);
        assertEquals(7, ownerGfx.value().toInt().value());
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
    void testCloneCopiesArrayWithExplicitImplementation() {
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
    void testStringCopyFileCopiesContents(@org.junit.jupiter.api.io.TempDir Path tempDir) throws IOException {
        Context ctx = new ContextBuilder()
                .withVariable(new StringVariable("FS", ""))
                .build();

        Path assetsDir = tempDir.resolve("assets");
        Path storageDir = tempDir.resolve("storage");
        Files.createDirectories(assetsDir);
        Files.createDirectories(storageDir);
        Files.writeString(assetsDir.resolve("source.txt"), "payload");

        Game game = new Game(null, null);
        game.getVfs().mountAssets(new LocalFileSystem(assetsDir.toFile()));
        game.getVfs().setStorage(new LocalFileSystem(storageDir.toFile()));
        ctx.setGame(game);

        Value result = MethodHelper.callWithContext(
                ctx,
                "FS",
                "COPYFILE",
                new StringValue("$\\source.txt"),
                new StringValue("$\\destination.txt")
        );

        assertTrue(result.toBool().value());
        assertEquals("payload", Files.readString(storageDir.resolve("destination.txt")));
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
