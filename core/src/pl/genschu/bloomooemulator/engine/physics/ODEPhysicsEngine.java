package pl.genschu.bloomooemulator.engine.physics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import org.ode4j.math.DMatrix3C;
import org.ode4j.math.DVector3C;
import org.ode4j.ode.*;
import pl.genschu.bloomooemulator.geometry.points.Point3D;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.interpreter.variable.types.AnimoVariable;
import pl.genschu.bloomooemulator.interpreter.variable.types.ImageVariable;
import pl.genschu.bloomooemulator.world.GameObject;
import pl.genschu.bloomooemulator.world.Mesh;
import pl.genschu.bloomooemulator.world.MeshTriangle;
import pl.genschu.bloomooemulator.world.TriangleVertex;

import java.util.*;
import java.util.stream.Collectors;

public class ODEPhysicsEngine implements IPhysicsEngine {
    DWorld world;
    DSpace space;
    private final Map<Integer, List<DBody>> bodies = new HashMap<>();
    private final Map<DBody, Variable> linkedVariables = new HashMap<>();

    enum GeomType {
        BOX,
        CYLINDER,
        SPHERE
    }

    @Override
    public void init() {
        OdeHelper.initODE2(0);

        world = OdeHelper.createWorld();
        world.setGravity(0, 0, -9.8100004); // Default gravity in Sekai
        world.setCFM(0.00001f);
        world.setERP(0.8f);

        space = OdeHelper.createSimpleSpace();

    }

    @Override
    public void createBody(GameObject gameObject, Mesh geometryMesh) {
        DBody body = createBasicBody(gameObject.getId(), gameObject.getX(), gameObject.getY(), gameObject.getZ());
        setMass(body, gameObject.getMass(), gameObject.getGeomType());
        attachGeometry(body, gameObject.getGeomType());
        body.setData(gameObject);
        gameObject.setBody(body);
        gameObject.setMesh(geometryMesh);
        attachMesh(body, geometryMesh);
    }

    @Override
    public void createBody(
            int objectId, double mass, double mu, double mu2,
            double bounce, double bounceVelocity, double maxVelocity,
            int bodyType, int geomType,
            double x, double y, double z
    ) {
        createBody(objectId, mass, mu, mu2, bounce, bounceVelocity, maxVelocity, bodyType, geomType, x, y, z, null);
    }

