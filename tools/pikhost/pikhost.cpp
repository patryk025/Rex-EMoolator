// pikhost - drives the script interpreter of an original PIKLIB/BlooMoo DLL.
//
// The DLL is loaded into this 32-bit process and bootstrapped just far enough
// for its own CNV loader and interpreter to work: no window, no renderer, no
// message pump. Everything else (object creation, method dispatch, type
// conversion) is done by the original engine code.
//
// Protocol: one command per stdin line, replies on stdout, one line each.
// Text is passed through untouched as bytes, so the caller encodes it in the
// engine's code page (cp1250). Values in replies escape \ \n \r \t.
//
//   LOAD <path>     load a CNV file into the test scene    -> OK | ERR <why>
//   RUN <line>      run one script line ("OBJ^METHOD(...)") -> RES <type>\t<value> | RES -
//   GET <name>      read an object                         -> VAL <type>\t<value> | VAL -
//                   "I|V" reads V of the class instance I (7.2+), its own
//                   objects only; segments nest ("I|J|V")
//   QUIT
//
// Asynchronous replies: "MSG <text>" for every message box the engine tries
// to show. A crash inside the engine is reported as "CRASH <code> <rva>" and
// ends the process, because the engine state cannot be trusted afterwards;
// with PIKHOST_HANG=<seconds>, so is a command that runs too long ("HANG <rva>").
//
// Environment: PIKHOST_TRACE=1 bootstrap steps on stderr, PIKHOST_HANG as
// above, PIKHOST_CD=<dir> the CD path a leading $ in file names stands for
// (7.2 and later; by default empty, so $ paths are relative to the working
// directory, e.g. a CLASS DEF is read from common\classes there).
#define WIN32_LEAN_AND_MEAN
#include <windows.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

// CXString changes size between engine generations and is passed by value, so
// its layout is measured at startup (measureString) instead of being declared:
//   PIKLIB 6.1         32 B  CXObject base, char * at +0x18
//   PIKLIB 7.1         12 B  vftable, length, char *
//   PIKLIB 7.2+, BM    16 B  vftable, length, capacity, char *
// A by-value string is always built by the engine's own constructor and then
// destroyed by the callee; strings the host receives it destroys itself.
template <int Size> struct XStr { DWORD words[Size / 4]; };
enum { MaxStringSize = 64 };
static int g_stringSize;     // sizeof(CXString) in this engine
static int g_stringText;     // offset of its char *

static HMODULE g_lib;
static void *(__cdecl *g_new)(size_t);
static void *g_scene;
static bool g_legacy;        // PIKLIB 6.x/7.1: CMC_Scene based, 3-argument runLine
static int g_toStringSlot = -1;
static int g_initSlot = -1;

// ---------------------------------------------------------------- symbols

// Finds an export named "<prefix>@..." (PIKLIB) or "<prefix>@BlooMooDLL@@..."
// (BlooMoo keeps the same names inside a namespace) whose remainder contains
// both fragments. The mangled tails differ between versions, the prefixes do not.
static FARPROC find(const char *prefix, const char *need1 = 0, const char *need2 = 0)
{
    BYTE *base = (BYTE *)g_lib;
    IMAGE_NT_HEADERS *nt = (IMAGE_NT_HEADERS *)(base + ((IMAGE_DOS_HEADER *)base)->e_lfanew);
    IMAGE_DATA_DIRECTORY dir = nt->OptionalHeader.DataDirectory[IMAGE_DIRECTORY_ENTRY_EXPORT];
    IMAGE_EXPORT_DIRECTORY *ex = (IMAGE_EXPORT_DIRECTORY *)(base + dir.VirtualAddress);
    DWORD *names = (DWORD *)(base + ex->AddressOfNames);
    WORD *ordinals = (WORD *)(base + ex->AddressOfNameOrdinals);
    DWORD *functions = (DWORD *)(base + ex->AddressOfFunctions);
    size_t length = strlen(prefix);
    for (DWORD i = 0; i < ex->NumberOfNames; ++i) {
        const char *name = (const char *)(base + names[i]);
        if (strncmp(name, prefix, length) != 0) continue;
        const char *rest = name + length;
        if (rest[0] != '@' && strncmp(rest, "BlooMooDLL@@", 12) != 0) continue;
        if (need1 && !strstr(rest, need1)) continue;
        if (need2 && !strstr(rest, need2)) continue;
        return (FARPROC)(base + functions[ordinals[i]]);
    }
    return 0;
}

static FARPROC require(const char *prefix, const char *need1 = 0, const char *need2 = 0)
{
    FARPROC f = find(prefix, need1, need2);
    if (!f) {
        printf("ERR missing export %s [%s] [%s]\n", prefix, need1 ? need1 : "", need2 ? need2 : "");
        fflush(stdout);
        ExitProcess(4);
    }
    return f;
}

