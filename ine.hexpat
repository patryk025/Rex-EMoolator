enum BodyType : s32 {
    CBodyBox=1,
    CBodySphere=2,
    CBodyCone=3,
    CBodyCCylinder=4,
    CBodyCylinder=5,
    CBodyHull=6,
    CBodyLevel=7
};

enum Material : s32 {
    DEFAULT=0,
    BOUNCY=1,
    RIGID=2,
    TRIGGER=5, // dedicate callbacks
    GHOST=6,
    REACTIVE=7 // mixed callbacks
};

struct Point2D {
    float x_px;
    float y_px;
};

struct VertexArray {
    u32 nVerts;
    Point2D verts[nVerts]; // it's converted later to vec4(x_px, y_px, 0, 1)
};

struct Body {
    s32 object_id; // if -1 it's automatic
    BodyType body_type;
    u32 unused; // read but not used
    Material groupId;
    float x_px; // mapped (x-400)*0.01, 100px per meter
    float y_px; // mapped (300-y)*0.01, reversed y-scale, 100px per meter
    float angle; //?
    if(body_type != 7) {
        float mass;
        if(body_type != 6) {
            float dimm1;
            float dimm2;
            if(body_type == 1 || body_type == 2) {
                float dimm3;
            }
        }
    }
    if(body_type == 6 || body_type == 7) {
        VertexArray array; // flat polyline extruded to collision walls
        // for CBodyHull it's 10 meters
        // for CBodyLevel it's 2 meters
    }
};

struct INE {
    char magic[4]; // INE1
    u32 count;
    Body bodies[count];
};

INE ine @ 0x0;