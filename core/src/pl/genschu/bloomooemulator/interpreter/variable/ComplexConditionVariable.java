package pl.genschu.bloomooemulator.interpreter.variable;

import pl.genschu.bloomooemulator.interpreter.helpers.ArgumentHelper;
import pl.genschu.bloomooemulator.interpreter.values.*;

import java.util.HashMap;
import java.util.Map;

/**
 * ComplexConditionVariable combines two conditions with a logical operator (AND/OR).
 *
 * The actual check() logic must be performed by the interpreter with access to the context (we need to send opcode to do this).
 **/
public record ComplexConditionVariable(
    String name,
    String condition1Name,
    String condition2Name,
    String operator,  // AND or OR
    Map<String, SignalHandler> signals
) implements Variable {

    public ComplexConditionVariable {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Variable name cannot be null or empty");
        }
        if (condition1Name == null) condition1Name = "";
        if (condition2Name == null) condition2Name = "";
        if (operator == null) operator = "AND";
        if (signals == null) {
            signals = Map.of();
        } else {
            signals = Map.copyOf(signals);
        }
    }

    public ComplexConditionVariable(String name) {
        this(name, "", "", "AND", Map.of());
    }

    public ComplexConditionVariable(String name, String condition1Name, String condition2Name, String operator) {
        this(name, condition1Name, condition2Name, operator, Map.of());
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
        return VariableType.COMPLEXCONDITION;
    }

    @Override
    public Variable withValue(Value newValue) {
        // Cannot directly set value on condition
        return this;
    }

    @Override
    public Map<String, VariableMethod> methods() {
        return METHODS;
    }

    @Override
    public Variable withSignal(String signalName, SignalHandler handler) {
        Map<String, SignalHandler> newSignals = new HashMap<>(signals);
        newSignals.put(signalName, handler);
        return new ComplexConditionVariable(name, condition1Name, condition2Name, operator, newSignals);
    }

    // ========================================
    // METHODS DEFINITION
    // ========================================

    private static final Map<String, VariableMethod> METHODS = Map.ofEntries(
        Map.entry("CHECK", (self, args) -> {
            // The actual check requires context to resolve conditions
            // Return false as placeholder - interpreter should override
            boolean emitSignal = !args.isEmpty() && ArgumentHelper.getBoolean(args.get(0));
            return MethodResult.noChange(BoolValue.FALSE);
        }),

        Map.entry("BREAK", (self, args) -> {
            // The actual check requires context to resolve conditions
            // Interpreter should handle this
            return MethodResult.noChange(NullValue.INSTANCE);
        }),

        Map.entry("ONE_BREAK", (self, args) -> {
            // The actual check requires context to resolve conditions
            // Interpreter should handle this
            return MethodResult.noChange(NullValue.INSTANCE);
        })
    );

    // ========================================
    // CONVENIENT ACCESSORS
    // ========================================

    /**
     * Returns true if this is an AND condition.
     */
    public boolean isAnd() {
        return "AND".equalsIgnoreCase(operator);
    }

    /**
     * Returns true if this is an OR condition.
     */
    public boolean isOr() {
        return "OR".equalsIgnoreCase(operator);
    }

    @Override
    public String toString() {
        return "ComplexConditionVariable[" + name + "=" + condition1Name + " " + operator + " " + condition2Name + "]";
    }
}
