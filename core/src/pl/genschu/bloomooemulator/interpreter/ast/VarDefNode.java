package pl.genschu.bloomooemulator.interpreter.ast;

import pl.genschu.bloomooemulator.interpreter.errors.SourceLocation;

/**
 * Represents a variable definition statement.
 * Example: @INT(x, 10), @STRING(name, "John")
 */
public record VarDefNode(
    String varType,    // INT, STRING, DOUBLE, BOOL
    String varName,    // Name of the variable
    ASTNode initialValue, // Initial value expression
    SourceLocation location
) implements ASTNode {

    public VarDefNode {
        if (varType == null || varType.isEmpty()) {
            throw new IllegalArgumentException("Variable type cannot be null or empty");
        }
        if (varName == null || varName.isEmpty()) {
            throw new IllegalArgumentException("Variable name cannot be null or empty");
        }
        if (initialValue == null) {
            throw new IllegalArgumentException("Initial value cannot be null");
        }
        if (location == null) {
            location = SourceLocation.UNKNOWN;
        }
    }
}
