#!/usr/bin/env python3
import argparse
from io import BufferedReader
import os
import struct

MAGIC = 0x584D454D  # 'MEMX'
HEADER_FMT = "<IIII"
REGION_FMT = "<IIIIQ"
HEADER_SIZE = struct.calcsize(HEADER_FMT)
REGION_SIZE = struct.calcsize(REGION_FMT)

CWINDOW_PTR = 0x00419ADC
CWINDOW_APPLICATION_IFC_OFFSET = 0x18
CAPPLICATION_MCAPP_OFFSET = 0x4C

CMC_OBJECTS_VECTOR_OFFSET = 0x4C
CMC_CURRENT_SCENE_OFFSET = 0xFC
CMC_OBJECT_NAME_OFFSET = 0x10
CMC_OBJECT_TYPE_OFFSET = 0x20

BEHAVIOUR_CODE_OFFSET = 0x4C
BEHAVIOUR_CONDITION_TEXT_OFFSET = 0x5C
BEHAVIOUR_CONDITION_PTR_OFFSET = 0x6C

EXPRESSION_OPERAND1_OFFSET = 0x58
EXPRESSION_OPERAND2_OFFSET = 0x68
EXPRESSION_HELPER_BEHAVIOUR_OFFSET = 0x78

CONDITION_OPERAND1_OFFSET = 0x5C
CONDITION_OPERAND2_OFFSET = 0x6C
CONDITION_OPERATOR_TEXT_OFFSET = 0x7C
CONDITION_OPERATOR_ID_OFFSET = 0x8C

CXVECTOR_ITEMS_OFFSET = 0x14
CXVECTOR_COUNT_OFFSET = 0x1C

ROOT_PATH_PTR = 0x10468FF8
DANE_PATH_PTR = 0x1024D964

TYPE_NAMES = {
    1: "INTEGER",
    2: "STRING",
    3: "BOOL",
    4: "DOUBLE",
    10: "CONDITION",
    11: "COMPLEX CONDITION",
    12: "EXPRESSION",
    13: "BEHAVIOUR",
    100: "MOUSE",
    101: "KEYBOARD",
    102: "RANDOM",
    103: "CANVAS OBSERVER",
    1000: "APPLICATION",
    1001: "EPISODE",
    1002: "SCENE",
    2000: "ANIMO",
    2001: "BUTTON",
    2002: "FONT",
    2004: "SEQUENCE",
    2005: "SOUND",
    2006: "TEXT",
    2007: "TIMER",
    2008: "IMAGE",
    2010: "NETPEER",
    2013: "DIALOG",
    2014: "FILTER",
    3001: "ARRAY",
    11000: "SYSTEM",
    12000: "CNVLOADER",
}

CONTAINER_TYPES = {1000, 1001, 1002}

CONDITION_OP_NAMES = {
    1: "EQUAL",
    2: "NOTEQUAL",
    3: "LESS",
    4: "LESSEQUAL",
    5: "GREATER",
    6: "GREATEREQUAL",
    7: "INSTANCEOF",
}


class CXString:
    __slots__ = ("data", "length", "capacity")

    def __init__(self, data, length, capacity):
        self.data = data
        self.length = length
        self.capacity = capacity

    def __repr__(self):
        return (
            f"CXString(data={self.data!r}, length={self.length}, "
            f"capacity={self.capacity})"
        )


class RegionIdx:
    __slots__ = ("base", "size", "protect", "type", "file_offset")

    def __init__(self, base, size, protect, rtype, file_offset):
        self.base = base
        self.size = size
        self.protect = protect
        self.type = rtype
        self.file_offset = file_offset

    def __repr__(self):
        return (
            f"Region(base=0x{self.base:08X}, size=0x{self.size:X}, "
            f"protect=0x{self.protect:X}, type=0x{self.type:X}, "
            f"file_offset=0x{self.file_offset:X})"
        )


