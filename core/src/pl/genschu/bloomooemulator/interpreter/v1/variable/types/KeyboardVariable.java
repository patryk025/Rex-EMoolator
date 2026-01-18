package pl.genschu.bloomooemulator.interpreter.v1.variable.types;

import com.badlogic.gdx.Gdx;
import pl.genschu.bloomooemulator.interpreter.v1.exceptions.ClassMethodNotImplementedException;
import pl.genschu.bloomooemulator.interpreter.v1.Context;
import pl.genschu.bloomooemulator.interpreter.v1.util.KeyboardsKeysMapper;
import pl.genschu.bloomooemulator.interpreter.v1.variable.*;
import pl.genschu.bloomooemulator.utils.LegacyArgumentsHelper;

import java.util.List;

public class KeyboardVariable extends GlobalVariable {
	private boolean autoRepeat = false;
	private boolean isEnabled = true;

	public KeyboardVariable(String name, Context context) {
		super(name, context);
	}

	@Override
	public String getType() {
		return "KEYBOARD";
	}

	@Override
	protected void setMethods() {
		super.setMethods();

		this.setMethod("DISABLE", new Method(
				"void"
		) {
			@Override
			public Variable execute(Variable self, List<Object> arguments) {
				KeyboardVariable selfVar = (KeyboardVariable) self;
				selfVar.setEnabled(false);
				return null;
			}
		});
		this.setMethod("ENABLE", new Method(
				"void"
		) {
			@Override
			public Variable execute(Variable self, List<Object> arguments) {
				KeyboardVariable selfVar = (KeyboardVariable) self;
				selfVar.setEnabled(true);
				return null;
			}
		});
		this.setMethod("GETLATESTKEY", new Method(
				"STRING"
		) {
			@Override
			public Variable execute(Variable self, List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method GETLATESTKEY is not implemented yet");
			}
		});
		this.setMethod("ISENABLED", new Method(
				"BOOL"
		) {
			@Override
			public Variable execute(Variable self, List<Object> arguments) {
				KeyboardVariable selfVar = (KeyboardVariable) self;
				return new BoolVariable("", selfVar.isEnabled(), selfVar.getContext());
			}
		});
		this.setMethod("ISKEYDOWN", new Method(
				List.of(
						new Parameter("STRING", "keyName", true)
				),
				"BOOL"
		) {
			@Override
			public Variable execute(Variable self, List<Object> arguments) {
				KeyboardVariable selfVar = (KeyboardVariable) self;
				String key = LegacyArgumentsHelper.getString(arguments.get(0));
				int keyCode = KeyboardsKeysMapper.getKeyCode(key);
				if (keyCode != -1) {
					return new BoolVariable("", Gdx.input.isKeyPressed(keyCode), selfVar.getContext());
				} else {
					Gdx.app.error("KeyboardVariable", "Key " + key + " not found in lookup table. Returning false.");
					return new BoolVariable("", false, selfVar.getContext());
				}
			}
		});
		this.setMethod("SETAUTOREPEAT", new Method(
				List.of(
						new Parameter("BOOL", "autorepeat", true)
				),
				"void"
		) {
			@Override
			public Variable execute(Variable self, List<Object> arguments) {
				KeyboardVariable selfVar = (KeyboardVariable) self;
				selfVar.setAutoRepeat(LegacyArgumentsHelper.getBoolean(arguments.get(0)));
				return null;
			}
		});
	}

	@Override
	public void setAttribute(String name, Attribute attribute) {
		return; // no fields in this class
	}

	public boolean isAutoRepeat() {
		return autoRepeat;
	}

	public void setAutoRepeat(boolean autoRepeat) {
		this.autoRepeat = autoRepeat;
	}

	public boolean isEnabled() {
		return isEnabled;
	}

	public void setEnabled(boolean enabled) {
		isEnabled = enabled;
	}
}
