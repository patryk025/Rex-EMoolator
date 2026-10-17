package pl.genschu.bloomooemulator.tests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import pl.genschu.bloomooemulator.engine.filesystem.LocalFileSystem;
import pl.genschu.bloomooemulator.engine.filesystem.MemoryDataSource;
import pl.genschu.bloomooemulator.engine.filesystem.SndArchiveFileSystem;
import pl.genschu.bloomooemulator.engine.filesystem.VFS;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Layout of {@code wavs/wav.snd} as read by the Reksio i Kapitan Nemo installer
 * (setup.exe FUN_0040357f): repeated {u32 type, u32 size, u32 nameLen, name\0, payload}.
 */
class SndArchiveFileSystemTest {
    private static final String FIXTURE = "snd/tone440_8k_mono.ogg";
    /** 2048 decoded mono samples behind the 56-byte installer header. */
    private static final long FIXTURE_WAVE_LENGTH = 56 + 2048 * 2;

    @TempDir
    Path root;

    @Test
    void exposesRecordsAsWaveFilesNextToTheArchive() throws IOException {
        byte[] ogg = fixture();
        writeArchive("wavs/wav.snd", record(0, "Tone_W001.wav", ogg), record(0, "Other.wav", ogg));
        LocalFileSystem base = new LocalFileSystem(root.toFile());

        List<SndArchiveFileSystem> archives = SndArchiveFileSystem.discover(base);

        assertEquals(1, archives.size());
        SndArchiveFileSystem archive = archives.get(0);
        assertArrayEquals(new String[] {"Other.wav", "Tone_W001.wav"}, archive.list("wavs"));
        assertArrayEquals(new String[] {"wavs"}, archive.list(""));
        assertTrue(archive.exists("WAVS\\tone_w001.WAV"));
        assertTrue(archive.isDirectory("wavs"));
        assertFalse(archive.exists("tone_w001.wav"));

        byte[] wave = readAll(archive, "wavs/Tone_W001.wav");
        assertEquals(FIXTURE_WAVE_LENGTH, wave.length);
        assertEquals(FIXTURE_WAVE_LENGTH, archive.length("wavs/tone_w001.wav"));
        assertEquals("RIFF", new String(wave, 0, 4, StandardCharsets.US_ASCII));
        assertEquals("fact", new String(wave, 36, 4, StandardCharsets.US_ASCII));
        assertEquals("data", new String(wave, 48, 4, StandardCharsets.US_ASCII));
        assertEquals(8000, le32(wave, 24));
    }

    @Test
    void lengthIsKnownBeforeTheFirstOpen() throws IOException {
        writeArchive("wavs/wav.snd", record(0, "a.wav", fixture()));
        SndArchiveFileSystem archive = SndArchiveFileSystem.discover(new LocalFileSystem(root.toFile())).get(0);

        assertEquals(FIXTURE_WAVE_LENGTH, archive.length("wavs/a.wav"));
        assertEquals(FIXTURE_WAVE_LENGTH, readAll(archive, "wavs/a.wav").length);
    }

    @Test
    void realFilesOnTheGameSourceKeepPriority() throws IOException {
        writeArchive("wavs/wav.snd", record(0, "dup.wav", fixture()), record(0, "only.wav", fixture()));
        Files.write(root.resolve("wavs/dup.wav"), new byte[] {1, 2, 3});
        LocalFileSystem base = new LocalFileSystem(root.toFile());
        VFS vfs = new VFS();
        for (SndArchiveFileSystem archive : SndArchiveFileSystem.discover(base)) {
            vfs.mountAssets(archive);
        }
        vfs.mountAssets(base);

        try (InputStream in = vfs.openRead("wavs/dup.wav")) {
            assertArrayEquals(new byte[] {1, 2, 3}, in.readAllBytes());
        }
        assertEquals(FIXTURE_WAVE_LENGTH, vfs.length("WAVS/ONLY.WAV"));
        List<String> listed = Arrays.asList(vfs.list("wavs"));
        assertTrue(listed.contains("only.wav"));
        assertTrue(listed.contains("wav.snd"));
    }

