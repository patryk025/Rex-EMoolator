#ifndef SEKAI_SHIM_H
#define SEKAI_SHIM_H

#pragma once

// =============================================================================
// DLL Export/Import Configuration
// =============================================================================
#ifdef SEKAI_SHIM_EXPORTS
#define SEKAI_API __declspec(dllexport)
#else
#define SEKAI_API __declspec(dllimport)
#endif

// =============================================================================
// Includes
// =============================================================================
#include <windows.h>
#include <cstdint>
#include <cstdio>

// =============================================================================
// Forward Declarations
// =============================================================================
class ISekai;
template<typename T> class IVector;

// =============================================================================
// Data Structures - Sekai Manager
// =============================================================================

// Sekai Manager structure (at fixed global address)
struct SekaiManager {
    void* vtable;                      // +0x00
    uint32_t capacity;                 // +0x04 - capacity
    uint32_t count;                    // +0x08 - number of objects
    void** objects;                    // +0x0C - array of object pointers
};

// =============================================================================
// Data Structures - Sekai Object Base
// =============================================================================

// Sekai Object Base structure
struct SekaiObjectBase {
    void* vtable;                      // +0x00
    uint32_t field_04;                 // +0x04 (looks like pointer)
    uint32_t id;                       // +0x08
    uint32_t field_0C;                 // +0x0C
    uint32_t geomType;                 // +0x10 (0=box, 1=cyl, 2=sphere, 3=mesh, 4=trimesh)
    uint32_t field_14;                 // +0x14
    bool isRigid;                      // +0x18
    bool hasLimits;                    // +0x19
    bool collisionEnabled;             // +0x1A
    bool gravityCenter;                // +0x1B
    void* physicsBody;                 // +0x1C (dBodyID)
    void* physicsGeom;                 // +0x20 (dGeomID)
    void* joint;                       // +0x24 (dJointID)
    double field_28;                   // +0x28
    double field_30;                   // +0x30
    double field_38;                   // +0x38
    double CFM;                        // +0x40
    double maxSpeed;                   // +0x48
    float field_4C;                    // +0x4C
    double currentSpeed;               // +0x50
    float field_54;                    // +0x54
    double mass;                       // +0x58
    float field_60;                    // +0x60
    float field_64;                    // +0x64
    double gravity_G;                  // +0x68
    // ... more fields ...
    uint8_t gap_70[0xB4];              // +0x70 to +0x123
    
    // +0x124 - Waypoint vector
    void* waypointVtable;              // +0x124
    uint32_t field_128;                // +0x128
    uint32_t field_12C;                // +0x12C
    
    // +0x134 - Path vector
    void* pathVtable;                  // +0x134
    uint32_t waypointCapacity;         // +0x138
    uint32_t waypointCount;            // +0x13C
    void* waypoints;                   // +0x140 (Vector3*)
    uint32_t field_144;                // +0x144
    
    // +0x148 - Collision vector (IVector<ulong>)
    void* collisionVtable;             // +0x148
    uint32_t collisionCapacity;        // +0x14C
    uint32_t collisionCount;           // +0x150
    uint32_t* collisionIDs;            // +0x154
};

// =============================================================================
// Data Structures - Shim Context
// =============================================================================

// Shim context
struct SekaiShim {
    HMODULE originalDll;
    void* managerPtr;                  // Pointer to global manager (DAT_10040624)
    float forcedStepTime;              // Override step time for deterministic testing
    bool dumpEnabled;                  // Enable object dumping
    FILE* logFile;
    
    // Original function pointers (loaded from Sekai_orig.dll)
    void* originalFunctions[76];       // Array of original function pointers
};

// =============================================================================
// Global Variables
// =============================================================================
extern SekaiShim g_shim;

// =============================================================================
// Helper Functions
// =============================================================================
void InitShim();
void DumpObject(uint32_t objectId);
void DumpAllObjects();
void LogCall(const char* funcName, const char* format, ...);
SekaiObjectBase* GetObjectById(uint32_t objectId);
int GetObjectCount();

// =============================================================================
// Macros
// =============================================================================
#define LOG_CALL(name, ...) if (g_shim.logFile) LogCall(name, __VA_ARGS__)

// =============================================================================
// Vtable Function Signatures & Inline Helpers
// =============================================================================

// Vtable function signatures (for direct calls if needed)
typedef uint32_t (__thiscall *GetIdFunc)(void* obj);
typedef void (__thiscall *GetPositionFunc)(void* obj, float* outX, float* outY, float* outZ);
typedef bool (__thiscall *IsActiveFunc)(void* obj);
typedef float (__thiscall *GetSpeedFunc)(void* obj);

