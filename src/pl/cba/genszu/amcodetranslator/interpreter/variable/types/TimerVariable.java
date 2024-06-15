package pl.cba.genszu.amcodetranslator.interpreter.variable.types;

import pl.cba.genszu.amcodetranslator.interpreter.Context;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Attribute;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Method;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Parameter;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Variable;

import java.util.List;

public class TimerVariable extends Variable {
	public TimerVariable(String name, Context context) {
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
		this.setMethod("ENABLE", new Method(
			"void"
		) {
			@Override
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				return null;
			}
		});
		this.setMethod("GETTICKS", new Method(
			"INTEGER"
		) {
			@Override
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				return null;
			}
		});
		this.setMethod("RESET", new Method(
			"void"
		) {
			@Override
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				return null;
			}
		});
		this.setMethod("SETELAPSE", new Method(
			List.of(
				new Parameter("INTEGER", "timeMs", true)
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
		return "TIMER";
	}

	@Override
	public void setAttribute(String name, Attribute attribute) {
		List<String> knownAttributes = List.of("ELAPSE", "ENABLED", "TICKS");
		if(knownAttributes.contains(name)) {
			super.setAttribute(name, attribute);
		}
	}

}
