package pl.genschu.bloomooemulator.tests;

import com.badlogic.gdx.Gdx;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import pl.genschu.bloomooemulator.TestEnvironment;
import pl.genschu.bloomooemulator.builders.ContextBuilder;
import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.variable.Attribute;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.interpreter.variable.types.*;
import pl.genschu.bloomooemulator.loader.MultiArrayLoader;
import pl.genschu.bloomooemulator.saver.MultiArraySaver;
import pl.genschu.bloomooemulator.utils.FileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class MultiArrayTest {
    private Context ctx;
    private MultiArrayVariable multiArrayVar;

    @BeforeAll
    static void boot() {
        TestEnvironment.init();
    }

    @BeforeEach
    void setUp() {
        ctx = new ContextBuilder().build();
        multiArrayVar = new MultiArrayVariable("TEST_MULTIARRAY", ctx);
    }

    @Test
    void testSetAndGet2D() {
        // Initialise 3x4 array
        multiArrayVar.setDimensions(new int[]{4, 3}); // 3x4

        // Set some values
        multiArrayVar.fireMethod("SET",
                new IntegerVariable("", 0, ctx),
                new IntegerVariable("", 0, ctx),
                new IntegerVariable("", 10, ctx));

        multiArrayVar.fireMethod("SET",
                new IntegerVariable("", 1, ctx),
                new IntegerVariable("", 2, ctx),
                new StringVariable("", "test", ctx));

        multiArrayVar.fireMethod("SET",
                new IntegerVariable("", 2, ctx),
                new IntegerVariable("", 3, ctx),
                new DoubleVariable("", 3.14, ctx));

        // Sprawdź odczyt
        Variable result1 = multiArrayVar.fireMethod("GET",
                new IntegerVariable("", 0, ctx),
                new IntegerVariable("", 0, ctx));
        assertEquals("INTEGER", result1.getType());
        assertEquals(10, ((IntegerVariable) result1).GET());

        Variable result2 = multiArrayVar.fireMethod("GET",
                new IntegerVariable("", 1, ctx),
                new IntegerVariable("", 2, ctx));
        assertEquals("STRING", result2.getType());
        assertEquals("test", ((StringVariable) result2).GET());

        Variable result3 = multiArrayVar.fireMethod("GET",
                new IntegerVariable("", 2, ctx),
                new IntegerVariable("", 3, ctx));
        assertEquals("DOUBLE", result3.getType());
        assertEquals(3.14, ((DoubleVariable) result3).GET());
    }

    @Test
    void testSetAndGet3D() {
        // Initialise 2x3x4 array (yup, arguments are in reverse order)
        multiArrayVar.setDimensions(new int[]{4, 3, 2});

        // Set values in different places
        multiArrayVar.fireMethod("SET",
                new IntegerVariable("", 0, ctx),
                new IntegerVariable("", 0, ctx),
                new IntegerVariable("", 0, ctx),
                new StringVariable("", "corner", ctx));

        multiArrayVar.fireMethod("SET",
                new IntegerVariable("", 1, ctx),
                new IntegerVariable("", 2, ctx),
                new IntegerVariable("", 3, ctx),
                new IntegerVariable("", 42, ctx));

        // Check reading
        Variable result1 = multiArrayVar.fireMethod("GET",
                new IntegerVariable("", 0, ctx),
                new IntegerVariable("", 0, ctx),
                new IntegerVariable("", 0, ctx));
        assertEquals("STRING", result1.getType());
        assertEquals("corner", ((StringVariable) result1).GET());

        Variable result2 = multiArrayVar.fireMethod("GET",
                new IntegerVariable("", 1, ctx),
                new IntegerVariable("", 2, ctx),
                new IntegerVariable("", 3, ctx));
        assertEquals("INTEGER", result2.getType());
        assertEquals(42, ((IntegerVariable) result2).GET());
    }

    @Test
    void testGetNullValue() {
        multiArrayVar.setDimensions(new int[]{3, 3});

        // Read uninitialized slot
        Variable result = multiArrayVar.fireMethod("GET",
                new IntegerVariable("", 1, ctx),
                new IntegerVariable("", 1, ctx));

        assertEquals("STRING", result.getType());
        assertEquals("NULL", ((StringVariable) result).GET());
    }

    @Test
    void testOverwriteValue() {
        multiArrayVar.setDimensions(new int[]{3, 3});

        // Set value
        multiArrayVar.fireMethod("SET",
                new IntegerVariable("", 1, ctx),
                new IntegerVariable("", 1, ctx),
                new StringVariable("", "old", ctx));

        // Override
        multiArrayVar.fireMethod("SET",
                new IntegerVariable("", 1, ctx),
                new IntegerVariable("", 1, ctx),
                new StringVariable("", "new", ctx));

        // Check
        Variable result = multiArrayVar.fireMethod("GET",
                new IntegerVariable("", 1, ctx),
                new IntegerVariable("", 1, ctx));

        assertEquals("new", ((StringVariable) result).GET());
    }

    @Test
    void testDifferentTypes() {
        multiArrayVar.setDimensions(new int[]{2, 2});

        // Different types in different slots
        multiArrayVar.fireMethod("SET",
                new IntegerVariable("", 0, ctx),
                new IntegerVariable("", 0, ctx),
                new IntegerVariable("", 42, ctx));

        multiArrayVar.fireMethod("SET",
                new IntegerVariable("", 0, ctx),
                new IntegerVariable("", 1, ctx),
                new StringVariable("", "text", ctx));

        multiArrayVar.fireMethod("SET",
                new IntegerVariable("", 1, ctx),
                new IntegerVariable("", 0, ctx),
                new DoubleVariable("", 2.5, ctx));

        multiArrayVar.fireMethod("SET",
                new IntegerVariable("", 1, ctx),
                new IntegerVariable("", 1, ctx),
                new BoolVariable("", true, ctx));

        // Check all types
        assertEquals("INTEGER", multiArrayVar.fireMethod("GET",
                new IntegerVariable("", 0, ctx),
                new IntegerVariable("", 0, ctx)).getType());

        assertEquals("STRING", multiArrayVar.fireMethod("GET",
                new IntegerVariable("", 0, ctx),
                new IntegerVariable("", 1, ctx)).getType());

        assertEquals("DOUBLE", multiArrayVar.fireMethod("GET",
                new IntegerVariable("", 1, ctx),
                new IntegerVariable("", 0, ctx)).getType());

        assertEquals("BOOL", multiArrayVar.fireMethod("GET",
                new IntegerVariable("", 1, ctx),
                new IntegerVariable("", 1, ctx)).getType());
    }

    @Test
    void testIndexCalculation() {
        // Check index calculation
        multiArrayVar.setDimensions(new int[]{4, 3});

        multiArrayVar.fireMethod("SET",
                new IntegerVariable("", 2, ctx),
                new IntegerVariable("", 3, ctx),
                new IntegerVariable("", 999, ctx));

        // Sprawdź czy jest pod poprawnym flat indexem
        Variable[] data = multiArrayVar.getData();
        int expectedFlatIndex = 2 * 4 + 3; // = 11
        assertNotNull(data[expectedFlatIndex]);
        assertEquals(999, ((IntegerVariable) data[expectedFlatIndex]).GET());
    }

    @Test
    void testLargeArray() {
        // Bigger array
        multiArrayVar.setDimensions(new int[]{16, 16});

        // Fill diagonally
        for (int i = 0; i < 16; i++) {
            multiArrayVar.fireMethod("SET",
                    new IntegerVariable("", i, ctx),
                    new IntegerVariable("", i, ctx),
                    new IntegerVariable("", i * 10, ctx));
        }

        // Check different fields
        Variable result1 = multiArrayVar.fireMethod("GET",
                new IntegerVariable("", 0, ctx),
                new IntegerVariable("", 0, ctx));
        assertEquals(0, ((IntegerVariable) result1).GET());

        Variable result2 = multiArrayVar.fireMethod("GET",
                new IntegerVariable("", 7, ctx),
                new IntegerVariable("", 7, ctx));
        assertEquals(70, ((IntegerVariable) result2).GET());

        Variable result3 = multiArrayVar.fireMethod("GET",
                new IntegerVariable("", 15, ctx),
                new IntegerVariable("", 15, ctx));
        assertEquals(150, ((IntegerVariable) result3).GET());
    }

    @Test
    void testLoadFromFile() {
        String filename = "test.mar";

        try (MockedStatic<FileUtils> fu = Mockito.mockStatic(FileUtils.class)) {
            String absPath = Gdx.files.internal("../assets/test-assets/"+filename).file().getAbsolutePath();
            fu.when(() -> FileUtils.resolveRelativePath(Mockito.any(Variable.class), Mockito.anyString()))
                    .thenReturn(absPath);

            MultiArrayLoader.loadMultiArray(multiArrayVar, filename);
        }

        Variable result1 = multiArrayVar.fireMethod("GET",
                new IntegerVariable("", 0, ctx),
                new IntegerVariable("", 0, ctx));
        assertEquals(0, ((IntegerVariable) result1).GET());

        Variable result2 = multiArrayVar.fireMethod("GET",
                new IntegerVariable("", 0, ctx),
                new IntegerVariable("", 1, ctx));
        assertEquals(1, ((IntegerVariable) result2).GET());

        Variable result3 = multiArrayVar.fireMethod("GET",
                new IntegerVariable("", 0, ctx),
                new IntegerVariable("", 2, ctx));
        assertEquals(2, ((IntegerVariable) result3).GET());

        Variable result4 = multiArrayVar.fireMethod("GET",
                new IntegerVariable("", 1, ctx),
                new IntegerVariable("", 0, ctx));
        assertEquals(3, ((IntegerVariable) result4).GET());

        Variable result5 = multiArrayVar.fireMethod("GET",
                new IntegerVariable("", 1, ctx),
                new IntegerVariable("", 1, ctx));
        assertEquals(4, ((IntegerVariable) result5).GET());

        Variable result6 = multiArrayVar.fireMethod("GET",
                new IntegerVariable("", 1, ctx),
                new IntegerVariable("", 2, ctx));
        assertEquals(5, ((IntegerVariable) result6).GET());

        Variable result7 = multiArrayVar.fireMethod("GET",
                new IntegerVariable("", 2, ctx),
                new IntegerVariable("", 0, ctx));
        assertEquals(6, ((IntegerVariable) result7).GET());

        Variable result8 = multiArrayVar.fireMethod("GET",
                new IntegerVariable("", 2, ctx),
                new IntegerVariable("", 1, ctx));
        assertEquals(7, ((IntegerVariable) result8).GET());

        Variable result9 = multiArrayVar.fireMethod("GET",
                new IntegerVariable("", 2, ctx),
                new IntegerVariable("", 2, ctx));
        assertEquals(8, ((IntegerVariable) result9).GET());
    }

    @Test
    void testSaveToFile() {
        String filename = "test.mar";

        try (MockedStatic<FileUtils> fu = Mockito.mockStatic(FileUtils.class)) {
            String absPath = Gdx.files.internal("../assets/test-assets/"+filename).file().getAbsolutePath();
            fu.when(() -> FileUtils.resolveRelativePath(Mockito.any(Variable.class), Mockito.anyString()))
                    .thenReturn(absPath);

            MultiArrayLoader.loadMultiArray(multiArrayVar, filename);

            MultiArraySaver.saveMultiArray(multiArrayVar, filename);
        }

        filename = "test_save.mar";
        try (MockedStatic<FileUtils> fu = Mockito.mockStatic(FileUtils.class)) {
            String absPath = Gdx.files.internal("../assets/test-assets/"+filename).file().getAbsolutePath();
            fu.when(() -> FileUtils.resolveRelativePath(Mockito.any(Variable.class), Mockito.anyString()))
                    .thenReturn(absPath);

            MultiArraySaver.saveMultiArray(multiArrayVar, filename);
        }

        // compare two files if binary match
        try {
            String orig = Gdx.files.internal("../assets/test-assets/test.mar").file().getAbsolutePath();
            String savedFile = Gdx.files.internal("../assets/test-assets/test_save.mar").file().getAbsolutePath();
            byte[] original = Files.readAllBytes(Paths.get(orig));
            Path savedPath = Paths.get(savedFile);
            byte[] saved = Files.readAllBytes(savedPath);
            assertArrayEquals(original, saved);
            Files.deleteIfExists(savedPath);
        } catch (IOException e) {
            fail(e);
        }
    }
}