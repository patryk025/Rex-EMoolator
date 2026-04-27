package pl.genschu.bloomooemulator.interpreter.variable;

import pl.genschu.bloomooemulator.annotations.InternalMutable;
import pl.genschu.bloomooemulator.engine.physics.IPhysicsEngine;
import pl.genschu.bloomooemulator.engine.physics.ODEPhysicsEngine;
import pl.genschu.bloomooemulator.interpreter.helpers.ArgumentHelper;
import pl.genschu.bloomooemulator.interpreter.values.*;
import pl.genschu.bloomooemulator.loader.SEKLoadable;
import pl.genschu.bloomooemulator.loader.SEKLoader;

import java.util.*;

/**
 * WorldVariable represents a physics simulation world (ODE-based).
 * Manages physics bodies, joints, forces, gravity, and links to graphical variables.
 *
 * Uses mutable WorldState because the physics engine is inherently stateful.
 */
public record WorldVariable(
    String name,
    @InternalMutable WorldState state,
    Map<String, SignalHandler> signals
) implements Variable, SEKLoadable {

    public static final class WorldState {
        public final IPhysicsEngine physicsEngine = new ODEPhysicsEngine();
        public String sekVersion;
        public String filename;
        public pl.genschu.bloomooemulator.engine.Game gameRef;

        public WorldState() {}

        public WorldState copy() {
            WorldState copy = new WorldState();
            copy.sekVersion = this.sekVersion;
            copy.filename = this.filename;
            return copy;
        }

        public void dispose() {
            physicsEngine.shutdown();
        }
    }

    public WorldVariable {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Variable name cannot be null or empty");
        }
        if (state == null) {
            state = new WorldState();
        }
        if (signals == null) {
            signals = Map.of();
        } else {
            signals = Map.copyOf(signals);
        }
    }

    public WorldVariable(String name) {
        this(name, new WorldState(), Map.of());
    }

    @Override
    public Value value() {
        return NullValue.INSTANCE;
    }

    @Override
    public VariableType type() {
        return VariableType.WORLD;
    }

    @Override
    public Variable withValue(Value newValue) {
        return this;
    }

    @Override
    public Map<String, MethodSpec> methods() {
        return METHODS;
    }

    @Override
    public Variable withSignal(String signalName, SignalHandler handler) {
        Map<String, SignalHandler> newSignals = new HashMap<>(signals);
        if (handler != null) {
            newSignals.put(signalName, handler);
        } else {
            newSignals.remove(signalName);
        }
        return new WorldVariable(name, state, newSignals);
    }

    // ========================================
    // PUBLIC ACCESSORS (for SEKLoader, engine)
    // ========================================

    @Override public String getName() { return name; }
    public IPhysicsEngine getPhysicsEngine() { return state.physicsEngine; }
    public String getSekVersion() { return state.sekVersion; }
    public void setSekVersion(String sekVersion) { state.sekVersion = sekVersion; }
    @Override public String getFilename() { return state.filename != null ? state.filename : ""; }
    @Override public pl.genschu.bloomooemulator.engine.Game getGameReference() { return state.gameRef; }

    // ========================================
    // METHODS
    // ========================================

    private static final Map<String, MethodSpec> METHODS = Map.ofEntries(
        Map.entry("ADDBODY", MethodSpec.of((self, args, ctx) -> {
            WorldVariable w = (WorldVariable) self;
            int objectId = ArgumentHelper.getInt(args.get(0));
            double mass = ArgumentHelper.getDouble(args.get(1));
            double mu = ArgumentHelper.getDouble(args.get(2));
            double mu2 = ArgumentHelper.getDouble(args.get(3));
            double bounce = ArgumentHelper.getDouble(args.get(4));
            double bounceVel = ArgumentHelper.getDouble(args.get(5));
            double maxVel = ArgumentHelper.getDouble(args.get(6));
            int bodyType = ArgumentHelper.getInt(args.get(7));
            int geomType = ArgumentHelper.getInt(args.get(8));
            double dim0 = ArgumentHelper.getDouble(args.get(9));
            double dim1 = ArgumentHelper.getDouble(args.get(10));
            double dim2 = ArgumentHelper.getDouble(args.get(11));
            w.state.physicsEngine.createBody(objectId, mass, mu, mu2, bounce, bounceVel, maxVel, bodyType, geomType, dim0, dim1, dim2);
            return MethodResult.noReturn();
        })),

        Map.entry("ADDFORCE", MethodSpec.of((self, args, ctx) -> {
            WorldVariable w = (WorldVariable) self;
            int objectId = ArgumentHelper.getInt(args.get(0));
            double forceX = ArgumentHelper.getDouble(args.get(1));
            double forceY = ArgumentHelper.getDouble(args.get(2));
            double forceZ = args.size() > 3 ? ArgumentHelper.getDouble(args.get(3)) : 0.0;
            w.state.physicsEngine.addForce(objectId, forceX, forceY, forceZ);
            return MethodResult.noReturn();
        })),

        Map.entry("ADDGRAVITYEX", MethodSpec.of((self, args, ctx) -> {
            throw new UnsupportedOperationException("WORLD.ADDGRAVITYEX not implemented");
        })),

        Map.entry("FINDPATH", MethodSpec.of((self, args, ctx) -> {
            WorldVariable w = (WorldVariable) self;
            int objectId = ArgumentHelper.getInt(args.get(0));
            int pointObjectId = ArgumentHelper.getInt(args.get(1));
            int targetX = ArgumentHelper.getInt(args.get(2));
            int targetY = ArgumentHelper.getInt(args.get(3));
            int targetZ = ArgumentHelper.getInt(args.get(4));
            boolean saveIntermediates = ArgumentHelper.getBoolean(args.get(5));
            boolean unknown = args.size() > 6 && ArgumentHelper.getBoolean(args.get(6));
            w.state.physicsEngine.findPath(objectId, pointObjectId, targetX, targetY, targetZ, saveIntermediates, unknown);
            return MethodResult.noReturn();
        })),

        Map.entry("FOLLOWPATH", MethodSpec.of((self, args, ctx) -> {
            WorldVariable w = (WorldVariable) self;
            int objectId = ArgumentHelper.getInt(args.get(0));
            int arrivalRadius = ArgumentHelper.getInt(args.get(1));
            double turnClamp = ArgumentHelper.getDouble(args.get(2));
            double speed = ArgumentHelper.getDouble(args.get(3));
            float outDist = w.state.physicsEngine.followPath(objectId, arrivalRadius, turnClamp, speed);
            return MethodResult.returns(new DoubleValue(outDist));
        })),

        Map.entry("GETANGLE", MethodSpec.of((self, args, ctx) -> {
            WorldVariable w = (WorldVariable) self;
            int objectId = ArgumentHelper.getInt(args.get(0));
            double angle = w.state.physicsEngine.getAngle(objectId);
            return MethodResult.returns(new DoubleValue(Math.toDegrees(angle)));
        })),

        Map.entry("GETBKGPOSX", MethodSpec.of((self, args, ctx) ->
            MethodResult.returns(new IntValue(0)) // stub
        )),

        Map.entry("GETBKGPOSY", MethodSpec.of((self, args, ctx) ->
            MethodResult.returns(new IntValue(0)) // stub
        )),

        Map.entry("GETMOVEDISTANCE", MethodSpec.of((self, args, ctx) -> {
            WorldVariable w = (WorldVariable) self;
            int objectId = ArgumentHelper.getInt(args.get(0));
            return MethodResult.returns(new DoubleValue(w.state.physicsEngine.getMoveDistance(objectId)));
        })),

        Map.entry("GETPOSITIONX", MethodSpec.of((self, args, ctx) -> {
            WorldVariable w = (WorldVariable) self;
            int objectId = ArgumentHelper.getInt(args.get(0));
            double[] position = w.state.physicsEngine.getPosition(objectId);
            if (position == null || position.length < 3) {
                throw new IllegalArgumentException("Object with ID " + objectId + " does not exist or has no position.");
            }
            return MethodResult.returns(new DoubleValue(position[0] + 400));
        })),

        Map.entry("GETPOSITIONY", MethodSpec.of((self, args, ctx) -> {
            WorldVariable w = (WorldVariable) self;
            int objectId = ArgumentHelper.getInt(args.get(0));
            double[] position = w.state.physicsEngine.getPosition(objectId);
            if (position == null || position.length < 3) {
                throw new IllegalArgumentException("Object with ID " + objectId + " does not exist or has no position.");
            }
            return MethodResult.returns(new DoubleValue(300 - position[1]));
        })),

        Map.entry("GETPOSITIONZ", MethodSpec.of((self, args, ctx) -> {
            WorldVariable w = (WorldVariable) self;
            int objectId = ArgumentHelper.getInt(args.get(0));
            double[] position = w.state.physicsEngine.getPosition(objectId);
            if (position == null || position.length < 3) {
                throw new IllegalArgumentException("Object with ID " + objectId + " does not exist or has no position.");
            }
            return MethodResult.returns(new DoubleValue(position[2]));
        })),

        Map.entry("GETROTATIONZ", MethodSpec.of((self, args, ctx) -> {
            WorldVariable w = (WorldVariable) self;
            int objectId = ArgumentHelper.getInt(args.get(0));
            return MethodResult.returns(new DoubleValue(Math.toDegrees(w.state.physicsEngine.getRotationZ(objectId))));
        })),

        Map.entry("GETSPEED", MethodSpec.of((self, args, ctx) -> {
            WorldVariable w = (WorldVariable) self;
            int objectId = ArgumentHelper.getInt(args.get(0));
            double[] speed = w.state.physicsEngine.getSpeed(objectId);
            double v = Math.sqrt(speed[0] * speed[0] + speed[1] * speed[1] + speed[2] * speed[2]);
            return MethodResult.returns(new DoubleValue(v));
        })),

        Map.entry("JOIN", MethodSpec.of((self, args, ctx) -> {
            WorldVariable w = (WorldVariable) self;
            int firstId = ArgumentHelper.getInt(args.get(0));
            int secondId = ArgumentHelper.getInt(args.get(1));
            double anchorX = ArgumentHelper.getDouble(args.get(2));
            double anchorY = ArgumentHelper.getDouble(args.get(3));
            double anchorZ = ArgumentHelper.getDouble(args.get(4));
            double limitMotor = ArgumentHelper.getDouble(args.get(5));
            double lowStop = ArgumentHelper.getDouble(args.get(6));
            double highStop = ArgumentHelper.getDouble(args.get(7));
            double hingeAxisX = (args.size() == 11) ? ArgumentHelper.getDouble(args.get(8)) : 0;
            double hingeAxisY = (args.size() == 11) ? ArgumentHelper.getDouble(args.get(9)) : 0;
            double hingeAxisZ = (args.size() == 11) ? ArgumentHelper.getDouble(args.get(10)) : 1;
            w.state.physicsEngine.addJoint(firstId, secondId, anchorX, anchorY, anchorZ, limitMotor, lowStop, highStop, hingeAxisX, hingeAxisY, hingeAxisZ);
            return MethodResult.noReturn();
        })),

        Map.entry("LINK", MethodSpec.of((self, args, ctx) -> {
            WorldVariable w = (WorldVariable) self;
            int objectId = ArgumentHelper.getInt(args.get(0));
            String objectName = ArgumentHelper.getString(args.get(1));
            Variable objectVariable = ctx.getVariable(objectName);
            if (!(objectVariable instanceof AnimoVariable) && !(objectVariable instanceof ImageVariable)) {
                throw new IllegalArgumentException("Object with name " + objectName + " is not a valid type for linking.");
            }
            w.state.physicsEngine.linkVariable(objectVariable, objectId);
            return MethodResult.noReturn();
        })),

        Map.entry("LOAD", MethodSpec.of((self, args, ctx) -> {
            WorldVariable w = (WorldVariable) self;
            String filename = ArgumentHelper.getString(args.get(0));
            w.state.filename = filename;
            w.state.gameRef = ctx.getGame();
            w.state.physicsEngine.shutdown();
            w.state.physicsEngine.init();
            SEKLoader.loadSek(w);
            return MethodResult.noReturn();
        })),

        Map.entry("MOVEOBJECTS", MethodSpec.of((self, args, ctx) -> {
            WorldVariable w = (WorldVariable) self;
            double elapsedTime = w.state.physicsEngine.stepSimulation();
            return MethodResult.returns(new DoubleValue(elapsedTime));
        })),

        Map.entry("REMOVEOBJECT", MethodSpec.of((self, args, ctx) -> {
            WorldVariable w = (WorldVariable) self;
            int objectId = ArgumentHelper.getInt(args.get(0));
            w.state.physicsEngine.destroyBody(objectId);
            return MethodResult.noReturn();
        })),

        Map.entry("SETACTIVE", MethodSpec.of((self, args, ctx) -> {
            WorldVariable w = (WorldVariable) self;
            int objectId = ArgumentHelper.getInt(args.get(0));
            boolean active = ArgumentHelper.getBoolean(args.get(1));
            boolean collidable = ArgumentHelper.getBoolean(args.get(2));
            w.state.physicsEngine.setActive(objectId, active, collidable);
            return MethodResult.noReturn();
        })),

        Map.entry("SETBKGSIZE", MethodSpec.of((self, args, ctx) -> {
            throw new UnsupportedOperationException("WORLD.SETBKGSIZE not implemented");
        })),

        Map.entry("SETG", MethodSpec.of((self, args, ctx) -> {
            WorldVariable w = (WorldVariable) self;
            w.state.physicsEngine.setG(ArgumentHelper.getInt(args.get(0)), ArgumentHelper.getDouble(args.get(1)));
            return MethodResult.noReturn();
        })),

        Map.entry("SETGRAVITY", MethodSpec.of((self, args, ctx) -> {
            WorldVariable w = (WorldVariable) self;
            double gravityX = ArgumentHelper.getDouble(args.get(0));
            double gravityY = ArgumentHelper.getDouble(args.get(1));
            double gravityZ = ArgumentHelper.getDouble(args.get(2));
            w.state.physicsEngine.setGravity(gravityX, gravityY, gravityZ);
            return MethodResult.noReturn();
        })),

        Map.entry("SETGRAVITYCENTER", MethodSpec.of((self, args, ctx) -> {
            WorldVariable w = (WorldVariable) self;
            int objectId = ArgumentHelper.getInt(args.get(0));
            boolean gravityCenter = ArgumentHelper.getBoolean(args.get(1));
            w.state.physicsEngine.setGravityCenter(objectId, gravityCenter);
            return MethodResult.noReturn();
        })),

        Map.entry("SETLIMIT", MethodSpec.of((self, args, ctx) -> {
            WorldVariable w = (WorldVariable) self;
            int objectId = ArgumentHelper.getInt(args.get(0));
            int minX = ArgumentHelper.getInt(args.get(1));
            int minY = ArgumentHelper.getInt(args.get(2));
            int minZ = ArgumentHelper.getInt(args.get(3));
            int maxX = ArgumentHelper.getInt(args.get(4));
            int maxY = ArgumentHelper.getInt(args.get(5));
            int maxZ = ArgumentHelper.getInt(args.get(6));
            w.state.physicsEngine.setLimit(objectId, minX, minY, minZ, maxX, maxY, maxZ);
            return MethodResult.noReturn();
        })),

        Map.entry("SETMAXSPEED", MethodSpec.of((self, args, ctx) -> {
            WorldVariable w = (WorldVariable) self;
            int objectId = ArgumentHelper.getInt(args.get(0));
            int maxSpeed = ArgumentHelper.getInt(args.get(1));
            w.state.physicsEngine.setMaxVelocity(objectId, maxSpeed);
            return MethodResult.noReturn();
        })),

        Map.entry("SETMOVEFLAGS", MethodSpec.of((self, args, ctx) -> {
            throw new UnsupportedOperationException("WORLD.SETMOVEFLAGS not implemented");
        })),

        Map.entry("SETPOSITION", MethodSpec.of((self, args, ctx) -> {
            WorldVariable w = (WorldVariable) self;
            int objectId = ArgumentHelper.getInt(args.get(0));
            double x = ArgumentHelper.getDouble(args.get(1));
            double y = ArgumentHelper.getDouble(args.get(2));
            double z = ArgumentHelper.getDouble(args.get(3));
            w.state.physicsEngine.setPosition(objectId, x - 400, 300 - y, z);
            return MethodResult.noReturn();
        })),

        Map.entry("SETREFOBJECT", MethodSpec.of((self, args, ctx) -> {
            WorldVariable w = (WorldVariable) self;
            int objectId = ArgumentHelper.getInt(args.get(0));
            w.state.physicsEngine.setReferenceObjectId(objectId);
            return MethodResult.noReturn();
        })),

        Map.entry("SETVELOCITY", MethodSpec.of((self, args, ctx) -> {
            WorldVariable w = (WorldVariable) self;
            int objectId = ArgumentHelper.getInt(args.get(0));
            double x = ArgumentHelper.getDouble(args.get(1));
            double y = ArgumentHelper.getDouble(args.get(2));
            double z = ArgumentHelper.getDouble(args.get(3));
            w.state.physicsEngine.setSpeed(objectId, x, y, z);
            return MethodResult.noReturn();
        })),

        Map.entry("START", MethodSpec.of((self, args, ctx) -> {
            ((WorldVariable) self).state.physicsEngine.start();
            return MethodResult.noReturn();
        })),

        Map.entry("STOP", MethodSpec.of((self, args, ctx) -> {
            ((WorldVariable) self).state.physicsEngine.stop();
            return MethodResult.noReturn();
        })),

        Map.entry("UNLINK", MethodSpec.of((self, args, ctx) -> {
            WorldVariable w = (WorldVariable) self;
            int objectId = ArgumentHelper.getInt(args.get(0));
            w.state.physicsEngine.unlinkVariable(objectId);
            return MethodResult.noReturn();
        }))
    );

    @Override
    public String toString() {
        return "WorldVariable[" + name + "]";
    }
}
