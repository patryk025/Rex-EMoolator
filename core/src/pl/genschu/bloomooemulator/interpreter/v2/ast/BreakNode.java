package pl.genschu.bloomooemulator.interpreter.v2.ast;

import pl.genschu.bloomooemulator.interpreter.v2.errors.SourceLocation;

/**
 * Represents a @BREAK statement.
 * Breaks out of ALL enclosing loops and behaviours.
 */
public record BreakNode(SourceLocation location) implements ASTNode {

    public BreakNode {
        if (location == null) {
            location = SourceLocation.UNKNOWN;
        }
    }
}
