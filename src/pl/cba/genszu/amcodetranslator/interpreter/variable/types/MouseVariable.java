package pl.cba.genszu.amcodetranslator.interpreter.variable.types;

import pl.cba.genszu.amcodetranslator.interpreter.Context;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Attribute;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Method;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Variable;

import java.util.List;

public class MouseVariable extends Variable {
	public MouseVariable(String name, Context context) {
		super(name, context);

		this.setMethod("DISABLE", new Method(
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method DISABLE is not implemented yet");
				return null;
			}
		});
	}

	@Override
	public String getType() {
		return "MOUSE";
	}

	@Override
	public void setAttribute(String name, Attribute attribute) {
		List<String> knownAttributes = List.of("RAW");
		if(knownAttributes.contains(name)) {
			super.setAttribute(name, attribute);
		}
	}

}