// __thiscall is called as __fastcall(this, unused edx, ...): same register for
// `this`, same callee-cleaned stack.
typedef void *(__fastcall *CtorCStr)(void *, void *, const char *);
typedef void (__fastcall *Dtor)(void *, void *);

static void constructString(void *target, const char *s)
{
    static CtorCStr ctor = (CtorCStr)require("??0CXString", "QAE@PAD@Z");
    ctor(target, 0, s);
}

static void destroyString(void *target)
{
    static Dtor dtor = (Dtor)require("??1CXString");
    dtor(target, 0);
}

template <int Size> static XStr<Size> str(const char *s)
{
    XStr<Size> x = {};
    constructString(&x, s);
    return x;
}

static const char *stringText(const void *string)
{
    return *(const char *const *)((const BYTE *)string + g_stringText);
}

// Builds a string over a sentinel-filled buffer: the last overwritten word ends
// the object, and the word pointing at a copy of the text is its char *.
static bool measureString()
{
    static const char probe[] = "pikhost-probe";
    DWORD buffer[MaxStringSize / 4];
    for (int i = 0; i < MaxStringSize / 4; ++i) buffer[i] = 0xA5A5A5A5;
    constructString(buffer, probe);
    int last = -1;
    g_stringText = -1;
    for (int i = 0; i < MaxStringSize / 4; ++i) {
        if (buffer[i] != 0xA5A5A5A5) last = i;
        const char *text = (const char *)buffer[i];
        if (text != probe && !IsBadReadPtr(text, sizeof probe) && memcmp(text, probe, sizeof probe) == 0)
            g_stringText = i * 4;
    }
    destroyString(buffer);
    g_stringSize = (last + 1) * 4;
    return g_stringText >= 0 && g_stringText < g_stringSize;
}

// Calls fn<sizeof(CXString)> args for the sizes seen so far.
#define BY_STRING_SIZE(fn, args)                                                          \
    switch (g_stringSize) {                                                               \
    case 12: fn<12> args; break;                                                          \
    case 16: fn<16> args; break;                                                          \
    case 32: fn<32> args; break;                                                          \
    default: printf("ERR unsupported CXString size %d\n", g_stringSize); ExitProcess(4); \
    }

// Objects the engine may later delete must come from the engine's own heap.
static void *alloc(size_t n)
{
    void *p = g_new(n);
    memset(p, 0, n);
    return p;
}

// ---------------------------------------------------------------- output

static void putEscaped(const char *s)
{
    for (; s && *s; ++s) {
        switch (*s) {
        case '\\': fputs("\\\\", stdout); break;
        case '\n': fputs("\\n", stdout); break;
        case '\r': fputs("\\r", stdout); break;
        case '\t': fputs("\\t", stdout); break;
        default: fputc(*s, stdout);
        }
    }
}

static int WINAPI captureMessageBoxA(HWND, LPCSTR text, LPCSTR caption, UINT)
{
    fputs("MSG ", stdout);
    putEscaped(caption);
    fputs(": ", stdout);
    putEscaped(text);
    fputc('\n', stdout);
    fflush(stdout);
    return IDOK;
}

// Points every import of `original` in the DLL at `replacement`.
static void hookImport(FARPROC original, void *replacement)
{
    BYTE *base = (BYTE *)g_lib;
    IMAGE_NT_HEADERS *nt = (IMAGE_NT_HEADERS *)(base + ((IMAGE_DOS_HEADER *)base)->e_lfanew);
    IMAGE_DATA_DIRECTORY dir = nt->OptionalHeader.DataDirectory[IMAGE_DIRECTORY_ENTRY_IMPORT];
    for (IMAGE_IMPORT_DESCRIPTOR *d = (IMAGE_IMPORT_DESCRIPTOR *)(base + dir.VirtualAddress); d->Name; ++d) {
        for (IMAGE_THUNK_DATA *t = (IMAGE_THUNK_DATA *)(base + d->FirstThunk); t->u1.Function; ++t) {
            if ((FARPROC)t->u1.Function != original) continue;
            DWORD old;
            VirtualProtect(&t->u1.Function, sizeof(DWORD), PAGE_READWRITE, &old);
            t->u1.Function = (DWORD)replacement;
            VirtualProtect(&t->u1.Function, sizeof(DWORD), old, &old);
        }
    }
}

// The engines' double frees went unnoticed on the heaps they were built for, while
// today's heap ends the process. A case is one short process, so nothing is freed.
static void __cdecl keepBlock(void *) {}

// PIKHOST_TRACE=1 prints every bootstrap step to stderr, for bringing up new engines.
static bool g_trace;

