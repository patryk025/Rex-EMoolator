package pl.genschu.bloomooemulator.interpreter.v2.ast;

import pl.genschu.bloomooemulator.interpreter.v2.errors.SourceLocation;

/**
 * Represents a type conversion statement.
 * Example: @CONV(x, "INT")
 *
 * Converts a variable to a different type and returns the converted value.
 */
public record ConvNode(
    ASTNode variable,
    String targetType,
    SourceLocation location
) implements ASTNode {

    public ConvNode {
        if (variable == null) {
            throw new IllegalArgumentException("Conversion variable cannot be null");
        }
        if (targetType == null || targetType.isEmpty()) {
            throw new IllegalArgumentException("Target type cannot be null or empty");
        }
        if (location == null) {
            location = SourceLocation.UNKNOWN;
        }
    }
}