def load_index(path):
    with open(path, "rb") as f:
        header_data = f.read(HEADER_SIZE)
        if len(header_data) != HEADER_SIZE:
            raise RuntimeError("Index file too short (no header)")
        magic, version, count, reserved = struct.unpack(HEADER_FMT, header_data)
        if magic != MAGIC:
            raise RuntimeError(f"Bad magic 0x{magic:08X}, expected 0x{MAGIC:08X}")
        if version != 1:
            raise RuntimeError(f"Unsupported version {version}")
        regions = []
        for i in range(count):
            data = f.read(REGION_SIZE)
            if len(data) != REGION_SIZE:
                raise RuntimeError(f"Index truncated at region {i}/{count}")
            base, size, protect, rtype, file_offset = struct.unpack(REGION_FMT, data)
            regions.append(RegionIdx(base, size, protect, rtype, file_offset))
    return regions


def find_region_for_va(va, regions):
    for region in regions:
        if region.base <= va < region.base + region.size:
            return region
    return None


def read_bytes(f: BufferedReader, regions, va, size):
    region = find_region_for_va(va, regions)
    if not region:
        raise RuntimeError(f"VA 0x{va:08X} not found in any region")
    offset = region.file_offset + (va - region.base)
    f.seek(offset)
    data = f.read(size)
    if len(data) != size:
        raise RuntimeError(f"Failed to read {size} bytes at VA 0x{va:08X}")
    return data


def read_u32(f: BufferedReader, regions, va):
    return struct.unpack("<I", read_bytes(f, regions, va, 4))[0]


def get_cwindow(f: BufferedReader, regions):
    return read_u32(f, regions, CWINDOW_PTR)


def read_cxstring(f: BufferedReader, regions, ptr):
    vtable, length, capacity, data_ptr = struct.unpack(
        "<IIII", read_bytes(f, regions, ptr, 16)
    )
    if data_ptr == 0 or length == 0:
        return CXString(b"", length, capacity)
    text_data = read_bytes(f, regions, data_ptr, length)
    return CXString(text_data, length, capacity)


def safe_read_cxstring_text(f: BufferedReader, regions, ptr):
    try:
        value = read_cxstring(f, regions, ptr)
    except RuntimeError:
        return None
    return value.data.decode("latin-1", errors="replace")


def maybe_read_cxstring_text(f: BufferedReader, regions, ptr):
    try:
        return safe_read_cxstring_text(f, regions, ptr)
    except Exception:
        return None


def read_cxvector_layout(f: BufferedReader, regions, vector_ptr):
    items_ptr = read_u32(f, regions, vector_ptr + CXVECTOR_ITEMS_OFFSET)
    count = read_u32(f, regions, vector_ptr + CXVECTOR_COUNT_OFFSET)
    return items_ptr, count


def iter_object_pointers(f: BufferedReader, regions, container_ptr):
    vector_ptr = read_u32(f, regions, container_ptr + CMC_OBJECTS_VECTOR_OFFSET)
    if vector_ptr == 0:
        return vector_ptr, 0, []

    items_ptr, count = read_cxvector_layout(f, regions, vector_ptr)
    object_ptrs = []
    for index in range(count):
        object_ptrs.append(read_u32(f, regions, items_ptr + index * 4))
    return vector_ptr, count, object_ptrs


def read_object_name(f: BufferedReader, regions, obj_ptr):
    return safe_read_cxstring_text(f, regions, obj_ptr + CMC_OBJECT_NAME_OFFSET)


def read_object_type_id(f: BufferedReader, regions, obj_ptr):
    return read_u32(f, regions, obj_ptr + CMC_OBJECT_TYPE_OFFSET)


def type_name(type_id):
    return TYPE_NAMES.get(type_id, str(type_id))


def summarize_text(text, limit=48):
    if text is None:
        return None
    compact = " ".join(text.split())
    if len(compact) <= limit:
        return compact
    return compact[: limit - 3] + "..."


def parse_number(text):
    text = text.strip()
    if not text:
        return None
    try:
        if "." in text:
            return float(text)
        return int(text, 10)
    except ValueError:
        return None


def tokenize_linear_expression(expr):
    tokens = []
    current = []
    expect_value = True

    for char in expr.strip():
        if char.isspace():
            continue
        if char in "+-*%@":
            if char == "-" and expect_value:
                current.append(char)
                continue
            if not current:
                return None
            tokens.append("".join(current))
            tokens.append(char)
            current = []
            expect_value = True
            continue
        current.append(char)
        expect_value = False

    if not current:
        return None
    tokens.append("".join(current))
    if len(tokens) % 2 == 0:
        return None
    return tokens


