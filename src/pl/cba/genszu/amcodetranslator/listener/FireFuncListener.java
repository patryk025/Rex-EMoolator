package pl.cba.genszu.amcodetranslator.listener;
import pl.cba.genszu.amcodetranslator.antlr.*;
import org.antlr.v4.runtime.tree.*;
import pl.cba.genszu.amcodetranslator.antlr.AidemMediaParser.*;

public class FireFuncListener
extends AidemMediaBaseListener
implements ParseTreeListener
{
	@Override
	public void enterFunctionFire(AidemMediaParser.FunctionFireContext ctx)
	{
		System.out.println("Found function fire:");
		//System.out.println("Number of params: "+(ctx.getChildCount()-5));
	}

	@Override
	public void enterParam(AidemMediaParser.ParamContext ctx)
	{
		System.out.println("PARAM: "+ctx.getText());
	}

	@Override
	public void enterLiteral(AidemMediaParser.LiteralContext ctx)
	{
		System.out.println("LITERAL: "+ctx.getText());
	}

	@Override
	public void exitFunctionFire(AidemMediaParser.FunctionFireContext ctx)
	{
		System.out.println("End of function fire");
		System.out.println();
	}
	
}
