package pl.cba.genszu.amcodetranslator.interpreter.util;

public class TypeGuesser {
    public static String guessType(String string) {
		if(string.equals("TRUE") || string.equals("FALSE"))
			return "BOOL";
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
