#define WIN32_LEAN_AND_MEAN
// opcjonalnie dodatkowe zabezpieczenie (gdyby ktoś kiedyś zainkludował winsock.h):
#ifndef _WINSOCKAPI_
#define _WINSOCKAPI_
#endif

#include <winsock2.h>
#include <Ws2tcpip.h>
#include <windows.h>
#include "sekai_shim.h"
#include <cstdarg>
#include <cstring>
#include <atomic>
#include <cstdint>
#include <stdint.h>
#include <vector>

#pragma comment(lib, "ws2_32.lib")
#pragma comment(lib, "user32.lib")

extern "C" IMAGE_DOS_HEADER __ImageBase;

// Global shim instance
SekaiShim g_shim = {0};

// Known address of the manager in original DLL
#define MANAGER_OFFSET 0x10040624
#define WORLD_OFFSET 0x10040628
#define SPACE_OFFSET 0x1004062C
#define JOINT_GROUP_OFFSET 0x10040630

struct FrameHeader { uint32_t magic, version, frameIndex; double timestamp; uint32_t objectCount; };
struct ObjRec {
    uint32_t id, geomType;
    float pos_x,pos_y,pos_z;
    float r00,r01,r02, r10,r11,r12, r20,r21,r22;
    float speed;
    uint32_t flags;
};

struct ShimConfig {
    int  startServer = 1;
    char udpHost[64] = "127.0.0.1";
    int  udpPort = 44444;
    int  dumpEnabled = 1;
} g_cfg;

static void LoadIni() {
    char dllPath[MAX_PATH]{}, iniPath[MAX_PATH]{};
    GetModuleFileNameA((HMODULE)&__ImageBase, dllPath, MAX_PATH);
    // zamień rozszerzenie na .ini
    char* dot = strrchr(dllPath, '.');
    if (!dot) dot = dllPath + strlen(dllPath);
    strcpy_s(dot, MAX_PATH - (dot - dllPath), ".ini");
    strcpy_s(iniPath, dllPath);

    GetPrivateProfileStringA("udp", "host", "127.0.0.1", g_cfg.udpHost, (DWORD)sizeof(g_cfg.udpHost), iniPath);
    g_cfg.startServer   = GetPrivateProfileIntA("udp", "startServer", 1, iniPath);
    g_cfg.udpPort   = GetPrivateProfileIntA("udp", "port", 44444, iniPath);
    g_cfg.dumpEnabled = GetPrivateProfileIntA("debug", "dumpEnabled", 1, iniPath);
}

SOCKET sock = INVALID_SOCKET;
sockaddr_in addr;

void InitUDP(const char* host="127.0.0.1", unsigned short port=44444) {
    WSADATA w{};
    if (WSAStartup(MAKEWORD(2,2), &w) != 0) return;
    sock = socket(AF_INET, SOCK_DGRAM, IPPROTO_UDP);
    if (sock == INVALID_SOCKET) return;
    memset(&addr, 0, sizeof(addr));
    addr.sin_family = AF_INET;
    addr.sin_port = htons(port);
    InetPtonA(AF_INET, host, &addr.sin_addr);
}

void SendSnapshotUDP(uint32_t frameIndex, double ts, std::vector<ObjRec>& objs) {
    if (sock==INVALID_SOCKET) return;
    FrameHeader h = {0x53454B41, 1, frameIndex, ts, (uint32_t)objs.size()};
    // build buffer
    size_t sz = sizeof(h) + objs.size()*sizeof(ObjRec);
    std::vector<char> buf(sz);
    memcpy(buf.data(), &h, sizeof(h));
    memcpy(buf.data()+sizeof(h), objs.data(), objs.size()*sizeof(ObjRec));
    sendto(sock, buf.data(), (int)sz, 0, (sockaddr*)&addr, sizeof(addr));
}

