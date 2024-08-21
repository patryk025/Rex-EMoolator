package pl.genschu.bloomooemulator.interpreter.variable.types;

import pl.genschu.bloomooemulator.interpreter.exceptions.ClassMethodNotImplementedException;
import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.variable.Attribute;
import pl.genschu.bloomooemulator.interpreter.variable.Method;
import pl.genschu.bloomooemulator.interpreter.variable.Parameter;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;

import java.util.List;

public class PatternVariable extends Variable {
	public PatternVariable(String name, Context context) {
		super(name, context);
	}

	@Override
	public String getType() {
		return "PATTERN";
	}

	@Override
	protected void setMethods() {
		super.setMethods();

		this.setMethod("ADD", new Method(
				List.of(
						new Parameter("STRING", "unknown", true),
						new Parameter("INTEGER", "posX", true),
						new Parameter("INTEGER", "posY", true),
						new Parameter("STRING", "animoName", true),
						new Parameter("INTEGER", "layer?", true)
				),
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method ADD is not implemented yet");
			}
		});
	}

	@Override
	public void setAttribute(String name, Attribute attribute) {
		List<String> knownAttributes = List.of("GRIDX", "GRIDY", "HEIGHT", "LAYERS", "PRIORITY", "TOCANVAS", "VISIBLE", "WIDTH");
		if(knownAttributes.contains(name)) {
			super.setAttribute(name, attribute);
		}
	}

}
