package pl.genschu.bloomooemulator.interpreter.variable.types;

import pl.genschu.bloomooemulator.interpreter.exceptions.ClassMethodNotImplementedException;
import com.badlogic.gdx.Gdx;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
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

import java.util.List;

public class BehaviourVariable extends Variable {
	private Interpreter interpreter;
	private ConditionVariable condition;

	public BehaviourVariable(String name, String code, Context context) {
		super(name, context);
        
		try {
			AidemMediaLexer lexer = new AidemMediaLexer(CharStreams.fromString(code));
			AidemMediaParser parser = new AidemMediaParser(new CommonTokenStream(lexer));
			ParseTree tree = parser.script();

			ASTBuilderVisitor astBuilder = new ASTBuilderVisitor(context);
			Node astRoot = astBuilder.visit(tree);

			interpreter = new Interpreter(astRoot, context);
		} catch (Exception e) {
			Gdx.app.error("BehaviourVariable", "Failed to parse code: " + code, e);
		}

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
				boolean conditionResult = false;

				if(condition != null) {
					conditionResult = condition.check();
				}
				else {
					if(getAttribute("CONDITION") != null) {
						Attribute attribute = getAttribute("CONDITION");
						Variable variable = context.getVariable(attribute.getValue().toString());
						if(variable != null) {
							if(variable instanceof ConditionVariable) {
								condition = (ConditionVariable) variable;

								conditionResult = ArgumentsHelper.getBoolean(condition.getValue());
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
						Gdx.app.error("BehaviourVariable", "Condition variable not set in behaviour " + getName() + ". Assume false?");
					}
				}

				if(conditionResult) {
					return null;
				}

				if(arguments == null) {
					arguments = List.of();
				}

				if(interpreter == null) {
					Gdx.app.error("BehaviourVariable", "Interpreter is null");
					return null;
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
				return (Variable) interpreter.getReturnValue();
			}
		});
		this.setMethod("RUNLOOPED", new Method(
			List.of(
				new Parameter("INTEGER|DOUBLE", "startVal", true),
				new Parameter("INTEGER|DOUBLE", "endDiff", true),
				new Parameter("INTEGER|DOUBLE", "incrementBy", false),
				new Parameter("mixed", "param1...paramN", false)
			),
			"mixed"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method RUNLOOPED is not implemented yet");
			}
		});
	}

	@Override
	public String getType() {
		return "BEHAVIOUR";
	}

	@Override
	public void setAttribute(String name, Attribute attribute) {
		List<String> knownAttributes = List.of("CODE", "CONDITION");
		if(knownAttributes.contains(name)) {
			super.setAttribute(name, attribute);
		}
	}

}
