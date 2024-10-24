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
    public Node visitBlock(AidemMediaParser.BlockContext ctx) {
        List<Node> nodes = new ArrayList<>();
        for(int i = 0; i < ctx.statement().size(); i++) {
            Node node = visit(ctx.statement(i));
            if(node != null)
                nodes.add(node);
        }
        return new BlockExpression(nodes);
    }

    @Override
    public Node visitStatement(AidemMediaParser.StatementContext ctx) {
        if (ctx.functionCall() != null) {
            return visit(ctx.functionCall());
        } else if (ctx.specialFunction() != null) {
            return visit(ctx.specialFunction());
        } else if (ctx.loopStatement() != null) {
            return visit(ctx.loopStatement());
        } else if (ctx.ifStatement() != null) {
            return visit(ctx.ifStatement());
        } else if (ctx.expression() != null) {
            return visit(ctx.expression());
        } else if (ctx.inlineComment() != null) {
            return visit(ctx.inlineComment());
        }
        return null;
    }

    @Override
    public Node visitExpression(AidemMediaParser.ExpressionContext ctx) {
        if (ctx.primitive() != null) {
            return visit(ctx.primitive());
        } else if (ctx.variable() != null) {
            return visit(ctx.variable());
        } else if (ctx.functionCall() != null) {
            return visit(ctx.functionCall());
        }
        else if (ctx.mathExpression() != null) {
            return visit(ctx.mathExpression());
        }
        else if (ctx.variableReference() != null) {
            return visit(ctx.variableReference());
        }
        else if (ctx.structField() != null) {
            return visit(ctx.structField());
        }
        return null;
    }

    @Override
    public Node visitFunctionCall(AidemMediaParser.FunctionCallContext ctx) {
        String methodName = ctx.functionName().getText();

        Expression targetVariable = null;
        if(ctx.variable() != null) {
            targetVariable = (Expression) visitVariable(ctx.variable());
        }
        else if(ctx.structField() != null) {
            targetVariable = (Expression) visitStructField(ctx.structField());
        }
        else if(ctx.variableReference() != null) {
            targetVariable = (Expression) visitVariableReference(ctx.variableReference());
        }

        List<Expression> arguments = new ArrayList<>();
        if(ctx.paramList() != null) {
            for (int i = 0; i < ctx.paramList().param().size(); i++) {
                arguments.add((Expression) visit(ctx.paramList().param(i)));
            }
        }

        return new MethodCallExpression(targetVariable, methodName, arguments.toArray(new Expression[0]));
    }

    @Override
    public Node visitVariable(AidemMediaParser.VariableContext ctx) {
        return new VariableExpression(ctx.getText());
    }

    @Override
    public Node visitStructField(AidemMediaParser.StructFieldContext ctx) {
        return new StructExpression(ctx.variable().getText(), ctx.structColumn(0).getText());
    }

    @Override
    public Node visitVariableReference(AidemMediaParser.VariableReferenceContext ctx) {
        if(ctx.variable() != null) {
            return new VariableExpression(ctx.variable().getText());
        }
        else if(ctx.mathExpression() != null) {
            return new VariableExpression((Expression) visit(ctx.mathExpression()));
        }
        else {
            return new VariableExpression(ctx.getText());
        }
    }

    @Override
    public Node visitPrimitive(AidemMediaParser.PrimitiveContext ctx) {
        if(ctx.number() != null) {
            if(ctx.number().INTEGER() != null) {
                return new ConstantExpression(Integer.parseInt(ctx.number().INTEGER().getText()));
            }
            else {
                return new ConstantExpression(Double.parseDouble(ctx.number().FLOAT().getText()));
            }
        }
        else if(ctx.string() != null) {
            String tmp = ctx.string().getText();
            if(tmp.startsWith("\"\"") && tmp.endsWith("\"\"")) {
                return new ConstantExpression(tmp.substring(1, tmp.length() - 1));
            }
            else {
                return new ConstantExpression(tmp);
            }
        }
        else {
            return new ConstantExpression(ctx.BOOLEAN().getText().equals("TRUE"));
        }
    }

    private Expression createExpressionTree(List<Expression> terms, List<String> operators) {
        Stack<Expression> stack = new Stack<>();

        stack.push(terms.get(0));

        for (int i = 0; i < operators.size(); i++) {
            Expression right = terms.get(i + 1);
            String operator = operators.get(i);
            Expression left = stack.pop();

            stack.push(new ArithmeticExpression(left, right, operator));
        }

        return stack.pop();
    }

    @Override
    public Node visitMathFactor(AidemMediaParser.MathFactorContext ctx) {
        if (ctx.getChildCount() == 1) {
            return visit(ctx.getChild(0));
        } else if (ctx.getChildCount() == 3) { // inside brackets
            return visit(ctx.getChild(1));
        }

        throw new IllegalArgumentException("Nieznana struktura wyrażenia: " + ctx.getText());
    }

    @Override
    public Node visitMathExpression(AidemMediaParser.MathExpressionContext ctx) {
        List<Expression> terms = new ArrayList<>();
        List<String> operators = new ArrayList<>();

        for (int i = 0; i < ctx.getChildCount(); i++) {
            if (i % 2 == 0) {
                terms.add((Expression) visit(ctx.getChild(i)));
            } else {
                operators.add(ctx.getChild(i).getText().trim());
            }
        }

        return createExpressionTree(terms, operators);
    }

    @Override
    public Node visitIfStatement(AidemMediaParser.IfStatementContext ctx) {
        ConditionExpression condition;

        if (ctx.ifCondition().conditionSimple() != null) {
            condition = (ConditionExpression) visit(ctx.ifCondition().conditionSimple());
        } else {
            condition = (ConditionExpression) visit(ctx.ifCondition().conditionComplex());
        }

        Expression trueBranch = null;
        if (ctx.trueBranch() != null)
            trueBranch = (Expression) visit(ctx.trueBranch());
        Expression falseBranch = null;
        if (ctx.falseBranch() != null)
            falseBranch = (Expression) visit(ctx.falseBranch());

        return new IfStatement(condition, trueBranch, falseBranch);
    }

    @Override
    public Node visitConditionSimple(AidemMediaParser.ConditionSimpleContext ctx) {
        Expression left = (Expression) visitExpressonInParameters(ctx.expression(0));
        String operator = ctx.comparator().getText().substring(1, ctx.comparator().getText().length() - 1);
        Expression right = (Expression) visitExpressonInParameters(ctx.expression(1));

        return new ConditionExpression(left, right, operator);
    }

    @Override
    public Node visitConditionComplex(AidemMediaParser.ConditionComplexContext ctx) {
        List<Expression> terms = new ArrayList<>();
        List<String> logicOperators = new ArrayList<>();

        for (int i = 1; i < ctx.getChildCount() - 1; i++) {
            if (i % 2 != 0) {
                terms.add((Expression) visit(ctx.getChild(i)));
            } else {
                logicOperators.add(ctx.getChild(i).getText());
            }
        }

        return buildLogicalConditionTree(terms, logicOperators);
    }

    @Override
    public Node visitComplexTerm(AidemMediaParser.ComplexTermContext ctx) {
        if(!ctx.expression().isEmpty()) {
            return new ConditionExpression((Expression) visit(ctx.expression(0)), (Expression) visit(ctx.expression(1)), ctx.comparator().getText());
        }
        else {
            return visit(ctx.conditionComplex());
        }
    }

    private Expression buildLogicalConditionTree(List<Expression> terms, List<String> operators) {
        Stack<Expression> stack = new Stack<>();
        stack.push(terms.get(0));

        for (int i = 0; i < operators.size(); i++) {
            Expression right = terms.get(i + 1);
            String operator = operators.get(i);
            Expression left = stack.pop();

            stack.push(new ConditionExpression(left, right, operator));
        }

        return stack.pop();
    }

    @Override
    public Node visitTrueBranch(AidemMediaParser.TrueBranchContext ctx) {
        if (ctx.string() != null) {
            return new VariableExpression(ctx.string().getText().substring(1, ctx.string().getText().length() - 1));
        } else {
            return visit(ctx.block());
        }
    }

    @Override
    public Node visitFalseBranch(AidemMediaParser.FalseBranchContext ctx) {
        if (ctx.string() != null) {
            return new VariableExpression(ctx.string().getText().substring(1, ctx.string().getText().length() - 1));
        } else {
            return visit(ctx.block());
        }
    }

    @Override
    public Node visitLoopStatement(AidemMediaParser.LoopStatementContext ctx) {
        String loopType = ctx.getChild(0).getText();
        switch (loopType) {
            /*case "@FOR": // TODO: implement
                return visitForLoop(ctx);*/
            case "@WHILE":
                return visitWhileLoop(ctx);
            case "@LOOP":
                return visitLoopLoop(ctx);
        }

        return super.visitLoopStatement(ctx);
    }

    public Node visitWhileLoop(AidemMediaParser.LoopStatementContext ctx) {
        ConditionExpression condition = (ConditionExpression) visit(ctx.conditionSimple());
        Expression code = null;
        if(ctx.variable() != null) {
            code = (Expression) visit(ctx.variable(0));
        }
        else if(ctx.block() != null) {
            code = (Expression) visit(ctx.block());
        }
        return new WhileStatement(condition, code);
    }

    public Node visitLoopLoop(AidemMediaParser.LoopStatementContext ctx) {
        Expression code = null;
        if(!ctx.variable().isEmpty()) {
            code = (Expression) visit(ctx.variable(0));
        }
        else if(ctx.block() != null) {
            code = (Expression) visit(ctx.block());
        }
        Expression start = new VariableExpression((Expression) visitExpressonInParameters(ctx.expression(0)));
        Expression end = new VariableExpression((Expression) visitExpressonInParameters(ctx.expression(1)));
        Expression step = new VariableExpression((Expression) visitExpressonInParameters(ctx.expression(2)));
        return new LoopStatement(start, end, step, code);
    }

    @Override
    public Node visitSpecialFunction(AidemMediaParser.SpecialFunctionContext ctx) {
        String instructionName = ctx.functionName().getText();
        List<AidemMediaParser.ParamContext> params = new ArrayList<>();
        if(ctx.paramList() != null) {
            params = ctx.paramList().param();
        }

        switch(instructionName) {
            case "BOOL":
            case "STRING":
            case "DOUBLE":
            case "INT":
                String varName = params.get(0).getText();
                AidemMediaParser.ParamContext param2 = params.get(1);
                return new VariableDefinitionStatement(instructionName, varName, (Expression) visit(param2));
            case "CONV":
                Expression castedVariable = (Expression) visit(params.get(0));
                String targetVariableType = (String) ((Expression) visit(params.get(1))).evaluate(context);
                return new ConvStatement(castedVariable, targetVariableType);
            case "RETURN":
                return new ReturnExpression((Expression) visit(params.get(0)));
            case "BREAK":
                return new BreakStatement();
            case "ONEBREAK":
                return new OneBreakStatement();
            case "GETAPPLICATIONNAME":
                return new ConstantExpression(this.context.getGame().getApplicationVariable().getName());
            case "GETCURRENTSCENE":
                return new ConstantExpression(this.context.getGame().getCurrentScene());
            case "MSGBOX":
                // TODO: implement message box (but it looks like doesn't work in original game)
                return null;
            default:
                return null;
        }
    }

    public Node visitExpressonInParameters(AidemMediaParser.ExpressionContext ctx) {
        if (ctx.primitive() != null) {
            if(ctx.primitive().string() != null) {
                String tmp = ctx.primitive().string().getText();
                if(ctx.primitive().string().ESCAPED_STRING() != null) {
                    return new ConstantExpression(tmp.substring(2, tmp.length() - 2));
                }
                else {
                    tmp = tmp.substring(1, tmp.length() - 1);

                    // check if it's a number
                    if (tmp.matches("\\d+")) {
                        return new ConstantExpression(Integer.parseInt(tmp));
                    } else if (tmp.matches("[+-]?([0-9]*[.])?[0-9]+")) {
                        return new ConstantExpression(Double.parseDouble(tmp));
                    } else {
                        return new VariableExpression(tmp);
                    }
                }
            }
            else {
                return visit(ctx.primitive());
            }
        } else { // if somehow it pass this condition run as usual
            return visit(ctx);
        }
    }
}
