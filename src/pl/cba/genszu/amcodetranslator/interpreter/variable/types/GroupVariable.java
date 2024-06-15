package pl.cba.genszu.amcodetranslator.interpreter.variable.types;

import pl.cba.genszu.amcodetranslator.interpreter.Context;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Attribute;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Method;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Parameter;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Variable;

import java.util.List;

public class GroupVariable extends Variable {
	public GroupVariable(String name, Context context) {
		super(name, context);

		this.setMethod("[nazwa metody]", new Method(
			List.of(
				new Parameter("mixed", "param1...paramN", true)
			),
			"void"
		) {
			@Override
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method [nazwa metody] is not implemented yet");
				return null;
			}
		});
		this.setMethod("ADD", new Method(
			List.of(
				new Parameter("STRING", "varName1...varNameN", true)
			),
			"void"
		) {
			@Override
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method ADD is not implemented yet");
				return null;
			}
		});
		this.setMethod("ADDCLONES", new Method(
			List.of(
				new Parameter("STRING", "varName", true),
				new Parameter("INTEGER", "firstCloneIndex?", true),
				new Parameter("INTEGER", "numberOfClones", true)
			),
			"void"
		) {
			@Override
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method ADDCLONES is not implemented yet");
				return null;
			}
		});
		this.setMethod("GETSIZE", new Method(
			"INTEGER"
		) {
			@Override
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method GETSIZE is not implemented yet");
				return null;
			}
		});
		this.setMethod("NEXT", new Method(
			"mixed"
		) {
			@Override
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method NEXT is not implemented yet");
				return null;
			}
		});
		this.setMethod("PREV", new Method(
			"mixed"
		) {
			@Override
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method PREV is not implemented yet");
				return null;
			}
		});
		this.setMethod("REMOVE", new Method(
			List.of(
				new Parameter("STRING", "varName", true)
			),
			"void"
		) {
			@Override
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method REMOVE is not implemented yet");
				return null;
			}
		});
		this.setMethod("REMOVEALL", new Method(
			"void"
		) {
			@Override
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method REMOVEALL is not implemented yet");
				return null;
			}
		});
		this.setMethod("RESETMARKER", new Method(
			"void"
		) {
			@Override
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method RESETMARKER is not implemented yet");
				return null;
			}
		});
	}

	@Override
	public String getType() {
		return "GROUP";
	}

	@Override
	public void setAttribute(String name, Attribute attribute) {
		return; // no fields in this class
	}

}
