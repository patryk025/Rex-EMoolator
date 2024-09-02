package pl.genschu.bloomooemulator.interpreter.variable.types;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import pl.genschu.bloomooemulator.interpreter.exceptions.ClassMethodNotImplementedException;
import com.badlogic.gdx.Gdx;
import org.antlr.v4.runtime.tree.ParseTree;
import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.Interpreter;
import pl.genschu.bloomooemulator.interpreter.antlr.AidemMediaLexer;
import pl.genschu.bloomooemulator.interpreter.antlr.AidemMediaParser;
import pl.genschu.bloomooemulator.interpreter.ast.ASTBuilderVisitor;
import pl.genschu.bloomooemulator.interpreter.ast.Node;
import pl.genschu.bloomooemulator.interpreter.variable.Attribute;
import pl.genschu.bloomooemulator.interpreter.variable.Method;
import pl.genschu.bloomooemulator.interpreter.variable.Parameter;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.utils.ArgumentsHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BehaviourVariable extends Variable {
	private Interpreter interpreter;
	private ConditionVariable condition;

	public BehaviourVariable(String name, String code, Context context) {
		super(name, context);
        
		try {
			AidemMediaLexer lexer = new AidemMediaLexer(CharStreams.fromString(code));
			AidemMediaParser parser = new AidemMediaParser(new CommonTokenStream(lexer));

			//lexer.removeErrorListeners();
			//lexer.addErrorListener(ThrowingErrorListener.INSTANCE);

			//parser.removeErrorListeners();
			//parser.addErrorListener(ThrowingErrorListener.INSTANCE);

			ParseTree tree = parser.script();

			ASTBuilderVisitor astBuilder = new ASTBuilderVisitor(context);
			Node astRoot = astBuilder.visit(tree);

			interpreter = new Interpreter(astRoot, context);
		} catch (Exception e) {
			Gdx.app.error("BehaviourVariable", "Failed to parse code: " + code, e);
		}
	}

	@Override
	public String getType() {
		return "BEHAVIOUR";
	}

	@Override
	protected void setMethods() {
		super.setMethods();

		this.setMethod("PLAY", new Method(
				"mixed"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method PLAY is not implemented yet");
			}
		});
		this.setMethod("RUN", new Method(
				List.of(
						new Parameter("mixed", "param1...paramN", false)
				),
				"mixed"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				if(arguments == null) {
					arguments = List.of();
				}

				if(interpreter == null) {
					Gdx.app.error("BehaviourVariable", "Interpreter is null");
					return null;
				}

				// let's backup old arguments
				HashMap<String, Variable> backupVariables = new HashMap<>();
				for(String key : context.getVariables().keySet()) {
					if(key.startsWith("$")) {
						backupVariables.put(key, context.getVariable(key));
					}
				}

				for(int i = 0; i < arguments.size(); i++) {
					context.setVariable("$"+(i+1), (Variable) arguments.get(i));
				}

				Gdx.app.log("BehaviourVariable", "Running behaviour " + getName() + " with " + arguments.size() + " arguments");

				for(int i = 0; i < arguments.size(); i++) {
					Gdx.app.log("BehaviourVariable", "Argument " + (i+1) + ": " + arguments.get(i));
				}

				interpreter.interpret();

				for(int i = 0; i < arguments.size(); i++) {
					context.removeVariable("$"+(i+1));
				}

				// restore old arguments
				for(String key : backupVariables.keySet()) {
					context.setVariable(key, backupVariables.get(key));
				}

				return (Variable) interpreter.getReturnValue();
			}
		});
		this.setMethod("RUNC", new Method(
				List.of(
						new Parameter("mixed", "param1...paramN", true)
				),
				"mixed"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				Variable conditionResult = new BoolVariable("", true, context);

				if(condition != null) {
					conditionResult = condition.fireMethod("CHECK", new BoolVariable("", true, context));
				}
				else {
					if(getAttribute("CONDITION") != null) {
						Attribute attribute = getAttribute("CONDITION");
						Variable variable = context.getVariable(attribute.getValue().toString());
						if(variable != null) {
							if(variable instanceof ConditionVariable) {
								condition = (ConditionVariable) variable;

								conditionResult = condition.fireMethod("CHECK", new BoolVariable("", true, context));
							}
							else {
								Gdx.app.error("BehaviourVariable", "Variable " + attribute.getValue().toString() + " is not a condition variable");
							}
						}
						else {
							Gdx.app.error("BehaviourVariable", "Condition variable " + attribute.getValue().toString() + " not found");
						}
					}
					else {
						Gdx.app.error("BehaviourVariable", "Condition variable not set in behaviour " + getName() + ". Assume true?");
					}
				}

				boolean result = ArgumentsHelper.getBoolean(conditionResult);

				Gdx.app.log("BehaviourVariable", "RUNC in behaviour " + getName() + " condition result: " + result);

				if(!result) {
					return null;
				}

				if(arguments == null) {
					arguments = List.of();
				}

				if(interpreter == null) {
					Gdx.app.error("BehaviourVariable", "Interpreter is null");
					return null;
				}

				// let's backup old arguments
				HashMap<String, Variable> backupVariables = new HashMap<>();
				for(String key : context.getVariables().keySet()) {
					if(key.startsWith("$")) {
						backupVariables.put(key, context.getVariable(key));
					}
				}

				for(int i = 0; i < arguments.size(); i++) {
					context.setVariable("$"+(i+1), (Variable) arguments.get(i));
				}

				Gdx.app.log("BehaviourVariable", "Running behaviour " + getName() + " with " + arguments.size() + " arguments");

				for(int i = 0; i < arguments.size(); i++) {
					Gdx.app.log("BehaviourVariable", "Argument " + (i+1) + ": " + arguments.get(i));
				}

				interpreter.interpret();

				for(int i = 0; i < arguments.size(); i++) {
					context.removeVariable("$"+(i+1));
				}

				// restore old arguments
				for(String key : backupVariables.keySet()) {
					context.setVariable(key, backupVariables.get(key));
				}

				return (Variable) interpreter.getReturnValue();
			}
		});
		this.setMethod("RUNLOOPED", new Method(
				List.of(
						new Parameter("INTEGER", "startVal", true),
						new Parameter("INTEGER", "endDiff", true),
						new Parameter("INTEGER", "incrementBy", false),
						new Parameter("mixed", "param1...paramN", false)
				),
				"mixed"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				int startVal = ArgumentsHelper.getInteger(arguments.get(0));
				int endDiff = ArgumentsHelper.getInteger(arguments.get(1));
				int incrementBy = 1;
				if(arguments.size() > 2) {
					incrementBy = ArgumentsHelper.getInteger(arguments.get(2));
				}

				if(interpreter == null) {
					Gdx.app.error("BehaviourVariable", "Interpreter is null");
					return null;
				}

				Variable conditionResult;

				if(condition == null) {
					if(getAttribute("CONDITION") != null) {
						Attribute attribute = getAttribute("CONDITION");
						Variable variable = context.getVariable(attribute.getValue().toString());
						if(variable != null) {
							if(variable instanceof ConditionVariable) {
								condition = (ConditionVariable) variable;
							}
							else {
								Gdx.app.error("BehaviourVariable", "Variable " + attribute.getValue().toString() + " is not a condition variable");
							}
						}
						else {
							Gdx.app.error("BehaviourVariable", "Condition variable " + attribute.getValue().toString() + " not found");
						}
					}
				}

				// let's backup old arguments
				HashMap<String, Variable> backupVariables = new HashMap<>();
				for(String key : context.getVariables().keySet()) {
					if(key.startsWith("$")) {
						backupVariables.put(key, context.getVariable(key));
					}
				}

				if(arguments.size() > 3) {
					for(int i = 3; i < arguments.size(); i++) {
						context.setVariable("$"+(i-1), (Variable) arguments.get(i));
					}
				}

				Gdx.app.log("BehaviourVariable", "Running looped behaviour " + getName() + " with " + (arguments.size() - 2) + " arguments");

				Gdx.app.log("BehaviourVariable", "Argument 1: "+startVal);
				for(int i = 3; i < arguments.size(); i++) {
					Gdx.app.log("BehaviourVariable", "Argument " + (i-1) + ": " + arguments.get(i));
				}

				for(int i = startVal; i < endDiff; i+=incrementBy) {
					if(condition != null) {
						conditionResult = condition.fireMethod("CHECK", new BoolVariable("", true, context));
					}
					else {
						conditionResult = new BoolVariable("", true, context);
					}
					if(ArgumentsHelper.getBoolean(conditionResult)) {
						context.setVariable("$1", new IntegerVariable("_I_", i, context));
						interpreter.interpret();
					}
					else {
						Gdx.app.log("BehaviourVariable", "Skipping looped behaviour " + getName() + " at " + i + " due to condition " + condition.getName() + " result");
					}
				}

				for(int i = 1; i < arguments.size() - 1; i++) {
					context.removeVariable("$"+i);
				}

				// restore old arguments
				for(String key : backupVariables.keySet()) {
					context.setVariable(key, backupVariables.get(key));
				}

				return null;
			}
		});
	}

	@Override
	public void setAttribute(String name, Attribute attribute) {
		List<String> knownAttributes = List.of("CODE", "CONDITION");
		if(knownAttributes.contains(name)) {
			super.setAttribute(name, attribute);
		}
	}

	// class just for debugging
	private static class ThrowingErrorListener extends BaseErrorListener {

		public static final ThrowingErrorListener INSTANCE = new ThrowingErrorListener();

		@Override
		public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e)
				throws ParseCancellationException {
			throw new ParseCancellationException("line " + line + ":" + charPositionInLine + " " + msg);
		}
	}
}
