package pl.cba.genszu.amcodetranslator.interpreter.variable.types;

import pl.cba.genszu.amcodetranslator.interpreter.Context;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Attribute;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Method;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Parameter;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Variable;

import java.util.List;

public class CanvasObserverVariable extends Variable {
	public CanvasObserverVariable(String name, Context context) {
		super(name, context);

		this.setMethod("ADD", new Method(
			List.of(
				new Parameter("STRING", "animoVar", true)
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
		this.setMethod("ENABLENOTIFY", new Method(
			List.of(
				new Parameter("BOOL", "enable", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method ENABLENOTIFY is not implemented yet");
				return null;
			}
		});
		this.setMethod("GETGRAPHICSAT", new Method(
			List.of(
				new Parameter("INTEGER", "posX", true),
				new Parameter("INTEGER", "posY", true),
				new Parameter("BOOL", "unknown", true),
				new Parameter("INTEGER", "minZ", true),
				new Parameter("INTEGER", "maxZ", true)
			),
			"STRING"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method GETGRAPHICSAT is not implemented yet");
				return null;
			}
		});
		this.setMethod("GETGRAPHICSAT", new Method(
			List.of(
				new Parameter("INTEGER", "posX", true),
				new Parameter("INTEGER", "posY", true),
				new Parameter("BOOL", "unknown", true),
				new Parameter("INTEGER", "minZ", true),
				new Parameter("INTEGER", "maxZ", true),
				new Parameter("BOOL", "useAlpha", true)
			),
			"STRING"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method GETGRAPHICSAT is not implemented yet");
				return null;
			}
		});
		this.setMethod("MOVEBKG", new Method(
			List.of(
				new Parameter("INTEGER", "deltaX", true),
				new Parameter("INTEGER", "deltaY", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method MOVEBKG is not implemented yet");
				return null;
			}
		});
		this.setMethod("PASTE", new Method(
			List.of(
				new Parameter("STRING", "varName", true),
				new Parameter("INTEGER", "posX", true),
				new Parameter("INTEGER", "posY", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method PASTE is not implemented yet");
				return null;
			}
		});
		this.setMethod("REDRAW", new Method(
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method REDRAW is not implemented yet");
				return null;
			}
		});
		this.setMethod("REFRESH", new Method(
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method REFRESH is not implemented yet");
				return null;
			}
		});
		this.setMethod("REMOVE", new Method(
			List.of(
				new Parameter("STRING", "varName1...varNameN", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method REMOVE is not implemented yet");
				return null;
			}
		});
		this.setMethod("SAVE", new Method(
			List.of(
				new Parameter("STRING", "imgFileName", true),
				new Parameter("mixed", "unknownParam1...unknownParamN", true)
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
		this.setMethod("SETBACKGROUND", new Method(
			List.of(
				new Parameter("STRING", "imageName", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method SETBACKGROUND is not implemented yet");
				return null;
			}
		});
		this.setMethod("SETBKGPOS", new Method(
			List.of(
				new Parameter("INTEGER", "posX", true),
				new Parameter("INTEGER", "posY", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method SETBKGPOS is not implemented yet");
				return null;
			}
		});
	}

	@Override
	public String getType() {
		return "CANVAS_OBSERVER";
	}

	@Override
	public void setAttribute(String name, Attribute attribute) {
		return; // no fields in this class
	}

}
