package pl.genschu.bloomooemulator.interpreter.variable.types;

import pl.genschu.bloomooemulator.interpreter.exceptions.ClassMethodNotImplementedException;
import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.variable.Attribute;
import pl.genschu.bloomooemulator.interpreter.variable.Method;
import pl.genschu.bloomooemulator.interpreter.variable.Parameter;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;

import java.util.List;

public class ButtonVariable extends Variable {
	public ButtonVariable(String name, Context context) {
		super(name, context);

		this.setMethod("DISABLE", new Method(
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				setAttribute("ENABLE", new Attribute("BOOL", "FALSE"));
				setAttribute("VISIBLE", new Attribute("BOOL", "FALSE"));
				return null;
			}
		});
		this.setMethod("DISABLEBUTVISIBLE", new Method(
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				setAttribute("ENABLE", new Attribute("BOOL", "FALSE"));
				setAttribute("VISIBLE", new Attribute("BOOL", "TRUE"));
				return null;
			}
		});
		this.setMethod("ENABLE", new Method(
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				setAttribute("ENABLE", new Attribute("BOOL", "TRUE"));
				setAttribute("VISIBLE", new Attribute("BOOL", "TRUE"));
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
			public Variable execute(List<Object> arguments) {
				setAttribute("PRIORITY", new Attribute("PRIORITY", arguments.get(0).toString()));
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
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method SETRECT is not implemented yet");
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
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method SETRECT is not implemented yet");
			}
		});
		this.setMethod("SETSTD", new Method(
			List.of(
				new Parameter("STRING", "varName", true),
				new Parameter("BOOLEAN", "unknown", false)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method SETSTD is not implemented yet");
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
