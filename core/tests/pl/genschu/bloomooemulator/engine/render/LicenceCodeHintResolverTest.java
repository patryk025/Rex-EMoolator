package pl.genschu.bloomooemulator.engine.render;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import pl.genschu.bloomooemulator.TestEnvironment;
import pl.genschu.bloomooemulator.interpreter.context.Context;
import pl.genschu.bloomooemulator.interpreter.runtime.ExecutionContext;
import pl.genschu.bloomooemulator.interpreter.variable.AnimoVariable;
import pl.genschu.bloomooemulator.interpreter.variable.BehaviourVariable;
import pl.genschu.bloomooemulator.interpreter.variable.ConditionVariable;
import pl.genschu.bloomooemulator.objects.Event;
import pl.genschu.bloomooemulator.objects.FrameData;
import pl.genschu.bloomooemulator.objects.Image;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class LicenceCodeHintResolverTest {

    @BeforeAll
    static void boot() {
        TestEnvironment.init();
    }

    @Test
    void resolvesClassicCodeToOriginalSymbolFrames() {
        Context context = new Context(new ExecutionContext());
        List<Image> images = symbolImages();
        context.setVariable("ZNACZKI1", symbolAnimation("1", images));

        BehaviourVariable check = BehaviourVariable.fromScript("B_CHECK",
                "{VAR_KOD^SET([ZNACZKI1^GETFRAMENAME()]);"
                        + "@IF(\"VAR_KOD\",\"!_\",\"6234\",\"B_LOST\",\"B_WIN\");}",
                Map.of());
        context.setVariable("B_CHECK", check);

        LicenceCodeHintResolver.Hint hint = LicenceCodeHintResolver.resolve(context).orElseThrow();

        assertEquals(List.of(images.get(5), images.get(1), images.get(2), images.get(3)), hint.symbols());
    }

    @Test
    void resolvesPiratesConditionsUsingZeroBasedFrameNames() {
        Context context = new Context(new ExecutionContext());
        List<Image> images = symbolImages();
        context.setVariable("ZNACZKI1", symbolAnimation("0", images));

        int[] code = {5, 4, 3, 2};
        for (int i = 0; i < code.length; i++) {
            int slot = i + 1;
            context.setVariable("CON" + slot, new ConditionVariable(
                    "CON" + slot,
                    "ARRAYHASLA^GET(" + slot + ")",
                    Integer.toString(code[i]),
                    "EQUAL"));
        }

        LicenceCodeHintResolver.Hint hint = LicenceCodeHintResolver.resolve(context).orElseThrow();

        assertEquals(List.of(images.get(4), images.get(3), images.get(2), images.get(1)), hint.symbols());
    }

    @Test
    void ignoresScenesWithoutARecognisedPictureCodeCheck() {
        Context context = new Context(new ExecutionContext());
        context.setVariable("ZNACZKI1", symbolAnimation("1", symbolImages()));

        BehaviourVariable unrelated = BehaviourVariable.fromScript(
                "B_CHECK", "{OTHER^RUN(1234);}", Map.of());
        context.setVariable("B_CHECK", unrelated);

        assertTrue(LicenceCodeHintResolver.resolve(context).isEmpty());
    }

    private static List<Image> symbolImages() {
        List<Image> images = new ArrayList<>(6);
        for (int i = 0; i < 6; i++) {
            images.add(mock(Image.class));
        }
        return images;
    }

    private static AnimoVariable symbolAnimation(String firstFrameName, List<Image> images) {
        int first = Integer.parseInt(firstFrameName);
        List<FrameData> frameData = new ArrayList<>(6);
        List<Integer> frameNumbers = new ArrayList<>(6);
        for (int i = 0; i < 6; i++) {
            FrameData frame = new FrameData();
            frame.setName(Integer.toString(first + i));
            frameData.add(frame);
            frameNumbers.add(i);
        }

        Event play = new Event();
        play.setName("PLAY");
        play.setFrameData(frameData);
        play.setFramesNumbers(frameNumbers);
        play.setFrames(List.of());

        AnimoVariable.AnimoData data = new AnimoVariable.AnimoData(
                List.of(play), images, images.size(), 1,
                16, 15, 255, 48, 48, "", "");
        return new AnimoVariable(
                "ZNACZKI1", new AnimoVariable.AnimoPlaybackState(), data, Map.of());
    }
}
