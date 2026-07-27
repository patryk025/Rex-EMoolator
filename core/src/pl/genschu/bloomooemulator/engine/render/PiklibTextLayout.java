package pl.genschu.bloomooemulator.engine.render;

import pl.genschu.bloomooemulator.geometry.shapes.Box2D;
import pl.genschu.bloomooemulator.interpreter.variable.FontVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Pure layout implementation for the CMultilineText6/CText6 conventions.
 */
public final class PiklibTextLayout {
    public record GlyphPlacement(char character, int x, int top, int color) {}

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

    private record StyledCharacter(char character, int color) {}

    private record StyledLine(List<StyledCharacter> characters) {
        private StyledLine {
            characters = List.copyOf(characters);
        }

        private String text() {
            return textOf(characters);
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
        return layout(font, text, 0xFFFFFF, rect, horizontalJustify, verticalJustify);
    }

    public static Layout layout(
            FontVariable font,
            String text,
            int baseColor,
            Box2D rect,
            String horizontalJustify,
            String verticalJustify
    ) {
        if (font == null || rect == null || !font.isLoaded()) {
            return new Layout(List.of(), 0, 0);
        }

        List<StyledLine> lineTexts = splitAndWrapStyled(
                font,
                styledText(text, baseColor, font.getPixelFormat()),
                rect.getWidth()
        );
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
            StyledLine styledLine = lineTexts.get(i);
            String lineText = styledLine.text();
            int lineWidth = measure(font, lineText);
            int lineX = horizontalStart(rect, lineWidth, horizontalJustify);
            int lineTop = blockTop + i * lineStep;
            List<GlyphPlacement> glyphs = placeGlyphs(font, styledLine, lineX, lineTop);
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
        List<StyledLine> styledLines = splitAndWrapStyled(
                font,
                styledText(text, 0xFFFFFF, font.getPixelFormat()),
                maximumWidth
        );
        List<String> lines = new ArrayList<>(styledLines.size());
        for (StyledLine line : styledLines) {
            lines.add(line.text());
        }
        return lines;
    }

    private static List<StyledLine> splitAndWrapStyled(
            FontVariable font,
            List<StyledCharacter> text,
            int maximumWidth
    ) {
        List<StyledLine> lines = new ArrayList<>();
        List<StyledCharacter> explicitLine = new ArrayList<>();
        for (StyledCharacter character : text) {
            if (character.character() == '\n') {
                continue;
            }
            if (character.character() == '\r' || character.character() == '|') {
                wrapLine(font, explicitLine, maximumWidth, lines);
                explicitLine = new ArrayList<>();
            } else {
                explicitLine.add(character);
            }
        }
        wrapLine(font, explicitLine, maximumWidth, lines);
        return lines;
    }

    private static void wrapLine(
            FontVariable font,
            List<StyledCharacter> text,
            int maximumWidth,
            List<StyledLine> output
    ) {
        int start = 0;
        int end = text.size();
        while (start < end && text.get(start).character() <= ' ') {
            start++;
        }
        while (end > start && text.get(end - 1).character() <= ' ') {
            end--;
        }
        if (start == end) {
            output.add(new StyledLine(List.of()));
            return;
        }

        List<List<StyledCharacter>> words = new ArrayList<>();
        int index = start;
        while (index < end) {
            while (index < end && text.get(index).character() == ' ') {
                index++;
            }
            int wordStart = index;
            while (index < end && text.get(index).character() != ' ') {
                index++;
            }
            if (wordStart < index) {
                words.add(List.copyOf(text.subList(wordStart, index)));
            }
        }

        List<StyledCharacter> current = new ArrayList<>();
        for (List<StyledCharacter> word : words) {
            List<StyledCharacter> candidate = new ArrayList<>(current);
            if (!candidate.isEmpty()) {
                candidate.add(new StyledCharacter(' ', word.get(0).color()));
            }
            candidate.addAll(word);

            if (!current.isEmpty()
                    && maximumWidth > 0
                    && measure(font, textOf(candidate)) > maximumWidth) {
                output.add(new StyledLine(current));
                current = new ArrayList<>(word);
            } else {
                current = candidate;
            }
        }
        output.add(new StyledLine(current));
    }

