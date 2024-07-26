package pl.genschu.bloomooemulator.interpreter.ast.statements;

import com.badlogic.gdx.Gdx;
import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.ast.Expression;
import pl.genschu.bloomooemulator.interpreter.ast.Statement;
import pl.genschu.bloomooemulator.interpreter.ast.expressions.ConstantExpression;
import pl.genschu.bloomooemulator.interpreter.factories.VariableFactory;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.interpreter.variable.types.BehaviourVariable;

import java.util.Collections;

import static pl.genschu.bloomooemulator.interpreter.util.VariableHelper.getVariableFromObject;

public class IfStatement extends Statement {
    private final Expression condition;
    private final Expression trueBranch;
    private final Expression falseBranch;

    public IfStatement(Expression condition, Expression trueBranch, Expression falseBranch) {
        this.condition = condition;
        this.trueBranch = trueBranch;
        this.falseBranch = falseBranch;
    }

    @Override
    public void execute(Context context) {
        Object result;
        if ((Boolean) (((ConstantExpression) condition.evaluate(context)).evaluate(context))) {
            result = trueBranch.evaluate(context);
        } else if (falseBranch != null) {
            result = falseBranch.evaluate(context);
        }
        else {
            result = null;
        }

        Variable variable = getVariableFromObject(result, context);

        if(variable != null) {
            if(variable instanceof BehaviourVariable) {
                try {
                    variable.getMethod("RUN", Collections.singletonList("mixed")).execute(null);
                } catch (Exception e) {
                    Gdx.app.error("Game", "Error while running "+variable.getName()+" BEHAVIOUR in if instruction: " + e.getMessage());
                }
            }
        }
    }
}
