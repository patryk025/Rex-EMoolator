package pl.genschu.bloomooemulator.interpreter.ast;

import pl.genschu.bloomooemulator.interpreter.errors.SourceLocation;

/**
 * Represents a while loop statement.
 * Example: @WHILE(x, >, 0, "{body}")
 *
 * Executes body while condition is true.
 */
public record WhileNode(
    ASTNode condition,
    ASTNode body,
    SourceLocation location
) implements ASTNode {

    public WhileNode {
        if (condition == null || body == null) {
            throw new IllegalArgumentException("While parameters cannot be null");
        }
        if (location == null) {
            location = SourceLocation.UNKNOWN;
        }
    }
}