static void trace(const char *step, const void *detail = 0)
{
    if (!g_trace) return;
    fprintf(stderr, "pikhost: %s %p\n", step, detail);
    fflush(stderr);
}

// " <rva> [<rva>,...]": an instruction address, then stack words that point
// into the engine image - mostly return addresses, i.e. a rough call chain that
// can be resolved against the Ghidra listing. Addresses outside the image are
// printed as absolute values. Writes into `out` only, no heap and no locks, so
// the watchdog can use it while the main thread is stopped anywhere.
static void formatLocation(char *out, size_t size, DWORD ip, DWORD *stack)
{
    BYTE *base = (BYTE *)g_lib;
    IMAGE_NT_HEADERS *nt = (IMAGE_NT_HEADERS *)(base + ((IMAGE_DOS_HEADER *)base)->e_lfanew);
    DWORD start = (DWORD)base, end = start + nt->OptionalHeader.SizeOfImage;
    size_t used = _snprintf(out, size, " %08lx", ip >= start && ip < end ? ip - start : ip);
    int found = 0;
    for (int i = 0; i < 512 && found < 12 && used + 10 < size; ++i) {
        if (IsBadReadPtr(stack + i, sizeof(DWORD))) break;
        DWORD word = stack[i];
        if (word > start + nt->OptionalHeader.SizeOfHeaders && word < start + nt->OptionalHeader.SizeOfCode + nt->OptionalHeader.BaseOfCode) {
            used += _snprintf(out + used, size - used, "%c%08lx", found ? ',' : ' ', word - start);
            ++found;
        }
    }
}

// "CRASH <code> <rva> [<rva>,...]"
static int reportCrash(EXCEPTION_POINTERS *e)
{
    DWORD *stack = (DWORD *)e->ContextRecord->Esp;
    if (!IsBadReadPtr(stack, 4 * sizeof(DWORD))) {
        char words[64];
        _snprintf(words, sizeof words, "esp %08lx %08lx %08lx %08lx", stack[0], stack[1], stack[2], stack[3]);
        trace(words);
    }
    char location[256];
    formatLocation(location, sizeof location, e->ContextRecord->Eip, stack);
    printf("CRASH %08lx%s\n", e->ExceptionRecord->ExceptionCode, location);
    fflush(stdout);
    return EXCEPTION_EXECUTE_HANDLER;
}

// PIKHOST_HANG=<seconds> arms a watchdog for every command. When one runs that
// long, the main thread is stopped where it is and "HANG <rva> [<rva>,...]"
// is written, the same location format as CRASH, then the process exits: the
// address of an engine loop without a debugger. The main thread may be stopped
// inside the CRT holding the stdout lock, so the line bypasses stdio.
static HANDLE g_mainThread;
static volatile LONG g_commandStart;     // GetTickCount when a command began, 0 when idle

static DWORD WINAPI watchdog(void *limit)
{
    DWORD milliseconds = (DWORD)(size_t)limit;
    for (;;) {
        Sleep(50);
        LONG start = g_commandStart;
        if (!start || GetTickCount() - (DWORD)start < milliseconds) continue;
        SuspendThread(g_mainThread);
        CONTEXT context = {};
        context.ContextFlags = CONTEXT_CONTROL;
        char line[288] = "HANG";
        if (GetThreadContext(g_mainThread, &context))
            formatLocation(line + 4, sizeof line - 6, context.Eip, (DWORD *)context.Esp);
        strcat(line, "\n");
        DWORD written;
        WriteFile(GetStdHandle(STD_OUTPUT_HANDLE), line, (DWORD)strlen(line), &written, 0);
        ExitProcess(6);
    }
}

static void startWatchdog()
{
    const char *seconds = getenv("PIKHOST_HANG");
    double limit = seconds ? atof(seconds) : 0;
    if (limit <= 0) return;
    DuplicateHandle(GetCurrentProcess(), GetCurrentThread(), GetCurrentProcess(), &g_mainThread,
                    THREAD_SUSPEND_RESUME | THREAD_GET_CONTEXT, FALSE, 0);
    CreateThread(0, 0, watchdog, (void *)(size_t)(limit * 1000), 0, 0);
}

// ---------------------------------------------------------------- engine

static void callIfPresent(const char *prefix, const char *need)
{
    FARPROC f = find(prefix, need);
    trace(prefix, f);
    if (f) ((void(__cdecl *)())f)();
}

