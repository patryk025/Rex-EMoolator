#define WIN32_LEAN_AND_MEAN
#include <windows.h>

#include "trace_format.h"

#define EXPECTED_EXE_TIMESTAMP 0x40ab191aUL
#define EXPECTED_EXE_IMAGE_SIZE 0x00015000UL
#define EXPECTED_PIK_TIMESTAMP 0x40ab18e4UL
#define EXPECTED_PIK_IMAGE_SIZE 0x00336000UL

#define ORIGINAL_DOMODAL_NAME "?domodal@CWindow@@QAE_NXZ"

typedef BOOL (WINAPI *GET_MESSAGE_FUNCTION)(LPMSG, HWND, UINT, UINT);
typedef unsigned long P8_UINTPTR;

static HINSTANCE g_probe_module = 0;
static GET_MESSAGE_FUNCTION g_original_get_message = 0;
static FARPROC g_original_domodal = 0;
static volatile LONG g_logger_state = 0;
static volatile LONG g_pending_message_sequence = 0;
static BOOL g_exe_validated = FALSE;
static BOOL g_pik_validated = FALSE;
static BOOL g_message_hooked = FALSE;
static BOOL g_qpc_available = FALSE;
static void *g_last_scene = 0;

static HANDLE g_trace_file = INVALID_HANDLE_VALUE;
static HANDLE g_trace_mapping = 0;
static BYTE *g_trace_view = 0;
static P8_TRACE_HEADER *g_trace_header = 0;

static int AsciiLower(int value)
{
    if (value >= 'A' && value <= 'Z') {
        return value + ('a' - 'A');
    }
    return value;
}

static BOOL EqualAsciiInsensitive(const char *left, const char *right)
{
    if (left == 0 || right == 0) {
        return FALSE;
    }
    while (*left != 0 && *right != 0) {
        if (AsciiLower((unsigned char)*left) != AsciiLower((unsigned char)*right)) {
            return FALSE;
        }
        ++left;
        ++right;
    }
    return *left == 0 && *right == 0;
}

static DWORD StringLengthBounded(const char *value, DWORD maximum)
{
    DWORD length = 0;
    if (value == 0) {
        return 0;
    }
    while (length < maximum && value[length] != 0) {
        ++length;
    }
    return length;
}

static BOOL AppendCharacter(char *buffer, DWORD capacity, DWORD *length, char value)
{
    if (*length + 1 >= capacity) {
        return FALSE;
    }
    buffer[*length] = value;
    ++*length;
    buffer[*length] = 0;
    return TRUE;
}

static BOOL AppendText(char *buffer, DWORD capacity, DWORD *length, const char *value)
{
    while (*value != 0) {
        if (!AppendCharacter(buffer, capacity, length, *value)) {
            return FALSE;
        }
        ++value;
    }
    return TRUE;
}

static BOOL AppendFixedDecimal(
    char *buffer,
    DWORD capacity,
    DWORD *length,
    DWORD value,
    DWORD digits)
{
    DWORD divisor = 1;
    DWORD index;
    for (index = 1; index < digits; ++index) {
        divisor *= 10;
    }
    for (index = 0; index < digits; ++index) {
        DWORD digit = (value / divisor) % 10;
        if (!AppendCharacter(buffer, capacity, length, (char)('0' + digit))) {
            return FALSE;
        }
        if (divisor > 1) {
            divisor /= 10;
        }
    }
    return TRUE;
}

static BOOL AppendHex8(char *buffer, DWORD capacity, DWORD *length, DWORD value)
{
    static const char digits[] = "0123456789ABCDEF";
    int shift;
    for (shift = 28; shift >= 0; shift -= 4) {
        if (!AppendCharacter(buffer, capacity, length, digits[(value >> shift) & 15])) {
            return FALSE;
        }
    }
    return TRUE;
}

static BOOL ReadModuleIdentity(HMODULE module, DWORD *timestamp, DWORD *image_size)
{
    BYTE *base;
    IMAGE_DOS_HEADER *dos;
    IMAGE_NT_HEADERS32 *nt;
    if (module == 0) {
        return FALSE;
    }
    base = (BYTE *)module;
    dos = (IMAGE_DOS_HEADER *)base;
    if (dos->e_magic != IMAGE_DOS_SIGNATURE || dos->e_lfanew <= 0) {
        return FALSE;
    }
    nt = (IMAGE_NT_HEADERS32 *)(base + dos->e_lfanew);
    if (nt->Signature != IMAGE_NT_SIGNATURE ||
        nt->OptionalHeader.Magic != IMAGE_NT_OPTIONAL_HDR32_MAGIC ||
        nt->FileHeader.Machine != IMAGE_FILE_MACHINE_I386) {
        return FALSE;
    }
    *timestamp = nt->FileHeader.TimeDateStamp;
    *image_size = nt->OptionalHeader.SizeOfImage;
    return TRUE;
}

