package pl.genschu.bloomooemulator.interpreter.variable.types;

import pl.genschu.bloomooemulator.interpreter.exceptions.ClassMethodNotImplementedException;
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
				throw new ClassMethodNotImplementedException("Method ADDFORCE is not implemented yet");
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
				throw new ClassMethodNotImplementedException("Method CREATESPHERE is not implemented yet");
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
				throw new ClassMethodNotImplementedException("Method DELETEBODY is not implemented yet");
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
				throw new ClassMethodNotImplementedException("Method GETPOSITIONX is not implemented yet");
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
				throw new ClassMethodNotImplementedException("Method GETPOSITIONY is not implemented yet");
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
				throw new ClassMethodNotImplementedException("Method GETSPEED is not implemented yet");
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
				throw new ClassMethodNotImplementedException("Method LINK is not implemented yet");
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
				throw new ClassMethodNotImplementedException("Method LOAD is not implemented yet");
			}
		});
		this.setMethod("RESETTIMER", new Method(
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method RESETTIMER is not implemented yet");
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
				throw new ClassMethodNotImplementedException("Method SETGRAVITY is not implemented yet");
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
				throw new ClassMethodNotImplementedException("Method SETLINEARDAMPING is not implemented yet");
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
				throw new ClassMethodNotImplementedException("Method SETMATERIAL is not implemented yet");
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
				throw new ClassMethodNotImplementedException("Method SETPOSITION is not implemented yet");
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
				throw new ClassMethodNotImplementedException("Method SETVELOCITY is not implemented yet");
			}
		});
		this.setMethod("TICK", new Method(
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method TICK is not implemented yet");
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
				throw new ClassMethodNotImplementedException("Method UNLINK is not implemented yet");
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