// Prints "<type>\t<value>" for an engine object, using its virtual toString.
static void putObject(void *object)
{
    typedef int(__fastcall * GetType)(void *, void *);
    typedef void *(__cdecl * TypeToString)(void *, int);
    typedef void *(__fastcall * ToString)(void *, void *, void *);
    static GetType getType = (GetType)require("?getType@CMC_Object", "QAEHXZ");
    static TypeToString typeToString = (TypeToString)require("?typeToString@CMC_Object");

    DWORD type[MaxStringSize / 4] = {}, value[MaxStringSize / 4] = {};
    typeToString(type, getType(object, 0));
    ((ToString)((*(void ***)object)[g_toStringSlot]))(object, 0, value);
    putEscaped(stringText(type));
    fputc('\t', stdout);
    putEscaped(stringText(value));
    destroyString(type);
    destroyString(value);
}

template <int Size> static void getSceneObject(const char *name, void **object)
{
    typedef void *(__fastcall * GetObject)(void *, void *, XStr<Size>);
    *object = ((GetObject)require("?getObject@CMC_Scene", "VCXString"))(g_scene, 0, str<Size>(name));
}

static void *getObject(const char *name)
{
    if (FARPROC f = find("?getObject@CMC_ObjectsContainer", "PAD@Z"))
        return ((void *(__fastcall *)(void *, void *, const char *))f)(g_scene, 0, name);
    void *object;
    BY_STRING_SIZE(getSceneObject, (name, &object));
    return object;
}

static BYTE *skipJump(BYTE *code)
{
    return code[0] == 0xE9 ? code + 5 + *(int *)(code + 1) : code;
}

// Offset of the field an exported one-line getter returns. In every 7.2+
// engine these are "mov eax,[ecx+XX]; ret", possibly behind an
// incremental-linking jmp:
//   CMC_Object::getScene                0x38 (7.2), 0x3c (8, BlooMoo)
//   CMC_ObjectsContainer::getParent     the lookup parent
//   CMC_ObjectsContainer::getWindow     0x54 (7.2), 0x58 (8), 0x5c (BlooMoo)
static int getterOffset(const char *prefix)
{
    BYTE *code = (BYTE *)find(prefix);
    if (!code) return -1;
    code = skipJump(code);
    return code[0] == 0x8B && code[1] == 0x41 && code[3] == 0xC3 ? code[2] : -1;
}

// CPath::resolveFileName(CWindow *, CXString) expands a leading $ or & with
// the window's CD path (a CXString in CWindow) in PIKLIB 7.2 and some PIKLIB 8
// builds, and CMC_Class::getClassPath passes it "$COMMON\CLASSES\<DEF>" with
// the scene's window on every NEW. The scene has no window, and giving it one
// sends other objects into it, so only this call gets a stand-in: a zeroed
// CWindow whose CD path is empty, which leaves the path relative to the
// working directory, as the newer builds do. The function is detoured: its
// first instructions move to a trampoline, and a NULL window argument is
// swapped on the way in.
static void *g_fakeWindow;
static BYTE *g_resolveTrampoline;

static __declspec(naked) void resolveFileNameShim()
{
    __asm {
        cmp dword ptr [esp + 8], 0      // [esp+4] result, [esp+8] window
        jne pass
        mov eax, g_fakeWindow
        mov dword ptr [esp + 8], eax
    pass:
        jmp g_resolveTrampoline
    }
}

// Length of whole instructions to move, for the prologues seen in the engines:
//   6A FF 68 imm32        push -1; push handler
//   83 EC xx 53 55        sub esp,xx; push ebx; push ebp
//   51 8B 44 24 xx        push ecx; mov eax,[esp+xx]
//   53 56 8B F1 33 DB     push ebx; push esi; mov esi,ecx; xor ebx,ebx
static int prologueLength(const BYTE *code)
{
    if (code[0] == 0x6A && code[1] == 0xFF && code[2] == 0x68) return 7;
    if (code[0] == 0x83 && code[1] == 0xEC && code[3] == 0x53 && code[4] == 0x55) return 5;
    if (code[0] == 0x51 && code[1] == 0x8B && code[2] == 0x44 && code[3] == 0x24) return 5;
    if (code[0] == 0x53 && code[1] == 0x56 && code[2] == 0x8B && code[3] == 0xF1 && code[4] == 0x33 && code[5] == 0xDB) return 6;
    return 0;
}

// Sends every call of `code` to `shim`; returns the trampoline that runs the
// original (its moved prologue, then a jump back), or 0 for an unknown prologue.
static BYTE *detour(BYTE *code, void *shim)
{
    int length = prologueLength(code);
    if (!length) return 0;
    BYTE *trampoline = (BYTE *)VirtualAlloc(0, 16, MEM_COMMIT | MEM_RESERVE, PAGE_EXECUTE_READWRITE);
    memcpy(trampoline, code, length);
    trampoline[length] = 0xE9;
    *(int *)(trampoline + length + 1) = (int)(code + length) - (int)(trampoline + length + 5);
    DWORD old;
    VirtualProtect(code, 5, PAGE_EXECUTE_READWRITE, &old);
    code[0] = 0xE9;
    *(int *)(code + 1) = (int)(BYTE *)shim - (int)(code + 5);
    VirtualProtect(code, 5, old, &old);
    FlushInstructionCache(GetCurrentProcess(), code, 5);
    return trampoline;
}

