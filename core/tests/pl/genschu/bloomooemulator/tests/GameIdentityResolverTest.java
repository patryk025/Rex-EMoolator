package pl.genschu.bloomooemulator.tests;

import com.badlogic.gdx.utils.Json;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import pl.genschu.bloomooemulator.engine.filesystem.LocalFileSystem;
import pl.genschu.bloomooemulator.logic.*;
import pl.genschu.bloomooemulator.patch.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

class GameIdentityResolverTest {
    private static final String HASH = GameIdentityResolver.HERKULES_ODYSEUSZ_HASH;
    private static final String HERKULES = "Poznaj Mity: Herkules";
    private static final String ODYSEUSZ = "Poznaj Mity: Odyseusz";
    private static final String AMBIGUOUS = "Poznaj Mity: Herkules/Odyseusz";
    @TempDir Path temp;

    private String resolve() {
        return GameIdentityResolver.resolve(HASH.toLowerCase(Locale.ROOT), new LocalFileSystem(temp.toFile()));
    }

    @Test void executableNamesDistinguishIdenticalBinariesWithoutBloomooIni() throws Exception {
        Files.writeString(temp.resolve("HERKULES.EXE"), "same binary");
        assertEquals(HERKULES, resolve());
        Files.move(temp.resolve("HERKULES.EXE"), temp.resolve("Odyseusz.exe"));
        Files.writeString(temp.resolve("Herkules.ini"), "leftover configuration");
        assertEquals(ODYSEUSZ, resolve());
    }

    @Test void missingOrMultipleExecutablesDoNotGuessFromLooseIniOrDirectoryName() throws Exception {
        Files.writeString(temp.resolve("Herkules.ini"), "[MAIN]\nTITLE=Herkules");
        Files.createDirectories(temp.resolve("herkules.exe"));
        assertEquals(AMBIGUOUS, resolve());
        Files.delete(temp.resolve("herkules.exe"));
        Files.writeString(temp.resolve("Herkules.exe"), "same binary");
        Files.writeString(temp.resolve("Odyseusz.exe"), "same binary");
        assertEquals(AMBIGUOUS, resolve());
    }

    @Test void explicitExistingMainIniBreaksTieButNotAnUnambiguousExecutable() throws Exception {
        Files.writeString(temp.resolve("Herkules.exe"), "binary");
        Files.writeString(temp.resolve("Odyseusz.exe"), "binary");
        Files.writeString(temp.resolve("BlooMoo.ini"), "[MAIN]\nINI=.\\ODYSEUSZ.INI\n");
        assertEquals(AMBIGUOUS, resolve()); // A dangling INI pointer is insufficient.
        Files.writeString(temp.resolve("Odyseusz.ini"), "[GAME]");
        Files.writeString(temp.resolve("Herkules.ini"), "[GAME]");
        assertEquals(ODYSEUSZ, resolve());
        Files.delete(temp.resolve("Odyseusz.exe"));
        assertEquals(HERKULES, resolve());
        Files.delete(temp.resolve("Herkules.exe"));
        assertEquals(ODYSEUSZ, resolve());
    }

    @Test void invalidConfigurationKeepsAmbiguousIdentity() throws Exception {
        Files.writeString(temp.resolve("BlooMoo.ini"), "not an ini file");
        assertEquals(AMBIGUOUS, resolve());
        Files.writeString(temp.resolve("BlooMoo.ini"), "[MAIN]\nINI=../Herkules.ini\n");
        assertEquals(AMBIGUOUS, resolve());
    }

    @Test void filenamesCannotOverrideOtherOrUnknownDllHashes() throws Exception {
        Files.writeString(temp.resolve("Herkules.exe"), "binary");
        var fs = new LocalFileSystem(temp.toFile());
        String otherHash = "A70F97E970C6301166E90E8872F499DA59961464";
        assertEquals(KnownHashes.checkHash(otherHash), GameIdentityResolver.resolve(otherHash, fs));
        assertEquals("Nieznana gra", GameIdentityResolver.resolve("unknown", fs));
        assertEquals("Nieznana gra", GameIdentityResolver.resolve(null, fs));
    }

