package pl.genschu.bloomooemulator.tests;

import com.badlogic.gdx.utils.Json;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import pl.genschu.bloomooemulator.engine.filesystem.*;
import pl.genschu.bloomooemulator.logic.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.zip.*;

import static org.junit.jupiter.api.Assertions.*;

class GameSourceScannerTest {
    @TempDir Path temp;

    private void game(Path root, String title) throws IOException {
        Files.createDirectories(root.resolve("DANE"));
        Files.writeString(root.resolve("DANE/Application.def"), title);
        Files.writeString(root.resolve("BlooMoo.ini"), "[MAIN]\nTITLE=" + title + "\nINI=" + title + ".ini\n");
        Files.writeString(root.resolve(title + ".ini"), "[GAME]\nNAME=" + title);
        Files.writeString(root.resolve("PIKLIB8.dll"), title);
    }

    private GameEntry entry(String source, String root) {
        return new GameEntry("Test", source, root, "touch", false, false, true);
    }

    @Test void findsIndependentGamesAndIgnoresInstallerDlls() throws Exception {
        game(temp.resolve("Collection/First"), "Herkules");
        game(temp.resolve("Collection/Second"), "Odyseusz");
        Files.createDirectories(temp.resolve("DirectX"));
        Files.writeString(temp.resolve("DirectX/PIKLIB8.dll"), "installer");
        var found = GameSourceScanner.scan(temp.toString(), "");
        assertEquals(List.of("Herkules", "Odyseusz"), found.stream().map(GameSourceScanner.Candidate::name).toList());
        assertEquals(List.of("Collection/First", "Collection/Second"), found.stream().map(GameSourceScanner.Candidate::rootPath).toList());
        var first = entry(temp.toString(), found.get(0).rootPath());
        var second = entry(temp.toString(), found.get(1).rootPath());
        assertEquals("herkules.ini", first.getIniPath().toLowerCase(Locale.ROOT));
        assertEquals("odyseusz.ini", second.getIniPath().toLowerCase(Locale.ROOT));
        assertNotEquals(first.getDllHash(), second.getDllHash());
        assertNotEquals(first.getStorageId(), second.getStorageId());
        try (var data = first.openAssets().open("dane/application.def")) {
            assertEquals("Herkules", new String(data.readAllBytes(), StandardCharsets.UTF_8));
        }
    }

    @Test void singleRootGameAndLegacyJsonKeepWorking() throws Exception {
        game(temp, "Single");
        assertEquals("", GameSourceScanner.scan(temp.toString(), "").get(0).rootPath());
        GameEntry legacy = new Json().fromJson(GameEntry.class, "{}");
        assertEquals("", legacy.getRootPath());
        legacy.setSource(temp.toString(), "");
        assertTrue(legacy.openAssets().exists("dane/application.def"));
    }

    @Test void sourceChangeRefreshesMetadataAndRoundTripsRoot() throws Exception {
        game(temp.resolve("First"), "Herkules");
        game(temp.resolve("Second"), "Odyseusz");
        var original = entry(temp.toString(), "First");
        String storage = original.getStorageId();
        String oldHash = original.getDllHash();
        original.setSource(temp.toString(), "Second");
        assertNotEquals(oldHash, original.getDllHash());
        assertEquals("odyseusz.ini", original.getIniPath().toLowerCase(Locale.ROOT));
        var restored = new Json().fromJson(GameEntry.class, new Json().toJson(original));
        assertEquals("Second", restored.getRootPath());
        assertEquals(storage, restored.getStorageId());
        assertTrue(restored.openAssets().exists("Odyseusz.ini"));
        restored.setIniPath(null);
        restored.setDllHash(null);
        assertTrue(restored.ensureIniPath());
        assertTrue(restored.ensureDllHash());
        assertEquals(original.getDllHash(), restored.getDllHash());
    }

    @Test void zipUsesTheSameDiscoveryAndRelativeFileAccess() throws Exception {
        Path folder = temp.resolve("files");
        game(folder.resolve("First"), "Herkules");
        game(folder.resolve("Second"), "Odyseusz");
        Path zip = temp.resolve("collection.zip");
        try (var out = new ZipOutputStream(Files.newOutputStream(zip)); var paths = Files.walk(folder)) {
            for (Path file : paths.filter(Files::isRegularFile).toList()) {
                out.putNextEntry(new ZipEntry(folder.relativize(file).toString().replace('\\', '/')));
                Files.copy(file, out);
                out.closeEntry();
            }
        }
        assertEquals(2, GameSourceScanner.scan(zip.toString(), "").size());
        var fs = entry(zip.toString(), "first").openAssets();
        assertTrue(fs.exists("DANE/Application.def"));
        assertFalse(fs.exists("Second/Odyseusz.ini"));
        assertEquals(8, fs.length("dane/application.def"));
        try (var reader = fs.openSource("dane/application.def").openReader()) {
            assertNotNull(reader);
        }
        assertThrows(IllegalArgumentException.class, () -> fs.open("../Second/Odyseusz.ini"));
    }

    @Test void manualRootCanReachGamesBeyondAutomaticDepthLimit() throws Exception {
        game(temp.resolve("a/b/c/d/e"), "Deep");
        assertTrue(GameSourceScanner.scan(temp.toString(), "").isEmpty());
        assertEquals("a/b/c/d/e", GameSourceScanner.scan(temp.toString(), "a/b/c/d/e").get(0).rootPath());
        assertThrows(IOException.class, () -> GameSourceScanner.scan(temp.toString(), "../outside"));
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "REX_TEST_MULTI_GAME_SOURCE", matches = ".+")
    void verifiesRealMultiGameDiscWhenProvided() throws Exception {
        String source = System.getenv("REX_TEST_MULTI_GAME_SOURCE");
        var found = GameSourceScanner.scan(source, "");
        assertEquals(List.of("Herkules", "Odyseusz"), found.stream().map(GameSourceScanner.Candidate::name).toList());
        for (var candidate : found) {
            var game = entry(source, candidate.rootPath());
            assertEquals("Piklib v8", game.getVersion());
            assertNotNull(game.getDllHash());
            assertEquals("Poznaj Mity: " + candidate.name(), game.getGameName());
            assertEquals("poznaj-mity-" + candidate.name().toLowerCase(Locale.ROOT), game.resolveFamily());
            assertTrue(game.openAssets().exists(game.getIniPath()));
            try (var input = game.openAssets().open("dane/application.def")) {
                assertTrue(input.readAllBytes().length > 0);
            }
        }
    }
}
