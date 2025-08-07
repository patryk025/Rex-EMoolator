package pl.genschu.bloomooemulator.world;

public class GameObject {
    private int id;
    private float rotationZ;
    private float x, y, z;
    private double mass;
    private double mu;
    private double mu2;
    private double bounce;
    private double bounceVelocity;
    private double maxVelocity;
    private int geomType;
    private float minXLimit, minYLimit, minZLimit;
    private float maxXLimit, maxYLimit, maxZLimit;
    private float[] dimensions;
    private boolean rigidBody;
    private PointsData pointsData;

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
            obj.mass = 1.0f;
            obj.mu = -1.0f;
            obj.mu2 = 0.0f;
            obj.bounce = 0.0f;
            obj.bounceVelocity = 0.0f;
            obj.maxVelocity = 0.0f;
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

        public GameObjectBuilder mu2(double mu2) {
            obj.mu2 = mu2;
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
            return this;
        }

        public GameObjectBuilder pointsData(PointsData pointsData) {
            obj.pointsData = pointsData;
            return this;
        }

        public GameObject build() {
            return obj;
        }
    }

    public double getMass() {
        return mass;
    }

    public double getMu() {
        return mu;
    }

    public double getMu2() {
        return mu2;
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
}
