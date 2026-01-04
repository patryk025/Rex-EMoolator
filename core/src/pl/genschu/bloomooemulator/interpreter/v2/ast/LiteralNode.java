package pl.genschu.bloomooemulator.interpreter.v2.ast;

import pl.genschu.bloomooemulator.interpreter.v2.errors.SourceLocation;
import pl.genschu.bloomooemulator.interpreter.v2.values.Value;

/**
 * Represents a literal constant value in the AST.
 * Examples: 42, 3.14, "hello", TRUE, FALSE
 */
public record LiteralNode(Value value, SourceLocation location) implements ASTNode {

    public LiteralNode {
        if (value == null) {
            throw new IllegalArgumentException("Literal value cannot be null (use NullValue.INSTANCE)");
        }
        if (location == null) {
            location = SourceLocation.UNKNOWN;
        }
    }
}
