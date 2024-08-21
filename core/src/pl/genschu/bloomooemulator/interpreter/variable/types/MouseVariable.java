package pl.genschu.bloomooemulator.interpreter.variable.types;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Cursor.SystemCursor;
import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.variable.Attribute;
import pl.genschu.bloomooemulator.interpreter.variable.Method;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;

import java.util.List;

public class MouseVariable extends Variable {
	private boolean isEnabled = true;
	private int posX;
	private int posY;

	public MouseVariable(String name, Context context) {
		super(name, context);

		this.setMethod("DISABLE", new Method(
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments, Variable variable) {
				isEnabled = false;
				return null;
			}
		});
		this.setMethod("ENABLE", new Method(
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments, Variable variable) {
				isEnabled = true;
				return null;
			}
		});
		this.setMethod("GETPOSX", new Method(
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments, Variable variable) {
				return new IntegerVariable("", posX, context);
			}
		});
		this.setMethod("GETPOSY", new Method(
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments, Variable variable) {
				return new IntegerVariable("", posY, context);
			}
		});
		this.setMethod("SHOW", new Method(
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments, Variable variable) {
				Gdx.graphics.setSystemCursor(SystemCursor.Arrow);
				return null;
			}
		});
		this.setMethod("HIDE", new Method(
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments, Variable variable) {
				Gdx.graphics.setSystemCursor(SystemCursor.None);
				return null;
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

	public boolean isEnabled() {
		return isEnabled;
	}

	public void setEnabled(boolean enabled) {
		isEnabled = enabled;
	}

	public void update(int x, int y) {
		if(x != posX || y != posY) {
			emitSignal("ONMOVE");
		}
		this.posX = x;
		this.posY = y;
	}
}
