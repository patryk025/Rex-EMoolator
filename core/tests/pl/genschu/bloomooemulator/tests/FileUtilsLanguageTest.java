package pl.genschu.bloomooemulator.tests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import pl.genschu.bloomooemulator.TestEnvironment;
import pl.genschu.bloomooemulator.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for FileUtils language fallback mechanism.
 */
public class FileUtilsLanguageTest {

    @TempDir
    Path tempDir;

    @BeforeAll
    static void boot() {
        TestEnvironment.init();
    }

    @Test
    public void testLanguageFallback_LanguageSpecificExists() throws IOException {
        // Setup: POL/test.cnv exists, base test.cnv doesn't
        Path polDir = tempDir.resolve("POL");
        Files.createDirectories(polDir);
        Path polFile = polDir.resolve("test.cnv");
        Files.createFile(polFile);

        // Test
        File result = FileUtils.findRelativeFileWithLanguageFallback(
            tempDir.toFile(),
            "test.cnv",
            "POL"
        );

        // Assert
        assertNotNull(result, "Should find language-specific file");
        assertTrue(result.getAbsolutePath().contains("POL"), "Should be in POL directory");
        assertTrue(result.exists(), "File should exist");
    }

    @Test
    public void testLanguageFallback_FallbackToBase() throws IOException {
        // Setup: only base test.cnv exists (no POL directory)
        Path baseFile = tempDir.resolve("test.cnv");
        Files.createFile(baseFile);

        // Test
        File result = FileUtils.findRelativeFileWithLanguageFallback(
            tempDir.toFile(),
            "test.cnv",
            "POL"
        );

        // Assert
        assertNotNull(result, "Should find base file as fallback");
        assertFalse(result.getAbsolutePath().contains("POL"), "Should NOT be in POL directory");
        assertEquals(baseFile.toFile().getAbsolutePath(), result.getAbsolutePath(), "Should match base file path");
    }

    @Test
    public void testLanguageFallback_BothExist_PreferLanguage() throws IOException {
        // Setup: both POL/test.cnv and base test.cnv exist
        Path polDir = tempDir.resolve("POL");
        Files.createDirectories(polDir);
        Files.createFile(polDir.resolve("test.cnv"));
        Files.createFile(tempDir.resolve("test.cnv"));

        // Test
        File result = FileUtils.findRelativeFileWithLanguageFallback(
            tempDir.toFile(),
            "test.cnv",
            "POL"
        );

        // Assert
        assertNotNull(result, "Should find file");
        assertTrue(result.getAbsolutePath().contains("POL"), "Should prefer language-specific over base");
    }

    @Test
    public void testLanguageFallback_NeitherExists() {
        // Setup: no files exist

        // Test
        File result = FileUtils.findRelativeFileWithLanguageFallback(
            tempDir.toFile(),
            "nonexistent.cnv",
            "POL"
        );

        // Assert
        assertNull(result, "Should return null when neither file exists");
    }

    @Test
    public void testLanguageFallback_CaseInsensitive() throws IOException {
        // Setup: pol/test.cnv exists (lowercase directory)
        Path polDir = tempDir.resolve("pol");
        Files.createDirectories(polDir);
        Files.createFile(polDir.resolve("test.cnv"));

        // Test: query with uppercase POL
        File result = FileUtils.findRelativeFileWithLanguageFallback(
            tempDir.toFile(),
            "TEST.CNV",
            "POL"
        );

        // Assert
        assertNotNull(result, "Should find file with case-insensitive matching");
        assertTrue(result.exists(), "File should exist");
    }

    @Test
    public void testLanguageFallback_NullLanguageCode() throws IOException {
        // Setup: only base file exists
        Path baseFile = tempDir.resolve("test.cnv");
        Files.createFile(baseFile);

        // Test: null language code
        File result = FileUtils.findRelativeFileWithLanguageFallback(
            tempDir.toFile(),
            "test.cnv",
            null
        );

        // Assert
        assertNotNull(result, "Should return base file when language is null");
        assertEquals(baseFile.toFile().getAbsolutePath(), result.getAbsolutePath());
    }

    @Test
    public void testLanguageFallback_EmptyLanguageCode() throws IOException {
        // Setup: only base file exists
        Path baseFile = tempDir.resolve("test.cnv");
        Files.createFile(baseFile);

        // Test: empty language code
        File result = FileUtils.findRelativeFileWithLanguageFallback(
            tempDir.toFile(),
            "test.cnv",
            ""
        );

        // Assert
        assertNotNull(result, "Should return base file when language is empty");
        assertEquals(baseFile.toFile().getAbsolutePath(), result.getAbsolutePath());
    }

    @Test
    public void testLanguageFallback_DifferentLanguages() throws IOException {
        // Setup: multiple language directories
        Path polDir = tempDir.resolve("POL");
        Path hunDir = tempDir.resolve("HUN");
        Path czeDir = tempDir.resolve("CZE");
        Files.createDirectories(polDir);
        Files.createDirectories(hunDir);
        Files.createDirectories(czeDir);

        Files.createFile(polDir.resolve("test.cnv"));
        Files.createFile(hunDir.resolve("test.cnv"));
        Files.createFile(czeDir.resolve("test.cnv"));

        // Test: Find HUN version
        File hunResult = FileUtils.findRelativeFileWithLanguageFallback(
            tempDir.toFile(),
            "test.cnv",
            "HUN"
        );

        // Assert
        assertNotNull(hunResult, "Should find HUN file");
        assertTrue(hunResult.getAbsolutePath().contains("HUN"), "Should be in HUN directory");

        // Test: Find CZE version
        File czeResult = FileUtils.findRelativeFileWithLanguageFallback(
            tempDir.toFile(),
            "test.cnv",
            "CZE"
        );

        // Assert
        assertNotNull(czeResult, "Should find CZE file");
        assertTrue(czeResult.getAbsolutePath().contains("CZE"), "Should be in CZE directory");
    }
}
