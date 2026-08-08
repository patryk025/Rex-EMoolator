#!/usr/bin/env python3
"""Verify that P8PROBE.dll is a CRT-free 32-bit proxy for the supported EXE."""

from __future__ import annotations

import argparse
from pathlib import Path

from generate_proxy_def import DOMODAL, ORIGINAL_DLL
from pe32 import PE32, PEFormatError


EXPECTED_PROXY_IMAGE_BASE = 0x68000000


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("exe", type=Path, help="supported unmodified Czarodzieje.exe")
    parser.add_argument("dll", type=Path, help="built P8PROBE.dll")
    parser.add_argument(
        "--forward-domodal",
        action="store_true",
        help="expect all 240 exports, including domodal, to be forwarders",
    )
    args = parser.parse_args()

    try:
        expected = {
            item.name for item in PE32.from_path(args.exe).imports_for(ORIGINAL_DLL)
            if item.name is not None
        }
        proxy = PE32.from_path(args.dll)
        if proxy.image_base != EXPECTED_PROXY_IMAGE_BASE:
            raise PEFormatError(
                "unsafe proxy image base: "
                f"0x{proxy.image_base:08x}, expected 0x{EXPECTED_PROXY_IMAGE_BASE:08x}; "
                "0x10000000 must remain available for Piklib8.dll"
            )
        exports = proxy.exports()
        actual = {item.name for item in exports}
        if len(exports) != 240 or actual != expected:
            missing = sorted(expected - actual)
            extra = sorted(actual - expected)
            raise PEFormatError(
                f"proxy export mismatch: count={len(exports)}, missing={missing}, extra={extra}"
            )

        by_name = {item.name: item for item in exports}
        if args.forward_domodal:
            if by_name[DOMODAL].forwarder != f"Piklib8.{DOMODAL}":
                raise PEFormatError("domodal is not forwarded to the original DLL")
        elif by_name[DOMODAL].forwarder is not None:
            raise PEFormatError("domodal is still a forwarder")
        wrong_forwarders = [
            item.name
            for item in exports
            if (args.forward_domodal or item.name != DOMODAL) and
            item.forwarder != f"Piklib8.{item.name}"
        ]
        if wrong_forwarders:
            raise PEFormatError(f"invalid forwarders: {wrong_forwarders[:10]}")

        imports = proxy.all_imports()
        unexpected_dlls = [
            name for name in imports if name.casefold() != "kernel32.dll"
        ]
        if unexpected_dlls:
            raise PEFormatError(f"unexpected DLL imports: {unexpected_dlls}")
        if proxy.subsystem_version > (5, 1):
            raise PEFormatError(
                f"subsystem version is too new: {proxy.subsystem_version[0]}.{proxy.subsystem_version[1]:02d}"
            )
        imported_names = sorted(
            item.name or f"#{item.ordinal}"
            for entries in imports.values()
            for item in entries
        )
    except (OSError, PEFormatError) as exc:
        parser.error(str(exc))

    print(f"Verified {args.dll}")
    print(f"  PE32 image size: 0x{proxy.size_of_image:x}")
    print(f"  preferred image base: 0x{proxy.image_base:08x}")
    print(
        f"  subsystem: {proxy.subsystem}, version "
        f"{proxy.subsystem_version[0]}.{proxy.subsystem_version[1]:02d}"
    )
    if args.forward_domodal:
        print(f"  named exports: {len(exports)} (240 forwarders; zero-code control)")
    else:
        print(f"  named exports: {len(exports)} (239 forwarders + domodal wrapper)")
    print(f"  imported DLLs: {', '.join(imports)}")
    print(f"  imported APIs: {', '.join(imported_names)}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
