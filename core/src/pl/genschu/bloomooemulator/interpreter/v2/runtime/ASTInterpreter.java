package pl.genschu.bloomooemulator.interpreter.v2.runtime;

import pl.genschu.bloomooemulator.interpreter.v2.ast.*;
import pl.genschu.bloomooemulator.interpreter.v2.errors.InterpreterException;
import pl.genschu.bloomooemulator.interpreter.v2.errors.SourceLocation;
import pl.genschu.bloomooemulator.interpreter.v2.values.*;

/**
 * Interprets AST nodes and produces execution results.
 */
public class ASTInterpreter {
    private final ExecutionContext context;

    public ASTInterpreter(ExecutionContext context) {
        this.context = context;
    }

    /**
     * Executes an AST node and returns the result.
     */
    public ExecutionResult execute(ASTNode node) {
        // Pattern matching magic! No more instanceof chains! :D
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
        // Resolve variable from legacy context
        if (context.getLegacyContext() == null) {
            // No context - just return a reference
            return new NormalResult(new VariableRef(node.name()));
        }

        pl.genschu.bloomooemulator.interpreter.variable.Variable variable =
            context.getLegacyContext().getVariable(node.name());

        if (variable == null) {
            throw new InterpreterException(
                "Variable not found: " + node.name(),
                context,
                node.location()
            );
        }

        // Convert v1 Variable to v2 Value using the bridge
        Value value = VariableBridge.toValue(variable);
        return new NormalResult(value);
    }

    // === BLOCKS ===

