package pl.genschu.bloomooemulator.interpreter.variable;

import pl.genschu.bloomooemulator.interpreter.ast.LogicalNode;
import pl.genschu.bloomooemulator.interpreter.helpers.ArgumentHelper;
import pl.genschu.bloomooemulator.interpreter.ops.ValueOps;
import pl.genschu.bloomooemulator.interpreter.values.*;

import java.util.HashMap;
import java.util.List;
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
    public Map<String, MethodSpec> methods() {
        return METHODS;
    }

    @Override
    public Variable withSignal(String signalName, SignalHandler handler) {
        Map<String, SignalHandler> newSignals = new HashMap<>(signals);
        if (handler == null) {
            newSignals.remove(signalName);
        } else {
            newSignals.put(signalName, handler);
        }
        return new ComplexConditionVariable(name, condition1Name, condition2Name, operator, newSignals);
    }

    // ========================================
    // EVALUATION
    // ========================================

    /**
     * Evaluates this complex condition by resolving sub-conditions from context.
     */
    public BoolValue evaluate(MethodContext ctx) {
        Value left = resolveConditionValue(condition1Name, ctx);
        Value right = resolveConditionValue(condition2Name, ctx);

        if (!(left instanceof BoolValue) || !(right instanceof BoolValue)) {
            throw new RuntimeException("ComplexCondition operands must be BOOL");
        }

        LogicalNode.LogicalOp op = isAnd() ? LogicalNode.LogicalOp.AND : LogicalNode.LogicalOp.OR;
        return ValueOps.logical(left, right, op);
    }

    private static Value resolveConditionValue(String name, MethodContext ctx) {
        Variable var = ctx.getVariable(name);
        if (var == null) {
            throw new RuntimeException("Variable not found: " + name);
        }
        if (var instanceof ConditionVariable cond) return cond.evaluate(ctx);
        if (var instanceof ComplexConditionVariable complex) return complex.evaluate(ctx);
        return var.value();
    }

    private static void handleSignalEmission(Variable cond, BoolValue result, List<Value> args) {
        if (args != null && !args.isEmpty()) {
            if (ArgumentHelper.getBoolean(args.get(0))) {
                cond.emitSignal(result.value() ? "ONRUNTIMESUCCESS" : "ONRUNTIMEFAILED");
            }
        }
    }

    // ========================================
    // METHODS DEFINITION
    // ========================================

    private static final Map<String, MethodSpec> METHODS = Map.ofEntries(
        Map.entry("CHECK", MethodSpec.of((self, args, ctx) -> {
            ComplexConditionVariable cond = (ComplexConditionVariable) self;
            BoolValue result = cond.evaluate(ctx);
            handleSignalEmission(cond, result, args);
            return MethodResult.returns(result);
        })),
        Map.entry("BREAK", MethodSpec.of((self, args, ctx) -> {
            ComplexConditionVariable cond = (ComplexConditionVariable) self;
            BoolValue result = cond.evaluate(ctx);
            handleSignalEmission(cond, result, args);
            return result.value() ? MethodResult.breakAll() : MethodResult.noReturn();
        })),
        Map.entry("ONE_BREAK", MethodSpec.of((self, args, ctx) -> {
            ComplexConditionVariable cond = (ComplexConditionVariable) self;
            BoolValue result = cond.evaluate(ctx);
            handleSignalEmission(cond, result, args);
            return result.value() ? MethodResult.oneBreak() : MethodResult.noReturn();
        }))
    );

    // ========================================
    // CONVENIENT ACCESSORS
    // ========================================

    public boolean isAnd() {
        return "AND".equalsIgnoreCase(operator);
    }

    public boolean isOr() {
        return "OR".equalsIgnoreCase(operator);
    }

    @Override
    public String toString() {
        return "ComplexConditionVariable[" + name + "=" + condition1Name + " " + operator + " " + condition2Name + "]";
    }
}