static BOOL ValidateExecutable(void)
{
    DWORD timestamp = 0;
    DWORD image_size = 0;
    if (!ReadModuleIdentity(GetModuleHandleA(0), &timestamp, &image_size)) {
        return FALSE;
    }
    return timestamp == EXPECTED_EXE_TIMESTAMP && image_size == EXPECTED_EXE_IMAGE_SIZE;
}

static BOOL BuildTraceName(char *name, DWORD capacity, const SYSTEMTIME *time)
{
    DWORD length = 0;
    name[0] = 0;
    return AppendText(name, capacity, &length, "P8TRACE-") &&
           AppendFixedDecimal(name, capacity, &length, time->wYear, 4) &&
           AppendFixedDecimal(name, capacity, &length, time->wMonth, 2) &&
           AppendFixedDecimal(name, capacity, &length, time->wDay, 2) &&
           AppendCharacter(name, capacity, &length, '-') &&
           AppendFixedDecimal(name, capacity, &length, time->wHour, 2) &&
           AppendFixedDecimal(name, capacity, &length, time->wMinute, 2) &&
           AppendFixedDecimal(name, capacity, &length, time->wSecond, 2) &&
           AppendCharacter(name, capacity, &length, '-') &&
           AppendHex8(name, capacity, &length, GetCurrentProcessId()) &&
           AppendText(name, capacity, &length, ".bin");
}

static BOOL JoinPath(
    char *output,
    DWORD output_capacity,
    const char *directory,
    const char *name)
{
    DWORD length = 0;
    DWORD directory_length = StringLengthBounded(directory, output_capacity);
    output[0] = 0;
    if (directory_length == 0 || directory_length >= output_capacity) {
        return FALSE;
    }
    if (!AppendText(output, output_capacity, &length, directory)) {
        return FALSE;
    }
    if (output[length - 1] != '\\' && output[length - 1] != '/') {
        if (!AppendCharacter(output, output_capacity, &length, '\\')) {
            return FALSE;
        }
    }
    return AppendText(output, output_capacity, &length, name);
}

static BOOL GetProbeDirectory(char *directory, DWORD capacity)
{
    DWORD length;
    DWORD index;
    if (g_probe_module == 0 || capacity == 0) {
        return FALSE;
    }
    length = GetModuleFileNameA(g_probe_module, directory, capacity);
    if (length == 0 || length >= capacity) {
        return FALSE;
    }
    index = length;
    while (index > 0) {
        --index;
        if (directory[index] == '\\' || directory[index] == '/') {
            directory[index] = 0;
            return index != 0;
        }
    }
    return FALSE;
}

static HANDLE CreateTraceFile(
    const char *trace_name,
    char *full_path,
    DWORD full_path_capacity,
    BOOL *used_temp)
{
    char directory[MAX_PATH];
    DWORD result;
    HANDLE file;
    *used_temp = FALSE;

    result = GetProbeDirectory(directory, MAX_PATH);
    if (result &&
        JoinPath(full_path, full_path_capacity, directory, trace_name)) {
        file = CreateFileA(
            full_path,
            GENERIC_READ | GENERIC_WRITE,
            FILE_SHARE_READ,
            0,
            CREATE_NEW,
            FILE_ATTRIBUTE_NORMAL,
            0);
        if (file != INVALID_HANDLE_VALUE) {
            return file;
        }
    }

    result = GetTempPathA(MAX_PATH, directory);
    if (result > 0 && result < MAX_PATH &&
        JoinPath(full_path, full_path_capacity, directory, trace_name)) {
        file = CreateFileA(
            full_path,
            GENERIC_READ | GENERIC_WRITE,
            FILE_SHARE_READ,
            0,
            CREATE_NEW,
            FILE_ATTRIBUTE_NORMAL,
            0);
        if (file != INVALID_HANDLE_VALUE) {
            *used_temp = TRUE;
            return file;
        }
    }
    return INVALID_HANDLE_VALUE;
}

