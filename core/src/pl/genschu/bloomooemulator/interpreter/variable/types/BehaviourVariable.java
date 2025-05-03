package pl.genschu.bloomooemulator.interpreter.variable.types;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import pl.genschu.bloomooemulator.interpreter.antlr.AidemMediaErrorListener;
import pl.genschu.bloomooemulator.interpreter.antlr.AidemMediaErrorStrategy;
import com.badlogic.gdx.Gdx;
import org.antlr.v4.runtime.tree.ParseTree;
import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.Interpreter;
import pl.genschu.bloomooemulator.interpreter.antlr.AidemMediaLexer;
import pl.genschu.bloomooemulator.interpreter.antlr.AidemMediaParser;
import pl.genschu.bloomooemulator.interpreter.ast.ASTBuilderVisitor;
import pl.genschu.bloomooemulator.interpreter.ast.Node;
import pl.genschu.bloomooemulator.interpreter.exceptions.BreakException;
import pl.genschu.bloomooemulator.interpreter.exceptions.OneBreakException;
import pl.genschu.bloomooemulator.interpreter.variable.Attribute;
import pl.genschu.bloomooemulator.interpreter.variable.Method;
import pl.genschu.bloomooemulator.interpreter.variable.Parameter;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.utils.ArgumentsHelper;

import java.util.HashMap;
import java.util.List;

public class BehaviourVariable extends Variable {
	private Interpreter interpreter;
	private ConditionVariable condition;

	CommonTokenStream tokens = null;

	public BehaviourVariable(String name, String code, Context context) {
		super(name, context);

		AidemMediaErrorListener errorListener = new AidemMediaErrorListener();
		Node astRoot = parseCode(code, errorListener);

		List<AidemMediaErrorListener.ParseError> errors = errorListener.getErrors();
		if (!errors.isEmpty()) {
			Gdx.app.log("BehaviourVariable", "Found " + errors.size() + " errors in " + name);
			for (AidemMediaErrorListener.ParseError error : errors) {
				Gdx.app.log("BehaviourVariable", error.toString());
			}

			String fixedCode = fixCode(tokens, errors);
			Gdx.app.log("BehaviourVariable", "Fixed code: " + fixedCode);
			astRoot = parseCode(fixedCode, errorListener);
			errors = errorListener.getErrors();
			if (!errors.isEmpty()) {
				Gdx.app.error("BehaviourVariable", "Failed to fix " + errors.size() + " errors in " + name);
			}
		}

		interpreter = new Interpreter(astRoot, context);
	}

	private Node parseCode(String code, AidemMediaErrorListener errorListener) {
		errorListener.clearErrors();
		try {
			AidemMediaLexer lexer = new AidemMediaLexer(CharStreams.fromString(code));
			tokens = new CommonTokenStream(lexer);
			AidemMediaParser parser = new AidemMediaParser(tokens);

			lexer.removeErrorListeners();
			parser.removeErrorListeners();
			lexer.addErrorListener(errorListener);
			parser.addErrorListener(errorListener);

			parser.setErrorHandler(new AidemMediaErrorStrategy());

			ParseTree tree = parser.script();

			ASTBuilderVisitor astBuilder = new ASTBuilderVisitor(context);

            return astBuilder.visit(tree);
		} catch (Exception e) {
			Gdx.app.error("BehaviourVariable", "Failed to parse code: " + code, e);
		}

		return null;
	}

	@Override
	public String getType() {
		return "BEHAVIOUR";
	}

	@Override
	protected void setMethods() {
		super.setMethods();

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

				// lets backup old arguments
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

				// lets backup old arguments
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
						context.setVariable("$"+i, (Variable) arguments.get(i));
					}
				}

				Gdx.app.log("BehaviourVariable", "Running looped behaviour " + getName() + " with " + (arguments.size() - 2) + " arguments");

				Gdx.app.log("BehaviourVariable", "Argument 1: "+startVal);
				Gdx.app.log("BehaviourVariable", "Argument 2: "+incrementBy);
				for(int i = 3; i < arguments.size(); i++) {
					Gdx.app.log("BehaviourVariable", "Argument " + i + ": " + arguments.get(i));
				}

				for (int i = startVal; i < startVal + endDiff; i += incrementBy) {
					try {
						if (condition != null) {
							conditionResult = condition.fireMethod("CHECK", new BoolVariable("", true, context));
						} else {
							conditionResult = new BoolVariable("", true, context);
						}
						if (ArgumentsHelper.getBoolean(conditionResult)) {
							context.setVariable("$1", new IntegerVariable("_I_", i, context));
							context.setVariable("$2", new IntegerVariable("_step_", incrementBy, context));
							interpreter.interpret();
						} else {
							Gdx.app.log("BehaviourVariable", "Skipping looped behaviour " + getName() + " at " + i + " due to condition " + condition.getName() + " result");
						}
					}
					catch (BreakException e) {
						Gdx.app.log("BehaviourVariable", "Breaking looped behaviour " + getName() + " at " + i + " due to break");
						break;
					}
					catch (OneBreakException e) {
						Gdx.app.log("BehaviourVariable", "Behaviour " + getName() + " has one break at " + i + ". Continuing loop");
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

	private String fixCode(CommonTokenStream tokens, List<AidemMediaErrorListener.ParseError> errors) {
		TokenStreamRewriter rewriter = new TokenStreamRewriter(tokens);
		for (AidemMediaErrorListener.ParseError error : errors) {
			Token token = error.getOffendingToken();
			String msg = error.getMessage();

			if (msg.contains("no viable alternative") && token.getType() == AidemMediaParser.ENDINSTR) {
				Token prevToken = rewriter.getTokenStream().get(token.getTokenIndex() - 1);
				if (prevToken != null && prevToken.getType() == AidemMediaParser.RPAREN) {
					rewriter.insertBefore(prevToken, "\"");
					Gdx.app.log("FixCode", "Inserted missing quotation mark before " + prevToken.getText());
				}
			}
		}
		return rewriter.getText();
	}
}
