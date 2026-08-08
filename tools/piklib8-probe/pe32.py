"""Small, dependency-free PE32 reader used by the PIKLIB8 probe tools.

It intentionally implements only the parts needed here: PE32 headers, data
directories, sections and named imports.  Keeping the patcher independent of
third-party modules makes it usable on a clean Windows installation.
"""

from __future__ import annotations

import struct
from dataclasses import dataclass
from pathlib import Path
from typing import Iterator


class PEFormatError(ValueError):
    pass


@dataclass(frozen=True)
class Section:
    name: str
    virtual_size: int
    virtual_address: int
    raw_size: int
    raw_offset: int


@dataclass(frozen=True)
class ImportDescriptor:
    descriptor_offset: int
    original_first_thunk: int
    name_rva: int
    name_offset: int
    first_thunk: int
    dll: str


@dataclass(frozen=True)
class ImportEntry:
    name: str | None
    ordinal: int | None
    hint: int | None
    thunk_rva: int
    iat_rva: int


@dataclass(frozen=True)
class ExportEntry:
    name: str
    ordinal: int
    target_rva: int
    forwarder: str | None


class PE32:
    DIRECTORY_EXPORT = 0
    DIRECTORY_IMPORT = 1
    DIRECTORY_BOUND_IMPORT = 11

    def __init__(self, data: bytes | bytearray):
        self.data = bytearray(data)
        self._parse_headers()

    @classmethod
    def from_path(cls, path: str | Path) -> "PE32":
        return cls(Path(path).read_bytes())

    def _require(self, offset: int, size: int, description: str) -> None:
        if offset < 0 or size < 0 or offset + size > len(self.data):
            raise PEFormatError(
                f"{description} is outside the file: offset=0x{offset:x}, size=0x{size:x}"
            )

    def _u16(self, offset: int) -> int:
        self._require(offset, 2, "16-bit field")
        return struct.unpack_from("<H", self.data, offset)[0]

    def _u32(self, offset: int) -> int:
        self._require(offset, 4, "32-bit field")
        return struct.unpack_from("<I", self.data, offset)[0]

    def _parse_headers(self) -> None:
        self._require(0, 0x40, "DOS header")
        if self.data[0:2] != b"MZ":
            raise PEFormatError("missing MZ signature")

        self.pe_offset = self._u32(0x3C)
        self._require(self.pe_offset, 24, "PE header")
        if self.data[self.pe_offset : self.pe_offset + 4] != b"PE\0\0":
            raise PEFormatError("missing PE signature")

        file_header = self.pe_offset + 4
        self.machine = self._u16(file_header)
        if self.machine != 0x014C:
            raise PEFormatError(f"expected x86 PE (0x14c), got 0x{self.machine:x}")
        self.number_of_sections = self._u16(file_header + 2)
        self.timestamp = self._u32(file_header + 4)
        self.optional_header_size = self._u16(file_header + 16)

        self.optional_header_offset = file_header + 20
        self._require(
            self.optional_header_offset,
            self.optional_header_size,
            "optional header",
        )
        if self._u16(self.optional_header_offset) != 0x010B:
            raise PEFormatError("expected PE32 optional header")

        opt = self.optional_header_offset
        self.image_base = self._u32(opt + 28)
        self.os_version = (self._u16(opt + 40), self._u16(opt + 42))
        self.size_of_image = self._u32(opt + 56)
        self.size_of_headers = self._u32(opt + 60)
        self.subsystem = self._u16(opt + 68)
        self.subsystem_version = (self._u16(opt + 48), self._u16(opt + 50))
        self.number_of_directories = self._u32(opt + 92)
        self.directory_offset = opt + 96

        max_directories = max(0, (self.optional_header_size - 96) // 8)
        if self.number_of_directories > max_directories:
            raise PEFormatError("data-directory count exceeds optional header")

        section_offset = opt + self.optional_header_size
        self.sections: list[Section] = []
        for index in range(self.number_of_sections):
            offset = section_offset + index * 40
            self._require(offset, 40, "section header")
            raw_name = bytes(self.data[offset : offset + 8]).split(b"\0", 1)[0]
            self.sections.append(
                Section(
                    raw_name.decode("ascii", "replace"),
                    self._u32(offset + 8),
                    self._u32(offset + 12),
                    self._u32(offset + 16),
                    self._u32(offset + 20),
                )
            )

    def directory(self, index: int) -> tuple[int, int]:
        if index < 0 or index >= self.number_of_directories:
            return 0, 0
        offset = self.directory_offset + index * 8
        return self._u32(offset), self._u32(offset + 4)

    @property
    def bound_import_directory(self) -> tuple[int, int]:
        return self.directory(self.DIRECTORY_BOUND_IMPORT)

    def rva_to_offset(self, rva: int) -> int:
        if rva < self.size_of_headers:
            self._require(rva, 1, "header RVA")
            return rva

        for section in self.sections:
            span = max(section.virtual_size, section.raw_size)
            if section.virtual_address <= rva < section.virtual_address + span:
                delta = rva - section.virtual_address
                if delta >= section.raw_size:
                    raise PEFormatError(
                        f"RVA 0x{rva:x} is in the zero-filled tail of {section.name}"
                    )
                offset = section.raw_offset + delta
                self._require(offset, 1, f"RVA 0x{rva:x}")
                return offset
        raise PEFormatError(f"RVA 0x{rva:x} does not belong to a section")

    def read_c_string(self, offset: int, limit: int = 4096) -> str:
        self._require(offset, 1, "string")
        end_limit = min(len(self.data), offset + limit)
        end = self.data.find(0, offset, end_limit)
        if end < 0:
            raise PEFormatError(f"unterminated string at file offset 0x{offset:x}")
        return bytes(self.data[offset:end]).decode("ascii", "strict")

    def import_descriptors(self) -> Iterator[ImportDescriptor]:
        import_rva, import_size = self.directory(self.DIRECTORY_IMPORT)
        if import_rva == 0 or import_size == 0:
            return

        offset = self.rva_to_offset(import_rva)
        max_descriptors = import_size // 20
        for index in range(max_descriptors):
            descriptor_offset = offset + index * 20
            self._require(descriptor_offset, 20, "import descriptor")
            fields = struct.unpack_from("<IIIII", self.data, descriptor_offset)
            if fields == (0, 0, 0, 0, 0):
                return
            original_first_thunk, _time, _chain, name_rva, first_thunk = fields
            if name_rva == 0 or first_thunk == 0:
                raise PEFormatError("malformed import descriptor")
            name_offset = self.rva_to_offset(name_rva)
            yield ImportDescriptor(
                descriptor_offset,
                original_first_thunk,
                name_rva,
                name_offset,
                first_thunk,
                self.read_c_string(name_offset),
            )
        raise PEFormatError("import descriptor table has no terminator")

    def _imports_for_descriptor(self, descriptor: ImportDescriptor) -> list[ImportEntry]:
        lookup_rva = descriptor.original_first_thunk or descriptor.first_thunk
        lookup_offset = self.rva_to_offset(lookup_rva)

        result: list[ImportEntry] = []
        for index in range(65536):
            thunk_offset = lookup_offset + index * 4
            value = self._u32(thunk_offset)
            if value == 0:
                return result
            thunk_rva = lookup_rva + index * 4
            iat_rva = descriptor.first_thunk + index * 4
            if value & 0x80000000:
                result.append(ImportEntry(None, value & 0xFFFF, None, thunk_rva, iat_rva))
            else:
                name_offset = self.rva_to_offset(value)
                hint = self._u16(name_offset)
                name = self.read_c_string(name_offset + 2)
                result.append(ImportEntry(name, None, hint, thunk_rva, iat_rva))
        raise PEFormatError("import thunk table has no terminator")

    def imports_for(self, dll_name: str) -> list[ImportEntry]:
        matches = [
            item
            for item in self.import_descriptors()
            if item.dll.casefold() == dll_name.casefold()
        ]
        if len(matches) != 1:
            raise PEFormatError(
                f"expected exactly one import descriptor for {dll_name!r}, found {len(matches)}"
            )
        return self._imports_for_descriptor(matches[0])

    def all_imports(self) -> dict[str, list[ImportEntry]]:
        return {
            descriptor.dll: self._imports_for_descriptor(descriptor)
            for descriptor in self.import_descriptors()
        }

    def exports(self) -> list[ExportEntry]:
        export_rva, export_size = self.directory(self.DIRECTORY_EXPORT)
        if export_rva == 0 or export_size < 40:
            return []
        offset = self.rva_to_offset(export_rva)
        self._require(offset, 40, "export directory")
        fields = struct.unpack_from("<IIHHIIIIIII", self.data, offset)
        ordinal_base = fields[5]
        function_count = fields[6]
        name_count = fields[7]
        functions_rva = fields[8]
        names_rva = fields[9]
        ordinals_rva = fields[10]
        if name_count > function_count or function_count > 1_000_000:
            raise PEFormatError("malformed export-directory counts")

        functions_offset = self.rva_to_offset(functions_rva)
        names_offset = self.rva_to_offset(names_rva)
        ordinals_offset = self.rva_to_offset(ordinals_rva)
        self._require(functions_offset, function_count * 4, "export address table")
        self._require(names_offset, name_count * 4, "export name table")
        self._require(ordinals_offset, name_count * 2, "export ordinal table")

        result: list[ExportEntry] = []
        for index in range(name_count):
            name_rva = self._u32(names_offset + index * 4)
            ordinal_index = self._u16(ordinals_offset + index * 2)
            if ordinal_index >= function_count:
                raise PEFormatError("export ordinal index is out of range")
            target_rva = self._u32(functions_offset + ordinal_index * 4)
            name = self.read_c_string(self.rva_to_offset(name_rva))
            forwarder = None
            if export_rva <= target_rva < export_rva + export_size:
                forwarder = self.read_c_string(self.rva_to_offset(target_rva))
            result.append(
                ExportEntry(name, ordinal_base + ordinal_index, target_rva, forwarder)
            )
        return result

    def replace_import_dll(self, old_name: str, new_name: str) -> int:
        matches = [
            item
            for item in self.import_descriptors()
            if item.dll.casefold() == old_name.casefold()
        ]
        if len(matches) != 1:
            raise PEFormatError(
                f"expected exactly one import descriptor for {old_name!r}, found {len(matches)}"
            )
        descriptor = matches[0]
        old_bytes = descriptor.dll.encode("ascii")
        new_bytes = new_name.encode("ascii")
        if len(old_bytes) != len(new_bytes):
            raise PEFormatError(
                f"replacement must have the same length ({len(old_bytes)} bytes)"
            )
        start = descriptor.name_offset
        if bytes(self.data[start : start + len(old_bytes)]) != old_bytes:
            raise PEFormatError("import name changed while parsing")
        self.data[start : start + len(new_bytes)] = new_bytes
        return start
