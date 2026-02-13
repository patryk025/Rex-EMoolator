package pl.genschu.bloomooemulator.tests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.genschu.bloomooemulator.TestEnvironment;
import pl.genschu.bloomooemulator.builders.ContextBuilder;
import pl.genschu.bloomooemulator.interpreter.context.Context;
import pl.genschu.bloomooemulator.interpreter.values.StringValue;
import pl.genschu.bloomooemulator.interpreter.variable.*;
import pl.genschu.bloomooemulator.interpreter.variable.SequenceVariable.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for SequenceVariable v2 implementation using real animation files.
 * This is the v2 equivalent of SequenceTest (which uses v1).
 */
class SequenceTest {
    private static final List<String> capturedSignals = new ArrayList<>();
    private Context ctx;

    @BeforeAll
    static void boot() {
        TestEnvironment.init();
    }

    @BeforeEach
    void setUp() {
        ctx = new ContextBuilder().build();
        capturedSignals.clear();
    }

    /**
     * Helper to create a signal handler that captures signals.
     */
    static SignalHandler capturingHandler(String variableName, Runnable additionalAction) {
        return (var, signalName, args) -> {
            String arg = args.length > 0 && args[0] instanceof StringValue sv ? sv.value() : null;
            String fullSignal = variableName + "_" + (arg == null ? signalName : signalName + "^" + arg);

            // Filter out certain signals like in v1 test
            if (arg != null && arg.matches(".*_[0-9]+$")) {
                // ignore numbered events
            } else if (!signalName.contains("ONFRAMECHANGED")) {
                if (var instanceof SequenceVariable ||
                    (var instanceof AnimoVariable && (arg == null || arg.endsWith("_START") || arg.endsWith("_STOP")))) {
                    capturedSignals.add(fullSignal);
                }
            }

            if (additionalAction != null) {
                additionalAction.run();
            }
        };
    }

    /**
     * Helper to wrap AnimoVariable with signal capturing.
     */
    static AnimoVariable wrapAnimoForCapture(AnimoVariable animo, String variableName) {
        return (AnimoVariable) animo
            .withSignal("ONSTARTED", capturingHandler(variableName, null))
            .withSignal("ONFINISHED", capturingHandler(variableName, null))
            .withSignal("ONFRAMECHANGED", capturingHandler(variableName, null));
    }

    /**
     * Helper to wrap SequenceVariable with signal capturing.
     */
    static SequenceVariable wrapSequenceForCapture(SequenceVariable seq, String variableName, Runnable onStarted, Runnable onFinished) {
        return (SequenceVariable) seq
            .withSignal("ONSTARTED", capturingHandler(variableName, onStarted))
            .withSignal("ONFINISHED", capturingHandler(variableName, onFinished));
    }

    /**
     * Test basic sequence playback with state transitions.
     * This tests the basic SequenceVariable functionality without full sequence file loading.
     */
    @Test
    void testSequenceBasicPlayback() {
        // Create a simple sequence with SIMPLE events
        SequenceVariable seq = new SequenceVariable("TEST_SEQ");

        // Add events
        SequenceEvent event1 = new SequenceEvent("EVENT1", EventType.SIMPLE);
        SequenceEvent event2 = new SequenceEvent("EVENT2", EventType.SIMPLE);
        seq.addEvent(event1);
        seq.addEvent(event2);

        // Wrap with capturing handlers
        seq = wrapSequenceForCapture(seq, "TEST_SEQ", null, null);

        // Play EVENT1
        seq.callMethod("PLAY", new StringValue("EVENT1"));
        assertTrue(seq.isPlaying());
        assertEquals("EVENT1", seq.getCurrentEventName());

        // Verify signal was captured
        assertEquals(1, capturedSignals.size());
        assertEquals("TEST_SEQ_ONSTARTED^EVENT1", capturedSignals.get(0));

        // Stop with signal
        seq.callMethod("STOP", new pl.genschu.bloomooemulator.interpreter.values.BoolValue(true));
        assertEquals(2, capturedSignals.size());
        assertEquals("TEST_SEQ_ONFINISHED^EVENT1", capturedSignals.get(1));
    }

