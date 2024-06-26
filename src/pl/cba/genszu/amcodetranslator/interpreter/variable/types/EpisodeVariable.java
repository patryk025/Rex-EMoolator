package pl.cba.genszu.amcodetranslator.interpreter.variable.types;

import pl.cba.genszu.amcodetranslator.interpreter.Context;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Attribute;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Method;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Parameter;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Variable;

import java.util.List;

public class EpisodeVariable extends Variable {
	public EpisodeVariable(String name, Context context) {
		super(name, context);

		this.setMethod("BACK", new Method(
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method BACK is not implemented yet");
				return null;
			}
		});
		this.setMethod("GETCURRENTSCENE", new Method(
			"STRING"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method GETCURRENTSCENE is not implemented yet");
				return null;
			}
		});
		this.setMethod("GOTO", new Method(
			List.of(
				new Parameter("STRING", "sceneName", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method GOTO is not implemented yet");
				return null;
			}
		});
	}

	@Override
	public String getType() {
		return "EPISODE";
	}

	@Override
	public void setAttribute(String name, Attribute attribute) {
		List<String> knownAttributes = List.of("AUTHOR", "CREATIONTIME", "DESCRIPTION", "LASTMODIFYTIME", "PATH", "SCENES", "STARTWITH", "VERSION");
		if(knownAttributes.contains(name)) {
			super.setAttribute(name, attribute);
		}
	}

}
