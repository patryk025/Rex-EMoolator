package pl.genschu.bloomooemulator.interpreter.variable.types;

import pl.genschu.bloomooemulator.interpreter.exceptions.ClassMethodNotImplementedException;
import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.variable.Attribute;
import pl.genschu.bloomooemulator.interpreter.variable.Method;
import pl.genschu.bloomooemulator.interpreter.variable.Parameter;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.utils.ArgumentsHelper;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

public class DoubleVariable extends Variable {
	public DoubleVariable(String name, double value, Context context) {
		super(name, context);
		this.setAttribute("VALUE", new Attribute("DOUBLE", value));

		this.setMethod("ABS", new Method(
			List.of(
				new Parameter("DOUBLE", "doubleValue", true)
			),
			"DOUBLE"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method ABS is not implemented yet");
			}
		});
		this.setMethod("ADD", new Method(
			List.of(
				new Parameter("DOUBLE", "doubleValue", true)
			),
			"DOUBLE"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				double value = ArgumentsHelper.getDouble(arguments.get(0));
				set(GET()+value);
				return null;
			}
		});
		this.setMethod("ARCTAN", new Method(
			List.of(
				new Parameter("INTEGER|DOUBLE", "doubleValue", true)
			),
			"DOUBLE"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method ARCTAN is not implemented yet");
			}
		});
		this.setMethod("ARCTANEX", new Method(
			List.of(
				new Parameter("INTEGER|DOUBLE", "y", true),
				new Parameter("INTEGER|DOUBLE", "x", true)
			),
			"DOUBLE"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method ARCTANEX is not implemented yet");
			}
		});
		this.setMethod("CLAMP", new Method(
			List.of(
				new Parameter("INTEGER|DOUBLE", "rangeMin", true),
				new Parameter("INTEGER|DOUBLE", "rangeMax", true)
			),
			"DOUBLE"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method CLAMP is not implemented yet");
			}
		});
		this.setMethod("COSINUS", new Method(
			List.of(
				new Parameter("INTEGER|DOUBLE", "doubleValue", true)
			),
			"DOUBLE"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method COSINUS is not implemented yet");
			}
		});
		this.setMethod("DIV", new Method(
			List.of(
				new Parameter("INTEGER|DOUBLE", "divisor", true)
			),
			"DOUBLE"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method DIV is not implemented yet");
			}
		});
		this.setMethod("LENGTH", new Method(
			List.of(
				new Parameter("INTEGER|DOUBLE", "x", true),
				new Parameter("INTEGER|DOUBLE", "y", true)
			),
			"DOUBLE"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method LENGTH is not implemented yet");
			}
		});
		this.setMethod("MAXA", new Method(
			List.of(
				new Parameter("INTEGER|DOUBLE", "value1", true),
				new Parameter("INTEGER|DOUBLE", "value2...valueN", false)
			),
			"DOUBLE"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method MAXA is not implemented yet");
			}
		});
		this.setMethod("MINA", new Method(
			List.of(
				new Parameter("INTEGER|DOUBLE", "value1", true),
				new Parameter("INTEGER|DOUBLE", "value2...valueN", false)
			),
			"DOUBLE"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method MINA is not implemented yet");
			}
		});
		this.setMethod("MUL", new Method(
			List.of(
				new Parameter("INTEGER|DOUBLE", "multiplier", true)
			),
			"DOUBLE"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method MUL is not implemented yet");
			}
		});
		this.setMethod("SET", new Method(
			List.of(
				new Parameter("DOUBLE", "value", true)
			),
			"DOUBLE"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method SET is not implemented yet");
			}
		});
		this.setMethod("SINUS", new Method(
			List.of(
				new Parameter("INTEGER|DOUBLE", "doubleValue", true)
			),
			"DOUBLE"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method SINUS is not implemented yet");
			}
		});
		this.setMethod("SQRT", new Method(
			List.of(
				new Parameter("DOUBLE", "doubleValue", true)
			),
			"DOUBLE"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method SQRT is not implemented yet");
			}
		});
		this.setMethod("SUB", new Method(
			List.of(
				new Parameter("INTEGER|DOUBLE", "doubleValue", true)
			),
			"DOUBLE"
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
		return "DOUBLE";
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

	public int toInt() {
		double value = (double) this.getValue();
		if (value > 0) {
			return (int) Math.round(value);
		} else {
			return (int) Math.ceil(value - 0.5); // liczby ujemne zaokrągla "w dół"? Zamiast -0.5 == 0 robi -1
		}
	}

	public boolean toBool() {
		return (double) this.getValue() != 0;
	}

	public String toStringVariable() {
		if((double) this.getValue() == 0) {
			return "0";
		}
		NumberFormat formatter = new DecimalFormat("#0.00000");
		return formatter.format(this.getValue()).replace(",", ".");
	}

	public double clipToBool() {
		return (double) this.getValue() != 0 ? 1 : 0;
	}

	public Variable convert(String type) {
		if(type.equals("INTEGER")) {
			return new IntegerVariable(this.getName(), this.toInt(), this.context);
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

	public double GET() {
		return (double) this.getValue();
	}

	@Override
	public String toString() {
		return this.toStringVariable();
	}
}
