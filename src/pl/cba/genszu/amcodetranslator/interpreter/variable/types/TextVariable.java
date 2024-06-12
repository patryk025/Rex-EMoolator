package pl.cba.genszu.amcodetranslator.interpreter.variable.types;

import pl.cba.genszu.amcodetranslator.interpreter.Context;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Attribute;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Method;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Parameter;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Variable;

import java.util.List;

public class TextVariable extends Variable {
	public TextVariable(String name, Context context) {
		super(name, context);

		this.setMethod("SETTEXT", new Method(
			List.of(
				new Parameter("STRING", "text", true)
			),
			"void"
		) {
			@Override
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				return null;
			}
		});
		this.setMethod("SHOW", new Method(
			List.of(),
			"void"
		) {
			@Override
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				return null;
			}
		});
		this.setMethod("HIDE", new Method(
			List.of(),
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
		return "TEXT";
	}

	@Override
	public void setAttribute(String name, Attribute attribute) {
		List<String> knownAttributes = List.of("FONT", "HJUSTIFY", "MONITORCOLLISION", "MONITORCOLLISIONALPHA", "PRIORITY", "RECT", "TEXT", "TOCANVAS", "VISIBLE", "VJUSTIFY");
		if(knownAttributes.contains(name)) {
			super.setAttribute(name, attribute);
		}
	}

}
