package pl.genschu.bloomooemulator.interpreter.v2.errors;

/**
 * Represents a location in the source code.
 * Used for error reporting and debugging.
 */
public record SourceLocation(
    int line,
    int column,
    String snippet  // Short snippet of code at this location for display
) {
    /** Constant for unknown/synthetic locations. */
    public static final SourceLocation UNKNOWN = new SourceLocation(0, 0, "<unknown>");

    /**
     * Creates a SourceLocation with validation.
     */
    public SourceLocation {
        if (line < 0) line = 0;
        if (column < 0) column = 0;
        if (snippet == null) snippet = "";

        // Truncate snippet if too long
        if (snippet.length() > 80) {
            snippet = snippet.substring(0, 77) + "...";
        }
    }

    /**
     * Formats this location for display in error messages.
     */
    @Override
    public String toString() {
        if (this == UNKNOWN) {
            return "<unknown location>";
        }
        return String.format("line %d, col %d: %s", line, column, snippet);
    }

    /**
     * Returns a compact representation (just line:column).
     */
    public String toCompact() {
        if (this == UNKNOWN) {
            return "?:?";
        }
        return line + ":" + column;
    }
}