static void CopyHeaderText(char *destination, DWORD capacity, const char *source)
{
    DWORD index = 0;
    while (index + 1 < capacity && source[index] != 0) {
        destination[index] = source[index];
        ++index;
    }
    if (capacity != 0) {
        destination[index] = 0;
    }
}

static BOOL InitializeLogger(void)
{
    char trace_name[128];
    char full_path[MAX_PATH];
    SYSTEMTIME start_time;
    LARGE_INTEGER frequency;
    BOOL used_temp = FALSE;
    DWORD index;
    DWORD exe_timestamp = 0;
    DWORD exe_image_size = 0;
    DWORD pik_timestamp = 0;
    DWORD pik_image_size = 0;
    volatile DWORD *header_words;

    GetLocalTime(&start_time);
    if (!BuildTraceName(trace_name, sizeof(trace_name), &start_time)) {
        return FALSE;
    }

    g_trace_file = CreateTraceFile(
        trace_name, full_path, sizeof(full_path), &used_temp);
    if (g_trace_file == INVALID_HANDLE_VALUE) {
        return FALSE;
    }

    g_trace_mapping = CreateFileMappingA(
        g_trace_file,
        0,
        PAGE_READWRITE,
        0,
        P8_TRACE_FILE_SIZE,
        0);
    if (g_trace_mapping == 0) {
        CloseHandle(g_trace_file);
        g_trace_file = INVALID_HANDLE_VALUE;
        return FALSE;
    }

    g_trace_view = (BYTE *)MapViewOfFile(
        g_trace_mapping, FILE_MAP_WRITE, 0, 0, P8_TRACE_FILE_SIZE);
    if (g_trace_view == 0) {
        CloseHandle(g_trace_mapping);
        CloseHandle(g_trace_file);
        g_trace_mapping = 0;
        g_trace_file = INVALID_HANDLE_VALUE;
        return FALSE;
    }

    g_trace_header = (P8_TRACE_HEADER *)g_trace_view;
    header_words = (volatile DWORD *)g_trace_header;
    for (index = 0; index < P8_TRACE_HEADER_SIZE / sizeof(DWORD); ++index) {
        header_words[index] = 0;
    }

    g_trace_header->magic[0] = 'P';
    g_trace_header->magic[1] = '8';
    g_trace_header->magic[2] = 'T';
    g_trace_header->magic[3] = 'R';
    g_trace_header->magic[4] = 'A';
    g_trace_header->magic[5] = 'C';
    g_trace_header->magic[6] = 'E';
    g_trace_header->magic[7] = '1';
    g_trace_header->format_version = P8_TRACE_FORMAT_VERSION;
    g_trace_header->header_size = P8_TRACE_HEADER_SIZE;
    g_trace_header->record_size = P8_TRACE_RECORD_SIZE;
    g_trace_header->capacity =
        (P8_TRACE_FILE_SIZE - P8_TRACE_HEADER_SIZE) / P8_TRACE_RECORD_SIZE;
    g_trace_header->process_id = GetCurrentProcessId();
    g_trace_header->main_thread_id = GetCurrentThreadId();
    g_trace_header->start_tick = GetTickCount();
    g_trace_header->start_local_time.wYear = start_time.wYear;
    g_trace_header->start_local_time.wMonth = start_time.wMonth;
    g_trace_header->start_local_time.wDayOfWeek = start_time.wDayOfWeek;
    g_trace_header->start_local_time.wDay = start_time.wDay;
    g_trace_header->start_local_time.wHour = start_time.wHour;
    g_trace_header->start_local_time.wMinute = start_time.wMinute;
    g_trace_header->start_local_time.wSecond = start_time.wSecond;
    g_trace_header->start_local_time.wMilliseconds = start_time.wMilliseconds;

    g_qpc_available = QueryPerformanceFrequency(&frequency);
    if (g_qpc_available) {
        g_trace_header->qpc_frequency.QuadPart = frequency.QuadPart;
        g_trace_header->flags |= P8_HEADER_QPC_AVAILABLE;
    }
    if (g_message_hooked) {
        g_trace_header->flags |= P8_HEADER_MESSAGE_HOOKED;
    }
    if (g_exe_validated) {
        g_trace_header->flags |= P8_HEADER_EXE_VALIDATED;
    }
    if (g_pik_validated) {
        g_trace_header->flags |= P8_HEADER_PIK_VALIDATED;
    }
    if (used_temp) {
        g_trace_header->flags |= P8_HEADER_TEMP_PATH;
    }

    ReadModuleIdentity(GetModuleHandleA(0), &exe_timestamp, &exe_image_size);
    ReadModuleIdentity(GetModuleHandleA("Piklib8.dll"), &pik_timestamp, &pik_image_size);
    g_trace_header->exe_timestamp = exe_timestamp;
    g_trace_header->exe_image_size = exe_image_size;
    g_trace_header->pik_timestamp = pik_timestamp;
    g_trace_header->pik_image_size = pik_image_size;
    CopyHeaderText(g_trace_header->trace_name, sizeof(g_trace_header->trace_name), trace_name);
    return TRUE;
}

