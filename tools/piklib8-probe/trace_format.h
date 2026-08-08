#ifndef P8PROBE_TRACE_FORMAT_H
#define P8PROBE_TRACE_FORMAT_H

#include <windows.h>

#define P8_TRACE_FORMAT_VERSION 1
#define P8_TRACE_HEADER_SIZE 256
#define P8_TRACE_RECORD_SIZE 72
#define P8_TRACE_FILE_SIZE (64UL * 1024UL * 1024UL)

#define P8_HEADER_MESSAGE_HOOKED 0x00000001UL
#define P8_HEADER_EXE_VALIDATED  0x00000002UL
#define P8_HEADER_QPC_AVAILABLE  0x00000004UL
#define P8_HEADER_TEMP_PATH      0x00000008UL
#define P8_HEADER_PIK_VALIDATED  0x00000010UL

#define P8_EVENT_DOMODAL     1UL
#define P8_EVENT_GETMESSAGE  2UL
#define P8_EVENT_SCENE       3UL

#define P8_SCENE_NAME_TRUNCATED 0x00000001UL
#define P8_SCENE_NAME_INVALID   0x00000002UL
#define P8_SCENE_NULL           0x00000004UL

#pragma pack(push, 1)

typedef struct P8_TRACE_HEADER {
    char magic[8];
    DWORD format_version;
    DWORD header_size;
    DWORD record_size;
    DWORD capacity;
    volatile LONG next_index;
    volatile LONG dropped;
    volatile DWORD flags;
    DWORD process_id;
    DWORD main_thread_id;
    DWORD start_tick;
    LARGE_INTEGER qpc_frequency;
    SYSTEMTIME start_local_time;
    DWORD exe_timestamp;
    DWORD exe_image_size;
    DWORD pik_timestamp;
    DWORD pik_image_size;
    DWORD reserved[10];
    char trace_name[128];
} P8_TRACE_HEADER;

typedef struct P8_TRACE_RECORD {
    volatile LONG commit_sequence;
    DWORD type;
    DWORD flags;
    DWORD tick;
    LARGE_INTEGER qpc0;
    LARGE_INTEGER qpc1;
    DWORD object;
    DWORD args[8];
    DWORD reserved;
} P8_TRACE_RECORD;

#pragma pack(pop)

typedef char P8_TRACE_HEADER_SIZE_CHECK[
    sizeof(P8_TRACE_HEADER) == P8_TRACE_HEADER_SIZE ? 1 : -1
];
typedef char P8_TRACE_RECORD_SIZE_CHECK[
    sizeof(P8_TRACE_RECORD) == P8_TRACE_RECORD_SIZE ? 1 : -1
];

#endif

