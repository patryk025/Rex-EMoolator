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
import pl.genschu.bloomooemulator.interpreter.variable.types.*;
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
            if(arg != null && arg.toString().matches(".*_[0-9]+$")) { // ignore it
                superEmit.run();
                return;
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

        loadWithMockedPath("koguty", filename, kogut);

        CapturingAnimo kurator = new CapturingAnimo("KURATOR", ctx, 1000);
        ctx.setVariable("KURATOR", kurator);

        filename = "kurator.ann";
        kurator.setAttribute("FILENAME", new Attribute("STRING", filename.toUpperCase()));

        loadWithMockedPath("koguty", filename, kurator);

        CapturingSequence seq = new CapturingSequence("GADAJA", ctx, 1000);
        ctx.setVariable("GADAJA", seq);

        filename = "gadaja.seq";
        seq.setAttribute("FILENAME", new Attribute("STRING", filename.toUpperCase()));

        loadWithMockedPath("koguty", filename, seq);

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

    @Test
    void testPlaySequence2() {
        // S65_ZAMEK - sequence split into separate characters
        signals.clear();

        ctx.setGame(new Game(null, null)); // faking game

        AnimoVariable zabbaAnimo = new AnimoVariable("ANNZABA", ctx);
        ctx.setVariable("ANNZABA", zabbaAnimo);
        String filename = "zaba.ann";
        zabbaAnimo.setAttribute("FILENAME", new Attribute("STRING", filename.toUpperCase()));
        loadWithMockedPath("s65_Zamek", filename, zabbaAnimo);

        AnimoVariable rexAnimo = new AnimoVariable("ANNREKSIO", ctx);
        ctx.setVariable("ANNREKSIO", rexAnimo);
        filename = "reksio.ann";
        rexAnimo.setAttribute("FILENAME", new Attribute("STRING", filename.toUpperCase()));
        loadWithMockedPath("s65_Zamek", filename, rexAnimo);

        AnimoVariable kretesAnimo = new AnimoVariable("ANNKRET", ctx);
        ctx.setVariable("ANNKRET", kretesAnimo);
        filename = "kret.ann";
        kretesAnimo.setAttribute("FILENAME", new Attribute("STRING", filename.toUpperCase()));
        loadWithMockedPath("s65_Zamek", filename, kretesAnimo);

        CapturingSequence zabba = new CapturingSequence("SEQZABBA", ctx, 30);
        ctx.setVariable("SEQZABBA", zabba);

        CapturingSequence rex = new CapturingSequence("SEQREKSIO", ctx, 30);
        ctx.setVariable("SEQREKSIO", rex);

        CapturingSequence kretes = new CapturingSequence("SEQKRET", ctx, 30);
        ctx.setVariable("SEQKRET", kretes);

        filename = "zabba65.seq";
        zabba.setAttribute("FILENAME", new Attribute("STRING", filename.toUpperCase()));
        loadWithMockedPath("s65_Zamek", filename, zabba);

        filename = "reks65.seq";
        rex.setAttribute("FILENAME", new Attribute("STRING", filename.toUpperCase()));
        loadWithMockedPath("s65_Zamek", filename, rex);

        filename = "kret65.seq";
        kretes.setAttribute("FILENAME", new Attribute("STRING", filename.toUpperCase()));
        loadWithMockedPath("s65_Zamek", filename, kretes);

        // simulating game flow
        zabba.setSignal("ONFINISHED^1K10", new Signal() {
            @Override
            public void execute(Object argument) {
                kretes.fireMethod("PLAY", new StringVariable("", "1K11", ctx));
            }
        });
        zabba.setSignal("ONFINISHED^1K12", new Signal() {
            @Override
            public void execute(Object argument) {
                kretes.fireMethod("PLAY", new StringVariable("", "1K13", ctx));
            }
        });
        kretes.setSignal("ONFINISHED^1K1", new Signal() {
            @Override
            public void execute(Object argument) {
                rex.fireMethod("PLAY", new StringVariable("", "1K2", ctx));
            }
        });
        kretes.setSignal("ONFINISHED^1K3", new Signal() {
            @Override
            public void execute(Object argument) {
                rex.fireMethod("PLAY", new StringVariable("", "1K4", ctx));
            }
        });
        kretes.setSignal("ONFINISHED^1K5", new Signal() {
            @Override
            public void execute(Object argument) {
                rex.fireMethod("PLAY", new StringVariable("", "1K6", ctx));
            }
        });
        kretes.setSignal("ONFINISHED^1K7", new Signal() {
            @Override
            public void execute(Object argument) {
                rex.fireMethod("PLAY", new StringVariable("", "1K8", ctx));
            }
        });
        kretes.setSignal("ONFINISHED^1K9", new Signal() {
            @Override
            public void execute(Object argument) {
                zabba.fireMethod("PLAY", new StringVariable("", "1K10", ctx));
            }
        });
        kretes.setSignal("ONFINISHED^1K11", new Signal() {
            @Override
            public void execute(Object argument) {
                zabba.fireMethod("PLAY", new StringVariable("", "1K12", ctx));
            }
        });
        kretes.setSignal("ONFINISHED^1K13", new Signal() {
            @Override
            public void execute(Object argument) {
                rex.fireMethod("PLAY", new StringVariable("", "1K14", ctx));
            }
        });
        rex.setSignal("ONFINISHED^1K2", new Signal() {
            @Override
            public void execute(Object argument) {
                kretes.fireMethod("PLAY", new StringVariable("", "1K3", ctx));
            }
        });
        rex.setSignal("ONFINISHED^1K4", new Signal() {
            @Override
            public void execute(Object argument) {
                kretes.fireMethod("PLAY", new StringVariable("", "1K5", ctx));
            }
        });
        rex.setSignal("ONFINISHED^1K6", new Signal() {
            @Override
            public void execute(Object argument) {
                kretes.fireMethod("PLAY", new StringVariable("", "1K7", ctx));
            }
        });
        rex.setSignal("ONFINISHED^1K8", new Signal() {
            @Override
            public void execute(Object argument) {
                kretes.fireMethod("PLAY", new StringVariable("", "1K9", ctx));
            }
        });

        kretes.fireMethod("PLAY", new StringVariable("", "1K1", ctx));

        // very hacky way to simulate time passing
        List<String> eventNames = List.of(
                "1K1", "1K2", "1K3", "1K4", "1K5", "1K6", "1K7", "1K8", "1K9", "1K10", "1K11", "1K12", "1K13", "1K14"
        );

        Map<String, AnimoVariable> animoMap = Map.ofEntries( // Map.of has a limit of 10 entries, so we use Map.ofEntries
                Map.entry("1K1", kretesAnimo),
                Map.entry("1K2", rexAnimo),
                Map.entry("1K3", kretesAnimo),
                Map.entry("1K4", rexAnimo),
                Map.entry("1K5", kretesAnimo),
                Map.entry("1K6", rexAnimo),
                Map.entry("1K7", kretesAnimo),
                Map.entry("1K8", rexAnimo),
                Map.entry("1K9", kretesAnimo),
                Map.entry("1K10", zabbaAnimo),
                Map.entry("1K11", kretesAnimo),
                Map.entry("1K12", zabbaAnimo),
                Map.entry("1K13", kretesAnimo),
                Map.entry("1K14", rexAnimo)
        );

        Map<String, CapturingSequence> seqMap = Map.ofEntries(
                Map.entry("1K1", kretes),
                Map.entry("1K2", rex),
                Map.entry("1K3", kretes),
                Map.entry("1K4", rex),
                Map.entry("1K5", kretes),
                Map.entry("1K6", rex),
                Map.entry("1K7", kretes),
                Map.entry("1K8", rex),
                Map.entry("1K9", kretes),
                Map.entry("1K10", zabba),
                Map.entry("1K11", kretes),
                Map.entry("1K12", zabba),
                Map.entry("1K13", kretes),
                Map.entry("1K14", rex)
        );

        for (String event : eventNames) {
            AnimoVariable animo = animoMap.get(event);
            CapturingSequence seq = seqMap.get(event);
            if (animo != null) {
                animo.fireMethod("STOP");
            }
            if (seq != null && seq.getEventsByName().containsKey(event)) {
                Variable sound = seq.getEventsByName().get(event).getSound();
                if (sound != null) {
                    sound.fireMethod("STOP", new BoolVariable("", true, ctx));
                }
            }
            if (animo != null) {
                animo.fireMethod("STOP");
            }
        }

        // 1K14 to podsekwencja ze czterema animacjami, więc zatrzymujemy ją cztery razy
        for (int i = 0; i < 4; i++) {
            rexAnimo.fireMethod("STOP");
        }

        List<String> expected = List.of(
                "SEQKRET_ONSTARTED^1K1",
                "SEQKRET_ONFINISHED^1K1",
                "SEQREKSIO_ONSTARTED^1K2",
                "SEQREKSIO_ONFINISHED^1K2",
                "SEQKRET_ONSTARTED^1K3",
                "SEQKRET_ONFINISHED^1K3",
                "SEQREKSIO_ONSTARTED^1K4",
                "SEQREKSIO_ONFINISHED^1K4",
                "SEQKRET_ONSTARTED^1K5",
                "SEQKRET_ONFINISHED^1K5",
                "SEQREKSIO_ONSTARTED^1K6",
                "SEQREKSIO_ONFINISHED^1K6",
                "SEQKRET_ONSTARTED^1K7",
                "SEQKRET_ONFINISHED^1K7",
                "SEQREKSIO_ONSTARTED^1K8",
                "SEQREKSIO_ONFINISHED^1K8",
                "SEQKRET_ONSTARTED^1K9",
                "SEQKRET_ONFINISHED^1K9",
                "SEQZABBA_ONSTARTED^1K10",
                "SEQZABBA_ONFINISHED^1K10",
                "SEQKRET_ONSTARTED^1K11",
                "SEQKRET_ONFINISHED^1K11",
                "SEQZABBA_ONSTARTED^1K12",
                "SEQZABBA_ONFINISHED^1K12",
                "SEQKRET_ONSTARTED^1K13",
                "SEQKRET_ONFINISHED^1K13",
                "SEQREKSIO_ONSTARTED^1K14",
                "SEQREKSIO_ONFINISHED^1K14"
        );

        assertEquals(expected, signals);
    }

    private void loadWithMockedPath(String assetDirectory, String filename, Variable variable) {
        try (MockedStatic<FileUtils> fu = Mockito.mockStatic(FileUtils.class)) {
            String absPath = Gdx.files.internal("../assets/test-assets/" + assetDirectory + "/" + filename).file().getAbsolutePath();
            fu.when(() -> FileUtils.resolveRelativePath(Mockito.any(Variable.class)))
                    .thenReturn(absPath);

            variable.init();
        }
    }
}
