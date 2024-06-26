package pl.cba.genszu.amcodetranslator.interpreter.variable.types;

import pl.cba.genszu.amcodetranslator.interpreter.Context;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Attribute;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Method;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Parameter;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Variable;

import java.util.List;

public class VectorVariable extends Variable {
	public VectorVariable(String name, Context context) {
		super(name, context);

		this.setMethod("ADD", new Method(
			List.of(
				new Parameter("STRING|VECTOR", "vectorName", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method ADD is not implemented yet");
				return null;
			}
		});
		this.setMethod("ASSIGN", new Method(
			List.of(
				new Parameter("INTEGER", "x", true),
				new Parameter("INTEGER", "y", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method ASSIGN is not implemented yet");
				return null;
			}
		});
		this.setMethod("GET", new Method(
			List.of(
				new Parameter("INTEGER", "n", true)
			),
			"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method GET is not implemented yet");
				return null;
			}
		});
		this.setMethod("MUL", new Method(
			List.of(
				new Parameter("INTEGER", "scalar", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method MUL is not implemented yet");
				return null;
			}
		});
		this.setMethod("NORMALIZE", new Method(
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method NORMALIZE is not implemented yet");
				return null;
			}
		});
		this.setMethod("REFLECT", new Method(
			List.of(
				new Parameter("STRING", "vectorName", true),
				new Parameter("STRING", "normalVectorName", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method REFLECT is not implemented yet");
				return null;
			}
		});
	}

	@Override
	public String getType() {
		return "VECTOR";
	}

	@Override
	public void setAttribute(String name, Attribute attribute) {
		List<String> knownAttributes = List.of("SIZE", "INTEGER, INTEGER, [INTEGER...] VALUE");
		if(knownAttributes.contains(name)) {
			super.setAttribute(name, attribute);
		}
	}

}
