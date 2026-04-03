package pl.genschu.bloomooemulator.interpreter.variable;

import pl.genschu.bloomooemulator.interpreter.ast.ArithmeticNode;
import pl.genschu.bloomooemulator.interpreter.ops.ValueOps;
import pl.genschu.bloomooemulator.interpreter.values.*;

import java.util.HashMap;
import java.util.Map;

/**
 * ExpressionVariable represents a computed expression with two operands and an operator.
 * It's a fancy way to represent [operand1^operator(operand2)].
 *
 * Supported operators: ADD, SUB, MUL, DIV, MOD
 **/
public record ExpressionVariable(
    String name,
    String operand1,
    String operand2,
    String operator,
    Map<String, SignalHandler> signals
) implements Variable {

    public ExpressionVariable {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Variable name cannot be null or empty");
        }
        if (operand1 == null) operand1 = "";
        if (operand2 == null) operand2 = "";
        if (operator == null) operator = "ADD";
        if (signals == null) {
            signals = Map.of();
        } else {
            signals = Map.copyOf(signals);
        }
    }

    public ExpressionVariable(String name) {
        this(name, "", "", "ADD", Map.of());
    }

    public ExpressionVariable(String name, String operand1, String operand2, String operator) {
        this(name, operand1, operand2, operator, Map.of());
    }

    // ========================================
    // INTERFACE IMPLEMENTATION
    // ========================================

    @Override
    public Value value() {
        // Try to evaluate without context (works when operands are literals)
        try {
            return evaluate(null);
        } catch (Exception e) {
            // If evaluation fails (needs context for variable resolution), return placeholder
            return new StringValue(operand1 + " " + operator + " " + operand2);
        }
    }

    @Override
    public VariableType type() {
        return VariableType.EXPRESSION;
    }

    @Override
    public Variable withValue(Value newValue) {
        // Cannot directly set value on expression
        return this;
    }

    @Override
    public Map<String, MethodSpec> methods() {
        return METHODS;
    }

    @Override
    public Variable withSignal(String signalName, SignalHandler handler) {
        Map<String, SignalHandler> newSignals = new HashMap<>(signals);
        newSignals.put(signalName, handler);
        return new ExpressionVariable(name, operand1, operand2, operator, newSignals);
    }

    // ========================================
    // EVALUATION
    // ========================================

    /**
     * Evaluates this expression by resolving operands from context.
     */
    public Value evaluate(MethodContext ctx) {
        Value left = ConditionVariable.resolveOperandValue(operand1, ctx);
        Value right = ConditionVariable.resolveOperandValue(operand2, ctx);

        ArithmeticNode.ArithmeticOp op = switch (operator.toUpperCase()) {
            case "ADD" -> ArithmeticNode.ArithmeticOp.ADD;
            case "SUB" -> ArithmeticNode.ArithmeticOp.SUBTRACT;
            case "MUL" -> ArithmeticNode.ArithmeticOp.MULTIPLY;
            case "DIV" -> ArithmeticNode.ArithmeticOp.DIVIDE;
            case "MOD" -> ArithmeticNode.ArithmeticOp.MODULO;
            default -> throw new IllegalArgumentException("Unknown expression operator: " + operator);
        };

        return ValueOps.arithmetic(left, right, op);
    }

    // ========================================
    // METHODS DEFINITION
    // ========================================

    private static final Map<String, MethodSpec> METHODS = Map.of();

    // ========================================
    // CONVENIENT ACCESSORS
    // ========================================

    @Override
    public String toString() {
        return "ExpressionVariable[" + name + "=" + operand1 + " " + operator + " " + operand2 + "]";
    }
}
