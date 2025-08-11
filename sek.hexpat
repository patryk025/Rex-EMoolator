enum GeomType : u32 {
    BOX,
    CYLINDER,
    SPHERE,
    TRIMESH,
    CAR // really? 4 wheels and box
};

enum DataType : u32 {
    INT = 0,
    FLOAT = 1,
    STRING = 2,
    LIST = 6
};

struct Header {
    char magic[19];
    u32 entitiesNumber;
};

struct EntityProps {
    char nameOfProp[];
    DataType dataType;
    char valueOfProp[];
};

struct VertexWithNormalAndUV {
    float x, y, z;           // 3D coordinates
    float normalX, normalY, normalZ; // normal vector
    float u, v;              // UV?
};

struct MeshTriangle {
    char name[]; // of material?
    VertexWithNormalAndUV point1;
    VertexWithNormalAndUV point2;
    VertexWithNormalAndUV point3;
    float unknown25, unknown26, unknown27; // 0 or 1 (flags?)
};

struct ObjectHeader {
    u32 entityId;
    u32 type; // 0 (static mesh) or 3 (physical entity)
    float x; // relative to 400px
    float y; // relative to 300px
    float z; // relative to 0px
    float unknown7; // ?, 0 or 1
    float rotationZ; // angle, but with very weird behaviour
    float unknown9; // ?
    float bodyDimension[3];
    u32 numberOfProps;
    EntityProps props[numberOfProps];
};

struct SceneObject {
    u32 length; // 56 bytes + triangles data
    ObjectHeader header;
    GeomType geomType;
    u32 numberOfTriangles;
    MeshTriangle collisionMeshTriangle[numberOfTriangles];
};

struct Point {
    float x, y, z;
    padding[4]; // 3D vector in ODE are 4 element float arrays (for better alignment)
};

struct PathData {
    u32 fromPoint;
    u32 toPoint;
    u32 unknown; // always 3 so far, probably bitflag or direction flag
};

struct PointsData {
    u32 length; // length without padding, noOfPoints * 12 + noOfPaths * 12 + 56
    ObjectHeader header; // id 9000 or 9001, other params are always 0
    u32 noOfPoints;
    u32 noOfPaths;
    Point points[noOfPoints];
    PathData paths[noOfPaths];
};


struct Entity {
    u32 typeId;
    if(typeId == 1) {
        SceneObject object;
    }
    if(typeId == 4) {
        PointsData points;
    }
};

struct SEKFile {
    Header header;
    Entity entity[header.entitiesNumber];
};

SEKFile sek@0x00;
