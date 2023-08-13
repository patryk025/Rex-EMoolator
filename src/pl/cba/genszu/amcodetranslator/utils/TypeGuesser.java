package pl.cba.genszu.amcodetranslator.utils;

public class TypeGuesser {
    public static String guessNumber(String string) {
        try {
            double d = Double.parseDouble(string);
            if (d != Math.floor(d) || string.contains(".")) {
                return "DOUBLE";
            }
            else if ((d == Math.floor(d)) && !Double.isInfinite(d)) {
                return "INTEGER";
            }
        } catch (NumberFormatException e) {
            return "STRING";
        }
        return "STRING";
    }
}
