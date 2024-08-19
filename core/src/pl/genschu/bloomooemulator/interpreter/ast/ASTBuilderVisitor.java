package pl.genschu.bloomooemulator.interpreter.ast;

import java.util.*;

import org.antlr.v4.runtime.tree.ParseTree;
import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.antlr.AidemMediaLexer;
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
        String methodName = "";
        if(ctx.stringRef() != null || ctx.iterator() != null || ctx.struct() != null || ctx.variable() != null || ctx.varWithNumber() != null) {
            methodName = ctx.literal(0).getText();
        }
        else {
            methodName = ctx.literal(1).getText();
        }

        Expression targetExpression;
        if (ctx.literal() != null && ctx.literal().size() == 2) {
            targetExpression = new VariableExpression((Expression) visitLiteral(ctx.literal(0)));
        } else if (ctx.iterator() != null) {
            targetExpression = new VariableExpression("_I_");
        } else if (ctx.stringRef() != null) {
            targetExpression = (Expression) visit(ctx.stringRef());
        } else if (ctx.struct() != null) {
            targetExpression = (Expression) visitStruct(ctx.struct());
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
        if(!ctx.variable().isEmpty()) {
            List<Expression> operands = new ArrayList<>();
            for (int i = 0; i < ctx.getChildCount(); i++) {
                ParseTree child = ctx.getChild(i);

                if(child instanceof AidemMediaParser.VariableContext) {
                    operands.add((Expression) visit(child));
                }
                else {
                    operands.add(new ConstantExpression(child.getText()));
                }

                if(i < ctx.getChildCount()-1) {
                    operands.add(new OperatorExpression("+"));
                }
            }

            Expression expression = createExpressionTree(InfixToPostfix.convertToPostfix(operands));
            return new PointerExpression(expression);
        }
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
        else if(ctx.struct(0) != null) {
            AidemMediaParser.StructContext structContext = ctx.struct(0);
            return new StructExpression(structContext.literal(0).getText(), structContext.literal(1).getText());
        }
        else if(ctx.functionFire(0) != null) {
            return visit(ctx.functionFire(0));
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
    public Node visitLogic(AidemMediaParser.LogicContext ctx) {
        return new OperatorExpression(ctx.getText());
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

    @Override
    public Node visitStruct(AidemMediaParser.StructContext ctx) {
        return new StructExpression(ctx.literal(0).getText(), ctx.literal(1).getText());
    }

    @Override
    public Node visitVariable(AidemMediaParser.VariableContext ctx) {
        return new VariableExpression(ctx.getText());
    }

    @Override
    public Node visitParam(AidemMediaParser.ParamContext ctx) {
        if(ctx.number() != null) {
            String number = ctx.number().getText();
            if(ctx.arithmetic() != null && ctx.arithmetic().getText().equals("-")) {
                return new ConstantExpression(Integer.parseInt("-" + number));
            }
            return new ConstantExpression(Integer.parseInt(ctx.getText()));
        }
        else if(ctx.floatNumber() != null) {
            String number = ctx.floatNumber().getText();
            if(ctx.arithmetic() != null && ctx.arithmetic().getText().equals("-")) {
                return new ConstantExpression(Double.parseDouble("-" + number));
            }
            return new ConstantExpression(Double.parseDouble(ctx.getText()));
        }
        else if(ctx.literal() != null) {
            return new VariableExpression((Expression) visit(ctx.literal()));
        }
        return super.visitParam(ctx);
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
                Expression operand;
                if(ctx.getChild(i) instanceof AidemMediaParser.LiteralContext) {
                    operand = new VariableExpression((Expression) visit(ctx.getChild(i)));
                }
                else {
                    operand = (Expression) visit(ctx.getChild(i));
                }

                if(operand != null)
				    operands.add(operand);
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
        Expression left;
        if(ctx.getChild(0) instanceof AidemMediaParser.LiteralContext) {
            left = new VariableExpression((Expression) visit(ctx.getChild(0)));
        }
        else {
            left = (Expression) visit(ctx.getChild(0));
        }
        String operator = ctx.getChild(1).getText();
        Expression right;
        if(ctx.getChild(2) instanceof AidemMediaParser.LiteralContext) {
            right = new VariableExpression((Expression) visit(ctx.getChild(2)));
        }
        else {
            right = (Expression) visit(ctx.getChild(2));
        }
        return new ConditionExpression(left, right, operator);
    }

    private Expression buildConditionExpression(AidemMediaParser.ConditionContext ctx) {
        List<Expression> operands = new ArrayList<>();
        for (int i = 0; i < ctx.getChildCount(); i++) {
            Node operand = visit(ctx.getChild(i));

            if(operand instanceof ConstantExpression) {
                operand = new VariableExpression((Expression) operand);
            }

            operands.add((Expression) operand);
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
            case "BREAK":
                return new BreakStatement();
            case "ONEBREAK":
                return new OneBreakStatement();
			case "GETAPPLICATIONNAME":
				// TODO: podejrzeć jak to PikLib zwraca
				return new ConstantExpression("<no value>");
			case "GETCURRENTSCENE":
				return new ConstantExpression(this.context.getGame().getCurrentScene());
				
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
            Node left = visit(conditionsSimple.param(0));
            Node right = visit(conditionsSimple.param(1));

            if(left instanceof ConstantExpression) {
                left = new VariableExpression((Expression) left);
            }

            if(right instanceof ConstantExpression) {
                right = new VariableExpression((Expression) right);
            }
            condition = new ConditionExpression((Expression) left, (Expression) right, conditionsSimple.compare().getText());
        }
        else {
            condition = (ConditionExpression) buildConditionExpression(ctx.condition());
        }

        Node trueBranch = visit(ctx.ifTrue());
        Node falseBranch = visit(ctx.ifFalse());

        if(trueBranch instanceof ConstantExpression) {
            trueBranch = new VariableExpression((Expression) trueBranch);
        }

        if(falseBranch instanceof ConstantExpression) {
            falseBranch = new VariableExpression((Expression) falseBranch);
        }

        return new IfStatement(condition, (Expression) trueBranch, (Expression) falseBranch);
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
