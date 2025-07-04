package pl.genschu.bloomooemulator.interpreter.variable.types;

import com.badlogic.gdx.Gdx;
import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.variable.Attribute;
import pl.genschu.bloomooemulator.interpreter.variable.Method;
import pl.genschu.bloomooemulator.interpreter.variable.Parameter;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.loader.CNVParser;
import pl.genschu.bloomooemulator.utils.ArgumentsHelper;
import pl.genschu.bloomooemulator.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ClassVariable extends Variable {
	private final CNVParser cnvParser = new CNVParser();

	public ClassVariable(String name, Context context) {
		super(name, context);
	}

	@Override
	public String getType() {
		return "CLASS";
	}

	@Override
	protected void setMethods() {
		super.setMethods();

		this.setMethod("NEW", new Method(
				List.of(
						new Parameter("STRING", "varName", true),
						new Parameter("mixed", "param1...paramN", false)
				),
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				String className = getAttribute("DEF").getValue().toString();
				// classes are in common/classes/ directory
				className = "$COMMON/classes/" + className;
				Gdx.app.log("ClassVariable", "Loading class " + className);
				Context classContext = new Context();
				classContext.setParentContext(context);
				String filePath = FileUtils.resolveRelativePath(ClassVariable.this, className);

				File classFile = new File(filePath);

				if (classFile.exists()) {
					try {
						cnvParser.parseFile(classFile, classContext);
					} catch (NullPointerException | IOException e) {
						Gdx.app.error("Game", "Error while loading class " + className + " to variable " + ClassVariable.this.getName() + ":\n" + e.getMessage());
						return null;
					}
				} else {
					Gdx.app.error("Game", "Class definition " + className + " doesn't exist. Instance will be empty.");
				}

				String varName = ArgumentsHelper.getString(arguments.get(0));
				context.setVariable(varName, new InstanceVariable(varName, classContext));

				Gdx.app.log("ClassVariable", "Class " + className + " loaded to variable " + varName + ". Running constructor...");

				// fire constructor behaviour in the class
				Variable constructorBehaviour = classContext.getVariable("CONSTRUCTOR");

				if (constructorBehaviour instanceof StringVariable) {
					Gdx.app.log("ClassVariable", "CONSTRUCTOR BEHAVIOUR not found. Continue without it...");
					return null;
				}

				constructorBehaviour.getMethod("RUN", List.of("mixed")).execute(arguments);

				return null;
			}
		});
		this.setMethod("DELETE", new Method(
				List.of(
						new Parameter("STRING", "varName", true),
						new Parameter("mixed", "param1...paramN", true)
				),
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				String varName = ArgumentsHelper.getString(arguments.get(0));
				InstanceVariable var = (InstanceVariable) context.getVariable(varName);

				if (var == null) {
					Gdx.app.error("ClassVariable", "Variable not found: " + varName);
					return null;
				}

				BehaviourVariable destructor = (BehaviourVariable) var.getContext().getVariable("DESTRUCTOR");

				if (destructor != null) {
					destructor.getMethod("RUN", List.of("mixed")).execute(arguments);
				}
				else {
					Gdx.app.error("ClassVariable", "DESTRUCTOR BEHAVIOUR not found. Continue without it...");
				}

				context.removeVariable(varName);
				return null;
			}
		});
	}

	@Override
	public void setAttribute(String name, Attribute attribute) {
		List<String> knownAttributes = List.of("BASE", "DEF");
		if (knownAttributes.contains(name)) {
			super.setAttribute(name, attribute);
		}
	}

	public static class InstanceVariable extends Variable {
		private final Context instanceContext;

		public InstanceVariable(String name, Context context) {
			super(name, context);
			this.instanceContext = context;
		}

		@Override
		public Context getContext() {
			return instanceContext;
		}

		@Override
		public Method getMethod(String name, List<String> paramTypes) {
			return instanceContext.getVariable(name).getMethod("RUN", paramTypes);
		}

		@Override
		public String getType() {
			return "INSTANCE"; // generic
		}
	}
}
