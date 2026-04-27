package pl.genschu.bloomooemulator.interpreter.ast;

import pl.genschu.bloomooemulator.interpreter.errors.SourceLocation;

/**
 * Represents a @ONEBREAK statement.
 * Breaks out of the immediately enclosing loop only.
 */
public record OneBreakNode(SourceLocation location) implements ASTNode {

    public OneBreakNode {
        if (location == null) {
            location = SourceLocation.UNKNOWN;
        }
    }
}
