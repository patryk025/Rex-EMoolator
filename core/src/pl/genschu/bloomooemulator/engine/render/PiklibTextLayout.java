package pl.genschu.bloomooemulator.engine.render;

import pl.genschu.bloomooemulator.geometry.shapes.Box2D;
import pl.genschu.bloomooemulator.interpreter.variable.FontVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Pure layout implementation for the CMultilineText6/CText6 conventions.
 */
public final class PiklibTextLayout {
    private static final Pattern NON_PRINTING_FORMAT_CODE =
            Pattern.compile("(?i)</?(?:(?:I|B|LS|US|U)|COLOR[^>]*)>");

    public record GlyphPlacement(char character, int x, int top) {}

    public record Line(String text, int x, int top, int width, List<GlyphPlacement> glyphs) {
        public Line {
            glyphs = List.copyOf(glyphs);
        }
    }

    public record Layout(List<Line> lines, int width, int height) {
        public Layout {
            lines = List.copyOf(lines);
        }
    }

    private PiklibTextLayout() {}

    public static Layout layout(
            FontVariable font,
            String text,
            Box2D rect,
            String horizontalJustify,
            String verticalJustify
    ) {
        if (font == null || rect == null || !font.isLoaded()) {
            return new Layout(List.of(), 0, 0);
        }

        List<String> lineTexts = splitAndWrap(font, visibleText(text), rect.getWidth());
        int fontHeight = font.getCharHeight();
        int lineCount = lineTexts.size();

        // CMultilineText6::getHeight subtracts 2 px per line boundary, while
        // Redraw advances consecutive lines by fontHeight - 4.
        int layoutHeight = lineCount == 0
                ? 0
                : lineCount * fontHeight - Math.max(0, lineCount - 1) * 2;
        int blockTop = verticalStart(rect, layoutHeight, verticalJustify);
        int lineStep = fontHeight - 4;

        List<Line> lines = new ArrayList<>(lineCount);
        int widest = 0;
        for (int i = 0; i < lineCount; i++) {
            String lineText = lineTexts.get(i);
            int lineWidth = measure(font, lineText);
            int lineX = horizontalStart(rect, lineWidth, horizontalJustify);
            int lineTop = blockTop + i * lineStep;
            List<GlyphPlacement> glyphs = placeGlyphs(font, lineText, lineX, lineTop);
            lines.add(new Line(lineText, lineX, lineTop, lineWidth, glyphs));
            widest = Math.max(widest, lineWidth);
        }
        return new Layout(lines, widest, layoutHeight);
    }

    public static int measure(FontVariable font, String text) {
        int width = 0;
        char previous = '\0';
        for (int i = 0; i < text.length(); i++) {
            char current = text.charAt(i);
            width += font.getAdvance(previous, current);
            previous = current;
        }
        return width;
    }

    static List<String> splitAndWrap(FontVariable font, String text, int maximumWidth) {
        String normalized = text.replace("\n", "").replace('\r', '|');
        String[] explicitLines = normalized.split("\\|", -1);
        List<String> lines = new ArrayList<>();
        for (String explicitLine : explicitLines) {
            wrapLine(font, explicitLine, maximumWidth, lines);
        }
        return lines;
    }

    private static void wrapLine(
            FontVariable font,
            String text,
            int maximumWidth,
            List<String> output
    ) {
        String trimmed = text.trim();
        if (trimmed.isEmpty()) {
            output.add("");
            return;
        }

        String[] words = trimmed.split(" +");
        String current = "";
        for (String word : words) {
            String candidate = current.isEmpty() ? word : current + " " + word;
            if (!current.isEmpty()
                    && maximumWidth > 0
                    && measure(font, candidate) > maximumWidth) {
                output.add(current);
                current = word;
            } else {
                current = candidate;
            }
        }
        output.add(current);
    }

    private static List<GlyphPlacement> placeGlyphs(
            FontVariable font,
            String text,
            int startX,
            int top
    ) {
        List<GlyphPlacement> placements = new ArrayList<>();
        int penX = startX;
        char previous = '\0';

        for (int i = 0; i < text.length(); i++) {
            char current = text.charAt(i);
            if (current != ' ' && current != '~' && font.hasCharacter(current)) {
                int pairAdjustment = previous != '\0'
                        && font.hasCharacter(previous)
                        ? font.getCharKerning(previous, current)
                        : 0;
                placements.add(new GlyphPlacement(current, penX - pairAdjustment, top));
            }
            penX += font.getAdvance(previous, current);
            previous = current;
        }
        return placements;
    }

    private static String visibleText(String text) {
        if (text == null) {
            return "";
        }
        int nul = text.indexOf('\0');
        String terminated = nul >= 0 ? text.substring(0, nul) : text;
        return NON_PRINTING_FORMAT_CODE.matcher(terminated).replaceAll("");
    }

    private static int horizontalStart(Box2D rect, int lineWidth, String justify) {
        return switch (normalized(justify)) {
            case "RIGHT" -> rect.getXRight() - lineWidth;
            case "CENTER", "CENTRE" -> rect.getXLeft() + (rect.getWidth() - lineWidth) / 2;
            default -> rect.getXLeft();
        };
    }

    private static int verticalStart(Box2D rect, int layoutHeight, String justify) {
        // In script RECT syntax the second coordinate is top and the fourth is
        // bottom, despite the legacy Box2D accessor names.
        return switch (normalized(justify)) {
            case "BOTTOM" -> rect.getYTop() - layoutHeight;
            case "CENTER", "CENTRE" ->
                    rect.getYBottom() + (rect.getHeight() - layoutHeight) / 2;
            default -> rect.getYBottom();
        };
    }

    private static String normalized(String value) {
        return value == null ? "" : value.trim().toUpperCase(Locale.ROOT);
    }
}
