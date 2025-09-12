package pl.genschu.bloomooemulator.engine.physics;

import pl.genschu.bloomooemulator.interpreter.variable.Variable;
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
            double dim2,
            Mesh geometryMesh
    );

    void createBody(
            GameObject gameObject,
            Mesh geometryMesh
    );

    void addForce(int objectId, double forceX, double forceY, double forceZ);

    void setPosition(int objectId, double x, double y, double z);

    void setSpeed(int objectId, double speedX, double speedY, double speedZ);

    void setMass(int objectId, double mass, int geomType);

    void setGravity(double gravityX, double gravityY, double gravityZ);

    void setGravityCenter(int objectId, boolean gravityCenter);

    void setMaxVelocity(int objectId, double maxVelocity);

    void setLimit(int objectId, double minX, double minY, double minZ, double maxX, double maxY, double maxZ);

    double[] getPosition(int objectId);

    double[] getSpeed(int objectId);

    double getRotationZ(int objectId);

    double getAngle(int objectId);

    double getMoveDistance(int objectId);

    void stepSimulation();

    void stepSimulation(double deltaTime);

    void destroyBody(int objectId);

    void addJoint(int firstId, int secondId, double anchorX, double anchorY, double anchorZ, double limitMotor, double lowStop, double highStop, double hingeAxisX, double hingeAxisY, double hingeAxisZ);

    void setG(int objectId, double g);

    void setActive(int objectId, boolean active, boolean unknown);

    void setReferenceObjectId(int referenceObjectId);

    void linkVariable(Variable variable, int objectId);

    void unlinkVariable(int objectId);

    void start();

    void stop();

    List<GameObject> getGameObjects();

    void shutdown();

    void dumpGeometryData(String path);
}
