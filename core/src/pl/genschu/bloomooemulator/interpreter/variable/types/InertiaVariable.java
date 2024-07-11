package pl.genschu.bloomooemulator.interpreter.variable.types;

import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.variable.Attribute;
import pl.genschu.bloomooemulator.interpreter.variable.Method;
import pl.genschu.bloomooemulator.interpreter.variable.Parameter;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;

import java.util.List;

public class InertiaVariable extends Variable {
	public InertiaVariable(String name, Context context) {
		super(name, context);

		this.setMethod("ADDFORCE", new Method(
			List.of(
				new Parameter("INTEGER", "objectId", true),
				new Parameter("INTEGER", "forceX", true),
				new Parameter("INTEGER", "forceY", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method ADDFORCE is not implemented yet");
				return null;
			}
		});
		this.setMethod("CREATESPHERE", new Method(
			List.of(
				new Parameter("INTEGER", "objectId", true),
				new Parameter("INTEGER", "posX", true),
				new Parameter("INTEGER", "posY", true),
				new Parameter("INTEGER", "radius", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method CREATESPHERE is not implemented yet");
				return null;
			}
		});
		this.setMethod("DELETEBODY", new Method(
			List.of(
				new Parameter("INTEGER", "objectId", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method DELETEBODY is not implemented yet");
				return null;
			}
		});
		this.setMethod("GETPOSITIONX", new Method(
			List.of(
				new Parameter("INTEGER", "objectId", true)
			),
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
			List.of(
				new Parameter("INTEGER", "objectId", true)
			),
			"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method GETPOSITIONY is not implemented yet");
				return null;
			}
		});
		this.setMethod("GETSPEED", new Method(
			List.of(
				new Parameter("INTEGER", "objectId", true)
			),
			"INTEGER|DOUBLE?"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method GETSPEED is not implemented yet");
				return null;
			}
		});
		this.setMethod("LINK", new Method(
			List.of(
				new Parameter("INTEGER", "objectId", true),
				new Parameter("STRING", "objectName", true),
				new Parameter("BOOL", "unknown", true),
				new Parameter("BOOL", "unknown", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method LINK is not implemented yet");
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
		this.setMethod("RESETTIMER", new Method(
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method RESETTIMER is not implemented yet");
				return null;
			}
		});
		this.setMethod("SETGRAVITY", new Method(
			List.of(
				new Parameter("INTEGER|DOUBLE", "gravityX", true),
				new Parameter("INTEGER|DOUBLE", "gravityY", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method SETGRAVITY is not implemented yet");
				return null;
			}
		});
		this.setMethod("SETLINEARDAMPING", new Method(
			List.of(
				new Parameter("INTEGER", "objectId", true),
				new Parameter("INTEGER", "linearDamping", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method SETLINEARDAMPING is not implemented yet");
				return null;
			}
		});
		this.setMethod("SETMATERIAL", new Method(
			List.of(
				new Parameter("INTEGER", "objectId", true),
				new Parameter("STRING", "material", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method SETMATERIAL is not implemented yet");
				return null;
			}
		});
		this.setMethod("SETPOSITION", new Method(
			List.of(
				new Parameter("INTEGER", "objectId", true),
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
		this.setMethod("SETVELOCITY", new Method(
			List.of(
				new Parameter("INTEGER", "objectId", true),
				new Parameter("INTEGER", "speedX", true),
				new Parameter("INTEGER", "speedY", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method SETVELOCITY is not implemented yet");
				return null;
			}
		});
		this.setMethod("TICK", new Method(
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method TICK is not implemented yet");
				return null;
			}
		});
		this.setMethod("UNLINK", new Method(
			List.of(
				new Parameter("INTEGER", "objectId", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method UNLINK is not implemented yet");
				return null;
			}
		});
	}

	@Override
	public String getType() {
		return "INERTIA";
	}

	@Override
	public void setAttribute(String name, Attribute attribute) {
		return; // no fields in this class
	}

}
