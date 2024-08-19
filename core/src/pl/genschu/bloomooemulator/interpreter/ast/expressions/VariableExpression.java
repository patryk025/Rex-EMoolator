package pl.genschu.bloomooemulator.interpreter.ast.expressions;

import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.interpreter.ast.Expression;
import pl.genschu.bloomooemulator.interpreter.variable.types.StringVariable;

public class VariableExpression extends Expression {
    private final String variableName;
    private final Expression constantExpression;

    public VariableExpression(String variableName) {
        this.variableName = variableName;
        this.constantExpression = null;
    }

    public VariableExpression(Expression constantExpression) {
        this.constantExpression = constantExpression;
        this.variableName = null;
    }

    @Override
    public Object evaluate(Context context) {
        String variableName;
        if(constantExpression != null) {
            variableName = constantExpression.evaluate(context).toString();
        }
        else if(this.variableName != null) {
            variableName = this.variableName;
        }
        else {
            throw new RuntimeException("Variable name not defined");
        }
        Variable variable = context.getVariable(variableName);
        if (variable == null) {
            return new StringVariable("", variableName, context);
        }
        return variable;
    }
}