    /**
     * Test sequence with nested events (SEQUENCE type).
     */
    @Test
    void testSequenceNestedEvents() {
        SequenceVariable seq = new SequenceVariable("NESTED_SEQ");

        // Create parent event with sub-events
        SequenceEvent parent = new SequenceEvent("PARENT", EventType.SEQUENCE, SequenceMode.SEQUENCE, null, false, false);
        SequenceEvent child1 = new SequenceEvent("CHILD1", EventType.SIMPLE);
        SequenceEvent child2 = new SequenceEvent("CHILD2", EventType.SIMPLE);
        parent.addSubEvent(child1);
        parent.addSubEvent(child2);

        seq.addEvent(parent);

        // Verify structure
        assertEquals(1, seq.events().size());
        assertEquals("PARENT", seq.events().get(0).getName());
        assertEquals(2, seq.events().get(0).getSubEvents().size());
        assertEquals("CHILD1", seq.events().get(0).getSubEvents().get(0).getName());
        assertEquals("CHILD2", seq.events().get(0).getSubEvents().get(1).getName());
        assertEquals(parent, child1.getParent());
        assertEquals(parent, child2.getParent());
    }

    /**
     * Test sequence state management (pause/resume).
     */
    @Test
    void testSequencePauseResume() {
        SequenceVariable seq = new SequenceVariable("PAUSABLE_SEQ");

        SequenceEvent event = new SequenceEvent("EVENT1", EventType.SIMPLE);
        seq.addEvent(event);

        // Play
        seq.callMethod("PLAY", new StringValue("EVENT1"));
        assertTrue(seq.isPlaying());

        // Pause
        seq.callMethod("PAUSE");
        assertTrue(seq.isPaused());

        // Resume
        seq.callMethod("RESUME");
        assertTrue(seq.isPlaying());
        assertTrue(!seq.isPaused());
    }

    /**
     * Test speaking event type phases.
     */
    @Test
    void testSpeakingEventPhases() {
        SequenceEvent speaking = new SequenceEvent("SPEAK1", EventType.SPEAKING, SequenceMode.SEQUENCE,
            "GADA", true, true);

        assertEquals(EventType.SPEAKING, speaking.getType());
        assertEquals("GADA", speaking.getPrefix());
        assertTrue(speaking.hasStartAnimation());
        assertTrue(speaking.hasEndAnimation());

        // Verify playback state
        SequenceEventState state = speaking.getPlayback();
        assertEquals(PlaybackPhase.START, state.currentPhase);
        assertFalse(state.isPlaying);
    }

    /**
     * Test sequence signals with chaining between sequences.
     * This simulates the S65_ZAMEK scenario where multiple sequences interact.
     */
    @Test
    void testSequenceSignalChaining() {
        // Create sequences that will chain to each other
        SequenceVariable seqA = new SequenceVariable("SEQ_A");
        SequenceVariable seqB = new SequenceVariable("SEQ_B");

        SequenceEvent eventA1 = new SequenceEvent("A1", EventType.SIMPLE);
        SequenceEvent eventB1 = new SequenceEvent("B1", EventType.SIMPLE);

        seqA.addEvent(eventA1);
        seqB.addEvent(eventB1);

        // First wrap seqB so we can capture its signals
        seqB = wrapSequenceForCapture(seqB, "SEQ_B", null, null);

        // Use a holder to allow referencing the final seqB from the lambda
        final SequenceVariable[] seqBHolder = new SequenceVariable[]{seqB};

        // When A1 finishes, play B1
        seqA = wrapSequenceForCapture(seqA, "SEQ_A", null, () -> {
            seqBHolder[0].callMethod("PLAY", new StringValue("B1"));
        });

        // Start sequence A
        seqA.callMethod("PLAY", new StringValue("A1"));
        assertEquals("SEQ_A_ONSTARTED^A1", capturedSignals.get(0));

        // Stop A1 with signal - should trigger B1
        seqA.callMethod("STOP", new pl.genschu.bloomooemulator.interpreter.values.BoolValue(true));

        // Verify A finished and B started
        assertEquals(3, capturedSignals.size());
        assertEquals("SEQ_A_ONFINISHED^A1", capturedSignals.get(1));
        assertEquals("SEQ_B_ONSTARTED^B1", capturedSignals.get(2));
    }

