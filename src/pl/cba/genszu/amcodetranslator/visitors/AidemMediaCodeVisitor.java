package pl.cba.genszu.amcodetranslator.visitors; 

import org.antlr.v4.runtime.tree.*;
import pl.cba.genszu.amcodetranslator.antlr.*;
import java.util.*;
import pl.cba.genszu.amcodetranslator.antlr.AidemMediaParser.*;
import pl.cba.genszu.amcodetranslator.interpreter.*; 

public class AidemMediaCodeVisitor extends AidemMediaBaseVisitor<Void>
{
	public int indent;
	public int fixAttemps = 0;
	
	private void print(String text) {
		for(int i = 0; i < indent; i++) {
			System.out.print("    ");
		}
		System.out.println(text);
	}

	/*@Override
	public Void visitChildren(RuleNode node)
	{
		System.out.println("Fire of visitChildren with node "+node.getRuleContext().getText()+", type: "+node.getRuleContext().getClass().getSimpleName());
		return super.visitChildren(node);
	}*/
	
	private String compareHelper(String comparator) {
		if(comparator.equals("'") || comparator.equals("_"))
			return "==";
		if(comparator.equals("!'") || comparator.equals("!_"))
			return "!=";
		return comparator.replace("'", "=").replace("_", "=");
	}
	
	private Stack<String> operationsStack = new Stack<>();
	private Interpreter interpreter;
	
	

	@Override
	public Void visitScript(AidemMediaParser.ScriptContext ctx)
	{
		indent = 0;
		print("We are in script");
		indent++;
		return super.visitScript(ctx);
	}

	@Override
	public Void visitCodeBlock(AidemMediaParser.CodeBlockContext ctx)
	{
		print("We are in codeBlock");
		indent++;
		return super.visitCodeBlock(ctx);
	}
	
	@Override 
	public Void visitFunctionFire(AidemMediaParser.FunctionFireContext ctx)
	{ 
		print("Found functionFire!"); 
		print("{");
		indent++;
		List<AidemMediaParser.LiteralContext> literals = ctx.literal();
		List<AidemMediaParser.ParamContext> params = ctx.param();
		print("Fire of function " + literals.get(1).getText() + " for object " + literals.get(0).getText());
		print("No of params: "+params.size());
		visitChildren(ctx);
		indent--;
		print("}");
		//return super.visitFunctionFire(ctx); 
		return null;
	}

	@Override
	public Void visitParam(AidemMediaParser.ParamContext ctx)
	{
		String type = ""; 
		if (ctx.string() != null)
		{ 
			if (ctx.string().functionFire() != null)
			{ 
				if (!ctx.string().functionFire().isEmpty())
				{ 
					type = "string, functionFire"; 
					for (AidemMediaParser.FunctionFireContext ctx_functionFire : ctx.string().functionFire())
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
		else if (ctx.number() != null)
		{ 
			type = "number"; 
		}
		else if (ctx.functionFire() != null)
		{ 
			type = "functionFire"; 
			visitFunctionFire(ctx.string().functionFire().get(0)); 
		}
		else if (ctx.literal() != null)
		{ 
			type = "literal"; 
		} 
		String value = ctx.getText(); 
		print("Param: " + value + ", type: " + type);
		return super.visitParam(ctx);
	}

	@Override
	public Void visitLogic(AidemMediaParser.LogicContext ctx)
	{
		//print("Found logic!");
		print("logic: "+ctx.getText());
		return super.visitLogic(ctx);
	}

	@Override
	public Void visitCompare(AidemMediaParser.CompareContext ctx)
	{
		print("compare: "+ctx.getText()+" ("+compareHelper(ctx.getText())+")");
		return super.visitCompare(ctx);
	} 

	@Override 
	public Void visitIfInstr(AidemMediaParser.IfInstrContext ctx)
	{ 
		print("Found ifInstr!"); 
		List<AidemMediaParser.ConditionPartContext> conditions = ctx.condition().conditionPart();
		print((conditions.size() > 1 ? "multi" : "simple") + " condition");
		print("{");
		indent++;
		
		visitChildren(ctx);
		//ctx.getChildCount();
		/*ParseTree trueBranch = ctx.getChild(1);
		ParseTree falseBranch = ctx.getChild(2);*/
		indent--;
		print("}");
		//return super.visitIfInstr(ctx); 
		return null;
	}

	@Override
	public Void visitCondition(AidemMediaParser.ConditionContext ctx)
	{
		print("Found condition!");
		print("{");
		indent++;
		visitChildren(ctx);
		indent--;
		print("}");
		//return super.visitCondition(ctx);
		return null;
	}

	@Override
	public Void visitConditionPart(AidemMediaParser.ConditionPartContext ctx)
	{
		print("Found condition part!");
		print("{");
		indent++;
		visitChildren(ctx); //TODO: zgubiłem logic w condition part
		indent--;
		print("}");
		//return super.visitConditionPart(ctx);
		return null;
	} 
}
