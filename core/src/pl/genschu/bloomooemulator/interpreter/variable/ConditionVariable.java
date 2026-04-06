package pl.genschu.bloomooemulator.interpreter.variable;

import pl.genschu.bloomooemulator.interpreter.ast.ASTNode;
import pl.genschu.bloomooemulator.interpreter.ast.BlockNode;
import pl.genschu.bloomooemulator.interpreter.ast.ComparisonNode;
import pl.genschu.bloomooemulator.interpreter.helpers.ArgumentHelper;
import pl.genschu.bloomooemulator.interpreter.ops.ValueOps;
import pl.genschu.bloomooemulator.interpreter.runtime.ASTInterpreter;
import pl.genschu.bloomooemulator.interpreter.runtime.ExecutionResult;
import pl.genschu.bloomooemulator.interpreter.values.*;
import pl.genschu.bloomooemulator.loader.BehaviourCodeParser;

import java.util.HashMap;
import java.util.List;
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
        if (handler == null) {
            newSignals.remove(signalName);
        } else {
            newSignals.put(signalName, handler);
        }
        return new ConditionVariable(name, operand1, operand2, operator, newSignals);
    }

    // ========================================
    // EVALUATION
    // ========================================

    /**
     * Evaluates this condition by resolving operands from context.
     */
    public BoolValue evaluate(MethodContext ctx) {
        Value left = resolveOperandValue(operand1, ctx);
        Value right = resolveOperandValue(operand2, ctx);
        return ValueOps.compare(left, right, parseComparisonOp(operator));
    }

    static ComparisonNode.ComparisonOp parseComparisonOp(String operator) {
        return switch (operator.toUpperCase()) {
            case "EQUAL" -> ComparisonNode.ComparisonOp.EQUAL;
            case "NOTEQUAL" -> ComparisonNode.ComparisonOp.NOT_EQUAL;
            case "LESS" -> ComparisonNode.ComparisonOp.LESS;
            case "GREATER" -> ComparisonNode.ComparisonOp.GREATER;
            case "LESSEQUAL" -> ComparisonNode.ComparisonOp.LESS_EQUAL;
            case "GREATEREQUAL" -> ComparisonNode.ComparisonOp.GREATER_EQUAL;
            default -> throw new IllegalArgumentException("Unknown condition operator: " + operator);
        };
    }

    /**
     * Resolves an operand string to a Value.
     * Handles: null, string/bool literals, int/double literals, variable lookup
     * (including recursive evaluation of Expression/Condition/ComplexCondition).
     *
     * Package-private so ExpressionVariable and ComplexConditionVariable can reuse it.
     */
    static Value resolveOperandValue(String operand, MethodContext ctx) {
        if (operand == null) return NullValue.INSTANCE;
        String trimmed = operand.trim();
        if (trimmed.isEmpty()) return new StringValue("");

        if ((trimmed.startsWith("\"") && trimmed.endsWith("\"")) ||
            (trimmed.startsWith("'") && trimmed.endsWith("'"))) {
            return new StringValue(trimmed.substring(1, trimmed.length() - 1));
        }

        if ("TRUE".equalsIgnoreCase(trimmed)) return BoolValue.TRUE;
        if ("FALSE".equalsIgnoreCase(trimmed)) return BoolValue.FALSE;

        if (trimmed.matches("[-+]?\\d+")) {
            try { return new IntValue(Integer.parseInt(trimmed)); }
            catch (NumberFormatException ignored) {}
        }
        if (trimmed.matches("[-+]?\\d*\\.\\d+")) {
            try { return new DoubleValue(Double.parseDouble(trimmed)); }
            catch (NumberFormatException ignored) {}
        }

        if (looksLikeCodeOperand(trimmed)) {
            return evaluateCodeOperand(trimmed, ctx);
        }

        if (ctx == null) {
            throw new IllegalStateException("Cannot resolve operand without method context: " + trimmed);
        }

        Variable var = ctx.getVariable(trimmed);
        if (var == null) {
            throw new RuntimeException("Variable not found: " + trimmed);
        }

        if (var instanceof ExpressionVariable expr) return expr.evaluate(ctx);
        if (var instanceof ConditionVariable cond) return cond.evaluate(ctx);
        if (var instanceof ComplexConditionVariable complex) return complex.evaluate(ctx);
        return var.value();
    }

    private static boolean looksLikeCodeOperand(String operand) {
        return operand.startsWith("[")
            || operand.startsWith("*")
            || operand.contains("^")
            || operand.contains("|");
    }

    private static Value evaluateCodeOperand(String operand, MethodContext ctx) {
        if (ctx == null) {
            throw new IllegalStateException("Cannot evaluate code operand without method context: " + operand);
        }

        String code = operand.endsWith(";") ? operand : operand + ";";
        ASTNode parsed = BehaviourCodeParser.parseCode(code, "<operand>");
        ASTNode executable = unwrapSingleStatement(parsed);
        ASTInterpreter interpreter = new ASTInterpreter(ctx.context());
        ExecutionResult result = interpreter.execute(executable);

        Value returnValue = interpreter.getPendingReturnValue();
        return returnValue != null ? returnValue : result.getValue();
    }

    private static ASTNode unwrapSingleStatement(ASTNode parsed) {
        if (parsed instanceof BlockNode block && block.statements().size() == 1) {
            return block.statements().getFirst();
        }
        return parsed;
    }

    // ========================================
    // HELPER METHODS
    // ========================================

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
            ConditionVariable cond = (ConditionVariable) self;
            BoolValue result = cond.evaluate(ctx);
            handleSignalEmission(cond, result, args);
            return MethodResult.returns(result);
        })),
        Map.entry("BREAK", MethodSpec.of((self, args, ctx) -> {
            ConditionVariable cond = (ConditionVariable) self;
            BoolValue result = cond.evaluate(ctx);
            handleSignalEmission(cond, result, args);
            return result.value() ? MethodResult.breakAll() : MethodResult.noReturn();
        })),
        Map.entry("ONE_BREAK", MethodSpec.of((self, args, ctx) -> {
            ConditionVariable cond = (ConditionVariable) self;
            BoolValue result = cond.evaluate(ctx);
            handleSignalEmission(cond, result, args);
            return result.value() ? MethodResult.oneBreak() : MethodResult.noReturn();
        }))
    );

    // ========================================
    // CONVENIENT ACCESSORS
    // ========================================

    @Override
    public String toString() {
        return "ConditionVariable[" + name + "=" + operand1 + " " + getOperatorSymbol() + " " + operand2 + "]";
    }
}
