package pl.genschu.bloomooemulator.interpreter.v1.ast.statements;

import com.badlogic.gdx.Gdx;
import pl.genschu.bloomooemulator.interpreter.v1.Context;
import pl.genschu.bloomooemulator.interpreter.v1.ast.Expression;
import pl.genschu.bloomooemulator.interpreter.v1.ast.Statement;
import pl.genschu.bloomooemulator.interpreter.v1.ast.expressions.ConstantExpression;
import pl.genschu.bloomooemulator.interpreter.v1.exceptions.BreakException;
import pl.genschu.bloomooemulator.interpreter.v1.variable.types.BehaviourVariable;
import pl.genschu.bloomooemulator.interpreter.v1.variable.types.BoolVariable;

import java.util.Collections;

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
                    variable.getMethod("RUN", Collections.singletonList("mixed")).execute(variable, null);
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
