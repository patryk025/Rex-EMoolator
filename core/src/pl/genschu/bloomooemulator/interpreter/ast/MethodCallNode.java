package pl.genschu.bloomooemulator.interpreter.ast;

import pl.genschu.bloomooemulator.interpreter.errors.SourceLocation;

import java.util.List;

/**
 * Represents a method call on an object.
 * Example: x^ADD(5), player^MOVE(10, 20)
 */
public record MethodCallNode(
    ASTNode target,        // The object to call the method on
    String methodName,     // Name of the method
    List<ASTNode> arguments, // Arguments to pass
    SourceLocation location
) implements ASTNode {

    public MethodCallNode {
        if (target == null) {
            throw new IllegalArgumentException("Method call target cannot be null");
        }
        if (methodName == null || methodName.isEmpty()) {
            throw new IllegalArgumentException("Method name cannot be null or empty");
        }
        if (arguments == null) {
            arguments = List.of();
        } else {
            arguments = List.copyOf(arguments);
        }
        if (location == null) {
            location = SourceLocation.UNKNOWN;
        }
    }
}
