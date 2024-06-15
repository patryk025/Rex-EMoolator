package pl.cba.genszu.amcodetranslator.interpreter.variable.types;

import pl.cba.genszu.amcodetranslator.interpreter.Context;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Attribute;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Method;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Parameter;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Variable;

import java.util.List;

public class ButtonVariable extends Variable {
	public ButtonVariable(String name, Context context) {
		super(name, context);

		this.setMethod("DISABLE", new Method(
			"void"
		) {
			@Override
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				return null;
			}
		});
		this.setMethod("DISABLEBUTVISIBLE", new Method(
			"void"
		) {
			@Override
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				return null;
			}
		});
		this.setMethod("ENABLE", new Method(
			"void"
		) {
			@Override
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				return null;
			}
		});
		this.setMethod("SETPRIORITY", new Method(
			List.of(
				new Parameter("INTEGER", "posZ", true)
			),
			"void"
		) {
			@Override
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				return null;
			}
		});
		this.setMethod("SETRECT", new Method(
			List.of(
				new Parameter("STRING", "varName", true)
			),
			"void"
		) {
			@Override
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				return null;
			}
		});
		this.setMethod("SETRECT", new Method(
			List.of(
				new Parameter("INTEGER", "xLeft", true),
				new Parameter("INTEGER", "yBottom", true),
				new Parameter("INTEGER", "xRight", true),
				new Parameter("INTEGER", "yTop", true)
			),
			"void"
		) {
			@Override
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				return null;
			}
		});
		this.setMethod("SETSTD", new Method(
			List.of(
				new Parameter("STRING", "varName", true)
			),
			"void"
		) {
			@Override
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				return null;
			}
		});
		this.setMethod("SETSTD", new Method(
			List.of(
				new Parameter("STRING", "varName", true),
				new Parameter("BOOLEAN", "unknown", true)
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
		return "BUTTON";
	}

	@Override
	public void setAttribute(String name, Attribute attribute) {
		List<String> knownAttributes = List.of("DRAGGABLE", "ENABLE", "GFXONCLICK", "GFXONMOVE", "GFXSTANDARD", "RECT", "SNDONMOVE", "VISIBLE");
		if(knownAttributes.contains(name)) {
			super.setAttribute(name, attribute);
		}
	}

}
