package pl.genschu.bloomooemulator.interpreter.runtime;

import pl.genschu.bloomooemulator.engine.Game;
import pl.genschu.bloomooemulator.interpreter.ast.*;
import pl.genschu.bloomooemulator.interpreter.context.CloneRegistry;
import pl.genschu.bloomooemulator.interpreter.context.Context;
import pl.genschu.bloomooemulator.interpreter.errors.InterpreterException;
import pl.genschu.bloomooemulator.interpreter.errors.SourceLocation;
import pl.genschu.bloomooemulator.interpreter.factories.VariableFactory;
import pl.genschu.bloomooemulator.interpreter.helpers.ArgumentHelper;
import pl.genschu.bloomooemulator.interpreter.values.*;
import pl.genschu.bloomooemulator.interpreter.ops.ValueOps;
import pl.genschu.bloomooemulator.interpreter.variable.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Interprets AST nodes and produces execution results.
 */
public class ASTInterpreter {
    private final Context context;
    private final ExecutionContext exec;
    private final MethodContext methodContext;

    public ASTInterpreter(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Context cannot be null");
        }
        this.context = context;
        this.exec = context.exec();
        this.methodContext = createMethodContext();
    }

    private MethodContext createMethodContext() {
        return new MethodContext() {
            @Override
            public Variable getVariable(String name) {
                return context.getVariable(name);
            }

            @Override
            public void setVariable(String name, Variable variable) {
                context.setVariable(name, variable);
            }

            @Override
            public boolean updateVariable(String name, Variable variable) {
                return context.updateVariableInHierarchy(name, variable);
            }

            @Override
            public Game getGame() {
                return context.getGame();
            }

            @Override
            public Value runBehaviour(String frameName, Variable thisVar,
                                      BehaviourVariable behaviour, List<Value> args) {
                exec.pushFrame(frameName, behaviour.name(), null);
                try {
                    if (args != null && !args.isEmpty()) {
                        for (int i = 0; i < args.size(); i++) {
                            exec.setLocal("$" + (i + 1), args.get(i));
                        }
                    }
                    if (thisVar != null) {
                        exec.setThis(thisVar);
                    }
                    ASTInterpreter interpreter = new ASTInterpreter(context);
                    ExecutionResult result = interpreter.execute(behaviour.ast());
                    if (result instanceof ReturnResult returnResult) {
                        return returnResult.getValue();
                    }
                    return result.getValue();
                } finally {
                    exec.popFrame();
                }
            }

            @Override
            public CloneRegistry clones() {
                return context.clones();
            }
        };
    }

    /**
     * Executes an AST node and returns the result.
     */
    public ExecutionResult execute(ASTNode node) {
        return switch (node) {
            case LiteralNode n -> executeLiteral(n);
            case VariableNode n -> executeVariable(n);
            case BlockNode n -> executeBlock(n);
            case ArithmeticNode n -> executeArithmetic(n);
            case ComparisonNode n -> executeComparison(n);
            case LogicalNode n -> executeLogical(n);
            case IfNode n -> executeIf(n);
            case LoopNode n -> executeLoop(n);
            case WhileNode n -> executeWhile(n);
            case BreakNode n -> executeBreak(n);
            case OneBreakNode n -> executeOneBreak(n);
            case ReturnNode n -> executeReturn(n);
            case MethodCallNode n -> executeMethodCall(n);
            case PointerDerefNode n -> executePointerDeref(n);
            case StructAccessNode n -> executeStructAccess(n);
            case VarDefNode n -> executeVarDef(n);
            case ConvNode n -> executeConv(n);
        };
    }

    // === LITERALS ===

    private ExecutionResult executeLiteral(LiteralNode node) {
        return new NormalResult(node.value());
    }

    // === VARIABLES ===

    private ExecutionResult executeVariable(VariableNode node) {
        Variable variable = context.getVariable(node.name());

        return switch (variable) {
            case null -> throw new InterpreterException(
                    "Variable not found: " + node.name(),
                    exec,
                    node.location()
            );
            case ExpressionVariable expr -> new NormalResult(evaluateExpression(expr, node.location()));
            case ConditionVariable cond -> new NormalResult(evaluateCondition(cond, node.location()));
            case ComplexConditionVariable complex ->
                    new NormalResult(evaluateComplexCondition(complex, node.location()));
            default -> new NormalResult(variable.value());
        };

    }

    // === BLOCKS ===

    private ExecutionResult executeBlock(BlockNode node) {
        exec.pushFrame("Block", "{...}", node.location());
        try {
            ExecutionResult lastResult = new NormalResult(NullValue.INSTANCE);

            for (ASTNode stmt : node.statements()) {
                lastResult = execute(stmt);

                // Check for control flow
                if (!lastResult.shouldContinue()) {
                    return lastResult; // Propagate break/return
                }
            }

            return lastResult;
        } finally {
            exec.popFrame();
        }
    }

    // === ARITHMETIC ===

    private ExecutionResult executeArithmetic(ArithmeticNode node) {
        ExecutionResult leftResult = execute(node.left());
        if (!leftResult.shouldContinue()) return leftResult;

        ExecutionResult rightResult = execute(node.right());
        if (!rightResult.shouldContinue()) return rightResult;

        Value result = ValueOps.arithmetic(
            leftResult.getValue(),
            rightResult.getValue(),
            node.operator()
        );

        return new NormalResult(result);
    }


    // === COMPARISON ===

    private ExecutionResult executeComparison(ComparisonNode node) {
        ExecutionResult leftResult = execute(node.left());
        if (!leftResult.shouldContinue()) return leftResult;

        ExecutionResult rightResult = execute(node.right());
        if (!rightResult.shouldContinue()) return rightResult;

        BoolValue result = ValueOps.compare(
            leftResult.getValue(),
            rightResult.getValue(),
            node.operator()
        );

        return new NormalResult(result);
    }

    // === LOGICAL ===

    private ExecutionResult executeLogical(LogicalNode node) {
        ExecutionResult leftResult = execute(node.left());
        if (!leftResult.shouldContinue()) return leftResult;

        Value leftValue = leftResult.getValue();
        if (!(leftValue instanceof BoolValue(boolean leftBool))) {
            throw new InterpreterException(
                "Logical operation requires BOOL operands",
                exec,
                node.location()
            );
        }

        // Short-circuit evaluation
        if (node.operator() == LogicalNode.LogicalOp.AND && !leftBool) {
            return new NormalResult(BoolValue.FALSE);
        }
        if (node.operator() == LogicalNode.LogicalOp.OR && leftBool) {
            return new NormalResult(BoolValue.TRUE);
        }

        ExecutionResult rightResult = execute(node.right());
        if (!rightResult.shouldContinue()) return rightResult;

        Value rightValue = rightResult.getValue();
        if (!(rightValue instanceof BoolValue)) {
            throw new InterpreterException(
                "Logical operation requires BOOL operands",
                exec,
                node.location()
            );
        }

        BoolValue result = ValueOps.logical(leftValue, rightValue, node.operator());

        return new NormalResult(result);
    }

    // === CONTROL FLOW ===

    private ExecutionResult executeIf(IfNode node) {
        exec.pushFrame("If", "@IF", node.location());
        try {
            ExecutionResult condResult = execute(node.condition());
            if (!condResult.shouldContinue()) return condResult;

            boolean condition = ArgumentHelper.getBoolean(condResult.getValue());

            if (condition) {
                return execute(node.thenBranch());
            } else if (node.hasElse()) {
                return execute(node.elseBranch());
            } else {
                return new NormalResult(NullValue.INSTANCE);
            }
        } finally {
            exec.popFrame();
        }
    }

    private ExecutionResult executeLoop(LoopNode node) {
        exec.pushFrame("Loop", "@LOOP", node.location());
        try {
            // Evaluate loop bounds
            ExecutionResult startResult = execute(node.start());
            if (!startResult.shouldContinue()) return startResult;

            ExecutionResult diffResult = execute(node.diff());
            if (!diffResult.shouldContinue()) return diffResult;

            ExecutionResult stepResult = execute(node.step());
            if (!stepResult.shouldContinue()) return stepResult;

            int start = ArgumentHelper.getInt(startResult.getValue());
            int diff = ArgumentHelper.getInt(diffResult.getValue());
            int step = ArgumentHelper.getInt(stepResult.getValue());

            // Execute loop
            for (int i = start; i < start + diff; i += step) {
                exec.pushFrame("_I_ == "+i, "@LOOP", node.location());
                exec.setLocal("_I_", new IntValue(i));
                ExecutionResult bodyResult = execute(node.body());
                exec.popFrame();

                if (bodyResult instanceof OneBreakResult) {
                    // Break this loop only
                    break;
                }
                if (bodyResult instanceof BreakResult) {
                    // Break all loops - propagate up
                    return bodyResult;
                }
                if (bodyResult.isReturn()) {
                    // Propagate return
                    return bodyResult;
                }
            }

            return new NormalResult(NullValue.INSTANCE);
        } finally {
            exec.popFrame();
        }
    }

    private ExecutionResult executeWhile(WhileNode node) {
        exec.pushFrame("While", "@WHILE", node.location());
        try {
            while (true) {
                ExecutionResult condResult = execute(node.condition());
                if (!condResult.shouldContinue()) return condResult;

                if (!ArgumentHelper.getBoolean(condResult.getValue())) {
                    break;
                }

                ExecutionResult bodyResult = execute(node.body());

                if (bodyResult instanceof OneBreakResult) {
                    break;
                }
                if (bodyResult instanceof BreakResult) {
                    return bodyResult;
                }
                if (bodyResult.isReturn()) {
                    return bodyResult;
                }
            }

            return new NormalResult(NullValue.INSTANCE);
        } finally {
            exec.popFrame();
        }
    }

    private ExecutionResult executeBreak(BreakNode node) {
        return BreakResult.INSTANCE;
    }

    private ExecutionResult executeOneBreak(OneBreakNode node) {
        return OneBreakResult.INSTANCE;
    }

    private ExecutionResult executeReturn(ReturnNode node) {
        if (node.hasValue()) {
            ExecutionResult valueResult = execute(node.value());
            if (!valueResult.shouldContinue()) return valueResult;
            return new ReturnResult(valueResult.getValue());
        } else {
            return new ReturnResult(NullValue.INSTANCE);
        }
    }

    // === METHOD CALLS ===

    private ExecutionResult executeMethodCall(MethodCallNode node) {
        Variable target = resolveMethodTarget(node.target(), node.location());
        String methodName = node.methodName().toUpperCase();

        // ConditionVariable and ComplexConditionVariable are handled here
        // because evaluation requires deep context access (recursive operand resolution)
        if (target instanceof ConditionVariable cond) {
            ExecutionResult handled = handleConditionMethod(cond, methodName, node);
            if (handled != null) {
                return handled;
            }
        }
        if (target instanceof ComplexConditionVariable complex) {
            ExecutionResult handled = handleComplexConditionMethod(complex, methodName, node);
            if (handled != null) {
                return handled;
            }
        }

        MethodSpec spec = resolveMethodSpec(target, node.methodName(), node.location());

        // Evaluate all arguments normally
        List<Value> arguments = new ArrayList<>(node.arguments().size());
        for (ASTNode argNode : node.arguments()) {
            ExecutionResult argResult = execute(argNode);
            if (!argResult.shouldContinue()) return argResult;
            arguments.add(argResult.getValue());
        }

        // Execute method with MethodContext
        MethodResult result;
        try {
            result = spec.method().execute(target, arguments, methodContext);
        } catch (RuntimeException e) {
            throw new InterpreterException(
                "Method call failed: " + node.methodName(),
                exec,
                node.location(),
                e
            );
        }

        return new NormalResult(result.getReturnValue());
    }

    private ExecutionResult executePointerDeref(PointerDerefNode node) {
        ExecutionResult exprResult = execute(node.expression());
        if (!exprResult.shouldContinue()) return exprResult;

        String varName = ArgumentHelper.getString(exprResult.getValue());
        Variable resolved = context.getVariable(varName);
        if (resolved == null) {
            throw new InterpreterException(
                "Pointer dereference: variable not found: " + varName,
                exec,
                node.location()
            );
        }
        return new NormalResult(resolved.value());
    }

    private ExecutionResult executeStructAccess(StructAccessNode node) {
        Variable structVar = context.getVariable(node.structName());
        if (structVar == null) {
            throw new InterpreterException(
                "Struct not found: " + node.structName(),
                exec,
                node.location()
            );
        }
        if (!(structVar instanceof StructVariable struct)) {
            throw new InterpreterException(
                node.structName() + " is not a STRUCT",
                exec,
                node.location()
            );
        }
        return new NormalResult(struct.getFieldByName(node.fieldName()));
    }

    private ExecutionResult executeVarDef(VarDefNode node) {
        // Execute initial value expression
        ExecutionResult valueResult = execute(node.initialValue());
        if (!valueResult.shouldContinue()) return valueResult;

        Value initialValue = valueResult.getValue();

        // Check if variable already exists in current scope
        if(exec.getLocal(node.varName()) != null) {
            // Variable already exists - skip creation without error
            return new NormalResult(null);
        }

        // Create variable in local scope
        exec.setLocal(node.varName(), initialValue);

        return new NormalResult(null);
    }

    private ExecutionResult executeConv(ConvNode node) {
        ExecutionResult varResult = execute(node.variable());
        if (!varResult.shouldContinue()) return varResult;

        Value value = varResult.getValue();
        String targetType = node.targetType().toUpperCase();

        Value converted = switch (targetType) {
            case "INT", "INTEGER"  -> value.toInt();
            case "DOUBLE"          -> value.toDouble();
            case "STRING"          -> value.toStringValue();
            case "BOOL", "BOOLEAN" -> value.toBool();
            default                -> value;
        };

        return new NormalResult(converted);
    }

    private Variable resolveMethodTarget(ASTNode target, SourceLocation location) {
        if (target instanceof VariableNode variableNode) {
            String name = variableNode.name();
            if (!context.hasVariableInHierarchy(name)) {
                throw new InterpreterException(
                    "Variable not found: " + name,
                    exec,
                    location
                );
            }

            Variable variable = context.getVariable(name);
            if (variable == null) {
                throw new InterpreterException(
                    "Variable not found: " + name,
                    exec,
                    location
                );
            }

            return variable;
        }

        throw new InterpreterException(
            "Unsupported method call target: " + target.getClass().getSimpleName(),
            exec,
            location
        );
    }

    private MethodSpec resolveMethodSpec(Variable target, String methodName, SourceLocation location) {
        MethodSpec spec = target.methods().get(methodName.toUpperCase());
        if (spec == null || spec.method() == null) {
            MethodSpec global = target.globalMethods().get(methodName.toUpperCase());
            if (global == null || global.method() == null) {
                throw new InterpreterException(
                    "Method not found: " + methodName + " on " + target.type(),
                    exec,
                    location
                );
            }
            return global;
        }
        return spec;
    }

    private ExecutionResult handleConditionMethod(ConditionVariable cond, String methodName, MethodCallNode node) {
        BoolValue result = evaluateCondition(cond, node.location());
        boolean emitSignal = false;
        if (!node.arguments().isEmpty()) {
            ExecutionResult argRes = execute(node.arguments().get(0));
            if (!argRes.shouldContinue()) return argRes;
            emitSignal = ArgumentHelper.getBoolean(argRes.getValue());
        }
        if (emitSignal) {
            cond.emitSignal(result.value() ? "ONRUNTIMESUCCESS" : "ONRUNTIMEFAILED");
        }

        switch (methodName) {
            case "CHECK" -> {
                return new NormalResult(result);
            }
            case "BREAK" -> {
                return result.value() ? BreakResult.INSTANCE : new NormalResult(NullValue.INSTANCE);
            }
            case "ONE_BREAK" -> {
                return result.value() ? OneBreakResult.INSTANCE : new NormalResult(NullValue.INSTANCE);
            }
            default -> {}
        }

        return null;
    }

    private ExecutionResult handleComplexConditionMethod(ComplexConditionVariable cond, String methodName, MethodCallNode node) {
        BoolValue result = evaluateComplexCondition(cond, node.location());
        boolean emitSignal = false;
        if (!node.arguments().isEmpty()) {
            Value argValue = execute(node.arguments().get(0)).getValue();
            emitSignal = ArgumentHelper.getBoolean(argValue);
        }
        if (emitSignal) {
            cond.emitSignal(result.value() ? "ONRUNTIMESUCCESS" : "ONRUNTIMEFAILED");
        }

        switch (methodName) {
            case "CHECK" -> {
                return new NormalResult(result);
            }
            case "BREAK" -> {
                return result.value() ? BreakResult.INSTANCE : new NormalResult(NullValue.INSTANCE);
            }
            case "ONE_BREAK" -> {
                return result.value() ? OneBreakResult.INSTANCE : new NormalResult(NullValue.INSTANCE);
            }
            default -> {}
        }

        return null;
    }

    // TODO: Add safe checks for infinite recursion (but how to handle it? Original probably just freezes)
    private BoolValue evaluateComplexCondition(ComplexConditionVariable cond, SourceLocation location) {
        Value left = resolveConditionValue(cond.condition1Name(), location);
        Value right = resolveConditionValue(cond.condition2Name(), location);

        if (!(left instanceof BoolValue) || !(right instanceof BoolValue)) {
            throw new InterpreterException(
                "ComplexCondition operands must be BOOL",
                exec,
                location
            );
        }

        LogicalNode.LogicalOp op = cond.isAnd()
            ? LogicalNode.LogicalOp.AND
            : LogicalNode.LogicalOp.OR;

        return ValueOps.logical(left, right, op);
    }

    private Value resolveConditionValue(String name, SourceLocation location) {
        Variable var = context.getVariable(name);
        if (var == null) {
            throw new InterpreterException("Variable not found: " + name, exec, location);
        }
        if (var instanceof ConditionVariable cond) {
            return evaluateCondition(cond, location);
        }
        if (var instanceof ComplexConditionVariable complex) {
            return evaluateComplexCondition(complex, location);
        }
        return var.value();
    }

    private BoolValue evaluateCondition(ConditionVariable cond, SourceLocation location) {
        Value left = resolveOperandValue(cond.operand1(), location);
        Value right = resolveOperandValue(cond.operand2(), location);

        ComparisonNode.ComparisonOp op = switch (cond.operator().toUpperCase()) {
            case "EQUAL" -> ComparisonNode.ComparisonOp.EQUAL;
            case "NOTEQUAL" -> ComparisonNode.ComparisonOp.NOT_EQUAL;
            case "LESS" -> ComparisonNode.ComparisonOp.LESS;
            case "GREATER" -> ComparisonNode.ComparisonOp.GREATER;
            case "LESSEQUAL" -> ComparisonNode.ComparisonOp.LESS_EQUAL;
            case "GREATEREQUAL" -> ComparisonNode.ComparisonOp.GREATER_EQUAL;
            default -> throw new IllegalArgumentException("Unknown condition operator: " + cond.operator());
        };

        return ValueOps.compare(left, right, op);
    }

    private Value evaluateExpression(ExpressionVariable expr, SourceLocation location) {
        Value left = resolveOperandValue(expr.operand1(), location);
        Value right = resolveOperandValue(expr.operand2(), location);

        ArithmeticNode.ArithmeticOp op = switch (expr.operator().toUpperCase()) {
            case "ADD" -> ArithmeticNode.ArithmeticOp.ADD;
            case "SUB" -> ArithmeticNode.ArithmeticOp.SUBTRACT;
            case "MUL" -> ArithmeticNode.ArithmeticOp.MULTIPLY;
            case "DIV" -> ArithmeticNode.ArithmeticOp.DIVIDE;
            case "MOD" -> ArithmeticNode.ArithmeticOp.MODULO;
            default -> throw new IllegalArgumentException("Unknown expression operator: " + expr.operator());
        };

        return ValueOps.arithmetic(left, right, op);
    }

    private Value resolveOperandValue(String operand, SourceLocation location) {
        if (operand == null) {
            return NullValue.INSTANCE;
        }

        String trimmed = operand.trim();
        if (trimmed.isEmpty()) {
            return new StringValue("");
        }

        if ((trimmed.startsWith("\"") && trimmed.endsWith("\"")) ||
            (trimmed.startsWith("'") && trimmed.endsWith("'"))) {
            return new StringValue(trimmed.substring(1, trimmed.length() - 1));
        }

        if ("TRUE".equalsIgnoreCase(trimmed)) {
            return BoolValue.TRUE;
        }
        if ("FALSE".equalsIgnoreCase(trimmed)) {
            return BoolValue.FALSE;
        }

        if (trimmed.matches("[-+]?\\d+")) {
            try {
                return new IntValue(Integer.parseInt(trimmed));
            } catch (NumberFormatException ignored) {
            }
        }
        if (trimmed.matches("[-+]?\\d*\\.\\d+")) {
            try {
                return new DoubleValue(Double.parseDouble(trimmed));
            } catch (NumberFormatException ignored) {
            }
        }

        Variable var = context.getVariable(trimmed);
        if (var == null) {
            throw new InterpreterException("Variable not found: " + trimmed, exec, location);
        }

        if (var instanceof ExpressionVariable expr) {
            return evaluateExpression(expr, location);
        }
        if (var instanceof ConditionVariable cond) {
            return evaluateCondition(cond, location);
        }
        if (var instanceof ComplexConditionVariable complex) {
            return evaluateComplexCondition(complex, location);
        }

        return var.value();
    }
}
