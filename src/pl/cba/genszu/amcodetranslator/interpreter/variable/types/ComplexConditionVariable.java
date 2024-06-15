package pl.cba.genszu.amcodetranslator.interpreter.variable.types;

import pl.cba.genszu.amcodetranslator.interpreter.Context;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Attribute;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Method;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Parameter;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Variable;

import java.util.List;

public class ComplexConditionVariable extends Variable {
	public ComplexConditionVariable(String name, Context context) {
		super(name, context);

		this.setMethod("BREAK", new Method(
			List.of(
				new Parameter("BOOL", "haveToBreak", true)
			),
			"void"
		) {
			@Override
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				return null;
			}
		});
	}

	@Override
	public String getType() {
		return "COMPLEXCONDITION";
	}

	@Override
	public void setAttribute(String name, Attribute attribute) {
		List<String> knownAttributes = List.of("CONDITION1", "CONDITION2", "OPERATOR");
		if(knownAttributes.contains(name)) {
			super.setAttribute(name, attribute);
		}
	}

}
