package pl.genschu.bloomooemulator.world;

import pl.genschu.bloomooemulator.engine.physics.IPhysicsEngine;

public class GameObject {
    private int id;
    private float rotationZ;
    private float x, y, z;
    private float prevX, prevY, prevZ;
    private float velX, velY, velZ;
    private double mass;
    private double mu;
    private double friction;
    private double bounce;
    private double bounceVelocity;
    private double maxVelocity;
    private double G; // Gravitational constant
    private boolean gravityCenter;
    private boolean active;
    private int geomType;
    private float minXLimit, minYLimit, minZLimit;
    private float maxXLimit, maxYLimit, maxZLimit;
    private float[] dimensions;
    private boolean rigidBody;
    private PointsData pointsData;
    private Mesh mesh;
    private Object body; // Placeholder for the physics body, type depends on the physics engine
    private Object joint; // Placeholder for the physics joint
    private Object jointFeedback;
    private float limot;
    private double stepsize;
    private boolean isLimited;
    private IPhysicsEngine physicsEngine;

    private GameObject() {}

    public static GameObjectBuilder builder() {
        return new GameObjectBuilder();
    }

    public static class GameObjectBuilder {
        private final GameObject obj;

        public GameObjectBuilder() {
            obj = new GameObject();
            obj.id = -1;
            obj.rotationZ = 0f;
            obj.x = 0f;
            obj.y = 0f;
            obj.z = 0f;
            obj.prevX = 0f;
            obj.prevY = 0f;
            obj.prevZ = 0f;
            obj.velX = 0f;
            obj.velY = 0f;
            obj.velZ = 0f;
            obj.mass = 1.0f;
            obj.mu = -1.0f;
            obj.friction = 0.0f;
            obj.bounce = 0.0f;
            obj.bounceVelocity = 0.0f;
            obj.maxVelocity = 0.0f;
            obj.G = 0.0f;
            obj.gravityCenter = false;
            obj.active = true;
            obj.minXLimit = -100_000f;
            obj.minYLimit = -100_000f;
            obj.minZLimit = -100_000f;
            obj.maxXLimit = 100_000f;
            obj.maxYLimit = 100_000f;
            obj.maxZLimit = 100_000f;
            obj.rigidBody = false;
            obj.geomType = 2;
            obj.dimensions = new float[]{1.0f, 1.0f, 1.0f};
            obj.pointsData = null;
            obj.mesh = null;
            obj.body = null;
            obj.joint = null;
            obj.jointFeedback = null;
            obj.limot = 0f;
            obj.stepsize = 0;
            obj.isLimited = false;
            obj.physicsEngine = null;
        }

        public GameObjectBuilder id(int id) {
            obj.id = id;
            return this;
        }

        public GameObjectBuilder rotationZ(float rotationZ) {
            obj.rotationZ = rotationZ;
            return this;
        }

        public GameObjectBuilder position(float x, float y, float z) {
            obj.x = x;
            obj.y = y;
            obj.z = z;
            return this;
        }

        public GameObjectBuilder dimensions(float[] dimensions) {
            obj.dimensions = dimensions;
            return this;
        }

        public GameObjectBuilder mass(double mass) {
            obj.mass = mass;
            return this;
        }

        public GameObjectBuilder mu(double mu) {
            obj.mu = mu;
            return this;
        }

        public GameObjectBuilder friction(double friction) {
            obj.friction = friction;
            return this;
        }

        public GameObjectBuilder bounce(double bounce) {
            obj.bounce = bounce;
            return this;
        }

        public GameObjectBuilder bounceVelocity(double bounceVelocity) {
            obj.bounceVelocity = bounceVelocity;
            return this;
        }

        public GameObjectBuilder maxVelocity(double maxVelocity) {
            obj.maxVelocity = maxVelocity;
            return this;
        }

        public GameObjectBuilder geomType(int geomType) {
            obj.geomType = geomType;
            return this;
        }

        public GameObjectBuilder rigidBody(boolean rigidBody) {
            obj.rigidBody = rigidBody;
            return this;
        }

        public GameObjectBuilder limits(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
            obj.minXLimit = minX;
            obj.minYLimit = minY;
            obj.minZLimit = minZ;
            obj.maxXLimit = maxX;
            obj.maxYLimit = maxY;
            obj.maxZLimit = maxZ;
            obj.isLimited = true;
            return this;
        }

        public GameObjectBuilder pointsData(PointsData pointsData) {
            obj.pointsData = pointsData;
            return this;
        }

        public GameObjectBuilder mesh(Mesh mesh) {
            obj.mesh = mesh;
            return this;
        }

