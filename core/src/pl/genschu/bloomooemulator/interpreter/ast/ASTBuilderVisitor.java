package pl.genschu.bloomooemulator.interpreter.ast;

import java.util.*;
import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.arithmetic.utils.InfixToPostfix;
import pl.genschu.bloomooemulator.interpreter.ast.expressions.*;
import pl.genschu.bloomooemulator.interpreter.ast.statements.*;
import pl.genschu.bloomooemulator.interpreter.antlr.AidemMediaBaseVisitor;
import pl.genschu.bloomooemulator.interpreter.antlr.AidemMediaParser;

public class ASTBuilderVisitor extends AidemMediaBaseVisitor<Node> {
    private final Context context;

    public ASTBuilderVisitor(Context context) {
        this.context = context;
    }

    @Override
    public Node visitScript(AidemMediaParser.ScriptContext ctx) {
        return visit(ctx.codeBlock(0));
    }

    @Override
    public Node visitCodeBlock(AidemMediaParser.CodeBlockContext ctx) {
        List<Node> nodes = new ArrayList<>();
        for(int i = 0; i < ctx.getChildCount(); i++) {
            Node node = visit(ctx.getChild(i));
            if(node != null)
                nodes.add(node);
        }
        return new BlockExpression(nodes);
    }

    @Override
    public Node visitFunctionFire(AidemMediaParser.FunctionFireContext ctx) {
        String methodName = ctx.literal(1).getText();

        Expression targetExpression;
        if (ctx.literal() != null) {
            targetExpression = new VariableExpression(ctx.literal(0).getText());
        } else if (ctx.iterator() != null) {
            targetExpression = new VariableExpression("_I_");
        } else if (ctx.stringRef() != null) {
            targetExpression = new PointerExpression((Expression) visit(ctx.stringRef()));
        } else if (ctx.struct() != null) {
            targetExpression = (Expression) visit(ctx.struct());
        } else if (ctx.variable() != null) {
            targetExpression = new VariableExpression(ctx.variable().getText());
        } else if (ctx.varWithNumber() != null) {
            targetExpression = new VariableExpression(ctx.varWithNumber().getText());
        } else {
            throw new RuntimeException("Unsupported function fire target: " + ctx.getText());
        }

        List<Expression> argumentExpressions = new ArrayList<>();
        for (int i = 0; i < ctx.param().size(); i++) {
            argumentExpressions.add((Expression) visit(ctx.param(i)));
        }

        return new MethodCallExpression(targetExpression, methodName, argumentExpressions.toArray(new Expression[0]));
    }

    @Override
    public Node visitExpression(AidemMediaParser.ExpressionContext ctx) {
        return buildExpression(ctx);
    }

    @Override
    public Node visitNumber(AidemMediaParser.NumberContext ctx) {
        return new ConstantExpression(Integer.parseInt(ctx.getText()));
    }

    @Override
    public Node visitFloatNumber(AidemMediaParser.FloatNumberContext ctx) {
        return new ConstantExpression(Double.parseDouble(ctx.getText()));
    }

    @Override
    public Node visitLiteral(AidemMediaParser.LiteralContext ctx) {
        return new ConstantExpression(ctx.getText());
    }

	@Override
	public Node visitIterator(AidemMediaParser.IteratorContext ctx)
	{
		return new ConstantExpression(ctx.getText());
	}

    @Override
    public Node visitString(AidemMediaParser.StringContext ctx) {
        if(ctx.string() != null) {
            return new ConstantExpression(ctx.string().getText());
        }
        else {
			String text = ctx.getText();
            return new ConstantExpression(text.substring(1, text.length()-1));
        }
    }

    @Override
    public Node visitBool(AidemMediaParser.BoolContext ctx) {
        return new ConstantExpression(ctx.getText());
    }

    @Override
    public Node visitStringRef(AidemMediaParser.StringRefContext ctx) {
        if(ctx.expression() != null) {
            return new PointerExpression((Expression) visit(ctx.expression()));
        }
        else {
            return new PointerExpression((Expression) visit(ctx.literal()));
        }
    }

    private Expression buildExpression(AidemMediaParser.ExpressionContext ctx) {
        if (ctx.getChildCount() == 3) {
            return (Expression) visit(ctx.getChild(1));
        }

        List<Expression> operands = new ArrayList<>();
		for (int i = 1; i < ctx.getChildCount()-1; i++) {
			if(ctx.getChild(i).getText().trim().matches("[+\\-*@%()]")) {
				operands.add(new OperatorExpression(ctx.getChild(i).getText().trim()));
			}
			else {
				operands.add((Expression) visit(ctx.getChild(i)));
			}
		}

        return createExpressionTree(InfixToPostfix.convertToPostfix(operands));
    }