// Inline helpers for accessing vtable functions
inline uint32_t CallGetId(void* obj) {
    void** vtable = *(void***)obj;
    GetIdFunc func = (GetIdFunc)vtable[0x38 / 4];
    return func(obj);
}

inline void CallGetPosition(void* obj, float* x, float* y, float* z) {
    void** vtable = *(void***)obj;
    GetPositionFunc func = (GetPositionFunc)vtable[0x1C / 4];
    func(obj, x, y, z);
}

inline bool CallIsActive(void* obj) {
    void** vtable = *(void***)obj;
    IsActiveFunc func = (IsActiveFunc)vtable[0x24 / 4];
    return func(obj);
}

inline float CallGetSpeed(void* obj) {
    void** vtable = *(void***)obj;
    GetSpeedFunc func = (GetSpeedFunc)vtable[0x5C / 4];
    return func(obj);
}

// =============================================================================
// ISekai Interface Class
// =============================================================================

// ISekai class interface (must match original DLL vtable layout)
class SEKAI_API ISekai {
public:
    // Constructor/Destructor
    ISekai();
    virtual ~ISekai();
    
    // All exported methods (ordinals 1-75)
    virtual long Create(const char* path);
    virtual long AddBody(uint32_t id, float p1, float p2, float p3, float p4, float p5, float p6, 
                        int rigid, int geomType, float d1, float d2, float d3);
    virtual long Release();
    virtual long AddForceAt(uint32_t id, float fx, float fy, float fz, float px, float py, float pz);
    virtual long GetCollision(uint32_t id1, uint32_t id2, bool* result);
    virtual long GetCollision(uint32_t id, bool* result);
    virtual long Start();
    virtual long Stop();
    virtual long Step(float dt);
    long MoveObjects(float* outDt);
    virtual long SetPosition(uint32_t id, float x, float y, float z);
    virtual long GetAngle(uint32_t id, float* angle);
    virtual long GetRotationZ(uint32_t id, float* rotation);
    virtual long GetActive(uint32_t id, bool* active);
    virtual long GetPosition(uint32_t id, float* x, float* y, float* z);
    virtual long SetVelocity(uint32_t id, float vx, float vy, float vz, bool immediate);
    virtual long Follow(uint32_t id, uint32_t targetId, float p1, float p2, float p3, float p4, float* out);
    virtual long FollowPath(uint32_t id, float p1, float p2, float p3, float* out);
    virtual long FindPath(uint32_t id, uint32_t meshId, float x, float y, float z, bool flag);
    virtual long GetSpeed(uint32_t id, float* speed);
    virtual long GetAllCollisions(uint32_t id, IVector<uint32_t>** vector);
    virtual long Load(const char* path);
    virtual long RemoveObject(uint32_t id);
    virtual long IsAtGoal(uint32_t id, uint32_t* result);
    virtual long SetActive(uint32_t id, bool active, bool flag);
    virtual long SetActivePath(uint32_t id, uint32_t pathId, bool active);
    virtual long GetMoveDistance(uint32_t id, float* distance);
    virtual long GotoPath(uint32_t id, float param, float* out);
    virtual long SetGravity(float gx, float gy, float gz);
    virtual long SetMaxSpeed(uint32_t id, float maxSpeed);
    virtual long SetBodyDynamics(uint32_t id, float p1, float p2, float p3, float p4, float p5);
    virtual long SetLimit(uint32_t id, float lx, float ly, float lz, float hx, float hy, float hz);
    virtual long SetLimits(float lx, float ly, float lz, float hx, float hy, float hz);
    virtual long SetBodyProperties(uint32_t id, float mass, float p2, float p3, float p4);
    virtual long AddObject(uint32_t id, int type);
    virtual long AddForce(uint32_t id, float fx, float fy, float fz);
    virtual long SetMass(uint32_t id, float mass);
    virtual long SetRadius(uint32_t id, float rx, float ry, float rz);
    virtual long Rotate(uint32_t id, int axis, float angle);
    virtual long SetCFM(uint32_t id, float cfm);
    virtual long SetGravityCenter(uint32_t id, bool enable);
    virtual long SetG(uint32_t id, double g);
    virtual long Join(uint32_t id1, uint32_t id2, float p1, float p2, float p3, float p4, float p5, float p6);
    virtual long GetMoveVector(uint32_t id, float* vx, float* vy, float* vz);
    virtual long Break(uint32_t id);
    virtual long ZeroAll(uint32_t id);
    virtual long MoveTo(uint32_t id, float x, float y, float z, float p4, float p5, float* out);
    virtual long MoveToPath(uint32_t id, float p1, float p2, float* out);
    virtual long AddToPath(uint32_t id, float x, float y, float z);
    virtual long AddWayPoint(uint32_t id, float x, float y, float z);
};

#endif // SEKAI_SHIM_H
