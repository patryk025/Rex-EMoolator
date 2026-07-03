package pl.genschu.bloomooemulator.engine.physics;

import pl.genschu.bloomooemulator.engine.context.EngineVariable;
import pl.genschu.bloomooemulator.world.GameObject;
import pl.genschu.bloomooemulator.world.Mesh;

import java.util.List;

public interface IPhysicsEngine {
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

    void setSpeed(int objectId, double speedX, double speedY, double speedZ);

    void setMass(int objectId, double mass, int geomType);

    void setMass(int objectId, double mass);

    void setGravity(double gravityX, double gravityY, double gravityZ);

    void setGravityCenter(int objectId, boolean gravityCenter);

    void setMaxVelocity(int objectId, double maxVelocity);

    void setLimit(int objectId, double minX, double minY, double minZ, double maxX, double maxY, double maxZ);

    double[] getPosition(int objectId);

    double[] getSpeed(int objectId);

    double getRotationZ(int objectId);

    double getAngle(int objectId);

    double getMoveDistance(int objectId);

    double stepSimulation();

    double stepSimulation(double deltaTime);

    void destroyBody(int objectId);

    void addJoint(int firstId, int secondId, double anchorX, double anchorY, double anchorZ, double limitMotor, double lowStop, double highStop, double hingeAxisX, double hingeAxisY, double hingeAxisZ);

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

    void linkVariable(EngineVariable variable, int objectId);

    void unlinkVariable(int objectId);

    float followPath(int objectId, int arrivalRadius, double turnClamp, double speed);

    void findPath(int objectId, int pointObjectId, int targetX, int targetY, int targetZ, boolean saveIntermediates, boolean unknown);

    void start();

    void stop();

    List<GameObject> getGameObjects();

    void shutdown();

    void dumpGeometryData(String path);
}