void InitShim() {
    // Load original DLL with different name
    g_shim.originalDll = LoadLibraryA("Sekai_orig.dll");
    if (!g_shim.originalDll) {
        MessageBoxA(NULL, "Failed to load Sekai_orig.dll", "Error", MB_OK);
        ExitProcess(1);
    }
    
    // Calculate manager pointer (base + offset)
    HMODULE baseAddr = g_shim.originalDll;
    g_shim.managerPtr = (void*)((uintptr_t)baseAddr + (MANAGER_OFFSET - 0x10000000));
    
    // Open log file
    g_shim.logFile = fopen("sekai_shim.log", "w");
    if (g_shim.logFile) {
        fprintf(g_shim.logFile, "Sekai Shim initialized\n");
        fprintf(g_shim.logFile, "Original DLL base: 0x%p\n", baseAddr);
        fprintf(g_shim.logFile, "Manager pointer: 0x%p\n", g_shim.managerPtr);
        fflush(g_shim.logFile);
    }
    
    /*LoadIni();
    if(g_cfg.startServer) {
        InitUDP(g_cfg.udpHost, g_cfg.udpPort);
    }*/
    
    g_shim.dumpEnabled = g_cfg.dumpEnabled;
}

void LogCall(const char* funcName, const char* format, ...) {
    if (!g_shim.logFile) return;
    
    fprintf(g_shim.logFile, "[%s] ", funcName);
    
    va_list args;
    va_start(args, format);
    vfprintf(g_shim.logFile, format, args);
    va_end(args);
    
    fprintf(g_shim.logFile, "\n");
    fflush(g_shim.logFile);
}

int GetObjectCount() {
    if (!g_shim.managerPtr) return 0;
    
    SekaiManager* mgr = *(SekaiManager**)g_shim.managerPtr;
    if (!mgr) return 0;
    
    return mgr->count;
}

SekaiObjectBase* GetObjectById(uint32_t objectId) {
    if (!g_shim.managerPtr) return nullptr;

    auto* mgr = *(SekaiManager**)g_shim.managerPtr;
    if (!mgr || !mgr->objects || mgr->count == 0) return nullptr;

    for (int i = (int)mgr->count - 1; i >= 0; --i) {
        auto* obj = (SekaiObjectBase*)mgr->objects[i];
        if (!obj) continue;
        if (CallGetId(obj) == objectId)
            return obj;
    }
    return nullptr;
}

void DumpObject(uint32_t objectId) {
    if (!g_shim.logFile) return;
    
    SekaiObjectBase* obj = GetObjectById(objectId);
    if (!obj) {
        fprintf(g_shim.logFile, "Object %u not found\n", objectId);
        return;
    }
    
    fprintf(g_shim.logFile, "\n=== Object %u Dump ===\n", objectId);
    fprintf(g_shim.logFile, "vtable: 0x%p\n", obj->vtable);
    fprintf(g_shim.logFile, "geomType: %u ", obj->geomType);
    switch(obj->geomType) {
        case 0: fprintf(g_shim.logFile, "(Box)\n"); break;
        case 1: fprintf(g_shim.logFile, "(Cylinder)\n"); break;
        case 2: fprintf(g_shim.logFile, "(Sphere)\n"); break;
        case 3: fprintf(g_shim.logFile, "(Mesh)\n"); break;
        case 4: fprintf(g_shim.logFile, "(TriMesh)\n"); break;
        default: fprintf(g_shim.logFile, "(Unknown)\n"); break;
    }
    
    fprintf(g_shim.logFile, "Flags:\n");
    fprintf(g_shim.logFile, "  isRigid: %d\n", obj->isRigid);
    fprintf(g_shim.logFile, "  hasLimits: %d\n", obj->hasLimits);
    fprintf(g_shim.logFile, "  collisionEnabled: %d\n", obj->collisionEnabled);
    fprintf(g_shim.logFile, "  gravityCenter: %d\n", obj->gravityCenter);
    
    fprintf(g_shim.logFile, "Physics:\n");
    fprintf(g_shim.logFile, "  body: 0x%p\n", obj->physicsBody);
    fprintf(g_shim.logFile, "  geom: 0x%p\n", obj->physicsGeom);
    fprintf(g_shim.logFile, "  joint: 0x%p\n", obj->joint);
    fprintf(g_shim.logFile, "  mass: %.6f\n", obj->mass);
    fprintf(g_shim.logFile, "  currentSpeed: %.6f\n", obj->currentSpeed);
    fprintf(g_shim.logFile, "  maxSpeed: %.6f\n", obj->maxSpeed);
    fprintf(g_shim.logFile, "  CFM: %.6f\n", obj->CFM);
    fprintf(g_shim.logFile, "  gravity_G: %.6f\n", obj->gravity_G);
    
    // Try to get position via vtable
    float x, y, z;
    CallGetPosition(obj, &x, &y, &z);
    fprintf(g_shim.logFile, "Position: (%.3f, %.3f, %.3f)\n", x, y, z);
    
    // Get speed
    float speed = CallGetSpeed(obj);
    fprintf(g_shim.logFile, "Speed: %.3f\n", speed);
    
    // Collisions
    /*fprintf(g_shim.logFile, "Collisions: %u\n", obj->collisionCount);
    if (obj->collisionCount > 0 && obj->collisionIDs) {
        fprintf(g_shim.logFile, "  Colliding with: ");
        for (uint32_t i = 0; i < obj->collisionCount && i < 10; i++) {
            fprintf(g_shim.logFile, "%u ", obj->collisionIDs[i]);
        }
        if (obj->collisionCount > 10) {
            fprintf(g_shim.logFile, "... (%u more)", obj->collisionCount - 10);
        }
        fprintf(g_shim.logFile, "\n");
    }*/
    
    // Waypoints
    fprintf(g_shim.logFile, "Waypoints: %u/%u\n", obj->waypointCount, obj->waypointCapacity);
    
   
    // Binary dump (first 0x158 bytes for base object)
    fprintf(g_shim.logFile, "\nBinary dump (first 0x158 bytes):\n");
    uint8_t* bytes = (uint8_t*)obj;
    for (int i = 0; i < 0x158; i += 16) {
        fprintf(g_shim.logFile, "%04X: ", i);
        for (int j = 0; j < 16 && i + j < 0x158; j++) {
            fprintf(g_shim.logFile, "%02X ", bytes[i + j]);
        }
        fprintf(g_shim.logFile, "\n");
    }
    
    fprintf(g_shim.logFile, "=== End Dump ===\n\n");
    fflush(g_shim.logFile);
}

