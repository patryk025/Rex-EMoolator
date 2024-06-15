package pl.cba.genszu.amcodetranslator.interpreter.variable.types;

import pl.cba.genszu.amcodetranslator.interpreter.Context;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Attribute;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Method;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Parameter;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Variable;

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
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
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
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
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
