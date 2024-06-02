package pl.cba.genszu.amcodetranslator.interpreter.ast.statements;

import pl.cba.genszu.amcodetranslator.interpreter.Context;
import pl.cba.genszu.amcodetranslator.interpreter.ast.Statement;
import pl.cba.genszu.amcodetranslator.interpreter.ast.Expression;
import pl.cba.genszu.amcodetranslator.interpreter.ast.expressions.*;
import pl.cba.genszu.amcodetranslator.interpreter.variable.types.*;

public class LoopStatement extends Statement {
    private final Expression body;
    private final Expression start;
    private final Expression end;
    private final Expression step;

    public LoopStatement(Expression start, Expression end, Expression step, Expression body) {
        this.start = start;
        this.end = end;
        this.step = step;
        this.body = body;
    }

    @Override
    public void execute(Context context) {
		int startValue = (int) start.evaluate(context);
		int endValue = (int) end.evaluate(context);
		int stepValue = (int) step.evaluate(context);
		context.setVariable("_I_", new IntegerVariable("_I_", startValue));
        for (int i = startValue; i < endValue; i += stepValue) {
            Object result = body.evaluate(context);
			if(result instanceof OneBreakStatement) {
				break;
			}
			((IntegerVariable) context.getVariable("_I_")).ADD(stepValue);
        }
    }
}
