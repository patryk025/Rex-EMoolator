package pl.genschu.bloomooemulator.interpreter.variable.types;

import pl.genschu.bloomooemulator.interpreter.exceptions.ClassMethodNotImplementedException;
import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.variable.Attribute;
import pl.genschu.bloomooemulator.interpreter.variable.Method;
import pl.genschu.bloomooemulator.interpreter.variable.Parameter;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.utils.ArgumentsHelper;

import java.util.List;

public class BoolVariable extends Variable {
	public BoolVariable(String name, int value, Context context) {
		this(name, value != 0, context);
	}

	public BoolVariable(String name, boolean value, Context context) {
		super(name, context);
		this.setAttribute("VALUE", new Attribute("BOOL", value));
		super.setAttribute("INIT_VALUE", new Attribute("BOOL", value));
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
	protected void setMethods() {
		super.setMethods();

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
					set(false);
				}
				return null;
			}
		});
		this.setMethod("SET", new Method(
				List.of(
						new Parameter("BOOL", "value", true)
				),
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				Object value = ((Variable) arguments.get(0)).getValue();
				set(value);
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
				boolean value1 = ArgumentsHelper.getBoolean(arguments.get(0));
				boolean value2 = ArgumentsHelper.getBoolean(arguments.get(1));
				if(GET() != value1) {
					set(value2);
				}
				else {
					set(value1);
				}
				return null;
			}
		});
	}

	@Override
	public void setAttribute(String name, Attribute attribute) {
		List<String> knownAttributes = List.of("TOINI", "VALUE");
		if(knownAttributes.contains(name)) {
			super.setAttribute(name, attribute);
		}
	}

	public int toInt() {
		return this.GET() ? 1 : 0;
	}

	public double toDouble() {
		return this.GET() ? 1.0 : 0.0;
	}

	public String toStringVariable() {
		return this.GET() ? "TRUE" : "FALSE";
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
		return this.getValue().toString().equalsIgnoreCase("TRUE");
	}

	@Override
	public String toString() {
		return this.toStringVariable();
	}
}