    private static List<GlyphPlacement> placeGlyphs(
            FontVariable font,
            StyledLine line,
            int startX,
            int top
    ) {
        List<GlyphPlacement> placements = new ArrayList<>();
        int penX = startX;
        char previous = '\0';

        for (StyledCharacter styledCharacter : line.characters()) {
            char current = styledCharacter.character();
            if (current != ' ' && current != '~' && font.hasCharacter(current)) {
                int pairAdjustment = previous != '\0'
                        && font.hasCharacter(previous)
                        ? font.getCharKerning(previous, current)
                        : 0;
                placements.add(new GlyphPlacement(
                        current,
                        penX - pairAdjustment,
                        top,
                        styledCharacter.color()
                ));
            }
            penX += font.getAdvance(previous, current);
            previous = current;
        }
        return placements;
    }

    private static List<StyledCharacter> styledText(
            String text,
            int baseColor,
            int pixelFormat
    ) {
        if (text == null) {
            return List.of();
        }

        int currentColor = quantizeRgb(baseColor, pixelFormat);
        List<StyledCharacter> result = new ArrayList<>();
        for (int index = 0; index < text.length(); index++) {
            char character = text.charAt(index);
            if (character == '\0') {
                break;
            }

            if (character == '<') {
                int tagEnd = text.indexOf('>', index + 1);
                if (tagEnd >= 0) {
                    String code = text.substring(index + 1, tagEnd);
                    if (isStyleCode(code)) {
                        index = tagEnd;
                        continue;
                    }
                    Integer color = parseColorCode(code, pixelFormat);
                    if (color != null) {
                        currentColor = color;
                        index = tagEnd;
                        continue;
                    }
                }
            }

            result.add(new StyledCharacter(character, currentColor));
        }
        return result;
    }

    private static boolean isStyleCode(String code) {
        String value = normalized(code);
        if (value.startsWith("/")) {
            value = value.substring(1);
        }
        return switch (value) {
            case "I", "B", "LS", "US", "U" -> true;
            default -> false;
        };
    }

    private static Integer parseColorCode(String code, int pixelFormat) {
        String trimmed = code.trim();
        if (!trimmed.toUpperCase(Locale.ROOT).startsWith("COLOR")) {
            return null;
        }

        String payload = trimmed.substring(5).trim();
        if (payload.startsWith(",")) {
            payload = payload.substring(1);
        }
        if (payload.isEmpty()) {
            return 0;
        }

        try {
            if (payload.contains(",")) {
                String[] channels = payload.split(",", -1);
                if (channels.length != 3) {
                    return null;
                }
                int red = clampColorChannel(Integer.parseInt(channels[0].trim()));
                int green = clampColorChannel(Integer.parseInt(channels[1].trim()));
                int blue = clampColorChannel(Integer.parseInt(channels[2].trim()));
                return quantizeRgb((red << 16) | (green << 8) | blue, pixelFormat);
            }
            return decodeColorWord(Integer.parseInt(payload) & 0xFFFF, pixelFormat);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private static int quantizeRgb(int color, int pixelFormat) {
        int red = (color >>> 16) & 0xFF;
        int green = (color >>> 8) & 0xFF;
        int blue = color & 0xFF;
        int colorWord;
        if (pixelFormat == 15) {
            colorWord = ((red >> 3) << 10) | ((green >> 3) << 5) | (blue >> 3);
        } else {
            colorWord = ((red >> 3) << 11) | ((green >> 2) << 5) | (blue >> 3);
        }
        return decodeColorWord(colorWord, pixelFormat);
    }

    private static int decodeColorWord(int colorWord, int pixelFormat) {
        int red;
        int green;
        int blue;
        if (pixelFormat == 15) {
            int red5 = (colorWord >>> 10) & 0x1F;
            int green5 = (colorWord >>> 5) & 0x1F;
            int blue5 = colorWord & 0x1F;
            red = (red5 << 3) | (red5 >>> 2);
            green = (green5 << 3) | (green5 >>> 2);
            blue = (blue5 << 3) | (blue5 >>> 2);
        } else {
            int red5 = (colorWord >>> 11) & 0x1F;
            int green6 = (colorWord >>> 5) & 0x3F;
            int blue5 = colorWord & 0x1F;
            red = (red5 << 3) | (red5 >>> 2);
            green = (green6 << 2) | (green6 >>> 4);
            blue = (blue5 << 3) | (blue5 >>> 2);
        }
        return (red << 16) | (green << 8) | blue;
    }

    private static int clampColorChannel(int value) {
        return Math.max(0, Math.min(255, value));
    }

    private static String textOf(List<StyledCharacter> characters) {
        StringBuilder result = new StringBuilder(characters.size());
        for (StyledCharacter character : characters) {
            result.append(character.character());
        }
        return result.toString();
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
