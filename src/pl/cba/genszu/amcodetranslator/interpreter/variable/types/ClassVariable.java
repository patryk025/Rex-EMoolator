package pl.cba.genszu.amcodetranslator.interpreter.variable.types;

import pl.cba.genszu.amcodetranslator.interpreter.Context;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Attribute;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Method;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Parameter;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Variable;

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
