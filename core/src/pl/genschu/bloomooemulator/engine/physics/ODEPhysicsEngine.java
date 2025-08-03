package pl.genschu.bloomooemulator.engine.physics;

import org.ode4j.math.DVector3C;
import org.ode4j.ode.*;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.interpreter.variable.types.AnimoVariable;
import pl.genschu.bloomooemulator.interpreter.variable.types.ImageVariable;

import java.util.HashMap;
import java.util.Map;

public class ODEPhysicsEngine implements IPhysicsEngine {
    DWorld world;
    DSpace space;
    DJointGroup contactGroup;
    private final Map<Integer, DBody> bodies = new HashMap<>();
    private final Map<DBody, Variable> linkedVariables = new HashMap<>();

    @Override
    public void init() {
        OdeHelper.initODE2(0);

        world = OdeHelper.createWorld();
        world.setGravity(0, 0, -9.8100004); // Default gravity in Sekai
        world.setCFM(0.00001f);
        world.setERP(0.8f);

        space = OdeHelper.createSimpleSpace();

        contactGroup = OdeHelper.createJointGroup();
    }

    @Override
    public void createBody(int objectId, double mass, double mu, double mu2, double bounce, double bounceVelocity, double maxVelocity, int bodyType, int geomType, double x, double y, double z) {
        DBody body = OdeHelper.createBody(world);
        body.setPosition(x, y, z);

        bodies.put(objectId, body);

        // set mass
        setMass(objectId, mass, geomType);

        // create geometry
        // TODO: check lengths in Sekai
        switch (geomType) {
            case 0: // box
                DBox box = OdeHelper.createBox(1, 1, 1);
                box.setBody(body);
                break;
            case 1: // cylinder
                DCylinder cylinder = OdeHelper.createCylinder(1, 1);
                cylinder.setBody(body);
                break;
            case 2: // sphere
                DSphere sphere = OdeHelper.createSphere(1);
                sphere.setBody(body);
                break;
        }

        // TODO: move GameObject here and save as user data
        // body.setData();

        // set physical properties
    }

    @Override
    public void setPosition(int objectId, double x, double y, double z) {
        DBody body = bodies.get(objectId);
        body.setPosition(x, y, z);
        if(linkedVariables.containsKey(body)) {
            Variable var = linkedVariables.get(body);
            if(var instanceof ImageVariable) {
                ImageVariable imageVar = (ImageVariable) var;
                imageVar.setPosX((int) x);
                imageVar.setPosY((int) y);
                // TODO: scaling image based on Z axis
                imageVar.updateRect();
            }
            if(var instanceof AnimoVariable) {
                AnimoVariable animoVar = (AnimoVariable) var;
                animoVar.setPosX((int) x);
                animoVar.setPosY((int) y);
                // TODO: scaling image based on Z axis
                animoVar.updateRect();
            }
        }
    }

    @Override
    public void setSpeed(int objectId, double speedX, double speedY, double speedZ) {
        DBody body = bodies.get(objectId);
        body.setLinearVel(speedX, speedY, speedZ);
    }

    @Override
    public void setMass(int objectId, double mass, int geomType) {
        DBody body = bodies.get(objectId);
        DMass m = OdeHelper.createMass();
        switch (geomType) {
            case 0: // box
                m.setBoxTotal(mass, 1, 1, 1);
                break;
            case 1: // cylinder
                m.setCylinderTotal(mass, 3, 1, 1); // axis, radius, length
                break;
            case 2: // sphere
                m.setSphereTotal(mass, 1);
                break;
        }
        body.setMass(m);
    }

    @Override
    public void setGravity(double gravityX, double gravityY, double gravityZ) {
        world.setGravity(gravityX, gravityY, gravityZ);
    }

    @Override
    public double[] getPosition(int objectId) {
        DBody body = bodies.get(objectId);
        DVector3C position = body.getPosition();
        return position.toDoubleArray();
    }

    @Override
    public void stepSimulation(double deltaTime) {
        if(deltaTime > 0.3f) deltaTime = 0.3f; // that limit was in Sekai
        //world.step(deltaTime);
        world.quickStep(deltaTime);
    }

    @Override
    public void destroyBody(int objectId) {
        DBody body = bodies.remove(objectId);
        body.destroy();
    }

    @Override
    public void linkVariable(Variable variable, int objectId) {
        DBody body = bodies.get(objectId);
        linkedVariables.put(body, variable);
    }

    @Override
    public void shutdown() {
        world.destroy();
        space.destroy();
        contactGroup.destroy();
        OdeHelper.closeODE();
    }
}
