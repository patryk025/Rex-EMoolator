#include <std/mem.pat>

enum ValueType : u32 {
    INTEGER = 1,
    STRING  = 2,
    BOOL    = 3,
    DOUBLE  = 4
};

struct ArrayObject {
    u32       index;
    ValueType type;

    if (type == ValueType::INTEGER) {
        u32 value;
    }
    else if (type == ValueType::STRING) {
        u32 length;
        char value[length];
    }
    else if (type == ValueType::BOOL) {
        u32 value;
        bool bool_value = value != 0;
    }
    else if (type == ValueType::DOUBLE) {
        u32 fixed_value;
        double real_value = fixed_value / 10000.0;
    }
    else {
        u32 raw_value;
    }
};

struct MultiArray {
    u32 dimensions;
    u32 dimensionSizes[dimensions];

    // read until EOF but without last 4 bytes
    ArrayObject elements[while ($ < std::mem::size() - 4u)];

    u32 terminator; // total capacity of multiarray
};

MultiArray mar @ 0x0;