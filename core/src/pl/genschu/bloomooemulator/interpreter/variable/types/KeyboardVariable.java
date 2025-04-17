package pl.genschu.bloomooemulator.interpreter.variable.types;

import com.badlogic.gdx.Gdx;
import pl.genschu.bloomooemulator.interpreter.exceptions.ClassMethodNotImplementedException;
import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.util.KeyboardsKeysMapper;
import pl.genschu.bloomooemulator.interpreter.variable.*;
import pl.genschu.bloomooemulator.utils.ArgumentsHelper;

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
			public Variable execute(List<Object> arguments) {
				setEnabled(false);
				return null;
			}
		});
		this.setMethod("ENABLE", new Method(
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				setEnabled(true);
				return null;
			}
		});
		this.setMethod("GETLATESTKEY", new Method(
				"STRING"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method GETLATESTKEY is not implemented yet");
			}
		});
		this.setMethod("ISENABLED", new Method(
				"BOOL"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				return new BoolVariable("", isEnabled, getContext());
			}
		});
		this.setMethod("ISKEYDOWN", new Method(
				List.of(
						new Parameter("STRING", "keyName", true)
				),
				"BOOL"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				String key = ArgumentsHelper.getString(arguments.get(0));

				for (int keyCode : KeyboardsKeysMapper.getKeySet()) {
					String keyName = KeyboardsKeysMapper.getMappedKey(keyCode);

					if(key.equals(keyName)) {
						return new BoolVariable("", Gdx.input.isKeyPressed(keyCode), getContext());
					}
				}

				return new BoolVariable("", false, getContext());
			}
		});
		this.setMethod("SETAUTOREPEAT", new Method(
				List.of(
						new Parameter("BOOL", "autorepeat", true)
				),
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				setAutoRepeat(ArgumentsHelper.getBoolean(arguments.get(0)));
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
