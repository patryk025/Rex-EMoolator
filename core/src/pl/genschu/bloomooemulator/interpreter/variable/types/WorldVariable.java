package pl.genschu.bloomooemulator.interpreter.variable.types;

import pl.genschu.bloomooemulator.interpreter.exceptions.ClassMethodNotImplementedException;
import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.variable.Attribute;
import pl.genschu.bloomooemulator.interpreter.variable.Method;
import pl.genschu.bloomooemulator.interpreter.variable.Parameter;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.loader.SEKLoader;
import pl.genschu.bloomooemulator.utils.ArgumentsHelper;
import pl.genschu.bloomooemulator.world.GameObject;
import pl.genschu.bloomooemulator.world.PointsData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorldVariable extends Variable {
	private int entityCount;
	Map<Integer, GameObject> gameObjects = new HashMap<>();
	List<PointsData> points = new ArrayList<>();
	Map<Variable, GameObject> gameObjectsMap = new HashMap<>();

	public WorldVariable(String name, Context context) {
		super(name, context);
	}

	@Override
	protected void setMethods() {
		super.setMethods();

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
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method ADDBODY is not implemented yet");
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
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method ADDFORCE is not implemented yet");
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
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method ADDGRAVITYEX is not implemented yet");
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
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method FINDPATH is not implemented yet");
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
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method FOLLOWPATH is not implemented yet");
			}
		});
		this.setMethod("GETANGLE", new Method(
				List.of(
						new Parameter("INTEGER", "objectId", true)
				),
				"DOUBLE"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method GETANGLE is not implemented yet");
			}
		});
		this.setMethod("GETBKGPOSX", new Method(
				"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method GETBKGPOSX is not implemented yet");
			}
		});
		this.setMethod("GETBKGPOSY", new Method(
				"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method GETBKGPOSY is not implemented yet");
			}
		});
		this.setMethod("GETMOVEDISTANCE", new Method(
				List.of(
						new Parameter("INTEGER", "objectId", true)
				),
				"DOUBLE"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method GETMOVEDISTANCE is not implemented yet");
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
		this.setMethod("GETPOSITIONZ", new Method(
				List.of(
						new Parameter("INTEGER", "objectId", true)
				),
				"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method GETPOSITIONZ is not implemented yet");
			}
		});
		this.setMethod("GETROTATIONZ", new Method(
				List.of(
						new Parameter("INTEGER", "objectId", true)
				),
				"DOUBLE"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method GETROTATIONZ is not implemented yet");
			}
		});
		this.setMethod("GETSPEED", new Method(
				List.of(
						new Parameter("INTEGER", "objectId", true)
				),
				"DOUBLE"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method GETSPEED is not implemented yet");
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
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method JOIN is not implemented yet");
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
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method LINK is not implemented yet");
			}
		});
		this.setMethod("LOAD", new Method(
				List.of(
						new Parameter("STRING", "filename", true)
				),
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				String filename = ArgumentsHelper.getString(arguments.get(0));
				getAttribute("FILENAME").setValue(filename);
				SEKLoader.loadSek(WorldVariable.this);
				return null;
			}
		});
		this.setMethod("MOVEOBJECTS", new Method(
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method MOVEOBJECTS is not implemented yet");
			}
		});
		this.setMethod("REMOVEOBJECT", new Method(
				List.of(
						new Parameter("INTEGER", "objectId", true)
				),
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				gameObjects.remove(ArgumentsHelper.getInteger(arguments.get(0)));
				return null;
			}
		});
		this.setMethod("SETACTIVE", new Method(
				List.of(
						new Parameter("INTEGER", "pathId?", false),
						new Parameter("INTEGER", "objectId?", true),
						new Parameter("BOOLEAN", "active", true)
				),
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method SETACTIVE is not implemented yet");
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
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method SETBKGSIZE is not implemented yet");
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
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method SETG is not implemented yet");
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
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method SETGRAVITY is not implemented yet");
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
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method SETGRAVITYCENTER is not implemented yet");
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
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method SETLIMIT is not implemented yet");
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
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method SETMAXSPEED is not implemented yet");
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
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method SETMOVEFLAGS is not implemented yet");
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
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method SETPOSITION is not implemented yet");
			}
		});
		this.setMethod("SETREFOBJECT", new Method(
				List.of(
						new Parameter("INTEGER", "objectId", true)
				),
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method SETREFOBJECT is not implemented yet");
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
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method SETVELOCITY is not implemented yet");
			}
		});
		this.setMethod("START", new Method(
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method START is not implemented yet");
			}
		});
		this.setMethod("STOP", new Method(
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method STOP is not implemented yet");
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
		return "WORLD";
	}

	@Override
	public void setAttribute(String name, Attribute attribute) {
		List<String> knownAttributes = List.of("FILENAME");
		if(knownAttributes.contains(name)) {
			super.setAttribute(name, attribute);
			SEKLoader.loadSek(this);
		}
	}

	public int getEntityCount() {
		return entityCount;
	}

	public void setEntityCount(int entityCount) {
		this.entityCount = entityCount;
	}

	public Map<Integer, GameObject> getGameObjects() {
		return gameObjects;
	}

	public void setGameObjects(Map<Integer, GameObject> gameObjects) {
		this.gameObjects = gameObjects;
	}

	public List<PointsData> getPoints() {
		return points;
	}

	public void setPoints(List<PointsData> points) {
		this.points = points;
	}
}
