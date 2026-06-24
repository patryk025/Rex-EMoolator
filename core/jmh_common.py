"""Shared helpers for the JMH post-processing scripts (jmh_to_md / jmh_to_benchmark_json).

Resolves PIKLIB8 test-vector files referenced by the CLZW2 benchmarks so the reports can show
input size, decoded size and the allocation overhead (bytes allocated per byte of output) - the
size-independent metric that makes the allocation-heavy decoder's cost comparable across vectors.
"""
from pathlib import Path

# Vectors live next to the assets tree; resolve relative to this file so cwd doesn't matter
# (the workflow runs `python core/jmh_*.py` from the repo root).
VECTORS_DIR = (
    Path(__file__).resolve().parent.parent
    / "assets" / "test-assets" / "compression" / "vectors" / "piklib8"
)


def vector_name(bench):
    """Return the `vector` @Param value for a benchmark, or None if it isn't vector-based."""
    params = bench.get("params") or {}
    return params.get("vector")


def vector_sizes(name):
    """(input_size, decoded_size) for a vector, or None when the .clzw2 file is missing.

    decoded_size is the uncompressed length stored little-endian in the first 4 bytes of the
    CLZW2 header (see CLZW2Compression.decompress); input_size is the encoded file's size on disk.
    """
    if not name:
        return None
    path = VECTORS_DIR / f"{name}.clzw2"
    if not path.exists():
        return None
    raw = path.read_bytes()
    decoded = int.from_bytes(raw[0:4], "little") if len(raw) >= 4 else None
    return len(raw), decoded


def human_bytes(n):
    """Format a byte count with binary (IEC) units, e.g. 1536 -> '1.50 KiB'."""
    if n is None:
        return "n/a"
    units = ["B", "KiB", "MiB", "GiB", "TiB"]
    value = float(n)
    i = 0
    while value >= 1024 and i < len(units) - 1:
        value /= 1024
        i += 1
    if i == 0:
        return f"{int(n)} B"
    return f"{value:.2f} {units[i]}"
