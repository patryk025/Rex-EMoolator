package pl.genschu.bloomooemulator.interpreter.ast.expressions;

import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.interpreter.ast.Expression;

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
