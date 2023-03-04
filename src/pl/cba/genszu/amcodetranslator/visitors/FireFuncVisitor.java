package pl.cba.genszu.amcodetranslator.visitors;

import pl.cba.genszu.amcodetranslator.antlr.*;
import pl.cba.genszu.amcodetranslator.antlr.AidemMediaParser.*;

public class FireFuncVisitor extends AidemMediaBaseVisitor<Integer> implements AidemMediaVisitor<Integer>
{

	@Override
	public Integer visitFunctionFire(AidemMediaParser.FunctionFireContext ctx)
	{
		// TODO: Implement this method
		return super.visitFunctionFire(ctx);
	}

    protected Integer aggregateResult(Integer aggregate, Integer nextResult) {
        return Integer.sum(aggregate, nextResult);
    }

    protected Integer defaultResult() {
        return 0;
    }
}