// Where CWindow::getCDPath reads the CD path, the root that a leading $ or &
// in a file name stands for (so "$COMMON\CLASSES\X" is <CD>COMMON\CLASSES\X).
// Read off the getter's first load; a CXString is vftable, length, capacity,
// text, and the copy starts with the capacity:
//   8B 45 xx / 8B 43 xx / 8B 83 imm32   field of the window: 7.2 0x64,
//                                       older PIKLIB 8 0x60/0x68/0x80
//   A1 imm32                            global, capacity at imm32 (PIKLIB 8)
//   68 imm32                            global string pushed for a copy (BlooMoo)
static int g_cdPathField = -1;
static BYTE *g_cdPathGlobal;

static void locateCdPath()
{
    BYTE *code = (BYTE *)find("?getCDPath@CWindow");
    if (!code) return;
    code = skipJump(code);
    for (int i = 0; i < 24; ++i) {
        if (code[i] == 0xA1) { g_cdPathGlobal = *(BYTE **)(code + i + 1) - 8; break; }
        if (code[i] == 0x68) { g_cdPathGlobal = *(BYTE **)(code + i + 1); break; }
        if (code[i] == 0x8B && (code[i + 1] == 0x45 || code[i + 1] == 0x43)) { g_cdPathField = code[i + 2] - 8; break; }
        if (code[i] == 0x8B && code[i + 1] == 0x83) { g_cdPathField = *(int *)(code + i + 2) - 8; break; }
    }
    trace("CD path field", (void *)(size_t)g_cdPathField);
    trace("CD path global", g_cdPathGlobal);
}

// PIKHOST_CD sets the CD path, so cases can load classes and other $ files
// from a game's own folders. Unset, it is empty and $ paths stay relative to
// the working directory. The engine appends to it without a separator.
static void setCdPath(void *target)
{
    static char path[MAX_PATH + 2];
    const char *value = getenv("PIKHOST_CD");
    if (value && *value && !path[0]) {
        _snprintf(path, MAX_PATH, "%s", value);
        size_t n = strlen(path);
        if (path[n - 1] != '\\' && path[n - 1] != '/') path[n] = '\\';
    }
    constructString(target, path);
}

static void hookResolveFileName()
{
    BYTE *code = (BYTE *)find("?resolveFileName@CPath", "PAVCWindow");
    int cdPath = g_cdPathField;
    if (!code || cdPath < 0) return;
    code = skipJump(code);
    g_fakeWindow = alloc(0x1000);
    setCdPath((BYTE *)g_fakeWindow + cdPath);
    g_resolveTrampoline = detour(code, resolveFileNameShim);
    trace("resolveFileName detour", g_resolveTrampoline);
}

// ~CMC_Behaviour takes the behaviour off the timer of its scene's window:
// CXTimer::remove(scene->window->timer, this), with no NULL check. With no
// window every behaviour the engine destroys takes the process down; in a game
// that is the temporary behaviour of an inline @IF branch or @LOOP body, freed
// after some runs, e.g. one leaving through @RETURN. The scene gets a stand-in
// window for the duration of the destructor only (a permanent one would send
// other objects into it): every field of it points at a stand-in timer, every
// field of which points at a zeroed block, so whatever offsets a build uses,
// the timer's list reads as empty and remove returns.
static void *g_timerWindow;
static BYTE *g_destructorTrampoline;
static int g_sceneField = -1, g_windowField = -1;

static __declspec(naked) void behaviourDestructorShim()
{
    __asm {
        push ebx
        push esi
        mov esi, ecx                    // this
        mov eax, g_sceneField
        mov ebx, dword ptr [esi + eax]  // scene
        test ebx, ebx
        jz plain
        mov eax, g_windowField
        push dword ptr [ebx + eax]      // its window, restored below
        cmp dword ptr [ebx + eax], 0
        jne call_it
        mov edx, g_timerWindow
        mov dword ptr [ebx + eax], edx
    call_it:
        mov ecx, esi
        call dword ptr [g_destructorTrampoline]
        mov eax, g_windowField
        pop dword ptr [ebx + eax]
        pop esi
        pop ebx
        ret
    plain:
        mov ecx, esi
        pop esi
        pop ebx
        jmp dword ptr [g_destructorTrampoline]
    }
}

