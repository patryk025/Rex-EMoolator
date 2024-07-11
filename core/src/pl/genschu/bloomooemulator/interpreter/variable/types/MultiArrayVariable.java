package pl.genschu.bloomooemulator.interpreter.variable.types;

import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.variable.Attribute;
import pl.genschu.bloomooemulator.interpreter.variable.Method;
import pl.genschu.bloomooemulator.interpreter.variable.Parameter;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;

import java.util.List;

public class MultiArrayVariable extends Variable {
	public MultiArrayVariable(String name, Context context) {
		super(name, context);

		this.setMethod("GET", new Method(
			List.of(
				new Parameter("INTEGER", "x", true),
				new Parameter("INTEGER", "y", false),
				new Parameter("INTEGER", "z...", false)
			),
			"mixed"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method GET is not implemented yet");
				return null;
			}
		});
		this.setMethod("SET", new Method(
			List.of(
				new Parameter("INTEGER", "x", true),
				new Parameter("INTEGER", "y", false),
				new Parameter("INTEGER", "z...", false),
				new Parameter("mixed", "value", false)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method SET is not implemented yet");
				return null;
			}
		});
	}

	@Override
	public String getType() {
		return "MULTIARRAY";
	}

	@Override
	public void setAttribute(String name, Attribute attribute) {
		List<String> knownAttributes = List.of("DIMENSIONS");
		if(knownAttributes.contains(name)) {
			super.setAttribute(name, attribute);
		}
	}

}
