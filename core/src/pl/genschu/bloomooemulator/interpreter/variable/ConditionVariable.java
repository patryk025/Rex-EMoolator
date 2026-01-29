package pl.genschu.bloomooemulator.interpreter.variable;

import pl.genschu.bloomooemulator.interpreter.helpers.ArgumentHelper;
import pl.genschu.bloomooemulator.interpreter.values.*;

import java.util.HashMap;
import java.util.Map;

/**
 * ConditionVariable represents a conditional check with two operands and a comparison operator.
 *
 * Supported operators: EQUAL, NOTEQUAL, LESS, GREATER, LESSEQUAL, GREATEREQUAL
 **/
public record ConditionVariable(
    String name,
    String operand1,
    String operand2,
    String operator,
    Map<String, SignalHandler> signals
) implements Variable {

    public ConditionVariable {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Variable name cannot be null or empty");
        }
        if (operand1 == null) operand1 = "";
        if (operand2 == null) operand2 = "";
        if (operator == null) operator = "EQUAL";
        if (signals == null) {
            signals = Map.of();
        } else {
            signals = Map.copyOf(signals);
        }
    }

    public ConditionVariable(String name) {
        this(name, "", "", "EQUAL", Map.of());
    }

    public ConditionVariable(String name, String operand1, String operand2, String operator) {
        this(name, operand1, operand2, operator, Map.of());
    }

    // ========================================
    // INTERFACE IMPLEMENTATION
    // ========================================

    @Override
    public Value value() {
        // Condition value must be computed at runtime with context
        return BoolValue.FALSE;
    }

    @Override
    public VariableType type() {
        return VariableType.CONDITION;
    }

    @Override
    public Variable withValue(Value newValue) {
        // Cannot directly set value on condition
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
        return new ConditionVariable(name, operand1, operand2, operator, newSignals);
    }

    // ========================================
    // HELPER METHODS
    // ========================================

    /**
     * Returns the comparison operator symbol.
     */
    public String getOperatorSymbol() {
        return switch (operator.toUpperCase()) {
            case "EQUAL" -> "==";
            case "NOTEQUAL" -> "!=";
            case "LESS" -> "<";
            case "GREATER" -> ">";
            case "LESSEQUAL" -> "<=";
            case "GREATEREQUAL" -> ">=";
            default -> "<not defined>";
        };
    }

    // ========================================
    // METHODS DEFINITION
    // ========================================

    private static final Map<String, MethodSpec> METHODS = Map.ofEntries(
        /* These are implemented in ASTInterpreter */
    );

    // ========================================
    // CONVENIENT ACCESSORS
    // ========================================

    @Override
    public String toString() {
        return "ConditionVariable[" + name + "=" + operand1 + " " + getOperatorSymbol() + " " + operand2 + "]";
    }
}
