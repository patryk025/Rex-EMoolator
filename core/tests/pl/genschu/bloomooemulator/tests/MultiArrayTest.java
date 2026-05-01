package pl.genschu.bloomooemulator.tests;

import com.badlogic.gdx.Gdx;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.genschu.bloomooemulator.TestEnvironment;
import pl.genschu.bloomooemulator.interpreter.values.*;
import pl.genschu.bloomooemulator.interpreter.variable.MultiArrayVariable;
import pl.genschu.bloomooemulator.loader.MultiArrayLoader;
import pl.genschu.bloomooemulator.saver.MultiArraySaver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MultiArrayTest {
    private MultiArrayVariable multiArrayVar;

    @BeforeAll
    static void boot() {
        TestEnvironment.init();
    }

    @BeforeEach
    void setUp() {
        multiArrayVar = new MultiArrayVariable("TEST_MULTIARRAY");
    }

    @Test
    void testSetAndGet2D() {
        // Initialise 3x4 array
        multiArrayVar.setDimensions(new int[]{4, 3}); // 3x4

        // Set some values
        multiArrayVar.callMethod("SET", List.of(
                new IntValue(0), new IntValue(0), new IntValue(10)));

        multiArrayVar.callMethod("SET", List.of(
                new IntValue(1), new IntValue(2), new StringValue("test")));

        multiArrayVar.callMethod("SET", List.of(
                new IntValue(2), new IntValue(3), new DoubleValue(3.14)));

        // Check reading
        Value result1 = multiArrayVar.callMethod("GET", List.of(
                new IntValue(0), new IntValue(0))).getReturnValue();
        assertInstanceOf(IntValue.class, result1);
        assertEquals(10, ((IntValue) result1).value());

        Value result2 = multiArrayVar.callMethod("GET", List.of(
                new IntValue(1), new IntValue(2))).getReturnValue();
        assertInstanceOf(StringValue.class, result2);
        assertEquals("test", ((StringValue) result2).value());

        Value result3 = multiArrayVar.callMethod("GET", List.of(
                new IntValue(2), new IntValue(3))).getReturnValue();
        assertInstanceOf(DoubleValue.class, result3);
        assertEquals(3.14, ((DoubleValue) result3).value());
    }

    @Test
    void testSetAndGet3D() {
        // Initialise 2x3x4 array (yup, arguments are in reverse order)
        multiArrayVar.setDimensions(new int[]{4, 3, 2});

        // Set values in different places
        multiArrayVar.callMethod("SET", List.of(
                new IntValue(0), new IntValue(0), new IntValue(0), new StringValue("corner")));

        multiArrayVar.callMethod("SET", List.of(
                new IntValue(1), new IntValue(2), new IntValue(3), new IntValue(42)));

        // Check reading
        Value result1 = multiArrayVar.callMethod("GET", List.of(
                new IntValue(0), new IntValue(0), new IntValue(0))).getReturnValue();
        assertInstanceOf(StringValue.class, result1);
        assertEquals("corner", ((StringValue) result1).value());

        Value result2 = multiArrayVar.callMethod("GET", List.of(
                new IntValue(1), new IntValue(2), new IntValue(3))).getReturnValue();
        assertInstanceOf(IntValue.class, result2);
        assertEquals(42, ((IntValue) result2).value());
    }

    @Test
    void testGetNullValue() {
        multiArrayVar.setDimensions(new int[]{3, 3});

        // Read uninitialized slot
        Value result = multiArrayVar.callMethod("GET", List.of(
                new IntValue(1), new IntValue(1))).getReturnValue();

        assertInstanceOf(NullValue.class, result);
    }

    @Test
    void testOverwriteValue() {
        multiArrayVar.setDimensions(new int[]{3, 3});

        // Set value
        multiArrayVar.callMethod("SET", List.of(
                new IntValue(1), new IntValue(1), new StringValue("old")));

        // Override
        multiArrayVar.callMethod("SET", List.of(
                new IntValue(1), new IntValue(1), new StringValue("new")));

        // Check
        Value result = multiArrayVar.callMethod("GET", List.of(
                new IntValue(1), new IntValue(1))).getReturnValue();

        assertInstanceOf(StringValue.class, result);
        assertEquals("new", ((StringValue) result).value());
    }

    @Test
    void testDifferentTypes() {
        multiArrayVar.setDimensions(new int[]{2, 2});

        // Different types in different slots
        multiArrayVar.callMethod("SET", List.of(
                new IntValue(0), new IntValue(0), new IntValue(42)));
        multiArrayVar.callMethod("SET", List.of(
                new IntValue(0), new IntValue(1), new StringValue("text")));
        multiArrayVar.callMethod("SET", List.of(
                new IntValue(1), new IntValue(0), new DoubleValue(2.5)));
        multiArrayVar.callMethod("SET", List.of(
                new IntValue(1), new IntValue(1), new BoolValue(true)));

        // Check all types
        assertInstanceOf(IntValue.class, multiArrayVar.callMethod("GET", List.of(
                new IntValue(0), new IntValue(0))).getReturnValue());

        assertInstanceOf(StringValue.class, multiArrayVar.callMethod("GET", List.of(
                new IntValue(0), new IntValue(1))).getReturnValue());

        assertInstanceOf(DoubleValue.class, multiArrayVar.callMethod("GET", List.of(
                new IntValue(1), new IntValue(0))).getReturnValue());

        assertInstanceOf(BoolValue.class, multiArrayVar.callMethod("GET", List.of(
                new IntValue(1), new IntValue(1))).getReturnValue());
    }

    @Test
    void testIndexCalculation() {
        // Check index calculation
        multiArrayVar.setDimensions(new int[]{4, 3});

        multiArrayVar.callMethod("SET", List.of(
                new IntValue(2), new IntValue(3), new IntValue(999)));

        // Check if it's under the correct flat index
        Value[] data = multiArrayVar.getData();
        int expectedFlatIndex = 2 * 4 + 3; // = 11
        assertNotNull(data[expectedFlatIndex]);
        assertEquals(999, ((IntValue) data[expectedFlatIndex]).value());
    }

    @Test
    void testLargeArray() {
        // Bigger array
        multiArrayVar.setDimensions(new int[]{16, 16});

        // Fill diagonally
        for (int i = 0; i < 16; i++) {
            multiArrayVar.callMethod("SET", List.of(
                    new IntValue(i), new IntValue(i), new IntValue(i * 10)));
        }

        // Check different fields
        Value result1 = multiArrayVar.callMethod("GET", List.of(
                new IntValue(0), new IntValue(0))).getReturnValue();
        assertEquals(0, ((IntValue) result1).value());

        Value result2 = multiArrayVar.callMethod("GET", List.of(
                new IntValue(7), new IntValue(7))).getReturnValue();
        assertEquals(70, ((IntValue) result2).value());

        Value result3 = multiArrayVar.callMethod("GET", List.of(
                new IntValue(15), new IntValue(15))).getReturnValue();
        assertEquals(150, ((IntValue) result3).value());
    }

    @Test
    void testSetOnDefaultDimensions() {
        // no explicit setDimensions() — default state must already be a usable 16x16
        multiArrayVar.callMethod("SET", List.of(
                new IntValue(0), new IntValue(1), new StringValue("LP")));

        Value result = multiArrayVar.callMethod("GET", List.of(
                new IntValue(0), new IntValue(1))).getReturnValue();

        assertInstanceOf(StringValue.class, result);
        assertEquals("LP", ((StringValue) result).value());
    }

    @Test
    void testLoadFromFile() throws IOException {
        String filename = "test.mar";
        String absPath = Gdx.files.internal("../assets/test-assets/" + filename).file().getAbsolutePath();
        try (java.io.InputStream is = new java.io.FileInputStream(absPath)) {
            MultiArrayLoader.loadMultiArray(multiArrayVar, is);
        }

        Value result1 = multiArrayVar.callMethod("GET", List.of(
                new IntValue(0), new IntValue(0))).getReturnValue();
        assertEquals(0, ((IntValue) result1).value());

        Value result2 = multiArrayVar.callMethod("GET", List.of(
                new IntValue(0), new IntValue(1))).getReturnValue();
        assertEquals(1, ((IntValue) result2).value());

        Value result3 = multiArrayVar.callMethod("GET", List.of(
                new IntValue(0), new IntValue(2))).getReturnValue();
        assertEquals(2, ((IntValue) result3).value());

        Value result4 = multiArrayVar.callMethod("GET", List.of(
                new IntValue(1), new IntValue(0))).getReturnValue();
        assertEquals(3, ((IntValue) result4).value());

        Value result5 = multiArrayVar.callMethod("GET", List.of(
                new IntValue(1), new IntValue(1))).getReturnValue();
        assertEquals(4, ((IntValue) result5).value());

        Value result6 = multiArrayVar.callMethod("GET", List.of(
                new IntValue(1), new IntValue(2))).getReturnValue();
        assertEquals(5, ((IntValue) result6).value());

        Value result7 = multiArrayVar.callMethod("GET", List.of(
                new IntValue(2), new IntValue(0))).getReturnValue();
        assertEquals(6, ((IntValue) result7).value());

        Value result8 = multiArrayVar.callMethod("GET", List.of(
                new IntValue(2), new IntValue(1))).getReturnValue();
        assertEquals(7, ((IntValue) result8).value());

        Value result9 = multiArrayVar.callMethod("GET", List.of(
                new IntValue(2), new IntValue(2))).getReturnValue();
        assertEquals(8, ((IntValue) result9).value());
    }

    @Test
    void testSaveToFile() throws IOException {
        String filename = "test.mar";
        String absPath = Gdx.files.internal("../assets/test-assets/" + filename).file().getAbsolutePath();
        try (java.io.InputStream is = new java.io.FileInputStream(absPath)) {
            MultiArrayLoader.loadMultiArray(multiArrayVar, is);
        }

        String saveFilename = "test_save.mar";
        String saveAbsPath = Gdx.files.internal("../assets/test-assets/" + saveFilename).file().getAbsolutePath();
        try (java.io.OutputStream os = new java.io.FileOutputStream(saveAbsPath)) {
            MultiArraySaver.saveMultiArray(multiArrayVar, os);
        }

        // compare two files if binary match
        try {
            byte[] original = Files.readAllBytes(Paths.get(absPath));
            Path savedPath = Paths.get(saveAbsPath);
            byte[] saved = Files.readAllBytes(savedPath);
            assertArrayEquals(original, saved);
            Files.deleteIfExists(savedPath);
        } catch (IOException e) {
            fail(e);
        }
    }
}
