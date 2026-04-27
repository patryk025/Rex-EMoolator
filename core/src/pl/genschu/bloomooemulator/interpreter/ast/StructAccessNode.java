package pl.genschu.bloomooemulator.interpreter.ast;

import pl.genschu.bloomooemulator.interpreter.errors.SourceLocation;

/**
 * Represents struct field access: TYPE|FIELD
 * Example: PLAYER|X, ENEMY|HEALTH
 */
public record StructAccessNode(
    String structName,
    String fieldName,
    SourceLocation location
) implements ASTNode {

    public StructAccessNode {
        if (structName == null || structName.isEmpty()) {
            throw new IllegalArgumentException("Struct name cannot be null or empty");
        }
        if (fieldName == null || fieldName.isEmpty()) {
            throw new IllegalArgumentException("Field name cannot be null or empty");
        }
        if (location == null) {
            location = SourceLocation.UNKNOWN;
        }
    }
}
