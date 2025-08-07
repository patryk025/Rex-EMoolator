package pl.genschu.bloomooemulator.engine.physics;

import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.world.GameObject;
import pl.genschu.bloomooemulator.world.Mesh;

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
            double x,
            double y,
            double z
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
            double x,
            double y,
            double z,
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

    double[] getPosition(int objectId);

    double getSpeed(int objectId);

    void stepSimulation(double deltaTime);

    void destroyBody(int objectId);

    void linkVariable(Variable variable, int objectId);

    void shutdown();
}
