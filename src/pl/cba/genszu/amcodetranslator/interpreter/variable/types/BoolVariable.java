package pl.cba.genszu.amcodetranslator.interpreter.variable.types;

import pl.cba.genszu.amcodetranslator.interpreter.Context;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Attribute;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Method;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Parameter;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Variable;

import java.util.List;

public class BoolVariable extends Variable {
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
				// TODO: implement this method
				System.out.println("Method SET is not implemented yet");
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
				System.out.println("Method SWITCH is not implemented yet");
				return null;
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
