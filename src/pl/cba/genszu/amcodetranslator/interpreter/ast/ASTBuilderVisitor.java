package pl.cba.genszu.amcodetranslator.interpreter.ast;

import pl.cba.genszu.amcodetranslator.interpreter.Context;
import pl.cba.genszu.amcodetranslator.interpreter.antlr.AidemMediaBaseVisitor;
import pl.cba.genszu.amcodetranslator.interpreter.antlr.AidemMediaParser;
import pl.cba.genszu.amcodetranslator.interpreter.ast.expressions.ArithmeticExpression;
import pl.cba.genszu.amcodetranslator.interpreter.ast.expressions.MethodCallExpression;
import pl.cba.genszu.amcodetranslator.interpreter.ast.expressions.PointerExpression;
import pl.cba.genszu.amcodetranslator.interpreter.ast.expressions.VariableExpression;
import pl.cba.genszu.amcodetranslator.interpreter.ast.statements.BlockStatement;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

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
        List<Statement> statements = new ArrayList<>();
        for (AidemMediaParser.InstrContext stmtCtx : ctx.instr()) {
            statements.add((Statement) visit(stmtCtx));
        }
        return new BlockStatement(statements);
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

    private Expression buildExpression(AidemMediaParser.ExpressionContext ctx) {
        if (ctx.getChildCount() == 1) {
            return (Expression) visit(ctx.getChild(0));
        }

        List<Expression> operands = new ArrayList<>();
        List<String> operators = new ArrayList<>();

        operands.add((Expression) visit(ctx.getChild(0)));
        for (int i = 1; i < ctx.getChildCount(); i += 2) {
            operators.add(ctx.getChild(i).getText());
            operands.add((Expression) visit(ctx.getChild(i + 1)));
        }

        return createExpressionTree(operands, operators);
    }

    private Expression createExpressionTree(List<Expression> operands, List<String> operators) {
        Stack<Expression> operandStack = new Stack<>();
        Stack<String> operatorStack = new Stack<>();

        operandStack.push(operands.get(0));

        for (int i = 0; i < operators.size(); i++) {
            String operator = operators.get(i);
            Expression operand = operands.get(i + 1);

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
