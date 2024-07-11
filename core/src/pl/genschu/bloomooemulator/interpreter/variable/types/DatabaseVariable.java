package pl.genschu.bloomooemulator.interpreter.variable.types;

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
				System.out.println("Method FIND is not implemented yet");
				return null;
			}
		});
		this.setMethod("GETROWSNO", new Method(
			"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method GETROWSNO is not implemented yet");
				return null;
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
				System.out.println("Method LOAD is not implemented yet");
				return null;
			}
		});
		this.setMethod("NEXT", new Method(
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method NEXT is not implemented yet");
				return null;
			}
		});
		this.setMethod("REMOVEALL", new Method(
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method REMOVEALL is not implemented yet");
				return null;
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
				System.out.println("Method SAVE is not implemented yet");
				return null;
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
				System.out.println("Method SELECT is not implemented yet");
				return null;
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
