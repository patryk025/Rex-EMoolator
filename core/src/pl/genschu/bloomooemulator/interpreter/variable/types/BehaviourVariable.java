package pl.genschu.bloomooemulator.interpreter.variable.types;

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

import java.util.List;

public class BehaviourVariable extends Variable {
	private Interpreter interpreter;

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
				System.out.println("Method PLAY is not implemented yet");
				return null;
			}
		});
		// temporary implementation
		this.setMethod("RUN", new Method(
				List.of(
				),
				"mixed"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				if(interpreter == null) {
					Gdx.app.error("BehaviourVariable", "Interpreter is null");
					return null;
				}
				interpreter.interpret();
				return (Variable) interpreter.getReturnValue();
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
				// TODO: przepakowanie argumentów do $1, $2, $3 itd.
				if(interpreter == null) {
					Gdx.app.error("BehaviourVariable", "Interpreter is null");
					return null;
				}
				interpreter.interpret();
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
				// TODO: implement this method
				System.out.println("Method RUNC is not implemented yet");
				return null;
			}
		});
		this.setMethod("RUNLOOPED", new Method(
			List.of(
				new Parameter("INTEGER|DOUBLE", "startVal", true),
				new Parameter("INTEGER|DOUBLE", "endDiff", true)
			),
			"mixed"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method RUNLOOPED is not implemented yet");
				return null;
			}
		});
		this.setMethod("RUNLOOPED", new Method(
			List.of(
				new Parameter("INTEGER|DOUBLE", "startVal", true),
				new Parameter("INTEGER|DOUBLE", "endDiff", true),
				new Parameter("INTEGER|DOUBLE", "incrementBy", true),
				new Parameter("mixed", "param1...paramN", true)
			),
			"mixed"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method RUNLOOPED is not implemented yet");
				return null;
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
