package pl.cba.genszu.amcodetranslator.interpreter.ast.statements;

import pl.cba.genszu.amcodetranslator.interpreter.Context;
import pl.cba.genszu.amcodetranslator.interpreter.ast.Expression;
import pl.cba.genszu.amcodetranslator.interpreter.ast.Statement;

public class IfStatement extends Statement {
    private final Expression condition;
    private final Statement trueBranch;
    private final Statement falseBranch;

    public IfStatement(Expression condition, Statement trueBranch, Statement falseBranch) {
        this.condition = condition;
        this.trueBranch = trueBranch;
        this.falseBranch = falseBranch;
    }

    @Override
    public void execute(Context context) {
        if ((int) condition.evaluate(context) != 0) {
            trueBranch.execute(context);
        } else if (falseBranch != null) {
            falseBranch.execute(context);
        }
    }
}
