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
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				return null;
			}
		});
		this.setMethod("GETCURRENTSCENE", new Method(
			"STRING"
		) {
			@Override
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
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
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
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
