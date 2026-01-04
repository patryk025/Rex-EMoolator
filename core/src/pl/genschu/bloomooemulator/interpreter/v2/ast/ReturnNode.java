package pl.genschu.bloomooemulator.interpreter.v2.ast;

import pl.genschu.bloomooemulator.interpreter.v2.errors.SourceLocation;

/**
 * Represents a @RETURN statement.
 * Returns a value from the current behaviour.
 */
public record ReturnNode(
    ASTNode value,  // Can be null for void return
    SourceLocation location
) implements ASTNode {

    public ReturnNode {
        // value can be null
        if (location == null) {
            location = SourceLocation.UNKNOWN;
        }
    }

    /**
     * Returns true if this return statement has a value.
     */
    public boolean hasValue() {
        return value != null;
    }
}
