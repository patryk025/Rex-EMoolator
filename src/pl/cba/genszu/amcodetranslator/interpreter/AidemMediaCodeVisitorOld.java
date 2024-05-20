package pl.cba.genszu.amcodetranslator.interpreter;

import org.antlr.v4.runtime.tree.*;

import java.util.*;

import pl.cba.genszu.amcodetranslator.interpreter.antlr.AidemMediaBaseVisitor;
import pl.cba.genszu.amcodetranslator.interpreter.antlr.AidemMediaParser;
import pl.cba.genszu.amcodetranslator.interpreter.antlr.AidemMediaParser.*;
import pl.cba.genszu.amcodetranslator.interpreter.exceptions.InterpreterException;
import pl.cba.genszu.amcodetranslator.interpreter.exceptions.VariableNotFoundException;
import pl.cba.genszu.amcodetranslator.interpreter.exceptions.VariableUnsupportedOperationException;
import pl.cba.genszu.amcodetranslator.interpreter.arithmetic.ArithmeticSolver;
import pl.cba.genszu.amcodetranslator.interpreter.util.ConditionChecker;
import pl.cba.genszu.amcodetranslator.interpreter.util.ParamHelper;
import pl.cba.genszu.amcodetranslator.interpreter.types.StructVariable;
import pl.cba.genszu.amcodetranslator.interpreter.factories.*;
import pl.cba.genszu.amcodetranslator.interpreter.types.*;
import pl.cba.genszu.amcodetranslator.interpreter.exceptions.*;

public class AidemMediaCodeVisitorOld extends AidemMediaBaseVisitor<Variable>
{
	public int indent;
	public int fixAttemps = 0;
	private final InterpreterOld interpreter;

