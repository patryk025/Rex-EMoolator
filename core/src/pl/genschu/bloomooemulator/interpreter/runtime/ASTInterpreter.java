package pl.genschu.bloomooemulator.interpreter.runtime;

import com.badlogic.gdx.Gdx;
import pl.genschu.bloomooemulator.engine.Game;
import pl.genschu.bloomooemulator.interpreter.ast.*;
import pl.genschu.bloomooemulator.interpreter.context.CloneRegistry;
import pl.genschu.bloomooemulator.interpreter.context.Context;
import pl.genschu.bloomooemulator.interpreter.errors.InterpreterException;
import pl.genschu.bloomooemulator.interpreter.errors.SourceLocation;
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

    // @RETURN does not exit the behaviour — it stores the value here.
    // Each subsequent @RETURN overrides the previous value.
    private Value pendingReturnValue;

    public ASTInterpreter(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Context cannot be null");
        }
        this.context = context;
        this.exec = context.exec();
        this.methodContext = createMethodContext();
    }

    /**
     * Returns the pending return value set by @RETURN, or null if none was set.
     */
    public Value getPendingReturnValue() {
        return pendingReturnValue;
    }

    /**
     * Executes a BehaviourVariable's AST in a new frame.
     * Public convenience for signal execution and external callers.
     *
     * <p>Returns the full {@link ExecutionResult} so callers can decide whether
     * a {@code @BREAK} should keep climbing the procedure stack. External entry
     * points (signals, {@code __INIT__}) typically discard the result — they
     * are top-level handlers with no enclosing procedure to propagate into.
     */
    public ExecutionResult runBehaviour(String frameName, Variable thisVar, BehaviourVariable behaviour, List<Value> args) {
        return methodContext.runBehaviour(frameName, thisVar, behaviour, args);
    }

    public MethodContext getMethodContext() {
        return methodContext;
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
            public boolean removeVariable(String name) {
                return context.removeVariableInHierarchy(name);
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
            public ExecutionResult runBehaviour(String frameName, Variable thisVar,
                                                BehaviourVariable behaviour, List<Value> args) {
                exec.pushFrame(frameName, behaviour.name(), null);
                try {
                    if (args != null && !args.isEmpty()) {
                        for (int i = 0; i < args.size(); i++) {
                            exec.setLocal("$" + (i + 1), valueToVariable("$" + (i + 1), args.get(i)));
                        }
                    }
                    if (thisVar != null) {
                        exec.setThis(thisVar);
                    }
                    ASTInterpreter interpreter = new ASTInterpreter(context);
                    ExecutionResult execResult = interpreter.execute(behaviour.ast());

                    // @BREAK escapes the procedure boundary — let the caller decide
                    // how far to keep propagating (it'll bubble through executeMethodCall
                    // and any enclosing loops).
                    if (execResult instanceof BreakResult) {
                        return BreakResult.INSTANCE;
                    }

                    // @ONEBREAK is procedure-local: swallow it here so the caller continues.
                    // NormalResult / OneBreakResult / ReturnResult all collapse to a normal
                    // return carrying the pending @RETURN value (if any).
                    Value returnValue = interpreter.getPendingReturnValue();
                    if (returnValue == null) {
                        returnValue = NullValue.INSTANCE;
                    }
                    return new NormalResult(returnValue);
                } finally {
                    exec.popFrame();
                }
            }

            @Override
            public CloneRegistry clones() {
                return context.clones();
            }

            @Override
            public Context context() {
                return context;
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
            case ExpressionVariable expr -> new NormalResult(expr.evaluate(methodContext));
            case ConditionVariable cond -> new NormalResult(cond.evaluate(methodContext));
            case ComplexConditionVariable complex -> new NormalResult(complex.evaluate(methodContext));
            default -> new NormalResult(variable.value());
        };
    }

    // === BLOCKS ===

    private ExecutionResult executeBlock(BlockNode node) {
        exec.pushFrame("Block", "{...}", node.location());
        try {
            ExecutionResult lastResult = new NormalResult(NullValue.INSTANCE);

            for (ASTNode stmt : node.statements()) {
                try {
                    lastResult = execute(stmt);

                    // Check for control flow
                    if (!lastResult.shouldContinue()) {
                        return lastResult; // Propagate break/return
                    }
                } catch(InterpreterException e) {
                    Gdx.app.error("ASTInterpreter", "Caught error executing instruction in block statement: " + e.getMessage());
                    // skip that instruction and continue
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
                exec.setLocal("_I_", new IntegerVariable("_I_", i));
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
            pendingReturnValue = valueResult.getValue();
        } else {
            pendingReturnValue = NullValue.INSTANCE;
        }
        return new NormalResult(pendingReturnValue);
    }

    // === METHOD CALLS ===

    private ExecutionResult executeMethodCall(MethodCallNode node) {
        Variable target = resolveMethodTarget(node.target(), node.location());

        // Evaluate all arguments
        List<Value> arguments = new ArrayList<>(node.arguments().size());
        for (ASTNode argNode : node.arguments()) {
            ExecutionResult argResult = execute(argNode);
            if (!argResult.shouldContinue()) return argResult;
            arguments.add(argResult.getValue());
        }

        // Execute method via Variable.callMethod (handles method resolution + global fallback)
        MethodResult result;
        try {
            result = target.callMethod(node.methodName(), arguments, methodContext);
        } catch (RuntimeException e) {
            throw new InterpreterException(
                "Method call failed: " + node.methodName(),
                exec,
                node.location(),
                e
            );
        }

        return switch (result.controlFlow()) {
            case BREAK -> BreakResult.INSTANCE;
            case ONE_BREAK -> OneBreakResult.INSTANCE;
            case NONE -> new NormalResult(result.getReturnValue());
        };
    }

    private ExecutionResult executePointerDeref(PointerDerefNode node) {
        ExecutionResult exprResult = execute(node.expression());
        if (!exprResult.shouldContinue()) return exprResult;

        String varName = switch (exprResult.getValue()) {
            case VariableRef ref -> ref.name();
            case VariableValue variableValue -> variableValue.variable().name();
            default -> ArgumentHelper.getString(exprResult.getValue());
        };
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
        if (exec.getLocal(node.varName()) != null) {
            // Variable already exists - skip creation without error
            return new NormalResult(null);
        }

        // Create Variable in local scope so mutations are visible
        Variable variable = switch (node.varType().toUpperCase()) {
            case "INT"    -> new IntegerVariable(node.varName(), initialValue.toInt().value());
            case "DOUBLE" -> new DoubleVariable(node.varName(), initialValue.toDouble().value());
            case "STRING" -> new StringVariable(node.varName(), initialValue.toDisplayString());
            case "BOOL"   -> new BoolVariable(node.varName(), initialValue.toBool().value());
            default       -> new StringVariable(node.varName(), initialValue.toDisplayString());
        };
        exec.setLocal(node.varName(), variable);

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

        if (target instanceof LiteralNode literalNode) {
            String template = literalNode.value().toDisplayString();
            String resolvedName = interpolateParamRefs(template);
            Variable variable = context.getVariable(resolvedName);
            if (variable == null) {
                throw new InterpreterException(
                    "Variable not found: " + resolvedName + " (from template " + template + ")",
                    exec,
                    location
                );
            }
            return variable;
        }

        if (target instanceof PointerDerefNode pointerDerefNode) {
            ExecutionResult result = execute(pointerDerefNode.expression());
            String resolvedName = interpolateParamRefs(result.getValue().toDisplayString());
            return resolveMethodTarget(new VariableNode(resolvedName, location), location);
        }

        throw new InterpreterException(
            "Unsupported method call target: " + target.getClass().getSimpleName(),
            exec,
            location
        );
    }

    /**
     * Replaces $N parameter references in a string with their values from
     * the current execution frame locals (e.g. "ANIMOPOLE$1" with $1=3
     * becomes "ANIMOPOLE3").
     */
    private String interpolateParamRefs(String template) {
        if (!template.contains("$")) return template;
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < template.length()) {
            if (template.charAt(i) == '$' && i + 1 < template.length()
                    && Character.isDigit(template.charAt(i + 1))) {
                int j = i + 1;
                while (j < template.length() && Character.isDigit(template.charAt(j))) j++;
                String paramName = template.substring(i, j);
                Variable paramVar = exec.getLocal(paramName);
                if (paramVar != null) {
                    sb.append(paramVar.value().toDisplayString());
                } else {
                    sb.append(paramName);
                }
                i = j;
            } else {
                sb.append(template.charAt(i));
                i++;
            }
        }
        return sb.toString();
    }

    private static Variable valueToVariable(String name, Value value) {
        return switch (value) {
            case IntValue v    -> new IntegerVariable(name, v.value());
            case DoubleValue v -> new DoubleVariable(name, v.value());
            case StringValue v -> new StringVariable(name, v.value());
            case BoolValue v   -> new BoolVariable(name, v.value());
            default            -> new StringVariable(name, value.toDisplayString());
        };
    }
}
