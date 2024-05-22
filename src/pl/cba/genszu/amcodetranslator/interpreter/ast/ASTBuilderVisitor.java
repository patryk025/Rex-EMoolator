package pl.cba.genszu.amcodetranslator.interpreter.ast;

import java.util.*;
import pl.cba.genszu.amcodetranslator.interpreter.*;
import pl.cba.genszu.amcodetranslator.interpreter.antlr.*;
import pl.cba.genszu.amcodetranslator.interpreter.ast.expressions.*;
import pl.cba.genszu.amcodetranslator.interpreter.ast.statements.*;

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
        return new BlockStatement(nodes);
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

        return createExpressionTree(operands);
    }

    private Expression createExpressionTree(List<Expression> operands) {
		//implementation of Djikstra's Shunting-Yard algorithm
		
        Stack<Expression> operandStack = new Stack<>();
        Stack<String> operatorStack = new Stack<>();

        List<Expression> postfix = new ArrayList<>();

        for (Expression operand : operands) {
			if(!(operand instanceof OperatorExpression)) {
				operandStack.push(operand);
			}
			else {
				String operandVal = operand.evaluate(this.context).toString();
				if(operandVal.equals("("))
					operatorStack.push("(");
				else if(operandVal.equals(")")) {
					while(!operatorStack.isEmpty() && !operatorStack.peek().equals("(")) {
						postfix.add(operators.pop());
					}
					try {
						operatorStack.pop(); //remove left parenthesis
					}
					catch(EmptyStackException e) {
						System.out.println("DEBUG: pusty stack, wyrażenie => "+infix);
						throw e;
					}
				}
			}
			
			
            
            while (!operatorStack.isEmpty() && precedence(operator) <= precedence(operatorStack.peek())) {
                Expression right = operandStack.pop();
                Expression left = operandStack.pop();
                String op = operatorStack.pop();
                operandStack.push(new ArithmeticExpression(left, right, op));
            }

            operatorStack.push(operator);
            operandStack.push(operand);
        }

        while (!operatorStack.isEmpty()) {
            Expression right = operandStack.pop();
            Expression left = operandStack.pop();
            String op = operatorStack.pop();
            operandStack.push(new ArithmeticExpression(left, right, op));
        }

        return operandStack.pop();
    }

    private int precedence(String operator) {
        switch (operator) {
            case "*":
            case "@":
            case "%":
                return 2;
            case "+":
            case "-":
                return 1;
            default:
                return -1;
        }
    }
}
