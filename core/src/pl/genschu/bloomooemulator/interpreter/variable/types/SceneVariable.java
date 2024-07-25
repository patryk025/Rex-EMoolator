package pl.genschu.bloomooemulator.interpreter.variable.types;

import pl.genschu.bloomooemulator.interpreter.exceptions.ClassMethodNotImplementedException;
import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.variable.Attribute;
import pl.genschu.bloomooemulator.interpreter.variable.Method;
import pl.genschu.bloomooemulator.interpreter.variable.Parameter;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;

import java.io.File;
import java.util.List;

public class SceneVariable extends Variable {
	private File path;

	public SceneVariable(String name, Context context) {
		super(name, context);

		this.setMethod("PAUSE", new Method(
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method PAUSE is not implemented yet");
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
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method REMOVECLONES is not implemented yet");
			}
		});
		this.setMethod("RESUME", new Method(
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method RESUME is not implemented yet");
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
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method RUN is not implemented yet");
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
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method RUNCLONES is not implemented yet");
			}
		});
		this.setMethod("SETMINHSPRIORITY", new Method(
			List.of(
				new Parameter("INTEGER", "minHSPriority", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method SETMINHSPRIORITY is not implemented yet");
			}
		});
		this.setMethod("SETMUSICVOLUME", new Method(
			List.of(
				new Parameter("INTEGER", "volume", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method SETMUSICVOLUME is not implemented yet");
			}
		});
		this.setMethod("STARTMUSIC", new Method(
			List.of(
				new Parameter("STRING", "filename", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method STARTMUSIC is not implemented yet");
			}
		});
	}

	public File getPath() {
		return path;
	}

	public void setPath(File path) {
		this.path = path;
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
