package pl.genschu.bloomooemulator.interpreter.variable.types;

import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.variable.Attribute;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;

import java.util.List;

public class FontVariable extends Variable {
	public FontVariable(String name, Context context) {
		super(name, context);

	}

	@Override
	public String getType() {
		return "FONT";
	}

	@Override
	public void setAttribute(String name, Attribute attribute) {
		List<String> knownAttributes = List.of("DEF");
		if(knownAttributes.contains(name)) {
			super.setAttribute(name, attribute);
		}
	}

}
