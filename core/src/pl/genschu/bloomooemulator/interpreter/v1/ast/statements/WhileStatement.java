package pl.genschu.bloomooemulator.interpreter.v1.ast.statements;

import pl.genschu.bloomooemulator.interpreter.v1.Context;
import pl.genschu.bloomooemulator.interpreter.v1.ast.Expression;
import pl.genschu.bloomooemulator.interpreter.v1.ast.Statement;
import pl.genschu.bloomooemulator.interpreter.v1.ast.expressions.ConstantExpression;
import pl.genschu.bloomooemulator.interpreter.v1.variable.types.BoolVariable;

public class WhileStatement extends Statement {
    private final Expression condition;
    private final Expression code;

    public WhileStatement(Expression condition, Expression code) {
        this.condition = condition;
        this.code = code;
    }

    @Override
    public void execute(Context context) {
        while (((BoolVariable) (((ConstantExpression) condition.evaluate(context)).evaluate(null))).GET()) {
            Object result = code.evaluate(context);
            if (result instanceof OneBreakStatement) {
                break;
            }
        }
    }
}
