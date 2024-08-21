package pl.genschu.bloomooemulator.interpreter.ast.statements;

import com.badlogic.gdx.Gdx;
import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.ast.Expression;
import pl.genschu.bloomooemulator.interpreter.ast.Statement;
import pl.genschu.bloomooemulator.interpreter.ast.expressions.ConstantExpression;
import pl.genschu.bloomooemulator.interpreter.exceptions.BreakException;
import pl.genschu.bloomooemulator.interpreter.factories.VariableFactory;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.interpreter.variable.types.BehaviourVariable;
import pl.genschu.bloomooemulator.interpreter.variable.types.BoolVariable;

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
        BoolVariable compareResult = (BoolVariable) ((ConstantExpression) condition.evaluate(context)).evaluate(context);
        if (compareResult.GET()) {
            result = trueBranch.evaluate(context);
        } else if (falseBranch != null) {
            result = falseBranch.evaluate(context);
        }
        else {
            result = null;
        }

        if(result != null) {
            if(result instanceof BehaviourVariable) {
                BehaviourVariable variable = (BehaviourVariable) result;
                try {
                    variable.getMethod("RUN", Collections.singletonList("mixed")).execute(null, variable);
                } catch(BreakException e) {
                    Gdx.app.log("IfStatement", "BREAK instruction in "+variable.getName()+" BEHAVIOUR");
                    throw e;
                }
                catch (Exception e) {
                    Gdx.app.error("IfStatement", "Error while running "+variable.getName()+" BEHAVIOUR in if instruction: " + e.getMessage(), e);
                }
            }
        }
    }
}
