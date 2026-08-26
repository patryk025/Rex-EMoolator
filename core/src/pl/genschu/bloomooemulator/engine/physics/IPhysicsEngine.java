package pl.genschu.bloomooemulator.engine.physics;

import pl.genschu.bloomooemulator.engine.compatibility.CompatibilityProfile;
import pl.genschu.bloomooemulator.engine.context.EngineVariable;
import pl.genschu.bloomooemulator.geometry.coordinates.CanvasScroll;
import pl.genschu.bloomooemulator.geometry.coordinates.PhysicsBox;
import pl.genschu.bloomooemulator.geometry.coordinates.PhysicsPoint;
import pl.genschu.bloomooemulator.world.GameObject;
import pl.genschu.bloomooemulator.world.Mesh;

import java.util.List;

public interface IPhysicsEngine {
    /**
     * Supplies engine- and game-specific compatibility information before init.
     * Implementations which do not need it may ignore it.
     */
    default void configureCompatibility(CompatibilityProfile profile) {}

    void init();

    void createBody(
            int objectId,
            double mass,
            double mu,
            double friction,
            double bounce,
            double bounceVelocity,
            double maxVelocity,
            int bodyType,
            int geomType,
            double dim0,
            double dim1,
            double dim2
    );

    void createBody(
            GameObject gameObject,
            Mesh geometryMesh
    );

    void addForce(int objectId, double forceX, double forceY, double forceZ);

    void addForceAt(int objectId, double forceX, double forceY, double forceZ, double posX, double posY, double posZ);

    void setPosition(int objectId, double x, double y, double z);

    /** Sets a body position that is already expressed in native Sekai/ODE coordinates. */
    default void setPosition(int objectId, PhysicsPoint position) {
        setPosition(objectId, position.x(), position.y(), position.z());
    }

    void setSpeed(int objectId, double speedX, double speedY, double speedZ);

    /**
     * Writes the linear velocity during Sekai's post-step damping without
     * invoking the extra angular reset performed by script-facing SetVelocity.
     */
    void setDampedSpeed(int objectId, double speedX, double speedY, double speedZ);

    void setMass(int objectId, double mass, int geomType);

    void setMass(int objectId, double mass);

    void setGravity(double gravityX, double gravityY, double gravityZ);

    void setGravityCenter(int objectId, boolean gravityCenter);

    void setMaxVelocity(int objectId, double maxVelocity);

    void setLimit(int objectId, double minX, double minY, double minZ, double maxX, double maxY, double maxZ);

    /** Sets normalized bounds that are already expressed in native Sekai/ODE coordinates. */
    default void setLimit(int objectId, PhysicsBox bounds) {
        setLimit(
                objectId,
                bounds.min().x(), bounds.min().y(), bounds.min().z(),
                bounds.max().x(), bounds.max().y(), bounds.max().z());
    }

    double[] getPosition(int objectId);

    /** Returns a body position in native Sekai/ODE coordinates. */
    default PhysicsPoint getPhysicsPosition(int objectId) {
        double[] position = getPosition(objectId);
        if (position == null || position.length < 3) {
            return null;
        }
        return new PhysicsPoint(position[0], position[1], position[2]);
    }

    double[] getSpeed(int objectId);

    double getRotationZ(int objectId);

    double getAngle(int objectId);

    double getMoveDistance(int objectId);

    double stepSimulation();

    double stepSimulation(double deltaTime);

    void destroyBody(int objectId);

    void addJoint(int firstId, int secondId, double anchorX, double anchorY, double anchorZ, double limitMotor, double lowStop, double highStop, double hingeAxisX, double hingeAxisY, double hingeAxisZ);

    /** Adds a joint whose anchor is already expressed in native Sekai/ODE coordinates. */
    default void addJoint(
            int firstId,
            int secondId,
            PhysicsPoint anchor,
            double limitMotor,
            double lowStop,
            double highStop,
            double hingeAxisX,
            double hingeAxisY,
            double hingeAxisZ
    ) {
        addJoint(
                firstId, secondId,
                anchor.x(), anchor.y(), anchor.z(),
                limitMotor, lowStop, highStop,
                hingeAxisX, hingeAxisY, hingeAxisZ);
    }

    void addJoint2(int firstId, int secondId,
                   double anchorX, double anchorY, double anchorZ,
                   double axis1X, double axis1Y, double axis1Z,
                   double axis2X, double axis2Y, double axis2Z);

    void breakJoint(int objectId);

    void jointSteer(int objectId, double angle);

    void jointSpeed(int objectId, double speed);

    void zeroAll(int objectId);

    void rotate(int objectId, double angleDegrees);

    double getRotationX(int objectId);

    double getRotationY(int objectId);

    boolean getCollision(int objectId);

    boolean getCollision(int objectId, int otherId);

    void setCollisionType(int objectId, int collisionType);

    void setLinkPaused(int objectId, boolean paused);

    void setBodyProperties(int objectId, double mass, double sizeX, double sizeY, double sizeZ);

    void setBodyDynamics(int objectId, double mu, double friction, double bounce, double bounceVelocity, double maxVelocity);

    void setGravityExclusion(int objectId, int centerId, boolean excluded);

    void setG(int objectId, double g);

    void setActive(int objectId, boolean active, boolean monitorCollisions);

    void setReferenceObjectId(int referenceObjectId);

    void setBkgSize(double minX, double maxX, double minY, double maxY);

    void setMoveFlags(double moveX, double moveY);

    int getBkgPosX();

    int getBkgPosY();

    /** Returns the exact camera displacement along the DirectDraw canvas axes. */
    default CanvasScroll getCanvasScroll() {
        return new CanvasScroll(getBkgPosX(), getBkgPosY());
    }

    void linkVariable(EngineVariable variable, int objectId);

    /**
     * Links a graphical variable and registers a callback for spatial-index invalidation after
     * a physics-driven position update. Implementations without link callbacks may ignore it.
     */
    default void linkVariable(
            EngineVariable variable,
            int objectId,
            Runnable onPositionChanged
    ) {
        linkVariable(variable, objectId);
    }

    void unlinkVariable(int objectId);

    float followPath(int objectId, int arrivalRadius, double turnClamp, double speed);

    void findPath(
            int objectId,
            int pointObjectId,
            PhysicsPoint target,
            boolean saveIntermediates,
            boolean unknown);

    void start();

    void stop();

    List<GameObject> getGameObjects();

    void shutdown();

    void dumpGeometryData(String path);
}
