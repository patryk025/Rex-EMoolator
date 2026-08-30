package pl.genschu.bloomooemulator.engine.render;

import pl.genschu.bloomooemulator.engine.context.EngineVariable;
import pl.genschu.bloomooemulator.engine.context.GameContext;
import pl.genschu.bloomooemulator.interpreter.variable.AnimoVariable;
import pl.genschu.bloomooemulator.interpreter.variable.BehaviourVariable;
import pl.genschu.bloomooemulator.interpreter.variable.ConditionVariable;
import pl.genschu.bloomooemulator.objects.Event;
import pl.genschu.bloomooemulator.objects.FrameData;
import pl.genschu.bloomooemulator.objects.Image;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Resolves the four picture symbols expected by a classic Mr Policeman scene.
 *
 * <p>The hint borrows images already owned by the scene's {@code ZNACZKI*.ANN}
 * animation. It never changes an animation frame and never owns or disposes a
 * texture. Unsupported protection scenes (including Kapitan Nemo) simply do not
 * match either known script shape.</p>
 */
final class LicenceCodeHintResolver {
    private static final Pattern CLASSIC_CODE = Pattern.compile("(?<![0-9])([1-6]{4})(?![0-9])");
    private static final List<String> SYMBOL_OBJECTS = List.of(
            "ZNACZKI1", "ZNACZKI2", "ZNACZKI3", "ZNACZKI4");

    private LicenceCodeHintResolver() {}

    static Optional<Hint> resolve(GameContext context) {
        if (context == null) {
            return Optional.empty();
        }

        Optional<CodeDefinition> code = resolveClassicCode(context);
        if (code.isEmpty()) {
            code = resolvePiratesCode(context);
        }
        if (code.isEmpty()) {
            return Optional.empty();
        }

        AnimoVariable symbolAnimation = findSymbolAnimation(context);
        if (symbolAnimation == null) {
            return Optional.empty();
        }

        List<Image> symbols = new ArrayList<>(4);
        for (String frameName : code.get().frameNames()) {
            Image image = findFrameImage(symbolAnimation, frameName);
            if (image == null) {
                return Optional.empty();
            }
            symbols.add(image);
        }
        return Optional.of(new Hint(symbols));
    }

    private static Optional<CodeDefinition> resolveClassicCode(GameContext context) {
        EngineVariable variable = context.getVariable("B_CHECK");
        if (!(variable instanceof BehaviourVariable behaviour)) {
            return Optional.empty();
        }
        String source = behaviour.sourceCode();
        String normalizedSource = source == null ? "" : source.toUpperCase(Locale.ROOT);
        if (!normalizedSource.contains("VAR_KOD")
                || !normalizedSource.contains("GETFRAMENAME")) {
            return Optional.empty();
        }

        Matcher matcher = CLASSIC_CODE.matcher(source);
        if (!matcher.find()) {
            return Optional.empty();
        }
        return Optional.of(new CodeDefinition(toCharacters(matcher.group(1))));
    }

    /**
     * Skarb Piratów validates four CONDITION objects. Its semantic button values
     * are 1..6, while the symbol animation frames are named 0..5.
     */
    private static Optional<CodeDefinition> resolvePiratesCode(GameContext context) {
        List<String> frameNames = new ArrayList<>(4);
        for (int i = 1; i <= 4; i++) {
            EngineVariable variable = context.getVariable("CON" + i);
            if (!(variable instanceof ConditionVariable condition)
                    || !condition.operand1().toUpperCase(Locale.ROOT).contains("ARRAYHASLA^GET(" + i + ")")
                    || !"EQUAL".equalsIgnoreCase(condition.operator())) {
                return Optional.empty();
            }
            int button;
            try {
                button = Integer.parseInt(condition.operand2().trim());
            } catch (NumberFormatException ignored) {
                return Optional.empty();
            }
            if (button < 1 || button > 6) {
                return Optional.empty();
            }
            frameNames.add(Integer.toString(button - 1));
        }
        return Optional.of(new CodeDefinition(frameNames));
    }

    private static List<String> toCharacters(String code) {
        List<String> result = new ArrayList<>(code.length());
        for (int i = 0; i < code.length(); i++) {
            result.add(String.valueOf(code.charAt(i)));
        }
        return result;
    }

    private static AnimoVariable findSymbolAnimation(GameContext context) {
        for (String name : SYMBOL_OBJECTS) {
            EngineVariable variable = context.getVariable(name);
            if (variable instanceof AnimoVariable animo && !animo.getEvents().isEmpty()) {
                return animo;
            }
        }
        return null;
    }

    private static Image findFrameImage(AnimoVariable animo, String frameName) {
        for (Event event : animo.getEvents()) {
            if (event == null || !"PLAY".equalsIgnoreCase(event.getName())) {
                continue;
            }
            List<FrameData> frameData = event.getFrameData();
            if (frameData == null) {
                continue;
            }
            for (int i = 0; i < frameData.size(); i++) {
                FrameData frame = frameData.get(i);
                if (frame == null || !frameName.equalsIgnoreCase(frame.getName())) {
                    continue;
                }

                List<Integer> imageNumbers = event.getFramesNumbers();
                if (imageNumbers != null && i < imageNumbers.size()) {
                    int imageNumber = imageNumbers.get(i);
                    if (imageNumber >= 0 && imageNumber < animo.getImages().size()) {
                        return animo.getImages().get(imageNumber);
                    }
                }
                List<Image> eventFrames = event.getFrames();
                if (eventFrames != null && i < eventFrames.size()) {
                    return eventFrames.get(i);
                }
                return null;
            }
        }
        return null;
    }

    record Hint(List<Image> symbols) {
        Hint {
            symbols = List.copyOf(symbols);
            if (symbols.size() != 4) {
                throw new IllegalArgumentException("A licence-code hint must contain four symbols");
            }
        }
    }

    private record CodeDefinition(List<String> frameNames) {
        CodeDefinition {
            frameNames = List.copyOf(frameNames);
        }
    }
}
