package pl.cba.genszu.amcodetranslator.interpreter.variable.types;

import pl.cba.genszu.amcodetranslator.interpreter.Context;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Attribute;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Method;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Parameter;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Variable;

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
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				return null;
			}
		});
		this.setMethod("GETROWSNO", new Method(
			"INTEGER"
		) {
			@Override
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
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
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				return null;
			}
		});
		this.setMethod("NEXT", new Method(
			"void"
		) {
			@Override
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				return null;
			}
		});
		this.setMethod("REMOVEALL", new Method(
			"void"
		) {
			@Override
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
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
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
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
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
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
