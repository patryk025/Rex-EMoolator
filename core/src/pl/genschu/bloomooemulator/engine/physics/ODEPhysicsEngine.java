package pl.genschu.bloomooemulator.engine.physics;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import org.ode4j.math.DMatrix3C;
import org.ode4j.math.DVector3C;
import org.ode4j.ode.*;
import pl.genschu.bloomooemulator.engine.physics.camera.CameraAnchor;
import pl.genschu.bloomooemulator.engine.physics.pathfinding.AStar;
import pl.genschu.bloomooemulator.engine.physics.pathfinding.Graph;
import pl.genschu.bloomooemulator.geometry.points.Point3D;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.interpreter.variable.types.IntegerVariable;
import pl.genschu.bloomooemulator.world.*;

import java.util.*;
import java.util.stream.Collectors;

import static org.ode4j.ode.OdeConstants.*;

public class ODEPhysicsEngine implements IPhysicsEngine {
    DWorld world;
    DSpace space;
    ODEPhysicsTimer timer;
    DJointGroup jointGroup;
    CameraAnchor cameraAnchor;
    private final Map<Integer, List<GameObject>> objects = new HashMap<>();
    private final Map<DBody, Variable> linkedVariables = new HashMap<>();
    private int cameraX = 0;
    private int cameraY = 0;
    private int referenceObjectId = 0;
    private float velEps = 0.001f;
    private final List<DTriMeshData> triMeshDatas = new ArrayList<>();

    enum GeomType {
        BOX,
        CYLINDER,
        SPHERE,
        TRI_MESH,
        CAR // wait, what? id == 4, 4 spheres, 1 box
    }