def eval_linear_expression(expr):
    tokens = tokenize_linear_expression(expr)
    if not tokens:
        return None

    value = parse_number(tokens[0])
    if value is None:
        return None

    index = 1
    while index < len(tokens):
        op = tokens[index]
        rhs = parse_number(tokens[index + 1])
        if rhs is None:
            return None

        if op == "+":
            value += rhs
        elif op == "-":
            value -= rhs
        elif op == "*":
            value *= rhs
        elif op == "@":
            value /= rhs
        elif op == "%":
            value %= rhs
        else:
            return None
        index += 2

    if isinstance(value, float) and value.is_integer():
        return int(value)
    return value


def resolve_legacy_operand(token):
    token = token.strip()
    if token.startswith("[") and token.endswith("]"):
        expr_value = eval_linear_expression(token[1:-1])
        if expr_value is not None:
            return expr_value

    if len(token) >= 2 and token[0] == token[-1] == '"':
        return token[1:-1]

    upper = token.upper()
    if upper == "TRUE":
        return True
    if upper == "FALSE":
        return False

    numeric = parse_number(token)
    if numeric is not None:
        return numeric

    return token


def split_legacy_comparison(expr):
    apos = expr.find("'")
    if apos != -1:
        op_text = "'"
        left_end = apos
        if apos > 0 and expr[apos - 1] in "!<>":
            op_text = expr[apos - 1] + "'"
            left_end -= 1
        return expr[:left_end], op_text, expr[apos + 1 :]

    for op_text in ("<", ">", "?"):
        pos = expr.find(op_text)
        if pos != -1:
            return expr[:pos], op_text, expr[pos + 1 :]

    return None


def compare_legacy_values(left_value, right_value, op_text):
    if op_text == "'":
        return left_value == right_value
    if op_text == "!'":
        return left_value != right_value

    if op_text in ("<", "<'", ">", ">'"):
        if isinstance(left_value, str) or isinstance(right_value, str):
            return False
        if op_text == "<":
            return left_value < right_value
        if op_text == "<'":
            return left_value <= right_value
        if op_text == ">":
            return left_value > right_value
        return left_value >= right_value

    if op_text == "?":
        return False

    return False


def emulate_legacy_if_condition(expr):
    expr = expr.strip()
    has_and = "&&" in expr
    has_or = "||" in expr

    if has_and or has_or:
        delimiter = "&&" if has_and else "||"
        stop_on = has_or
        parts = [part.strip() for part in expr.split(delimiter)]
        evaluations = []
        result = False

        for part in parts:
            part_result, part_trace = emulate_legacy_if_condition(part)
            evaluations.append(part_trace)
            result = part_result
            if part_result == stop_on:
                break

        return result, {
            "kind": "logic",
            "expr": expr,
            "delimiter": delimiter,
            "stop_on": stop_on,
            "result": result,
            "parts": evaluations,
        }

    pieces = split_legacy_comparison(expr)
    if pieces is None:
        value = resolve_legacy_operand(expr)
        result = bool(value)
        return result, {
            "kind": "value",
            "expr": expr,
            "value": value,
            "result": result,
        }

    left_text, op_text, right_text = pieces
    left_value = resolve_legacy_operand(left_text)
    right_value = resolve_legacy_operand(right_text)
    result = compare_legacy_values(left_value, right_value, op_text)
    return result, {
        "kind": "comparison",
        "expr": expr,
        "left_text": left_text.strip(),
        "right_text": right_text.strip(),
        "left_value": left_value,
        "right_value": right_value,
        "op": op_text,
        "result": result,
    }


def print_legacy_trace(trace, depth=0):
    indent = "  " * depth
    kind = trace["kind"]

    if kind == "logic":
        stop_label = "true" if trace["stop_on"] else "false"
        print(
            f"{indent}- logic {trace['delimiter']!r} stop_on={stop_label} "
            f"=> {trace['result']}"
        )
        print(f"{indent}  expr: {trace['expr']}")
        for part in trace["parts"]:
            print_legacy_trace(part, depth + 1)
        return

    if kind == "comparison":
        print(
            f"{indent}- cmp {trace['left_text']!r} {trace['op']} "
            f"{trace['right_text']!r} => {trace['result']}"
        )
        print(
            f"{indent}  values: {trace['left_value']!r} vs "
            f"{trace['right_value']!r}"
        )
        return

    print(f"{indent}- value {trace['expr']!r} => {trace['value']!r} ({trace['result']})")


