package pl.genschu.bloomooemulator.interpreter.variable.types;

import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.variable.Attribute;
import pl.genschu.bloomooemulator.interpreter.variable.Method;
import pl.genschu.bloomooemulator.interpreter.variable.Parameter;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;

import java.util.List;

public class StructVariable extends Variable {
	public StructVariable(String name, Context context) {
		super(name, context);

		this.setMethod("GETFIELD", new Method(
			List.of(
				new Parameter("INTEGER", "columnIndex", true)
			),
			"STRING"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method GETFIELD is not implemented yet");
				return null;
			}
		});
		this.setMethod("SET", new Method(
			List.of(
				new Parameter("STRING", "varName", true)
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
		return "STRUCT";
	}

	@Override
	public void setAttribute(String name, Attribute attribute) {
		List<String> knownAttributes = List.of("FIELDS");
		if(knownAttributes.contains(name)) {
			super.setAttribute(name, attribute);
		}
	}

}
