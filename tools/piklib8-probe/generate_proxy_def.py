#!/usr/bin/env python3
"""Generate the 240-export P8PROBE definition file from Czarodzieje.exe."""

from __future__ import annotations

import argparse
import hashlib
from pathlib import Path

from pe32 import PE32, PEFormatError


EXPECTED_EXE_SHA256 = "7157e4f0e6bceda25779413e13f01c43ddb63b9915803b7e4935931f0cd54acf"
ORIGINAL_DLL = "Piklib8.dll"
DOMODAL = "?domodal@CWindow@@QAE_NXZ"


def def_token(value: str) -> str:
    if any(character.isspace() for character in value) or '"' in value or "=" in value:
        raise ValueError(f"unsupported export name: {value!r}")
    return value


def generate(exe_path: Path, forward_domodal: bool = False) -> str:
    raw = exe_path.read_bytes()
    digest = hashlib.sha256(raw).hexdigest()
    if digest != EXPECTED_EXE_SHA256:
        raise PEFormatError(
            "unsupported Czarodzieje.exe: "
            f"SHA-256 is {digest}, expected {EXPECTED_EXE_SHA256}"
        )

    pe = PE32(raw)
    imports = pe.imports_for(ORIGINAL_DLL)
    if any(item.name is None for item in imports):
        raise PEFormatError("ordinal-only PIKLIB8 import is not supported")

    names = [item.name for item in imports if item.name is not None]
    if len(names) != 240:
        raise PEFormatError(f"expected 240 PIKLIB8 imports, found {len(names)}")
    if names.count(DOMODAL) != 1:
        raise PEFormatError(f"expected one {DOMODAL!r} import")

    lines = ["LIBRARY P8PROBE", "EXPORTS"]
    for name in names:
        if name == DOMODAL and not forward_domodal:
            # The wrapper is a real CWindow::domodal member marked dllexport,
            # so MSVC emits the exact decorated name without a fragile .def alias.
            continue
        lines.append(f"    {def_token(name)}={def_token('Piklib8.' + name)}")
    lines.append("")
    return "\n".join(lines)


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("exe", type=Path, help="unmodified supported Czarodzieje.exe")
    parser.add_argument("output", type=Path, help="output .def file")
    parser.add_argument(
        "--forward-domodal",
        action="store_true",
        help="generate a zero-code control proxy with all 240 exports forwarded",
    )
    args = parser.parse_args()

    try:
        content = generate(args.exe, forward_domodal=args.forward_domodal)
        args.output.parent.mkdir(parents=True, exist_ok=True)
        args.output.write_text(content, encoding="ascii", newline="\n")
    except (OSError, PEFormatError, ValueError) as exc:
        parser.error(str(exc))
    print(f"Generated {args.output}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
