package pl.cba.genszu.amcodetranslator.interpreter.variable.types;

import pl.cba.genszu.amcodetranslator.interpreter.Context;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Attribute;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Method;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Parameter;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Variable;

import java.util.List;

public class ApplicationVariable extends Variable {
	public ApplicationVariable(String name, Context context) {
		super(name, context);

		this.setMethod("EXIT", new Method(
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method EXIT is not implemented yet");
				return null;
			}
		});
		this.setMethod("GETLANGUAGE", new Method(
			"STRING"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method GETLANGUAGE is not implemented yet");
				return null;
			}
		});
		this.setMethod("RUN", new Method(
			List.of(
				new Parameter("STRING", "varName", true),
				new Parameter("STRING", "methodName", true),
				new Parameter("mixed", "param1...paramN", true)
			),
			"mixed?"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method RUN is not implemented yet");
				return null;
			}
		});
		this.setMethod("RUNENV", new Method(
			List.of(
				new Parameter("STRING", "sceneName", true),
				new Parameter("STRING", "behaviourName", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method RUNENV is not implemented yet");
				return null;
			}
		});
		this.setMethod("SETLANGUAGE", new Method(
			List.of(
				new Parameter("STRING", "languageCode", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method SETLANGUAGE is not implemented yet");
				return null;
			}
		});
	}

	@Override
	public String getType() {
		return "APPLICATION";
	}

	@Override
	public void setAttribute(String name, Attribute attribute) {
		List<String> knownAttributes = List.of("AUTHOR", "BLOOMOO_VERSION", "CREATIONTIME", "DESCRIPTION", "EPISODES", "LASTMODIFYTIME", "PATH", "SCENES", "STARTWITH", "VERSION");
		if(knownAttributes.contains(name)) {
			super.setAttribute(name, attribute);
		}
	}

}