    /**
     * Test sequence mode types.
     */
    @Test
    void testSequenceModes() {
        // Test SEQUENCE mode
        SequenceEvent seqMode = new SequenceEvent("SEQ", EventType.SEQUENCE, SequenceMode.SEQUENCE, null, false, false);
        assertEquals(SequenceMode.SEQUENCE, seqMode.getMode());

        // Test RANDOM mode
        SequenceEvent randomMode = new SequenceEvent("RAND", EventType.SEQUENCE, SequenceMode.RANDOM, null, false, false);
        assertEquals(SequenceMode.RANDOM, randomMode.getMode());

        // Test PARAMETER mode
        SequenceEvent paramMode = new SequenceEvent("PARAM", EventType.SEQUENCE, SequenceMode.PARAMETER, null, false, false);
        assertEquals(SequenceMode.PARAMETER, paramMode.getMode());
    }

    /**
     * Test that playback state is correctly copied.
     */
    @Test
    void testPlaybackStateCopy() {
        SequenceEventState state = new SequenceEventState();
        state.isPlaying = true;
        state.isPaused = true;
        state.currentPhase = PlaybackPhase.MAIN;
        state.currentAnimationNumber = 5;
        state.isOnFinishedWrapped = true;

        SequenceEventState copy = state.copy();

        assertTrue(copy.isPlaying);
        assertTrue(copy.isPaused);
        assertEquals(PlaybackPhase.MAIN, copy.currentPhase);
        assertEquals(5, copy.currentAnimationNumber);
        assertTrue(copy.isOnFinishedWrapped);

        // Verify independence
        copy.isPlaying = false;
        assertTrue(state.isPlaying);
    }

    /**
     * Test sequence state copy.
     */
    @Test
    void testSequenceStateCopy() {
        SequenceState state = new SequenceState();
        state.isPlaying = true;
        state.isPaused = true;
        state.filename = "test.seq";
        state.animosInSequence.add("ANIMO1");
        state.parametersMapping.put("EVENT1", 2);

        SequenceState copy = state.copy();

        assertTrue(copy.isPlaying);
        assertTrue(copy.isPaused);
        assertEquals("test.seq", copy.filename);
        assertTrue(copy.animosInSequence.contains("ANIMO1"));
        assertEquals(2, copy.parametersMapping.get("EVENT1"));

        // Verify independence
        copy.animosInSequence.add("ANIMO2");
        assertEquals(1, state.animosInSequence.size());
    }

    /**
     * Test GETEVENTNAME and ISPLAYING methods.
     */
    @Test
    void testSequenceQueryMethods() {
        SequenceVariable seq = new SequenceVariable("QUERY_SEQ");

        // GETEVENTNAME returns empty when not playing
        MethodResult result = seq.callMethod("GETEVENTNAME");
        assertEquals("", ((StringValue) result.returnValue()).value());

        // ISPLAYING returns false
        result = seq.callMethod("ISPLAYING");
        assertFalse(((pl.genschu.bloomooemulator.interpreter.values.BoolValue) result.returnValue()).value());

        // Add event and play
        SequenceEvent event = new SequenceEvent("PLAYING_EVENT", EventType.SIMPLE);
        seq.addEvent(event);
        seq.callMethod("PLAY", new StringValue("PLAYING_EVENT"));

        // Now queries should return correct values
        result = seq.callMethod("GETEVENTNAME");
        assertEquals("PLAYING_EVENT", ((StringValue) result.returnValue()).value());

        result = seq.callMethod("ISPLAYING");
        assertTrue(((pl.genschu.bloomooemulator.interpreter.values.BoolValue) result.returnValue()).value());
    }
}
