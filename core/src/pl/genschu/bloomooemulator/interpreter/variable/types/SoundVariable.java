package pl.genschu.bloomooemulator.interpreter.variable.types;

import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.variable.Attribute;
import pl.genschu.bloomooemulator.interpreter.variable.Method;
import pl.genschu.bloomooemulator.interpreter.variable.Parameter;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;

import java.util.List;

public class SoundVariable extends Variable {
	public SoundVariable(String name, Context context) {
		super(name, context);

		this.setMethod("ISPLAYING", new Method(
			"BOOL"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method ISPLAYING is not implemented yet");
				return null;
			}
		});
		this.setMethod("LOAD", new Method(
			List.of(
				new Parameter("STRING", "path", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method LOAD is not implemented yet");
				return null;
			}
		});
		this.setMethod("PAUSE", new Method(
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method PAUSE is not implemented yet");
				return null;
			}
		});
		this.setMethod("PLAY", new Method(
			List.of(
				new Parameter("STRING", "unknown", false)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method PLAY is not implemented yet");
				return null;
			}
		});
		this.setMethod("RESUME", new Method(
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method RESUME is not implemented yet");
				return null;
			}
		});
		this.setMethod("STOP", new Method(
			List.of(
				new Parameter("BOOL", "emitSignal", false)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method STOP is not implemented yet");
				return null;
			}
		});
	}

	@Override
	public String getType() {
		return "SOUND";
	}

	@Override
	public void setAttribute(String name, Attribute attribute) {
		List<String> knownAttributes = List.of("FILENAME", "FLUSHAFTERPLAYED", "PRELOAD", "RELEASE");
		if(knownAttributes.contains(name)) {
			super.setAttribute(name, attribute);
		}
	}

}
