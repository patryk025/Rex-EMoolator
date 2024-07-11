package pl.genschu.bloomooemulator.interpreter.variable.types;

import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.variable.Attribute;
import pl.genschu.bloomooemulator.interpreter.variable.Method;
import pl.genschu.bloomooemulator.interpreter.variable.Parameter;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;

import java.util.List;

public class VirtualGraphicsObjectVariable extends Variable {
	public VirtualGraphicsObjectVariable(String name, Context context) {
		super(name, context);

		this.setMethod("GETHEIGHT", new Method(
			"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method GETHEIGHT is not implemented yet");
				return null;
			}
		});
		this.setMethod("GETPOSITIONX", new Method(
			"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method GETPOSITIONX is not implemented yet");
				return null;
			}
		});
		this.setMethod("GETPOSITIONY", new Method(
			"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method GETPOSITIONY is not implemented yet");
				return null;
			}
		});
		this.setMethod("GETWIDTH", new Method(
			"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method GETWIDTH is not implemented yet");
				return null;
			}
		});
		this.setMethod("MOVE", new Method(
			List.of(
				new Parameter("INTEGER", "offsetX", true),
				new Parameter("INTEGER", "offsetY", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method MOVE is not implemented yet");
				return null;
			}
		});
		this.setMethod("SETMASK", new Method(
			List.of(
				new Parameter("STRING", "mask", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method SETMASK is not implemented yet");
				return null;
			}
		});
		this.setMethod("SETPOSITION", new Method(
			List.of(
				new Parameter("INTEGER", "x", true),
				new Parameter("INTEGER", "y", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method SETPOSITION is not implemented yet");
				return null;
			}
		});
		this.setMethod("SETPRIORITY", new Method(
			List.of(
				new Parameter("INTEGER", "priority", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method SETPRIORITY is not implemented yet");
				return null;
			}
		});
		this.setMethod("SETSOURCE", new Method(
			List.of(
				new Parameter("STRING", "source", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method SETSOURCE is not implemented yet");
				return null;
			}
		});
	}

	@Override
	public String getType() {
		return "VIRTUALGRAPHICSOBJECT";
	}

	@Override
	public void setAttribute(String name, Attribute attribute) {
		List<String> knownAttributes = List.of("ASBUTTON", "MASK", "MONITORCOLLISION", "MONITORCOLLISIONALPHA", "PRIORITY", "SOURCE", "TOCANVAS", "VISIBLE");
		if(knownAttributes.contains(name)) {
			super.setAttribute(name, attribute);
		}
	}

}