def print_legacy_if_analysis(expr):
    result, trace = emulate_legacy_if_condition(expr)
    print("[+] Legacy @IF condition emulation")
    print(f"[+] Input: {expr}")
    print_legacy_trace(trace, depth=1)
    print(f"[+] Result: {result}")


def describe_special_object(f: BufferedReader, regions, obj_ptr, type_id):
    if type_id == 13:
        code = summarize_text(
            maybe_read_cxstring_text(f, regions, obj_ptr + BEHAVIOUR_CODE_OFFSET)
        )
        cond_text = summarize_text(
            maybe_read_cxstring_text(
                f, regions, obj_ptr + BEHAVIOUR_CONDITION_TEXT_OFFSET
            )
        )
        cond_ptr = read_u32(f, regions, obj_ptr + BEHAVIOUR_CONDITION_PTR_OFFSET)
        parts = []
        if code:
            parts.append(f"code={code!r}")
        if cond_text:
            parts.append(f"cond={cond_text!r}")
        if cond_ptr:
            parts.append(f"cond_ptr=0x{cond_ptr:08X}")
        return ", ".join(parts)

    if type_id == 12:
        op1 = summarize_text(
            maybe_read_cxstring_text(f, regions, obj_ptr + EXPRESSION_OPERAND1_OFFSET)
        )
        op2 = summarize_text(
            maybe_read_cxstring_text(f, regions, obj_ptr + EXPRESSION_OPERAND2_OFFSET)
        )
        helper = read_u32(f, regions, obj_ptr + EXPRESSION_HELPER_BEHAVIOUR_OFFSET)
        parts = []
        if op1:
            parts.append(f"op1={op1!r}")
        if op2:
            parts.append(f"op2={op2!r}")
        if helper:
            parts.append(f"helper=0x{helper:08X}")
        return ", ".join(parts)

    if type_id in (10, 11):
        op1 = summarize_text(
            maybe_read_cxstring_text(f, regions, obj_ptr + CONDITION_OPERAND1_OFFSET)
        )
        op2 = summarize_text(
            maybe_read_cxstring_text(f, regions, obj_ptr + CONDITION_OPERAND2_OFFSET)
        )
        op_text = summarize_text(
            maybe_read_cxstring_text(
                f, regions, obj_ptr + CONDITION_OPERATOR_TEXT_OFFSET
            )
        )
        op_id = read_u32(f, regions, obj_ptr + CONDITION_OPERATOR_ID_OFFSET)
        op_name = CONDITION_OP_NAMES.get(op_id, str(op_id))
        parts = []
        if op1:
            parts.append(f"op1={op1!r}")
        if op_text:
            parts.append(f"op={op_text!r}/{op_name}")
        else:
            parts.append(f"op={op_name}")
        if op2:
            parts.append(f"op2={op2!r}")
        return ", ".join(parts)

    return None


def read_object_info(f: BufferedReader, regions, obj_ptr):
    type_id = read_object_type_id(f, regions, obj_ptr)
    name = read_object_name(f, regions, obj_ptr)
    if name is None:
        name = "<unreadable>"
    return {
        "ptr": obj_ptr,
        "type_id": type_id,
        "type_name": type_name(type_id),
        "name": name,
        "detail": describe_special_object(f, regions, obj_ptr, type_id),
    }


