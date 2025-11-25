package pl.genschu.bloomooemulator.jhm;

import org.openjdk.jmh.annotations.*;
import pl.genschu.bloomooemulator.TestEnvironment;
import pl.genschu.bloomooemulator.encoding.ScriptDecypher;
import pl.genschu.bloomooemulator.engine.Game;
import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.loader.CNVParser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Warmup(iterations = 3, time = 2)
@Measurement(iterations = 5, time = 3)
@Fork(1)
public class CNVParserBenchmark {

    private CNVParser parser;
    private Context testContext;

    private File smallScript;
    private File mediumScript;
    private File largeScript;

    private File encryptedSmall;
    private File encryptedLarge;

    private String largeScriptContent;

    private File asset(String name) {
        return new File("../assets/test-assets/scripts/" + name).getAbsoluteFile();
    }

    @Setup
    public void setup() throws IOException {
        // run headless gdx environment
        TestEnvironment.init();

        parser = new CNVParser();
        Game game = new Game(null, null);
        testContext = new Context();
        testContext.setGame(game);

        smallScript = asset("Arrajki.cnv");
        mediumScript = asset("Math_test.cnv");
        largeScript = asset("egipt.cnv");
        encryptedSmall = asset("Arrajki_encr.cnv");
        encryptedLarge = asset("egipt_encr.cnv");

        int smallBehaviours = countBehaviours(smallScript);
        int mediumBehaviours = countBehaviours(mediumScript);
        int largeBehaviours = countBehaviours(largeScript);

        System.out.println("\n=== SCRIPT STATISTICS ===");
        System.out.println("Small:  " + smallScript.length() / 1024 + " KB, " + smallBehaviours + " behaviours");
        System.out.println("Medium: " + mediumScript.length() / 1024 + " KB, " + mediumBehaviours + " behaviours");
        System.out.println("Large:  " + largeScript.length() / 1024 + " KB, " + largeBehaviours + " behaviours");

        largeScriptContent = new String(Files.readAllBytes(largeScript.toPath()));
    }

    @Benchmark
    public void parseSmallScript() throws IOException {
        parser.parseFile(smallScript, testContext);
    }

    @Benchmark
    public void parseMediumScript() throws IOException {
        parser.parseFile(mediumScript, testContext);
    }

    @Benchmark
    public void parseLargeScript() throws IOException {
        parser.parseFile(largeScript, testContext);
    }

    @Benchmark
    public void parseEncryptedSmall() throws IOException {
        parser.parseFile(encryptedSmall, testContext);
    }

    @Benchmark
    public void parseEncryptedLarge() throws IOException {
        parser.parseFile(encryptedLarge, testContext);
    }

    @Benchmark
    public String decryptionOnly() throws IOException {
        return ScriptDecypher.decode(largeScriptContent, 6);
    }

    private int countBehaviours(File file) throws IOException {
        try (java.util.stream.Stream<String> lines = Files.lines(file.toPath())) {
            long count = lines
                    .filter(line -> line.matches("^[^:]+:TYPE=BEHAVIOUR.*$"))
                    .count();
            return (int) count;
        }
    }
}
