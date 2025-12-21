package pl.genschu.bloomooemulator.interpreter.ast.expressions;

import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.interpreter.ast.Expression;

public class PointerExpression extends Expression {
    private final Expression expression;

    public PointerExpression(Expression expression) {
        this.expression = expression;
    }

    @Override
    public Object evaluate(Context context) {
        Object result = expression.evaluate(context);
        if (result instanceof ConstantExpression) {
            result = ((ConstantExpression) result).evaluate(context);
        }
        if (result instanceof Variable) {
            result = ((Variable) result).getValue();
        }
        if (!(result instanceof String)) {
            throw new RuntimeException("Dereferenced value must be a string: " + result);
        }
        return result;
    }
}

