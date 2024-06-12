package pl.cba.genszu.amcodetranslator.interpreter.variable.types;

import pl.cba.genszu.amcodetranslator.interpreter.Context;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Attribute;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Method;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Parameter;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Variable;

import java.util.List;

public class KeyboardVariable extends Variable {
	public KeyboardVariable(String name, Context context) {
		super(name, context);

		this.setMethod("DISABLE", new Method(
			List.of(),
			"void"
		) {
			@Override
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				return null;
			}
		});
		this.setMethod("ENABLE", new Method(
			List.of(),
			"void"
		) {
			@Override
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				return null;
			}
		});
		this.setMethod("GETLATESTKEY", new Method(
			List.of(),
			"STRING"
		) {
			@Override
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				return null;
			}
		});
		this.setMethod("ISENABLED", new Method(
			List.of(),
			"BOOL"
		) {
			@Override
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				return null;
			}
		});
		this.setMethod("ISKEYDOWN", new Method(
			List.of(
				new Parameter("STRING", "keyName", true)
			),
			"BOOL"
		) {
			@Override
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				return null;
			}
		});
		this.setMethod("SETAUTOREPEAT", new Method(
			List.of(
				new Parameter("BOOL", "autorepeat", true)
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
		return "KEYBOARD";
	}

	@Override
	public void setAttribute(String name, Attribute attribute) {
		List<String> knownAttributes = List.o);
		if(knownAttributes.contains(name)) {
			super.setAttribute(name, attribute);
		}
	}

}
