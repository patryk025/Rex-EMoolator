package pl.genschu.bloomooemulator.conformance;

import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestFactory;
import pl.genschu.bloomooemulator.TestEnvironment;
import pl.genschu.bloomooemulator.engine.Game;
import pl.genschu.bloomooemulator.engine.compatibility.Compatibility;
import pl.genschu.bloomooemulator.engine.compatibility.CompatibilityProfile;
import pl.genschu.bloomooemulator.engine.compatibility.EngineVariant;
import pl.genschu.bloomooemulator.engine.filesystem.LocalFileSystem;
import pl.genschu.bloomooemulator.interpreter.ast.ASTNode;
import pl.genschu.bloomooemulator.interpreter.ast.BlockNode;
import pl.genschu.bloomooemulator.interpreter.context.Context;
import pl.genschu.bloomooemulator.interpreter.parser.CodeParser;
import pl.genschu.bloomooemulator.interpreter.runtime.ASTInterpreter;
import pl.genschu.bloomooemulator.interpreter.runtime.ExecutionContext;
import pl.genschu.bloomooemulator.interpreter.values.NullValue;
import pl.genschu.bloomooemulator.interpreter.values.Value;
import pl.genschu.bloomooemulator.interpreter.variable.ArrayVariable;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.loader.CNVParser;
import pl.genschu.bloomooemulator.logic.GameFamilies;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.fail;

/**
 * Replays the cases recorded by {@code tools/crosscheck} on the original engine DLLs
 * through the emulator's interpreter and compares every step.
 *
 * <p>Each dynamic test covers one case on a group of engines that recorded identical
 * results and share a compatibility profile. The outcome is PASS (every check matched),
 * PARTIAL, FAIL (nothing matched) or SKIPPED (the engine left nothing to compare).
 * A summary of all outcomes is written to {@code build/reports/crosscheck/}.</p>
 *
 * <p>Run with {@code gradlew :core:crosscheck}; the regular {@code test} task excludes it.</p>
 */
@Tag("crosscheck")
class EngineCrosscheckTest {

    enum Outcome { PASS, PARTIAL, FAIL, SKIPPED }

    /** One case on one group of engines. */
    record Verdict(String caseId, List<String> engines, String variant, String gameFamily, Outcome outcome,
                   int stateMatched, int stateChecks, int resultMatched, int resultChecks,
                   List<String> mismatches, List<String> notes) {
        int matched() { return stateMatched + resultMatched; }
        int checks() { return stateChecks + resultChecks; }
    }

    /** What one run produced: the call result and the watched objects after every step. */
    private record Observation(List<String[]> results, List<Map<String, String[]>> states, String error, int failedStep) {
    }

    private static final Charset ENGINE_CHARSET = Charset.forName("windows-1250");
    private static final List<Verdict> VERDICTS = Collections.synchronizedList(new ArrayList<>());

    @BeforeAll
    static void boot() {
        TestEnvironment.init();
    }

    @TestFactory
    Stream<DynamicTest> engineCases() throws IOException {
        Path manifestPath = Path.of(System.getProperty("crosscheck.manifest", "../tools/crosscheck/engine-results.json"));
        JsonValue manifest = new JsonReader().parse(Files.readString(manifestPath, StandardCharsets.UTF_8));
        JsonValue engines = manifest.get("engines");

        List<DynamicTest> tests = new ArrayList<>();
        for (JsonValue testCase : manifest.get("cases")) {
            for (Map.Entry<String, List<String>> group : groupEngines(testCase, engines).entrySet()) {
                List<String> ids = group.getValue();
                JsonValue engine = engines.get(ids.get(0));
                String name = testCase.getString("id") + " @ " + String.join(", ", ids);
                // A family is only worth naming when it is what sets the group apart.
                String family = ids.stream().map(id -> profileFor(engines.get(id)).gameFamily())
                        .distinct().count() == 1 ? profileFor(engine).gameFamily() : null;
                tests.add(DynamicTest.dynamicTest(name, () -> check(testCase, ids, engine, family)));
            }
        }
        return tests.stream();
    }

