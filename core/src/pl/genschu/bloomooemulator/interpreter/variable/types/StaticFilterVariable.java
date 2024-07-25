package pl.genschu.bloomooemulator.interpreter.variable.types;

import pl.genschu.bloomooemulator.interpreter.exceptions.ClassMethodNotImplementedException;
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
				throw new ClassMethodNotImplementedException("Method LINK is not implemented yet");
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
				throw new ClassMethodNotImplementedException("Method SETPROPERTY is not implemented yet");
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
				throw new ClassMethodNotImplementedException("Method UNLINK is not implemented yet");
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