static BOOL EnsureLogger(void)
{
    if (g_logger_state == 2) {
        return g_trace_header != 0;
    }
    if (g_logger_state != 0) {
        return FALSE;
    }
    g_logger_state = 1;
    InitializeLogger();
    g_logger_state = 2;
    return g_trace_header != 0;
}

static LONGLONG ReadQpc(void)
{
    LARGE_INTEGER value;
    if (g_qpc_available && QueryPerformanceCounter(&value)) {
        return value.QuadPart;
    }
    return 0;
}

static P8_TRACE_RECORD *BeginRecord(DWORD type, LONG *sequence)
{
    P8_TRACE_RECORD *record;
    volatile DWORD *words;
    DWORD index;
    LONG next;
    if (g_trace_header == 0) {
        return 0;
    }
    next = g_trace_header->next_index + 1;
    g_trace_header->next_index = next;
    if (next <= 0 || (DWORD)next > g_trace_header->capacity) {
        g_trace_header->dropped = g_trace_header->dropped + 1;
        return 0;
    }
    record = (P8_TRACE_RECORD *)(
        g_trace_view + P8_TRACE_HEADER_SIZE +
        ((DWORD)next - 1) * P8_TRACE_RECORD_SIZE);
    words = (volatile DWORD *)record;
    for (index = 0; index < P8_TRACE_RECORD_SIZE / sizeof(DWORD); ++index) {
        words[index] = 0;
    }
    record->type = type;
    *sequence = next;
    return record;
}

static void CommitRecord(P8_TRACE_RECORD *record, LONG sequence)
{
    if (record != 0) {
        InterlockedExchange((LONG *)&record->commit_sequence, sequence);
    }
}

static void *GetCurrentScene(void *window_object)
{
    BYTE *application;
    BYTE *mc_application;
    if (!g_pik_validated || window_object == 0) {
        return 0;
    }
    application = *(BYTE **)((BYTE *)window_object + 0x18);
    if (application == 0) {
        return 0;
    }
    mc_application = *(BYTE **)(application + 0x4c);
    if (mc_application == 0) {
        return 0;
    }
    return *(void **)(mc_application + 0xf4);
}

static void LogSceneIfChanged(void *scene, LONGLONG qpc)
{
    P8_TRACE_RECORD *record;
    LONG sequence;
    LONG name_length;
    char *name;
    BYTE *name_output;
    DWORD copy_length;
    DWORD index;

    if (scene == g_last_scene) {
        return;
    }
    g_last_scene = scene;
    record = BeginRecord(P8_EVENT_SCENE, &sequence);
    if (record == 0) {
        return;
    }
    record->qpc0.QuadPart = qpc;
    record->object = (DWORD)(P8_UINTPTR)scene;
    if (scene == 0) {
        record->flags |= P8_SCENE_NULL;
        CommitRecord(record, sequence);
        return;
    }

    name_length = *(LONG *)((BYTE *)scene + 0x14);
    name = *(char **)((BYTE *)scene + 0x1c);
    if (name_length < 0 || name_length > 4096 || (name_length != 0 && name == 0)) {
        record->flags |= P8_SCENE_NAME_INVALID;
        CommitRecord(record, sequence);
        return;
    }
    record->tick = (DWORD)name_length;
    copy_length = (DWORD)name_length;
    if (copy_length > sizeof(record->args) - 1) {
        copy_length = sizeof(record->args) - 1;
        record->flags |= P8_SCENE_NAME_TRUNCATED;
    }
    name_output = (BYTE *)&record->args[0];
    for (index = 0; index < copy_length; ++index) {
        if (name[index] == 0) {
            break;
        }
        name_output[index] = (BYTE)name[index];
    }
    name_output[index] = 0;
    CommitRecord(record, sequence);
}

