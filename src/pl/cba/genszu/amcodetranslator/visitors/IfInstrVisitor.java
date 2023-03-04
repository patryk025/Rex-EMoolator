package pl.cba.genszu.amcodetranslator.visitors; 

import org.antlr.v4.runtime.tree.*;
import pl.cba.genszu.amcodetranslator.antlr.*; 

public class IfInstrVisitor extends AidemMediaBaseVisitor<Void>
{ 

	@Override
	public Void visitChildren(RuleNode node)
	{
		System.out.println("Fire of visitChildren with node "+node.getRuleContext().getText()+", type: "+node.getRuleContext().getClass().getSimpleName());
		return super.visitChildren(node);
	}

	@Override
	public Void visitScript(AidemMediaParser.ScriptContext ctx)
	{
		System.out.println("We are in script");
		return super.visitScript(ctx);
	}

	@Override
	public Void visitCodeBlock(AidemMediaParser.CodeBlockContext ctx)
	{
		System.out.println("We are in codeBlock");
		return super.visitCodeBlock(ctx);
	}

	@Override 
	public Void visitFunctionFire(AidemMediaParser.FunctionFireContext ctx)
	{ 
		System.out.println("Found functionFire!"); 
		return super.visitFunctionFire(ctx); 
	} 

	@Override 
	public Void visitIfInstr(AidemMediaParser.IfInstrContext ctx)
	{ 
		System.out.println("Found ifInstr!"); 
		for (AidemMediaParser.ParamContext param : ctx.param())
		{ 
			String type = ""; 
			if (param.string() != null)
			{ 
				if (param.string().functionFire() != null)
				{ 
					if (!param.string().functionFire().isEmpty())
					{ 
						type = "string, functionFire"; 
						for (AidemMediaParser.FunctionFireContext ctx_functionFire : param.string().functionFire())
						{ 
							visitFunctionFire(ctx_functionFire); 
						} 
					} 
					else 
						type = "string"; 
				} 
				else
				{ 
					type = "string"; 
				} 
			}
			else if (param.number() != null)
			{ 
				type = "number"; 
			}
			else if (param.functionFire() != null)
			{ 
				type = "functionFire"; 
				visitFunctionFire(param.string().functionFire().get(0)); 
			}
			else if (param.literal() != null)
			{ 
				type = "literal"; 
			} 
			String value = param.getText(); 
			System.out.println("Param: " + value + ", type: " + type); 
		} 
		return super.visitIfInstr(ctx); 
	} 
}
