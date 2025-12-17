struct PIKString {
    u32 length;
    char value[length];
};

struct PIKFile {
    PIKString filename;
    u64 offset;
    u32 size;
    u32 flags;
    // u8 data[size] @ offset; // there is no need here to read data
};

struct PIKEntry {
    PIKString entryPath;
    u32 file_count;
    u32 subdir_count;
    PIKEntry subdirs[subdir_count];
    PIKFile files[file_count];
};

struct PIKHeader {
    PIKString root_path; // it looks like '\'
    PIKString unknown2; // another string?
    u32 file_data_size; // in bytes
    u64 unknown3;
    u64 unknown4;
    u32 number_of_files; // maybe
    u32 number_of_folders; //maybe
    PIKEntry entryTable;
};

struct PIK {
    PIKHeader header;
};

PIK pik @ 0x0;