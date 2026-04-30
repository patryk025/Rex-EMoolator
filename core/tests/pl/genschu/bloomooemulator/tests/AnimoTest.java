package pl.genschu.bloomooemulator.tests;

import com.badlogic.gdx.Gdx;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import pl.genschu.bloomooemulator.TestEnvironment;
import pl.genschu.bloomooemulator.builders.ContextBuilder;
import pl.genschu.bloomooemulator.engine.Game;
import pl.genschu.bloomooemulator.engine.filesystem.LocalFileSystem;
import pl.genschu.bloomooemulator.engine.filesystem.VFS;
import pl.genschu.bloomooemulator.interpreter.context.Context;
import pl.genschu.bloomooemulator.interpreter.values.IntValue;
import pl.genschu.bloomooemulator.interpreter.values.StringValue;
import pl.genschu.bloomooemulator.interpreter.variable.*;
import pl.genschu.bloomooemulator.loader.CNVParser;
import pl.genschu.bloomooemulator.loader.AnimoLoader;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for AnimoVariable implementation using real animation files.
 */
class AnimoTest {

    @TempDir
    Path tempDir;

    @BeforeAll
    static void boot() {
        TestEnvironment.init();
    }

    /**
     * Helper class to capture signals from AnimoVariable.
     */
    static class SignalCapture {
        final List<String> signals = new ArrayList<>();
        final List<Integer> frames = new ArrayList<>();
        final List<Integer> images = new ArrayList<>();
        final int eventLimit;
        final Map<String, SignalHandler> additionalHandlers = new HashMap<>();

        SignalCapture() { this(Integer.MAX_VALUE); }
        SignalCapture(int eventLimit) { this.eventLimit = eventLimit; }

        void addHandler(String signalName, SignalHandler handler) {
            additionalHandlers.put(signalName, handler);
        }

        SignalHandler createHandler(String signalType) {
            return (var, signalName, args) -> {
                if (signals.size() >= eventLimit) throw new RuntimeException("Too many signals");
                AnimoVariable av = (AnimoVariable) var;
                String arg = args.length > 0 && args[0] instanceof StringValue(String value) ? value : null;
                String sigKey = arg == null ? signalName : signalName + "^" + arg;
                signals.add(sigKey);
                frames.add(av.getCurrentFrameNumber());
                images.add(av.getCurrentImageNumber());

                // Chain to additional handler if present
                SignalHandler additional = additionalHandlers.get(sigKey);
                if (additional != null) {
                    additional.handle(var, signalName, args);
                }
            };
        }

        AnimoVariable wrapAnimo(AnimoVariable animo) {
            return (AnimoVariable) animo
                .withSignal("ONSTARTED", createHandler("ONSTARTED"))
                .withSignal("ONFINISHED", createHandler("ONFINISHED"))
                .withSignal("ONFRAMECHANGED", createHandler("ONFRAMECHANGED"));
        }
    }

    @Test
    void testPlayAnimo() throws Exception {
        Context ctx = new ContextBuilder()
            .withVariable("INTEGER", "LOOP_NO", 0)
            .build();

        // Load animation data
        String filename = "MLYNEK.ANN";
        String absPath = Gdx.files.internal("../assets/test-assets/" + filename).file().getAbsolutePath();
        AnimoVariable.AnimoData data;
        try (InputStream is = new FileInputStream(absPath)) {
            data = AnimoLoader.load(is);
        }

        // Create signal capture
        SignalCapture capture = new SignalCapture();

        // Create AnimoVariable with data
        AnimoVariable animo = new AnimoVariable("ANIMOMLYNEK");
        animo = animo.withData(data);
        animo.setFps(data.fps());

        IntegerVariable loopNo = (IntegerVariable) ctx.getVariable("LOOP_NO");

        // Set up signal handlers
        capture.addHandler("ONFINISHED^B", (var, signal, args) -> {
            loopNo.callMethod("INC");
            if (loopNo.value().toInt().value() < 2) {
                var.callMethod("PLAY", new StringValue("B"));
            }
        });

        capture.addHandler("ONFRAMECHANGED^B", (var, signal, args) -> {
            AnimoVariable av = (AnimoVariable) var;
            if (loopNo.value().toInt().value() == 1 && av.getCurrentFrameNumber() == 2) {
                av.callMethod("PAUSE");
                av.callMethod("RESUME");
                av.callMethod("STOP");
            }
        });

        animo = capture.wrapAnimo(animo);
        ctx.setVariable("ANIMOMLYNEK", animo);

        // Sanity check
        assertTrue(animo.getEventsCount() > 0);

        // Play animation
        animo.callMethod("PLAY", new StringValue("B"));

        float frameTime = 1f / animo.getFps();
        while (animo.isPlaying()) {
            animo.updateAnimation(frameTime);
        }

        // Verify signals in strict order
        List<String> expected = List.of(
            "ONFRAMECHANGED^B",
            "ONSTARTED^B",
            "ONFRAMECHANGED^B",
            "ONFRAMECHANGED^B",
            "ONFRAMECHANGED^B",
            "ONFRAMECHANGED^B",
            "ONFRAMECHANGED^B",
            "ONFRAMECHANGED^B",
            "ONFINISHED^B",
            "ONFRAMECHANGED^B",
            "ONSTARTED^B",
            "ONFRAMECHANGED^B",
            "ONFRAMECHANGED^B",
            "ONFINISHED^B"
        );

        List<Integer> expectedFrames = List.of(0, 0, 1, 2, 3, 4, 5, 6, 6, 0, 0, 1, 2, 2);
        List<Integer> expectedImages = List.of(0, 0, 2, 4, 6, 8, 10, 12, 12, 0, 0, 2, 4, 4);

        assertEquals(expected, capture.signals);
        assertEquals(expectedFrames, capture.frames);
        assertEquals(expectedImages, capture.images);
    }

