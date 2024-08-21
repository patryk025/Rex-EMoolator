package pl.genschu.bloomooemulator.interpreter.variable.types;

import pl.genschu.bloomooemulator.interpreter.exceptions.ClassMethodNotImplementedException;
import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.variable.Attribute;
import pl.genschu.bloomooemulator.interpreter.variable.Method;
import pl.genschu.bloomooemulator.interpreter.variable.Parameter;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;

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
			public Variable execute(List<Object> arguments, Variable variable) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method ADD is not implemented yet");
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
			public Variable execute(List<Object> arguments, Variable variable) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method ASSIGN is not implemented yet");
			}
		});
		this.setMethod("GET", new Method(
			List.of(
				new Parameter("INTEGER", "n", true)
			),
			"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments, Variable variable) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method GET is not implemented yet");
			}
		});
		this.setMethod("MUL", new Method(
			List.of(
				new Parameter("INTEGER", "scalar", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments, Variable variable) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method MUL is not implemented yet");
			}
		});
		this.setMethod("NORMALIZE", new Method(
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments, Variable variable) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method NORMALIZE is not implemented yet");
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
			public Variable execute(List<Object> arguments, Variable variable) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method REFLECT is not implemented yet");
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
