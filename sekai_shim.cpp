// =============================================================================
// Windows & Standard Headers
// =============================================================================
#define WIN32_LEAN_AND_MEAN
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
#include <MinHook.h>

#pragma comment(lib, "ws2_32.lib")
#pragma comment(lib, "user32.lib")
#pragma comment(lib, "libMinHook.x86.lib")

extern "C" IMAGE_DOS_HEADER __ImageBase;

// =============================================================================
// Global Variables & Constants
// =============================================================================

// Global shim instance
SekaiShim g_shim = {0};

// Known address of the manager in original DLL
#define MANAGER_OFFSET 0x10040624
#define WORLD_OFFSET 0x10040628
#define SPACE_OFFSET 0x1004062C
#define JOINT_GROUP_OFFSET 0x10040630

// =============================================================================
// Data Structures - UDP Protocol
// =============================================================================

struct FrameHeader { uint32_t magic, version, frameIndex; double timestamp; uint32_t objectCount; };
struct ObjRec {
    uint32_t id, geomType;
    float pos_x,pos_y,pos_z;
    float r00,r01,r02, r10,r11,r12, r20,r21,r22;
    float speed;
    uint32_t flags;
};

// =============================================================================
// Configuration Structure
// =============================================================================

struct ShimConfig {
    int  startServer = 1;
    char udpHost[64] = "127.0.0.1";
    int  udpPort = 44444;
    int  dumpEnabled = 1;
    int  overrideTime = 0;
    int  overrideFPS = 30;
    int  dumpMem = 0;
} g_cfg;

// =============================================================================
// Memory Dump Structures
// =============================================================================

#pragma pack(push, 1)
struct RegionIdx {
    uint32_t base;       // base VA
    uint32_t size;       // region size
    uint32_t protect;    // MEMORY_BASIC_INFORMATION.Protect
    uint32_t type;       // MEM_IMAGE / MEM_MAPPED / MEM_PRIVATE
    uint64_t fileOffset; // offset in .mem file where this region starts
};
#pragma pack(pop)

// =============================================================================
// Helper Functions - Memory Operations
// =============================================================================

static bool IsReadablePage(DWORD prot) {
    if (prot & PAGE_GUARD) return false;
    if (prot & PAGE_NOACCESS) return false;
    // „readable” combinations
    static const DWORD readable[] = {
        PAGE_READONLY, PAGE_READWRITE, PAGE_WRITECOPY,
        PAGE_EXECUTE_READ, PAGE_EXECUTE_READWRITE, PAGE_EXECUTE_WRITECOPY
    };
    for (DWORD p : readable) if (prot == p) return true;
    
    if ( (prot & (PAGE_READONLY|PAGE_READWRITE|PAGE_WRITECOPY|
                  PAGE_EXECUTE_READ|PAGE_EXECUTE_READWRITE|PAGE_EXECUTE_WRITECOPY)) != 0 )
        return true;
    return false;
}

static void BuildDumpPaths(char* memPath, size_t memPathSz, char* idxPath, size_t idxPathSz) {
    char dllPath[MAX_PATH]{}, dirPath[MAX_PATH]{};
    GetModuleFileNameA((HMODULE)&__ImageBase, dllPath, MAX_PATH);
    strcpy_s(dirPath, dllPath);
    if (char* lastSlash = strrchr(dirPath, '\\')) *(lastSlash+1) = '\0';
    SYSTEMTIME st{}; GetLocalTime(&st);
    // Sekai_YYYYmmdd_HHMMSS.mem / .idx
    sprintf_s(memPath, memPathSz, "%sSekai_%04u%02u%02u_%02u%02u%02u.mem",
              dirPath, st.wYear, st.wMonth, st.wDay, st.wHour, st.wMinute, st.wSecond);
    sprintf_s(idxPath, idxPathSz, "%sSekai_%04u%02u%02u_%02u%02u%02u.idx",
              dirPath, st.wYear, st.wMonth, st.wDay, st.wHour, st.wMinute, st.wSecond);
}

// =============================================================================
// Configuration Loading
// =============================================================================

