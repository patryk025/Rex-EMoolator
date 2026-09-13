package pl.genschu.bloomooemulator.tests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import pl.genschu.bloomooemulator.engine.filesystem.LocalFileSystem;
import pl.genschu.bloomooemulator.logic.GameIniResolver;
import org.junit.jupiter.api.io.TempDir;
import pl.genschu.bloomooemulator.engine.filesystem.AssetSourceDispatcher;
import pl.genschu.bloomooemulator.engine.filesystem.VFS;
import pl.genschu.bloomooemulator.engine.filesystem.ZipFileSystem;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.junit.jupiter.api.Assertions.*;

class ZipFileSystemTest {

    private static Path createZip(Path path) throws IOException {
        try (ZipOutputStream zip = new ZipOutputStream(Files.newOutputStream(path))) {
            addFile(zip, "DANE/APPLICATION.DEF", "OBJECT=GAME");
            addFile(zip, "DANE/Game/BF/BF.CNV", "CLSBFOBJ:DEF=CLSBF.CLASS");
            addFile(zip, "COMMON/CLASSES/CLSBF.CLASS", "class-data");
            addFile(zip, "README.md", "Brainfuck Snake");
        }
        return path;
    }

    private static Path createZipWithLargeCompressedFile(Path path, byte[] content) throws IOException {
        try (ZipOutputStream zip = new ZipOutputStream(Files.newOutputStream(path))) {
            ZipEntry entry = new ZipEntry("DANE/GFX/IMAGE.PIK");
            zip.putNextEntry(entry);
            zip.write(content);
            zip.closeEntry();
        }
        return path;
    }

    private static void addFile(ZipOutputStream zip, String path, String content) throws IOException {
        ZipEntry entry = new ZipEntry(path);
        byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
        zip.putNextEntry(entry);
        zip.write(bytes);
        zip.closeEntry();
    }

