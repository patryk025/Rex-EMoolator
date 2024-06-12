package pl.cba.genszu.amcodetranslator.interpreter.variable.types;

import pl.cba.genszu.amcodetranslator.interpreter.Context;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Attribute;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Method;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Parameter;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Variable;

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
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				return null;
			}
		});
		this.setMethod("ADD", new Method(
			List.of(
				new Parameter("INTEGER|DOUBLE", "doubleValue", true)
			),
			"DOUBLE"
		) {
			@Override
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
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
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				return null;
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
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				return null;
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
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				return null;
			}
		});
		this.setMethod("COSINUS", new Method(
			List.of(
				new Parameter("INTEGER|DOUBLE", "doubleValue", true)
			),
			"DOUBLE"
		) {
			@Override
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				return null;
			}
		});
		this.setMethod("DIV", new Method(
			List.of(
				new Parameter("INTEGER|DOUBLE", "divisor", true)
			),
			"DOUBLE"
		) {
			@Override
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				return null;
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
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				return null;
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
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				return null;
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
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				return null;
			}
		});
		this.setMethod("MUL", new Method(
			List.of(
				new Parameter("INTEGER|DOUBLE", "multiplier", true)
			),
			"DOUBLE"
		) {
			@Override
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
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
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				return null;
			}
		});
		this.setMethod("SINUS", new Method(
			List.of(
				new Parameter("INTEGER|DOUBLE", "doubleValue", true)
			),
			"DOUBLE"
		) {
			@Override
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				return null;
			}
		});
		this.setMethod("SQRT", new Method(
			List.of(
				new Parameter("DOUBLE", "doubleValue", true)
			),
			"DOUBLE"
		) {
			@Override
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				return null;
			}
		});
		this.setMethod("SUB", new Method(
			List.of(
				new Parameter("INTEGER|DOUBLE", "doubleValue", true)
			),
			"DOUBLE"
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
		return (int) this.getValue();
	}

	public boolean toBool() {
		return (double) this.getValue() != 0;
	}

	public String toStringVariable() {
		return String.valueOf(this.getValue());
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

}
