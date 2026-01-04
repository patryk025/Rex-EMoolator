package pl.genschu.bloomooemulator.interpreter.v2.ast;

import pl.genschu.bloomooemulator.interpreter.v2.errors.SourceLocation;

/**
 * Represents an arithmetic operation: +, -, *, /, %
 */
public record ArithmeticNode(
    ASTNode left,
    ASTNode right,
    ArithmeticOp operator,
    SourceLocation location
) implements ASTNode {

    public ArithmeticNode {
        if (left == null || right == null) {
            throw new IllegalArgumentException("Arithmetic operands cannot be null");
        }
        if (operator == null) {
            throw new IllegalArgumentException("Arithmetic operator cannot be null");
        }
        if (location == null) {
            location = SourceLocation.UNKNOWN;
        }
    }

    public enum ArithmeticOp {
        ADD("+"),
        SUBTRACT("-"),
        MULTIPLY("*"),
        DIVIDE("@"),  // @ in AidemMedia
        MODULO("%");

        private final String symbol;

        ArithmeticOp(String symbol) {
            this.symbol = symbol;
        }

        public String symbol() {
            return symbol;
        }

        public static ArithmeticOp fromString(String s) {
            return switch (s) {
                case "+" -> ADD;
                case "-" -> SUBTRACT;
                case "*" -> MULTIPLY;
                case "@" -> DIVIDE;
                case "%" -> MODULO;
                default -> throw new IllegalArgumentException("Unknown operator: " + s);
            };
        }
    }
}
