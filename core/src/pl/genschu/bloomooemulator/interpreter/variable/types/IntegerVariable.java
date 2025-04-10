package pl.genschu.bloomooemulator.interpreter.variable.types;

import pl.genschu.bloomooemulator.interpreter.exceptions.ClassMethodNotImplementedException;
import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.variable.Attribute;
import pl.genschu.bloomooemulator.interpreter.variable.Method;
import pl.genschu.bloomooemulator.interpreter.variable.Parameter;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;

import java.util.List;
import java.util.Random;

import static pl.genschu.bloomooemulator.interpreter.util.VariableHelper.getValueFromString;
import pl.genschu.bloomooemulator.utils.ArgumentsHelper;

public class IntegerVariable extends Variable {
	public IntegerVariable(String name, int value, Context context) {
		super(name, context);
		this.setAttribute("VALUE", new Attribute("INTEGER", value));
		super.setAttribute("INIT_VALUE", new Attribute("INTEGER", value));
	}

	@Override
	public String getType() {
		return "INTEGER";
	}

	@Override
	protected void setMethods() {
		super.setMethods();

		this.setMethod("ABS", new Method(
				List.of(
						new Parameter("INTEGER", "value", true)
				),
				"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				int value = ArgumentsHelper.getInteger(arguments.get(0));
				int result = Math.abs(value);
				set(result);
				return IntegerVariable.this;
			}
		});
		this.setMethod("ADD", new Method(
				List.of(
						new Parameter("INTEGER", "addend", true)
				),
				"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				int result = GET() + ArgumentsHelper.getInteger(arguments.get(0));
				set(result);
				return IntegerVariable.this;
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
				int result = GET() & ArgumentsHelper.getInteger(arguments.get(0));
				set(result);
				return IntegerVariable.this;
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
				int currentValue = GET();
				int rangeMin = ArgumentsHelper.getInteger(arguments.get(0));
				int rangeMax = ArgumentsHelper.getInteger(arguments.get(1));

				if (currentValue < rangeMin) {
					currentValue = rangeMin;
				} else if (currentValue > rangeMax) {
					currentValue = rangeMax;
				}

				set(currentValue);
				return IntegerVariable.this;
			}
		});
		this.setMethod("DEC", new Method(
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				int result = GET() - 1;
				set(result);
				return IntegerVariable.this;
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
				try {
					int result = GET() / ArgumentsHelper.getInteger(arguments.get(0));
					set(result);
				} catch (ArithmeticException e) {
					set(0); // division by zero normally crashes engine
				}
				return IntegerVariable.this;
			}
		});
		this.setMethod("GET", new Method(
				"BOOL"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				return IntegerVariable.this;
			}
		});
		this.setMethod("INC", new Method(
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				int result = GET() + 1;
				set(result);
				return IntegerVariable.this;
			}
		});
		this.setMethod("LENGTH", new Method(
				List.of(
						new Parameter("INTEGER", "x", true),
						new Parameter("INTEGER", "y", true)
				),
				"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				int x = ArgumentsHelper.getInteger(arguments.get(0));
				int y = ArgumentsHelper.getInteger(arguments.get(1));

				int result = (int) Math.sqrt(x * x + y * y);
				set(result);
				return IntegerVariable.this;
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
				int result = GET() % (ArgumentsHelper.getInteger(arguments.get(0)));
				set(result);
				return IntegerVariable.this;
			}
		});
		this.setMethod("MUL", new Method(
				List.of(
						new Parameter("INTEGER", "multiplier", true)
				),
				"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				int result = GET() * ArgumentsHelper.getInteger(arguments.get(0));
				set(result);
				return IntegerVariable.this;
			}
		});
		this.setMethod("RANDOM", new Method(
				List.of(
						new Parameter("INTEGER", "param1", true),
						new Parameter("INTEGER", "param2", false)
				),
				"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				if(arguments.size() == 1) {
					Random random = new Random();
					int bound = ArgumentsHelper.getInteger(arguments.get(0));
					int result = random.nextInt(bound);
					set(result);
					return IntegerVariable.this;
				} else {
					int min = ArgumentsHelper.getInteger(arguments.get(0));
					int max = ArgumentsHelper.getInteger(arguments.get(1));
					Random random = new Random();
					int result = min + random.nextInt(max - min + 1);
					set(result);
					return IntegerVariable.this;
				}
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
					set(0);
				}
				return null;
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
				int value = ArgumentsHelper.getInteger(arguments.get(0));
				set(value);
				return null;
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
				int result = GET() - getValueFromString((Variable) arguments.get(0));
				set(result);
				return IntegerVariable.this;
			}
		});
		this.setMethod("SWITCH", new Method(
				List.of(
						new Parameter("INTEGER", "val1", true),
						new Parameter("INTEGER", "val1", true)
				),
				"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				int val1 = ArgumentsHelper.getInteger(arguments.get(0));
				int val2 = ArgumentsHelper.getInteger(arguments.get(1));

				if(GET() == val1) {
					set(val2);
				} else {
					set(val1);
				}
				return IntegerVariable.this;
			}
		});
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
		return this.GET() != 0;
	}

	public double toDouble() {
		return this.GET();
	}

	public int clipToBool() {
		return this.GET() != 0 ? 1 : 0;
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
			try {
				return Integer.parseInt(String.valueOf(this.getValue()));
			} catch (NumberFormatException e2) {
				return 0;
			}
		}
	}

	@Override
	public String toString() {
		return this.toStringVariable();
	}
}