    private Expression createExpressionTree(Deque<Expression> operands) {
        Stack<Expression> stack = new Stack<>();

        while (!operands.isEmpty()) {
            Expression operand = operands.removeFirst();
            if (operand instanceof OperatorExpression) {
                Expression right = stack.pop();
                Expression left = stack.pop();
                stack.push(new ArithmeticExpression(left, right, operand.evaluate(null).toString()));
            } else {
                stack.push(operand);
            }
        }

        return stack.pop();
    }

    @Override
    public Node visitConditionPart(AidemMediaParser.ConditionPartContext ctx) {
        Expression left = (Expression) visit(ctx.getChild(0));
        String operator = ctx.getChild(1).getText();
        Expression right = (Expression) visit(ctx.getChild(2));
        return new ConditionExpression(left, right, operator);
    }

    private Expression buildConditionExpression(AidemMediaParser.ConditionContext ctx) {
        List<Expression> operands = new ArrayList<>();
        for (int i = 0; i < ctx.getChildCount(); i++) {
            operands.add((Expression) visit(ctx.getChild(i)));
        }

        return createConditionExpressionTree(InfixToPostfix.convertToPostfix(operands));
    }

    private Expression createConditionExpressionTree(Deque<Expression> operands) {
        if(operands.size() == 1) {
            return operands.pop();
        }

        Stack<Expression> stack = new Stack<>();

        while (!operands.isEmpty()) {
            Expression operand = operands.removeFirst();
            if (operand instanceof OperatorExpression) {
                Expression right = stack.pop();
                Expression left = stack.pop();
                stack.push(new ConditionExpression(left, right, operand.evaluate(null).toString()));
            } else {
                stack.push(operand);
            }
        }

        return stack.pop();
    }

    @Override
    public Node visitInstr(AidemMediaParser.InstrContext ctx) {
        String instructionName = ctx.literal().getText();
        List<AidemMediaParser.ParamContext> params = ctx.param();

        switch(instructionName) {
            case "BOOL":
            case "STRING":
            case "DOUBLE":
            case "INT":
                String varName = ctx.param(0).getText();
                AidemMediaParser.ParamContext param2 = ctx.param(1);
                return new VariableDefinitionStatement(instructionName, varName, (Expression) visit(param2));
            case "CONV":
				Expression castedVariable = (Expression) visit(ctx.param(0));
				String targetVariableType = (String) ((Expression) visit(ctx.param(1))).evaluate(context);
				return new ConvStatement(castedVariable, targetVariableType);
			case "RETURN":
				return new ReturnExpression((Expression) visit(ctx.param(0)));
			case "GETAPPLICATIONNAME":
				// TODO: podejrzeć jak to PikLib zwraca
				return new ConstantExpression("<no value>");
			case "GETCURRENTSCENE":
				return new ConstantExpression(this.context.getSceneName());
				
			default:
                return null;
        }
    }

    @Override
    public Node visitIfInstr(AidemMediaParser.IfInstrContext ctx) {
        ConditionExpression condition;

        AidemMediaParser.ConditionSimpleContext conditionsSimple = ctx.conditionSimple();
        AidemMediaParser.ConditionContext conditions = ctx.condition();

        if(conditionsSimple != null && conditions == null) {
            condition = new ConditionExpression((Expression) visit(conditionsSimple.param(0)), (Expression) visit(conditionsSimple.param(1)), conditionsSimple.compare().getText());
        }
        else {
            condition = (ConditionExpression) buildConditionExpression(ctx.condition());
        }

        return new IfStatement(condition, (Expression) visit(ctx.ifTrue()), (Expression) visit(ctx.ifFalse()));
    }

	@Override
	public Node visitLoopInstr(AidemMediaParser.LoopInstrContext ctx)
	{
		Expression code = (Expression) visit(ctx.loopCodeParam());
		Expression start = (Expression) visit(ctx.param(0));
		Expression end = (Expression) visit(ctx.param(1));
		Expression step = (Expression) visit(ctx.param(2));
		return new LoopStatement(start, end, step, code);
	}

	@Override
	public Node visitLoopCodeParam(AidemMediaParser.LoopCodeParamContext ctx)
	{
		if(ctx.codeBlock() != null) {
			return visit(ctx.codeBlock());
		}
		else if(ctx.string() != null) {
			return visit(ctx.string());
		}
		else {
			return visit(ctx.literal());
		}
	}

    @Override
    public Node visitWhileInstr(AidemMediaParser.WhileInstrContext ctx) {
        ConditionExpression condition = new ConditionExpression((Expression) visit(ctx.param(0)), (Expression) visit(ctx.param(1)), ctx.compare().getText());
        Expression code = null;
        if(ctx.string() != null) {
            code = (Expression) visit(ctx.string());
        }
        else if(ctx.codeBlock() != null) {
            code = (Expression) visit(ctx.codeBlock());
        }
        return new WhileStatement(condition, code);
    }
}
