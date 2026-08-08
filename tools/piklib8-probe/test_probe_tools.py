from __future__ import annotations

import struct
import unittest
from pathlib import Path

import analyze_trace
from generate_proxy_def import DOMODAL, generate
from pe32 import PE32, PEFormatError


ORIGINAL_EXE = Path(
    r"D:\Program Files\AidemMedia\Reksio i Czarodzieje\Czarodzieje.exe"
)


class TraceFormatTest(unittest.TestCase):
    def test_synthetic_header_and_record(self) -> None:
        header = bytearray(analyze_trace.HEADER_SIZE)
        header[:8] = analyze_trace.MAGIC
        struct.pack_into(
            "<7I",
            header,
            8,
            analyze_trace.FORMAT_VERSION,
            analyze_trace.HEADER_SIZE,
            analyze_trace.RECORD_SIZE,
            10,
            1,
            0,
            7,
        )
        struct.pack_into("<3I", header, 36, 123, 456, 789)
        struct.pack_into("<q", header, 48, 10_000_000)

        record = struct.pack(
            "<lIIIqqI8II",
            1,
            analyze_trace.EVENT_DOMODAL,
            0,
            100,
            1_000,
            2_000,
            0x1234,
            0,
            0,
            1,
            1,
            0,
            0,
            0,
            0,
            0,
        )
        parsed_header = analyze_trace.parse_header(bytes(header))
        records, incomplete = analyze_trace.parse_records(
            bytes(header) + record, parsed_header
        )
        self.assertEqual(10_000_000, parsed_header.qpc_frequency)
        self.assertEqual(0, incomplete)
        self.assertEqual(1, len(records))
        self.assertEqual(1, records[0].args[3])

    def test_incomplete_record_is_ignored(self) -> None:
        header = bytearray(analyze_trace.HEADER_SIZE)
        header[:8] = analyze_trace.MAGIC
        struct.pack_into(
            "<7I",
            header,
            8,
            1,
            analyze_trace.HEADER_SIZE,
            analyze_trace.RECORD_SIZE,
            1,
            1,
            0,
            0,
        )
        parsed_header = analyze_trace.parse_header(bytes(header))
        records, incomplete = analyze_trace.parse_records(
            bytes(header) + bytes(analyze_trace.RECORD_SIZE), parsed_header
        )
        self.assertEqual([], records)
        self.assertEqual(1, incomplete)


@unittest.skipUnless(ORIGINAL_EXE.is_file(), "supported Czarodzieje.exe is unavailable")
class SupportedExecutableTest(unittest.TestCase):
    def test_import_shape_and_in_place_patch(self) -> None:
        original = ORIGINAL_EXE.read_bytes()
        pe = PE32(original)
        self.assertEqual((0, 0), pe.bound_import_directory)
        self.assertEqual(240, len(pe.imports_for("Piklib8.dll")))
        offset = pe.replace_import_dll("Piklib8.dll", "P8PROBE.dll")
        self.assertEqual(0xC46E, offset)
        self.assertEqual(len(original), len(pe.data))
        changed = [
            index for index, (before, after) in enumerate(zip(original, pe.data))
            if before != after
        ]
        self.assertEqual([0xC46F, 0xC470, 0xC471, 0xC472, 0xC474], changed)
        self.assertEqual(240, len(PE32(pe.data).imports_for("P8PROBE.dll")))
        with self.assertRaises(PEFormatError):
            PE32(pe.data).imports_for("Piklib8.dll")

    def test_definition_contains_239_forwarders(self) -> None:
        definition = generate(ORIGINAL_EXE)
        self.assertEqual(239, definition.count("=Piklib8."))
        self.assertNotIn(f"{DOMODAL}=", definition)
        self.assertNotIn('"', definition)

    def test_control_definition_forwards_domodal_too(self) -> None:
        definition = generate(ORIGINAL_EXE, forward_domodal=True)
        self.assertEqual(240, definition.count("=Piklib8."))
        self.assertIn(f"{DOMODAL}=Piklib8.{DOMODAL}", definition)
        self.assertNotIn('"', definition)


if __name__ == "__main__":
    unittest.main()
