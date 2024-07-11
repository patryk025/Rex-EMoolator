package pl.genschu.bloomooemulator.interpreter.variable.types;

import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.variable.Attribute;
import pl.genschu.bloomooemulator.interpreter.variable.Method;
import pl.genschu.bloomooemulator.interpreter.variable.Parameter;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;

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
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method BREAK is not implemented yet");
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