    @Test
    void laterRecordWithTheSameNameWins() throws IOException {
        // The installer writes records in order, so a repeated name overwrites the earlier file.
        byte[] ogg = fixture();
        byte[] notOgg = "definitely not ogg".getBytes(StandardCharsets.US_ASCII);
        SndArchiveFileSystem archive = new SndArchiveFileSystem(
                new MemoryDataSource("wav.snd", concat(record(0, "x.wav", notOgg), record(0, "X.WAV", ogg))), "wavs");

        assertEquals(FIXTURE_WAVE_LENGTH, readAll(archive, "wavs/x.wav").length);
    }

    @Test
    void speexRecordsAreSkippedButDoNotBreakTheIndex() throws IOException {
        SndArchiveFileSystem archive = new SndArchiveFileSystem(
                new MemoryDataSource("wav.snd", concat(record(1, "speex.wav", new byte[] {9, 9, 9}),
                        record(0, "vorbis.wav", fixture()))), "");

        assertFalse(archive.exists("speex.wav"));
        assertTrue(archive.exists("vorbis.wav"));
    }

    @Test
    void archiveInTheRootExposesEntriesInTheRoot() throws IOException {
        writeArchive("sounds.SND", record(0, "root.wav", fixture()));

        SndArchiveFileSystem archive = SndArchiveFileSystem.discover(new LocalFileSystem(root.toFile())).get(0);

        assertTrue(archive.exists("root.wav"));
    }

    @Test
    void malformedArchivesAreIgnoredByDiscovery() throws IOException {
        byte[] good = record(0, "a.wav", fixture());
        writeArchive("garbage/wav.snd", "not an archive at all".getBytes(StandardCharsets.US_ASCII));
        writeArchive("truncated/wav.snd", Arrays.copyOf(good, good.length - 1));
        writeArchive("badtype/wav.snd", record(7, "a.wav", fixture()));
        writeArchive("traversal/wav.snd", record(0, "..\\evil.wav", fixture()));

        assertTrue(SndArchiveFileSystem.discover(new LocalFileSystem(root.toFile())).isEmpty());
    }

    @Test
    void recordRunningPastTheEndIsRejected() {
        byte[] good = record(0, "a.wav", new byte[] {1, 2, 3, 4});
        byte[] truncated = Arrays.copyOf(good, good.length - 1);

        assertThrows(IOException.class,
                () -> new SndArchiveFileSystem(new MemoryDataSource("wav.snd", truncated), "wavs"));
    }

    private void writeArchive(String path, byte[]... records) throws IOException {
        Path target = root.resolve(path);
        Files.createDirectories(target.getParent());
        Files.write(target, concat(records));
    }

    private static byte[] record(int type, String name, byte[] payload) {
        byte[] nameBytes = (name + "\0").getBytes(StandardCharsets.US_ASCII);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        writeLe32(out, type);
        writeLe32(out, payload.length);
        writeLe32(out, nameBytes.length);
        out.writeBytes(nameBytes);
        out.writeBytes(payload);
        return out.toByteArray();
    }

    private static byte[] concat(byte[]... parts) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        for (byte[] part : parts) {
            out.writeBytes(part);
        }
        return out.toByteArray();
    }

    private static void writeLe32(ByteArrayOutputStream out, int value) {
        out.write(value);
        out.write(value >> 8);
        out.write(value >> 16);
        out.write(value >> 24);
    }

    private static int le32(byte[] data, int offset) {
        return (data[offset] & 0xFF) | (data[offset + 1] & 0xFF) << 8
                | (data[offset + 2] & 0xFF) << 16 | (data[offset + 3] & 0xFF) << 24;
    }

    private static byte[] readAll(SndArchiveFileSystem archive, String path) throws IOException {
        try (InputStream in = archive.open(path)) {
            return in.readAllBytes();
        }
    }

    private static byte[] fixture() throws IOException {
        for (Path candidate : new Path[] {
                Path.of("../assets/test-assets/" + FIXTURE),
                Path.of("assets/test-assets/" + FIXTURE)
        }) {
            if (Files.isRegularFile(candidate)) {
                return Files.readAllBytes(candidate);
            }
        }
        throw new AssertionError("Missing fixture: assets/test-assets/" + FIXTURE);
    }
}
