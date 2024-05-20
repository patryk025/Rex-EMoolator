package pl.cba.genszu.amcodetranslator.interpreter.ast.statements;

import pl.cba.genszu.amcodetranslator.interpreter.Context;
import pl.cba.genszu.amcodetranslator.interpreter.ast.Statement;
import pl.cba.genszu.amcodetranslator.interpreter.ast.Expression;

public class LoopStatement extends Statement {
    private final Statement body;
    private final Expression start;
    private final Expression end;
    private final Expression step;

    public LoopStatement(Expression start, Expression end, Expression step, Statement body) {
        this.start = start;
        this.end = end;
        this.step = step;
        this.body = body;
    }

    @Override
    public void execute(Context context) {
        for (int i = (int) start.evaluate(context); i < (int) end.evaluate(context); i += (int) step.evaluate(context)) {
            body.execute(context);
        }
    }
}
