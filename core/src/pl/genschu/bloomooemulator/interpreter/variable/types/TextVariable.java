package pl.genschu.bloomooemulator.interpreter.variable.types;

import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.variable.Attribute;
import pl.genschu.bloomooemulator.interpreter.variable.Method;
import pl.genschu.bloomooemulator.interpreter.variable.Parameter;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;

import java.util.List;

public class TextVariable extends Variable {
	public TextVariable(String name, Context context) {
		super(name, context);

		this.setMethod("SETTEXT", new Method(
			List.of(
				new Parameter("STRING", "text", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method SETTEXT is not implemented yet");
				return null;
			}
		});
		this.setMethod("SHOW", new Method(
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method SHOW is not implemented yet");
				return null;
			}
		});
		this.setMethod("HIDE", new Method(
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method HIDE is not implemented yet");
				return null;
			}
		});
	}

	@Override
	public String getType() {
		return "TEXT";
	}

	@Override
	public void setAttribute(String name, Attribute attribute) {
		List<String> knownAttributes = List.of("FONT", "HJUSTIFY", "MONITORCOLLISION", "MONITORCOLLISIONALPHA", "PRIORITY", "RECT", "TEXT", "TOCANVAS", "VISIBLE", "VJUSTIFY");
		if(knownAttributes.contains(name)) {
			super.setAttribute(name, attribute);
		}
	}

}
