package pl.genschu.bloomooemulator.interpreter.variable.types;

import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.variable.Attribute;
import pl.genschu.bloomooemulator.interpreter.variable.Method;
import pl.genschu.bloomooemulator.interpreter.variable.Parameter;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;

import java.util.List;

public class ClassVariable extends Variable {
	public ClassVariable(String name, Context context) {
		super(name, context);

		this.setMethod("NEW", new Method(
			List.of(
				new Parameter("STRING", "varName", true),
				new Parameter("mixed", "param1...paramN", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method NEW is not implemented yet");
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
				// TODO: implement this method
				System.out.println("Method DELETE is not implemented yet");
				return null;
			}
		});
	}

	@Override
	public String getType() {
		return "CLASS";
	}

	@Override
	public void setAttribute(String name, Attribute attribute) {
		List<String> knownAttributes = List.of("BASE", "DEF");
		if(knownAttributes.contains(name)) {
			super.setAttribute(name, attribute);
		}
	}

}
