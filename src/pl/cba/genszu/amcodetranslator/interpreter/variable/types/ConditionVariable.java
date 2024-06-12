package pl.cba.genszu.amcodetranslator.interpreter.variable.types;

import pl.cba.genszu.amcodetranslator.interpreter.Context;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Attribute;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Method;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Parameter;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Variable;

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
