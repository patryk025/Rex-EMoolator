package pl.genschu.bloomooemulator.interpreter.variable.types;

import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.variable.Attribute;
import pl.genschu.bloomooemulator.interpreter.variable.Method;
import pl.genschu.bloomooemulator.interpreter.variable.Parameter;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;

import java.util.List;

public class KeyboardVariable extends Variable {
	public KeyboardVariable(String name, Context context) {
		super(name, context);

		this.setMethod("DISABLE", new Method(
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method DISABLE is not implemented yet");
				return null;
			}
		});
		this.setMethod("ENABLE", new Method(
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method ENABLE is not implemented yet");
				return null;
			}
		});
		this.setMethod("GETLATESTKEY", new Method(
			"STRING"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method GETLATESTKEY is not implemented yet");
				return null;
			}
		});
		this.setMethod("ISENABLED", new Method(
			"BOOL"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method ISENABLED is not implemented yet");
				return null;
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
				// TODO: implement this method
				System.out.println("Method ISKEYDOWN is not implemented yet");
				return null;
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
				// TODO: implement this method
				System.out.println("Method SETAUTOREPEAT is not implemented yet");
				return null;
			}
		});
	}

	@Override
	public String getType() {
		return "KEYBOARD";
	}

	@Override
	public void setAttribute(String name, Attribute attribute) {
		return; // no fields in this class
	}

}
