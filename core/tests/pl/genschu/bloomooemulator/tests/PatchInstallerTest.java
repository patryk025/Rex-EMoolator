package pl.genschu.bloomooemulator.tests;

import com.badlogic.gdx.utils.Json;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import pl.genschu.bloomooemulator.patch.InstalledPatch;
import pl.genschu.bloomooemulator.patch.PatchInstaller;
import pl.genschu.bloomooemulator.patch.PatchManifest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Extraction, filtering and materialisation in {@link PatchInstaller}, driven by
 * synthetic ZIP archives (no network). RAR extraction is validated end-to-end
 * against real archives outside the unit suite.
 */
class PatchInstallerTest {

    @TempDir
    Path tmp;

    private File zip(String name, Map<String, String> entries) throws Exception {
        File file = tmp.resolve(name).toFile();
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(file))) {
            for (Map.Entry<String, String> e : entries.entrySet()) {
                zos.putNextEntry(new ZipEntry(e.getKey()));
                zos.write(e.getValue().getBytes(StandardCharsets.UTF_8));
                zos.closeEntry();
            }
        }
        return file;
    }

    private PatchManifest manifest(String id) {
        PatchManifest m = new PatchManifest();
        m.setId(id);
        return m;
    }

    @Test
    void extractsGameDataDropsBinariesAndWritesManifest() throws Exception {
        Map<String, String> entries = new LinkedHashMap<>();
        entries.put("Dane/Game/Przygoda/Dragon/Dragon.cnv", "script");
        entries.put("Dane/Game.class", "engine class text"); // game data, must be KEPT
        entries.put("wavs/snd_Forest.wav", "audio");
        entries.put("PIKLIB8.dll", "engine binary");           // dropped
        entries.put("Czarodzieje.exe", "engine binary");       // dropped
        entries.put("INSTALL.INI", "installer");               // dropped
        File archive = zip("patch.zip", entries);

        InstalledPatch installed = PatchInstaller.installFromArchive(manifest("czaro-test"), archive, tmp.toFile());

        File files = installed.getFilesDir();
        assertTrue(new File(files, "Dane/Game/Przygoda/Dragon/Dragon.cnv").isFile());
        assertTrue(new File(files, "Dane/Game.class").isFile(), ".class is game data and must be kept");
        assertTrue(new File(files, "wavs/snd_Forest.wav").isFile());
        assertFalse(new File(files, "PIKLIB8.dll").exists(), "engine DLL must be stripped");
        assertFalse(new File(files, "Czarodzieje.exe").exists(), "engine exe must be stripped");
        assertFalse(new File(files, "INSTALL.INI").exists(), "installer ini must be stripped");

        File manifestFile = new File(installed.getRootDir(), "patch.json");
        assertTrue(manifestFile.isFile());
        PatchManifest reparsed = new Json().fromJson(PatchManifest.class, Files.readString(manifestFile.toPath()));
        assertEquals("czaro-test", reparsed.getId());
    }

    @Test
    void archiveRootStripsWrapperFolder() throws Exception {
        Map<String, String> entries = new LinkedHashMap<>();
        entries.put("Reksio Piraci/Dane/music.cnv", "score");
        entries.put("Reksio Piraci/wavs/track1.wav", "audio");
        entries.put("readme.txt", "outside the wrapper");
        File archive = zip("soundtrack.zip", entries);

        PatchManifest m = manifest("skarb-soundtrack");
        m.setArchiveRoot("Reksio Piraci");
        InstalledPatch installed = PatchInstaller.installFromArchive(m, archive, tmp.toFile());

        File files = installed.getFilesDir();
        assertTrue(new File(files, "Dane/music.cnv").isFile(), "wrapper folder must be stripped");
        assertTrue(new File(files, "wavs/track1.wav").isFile());
        assertFalse(new File(files, "Reksio Piraci/Dane/music.cnv").exists(), "no double nesting");
        assertFalse(new File(files, "readme.txt").exists(), "entries outside archiveRoot are ignored");
    }

    @Test
    void reinstallReplacesPreviousOverlay() throws Exception {
        File first = zip("first.zip", Map.of("Dane/old.cnv", "old"));
        PatchInstaller.installFromArchive(manifest("p"), first, tmp.toFile());

        File second = zip("second.zip", Map.of("Dane/new.cnv", "new"));
        InstalledPatch installed = PatchInstaller.installFromArchive(manifest("p"), second, tmp.toFile());

        assertFalse(new File(installed.getFilesDir(), "Dane/old.cnv").exists(), "stale file should be gone");
        assertTrue(new File(installed.getFilesDir(), "Dane/new.cnv").isFile());
    }

    @Test
    void rejectsZipSlipTraversal() throws Exception {
        File evil = zip("evil.zip", Map.of("../escape.txt", "pwned"));

        assertThrows(java.io.IOException.class,
                () -> PatchInstaller.installFromArchive(manifest("evil"), evil, tmp.toFile()));
        assertFalse(tmp.resolve("escape.txt").toFile().exists());
    }

    @Test
    void rejectsUnknownArchiveFormat() throws Exception {
        File bogus = tmp.resolve("not-an-archive.bin").toFile();
        try (OutputStream os = new FileOutputStream(bogus)) {
            os.write("definitely not a zip or rar".getBytes(StandardCharsets.UTF_8));
        }

        assertThrows(java.io.IOException.class,
                () -> PatchInstaller.installFromArchive(manifest("x"), bogus, tmp.toFile()));
    }

    @Test
    void rejectsManifestWithoutId() throws Exception {
        File archive = zip("p.zip", Map.of("Dane/x.cnv", "x"));

        assertThrows(java.io.IOException.class,
                () -> PatchInstaller.installFromArchive(new PatchManifest(), archive, tmp.toFile()));
    }
}