    @Test
    void testPlayAnimo2() throws Exception {
        Context ctx = new ContextBuilder().build();

        // Load animation data
        String filename = "odliczanie.ann";
        String absPath = Gdx.files.internal("../assets/test-assets/" + filename).file().getAbsolutePath();
        AnimoVariable.AnimoData data;
        try (InputStream is = new FileInputStream(absPath)) {
            data = AnimoLoader.load(is);
        }

        // Create signal capture
        SignalCapture capture = new SignalCapture();

        // Create AnimoVariable with data
        AnimoVariable animo = new AnimoVariable("ANNLAMPKI");
        animo = animo.withData(data);

        // Set up signal handlers
        capture.addHandler("ONFINISHED^PLAY", (var, signal, args) -> {
            var.callMethod("HIDE");
        });

        animo = capture.wrapAnimo(animo);
        ctx.setVariable("ANNLAMPKI", animo);

        // Sanity check
        assertTrue(animo.getEventsCount() > 0);

        // Play animation
        animo.callMethod("PLAY", new StringValue("PLAY"));

        float frameTime = 1f / animo.getFps();
        while (animo.isPlaying()) {
            animo.updateAnimation(frameTime);
        }

        // Verify signals
        List<String> expected = List.of(
            "ONFRAMECHANGED^PLAY",
            "ONSTARTED^PLAY",
            "ONFRAMECHANGED^PLAY",
            "ONFRAMECHANGED^PLAY",
            "ONFINISHED^PLAY"
        );

        List<Integer> expectedFrames = List.of(0, 0, 1, 2, 2);
        List<Integer> expectedImages = List.of(0, 0, 1, 2, 2);

        assertEquals(expected, capture.signals);
        assertEquals(expectedFrames, capture.frames);
        assertEquals(expectedImages, capture.images);
    }

    @Test
    void testPlayAnimo3() throws Exception {
        Context ctx = new ContextBuilder().build();

        // Load animation data
        String filename = "stl2.ann";
        String absPath = Gdx.files.internal("../assets/test-assets/" + filename).file().getAbsolutePath();
        AnimoVariable.AnimoData data;
        try (InputStream is = new FileInputStream(absPath)) {
            data = AnimoLoader.load(is);
        }

        // Create signal capture
        SignalCapture capture = new SignalCapture();

        // Create AnimoVariable with data
        AnimoVariable animo = new AnimoVariable("STLIDLE0");
        animo = animo.withData(data);
        animo = capture.wrapAnimo(animo);
        ctx.setVariable("STLIDLE0", animo);

        // Sanity check
        assertTrue(animo.getEventsCount() > 0);

        // Play animation (only one frame update)
        animo.callMethod("PLAY", new StringValue("ELAPSE"));

        float frameTime = 1f / animo.getFps();
        animo.updateAnimation(frameTime);

        // Verify signals
        List<String> expected = List.of(
            "ONFRAMECHANGED^ELAPSE",
            "ONSTARTED^ELAPSE"
        );

        List<Integer> expectedFrames = List.of(0, 0);
        List<Integer> expectedImages = List.of(0, 0);

        assertEquals(expected, capture.signals);
        assertEquals(expectedFrames, capture.frames);
        assertEquals(expectedImages, capture.images);
    }

