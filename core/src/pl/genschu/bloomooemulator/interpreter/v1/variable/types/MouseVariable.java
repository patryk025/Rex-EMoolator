package pl.genschu.bloomooemulator.interpreter.v1.variable.types;

import pl.genschu.bloomooemulator.interpreter.v1.Context;
import pl.genschu.bloomooemulator.interpreter.v1.variable.*;
import pl.genschu.bloomooemulator.utils.LegacyArgumentsHelper;

import java.util.List;

public class MouseVariable extends GlobalVariable {
	private boolean isEnabled = true;
	private int posX;
	private int posY;
	private boolean emitSignals = true;

	public int getPosX() { return posX; }
	public int getPosY() { return posY; }

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
			public Variable execute(Variable self, List<Object> arguments) {
				MouseVariable selfVar = (MouseVariable) self;
				selfVar.setEnabled(false);
				return null;
			}
		});
		this.setMethod("DISABLESIGNAL", new Method(
				"void"
		) {
			@Override
			public Variable execute(Variable self, List<Object> arguments) {
				MouseVariable selfVar = (MouseVariable) self;
				selfVar.setEmitSignals(false);
				return null;
			}
		});
		this.setMethod("ENABLE", new Method(
				"void"
		) {
			@Override
			public Variable execute(Variable self, List<Object> arguments) {
				MouseVariable selfVar = (MouseVariable) self;
				selfVar.setEnabled(true);
				return null;
			}
		});
		this.setMethod("ENABLESIGNAL", new Method(
				"void"
		) {
			@Override
			public Variable execute(Variable self, List<Object> arguments) {
				MouseVariable selfVar = (MouseVariable) self;
				selfVar.setEmitSignals(true);
				return null;
			}
		});
		this.setMethod("GETPOSX", new Method(
				"void"
		) {
			@Override
			public Variable execute(Variable self, List<Object> arguments) {
				MouseVariable selfVar = (MouseVariable) self;
				return new IntegerVariable("", selfVar.getPosX(), selfVar.getContext());
			}
		});
		this.setMethod("GETPOSY", new Method(
				"void"
		) {
			@Override
			public Variable execute(Variable self, List<Object> arguments) {
				MouseVariable selfVar = (MouseVariable) self;
				return new IntegerVariable("", selfVar.getPosY(), selfVar.getContext());
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
			public Variable execute(Variable self, List<Object> arguments) {
				MouseVariable selfVar = (MouseVariable) self;
				int posX = LegacyArgumentsHelper.getInteger(arguments.get(0));
				int posY = LegacyArgumentsHelper.getInteger(arguments.get(1));

				posX = Math.max(0, Math.min(800, posX));
				posY = Math.max(0, Math.min(600, posY));

				selfVar.update(posX, posY);
				return null;
			}
		});
		this.setMethod("SHOW", new Method(
				"void"
		) {
			@Override
			public Variable execute(Variable self, List<Object> arguments) {
				MouseVariable selfVar = (MouseVariable) self;
				selfVar.getContext().getGame().getInputManager().setMouseVisible(true);
				return null;
			}
		});
		this.setMethod("HIDE", new Method(
				"void"
		) {
			@Override
			public Variable execute(Variable self, List<Object> arguments) {
				MouseVariable selfVar = (MouseVariable) self;
				selfVar.getContext().getGame().getInputManager().setMouseVisible(false);
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

	public boolean isEmitSignals() {
		return emitSignals;
	}

	public void setEmitSignals(boolean emitSignals) {
		this.emitSignals = emitSignals;
	}
}