static void hookBehaviourDestructor()
{
    BYTE *code = (BYTE *)find("??1CMC_Behaviour");
    g_sceneField = getterOffset("?getScene@CMC_Object");
    g_windowField = getterOffset("?getWindow@CMC_ObjectsContainer");
    if (!code || g_sceneField < 0 || g_windowField < 0) return;
    void **zero = (void **)alloc(0x400), **timer = (void **)alloc(0x400), **window = (void **)alloc(0x1000);
    for (int i = 0; i < 0x100; ++i) timer[i] = zero;
    for (int i = 0; i < 0x400; ++i) window[i] = timer;
    g_timerWindow = window;
    g_destructorTrampoline = detour(skipJump(code), behaviourDestructorShim);
    trace("~CMC_Behaviour detour", g_destructorTrampoline);
}

// The scene gets an empty application container above it, as in a game.
// @RETURN stores __RETURN__ with the owner of the container the line runs in,
// which for a top-level RUN is the scene's owner; the caller then looks it up
// in the scene, and finds it only through the scene's lookup parent.
//
// A game scene is its own scene: the CMC_Scene constructor ends with getScene
// = this. CMC_ObjectsContainer::resolve builds every CLASS with getScene() of
// the container, and an instance looks names up in that. The real constructor
// cannot run here (its default CUnit6 reads the window), so the field is set
// by hand; left as the application, instances would see nothing of the scene.
template <int Size> static void createContainer(FARPROC ctor)
{
    typedef void *(__fastcall * Ctor)(void *, void *, void *, void *, XStr<Size>, int);
    void *application = alloc(0x1000);
    ((Ctor)ctor)(application, 0, 0, 0, str<Size>("PIKHOST_APPLICATION"), 0);
    ((Ctor)ctor)(g_scene, 0, 0, application, str<Size>("PIKHOST"), 0);
    if (FARPROC setParent = find("?setParent@CMC_ObjectsContainer", "QAEXPAV1"))
        ((void(__fastcall *)(void *, void *, void *))setParent)(g_scene, 0, application);
    int scene = getterOffset("?getScene@CMC_Object");
    trace("scene field", (void *)(size_t)scene);
    if (scene > 0) *(void **)((BYTE *)g_scene + scene) = g_scene;
}

template <int Size> static void createElement(void *element)
{
    typedef void *(__fastcall * Ctor)(void *, void *, XStr<Size>);
    ((Ctor)require("??0CMElement", "QAE@VCXString"))(element, 0, str<Size>("PIKHOST"));
}

// The legacy scene gets its object tables in CMC_Scene::initialize, together
// with units, cursors and music that all need a real window. Only the two
// tables are created here. Their offsets are per generation, and the CXString
// size tells the generations apart.
struct LegacySceneLayout { int stringSize; int objectList; int nameTable; };
static const LegacySceneLayout g_legacyLayouts[] = {
    { 12, 0xa8, 0xac },      // PIKLIB 7.1
    { 32, 0x158, 0x15c },    // PIKLIB 6.1
};

static void createLegacySceneTables()
{
    const LegacySceneLayout *layout = 0;
    for (size_t i = 0; i < sizeof g_legacyLayouts / sizeof g_legacyLayouts[0]; ++i)
        if (g_legacyLayouts[i].stringSize == g_stringSize) layout = &g_legacyLayouts[i];
    if (!layout) {
        trace("no legacy scene layout for this CXString size");
        return;
    }
    BYTE *scene = (BYTE *)g_scene;
    void *names = alloc(0x100), *objects = alloc(0x100);
    ((void *(__fastcall *)(void *, void *, int, double, bool))require("??0CHashtable", "QAE@HN_N@Z"))(names, 0, 10001, 0.75, false);
    ((void *(__fastcall *)(void *, void *, int, float))require("??0CXVector", "QAE@HM@Z"))(objects, 0, 1001, 0.75f);
    *(void **)(scene + layout->nameTable) = names;
    *(void **)(scene + layout->objectList) = objects;
    trace("legacy name table", names);
}

