package pl.cba.genszu.amcodetranslator.interpreter.ast.statements;

import pl.cba.genszu.amcodetranslator.interpreter.Context;
import pl.cba.genszu.amcodetranslator.interpreter.ast.Expression;
import pl.cba.genszu.amcodetranslator.interpreter.ast.Statement;
import pl.cba.genszu.amcodetranslator.interpreter.ast.expressions.ConstantExpression;
import pl.cba.genszu.amcodetranslator.interpreter.variable.types.IntegerVariable;

public class WhileStatement extends Statement {
    private final Expression condition;
    private final Expression code;

    public WhileStatement(Expression condition, Expression code) {
        this.condition = condition;
        this.code = code;
    }

    @Override
    public void execute(Context context) {
        while ((Boolean) (((ConstantExpression) condition.evaluate(context)).evaluate(null))) {
            Object result = code.evaluate(context);
            if (result instanceof OneBreakStatement) {
                break;
            }
        }
    }
}