static BOOL ResolveOriginalDomodal(void)
{
    HMODULE module;
    DWORD timestamp = 0;
    DWORD image_size = 0;
    if (g_original_domodal != 0) {
        return TRUE;
    }
    module = GetModuleHandleA("Piklib8.dll");
    if (module == 0) {
        module = LoadLibraryA("Piklib8.dll");
    }
    if (module == 0) {
        return FALSE;
    }
    if (ReadModuleIdentity(module, &timestamp, &image_size) &&
        timestamp == EXPECTED_PIK_TIMESTAMP && image_size == EXPECTED_PIK_IMAGE_SIZE) {
        g_pik_validated = TRUE;
        if (g_trace_header != 0) {
            g_trace_header->flags |= P8_HEADER_PIK_VALIDATED;
            g_trace_header->pik_timestamp = timestamp;
            g_trace_header->pik_image_size = image_size;
        }
    }
    g_original_domodal = GetProcAddress(module, ORIGINAL_DOMODAL_NAME);
    return g_original_domodal != 0;
}

static unsigned char CallOriginalDomodal(void *window_object)
{
    FARPROC function = g_original_domodal;
    unsigned char result = 1;
    __asm {
        mov eax, function
        mov ecx, window_object
        call eax
        mov result, al
    }
    return result;
}

extern "C" unsigned char __cdecl P8ProbeDomodalImpl(void *window_object)
{
    unsigned char result;
    void *scene_before;
    void *scene_after;
    BYTE active;
    DWORD entry_tick;
    LONGLONG qpc_enter;
    LONGLONG qpc_exit;
    LONG paired_message;
    LONG sequence;
    P8_TRACE_RECORD *record;

    if (!ResolveOriginalDomodal()) {
        return 1;
    }
    if (!g_exe_validated || !g_pik_validated || !EnsureLogger()) {
        return CallOriginalDomodal(window_object);
    }

    scene_before = GetCurrentScene(window_object);
    if (scene_before != g_last_scene) {
        LogSceneIfChanged(scene_before, ReadQpc());
    }
    active = *((BYTE *)window_object + 0x98);
    entry_tick = GetTickCount();
    qpc_enter = ReadQpc();
    result = CallOriginalDomodal(window_object);
    qpc_exit = ReadQpc();
    scene_after = GetCurrentScene(window_object);
    if (scene_after != g_last_scene) {
        LogSceneIfChanged(scene_after, qpc_exit);
    }
    paired_message = g_pending_message_sequence;
    g_pending_message_sequence = 0;

    record = BeginRecord(P8_EVENT_DOMODAL, &sequence);
    if (record != 0) {
        record->tick = entry_tick;
        record->qpc0.QuadPart = qpc_enter;
        record->qpc1.QuadPart = qpc_exit;
        record->object = (DWORD)(P8_UINTPTR)window_object;
        record->args[0] = (DWORD)(P8_UINTPTR)scene_before;
        record->args[1] = (DWORD)(P8_UINTPTR)scene_after;
        record->args[2] = (DWORD)result;
        record->args[3] = (DWORD)active;
        record->args[4] = (DWORD)paired_message;
        CommitRecord(record, sequence);
    }
    return result;
}

class CWindow
{
public:
    __declspec(dllexport) bool domodal(void);
};

bool CWindow::domodal(void)
{
    return P8ProbeDomodalImpl(this) != 0;
}

extern "C" BOOL WINAPI P8ProbeGetMessageA(
    LPMSG message,
    HWND window,
    UINT minimum_filter,
    UINT maximum_filter)
{
    BOOL result;
    LONGLONG qpc_enter;
    LONGLONG qpc_exit;
    P8_TRACE_RECORD *record;
    LONG sequence = 0;

    EnsureLogger();
    qpc_enter = ReadQpc();
    result = g_original_get_message(message, window, minimum_filter, maximum_filter);
    qpc_exit = ReadQpc();

    record = BeginRecord(P8_EVENT_GETMESSAGE, &sequence);
    if (record != 0) {
        record->qpc0.QuadPart = qpc_enter;
        record->qpc1.QuadPart = qpc_exit;
        record->args[0] = (DWORD)result;
        record->args[4] = minimum_filter;
        record->args[5] = maximum_filter;
        record->args[6] = (DWORD)(P8_UINTPTR)message;
        if (result >= 0 && message != 0) {
            record->tick = message->time;
            record->object = (DWORD)(P8_UINTPTR)message->hwnd;
            record->args[1] = message->message;
            record->args[2] = (DWORD)message->wParam;
            record->args[3] = (DWORD)message->lParam;
        }
        CommitRecord(record, sequence);
    }
    if (result > 0 && sequence > 0) {
        g_pending_message_sequence = sequence;
    } else {
        g_pending_message_sequence = 0;
    }
    return result;
}

