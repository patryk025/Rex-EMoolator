package pl.genschu.bloomooemulator.tests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import pl.genschu.bloomooemulator.logic.AppPaths;
import pl.genschu.bloomooemulator.logic.GameEntry;
import pl.genschu.bloomooemulator.logic.GameManager;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GameStorageTest {

    @Test
    void gameEntryCreatesStableUuidStorageId() {
        GameEntry entry = new GameEntry("test", "missing.iso", "Dotykowo", false, false, true);
        String storageId = entry.getStorageId();

        assertDoesNotThrow(() -> UUID.fromString(storageId));
        assertEquals(storageId, entry.getStorageId());
    }

    @Test
    void gameManagerMigratesEntriesWithoutStorageId(@TempDir Path tempDir) throws Exception {
        Path gamesJson = tempDir.resolve("games.json");
        Files.writeString(gamesJson, "[{\"id\":0,\"name\":\"Legacy\",\"path\":\"game.iso\"}]");

        GameManager manager = new GameManager(tempDir.toString());
        GameEntry entry = manager.getGames().first();

        assertDoesNotThrow(() -> UUID.fromString(entry.getStorageId()));
        assertTrue(Files.readString(gamesJson).contains("storageId"));
    }

    @Test
    void storageDirectoryUsesGameEntryStorageId() {
        GameEntry entry = new GameEntry("test", "same-name.iso", "Dotykowo", false, false, true);

        assertEquals(entry.getStorageId(), AppPaths.storageDirFor(entry).getName());
        assertEquals("storage", AppPaths.storageDirFor(entry).getParentFile().getName());
    }
}