static void LoadIni() {
    char dllPath[MAX_PATH]{}, iniPath[MAX_PATH]{};
    GetModuleFileNameA((HMODULE)&__ImageBase, dllPath, MAX_PATH);
    
    if (g_shim.logFile) {
        fprintf(g_shim.logFile, "[LoadIni] DLL path: %s\n", dllPath);
    }
    
    // use module name and change extension to .ini
    char* dot = strrchr(dllPath, '.');
    if (!dot) dot = dllPath + strlen(dllPath);
    strcpy_s(dot, MAX_PATH - (dot - dllPath), ".ini");
    strcpy_s(iniPath, dllPath);

    if (g_shim.logFile) {
        fprintf(g_shim.logFile, "[LoadIni] INI path: %s\n", iniPath);
    }

    GetPrivateProfileStringA("udp", "host", "127.0.0.1", g_cfg.udpHost, (DWORD)sizeof(g_cfg.udpHost), iniPath);
    g_cfg.startServer   = GetPrivateProfileIntA("udp", "startServer", 1, iniPath);
    g_cfg.udpPort   = GetPrivateProfileIntA("udp", "port", 44444, iniPath);
    g_cfg.dumpEnabled = GetPrivateProfileIntA("debug", "dumpEnabled", 1, iniPath);
    g_cfg.overrideTime = GetPrivateProfileIntA("time", "overrideTime", 1, iniPath);
    g_cfg.overrideFPS = GetPrivateProfileIntA("time", "overrideFPS", 30, iniPath);
    g_cfg.dumpMem = GetPrivateProfileIntA("dump", "dumpMem", 1, iniPath);
    
    if (g_shim.logFile) {
        fprintf(g_shim.logFile, "[LoadIni] Loaded config:\n");
        fprintf(g_shim.logFile, "  startServer: %d\n", g_cfg.startServer);
        fprintf(g_shim.logFile, "  udpHost: %s\n", g_cfg.udpHost);
        fprintf(g_shim.logFile, "  udpPort: %d\n", g_cfg.udpPort);
        fprintf(g_shim.logFile, "  dumpEnabled: %d\n", g_cfg.dumpEnabled);
        fprintf(g_shim.logFile, "  overrideTime: %d\n", g_cfg.overrideTime);
        fprintf(g_shim.logFile, "  overrideFPS: %d\n", g_cfg.overrideFPS);
        fprintf(g_shim.logFile, "  dumpMem: %d\n", g_cfg.dumpMem);
        fflush(g_shim.logFile);
    }
}

// =============================================================================
// UDP Network Functions
// =============================================================================

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

// =============================================================================
// Time Hook Functions (MinHook)
// =============================================================================

typedef float (__cdecl *GenerateDtFunc)();

GenerateDtFunc originalGenerateDt = nullptr;

float __cdecl HookedGenerateDt() {
    if (g_shim.forcedStepTime > 0.0f) {
        return g_shim.forcedStepTime;
    }
    return originalGenerateDt();
}

void InitTimeHook() {
    if (MH_Initialize() != MH_OK) {
        MessageBoxA(0, "MinHook init failed", "Error", 0);
        return;
    }

    void* base = g_shim.originalDll;
    void* target = (void*)((uintptr_t)base + 0x69b0);

    if (MH_CreateHook(target, &HookedGenerateDt, (LPVOID*)&originalGenerateDt) != MH_OK) {
        LogCall("Init", "Failed to create hook for FUN_100069b0");
        return;
    }

    if (MH_EnableHook(target) != MH_OK) {
        LogCall("Init", "Failed to enable hook");
        return;
    }

    LogCall("Init", "Hooked FUN_100069b0 at 0x%p", target);
}

// =============================================================================
// Initialization Functions
// =============================================================================

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
    
    fprintf(g_shim.logFile, "Loading config...\n");
    fflush(g_shim.logFile);
    LoadIni();
    if(g_cfg.startServer) {
        fprintf(g_shim.logFile, "Starting UDP server on %s:%d\n", g_cfg.udpHost, g_cfg.udpPort);
        fflush(g_shim.logFile);
        InitUDP(g_cfg.udpHost, g_cfg.udpPort);
    }

    if(g_cfg.overrideTime) {
        InitTimeHook();
    }
    
    g_shim.dumpEnabled = g_cfg.dumpEnabled;
}

