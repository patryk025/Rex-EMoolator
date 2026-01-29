package pl.genschu.bloomooemulator.interpreter.variable;

import pl.genschu.bloomooemulator.interpreter.v1.variable.Method;
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
        // Expression value must be computed at runtime with context
        // Return a placeholder - actual evaluation happens in interpreter
        return new StringValue(operand1 + " " + operator + " " + operand2);
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
    // METHODS DEFINITION
    // ========================================

    // EXPRESSION doesn't expose any specific methods (it's evaluated at runtime)
    private static final Map<String, MethodSpec> METHODS = Map.of();

    // ========================================
    // CONVENIENT ACCESSORS
    // ========================================

    @Override
    public String toString() {
        return "ExpressionVariable[" + name + "=" + operand1 + " " + operator + " " + operand2 + "]";
    }
}
