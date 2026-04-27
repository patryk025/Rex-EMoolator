package pl.genschu.bloomooemulator.interpreter.ast;

import pl.genschu.bloomooemulator.interpreter.errors.SourceLocation;

/**
 * Represents a logical operation: AND (&&), OR (||)
 * Returns a boolean value.
 */
public record LogicalNode(
    ASTNode left,
    ASTNode right,
    LogicalOp operator,
    SourceLocation location
) implements ASTNode {

    public LogicalNode {
        if (left == null || right == null) {
            throw new IllegalArgumentException("Logical operands cannot be null");
        }
        if (operator == null) {
            throw new IllegalArgumentException("Logical operator cannot be null");
        }
        if (location == null) {
            location = SourceLocation.UNKNOWN;
        }
    }

    public enum LogicalOp {
        AND("&&"),
        OR("||");

        private final String symbol;

        LogicalOp(String symbol) {
            this.symbol = symbol;
        }

        public String symbol() {
            return symbol;
        }

        public static LogicalOp fromString(String s) {
            return switch (s) {
                case "&&" -> AND;
                case "||" -> OR;
                default -> throw new IllegalArgumentException("Unknown operator: " + s);
            };
        }
    }
}
