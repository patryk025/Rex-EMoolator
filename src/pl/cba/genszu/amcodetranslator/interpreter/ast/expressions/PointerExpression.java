package pl.cba.genszu.amcodetranslator.interpreter.ast.expressions;

import pl.cba.genszu.amcodetranslator.interpreter.Context;
import pl.cba.genszu.amcodetranslator.interpreter.Variable;
import pl.cba.genszu.amcodetranslator.interpreter.ast.Expression;

public class PointerExpression extends Expression {
    private final Expression expression;

    public PointerExpression(Expression expression) {
        this.expression = expression;
    }

    @Override
    public Object evaluate() {
        return null;
    }

    @Override
    public Object evaluate(Context context) {
        Object result = expression.evaluate(context);
        if (!(result instanceof String)) {
            throw new RuntimeException("Dereferenced value must be a string: " + result);
        }
        String targetVariableName = (String) result;
        Variable targetVariable = context.getVariable(targetVariableName);
        if (targetVariable == null) {
            throw new RuntimeException("Variable not defined: " + targetVariableName);
        }
        return targetVariable.getValue();
    }
}

