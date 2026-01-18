package pl.genschu.bloomooemulator.interpreter.v1.variable.types;

import pl.genschu.bloomooemulator.interpreter.v1.Context;
import pl.genschu.bloomooemulator.interpreter.v1.variable.Attribute;
import pl.genschu.bloomooemulator.interpreter.v1.variable.Method;
import pl.genschu.bloomooemulator.interpreter.v1.variable.Parameter;
import pl.genschu.bloomooemulator.interpreter.v1.variable.Variable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

import pl.genschu.bloomooemulator.utils.LegacyArgumentsHelper;

public class IntegerVariable extends Variable {
	private static final Map<String, List<Method>> METHOD_TEMPLATES = createMethodTemplates();

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
		this.methods = METHOD_TEMPLATES;
	}

	private static Map<String, List<Method>> createMethodTemplates() {
		Map<String, List<Method>> methods = newTemplateMap(baseMethodTemplates());

		addMethodTemplate(methods, "ABS", new Method(
				List.of(
						new Parameter("INTEGER", "value", true)
				),
				"INTEGER"
		) {
			@Override
			public Variable execute(Variable self, List<Object> arguments) {
				IntegerVariable selfVar = (IntegerVariable) self;
				int value = LegacyArgumentsHelper.getInteger(arguments.get(0));
				int result = Math.abs(value);
				selfVar.set(result);
				return selfVar;
			}
		});
		addMethodTemplate(methods, "ADD", new Method(
				List.of(
						new Parameter("INTEGER", "addend", true)
				),
				"INTEGER"
		) {
			@Override
			public Variable execute(Variable self, List<Object> arguments) {
				IntegerVariable selfVar = (IntegerVariable) self;
				int result = selfVar.GET() + LegacyArgumentsHelper.getInteger(arguments.get(0));
				selfVar.set(result);
				return selfVar;
			}
		});
		addMethodTemplate(methods, "AND", new Method(
				List.of(
						new Parameter("INTEGER", "value", true)
				),
				"INTEGER"
		) {
			@Override
			public Variable execute(Variable self, List<Object> arguments) {
				IntegerVariable selfVar = (IntegerVariable) self;
				int result = selfVar.GET() & LegacyArgumentsHelper.getInteger(arguments.get(0));
				selfVar.set(result);
				return selfVar;
			}
		});
		addMethodTemplate(methods, "CLAMP", new Method(
				List.of(
						new Parameter("INTEGER", "rangeMin", true),
						new Parameter("INTEGER", "rangeMax", true)
				),
				"INTEGER"
		) {
			@Override
			public Variable execute(Variable self, List<Object> arguments) {
				IntegerVariable selfVar = (IntegerVariable) self;
				int currentValue = selfVar.GET();
				int rangeMin = LegacyArgumentsHelper.getInteger(arguments.get(0));
				int rangeMax = LegacyArgumentsHelper.getInteger(arguments.get(1));

				if (currentValue < rangeMin) {
					currentValue = rangeMin;
				} else if (currentValue > rangeMax) {
					currentValue = rangeMax;
				}

				selfVar.set(currentValue);
				return selfVar;
			}
		});
		addMethodTemplate(methods, "CLEAR", new Method(
				List.of(),
				"DOUBLE"
		) {
			@Override
			public Variable execute(Variable self, List<Object> arguments) {
				IntegerVariable selfVar = (IntegerVariable) self;
				selfVar.set(0);
				return selfVar;
			}
		});
		addMethodTemplate(methods, "DEC", new Method(
				"void"
		) {
			@Override
			public Variable execute(Variable self, List<Object> arguments) {
				IntegerVariable selfVar = (IntegerVariable) self;
				int result = selfVar.GET() - 1;
				selfVar.set(result);
				return selfVar;
			}
		});
		addMethodTemplate(methods, "DIV", new Method(
				List.of(
						new Parameter("INTEGER", "divisor", true)
				),
				"INTEGER"
		) {
			@Override
			public Variable execute(Variable self, List<Object> arguments) {
				IntegerVariable selfVar = (IntegerVariable) self;
				try {
					int result = selfVar.GET() / LegacyArgumentsHelper.getInteger(arguments.get(0));
					selfVar.set(result);
				} catch (ArithmeticException e) {
					selfVar.set(0); // division by zero normally crashes engine
				}
				return selfVar;
			}
		});
		addMethodTemplate(methods, "GET", new Method(
				"BOOL"
		) {
			@Override
			public Variable execute(Variable self, List<Object> arguments) {
				return (IntegerVariable) self;
			}
		});
		addMethodTemplate(methods, "INC", new Method(
				"void"
		) {
			@Override
			public Variable execute(Variable self, List<Object> arguments) {
				IntegerVariable selfVar = (IntegerVariable) self;
				int result = selfVar.GET() + 1;
				selfVar.set(result);
				return selfVar;
			}
		});
		addMethodTemplate(methods, "LENGTH", new Method(
				List.of(
						new Parameter("INTEGER", "x", true),
						new Parameter("INTEGER", "y", true)
				),
				"INTEGER"
		) {
			@Override
			public Variable execute(Variable self, List<Object> arguments) {
				IntegerVariable selfVar = (IntegerVariable) self;
				int x = LegacyArgumentsHelper.getInteger(arguments.get(0));
				int y = LegacyArgumentsHelper.getInteger(arguments.get(1));

				int result = (int) Math.sqrt(x * x + y * y);
				selfVar.set(result);
				return selfVar;
			}
		});
		addMethodTemplate(methods, "MOD", new Method(
				List.of(
						new Parameter("INTEGER", "divisor", true)
				),
				"INTEGER"
		) {
			@Override
			public Variable execute(Variable self, List<Object> arguments) {
				IntegerVariable selfVar = (IntegerVariable) self;
				int result = selfVar.GET() % (LegacyArgumentsHelper.getInteger(arguments.get(0)));
				selfVar.set(result);
				return selfVar;
			}
		});
		addMethodTemplate(methods, "MUL", new Method(
				List.of(
						new Parameter("INTEGER", "multiplier", true)
				),
				"INTEGER"
		) {
			@Override
			public Variable execute(Variable self, List<Object> arguments) {
				IntegerVariable selfVar = (IntegerVariable) self;
				int result = selfVar.GET() * LegacyArgumentsHelper.getInteger(arguments.get(0));
				selfVar.set(result);
				return selfVar;
			}
		});
		addMethodTemplate(methods, "NOT", new Method(
				List.of(),
				"INTEGER"
		) {
			@Override
			public Variable execute(Variable self, List<Object> arguments) {
				IntegerVariable selfVar = (IntegerVariable) self;
				int result = ~selfVar.GET();
				selfVar.set(result);
				return selfVar;
			}
		});
		addMethodTemplate(methods, "OR", new Method(
				List.of(
						new Parameter("INTEGER", "value", true)
				),
				"INTEGER"
		) {
			@Override
			public Variable execute(Variable self, List<Object> arguments) {
				IntegerVariable selfVar = (IntegerVariable) self;
				int result = selfVar.GET() | LegacyArgumentsHelper.getInteger(arguments.get(0));
				selfVar.set(result);
				return selfVar;
			}
		});
		addMethodTemplate(methods, "POWER", new Method(
				List.of(
						new Parameter("INTEGER", "power", true)
				),
				"INTEGER"
		) {
			@Override
			public Variable execute(Variable self, List<Object> arguments) {
				IntegerVariable selfVar = (IntegerVariable) self;
				double result = Math.pow(selfVar.GET(), LegacyArgumentsHelper.getInteger(arguments.get(0)));
				if (result > 0) {
					selfVar.set((int) Math.round(result));
				} else {
					selfVar.set((int) Math.ceil(result - 0.5));
				}
				return selfVar;
			}
		});
		addMethodTemplate(methods, "RANDOM", new Method(
				List.of(
						new Parameter("INTEGER", "param1", true),
						new Parameter("INTEGER", "param2", false)
				),
				"INTEGER"
		) {
			@Override
			public Variable execute(Variable self, List<Object> arguments) {
				IntegerVariable selfVar = (IntegerVariable) self;
				if(arguments.size() == 1) {
					Random random = new Random();
					int bound = LegacyArgumentsHelper.getInteger(arguments.get(0));
					int result = random.nextInt(bound);
					selfVar.set(result);
					return selfVar;
				} else {
					int min = LegacyArgumentsHelper.getInteger(arguments.get(0));
					int max = LegacyArgumentsHelper.getInteger(arguments.get(1));
					Random random = new Random();
					int result = min + random.nextInt(max - min + 1);
					selfVar.set(result);
					return selfVar;
				}
			}
		});
		addMethodTemplate(methods, "RESETINI", new Method(
				"void"
		) {
			@Override
			public Variable execute(Variable self, List<Object> arguments) {
				IntegerVariable selfVar = (IntegerVariable) self;
				if(selfVar.getAttribute("DEFAULT") != null) {
					selfVar.set(selfVar.getAttribute("DEFAULT").getValue());
				}
				else if(selfVar.getAttribute("INIT_VALUE") != null) {
					selfVar.set(selfVar.getAttribute("INIT_VALUE").getValue());
				}
				else {
					selfVar.set(0);
				}
				return null;
			}
		});
		addMethodTemplate(methods, "SET", new Method(
				List.of(
						new Parameter("INTEGER", "value", true)
				),
				"void"
		) {
			@Override
			public Variable execute(Variable self, List<Object> arguments) {
				IntegerVariable selfVar = (IntegerVariable) self;
				int value = LegacyArgumentsHelper.getInteger(arguments.get(0));
				selfVar.set(value);
				return null;
			}
		});
		addMethodTemplate(methods, "SUB", new Method(
				List.of(
						new Parameter("INTEGER", "subtrahend", true)
				),
				"INTEGER"
		) {
			@Override
			public Variable execute(Variable self, List<Object> arguments) {
				IntegerVariable selfVar = (IntegerVariable) self;
				int result = selfVar.GET() - LegacyArgumentsHelper.getInteger(arguments.get(0));
				selfVar.set(result);
				return selfVar;
			}
		});
		addMethodTemplate(methods, "SWITCH", new Method(
				List.of(
						new Parameter("INTEGER", "val1", true),
						new Parameter("INTEGER", "val1", true)
				),
				"INTEGER"
		) {
			@Override
			public Variable execute(Variable self, List<Object> arguments) {
				IntegerVariable selfVar = (IntegerVariable) self;
				int val1 = LegacyArgumentsHelper.getInteger(arguments.get(0));
				int val2 = LegacyArgumentsHelper.getInteger(arguments.get(1));

				if(selfVar.GET() == val1) {
					selfVar.set(val2);
				} else {
					selfVar.set(val1);
				}
				return selfVar;
			}
		});
		addMethodTemplate(methods, "XOR", new Method(
				List.of(
						new Parameter("INTEGER", "value", true)
				),
				"INTEGER"
		) {
			@Override
			public Variable execute(Variable self, List<Object> arguments) {
				IntegerVariable selfVar = (IntegerVariable) self;
				int result = selfVar.GET() ^ LegacyArgumentsHelper.getInteger(arguments.get(0));
				selfVar.set(result);
				return selfVar;
			}
		});

		return Collections.unmodifiableMap(methods);
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
			return new DoubleVariable(this.getName(), this.toDouble(), this.getContext());
		}
		else if(type.equals("BOOL")) {
			return new BoolVariable(this.getName(), this.toBool(), this.getContext());
		}
		else if(type.equals("STRING")) {
			return new StringVariable(this.getName(), this.toStringVariable(), this.getContext());
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
