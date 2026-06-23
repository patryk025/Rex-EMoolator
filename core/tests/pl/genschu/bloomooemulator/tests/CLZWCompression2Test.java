package pl.genschu.bloomooemulator.tests;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import pl.genschu.bloomooemulator.encoding.CLZW2Compression;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Reference-vector verification for {@link CLZW2Compression}: every compressed vector is decompressed
 * and compared byte-for-byte against its {@code .raw} counterpart in
 * {@code assets/test-assets/compression/raw}.
 *
 * <p>Two independent vector sets are exercised, kept separate on purpose because they come from
 * different LZO1X-1 encoders and therefore stress different decoder paths:
 * <ul>
 *   <li><b>vectors/piklib8/</b> - {@code .clzw2} blocks produced by the native PIKLIB8.dll compressor
 *       (the engine's reference). They already carry the full CLZW2 wrapper (8-byte header +
 *       payload) and PIKLIB8 never emits a back-reference to output offset 0, so they all
 *       round-trip cleanly.</li>
 *   <li><b>vectors/liblzo2/</b> - raw {@code .lzo1x} blocks produced by liblzo2 via python-lzo
 *       (oracle only). This encoder <i>does</i> emit matches that reference output offset 0
 *       ({@code distance == decPtr}), which is exactly what probes the {@code decPtr - distance}
 *       boundary in {@link CLZW2Compression}. These files lack the 8-byte wrapper, so the header
 *       (LE32 raw size, LE32 payload size) is reconstructed here before decoding.</li>
 * </ul>
 *
 * <p>Each vector is emitted as its own dynamic test so a failure on one file does not mask the
 * others.
 */
class CLZWCompression2Test {

    private static final String RAW_EXTENSION = ".raw";
    private static final String CLZW2_EXTENSION = ".clzw2";
    private static final String LZO1X_EXTENSION = ".lzo1x";
    private static final int HEADER_LENGTH = 8;

    private static Path resolveDir(String relative) {
        for (Path base : new Path[]{Path.of(".."), Path.of(".")}) {
            Path candidate = base.resolve(relative).toAbsolutePath().normalize();
            if (Files.isDirectory(candidate)) {
                return candidate;
            }
        }
        throw new AssertionError("Missing test-assets directory: " + relative);
    }

    /** PIKLIB8 reference vectors: the {@code .clzw2} file is already a complete CLZW2 block. */
    @TestFactory
    List<DynamicTest> decompressesPiklib8Vectors() throws IOException {
        return buildVectorTests(
                "piklib8",
                resolveDir("assets/test-assets/compression/vectors/piklib8"),
                CLZW2_EXTENSION,
                (fileBytes, expectedRaw) -> fileBytes);
    }

    /** liblzo2 regression vectors: a header has to be prepended to the raw {@code .lzo1x} block. */
    @TestFactory
    List<DynamicTest> decompressesLiblzo2Vectors() throws IOException {
        return buildVectorTests(
                "liblzo2",
                resolveDir("assets/test-assets/compression/vectors/liblzo2"),
                LZO1X_EXTENSION,
                (fileBytes, expectedRaw) -> wrapWithHeader(expectedRaw.length, fileBytes));
    }

    /**
     * Builds one dynamic test per compressed vector in {@code sourceDir}.
     *
     * @param label     display-name prefix identifying the vector source
     * @param sourceDir directory holding the compressed vectors
     * @param sourceExt extension of the compressed vectors (used to derive the {@code .raw} stem)
     * @param toBlock   turns (compressed file bytes, expected raw bytes) into the CLZW2 block to decode
     */
    private List<DynamicTest> buildVectorTests(String label, Path sourceDir, String sourceExt,
                                               BiFunction<byte[], byte[], byte[]> toBlock) throws IOException {
        Path rawDir = resolveDir("assets/test-assets/compression/raw");
        try (Stream<Path> files = Files.list(sourceDir)) {
            return files
                    .filter(p -> hasExtension(p, sourceExt))
                    .sorted()
                    .map(source -> {
                        String stem = stripExtension(source, sourceExt);
                        Path raw = rawDir.resolve(stem + RAW_EXTENSION);
                        return DynamicTest.dynamicTest(label + "/" + source.getFileName(), () -> {
                            if (!Files.isRegularFile(raw)) {
                                fail("No .raw counterpart for " + source.getFileName()
                                        + " (expected " + raw + ")");
                            }

                            byte[] expected = Files.readAllBytes(raw);
                            byte[] block = toBlock.apply(Files.readAllBytes(source), expected);
                            byte[] actual = CLZW2Compression.decompress(block);

                            assertNotNull(actual, "decompress() returned null for " + source.getFileName());
                            assertArrayEquals(expected, actual, "Decompressed data differs from " + raw.getFileName());
                        });
                    })
                    .collect(Collectors.toList());
        }
    }

    /** Prepends the CLZW2 wrapper (LE32 decoded length, LE32 payload length) to a raw LZO1X block. */
    private static byte[] wrapWithHeader(int rawLength, byte[] payload) {
        byte[] block = new byte[HEADER_LENGTH + payload.length];
        ByteBuffer.wrap(block).order(ByteOrder.LITTLE_ENDIAN).putInt(rawLength).putInt(payload.length);
        System.arraycopy(payload, 0, block, HEADER_LENGTH, payload.length);
        return block;
    }

    private static boolean hasExtension(Path path, String ext) {
        return path.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(ext);
    }

    private static String stripExtension(Path path, String ext) {
        String name = path.getFileName().toString();
        return name.substring(0, name.length() - ext.length());
    }
}
