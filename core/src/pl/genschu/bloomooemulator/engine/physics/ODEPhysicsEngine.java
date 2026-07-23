package pl.genschu.bloomooemulator.engine.physics;

import com.badlogic.gdx.Gdx;
import org.ode4j.math.*;
import org.ode4j.ode.*;
import pl.genschu.bloomooemulator.engine.compatibility.CompatibilityProfile;
import pl.genschu.bloomooemulator.engine.physics.camera.CameraAnchor;
import pl.genschu.bloomooemulator.engine.physics.pathfinding.AStar;
import pl.genschu.bloomooemulator.geometry.points.Point3D;
import pl.genschu.bloomooemulator.logic.GameFamilies;
import pl.genschu.bloomooemulator.engine.context.EngineVariable;
import pl.genschu.bloomooemulator.interpreter.values.IntValue;
import pl.genschu.bloomooemulator.interpreter.values.StringValue;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.world.*;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static org.ode4j.ode.OdeConstants.*;

public class ODEPhysicsEngine implements IPhysicsEngine {
    private CompatibilityProfile compatibilityProfile = CompatibilityProfile.unknown();
    DWorld world;
    DSpace space;
    ODEPhysicsTimer timer;
    DJointGroup jointGroup;
    CameraAnchor cameraAnchor = new CameraAnchor();
    private final Map<Integer, List<GameObject>> objects = new HashMap<>();
    private final Map<DBody, EngineVariable> linkedVariables = new HashMap<>();
    private final Set<Integer> pausedLinkIds = new HashSet<>();
    private int referenceObjectId = 0;
    private final List<DTriMeshData> triMeshDatas = new ArrayList<>();
    private PhysicsTraceWriter traceWriter;

    // Session-long running dt average for lag-spike smoothing
    private double dtTotal = 0.0;
    private long dtFrames = 0;

    enum GeomType {
        BOX,
        CAPSULE, // old ODE's dCreateCCylinder = capsule (z-aligned), not a flat cylinder
        SPHERE,
        TRI_MESH,
        CAR // wait, what? id == 4, 4 spheres, 1 box
    }

    @Override
    public void configureCompatibility(CompatibilityProfile profile) {
        compatibilityProfile = profile != null
                ? profile
                : CompatibilityProfile.unknown();
    }

    public CompatibilityProfile getCompatibilityProfile() {
        return compatibilityProfile;
    }

    // class for faster and safer lookup of vertices
    record VertexKey(int ix, int iy, int iz) {
        VertexKey(float ix, float iy, float iz) {
            this(Float.floatToIntBits(ix), Float.floatToIntBits(iy), Float.floatToIntBits(iz));
        }

        @Override
        public boolean equals(Object o) {
                if (this == o) return true;
                if (!(o instanceof VertexKey(int ix1, int iy1, int iz1))) return false;
                return ix == ix1 && iy == iy1 && iz == iz1;
            }
    }

    @Override
    public void init() {
        OdeHelper.initODE2(0);

        world = OdeHelper.createWorld();
        world.setGravity(0, -9.8100004, 0);

        dtTotal = 0.0;
        dtFrames = 0;

        space = OdeHelper.createSimpleSpace();

        jointGroup = OdeHelper.createJointGroup();

        timer = new ODEPhysicsTimer();

        try {
            traceWriter = PhysicsTraceWriter.openConfigured();
        } catch (IOException e) {
            traceWriter = null;
            Gdx.app.error("ODEPhysicsEngine", "Cannot open physics trace", e);
        }
    }

    /**
     * Returns the last body registered under {@code objectId}, or {@code null} if none exists.
     */
    private GameObject getObject(int objectId) {
        List<GameObject> objectsData = objects.get(objectId);
        if (objectsData == null || objectsData.isEmpty()) {
            Gdx.app.debug("ODEPhysicsEngine", "No objects found with ID: " + objectId + " (ignored)");
            return null;
        }
        return objectsData.get(objectsData.size() - 1);
    }

