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
//   QUIT
//
// Asynchronous replies: "MSG <text>" for every message box the engine tries
// to show. A crash inside the engine is reported as "CRASH <code> <rva>" and
// ends the process, because the engine state cannot be trusted afterwards.
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

// Redirects the DLL's MessageBoxA import so a script error cannot block the run.
static void hookMessageBox()
{
    BYTE *base = (BYTE *)g_lib;
    IMAGE_NT_HEADERS *nt = (IMAGE_NT_HEADERS *)(base + ((IMAGE_DOS_HEADER *)base)->e_lfanew);
    IMAGE_DATA_DIRECTORY dir = nt->OptionalHeader.DataDirectory[IMAGE_DIRECTORY_ENTRY_IMPORT];
    FARPROC original = GetProcAddress(GetModuleHandleA("user32.dll"), "MessageBoxA");
    for (IMAGE_IMPORT_DESCRIPTOR *d = (IMAGE_IMPORT_DESCRIPTOR *)(base + dir.VirtualAddress); d->Name; ++d) {
        if (_stricmp((const char *)(base + d->Name), "user32.dll") != 0) continue;
        for (IMAGE_THUNK_DATA *t = (IMAGE_THUNK_DATA *)(base + d->FirstThunk); t->u1.Function; ++t) {
            if ((FARPROC)t->u1.Function != original) continue;
            DWORD old;
            VirtualProtect(&t->u1.Function, sizeof(DWORD), PAGE_READWRITE, &old);
            t->u1.Function = (DWORD)(void *)captureMessageBoxA;
            VirtualProtect(&t->u1.Function, sizeof(DWORD), old, &old);
        }
    }
}

// PIKHOST_TRACE=1 prints every bootstrap step to stderr, for bringing up new engines.
static bool g_trace;

static void trace(const char *step, const void *detail = 0)
{
    if (!g_trace) return;
    fprintf(stderr, "pikhost: %s %p\n", step, detail);
    fflush(stderr);
}

// "CRASH <code> <rva> [<rva>,...]": the faulting address, then stack words that
// point into the engine image - mostly return addresses, i.e. a rough call chain
// that can be resolved against the Ghidra listing. Addresses outside the image
// are printed as absolute values.
static int reportCrash(EXCEPTION_POINTERS *e)
{
    BYTE *base = (BYTE *)g_lib;
    IMAGE_NT_HEADERS *nt = (IMAGE_NT_HEADERS *)(base + ((IMAGE_DOS_HEADER *)base)->e_lfanew);
    DWORD start = (DWORD)base, end = start + nt->OptionalHeader.SizeOfImage;
    DWORD ip = e->ContextRecord->Eip;
    printf("CRASH %08lx %08lx", e->ExceptionRecord->ExceptionCode, ip >= start && ip < end ? ip - start : ip);
    DWORD *stack = (DWORD *)e->ContextRecord->Esp;
    if (!IsBadReadPtr(stack, 4 * sizeof(DWORD))) {
        char words[64];
        _snprintf(words, sizeof words, "esp %08lx %08lx %08lx %08lx", stack[0], stack[1], stack[2], stack[3]);
        trace(words);
    }
    int found = 0;
    for (int i = 0; i < 512 && found < 12; ++i) {
        DWORD word;
        if (IsBadReadPtr(stack + i, sizeof word)) break;
        word = stack[i];
        if (word > start + nt->OptionalHeader.SizeOfHeaders && word < start + nt->OptionalHeader.SizeOfCode + nt->OptionalHeader.BaseOfCode) {
            printf("%c%08lx", found ? ',' : ' ', word - start);
            ++found;
        }
    }
    fputc('\n', stdout);
    fflush(stdout);
    return EXCEPTION_EXECUTE_HANDLER;
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

template <int Size> static void createContainer(FARPROC ctor)
{
    typedef void *(__fastcall * Ctor)(void *, void *, void *, void *, XStr<Size>, int);
    ((Ctor)ctor)(g_scene, 0, 0, 0, str<Size>("PIKHOST"), 0);
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
// there). CXVector keeps the count at +0x1c in every version; where the items
// live differs, so they are read through CXVector::at.
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
    for (int i = from; i < *(int *)(objects + 0x1c); ++i)
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

static void commandGet(const char *name)
{
    void *object = getObject(name);
    fputs("VAL ", stdout);
    if (object) putObject(object); else fputc('-', stdout);
    fputc('\n', stdout);
}

static bool dispatch(char *line)
{
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
    hookMessageBox();
    g_trace = getenv("PIKHOST_TRACE") != 0;

    __try {
        bootstrap();
    } __except (reportCrash(GetExceptionInformation())) {
        return 5;
    }
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
