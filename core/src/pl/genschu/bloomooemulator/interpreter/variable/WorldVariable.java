package pl.genschu.bloomooemulator.interpreter.variable;

import com.badlogic.gdx.Gdx;
import pl.genschu.bloomooemulator.annotations.InternalMutable;
import pl.genschu.bloomooemulator.engine.Game;
import pl.genschu.bloomooemulator.engine.physics.IPhysicsEngine;
import pl.genschu.bloomooemulator.engine.physics.ODEPhysicsEngine;
import pl.genschu.bloomooemulator.interpreter.context.Context;
import pl.genschu.bloomooemulator.interpreter.helpers.ArgumentHelper;
import pl.genschu.bloomooemulator.interpreter.values.*;
import pl.genschu.bloomooemulator.interpreter.variable.capabilities.Initializable;
import pl.genschu.bloomooemulator.loader.SEKLoadable;
import pl.genschu.bloomooemulator.loader.SEKLoader;
import pl.genschu.bloomooemulator.utils.FileUtils;

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
) implements Variable, SEKLoadable, Initializable {

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

    @Override
    public void init(Context context) {
        String filename = context.attributes().get(name, "FILENAME");
        if (filename != null) {
            state.filename = filename;
        }
        reload(context.getGame());
    }

    private void reload(Game game) {
        state.gameRef = game;
        state.physicsEngine.shutdown();
        state.physicsEngine.init();

        if (game == null || state.filename == null || state.filename.isBlank()) {
            return;
        }

        String vfsPath = FileUtils.resolveVfsPath(game, state.filename);
        try (java.io.InputStream is = game.getVfs().openRead(vfsPath)) {
            SEKLoader.loadSek(this, is);
        } catch (java.io.IOException e) {
            Gdx.app.error("WorldVariable", "Failed to open SEK via VFS: " + vfsPath, e);
        }
    }

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
            if (args.size() >= 7) {
                // Sekai maps the 7-argument form to AddForceAt (force + application point).
                double forceZ = ArgumentHelper.getDouble(args.get(3));
                double posX = ArgumentHelper.getDouble(args.get(4));
                double posY = ArgumentHelper.getDouble(args.get(5));
                double posZ = ArgumentHelper.getDouble(args.get(6));
                w.state.physicsEngine.addForceAt(objectId, forceX, forceY, forceZ, posX, posY, posZ);
                return MethodResult.noReturn();
            }
            double forceZ = args.size() > 3 ? ArgumentHelper.getDouble(args.get(3)) : 0.0;
            w.state.physicsEngine.addForce(objectId, forceX, forceY, forceZ);
            return MethodResult.noReturn();
        })),

        Map.entry("ADDGRAVITYEX", MethodSpec.of((self, args, ctx) -> {
            WorldVariable w = (WorldVariable) self;
            int objectId = ArgumentHelper.getInt(args.get(0));
            int centerId = ArgumentHelper.getInt(args.get(1));
            // TRUE excludes the magnet (centerId) from acting on objectId, FALSE re-enables it.
            boolean exclude = args.size() <= 2 || ArgumentHelper.getBoolean(args.get(2));
            w.state.physicsEngine.setGravityExclusion(objectId, centerId, exclude);
            return MethodResult.noReturn();
        })),

        Map.entry("ADDOBJECT", MethodSpec.of((self, args, ctx) -> {
            WorldVariable w = (WorldVariable) self;
            int objectId = ArgumentHelper.getInt(args.get(0));
            double x = ArgumentHelper.getDouble(args.get(1));
            double y = ArgumentHelper.getDouble(args.get(2));
            double z = ArgumentHelper.getDouble(args.get(3));
            double dim0 = ArgumentHelper.getDouble(args.get(4));
            double dim1 = ArgumentHelper.getDouble(args.get(5));
            double dim2 = ArgumentHelper.getDouble(args.get(6));
            double mass = ArgumentHelper.getDouble(args.get(7));
            double maxSpeed = ArgumentHelper.getDouble(args.get(9));
            // Legacy mover: a dynamic sphere with Sekai's AddObject defaults
            // (mu=-1, bounce=0.1, bounceVel=0.1); args.get(8) fed SetRigid, a no-op stub.
            w.state.physicsEngine.createBody(objectId, mass, -1.0, 0.0, 0.1, 0.1, maxSpeed, 1, 2, dim0, dim1, dim2);
            w.state.physicsEngine.setPosition(objectId, x - 400, 300 - y, z);
            return MethodResult.noReturn();
        })),

        Map.entry("BREAK", MethodSpec.of((self, args, ctx) -> {
            WorldVariable w = (WorldVariable) self;
            w.state.physicsEngine.breakJoint(ArgumentHelper.getInt(args.get(0)));
            return MethodResult.noReturn();
        })),

        Map.entry("ZEROALL", MethodSpec.of((self, args, ctx) -> {
            WorldVariable w = (WorldVariable) self;
            w.state.physicsEngine.zeroAll(ArgumentHelper.getInt(args.get(0)));
            return MethodResult.noReturn();
        })),

        Map.entry("FINDPATH", MethodSpec.of((self, args, ctx) -> {
            WorldVariable w = (WorldVariable) self;
            int objectId = ArgumentHelper.getInt(args.get(0));
            int pointObjectId = ArgumentHelper.getInt(args.get(1));
            int targetX = ArgumentHelper.getInt(args.get(2));
            int targetY = ArgumentHelper.getInt(args.get(3));
            int targetZ = ArgumentHelper.getInt(args.get(4));
            boolean saveIntermediates = args.size() > 5 && ArgumentHelper.getBoolean(args.get(5));
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
            // Scripts expect a 0..360 heading (e.g. direction index = (GETANGLE+22.5)/45),
            // but atan2 yields -180..180 — negative (downward) headings gave wrong/negative
            // indices, so the facing only updated near the horizontal. Normalise to [0, 360).
            double degrees = Math.toDegrees(angle) % 360.0;
            if (degrees < 0) degrees += 360.0;
            return MethodResult.returns(new DoubleValue(degrees));
        })),

        Map.entry("GETBKGPOSX", MethodSpec.of((self, args, ctx) -> {
            WorldVariable w = (WorldVariable) self;
            return MethodResult.returns(new IntValue(w.state.physicsEngine.getBkgPosX()));
        })),

        Map.entry("GETBKGPOSY", MethodSpec.of((self, args, ctx) -> {
            WorldVariable w = (WorldVariable) self;
            return MethodResult.returns(new IntValue(w.state.physicsEngine.getBkgPosY()));
        })),

        Map.entry("GETCOLLISION", MethodSpec.of((self, args, ctx) -> {
            WorldVariable w = (WorldVariable) self;
            int objectId = ArgumentHelper.getInt(args.get(0));
            boolean colliding = (args.size() > 1)
                    ? w.state.physicsEngine.getCollision(objectId, ArgumentHelper.getInt(args.get(1)))
                    : w.state.physicsEngine.getCollision(objectId);
            return MethodResult.returns(new BoolValue(colliding));
        })),

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
            // World.dll uses the camera origin maintained by CWorld::MoveObjects
            // (world + 0xe8), not a fixed canvas centre. With a scrolling
            // background this is equivalent to 400 + worldX - bkgPosX.
            return MethodResult.returns(new DoubleValue(
                    position[0] + 400 - w.state.physicsEngine.getBkgPosX()));
        })),

        Map.entry("GETPOSITIONY", MethodSpec.of((self, args, ctx) -> {
            WorldVariable w = (WorldVariable) self;
            int objectId = ArgumentHelper.getInt(args.get(0));
            double[] position = w.state.physicsEngine.getPosition(objectId);
            if (position == null || position.length < 3) {
                throw new IllegalArgumentException("Object with ID " + objectId + " does not exist or has no position.");
            }
            // Disassembly of World.dll FUN_100020d0: [CWorld+0xec] - worldY.
            // CWorld+0xec is 300 - bkgPosY, so GETPOSITIONY already includes
            // the current camera scroll.
            return MethodResult.returns(new DoubleValue(
                    300 - position[1] - w.state.physicsEngine.getBkgPosY()));
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

        Map.entry("GETROTATIONX", MethodSpec.of((self, args, ctx) -> {
            WorldVariable w = (WorldVariable) self;
            int objectId = ArgumentHelper.getInt(args.get(0));
            return MethodResult.returns(new DoubleValue(Math.toDegrees(w.state.physicsEngine.getRotationX(objectId))));
        })),

        Map.entry("GETROTATIONY", MethodSpec.of((self, args, ctx) -> {
            WorldVariable w = (WorldVariable) self;
            int objectId = ArgumentHelper.getInt(args.get(0));
            return MethodResult.returns(new DoubleValue(Math.toDegrees(w.state.physicsEngine.getRotationY(objectId))));
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
            int n = args.size();
            if (n != 6 && n != 8 && n != 11) {
                com.badlogic.gdx.Gdx.app.error("WorldVariable", "JOIN: unsupported argument count " + n + " (expected 6, 8 or 11), ignored");
                return MethodResult.noReturn();
            }
            int firstId = ArgumentHelper.getInt(args.get(0));
            int secondId = ArgumentHelper.getInt(args.get(1));
            // Anchor comes in screen coordinates, stops in degrees.
            double anchorX = ArgumentHelper.getDouble(args.get(2)) - 400;
            double anchorY = 300 - ArgumentHelper.getDouble(args.get(3));
            double anchorZ = ArgumentHelper.getDouble(args.get(4));
            double limitMotor = ArgumentHelper.getDouble(args.get(5));
            double lowStop = Math.toRadians((n >= 8) ? ArgumentHelper.getDouble(args.get(6)) : -90.0);
            double highStop = Math.toRadians((n >= 8) ? ArgumentHelper.getDouble(args.get(7)) : 90.0);
            double hingeAxisX = (n == 11) ? ArgumentHelper.getDouble(args.get(8)) : 0;
            double hingeAxisY = (n == 11) ? ArgumentHelper.getDouble(args.get(10)) : 1;
            double hingeAxisZ = (n == 11) ? ArgumentHelper.getDouble(args.get(9)) : 0;
            w.state.physicsEngine.addJoint(firstId, secondId, anchorX, anchorY, anchorZ, limitMotor, lowStop, highStop, hingeAxisX, hingeAxisY, hingeAxisZ);
            return MethodResult.noReturn();
        })),

        Map.entry("JOIN2", MethodSpec.of((self, args, ctx) -> {
            WorldVariable w = (WorldVariable) self;
            int firstId = ArgumentHelper.getInt(args.get(0));
            int secondId = ArgumentHelper.getInt(args.get(1));
            double[] v = new double[9];
            for (int i = 0; i < 9; i++) {
                v[i] = ArgumentHelper.getDouble(args.get(2 + i));
            }
            // Unlike JOIN, the anchor is already in world coordinates
            // (no screen transform in the original either).
            w.state.physicsEngine.addJoint2(firstId, secondId,
                    v[0], v[1], v[2],
                    v[3], v[5], v[4],
                    v[6], v[8], v[7]);
            return MethodResult.noReturn();
        })),

        Map.entry("JOINTSTEER", MethodSpec.of((self, args, ctx) -> {
            WorldVariable w = (WorldVariable) self;
            w.state.physicsEngine.jointSteer(ArgumentHelper.getInt(args.get(0)), ArgumentHelper.getDouble(args.get(1)));
            return MethodResult.noReturn();
        })),

        Map.entry("JOINTSPEED", MethodSpec.of((self, args, ctx) -> {
            WorldVariable w = (WorldVariable) self;
            w.state.physicsEngine.jointSpeed(ArgumentHelper.getInt(args.get(0)), ArgumentHelper.getDouble(args.get(1)));
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
            w.state.filename = ArgumentHelper.getString(args.get(0));
            w.reload(ctx.getGame());
            return MethodResult.noReturn();
        })),

        Map.entry("MOVEOBJECTS", MethodSpec.of((self, args, ctx) -> {
            WorldVariable w = (WorldVariable) self;
            double elapsedTime = w.state.physicsEngine.stepSimulation();
            return MethodResult.returns(new DoubleValue(elapsedTime));
        })),

        Map.entry("PAUSELINK", MethodSpec.of((self, args, ctx) -> {
            WorldVariable w = (WorldVariable) self;
            w.state.physicsEngine.setLinkPaused(ArgumentHelper.getInt(args.get(0)), true);
            return MethodResult.noReturn();
        })),

        Map.entry("RESUMELINK", MethodSpec.of((self, args, ctx) -> {
            WorldVariable w = (WorldVariable) self;
            w.state.physicsEngine.setLinkPaused(ArgumentHelper.getInt(args.get(0)), false);
            return MethodResult.noReturn();
        })),

        Map.entry("REMOVEOBJECT", MethodSpec.of((self, args, ctx) -> {
            WorldVariable w = (WorldVariable) self;
            int objectId = ArgumentHelper.getInt(args.get(0));
            w.state.physicsEngine.destroyBody(objectId);
            return MethodResult.noReturn();
        })),

        Map.entry("ROTATE", MethodSpec.of((self, args, ctx) -> {
            WorldVariable w = (WorldVariable) self;
            int objectId = ArgumentHelper.getInt(args.get(0));
            // Sekai's Rotate ignores the middle (direction) argument and sets an absolute angle (why they done that?).
            double angle = ArgumentHelper.getDouble(args.get(args.size() > 2 ? 2 : 1));
            w.state.physicsEngine.rotate(objectId, angle);
            return MethodResult.noReturn();
        })),

        Map.entry("SETACTIVE", MethodSpec.of((self, args, ctx) -> {
            WorldVariable w = (WorldVariable) self;
            int objectId = ArgumentHelper.getInt(args.get(0));
            boolean active = ArgumentHelper.getBoolean(args.get(1));
            boolean collidable = args.size() > 2 ? ArgumentHelper.getBoolean(args.get(2)) : active;
            w.state.physicsEngine.setActive(objectId, active, collidable);
            return MethodResult.noReturn();
        })),

        Map.entry("SETBKGSIZE", MethodSpec.of((self, args, ctx) -> {
            WorldVariable w = (WorldVariable) self;
            double minX = ArgumentHelper.getDouble(args.get(0));
            double maxX = ArgumentHelper.getDouble(args.get(1));
            double minY = ArgumentHelper.getDouble(args.get(2));
            double maxY = ArgumentHelper.getDouble(args.get(3));
            w.state.physicsEngine.setBkgSize(minX, maxX, minY, maxY);
            return MethodResult.noReturn();
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
            double minX = ArgumentHelper.getDouble(args.get(1));
            double minY = ArgumentHelper.getDouble(args.get(2));
            double minZ = ArgumentHelper.getDouble(args.get(3));
            double maxX = ArgumentHelper.getDouble(args.get(4));
            double maxY = ArgumentHelper.getDouble(args.get(5));
            double maxZ = ArgumentHelper.getDouble(args.get(6));
            // CWorld converts the screen-space box to Sekai's centred, Y-up world space.
            w.state.physicsEngine.setLimit(objectId,
                    minX - 400, 300 - maxY, minZ,
                    maxX - 400, 300 - minY, maxZ);
            return MethodResult.noReturn();
        })),

        Map.entry("SETMASS", MethodSpec.of((self, args, ctx) -> {
            WorldVariable w = (WorldVariable) self;
            int objectId = ArgumentHelper.getInt(args.get(0));
            double mass = ArgumentHelper.getDouble(args.get(1));
            w.state.physicsEngine.setMass(objectId, mass);
            return MethodResult.noReturn();
        })),

        Map.entry("SETMAXSPEED", MethodSpec.of((self, args, ctx) -> {
            WorldVariable w = (WorldVariable) self;
            int objectId = ArgumentHelper.getInt(args.get(0));
            int maxSpeed = ArgumentHelper.getInt(args.get(1));
            w.state.physicsEngine.setMaxVelocity(objectId, maxSpeed);
            return MethodResult.noReturn();
        })),

        Map.entry("SETCOLLISIONTYPE", MethodSpec.of((self, args, ctx) -> {
            WorldVariable w = (WorldVariable) self;
            w.state.physicsEngine.setCollisionType(ArgumentHelper.getInt(args.get(0)), ArgumentHelper.getInt(args.get(1)));
            return MethodResult.noReturn();
        })),

        Map.entry("SETBODYPROPERTIES", MethodSpec.of((self, args, ctx) -> {
            WorldVariable w = (WorldVariable) self;
            w.state.physicsEngine.setBodyProperties(
                    ArgumentHelper.getInt(args.get(0)),
                    ArgumentHelper.getDouble(args.get(1)),
                    ArgumentHelper.getDouble(args.get(2)),
                    ArgumentHelper.getDouble(args.get(3)),
                    ArgumentHelper.getDouble(args.get(4)));
            return MethodResult.noReturn();
        })),

        Map.entry("SETBODYDYNAMICS", MethodSpec.of((self, args, ctx) -> {
            WorldVariable w = (WorldVariable) self;
            w.state.physicsEngine.setBodyDynamics(
                    ArgumentHelper.getInt(args.get(0)),
                    ArgumentHelper.getDouble(args.get(1)),
                    ArgumentHelper.getDouble(args.get(2)),
                    ArgumentHelper.getDouble(args.get(3)),
                    ArgumentHelper.getDouble(args.get(4)),
                    ArgumentHelper.getDouble(args.get(5)));
            return MethodResult.noReturn();
        })),

        Map.entry("SETFORCE", MethodSpec.of(WorldVariable::setForceHandler)),
        Map.entry("SETOBJECTFORCE", MethodSpec.of(WorldVariable::setForceHandler)),

        Map.entry("SETPOSITIONCOORD", MethodSpec.of((self, args, ctx) -> {
            WorldVariable w = (WorldVariable) self;
            int objectId = ArgumentHelper.getInt(args.get(0));
            int coordIndex = ArgumentHelper.getInt(args.get(1));
            double value = ArgumentHelper.getDouble(args.get(2));
            double[] position = w.state.physicsEngine.getPosition(objectId);
            if (coordIndex == 0) {
                position[0] = value - 400;
            } else if (coordIndex == 1) {
                position[1] = 300 - value;
            } else {
                return MethodResult.noReturn(); // original leaves other indices untouched
            }
            w.state.physicsEngine.setPosition(objectId, position[0], position[1], position[2]);
            return MethodResult.noReturn();
        })),

        // This methods are no-ops (exports returns E_FAIL)
        Map.entry("SETANGLE", MethodSpec.of((self, args, ctx) -> MethodResult.noReturn())),
        Map.entry("SETSPEED", MethodSpec.of((self, args, ctx) -> MethodResult.noReturn())),
        Map.entry("SETDISPERSION", MethodSpec.of((self, args, ctx) -> MethodResult.noReturn())),
        Map.entry("SETALWAYSACTIVE", MethodSpec.of((self, args, ctx) -> MethodResult.noReturn())),
        Map.entry("ENABLEMESH", MethodSpec.of((self, args, ctx) -> MethodResult.noReturn())),
        Map.entry("COLLIDEMESH", MethodSpec.of((self, args, ctx) -> MethodResult.noReturn())),
        Map.entry("USEFF", MethodSpec.of((self, args, ctx) -> MethodResult.noReturn())),
        Map.entry("ADDMESHWAYPOINT", MethodSpec.of((self, args, ctx) -> MethodResult.noReturn())),
        Map.entry("ISMESHENABLED", MethodSpec.of((self, args, ctx) -> MethodResult.returns(new BoolValue(false)))),
        Map.entry("ISMESHCOLLIDING", MethodSpec.of((self, args, ctx) -> MethodResult.returns(new BoolValue(false)))),

        // Navigation methods not reimplemented yet
        Map.entry("SETOBJECTPROPERTIES", MethodSpec.of((self, args, ctx) -> {
            Gdx.app.log("WorldVariable", "SETOBJECTPROPERTIES is not implemented yet");
            return MethodResult.noReturn();
        })),
        Map.entry("FOLLOW", MethodSpec.of((self, args, ctx) -> {
            Gdx.app.log("WorldVariable", "FOLLOW is not implemented yet");
            return MethodResult.noReturn();
        })),
        Map.entry("MOVETO", MethodSpec.of((self, args, ctx) -> {
            Gdx.app.log("WorldVariable", "MOVETO is not implemented yet");
            return MethodResult.noReturn();
        })),
        Map.entry("MOVETOPATH", MethodSpec.of((self, args, ctx) -> {
            Gdx.app.log("WorldVariable", "MOVETOPATH is not implemented yet");
            return MethodResult.noReturn();
        })),
        Map.entry("GOTOPATH", MethodSpec.of((self, args, ctx) -> {
            Gdx.app.log("WorldVariable", "GOTOPATH is not implemented yet");
            return MethodResult.noReturn();
        })),
        Map.entry("ADDTOPATH", MethodSpec.of((self, args, ctx) -> {
            Gdx.app.log("WorldVariable", "ADDTOPATH is not implemented yet");
            return MethodResult.noReturn();
        })),
        Map.entry("ADDWAYPOINT", MethodSpec.of((self, args, ctx) -> {
            Gdx.app.log("WorldVariable", "ADDWAYPOINT is not implemented yet");
            return MethodResult.noReturn();
        })),
        Map.entry("REMOVEWAYPOINT", MethodSpec.of((self, args, ctx) -> {
            Gdx.app.log("WorldVariable", "REMOVEWAYPOINT is not implemented yet");
            return MethodResult.noReturn();
        })),
        Map.entry("REMOVEROUTE", MethodSpec.of((self, args, ctx) -> {
            Gdx.app.log("WorldVariable", "REMOVEROUTE is not implemented yet");
            return MethodResult.noReturn();
        })),
        Map.entry("FOLLOWROUTE", MethodSpec.of((self, args, ctx) -> {
            Gdx.app.log("WorldVariable", "FOLLOWROUTE is not implemented yet");
            return MethodResult.noReturn();
        })),

        Map.entry("SETMOVEFLAGS", MethodSpec.of((self, args, ctx) -> {
            WorldVariable w = (WorldVariable) self;
            double moveX = ArgumentHelper.getDouble(args.get(0));
            double moveY = ArgumentHelper.getDouble(args.get(1));
            w.state.physicsEngine.setMoveFlags(moveX, moveY);
            return MethodResult.noReturn();
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

    private static MethodResult setForceHandler(Variable self, java.util.List<Value> args, MethodContext ctx) {
        WorldVariable w = (WorldVariable) self;
        int objectId = ArgumentHelper.getInt(args.get(0));
        double forceX = ArgumentHelper.getDouble(args.get(1));
        double forceY = ArgumentHelper.getDouble(args.get(2));
        double forceZ = args.size() > 3 ? ArgumentHelper.getDouble(args.get(3)) : 0.0;
        w.state.physicsEngine.addForce(objectId, forceX, forceY, forceZ);
        return MethodResult.noReturn();
    }

    @Override
    public String toString() {
        return "WorldVariable[" + name + "]";
    }
}
