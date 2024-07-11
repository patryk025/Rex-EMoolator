package pl.genschu.bloomooemulator.interpreter.variable.types;

import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.variable.Attribute;
import pl.genschu.bloomooemulator.interpreter.variable.Method;
import pl.genschu.bloomooemulator.interpreter.variable.Parameter;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;

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
