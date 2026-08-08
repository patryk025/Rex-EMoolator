#!/usr/bin/env python3
"""Parse P8TRACE binary logs and report the original message/timing cadence."""

from __future__ import annotations

import argparse
import csv
import math
import statistics
import struct
from collections import Counter, defaultdict
from dataclasses import dataclass
from pathlib import Path
from typing import Iterable


MAGIC = b"P8TRACE1"
FORMAT_VERSION = 1
HEADER_SIZE = 256
RECORD_SIZE = 72

EVENT_DOMODAL = 1
EVENT_GETMESSAGE = 2
EVENT_SCENE = 3

EVENT_NAMES = {
    EVENT_DOMODAL: "DOMODAL",
    EVENT_GETMESSAGE: "GETMESSAGE",
    EVENT_SCENE: "SCENE",
}

WINDOW_MESSAGES = {
    0x0000: "WM_NULL",
    0x0001: "WM_CREATE",
    0x0002: "WM_DESTROY",
    0x0003: "WM_MOVE",
    0x0005: "WM_SIZE",
    0x0006: "WM_ACTIVATE",
    0x0007: "WM_SETFOCUS",
    0x0008: "WM_KILLFOCUS",
    0x000A: "WM_ENABLE",
    0x000B: "WM_SETREDRAW",
    0x000C: "WM_SETTEXT",
    0x000D: "WM_GETTEXT",
    0x000E: "WM_GETTEXTLENGTH",
    0x000F: "WM_PAINT",
    0x0010: "WM_CLOSE",
    0x0011: "WM_QUERYENDSESSION",
    0x0012: "WM_QUIT",
    0x0014: "WM_ERASEBKGND",
    0x0018: "WM_SHOWWINDOW",
    0x001C: "WM_ACTIVATEAPP",
    0x0020: "WM_SETCURSOR",
    0x0021: "WM_MOUSEACTIVATE",
    0x0024: "WM_GETMINMAXINFO",
    0x0046: "WM_WINDOWPOSCHANGING",
    0x0047: "WM_WINDOWPOSCHANGED",
    0x007B: "WM_CONTEXTMENU",
    0x007E: "WM_DISPLAYCHANGE",
    0x007F: "WM_GETICON",
    0x0081: "WM_NCCREATE",
    0x0082: "WM_NCDESTROY",
    0x0083: "WM_NCCALCSIZE",
    0x0084: "WM_NCHITTEST",
    0x0085: "WM_NCPAINT",
    0x0086: "WM_NCACTIVATE",
    0x00A0: "WM_NCMOUSEMOVE",
    0x0100: "WM_KEYDOWN",
    0x0101: "WM_KEYUP",
    0x0102: "WM_CHAR",
    0x0104: "WM_SYSKEYDOWN",
    0x0105: "WM_SYSKEYUP",
    0x0106: "WM_SYSCHAR",
    0x0111: "WM_COMMAND",
    0x0112: "WM_SYSCOMMAND",
    0x0113: "WM_TIMER",
    0x011F: "WM_MENUSELECT",
    0x0120: "WM_MENUCHAR",
    0x0121: "WM_ENTERIDLE",
    0x0200: "WM_MOUSEMOVE",
    0x0201: "WM_LBUTTONDOWN",
    0x0202: "WM_LBUTTONUP",
    0x0203: "WM_LBUTTONDBLCLK",
    0x0204: "WM_RBUTTONDOWN",
    0x0205: "WM_RBUTTONUP",
    0x0206: "WM_RBUTTONDBLCLK",
    0x0207: "WM_MBUTTONDOWN",
    0x0208: "WM_MBUTTONUP",
    0x0209: "WM_MBUTTONDBLCLK",
    0x020A: "WM_MOUSEWHEEL",
    0x0211: "WM_ENTERMENULOOP",
    0x0212: "WM_EXITMENULOOP",
    0x0215: "WM_CAPTURECHANGED",
    0x0216: "WM_MOVING",
    0x0231: "WM_ENTERSIZEMOVE",
    0x0232: "WM_EXITSIZEMOVE",
    0x02A0: "WM_NCMOUSEHOVER",
    0x02A1: "WM_MOUSEHOVER",
    0x02A2: "WM_NCMOUSELEAVE",
    0x02A3: "WM_MOUSELEAVE",
    0x031D: "WM_CLIPBOARDUPDATE",
}


@dataclass(frozen=True)
class Header:
    format_version: int
    header_size: int
    record_size: int
    capacity: int
    next_index: int
    dropped: int
    flags: int
    process_id: int
    main_thread_id: int
    start_tick: int
    qpc_frequency: int
    start_time: tuple[int, ...]
    exe_timestamp: int
    exe_image_size: int
    pik_timestamp: int
    pik_image_size: int
    trace_name: str


@dataclass(frozen=True)
class Record:
    sequence: int
    type: int
    flags: int
    tick: int
    qpc0: int
    qpc1: int
    object: int
    args: tuple[int, ...]
    raw: bytes


def signed32(value: int) -> int:
    return value if value < 0x80000000 else value - 0x100000000