    private ExecutionResult executeBlock(BlockNode node) {
        context.pushFrame("Block", "{...}", node.location());
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
            context.popFrame();
        }
    }

    // === ARITHMETIC ===

    private ExecutionResult executeArithmetic(ArithmeticNode node) {
        ExecutionResult leftResult = execute(node.left());
        if (!leftResult.shouldContinue()) return leftResult;

        ExecutionResult rightResult = execute(node.right());
        if (!rightResult.shouldContinue()) return rightResult;

        Value result = performArithmetic(
            leftResult.getValue(),
            rightResult.getValue(),
            node.operator()
        );

        return new NormalResult(result);
    }

    private Value performArithmetic(Value left, Value right, ArithmeticNode.ArithmeticOp op) {
        // Handle int + int
        if (left instanceof IntValue(int value) && right instanceof IntValue(int value1)) {
            return switch (op) {
                case ADD -> new IntValue(value + value1);
                case SUBTRACT -> new IntValue(value - value1);
                case MULTIPLY -> new IntValue(value * value1);
                case DIVIDE -> new IntValue(value1 != 0 ? value / value1 : 0);
                case MODULO -> new IntValue(value1 != 0 ? value % value1 : 0);
            };
        }

        // Handle double operations (convert if needed)
        double leftVal = toDouble(left);
        double rightVal = toDouble(right);

        return switch (op) {
            case ADD -> new DoubleValue(leftVal + rightVal);
            case SUBTRACT -> new DoubleValue(leftVal - rightVal);
            case MULTIPLY -> new DoubleValue(leftVal * rightVal);
            case DIVIDE -> new DoubleValue(rightVal != 0 ? leftVal / rightVal : 0.0);
            case MODULO -> new DoubleValue(rightVal != 0 ? leftVal % rightVal : 0.0);
        };
    }

    private double toDouble(Value value) {
        return switch (value) {
            case IntValue v -> v.value();
            case DoubleValue v -> v.value();
            case StringValue v -> {
                DoubleValue d = v.tryParseDouble();
                yield d != null ? d.value() : 0.0;
            }
            default -> 0.0;
        };
    }

    // === COMPARISON ===

    private ExecutionResult executeComparison(ComparisonNode node) {
        ExecutionResult leftResult = execute(node.left());
        if (!leftResult.shouldContinue()) return leftResult;

        ExecutionResult rightResult = execute(node.right());
        if (!rightResult.shouldContinue()) return rightResult;

        boolean result = performComparison(
            leftResult.getValue(),
            rightResult.getValue(),
            node.operator()
        );

        return new NormalResult(BoolValue.of(result));
    }

    private boolean performComparison(Value left, Value right, ComparisonNode.ComparisonOp op) {
        // String comparison
        if (left instanceof StringValue(String value) && right instanceof StringValue(String value1)) {
            int cmp = value.compareTo(value1);
            return switch (op) {
                case EQUAL -> cmp == 0;
                case NOT_EQUAL -> cmp != 0;
                case LESS -> cmp < 0;
                case LESS_EQUAL -> cmp <= 0;
                case GREATER -> cmp > 0;
                case GREATER_EQUAL -> cmp >= 0;
            };
        }

        // Numeric comparison
        double leftVal = toDouble(left);
        double rightVal = toDouble(right);

        return switch (op) {
            case EQUAL -> leftVal == rightVal;
            case NOT_EQUAL -> leftVal != rightVal;
            case LESS -> leftVal < rightVal;
            case LESS_EQUAL -> leftVal <= rightVal;
            case GREATER -> leftVal > rightVal;
            case GREATER_EQUAL -> leftVal >= rightVal;
        };
    }

    // === LOGICAL ===

    private ExecutionResult executeLogical(LogicalNode node) {
        ExecutionResult leftResult = execute(node.left());
        if (!leftResult.shouldContinue()) return leftResult;

        boolean leftBool = toBool(leftResult.getValue());

        // Short-circuit evaluation
        if (node.operator() == LogicalNode.LogicalOp.AND && !leftBool) {
            return new NormalResult(BoolValue.FALSE);
        }
        if (node.operator() == LogicalNode.LogicalOp.OR && leftBool) {
            return new NormalResult(BoolValue.TRUE);
        }

        ExecutionResult rightResult = execute(node.right());
        if (!rightResult.shouldContinue()) return rightResult;

        boolean rightBool = toBool(rightResult.getValue());

        boolean result = switch (node.operator()) {
            case AND -> leftBool && rightBool;
            case OR -> leftBool || rightBool;
        };

        return new NormalResult(BoolValue.of(result));
    }

    private boolean toBool(Value value) {
        return switch (value) {
            case BoolValue v -> v.value();
            case IntValue v -> v.value() != 0;
            case DoubleValue v -> v.value() != 0.0;
            case StringValue v -> v.value().equalsIgnoreCase("TRUE");
            default -> false;
        };
    }

    // === CONTROL FLOW ===

    private ExecutionResult executeIf(IfNode node) {
        context.pushFrame("If", "@IF", node.location());
        try {
            ExecutionResult condResult = execute(node.condition());
            if (!condResult.shouldContinue()) return condResult;

            boolean condition = toBool(condResult.getValue());

            if (condition) {
                return execute(node.thenBranch());
            } else if (node.hasElse()) {
                return execute(node.elseBranch());
            } else {
                return new NormalResult(NullValue.INSTANCE);
            }
        } finally {
            context.popFrame();
        }
    }

    private ExecutionResult executeLoop(LoopNode node) {
        context.pushFrame("Loop", "@LOOP", node.location());
        try {
            // Evaluate loop bounds
            ExecutionResult startResult = execute(node.start());
            if (!startResult.shouldContinue()) return startResult;

            ExecutionResult diffResult = execute(node.diff());
            if (!diffResult.shouldContinue()) return diffResult;

            ExecutionResult stepResult = execute(node.step());
            if (!stepResult.shouldContinue()) return stepResult;

            int start = toInt(startResult.getValue());
            int diff = toInt(diffResult.getValue());
            int step = toInt(stepResult.getValue());

            // Execute loop
            for (int i = start; i < start + diff; i += step) {
                // Set _I_ variable (TODO: implement properly)
                ExecutionResult bodyResult = execute(node.body());

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
            context.popFrame();
        }
    }

    private ExecutionResult executeWhile(WhileNode node) {
        context.pushFrame("While", "@WHILE", node.location());
        try {
            while (true) {
                ExecutionResult condResult = execute(node.condition());
                if (!condResult.shouldContinue()) return condResult;

                if (!toBool(condResult.getValue())) {
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
            context.popFrame();
        }
    }

    private int toInt(Value value) {
        return switch (value) {
            case IntValue v -> v.value();
            case DoubleValue v -> (int) v.value();
            case StringValue v -> {
                IntValue i = v.tryParseInt();
                yield i != null ? i.value() : 0;
            }
            default -> 0;
        };
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

    // === TODO: Implement these ===

    private ExecutionResult executeMethodCall(MethodCallNode node) {
        // Step 1: Execute target to get the object
        ExecutionResult targetResult = execute(node.target());
        if (!targetResult.shouldContinue()) return targetResult;

        Value targetValue = targetResult.getValue();

        // Step 2: Get the actual Variable object from context
        // We need the Variable because it has the methods!
        pl.genschu.bloomooemulator.interpreter.variable.Variable targetVariable =
            resolveVariableObject(node.target(), targetValue);

        if (targetVariable == null) {
            throw new InterpreterException(
                "Cannot call method on " + targetValue.toDisplayString(),
                context,
                node.location()
            );
        }

        // Step 3: Evaluate arguments
        java.util.List<Object> methodArgs = new java.util.ArrayList<>();
        for (ASTNode argNode : node.arguments()) {
            ExecutionResult argResult = execute(argNode);
            if (!argResult.shouldContinue()) return argResult;

            // Convert Value to Variable for method call
            Value argValue = argResult.getValue();
            pl.genschu.bloomooemulator.interpreter.variable.Variable argVariable =
                VariableBridge.toVariable(
                    argValue,
                    "_arg_",
                    context.getLegacyContext()
                );
            methodArgs.add(argVariable);
        }

        // Step 4: Call the method on the Variable
        try {
            pl.genschu.bloomooemulator.interpreter.variable.Variable result =
                targetVariable.fireMethod(node.methodName(), methodArgs.toArray());

            // Step 5: Convert result back to Value
            if (result == null) {
                return new NormalResult(NullValue.INSTANCE);
            }

            Value resultValue = VariableBridge.toValue(result);
            return new NormalResult(resultValue);

        } catch (Exception e) {
            throw new InterpreterException(
                "Method call failed: " + node.methodName() + " - " + e.getMessage(),
                context,
                node.location(),
                e
            );
        }
    }

    /**
     * Helper: Resolves a Variable object from target node and value.
     *
     * Why do we need this?
     * - If target is a VariableNode, we look it up in context
     * - If target evaluated to VariableRef, we look it up
     * - Otherwise, we try to create a temporary Variable from the Value
     */
    private pl.genschu.bloomooemulator.interpreter.variable.Variable resolveVariableObject(
        ASTNode targetNode,
        Value targetValue
    ) {
        if (context.getLegacyContext() == null) {
            return null;
        }

        // If it's a VariableRef, look it up
        if (targetValue instanceof VariableRef ref) {
            return context.getLegacyContext().getVariable(ref.name());
        }

        // If target is a VariableNode, look it up directly
        if (targetNode instanceof VariableNode varNode) {
            return context.getLegacyContext().getVariable(varNode.name());
        }

        // Otherwise, create a temporary Variable from the Value
        // (This handles cases like (5 + 3)^ADD(2))
        try {
            return VariableBridge.toVariable(
                targetValue,
                "_temp_",
                context.getLegacyContext()
            );
        } catch (Exception e) {
            return null;
        }
    }

    private ExecutionResult executePointerDeref(PointerDerefNode node) {
        // TODO: Implement pointer dereferencing
        return execute(node.expression());
    }

    private ExecutionResult executeStructAccess(StructAccessNode node) {
        // TODO: Implement struct field access
        return new NormalResult(NullValue.INSTANCE);
    }

    private ExecutionResult executeVarDef(VarDefNode node) {
        // Execute initial value expression
        ExecutionResult valueResult = execute(node.initialValue());
        if (!valueResult.shouldContinue()) return valueResult;

        Value initialValue = valueResult.getValue();

        // Create variable in legacy context
        if (context.getLegacyContext() != null) {
            pl.genschu.bloomooemulator.interpreter.variable.Variable variable =
                VariableBridge.toVariable(
                    initialValue,
                    node.varName(),
                    context.getLegacyContext()
                );

            context.getLegacyContext().setVariable(node.varName(), variable);
        }

        return new NormalResult(initialValue);
    }

    private ExecutionResult executeConv(ConvNode node) {
        // TODO: Implement type conversion
        ExecutionResult varResult = execute(node.variable());
        if (!varResult.shouldContinue()) return varResult;

        // Basic conversion for now
        Value value = varResult.getValue();
        return new NormalResult(value);
    }
}
