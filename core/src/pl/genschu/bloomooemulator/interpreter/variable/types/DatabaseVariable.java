package pl.genschu.bloomooemulator.interpreter.variable.types;

import pl.genschu.bloomooemulator.interpreter.exceptions.ClassMethodNotImplementedException;
import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.variable.Attribute;
import pl.genschu.bloomooemulator.interpreter.variable.Method;
import pl.genschu.bloomooemulator.interpreter.variable.Parameter;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;

import java.util.List;

public class DatabaseVariable extends Variable {
	public DatabaseVariable(String name, Context context) {
		super(name, context);

		this.setMethod("FIND", new Method(
			List.of(
				new Parameter("STRING", "columnName", true),
				new Parameter("VARIABLE", "columnValue", true),
				new Parameter("INTEGER", "defaultIndex?", true)
			),
			"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method FIND is not implemented yet");
			}
		});
		this.setMethod("GETROWSNO", new Method(
			"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method GETROWSNO is not implemented yet");
			}
		});
		this.setMethod("LOAD", new Method(
			List.of(
				new Parameter("STRING", "dtaName", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method LOAD is not implemented yet");
			}
		});
		this.setMethod("NEXT", new Method(
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method NEXT is not implemented yet");
			}
		});
		this.setMethod("REMOVEALL", new Method(
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method REMOVEALL is not implemented yet");
			}
		});
		this.setMethod("SAVE", new Method(
			List.of(
				new Parameter("STRING", "dtaName", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method SAVE is not implemented yet");
			}
		});
		this.setMethod("SELECT", new Method(
			List.of(
				new Parameter("INTEGER", "rowIndex", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method SELECT is not implemented yet");
			}
		});
	}

	@Override
	public String getType() {
		return "DATABASE";
	}

	@Override
	public void setAttribute(String name, Attribute attribute) {
		List<String> knownAttributes = List.of("MODEL");
		if(knownAttributes.contains(name)) {
			super.setAttribute(name, attribute);
		}
	}

}
