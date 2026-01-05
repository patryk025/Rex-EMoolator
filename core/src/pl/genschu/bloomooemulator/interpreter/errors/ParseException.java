package pl.genschu.bloomooemulator.interpreter.errors;

/**
 * Exception thrown during AST building when parsing fails.
 * Contains source location information for better error messages.
 */
public class ParseException extends RuntimeException {
    private final SourceLocation location;

    public ParseException(String message, SourceLocation location) {
        super(formatMessage(message, location));
        this.location = location;
    }

    public ParseException(String message, SourceLocation location, Throwable cause) {
        super(formatMessage(message, location), cause);
        this.location = location;
    }

    public SourceLocation getLocation() {
        return location;
    }

    private static String formatMessage(String message, SourceLocation location) {
        if (location == null || location == SourceLocation.UNKNOWN) {
            return "Parse error: " + message;
        }

        return String.format(
            "Parse error at %s: %s",
            location.toString(),
            message
        );
    }
}
