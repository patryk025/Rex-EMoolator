package pl.genschu.bloomooemulator.tests;

import org.junit.jupiter.api.Test;
import pl.genschu.bloomooemulator.engine.filesystem.AssetSourceDispatcher;
import pl.genschu.bloomooemulator.engine.filesystem.IsoFileSystem;
import pl.genschu.bloomooemulator.engine.filesystem.VFS;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class IsoFileSystemTest {

    private static File isoFixture() {
        for (Path candidate : new Path[] {
                Path.of("../assets/test-assets/BFMoo.iso"),
                Path.of("assets/test-assets/BFMoo.iso")
        }) {
            Path absolute = candidate.toAbsolutePath().normalize();
            if (Files.isRegularFile(absolute)) {
                return absolute.toFile();
            }
        }

        throw new AssertionError("Missing ISO fixture: assets/test-assets/BFMoo.iso");
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
    void indexesKnownIsoEntriesCaseInsensitively() {
        IsoFileSystem fs = new IsoFileSystem(isoFixture());

        assertTrue(fs.exists("README.md"));
        assertTrue(fs.exists("readme.pl.md"));
        assertTrue(fs.exists("DANE\\APPLICATION.DEF"));
        assertTrue(fs.exists("COMMON/CLASSES/CLSBF.CLASS"));

        assertTrue(fs.isDirectory("COMMON"));
        assertTrue(fs.isDirectory("dane/game/bf"));
        assertFalse(fs.isDirectory("dane/application.def"));

        Set<String> rootNames = lowerCaseSet(fs.list(""));
        assertTrue(rootNames.containsAll(Set.of(
                "common",
                "dane",
                "readme.md",
                "readme.pl.md",
                "nemo.ini",
                "bloomoo.ini"
        )));

        Set<String> classNames = lowerCaseSet(fs.list("COMMON/CLASSES"));
        assertEquals(Set.of("clsbf.class", "clsscreen.class", "clsstack.class"), classNames);
    }

    @Test
    void readsKnownFilesFromIsoAndReportsTheirLength() throws IOException {
        IsoFileSystem fs = new IsoFileSystem(isoFixture());

        byte[] applicationDefBytes;
        try (InputStream is = fs.open("dane/application.def")) {
            applicationDefBytes = is.readAllBytes();
        }

        String applicationDef = new String(applicationDefBytes, StandardCharsets.UTF_8);
        assertTrue(applicationDef.contains("OBJECT=GAME"));
        assertTrue(applicationDef.contains("BF:PATH=GAME\\BF"));
        assertEquals(applicationDefBytes.length, fs.length("DANE/APPLICATION.DEF"));

        String readme = readAll(fs.open("README.md"));
        assertTrue(readme.contains("Brainfuck Snake"));
    }

    @Test
    void vfsCanMountIsoAndReadScriptsThroughDispatcher() throws IOException {
        VFS vfs = new VFS();
        vfs.mountAssets(AssetSourceDispatcher.openAssets(isoFixture()));

        assertTrue(vfs.exists("dane/game/bf/bf.cnv"));
        assertTrue(vfs.exists("COMMON\\CLASSES\\CLSBF.CLASS"));
        assertTrue(vfs.isDirectory("dane/game/bf/snake"));

        Set<String> episodeNames = lowerCaseSet(vfs.list("DANE/GAME/BF"));
        assertTrue(episodeNames.containsAll(Set.of("bf.cnv", "snake")));

        String script = readAll(vfs.openRead("dane/game/bf/bf.cnv"));
        assertTrue(script.contains("CLSBFOBJ:DEF=CLSBF.CLASS"));
        assertEquals(script.getBytes(StandardCharsets.UTF_8).length, vfs.length("DANE/GAME/BF/BF.CNV"));
    }
}
