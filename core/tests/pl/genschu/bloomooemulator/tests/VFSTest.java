package pl.genschu.bloomooemulator.tests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import pl.genschu.bloomooemulator.engine.filesystem.LocalFileSystem;
import pl.genschu.bloomooemulator.engine.filesystem.VFS;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class VFSTest {

    private static String readAll(InputStream is) throws IOException {
        try (is) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    @Test
    void readsFromSingleAssetSource(@TempDir Path tempDir) throws IOException {
        Files.writeString(tempDir.resolve("a.txt"), "alpha");

        VFS vfs = new VFS();
        vfs.mountAssets(new LocalFileSystem(tempDir.toFile()));

        assertTrue(vfs.exists("a.txt"));
        assertEquals(5L, vfs.length("a.txt"));
        assertEquals("alpha", readAll(vfs.openRead("a.txt")));
    }

    @Test
    void openReadThrowsWhenPathMissingEverywhere(@TempDir Path tempDir) {
        VFS vfs = new VFS();
        vfs.mountAssets(new LocalFileSystem(tempDir.toFile()));

        assertFalse(vfs.exists("missing.txt"));
        assertEquals(0L, vfs.length("missing.txt"));
        assertThrows(IOException.class, () -> vfs.openRead("missing.txt"));
    }

    @Test
    void lastMountedAssetSourceWins(@TempDir Path tempDir) throws IOException {
        Path baseDir = tempDir.resolve("base");
        Path patchDir = tempDir.resolve("patch");
        Files.createDirectories(baseDir);
        Files.createDirectories(patchDir);
        Files.writeString(baseDir.resolve("file.txt"), "from-base");
        Files.writeString(patchDir.resolve("file.txt"), "from-patch");

        VFS vfs = new VFS();
        vfs.mountAssets(new LocalFileSystem(baseDir.toFile()));
        vfs.mountAssets(new LocalFileSystem(patchDir.toFile())); // mounted later → wins

        assertEquals("from-patch", readAll(vfs.openRead("file.txt")));
    }

    @Test
    void storageOverridesAssetSources(@TempDir Path tempDir) throws IOException {
        Path assetsDir = tempDir.resolve("assets");
        Path storageDir = tempDir.resolve("storage");
        Files.createDirectories(assetsDir);
        Files.createDirectories(storageDir);
        Files.writeString(assetsDir.resolve("save.dat"), "asset-version");
        Files.writeString(storageDir.resolve("save.dat"), "storage-version");

        VFS vfs = new VFS();
        vfs.mountAssets(new LocalFileSystem(assetsDir.toFile()));
        vfs.setStorage(new LocalFileSystem(storageDir.toFile()));

        assertEquals("storage-version", readAll(vfs.openRead("save.dat")));
    }

    @Test
    void languagePrefixedFileWinsOverBare(@TempDir Path tempDir) throws IOException {
        Files.writeString(tempDir.resolve("greeting.txt"), "hello");
        Path polDir = tempDir.resolve("POL");
        Files.createDirectories(polDir);
        Files.writeString(polDir.resolve("greeting.txt"), "czesc");

        VFS vfs = new VFS();
        vfs.mountAssets(new LocalFileSystem(tempDir.toFile()));
        vfs.setLanguage("POL");

        assertEquals("czesc", readAll(vfs.openRead("greeting.txt")));
    }

    @Test
    void fallsBackToBarePathWhenLocalizedMissing(@TempDir Path tempDir) throws IOException {
        Files.writeString(tempDir.resolve("greeting.txt"), "hello");

        VFS vfs = new VFS();
        vfs.mountAssets(new LocalFileSystem(tempDir.toFile()));
        vfs.setLanguage("POL");

        assertEquals("hello", readAll(vfs.openRead("greeting.txt")));
    }

    @Test
    void emptyOrNullLanguageDisablesLocalizedLookup(@TempDir Path tempDir) throws IOException {
        Path polDir = tempDir.resolve("POL");
        Files.createDirectories(polDir);
        Files.writeString(polDir.resolve("greeting.txt"), "czesc");
        Files.writeString(tempDir.resolve("greeting.txt"), "hello");

        VFS vfs = new VFS();
        vfs.mountAssets(new LocalFileSystem(tempDir.toFile()));

        // null language
        vfs.setLanguage(null);
        assertEquals("hello", readAll(vfs.openRead("greeting.txt")));

        // empty language
        vfs.setLanguage("");
        assertEquals("hello", readAll(vfs.openRead("greeting.txt")));
    }

    @Test
    void openWriteWithoutStorageThrows(@TempDir Path tempDir) {
        VFS vfs = new VFS();
        vfs.mountAssets(new LocalFileSystem(tempDir.toFile()));

        assertThrows(IOException.class, () -> vfs.openWrite("foo.dat"));
    }

    @Test
    void openWriteRoutesToStorageAndIsVisibleViaOpenRead(@TempDir Path tempDir) throws IOException {
        Path assetsDir = tempDir.resolve("assets");
        Path storageDir = tempDir.resolve("storage");
        Files.createDirectories(assetsDir);
        Files.createDirectories(storageDir);

        VFS vfs = new VFS();
        vfs.mountAssets(new LocalFileSystem(assetsDir.toFile()));
        vfs.setStorage(new LocalFileSystem(storageDir.toFile()));

        try (OutputStream os = vfs.openWrite("game.ini")) {
            os.write("[MAIN]".getBytes(StandardCharsets.UTF_8));
        }

        // The file lands in storage, not in assets.
        assertTrue(Files.exists(storageDir.resolve("game.ini")));
        assertFalse(Files.exists(assetsDir.resolve("game.ini")));

        // And subsequent reads via VFS see it (through storage layer).
        assertEquals("[MAIN]", readAll(vfs.openRead("game.ini")));
    }
}
