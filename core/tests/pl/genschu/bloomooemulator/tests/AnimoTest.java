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
        CapturingAnimo(String name, Context ctx) { super(name, ctx); }
        @Override
        public void emitSignal(String s, Object arg) {
            signals.add(arg == null ? s : s + "^" + arg);
            super.emitSignal(s, arg);
        }
    }

    @Test
    void testPlayAnimo() {
        Context ctx = new ContextBuilder().build();
        CapturingAnimo animo = new CapturingAnimo("ANIMOMLYNEK", ctx);
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
                "ONFINISHED^B"
        );
        assertEquals(expected, animo.signals);
    }
}
