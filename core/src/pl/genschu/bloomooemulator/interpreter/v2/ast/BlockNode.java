package pl.genschu.bloomooemulator.interpreter.v2.ast;

import pl.genschu.bloomooemulator.interpreter.v2.errors.SourceLocation;

import java.util.List;

/**
 * Represents a block of statements/expressions.
 * Executes each child in sequence and returns the value of the last one.
 *
 * Example: { stmt1; stmt2; stmt3 }
 */
public record BlockNode(List<ASTNode> statements, SourceLocation location) implements ASTNode {

    public BlockNode {
        if (statements == null) {
            statements = List.of();
        } else {
            // Make defensive copy to ensure immutability
            statements = List.copyOf(statements);
        }
        if (location == null) {
            location = SourceLocation.UNKNOWN;
        }
    }

    /**
     * Returns true if this block is empty.
     */
    public boolean isEmpty() {
        return statements.isEmpty();
    }

    /**
     * Returns the number of statements in this block.
     */
    public int size() {
        return statements.size();
    }
}
