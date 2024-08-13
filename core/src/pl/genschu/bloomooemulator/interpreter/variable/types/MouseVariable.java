package pl.genschu.bloomooemulator.interpreter.variable.types;

import pl.genschu.bloomooemulator.interpreter.exceptions.ClassMethodNotImplementedException;
import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.variable.Attribute;
import pl.genschu.bloomooemulator.interpreter.variable.Method;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;

import java.util.List;

public class MouseVariable extends Variable {
	private boolean isEnabled = false;
	private int posX;
	private int posY;

	public MouseVariable(String name, Context context) {
		super(name, context);

		this.setMethod("DISABLE", new Method(
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				isEnabled = false;
				return null;
			}
		});
		this.setMethod("ENABLE", new Method(
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				isEnabled = true;
				return null;
			}
		});
		this.setMethod("GETPOSX", new Method(
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				return new IntegerVariable("", posX, context);
			}
		});
		this.setMethod("GETPOSY", new Method(
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				return new IntegerVariable("", posY, context);
			}
		});
	}

	@Override
	public String getType() {
		return "MOUSE";
	}

	@Override
	public void setAttribute(String name, Attribute attribute) {
		List<String> knownAttributes = List.of("RAW");
		if(knownAttributes.contains(name)) {
			super.setAttribute(name, attribute);
		}
	}

	public void update(int x, int y) {
		this.posX = x;
		this.posY = y;
	}
}
