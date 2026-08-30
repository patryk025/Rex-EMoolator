package pl.genschu.bloomooemulator.tests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import pl.genschu.bloomooemulator.engine.filesystem.AssetSourceDispatcher;
import pl.genschu.bloomooemulator.engine.filesystem.DataSource;
import pl.genschu.bloomooemulator.engine.filesystem.FileDataSource;
import pl.genschu.bloomooemulator.engine.filesystem.FileSystemType;
import pl.genschu.bloomooemulator.engine.filesystem.FileSystemDetector;
import pl.genschu.bloomooemulator.engine.filesystem.IFileSystem;
import pl.genschu.bloomooemulator.engine.filesystem.IsoFileSystem;
import pl.genschu.bloomooemulator.engine.filesystem.LocalFileSystem;
import pl.genschu.bloomooemulator.engine.filesystem.MemoryDataSource;
import pl.genschu.bloomooemulator.engine.filesystem.SlicedDataSource;
import pl.genschu.bloomooemulator.engine.filesystem.UdfFileSystem;
import pl.genschu.bloomooemulator.engine.filesystem.VFS;
import pl.genschu.bloomooemulator.loader.helpers.SeekableBinaryReader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Covers mounting a container that lives inside another mount, which is what the
 * {@link DataSource} indirection exists for: the parsers never see a {@link File},
 * so an image embedded in a bigger blob mounts exactly like one on disk.
 */
class NestedAssetSourceTest {
    private static final String KNOWN_ENTRY = "README.md";
    private static final int PREFIX_LENGTH = 4096;

    private static File fixture(String name) {
        for (Path candidate : new Path[] {
                Path.of("../assets/test-assets/" + name),
                Path.of("assets/test-assets/" + name)
        }) {
            Path absolute = candidate.toAbsolutePath().normalize();
            if (Files.isRegularFile(absolute)) {
                return absolute.toFile();
            }
        }
        throw new AssertionError("Missing fixture: assets/test-assets/" + name);
    }

    private static byte[] readAll(IFileSystem fs, String path) throws IOException {
        try (InputStream input = fs.open(path)) {
            return input.readAllBytes();
        }
    }

    private static byte[] readAll(DataSource source) throws IOException {
        try (SeekableBinaryReader reader = source.openReader()) {
            return reader.readBytes(Math.toIntExact(reader.length()));
        }
    }

    /** Writes the image into the middle of a larger file, mimicking an embedded container. */
    private static File embed(Path target, File image) throws IOException {
        byte[] padding = new byte[PREFIX_LENGTH];
        new Random(7).nextBytes(padding);
        try (OutputStream out = Files.newOutputStream(target)) {
            out.write(padding);
            Files.copy(image.toPath(), out);
            out.write(padding, 0, 1024);
        }
        return target.toFile();
    }

    @Test
    void slicedSourceMountsAnEmbeddedIso(@TempDir Path tempDir) throws IOException {
        File image = fixture("BFMoo.iso");
        File container = embed(tempDir.resolve("container.bin"), image);

        DataSource slice = new SlicedDataSource(
                new FileDataSource(container),
                "embedded.iso",
                PREFIX_LENGTH,
                image.length()
        );

        assertEquals(FileSystemType.ISO9660, FileSystemDetector.detectFileSystemType(slice));

        IFileSystem nested = AssetSourceDispatcher.openAssets(slice);
        assertTrue(nested.exists(KNOWN_ENTRY));
        assertArrayEquals(
                readAll(new IsoFileSystem(image), KNOWN_ENTRY),
                readAll(nested, KNOWN_ENTRY)
        );
    }

    @Test
    void embeddedIsoMountsAsAnAdditionalVfsLayer(@TempDir Path tempDir) throws IOException {
        File image = fixture("BFMoo.iso");
        File container = embed(tempDir.resolve("container.bin"), image);

        VFS vfs = new VFS();
        vfs.mountAssets(AssetSourceDispatcher.openAssets(new SlicedDataSource(
                new FileDataSource(container),
                "embedded.iso",
                PREFIX_LENGTH,
                image.length()
        )));

        assertTrue(vfs.exists("DANE/APPLICATION.DEF"));
        assertTrue(vfs.isDirectory("common"));
        try (InputStream input = vfs.openRead(KNOWN_ENTRY)) {
            assertTrue(input.readAllBytes().length > 0);
        }
    }

    @Test
    void memorySourceMountsAnIsoHeldInMemory() throws IOException {
        File image = fixture("BFMoo.iso");
        DataSource source = new MemoryDataSource("BFMoo.iso", Files.readAllBytes(image.toPath()));

        IFileSystem mounted = AssetSourceDispatcher.openAssets(source);
        assertArrayEquals(
                readAll(new IsoFileSystem(image), KNOWN_ENTRY),
                readAll(mounted, KNOWN_ENTRY)
        );
    }

    @Test
    void isoEntriesAreExposedAsZeroCopySlices() throws IOException {
        IsoFileSystem fs = new IsoFileSystem(fixture("BFMoo.iso"));

        DataSource entry = fs.openSource(KNOWN_ENTRY);
        assertInstanceOf(SlicedDataSource.class, entry);
        assertEquals(fs.length(KNOWN_ENTRY), entry.length());
        assertArrayEquals(readAll(fs, KNOWN_ENTRY), readAll(entry));
    }

    @Test
    void udfEntriesAreExposedAsZeroCopySlices() throws IOException {
        UdfFileSystem fs = new UdfFileSystem(fixture("BFMoo-udf.iso"));

        DataSource entry = fs.openSource(KNOWN_ENTRY);
        assertInstanceOf(SlicedDataSource.class, entry);
        assertArrayEquals(readAll(fs, KNOWN_ENTRY), readAll(entry));
    }

    @Test
    void folderEntriesAreExposedAsFileSources() throws IOException {
        LocalFileSystem fs = new LocalFileSystem(fixture("BFMoo.iso").getParentFile());

        DataSource entry = fs.openSource("bfmoo.iso");
        assertNotNull(entry.asFile(), "a folder entry should stay a real file");
        assertTrue(AssetSourceDispatcher.openAssets(entry).exists(KNOWN_ENTRY));
    }

    @Test
    void nestedZipReportsThatItNeedsARealFile() throws IOException {
        DataSource source = new MemoryDataSource("game.zip", new byte[] {'P', 'K', 3, 4, 0, 0, 0, 0});

        IOException failure = assertThrows(IOException.class, () -> AssetSourceDispatcher.openAssets(source));
        assertTrue(failure.getMessage().contains("file on disk"), failure.getMessage());
    }
}