static void bootstrap()
{
    if (!measureString()) {
        puts("ERR cannot measure CXString");
        ExitProcess(4);
    }
    trace("CXString size", (void *)(size_t)g_stringSize);
    trace("CXString text offset", (void *)(size_t)g_stringText);

    // toString is virtual; its slot is found by matching the exported
    // implementation against the exported CMC_Integer vtable.
    void **vtable = (void **)require("??_7CMC_Integer", "6B@");
    void *toString = (void *)require("?toString@CMC_Integer");
    for (int i = 0; i < 256 && g_toStringSlot < 0; ++i)
        if (vtable[i] == toString) g_toStringSlot = i;
    trace("toString slot", (void *)(size_t)g_toStringSlot);
    if (FARPROC init = find("?init@CMC_Condition", "UAEXXZ")) {
        void **conditionVtable = (void **)require("??_7CMC_Condition", "6B@");
        for (int i = 0; i < 256 && g_initSlot < 0; ++i)
            if (conditionVtable[i] == (void *)init) g_initSlot = i;
        trace("init slot", (void *)(size_t)g_initSlot);
    }

    // Global singletons the application normally creates at startup.
    callIfPresent("?initialize@CMC_RunnerFactory", "SAXXZ");
    if (FARPROC f = find("?initialize@CMC_VariableFactory", "SAXH@Z")) {
        trace("?initialize@CMC_VariableFactory(int)", f);
        ((void(__cdecl *)(int))f)(1000);
    }
    callIfPresent("?initialize@CMC_VariableFactory", "SAXXZ");
    callIfPresent("?initTrash@CMC_Behaviour", "SAXXZ");

    g_scene = alloc(0x1000);
    if (FARPROC f = find("??0CMC_ObjectsContainer", "VCXString")) {
        BY_STRING_SIZE(createContainer, (f));
        locateCdPath();
        hookResolveFileName();
        hookBehaviourDestructor();
        if (g_cdPathGlobal && getenv("PIKHOST_CD")) setCdPath(g_cdPathGlobal);
    } else {
        // The older scene reads its name and properties from a definition element.
        g_legacy = true;
        void *element = alloc(0x400);
        trace("legacy scene", g_scene);
        BY_STRING_SIZE(createElement, (element));
        ((void *(__fastcall *)(void *, void *, void *, void *, void *, int))require("??0CMC_Scene", "PAVCMElement"))(g_scene, 0, 0, 0, element, 0);
        createLegacySceneTables();
    }
    trace("scene ready", g_scene);
}

// From 7.2 on, objects finish construction in the virtual init, which a scene
// calls after resolveObjects (CMC_Condition, for one, builds its operand entries
// there). Only the objects that were there before the first init: a behaviour's
// init turns every inline {...} block into a behaviour of its own, already
// initialised, and a second init of those takes the engine down. CXVector keeps
// the count at +0x1c in every version; where the items live differs, so they
// are read through CXVector::at.
static void *containerObjects()
{
    return ((void *(__fastcall *)(void *, void *))require("?getObjects@CMC_ObjectsContainer", "QAEPAVCXVector"))(g_scene, 0);
}

static int objectCount()
{
    BYTE *objects = (BYTE *)containerObjects();
    return objects ? *(int *)(objects + 0x1c) : 0;
}

static void initObjects(int from)
{
    BYTE *objects = (BYTE *)containerObjects();
    if (!objects || g_initSlot < 0) return;
    typedef void *(__fastcall * At)(void *, void *, int);
    At at = (At)require("?at@CXVector", "QAEPAVCXObject");
    int count = *(int *)(objects + 0x1c);
    for (int i = from; i < count; ++i)
        if (void *object = at(objects, 0, i))
            ((void(__fastcall *)(void *, void *))((*(void ***)object)[g_initSlot]))(object, 0);
}

// Loads the file the way a scene loads its own: parse it into definition
// elements and let resolveObjects build them in passes by type (behaviours,
// then variables, conditions and expressions, graphics, input, groups ...),
// registering each object and, from 7.2 on, binding every BEHAVIOUR to its
// CONDITION afterwards.
//
// CMC_CNVLoader::load is not used: it resolves definitions one by one and never
// binds conditions, so RUNC there runs unconditionally. In PIKLIB 6.1 it is
// broken altogether (a CMC_Groupped built from a NULL definition element, and
// CMC_Scene::resolve turning every definition into a CMC_Behaviour).
//
// No window: objects would otherwise call into it. A few PIKLIB 8 builds (Robin
// Hood, Ksiega Dzungli, Angielski) read the window's language settings in
// CFile::Open and crash here instead.
template <int Size> static void loadElements(const char *path)
{
    typedef void *(__cdecl * LoadElements)(void *, XStr<Size>);
    typedef void *(__fastcall * ResolveObjects)(void *, void *, void *);
    void *elements = ((LoadElements)require("?load@CMElementsLoader", "PAVCWindow", "VCXString"))(0, str<Size>(path));
    trace("elements", elements);
    if (!elements) return;
    if (g_legacy) {
        ((ResolveObjects)require("?resolveObjects@CMC_Scene", "PAVCMElements"))(g_scene, 0, elements);
        return;
    }
    int before = objectCount();
    ((ResolveObjects)require("?resolveObjects@CMC_ObjectsContainer", "PAVCMElements"))(g_scene, 0, elements);
    initObjects(before);
}

static void commandLoad(const char *path)
{
    BY_STRING_SIZE(loadElements, (path));
    puts("OK");
}

