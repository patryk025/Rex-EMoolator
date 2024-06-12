package pl.cba.genszu.amcodetranslator.interpreter.variable.types;

import pl.cba.genszu.amcodetranslator.interpreter.Context;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Attribute;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Method;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Parameter;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Variable;

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
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
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
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
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
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
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
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
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
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
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
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
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
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
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
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				return null;
			}
		});
		this.setMethod("RESETTIMER", new Method(
			List.of(),
			"void"
		) {
			@Override
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
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
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
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
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
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
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
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
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
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
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				return null;
			}
		});
		this.setMethod("TICK", new Method(
			List.of(),
			"void"
		) {
			@Override
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
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
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
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
		List<String> knownAttributes = List.o);
		if(knownAttributes.contains(name)) {
			super.setAttribute(name, attribute);
		}
	}

}
