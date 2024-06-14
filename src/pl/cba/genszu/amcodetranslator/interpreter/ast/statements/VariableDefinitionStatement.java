package pl.cba.genszu.amcodetranslator.interpreter.ast.statements;

import pl.cba.genszu.amcodetranslator.interpreter.Context;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Variable;
import pl.cba.genszu.amcodetranslator.interpreter.ast.Expression;
import pl.cba.genszu.amcodetranslator.interpreter.ast.Statement;
import pl.cba.genszu.amcodetranslator.interpreter.factories.VariableFactory;

public class VariableDefinitionStatement extends Statement {
    private final String type;
    private final String variableName;
    private final Expression expression;

    public VariableDefinitionStatement(String type, String variableName, Expression expression) {
        this.type = type;
        this.variableName = variableName;
        this.expression = expression;
    }

    @Override
    public void execute(Context context) {
        Object value = expression.evaluate(context);
        Variable variable = VariableFactory.createVariable(type, variableName, value, context);
        context.setVariable(variableName, variable);
    }
}
