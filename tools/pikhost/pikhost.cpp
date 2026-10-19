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
#include <string.h>

// CXString: {vftable, length, capacity, char *}. Passed by value and destroyed
// by the callee, so it is always constructed with the engine's own constructor.
struct XStr { void *vt; int len; int cap; char *p; };

static HMODULE g_lib;
static void *(__cdecl *g_new)(size_t);
static void *g_scene;
static bool g_legacy;        // PIKLIB 6.x/7.1: CMC_Scene based, 3-argument runLine
static int g_toStringSlot = -1;

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

static XStr str(const char *s)
{
    static CtorCStr ctor = (CtorCStr)require("??0CXString", "QAE@PAD@Z");
    XStr x = {};
    ctor(&x, 0, s);
    return x;
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
    if (FARPROC f = find(prefix, need)) ((void(__cdecl *)())f)();
}

// Prints "<type>\t<value>" for an engine object, using its virtual toString.
static void putObject(void *object)
{
    typedef int(__fastcall * GetType)(void *, void *);
    typedef XStr *(__cdecl * TypeToString)(XStr *, int);
    typedef XStr *(__fastcall * ToString)(void *, void *, XStr *);
    static GetType getType = (GetType)require("?getType@CMC_Object", "QAEHXZ");
    static TypeToString typeToString = (TypeToString)require("?typeToString@CMC_Object");

    XStr type = {}, value = {};
    typeToString(&type, getType(object, 0));
    ((ToString)((*(void ***)object)[g_toStringSlot]))(object, 0, &value);
    putEscaped(type.p);
    fputc('\t', stdout);
    putEscaped(value.p);
}

static void *getObject(const char *name)
{
    if (FARPROC f = find("?getObject@CMC_ObjectsContainer", "PAD@Z"))
        return ((void *(__fastcall *)(void *, void *, const char *))f)(g_scene, 0, name);
    return ((void *(__fastcall *)(void *, void *, XStr))require("?getObject@CMC_Scene", "VCXString"))(g_scene, 0, str(name));
}

static void bootstrap()
{
    // toString is virtual; its slot is found by matching the exported
    // implementation against the exported CMC_Integer vtable.
    void **vtable = (void **)require("??_7CMC_Integer", "6B@");
    void *toString = (void *)require("?toString@CMC_Integer");
    for (int i = 0; i < 256 && g_toStringSlot < 0; ++i)
        if (vtable[i] == toString) g_toStringSlot = i;

    // Global singletons the application normally creates at startup.
    callIfPresent("?initialize@CMC_RunnerFactory", "SAXXZ");
    if (FARPROC f = find("?initialize@CMC_VariableFactory", "SAXH@Z")) ((void(__cdecl *)(int))f)(1000);
    callIfPresent("?initialize@CMC_VariableFactory", "SAXXZ");
    callIfPresent("?initTrash@CMC_Behaviour", "SAXXZ");

    g_scene = alloc(0x1000);
    if (FARPROC f = find("??0CMC_ObjectsContainer", "VCXString")) {
        ((void *(__fastcall *)(void *, void *, void *, void *, XStr, int))f)(g_scene, 0, 0, 0, str("PIKHOST"), 0);
    } else {
        // The older scene reads its name and properties from a definition element.
        g_legacy = true;
        void *element = alloc(0x400);
        ((void *(__fastcall *)(void *, void *, XStr))require("??0CMElement", "QAE@VCXString"))(element, 0, str("PIKHOST"));
        ((void *(__fastcall *)(void *, void *, void *, void *, void *, int))require("??0CMC_Scene", "PAVCMElement"))(g_scene, 0, 0, 0, element, 0);
    }
}

static void commandLoad(const char *path)
{
    // No window: objects created by the loader would otherwise call into it.
    // A few PIKLIB 8 builds (Robin Hood, Ksiega Dzungli, Angielski) read the
    // window's language settings in CFile::Open and crash here instead.
    void *loader = alloc(0x1000);
    ((void *(__fastcall *)(void *, void *, void *, void *))require("??0CMC_CNVLoader", "PAVCWindow"))(loader, 0, 0, g_scene);
    ((void(__fastcall *)(void *, void *, XStr))require("?load@CMC_CNVLoader", "UAEX"))(loader, 0, str(path));
    puts("OK");
}

static void commandRun(const char *line)
{
    void *result;
    if (!g_legacy) {
        typedef void *(__cdecl * RunLine)(void *, void *, XStr, void *);
        static RunLine run = (RunLine)require("?runLine@CMC_Behaviour", "SAPAVCMC_Variable");
        result = run(g_scene, 0, str(line), 0);
    } else {
        typedef void *(__cdecl * RunLine)(void *, void *, XStr);
        static RunLine run = (RunLine)require("?runLine@CMC_Behaviour", "SAPAVCMC_Variable");
        result = run(g_scene, 0, str(line));
    }
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
