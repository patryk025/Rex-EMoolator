package pl.genschu.bloomooemulator.interpreter.variable.types;

import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.exceptions.ClassMethodNotImplementedException;
import pl.genschu.bloomooemulator.interpreter.variable.Attribute;
import pl.genschu.bloomooemulator.interpreter.variable.Method;
import pl.genschu.bloomooemulator.interpreter.variable.Parameter;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.utils.ArgumentsHelper;

import java.util.List;
import java.util.Random;

public class RandVariable extends Variable {
	public RandVariable(String name, Context context) {
		super(name, context);

		this.setMethod("GET", new Method(
			List.of(
				new Parameter("INTEGER", "param1", true), // [offset] or range
				new Parameter("INTEGER", "param2", false) // range if [offset] is present
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				int offset = 0;
				int range = 1;
				if(arguments.size() >= 2) {
					offset = ArgumentsHelper.getInteger(arguments.get(0));
					range = ArgumentsHelper.getInteger(arguments.get(1));
				}
				else {
					range = ArgumentsHelper.getInteger(arguments.get(0));
				}

				return new IntegerVariable("", getRandom(offset, range), getContext());
			}
		});
	}

	private int getRandom(int range) {
		return getRandom(0, range);
	}

	private int getRandom(int offset, int range) {
		Random random = new Random();
		return random.nextInt(range) + offset;
	}

	@Override
	public String getType() {
		return "RAND";
	}

	@Override
	public void setAttribute(String name, Attribute attribute) {
		// no known parameters
    }

}
