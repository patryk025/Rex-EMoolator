package pl.genschu.bloomooemulator.interpreter.v2.ast;

import pl.genschu.bloomooemulator.interpreter.v2.errors.SourceLocation;

/**
 * Represents pointer dereferencing: *expr
 * Used to access the value that a variable reference points to.
 */
public record PointerDerefNode(ASTNode expression, SourceLocation location) implements ASTNode {

    public PointerDerefNode {
        if (expression == null) {
            throw new IllegalArgumentException("Pointer dereference expression cannot be null");
        }
        if (location == null) {
            location = SourceLocation.UNKNOWN;
        }
    }
}
