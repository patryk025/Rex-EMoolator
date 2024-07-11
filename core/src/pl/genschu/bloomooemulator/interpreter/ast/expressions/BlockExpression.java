package pl.genschu.bloomooemulator.interpreter.ast.expressions;

import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.ast.Expression;
import pl.genschu.bloomooemulator.interpreter.ast.Node;
import pl.genschu.bloomooemulator.interpreter.ast.Statement;
import pl.genschu.bloomooemulator.interpreter.exceptions.BreakException;
import pl.genschu.bloomooemulator.interpreter.exceptions.OneBreakException;

import java.util.List;

public class BlockExpression extends Expression {
    private final List<Node> nodes;
    private Object returnValue;

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
                        returnValue = ((ReturnExpression) node).evaluate(context);
                        context.setReturnValue(returnValue);
                    } else {
                        ((Expression) node).evaluate(context);
                    }
                }
            }
            return returnValue;
        } catch (OneBreakException e) {
            // zwraca nulla i przerywa procedurę, nie propaguje się dalej
            return null;
        } catch (BreakException e) {
            // nic nie zwraca i propaguje się dalej
            throw e;
        }
    }
}
