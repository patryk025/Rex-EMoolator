package pl.genschu.bloomooemulator.interpreter.variable.types;

import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.variable.Attribute;
import pl.genschu.bloomooemulator.interpreter.variable.Method;
import pl.genschu.bloomooemulator.interpreter.variable.Parameter;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;

import java.util.List;

public class CNVLoaderVariable extends Variable {
	public CNVLoaderVariable(String name, Context context) {
		super(name, context);

		this.setMethod("LOAD", new Method(
			List.of(
				new Parameter("STRING", "cnvFile", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method LOAD is not implemented yet");
				return null;
			}
		});
		this.setMethod("RELEASE", new Method(
			List.of(
				new Parameter("STRING", "cnvFile", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method RELEASE is not implemented yet");
				return null;
			}
		});
	}

	@Override
	public String getType() {
		return "CNVLOADER";
	}

	@Override
	public void setAttribute(String name, Attribute attribute) {
		return; // no fields in this class
	}

}
