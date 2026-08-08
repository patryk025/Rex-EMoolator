#!/usr/bin/env python3
"""Create a safe probe copy of Czarodzieje.exe.

Only the import-module string Piklib8.dll -> P8PROBE.dll is changed.  The
input is accepted only when its SHA-256 matches the analyzed executable.
"""

from __future__ import annotations

import argparse
import hashlib
from pathlib import Path

from pe32 import PE32, PEFormatError


EXPECTED_EXE_SHA256 = "7157e4f0e6bceda25779413e13f01c43ddb63b9915803b7e4935931f0cd54acf"
ORIGINAL_DLL = "Piklib8.dll"
PROBE_DLL = "P8PROBE.dll"


def default_output(source: Path) -> Path:
    return source.with_name(source.stem + ".probe" + source.suffix)


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("exe", type=Path, help="unmodified supported Czarodzieje.exe")
    parser.add_argument("output", type=Path, nargs="?", help="new probe EXE")
    args = parser.parse_args()
    output = args.output or default_output(args.exe)

    try:
        raw = args.exe.read_bytes()
        digest = hashlib.sha256(raw).hexdigest()
        if digest != EXPECTED_EXE_SHA256:
            raise PEFormatError(
                "refusing to patch an unknown executable: "
                f"SHA-256 is {digest}, expected {EXPECTED_EXE_SHA256}"
            )
        if args.exe.resolve() == output.resolve():
            raise PEFormatError("output must not replace the original executable")
        if output.exists():
            raise PEFormatError(f"output already exists: {output}")

        pe = PE32(raw)
        bound_rva, bound_size = pe.bound_import_directory
        if bound_rva != 0 or bound_size != 0:
            raise PEFormatError(
                "bound-import directory is non-empty; this executable is not the analyzed build"
            )
        patch_offset = pe.replace_import_dll(ORIGINAL_DLL, PROBE_DLL)

        verification = PE32(pe.data)
        verification.imports_for(PROBE_DLL)
        try:
            verification.imports_for(ORIGINAL_DLL)
        except PEFormatError:
            pass
        else:
            raise PEFormatError("the original import descriptor is still present")

        output.parent.mkdir(parents=True, exist_ok=True)
        with output.open("xb") as stream:
            stream.write(pe.data)
    except (OSError, PEFormatError) as exc:
        parser.error(str(exc))

    patched_digest = hashlib.sha256(output.read_bytes()).hexdigest()
    print(f"Created: {output}")
    print(f"Patched import-name file offset: 0x{patch_offset:08x}")
    print(f"Patched SHA-256: {patched_digest}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())

