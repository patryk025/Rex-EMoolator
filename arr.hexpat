enum ValueType : u32 {
    INTEGER = 1,
    STRING  = 2,
    BOOL    = 3,
    DOUBLE  = 4
};

struct ArrayObject {
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

struct Array {
    u32 arraySize;
    ArrayObject elements[arraySize];
};

Array arr @ 0x0;