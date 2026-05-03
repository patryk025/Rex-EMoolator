package pl.genschu.bloomooemulator.tests;

import com.badlogic.gdx.Gdx;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import pl.genschu.bloomooemulator.TestEnvironment;
import pl.genschu.bloomooemulator.interpreter.values.IntValue;
import pl.genschu.bloomooemulator.interpreter.variable.MatrixVariable;
import pl.genschu.bloomooemulator.interpreter.variable.SignalHandler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class MatrixTest {
    private static final int WIDTH = 20;
    private static final int HEIGHT = 11;
    private static final int CELL_COUNT = WIDTH * HEIGHT;
    private static final int SAFETY_LIMIT = 100_000;

    @BeforeAll
    static void boot() {
        TestEnvironment.init();
    }

    @Test
    void engineDumpReplaysEventsForEveryEventEmittingTick() {
        replayEventTicks("test_silnik.txt");
    }

    @Test
    void stoneFallingOnMoleDumpReplaysEventsForEveryEventEmittingTick() {
        replayEventTicks("test_spadek_na_leb.txt");
    }

    private void replayEventTicks(String filename) {
        List<RecordedTick> ticks = MatrixDumpLoader.load(filename);
        int checked = 0;
        for (int i = 0; i < ticks.size(); i++) {
            RecordedTick tick = ticks.get(i);
            if (tick.killed || tick.events.isEmpty()) continue;

            List<RecordedEvent> captured = replayTick(tick);
            assertEventsMatch(tick.events, captured, filename + " tick " + i);
            checked++;
        }
        assertTrue(checked > 0, filename + ": no event-emitting ticks found to replay");
    }

    /**
     * Compares expected (from dump) against actual (captured during replay).
     * Recording sometimes omits _NEXT_<result> for ONLATEST events — when expected.result
     * is null, the result field is not compared.
     */
    private static void assertEventsMatch(List<RecordedEvent> expected, List<RecordedEvent> actual, String header) {
        if (expected.size() != actual.size()) {
            fail(header + ": expected " + expected.size() + " events " + expected
                    + " but got " + actual.size() + " " + actual);
        }
        for (int j = 0; j < expected.size(); j++) {
            RecordedEvent e = expected.get(j);
            RecordedEvent a = actual.get(j);
            if (!e.type.equals(a.type) || e.col != a.col || e.row != a.row || e.dir != a.dir) {
                fail(header + " event " + j + ": expected " + e + " but was " + a);
            }
            if (e.result != null && !e.result.equals(a.result)) {
                fail(header + " event " + j + " result: expected " + e.result + " but was " + a.result);
            }
        }
    }

    private List<RecordedEvent> replayTick(RecordedTick tick) {
        MatrixVariable matrix = new MatrixVariable("MAT");
        matrix.state().initGrid(WIDTH, HEIGHT);
        System.arraycopy(tick.cells, 0, matrix.state().data, 0, CELL_COUNT);

        List<RecordedEvent> captured = new ArrayList<>();
        SignalHandler handler = (var, signal, args) -> {
            int col = ((IntValue) args[0]).value();
            int row = ((IntValue) args[1]).value();
            int dir = ((IntValue) args[2]).value();
            captured.add(new RecordedEvent(signal, col, row, dir, null));
        };
        matrix = (MatrixVariable) matrix.withSignal("ONNEXT", handler);
        matrix = (MatrixVariable) matrix.withSignal("ONLATEST", handler);

        matrix.callMethod("TICK");

        int safety = SAFETY_LIMIT;
        while (--safety > 0) {
            int beforeSize = captured.size();
            int code = ((IntValue) matrix.callMethod("NEXT").getReturnValue()).value();
            if (captured.size() > beforeSize) {
                captured.get(captured.size() - 1).result = code;
            } else if (code == 0) {
                break;
            }
            // Mole collision aborts the drain — original game scripts stop dispatching
            // pending moves once the player dies in the middle of a tick.
            if (code == 2) break;
        }
        if (safety <= 0) fail("NEXT drain hit safety limit");
        return captured;
    }

    // ============ Recorded data ============

    static final class RecordedEvent {
        final String type;
        final int col, row, dir;
        Integer result;  // null when recording omitted _NEXT_<result>

        RecordedEvent(String type, int col, int row, int dir, Integer result) {
            this.type = type;
            this.col = col;
            this.row = row;
            this.dir = dir;
            this.result = result;
        }

        @Override
        public String toString() {
            return type + "(col=" + col + ",row=" + row + ",dir=" + dir
                    + ",result=" + (result == null ? "?" : result) + ")";
        }
    }

    static final class RecordedTick {
        final int[] cells;
        final List<RecordedEvent> events;
        final boolean killed;

        RecordedTick(int[] cells, List<RecordedEvent> events, boolean killed) {
            this.cells = cells;
            this.events = events;
            this.killed = killed;
        }
    }

    // ============ Loader ============

    static final class MatrixDumpLoader {
        private static final Pattern EVENT_RE = Pattern.compile(
                "_(ONNEXT|ONLATEST)_(\\d+)_(\\d+)_(\\d+)(?:_NEXT_(\\d+))?"
        );

        static List<RecordedTick> load(String filename) {
            String absPath = Gdx.files.internal("../assets/test-assets/matrix_tests/" + filename)
                    .file().getAbsolutePath();
            List<RecordedTick> ticks = new ArrayList<>();
            try (BufferedReader br = new BufferedReader(new FileReader(absPath))) {
                String raw;
                while ((raw = br.readLine()) != null) {
                    if (!raw.startsWith("_DATA_")) continue;
                    String line = raw.substring("_DATA_".length()).trim();

                    boolean killed = line.contains("_KILLED_");
                    if (killed) line = line.replace("_KILLED_", "");

                    int firstEv = line.indexOf("_ONNEXT_");
                    int altEv = line.indexOf("_ONLATEST_");
                    if (firstEv == -1 || (altEv != -1 && altEv < firstEv)) firstEv = altEv;

                    String matrixPart = firstEv == -1 ? line : line.substring(0, firstEv);
                    String eventsPart = firstEv == -1 ? "" : line.substring(firstEv);

                    int[] cells = parseCells(matrixPart);
                    if (cells.length != CELL_COUNT) {
                        throw new RuntimeException(filename + ": expected " + CELL_COUNT
                                + " cells, got " + cells.length);
                    }

                    List<RecordedEvent> events = new ArrayList<>();
                    Matcher m = EVENT_RE.matcher(eventsPart);
                    while (m.find()) {
                        events.add(new RecordedEvent(
                                m.group(1),
                                Integer.parseInt(m.group(2)),
                                Integer.parseInt(m.group(3)),
                                Integer.parseInt(m.group(4)),
                                m.group(5) != null ? Integer.parseInt(m.group(5)) : null
                        ));
                    }
                    ticks.add(new RecordedTick(cells, events, killed));
                }
            } catch (IOException e) {
                throw new RuntimeException("Failed to load matrix dump: " + filename, e);
            }
            return ticks;
        }

        private static int[] parseCells(String matrixPart) {
            String[] tokens = matrixPart.contains("v")
                    ? matrixPart.split("v")
                    : matrixPart.split("");
            int[] cells = new int[tokens.length];
            for (int j = 0; j < tokens.length; j++) {
                cells[j] = Integer.parseInt(tokens[j]);
            }
            return cells;
        }
    }
}
