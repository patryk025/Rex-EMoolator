package pl.genschu.bloomooemulator.interpreter.v2.ast;

import pl.genschu.bloomooemulator.interpreter.v2.errors.SourceLocation;

/**
 * Represents a for loop statement.
 * Example: @LOOP("{body}", start, diff, step)
 *
 * Executes body for i = start; i < start + diff; i += step
 */
public record LoopNode(
    ASTNode start,    // Starting value
    ASTNode diff,     // Difference (end - start)
    ASTNode step,     // Step increment
    ASTNode body,     // Body to execute
    SourceLocation location
) implements ASTNode {

    public LoopNode {
        if (start == null || diff == null || step == null || body == null) {
            throw new IllegalArgumentException("Loop parameters cannot be null");
        }
        if (location == null) {
            location = SourceLocation.UNKNOWN;
        }
    }
}