// =============================================================================
// Logging & Debugging Functions
// =============================================================================

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

// =============================================================================
// Object Query Functions
// =============================================================================

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

// =============================================================================
// Memory Dump Functions
// =============================================================================

void DumpAllMemory() {
    char memPath[MAX_PATH]{}, idxPath[MAX_PATH]{};
    BuildDumpPaths(memPath, sizeof(memPath), idxPath, sizeof(idxPath));

    FILE* fMem = fopen(memPath, "wb");
    if (!fMem) { LogCall("DumpAllMemory", "Cannot open %s", memPath); return; }

    std::vector<RegionIdx> index;
    index.reserve(4096);

    SYSTEM_INFO si{};
    GetSystemInfo(&si);
    uintptr_t p = (uintptr_t)si.lpMinimumApplicationAddress;
    uintptr_t max = (uintptr_t)si.lpMaximumApplicationAddress;

    HANDLE self = GetCurrentProcess();
    SIZE_T totalWritten = 0;

    while (p < max) {
        MEMORY_BASIC_INFORMATION mbi{};
        SIZE_T got = VirtualQuery((LPCVOID)p, &mbi, sizeof(mbi));
        if (!got) break;

        if (mbi.State == MEM_COMMIT && IsReadablePage(mbi.Protect)) {
            RegionIdx ri{};
            ri.base = (uint32_t)(uintptr_t)mbi.BaseAddress;
            ri.size = (uint32_t)mbi.RegionSize;
            ri.protect = mbi.Protect;
            ri.type = mbi.Type;
            ri.fileOffset = (uint64_t)_ftelli64(fMem);

            // read region by chunks of 1 MiB
            const size_t CHUNK = 1 << 20; // 1 MiB
            std::vector<uint8_t> buf; buf.resize((std::min)((size_t)ri.size, CHUNK));

            uintptr_t cur = (uintptr_t)mbi.BaseAddress;
            SIZE_T left = mbi.RegionSize;
            while (left > 0) {
                SIZE_T toRead = (SIZE_T)std::min<size_t>(left, buf.size());
                SIZE_T read = 0;
                if (!ReadProcessMemory(self, (LPCVOID)cur, buf.data(), toRead, &read)) {
                    if (read > 0) fwrite(buf.data(), 1, read, fMem);
                    break;
                }
                fwrite(buf.data(), 1, read, fMem);
                totalWritten += (SIZE_T)read;
                cur += read;
                left -= read;
                if (read < toRead) break;
            }

            ri.size = (uint32_t)((uint64_t)_ftelli64(fMem) - ri.fileOffset);
            if (ri.size > 0) index.push_back(ri);
        }

        p = (uintptr_t)mbi.BaseAddress + mbi.RegionSize;
    }

    fclose(fMem);

    FILE* fIdx = fopen(idxPath, "wb");
    if (!fIdx) { LogCall("DumpAllMemory", "Cannot open %s", idxPath); return; }

    struct {
        uint32_t magic;   // 'M''E''M''X' -> 0x584D454D
        uint32_t version; // 1
        uint32_t count;   // number of regions
        uint32_t reserved;
    } hdr{ 0x584D454D, 1, (uint32_t)index.size(), 0 };

    fwrite(&hdr, sizeof(hdr), 1, fIdx);
    if (!index.empty()) fwrite(index.data(), sizeof(RegionIdx), index.size(), fIdx);
    fclose(fIdx);

    LogCall("DumpAllMemory", "DONE: wrote %zu regions, total ~%zu bytes -> %s (+ %s)",
            index.size(), (size_t)totalWritten, memPath, idxPath);
}

// =============================================================================
// Object Dump Functions
// =============================================================================

