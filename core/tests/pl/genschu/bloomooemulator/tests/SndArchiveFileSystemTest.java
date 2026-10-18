package pl.genschu.bloomooemulator.tests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import pl.genschu.bloomooemulator.engine.filesystem.DataSource;
import pl.genschu.bloomooemulator.engine.filesystem.IFileSystem;
import pl.genschu.bloomooemulator.engine.filesystem.LocalFileSystem;
import pl.genschu.bloomooemulator.engine.filesystem.SndArchiveFileSystem;
import pl.genschu.bloomooemulator.engine.filesystem.VFS;
import pl.genschu.bloomooemulator.engine.filesystem.ZipFileSystem;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Layout of {@code wavs/wav.snd} as read by the Reksio i Kapitan Nemo installer
 * (setup.exe FUN_0040357f): repeated {u32 type, u32 size, u32 nameLen, name\0, payload}.
 * Archives are found through {@code install.ini [std]} like the installer does
 * (FUN_0040306c / FUN_0040291b).
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
        writeFile("wavs/wav.snd", record(0, "Tone_W001.wav", ogg), record(0, "Other.wav", ogg));
        writeInstallIni("am.url", "wavs\\wav.snd");

        List<SndArchiveFileSystem> archives = SndArchiveFileSystem.discover(local());

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
        assertEquals(FIXTURE_WAVE_LENGTH - 8, le32(wave, 4));
        assertEquals("fact", new String(wave, 36, 4, StandardCharsets.US_ASCII));
        assertEquals("data", new String(wave, 48, 4, StandardCharsets.US_ASCII));
        assertEquals(FIXTURE_WAVE_LENGTH - 56, le32(wave, 52));
        assertEquals(8000, le32(wave, 24));
    }

    @Test
    void nothingIsMountedWithoutInstallIni() throws IOException {
        writeFile("wavs/wav.snd", record(0, "a.wav", fixture()));

        assertTrue(SndArchiveFileSystem.discover(local()).isEmpty());
    }

    @Test
    void onlyStdListEntriesContainingSndAreArchives() throws IOException {
        // [min]/[max] are ignored: setup.exe preselects "std" (FUN_00408ffa).
        // Entries are lower-cased before the ".snd" test, '\'-terminated ones are
        // directories, and entries past "files" are never read.
        writeFile("install.ini", ("""
                [min]
                files = 1
                0 = other\\min.snd

                [std]
                files	= 4
                0	= Nemo.exe
                1	= SOUNDS\\
                2	= WAVS\\WAV.SND
                3	= extra\\voice.snd;Polish
                4	= late\\never.snd
                """).getBytes(StandardCharsets.US_ASCII));
        byte[] archive = record(0, "a.wav", fixture());
        for (String path : List.of("other/min.snd", "wavs/wav.snd", "extra/voice.snd", "late/never.snd")) {
            writeFile(path, archive);
        }

        List<SndArchiveFileSystem> archives = SndArchiveFileSystem.discover(local());

        assertEquals(2, archives.size());
        assertTrue(archives.get(0).exists("wavs/a.wav"));
        assertTrue(archives.get(1).exists("extra/a.wav"));
    }

    @Test
    void lengthIsKnownBeforeTheFirstOpen() throws IOException {
        writeFile("wavs/wav.snd", record(0, "a.wav", fixture()));
        SndArchiveFileSystem archive = new SndArchiveFileSystem(local(), "wavs/wav.snd");

        assertEquals(FIXTURE_WAVE_LENGTH, archive.length("wavs/a.wav"));
        assertEquals(FIXTURE_WAVE_LENGTH, readAll(archive, "wavs/a.wav").length);
    }

    @Test
    void realFilesOnTheGameSourceKeepPriority() throws IOException {
        writeFile("wavs/wav.snd", record(0, "dup.wav", fixture()), record(0, "only.wav", fixture()));
        writeFile("wavs/dup.wav", new byte[] {1, 2, 3});
        writeInstallIni("wavs\\wav.snd");
        LocalFileSystem base = local();
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
        byte[] notOgg = "definitely not ogg".getBytes(StandardCharsets.US_ASCII);
        writeFile("wavs/wav.snd", record(0, "x.wav", notOgg), record(0, "X.WAV", fixture()));
        SndArchiveFileSystem archive = new SndArchiveFileSystem(local(), "wavs/wav.snd");

        assertEquals(FIXTURE_WAVE_LENGTH, readAll(archive, "wavs/x.wav").length);
    }

    @Test
    void speexRecordsAreSkippedButDoNotBreakTheIndex() throws IOException {
        writeFile("sounds.snd", record(1, "speex.wav", new byte[] {9, 9, 9}), record(0, "vorbis.wav", fixture()));
        SndArchiveFileSystem archive = new SndArchiveFileSystem(local(), "sounds.snd");

        assertFalse(archive.exists("speex.wav"));
        assertTrue(archive.exists("vorbis.wav"));
    }

    @Test
    void malformedArchivesReadAsEmpty() throws IOException {
        byte[] good = record(0, "a.wav", fixture());
        writeFile("garbage/wav.snd", "not an archive at all".getBytes(StandardCharsets.US_ASCII));
        writeFile("truncated/wav.snd", Arrays.copyOf(good, good.length - 1));
        writeFile("badtype/wav.snd", record(7, "a.wav", fixture()));
        writeFile("traversal/wav.snd", record(0, "..\\evil.wav", fixture()));
        LocalFileSystem base = local();

        for (String dir : List.of("garbage", "truncated", "badtype", "traversal")) {
            SndArchiveFileSystem archive = new SndArchiveFileSystem(base, dir + "/wav.snd");
            assertArrayEquals(new String[0], archive.list(dir), dir);
            assertFalse(archive.exists(dir + "/a.wav"), dir);
        }
    }

    @Test
    void undecodableEntryReportsZeroLengthAndFailsToOpen() throws IOException {
        writeFile("wavs/wav.snd", record(0, "bad.wav", "not ogg".getBytes(StandardCharsets.US_ASCII)));
        SndArchiveFileSystem archive = new SndArchiveFileSystem(local(), "wavs/wav.snd");

        assertTrue(archive.exists("wavs/bad.wav"));
        assertEquals(0, archive.length("wavs/bad.wav"));
        assertThrows(IOException.class, () -> archive.open("wavs/bad.wav"));
    }

    @Test
    void inaccessibleListedPathsAreSkippedInsteadOfAbortingDiscovery() throws IOException {
        // LocalFileSystem throws SecurityException for paths escaping its root (symlinks).
        writeFile("wavs/wav.snd", record(0, "a.wav", fixture()));
        writeInstallIni("evil\\wav.snd", "wavs\\wav.snd");
        LocalFileSystem local = local();
        IFileSystem guarded = new DelegatingFileSystem(local) {
            @Override public boolean exists(String path) {
                if (path.startsWith("evil")) throw new SecurityException("Path escapes filesystem root");
                return super.exists(path);
            }
        };

        List<SndArchiveFileSystem> archives = SndArchiveFileSystem.discover(guarded);

        assertEquals(1, archives.size());
        assertTrue(archives.get(0).exists("wavs/a.wav"));
    }

    @Test
    void archiveIsStreamedAndNeverMaterializedThroughOpenSource() throws IOException {
        // On ZIP and fragmented UDF sources openSource() copies the entry into memory.
        writeFile("wavs/wav.snd", record(0, "a.wav", fixture()), record(0, "b.wav", fixture()));
        IFileSystem noSlicing = new DelegatingFileSystem(local()) {
            @Override public DataSource openSource(String path) {
                throw new AssertionError("openSource must not be used for " + path);
            }
        };
        SndArchiveFileSystem archive = new SndArchiveFileSystem(noSlicing, "wavs/wav.snd");

        assertEquals(FIXTURE_WAVE_LENGTH, readAll(archive, "wavs/b.wav").length);
    }

    @Test
    void readsArchiveFromCompressedZip() throws IOException {
        byte[] archiveBytes = concat(record(0, "a.wav", fixture()), record(0, "b.wav", fixture()));
        Path zip = root.resolve("game.zip");
        try (ZipOutputStream out = new ZipOutputStream(Files.newOutputStream(zip))) {
            putZipEntry(out, "install.ini", "[std]\r\nfiles=1\r\n0=wavs\\wav.snd\r\n".getBytes(StandardCharsets.US_ASCII));
            putZipEntry(out, "wavs/wav.snd", archiveBytes);
        }
        ZipFileSystem zipFs = new ZipFileSystem(zip.toFile());

        List<SndArchiveFileSystem> archives = SndArchiveFileSystem.discover(zipFs);

        assertEquals(1, archives.size());
        assertEquals(FIXTURE_WAVE_LENGTH, readAll(archives.get(0), "wavs/b.wav").length);
    }

    @Test
    void concurrentOpensDecodeOnce() throws Exception {
        writeFile("wavs/wav.snd", record(0, "a.wav", fixture()));
        AtomicInteger archiveOpens = new AtomicInteger();
        IFileSystem counting = new DelegatingFileSystem(local()) {
            @Override public InputStream open(String path) throws IOException {
                archiveOpens.incrementAndGet();
                return super.open(path);
            }
        };
        SndArchiveFileSystem archive = new SndArchiveFileSystem(counting, "wavs/wav.snd");
        assertTrue(archive.exists("wavs/a.wav"));
        assertEquals(1, archiveOpens.get(), "index pass");

        ExecutorService pool = Executors.newFixedThreadPool(8);
        try {
            List<Callable<Integer>> tasks = new ArrayList<>();
            for (int i = 0; i < 8; i++) {
                tasks.add(() -> readAll(archive, "wavs/a.wav").length);
            }
            for (Future<Integer> result : pool.invokeAll(tasks)) {
                assertEquals(FIXTURE_WAVE_LENGTH, (long) result.get());
            }
        } finally {
            pool.shutdownNow();
        }
        assertEquals(2, archiveOpens.get(), "index pass + one decode");
    }

    private LocalFileSystem local() {
        return new LocalFileSystem(root.toFile());
    }

    private void writeInstallIni(String... entries) throws IOException {
        StringBuilder ini = new StringBuilder("[Option]\r\nprogramexe_name = Nemo.exe\r\n\r\n[std]\r\n");
        ini.append("files\t= ").append(entries.length).append("\r\n");
        for (int i = 0; i < entries.length; i++) {
            ini.append(i).append("\t= ").append(entries[i]).append("\r\n");
        }
        writeFile("install.ini", ini.toString().getBytes(StandardCharsets.US_ASCII));
    }

    private void writeFile(String path, byte[]... parts) throws IOException {
        Path target = root.resolve(path);
        if (target.getParent() != null) {
            Files.createDirectories(target.getParent());
        }
        Files.write(target, concat(parts));
    }

    private static void putZipEntry(ZipOutputStream out, String name, byte[] data) throws IOException {
        ZipEntry entry = new ZipEntry(name);
        entry.setMethod(ZipEntry.DEFLATED);
        out.putNextEntry(entry);
        out.write(data);
        out.closeEntry();
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

    private static long le32(byte[] data, int offset) {
        return (data[offset] & 0xFFL) | (data[offset + 1] & 0xFFL) << 8
                | (data[offset + 2] & 0xFFL) << 16 | (data[offset + 3] & 0xFFL) << 24;
    }

    private static byte[] readAll(IFileSystem fs, String path) throws IOException {
        try (InputStream in = fs.open(path)) {
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

    private static class DelegatingFileSystem implements IFileSystem {
        private final IFileSystem inner;

        DelegatingFileSystem(IFileSystem inner) {
            this.inner = inner;
        }

        @Override public InputStream open(String path) throws IOException { return inner.open(path); }
        @Override public boolean exists(String path) { return inner.exists(path); }
        @Override public boolean isDirectory(String path) { return inner.isDirectory(path); }
        @Override public String[] list(String path) { return inner.list(path); }
        @Override public long length(String path) { return inner.length(path); }
        @Override public DataSource openSource(String path) throws IOException { return inner.openSource(path); }
    }
}
