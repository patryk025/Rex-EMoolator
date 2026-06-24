package pl.genschu.bloomooemulator.jmh;

import org.openjdk.jmh.annotations.*;
import pl.genschu.bloomooemulator.encoding.CLZW2Compression;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Decode-side benchmark for {@link CLZW2Compression#decompress(byte[])}.
 *
 * <p>Establishes a baseline (time + allocation) for the current, allocation-heavy decoder against
 * a representative spread of PIKLIB8 reference vectors so a future rewrite can be proven numerically.
 * Run with the GC profiler ({@code -prof gc}, wired into the {@code :core:jmh} Gradle task) to record
 * {@code gc.alloc.rate.norm} (bytes/op) alongside the average time.
 *
 * <p>The chosen {@code @Param} vectors cover every decoder path and a realistic size range:
 * pure literals, zero-run/RLE, tight back-references, gradients, real image dimensions and a large
 * incompressible stress case. Correctness is already guaranteed by {@code CLZWCompression2Test};
 * this benchmark only measures.
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(1)
public class CLZW2DecodeBenchmark {

    @Param({
            "first_literals_20",
            "rle00_1000",
            "period_8",
            "gradient_ramp",
            "img_16x16",
            "img_64x64",
            "img_320x200",
            "img_640x480",
            "random_65536"
    })
    public String vector;

    private final Map<String, byte[]> blocks = new HashMap<>();
    private byte[] block;

    private static File vectorFile(String name) {
        return new File("../assets/test-assets/compression/vectors/piklib8/" + name + ".clzw2").getAbsoluteFile();
    }

    @Setup(Level.Trial)
    public void loadVectors() throws IOException {
        for (String name : new String[]{
                "first_literals_20", "rle00_1000", "period_8", "gradient_ramp",
                "img_16x16", "img_64x64", "img_320x200", "img_640x480", "random_65536"}) {
            blocks.put(name, Files.readAllBytes(vectorFile(name).toPath()));
        }
    }

    @Setup(Level.Iteration)
    public void pickVector() {
        block = blocks.get(vector);
    }

    @Benchmark
    public byte[] decode() {
        return CLZW2Compression.decompress(block);
    }
}
