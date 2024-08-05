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
            result = ((ConstantExpression) result).evaluate(context).toString();
        }
        if (!(result instanceof String)) {
            throw new RuntimeException("Dereferenced value must be a string: " + result);
        }
        Variable targetVariableNameValue = context.getVariable((String) result);
        String targetVariableName = targetVariableNameValue.getValue().toString();
        Variable targetVariable = context.getVariable(targetVariableName);
        if (targetVariable == null) {
            throw new RuntimeException("Variable not defined: " + targetVariableName);
        }
        return targetVariableName;
    }
}

