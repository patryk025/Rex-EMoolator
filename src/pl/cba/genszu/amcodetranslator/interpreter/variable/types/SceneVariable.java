package pl.cba.genszu.amcodetranslator.interpreter.variable.types;

import pl.cba.genszu.amcodetranslator.interpreter.Context;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Attribute;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Method;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Parameter;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Variable;

import java.util.List;

public class SceneVariable extends Variable {
	public SceneVariable(String name, Context context) {
		super(name, context);

		this.setMethod("PAUSE", new Method(
			"void"
		) {
			@Override
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				return null;
			}
		});
		this.setMethod("REMOVECLONES", new Method(
			List.of(
				new Parameter("STRING", "varName", true),
				new Parameter("INTEGER", "unknown", true),
				new Parameter("INTEGER", "unknown", true)
			),
			"void"
		) {
			@Override
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				return null;
			}
		});
		this.setMethod("RESUME", new Method(
			"void"
		) {
			@Override
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				return null;
			}
		});
		this.setMethod("RUN", new Method(
			List.of(
				new Parameter("STRING", "varName", true),
				new Parameter("STRING", "methodName", true),
				new Parameter("mixed", "param1...paramN", false)
			),
			"void"
		) {
			@Override
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				return null;
			}
		});
		this.setMethod("RUNCLONES", new Method(
			List.of(
				new Parameter("STRING", "varName", true),
				new Parameter("INTEGER", "unknown", true),
				new Parameter("INTEGER", "unknown", true),
				new Parameter("STRING", "behaviourName", true)
			),
			"void"
		) {
			@Override
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				return null;
			}
		});
		this.setMethod("SETMINHSPRIORITY", new Method(
			List.of(
				new Parameter("INTEGER", "minHSPriority", true)
			),
			"void"
		) {
			@Override
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				return null;
			}
		});
		this.setMethod("SETMUSICVOLUME", new Method(
			List.of(
				new Parameter("INTEGER", "volume", true)
			),
			"void"
		) {
			@Override
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				return null;
			}
		});
		this.setMethod("STARTMUSIC", new Method(
			List.of(
				new Parameter("STRING", "filename", true)
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
		return "SCENE";
	}

	@Override
	public void setAttribute(String name, Attribute attribute) {
		List<String> knownAttributes = List.of("AUTHOR", "BACKGROUND", "CREATIONTIME", "DLLS", "LASTMODIFYTIME", "MUSIC", "PATH", "VERSION");
		if(knownAttributes.contains(name)) {
			super.setAttribute(name, attribute);
		}
	}

}