    @Override
    public void createBody(
            int objectId, double mass, double mu, double mu2,
            double bounce, double bounceVelocity, double maxVelocity,
            int bodyType, int geomType,
            double x, double y, double z, Mesh geometryMesh
    ) {
        DBody body = createBasicBody(objectId, x, y, z);
        setMass(body, mass, geomType);
        attachGeometry(body, geomType);

        GameObject go = toGameObject(objectId, mass, mu, mu2, bounce, bounceVelocity, maxVelocity, geomType, x, y, z);
        body.setData(go);
        go.setBody(body);
        go.setMesh(geometryMesh);

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
                OdeHelper.createBox(dimensions[0], dimensions[1], dimensions[2]).setBody(body);
                break;
            case CYLINDER:
                OdeHelper.createCylinder(dimensions[0], dimensions[1]).setBody(body);
                break;
            case SPHERE:
                OdeHelper.createSphere(dimensions[0]).setBody(body);
                break;
        }
    }

    private void attachMesh(DBody body, Mesh mesh) {
        if (mesh == null) return;

        DTriMeshData meshData = OdeHelper.createTriMeshData();
        List<Float> verticesList = new ArrayList<>();
        List<Integer> indicesList = new ArrayList<>();
        Map<Point3D, Integer> vertexToIndex = new HashMap<>();

        int index = 0;
        for (MeshTriangle triangle : mesh.getTriangles()) {
            for (TriangleVertex v : triangle.getVertices()) {
                Point3D point = v.getPoint();
                if (!vertexToIndex.containsKey(point)) {
                    verticesList.add((float) point.x);
                    verticesList.add((float) point.y);
                    verticesList.add((float) point.z);
                    vertexToIndex.put(point, index++);
                }
                indicesList.add(vertexToIndex.get(point));
            }
        }

        float[] vertices = new float[verticesList.size()];
        for (int i = 0; i < verticesList.size(); i++) {
            vertices[i] = verticesList.get(i);
        }
        int[] indices = indicesList.stream().mapToInt(i -> i).toArray();
        meshData.build(vertices, indices);

        DTriMesh triMesh = OdeHelper.createTriMesh(space, meshData, null, null, null);
        triMesh.setBody(body);
    }

    private GameObject toGameObject(
            int objectId, double mass, double mu, double mu2,
            double bounce, double bounceVelocity, double maxVelocity,
            int geomType, double x, double y, double z
    ) {
        return GameObject.builder()
                .id(objectId)
                .mass(mass)
                .mu(mu)
                .mu2(mu2)
                .bounce(bounce)
                .bounceVelocity(bounceVelocity)
                .maxVelocity(maxVelocity)
                .geomType(geomType)
                .position((float)x, (float)y, (float)z)
                .build();
    }


    @Override
    public void addForce(int objectId, double forceX, double forceY, double forceZ) {
        List<DBody> bodyObjects = bodies.get(objectId);
        if (bodyObjects == null) {
            throw new IllegalArgumentException("No body found with ID: " + objectId);
        }
        if( bodyObjects.isEmpty()) {
            throw new IllegalStateException("No bodies associated with ID: " + objectId);
        }
        for (DBody body : bodyObjects) {
            if (body == null) {
                continue; // Skip null bodies
            }
            // Apply force to each body associated with the objectId
            body.addForce(forceX, forceY, forceZ);
        }
    }

    @Override
    public void setPosition(int objectId, double x, double y, double z) {
        DBody body = bodies.get(objectId).get(0); // TODO: check this case, maybe we should use all bodies?
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
        if (body.getData() instanceof GameObject) {
            GameObject go = (GameObject) body.getData();
            go.setX((float) x);
            go.setY((float) y);
            go.setZ((float) z);
        }
    }

    @Override
    public void setSpeed(int objectId, double speedX, double speedY, double speedZ) {
        for(DBody body : bodies.get(objectId)) {
            if (body == null) {
                continue; // Skip null bodies
            }
            // Set linear velocity for each body associated with the objectId
            body.setLinearVel(speedX, speedY, speedZ);
        }
    }

    private void setMass(DBody body, double mass, int geomType) {
        float[] dimensions = new float[]{1.0f, 1.0f, 1.0f};

        if(body.getData() instanceof GameObject) {
            GameObject go = (GameObject) body.getData();
            dimensions = go.getDimensions();
        }

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
        body.setMass(m);
    }

    @Override
    public void setMass(int objectId, double mass, int geomType) {
        List<DBody> bodyObjects = bodies.get(objectId);
        if (bodyObjects == null || bodyObjects.isEmpty()) {
            throw new IllegalArgumentException("No body found with ID: " + objectId);
        }
        for(DBody body : bodyObjects) {
            if (body == null) {
                continue; // Skip null bodies
            }

            setMass(body, mass, geomType);
        }
    }

    @Override
    public void setGravity(double gravityX, double gravityY, double gravityZ) {
        world.setGravity(gravityX, gravityY, gravityZ);
    }

    @Override
    public void setMaxVelocity(int objectId, double maxVelocity) {
        List<DBody> bodyObjects = bodies.get(objectId);
        if (bodyObjects == null || bodyObjects.isEmpty()) {
            throw new IllegalArgumentException("No body found with ID: " + objectId);
        }
        for(DBody body : bodyObjects) {
            GameObject go = (GameObject) body.getData();
            go.setMaxVelocity(maxVelocity);
        }
    }

    @Override
    public double[] getPosition(int objectId) {
        DBody body = bodies.get(objectId).get(0);
        DVector3C position = body.getPosition();
        return position.toDoubleArray();
    }

    @Override
    public double getSpeed(int objectId) {
        DBody body = bodies.get(objectId).get(0);
        DVector3C velocity = body.getLinearVel();
        return velocity.length();
    }

    @Override
    public double getRotationZ(int objectId) {
        DBody body = bodies.get(objectId).get(0);
        // ODE does not provide a direct way to get rotation, we can calculate it from the orientation
        DMatrix3C rotation = body.getRotation();
        // Assuming the rotation is in a 3x3 matrix, we can extract the Z rotation
        return Math.atan2(rotation.get(1, 0), rotation.get(0, 0));
    }

    @Override
    public double getAngle(int objectId) {
        // get speed vector and calculate angle
        DBody body = bodies.get(objectId).get(0);
        DVector3C velocity = body.getLinearVel();
        if (velocity.length() == 0) {
            return 0.0; // No movement, angle is 0
        }
        return Math.atan2(velocity.get(1), velocity.get(0));
    }

    @Override
    public void stepSimulation(double deltaTime) {
        if(deltaTime > 0.03f) deltaTime = 0.03f; // that limit was in Sekai
        //world.step(deltaTime);
        world.quickStep(deltaTime);

        for (DBody body : bodies.values().stream().flatMap(List::stream).filter(Objects::nonNull).collect(Collectors.toList())) {
            if (body == null) {
                continue; // Skip null bodies
            }

            if(body.getData() instanceof GameObject) {
                GameObject go = (GameObject) body.getData();

                // clamp velocity
                double maxVel = go.getMaxVelocity();

                if(maxVel > 0) {
                    DVector3C vel = body.getLinearVel();
                    double speed = vel.length();
                    if (speed > maxVel) {
                        double scale = maxVel / speed;
                        body.setLinearVel(
                                vel.get0() * scale,
                                vel.get1() * scale,
                                vel.get2() * scale
                        );
                    }
                }

                // clamp position
                DVector3C pos = body.getPosition();
                float minX = go.getMinXLimit(), maxX = go.getMaxXLimit();
                float minY = go.getMinYLimit(), maxY = go.getMaxYLimit();
                float minZ = go.getMinZLimit(), maxZ = go.getMaxZLimit();

                double clampedX = MathUtils.clamp(pos.get0(), minX, maxX);
                double clampedY = MathUtils.clamp(pos.get1(), minY, maxY);
                double clampedZ = MathUtils.clamp(pos.get2(), minZ, maxZ);
                body.setPosition(clampedX, clampedY, clampedZ);

                go.setX((float) clampedX);
                go.setY((float) clampedY);
                go.setZ((float) clampedZ);
                go.setRotationZ((float) getRotationZ(go.getId()));

                DVector3C v = body.getLinearVel();
                go.setVelocity((float)v.get0(), (float)v.get1(), (float)v.get2());
            }

            // Update the position of the body in the linked variable
            if(linkedVariables.containsKey(body)) {
                Variable var = linkedVariables.get(body);
                if(var instanceof ImageVariable) {
                    ImageVariable imageVar = (ImageVariable) var;
                    double[] position = body.getPosition().toDoubleArray();
                    imageVar.setPosX((int) position[0]);
                    imageVar.setPosY((int) position[1]);
                    // TODO: scaling image based on Z axis
                    imageVar.updateRect();
                }
                if(var instanceof AnimoVariable) {
                    AnimoVariable animoVar = (AnimoVariable) var;
                    double[] position = body.getPosition().toDoubleArray();
                    animoVar.setPosX((int) position[0]);
                    animoVar.setPosY((int) position[1]);
                    // TODO: scaling image based on Z axis
                    animoVar.updateRect();
                }
            }
        }
    }

    @Override
    public void setLimit(int objectId, double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        for(DBody body : bodies.get(objectId)) {
            if (body == null) {
                continue; // Skip null bodies
            }
            GameObject go = (GameObject) body.getData();
            go.setLimits(minX, maxX, minY, maxY, minZ, maxZ);
        }
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
            space.remove(body.getFirstGeom());
            linkedVariables.remove(body); // Remove any linked variable
            body.destroy();
        }
    }

    @Override
    public void linkVariable(Variable variable, int objectId) {
        DBody body = bodies.get(objectId).get(0);
        linkedVariables.put(body, variable);
    }

    @Override
    public void unlinkVariable(int objectId) {
        List<DBody> objectBodies = bodies.get(objectId);
        if (objectBodies == null || objectBodies.isEmpty()) {
            throw new IllegalArgumentException("No body found with ID: " + objectId);
        }
        for (DBody body : objectBodies) {
            linkedVariables.remove(body);
        }
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
}
