package pl.genschu.bloomooemulator.interpreter.v1.ast.statements;

import com.badlogic.gdx.Gdx;
import pl.genschu.bloomooemulator.interpreter.v1.Context;
import pl.genschu.bloomooemulator.interpreter.v1.variable.Variable;
import pl.genschu.bloomooemulator.interpreter.v1.ast.Expression;
import pl.genschu.bloomooemulator.interpreter.v1.ast.Statement;
import pl.genschu.bloomooemulator.interpreter.factories.LegacyVariableFactory;
import pl.genschu.bloomooemulator.interpreter.v1.ast.expressions.ConstantExpression;

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

        if(context.hasVariable(variableName)) {
            Gdx.app.log("VariableDefinitionStatement", "Variable " + variableName + " already exists in this context, skipping");
            return;
        }
        Variable variable = LegacyVariableFactory.createVariable(type, variableName, valueString, context);
        context.setVariable(variableName, variable);
    }
}
