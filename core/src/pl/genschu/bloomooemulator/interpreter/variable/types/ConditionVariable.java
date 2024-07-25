package pl.genschu.bloomooemulator.interpreter.variable.types;

import pl.genschu.bloomooemulator.interpreter.exceptions.ClassMethodNotImplementedException;
import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.variable.Attribute;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;

import java.util.List;

public class ConditionVariable extends Variable {
	public ConditionVariable(String name, Context context) {
		super(name, context);

	}

	@Override
	public String getType() {
		return "CONDITION";
	}

	@Override
	public void setAttribute(String name, Attribute attribute) {
		List<String> knownAttributes = List.of("OPERAND1", "OPERAND2", "OPERATOR");
		if(knownAttributes.contains(name)) {
			super.setAttribute(name, attribute);
		}
	}

}
