package pl.genschu.bloomooemulator.interpreter.ast;

import pl.genschu.bloomooemulator.interpreter.errors.SourceLocation;

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