template <int Size> static void runLine(const char *line, void **result)
{
    FARPROC run = require("?runLine@CMC_Behaviour", "SAPAVCMC_Variable");
    if (!g_legacy)
        *result = ((void *(__cdecl *)(void *, void *, XStr<Size>, void *))run)(g_scene, 0, str<Size>(line), 0);
    else
        *result = ((void *(__cdecl *)(void *, void *, XStr<Size>))run)(g_scene, 0, str<Size>(line));
}

static void commandRun(const char *line)
{
    void *result;
    BY_STRING_SIZE(runLine, (line, &result));
    fputs("RES ", stdout);
    if (result) putObject(result); else fputc('-', stdout);
    fputc('\n', stdout);
}

enum { ClassType = 3004 };   // CMC_Class: a CLASS and every instance of it

// "I|V": V among the instance's own objects. The container lookup would go on
// to its parent (the scene) for a name it lacks, so that link is cut for the
// call: a field the instance does not have reads as missing, not as the
// scene's object of the same name.
static void *getMember(void *container, const char *name)
{
    typedef int(__fastcall * GetType)(void *, void *);
    typedef void *(__fastcall * GetObject)(void *, void *, const char *);
    static GetType getType = (GetType)require("?getType@CMC_Object", "QAEHXZ");
    static int parent = getterOffset("?getParent@CMC_ObjectsContainer");
    FARPROC lookup = find("?getObject@CMC_ObjectsContainer", "PAD@Z");
    if (!container || !lookup || parent < 0 || getType(container, 0) != ClassType) return 0;
    void **link = (void **)((BYTE *)container + parent);
    void *saved = *link;
    *link = 0;
    void *object = ((GetObject)lookup)(container, 0, name);
    *link = saved;
    return object;
}

static void commandGet(char *name)
{
    char *next = strchr(name, '|');
    if (next) *next++ = 0;
    void *object = getObject(name);
    while (object && next) {
        char *part = next;
        next = strchr(part, '|');
        if (next) *next++ = 0;
        object = getMember(object, part);
    }
    fputs("VAL ", stdout);
    if (object) putObject(object); else fputc('-', stdout);
    fputc('\n', stdout);
}

static bool dispatch(char *line)
{
    g_commandStart = GetTickCount() | 1;
    __try {
        if (strncmp(line, "LOAD ", 5) == 0) commandLoad(line + 5);
        else if (strncmp(line, "RUN ", 4) == 0) commandRun(line + 4);
        else if (strncmp(line, "GET ", 4) == 0) commandGet(line + 4);
        else if (strcmp(line, "QUIT") == 0) return false;
        else printf("ERR unknown command\n");
    } __except (reportCrash(GetExceptionInformation())) {
        ExitProcess(5);
    }
    fflush(stdout);
    g_commandStart = 0;
    return true;
}

int wmain(int argc, wchar_t **argv)
{
    if (argc != 2) {
        fputs("usage: pikhost <engine.dll>\n", stderr);
        return 2;
    }
    SetErrorMode(SEM_FAILCRITICALERRORS | SEM_NOGPFAULTERRORBOX | SEM_NOOPENFILEERRORBOX);
    g_lib = LoadLibraryW(argv[1]);
    if (!g_lib) {
        printf("ERR LoadLibrary failed: %lu\n", GetLastError());
        return 3;
    }
    HMODULE crt = GetModuleHandleA("MSVCR80.dll");      // BlooMoo
    if (!crt) crt = GetModuleHandleA("MSVCRT.dll");     // PIKLIB
    g_new = crt ? (void *(__cdecl *)(size_t))GetProcAddress(crt, "??2@YAPAXI@Z") : 0;
    if (!g_new) {
        puts("ERR engine CRT operator new not found");
        return 3;
    }
    // So a script error cannot block the run.
    hookImport(GetProcAddress(GetModuleHandleA("user32.dll"), "MessageBoxA"), captureMessageBoxA);
    hookImport(GetProcAddress(crt, "free"), keepBlock);
    hookImport(GetProcAddress(crt, "??3@YAXPAX@Z"), keepBlock);
    g_trace = getenv("PIKHOST_TRACE") != 0;
    startWatchdog();

    g_commandStart = GetTickCount() | 1;
    __try {
        bootstrap();
    } __except (reportCrash(GetExceptionInformation())) {
        return 5;
    }
    g_commandStart = 0;
    printf("READY %s\n", g_legacy ? "legacy" : "modern");
    fflush(stdout);

    static char line[65536];
    while (fgets(line, sizeof line, stdin)) {
        size_t n = strlen(line);
        while (n && (line[n - 1] == '\n' || line[n - 1] == '\r')) line[--n] = 0;
        if (!dispatch(line)) break;
    }
    return 0;
}
