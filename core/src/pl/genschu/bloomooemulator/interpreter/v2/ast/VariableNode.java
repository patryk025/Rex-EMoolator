package pl.genschu.bloomooemulator.interpreter.v2.ast;

import pl.genschu.bloomooemulator.interpreter.v2.errors.SourceLocation;

/**
 * Represents a variable reference in the AST.
 * This is resolved at runtime to get the actual variable value.
 *
 * Examples: x, counter, PLAYER
 */
public record VariableNode(String name, SourceLocation location) implements ASTNode {

    public VariableNode {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Variable name cannot be null or empty");
        }
        if (location == null) {
            location = SourceLocation.UNKNOWN;
        }
    }
}