static BOOL PatchGetMessageImport(void)
{
    BYTE *base = (BYTE *)GetModuleHandleA(0);
    IMAGE_DOS_HEADER *dos;
    IMAGE_NT_HEADERS32 *nt;
    IMAGE_DATA_DIRECTORY directory;
    IMAGE_IMPORT_DESCRIPTOR *descriptor;
    DWORD descriptor_index;

    if (base == 0) {
        return FALSE;
    }
    dos = (IMAGE_DOS_HEADER *)base;
    if (dos->e_magic != IMAGE_DOS_SIGNATURE || dos->e_lfanew <= 0) {
        return FALSE;
    }
    nt = (IMAGE_NT_HEADERS32 *)(base + dos->e_lfanew);
    if (nt->Signature != IMAGE_NT_SIGNATURE ||
        nt->OptionalHeader.Magic != IMAGE_NT_OPTIONAL_HDR32_MAGIC) {
        return FALSE;
    }
    directory = nt->OptionalHeader.DataDirectory[IMAGE_DIRECTORY_ENTRY_IMPORT];
    if (directory.VirtualAddress == 0 || directory.Size < sizeof(IMAGE_IMPORT_DESCRIPTOR)) {
        return FALSE;
    }
    descriptor = (IMAGE_IMPORT_DESCRIPTOR *)(base + directory.VirtualAddress);
    for (descriptor_index = 0;
         descriptor_index < directory.Size / sizeof(IMAGE_IMPORT_DESCRIPTOR);
         ++descriptor_index, ++descriptor) {
        IMAGE_THUNK_DATA32 *names;
        IMAGE_THUNK_DATA32 *iat;
        DWORD thunk_index;
        char *dll_name;
        if (descriptor->Name == 0 && descriptor->FirstThunk == 0) {
            break;
        }
        if (descriptor->Name == 0 || descriptor->FirstThunk == 0) {
            return FALSE;
        }
        dll_name = (char *)(base + descriptor->Name);
        if (!EqualAsciiInsensitive(dll_name, "USER32.dll")) {
            continue;
        }
        if (descriptor->OriginalFirstThunk == 0) {
            return FALSE;
        }
        names = (IMAGE_THUNK_DATA32 *)(base + descriptor->OriginalFirstThunk);
        iat = (IMAGE_THUNK_DATA32 *)(base + descriptor->FirstThunk);
        for (thunk_index = 0; names[thunk_index].u1.Ordinal != 0; ++thunk_index) {
            IMAGE_IMPORT_BY_NAME *import_name;
            DWORD old_protection;
            DWORD ignored_protection;
            if ((names[thunk_index].u1.Ordinal & 0x80000000UL) != 0) {
                continue;
            }
            import_name = (IMAGE_IMPORT_BY_NAME *)(
                base + names[thunk_index].u1.Ordinal);
            if (!EqualAsciiInsensitive((char *)import_name->Name, "GetMessageA")) {
                continue;
            }
            g_original_get_message =
                (GET_MESSAGE_FUNCTION)(P8_UINTPTR)iat[thunk_index].u1.Ordinal;
            if (g_original_get_message == 0) {
                return FALSE;
            }
            if (!VirtualProtect(
                    &iat[thunk_index],
                    sizeof(iat[thunk_index]),
                    PAGE_READWRITE,
                    &old_protection)) {
                g_original_get_message = 0;
                return FALSE;
            }
            iat[thunk_index].u1.Ordinal = (DWORD)(P8_UINTPTR)&P8ProbeGetMessageA;
            VirtualProtect(
                &iat[thunk_index],
                sizeof(iat[thunk_index]),
                old_protection,
                &ignored_protection);
            return TRUE;
        }
        return FALSE;
    }
    return FALSE;
}

extern "C" BOOL WINAPI P8ProbeDllMain(
    HINSTANCE instance,
    DWORD reason,
    LPVOID reserved)
{
    (void)reserved;
    if (reason == DLL_PROCESS_ATTACH) {
        g_probe_module = instance;
        DisableThreadLibraryCalls(instance);
        g_exe_validated = ValidateExecutable();
        if (g_exe_validated) {
            g_message_hooked = PatchGetMessageImport();
        }
    }
    return TRUE;
}