    @Test void libraryLoadMigratesCachedIdentityAndPersistsWithoutChangingUserData() throws Exception {
        Path assets = temp.resolve("assets");
        Files.createDirectories(assets.resolve("Odyseusz"));
        Files.writeString(assets.resolve("Herkules.exe"), "outside selected root");
        Files.writeString(assets.resolve("Odyseusz/ODYSEUSZ.EXE"), "binary");
        GameEntry old = new GameEntry("My custom name", assets.toString(), "Odyseusz", "touch", false, false, true);
        old.setDllHash(HASH);
        old.setGameName(AMBIGUOUS);
        old.setFamilyOverride("custom-patches");
        String storageId = old.getStorageId();
        Path library = temp.resolve("library");
        Files.createDirectories(library);
        Files.writeString(library.resolve("games.json"), new Json().toJson(new GameEntry[]{old}));
        var manager = new GameManager(library.toString());
        var migrated = manager.getGames().first();
        assertEquals(ODYSEUSZ, migrated.getGameName());
        assertEquals("My custom name", migrated.getName());
        assertEquals("custom-patches", migrated.resolveFamily());
        assertEquals(storageId, migrated.getStorageId());
        assertEquals(HASH, migrated.getDllHash());
        assertFalse(migrated.ensureGameIdentity());
        var persisted = new Json().fromJson(GameEntry[].class, Files.readString(library.resolve("games.json")))[0];
        assertEquals(ODYSEUSZ, persisted.getGameName());
        assertEquals("Odyseusz", persisted.getRootPath());
    }

    @Test void missingMediaDoesNotOverwriteCachedMetadata() {
        var game = new GameEntry("Custom", temp.resolve("missing.iso").toString(), "touch", false, false, true);
        game.setDllHash(HASH);
        game.setGameName(AMBIGUOUS);
        assertFalse(game.ensureGameIdentity());
        assertEquals(AMBIGUOUS, game.getGameName());
    }

    @Test void resolvedGamesHaveDistinctFamiliesIncludingModifiedDllFallback() {
        assertEquals(GameFamilies.POZNAJ_MITY_HERKULES, GameFamilies.familyFor(HASH, HERKULES));
        assertEquals(GameFamilies.POZNAJ_MITY_ODYSEUSZ, GameFamilies.familyFor(HASH, ODYSEUSZ));
        assertEquals("poznaj-mity-herkules-odyseusz", GameFamilies.familyFor(HASH, AMBIGUOUS));
        assertEquals(GameFamilies.POZNAJ_MITY_HERKULES, GameFamilies.familyFor("modified", HERKULES));
        assertEquals(GameFamilies.POZNAJ_MITY_ODYSEUSZ, GameFamilies.familyFor("modified", ODYSEUSZ));
    }

    @Test void titleSpecificPatchCannotUseSharedHashToMatchOtherOrUnresolvedGame() {
        var patch = new PatchManifest();
        patch.setTargetHashes(new String[]{HASH});
        for (String target : new String[]{GameFamilies.POZNAJ_MITY_HERKULES, GameFamilies.POZNAJ_MITY_ODYSEUSZ}) {
            patch.setTargetFamily(target);
            assertEquals(PatchCompatibility.EXACT, patch.compatibilityFor(HASH, target));
            String other = target.equals(GameFamilies.POZNAJ_MITY_HERKULES)
                    ? GameFamilies.POZNAJ_MITY_ODYSEUSZ : GameFamilies.POZNAJ_MITY_HERKULES;
            assertEquals(PatchCompatibility.NONE, patch.compatibilityFor(HASH, other));
            assertEquals(PatchCompatibility.NONE, patch.compatibilityFor(HASH, null));
            assertEquals(PatchCompatibility.NONE, patch.compatibilityFor(HASH, "poznaj-mity-herkules-odyseusz"));
            assertEquals(PatchCompatibility.FAMILY, patch.compatibilityFor("modified", target));
        }
        patch.setTargetFamily("poznaj-mity-herkules-odyseusz");
        assertEquals(PatchCompatibility.EXACT, patch.compatibilityFor(HASH, GameFamilies.POZNAJ_MITY_HERKULES));
        patch.setTargetFamily(null);
        assertEquals(PatchCompatibility.EXACT, patch.compatibilityFor(HASH, GameFamilies.POZNAJ_MITY_ODYSEUSZ));
    }
}
