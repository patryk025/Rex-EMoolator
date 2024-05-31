package pl.cba.genszu.amcodetranslator.interpreter.ast.expressions;

import pl.cba.genszu.amcodetranslator.interpreter.Context;
import pl.cba.genszu.amcodetranslator.interpreter.Variable;
import pl.cba.genszu.amcodetranslator.interpreter.ast.Expression;

public class VariableExpression extends Expression {
    private final String variableName;

    public VariableExpression(String variableName) {
        this.variableName = variableName;
    }

    @Override
    public Object evaluate(Context context) {
        Variable variable = context.getVariable(variableName);
        if (variable == null) {
            throw new RuntimeException("Variable not defined: " + variableName);
        }
        return variable;
    }
}