    // class for faster and safer lookup of vertices
    static final class VertexKey {
        final int ix, iy, iz;
        VertexKey(float x, float y, float z) {
            this.ix = Float.floatToIntBits(x);
            this.iy = Float.floatToIntBits(y);
            this.iz = Float.floatToIntBits(z);
        }
        @Override public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof VertexKey)) return false;
            VertexKey k = (VertexKey) o;
            return ix == k.ix && iy == k.iy && iz == k.iz;
        }
        @Override public int hashCode() {
            int h = ix; h = 31*h + iy; h = 31*h + iz; return h;
        }
    }

    @Override
    public void init() {
        OdeHelper.initODE2(0);

        world = OdeHelper.createWorld();
        world.setGravity(0, 0, -9.8100004); // Default gravity in Sekai
        world.setCFM(0.00001f);
        world.setERP(0.8f);

        space = OdeHelper.createSimpleSpace();

        jointGroup = OdeHelper.createJointGroup();

        timer = new ODEPhysicsTimer();

        cameraAnchor = new CameraAnchor();
    }

    private GameObject getObject(int objectId) {
        // get last body
        List<GameObject> objectsData = objects.get(objectId);
        if (objectsData == null) {
            throw new IllegalArgumentException("No objects found with ID: " + objectId);
        }
        if(objectsData.isEmpty()) {
            throw new IllegalStateException("No objects associated with ID: " + objectId);
        }
        return objectsData.get(objectsData.size() - 1);
    }

    @Override
    public void createBody(GameObject gameObject, Mesh geometryMesh) {
        if(gameObject.isRigidBody()) {
            DBody body = createBasicBody(gameObject.getId(), gameObject.getX(), gameObject.getY(), gameObject.getZ());
            body.setData(gameObject);
            gameObject.setBody(body);
            gameObject.setMesh(null);
            attachGeometry(body, gameObject.getGeomType());
            setMass(body, gameObject.getMass(), gameObject.getGeomType());
        }
        else { // no body, no mass, pure geometry
            gameObject.setBody(null);
            gameObject.setMesh(geometryMesh);
            attachMesh(gameObject, geometryMesh);
        }

        objects.putIfAbsent(gameObject.getId(), new ArrayList<>());
        objects.get(gameObject.getId()).add(gameObject);

        DJoint.DJointFeedback jointFeedback = OdeHelper.createJointFeedback();
        gameObject.setJointFeedback(jointFeedback);
    }

    @Override
    public void createBody(
            int objectId, double mass, double mu, double mu2,
            double bounce, double bounceVelocity, double maxVelocity,
            int bodyType, int geomType,
            double dim0, double dim1, double dim2
    ) {
        GameObject go = toGameObject(objectId, mass, mu, mu2, bounce, bounceVelocity, maxVelocity, geomType, dim0, dim1, dim2);

        DBody body = createBasicBody(objectId, 0, 0, 0);
        objects.putIfAbsent(objectId, new ArrayList<>());
        objects.get(objectId).add(go);
        body.setData(go);
        go.setBody(body);
        go.setMesh(null);

        attachGeometry(body, geomType);
        setMass(body, mass, geomType);

        DJoint.DJointFeedback jointFeedback = OdeHelper.createJointFeedback();
        go.setJointFeedback(jointFeedback);
    }

    private DBody createBasicBody(int objectId, double x, double y, double z) {
        Gdx.app.log("ODEPhysicsEngine", "Creating body for objectId: " + objectId + " at position: (" + x + ", " + y + ", " + z + ")");
        DBody body = OdeHelper.createBody(world);
        body.setPosition(x, y, z);
        return body;
    }

    private void attachGeometry(DBody body, int geomType) {
        float[] dimensions = new float[]{1.0f, 1.0f, 1.0f};

        GameObject go = null;
        if(body.getData() instanceof GameObject) {
            go = (GameObject) body.getData();
            dimensions = go.getDimensions();
        }

        switch (GeomType.values()[geomType]) {
            case BOX: 
                DBox box = OdeHelper.createBox(space, dimensions[0], dimensions[1], dimensions[2]);
                box.setBody(body);
                box.setData(go);
                break;
            case CYLINDER:
                DCylinder cylinder = OdeHelper.createCylinder(space, dimensions[0], dimensions[1]);
                cylinder.setBody(body);
                cylinder.setData(go);
                break;
            case SPHERE:
                DSphere sphere = OdeHelper.createSphere(space, dimensions[0]);
                sphere.setBody(body);
                sphere.setData(go);
                break;
        }
    }

    private void attachMesh(GameObject go, Mesh mesh) {
        if (mesh == null) return;

        DTriMeshData meshData = OdeHelper.createTriMeshData();
        List<Float> verts = new ArrayList<>();
        List<Integer> idx  = new ArrayList<>();
        Map<VertexKey, Integer> indexOf = new HashMap<>();

        int next = 0;
        for (MeshTriangle t : mesh.getTriangles()) {
            for (TriangleVertex tv : t.getVertices()) {
                float x = (float) tv.getPoint().x;
                float y = (float) tv.getPoint().y;
                float z = (float) tv.getPoint().z;
                VertexKey key = new VertexKey(x, y, z);
                Integer i = indexOf.get(key);
                if (i == null) {
                    i = next++;
                    indexOf.put(key, i);
                    verts.add(x); verts.add(y); verts.add(z);
                }
                idx.add(i);
            }
        }

        float[] vertices = new float[verts.size()];
        for (int i = 0; i < verts.size(); i++) vertices[i] = verts.get(i);
        int[] indices = idx.stream().mapToInt(i -> i).toArray();

        meshData.build(vertices, indices);
        triMeshDatas.add(meshData);

        DTriMesh triMesh = OdeHelper.createTriMesh(space, meshData, null, null, null);
        triMesh.setBody(null);
        triMesh.setData(go);
    }

    private GameObject toGameObject(
            int objectId, double mass, double mu, double mu2,
            double bounce, double bounceVelocity, double maxVelocity,
            int geomType, double dim0, double dim1, double dim2
    ) {
        return GameObject.builder()
                .id(objectId)
                .mass(mass)
                .mu(mu)
                .friction(mu2)
                .bounce(bounce)
                .bounceVelocity(bounceVelocity)
                .maxVelocity(maxVelocity)
                .geomType(geomType)
                .dimensions(new float[]{(float) dim0, (float) dim1, (float) dim2})
                .position(0, 0, 0)
                .physicsEngine(this)
                .build();
    }


    @Override
    public void addForce(int objectId, double forceX, double forceY, double forceZ) {
        GameObject go = getObject(objectId);
        DBody body = (DBody) go.getBody();
        try {
            body.addForce(forceX, forceY, forceZ);
        }
        catch (NullPointerException e) {
            Gdx.app.error("ODEPhysicsEngine", "Failed to add force to object " + objectId + ". Body is null (object is not rigid body)");
        }
    }

    @Override
    public void setPosition(int objectId, double x, double y, double z) {
        GameObject go = getObject(objectId);
        DBody body = (DBody) go.getBody();
        try {
            body.setPosition(x, y, z);
        }
        catch (NullPointerException e) {
            Gdx.app.error("ODEPhysicsEngine", "Failed to set position to object " + objectId + ". Body is null (object is not rigid body)");
        }
    }

    @Override
    public void setSpeed(int objectId, double speedX, double speedY, double speedZ) {
        GameObject go = getObject(objectId);
        DBody body = (DBody) go.getBody();
        try {
            body.setLinearVel(speedX, speedY, speedZ);
        }
        catch (NullPointerException e) {
            Gdx.app.error("ODEPhysicsEngine", "Failed to set linear velocity to object " + objectId + ". Body is null (object is not rigid body)");
        }
    }

    private void setMass(DBody body, double mass, int geomType) {
        float[] dimensions = new float[]{1.0f, 1.0f, 1.0f};

        if(body.getData() instanceof GameObject) {
            GameObject go = (GameObject) body.getData();
            dimensions = go.getDimensions();
        }

        Gdx.app.log("ODEPhysicsEngine", "Setting mass for geomType: " + geomType +
                ", dimensions: [" + dimensions[0] + ", " + dimensions[1] + ", " + dimensions[2] + "]");


        DMass m = OdeHelper.createMass();
        switch (GeomType.values()[geomType]) {
            case BOX:
                m.setBoxTotal(mass, dimensions[0], dimensions[1], dimensions[2]);
                break;
            case CYLINDER:
                m.setCylinderTotal(mass, 3, dimensions[0], dimensions[1]); // axis, radius, length
                break;
            case SPHERE:
                m.setSphereTotal(mass, dimensions[0]);
                break;
        }
        try {
            body.setMass(m);
        } catch (Exception e) {
            Gdx.app.error("ODEPhysicsEngine", "Failed to set mass for geomType: " + geomType +
                    ", dimensions: [" + dimensions[0] + ", " + dimensions[1] + ", " + dimensions[2] + "]");
        }
    }

    @Override
    public void setMass(int objectId, double mass, int geomType) {
        GameObject go = getObject(objectId);
        DBody body = (DBody) go.getBody();
        try {
            setMass(body, mass, geomType);
        }
        catch (NullPointerException e) {
            Gdx.app.error("ODEPhysicsEngine", "Cannot set mass to object " + objectId + ". Body is null (object is not rigid body)");
        }
    }

    @Override
    public void setGravity(double gravityX, double gravityY, double gravityZ) {
        world.setGravity(gravityX, gravityY, gravityZ);
    }

    @Override
    public void setGravityCenter(int objectId, boolean gravityCenter) {
        GameObject go = getObject(objectId);
        go.setGravityCenter(gravityCenter);
    }

    @Override
    public void setMaxVelocity(int objectId, double maxVelocity) {
        GameObject go = getObject(objectId);
        go.setMaxVelocity(maxVelocity);
    }

    @Override
    public double[] getPosition(int objectId) {
        GameObject go = getObject(objectId);
        DBody body = (DBody) go.getBody();
        try {
            DVector3C position = body.getPosition();
            return position.toDoubleArray();
        }
        catch (NullPointerException e) {
            Gdx.app.error("ODEPhysicsEngine", "Cannot get position of object " + objectId + ". Body is null (object is not rigid body). Returning [0, 0, 0] instead");
            return new double[] {0, 0, 0};
        }
    }

    @Override
    public double[] getSpeed(int objectId) {
        GameObject go = getObject(objectId);
        DBody body = (DBody) go.getBody();
        try {
            DVector3C velocity = body.getLinearVel();
            return velocity.toDoubleArray();
        }
        catch (NullPointerException e) {
            Gdx.app.error("ODEPhysicsEngine", "Cannot get velocity of object " + objectId + ". Body is null (object is not rigid body). Returning [0, 0, 0] instead");
            return new double[] {0, 0, 0};
        }
    }

    @Override
    public double getRotationZ(int objectId) {
        GameObject go = getObject(objectId);
        DBody body = (DBody) go.getBody();
        try {
            // ODE does not provide a direct way to get rotation, we can calculate it from the orientation
            DMatrix3C rotation = body.getRotation();
            // Assuming the rotation is in a 3x3 matrix, we can extract the Z rotation
            return Math.atan2(rotation.get(1, 0), rotation.get(0, 0));
        }
        catch (NullPointerException e) {
            Gdx.app.error("ODEPhysicsEngine", "Cannot get rotation of object " + objectId + ". Body is null (object is not rigid body). Returning 0 instead");
            return 0.0;
        }
    }

    @Override
    public double getAngle(int objectId) {
        GameObject go = getObject(objectId);
        DBody body = (DBody) go.getBody();
        try {
            // get speed vector and calculate angle
            DVector3C velocity = body.getLinearVel();
            if (velocity.length() == 0) {
                return 0.0; // No movement, angle is 0
            }
            return Math.atan2(velocity.get(1), velocity.get(0));
        }
        catch (NullPointerException e) {
            Gdx.app.error("ODEPhysicsEngine", "Cannot get angle of object " + objectId + ". Body is null (object is not rigid body). Returning 0 instead");
            return 0.0;
        }
    }

    @Override
    public double getMoveDistance(int objectId) {
        GameObject go = getObject(objectId);
        return go.getMoveDistance();
    }

    @Override
    public double stepSimulation() {
        return stepSimulation(timer.calculateStepSize());
    }

    private void synchronizeObjects() {
        // first get position of reference object
        try {
            double[] refPos = getPosition(referenceObjectId);
            cameraAnchor.updateCameraAnchor(
                    (float) refPos[0],
                    (float) refPos[1],
                    (float) refPos[2]
            );
        } catch (Exception e) {
            // no reference object, ignore
        }

        // iteration over links
        for (DBody linkedBody : linkedVariables.keySet()) {
            // technically there is possibility of pausing and resuming links,
            // but I didn't find usage of this feature, so I skip it for now

            // get variable
            GameObject go = (GameObject) linkedBody.getData();
            Variable var = linkedVariables.get(linkedBody);

            // get body position
            double[] worldPos = linkedBody.getPosition().toDoubleArray();

            // synchronizing object position
            if ((go.getFlags() & 1) != 0) {
                // convert world position to screen position
                float screenX = (float) worldPos[0] + cameraAnchor.getCameraPosX();
                float screenY = cameraAnchor.getCameraPosY() - (float) worldPos[1];

                // set position in variable
                var.fireMethod("SETPOSITION",
                        new IntegerVariable("", (int) screenX, var.getContext()),
                        new IntegerVariable("", (int) screenY, var.getContext())
                );
            }

            // pathfinding things
            if ((go.getFlags() & 2) != 0) {
                // let's tell emulator that object is at goal and if it has collisions and with what

                // checking if is at goal
                int isAtGoal = go.getIsAtGoal();
                if (isAtGoal == 1) {
                    var.emitSignal("ONSIGNAL", "ATGOAL");
                    continue;  // skip other checks if at goal
                } else if (isAtGoal == 2) {
                    var.emitSignal("ONSIGNAL", "NOPATH");
                    continue; // and if there is no path
                }

                // check if it has any collisions
                List<Integer> collisionIds = go.getCollisionIds();
                if (!collisionIds.isEmpty()) {
                    // send signal for all collision ids
                    for (Integer collisionId : collisionIds) {
                        var.emitSignal("ONSIGNAL", String.valueOf(collisionId));
                    }
                    var.emitSignal("ONSIGNAL", "ANY");
                } else {
                    var.emitSignal("ONSIGNAL", "NOCOLL");
                }

                // last step, let's check if object started moving or stopped
                float currentSpeed = go.getSpeed();
                float lastSpeed = go.getLastSpeed();

                if (currentSpeed != lastSpeed) {
                    if (currentSpeed == 0.0f && lastSpeed != 0.0f) {
                        var.emitSignal("ONSIGNAL", "ONFINISHED");
                    } else if (lastSpeed == 0.0f && currentSpeed != 0.0f) {
                        var.emitSignal("ONSIGNAL", "ONSTARTED");
                    }
                }
            }
        }
    }

    private void calculateBodiesAttraction(double deltaTime) {
        // Newton's law of universal gravitation baby
        // This method is mainly used for magnets in Reksio i Wehikuł Czasu, where G is modified
        if (world == null) return;

        final double EPS2 = 1e-6;

        List<GameObject> gameObjects = getGameObjects();

        for (GameObject go : gameObjects) {
            if (!(go.isGravityCenter() && go.isActive())) continue;
            DBody centerBody = (DBody) go.getBody();

            if(centerBody == null) continue; // ignore non-rigid bodies

            DVector3C pc = centerBody.getPosition();
            double m1 = go.getMass();
            double G  = go.getG();

            go.setStepsize(deltaTime);

            for (GameObject go2 : gameObjects) {
                DBody other = (DBody) go2.getBody();
                if (other == centerBody) continue;
                if (other == null) continue; // ignore non-rigid bodies

                if(!go2.isActive()) continue;

                DVector3C po = other.getPosition();

                double m2 = go2.getMass();

                double dx = pc.get0() - po.get0();
                double dy = pc.get1() - po.get1();
                double dz = pc.get2() - po.get2();

                double r2 = dx*dx + dy*dy + dz*dz;
                if (r2 < EPS2) continue;

                double invR = 1.0 / Math.sqrt(r2);
                double invR3 = invR * invR * invR;

                double scalar = G * m1 * m2 * invR3;

                double fx = dx * scalar;
                double fy = dy * scalar;
                double fz = dz * scalar;

                other.addForce(fx, fy, fz);
            }
        }
    }

    private boolean breakJointIfOverloaded(GameObject go, double fx, double fy, double fz) {
        if (go.getJoint() != null) {
            DJoint joint = (DJoint) go.getJoint();

            double limot = go.getLimot();

            double force = Math.sqrt(fx * fx + fy * fy + fz * fz);

            if (force > limot) {
                joint.setFeedback(null);
                joint.destroy();
                go.setJoint(null, (float) limot);
                return true;
            }
        }
        return false;
    }

    private void clearCollisions() {
        for (GameObject go : getGameObjects()) {
            go.getCollisionIds().clear();
        }
    }

    @Override
    public double stepSimulation(double deltaTime) {
        if(deltaTime > 0.03f) deltaTime = 0.03f; // that limit was in Sekai
        if(deltaTime <= 0) return 0.0; // ignore zero and negative delta times
        clearCollisions(); // clear collisions from previous frame
        calculateBodiesAttraction(deltaTime);
        space.collide(this, nearCallback);
        //world.step(deltaTime);
        world.quickStep(deltaTime);
        jointGroup.clear();

        for (GameObject go : getGameObjects()) {
            DBody body = (DBody) go.getBody();
            if (body == null) {
                continue; // Skip null bodies
            }

            // check joints
            if(go.getJoint() != null) {
                DJoint joint = (DJoint) go.getJoint();

                DJoint.DJointFeedback jointFeedback = joint.getFeedback();

                if(jointFeedback != null) {
                    DVector3C f1 = jointFeedback.f1;
                    if(!breakJointIfOverloaded(go, f1.get0(), f1.get1(), f1.get2())) {
                        DVector3C f2 = jointFeedback.f2;
                        breakJointIfOverloaded(go, f2.get0(), f2.get1(), f2.get2());
                    }
                }
            }

            go.updateObject();
        }

        synchronizeObjects();
        return deltaTime;
    }

    @Override
    public void start() {
        timer.resume();
    }

    @Override
    public void stop() {
        timer.pause();
    }

    @Override
    public void setLimit(int objectId, double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        GameObject go = getObject(objectId);
        go.setLimits(minX, maxX, minY, maxY, minZ, maxZ);
    }

    @Override
    public void destroyBody(int objectId) {
        List<GameObject> gameObjects = objects.remove(objectId);
        if (gameObjects == null) {
            throw new IllegalArgumentException("No objects found with ID: " + objectId);
        }
        for (GameObject go : gameObjects) {
            DBody body = (DBody) go.getBody();

            if (body == null) {
                continue; // Skip null bodies
            }
            // Remove the body from the space and destroy it
            Iterator<DGeom> iterator = body.getGeomIterator();
            while (iterator.hasNext()) {
                DGeom geom = iterator.next();
                space.remove(geom);
                geom.destroy();
            }
            body.destroy();
            linkedVariables.remove(body); // Remove any linked variable
            body.destroy();
        }
    }

    @Override
    public void addJoint(int firstId, int secondId, double anchorX, double anchorY, double anchorZ, double limitMotor, double lowStop, double highStop, double hingeAxisX, double hingeAxisY, double hingeAxisZ) {
        GameObject go = getObject(firstId);
        GameObject go2 = getObject(secondId);
        DBody body1 = (DBody) go.getBody();
        DBody body2 = (DBody) go2.getBody();
        if (body1 == null) {
            throw new IllegalArgumentException("No body found with ID: " + firstId);
        }
        if (body2 == null) {
            Gdx.app.log("ODEPhysicsEngine", "No second body supplied for joint. Joining to plane.");
        }
        DHingeJoint joint = OdeHelper.createHingeJoint(body1.getWorld());

        if (go.getJoint() != null) {
            DJoint oldJoint = (DJoint) go.getJoint();
            oldJoint.setFeedback(null);
            oldJoint.destroy();
        }
        go.setJoint(joint, (float) limitMotor);

        if (limitMotor <= 0.0) {
            joint.setFeedback(null);
        }
        else {
            joint.setFeedback((DJoint.DJointFeedback) go.getJointFeedback());
        }

        joint.attach(body1, body2);
        joint.setAxis(hingeAxisX, hingeAxisY, hingeAxisZ);
        joint.setAnchor(anchorX, anchorY, anchorZ);
        joint.setParamLoStop(lowStop);
        joint.setParamHiStop(highStop);
    }

    @Override
    public void setG(int objectId, double g) {
        GameObject go = getObject(objectId);
        go.setG(g);
    }

    @Override
    public void setActive(int objectId, boolean active, boolean unknown) {
        GameObject go = getObject(objectId);
        go.setActive(active);
    }

    @Override
    public void setReferenceObjectId(int referenceObjectId) {
        this.referenceObjectId = referenceObjectId;
    }

    @Override
    public void linkVariable(Variable variable, int objectId) {
        GameObject go = getObject(objectId);
        DBody body = (DBody) go.getBody();
        if(body != null) {
            linkedVariables.put(body, variable);
        }
        else {
            Gdx.app.error("ODEPhysicsEngine", "Cannot link variable to object " + objectId + ". Body is null (object is not rigid body).");
        }
    }

    @Override
    public void unlinkVariable(int objectId) {
        GameObject go = getObject(objectId);
        DBody body = (DBody) go.getBody();
        if(body != null) {
            linkedVariables.remove(body);
        }
        else {
            Gdx.app.error("ODEPhysicsEngine", "Cannot unlink variable from object " + objectId + ". Body is null (object is not rigid body).");
        }
    }

    @Override
    public float followPath(int objectId, int arrivalRadius, double turnClamp, double speed) {
        GameObject go = getObject(objectId);

        while (true) {
            Point3D currentWaypoint = go.getCurrentPathPoint();

            if (currentWaypoint == null) {
                go.setIsAtGoal(1);
                return 0.0f;
            }

            double[] position = getPosition(objectId);
            double dx = currentWaypoint.x - position[0];
            double dy = currentWaypoint.y - position[1];
            double dz = currentWaypoint.z - position[2];
            double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

            // Is point far away?
            if (distance > arrivalRadius) {
                // If so, go to them

                // calculate normalised direction vector
                double dirX = dx / distance;
                double dirY = dy / distance;
                double dirZ = dz / distance;

                // get normalized velocity
                double[] velocity = getSpeed(objectId);
                double vel = Math.sqrt(velocity[0] * velocity[0] +
                        velocity[1] * velocity[1] +
                        velocity[2] * velocity[2]);
                double normVelX = vel > 0 ? velocity[0] / vel : 0;
                double normVelY = vel > 0 ? velocity[1] / vel : 0;
                double normVelZ = vel > 0 ? velocity[2] / vel : 0;

                // direction difference
                double diffX = dirX - normVelX;
                double diffY = dirY - normVelY;
                double diffZ = dirZ - normVelZ;

                // Limit rotation speed
                turnClamp = Math.abs(turnClamp);
                double corrX = diffX * turnClamp;
                double corrY = diffY * turnClamp;
                double corrZ = diffZ * turnClamp;

                // New direction
                double newDirX = normVelX + corrX;
                double newDirY = normVelY + corrY;
                double newDirZ = normVelZ + corrZ;

                // Normalize and scale by speed
                double newLen = Math.sqrt(newDirX * newDirX +
                        newDirY * newDirY +
                        newDirZ * newDirZ);
                if (newLen > 1e-6) {
                    newDirX = (newDirX / newLen) * speed;
                    newDirY = (newDirY / newLen) * speed;
                    newDirZ = (newDirZ / newLen) * speed;
                } else {
                    newDirX = 0;
                    newDirY = 0;
                    newDirZ = 0;
                }

                setSpeed(objectId, newDirX, newDirY, newDirZ);
                return (float) distance;
            }

            // if not, get next point
            go.getNextPointInPath();
        }
    }

    @Override
    public void findPath(int objectId, int pointObjectId, int targetX, int targetY, int targetZ, boolean saveIntermediates, boolean unknown) {
        GameObject go = getObject(objectId);
        GameObject go2 = getObject(pointObjectId);
        DBody body = (DBody) go.getBody();
        if(body == null) {
            Gdx.app.error("ODEPhysicsEngine", "Cannot find path for object " + objectId + ". Body is null (object is not rigid body)." );
            return;
        }
        DVector3C position = body.getPosition();
        Point3D start = new Point3D(position.get(0), position.get(1), position.get(2));
        Point3D target = new Point3D(targetX-400, 300-targetY, targetZ);

        AStar pathfinder = go2.getPathfinder();

        List<Point3D> path = pathfinder.findPath(start, target);

        go.getPath().clear();

        if(path == null || path.isEmpty()) {
            go.setIsAtGoal(1);
            return;
        }

        for(Point3D point : path) {
            go.addPointToPath(point);
        }
        go.setIsAtGoal(2);
    }

    @Override
    public List<GameObject> getGameObjects() {
        return objects.values().stream().flatMap(List::stream).collect(Collectors.toList());
    }

    @Override
    public void shutdown() {
        for (Integer id : new ArrayList<>(objects.keySet())) {
            destroyBody(id);
        }
        for (DTriMeshData meshData : triMeshDatas) {
            meshData.destroy();
        }
        triMeshDatas.clear();
        jointGroup.destroy();
        world.destroy();
        space.destroy();
        OdeHelper.closeODE();
    }

    private final DGeom.DNearCallback nearCallback = new DGeom.DNearCallback() {
        @Override
        public void call(Object data, DGeom g1, DGeom g2) {
            GameObject go1 = (GameObject) g1.getData();
            GameObject go2 = (GameObject) g2.getData();

            if (go1 == null || go2 == null) {
                return; // Skip if either GameObject is null
            }
            if (go1 == go2) {
                return; // Skip self-collisions
            }

            int N = 4;
            DContactBuffer contacts = new DContactBuffer(N);
            DContactGeomBuffer gb = contacts.getGeomBuffer();
            int numContacts = OdeHelper.collide(g1, g2, N, gb);

            if(numContacts > 0) {
                go1.getCollisionIds().add(go2.getId());
                go2.getCollisionIds().add(go1.getId());
            }

            for (int i = 0; i < numContacts; i++) {
                DContact c = contacts.get(i);
                c.surface.mode = dContactBounce | dContactSoftERP | dContactSoftCFM;
                c.surface.mu = Math.min(go1.getMu(), go2.getMu());
                c.surface.bounce = Math.max(go1.getBounce(), go2.getBounce());
                c.surface.bounce_vel = Math.max(go1.getBounceVelocity(), go2.getBounceVelocity());
                c.surface.soft_erp = 0.8;
                c.surface.soft_cfm = 1e-5;

                DJoint j = OdeHelper.createContactJoint(world, jointGroup, c);
                j.attach(g1.getBody(), g2.getBody());
            }
        }
    };

    @Override
    public void dumpGeometryData(String path) {
        try (java.io.PrintWriter out = new java.io.PrintWriter(path)) {
            StringBuilder sb = new StringBuilder();
            sb.append("{\"trimeshes\":[");
            boolean firstTri = true;

            for (GameObject go : getGameObjects()) {
                Mesh mesh = go.getMesh();
                if (mesh != null && mesh.getTriangles() != null && !mesh.getTriangles().isEmpty()) {
                    java.util.Map<VertexKey, Integer> idx = new java.util.LinkedHashMap<>();
                    java.util.List<float[]> verts = new java.util.ArrayList<>();
                    java.util.List<Integer> indices = new java.util.ArrayList<>();
                    int next = 0;

                    for (MeshTriangle t : mesh.getTriangles()) {
                        for (TriangleVertex tv : t.getVertices()) {
                            float x = (float) tv.getPoint().x;
                            float y = (float) tv.getPoint().y;
                            float z = (float) tv.getPoint().z;

                            VertexKey key = new VertexKey(x, y, z);
                            Integer id = idx.get(key);
                            if (id == null) {
                                id = next++;
                                idx.put(key, id);
                                verts.add(new float[]{x, y, z});
                            }
                            indices.add(id);
                        }
                    }

                    if (!firstTri) sb.append(",");
                    firstTri = false;
                    sb.append("{\"id\":").append(go.getId()).append(",");
                    sb.append("\"pose\":{\"p\":[").append(0).append(",").append(0).append(",").append(0).append("],");
                    sb.append("\"R\":[").append(1).append(",").append(0).append(",").append(0).append(",")
                            .append(0).append(",").append(1).append(",").append(0).append(",")
                            .append(0).append(",").append(0).append(",").append(1).append("]},");
                    sb.append("\"verts\":[");
                    for (int i=0;i<verts.size();i++) {
                        float[] v = verts.get(i);
                        if (i>0) sb.append(",");
                        sb.append("[").append(v[0]).append(",").append(v[1]).append(",").append(v[2]).append("]");
                    }
                    sb.append("],\"indices\":[");
                    for (int i=0;i<indices.size();i++) {
                        if (i>0) sb.append(",");
                        sb.append(indices.get(i));
                    }
                    sb.append("]}");
                }
            }
            sb.append("],\"primitives\":[");

            boolean firstPrim = true;
            for (GameObject go : getGameObjects()) {
                DBody body = (DBody) go.getBody();
                if (body == null) continue;

                float[] d = go.getDimensions();
                String type = GeomType.values()[go.getGeomType()].name();

                double px = body.getPosition().get0();
                double py = body.getPosition().get1();
                double pz = body.getPosition().get2();
                org.ode4j.math.DMatrix3C R = body.getRotation();
                double r00 = R.get00(), r01 = R.get01(), r02 = R.get02();
                double r10 = R.get10(), r11 = R.get11(), r12 = R.get12();
                double r20 = R.get20(), r21 = R.get21(), r22 = R.get22();

                if (!firstPrim) sb.append(",");
                firstPrim = false;
                sb.append("{\"id\":").append(go.getId()).append(",");
                sb.append("\"type\":\"").append(type).append("\",");
                sb.append("\"params\":{");
                switch (GeomType.values()[go.getGeomType()]) {
                    case BOX:
                        sb.append("\"dx\":").append(d[0]).append(",\"dy\":").append(d[1]).append(",\"dz\":").append(d[2]);
                        break;
                    case SPHERE:
                        sb.append("\"radius\":").append(d[0]);
                        break;
                    case CYLINDER:
                        sb.append("\"radius\":").append(d[0]).append(",\"length\":").append(d[1]).append(",\"axis\":3");
                        break;
                    default:
                        sb.append("\"note\":\"unsupported-or-custom\"");
                        break;
                }
                sb.append("},");
                sb.append("\"pose\":{\"p\":[").append(px).append(",").append(py).append(",").append(pz).append("],");
                sb.append("\"R\":[")
                        .append(r00).append(",").append(r01).append(",").append(r02).append(",")
                        .append(r10).append(",").append(r11).append(",").append(r12).append(",")
                        .append(r20).append(",").append(r21).append(",").append(r22).append("]}}");
            }
            sb.append("]}");
            out.println(sb.toString());
        } catch (Exception e) {
            Gdx.app.error("ODEPhysicsEngine", "dumpSceneToJson failed", e);
        }
    }
}
