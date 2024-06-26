package pl.cba.genszu.amcodetranslator.interpreter.ast.expressions;

import pl.cba.genszu.amcodetranslator.interpreter.Context;
import pl.cba.genszu.amcodetranslator.interpreter.ast.Expression;
import pl.cba.genszu.amcodetranslator.interpreter.ast.Node;
import pl.cba.genszu.amcodetranslator.interpreter.ast.Statement;
import pl.cba.genszu.amcodetranslator.interpreter.exceptions.BreakException;
import pl.cba.genszu.amcodetranslator.interpreter.exceptions.OneBreakException;

import java.util.List;

public class BlockExpression extends Expression {
    private final List<Node> nodes;

    public BlockExpression(List<Node> nodes) {
        this.nodes = nodes;
    }

    @Override
    public Object evaluate(Context context) {
        try {
            for (Node node : nodes) {
                if (node instanceof Statement) {
                    ((Statement) node).execute(context);
                } else {
                    if (node instanceof ReturnExpression) {
                        return ((ReturnExpression) node).evaluate(context);
                    } else {
                        ((Expression) node).evaluate(context);
                    }
                }
            }
        } catch (OneBreakException e) {
            // zwraca nulla i przerywa procedurę, nie propaguje się dalej
            return null;
        } catch (BreakException e) {
            // nic nie zwraca i propaguje się dalej
            throw e;
        }

        return null;
    }
}
