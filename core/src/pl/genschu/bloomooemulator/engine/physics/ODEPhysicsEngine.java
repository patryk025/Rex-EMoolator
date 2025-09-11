package pl.genschu.bloomooemulator.engine.physics;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import org.ode4j.math.DMatrix3C;
import org.ode4j.math.DVector3C;
import org.ode4j.ode.*;
import pl.genschu.bloomooemulator.geometry.points.Point3D;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.interpreter.variable.types.AnimoVariable;
import pl.genschu.bloomooemulator.interpreter.variable.types.ImageVariable;
import pl.genschu.bloomooemulator.interpreter.variable.types.IntegerVariable;
import pl.genschu.bloomooemulator.world.GameObject;
import pl.genschu.bloomooemulator.world.Mesh;
import pl.genschu.bloomooemulator.world.MeshTriangle;
import pl.genschu.bloomooemulator.world.TriangleVertex;

import java.util.*;
import java.util.stream.Collectors;

import static org.ode4j.ode.OdeConstants.*;

public class ODEPhysicsEngine implements IPhysicsEngine {
    DWorld world;
    DSpace space;
    ODEPhysicsTimer timer;
    DJointGroup jointGroup;
    private final Map<Integer, List<DBody>> bodies = new HashMap<>();
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
    }

    private DBody getBody(int objectId) {
        // get last body
        List<DBody> objectBodies = bodies.get(objectId);
        if (objectBodies == null) {
            throw new IllegalArgumentException("No body found with ID: " + objectId);
        }
        if(objectBodies.isEmpty()) {
            throw new IllegalStateException("No bodies associated with ID: " + objectId);
        }
        return objectBodies.get(objectBodies.size() - 1);
    }

    @Override
    public void createBody(GameObject gameObject, Mesh geometryMesh) {
        DBody body = createBasicBody(gameObject.getId(), gameObject.getX(), gameObject.getY(), gameObject.getZ());
        body.setData(gameObject);
        gameObject.setBody(body);
        gameObject.setMesh(geometryMesh);
        if (geometryMesh != null) {
            attachMesh(body, geometryMesh);
        } else {
            attachGeometry(body, gameObject.getGeomType());
        }
        setMass(body, gameObject.getMass(), gameObject.getGeomType());
    }

    @Override
    public void createBody(
            int objectId, double mass, double mu, double mu2,
            double bounce, double bounceVelocity, double maxVelocity,
            int bodyType, int geomType,
            double dim0, double dim1, double dim2
    ) {
        createBody(objectId, mass, mu, mu2, bounce, bounceVelocity, maxVelocity, bodyType, geomType, dim0, dim1, dim2, null);
    }

    @Override
    public void createBody(
            int objectId, double mass, double mu, double mu2,
            double bounce, double bounceVelocity, double maxVelocity,
            int bodyType, int geomType,
            double dim0, double dim1, double dim2, Mesh geometryMesh
    ) {
        GameObject go = toGameObject(objectId, mass, mu, mu2, bounce, bounceVelocity, maxVelocity, geomType, dim0, dim1, dim2);

        DBody body = createBasicBody(objectId, 0, 0, 0);
        body.setData(go);
        go.setBody(body);
        go.setMesh(geometryMesh);

        if (geometryMesh != null) {
            attachMesh(body, geometryMesh);
        } else {
            attachGeometry(body, geomType);
        }

        setMass(body, mass, geomType);

        DJoint.DJointFeedback jointFeedback = OdeHelper.createJointFeedback();
        go.setJointFeedback(jointFeedback);

        attachMesh(body, geometryMesh);
    }

    private DBody createBasicBody(int objectId, double x, double y, double z) {
        DBody body = OdeHelper.createBody(world);
        body.setPosition(x, y, z);
        bodies.putIfAbsent(objectId, new ArrayList<>());
        bodies.get(objectId).add(body);
        if( bodies.get(objectId).size() > 1) {
            Gdx.app.log("ODEPhysicsEngine", "Warning: Multiple bodies created for objectId: " + objectId + ". Creating fixed joint...");
            // Create a fixed joint to link the bodies together
            DFixedJoint joint = OdeHelper.createFixedJoint(world);
            DBody first = bodies.get(objectId).get(0);
            joint.attach(first, body);
            joint.setFixed();

        }
        return body;
    }

    private void attachGeometry(DBody body, int geomType) {
        float[] dimensions = new float[]{1.0f, 1.0f, 1.0f};

        if(body.getData() instanceof GameObject) {
            GameObject go = (GameObject) body.getData();
            dimensions = go.getDimensions();
        }

        switch (GeomType.values()[geomType]) {
            case BOX: 
                OdeHelper.createBox(space, dimensions[0], dimensions[1], dimensions[2]).setBody(body);
                break;
            case CYLINDER:
                OdeHelper.createCylinder(space, dimensions[0], dimensions[1]).setBody(body);
                break;
            case SPHERE:
                OdeHelper.createSphere(space, dimensions[0]).setBody(body);
                break;
        }
    }

    private void attachMesh(DBody body, Mesh mesh) {
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
        triMesh.setBody(body);
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
        DBody body = getBody(objectId);
        body.addForce(forceX, forceY, forceZ);
    }

    @Override
    public void setPosition(int objectId, double x, double y, double z) {
        DBody body = getBody(objectId);
        body.setPosition(x, y, z);
    }

    @Override
    public void setSpeed(int objectId, double speedX, double speedY, double speedZ) {
        DBody body = getBody(objectId);
        body.setLinearVel(speedX, speedY, speedZ);
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
        DBody body = getBody(objectId);
        setMass(body, mass, geomType);
    }

    @Override
    public void setGravity(double gravityX, double gravityY, double gravityZ) {
        world.setGravity(gravityX, gravityY, gravityZ);
    }

    @Override
    public void setGravityCenter(int objectId, boolean gravityCenter) {
        DBody body = getBody(objectId);
        GameObject go = (GameObject) body.getData();
        go.setGravityCenter(gravityCenter);
    }

    @Override
    public void setMaxVelocity(int objectId, double maxVelocity) {
        DBody body = getBody(objectId);
        GameObject go = (GameObject) body.getData();
        go.setMaxVelocity(maxVelocity);
    }

    @Override
    public double[] getPosition(int objectId) {
        DBody body = getBody(objectId);
        DVector3C position = body.getPosition();
        return position.toDoubleArray();
    }

    @Override
    public double[] getSpeed(int objectId) {
        DBody body = getBody(objectId);
        DVector3C velocity = body.getLinearVel();
        return velocity.toDoubleArray();
    }

    @Override
    public double getRotationZ(int objectId) {
        DBody body = getBody(objectId);
        // ODE does not provide a direct way to get rotation, we can calculate it from the orientation
        DMatrix3C rotation = body.getRotation();
        // Assuming the rotation is in a 3x3 matrix, we can extract the Z rotation
        return Math.atan2(rotation.get(1, 0), rotation.get(0, 0));
    }

    @Override
    public double getAngle(int objectId) {
        // get speed vector and calculate angle
        DBody body = getBody(objectId);
        DVector3C velocity = body.getLinearVel();
        if (velocity.length() == 0) {
            return 0.0; // No movement, angle is 0
        }
        return Math.atan2(velocity.get(1), velocity.get(0));
    }

    @Override
    public double getMoveDistance(int objectId) {
        DBody body = getBody(objectId);
        GameObject go = (GameObject) body.getData();
        return go.getMoveDistance();
    }

    @Override
    public void stepSimulation() {
        stepSimulation(timer.calculateStepSize());
    }

    private void synchronizeObjects() {
        // first get position of reference object
        double[] position = getPosition(referenceObjectId);

        // next gets anchor and converts it to screen space (for now, I didn't reverse it)

        // iteration over links
        for (DBody linkedBody : linkedVariables.keySet()) {
            // technically there is possibility of pausing and resuming links,
            // but I didn't find usage of this feature, so I skip it for now

            // get position of object
            double[] bodyPosition = linkedBody.getPosition().toDoubleArray();

            // camera correction
            bodyPosition[0] += cameraX;
            bodyPosition[1] -= cameraY;

            // also correct by 400, 300 (jeez, seriously?)
            bodyPosition[0] += 400;
            bodyPosition[1] = 300 - bodyPosition[1];

            // synchronizing object position
            Variable var = linkedVariables.get(linkedBody);
            IntegerVariable x = new IntegerVariable("", (int) bodyPosition[0], var.getContext());
            IntegerVariable y = new IntegerVariable("", (int) bodyPosition[1], var.getContext());
            // I don't need to check type right now, fireMethod is smart enough
            var.fireMethod("SETPOSITION", x, y);

            // let's tell emulator that object is at goal and if it has collisions and with what

            // checking if is at goal (not implemented yet)
            int isAtGoal = 0; // TODO: implement IsAtGoal... and pathfinding... and waypoint map, jeez
            if (isAtGoal == 1) {
                var.emitSignal("ONSIGNAL", "ATGOAL");
            } else if (isAtGoal == 2) {
                var.emitSignal("ONSIGNAL", "NOPATH");
            }

            // check if it has any collisions
            GameObject go = (GameObject) linkedBody.getData();
            List<Integer> collisionsIds = go.getCollisionIds();
            if (!collisionsIds.isEmpty()) {
                for(Integer collisionId : collisionsIds) {
                    var.emitSignal("ONSIGNAL", collisionId);
                }
                var.emitSignal("ONSIGNAL", "ANY");
            }
            else {
                var.emitSignal("ONSIGNAL", "NOCOLL");
            }

            // last step, let's check if object started moving or stopped
            float vx = go.getVelX();
            float vy = go.getVelY();
            float vz = go.getVelZ();

            float pvx = go.getPrevVelX();
            float pvy = go.getPrevVelY();
            float pvz = go.getPrevVelZ();

            double v = Math.sqrt(vx * vx + vy * vy + vz * vz);
            double pv = Math.sqrt(pvx * pvx + pvy * pvy + pvz * pvz);

            double vdiff = v - pv;

            if(vdiff > 0 && pv < velEps) {
                var.emitSignal("ONSIGNAL", "ONSTARTED");
            }
            else if(vdiff < 0 && v < velEps) {
                var.emitSignal("ONSIGNAL", "ONSTOPPED");
            }
        }
    }

    private void calculateBodiesAttraction(double deltaTime) {
        // Newton's law of universal gravitation baby
        // This method is mainly used for magnets in Reksio i Wehikuł Czasu, where G is modified
        if (world == null) return;

        final double EPS2 = 1e-6;

        List<DBody> bodyList = bodies.values().stream()
                .flatMap(List::stream)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        for (DBody centerBody : bodyList) {
            GameObject centerGO = (GameObject) centerBody.getData();
            if (!(centerGO.isGravityCenter() && centerGO.isActive())) continue;

            DVector3C pc = centerBody.getPosition();
            double m1 = centerGO.getMass();
            double G  = centerGO.getG();

            centerGO.setStepsize(deltaTime);

            for (DBody other : bodyList) {
                if (other == centerBody) continue;

                GameObject go2 = (GameObject) other.getData();

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
        for (DBody body : bodies.values().stream().flatMap(List::stream).filter(Objects::nonNull).collect(Collectors.toList())) {
            GameObject go = (GameObject) body.getData();
            go.getCollisionIds().clear();
        }
    }

    @Override
    public void stepSimulation(double deltaTime) {
        if(deltaTime > 0.03f) deltaTime = 0.03f; // that limit was in Sekai
        if(deltaTime <= 0) return; // ignore zero and negative delta times
        clearCollisions(); // clear collisions from previous frame
        calculateBodiesAttraction(deltaTime);
        space.collide(this, nearCallback);
        //world.step(deltaTime);
        world.quickStep(deltaTime);
        jointGroup.clear();

        for (DBody body : bodies.values().stream().flatMap(List::stream).filter(Objects::nonNull).collect(Collectors.toList())) {
            if (body == null) {
                continue; // Skip null bodies
            }

            if(body.getData() instanceof GameObject) {
                GameObject go = (GameObject) body.getData();

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
        }

        synchronizeObjects();
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
        DBody body = getBody(objectId);
        GameObject go = (GameObject) body.getData();
        go.setLimits(minX, maxX, minY, maxY, minZ, maxZ);
    }

    @Override
    public void destroyBody(int objectId) {
        List<DBody> objectBodies = bodies.remove(objectId);
        if (objectBodies == null) {
            throw new IllegalArgumentException("No body found with ID: " + objectId);
        }
        for (DBody body : objectBodies) {
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
        DBody body1 = getBody(firstId);
        DBody body2 = getBody(secondId);
        if (body1 == null) {
            throw new IllegalArgumentException("No body found with ID: " + firstId);
        }
        if (body2 == null) {
            Gdx.app.log("ODEPhysicsEngine", "No second body supplied for joint. Joining to plane.");
        }
        DHingeJoint joint = OdeHelper.createHingeJoint(body1.getWorld());

        GameObject go = (GameObject) body1.getData();

        if (go != null) {
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
        }

        joint.attach(body1, body2);
        joint.setAxis(hingeAxisX, hingeAxisY, hingeAxisZ);
        joint.setAnchor(anchorX, anchorY, anchorZ);
        joint.setParamLoStop(lowStop);
        joint.setParamHiStop(highStop);
    }

    @Override
    public void setG(int objectId, double g) {
        DBody body = getBody(objectId);
        GameObject go = (GameObject) body.getData();
        go.setG(g);
    }

    @Override
    public void setActive(int objectId, boolean active, boolean unknown) {
        DBody body = getBody(objectId);
        GameObject go = (GameObject) body.getData();
        go.setActive(active);
    }

    @Override
    public void setReferenceObjectId(int referenceObjectId) {
        this.referenceObjectId = referenceObjectId;
    }

    @Override
    public void linkVariable(Variable variable, int objectId) {
        DBody body = getBody(objectId);
        linkedVariables.put(body, variable);
    }

    @Override
    public void unlinkVariable(int objectId) {
        DBody body = getBody(objectId);
        linkedVariables.remove(body);
    }

    @Override
    public List<GameObject> getGameObjects() {
        return bodies.values().stream()
                .flatMap(List::stream)
                .filter(Objects::nonNull)
                .map(DBody::getData)
                .filter(data -> data instanceof GameObject)
                .map(data -> (GameObject) data)
                .collect(Collectors.toList());
    }

    @Override
    public void shutdown() {
        world.destroy();
        space.destroy();
        OdeHelper.closeODE();
    }

    private final DGeom.DNearCallback nearCallback = new DGeom.DNearCallback() {
        @Override
        public void call(Object data, DGeom g1, DGeom g2) {
            if (g1.getBody() == null || g2.getBody() == null) return;

            GameObject go1 = (GameObject) g1.getBody().getData();
            GameObject go2 = (GameObject) g2.getBody().getData();

            if (g1.getBody() != null && g2.getBody() != null) {
                if (go1.getId() == go2.getId()) return;
            }

            Gdx.app.log("ODEPhysicsEngine", "Colliding " + go1.getId() + " with " + go2.getId());
            Gdx.app.log("ODEPhysicsEngine", g1.getPosition().toString());
            Gdx.app.log("ODEPhysicsEngine", g2.getPosition().toString());

            int N = 4;
            DContactBuffer contacts = new DContactBuffer(N);
            DContactGeomBuffer gb = contacts.getGeomBuffer();
            int numContacts = OdeHelper.collide(g1, g2, N, gb);

            if(numContacts > 0) {
                go1.getCollisionIds().add(go2.getId());
                go2.getCollisionIds().add(go1.getId());
            }

            Gdx.app.log("ODEPhysicsEngine", "Number of contacts: " + numContacts);

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
}
