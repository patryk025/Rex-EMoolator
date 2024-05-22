package pl.cba.genszu.amcodetranslator.interpreter.ast;

import java.util.*;
import pl.cba.genszu.amcodetranslator.interpreter.*;
import pl.cba.genszu.amcodetranslator.interpreter.antlr.*;
import pl.cba.genszu.amcodetranslator.interpreter.arithmetic.utils.InfixToPostfix;
import pl.cba.genszu.amcodetranslator.interpreter.ast.expressions.*;
import pl.cba.genszu.amcodetranslator.interpreter.ast.statements.*;
import pl.cba.genszu.amcodetranslator.interpreter.types.StructVariable;

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
    public Node visitString(AidemMediaParser.StringContext ctx) {
        if(ctx.string() != null) {
            return new ConstantExpression(ctx.string().getText());
        }
        else {
            return new ConstantExpression(ctx.getText());
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
			if(ctx.getChild(i).getText().matches("[+\\-*@%()]")) {
				operands.add(new OperatorExpression(ctx.getChild(i).getText()));
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
            default:
                return null;
        }
    }
}