    /**
     * Engines that the emulator treats alike (same resolved behaviour) and that recorded
     * the same steps form one group, so a difference shared by many builds is one test.
     */
    private static Map<String, List<String>> groupEngines(JsonValue testCase, JsonValue engines) {
        Map<String, List<String>> groups = new LinkedHashMap<>();
        for (JsonValue result : testCase.get("results")) {
            for (String id : result.get("engines").asStringArray()) {
                JsonValue engine = engines.get(id);
                String key = profileFor(engine).behaviour() + "|" + engine.getString("variant")
                        + "|" + result.getString("status") + "|" + result.get("steps").toJson(JsonWriter.OutputType.json);
                groups.computeIfAbsent(key, k -> new ArrayList<>()).add(id);
            }
        }
        return groups;
    }

    /** The recorded result an engine belongs to. */
    private static JsonValue resultOf(JsonValue testCase, String engineId) {
        for (JsonValue result : testCase.get("results")) {
            if (List.of(result.get("engines").asStringArray()).contains(engineId)) {
                return result;
            }
        }
        throw new IllegalArgumentException("no result for " + engineId);
    }

    /** The profile the emulator would pick for a game shipping this DLL. */
    private static CompatibilityProfile profileFor(JsonValue engine) {
        return new CompatibilityProfile(EngineVariant.valueOf(engine.getString("variant")),
                GameFamilies.familyFor(engine.getString("sha1")));
    }

    private static void check(JsonValue testCase, List<String> engineIds, JsonValue engine, String family) {
        CompatibilityProfile profile = profileFor(engine);
        EngineVariant variant = profile.engine();
        JsonValue recorded = resultOf(testCase, engineIds.get(0));
        List<String> read = List.of(testCase.get("read").asStringArray());
        List<String> steps = List.of(testCase.get("steps").asStringArray());

        List<String> mismatches = new ArrayList<>();
        List<String> notes = new ArrayList<>();
        // Object state and call results are counted apart: a method that returns a value
        // where the engine returns nothing only matters once the call is nested.
        int stateMatched = 0, stateChecks = 0;
        int resultMatched = 0, resultChecks = 0;

        JsonValue recordedSteps = recorded.get("steps");
        String status = recorded.getString("status");
        if (!status.equals("ok")) {
            notes.add("engine " + status + " in " + recorded.getString("stage", "step " + recordedSteps.size()));
        }

        Observation observed = runOnEmulator(testCase.getString("cnv"), testCase.get("files"), steps, read, profile);
        if (observed.error() != null) {
            notes.add("emulator failed at " + (observed.failedStep() < 0 ? "load" : "step " + observed.failedStep()) + ": " + observed.error());
        }

        for (int i = 0; i < recordedSteps.size(); i++) {
            JsonValue step = recordedSteps.get(i);
            String line = steps.get(i);
            boolean ran = i < observed.results().size();

            resultChecks++;
            String[] expected = pair(step.get("result"));
            String[] actual = ran ? observed.results().get(i) : null;
            if (ran && Objects.equals(describe(expected), describe(actual))) {
                resultMatched++;
            } else {
                mismatches.add(line + " returned " + (ran ? describe(actual) : "<not run>") + ", engine " + describe(expected));
            }

            for (String name : read) {
                stateChecks++;
                String[] expectedState = pair(step.get("state").get(name));
                String[] actualState = ran ? observed.states().get(i).get(name) : null;
                if (ran && Objects.equals(describe(expectedState), describe(actualState))) {
                    stateMatched++;
                } else {
                    mismatches.add("after " + line + ": " + name + " = " + (ran ? describe(actualState) : "<not run>")
                            + ", engine " + describe(expectedState));
                }
            }
        }

        int matched = stateMatched + resultMatched;
        int checks = stateChecks + resultChecks;
        Outcome outcome = checks == 0 ? Outcome.SKIPPED
                : matched == checks ? Outcome.PASS
                : matched == 0 ? Outcome.FAIL
                : Outcome.PARTIAL;
        VERDICTS.add(new Verdict(testCase.getString("id"), engineIds, variant.name(), family, outcome,
                stateMatched, stateChecks, resultMatched, resultChecks, mismatches, notes));

        if (outcome == Outcome.PARTIAL || outcome == Outcome.FAIL) {
            StringBuilder message = new StringBuilder(outcome + ": state " + stateMatched + "/" + stateChecks
                    + ", results " + resultMatched + "/" + resultChecks);
            notes.forEach(note -> message.append("\n  note: ").append(note));
            mismatches.forEach(mismatch -> message.append("\n  ").append(mismatch));
            fail(message.toString());
        }
    }

