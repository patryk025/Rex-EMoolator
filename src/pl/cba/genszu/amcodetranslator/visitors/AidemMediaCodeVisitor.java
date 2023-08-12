package pl.cba.genszu.amcodetranslator.visitors; 

import org.antlr.v4.runtime.tree.*;
import pl.cba.genszu.amcodetranslator.antlr.*;
import java.util.*;
import pl.cba.genszu.amcodetranslator.antlr.AidemMediaParser.*;
import pl.cba.genszu.amcodetranslator.interpreter.*;
import pl.cba.genszu.amcodetranslator.interpreter.util.ConditionChecker;
import pl.cba.genszu.amcodetranslator.interpreter.util.ParamHelper;
import pl.cba.genszu.amcodetranslator.interpreter.variabletypes.StructVariable;
import pl.cba.genszu.amcodetranslator.interpreter.factories.*;

public class AidemMediaCodeVisitor extends AidemMediaBaseVisitor<Variable>
{
	public int indent;
	public int fixAttemps = 0;
	private final Interpreter interpreter;

	public AidemMediaCodeVisitor(Interpreter interpreter) {
		this.interpreter = interpreter;
	}
	
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

	@Override
	public Variable visitScript(AidemMediaParser.ScriptContext ctx)
	{
		indent = 0;
		print("We are in script");
		indent++;
		return super.visitScript(ctx);
	}

	@Override
	public Variable visitCodeBlock(AidemMediaParser.CodeBlockContext ctx)
	{
		print("We are in codeBlock");
		indent++;
		super.visitCodeBlock(ctx);
		indent--;
		return null;
	}
	
	@Override 
	public Variable visitFunctionFire(AidemMediaParser.FunctionFireContext ctx)
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
		return null;
	}

	@Override
	public Variable visitLogic(AidemMediaParser.LogicContext ctx)
	{
		//print("Found logic!");
		print("logic: "+ctx.getText());
		return super.visitLogic(ctx);
	}

	@Override
	public Variable visitCompare(AidemMediaParser.CompareContext ctx)
	{
		print("compare: "+ctx.getText()+" ("+compareHelper(ctx.getText())+")");
		return super.visitCompare(ctx);
	} 

	@Override 
	public Variable visitIfInstr(AidemMediaParser.IfInstrContext ctx)
	{ 
		print("Found ifInstr!");
		AidemMediaParser.ConditionSimpleContext conditionsSimple = ctx.conditionSimple();
		AidemMediaParser.ConditionContext conditions = ctx.condition();
		List<String> comparator = new ArrayList<>();
		if(conditionsSimple != null && conditions == null) {
			print("5 param if");
			comparator.add(conditionsSimple.param(0).getText());
			comparator.add(conditionsSimple.compare().getText());
			comparator.add(conditionsSimple.param(1).getText());
		}
		else {
			print("3 param if");
			//comparator.add(
			for(int i = 0; i < conditions.conditionPart().size(); i++) {
				if(i >= 1)
					comparator.add(conditions.logic(i-1).getText());
				ConditionPartContext conditionPart = conditions.conditionPart(i);
				//comparator.add(conditionPart..getText());
				ParamContext param = conditionPart.param();
				if(conditionPart.expression() != null) {
					comparator.add((String) visitExpression(conditionPart.expression()).getValue());
				}
				else if(conditionPart.literal() != null) {
					comparator.add("" + interpreter.getVariable(conditionPart.literal().getText()).getValue()); //trochę druciarskie, no ale muszę się jednak upewnić, że trafia tam String
				}
				else if(conditionPart.functionFire() != null) {
					comparator.add((String) visitFunctionFire(conditionPart.functionFire()).getValue());
				}
				else if(conditionPart.iterator() != null) {
					comparator.add((String) interpreter.getVariable("_I_").getValue());
				}
				else if(conditionPart.struct() != null) {
					String[] structFields = conditionPart.struct().getText().split("\\|");
					comparator.add((String) ((StructVariable) interpreter.getVariable(structFields[0])).GETFIELD(structFields[1]).getValue());
				}
				comparator.add(conditionPart.compare().getText());
				comparator.add(ParamHelper.getValueFromParam(this, param));
			}
		}
		boolean conditionResult = ConditionChecker.checkCondition(comparator);
		print("{");
		indent++;
		print("Condition result: "+conditionResult);
		IfTrueContext trueBranch = ctx.ifTrue();
		IfFalseContext falseBranch = ctx.ifFalse();
		if(conditionResult) 
			super.visitIfTrue(trueBranch);
		else
			super.visitIfFalse(falseBranch);
		indent--;
		print("}");
		return null;
	}

	@Override
	public Variable visitCondition(AidemMediaParser.ConditionContext ctx)
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
	public Variable visitConditionPart(AidemMediaParser.ConditionPartContext ctx)
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

	@Override
	public Variable visitIfTrue(AidemMediaParser.IfTrueContext ctx)
	{
		if(ctx.codeBlock() != null)
			return visitCodeBlock(ctx.codeBlock());
		else if(ctx.string() != null) {
			//TODO: behavioury do zrobienia, ale to zaraz po funkcjach, bo inaczej nie ruszę ;)
			Variable var = VariableFactory.createVariable("BEHAVIOUR", ctx.string().getText(), null); //taki tam placeholder
			return var;
		}
		return super.visitIfTrue(ctx);
	}

	@Override
	public Variable visitIfFalse(AidemMediaParser.IfFalseContext ctx)
	{
		if(ctx.codeBlock() != null)
			return visitCodeBlock(ctx.codeBlock());
		else if(ctx.string() != null) {
			Variable var = VariableFactory.createVariable("BEHAVIOUR", ctx.string().getText(), null);
			return var;
		}
		return super.visitIfFalse(ctx);
	}

	@Override
	public Variable visitExpression(AidemMediaParser.ExpressionContext ctx)
	{
		print("We are in expression");
		print("{");
		indent++;
		//visitChildren(ctx);
		List<String> expression_parts = new ArrayList<>();
		for(int i = 1; i < ctx.getChildCount()-1; i++) {
			ParseTree child = ctx.getChild(i);
			print("part " + (i - 1) + " -> " + child.getText());
			expression_parts.add(child.getText());
		}
		Variable result = interpreter.calcArithmetic(expression_parts);
		print("DEBUG result: "+result.getValue());
		indent--;
		print("}");
		return result;
	}
}
