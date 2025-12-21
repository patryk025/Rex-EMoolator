struct ShortString {
    u8 length;
    char data[length];
};

struct PtrSequencePair {
    u32 left;
    u32 right;
};

struct PtrSequenceStep {
    u32 number_of_pairs;
    PtrSequencePair pairs[number_of_pairs];
};

struct PtrSequenceMeta {
    u32 left;
    u32 top;
    u32 right;
    u32 bottom;
    u32 number_of_sequence_steps;
    PtrSequenceStep sequence_steps[number_of_sequence_steps];
};

struct PtrSubObjectPair {
    u32 left;
    u32 right;
};

struct PtrSubObjectVector {
    u32 unknown;
    char string1[256];
    char string2[256];
    u32 number_of_pairs;
    PtrSubObjectPair pairs[number_of_pairs];
};

struct PtrSubObjectVectorMeta {
    u32 number_of_subjects;
    PtrSubObjectVector subobjects[number_of_subjects];
};

struct PtrField {
    char name[256];
    u8 isLocked;
    u8 isDisabled;
    u8 unknown;
    u32 groupId; // 0xFFFFFFFF - no group
    u32 requiredColorID; // 0xFFFFFFFF - any color
    u32 requiredFillColor; // 0xFFFFFFFF - no requirements
    PtrSequenceMeta sequence1; // coloring animo
    PtrSequenceMeta sequence2; // audio?
    PtrSubObjectVectorMeta subobjects; // not sure, what it is
};

struct PtrColorPalette {
    u32 number_of_colors;
    u32 colors[number_of_colors]; // RGBA?
};

struct PtrFile {
    ShortString string1;
    ShortString string2;
    ShortString string3;
    ShortString string4;
    u32 width;
    u32 height;
    u8 bitmap[width * height];
    PtrColorPalette colorPalette;
    u32 number_of_fields;
    PtrField fields[number_of_fields];
};

PtrFile ptrFile @ 0x0;