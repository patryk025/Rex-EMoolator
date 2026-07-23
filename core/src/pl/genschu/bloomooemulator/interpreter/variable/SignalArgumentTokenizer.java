package pl.genschu.bloomooemulator.interpreter.variable;

import java.util.ArrayList;
import java.util.List;

/** Reconstructs CXString::toVector used by signal behaviour specifications. */
public final class SignalArgumentTokenizer {
    public enum Dialect {
        PIKLIB8,
        BLOOMOO
    }

    private SignalArgumentTokenizer() {
    }

    public static List<String> tokenize(String text) {
        return tokenize(text, Dialect.PIKLIB8);
    }

    public static List<String> tokenize(String text, Dialect dialect) {
        if (text == null || text.isEmpty()) {
            return List.of();
        }

        List<String> result = new ArrayList<>();
        int tokenStart = -1;
        for (int i = 0; i < text.length(); i++) {
            if (isDelimiter(text.charAt(i), dialect)) {
                if (tokenStart >= 0) {
                    result.add(text.substring(tokenStart, i));
                    tokenStart = -1;
                }
            } else if (tokenStart < 0) {
                tokenStart = i;
            }
        }
        if (tokenStart >= 0) {
            result.add(text.substring(tokenStart));
        }
        return List.copyOf(result);
    }

    private static boolean isDelimiter(char value, Dialect dialect) {
        return value == ',' || value == ' '
            || (dialect == Dialect.BLOOMOO && (value == '\n' || value == '\r'));
    }
}
