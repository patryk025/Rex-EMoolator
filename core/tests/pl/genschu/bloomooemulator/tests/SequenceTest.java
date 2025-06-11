package pl.genschu.bloomooemulator.tests;

import com.badlogic.gdx.Gdx;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import pl.genschu.bloomooemulator.TestEnvironment;
import pl.genschu.bloomooemulator.builders.ContextBuilder;
import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.variable.Attribute;
import pl.genschu.bloomooemulator.interpreter.variable.Signal;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.interpreter.variable.types.AnimoVariable;
import pl.genschu.bloomooemulator.interpreter.variable.types.SequenceVariable;
import pl.genschu.bloomooemulator.interpreter.variable.types.StringVariable;
import pl.genschu.bloomooemulator.utils.FileUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class SequenceTest {
    private static List<String> signals = new ArrayList<>();
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

        while(seq.isPlaying()) {
            Set<Variable> animos = seq.getAnimosInSequence();

            boolean anyPlaying = false;

            for(Variable animo : animos) {
                CapturingAnimo ca = (CapturingAnimo) animo;
                if(ca.isPlaying()) {
                    anyPlaying = true;
                    float frameTime = 1f / ca.getFps();
                    ca.updateAnimation(frameTime);
                }
            }

            if(!anyPlaying) {
                fail("Sequence is stuck");
            }
        }

        List<String> expected = List.of(
                "GADAJA_ONSTARTED^SEKWEN",
                "KOGUT_ONFRAMECHANGED^GADA_START",
                "KOGUT_ONSTARTED^GADA_START",
                "KOGUT_ONFRAMECHANGED^GADA_START;6",
                "KOGUT_ONFINISHED^GADA_START",
                "KOGUT_ONFRAMECHANGED^GADA_*",
                "KOGUT_ONSTARTED^GADA_*",
                "KOGUT_ONFRAMECHANGED^GADA_*;11",
                "KOGUT_ONFINISHED^GADA_*",
                "KOGUT_ONFRAMECHANGED^GADA_*",
                "KOGUT_ONSTARTED^GADA_*",
                "KOGUT_ONFRAMECHANGED^GADA_*;11",
                "KOGUT_ONFINISHED^GADA_*",
                "KOGUT_ONFRAMECHANGED^GADA_*",
                "KOGUT_ONSTARTED^GADA_*",
                "KOGUT_ONFRAMECHANGED^GADA_*;11",
                "KOGUT_ONFINISHED^GADA_*",
                "KOGUT_ONFRAMECHANGED^GADA_*",
                "KOGUT_ONSTARTED^GADA_*",
                "KOGUT_ONFRAMECHANGED^GADA_*;6",
                "KOGUT_ONFINISHED^GADA_*",
                "KOGUT_ONFRAMECHANGED^GADA_*",
                "KOGUT_ONSTARTED^GADA_*",
                "KOGUT_ONFRAMECHANGED^GADA_*;6",
                "KOGUT_ONFINISHED^GADA_*",
                "KOGUT_ONFRAMECHANGED^GADA_*",
                "KOGUT_ONSTARTED^GADA_*",
                "KOGUT_ONFRAMECHANGED^GADA_*;6",
                "KOGUT_ONFINISHED^GADA_*",
                "KOGUT_ONFRAMECHANGED^GADA_*",
                "KOGUT_ONSTARTED^GADA_*",
                "KOGUT_ONFRAMECHANGED^GADA_*;6",
                "KOGUT_ONFRAMECHANGED^GADA_STOP",
                "KOGUT_ONSTARTED^GADA_STOP",
                "KOGUT_ONFRAMECHANGED^GADA_STOP;6",
                "KOGUT_ONFINISHED^GADA_STOP",
                "KURATOR_ONFRAMECHANGED^GADA_START",
                "KURATOR_ONSTARTED^GADA_START",
                "GADAJA_ONFINISHED^SEKWEN",
                "KURATOR_ONFRAMECHANGED^GADA_START;2",
                "KURATOR_ONFINISHED^GADA_START",
                "KURATOR_ONFRAMECHANGED^GADA_*",
                "KURATOR_ONSTARTED^GADA_*",
                "KURATOR_ONFRAMECHANGED^GADA_*;13",
                "KURATOR_ONFINISHED^GADA_*",
                "KURATOR_ONFRAMECHANGED^GADA_*",
                "KURATOR_ONSTARTED^GADA_*",
                "KURATOR_ONFRAMECHANGED^GADA_*;3",
                "KURATOR_ONFINISHED^GADA_*",
                "KURATOR_ONFRAMECHANGED^GADA_*",
                "KURATOR_ONSTARTED^GADA_*",
                "KURATOR_ONFRAMECHANGED^GADA_*;3",
                "KURATOR_ONFINISHED^GADA_*",
                "KURATOR_ONFRAMECHANGED^GADA_*",
                "KURATOR_ONSTARTED^GADA_*",
                "KURATOR_ONFRAMECHANGED^GADA_*;9",
                "KURATOR_ONFINISHED^GADA_*",
                "KURATOR_ONFRAMECHANGED^GADA_*",
                "KURATOR_ONSTARTED^GADA_*",
                "KURATOR_ONFRAMECHANGED^GADA_*;9",
                "KURATOR_ONFINISHED^GADA_*",
                "KURATOR_ONFRAMECHANGED^GADA_*",
                "KURATOR_ONSTARTED^GADA_*",
                "KURATOR_ONFRAMECHANGED^GADA_*;9",
                "KURATOR_ONFINISHED^GADA_*",
                "KURATOR_ONFRAMECHANGED^GADA_*",
                "KURATOR_ONSTARTED^GADA_*",
                "KURATOR_ONFRAMECHANGED^GADA_*;9",
                "KURATOR_ONFINISHED^GADA_*",
                "KURATOR_ONFRAMECHANGED^GADA_*",
                "KURATOR_ONSTARTED^GADA_*",
                "KURATOR_ONFRAMECHANGED^GADA_*",
                "KURATOR_ONFRAMECHANGED^GADA_STOP",
                "KURATOR_ONSTARTED^GADA_STOP",
                "KURATOR_ONFRAMECHANGED^GADA_STOP;2",
                "KURATOR_ONFINISHED^GADA_STOP",
                "KOGUT_ONFRAMECHANGED^GADA_START",
                "KOGUT_ONSTARTED^GADA_START",
                "KOGUT_ONFRAMECHANGED^GADA_START;6",
                "KOGUT_ONFINISHED^GADA_START",
                "KOGUT_ONFRAMECHANGED^GADA_*",
                "KOGUT_ONSTARTED^GADA_*",
                "KOGUT_ONFRAMECHANGED^GADA_*;9",
                "KOGUT_ONFINISHED^GADA_*",
                "KOGUT_ONFRAMECHANGED^GADA_*",
                "KOGUT_ONSTARTED^GADA_*",
                "KOGUT_ONFRAMECHANGED^GADA_*;9",
                "KOGUT_ONFINISHED^GADA_*",
                "KOGUT_ONFRAMECHANGED^GADA_*",
                "KOGUT_ONSTARTED^GADA_*",
                "KOGUT_ONFRAMECHANGED^GADA_*;6",
                "KOGUT_ONFINISHED^GADA_*",
                "KOGUT_ONFRAMECHANGED^GADA_*",
                "KOGUT_ONSTARTED^GADA_*",
                "KOGUT_ONFRAMECHANGED^GADA_*;9",
                "KOGUT_ONFINISHED^GADA_*",
                "KOGUT_ONFRAMECHANGED^GADA_*",
                "KOGUT_ONSTARTED^GADA_*",
                "KOGUT_ONFRAMECHANGED^GADA_*;6",
                "KOGUT_ONFINISHED^GADA_*",
                "KOGUT_ONFRAMECHANGED^GADA_*",
                "KOGUT_ONSTARTED^GADA_*",
                "KOGUT_ONFRAMECHANGED^GADA_*;11",
                "KOGUT_ONFINISHED^GADA_*",
                "KOGUT_ONFRAMECHANGED^GADA_*",
                "KOGUT_ONSTARTED^GADA_*",
                "KOGUT_ONFRAMECHANGED^GADA_*;11",
                "KOGUT_ONFINISHED^GADA_*",
                "KOGUT_ONFRAMECHANGED^GADA_*",
                "KOGUT_ONSTARTED^GADA_*",
                "KOGUT_ONFRAMECHANGED^GADA_*;6",
                "KOGUT_ONFINISHED^GADA_*",
                "KOGUT_ONFRAMECHANGED^GADA_*",
                "KOGUT_ONSTARTED^GADA_*",
                "KOGUT_ONFRAMECHANGED^GADA_*;11",
                "KOGUT_ONFINISHED^GADA_*",
                "KOGUT_ONFRAMECHANGED^GADA_*",
                "KOGUT_ONSTARTED^GADA_*",
                "KOGUT_ONFRAMECHANGED^GADA_*;6",
                "KOGUT_ONFINISHED^GADA_*",
                "KOGUT_ONFRAMECHANGED^GADA_*",
                "KOGUT_ONSTARTED^GADA_*",
                "KOGUT_ONFRAMECHANGED^GADA_*;10",
                "KOGUT_ONFRAMECHANGED^GADA_STOP",
                "KOGUT_ONSTARTED^GADA_STOP",
                "KOGUT_ONFRAMECHANGED^GADA_STOP;6",
                "KOGUT_ONFINISHED^GADA_STOP",
                "KURATOR_ONFRAMECHANGED^GADA_START",
                "KURATOR_ONSTARTED^GADA_START",
                "KURATOR_ONFRAMECHANGED^GADA_START;2",
                "KURATOR_ONFINISHED^GADA_START",
                "KURATOR_ONFRAMECHANGED^GADA_*",
                "KURATOR_ONSTARTED^GADA_*",
                "KURATOR_ONFRAMECHANGED^GADA_*;3",
                "KURATOR_ONFINISHED^GADA_*",
                "KURATOR_ONFRAMECHANGED^GADA_*",
                "KURATOR_ONSTARTED^GADA_*",
                "KURATOR_ONFRAMECHANGED^GADA_*;3",
                "KURATOR_ONFINISHED^GADA_*",
                "KURATOR_ONFRAMECHANGED^GADA_*",
                "KURATOR_ONSTARTED^GADA_*",
                "KURATOR_ONFRAMECHANGED^GADA_*;13",
                "KURATOR_ONFINISHED^GADA_*",
                "KURATOR_ONFRAMECHANGED^GADA_*",
                "KURATOR_ONSTARTED^GADA_*",
                "KURATOR_ONFRAMECHANGED^GADA_*;13",
                "KURATOR_ONFINISHED^GADA_*",
                "KURATOR_ONFRAMECHANGED^GADA_*",
                "KURATOR_ONSTARTED^GADA_*",
                "KURATOR_ONFRAMECHANGED^GADA_*;3",
                "KURATOR_ONFINISHED^GADA_*",
                "KURATOR_ONFRAMECHANGED^GADA_*",
                "KURATOR_ONSTARTED^GADA_*",
                "KURATOR_ONFRAMECHANGED^GADA_*;9",
                "KURATOR_ONFINISHED^GADA_*",
                "KURATOR_ONFRAMECHANGED^GADA_*",
                "KURATOR_ONSTARTED^GADA_*",
                "KURATOR_ONFRAMECHANGED^GADA_*;3",
                "KURATOR_ONFINISHED^GADA_*",
                "KURATOR_ONFRAMECHANGED^GADA_*",
                "KURATOR_ONSTARTED^GADA_*",
                "KURATOR_ONFRAMECHANGED^GADA_*;13",
                "KURATOR_ONFINISHED^GADA_*",
                "KURATOR_ONFRAMECHANGED^GADA_*",
                "KURATOR_ONSTARTED^GADA_*",
                "KURATOR_ONFRAMECHANGED^GADA_*;4",
                "KURATOR_ONFRAMECHANGED^GADA_STOP",
                "KURATOR_ONSTARTED^GADA_STOP",
                "KURATOR_ONFRAMECHANGED^GADA_STOP;2",
                "KURATOR_ONFINISHED^GADA_STOP",
                "KOGUT_ONFRAMECHANGED^GADA_START",
                "KOGUT_ONSTARTED^GADA_START",
                "KOGUT_ONFRAMECHANGED^GADA_START;6",
                "KOGUT_ONFINISHED^GADA_START",
                "KOGUT_ONFRAMECHANGED^GADA_*",
                "KOGUT_ONSTARTED^GADA_*",
                "KOGUT_ONFRAMECHANGED^GADA_*;6",
                "KOGUT_ONFINISHED^GADA_*",
                "KOGUT_ONFRAMECHANGED^GADA_*",
                "KOGUT_ONSTARTED^GADA_*",
                "KOGUT_ONFRAMECHANGED^GADA_*;11",
                "KOGUT_ONFINISHED^GADA_*",
                "KOGUT_ONFRAMECHANGED^GADA_*",
                "KOGUT_ONSTARTED^GADA_*",
                "KOGUT_ONFRAMECHANGED^GADA_*;9",
                "KOGUT_ONFINISHED^GADA_*",
                "KOGUT_ONFRAMECHANGED^GADA_*",
                "KOGUT_ONSTARTED^GADA_*",
                "KOGUT_ONFRAMECHANGED^GADA_*;6",
                "KOGUT_ONFINISHED^GADA_*",
                "KOGUT_ONFRAMECHANGED^GADA_*",
                "KOGUT_ONSTARTED^GADA_*",
                "KOGUT_ONFRAMECHANGED^GADA_*;6",
                "KOGUT_ONFINISHED^GADA_*",
                "KOGUT_ONFRAMECHANGED^GADA_*",
                "KOGUT_ONSTARTED^GADA_*",
                "KOGUT_ONFRAMECHANGED^GADA_*;9",
                "KOGUT_ONFINISHED^GADA_*",
                "KOGUT_ONFRAMECHANGED^GADA_*",
                "KOGUT_ONSTARTED^GADA_*",
                "KOGUT_ONFRAMECHANGED^GADA_*;11",
                "KOGUT_ONFINISHED^GADA_*",
                "KOGUT_ONFRAMECHANGED^GADA_*",
                "KOGUT_ONSTARTED^GADA_*",
                "KOGUT_ONFRAMECHANGED^GADA_*;3",
                "KOGUT_ONFRAMECHANGED^GADA_STOP",
                "KOGUT_ONSTARTED^GADA_STOP",
                "KOGUT_ONFRAMECHANGED^GADA_STOP;6",
                "KOGUT_ONFINISHED^GADA_STOP",
                "KURATOR_ONFRAMECHANGED^GADA_START",
                "KURATOR_ONSTARTED^GADA_START",
                "KURATOR_ONFRAMECHANGED^GADA_START;2",
                "KURATOR_ONFINISHED^GADA_START",
                "KURATOR_ONFRAMECHANGED^GADA_*",
                "KURATOR_ONSTARTED^GADA_*",
                "KURATOR_ONFRAMECHANGED^GADA_*;9",
                "KURATOR_ONFINISHED^GADA_*",
                "KURATOR_ONFRAMECHANGED^GADA_*",
                "KURATOR_ONSTARTED^GADA_*",
                "KURATOR_ONFRAMECHANGED^GADA_*;9",
                "KURATOR_ONFINISHED^GADA_*",
                "KURATOR_ONFRAMECHANGED^GADA_*",
                "KURATOR_ONSTARTED^GADA_*",
                "KURATOR_ONFRAMECHANGED^GADA_*;3",
                "KURATOR_ONFINISHED^GADA_*",
                "KURATOR_ONFRAMECHANGED^GADA_*",
                "KURATOR_ONSTARTED^GADA_*",
                "KURATOR_ONFRAMECHANGED^GADA_*;3",
                "KURATOR_ONFINISHED^GADA_*",
                "KURATOR_ONFRAMECHANGED^GADA_*",
                "KURATOR_ONSTARTED^GADA_*",
                "KURATOR_ONFRAMECHANGED^GADA_*;3",
                "KURATOR_ONFINISHED^GADA_*",
                "KURATOR_ONFRAMECHANGED^GADA_*",
                "KURATOR_ONSTARTED^GADA_*",
                "KURATOR_ONFRAMECHANGED^GADA_*;13",
                "KURATOR_ONFINISHED^GADA_*",
                "KURATOR_ONFRAMECHANGED^GADA_*",
                "KURATOR_ONSTARTED^GADA_*",
                "KURATOR_ONFRAMECHANGED^GADA_*;13",
                "KURATOR_ONFINISHED^GADA_*",
                "KURATOR_ONFRAMECHANGED^GADA_*",
                "KURATOR_ONSTARTED^GADA_*",
                "KURATOR_ONFRAMECHANGED^GADA_*;9",
                "KURATOR_ONFINISHED^GADA_*",
                "KURATOR_ONFRAMECHANGED^GADA_*",
                "KURATOR_ONSTARTED^GADA_*",
                "KURATOR_ONFRAMECHANGED^GADA_*;9",
                "KURATOR_ONFRAMECHANGED^GADA_STOP",
                "KURATOR_ONSTARTED^GADA_STOP",
                "KURATOR_ONFRAMECHANGED^GADA_STOP;2",
                "KURATOR_ONFINISHED^GADA_STOP;2",
                "GADAJA_ONFINISHED^SEKWEN;2",
                "KOGUT_ONFRAMECHANGED^PRZEWRACA",
                "KOGUT_ONSTARTED^PRZEWRACA",
                "KOGUT_ONFRAMECHANGED^PRZEWRACA;8",
                "KOGUT_ONFINISHED^PRZEWRACA",
                "GADAJA_ONSTARTED^FINALKOG",
                "KOGUT_ONFRAMECHANGED^GADA_START",
                "KOGUT_ONSTARTED^GADA_START",
                "KOGUT_ONFRAMECHANGED^GADA_START;6",
                "KOGUT_ONFINISHED^GADA_START",
                "KOGUT_ONFRAMECHANGED^GADA_*",
                "KOGUT_ONSTARTED^GADA_*",
                "KOGUT_ONFRAMECHANGED^GADA_*;9",
                "KOGUT_ONFINISHED^GADA_*",
                "KOGUT_ONFRAMECHANGED^GADA_*",
                "KOGUT_ONSTARTED^GADA_*",
                "KOGUT_ONFRAMECHANGED^GADA_*;9",
                "KOGUT_ONFINISHED^GADA_*",
                "KOGUT_ONFRAMECHANGED^GADA_*",
                "KOGUT_ONSTARTED^GADA_*",
                "KOGUT_ONFRAMECHANGED^GADA_*;6",
                "KOGUT_ONFINISHED^GADA_*",
                "KOGUT_ONFRAMECHANGED^GADA_*",
                "KOGUT_ONSTARTED^GADA_*",
                "KOGUT_ONFRAMECHANGED^GADA_*;9",
                "KOGUT_ONFRAMECHANGED^GADA_STOP",
                "KOGUT_ONSTARTED^GADA_STOP",
                "KOGUT_ONFRAMECHANGED^GADA_STOP;6",
                "KOGUT_ONFINISHED^GADA_STOP",
                "GADAJA_ONFINISHED^FINALKOG"
        );

        List<String> prepared = prepareSignalsList(expected);

        assertEquals(prepared, signals);
    }

    private static List<String> prepareSignalsList(List<String> signals) {
        List<String> result = new ArrayList<>();
        for (String signal : signals) {
            if(signal.contains(";")) {
                String[] parts = signal.split(";");
                String signalName = parts[0];
                int amount = Integer.parseInt(parts[1]);
                for(int i = 0; i < amount; i++) result.add(signalName);
            }
            else {
                result.add(signal);
            }
        }
        return result;
    }
}
