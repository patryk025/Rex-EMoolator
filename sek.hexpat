struct Header {
    char magic[19];
    u32 entitiesNumber;
};

struct EntityProps {
    char nameOfProp[];
    padding[4]; //?
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

struct SceneObject {
    u32 length; // 56 bytes + triangles data
    u32 entityId;
    u32 unknown3; // 0 (walls) or 3 (rigid body?)
    float x; // relative to 400px
    float y; // relative to 300px
    float z; // relative to 0px
    float unknown7; // ?, 0 or 1
    float rotationZ; // normalized angle, but with very weird behaviour
    padding[4]; // ?
    float unknown10; // have impact on speed somehow
    float unknown11;
    float unknown12;
    u32 numberOfProps;
    EntityProps props[numberOfProps];
    u32 unknown13; // 0 or 2
    u32 numberOfTriangles;
    MeshTriangle meshTriangle[numberOfTriangles];
};

struct Point {
    float x, y, z;
    padding[4];
};

struct PathData {
    u32 fromPoint;
    u32 toPoint;
    u32 unknown; // always 3 so far, probably bitflag or direction flag
};

struct PointsData {
    u32 length; // length without padding, noOfPoints * 12 + noOfPaths * 12 + 56
    u32 unknown2; // 9000 or 9001?
    char unknown3[44]; // NUL bytes
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