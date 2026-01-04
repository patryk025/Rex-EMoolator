package pl.genschu.bloomooemulator.interpreter.v2.ast;

import pl.genschu.bloomooemulator.interpreter.v2.errors.SourceLocation;

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
