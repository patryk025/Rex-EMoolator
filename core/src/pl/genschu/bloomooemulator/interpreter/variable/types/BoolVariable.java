package pl.genschu.bloomooemulator.interpreter.variable.types;

import pl.genschu.bloomooemulator.interpreter.exceptions.ClassMethodNotImplementedException;
import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.variable.Attribute;
import pl.genschu.bloomooemulator.interpreter.variable.Method;
import pl.genschu.bloomooemulator.interpreter.variable.Parameter;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;

import java.util.List;

public class BoolVariable extends Variable {
	public BoolVariable(String name, int value, Context context) {
		this(name, value != 0, context);
	}

	public BoolVariable(String name, boolean value, Context context) {
		super(name, context);
		this.setAttribute("VALUE", new Attribute("BOOL", value));

		this.setMethod("SET", new Method(
			List.of(
				new Parameter("BOOL", "value", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				Object value = ((Variable) arguments.get(0)).getValue();
				getAttribute("VALUE").setValue(value);
				return null;
			}
		});
		this.setMethod("SWITCH", new Method(
			List.of(
				new Parameter("BOOL", "value1", true),
				new Parameter("BOOL", "value2", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method SWITCH is not implemented yet");
			}
		});
	}

	@Override
	public String getType() {
		return "BOOL";
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
		return (boolean) this.getValue() ? 1 : 0;
	}

	public double toDouble() {
		return (boolean) this.getValue() ? 1.0 : 0.0;
	}

	public String toStringVariable() {
		return (boolean) this.getValue() ? "TRUE" : "FALSE";
	}

	public Variable convert(String type) {
		if(type.equals("INTEGER")) {
			return new IntegerVariable(this.getName(), this.toInt(), this.context);
		}
		else if(type.equals("DOUBLE")) {
			return new DoubleVariable(this.getName(), this.toDouble(), this.context);
		}
		else if(type.equals("STRING")) {
			return new StringVariable(this.getName(), this.toStringVariable(), this.context);
		}
		else {
			return this;
		}
	}

	public boolean GET() {
		return (boolean) this.getValue();
	}

	@Override
	public String toString() {
		return this.toStringVariable();
	}
}
