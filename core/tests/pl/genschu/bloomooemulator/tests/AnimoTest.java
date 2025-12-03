package pl.genschu.bloomooemulator.tests;

import com.badlogic.gdx.Gdx;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import pl.genschu.bloomooemulator.TestEnvironment;
import pl.genschu.bloomooemulator.builders.ContextBuilder;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.genschu.bloomooemulator.interpreter.variable.Attribute;
import pl.genschu.bloomooemulator.interpreter.variable.Signal;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.interpreter.variable.types.AnimoVariable;
import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.variable.types.IntegerVariable;
import pl.genschu.bloomooemulator.interpreter.variable.types.StringVariable;
import pl.genschu.bloomooemulator.objects.Event;
import pl.genschu.bloomooemulator.utils.FileUtils;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class AnimoTest {
    @BeforeAll
    static void boot() {
        TestEnvironment.init();
    }

    // helper for capturing signals
    static class CapturingAnimo extends AnimoVariable {
        List<String> signals = new ArrayList<>();
        List<Integer> frames = new ArrayList<>();
        List<Integer> images = new ArrayList<>();

        int eventLimit;
        CapturingAnimo(String name, Context ctx) { this(name, ctx, Integer.MAX_VALUE); }
        CapturingAnimo(String name, Context ctx, int eventLimit) { super(name, ctx); this.eventLimit = eventLimit; }
        @Override
        public void emitSignal(String s, Object arg) {
            if(signals.size() >= eventLimit) throw new RuntimeException("Too many signals");
            signals.add(arg == null ? s : s + "^" + arg);
            frames.add(getCurrentFrameNumber());
            images.add(getCurrentImageNumber());
            super.emitSignal(s, arg);
        }
    }

    @Test
    void testPlayAnimo() {
        Context ctx = new ContextBuilder()
                .withFactory("INTEGER", "LOOP_NO", 0)
                .build();
        CapturingAnimo animo = new CapturingAnimo("ANIMOMLYNEK", ctx);
        ctx.setVariable("ANIMOMLYNEK", animo);

        IntegerVariable loopNo = (IntegerVariable) ctx.getVariable("LOOP_NO");

        animo.setSignal("ONFINISHED^B", new Signal() {
            @Override
            public void execute(Object argument) {
                loopNo.fireMethod("INC");
                if(loopNo.GET() < 2) {
                    animo.fireMethod("PLAY", new StringVariable("", "B", ctx));
                }
            }
        });

        animo.setSignal("ONFRAMECHANGED^B", new Signal() {
            @Override
            public void execute(Object argument) {
                if(loopNo.GET() == 1 && animo.getCurrentFrameNumber() == 2) {
                    animo.fireMethod("PAUSE");
                    animo.fireMethod("RESUME");
                    animo.fireMethod("STOP");
                }
            }
        });

        String filename = "MLYNEK.ANN";
        animo.setAttribute("FILENAME", new Attribute("STRING", filename));

        try (MockedStatic<FileUtils> fu = Mockito.mockStatic(FileUtils.class)) {
            String absPath = Gdx.files.internal("../assets/test-assets/"+filename).file().getAbsolutePath();
            fu.when(() -> FileUtils.resolveRelativePath(Mockito.any(Variable.class)))
                    .thenReturn(absPath);

            animo.init();
        }

        // sanity check – check if loader works
        assertTrue(animo.getEventsCount() > 0);

        // switch state machine to PLAYING on that event
        animo.fireMethod("PLAY", new StringVariable("", "B", ctx));

        float frameTime = 1f / animo.getFps();
        while(animo.isPlaying()) {
            animo.updateAnimation(frameTime);
        }

        // captured signals in strict order
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
        assertEquals(expected, animo.signals);
        assertEquals(expectedFrames, animo.frames);
        assertEquals(expectedImages, animo.images);
    }

    @Test
    void testPlayAnimo2() {
        Context ctx = new ContextBuilder().build();
        CapturingAnimo animo = new CapturingAnimo("ANNLAMPKI", ctx);
        ctx.setVariable("ANNLAMPKI", animo);

        animo.setSignal("ONFINISHED^PLAY", new Signal() {
            @Override
            public void execute(Object argument) {
                animo.fireMethod("HIDE");
            }
        });

        animo.setSignal("ONINIT", new Signal() {
            @Override
            public void execute(Object argument) {
                animo.fireMethod("HIDE");
                animo.fireMethod("SETFPS", new IntegerVariable("", 1, ctx));
            }
        });

        String filename = "odliczanie.ann";
        animo.setAttribute("FILENAME", new Attribute("STRING", filename));

        try (MockedStatic<FileUtils> fu = Mockito.mockStatic(FileUtils.class)) {
            String absPath = Gdx.files.internal("../assets/test-assets/"+filename).file().getAbsolutePath();
            fu.when(() -> FileUtils.resolveRelativePath(Mockito.any(Variable.class)))
                    .thenReturn(absPath);

            animo.init();
        }

        // sanity check – check if loader works
        assertTrue(animo.getEventsCount() > 0);

        // switch state machine to PLAYING on that event
        animo.fireMethod("PLAY", new StringVariable("", "PLAY", ctx));

        float frameTime = 1f / animo.getFps();
        while(animo.isPlaying()) {
            animo.updateAnimation(frameTime);
        }

        // captured signals in strict order
        List<String> expected = List.of(
                "ONFRAMECHANGED^PLAY",
                "ONSTARTED^PLAY",
                "ONFRAMECHANGED^PLAY",
                "ONFRAMECHANGED^PLAY",
                "ONFINISHED^PLAY"
        );

        List<Integer> expectedFrames = List.of(0, 0, 1, 2, 2);
        List<Integer> expectedImages = List.of(0, 0, 1, 2, 2);
        assertEquals(expected, animo.signals);
        assertEquals(expectedFrames, animo.frames);
        assertEquals(expectedImages, animo.images);
    }

    @Test
    void testPlayAnimo3() {
        Context ctx = new ContextBuilder().build();
        CapturingAnimo animo = new CapturingAnimo("STLIDLE0", ctx);
        ctx.setVariable("STLIDLE0", animo);

        String filename = "stl2.ann";
        animo.setAttribute("FILENAME", new Attribute("STRING", filename));

        try (MockedStatic<FileUtils> fu = Mockito.mockStatic(FileUtils.class)) {
            String absPath = Gdx.files.internal("../assets/test-assets/"+filename).file().getAbsolutePath();
            fu.when(() -> FileUtils.resolveRelativePath(Mockito.any(Variable.class)))
                    .thenReturn(absPath);

            animo.init();
        }

        // sanity check – check if loader works
        assertTrue(animo.getEventsCount() > 0);

        // switch state machine to PLAYING on that event
        animo.fireMethod("PLAY", new StringVariable("", "ELAPSE", ctx));

        float frameTime = 1f / animo.getFps();
        animo.updateAnimation(frameTime);

        // captured signals in strict order
        List<String> expected = List.of(
                "ONFRAMECHANGED^ELAPSE",
                "ONSTARTED^ELAPSE"
        );

        List<Integer> expectedFrames = List.of(0, 0);
        List<Integer> expectedImages = List.of(0, 0);
        assertEquals(expected, animo.signals);
        assertEquals(expectedFrames, animo.frames);
        assertEquals(expectedImages, animo.images);
    }

    @Test
    void testPauseDuringRestartAnimo() {
        // S65_ZAMEK
        Context ctx = new ContextBuilder()
                .withFactory("INTEGER", "VAROPACITY", 255)
                .build();
        CapturingAnimo animo = new CapturingAnimo("ANNST2", ctx, 200);
        ctx.setVariable("ANNST2", animo);

        IntegerVariable varOpacity = (IntegerVariable) ctx.getVariable("VAROPACITY");

        animo.setSignal("ONFINISHED^ELAPSE", new Signal() {
            @Override
            public void execute(Object argument) {
                animo.fireMethod("PLAY", new StringVariable("", "ELAPSE", ctx));
            }
        });

        animo.setSignal("ONFRAMECHANGED^ELAPSE", new Signal() {
            @Override
            public void execute(Object argument) {
                varOpacity.fireMethod("SUB", new IntegerVariable("", 4, ctx));
            }
        });

        varOpacity.setSignal("ONCHANGED^0", new Signal() {
            @Override
            public void execute(Object argument) {
                animo.fireMethod("PAUSE");
            }
        });

        varOpacity.setSignal("ONCHANGED^-1", new Signal() {
            @Override
            public void execute(Object argument) {
                animo.fireMethod("PAUSE");
            }
        });

        varOpacity.setSignal("ONCHANGED^-2", new Signal() {
            @Override
            public void execute(Object argument) {
                animo.fireMethod("PAUSE");
            }
        });

        varOpacity.setSignal("ONCHANGED^-3", new Signal() {
            @Override
            public void execute(Object argument) {
                animo.fireMethod("PAUSE");
            }
        });

        varOpacity.setSignal("ONCHANGED^-4", new Signal() {
            @Override
            public void execute(Object argument) {
                animo.fireMethod("PAUSE");
            }
        });

        String filename = "st2.ann";
        animo.setAttribute("FILENAME", new Attribute("STRING", filename));

        try (MockedStatic<FileUtils> fu = Mockito.mockStatic(FileUtils.class)) {
            String absPath = Gdx.files.internal("../assets/test-assets/"+filename).file().getAbsolutePath();
            fu.when(() -> FileUtils.resolveRelativePath(Mockito.any(Variable.class)))
                    .thenReturn(absPath);

            animo.init();
        }

        // sanity check – check if loader works
        assertTrue(animo.getEventsCount() > 0);

        // switch state machine to PLAYING on that event
        animo.fireMethod("PLAY", new StringVariable("", "ELAPSE", ctx));

        float frameTime = 1f / animo.getFps();
        while(animo.isPlaying()) {
            animo.updateAnimation(frameTime);
        }

        List<String> expected = new ArrayList<>();

        // captured signals in strict order
        for(int i=0; i<63; i++) {
            expected.add("ONFRAMECHANGED^ELAPSE");
            expected.add("ONSTARTED^ELAPSE");
            expected.add("ONFINISHED^ELAPSE");
        }
        expected.add("ONFRAMECHANGED^ELAPSE");
        expected.add("ONSTARTED^ELAPSE");

        assertEquals(expected, animo.signals);
    }
}