    private static Observation runOnEmulator(String cnv, JsonValue files, List<String> steps, List<String> read,
                                             CompatibilityProfile profile) {
        List<String[]> results = new ArrayList<>();
        List<Map<String, String[]>> states = new ArrayList<>();
        Compatibility.install(profile);
        Path directory = null;
        try {
            Context context = new Context(new ExecutionContext());
            try {
                directory = gameDirectory(files);
                context.setGame(gameOn(directory));
                // Plain scripts are decoded with the platform charset, see ScriptDecypher.
                new CNVParser().parse(new ByteArrayInputStream(cnv.getBytes(Charset.defaultCharset())), "crosscheck.cnv", context);
            } catch (Exception | AssertionError error) {
                return new Observation(results, states, error.toString(), -1);
            }
            for (int i = 0; i < steps.size(); i++) {
                try {
                    results.add(pair(execute(context, steps.get(i))));
                } catch (Exception | AssertionError error) {
                    return new Observation(results, states, error.toString(), i);
                }
                Map<String, String[]> state = new LinkedHashMap<>();
                for (String name : read) {
                    Variable variable = context.getVariable(name);
                    state.put(name, variable == null ? null
                            : new String[]{typeName(variable.getTypeName()), stateText(variable)});
                }
                states.add(state);
            }
            return new Observation(results, states, null, -1);
        } finally {
            Compatibility.reset();
            deleteDirectory(directory);
        }
    }

    /** A clean directory with the case's files, the counterpart of pikhost's working directory. */
    private static Path gameDirectory(JsonValue files) throws IOException {
        Path directory = Files.createTempDirectory("crosscheck");
        if (files != null) {
            // A game with no scene open resolves relative paths under DANE.
            Path resources = Files.createDirectory(directory.resolve("DANE"));
            for (JsonValue file : files) {
                Files.write(resources.resolve(file.name()), file.asString().replace("\n", "\r\n").getBytes(ENGINE_CHARSET));
            }
        }
        return directory;
    }

    private static Game gameOn(Path directory) {
        Game game = new Game(null, null);
        LocalFileSystem fileSystem = new LocalFileSystem(directory.toFile());
        game.getVfs().mountAssets(fileSystem);
        game.getVfs().setStorage(fileSystem);
        return game;
    }

    private static void deleteDirectory(Path directory) {
        if (directory == null) {
            return;
        }
        try (Stream<Path> files = Files.walk(directory)) {
            files.sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
        } catch (IOException ignored) {
            // A temp directory left behind harms nothing.
        }
    }

    /** The object's text as pikhost reads it; the engine prints an ARRAY as its elements. */
    private static String stateText(Variable variable) {
        if (variable instanceof ArrayVariable array) {
            return array.elements().stream().map(Value::toDisplayString).collect(Collectors.joining(","));
        }
        return variable.value().toDisplayString();
    }

    /** Runs one script line and returns what the call itself returned. */
    private static Value execute(Context context, String line) {
        // The parser only emits a statement once it reaches its ';'.
        ASTNode parsed = CodeParser.parseCode("{" + line + ";}");
        if (((BlockNode) parsed).statements().isEmpty()) {
            throw new IllegalStateException("parsed to nothing: " + line);
        }
        ASTInterpreter interpreter = new ASTInterpreter(context);
        Value last = null;
        for (ASTNode statement : ((BlockNode) parsed).statements()) {
            last = interpreter.execute(statement).getValue();
        }
        return last;
    }

    private static String[] pair(Value value) {
        if (value == null || value instanceof NullValue) {
            return null;
        }
        return new String[]{typeName(value.getType().name()), value.toDisplayString()};
    }

    private static String[] pair(JsonValue value) {
        if (value == null || value.isNull()) {
            return null;
        }
        return value.asStringArray();
    }

    /** The engine reports BOOL where the emulator says BOOLEAN. */
    private static String typeName(String name) {
        return name.equals("BOOLEAN") ? "BOOL" : name;
    }

    private static String describe(String[] value) {
        return value == null ? "nothing" : "\"" + value[1] + "\" (" + value[0] + ")";
    }

    // ------------------------------------------------------------------ report