def message_name(value: int) -> str:
    return WINDOW_MESSAGES.get(value, f"0x{value:04X}")


def parse_header(data: bytes) -> Header:
    if len(data) < HEADER_SIZE:
        raise ValueError("trace is smaller than its header")
    if data[:8] != MAGIC:
        raise ValueError(f"bad trace magic: {data[:8]!r}")
    values = struct.unpack_from("<7I", data, 8)
    format_version, header_size, record_size, capacity = values[:4]
    next_index, dropped, flags = values[4:]
    if format_version != FORMAT_VERSION:
        raise ValueError(f"unsupported trace format {format_version}")
    if header_size != HEADER_SIZE or record_size != RECORD_SIZE:
        raise ValueError(
            f"unsupported layout: header={header_size}, record={record_size}"
        )
    process_id, main_thread_id, start_tick = struct.unpack_from("<3I", data, 36)
    qpc_frequency = struct.unpack_from("<q", data, 48)[0]
    start_time = struct.unpack_from("<8H", data, 56)
    exe_timestamp, exe_size, pik_timestamp, pik_size = struct.unpack_from(
        "<4I", data, 72
    )
    trace_name = data[128:256].split(b"\0", 1)[0].decode("ascii", "replace")
    return Header(
        format_version,
        header_size,
        record_size,
        capacity,
        next_index,
        dropped,
        flags,
        process_id,
        main_thread_id,
        start_tick,
        qpc_frequency,
        start_time,
        exe_timestamp,
        exe_size,
        pik_timestamp,
        pik_size,
        trace_name,
    )