	public AidemMediaCodeVisitorOld(InterpreterOld interpreter) {
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
		Variable var = interpreter.getVariable(literals.get(0).getText());
		Variable paramsObj[] = new Variable[params.size()];
		int idx = 0;
		for(ParamContext paramCtx : params) {
			if(paramCtx.expression() != null) {
				paramsObj[idx++] = visitExpression(paramCtx.expression());
			}
			else if(paramCtx.literal() != null) {
				paramsObj[idx++] = interpreter.getVariable(paramCtx.literal().getText());
			}
			else if(paramCtx.functionFire() != null) {
				paramsObj[idx++] = visitFunctionFire(paramCtx.functionFire());
			}
			else if(paramCtx.iterator() != null) {
				paramsObj[idx++] = interpreter.getVariable("_I_");
			}
			else if(paramCtx.struct() != null) {
				String[] structFields = paramCtx.struct().getText().split("\\|");
				paramsObj[idx++] = ((StructVariable) interpreter.getVariable(structFields[0])).GETFIELD(structFields[1]);
			}
		}
		try {
			var.fireFunction(literals.get(1).getText(), paramsObj);
		}
		catch(ClassMethodNotFoundException e) {
			print("Error: "+e.getMessage());
		}
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
				comparator.add(""+ParamHelper.getValueFromParam(this, param));
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
			if(child instanceof AidemMediaParser.StringRefContext) {
				Variable resolvedStringRef = visitStringRef((StringRefContext) child);
				expression_parts.add((String) resolvedStringRef.getValue());
				print("part " + (i - 1) + " -> " + child.getText() + " => " + resolvedStringRef.getValue());
			}
			else {
				print("part " + (i - 1) + " -> " + child.getText());
				expression_parts.add(child.getText());
			}
		}
		Variable result = interpreter.calcArithmetic(expression_parts);
		print("DEBUG result: "+result.getValue());
		indent--;
		print("}");
		return result;
	}

	@Override
	public Variable visitLoopInstr(AidemMediaParser.LoopInstrContext ctx) {
		LoopCodeParamContext loopFunction = ctx.loopCodeParam();
		//System.out.println(ctx.param());
		Variable startValue = VariableFactory.createVariable(null, ParamHelper.getValueFromParam(this, ctx.param(0)));
		Variable diffValue = VariableFactory.createVariable(null, ParamHelper.getValueFromParam(this, ctx.param(1)));
		Variable endValue = ArithmeticSolver.add(startValue, diffValue);
		Variable incrementValue = VariableFactory.createVariable(null, ParamHelper.getValueFromParam(this, ctx.param(2)));
		
		//if(startValue.getType().equals("STRING"))

		if(startValue.getType().equals("STRING")) {
			throw new InterpreterException("Start value cannot be a string");
		}
		if(diffValue.getType().equals("STRING")) {
			throw new InterpreterException("Diff value cannot be a string");
		}
		if(incrementValue.getType().equals("STRING")) {
			throw new InterpreterException("Increment value cannot be a string");
		}

		Variable currentValue = interpreter.createVariable("_I_", null, startValue.getValue());

		boolean isCode = loopFunction.codeBlock() != null;
		boolean isLiteral = loopFunction.literal() != null;

		boolean doLoop = ConditionChecker.check(new ArrayList<>(Arrays.asList("" + currentValue.getValue(), "<", "" + endValue.getValue())));

		double incPrimitive = 1;
		if(incrementValue instanceof DoubleVariable || incrementValue instanceof IntegerVariable) {
			try {
				incPrimitive = ((double) incrementValue.getValue());
			}
			catch(ClassCastException e) {
				incPrimitive = ((int) incrementValue.getValue())*1.0;
			}
		}
		
		while(doLoop) {
			if(isCode) {
				visitCodeBlock(loopFunction.codeBlock());
			}
			else if(isLiteral) {
				Variable tmp = interpreter.getVariable(loopFunction.literal().getText());
				if(tmp != null) {
					//TODO: obsługa BEHAVIOUR
					if(tmp.getType().equals("BEHAVIOUR")) {
						throw new InterpreterException("BEHAVIOUR are not supported yet");
					}
					else {
						throw new InterpreterException(String.format("%s type is not supported in @LOOP", tmp.getType()));
					}
				}
				else {
					throw new VariableNotFoundException(loopFunction.literal().getText());
				}
			}
			else {
				throw new InterpreterException("Strings are not supported");
			}
			if(currentValue instanceof IntegerVariable) {
				((IntegerVariable) currentValue).ADD((int) incPrimitive);
			}
			else if(incrementValue instanceof DoubleVariable) {
				((DoubleVariable) currentValue).ADD(incPrimitive);
			}
			//currentValue = ArithmeticSolver.add(currentValue, incrementValue);
			doLoop = ConditionChecker.check(new ArrayList<>(Arrays.asList("" + currentValue.getValue(), "<", "" + endValue.getValue())));
		}
		return null;
	}

	@Override
	public Variable visitWhileInstr(WhileInstrContext ctx) {
		// (STRING, STRING, STRING) condition, STRING behaviour
		String condition = ctx.param(0).getText() + ctx.compare().getText() + ctx.param(1);
		boolean doLoop = ConditionChecker.check(new ArrayList<>(List.of(condition)));
		
		Variable behaviour = ctx.string() != null ? this.interpreter.getVariable(ctx.string().getText()) : null;

		while(doLoop) {
			if(ctx.codeBlock() != null) {
				visitCodeBlock(ctx.codeBlock());
			}
			else {
				// obsługa Behaviour
				
			}
		}
		return null;
	}

	@Override
	public Variable visitInstr(InstrContext ctx) {
		String instructionName = ctx.literal().getText();
		List<ParamContext> params = ctx.param();

		switch(instructionName) {
			case "CONTINUE":
			case "BREAK":
			case "ONEBREAK":
				return VariableFactory.createVariable("OPCODE", instructionName, null);
			case "GETAPPLICATIONNAME":
				// TODO: podejrzeć jak to PikLib zwraca
				return VariableFactory.createVariable("STRING", "<no value>", null);
			case "GETCURRENTSCENE":
				return VariableFactory.createVariable("STRING", this.interpreter.getSceneName(), null);
			case "RETURN":
				print("Returning variable "+params.get(0).getText()+", debug value: "+interpreter.getVariable(params.get(0).getText()).getValue());
				return VariableFactory.createVariable("OPCODE", "RETURN|"+params.get(0), null);
			case "BOOL":
			case "STRING":
			case "DOUBLE":
			case "INT":
				String varName = ctx.param(0).getText();
				ParamContext param2 = ctx.param(1);
				String value = "";
				if(param2.expression() != null) {
					value = (String) visitExpression(param2.expression()).getValue();
				}
				else if(param2.literal() != null) {
					value = "" + interpreter.getVariable(param2.literal().getText()).getValue();
				}
				else if(param2.functionFire() != null) {
					value = (String) visitFunctionFire(param2.functionFire()).getValue();
				}
				else if(param2.iterator() != null) {
					value = (String) interpreter.getVariable("_I_").getValue();
				}
				else if(param2.struct() != null) {
					String[] structFields = param2.struct().getText().split("\\|");
					value = (String) ((StructVariable) interpreter.getVariable(structFields[0])).GETFIELD(structFields[1]).getValue();
				}
				return interpreter.createVariable(varName, instructionName.equals("INT") ? "INTEGER" : instructionName, value);
			case "CONV":
				String variableName = ctx.param(0).getText();
				String variableType = ctx.param(1).string().literal(0).getText();
				Variable variable = interpreter.getVariable(variableName);
				if(variable == null) {
					throw new VariableNotFoundException(variableName);
				}
				try {
					variable = variable.convertTo(variableType);
					interpreter.setVariable(variableName, variable);
					System.out.println("Converted "+variable.getName()+" to "+variable.getType());
					System.out.println("Debug: "+variable.getValue());
					return variable;
				}
				catch(VariableUnsupportedOperationException e) {
					System.out.println("Błąd castowania zmiennych: "+e.getMessage());
				}
			default:
				throw new InterpreterException(String.format("Instrukcja @%s nie jest jeszcze obsługiwana", instructionName));
		}
	}

	@Override
	public Variable visitStringRef(StringRefContext ctx) {
		if(ctx.literal() != null) {
			return interpreter.getVariable(ctx.literal().getText());
		}
		else {
			Variable result = visitExpression(ctx.expression());
			String value = (String) result.getValue();
			return interpreter.getVariable(value);
		}
	}
}