void DumpAllObjects() {
    if (!g_shim.logFile) return;
    
    int count = GetObjectCount();
    fprintf(g_shim.logFile, "\n=== Dumping all objects (count: %d) ===\n", count);
    
    if (!g_shim.managerPtr) {
        fprintf(g_shim.logFile, "Manager not available\n");
        return;
    }
    
    SekaiManager* mgr = *(SekaiManager**)g_shim.managerPtr;
    if (!mgr || !mgr->objects) {
        fprintf(g_shim.logFile, "Manager or objects array not available\n");
        return;
    }
    
    for (uint32_t i = 0; i < mgr->count; i++) {
        SekaiObjectBase* obj = (SekaiObjectBase*)mgr->objects[i];
        if (!obj) continue;
        
        uint32_t id = CallGetId(obj);
        DumpObject(id);
    }
    
    fflush(g_shim.logFile);
}

// DLL Main
BOOL APIENTRY DllMain(HMODULE hModule, DWORD ul_reason_for_call, LPVOID lpReserved) {
    switch (ul_reason_for_call) {
    case DLL_PROCESS_ATTACH:
        InitShim();
        break;
    case DLL_PROCESS_DETACH:
        if (g_shim.logFile) {
            fclose(g_shim.logFile);
        }
        if (sock != INVALID_SOCKET) { closesocket(sock); sock = INVALID_SOCKET; }
        WSACleanup();
        break;
    }
    return TRUE;
}

long __thiscall ISekai::MoveObjects(float* outDt) {
    // Get original function
    typedef long (__thiscall *OrigFunc)(ISekai*, float*);
    static OrigFunc origFunc = nullptr;

    LogCall("MoveObjects", "Called MoveObjects");
    
    if (!origFunc) {
        origFunc = (OrigFunc)GetProcAddress(g_shim.originalDll, "?MoveObjects@ISekai@@QAEJPAM@Z");
        if (!origFunc) {
            LogCall("MoveObjects", "ERROR: Could not find original function");
            return -1;
        }
    }
    
    // Call original
    long result = origFunc(this, outDt);
    
    LogCall("MoveObjects", "dt = %.6f", *outDt);
    
    // Dump objects if enabled
    if (g_shim.dumpEnabled) {
        DumpAllObjects();
        g_shim.dumpEnabled = false;
    }
    
    return result;
}

// Helper function to trigger object dump
extern "C" __declspec(dllexport) void TriggerObjectDump() {
    g_shim.dumpEnabled = true;
}

// Helper to dump specific object
extern "C" __declspec(dllexport) void DumpObjectById(uint32_t id) {
    DumpObject(id);
}

// Helper to get object count
extern "C" __declspec(dllexport) int GetSekaiObjectCount() {
    return GetObjectCount();
}