    private static String readAll(InputStream is) throws IOException {
        try (is) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private static Set<String> lowerCaseSet(String[] names) {
        assertNotNull(names);
        return Arrays.stream(names)
                .map(name -> name.toLowerCase(Locale.ROOT))
                .collect(Collectors.toSet());
    }

    @Test
    void indexesEntriesCaseInsensitively(@TempDir Path tempDir) throws IOException {
        ZipFileSystem fs = new ZipFileSystem(createZip(tempDir.resolve("game.zip")).toFile());

        assertTrue(fs.exists("dane/application.def"));
        assertTrue(fs.exists("DANE\\GAME\\BF\\BF.CNV"));
        assertTrue(fs.exists("common/classes/clsbf.class"));

        assertTrue(fs.isDirectory("DANE"));
        assertTrue(fs.isDirectory("dane/game/bf"));
        assertFalse(fs.isDirectory("dane/application.def"));

        Set<String> rootNames = lowerCaseSet(fs.list(""));
        assertEquals(Set.of("common", "dane", "readme.md"), rootNames);

        Set<String> classNames = lowerCaseSet(fs.list("COMMON/CLASSES"));
        assertEquals(Set.of("clsbf.class"), classNames);
    }

    @Test
    void readsFilesAndReportsTheirLength(@TempDir Path tempDir) throws IOException {
        ZipFileSystem fs = new ZipFileSystem(createZip(tempDir.resolve("game.zip")).toFile());

        String applicationDef = readAll(fs.open("DANE/APPLICATION.DEF"));
        assertEquals("OBJECT=GAME", applicationDef);
        assertEquals(applicationDef.getBytes(StandardCharsets.UTF_8).length, fs.length("dane/application.def"));
        assertEquals(0, fs.length("DANE"));
    }

    @Test
    void singleBufferReadFillsRequestedBytesUntilEof(@TempDir Path tempDir) throws IOException {
        byte[] expected = new byte[128 * 1024];
        for (int i = 0; i < expected.length; i++) {
            expected[i] = (byte) (i % 251);
        }

        ZipFileSystem fs = new ZipFileSystem(createZipWithLargeCompressedFile(tempDir.resolve("game.zip"), expected).toFile());

        byte[] actual = new byte[expected.length];
        try (InputStream is = fs.open("dane/gfx/image.pik")) {
            assertEquals(expected.length, is.read(actual));
            assertEquals(-1, is.read());
        }
        assertArrayEquals(expected, actual);
    }

    @Test
    void vfsCanMountZipThroughDispatcher(@TempDir Path tempDir) throws IOException {
        VFS vfs = new VFS();
        vfs.mountAssets(AssetSourceDispatcher.openAssets(createZip(tempDir.resolve("game.zip")).toFile()));

        assertTrue(vfs.exists("dane/game/bf/bf.cnv"));
        assertTrue(vfs.exists("COMMON\\CLASSES\\CLSBF.CLASS"));
        assertTrue(vfs.isDirectory("dane/game/bf"));

        Set<String> episodeNames = lowerCaseSet(vfs.list("DANE/GAME/BF"));
        assertEquals(Set.of("bf.cnv"), episodeNames);

        String script = readAll(vfs.openRead("dane/game/bf/bf.cnv"));
        assertTrue(script.contains("CLSBFOBJ:DEF=CLSBF.CLASS"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"../outside.ini", "..\\outside.ini", "data/../../outside.ini",
            "data/../outside.ini", "/outside.ini", "\\outside.ini", "C:/outside.ini",
            "C:outside.ini", "//server/share/outside.ini", "../", "data/../../"})
    void rejectsUnsafeArchiveEntriesBeforeExposingFiles(String name, @TempDir Path tempDir) throws IOException {
        Path archive = tempDir.resolve("unsafe.zip");
        try (ZipOutputStream zip = new ZipOutputStream(Files.newOutputStream(archive))) {
            addFile(zip, "safe.ini", "safe");
            addFile(zip, name, "payload");
        }
        ZipFileSystem fs = new ZipFileSystem(archive.toFile());
        assertThrows(IOException.class, () -> fs.open("safe.ini"));
        // A failed index must not expose the safe prefix on subsequent calls either.
        assertThrows(IllegalStateException.class, () -> fs.list(""));
        assertThrows(IOException.class, () -> fs.open(name));
    }

    @Test
    void acceptsOrdinaryWindowsSeparatorsAndDirectoryEntries(@TempDir Path tempDir) throws IOException {
        Path archive = tempDir.resolve("safe.zip");
        try (ZipOutputStream zip = new ZipOutputStream(Files.newOutputStream(archive))) {
            addFile(zip, "DANE/", "");
            addFile(zip, "DANE\\GAME.INI", "safe");
        }
        ZipFileSystem fs = new ZipFileSystem(archive.toFile());
        assertTrue(fs.isDirectory("dane"));
        assertEquals("safe", readAll(fs.open("dane/game.ini")));
    }

    @ParameterizedTest
    @ValueSource(strings = {"../outside.ini", "..\\outside.ini", "../new/outside.ini"})
    void iniReferenceInsideZipCannotWriteOutsideStorage(String reference, @TempDir Path tempDir) throws IOException {
        Path victim = Files.writeString(tempDir.resolve("outside.ini"), "untouched");
        Path archive = tempDir.resolve("game.zip");
        try (ZipOutputStream zip = new ZipOutputStream(Files.newOutputStream(archive))) {
            addFile(zip, "bloomoo.ini", "[MAIN]\nINI=" + reference.replace("\\", "\\\\") + "\n");
        }
        ZipFileSystem assets = new ZipFileSystem(archive.toFile());
        String iniPath = GameIniResolver.resolve(assets::exists, assets.list(""),
                GameIniResolver.boundedReader(assets::length, assets::open));
        assertNotNull(iniPath);
        assertEquals(reference.replace('\\', '/'), iniPath);
        VFS vfs = new VFS();
        vfs.mountAssets(assets);
        vfs.setStorage(new LocalFileSystem(Files.createDirectory(tempDir.resolve("storage")).toFile()));
        // Same write boundary used by Game.persistGameINI, without launching the renderer.
        assertThrows(SecurityException.class, () -> {
            try (var out = vfs.openWrite(iniPath)) {
                out.write("overwritten".getBytes(StandardCharsets.UTF_8));
            }
        });
        assertEquals("untouched", Files.readString(victim));
        assertFalse(Files.exists(tempDir.resolve("new")));
    }
}
