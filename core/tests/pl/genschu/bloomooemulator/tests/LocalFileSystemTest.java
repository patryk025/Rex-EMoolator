package pl.genschu.bloomooemulator.tests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import pl.genschu.bloomooemulator.engine.filesystem.LocalFileSystem;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class LocalFileSystemTest {

    private static String readAll(InputStream is) throws IOException {
        try (is) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    @Test
    void readsExistingFileExactCase(@TempDir Path tempDir) throws IOException {
        Files.writeString(tempDir.resolve("hello.txt"), "hi");
        LocalFileSystem fs = new LocalFileSystem(tempDir.toFile());

        assertTrue(fs.exists("hello.txt"));
        assertEquals("hi", readAll(fs.open("hello.txt")));
        assertEquals(2L, fs.length("hello.txt"));
    }

    @Test
    void resolvesPathSegmentsCaseInsensitively(@TempDir Path tempDir) throws IOException {
        Path dir = tempDir.resolve("Common").resolve("CLASSES");
        Files.createDirectories(dir);
        Files.writeString(dir.resolve("MyClass.cnv"), "payload");

        LocalFileSystem fs = new LocalFileSystem(tempDir.toFile());

        assertTrue(fs.exists("common/classes/myclass.cnv"));
        assertEquals("payload", readAll(fs.open("COMMON/CLASSES/MYCLASS.CNV")));
    }

    @Test
    void normalizesBackslashSeparators(@TempDir Path tempDir) throws IOException {
        Path dir = tempDir.resolve("WAVS");
        Files.createDirectories(dir);
        Files.writeString(dir.resolve("INTRO1.WAV"), "wav-bytes");

        LocalFileSystem fs = new LocalFileSystem(tempDir.toFile());

        assertTrue(fs.exists("WAVS\\INTRO1.WAV"));
        assertEquals("wav-bytes", readAll(fs.open("wavs\\intro1.wav")));
    }

    @Test
    void existsReturnsFalseForMissingFile(@TempDir Path tempDir) {
        LocalFileSystem fs = new LocalFileSystem(tempDir.toFile());

        assertFalse(fs.exists("nope.txt"));
        assertFalse(fs.exists("missing/dir/nope.txt"));
    }

    @Test
    void isDirectoryDistinguishesFilesFromFolders(@TempDir Path tempDir) throws IOException {
        Files.createDirectories(tempDir.resolve("DANE"));
        Files.writeString(tempDir.resolve("DANE").resolve("application.def"), "OBJECT=APP\n");

        LocalFileSystem fs = new LocalFileSystem(tempDir.toFile());

        assertTrue(fs.isDirectory("DANE"));
        assertFalse(fs.isDirectory("DANE/application.def"));
    }

    @Test
    void listReturnsChildrenOfDirectory(@TempDir Path tempDir) throws IOException {
        Path dir = tempDir.resolve("DANE");
        Files.createDirectories(dir);
        Files.writeString(dir.resolve("a.cnv"), "");
        Files.writeString(dir.resolve("b.cnv"), "");

        LocalFileSystem fs = new LocalFileSystem(tempDir.toFile());
        String[] names = fs.list("dane");

        assertNotNull(names);
        Arrays.sort(names);
        assertArrayEquals(new String[]{"a.cnv", "b.cnv"}, names);
    }

    @Test
    void writeCreatesParentDirectoriesAndIsVisibleToReads(@TempDir Path tempDir) throws IOException {
        LocalFileSystem fs = new LocalFileSystem(tempDir.toFile());

        try (OutputStream os = fs.openWrite("savegames/slot1.dat")) {
            os.write("save".getBytes(StandardCharsets.UTF_8));
        }

        assertTrue(Files.exists(tempDir.resolve("savegames").resolve("slot1.dat")));
        assertTrue(fs.exists("savegames/slot1.dat"));
        assertEquals("save", readAll(fs.open("savegames/slot1.dat")));
    }

    @Test
    void writeInvalidatesResolveCache(@TempDir Path tempDir) throws IOException {
        LocalFileSystem fs = new LocalFileSystem(tempDir.toFile());

        // First lookup misses — caches the non-existent path resolution.
        assertFalse(fs.exists("LATER.DAT"));

        try (OutputStream os = fs.openWrite("LATER.DAT")) {
            os.write("ok".getBytes(StandardCharsets.UTF_8));
        }

        assertTrue(fs.exists("LATER.DAT"));
        assertEquals("ok", readAll(fs.open("LATER.DAT")));
    }

    @Test
    void deleteRemovesFile(@TempDir Path tempDir) throws IOException {
        Files.writeString(tempDir.resolve("victim.txt"), "x");
        LocalFileSystem fs = new LocalFileSystem(tempDir.toFile());

        assertTrue(fs.exists("victim.txt"));
        assertTrue(fs.delete("victim.txt"));
        assertFalse(fs.exists("victim.txt"));
    }
}
