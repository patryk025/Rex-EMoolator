package pl.cba.genszu.amcodetranslator.interpreter.ast.statements;

import pl.cba.genszu.amcodetranslator.interpreter.Context;
import pl.cba.genszu.amcodetranslator.interpreter.ast.Expression;
import pl.cba.genszu.amcodetranslator.interpreter.ast.Statement;
import pl.cba.genszu.amcodetranslator.interpreter.ast.expressions.ConstantExpression;

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
        if ((Boolean) (((ConstantExpression) condition.evaluate(context)).evaluate(null))) {
            trueBranch.evaluate(context);
        } else if (falseBranch != null) {
            falseBranch.evaluate(context);
        }
    }
}