def dump_container(f: BufferedReader, regions, container_ptr, label, visited=None, depth=0):
    if visited is None:
        visited = set()

    indent = "  " * depth
    if container_ptr in visited:
        print(f"{indent}[=] {label}: 0x{container_ptr:08X} (already visited)")
        return

    visited.add(container_ptr)
    info = read_object_info(f, regions, container_ptr)
    vector_ptr, object_count, object_ptrs = iter_object_pointers(f, regions, container_ptr)

    print(
        f"{indent}[+] {label}: 0x{container_ptr:08X} "
        f"{info['name']} [{info['type_name']}] "
        f"vector=0x{vector_ptr:08X} count={object_count}"
    )

    for index, obj_ptr in enumerate(object_ptrs):
        child = read_object_info(f, regions, obj_ptr)
        line = (
            f"{indent}    [{index:03d}] 0x{obj_ptr:08X} "
            f"{child['name']} [{child['type_name']}]"
        )
        if child["detail"]:
            line += f" :: {child['detail']}"
        print(line)

    for obj_ptr in object_ptrs:
        child = read_object_info(f, regions, obj_ptr)
        if child["type_id"] in CONTAINER_TYPES and obj_ptr not in visited:
            dump_container(
                f,
                regions,
                obj_ptr,
                f"{child['type_name']} child",
                visited=visited,
                depth=depth + 1,
            )


def read_ascii_cxstring(f: BufferedReader, regions, ptr):
    value = read_cxstring(f, regions, ptr)
    return value.data.decode("ascii", errors="replace")


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("idx_path", nargs="?")
    parser.add_argument(
        "--emulate-if",
        dest="emulate_if",
        help='emulate legacy @IF("...") condition parsing',
    )
    args = parser.parse_args()

    if args.idx_path:
        idx_path = args.idx_path
        if not os.path.isfile(idx_path):
            parser.error(f"Missing file: {idx_path}")

        print(f"[+] Loading index: {idx_path}")
        regions = load_index(idx_path)
        print(f"[+] Loaded regions: {len(regions)}")

        mem_guess = idx_path.rsplit(".", 1)[0] + ".mem"
        print(f"[i] Expected .mem file: {mem_guess}")

        try:
            with open(mem_guess, "rb") as f:
                print(f"[+] Opened dump: {mem_guess}")

                cwindow_ptr = get_cwindow(f, regions)
                print(f"[+] CWindow ptr: 0x{cwindow_ptr:08X}")

                app_pointer = read_u32(
                    f, regions, cwindow_ptr + CWINDOW_APPLICATION_IFC_OFFSET
                )
                mcapp_pointer = read_u32(
                    f, regions, app_pointer + CAPPLICATION_MCAPP_OFFSET
                )
                print(f"[+] CApplication ptr: 0x{app_pointer:08X}")
                print(f"[+] CMC_Application ptr: 0x{mcapp_pointer:08X}")

                current_scene_ptr = read_u32(
                    f, regions, mcapp_pointer + CMC_CURRENT_SCENE_OFFSET
                )
                print(f"[+] Current scene ptr: 0x{current_scene_ptr:08X}")
                if current_scene_ptr:
                    current_scene_name = read_object_name(f, regions, current_scene_ptr)
                    if current_scene_name is not None:
                        print(f"[+] Current scene name: {current_scene_name}")

                visited = set()
                dump_container(
                    f, regions, mcapp_pointer, "CMC_Application", visited=visited
                )

                if current_scene_ptr:
                    if current_scene_ptr in visited:
                        current_info = read_object_info(f, regions, current_scene_ptr)
                        print(
                            f"[=] Current scene already covered: "
                            f"0x{current_scene_ptr:08X} "
                            f"{current_info['name']} [{current_info['type_name']}]"
                        )
                    else:
                        dump_container(
                            f,
                            regions,
                            current_scene_ptr,
                            "Current scene",
                            visited=visited,
                        )

                print(f"[+] RootPath ptr: 0x{ROOT_PATH_PTR:08X}")
                print(f"[+] RootPath: {read_ascii_cxstring(f, regions, ROOT_PATH_PTR)}")

                print(f"[+] DanePath ptr: 0x{DANE_PATH_PTR:08X}")
                print(f"[+] DanePath: {read_ascii_cxstring(f, regions, DANE_PATH_PTR)}")

        except OSError as exc:
            print(f"Failed to open dump: {exc}")
    elif not args.emulate_if:
        parser.error("Provide dump.idx or use --emulate-if")

    if args.emulate_if:
        print_legacy_if_analysis(args.emulate_if)


if __name__ == "__main__":
    main()
