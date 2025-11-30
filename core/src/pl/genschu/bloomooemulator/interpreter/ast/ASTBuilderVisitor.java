package pl.genschu.bloomooemulator.interpreter.ast;

import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.antlr.AidemMediaParser;
import pl.genschu.bloomooemulator.interpreter.antlr.AidemMediaParserBaseVisitor;
import pl.genschu.bloomooemulator.interpreter.ast.expressions.BlockExpression;
import pl.genschu.bloomooemulator.interpreter.ast.expressions.MethodCallExpression;
import pl.genschu.bloomooemulator.interpreter.ast.expressions.StructExpression;
import pl.genschu.bloomooemulator.interpreter.ast.expressions.VariableExpression;

import java.util.ArrayList;
import java.util.List;

public class ASTBuilderVisitor extends AidemMediaParserBaseVisitor<Node> {
    private final Context context;

    public ASTBuilderVisitor(Context context) {
        this.context = context;
    }

    @Override
    public Node visitScript(AidemMediaParser.ScriptContext ctx) {
        List<Node> nodes = new ArrayList<>();
        for(AidemMediaParser.StatementContext statement : ctx.statement()) {
            Node node = visit(statement);
            if(node != null)
                nodes.add(node);
        }
        return new BlockExpression(nodes);
    }

    @Override
    public Node visitMethodCall(AidemMediaParser.MethodCallContext ctx) {
        Expression targetExpression = null;
        if(ctx.objectName() != null) {
            targetExpression = (Expression) visitObjectName(ctx.objectName());
        }
        if(ctx.objectReference() != null) {
            targetExpression = (Expression) visitObjectReference(ctx.objectReference());
        }
        String methodName = ctx.method.getText();
        AidemMediaParser.ArgListOptContext argListOptContext = ctx.argListOpt();
        List<Expression> argumentExpressions = new ArrayList<>();
        if(argListOptContext != null) {
            for(AidemMediaParser.ArgContext argContext : argListOptContext.arg()) {
                argumentExpressions.add((Expression) visit(argContext));
            }
        }

        return new MethodCallExpression(targetExpression, methodName, argumentExpressions.toArray(new Expression[0]));
    }

    @Override
    public Node visitObjectName(AidemMediaParser.ObjectNameContext ctx) {
        if(ctx.field != null) {
            return new StructExpression(ctx.name.getText(), ctx.field.getText());
        }
        return new VariableExpression(ctx.name.getText());
    }
}
