package pl.cba.genszu.amcodetranslator.interpreter.ast.expressions;

import pl.cba.genszu.amcodetranslator.interpreter.Context;
import pl.cba.genszu.amcodetranslator.interpreter.ast.Expression;
import pl.cba.genszu.amcodetranslator.interpreter.ast.Node;
import pl.cba.genszu.amcodetranslator.interpreter.ast.Statement;

import java.util.List;

public class BlockExpression extends Expression {
    private final List<Node> nodes;

    public BlockExpression(List<Node> nodes) {
        this.nodes = nodes;
    }

    @Override
    public Object evaluate(Context context) {
        for (Node node : nodes) {
            if(node instanceof Statement)
                ((Statement) node).execute(context);
            else {
				if(node instanceof ReturnExpression) {
					// prawdopodobnie z bomby zwraca wartość, do sprawdzenia, czy jednak nie zapisuje wartości do zwrócenia
					return ((ReturnExpression) node).evaluate(context);
				}
				else {
					((Expression) node).evaluate(context);
				}
			}
        }
		
		return null;
    }
}