        public GameObjectBuilder physicsEngine(IPhysicsEngine physicsEngine) {
            obj.physicsEngine = physicsEngine;
            return this;
        }

        public GameObject build() {
            return obj;
        }
    }

    private void speedDamping() {
        double[] v = physicsEngine.getSpeed(id);
        double vx = v[0], vy = v[1], vz = v[2];

        double mag = Math.sqrt(vx*vx + vy*vy + vz*vz);

        if (!(mag >= 0) || !(maxVelocity >= 0) || !(friction >= 0) || !(stepsize >= 0)) {
            return;
        }

        double magPrime = Math.max(0.0, Math.min(maxVelocity, mag - friction * stepsize));

        double scale = (mag > 1e-9) ? (magPrime / mag) : 0.0;

        double nx = vx * scale;
        double ny = vy * scale;
        double nz = vz * scale;

        if (Double.isFinite(nx) && Double.isFinite(ny) && Double.isFinite(nz)) {
            physicsEngine.setSpeed(id, nx, ny, nz);
        }
    }

    public void updateObject() {
        double[] position = physicsEngine.getPosition(id);

        this.prevX = this.x;
        this.prevY = this.y;
        this.prevZ = this.z;

        this.x = (float) position[0];
        this.y = (float) position[1];
        this.z = (float) position[2];

        this.speedDamping();
    }

    public double getMass() {
        return mass;
    }

    public double getMu() {
        return mu;
    }

    public double getFriction() {
        return friction;
    }

    public double getBounce() {
        return bounce;
    }

    public double getBounceVelocity() {
        return bounceVelocity;
    }

    public double getMaxVelocity() {
        return maxVelocity;
    }

    public int getGeomType() {
        return geomType;
    }

    public float getMinXLimit() {
        return minXLimit;
    }

    public float getMinYLimit() {
        return minYLimit;
    }

    public float getMinZLimit() {
        return minZLimit;
    }

    public float getMaxXLimit() {
        return maxXLimit;
    }

    public float getMaxYLimit() {
        return maxYLimit;
    }

    public float getMaxZLimit() {
        return maxZLimit;
    }

    public int getId() {
        return id;
    }

    public float getRotationZ() {
        return rotationZ;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    public float[] getDimensions() {
        return dimensions;
    }

    public boolean isRigidBody() {
        return rigidBody;
    }

    public PointsData getPointsData() {
        return pointsData;
    }

    public Mesh getMesh() { return mesh; }

    public void setMesh(Mesh mesh) {
        this.mesh = mesh;
    }

    public void setMaxVelocity(double maxVelocity) {
        this.maxVelocity = maxVelocity;
    }

    public void setLimits(double minX, double maxX, double minY, double maxY, double minZ, double maxZ) {
        minXLimit = (float) minX;
        minYLimit = (float) minY;
        minZLimit = (float) minZ;
        maxXLimit = (float) maxX;
        maxYLimit = (float) maxY;
        maxZLimit = (float) maxZ;
    }

    public void setRotationZ(float rotationZ) {
        this.rotationZ = rotationZ;
    }

    public void setX(float x) {
        this.prevX = this.x;
        this.x = x;
    }

    public void setY(float y) {
        this.prevY = this.y;
        this.y = y;
    }

    public void setZ(float z) {
        this.prevZ = this.z;
        this.z = z;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }

    public float getVelX() { return velX; }
    public float getVelY() { return velY; }
    public float getVelZ() { return velZ; }

    public void setVelocity(float vx, float vy, float vz) {
        this.velX = vx;
        this.velY = vy;
        this.velZ = vz;
    }

    public void setJoint(Object joint, float limot) {
        this.joint = joint;
        this.limot = limot;
    }

    public Object getJoint() {
        return joint;
    }

    public Object getJointFeedback() {
        return jointFeedback;
    }

    public void setJointFeedback(Object jointFeedback) {
        this.jointFeedback = jointFeedback;
    }

    public double getG() {
        return G;
    }

    public void setG(double g) {
        G = g;
    }

    public boolean isGravityCenter() {
        return gravityCenter;
    }

    public void setGravityCenter(boolean gravityCenter) {
        this.gravityCenter = gravityCenter;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public double getMoveDistance() {
        float xDelta = x - prevX;
        float yDelta = y - prevY;
        float zDelta = z - prevZ;
        return Math.sqrt(xDelta * xDelta + yDelta * yDelta + zDelta * zDelta);
    }

    public float getLimot() {
        return limot;
    }

    public void setLimot(float limot) {
        this.limot = limot;
    }

    public double getStepsize() {
        return stepsize;
    }

    public void setStepsize(double stepsize) {
        this.stepsize = stepsize;
    }
}
