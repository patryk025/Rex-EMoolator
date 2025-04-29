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
		super.setAttribute("INIT_VALUE", new Attribute("DOUBLE", value));
	}

	@Override
	protected void setMethods() {
		super.setMethods();

		this.setMethod("ABS", new Method(
				List.of(
						new Parameter("DOUBLE", "doubleValue", true)
				),
				"DOUBLE"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				double value = ArgumentsHelper.getDouble(arguments.get(0));
				set(Math.abs(value));
				return DoubleVariable.this;
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
				return DoubleVariable.this;
			}
		});
		this.setMethod("ARCTAN", new Method(
				List.of(
						new Parameter("DOUBLE", "angle", true)
				),
				"DOUBLE"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				double angle = ArgumentsHelper.getDouble(arguments.get(0));

				double atanRadians = Math.atan(Math.toRadians(angle));

				set(atanRadians);
				return DoubleVariable.this;
			}
		});
		this.setMethod("ARCTANEX", new Method(
				List.of(
						new Parameter("DOUBLE", "y", true),
						new Parameter("DOUBLE", "x", true)
				),
				"DOUBLE"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				double y = ArgumentsHelper.getDouble(arguments.get(0));
				double x = ArgumentsHelper.getDouble(arguments.get(1));

				double atanRadians = Math.atan2(y, x);

				set(atanRadians);
				return DoubleVariable.this;
			}
		});
		this.setMethod("CLAMP", new Method(
				List.of(
						new Parameter("DOUBLE", "rangeMin", true),
						new Parameter("DOUBLE", "rangeMax", true)
				),
				"DOUBLE"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				double rangeMin = ArgumentsHelper.getDouble(arguments.get(0));
				double rangeMax = ArgumentsHelper.getDouble(arguments.get(1));

				double value = GET();

				if(value < rangeMin) {
					value = rangeMin;
				} else if(value > rangeMax) {
					value = rangeMax;
				}

				set(value);
				return DoubleVariable.this;
			}
		});
		this.setMethod("COSINUS", new Method(
				List.of(
						new Parameter("DOUBLE", "angle", true)
				),
				"DOUBLE"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				double angle = ArgumentsHelper.getDouble(arguments.get(0));

				double cosRadians = Math.cos(Math.toRadians(angle));

				set(cosRadians);
				return DoubleVariable.this;
			}
		});
		this.setMethod("DIV", new Method(
				List.of(
						new Parameter("DOUBLE", "divisor", true)
				),
				"DOUBLE"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				double divisor = ArgumentsHelper.getDouble(arguments.get(0));
				try {
					set(GET() / divisor);
				} catch(ArithmeticException e) {
					set(0.0); // divide by zero normally crashes engine
				}
				return DoubleVariable.this;
			}
		});
		this.setMethod("GET", new Method(
				"BOOL"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				return DoubleVariable.this;
			}
		});
		this.setMethod("LENGTH", new Method(
				List.of(
						new Parameter("DOUBLE", "x", true),
						new Parameter("DOUBLE", "y", true)
				),
				"DOUBLE"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				double x = ArgumentsHelper.getDouble(arguments.get(0));
				double y = ArgumentsHelper.getDouble(arguments.get(1));
				set(Math.sqrt(x * x + y * y));
				return DoubleVariable.this;
			}
		});
		this.setMethod("MAXA", new Method(
				List.of(
						new Parameter("DOUBLE", "value1...valueN", true)
				),
				"DOUBLE"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				double max = Integer.MIN_VALUE;
				for(Object argument : arguments) {
					double value = ArgumentsHelper.getDouble(argument);
					if(value > max) {
						max = value;
					}
				}

				set(max);
				return DoubleVariable.this;
			}
		});
		this.setMethod("MINA", new Method(
				List.of(
						new Parameter("DOUBLE", "value1...valueN", true)
				),
				"DOUBLE"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				double min = Integer.MAX_VALUE;
				for(Object argument : arguments) {
					double value = ArgumentsHelper.getDouble(argument);
					if(value < min) {
						min = value;
					}
				}

				set(min);
				return DoubleVariable.this;
			}
		});
		this.setMethod("MUL", new Method(
				List.of(
						new Parameter("DOUBLE", "multiplier", true)
				),
				"DOUBLE"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				double multiplier = ArgumentsHelper.getDouble(arguments.get(0));
				set(GET() * multiplier);
				return DoubleVariable.this;
			}
		});
		this.setMethod("RESETINI", new Method(
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				if(getAttribute("DEFAULT") != null) {
					set(getAttribute("DEFAULT").getValue());
				}
				else if(getAttribute("INIT_VALUE") != null) {
					set(getAttribute("INIT_VALUE").getValue());
				}
				else {
					set(0.0);
				}
				return null;
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
				set(ArgumentsHelper.getDouble(arguments.get(0)));
				return DoubleVariable.this;
			}
		});
		this.setMethod("SINUS", new Method(
				List.of(
						new Parameter("DOUBLE", "angle", true)
				),
				"DOUBLE"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				double angle = ArgumentsHelper.getDouble(arguments.get(0));

				double sinRadians = Math.sin(Math.toRadians(angle));

				set(sinRadians);
				return DoubleVariable.this;
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
				double value = ArgumentsHelper.getDouble(arguments.get(0));
				set(Math.sqrt(value));
				return DoubleVariable.this;
			}
		});
		this.setMethod("SUB", new Method(
				List.of(
						new Parameter("DOUBLE", "doubleValue", true)
				),
				"DOUBLE"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				double value = ArgumentsHelper.getDouble(arguments.get(0));
				set(GET() - value);
				return DoubleVariable.this;
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
		double value = this.GET();
		if (value > 0) {
			return (int) Math.round(value);
		} else {
			return (int) Math.ceil(value - 0.5); // liczby ujemne zaokrągla "w dół"? Zamiast -0.5 == 0 robi -1
		}
	}

	public boolean toBool() {
		return this.GET() != 0;
	}

	public String toStringVariable() {
		if(this.GET() == 0) {
			return "0";
		}
		NumberFormat formatter = new DecimalFormat("#0.00000");
		return formatter.format(this.getValue()).replace(",", ".");
	}

	public double clipToBool() {
		return this.GET() != 0 ? 1 : 0;
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
		try {
			return (double) this.getValue();
		} catch (ClassCastException e) {
			try {
				return Double.parseDouble(String.valueOf(this.getValue()));
			} catch (NumberFormatException e2) {
				return 0.0;
			}
		}
	}

	@Override
	public String toString() {
		return this.toStringVariable();
	}
}