def parse_records(data: bytes, header: Header) -> tuple[list[Record], int]:
    available = max(0, (len(data) - header.header_size) // header.record_size)
    count = min(max(0, header.next_index), header.capacity, available)
    result: list[Record] = []
    incomplete = 0
    for index in range(count):
        offset = header.header_size + index * header.record_size
        raw = data[offset : offset + header.record_size]
        commit, event_type, flags, tick, qpc0, qpc1, object_value, *tail = (
            struct.unpack("<lIIIqqI8II", raw)
        )
        if commit != index + 1:
            incomplete += 1
            continue
        result.append(
            Record(
                commit,
                event_type,
                flags,
                tick,
                qpc0,
                qpc1,
                object_value,
                tuple(tail[:8]),
                raw,
            )
        )
    return result, incomplete


def percentile(sorted_values: list[float], percentage: float) -> float:
    if not sorted_values:
        return math.nan
    position = (len(sorted_values) - 1) * percentage
    lower = math.floor(position)
    upper = math.ceil(position)
    if lower == upper:
        return sorted_values[lower]
    fraction = position - lower
    return sorted_values[lower] * (1.0 - fraction) + sorted_values[upper] * fraction


def describe(values: Iterable[float]) -> str:
    ordered = sorted(values)
    if not ordered:
        return "brak danych"
    return (
        f"n={len(ordered)}, min={ordered[0]:.3f} ms, "
        f"mediana={statistics.median(ordered):.3f} ms, "
        f"średnia={statistics.fmean(ordered):.3f} ms, "
        f"p90={percentile(ordered, 0.90):.3f} ms, "
        f"p99={percentile(ordered, 0.99):.3f} ms, max={ordered[-1]:.3f} ms"
    )


def qpc_milliseconds(delta: int, frequency: int) -> float:
    if frequency <= 0:
        return math.nan
    return delta * 1000.0 / frequency


def scene_name(record: Record) -> str:
    name_bytes = record.raw[36:68].split(b"\0", 1)[0]
    if record.object == 0:
        return "<null>"
    if not name_bytes:
        return f"<scene@0x{record.object:08X}>"
    return name_bytes.decode("cp1250", "replace")


def write_csv(
    path: Path,
    records: list[Record],
    frequency: int,
    scenes: dict[int, str],
    messages: dict[int, int],
) -> None:
    fieldnames = [
        "sequence", "type", "flags", "tick", "qpc0", "qpc1", "duration_ms",
        "object", *[f"arg{i}" for i in range(8)], "description",
        "scene_before", "scene_after", "paired_message",
    ]
    with path.open("w", encoding="utf-8-sig", newline="") as stream:
        writer = csv.DictWriter(stream, fieldnames=fieldnames)
        writer.writeheader()
        for record in records:
            description = ""
            scene_before = ""
            scene_after = ""
            paired_message = ""
            if record.type == EVENT_GETMESSAGE:
                description = message_name(record.args[1])
            elif record.type == EVENT_SCENE:
                description = scene_name(record)
            elif record.type == EVENT_DOMODAL:
                scene_before = scenes.get(record.args[0], f"0x{record.args[0]:08X}")
                scene_after = scenes.get(record.args[1], f"0x{record.args[1]:08X}")
                message = messages.get(record.args[4])
                if message is not None:
                    paired_message = message_name(message)
            row = {
                "sequence": record.sequence,
                "type": EVENT_NAMES.get(record.type, str(record.type)),
                "flags": f"0x{record.flags:08X}",
                "tick": record.tick,
                "qpc0": record.qpc0,
                "qpc1": record.qpc1,
                "duration_ms": (
                    f"{qpc_milliseconds(record.qpc1 - record.qpc0, frequency):.6f}"
                    if record.qpc1 and record.qpc0 else ""
                ),
                "object": f"0x{record.object:08X}",
                "description": description,
                "scene_before": scene_before,
                "scene_after": scene_after,
                "paired_message": paired_message,
            }
            row.update({f"arg{i}": f"0x{value:08X}" for i, value in enumerate(record.args)})
            writer.writerow(row)


def report(path: Path, header: Header, records: list[Record], incomplete: int) -> None:
    domodal = [record for record in records if record.type == EVENT_DOMODAL]
    getmessage = [record for record in records if record.type == EVENT_GETMESSAGE]
    scene_records = [record for record in records if record.type == EVENT_SCENE]
    scenes = {record.object: scene_name(record) for record in scene_records}
    messages = {record.sequence: record.args[1] for record in getmessage}

    print(f"Ślad: {path}")
    print(
        f"Format {header.format_version}, PID={header.process_id}, "
        f"thread={header.main_thread_id}, QPC={header.qpc_frequency} Hz"
    )
    print(
        f"Rekordy zatwierdzone={len(records)}, niepełne={incomplete}, "
        f"dropped={header.dropped}, next_index={header.next_index}"
    )
    print(
        f"Zdarzenia: domodal={len(domodal)}, GetMessageA={len(getmessage)}, "
        f"scene={len(scene_records)}"
    )

    frequency = header.qpc_frequency
    domodal_duration = [
        qpc_milliseconds(item.qpc1 - item.qpc0, frequency)
        for item in domodal if item.qpc1 >= item.qpc0 and item.qpc0 != 0
    ]
    cadence = [
        qpc_milliseconds(current.qpc0 - previous.qpc0, frequency)
        for previous, current in zip(domodal, domodal[1:])
        if current.qpc0 >= previous.qpc0 and previous.qpc0 != 0
    ]
    blocking = [
        qpc_milliseconds(item.qpc1 - item.qpc0, frequency)
        for item in getmessage if item.qpc1 >= item.qpc0 and item.qpc0 != 0
    ]
    print(f"Czas CWindow::domodal: {describe(domodal_duration)}")
    print(f"Odstęp wejść domodal: {describe(cadence)}")
    print(f"Czas blokowania GetMessageA: {describe(blocking)}")

    paired = sum(1 for item in domodal if item.args[4] in messages)
    print(
        f"Powiązanie wiadomość -> domodal: {paired}/{len(domodal)} "
        f"({len(domodal) - paired} bez poprzedzającej zarejestrowanej wiadomości)"
    )

    print("\nHistogram wiadomości zwróconych przez GetMessageA:")
    histogram = Counter(item.args[1] for item in getmessage if signed32(item.args[0]) >= 0)
    total_messages = sum(histogram.values())
    for message, count in histogram.most_common():
        percentage = 100.0 * count / total_messages if total_messages else 0.0
        print(f"  {message_name(message):24s} {count:8d}  {percentage:7.3f}%")

    gtc_deltas = Counter(
        (current.tick - previous.tick) & 0xFFFFFFFF
        for previous, current in zip(domodal, domodal[1:])
    )
    print("\nNajczęstsze różnice entry GetTickCount między domodal:")
    for delta, count in gtc_deltas.most_common(16):
        print(f"  {delta:8d} ms  {count:8d}")

    print("\nZmiany scen:")
    if not scene_records:
        print("  brak")
    for item in scene_records:
        print(
            f"  seq={item.sequence:8d} qpc={item.qpc0:16d} "
            f"ptr=0x{item.object:08X} name={scene_name(item)}"
        )

    by_scene: dict[int, list[float]] = defaultdict(list)
    for item in domodal:
        if item.qpc1 >= item.qpc0 and item.qpc0 != 0:
            by_scene[item.args[0]].append(
                qpc_milliseconds(item.qpc1 - item.qpc0, frequency)
            )
    print("\nCzas domodal według sceny wejściowej:")
    for pointer, durations in sorted(by_scene.items(), key=lambda pair: len(pair[1]), reverse=True):
        label = scenes.get(pointer, f"<scene@0x{pointer:08X}>")
        print(f"  {label:32s} {describe(durations)}")


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("trace", type=Path)
    parser.add_argument("--csv", type=Path, help="also write every committed record as CSV")
    args = parser.parse_args()

    try:
        data = args.trace.read_bytes()
        header = parse_header(data)
        records, incomplete = parse_records(data, header)
        report(args.trace, header, records, incomplete)
        if args.csv:
            scenes = {
                item.object: scene_name(item)
                for item in records if item.type == EVENT_SCENE
            }
            messages = {
                item.sequence: item.args[1]
                for item in records if item.type == EVENT_GETMESSAGE
            }
            write_csv(args.csv, records, header.qpc_frequency, scenes, messages)
            print(f"\nCSV: {args.csv}")
    except (OSError, ValueError, struct.error) as exc:
        parser.error(str(exc))
    return 0


if __name__ == "__main__":
    raise SystemExit(main())

