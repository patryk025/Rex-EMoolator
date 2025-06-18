package pl.genschu.bloomooemulator.tests;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.TimeUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import pl.genschu.bloomooemulator.TestEnvironment;
import pl.genschu.bloomooemulator.builders.ContextBuilder;
import pl.genschu.bloomooemulator.engine.Game;
import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.variable.Attribute;
import pl.genschu.bloomooemulator.interpreter.variable.Signal;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.interpreter.variable.types.AnimoVariable;
import pl.genschu.bloomooemulator.interpreter.variable.types.SequenceVariable;
import pl.genschu.bloomooemulator.interpreter.variable.types.SoundVariable;
import pl.genschu.bloomooemulator.interpreter.variable.types.StringVariable;
import pl.genschu.bloomooemulator.utils.FileUtils;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class SequenceTest {
    private static final List<String> signals = new ArrayList<>();
    private Context ctx;

    // helpers for capturing signals
    static abstract class CapturingVariable<T> {
        int eventLimit;

        CapturingVariable(int eventLimit) {
            this.eventLimit = eventLimit;
        }

        public void emitSignal(Variable v, String s, Object arg, Runnable superEmit) {
            if (signals.size() >= eventLimit) throw new RuntimeException("Too many signals");
            if(arg != null && arg.toString().matches(".*_[0-9]+$")) { // remove event number
                arg = arg.toString().replaceFirst("_[0-9]+$", "_*");
            }
            if(!s.contains("ONFRAMECHANGED") && (
                    v instanceof CapturingSequence ||
                    (v instanceof CapturingAnimo && (arg == null || arg.toString().endsWith("_START") || arg.toString().endsWith("_STOP")))
                )
            )
            	signals.add(v.getName()+"_"+(arg == null ? s : s + "^" + arg));
            superEmit.run();
        }
    }

    static class CapturingAnimo extends AnimoVariable {
        List<Integer> frames = new ArrayList<>();
        List<Integer> images = new ArrayList<>();
        CapturingVariable<AnimoVariable> helper;

        CapturingAnimo(String name, Context ctx) { this(name, ctx, Integer.MAX_VALUE); }
        CapturingAnimo(String name, Context ctx, int eventLimit) {
            super(name, ctx);
            helper = new CapturingVariable<>(eventLimit) {};
        }
        @Override
        public void emitSignal(String s, Object arg) {
            helper.emitSignal(this, s, arg, () -> {
                frames.add(getCurrentFrameNumber());
                images.add(getCurrentImageNumber());
                super.emitSignal(s, arg);
            });
        }
    }

    static class CapturingSequence extends SequenceVariable {
        CapturingVariable<SequenceVariable> helper;

        CapturingSequence(String name, Context ctx) { this(name, ctx, Integer.MAX_VALUE); }
        CapturingSequence(String name, Context ctx, int eventLimit) {
            super(name, ctx);
            helper = new CapturingVariable<>(eventLimit) {};
        }
        @Override
        public void emitSignal(String s, Object arg) {
            helper.emitSignal(this, s, arg, () -> super.emitSignal(s, arg));
        }
    }

    @BeforeAll
    static void boot() {
        TestEnvironment.init();
        //TestEnvironment.enableLogs();
    }

    @BeforeEach
    void setUp() {
        ctx = new ContextBuilder().build();
    }

    @Test
    void testPlaySequence() {
        signals.clear();
        CapturingAnimo kogut = new CapturingAnimo("KOGUT", ctx, 1000);
        ctx.setVariable("KOGUT", kogut);

        ctx.setGame(new Game(null, null)); // faking game

        String filename = "kogut.ann";
        kogut.setAttribute("FILENAME", new Attribute("STRING", filename.toUpperCase()));

        try (MockedStatic<FileUtils> fu = Mockito.mockStatic(FileUtils.class)) {
            String absPath = Gdx.files.internal("../assets/test-assets/koguty/"+filename).file().getAbsolutePath();
            fu.when(() -> FileUtils.resolveRelativePath(Mockito.any(Variable.class)))
                    .thenReturn(absPath);

            kogut.init();
        }

        CapturingAnimo kurator = new CapturingAnimo("KURATOR", ctx, 1000);
        ctx.setVariable("KURATOR", kurator);

        filename = "kurator.ann";
        kurator.setAttribute("FILENAME", new Attribute("STRING", filename.toUpperCase()));

        try (MockedStatic<FileUtils> fu = Mockito.mockStatic(FileUtils.class)) {
            String absPath = Gdx.files.internal("../assets/test-assets/koguty/"+filename).file().getAbsolutePath();
            fu.when(() -> FileUtils.resolveRelativePath(Mockito.any(Variable.class)))
                    .thenReturn(absPath);

            kurator.init();
        }

        CapturingSequence seq = new CapturingSequence("GADAJA", ctx, 1000);
        ctx.setVariable("GADAJA", seq);

        filename = "gadaja.seq";
        seq.setAttribute("FILENAME", new Attribute("STRING", filename.toUpperCase()));

        try (MockedStatic<FileUtils> fu = Mockito.mockStatic(FileUtils.class)) {
            String absPath = Gdx.files.internal("../assets/test-assets/koguty/"+filename).file().getAbsolutePath();
            fu.when(() -> FileUtils.resolveRelativePath(Mockito.any(Variable.class)))
                    .thenReturn(absPath);

            seq.init();
        }

        kogut.setSignal("ONFINISHED^PRZEWRACA", new Signal() {
            @Override
            public void execute(Object argument) {
                seq.fireMethod("PLAY", new StringVariable("", "FINALKOG", ctx));
            }
        });

        seq.setSignal("ONFINISHED^SEKWEN", new Signal() {
            @Override
            public void execute(Object argument) {
                kogut.fireMethod("PLAY", new StringVariable("", "PRZEWRACA", ctx));
            }
        });

        seq.fireMethod("PLAY", new StringVariable("", "SEKWEN", ctx));

        Map<String, Integer> audioFilesTimes = new HashMap<>();
        audioFilesTimes.put("KOGUT_I019.WAV", 4047);
        audioFilesTimes.put("KURATOR_I020.WAV", 4021);
        audioFilesTimes.put("KOGUT_I021.WAV", 6686);
        audioFilesTimes.put("KURATOR_I022.WAV", 4599);
        audioFilesTimes.put("KOGUT_I023.WAV", 4323);
        audioFilesTimes.put("KURATOR_I024.WAV", 5073);
        audioFilesTimes.put("KOGUT_I025.WAV", 2321);

        Map<String, Variable> audioFiles = ctx.getSoundVariables();

        // insert duration times
        for(Map.Entry<String, Variable> entry : audioFiles.entrySet()) {
            String audioFilename = entry.getKey();
            SoundVariable audio = (SoundVariable) entry.getValue();
            audio.setDuration(audioFilesTimes.get(audioFilename)/1000f);
        }

        long fakeNow = TimeUtils.nanoTime();

        try (MockedStatic<TimeUtils> timeMock = Mockito.mockStatic(TimeUtils.class)) {
            Set<Variable> animos = seq.getAnimosInSequence();
            while (seq.isPlaying()) {
                boolean anyPlaying = false;

                for (Variable animo : animos) {
                    CapturingAnimo ca = (CapturingAnimo) animo;
                    if (ca.isPlaying()) {
                        anyPlaying = true;
                        float frameTime = 1f / ca.getFps();
                        long advance = (long)(frameTime * 1_000_000_000L);
                        fakeNow += advance;
                        timeMock.when(TimeUtils::nanoTime).thenReturn(fakeNow);
                        ca.updateAnimation(frameTime);
                    }
                }

                for (Map.Entry<String, Variable> entry : audioFiles.entrySet()) {
                    SoundVariable audio = (SoundVariable) entry.getValue();
                    if (audio.isPlaying()) {
                        anyPlaying = true;
                        timeMock.when(TimeUtils::nanoTime).thenReturn(fakeNow);
                        audio.update();
                    }
                }

                if (!anyPlaying) {
                    fail("Sequence is stuck");
                }
            }
        }

        List<String> expected = List.of(
                "GADAJA_ONSTARTED^SEKWEN",
                "GADAJA_ONSTARTED^KOGUTY1",
                "KOGUT_ONSTARTED^GADA_START",
                "KOGUT_ONFINISHED^GADA_START",
                "KOGUT_ONSTARTED^GADA_STOP",
                "KOGUT_ONFINISHED^GADA_STOP",
                "GADAJA_ONSTARTED^KOGUTY2",
                "KURATOR_ONSTARTED^GADA_START",
                "GADAJA_ONFINISHED^KOGUTY1",
                "KURATOR_ONFINISHED^GADA_START",
                "KURATOR_ONSTARTED^GADA_STOP",
                "KURATOR_ONFINISHED^GADA_STOP",
                "GADAJA_ONSTARTED^KOGUTY3",
                "KOGUT_ONSTARTED^GADA_START",
                "GADAJA_ONFINISHED^KOGUTY2",
                "KOGUT_ONFINISHED^GADA_START",
                "KOGUT_ONSTARTED^GADA_STOP",
                "KOGUT_ONFINISHED^GADA_STOP",
                "GADAJA_ONSTARTED^KOGUTY4",
                "KURATOR_ONSTARTED^GADA_START",
                "GADAJA_ONFINISHED^KOGUTY3",
                "KURATOR_ONFINISHED^GADA_START",
                "KURATOR_ONSTARTED^GADA_STOP",
                "KURATOR_ONFINISHED^GADA_STOP",
                "GADAJA_ONSTARTED^KOGUTY5",
                "KOGUT_ONSTARTED^GADA_START",
                "GADAJA_ONFINISHED^KOGUTY4",
                "KOGUT_ONFINISHED^GADA_START",
                "KOGUT_ONSTARTED^GADA_STOP",
                "KOGUT_ONFINISHED^GADA_STOP",
                "GADAJA_ONSTARTED^KOGUTY6",
                "KURATOR_ONSTARTED^GADA_START",
                "GADAJA_ONFINISHED^KOGUTY5",
                "KURATOR_ONFINISHED^GADA_START",
                "KURATOR_ONSTARTED^GADA_STOP",
                "KURATOR_ONFINISHED^GADA_STOP",
                "GADAJA_ONFINISHED^SEKWEN",
                "GADAJA_ONFINISHED^KOGUTY6",
                "GADAJA_ONSTARTED^FINALKOG",
                "KOGUT_ONSTARTED^GADA_START",
                "KOGUT_ONFINISHED^GADA_START",
                "KOGUT_ONSTARTED^GADA_STOP",
                "KOGUT_ONFINISHED^GADA_STOP",
                "GADAJA_ONFINISHED^FINALKOG"
        );
        
        assertEquals(expected, signals);
    }
}
