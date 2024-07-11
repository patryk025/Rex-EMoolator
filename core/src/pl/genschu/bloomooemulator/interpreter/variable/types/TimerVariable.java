package pl.genschu.bloomooemulator.interpreter.variable.types;

import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.variable.Attribute;
import pl.genschu.bloomooemulator.interpreter.variable.Method;
import pl.genschu.bloomooemulator.interpreter.variable.Parameter;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;

import java.util.List;

public class TimerVariable extends Variable {
	public TimerVariable(String name, Context context) {
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
		this.setMethod("ENABLE", new Method(
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method ENABLE is not implemented yet");
				return null;
			}
		});
		this.setMethod("GETTICKS", new Method(
			"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method GETTICKS is not implemented yet");
				return null;
			}
		});
		this.setMethod("RESET", new Method(
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method RESET is not implemented yet");
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
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method SETELAPSE is not implemented yet");
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
