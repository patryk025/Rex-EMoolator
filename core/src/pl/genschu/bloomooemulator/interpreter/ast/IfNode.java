package pl.genschu.bloomooemulator.interpreter.ast;

import pl.genschu.bloomooemulator.interpreter.errors.SourceLocation;

/**
 * Represents an if-then-else statement.
 * Example: @IF(x > 5, "{code1}", "{code2}")
 */
public record IfNode(
    ASTNode condition,
    ASTNode thenBranch,
    ASTNode elseBranch,  // Can be null
    SourceLocation location
) implements ASTNode {

    public IfNode {
        if (condition == null) {
            throw new IllegalArgumentException("If condition cannot be null");
        }
        if (thenBranch == null) {
            throw new IllegalArgumentException("If then-branch cannot be null");
        }
        // elseBranch can be null
        if (location == null) {
            location = SourceLocation.UNKNOWN;
        }
    }

    /**
     * Returns true if this if statement has an else branch.
     */
    public boolean hasElse() {
        return elseBranch != null;
    }
}