    @AfterAll
    static void writeReport() throws IOException {
        Path directory = Path.of(System.getProperty("crosscheck.report", "build/reports/crosscheck"));
        Files.createDirectories(directory);
        List<Verdict> verdicts = new ArrayList<>(VERDICTS);
        Files.writeString(directory.resolve("summary.md"), markdown(verdicts), StandardCharsets.UTF_8);
        Files.writeString(directory.resolve("summary.json"), json(verdicts), StandardCharsets.UTF_8);
    }

    private static String markdown(List<Verdict> verdicts) {
        Map<Outcome, Integer> totals = new LinkedHashMap<>();
        for (Outcome outcome : Outcome.values()) totals.put(outcome, 0);
        Map<String, Map<Outcome, Integer>> byVariant = new LinkedHashMap<>();
        for (Verdict verdict : verdicts) {
            totals.merge(verdict.outcome(), 1, Integer::sum);
            byVariant.computeIfAbsent(verdict.variant(), v -> new LinkedHashMap<>()).merge(verdict.outcome(), 1, Integer::sum);
        }

        StringBuilder out = new StringBuilder("# Engine crosscheck\n\n");
        out.append("Emulator against the original engines, one row per case and group of engines with identical results.\n\n");
        out.append("| | PASS | PARTIAL | FAIL | SKIPPED |\n|---|---|---|---|---|\n");
        out.append(row("**all**", totals));
        byVariant.forEach((variant, counts) -> out.append(row(variant, counts)));

        out.append("\n| Case | Engines | Outcome | State | Results | First difference |\n|---|---|---|---|---|---|\n");
        for (Verdict verdict : verdicts) {
            // A state difference says more than a return value does, so it is shown first.
            String first = verdict.mismatches().stream().filter(m -> m.startsWith("after ")).findFirst()
                    .orElse(!verdict.mismatches().isEmpty() ? verdict.mismatches().get(0)
                            : !verdict.notes().isEmpty() ? verdict.notes().get(0) : "");
            out.append("| `").append(verdict.caseId()).append("` | ")
                    .append(engineLabel(verdict)).append(" | ")
                    .append(verdict.outcome()).append(" | ")
                    .append(verdict.stateMatched()).append('/').append(verdict.stateChecks()).append(" | ")
                    .append(verdict.resultMatched()).append('/').append(verdict.resultChecks()).append(" | ")
                    .append(first.replace("|", "\\|")).append(" |\n");
        }
        return out.toString();
    }

    private static String row(String label, Map<Outcome, Integer> counts) {
        StringBuilder out = new StringBuilder("| ").append(label);
        for (Outcome outcome : Outcome.values()) out.append(" | ").append(counts.getOrDefault(outcome, 0));
        return out.append(" |\n").toString();
    }

    private static String engineLabel(Verdict verdict) {
        String label = verdict.engines().size() == 1 ? verdict.engines().get(0)
                : verdict.variant() + " x" + verdict.engines().size();
        return verdict.gameFamily() != null ? label + " (" + verdict.gameFamily() + ")" : label;
    }

    private static String json(List<Verdict> verdicts) {
        JsonValue root = new JsonValue(JsonValue.ValueType.array);
        for (Verdict verdict : verdicts) {
            JsonValue entry = new JsonValue(JsonValue.ValueType.object);
            entry.addChild("case", new JsonValue(verdict.caseId()));
            entry.addChild("engines", strings(verdict.engines()));
            entry.addChild("variant", new JsonValue(verdict.variant()));
            if (verdict.gameFamily() != null) entry.addChild("gameFamily", new JsonValue(verdict.gameFamily()));
            entry.addChild("outcome", new JsonValue(verdict.outcome().name()));
            entry.addChild("stateMatched", new JsonValue(verdict.stateMatched()));
            entry.addChild("stateChecks", new JsonValue(verdict.stateChecks()));
            entry.addChild("resultMatched", new JsonValue(verdict.resultMatched()));
            entry.addChild("resultChecks", new JsonValue(verdict.resultChecks()));
            entry.addChild("mismatches", strings(verdict.mismatches()));
            entry.addChild("notes", strings(verdict.notes()));
            root.addChild(entry);
        }
        return root.prettyPrint(JsonWriter.OutputType.json, 120);
    }

    private static JsonValue strings(List<String> values) {
        JsonValue array = new JsonValue(JsonValue.ValueType.array);
        values.forEach(value -> array.addChild(new JsonValue(value)));
        return array;
    }
}
