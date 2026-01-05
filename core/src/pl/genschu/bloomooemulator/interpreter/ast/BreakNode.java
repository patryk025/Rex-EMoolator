package pl.genschu.bloomooemulator.interpreter.ast;

import pl.genschu.bloomooemulator.interpreter.errors.SourceLocation;

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
