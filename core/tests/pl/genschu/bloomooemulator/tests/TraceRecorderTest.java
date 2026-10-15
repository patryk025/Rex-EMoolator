package pl.genschu.bloomooemulator.tests;

import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import pl.genschu.bloomooemulator.engine.trace.*;
import pl.genschu.bloomooemulator.interpreter.ast.*;
import pl.genschu.bloomooemulator.interpreter.context.Context;
import pl.genschu.bloomooemulator.interpreter.runtime.*;
import pl.genschu.bloomooemulator.interpreter.values.*;
import pl.genschu.bloomooemulator.interpreter.variable.*;
import java.nio.file.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class TraceRecorderTest {
    @org.junit.jupiter.api.BeforeAll static void boot() {
        pl.genschu.bloomooemulator.TestEnvironment.init();
    }
    @TempDir Path directory;

    private List<JsonValue> read(Path file) throws Exception {
        List<JsonValue> records = new ArrayList<>();
        for (String line : Files.readAllLines(file)) records.add(new JsonReader().parse(line));
        return records;
    }

    @Test void snapshotsDetachMutableArraysAndDatabaseRows() {
        ArrayVariable array = new ArrayVariable("a", List.of(new IntValue(1)));
        var oldArray = TraceValues.snapshot(array);
        array.elements().set(0, new IntValue(2));
        assertNotEquals(oldArray, TraceValues.snapshot(array));
        assertEquals(1, ((Map<?, ?>)((List<?>)oldArray.get("state")).get(0)).get("value"));

        DatabaseVariable db = new DatabaseVariable("db");
        db.state().setData(List.of(new ArrayList<>(List.of("old"))));
        var oldDatabase = TraceValues.snapshot(db);
        db.state().data().get(0).set(0, "new");
        assertEquals(List.of(List.of("old")), ((Map<?, ?>)oldDatabase.get("state")).get("rows"));
    }

    @Test void interpreterRecordsNestedCallsAndReversibleLocalChanges() throws Exception {
        Path file = directory.resolve("trace.jsonl");
        var exec = new ExecutionContext();
        var context = new Context(exec);
        exec.pushFrame("test", "test", null);
        context.setVariable("array", new ArrayVariable("array", List.of(new IntValue(1))));
        ASTNode declaration = new VarDefNode("INT", "x", new LiteralNode(new IntValue(7), null), null);
        ASTNode root = new BlockNode(List.of(declaration), null);
        try (var recorder = new TraceRecorder(file.toFile(), 100_000, Map.of())) {
            exec.setTraceRecorder(recorder);
            recorder.boundary("updateBegin", context, 0);
            recorder.script("test", "@INT(x,7);", root);
            recorder.script("test", "@INT(x,7);", root);
            new ASTInterpreter(context).execute(root);
            recorder.boundary("updateEnd", context, 16);
            assertNull(recorder.failure());
        }
        var records = read(file);
        assertEquals(1, records.stream().filter(r -> r.getString("kind").equals("script")).count());
        var enter = records.stream().filter(r -> r.getString("kind").equals("enter")).findFirst().orElseThrow();
        var leave = records.stream().filter(r -> r.getString("kind").equals("leave")).findFirst().orElseThrow();
        assertEquals(enter.getLong("call"), leave.getLong("call"));
        assertTrue(leave.get("changes").toString().contains("7"));
        assertEquals("closed", records.get(records.size() - 1).getString("reason"));

        // Applying all changes and reversing them must restore the empty initial observation.
        Map<String, String> state = new HashMap<>();
        for (var record : records) apply(state, record, "after");
        Collections.reverse(records);
        for (var record : records) apply(state, record, "before");
        assertTrue(state.isEmpty());
    }

    private void apply(Map<String, String> state, JsonValue record, String side) {
        JsonValue changes = record.get("changes");
        if (changes == null) return;
        for (JsonValue change : changes) {
            JsonValue value = change.get(side);
            if (value.isNull()) state.remove(change.name());
            else state.put(change.name(), value.toString());
        }
    }

    @Test void nestedEventsHaveParentAndErrorsDoNotChangeInterpreterSemantics() throws Exception {
        var context = new Context(new ExecutionContext());
        Path file = directory.resolve("nested.jsonl");
        ASTNode child = new MethodCallNode(new VariableNode("missing", null), "RUN", List.of(), null);
        ASTNode root = new IfNode(new LiteralNode(BoolValue.TRUE, null), child, null, null);
        try (var recorder = new TraceRecorder(file.toFile(), 100_000, Map.of())) {
            context.exec().setTraceRecorder(recorder);
            assertThrows(RuntimeException.class, () -> new ASTInterpreter(context).execute(root));
            assertNull(recorder.failure());
        }
        var records = read(file);
        var entries = records.stream().filter(r -> r.getString("kind").equals("enter")).toList();
        assertEquals(2, entries.size());
        assertEquals(entries.get(0).getLong("call"), entries.get(1).getLong("parent"));
        assertTrue(records.stream().anyMatch(r -> r.getString("outcome", "").startsWith("error:")));
    }

    @Test void limitEndsRecordingWithoutOverwritingExistingFiles() throws Exception {
        Path file = directory.resolve("limit.jsonl");
        try (var recorder = new TraceRecorder(file.toFile(), 1024, Map.of())) {
            var context = new Context(new ExecutionContext());
            context.setVariable("large", new StringVariable("large", "x".repeat(5000)));
            recorder.boundary("loaded", context, 0);
            assertFalse(recorder.isActive());
        }
        assertTrue(Files.size(file) <= 1024);
        var records = read(file);
        assertEquals("limit", records.get(records.size() - 1).getString("reason"));
        assertThrows(java.io.IOException.class, () -> new TraceRecorder(file.toFile(), 1024, Map.of()));
    }

    @Test void stringsAndSpecialDoublesProduceValidJson() throws Exception {
        Path file = directory.resolve("strings.jsonl");
        String text = "quote\" newline\n slash\\ emoji\uD83D\uDE00";
        var context = new Context(new ExecutionContext());
        context.setVariable("s", new StringVariable("s", text));
        context.setVariable("d", new DoubleVariable("d", Double.NaN));
        try (var recorder = new TraceRecorder(file.toFile(), 100_000, Map.of())) {
            recorder.boundary("loaded", context, 0);
        }
        var record = read(file).get(1);
        boolean found = false;
        for (var entry : record.get("checkpoint")) {
            if (entry.getString("name").equals("s")) {
                assertEquals(text, entry.get("state").getString("value"));
                found = true;
            }
        }
        assertTrue(found);
    }

    @Test void gameUsesItsOwnConfigAndRecordsFromInitialization() throws Exception {
        Path file = directory.resolve("game.jsonl");
        var engine = new pl.genschu.bloomooemulator.BlooMooEngine(null);
        engine.getConfig().setTraceEnabled(true);
        engine.getConfig().setTracePath(file.toString());
        var game = new pl.genschu.bloomooemulator.engine.Game(null, engine, () -> 123);
        assertNotNull(game.getTraceRecorder());
        game.traceBoundary("loaded");
        game.getTraceRecorder().close();
        var loaded = read(file).get(1);
        assertEquals(0, loaded.getLong("update"));
        assertEquals(123, loaded.getLong("engineTimeMs"));
        engine.getConfig().setTraceEnabled(false);
        assertNull(new pl.genschu.bloomooemulator.engine.Game(null, engine, () -> 0).getTraceRecorder());
    }

    @Test void contextCyclesAndShadowedNamesHaveSeparateSlots() throws Exception {
        var parent = new Context(new ExecutionContext());
        var child = new Context(new ExecutionContext(), parent);
        parent.addAdditionalContext(child);
        parent.setVariable("x", new IntegerVariable("x", 1));
        child.setVariable("x", new IntegerVariable("x", 2));
        Path file = directory.resolve("scopes.jsonl");
        try (var recorder = new TraceRecorder(file.toFile(), 100_000, Map.of())) {
            recorder.boundary("loaded", child, 0);
            assertNull(recorder.failure());
        }
        assertEquals(2, read(file).get(1).get("checkpoint").size);
    }

    @Test void adapterFailureDisablesRecordingWithoutEscapingIntoGame() throws Exception {
        var context = new Context(new ExecutionContext());
        var db = new DatabaseVariable("broken");
        db.state().data().add(null);
        context.setVariable("broken", db);
        Path file = directory.resolve("broken.jsonl");
        try (var recorder = new TraceRecorder(file.toFile(), 100_000, Map.of())) {
            assertDoesNotThrow(() -> recorder.boundary("loaded", context, 0));
            assertFalse(recorder.isActive());
            assertNotNull(recorder.failure());
        }
        assertFalse(read(file).stream().anyMatch(r -> r.getString("kind").equals("end")));
    }

    @Test void playbackAdaptersAreDetachedAndDoNotPollAudio() {
        var timer = new TimerVariable("timer", 50, true, 3, 100, 2, Map.of());
        var timerState = (Map<?, ?>)TraceValues.snapshot(timer).get("state");
        timer.state().currentTickCount = 3;
        assertEquals(2, timerState.get("tickCount"));
        assertEquals(100L, timerState.get("lastTickTimeMs"));

        var sound = new SoundVariable("sound");
        sound.state().sound = org.mockito.Mockito.mock(com.badlogic.gdx.audio.Sound.class);
        sound.state().playing = true;
        sound.state().paused = true;
        sound.state().playStartTime = 12.5;
        var audio = (Map<?, ?>)TraceValues.snapshot(sound).get("state");
        sound.state().playing = false;
        assertEquals(true, audio.get("playing"));
        assertEquals("engine-bookkeeping-not-audio-position", audio.get("timingSource"));
        org.mockito.Mockito.verifyNoInteractions(sound.state().sound);

        var sequence = new SequenceVariable("sequence");
        var event = new SequenceVariable.SequenceEvent("speaking", SequenceVariable.EventType.SPEAKING);
        event.getPlayback().activeAnimationName = "REKSIO";
        event.getSubEvents().add(event); // Malformed graph must not recurse forever.
        sequence.events().add(event);
        sequence.state().currentEvent = event;
        sequence.state().pendingFinishedEvents.add(event);
        var old = (Map<?, ?>)TraceValues.snapshot(sequence).get("state");
        event.getPlayback().activeAnimationName = "OTHER";
        sequence.state().pendingFinishedEvents.clear();
        assertEquals(List.of("speaking"), old.get("pendingFinishedEvents"));
        var events = (List<?>)old.get("events");
        assertEquals(1, events.size());
        assertEquals("REKSIO", ((Map<?, ?>)events.get(0)).get("activeAnimation"));
    }

    @Test void remainingContainersAndGraphicsSnapshotStoredFieldsOnly() {
        var multi = new MultiArrayVariable("multi");
        multi.setDimensions(new int[]{2, 2});
        multi.getData()[0] = new IntValue(7);
        var old = (Map<?, ?>)TraceValues.snapshot(multi).get("state");
        multi.getData()[0] = new IntValue(9);
        assertEquals(7, ((Map<?, ?>)((List<?>)old.get("elements")).get(0)).get("value"));
        assertEquals("NULL", ((Map<?, ?>)((List<?>)old.get("elements")).get(1)).get("type"));
        var matrix = new MatrixVariable("matrix");
        matrix.state().initGrid(2, 2);
        matrix.state().data[0] = 8;
        var grid = (Map<?, ?>)TraceValues.snapshot(matrix).get("state");
        matrix.state().data[0] = 10;
        assertEquals(8, ((List<?>)grid.get("cells")).get(0));
        var struct = new StructVariable("row", List.of("X"), List.of("INTEGER"), List.of(new IntValue(1)));
        assertEquals(true, TraceValues.snapshot(struct).get("supported"));
        var db = new DatabaseVariable("db");
        db.state().setData(List.of(new ArrayList<>(List.of("before"))));
        var cursor = (Map<?, ?>)TraceValues.snapshot(new DatabaseCursorVariable(db)).get("state");
        db.state().data().get(0).set(0, "after");
        assertEquals(List.of("before"), cursor.get("row"));
        var provider = org.mockito.Mockito.mock(pl.genschu.bloomooemulator.engine.context.CanvasBoundsProvider.class);
        var button = new ButtonVariable("button");
        button.state().rectProvider = provider;
        var text = new TextVariable("text");
        text.state().rectProvider = provider;
        assertEquals(true, TraceValues.snapshot(button).get("supported"));
        assertEquals(true, TraceValues.snapshot(text).get("supported"));
        assertEquals(true, TraceValues.snapshot(new ImageVariable("image")).get("supported"));
        org.mockito.Mockito.verifyNoInteractions(provider);
    }

    @Test void signalsRecordUnhandledExactFallbackAndReentrantCausality() throws Exception {
        Path file = directory.resolve("signals.jsonl");
        var context = new Context(new ExecutionContext());
        var interpreter = new ASTInterpreter(context);
        var source = new IntegerVariable("source", 0);
        Variable nested = source.withSignal("PING^exact", (v, signal, args) -> {
            assertEquals(0, args.length); // Exact binding must not acquire fallback payload.
            v.emitSignal("UNHANDLED");
            interpreter.execute(new ReturnNode(new LiteralNode(new IntValue(1), null), null));
        }).withSignal("PING", (v, signal, args) -> assertEquals(9, args[0].toInt().value()));
        try (var recorder = new TraceRecorder(file.toFile(), 100_000, Map.of())) {
            context.exec().setTraceRecorder(recorder);
            try (var ignored = recorder.activate(() -> context)) {
                nested.emitSignal("PING", new StringValue("exact"), new IntValue(9));
                nested.emitSignal("PING", new StringValue("fallback"), new IntValue(9));
            }
            nested.emitSignal("UNHANDLED"); // No lingering thread-local session.
            assertNull(recorder.failure());
        }
        var records = read(file);
        assertEquals(2, records.get(0).getInt("version"));
        var signals = records.stream().filter(r -> r.getString("kind").equals("signal")).toList();
        assertEquals(3, signals.size());
        assertEquals("PING^exact", signals.get(0).getString("binding"));
        assertEquals(0, signals.get(0).get("payload").size);
        assertFalse(signals.get(1).getBoolean("handled"));
        assertEquals(signals.get(0).getLong("signalId"), signals.get(1).getLong("parentSignal"));
        assertEquals("PING", signals.get(2).getString("binding"));
        var entry = records.stream().filter(r -> r.getString("kind").equals("enter")).findFirst().orElseThrow();
        assertEquals(signals.get(0).getLong("signalId"), entry.getLong("causeSignal"));
    }

    @Test void conditionsAreNotEvaluatedAgainBySnapshots() throws Exception {
        var condition = org.mockito.Mockito.mock(ConditionVariable.class);
        org.mockito.Mockito.when(condition.name()).thenReturn("CHECK");
        org.mockito.Mockito.when(condition.type()).thenReturn(VariableType.CONDITION);
        org.mockito.Mockito.when(condition.evaluate(org.mockito.ArgumentMatchers.any(MethodContext.class))).thenReturn(BoolValue.TRUE);
        var context = new Context(new ExecutionContext());
        context.setVariable("CHECK", condition);
        var interpreter = new ASTInterpreter(context);
        Path file = directory.resolve("condition.jsonl");
        try (var recorder = new TraceRecorder(file.toFile(), 100_000, Map.of())) {
            context.exec().setTraceRecorder(recorder);
            interpreter.execute(new IfNode(new VariableNode("CHECK", null), new BlockNode(List.of(), null), null, null));
            interpreter.execute(new WhileNode(new LiteralNode(BoolValue.FALSE, null), new BlockNode(List.of(), null), null));
            assertNull(recorder.failure());
        }
        org.mockito.Mockito.verify(condition, org.mockito.Mockito.times(1)).evaluate(org.mockito.ArgumentMatchers.any(MethodContext.class));
        var records = read(file).stream().filter(r -> r.getString("kind").equals("condition")).toList();
        assertEquals(2, records.size());
        assertTrue(records.get(0).getBoolean("result"));
        assertFalse(records.get(1).getBoolean("result"));
    }

    @Test void behaviourGuardRecordsPermissiveErrorAndSignalScopeUnwinds() throws Exception {
        var condition = org.mockito.Mockito.mock(ConditionVariable.class);
        org.mockito.Mockito.when(condition.name()).thenReturn("CHECK");
        org.mockito.Mockito.when(condition.type()).thenReturn(VariableType.CONDITION);
        org.mockito.Mockito.when(condition.evaluate(org.mockito.ArgumentMatchers.any(MethodContext.class), org.mockito.ArgumentMatchers.eq(true)))
                .thenThrow(new IllegalStateException("test failure"));
        var context = new Context(new ExecutionContext());
        context.setVariable("CHECK", condition);
        context.setAttribute("BEHAVIOUR", "CONDITION", "CHECK");
        var behaviour = new BehaviourVariable("BEHAVIOUR", new BlockNode(List.of(), null), Map.of());
        var methodContext = new ASTInterpreter(context).getMethodContext();
        Path file = directory.resolve("guard.jsonl");
        try (var recorder = new TraceRecorder(file.toFile(), 100_000, Map.of())) {
            context.exec().setTraceRecorder(recorder);
            assertTrue(BehaviourVariable.checkCondition(behaviour, methodContext));
            var source = new IntegerVariable("source", 0).withSignal("FAIL", (v, signal, args) -> { throw new IllegalStateException("handler"); });
            assertThrows(IllegalStateException.class, () -> {
                try (var ignored = recorder.activate(() -> context)) { source.emitSignal("FAIL"); }
            });
            source.emitSignal("UNHANDLED");
            assertNull(recorder.failure());
        }
        var records = read(file);
        var guard = records.stream().filter(r -> r.getString("kind").equals("condition")).findFirst().orElseThrow();
        assertTrue(guard.getBoolean("result"));
        assertTrue(guard.getString("evaluationError").contains("test failure"));
        assertEquals(1, records.stream().filter(r -> r.getString("kind").equals("signalEnd")).count());
        assertEquals(1, records.stream().filter(r -> r.getString("kind").equals("signal")).count());
    }
}
