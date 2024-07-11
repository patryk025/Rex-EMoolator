package pl.genschu.bloomooemulator.interpreter.variable.types;

import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.variable.Attribute;
import pl.genschu.bloomooemulator.interpreter.variable.Method;
import pl.genschu.bloomooemulator.interpreter.variable.Parameter;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;

import java.util.List;

public class StaticFilterVariable extends Variable {
	public StaticFilterVariable(String name, Context context) {
		super(name, context);

		this.setMethod("LINK", new Method(
			List.of(
				new Parameter("STRING", "imageName", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method LINK is not implemented yet");
				return null;
			}
		});
		this.setMethod("SETPROPERTY", new Method(
			List.of(
				new Parameter("STRING", "filterName", true),
				new Parameter("STRING|INTEGER", "value", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method SETPROPERTY is not implemented yet");
				return null;
			}
		});
		this.setMethod("UNLINK", new Method(
			List.of(
				new Parameter("STRING", "imageName", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method UNLINK is not implemented yet");
				return null;
			}
		});
	}

	@Override
	public String getType() {
		return "STATICFILTER";
	}

	@Override
	public void setAttribute(String name, Attribute attribute) {
		List<String> knownAttributes = List.of("ACTION");
		if(knownAttributes.contains(name)) {
			super.setAttribute(name, attribute);
		}
	}

}