    @Override
    public void createBody(GameObject gameObject, Mesh geometryMesh) {
        if(gameObject.isRigidBody()) {
            if(gameObject.getGeomType() == GeomType.CAR.ordinal()) {
                // it's a car, create 4 spheres and 1 box
                // first box
                DBody body = createBasicBody(gameObject.getId(), 0, 0, 0); // position was set to 0,0,0 during creation
                body.setData(gameObject);
                gameObject.setBody(body);
                gameObject.setMesh(null);
                attachGeometry(body, GeomType.BOX.ordinal());
                setMass(body, gameObject.getMass(), GeomType.BOX.ordinal());

                // wheels
                List<DBody> wheels = new ArrayList<>(4);

                for (int i = 0; i < 4; i++) {
                    // create body
                    DBody wheel = createBasicBody(gameObject.getId(), 0, 0, 0);
                    wheel.setData(gameObject);

                    // rotate it
                    DMatrix3 R = new DMatrix3();
                    DRotation.dRFromAxisAndAngle(R, 1.0, 0.0, 0.0, Math.PI / 2);
                    wheel.setRotation(R);

                    // set mass
                    DMass mass = OdeHelper.createMass();
                    mass.setSphereTotal(gameObject.getMass() * 0.2, gameObject.getDimensions()[1]);
                    wheel.setMass(mass);

                    // attach geometry
                    DSphere sphere = OdeHelper.createSphere(space, gameObject.getDimensions()[1]);
                    sphere.setBody(wheel);

                    wheels.add(wheel);
                }

                float halfWidth = gameObject.getDimensions()[0] * 0.5f;
                float halfLength = gameObject.getDimensions()[1] * 0.5f;
                float halfHeight = gameObject.getDimensions()[2] * 0.5f;

                // position wheels
                wheels.get(0).setPosition(
                    halfWidth,
                    halfLength,
                    -halfHeight
                );

                wheels.get(1).setPosition(
                    +halfWidth,
                    -halfLength,
                    -halfHeight
                );

                wheels.get(2).setPosition(
                    -halfWidth,
                    halfLength,
                    -halfHeight
                );

                wheels.get(3).setPosition(
                    -halfWidth,
                    -halfLength,
                    -halfHeight
                );

                // now it's time for suspensions
                for (int i = 0; i < 4; i++) {
                    DBody wheel = wheels.get(i);
                    DHingeJoint suspension = OdeHelper.createHingeJoint(world);
                    suspension.attach(body, wheel);

                    DVector3C wheelPos = wheel.getPosition();

                    suspension.setAnchor(wheelPos);
                    suspension.setAxis(0, 1, 0); // hinge axis along Y
                    suspension.setParamLoStop(0.0);
                    suspension.setParamHiStop(0.0);
                    suspension.setParam(DJoint.PARAM_N.dParamSuspensionERP1, 0.8);
                    suspension.setParam(DJoint.PARAM_N.dParamSuspensionCFM1, 0.00001);
                    suspension.setParamVel(0.0);
                    suspension.setParamFMax(0.2);
                }
            }
            else {
                DBody body = createBasicBody(gameObject.getId(), gameObject.getX(), gameObject.getY(), gameObject.getZ());
                body.setData(gameObject);
                gameObject.setBody(body);
                gameObject.setMesh(null);
                attachGeometry(body, gameObject.getGeomType());
                setMass(body, gameObject.getMass(), gameObject.getGeomType());
            }
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
        GameObject go = toGameObject(objectId, mass, mu, mu2, bounce, bounceVelocity,
                maxVelocity, bodyType, geomType, dim0, dim1, dim2);

        DBody body = createBasicBody(objectId, 0, 0, 0);
        objects.putIfAbsent(objectId, new ArrayList<>());
        objects.get(objectId).add(go);
        body.setData(go);
        go.setBody(body);
        go.setMesh(null);

        attachGeometry(body, geomType);
        setMass(body, mass, geomType);
        if (bodyType == 0) {
            // Sekai's bodyType=0 has collision geometry and a script-visible position, but no
            // dynamic ODE body. Kinematic mode must be set after mass initialization in ode4j.
            body.setKinematic();
        }

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
            case CAPSULE:
                DCapsule capsule = OdeHelper.createCapsule(space, dimensions[0], dimensions[1]);
                capsule.setBody(body);
                capsule.setData(go);
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
            int bodyType, int geomType, double dim0, double dim1, double dim2
    ) {
        return GameObject.builder()
                .id(objectId)
                .mass(mass)
                .mu(mu)
                .friction(mu2)
                .bounce(bounce)
                .bounceVelocity(bounceVelocity)
                .maxVelocity(maxVelocity)
                .rigidBody(bodyType != 0)
                .geomType(geomType)
                .dimensions(new float[]{(float) dim0, (float) dim1, (float) dim2})
                .position(0, 0, 0)
                .physicsEngine(this)
                .build();
    }


    @Override
    public void addForce(int objectId, double forceX, double forceY, double forceZ) {
        GameObject go = getObject(objectId);
        if (go == null) return;
        DBody body = (DBody) go.getBody();
        try {
            body.addForce(forceX, forceY, forceZ);
        }
        catch (NullPointerException e) {
            Gdx.app.error("ODEPhysicsEngine", "Failed to add force to object " + objectId + ". Body is null (object is not rigid body)");
        }
    }

    @Override
    public void addForceAt(int objectId, double forceX, double forceY, double forceZ, double posX, double posY, double posZ) {
        GameObject go = getObject(objectId);
        if (go == null) return;
        DBody body = (DBody) go.getBody();
        if (body == null) {
            Gdx.app.error("ODEPhysicsEngine", "Failed to add force at position to object " + objectId + ". Body is null (object is not rigid body)");
            return;
        }
        body.addForceAtPos(forceX, forceY, forceZ, posX, posY, posZ);
    }

    @Override
    public void setPosition(int objectId, double x, double y, double z) {
        GameObject go = getObject(objectId);
        if (go == null) return;
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
        if (go == null) return;
        // Remember the heading while actually moving so a later stop keeps the facing direction.
        if (speedX != 0.0 || speedY != 0.0) {
            go.setLastAngle(Math.atan2(speedY, speedX));
        }
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

        if(body.getData() instanceof GameObject go) {
            dimensions = go.getDimensions();
        }

        Gdx.app.log("ODEPhysicsEngine", "Setting mass for geomType: " + geomType +
                ", dimensions: [" + dimensions[0] + ", " + dimensions[1] + ", " + dimensions[2] + "]");


        DMass m = OdeHelper.createMass();
        switch (GeomType.values()[geomType]) {
            case BOX:
                m.setBoxTotal(mass, dimensions[0], dimensions[1], dimensions[2]);
                break;
            case CAPSULE:
                m.setCapsuleTotal(mass, 3, dimensions[0], dimensions[1]); // axis, radius, length
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
        if (go == null) return;
        DBody body = (DBody) go.getBody();
        try {
            setMass(body, mass, geomType);
        }
        catch (NullPointerException e) {
            Gdx.app.error("ODEPhysicsEngine", "Cannot set mass to object " + objectId + ". Body is null (object is not rigid body)");
        }
    }

    @Override
    public void setMass(int objectId, double mass) {
        GameObject go = getObject(objectId);
        if (go == null) return;
        go.setMass(mass);
        DBody body = (DBody) go.getBody();
        if (body == null) return;
        setMass(body, mass, go.getGeomType());
    }

    @Override
    public void setGravity(double gravityX, double gravityY, double gravityZ) {
        world.setGravity(gravityX, gravityY, gravityZ);
    }

    @Override
    public void setGravityCenter(int objectId, boolean gravityCenter) {
        GameObject go = getObject(objectId);
        if (go == null) return;
        go.setGravityCenter(gravityCenter);
    }

    @Override
    public void setMaxVelocity(int objectId, double maxVelocity) {
        GameObject go = getObject(objectId);
        if (go == null) return;
        go.setMaxVelocity(maxVelocity);
    }

    @Override
    public double[] getPosition(int objectId) {
        GameObject go = getObject(objectId);
        if (go == null) return new double[] {0, 0, 0};
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
        if (go == null) return new double[] {0, 0, 0};
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
        if (go == null) return 0.0;
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
        if (go == null) return 0.0;
        DBody body = (DBody) go.getBody();
        try {
            // get speed vector and calculate angle
            DVector3C velocity = body.getLinearVel();
            if (velocity.length() == 0) {
                return go.getLastAngle(); // stopped: keep the last movement heading
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
        if (go == null) return 0.0;
        return go.getMoveDistance();
    }

    @Override
    public double stepSimulation() {
        double reportedDt = smoothDeltaTime(timer.calculateStepSize());
        double usedDt = stepSimulation(reportedDt);
        writeTraceFrame(reportedDt, usedDt);
        return usedDt;
    }

    private void writeTraceFrame(double reportedDt, double usedDt) {
        if (traceWriter == null) return;
        try {
            traceWriter.writeFrame(reportedDt, usedDt, getGameObjects());
        } catch (IOException e) {
            Gdx.app.error("ODEPhysicsEngine", "Disabling physics trace after a write error", e);
            try {
                traceWriter.close();
            } catch (IOException ignored) {
                // Preserve the original write failure.
            }
            traceWriter = null;
        }
    }

    /**
     * Lag-spike smoothing from ISekai::MoveObjects: a dt more than twice the running
     * session average is replaced by that average, so a loading hitch doesn't turn into
     * one giant physics step.
     */
    private double smoothDeltaTime(double dt) {
        if (dt <= 0) return dt;
        dtFrames++;
        double avg = dtTotal / dtFrames;
        dtTotal += dt;
        if (2 * avg < dt && avg > 0.001) {
            dt = avg;
        }
        return dt;
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
            // get variable
            GameObject go = (GameObject) linkedBody.getData();
            EngineVariable var = linkedVariables.get(linkedBody);

            if (pausedLinkIds.contains(go.getId())) {
                continue; // PAUSELINK
            }

            // get body position
            double[] worldPos = linkedBody.getPosition().toDoubleArray();

            // synchronizing object position
            if ((go.getFlags() & 1) != 0) {
                // convert world position to screen position
                float screenX = cameraAnchor.worldToScreenX((float) worldPos[0]);
                float screenY = cameraAnchor.worldToScreenY((float) worldPos[1]);

                if (var instanceof Variable v2Var) {
                    v2Var.callMethod("SETPOSITION", List.of(
                            new IntValue((int) screenX),
                            new IntValue((int) screenY)
                    ), null);
                }
            }

            // pathfinding things
            if ((go.getFlags() & 2) != 0) {
                int isAtGoal = go.getIsAtGoal();
                if (isAtGoal == 1) {
                    emitSignalOnVar(var, "ATGOAL");
                    continue;
                } else if (isAtGoal == 2) {
                    emitSignalOnVar(var, "NOPATH");
                    continue;
                }

                List<Integer> collisionIds = go.getCollisionIds();
                if (!collisionIds.isEmpty()) {
                    for (Integer collisionId : collisionIds) {
                        emitSignalOnVar(var, String.valueOf(collisionId));
                    }
                    emitSignalOnVar(var, "ANY");
                } else {
                    emitSignalOnVar(var, "NOCOLL");
                }

                float currentSpeed = go.getSpeed();
                float lastSpeed = go.getLastSpeed();

                if (currentSpeed != lastSpeed) {
                    if (currentSpeed == 0.0f && lastSpeed != 0.0f) {
                        emitSignalOnVar(var, "ONFINISHED");
                    } else if (lastSpeed == 0.0f && currentSpeed != 0.0f) {
                        emitSignalOnVar(var, "ONSTARTED");
                    }
                }
            }
        }
    }

    private void emitSignalOnVar(EngineVariable var, String signalValue) {
        if (var instanceof Variable v2Var) {
            v2Var.emitSignal("ONSIGNAL", new StringValue(signalValue));
        }
    }

    private void calculateBodiesAttraction() {
        // SETG belongs to the gravity center only. Sekai multiplies it by both bodies' masses:
        // |F| = G_center * mass_center * mass_other / r². FootballMatch deliberately never
        // calls SETG on the ball, so treating the other body's G as a factor disables magnets.
        if (world == null) return;

        // The original has no r=0 guard (NaN/explosion); we keep a small epsilon instead.
        final double EPS2 = 1e-6;

        List<GameObject> gameObjects = getGameObjects();

        for (GameObject go : gameObjects) {
            if (!(go.isGravityCenter() && go.isActive())) continue;
            DBody centerBody = (DBody) go.getBody();

            if(centerBody == null) continue; // ignore non-rigid bodies

            double centerStrength = go.getG() * go.getMass();
            if (centerStrength == 0.0) continue;

            DVector3C pc = centerBody.getPosition();

            for (GameObject go2 : gameObjects) {
                if (!go2.isRigidBody()) continue;
                DBody other = (DBody) go2.getBody();
                if (other == centerBody) continue;
                if (other == null) continue; // ignore non-rigid bodies

                if(!go2.isActive()) continue;
                if(go2.isGravityExcluded(go.getId())) continue; // ADDGRAVITYEX

                DVector3C po = other.getPosition();

                double dx = pc.get0() - po.get0();
                double dy = pc.get1() - po.get1();
                double dz = pc.get2() - po.get2();

                double r2 = dx*dx + dy*dy + dz*dz;
                if (r2 < EPS2) continue;

                // Components are the normalized direction multiplied by |F|.
                double scalar = centerStrength * go2.getMass() / (r2 * Math.sqrt(r2));

                other.addForce(dx * scalar, dy * scalar, dz * scalar);
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

    @Override
    public double stepSimulation(double deltaTime) {
        if(deltaTime <= 0) return 0.0; // ignore zero and negative delta times
        if(deltaTime > 0.03) deltaTime = 0.03; // hard step cap from Sekai

        for (GameObject go : getGameObjects()) {
            go.getCollisionIds().clear(); // collisions from the previous frame
            go.setStepsize(deltaTime); // the original stores dt on every object each frame
        }

        calculateBodiesAttraction();

        // RiC predates substepping and always performs exactly one world step.
        // RiWC introduced internal ~60 Hz substeps (dt=0.03 → 2 × 0.015).
        // Each substep gathers fresh contacts and clears its temporary contact group,
        // as in the original.
        // dWorldStep (Dantzig LCP), not quickStep — the original never used SOR.
        int substeps = calculateSubstepCount(deltaTime);
        double h = deltaTime / substeps;
        for (int i = 0; i < substeps; i++) {
            space.collide(this, nearCallback);
            world.step(h);
            jointGroup.clear();
        }

        for (GameObject go : getGameObjects()) {
            DBody body = (DBody) go.getBody();
            if (body == null) {
                continue; // Skip null bodies
            }

            enforcePositionLimits(go, body);

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

    /**
     * Sekai clamps every coordinate after the ODE step and writes the clamped position back
     * to the body. It deliberately leaves velocity untouched.
     */
    private void enforcePositionLimits(GameObject go, DBody body) {
        DVector3C position = body.getPosition();
        double x = Math.max(go.getMinXLimit(), Math.min(go.getMaxXLimit(), position.get0()));
        double y = Math.max(go.getMinYLimit(), Math.min(go.getMaxYLimit(), position.get1()));
        double z = Math.max(go.getMinZLimit(), Math.min(go.getMaxZLimit(), position.get2()));
        if (x != position.get0() || y != position.get1() || z != position.get2()) {
            body.setPosition(x, y, z);
        }
    }

    int calculateSubstepCount(double deltaTime) {
        if (compatibilityProfile.isGameFamily(GameFamilies.REKSIO_CZARODZIEJE)) {
            return 1;
        }
        return 1 + (int) (deltaTime * 60.0);
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
        if (go == null) return;
        go.setLimits(minX, maxX, minY, maxY, minZ, maxZ);
    }

    @Override
    public void destroyBody(int objectId) {
        List<GameObject> gameObjects = objects.remove(objectId);
        if (gameObjects == null) {
            Gdx.app.debug("ODEPhysicsEngine", "destroyBody: no objects found with ID: " + objectId + " (ignored)");
            return;
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
            linkedVariables.remove(body); // Remove any linked variable
            body.destroy();
        }
    }

    @Override
    public void addJoint(int firstId, int secondId, double anchorX, double anchorY, double anchorZ, double limitMotor, double lowStop, double highStop, double hingeAxisX, double hingeAxisY, double hingeAxisZ) {
        GameObject go = getObject(firstId);
        if (go == null) return;
        DBody body1 = (DBody) go.getBody();
        if (body1 == null) {
            Gdx.app.error("ODEPhysicsEngine", "addJoint: object " + firstId + " has no rigid body, joint skipped");
            return;
        }
        GameObject go2 = getObject(secondId);
        // bodyType=0 has no dBody in Sekai. We keep a kinematic carrier in ode4j so its
        // geometry and script-visible position work, but joints must still attach to world.
        DBody body2 = (go2 != null && go2.isRigidBody()) ? (DBody) go2.getBody() : null;
        if (body2 == null) {
            Gdx.app.log("ODEPhysicsEngine", "No second body supplied for joint. Joining to world.");
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
    public void addJoint2(int firstId, int secondId,
                          double anchorX, double anchorY, double anchorZ,
                          double axis1X, double axis1Y, double axis1Z,
                          double axis2X, double axis2Y, double axis2Z) {
        GameObject go = getObject(firstId);
        if (go == null) return;
        DBody body1 = (DBody) go.getBody();
        if (body1 == null) {
            Gdx.app.error("ODEPhysicsEngine", "addJoint2: object " + firstId + " has no rigid body, joint skipped");
            return;
        }
        GameObject go2 = getObject(secondId);
        DBody body2 = (go2 != null && go2.isRigidBody()) ? (DBody) go2.getBody() : null;

        if (go.getJoint() != null) {
            DJoint oldJoint = (DJoint) go.getJoint();
            oldJoint.setFeedback(null);
            oldJoint.destroy();
        }

        DHinge2Joint joint = OdeHelper.createHinge2Joint(world);
        go.setJoint(joint, 0f); // JOIN2 never sets a break force, so no feedback either
        joint.attach(body1, body2);
        joint.setAnchor(anchorX, anchorY, anchorZ);
        setHinge2Axes(joint, axis1X, axis1Y, axis1Z, axis2X, axis2Y, axis2Z);
        // Constants hardcoded in Sekai (ODE demo_buggy pattern): stiff suspension,
        // steering locked at 0, wheel motor idle.
        joint.setParam(DJoint.PARAM_N.dParamSuspensionERP1, 0.8);
        joint.setParam(DJoint.PARAM_N.dParamSuspensionCFM1, 1e-5);
        joint.setParam(DJoint.PARAM_N.dParamLoStop1, 0.0);
        joint.setParam(DJoint.PARAM_N.dParamHiStop1, 0.0);
        joint.setParam(DJoint.PARAM_N.dParamVel2, 0.0);
        joint.setParam(DJoint.PARAM_N.dParamFMax2, 0.1);
        joint.setParam(DJoint.PARAM_N.dParamVel1, 0.0);
        joint.setParam(DJoint.PARAM_N.dParamFMax1, 0.2);
    }

    /**
     * ode4j validates each axis against the joint's other (initially default) axis, so setting
     * both target axes directly can trip the "linearly independent" assert. Routing through a
     * vector perpendicular to both targets makes the sequence safe for any independent pair.
     */
    private static void setHinge2Axes(DHinge2Joint joint,
                                      double a1x, double a1y, double a1z,
                                      double a2x, double a2y, double a2z) {
        double cx = a1y * a2z - a1z * a2y;
        double cy = a1z * a2x - a1x * a2z;
        double cz = a1x * a2y - a1y * a2x;
        double crossLen = Math.sqrt(cx * cx + cy * cy + cz * cz);
        if (crossLen < 1e-9) {
            Gdx.app.error("ODEPhysicsEngine", "addJoint2: hinge2 axes are parallel, keeping default axes");
            return;
        }
        // The cross product is parallel to the default axis2 (0,1,0) only when cx≈0 and cz≈0.
        if (Math.abs(cx) > 1e-9 * crossLen || Math.abs(cz) > 1e-9 * crossLen) {
            joint.setAxis1(cx, cy, cz);
            joint.setAxis2(a2x, a2y, a2z);
            joint.setAxis1(a1x, a1y, a1z);
        } else {
            joint.setAxis2(cx, cy, cz);
            joint.setAxis1(a1x, a1y, a1z);
            joint.setAxis2(a2x, a2y, a2z);
        }
    }

    @Override
    public void breakJoint(int objectId) {
        GameObject go = getObject(objectId);
        if (go == null || go.getJoint() == null) return;
        DJoint joint = (DJoint) go.getJoint();
        joint.setFeedback(null);
        joint.destroy();
        go.setJoint(null, go.getLimot());
    }

    @Override
    public void jointSteer(int objectId, double angle) {
        GameObject go = getObject(objectId);
        if (go == null) return;
        if (!(go.getJoint() instanceof DHinge2Joint joint)) return;
        // Proportional steering: velocity = angle error, stops at ±30°.
        joint.setParam(DJoint.PARAM_N.dParamVel1, angle - joint.getAngle1());
        joint.setParam(DJoint.PARAM_N.dParamFMax1, 0.2);
        joint.setParam(DJoint.PARAM_N.dParamLoStop1, -0.5236);
        joint.setParam(DJoint.PARAM_N.dParamHiStop1, 0.5236);
    }

    @Override
    public void jointSpeed(int objectId, double speed) {
        GameObject go = getObject(objectId);
        if (go == null) return;
        if (!(go.getJoint() instanceof DHinge2Joint joint)) return;
        joint.setParam(DJoint.PARAM_N.dParamVel2, speed);
        joint.setParam(DJoint.PARAM_N.dParamFMax2, 25.0);
    }

    @Override
    public void zeroAll(int objectId) {
        GameObject go = getObject(objectId);
        if (go == null) return;
        DBody body = (DBody) go.getBody();
        if (body == null) return;
        body.setLinearVel(0, 0, 0);
        body.setAngularVel(0, 0, 0);
        body.setForce(0, 0, 0);
        body.setTorque(0, 0, 0);
    }

    @Override
    public void rotate(int objectId, double angleDegrees) {
        GameObject go = getObject(objectId);
        if (go == null) return;
        DBody body = (DBody) go.getBody();
        if (body == null) return;
        DMatrix3 R = new DMatrix3();
        DRotation.dRFromAxisAndAngle(R, 0, 0, 1, Math.toRadians(angleDegrees));
        body.setRotation(R);
    }

    @Override
    public double getRotationX(int objectId) {
        GameObject go = getObject(objectId);
        if (go == null) return 0.0;
        DBody body = (DBody) go.getBody();
        try {
            DMatrix3C rotation = body.getRotation();
            return Math.atan2(rotation.get(2, 1), rotation.get(2, 2));
        }
        catch (NullPointerException e) {
            Gdx.app.error("ODEPhysicsEngine", "Cannot get rotation of object " + objectId + ". Body is null (object is not rigid body). Returning 0 instead");
            return 0.0;
        }
    }

    @Override
    public double getRotationY(int objectId) {
        GameObject go = getObject(objectId);
        if (go == null) return 0.0;
        DBody body = (DBody) go.getBody();
        try {
            DMatrix3C rotation = body.getRotation();
            return Math.atan2(-rotation.get(2, 0),
                    Math.hypot(rotation.get(2, 1), rotation.get(2, 2)));
        }
        catch (NullPointerException e) {
            Gdx.app.error("ODEPhysicsEngine", "Cannot get rotation of object " + objectId + ". Body is null (object is not rigid body). Returning 0 instead");
            return 0.0;
        }
    }

    @Override
    public boolean getCollision(int objectId) {
        GameObject go = getObject(objectId);
        return go != null && !go.getCollisionIds().isEmpty();
    }

    @Override
    public boolean getCollision(int objectId, int otherId) {
        GameObject go = getObject(objectId);
        return go != null && go.getCollisionIds().contains(otherId);
    }

    @Override
    public void setCollisionType(int objectId, int collisionType) {
        GameObject go = getObject(objectId);
        if (go == null) return;
        go.setCollisionType(collisionType);
    }

    @Override
    public void setLinkPaused(int objectId, boolean paused) {
        if (paused) {
            pausedLinkIds.add(objectId);
        } else {
            pausedLinkIds.remove(objectId);
        }
    }

    @Override
    public void setBodyProperties(int objectId, double mass, double sizeX, double sizeY, double sizeZ) {
        GameObject go = getObject(objectId);
        if (go == null) return;
        float[] dims = go.getDimensions();
        dims[0] = (float) sizeX;
        dims[1] = (float) sizeY;
        dims[2] = (float) sizeZ;
        go.setMass(mass);
        DBody body = (DBody) go.getBody();
        if (body == null) return;
        Iterator<DGeom> iterator = body.getGeomIterator();
        while (iterator.hasNext()) {
            DGeom geom = iterator.next();
            if (geom instanceof DBox) {
                ((DBox) geom).setLengths(sizeX, sizeY, sizeZ);
            } else if (geom instanceof DSphere) {
                ((DSphere) geom).setRadius(sizeX);
            } else if (geom instanceof DCapsule) {
                ((DCapsule) geom).setParams(sizeX, sizeY);
            }
        }
        setMass(body, mass, go.getGeomType());
    }

    @Override
    public void setBodyDynamics(int objectId, double mu, double friction, double bounce, double bounceVelocity, double maxVelocity) {
        GameObject go = getObject(objectId);
        if (go == null) return;
        go.setMu(mu);
        go.setFriction(friction);
        go.setBounce(bounce);
        go.setBounceVelocity(bounceVelocity);
        go.setMaxVelocity(maxVelocity);
    }

    @Override
    public void setGravityExclusion(int objectId, int centerId, boolean excluded) {
        GameObject go = getObject(objectId);
        if (go == null) return;
        go.setGravityExcluded(centerId, excluded);
    }

    @Override
    public void setG(int objectId, double g) {
        GameObject go = getObject(objectId);
        if (go == null) return;
        go.setG(g);
    }

    @Override
    public void setActive(int objectId, boolean active, boolean monitorCollisions) {
        GameObject go = getObject(objectId);
        if (go == null) return;
        go.setActive(active);
        go.setCollisionMonitoring(monitorCollisions);
    }

    @Override
    public void setReferenceObjectId(int referenceObjectId) {
        this.referenceObjectId = referenceObjectId;
    }

    @Override
    public void setBkgSize(double minX, double maxX, double minY, double maxY) {
        cameraAnchor.setLimits((float) minX, (float) maxX, (float) minY, (float) maxY);
    }

    @Override
    public void setMoveFlags(double moveX, double moveY) {
        cameraAnchor.setMoveFlags((float) moveX, (float) moveY);
    }

    @Override
    public int getBkgPosX() {
        return (int) cameraAnchor.getBkgPosX();
    }

    @Override
    public int getBkgPosY() {
        return (int) cameraAnchor.getBkgPosY();
    }

    @Override
    public void linkVariable(EngineVariable variable, int objectId) {
        GameObject go = getObject(objectId);
        if (go == null) return;
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
        if (go == null) return;
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
        if (go == null) return 0.0f;

        while (true) {
            Point3D currentWaypoint = go.getCurrentPathPoint();

            if (currentWaypoint == null) {
                go.setIsAtGoal(1);
                // Path exhausted = final goal reached: stop the body so it doesn't keep
                // coasting along the last steering vector.
                setSpeed(objectId, 0, 0, 0);
                return 0.0f;
            }

            double[] position = getPosition(objectId);
            double dx = currentWaypoint.x - position[0];
            double dy = currentWaypoint.y - position[1];
            double dz = currentWaypoint.z - position[2];

            DVector3 deltaToTarget = new DVector3(dx, dy, dz);
            double distance = deltaToTarget.length();

            // Is point far away?
            if (distance > arrivalRadius) {
                // If so, go to them

                // calculate normalized direction vector
                if(distance > 0.0f) {
                    deltaToTarget.normalize();
                }

                // get current forward direction
                DVector3C velVector = ((DBody) go.getBody()).getLinearVel();
                DVector3 forward = new DVector3(velVector.get0(), velVector.get1(), velVector.get2());
                if(forward.length() > 0.0f) {
                    forward.normalize();
                }

                DVector3 steeringForce = new DVector3(
                        deltaToTarget.get0() - forward.get0(),
                        deltaToTarget.get1() - forward.get1(),
                        deltaToTarget.get2() - forward.get2()
                );

                turnClamp = Math.abs(turnClamp);
                steeringForce.scale(turnClamp);

                forward.add(steeringForce);
                forward.normalize();
                forward.scale(speed);

                setSpeed(objectId, forward.get0(), forward.get1(), forward.get2());
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
        if (go == null || go2 == null) return;
        DBody body = (DBody) go.getBody();
        if(body == null) {
            Gdx.app.error("ODEPhysicsEngine", "Cannot find path for object " + objectId + ". Body is null (object is not rigid body)." );
            return;
        }
        DVector3C position = body.getPosition();
        Point3D start = new Point3D(position.get(0), position.get(1), position.get(2));
        Point3D target = new Point3D(
                cameraAnchor.screenToWorldX(targetX),
                cameraAnchor.screenToWorldY(targetY),
                targetZ
        );

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
        boolean wasInitialized = world != null;

        if (traceWriter != null) {
            try {
                traceWriter.close();
            } catch (IOException e) {
                Gdx.app.error("ODEPhysicsEngine", "Cannot close physics trace", e);
            }
            traceWriter = null;
        }

        for (Integer id : new ArrayList<>(objects.keySet())) {
            destroyBody(id);
        }
        pausedLinkIds.clear();
        for (DTriMeshData meshData : triMeshDatas) {
            meshData.destroy();
        }
        triMeshDatas.clear();
        if (jointGroup != null) {
            jointGroup.destroy();
            jointGroup = null;
        }
        if (world != null) {
            world.destroy();
            world = null;
        }
        if (space != null) {
            space.destroy();
            space = null;
        }
        timer = null;
        if (wasInitialized) {
            OdeHelper.closeODE();
        }
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
            if (!go1.isRigidBody() && !go2.isRigidBody()) {
                return; // Sekai does not collide two objects without dynamic bodies
            }

            DBody body1 = g1.getBody();
            DBody body2 = g2.getBody();
            if (body1 == null && body2 == null) {
                return; // Two static geoms never interact
            }
            if (body1 != null && body2 != null && OdeHelper.areConnected(body1, body2)) {
                return; // Joint-connected bodies don't generate contacts
            }

            int N = 4;
            DContactBuffer contacts = new DContactBuffer(N);
            DContactGeomBuffer gb = contacts.getGeomBuffer();
            int numContacts = OdeHelper.collide(g1, g2, N, gb);

            if (numContacts == 0) {
                return;
            }

            if (go1.isCollisionMonitoring()) {
                go1.getCollisionIds().add(go2.getId());
            }
            if (go2.isCollisionMonitoring()) {
                go2.getCollisionIds().add(go1.getId());
            }

            if (!go1.isActive() || !go2.isActive()) {
                return; // Contacts are reported above but push bodies only when both are active
            }

            for (int i = 0; i < numContacts; i++) {
                DContact c = contacts.get(i);
                // Same surface setup as Sekai: friction pyramid, per-pair CFM (0 unless SetCFM
                // was used — neither game does), no contact-level ERP.
                c.surface.mode = dContactApprox1 | dContactSoftCFM | dContactBounce;
                c.surface.mu = Math.min(go1.getMu(), go2.getMu());
                c.surface.bounce = Math.max(go1.getBounce(), go2.getBounce());
                c.surface.bounce_vel = Math.max(go1.getBounceVelocity(), go2.getBounceVelocity());
                c.surface.soft_cfm = Math.min(go1.getCfm(), go2.getCfm());

                DJoint j = OdeHelper.createContactJoint(world, jointGroup, c);
                j.attach(body1, body2);
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
                    case CAPSULE:
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
            out.println(sb);
        } catch (Exception e) {
            Gdx.app.error("ODEPhysicsEngine", "dumpSceneToJson failed", e);
        }
    }
}
