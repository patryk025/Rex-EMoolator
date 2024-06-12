package pl.cba.genszu.amcodetranslator.interpreter.variable.types;

import pl.cba.genszu.amcodetranslator.interpreter.Context;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Attribute;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Method;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Parameter;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Variable;

import java.util.List;

public class WorldVariable extends Variable {
	public WorldVariable(String name, Context context) {
		super(name, context);

		this.setMethod("ADDBODY", new Method(
			List.of(
				new Parameter("INTEGER", "objectId", true),
				new Parameter("INTEGER", "mass", true),
				new Parameter("DOUBLE", "friction", true),
				new Parameter("DOUBLE", "frictionV", true),
				new Parameter("DOUBLE", "bounce", true),
				new Parameter("DOUBLE", "bounceV", true),
				new Parameter("DOUBLE", "maxV", true),
				new Parameter("INTEGER", "unknown", true),
				new Parameter("INTEGER", "unknown", true),
				new Parameter("INTEGER", "x?", true),
				new Parameter("INTEGER", "y?", true),
				new Parameter("INTEGER", "z?", true)
			),
			"void"
		) {
			@Override
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				return null;
			}
		});
		this.setMethod("ADDFORCE", new Method(
			List.of(
				new Parameter("INTEGER", "objectId", true),
				new Parameter("DOUBLE", "forceX", true),
				new Parameter("DOUBLE", "forceY", true),
				new Parameter("DOUBLE", "forceZ?", true)
			),
			"void"
		) {
			@Override
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				return null;
			}
		});
		this.setMethod("ADDGRAVITYEX", new Method(
			List.of(
				new Parameter("INTEGER", "objectId", true),
				new Parameter("INTEGER", "unknown", true),
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
		this.setMethod("FINDPATH", new Method(
			List.of(
				new Parameter("INTEGER", "objectId", true),
				new Parameter("INTEGER", "pathId", true),
				new Parameter("INTEGER", "x", true),
				new Parameter("INTEGER", "y", true),
				new Parameter("INTEGER", "z?", true),
				new Parameter("BOOL", "unknown", true),
				new Parameter("BOOL", "unknown", false)
			),
			"void?"
		) {
			@Override
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				return null;
			}
		});
		this.setMethod("FOLLOWPATH", new Method(
			List.of(
				new Parameter("INTEGER", "objectId", true),
				new Parameter("INTEGER", "pathId", true),
				new Parameter("DOUBLE", "unknown", true),
				new Parameter("DOUBLE", "speed?", true)
			),
			"void"
		) {
			@Override
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				return null;
			}
		});
		this.setMethod("GETANGLE", new Method(
			List.of(
				new Parameter("INTEGER", "objectId", true)
			),
			"DOUBLE"
		) {
			@Override
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				return null;
			}
		});
		this.setMethod("GETBKGPOSX", new Method(
			List.of(),
			"INTEGER"
		) {
			@Override
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				return null;
			}
		});
		this.setMethod("GETBKGPOSY", new Method(
			List.of(),
			"INTEGER"
		) {
			@Override
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				return null;
			}
		});
		this.setMethod("GETMOVEDISTANCE", new Method(
			List.of(
				new Parameter("INTEGER", "objectId", true)
			),
			"DOUBLE"
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
		this.setMethod("GETPOSITIONZ", new Method(
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
		this.setMethod("GETROTATIONZ", new Method(
			List.of(
				new Parameter("INTEGER", "objectId", true)
			),
			"DOUBLE"
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
			"DOUBLE"
		) {
			@Override
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				return null;
			}
		});
		this.setMethod("JOIN", new Method(
			List.of(
				new Parameter("INTEGER", "firstId", true),
				new Parameter("INTEGER", "secondId", true),
				new Parameter("INTEGER", "posX", true),
				new Parameter("INTEGER", "posY", true),
				new Parameter("INTEGER", "posZ?", true),
				new Parameter("INTEGER", "rotationX?", true),
				new Parameter("INTEGER", "rotationY", true),
				new Parameter("INTEGER", "rotationZ?", true),
				new Parameter("INTEGER", "unknown", false),
				new Parameter("INTEGER", "unknown", false),
				new Parameter("INTEGER", "unknown", false)
			),
			"void"
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
				new Parameter("INTEGER", "unknown", true)
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
				new Parameter("STRING", "filename", true)
			),
			"void"
		) {
			@Override
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				return null;
			}
		});
		this.setMethod("MOVEOBJECTS", new Method(
			List.of(),
			"void"
		) {
			@Override
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				return null;
			}
		});
		this.setMethod("REMOVEOBJECT", new Method(
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
		this.setMethod("SETACTIVE", new Method(
			List.of(
				new Parameter("INTEGER", "objectId", true),
				new Parameter("BOOLEAN", "active?", true)
			),
			"void"
		) {
			@Override
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				return null;
			}
		});
		this.setMethod("SETACTIVE", new Method(
			List.of(
				new Parameter("INTEGER", "objectId", true),
				new Parameter("INTEGER", "unknown", true),
				new Parameter("INTEGER", "unknown", true)
			),
			"void"
		) {
			@Override
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				return null;
			}
		});
		this.setMethod("SETBKGSIZE", new Method(
			List.of(
				new Parameter("INTEGER", "leftX", true),
				new Parameter("INTEGER", "rightX", true),
				new Parameter("INTEGER", "topY", true),
				new Parameter("INTEGER", "bottomY", true)
			),
			"void"
		) {
			@Override
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				return null;
			}
		});
		this.setMethod("SETG", new Method(
			List.of(
				new Parameter("INTEGER", "objectId", true),
				new Parameter("INTEGER", "force", true)
			),
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
				new Parameter("DOUBLE", "forceX?", true),
				new Parameter("DOUBLE", "forceY?", true),
				new Parameter("DOUBLE", "forceZ?", true)
			),
			"void"
		) {
			@Override
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				return null;
			}
		});
		this.setMethod("SETGRAVITYCENTER", new Method(
			List.of(
				new Parameter("INTEGER", "objectId", true),
				new Parameter("BOOL", "gravityCenter", true)
			),
			"void"
		) {
			@Override
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				return null;
			}
		});
		this.setMethod("SETLIMIT", new Method(
			List.of(
				new Parameter("INTEGER", "objectId", true),
				new Parameter("INTEGER", "minX", true),
				new Parameter("INTEGER", "minY", true),
				new Parameter("INTEGER", "minZ", true),
				new Parameter("INTEGER", "maxX", true),
				new Parameter("INTEGER", "maxY", true),
				new Parameter("INTEGER", "maxZ", true)
			),
			"void"
		) {
			@Override
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				return null;
			}
		});
		this.setMethod("SETMAXSPEED", new Method(
			List.of(
				new Parameter("INTEGER", "objectId", true),
				new Parameter("INTEGER", "maxSpeed", true)
			),
			"void"
		) {
			@Override
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				return null;
			}
		});
		this.setMethod("SETMOVEFLAGS", new Method(
			List.of(
				new Parameter("BOOL", "moveX?", true),
				new Parameter("BOOL", "moveY?", true)
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
				new Parameter("INTEGER", "x", true),
				new Parameter("INTEGER", "y", true),
				new Parameter("INTEGER", "z", true)
			),
			"void"
		) {
			@Override
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				return null;
			}
		});
		this.setMethod("SETREFOBJECT", new Method(
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
		this.setMethod("SETVELOCITY", new Method(
			List.of(
				new Parameter("INTEGER", "objectId", true),
				new Parameter("INTEGER", "x", true),
				new Parameter("INTEGER", "y", true),
				new Parameter("INTEGER", "z", true)
			),
			"void"
		) {
			@Override
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				return null;
			}
		});
		this.setMethod("START", new Method(
			List.of(),
			"void"
		) {
			@Override
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				return null;
			}
		});
		this.setMethod("STOP", new Method(
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
		return "WORLD";
	}

	@Override
	public void setAttribute(String name, Attribute attribute) {
		List<String> knownAttributes = List.of("FILENAME");
		if(knownAttributes.contains(name)) {
			super.setAttribute(name, attribute);
		}
	}

}
