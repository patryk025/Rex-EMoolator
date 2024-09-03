package pl.genschu.bloomooemulator.interpreter.ast.statements;

import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.interpreter.ast.Expression;
import pl.genschu.bloomooemulator.interpreter.ast.Statement;
import pl.genschu.bloomooemulator.interpreter.factories.VariableFactory;
import pl.genschu.bloomooemulator.interpreter.ast.expressions.ConstantExpression;

public class VariableDefinitionStatement extends Statement {
    private final String type;
    private final String variableName;
    private final Expression expression;

    public VariableDefinitionStatement(String type, String variableName, Expression expression) {
        this.type = type;
        if(variableName.startsWith("\"") && variableName.endsWith("\"")) {
            this.variableName = variableName.substring(1, variableName.length() - 1);
        }
        else {
            this.variableName = variableName;
        }
        this.expression = expression;
    }

    @Override
    public void execute(Context context) {
        Object value = expression.evaluate(context);
      	if(value instanceof ConstantExpression) {
            value = ((Expression) value).evaluate(context);
        }

        String valueString = value.toString(); // expression returns Variable, not String expected by createVariable

        Variable variable = VariableFactory.createVariable(type, variableName, valueString, context);
        context.setVariable(variableName, variable);
    }
}