void DumpObjectToBin(SekaiObjectBase* obj, uint32_t objectId) {
    if (!obj) return;
    
    char dllPath[MAX_PATH]{}, dirPath[MAX_PATH]{};
    GetModuleFileNameA((HMODULE)&__ImageBase, dllPath, MAX_PATH);
    
    // Get directory path
    strcpy_s(dirPath, dllPath);
    char* lastSlash = strrchr(dirPath, '\\');
    if (lastSlash) {
        *(lastSlash + 1) = '\0';
    }
    
    time_t t = time(NULL);
    char filename[MAX_PATH];
    sprintf_s(filename, sizeof(filename), "%sobj_dump_%u_%lu.bin", dirPath, objectId, (unsigned long)t);
    
    FILE* file = fopen(filename, "wb");
    if (!file) {
        fprintf(g_shim.logFile, "Failed to open file %s\n", filename);
        return;
    }

    uint8_t* bytes = (uint8_t*)obj;
    fwrite(bytes, 0x158, 1, file);
    fclose(file);

    fprintf(g_shim.logFile, "Object %u dumped to %s\n", objectId, filename);
}

void DumpObject(uint32_t objectId) {
    if (!g_shim.logFile) return;
    
    SekaiObjectBase* obj = GetObjectById(objectId);
    if (!obj) {
        fprintf(g_shim.logFile, "Object %u not found\n", objectId);
        return;
    }
    
    fprintf(g_shim.logFile, "\n=== Object %u Dump ===\n", objectId);
    fprintf(g_shim.logFile, "Object address: 0x%p\n", obj);
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
    fprintf(g_shim.logFile, "Dumping to .bin file...\n");
    DumpObjectToBin(obj, objectId);
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

// =============================================================================
// DLL Entry Point
// =============================================================================

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

// =============================================================================
// ISekai Method Implementations
// =============================================================================

// Suppress C4273: inconsistent dll linkage warning for MoveObjects
// This is intentional - we need non-virtual __thiscall to match original DLL name mangling
#pragma warning(push)
#pragma warning(disable: 4273)

long __thiscall ISekai::Create(const char* path) {
    // Get original function
    typedef long (__thiscall *OrigFunc)(ISekai*, const char*);
    static OrigFunc origFunc = nullptr;

    LogCall("MoveObjects", "Called Create(%s)", path);
    
    if (!origFunc) {
        origFunc = (OrigFunc)GetProcAddress(g_shim.originalDll, "?Create@ISekai@@QAEJPBD@Z");
        if (!origFunc) {
            LogCall("MoveObjects", "ERROR: Could not find original function");
            return -1;
        }
    }
    
    // Call original
    long result = origFunc(this, path);

    // TODO: Call ISekai::AddBody with fixed parameters (for reconstructing objects templates)
    
    return result;
}

long __thiscall ISekai::Load(const char* path) {
    // Get original function
    typedef long (__thiscall *OrigFunc)(ISekai*, const char*);
    static OrigFunc origFunc = nullptr;

    LogCall("MoveObjects", "Called Load(%s)", path);
    
    if (!origFunc) {
        origFunc = (OrigFunc)GetProcAddress(g_shim.originalDll, "?Load@ISekai@@QAEJPBD@Z");
        if (!origFunc) {
            LogCall("MoveObjects", "ERROR: Could not find original function");
            return -1;
        }
    }
    
    // Call original
    long result = origFunc(this, path);

    // TODO: Call ISekai::AddBody with fixed parameters (for reconstructing objects templates)
    
    return result;
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

    // Dump objects before physics tick
    if (g_shim.dumpEnabled) {
        if (g_cfg.dumpMem) DumpAllMemory();
    }
    
    // Call original
    long result = origFunc(this, outDt);
    
    LogCall("MoveObjects", "dt = %.6f", *outDt);
    if(g_cfg.overrideTime) {
        LogCall("MoveObjects", "Override time: %.6f", g_cfg.overrideTime);
    }
    
    // Dump objects if enabled
    if (g_shim.dumpEnabled) {
        if (g_cfg.dumpMem) DumpAllMemory();
        DumpAllObjects();
        g_shim.dumpEnabled = false;
    }
    
    return result;
}

#pragma warning(pop)

// =============================================================================
// Exported Helper Functions
// =============================================================================

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
