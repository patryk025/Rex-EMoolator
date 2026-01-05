package pl.genschu.bloomooemulator.interpreter.ast;

import pl.genschu.bloomooemulator.interpreter.errors.SourceLocation;

/**
 * Represents a comparison operation: ==, !=, <, >, <=, >=
 * Returns a boolean value.
 */
public record ComparisonNode(
    ASTNode left,
    ASTNode right,
    ComparisonOp operator,
    SourceLocation location
) implements ASTNode {

    public ComparisonNode {
        if (left == null || right == null) {
            throw new IllegalArgumentException("Comparison operands cannot be null");
        }
        if (operator == null) {
            throw new IllegalArgumentException("Comparison operator cannot be null");
        }
        if (location == null) {
            location = SourceLocation.UNKNOWN;
        }
    }

    public enum ComparisonOp {
        EQUAL("'"),
        NOT_EQUAL("!'"),
        LESS("<"),
        LESS_EQUAL("<'"),
        GREATER(">"),
        GREATER_EQUAL(">'");

        private final String symbol;

        ComparisonOp(String symbol) {
            this.symbol = symbol;
        }

        public String symbol() {
            return symbol;
        }

        public static ComparisonOp fromString(String s) {
            return switch (s) {
                case "'" -> EQUAL;
                case "!'" -> NOT_EQUAL;
                case "<" -> LESS;
                case "<'" -> LESS_EQUAL;
                case ">" -> GREATER;
                case ">'" -> GREATER_EQUAL;
                default -> throw new IllegalArgumentException("Unknown operator: " + s);
            };
        }
    }
}
