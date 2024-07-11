package pl.genschu.bloomooemulator.interpreter.variable.types;

import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.variable.Attribute;
import pl.genschu.bloomooemulator.interpreter.variable.Method;
import pl.genschu.bloomooemulator.interpreter.variable.Parameter;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;

import java.util.List;

public class ImageVariable extends Variable {
	public ImageVariable(String name, Context context) {
		super(name, context);

		this.setMethod("GETALPHA", new Method(
			List.of(
				new Parameter("INTEGER", "posX", true),
				new Parameter("INTEGER", "posY", true)
			),
			"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method GETALPHA is not implemented yet");
				return null;
			}
		});
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
		this.setMethod("GETPIXEL", new Method(
			List.of(
				new Parameter("INTEGER", "posX", true),
				new Parameter("INTEGER", "posY", true)
			),
			"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method GETPIXEL is not implemented yet");
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
		this.setMethod("HIDE", new Method(
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method HIDE is not implemented yet");
				return null;
			}
		});
		this.setMethod("INVALIDATE", new Method(
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method INVALIDATE is not implemented yet");
				return null;
			}
		});
		this.setMethod("LOAD", new Method(
			List.of(
				new Parameter("STRING", "path", true)
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
		this.setMethod("MERGEALPHA", new Method(
			List.of(
				new Parameter("INTEGER", "posX", true),
				new Parameter("INTEGER", "posY", true),
				new Parameter("STRING", "mask", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method MERGEALPHA is not implemented yet");
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
		this.setMethod("SETCLIPPING", new Method(
			List.of(
				new Parameter("INTEGER", "xLeft", true),
				new Parameter("INTEGER", "yTop", true),
				new Parameter("INTEGER", "xRight", true),
				new Parameter("INTEGER", "yBottom", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method SETCLIPPING is not implemented yet");
				return null;
			}
		});
		this.setMethod("SETOPACITY", new Method(
			List.of(
				new Parameter("INTEGER", "opacity", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method SETOPACITY is not implemented yet");
				return null;
			}
		});
		this.setMethod("SETPOSITION", new Method(
			List.of(
				new Parameter("INTEGER", "posX", true),
				new Parameter("INTEGER", "posY", true)
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
		this.setMethod("SHOW", new Method(
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method SHOW is not implemented yet");
				return null;
			}
		});
	}

	@Override
	public String getType() {
		return "IMAGE";
	}

	@Override
	public void setAttribute(String name, Attribute attribute) {
		List<String> knownAttributes = List.of("FILENAME", "MONITORCOLLISION", "MONITORCOLLISIONALPHA", "PRELOAD", "PRIORITY", "RELEASE", "TOCANVAS", "VISIBLE");
		if(knownAttributes.contains(name)) {
			super.setAttribute(name, attribute);
		}
	}

}
