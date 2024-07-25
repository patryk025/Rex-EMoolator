package pl.genschu.bloomooemulator.interpreter.variable.types;

import pl.genschu.bloomooemulator.interpreter.exceptions.ClassMethodNotImplementedException;
import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.variable.Attribute;
import pl.genschu.bloomooemulator.interpreter.variable.Method;
import pl.genschu.bloomooemulator.interpreter.variable.Parameter;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;

import java.util.List;

public class IntegerVariable extends Variable {
	public IntegerVariable(String name, int value, Context context) {
		super(name, context);
		this.setAttribute("VALUE", new Attribute("INTEGER", value));

		this.setMethod("ABS", new Method(
			List.of(
				new Parameter("INTEGER", "value", true)
			),
			"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method ABS is not implemented yet");
			}
		});
		this.setMethod("ADD", new Method(
			List.of(
				new Parameter("INTEGER|DOUBLE", "addend", true)
			),
			"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method ADD is not implemented yet");
			}
		});
		this.setMethod("AND", new Method(
			List.of(
				new Parameter("INTEGER", "value", true)
			),
			"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method AND is not implemented yet");
			}
		});
		this.setMethod("CLAMP", new Method(
			List.of(
				new Parameter("INTEGER", "rangeMin", true),
				new Parameter("INTEGER", "rangeMax", true)
			),
			"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method CLAMP is not implemented yet");
			}
		});
		this.setMethod("DEC", new Method(
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method DEC is not implemented yet");
			}
		});
		this.setMethod("DIV", new Method(
			List.of(
				new Parameter("INTEGER", "divisor", true)
			),
			"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method DIV is not implemented yet");
			}
		});
		this.setMethod("INC", new Method(
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method INC is not implemented yet");
			}
		});
		this.setMethod("LENGTH", new Method(
			List.of(
				new Parameter("INTEGER", "value1", true),
				new Parameter("INTEGER", "value2", true)
			),
			"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method LENGTH is not implemented yet");
			}
		});
		this.setMethod("MOD", new Method(
			List.of(
				new Parameter("INTEGER", "divisor", true)
			),
			"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method MOD is not implemented yet");
			}
		});
		this.setMethod("MUL", new Method(
			List.of(
				new Parameter("INTEGER", "divisor", true)
			),
			"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method MUL is not implemented yet");
			}
		});
		this.setMethod("RANDOM", new Method(
			List.of(
				new Parameter("INTEGER", "rangeMax", true)
			),
			"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method RANDOM is not implemented yet");
			}
		});
		this.setMethod("RANDOM", new Method(
			List.of(
				new Parameter("INTEGER", "offset", true),
				new Parameter("INTEGER", "rangeMax", true)
			),
			"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method RANDOM is not implemented yet");
			}
		});
		this.setMethod("SET", new Method(
			List.of(
				new Parameter("INTEGER", "value", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method SET is not implemented yet");
			}
		});
		this.setMethod("SUB", new Method(
			List.of(
				new Parameter("INTEGER", "subtrahend", true)
			),
			"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method SUB is not implemented yet");
			}
		});
	}

	@Override
	public String getType() {
		return "INTEGER";
	}

	@Override
	public Object getValue() {
		return this.getAttribute("VALUE").getValue();
	}

	@Override
	public void setAttribute(String name, Attribute attribute) {
		List<String> knownAttributes = List.of("TOINI", "VALUE");
		if(knownAttributes.contains(name)) {
			super.setAttribute(name, attribute);
		}
	}

	public String toStringVariable() {
		return String.valueOf(this.getValue());
	}

	public boolean toBool() {
		return (int) this.getValue() != 0;
	}

	public double toDouble() {
		return (double) (Integer) this.getValue();
	}

	public int clipToBool() {
		return (int) this.getValue() != 0 ? 1 : 0;
	}

	public Variable convert(String type) {
		if(type.equals("DOUBLE")) {
			return new DoubleVariable(this.getName(), this.toDouble(), this.context);
		}
		else if(type.equals("BOOL")) {
			return new BoolVariable(this.getName(), this.toBool(), this.context);
		}
		else if(type.equals("STRING")) {
			return new StringVariable(this.getName(), this.toStringVariable(), this.context);
		}
		else {
			return this;
		}
	}

	public int GET() {
		try {
			return (int) this.getValue();
		} catch (ClassCastException e) {
			return Integer.parseInt(String.valueOf(this.getValue()));
		}
	}

	@Override
	public String toString() {
		return this.toStringVariable();
	}
}
