package pl.cba.genszu.amcodetranslator.interpreter.ast.expressions;

import pl.cba.genszu.amcodetranslator.interpreter.Context;
import pl.cba.genszu.amcodetranslator.interpreter.ast.Expression;
import pl.cba.genszu.amcodetranslator.interpreter.ast.Node;
import pl.cba.genszu.amcodetranslator.interpreter.ast.Statement;

import java.util.List;

public class BlockExpression extends Statement {
    private final List<Node> nodes;

    public BlockExpression(List<Node> nodes) {
        this.nodes = nodes;
    }

    @Override
    public void execute(Context context) {
        for (Node node : nodes) {
            if(node instanceof Statement)
                ((Statement) node).execute(context);
            else
                ((Expression) node).evaluate(context);
        }
    }
}
