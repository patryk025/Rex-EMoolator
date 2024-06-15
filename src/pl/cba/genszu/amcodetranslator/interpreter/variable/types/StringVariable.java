package pl.cba.genszu.amcodetranslator.interpreter.variable.types;

import pl.cba.genszu.amcodetranslator.interpreter.Context;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Attribute;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Method;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Parameter;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Variable;

import java.util.List;

public class StringVariable extends Variable {
	public StringVariable(String name, String value, Context context) {
		super(name, context);
		this.setAttribute("VALUE", new Attribute("STRING", value));

		this.setMethod("ADD", new Method(
			List.of(
				new Parameter("STRING", "stringValue", true)
			),
			"STRING"
		) {
			@Override
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				return null;
			}
		});
		this.setMethod("CHANGEAT", new Method(
			List.of(
				new Parameter("INTEGER", "index", true),
				new Parameter("STRING", "stringValue", true)
			),
			"void"
		) {
			@Override
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				return null;
			}
		});
		this.setMethod("COPYFILE", new Method(
			List.of(
				new Parameter("STRING", "source", true),
				new Parameter("STRING", "destination", true)
			),
			"BOOL"
		) {
			@Override
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				return null;
			}
		});
		this.setMethod("CUT", new Method(
			List.of(
				new Parameter("INTEGER", "index", true),
				new Parameter("INTEGER", "length", true)
			),
			"void"
		) {
			@Override
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				return null;
			}
		});
		this.setMethod("FIND", new Method(
			List.of(
				new Parameter("STRING", "needle", true),
				new Parameter("INTEGER", "offset", false)
			),
			"INTEGER"
		) {
			@Override
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				return null;
			}
		});
		this.setMethod("GET", new Method(
			List.of(
				new Parameter("INTEGER", "index", true),
				new Parameter("INTEGER", "length", false)
			),
			"STRING"
		) {
			@Override
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				return null;
			}
		});
		this.setMethod("LENGTH", new Method(
			"INTEGER"
		) {
			@Override
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				return null;
			}
		});
		this.setMethod("REPLACEAT", new Method(
			List.of(
				new Parameter("INTEGER", "index", true),
				new Parameter("STRING", "stringValue", true)
			),
			"void"
		) {
			@Override
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				return null;
			}
		});
		this.setMethod("SET", new Method(
			List.of(
				new Parameter("STRING", "value", true)
			),
			"void"
		) {
			@Override
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				return null;
			}
		});
		this.setMethod("SUB", new Method(
			List.of(
				new Parameter("INTEGER", "index", true),
				new Parameter("INTEGER", "length", true)
			),
			"void"
		) {
			@Override
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				return null;
			}
		});
		this.setMethod("UPPER", new Method(
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
		return "STRING";
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
		try {
			return Integer.parseInt((String) this.getValue());
		}
		catch(NumberFormatException e) {
			return 0;
		}
	}

	public double toDouble() {
		try {
			return Double.parseDouble((String) this.getValue());
		}
		catch(NumberFormatException e) {
			return 0;
		}
	}

	public boolean toBool() {
		try {
			return Boolean.parseBoolean((String) this.getValue());
		}
		catch(NumberFormatException e) {
			return false;
		}
	}

	public Variable convert(String type) {
		if(type.equals("DOUBLE")) {
			return new DoubleVariable(this.getName(), this.toDouble(), this.context);
		}
		else if(type.equals("BOOL")) {
			return new BoolVariable(this.getName(), this.toBool(), this.context);
		}
		else if(type.equals("INTEGER")) {
			return new IntegerVariable(this.getName(), this.toInt(), this.context);
		}
		else {
			return this;
		}
	}

	public String GET() {
		return (String) this.getValue();
	}

}