    @Test
    void testPauseDuringRestartAnimo() throws Exception {
        // This test simulates the S65_ZAMEK scenario where animation restarts in a loop
        // and is paused when a counter reaches a threshold
        // Scene S65_ZAMEK with bugged SETOPACITY implementation
        // (need INVALIDATE method call for proper opacity reset)

        // Load animation data
        String filename = "st2.ann";
        String absPath = Gdx.files.internal("../assets/test-assets/" + filename).file().getAbsolutePath();
        AnimoVariable.AnimoData data;
        try (InputStream is = new FileInputStream(absPath)) {
            data = AnimoLoader.load(is);
        }

        // Create signal capture with limit
        SignalCapture capture = new SignalCapture(200);

        // Create AnimoVariable with data
        AnimoVariable animo = new AnimoVariable("ANNST2");
        animo = animo.withData(data);

        AnimoVariable finalAnimo = animo;
        IntegerVariable opacity = (IntegerVariable) new IntegerVariable("OPACITY", 255)
                .withSignal("ONCHANGED^0", (var, signal, args) -> finalAnimo.callMethod("PAUSE"))
                .withSignal("ONCHANGED^-1", (var, signal, args) -> finalAnimo.callMethod("PAUSE"))
                .withSignal("ONCHANGED^-2", (var, signal, args) -> finalAnimo.callMethod("PAUSE"))
                .withSignal("ONCHANGED^-3", (var, signal, args) -> finalAnimo.callMethod("PAUSE"))
                .withSignal("ONCHANGED^-4", (var, signal, args) -> finalAnimo.callMethod("PAUSE"));

        // Set up signal handlers for animation restart loop
        capture.addHandler("ONFINISHED^ELAPSE", (var, signal, args) -> {
            var.callMethod("PLAY", new StringValue("ELAPSE"));
        });

        capture.addHandler("ONFRAMECHANGED^ELAPSE", (var, signal, args) -> {
            opacity.callMethod("SUB", new IntValue(4));
        });

        animo = capture.wrapAnimo(animo);

        // Sanity check
        assertTrue(animo.getEventsCount() > 0);

        // Play animation
        animo.callMethod("PLAY", new StringValue("ELAPSE"));

        float frameTime = 1f / animo.getFps();
        while (animo.isPlaying()) {
            animo.updateAnimation(frameTime);
        }

        // Build expected signals
        List<String> expected = new ArrayList<>();
        for (int i = 0; i < 63; i++) {
            expected.add("ONFRAMECHANGED^ELAPSE");
            expected.add("ONSTARTED^ELAPSE");
            expected.add("ONFINISHED^ELAPSE");
        }
        expected.add("ONFRAMECHANGED^ELAPSE");
        expected.add("ONSTARTED^ELAPSE");

        assertEquals(expected, capture.signals);
    }

    @Test
    void testSecondAnimoWithSameFilenameBecomesAliasOfFirstLoadedInstance() throws Exception {
        Path sourceAnn = Gdx.files.internal("../assets/test-assets/MLYNEK.ANN").file().toPath();
        Files.copy(sourceAnn, tempDir.resolve("MLYNEK.ANN"));

        Path cnv = tempDir.resolve("SCENE.CNV");
        Files.writeString(cnv, """
            #
            #ANIMOKURA3
            #
            OBJECT=ANIMOKURA3
            ANIMOKURA3:TYPE=ANIMO
            ANIMOKURA3:FILENAME=MLYNEK.ANN
            ANIMOKURA3:PRIORITY=1000

            #
            #ANIMOKURA4
            #
            OBJECT=ANIMOKURA4
            ANIMOKURA4:TYPE=ANIMO
            ANIMOKURA4:FILENAME=MLYNEK.ANN
            ANIMOKURA4:PRIORITY=50000
            """);

        Context ctx = new ContextBuilder().build();
        Game game = mock(Game.class);
        when(game.getLanguage()).thenReturn("POL");
        VFS vfs = new VFS();
        vfs.mountAssets(new LocalFileSystem(tempDir.toFile()));
        when(game.getVfs()).thenReturn(vfs);
        ctx.setGame(game);

        try (java.io.InputStream cnvStream = new java.io.FileInputStream(cnv.toFile())) {
            new CNVParser().parse(cnvStream, cnv.getFileName().toString(), ctx);
        }

        AnimoVariable first = (AnimoVariable) ctx.getVariable("ANIMOKURA3");
        AnimoVariable second = (AnimoVariable) ctx.getVariable("ANIMOKURA4");

        assertNotNull(first);
        assertNotNull(second);
        assertTrue(first.getEventsCount() > 0);
        assertTrue(second.getEventsCount() > 0);
        assertEquals(1000, first.getPriority());
        assertEquals(1000, second.getPriority());
        assertEquals("ANIMOKURA4", second.name());
    }
}
