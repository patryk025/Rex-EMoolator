package pl.genschu.bloomooemulator.interpreter.variable.types;

import pl.genschu.bloomooemulator.interpreter.exceptions.ClassMethodNotImplementedException;
import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.factories.VariableFactory;
import pl.genschu.bloomooemulator.interpreter.variable.Attribute;
import pl.genschu.bloomooemulator.interpreter.variable.Method;
import pl.genschu.bloomooemulator.interpreter.variable.Parameter;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;

import java.util.List;

public class StringVariable extends Variable {
	public StringVariable(String name, String value, final Context context) {
		super(name, context);
		this.setAttribute("VALUE", new Attribute("STRING", value));

		this.setMethod("ADD", new Method(
			List.of(
				new Parameter("STRING", "stringValue", true)
			),
			"STRING"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				String value = getAttribute("VALUE").getValue().toString();
				setAttribute("VALUE", new Attribute("STRING", value + arguments.get(0)));
				return StringVariable.this;
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
			public Variable execute(List<Object> arguments) {
				//TODO: do sprawdzenia
				String value = getAttribute("VALUE").getValue().toString();
				int index = (int) arguments.get(0);
				value = value.substring(0, index) + arguments.get(1) + value.substring(index + 1);
				setAttribute("VALUE", new Attribute("STRING", value));
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
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method COPYFILE is not implemented yet");
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
			public Variable execute(List<Object> arguments) {
				String value = getAttribute("VALUE").getValue().toString();
				int index = (int) arguments.get(0);
				int length = (int) arguments.get(1);
				value = value.substring(index, index + length);
				setAttribute("VALUE", new Attribute("STRING", value));
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
			public Variable execute(List<Object> arguments) {
				String value = getAttribute("VALUE").getValue().toString();
				Variable needle = (Variable) arguments.get(0);
				Variable offset = VariableFactory.createVariable("INTEGER", "0", context);
				if (arguments.size() > 1 && arguments.get(1) != null) {
					offset = (Variable) arguments.get(1);
				}
				int offsetValue = (int) offset.getValue();
				int index = value.indexOf(needle.getValue().toString(), offsetValue);
				return VariableFactory.createVariable("INTEGER", String.valueOf(index), context);
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
			public Variable execute(List<Object> arguments) {
				String value = getAttribute("VALUE").getValue().toString();
				Variable index = (Variable) arguments.get(0);
				Variable length = VariableFactory.createVariable("INTEGER", String.valueOf(value.length()), context);
				if (arguments.size() > 1 && arguments.get(1) != null) {
					length = (Variable) arguments.get(1);
				}
				int startIndex = (int) index.getValue();
				int endIndex = Math.min(startIndex + (int) length.getValue(), value.length());
				return VariableFactory.createVariable("STRING", value.substring(startIndex, endIndex), context);
			}
		});
		this.setMethod("LENGTH", new Method(
			"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				String value = getAttribute("VALUE").getValue().toString();
				return VariableFactory.createVariable("INTEGER", String.valueOf(value.length()), context);
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
			public Variable execute(List<Object> arguments) {
				String value = getAttribute("VALUE").getValue().toString();

				Variable indexVar = (Variable) arguments.get(0);
				Variable stringVar = (Variable) arguments.get(1);

				int index = (int) indexVar.getValue();
				String stringValue = stringVar.toString();

				String newValue;
				if (index < 0 || index > value.length()) {
					throw new IndexOutOfBoundsException("Index out of bounds: " + index);
				}

				if (index + stringValue.length() > value.length()) {
					newValue = value.substring(0, index) + stringValue;
				} else {
					newValue = value.substring(0, index) + stringValue + value.substring(index + stringValue.length());
				}

				setAttribute("VALUE", new Attribute("STRING", newValue));
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
			public Variable execute(List<Object> arguments) {
				Variable value = (Variable) arguments.get(0);
				setAttribute("VALUE", new Attribute("STRING", value.getValue()));
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
			public Variable execute(List<Object> arguments) {
				String value = getAttribute("VALUE").getValue().toString();
				int index = (int) arguments.get(0);
				int length = (int) arguments.get(1);

				String newValue = value.substring(0, index) + value.substring(index + length);
				setAttribute("VALUE", new Attribute("STRING", newValue));
				return null;
			}
		});
		this.setMethod("UPPER", new Method(
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				String value = getAttribute("VALUE").getValue().toString();
				setAttribute("VALUE", new Attribute("STRING", value.toUpperCase()));
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

	public Attribute getAttribute(String name) {
		List<String> knownAttributes = List.of("TOINI", "VALUE");
		if(knownAttributes.contains(name)) {
			return super.getAttribute(name);
		}
		return null;
	}

	public int toInt() {
		return toInt(false);
	}

	public int toInt(boolean clipToBool) {
		try {
			if(clipToBool) {
				return Integer.parseInt((String) this.getValue()) != 0 ? 1 : 0;
			}
			return Integer.parseInt((String) this.getValue());
		}
		catch(NumberFormatException e) {
			return 0;
		}
	}

	public double toDouble() {
		return toDouble(false);
	}

	public double toDouble(boolean clipToBool) {
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

	@Override
	public String toString() {
		return (String) this.getValue();
	}
}
