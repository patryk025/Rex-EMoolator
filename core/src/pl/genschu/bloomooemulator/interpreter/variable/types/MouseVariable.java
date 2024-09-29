package pl.genschu.bloomooemulator.interpreter.variable.types;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Cursor.SystemCursor;
import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.variable.Attribute;
import pl.genschu.bloomooemulator.interpreter.variable.Method;
import pl.genschu.bloomooemulator.interpreter.variable.Parameter;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.utils.ArgumentsHelper;

import java.util.List;

public class MouseVariable extends Variable {
	private boolean isEnabled = true;
	private int posX;
	private int posY;
	private boolean isVisible = true;
	private boolean emitSignals = true;

	public MouseVariable(String name, Context context) {
		super(name, context);
	}

	@Override
	public String getType() {
		return "MOUSE";
	}

	@Override
	protected void setMethods() {
		super.setMethods();

		this.setMethod("DISABLE", new Method(
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				for(MouseVariable mouseVariable : context.getGame().getCurrentSceneContext().getMouseVariables()) {
					mouseVariable.setEnabled(false);
				}
				return null;
			}
		});
		this.setMethod("DISABLESIGNAL", new Method(
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				for(MouseVariable mouseVariable : context.getGame().getCurrentSceneContext().getMouseVariables()) {
					mouseVariable.setEmitSignals(false);
				}
				return null;
			}
		});
		this.setMethod("ENABLE", new Method(
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				for(MouseVariable mouseVariable : context.getGame().getCurrentSceneContext().getMouseVariables()) {
					mouseVariable.setEnabled(true);
				}
				return null;
			}
		});
		this.setMethod("ENABLESIGNAL", new Method(
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				for(MouseVariable mouseVariable : context.getGame().getCurrentSceneContext().getMouseVariables()) {
					mouseVariable.setEmitSignals(true);
				}
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
		this.setMethod("SETPOSITION", new Method(
				List.of(
						new Parameter("INTEGER", "posX", true),
						new Parameter("INTEGER", "posY", true)
				),
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				int posX = ArgumentsHelper.getInteger(arguments.get(0));
				int posY = ArgumentsHelper.getInteger(arguments.get(1));

				posX = Math.max(0, Math.min(800, posX));
				posY = Math.max(0, Math.min(600, posY));

				for(MouseVariable mouseVariable : context.getGame().getCurrentSceneContext().getMouseVariables()) {
					mouseVariable.update(posX, posY);
				}
				return null;
			}
		});
		this.setMethod("SHOW", new Method(
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				Gdx.graphics.setSystemCursor(SystemCursor.Arrow);
				for(MouseVariable mouseVariable : context.getGame().getCurrentSceneContext().getMouseVariables()) {
					mouseVariable.setVisible(true);
				}
				return null;
			}
		});
		this.setMethod("HIDE", new Method(
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				Gdx.graphics.setSystemCursor(SystemCursor.None);
				for(MouseVariable mouseVariable : context.getGame().getCurrentSceneContext().getMouseVariables()) {
					mouseVariable.setVisible(false);
				}
				return null;
			}
		});
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

	public boolean isVisible() {
		return isVisible;
	}

	public void setVisible(boolean visible) {
		isVisible = visible;
	}

	public boolean isEmitSignals() {
		return emitSignals;
	}

	public void setEmitSignals(boolean emitSignals) {
		this.emitSignals = emitSignals;
	}
}
