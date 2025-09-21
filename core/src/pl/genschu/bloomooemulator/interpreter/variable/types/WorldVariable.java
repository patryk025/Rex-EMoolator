package pl.genschu.bloomooemulator.interpreter.variable.types;

import pl.genschu.bloomooemulator.engine.physics.IPhysicsEngine;
import pl.genschu.bloomooemulator.engine.physics.ODEPhysicsEngine;
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
	private final IPhysicsEngine physicsEngine = new ODEPhysicsEngine();
	private String sekVersion;

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
						new Parameter("INTEGER", "bodyType", true),
						new Parameter("INTEGER", "geomType", true),
						new Parameter("INTEGER", "dim0", true),
						new Parameter("INTEGER", "dim1", true),
						new Parameter("INTEGER", "dim2", true)
				),
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				int objectId = ArgumentsHelper.getInteger(arguments.get(0));
				double mass = ArgumentsHelper.getDouble(arguments.get(1));
				double mu = ArgumentsHelper.getDouble(arguments.get(2));
				double mu2 = ArgumentsHelper.getDouble(arguments.get(3));
				double bounce = ArgumentsHelper.getDouble(arguments.get(4));
				double bounceVel = ArgumentsHelper.getDouble(arguments.get(5));
				double maxVel = ArgumentsHelper.getDouble(arguments.get(6));
				int bodyType = ArgumentsHelper.getInteger(arguments.get(7));
				int geomType = ArgumentsHelper.getInteger(arguments.get(8));
				double dim0 = ArgumentsHelper.getDouble(arguments.get(9));
				double dim1 = ArgumentsHelper.getDouble(arguments.get(10));
				double dim2 = ArgumentsHelper.getDouble(arguments.get(11));

				physicsEngine.createBody(objectId, mass, mu, mu2, bounce, bounceVel, maxVel, bodyType, geomType, dim0, dim1, dim2);

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
			public Variable execute(List<Object> arguments) {
				int objectId = ArgumentsHelper.getInteger(arguments.get(0));
                double forceX = ArgumentsHelper.getDouble(arguments.get(1));
                double forceY = ArgumentsHelper.getDouble(arguments.get(2));
                double forceZ = arguments.size() > 3 ? ArgumentsHelper.getDouble(arguments.get(3)) : 0.0;

                physicsEngine.addForce(objectId, forceX, forceY, forceZ);

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
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method ADDGRAVITYEX is not implemented yet");
			}
		});
		this.setMethod("FINDPATH", new Method(
				List.of(
						new Parameter("INTEGER", "objectId", true),
						new Parameter("INTEGER", "pointObjectId", true),
						new Parameter("INTEGER", "targetX", true),
						new Parameter("INTEGER", "targetY", true),
						new Parameter("INTEGER", "targetZ", true),
						new Parameter("BOOL", "saveIntermediates?", true),
						new Parameter("BOOL", "unknown", false)
				),
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				int objectId = ArgumentsHelper.getInteger(arguments.get(0));
				int pointObjectId = ArgumentsHelper.getInteger(arguments.get(1));
				int targetX = ArgumentsHelper.getInteger(arguments.get(2));
				int targetY = ArgumentsHelper.getInteger(arguments.get(3));
				int targetZ = ArgumentsHelper.getInteger(arguments.get(4));
				boolean saveIntermediates = ArgumentsHelper.getBoolean(arguments.get(5));
				boolean unknown = ArgumentsHelper.getBoolean(arguments.get(6));

                physicsEngine.findPath(objectId, pointObjectId, targetX, targetY, targetZ, saveIntermediates, unknown);
                return null;
			}
		});
		this.setMethod("FOLLOWPATH", new Method(
				List.of(
						new Parameter("INTEGER", "objectId", true),
						new Parameter("INTEGER", "arrivalRadius", true),
						new Parameter("DOUBLE", "turnClamp", true),
						new Parameter("DOUBLE", "speed", true)
				),
				"DOUBLE"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				int objectId = ArgumentsHelper.getInteger(arguments.get(0));
				int arrivalRadius = ArgumentsHelper.getInteger(arguments.get(1));
				double turnClamp = ArgumentsHelper.getDouble(arguments.get(2));
				double speed = ArgumentsHelper.getDouble(arguments.get(3));

                float outDist = physicsEngine.followPath(objectId, arrivalRadius, turnClamp, speed);
                return new DoubleVariable("", outDist, WorldVariable.this.context);
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
				int objectId = ArgumentsHelper.getInteger(arguments.get(0));
                double angle = physicsEngine.getAngle(objectId);
                return new DoubleVariable("", Math.toDegrees(angle), WorldVariable.this.context);
			}
		});
		this.setMethod("GETBKGPOSX", new Method(
				"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
                // TODO: for now its stub, as I don't have implementation
				return new IntegerVariable("", 0, WorldVariable.this.context);
			}
		});
		this.setMethod("GETBKGPOSY", new Method(
				"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
                // TODO: for now its stub, as I don't have implementation
                return new IntegerVariable("", 0, WorldVariable.this.context);
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
                int objectId = ArgumentsHelper.getInteger(arguments.get(0));
				return new DoubleVariable("", physicsEngine.getMoveDistance(objectId), WorldVariable.this.context);
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
				int objectId = ArgumentsHelper.getInteger(arguments.get(0));
                double[] position = physicsEngine.getPosition(objectId);
                if (position == null || position.length < 3) {
                    throw new IllegalArgumentException("Object with ID " + objectId + " does not exist or has no position.");
                }
                return new DoubleVariable("", position[0]+400, WorldVariable.this.context); // position is offset by 400 in X direction
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
				int objectId = ArgumentsHelper.getInteger(arguments.get(0));
                double[] position = physicsEngine.getPosition(objectId);
                if (position == null || position.length < 3) {
                    throw new IllegalArgumentException("Object with ID " + objectId + " does not exist or has no position.");
                }
                return new DoubleVariable("", position[1]+300, WorldVariable.this.context); // position is offset by 300 in Y direction
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
				int objectId = ArgumentsHelper.getInteger(arguments.get(0));
                double[] position = physicsEngine.getPosition(objectId);
                if (position == null || position.length < 3) {
                    throw new IllegalArgumentException("Object with ID " + objectId + " does not exist or has no position.");
                }
                return new DoubleVariable("", position[2], WorldVariable.this.context);
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
				int objectId = ArgumentsHelper.getInteger(arguments.get(0));
                return new DoubleVariable("", Math.toDegrees(physicsEngine.getRotationZ(objectId)), WorldVariable.this.context);
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
                int objectId = ArgumentsHelper.getInteger(arguments.get(0));
                double[] speed = physicsEngine.getSpeed(objectId);
                double v = Math.sqrt(speed[0]*speed[0] + speed[1]*speed[1] + speed[2]*speed[2]);
				return new DoubleVariable("", v, WorldVariable.this.context);
			}
		});
		this.setMethod("JOIN", new Method(
				List.of(
						new Parameter("INTEGER", "firstId", true),
						new Parameter("INTEGER", "secondId", true),
						new Parameter("DOUBLE", "anchorX", true),
						new Parameter("DOUBLE", "anchorY", true),
						new Parameter("DOUBLE", "anchorZ", true),
						new Parameter("DOUBLE", "limitMotor?", true),
						new Parameter("DOUBLE", "lowStop", true),
						new Parameter("DOUBLE", "highStop", true),
						new Parameter("DOUBLE", "hingeAxisX", false),
						new Parameter("DOUBLE", "hingeAxisY", false),
						new Parameter("DOUBLE", "hingeAxisZ", false)
				),
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				int firstId = ArgumentsHelper.getInteger(arguments.get(0));
                int secondId = ArgumentsHelper.getInteger(arguments.get(1));
                double anchorX = ArgumentsHelper.getDouble(arguments.get(2));
                double anchorY = ArgumentsHelper.getDouble(arguments.get(3));
                double anchorZ = ArgumentsHelper.getDouble(arguments.get(4));
                double limitMotor = ArgumentsHelper.getDouble(arguments.get(5));
                double lowStop = ArgumentsHelper.getDouble(arguments.get(6));
                double highStop = ArgumentsHelper.getDouble(arguments.get(7));
                double hingeAxisX = (arguments.size() == 11) ? ArgumentsHelper.getDouble(arguments.get(8)) : 0;
                double hingeAxisY = (arguments.size() == 11) ? ArgumentsHelper.getDouble(arguments.get(9)) : 0;
                double hingeAxisZ = (arguments.size() == 11) ? ArgumentsHelper.getDouble(arguments.get(10)) : 1;
                physicsEngine.addJoint(firstId, secondId, anchorX, anchorY, anchorZ, limitMotor, lowStop, highStop, hingeAxisX, hingeAxisY, hingeAxisZ);
                return null;
			}
		});
		this.setMethod("LINK", new Method(
				List.of(
						new Parameter("INTEGER", "objectId", true),
						new Parameter("STRING", "objectName", true)
				),
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				int objectId = ArgumentsHelper.getInteger(arguments.get(0));
                String objectName = ArgumentsHelper.getString(arguments.get(1));

                Variable objectVariable = context.getVariable(objectName);
                if (!(objectVariable instanceof AnimoVariable || objectVariable instanceof ImageVariable)) {
                    throw new IllegalArgumentException("Object with name " + objectName + " is not a valid type for linking.");
                }
                physicsEngine.linkVariable(objectVariable, objectId);
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
			public Variable execute(List<Object> arguments) {
				String filename = ArgumentsHelper.getString(arguments.get(0));
				getAttribute("FILENAME").setValue(filename);
                physicsEngine.shutdown();
                physicsEngine.init();
                SEKLoader.loadSek(WorldVariable.this);
				return null;
			}
		});
		this.setMethod("MOVEOBJECTS", new Method(
				"DOUBLE"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
                double elapsedTime = physicsEngine.stepSimulation();
                return new DoubleVariable("", elapsedTime, WorldVariable.this.context);
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
				int objectId = ArgumentsHelper.getInteger(arguments.get(0));
				physicsEngine.destroyBody(objectId);
				return null;
			}
		});
		this.setMethod("SETACTIVE", new Method(
				List.of(
						new Parameter("INTEGER", "objectId", false),
						new Parameter("BOOLEAN", "active?", true),
						new Parameter("BOOLEAN", "collidable?", true)
				),
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				int objectId = ArgumentsHelper.getInteger(arguments.get(0));
                boolean active = ArgumentsHelper.getBoolean(arguments.get(1));
                boolean collidable = ArgumentsHelper.getBoolean(arguments.get(2));
                physicsEngine.setActive(objectId, active, collidable);
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
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method SETBKGSIZE is not implemented yet");
			}
		});
		this.setMethod("SETG", new Method(
				List.of(
						new Parameter("INTEGER", "objectId", true),
						new Parameter("DOUBLE", "g", true)
				),
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				physicsEngine.setG(ArgumentsHelper.getInteger(arguments.get(0)), ArgumentsHelper.getDouble(arguments.get(1)));
                return null;
			}
		});
		this.setMethod("SETGRAVITY", new Method(
				List.of(
						new Parameter("DOUBLE", "gravityX", true),
						new Parameter("DOUBLE", "gravityY", true),
						new Parameter("DOUBLE", "gravityZ", true)
				),
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				double gravityX = ArgumentsHelper.getDouble(arguments.get(0));
                double gravityY = ArgumentsHelper.getDouble(arguments.get(1));
                double gravityZ = ArgumentsHelper.getDouble(arguments.get(2));
                physicsEngine.setGravity(gravityX, gravityY, gravityZ);
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
			public Variable execute(List<Object> arguments) {
				int objectId = ArgumentsHelper.getInteger(ArgumentsHelper.getInteger(arguments.get(0)));
				boolean gravityCenter = ArgumentsHelper.getBoolean(ArgumentsHelper.getInteger(arguments.get(1)));
				physicsEngine.setGravityCenter(objectId, gravityCenter);
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
			public Variable execute(List<Object> arguments) {
				int objectId = ArgumentsHelper.getInteger(arguments.get(0));
                int minX = ArgumentsHelper.getInteger(arguments.get(1));
                int minY = ArgumentsHelper.getInteger(arguments.get(2));
                int minZ = ArgumentsHelper.getInteger(arguments.get(3));
                int maxX = ArgumentsHelper.getInteger(arguments.get(4));
                int maxY = ArgumentsHelper.getInteger(arguments.get(5));
                int maxZ = ArgumentsHelper.getInteger(arguments.get(6));
                physicsEngine.setLimit(objectId, minX, minY, minZ, maxX, maxY, maxZ);
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
			public Variable execute(List<Object> arguments) {
				int objectId = ArgumentsHelper.getInteger(arguments.get(0));
                int maxSpeed = ArgumentsHelper.getInteger(arguments.get(1));
                physicsEngine.setMaxVelocity(objectId, maxSpeed);
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
				int objectId = ArgumentsHelper.getInteger(arguments.get(0));
                double x = ArgumentsHelper.getDouble(arguments.get(1));
                double y = ArgumentsHelper.getDouble(arguments.get(2));
                double z = ArgumentsHelper.getDouble(arguments.get(3));
                physicsEngine.setPosition(objectId, x-400, 300-y, z); // 400 and 300 are correction by half of screen size
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
			public Variable execute(List<Object> arguments) {
				int objectId = ArgumentsHelper.getInteger(arguments.get(0));
                physicsEngine.setReferenceObjectId(objectId);
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
			public Variable execute(List<Object> arguments) {
				int objectId = ArgumentsHelper.getInteger(arguments.get(0));
                double x = ArgumentsHelper.getDouble(arguments.get(1));
                double y = ArgumentsHelper.getDouble(arguments.get(2));
                double z = ArgumentsHelper.getDouble(arguments.get(3));

                physicsEngine.setSpeed(objectId, x, y, z);
                return null;
			}
		});
		this.setMethod("START", new Method(
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				physicsEngine.start();
                return null;
			}
		});
		this.setMethod("STOP", new Method(
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				physicsEngine.stop();
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
				int objectId = ArgumentsHelper.getInteger(arguments.get(0));
                physicsEngine.unlinkVariable(objectId);
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
            physicsEngine.init();
            SEKLoader.loadSek(this);
		}
	}

	public String getSekVersion() {
		return sekVersion;
	}

	public void setSekVersion(String sekVersion) {
		this.sekVersion = sekVersion;
	}

	public IPhysicsEngine getPhysicsEngine() {
		return physicsEngine;
	}
}
