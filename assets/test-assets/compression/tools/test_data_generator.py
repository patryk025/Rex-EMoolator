#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
CLZWCompression2 test-vector generator.
"""

import os
import random

ROOT_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
OUT_DIR = os.path.join(ROOT_DIR, "raw")
SEED = 19980915 # just random value

def synth_image(width: int, height: int, rng: random.Random) -> bytes:
    """Generate a pseudo-paletted image: horizontal bands of uniform color
    (flat regions), occasional noise, and sometimes a repeat of an entire
    row. Produces matches with distance ~width (M3/M4) and long RLE matches
    (distance 1).
    """
    rows = []
    prev = None
    for y in range(height):
        if prev is not None and rng.random() < 0.45:
            rows.append(prev)                       # exact row repeat
            continue
        row = bytearray()
        x = 0
        while x < width:
            run = min(width - x, rng.randint(1, 24))
            col = rng.randint(0, 15)                 # 16-color palette
            row.extend([col] * run)
            x += run
        if rng.random() < 0.15:                      # occasional noise
            for _ in range(rng.randint(1, 8)):
                row[rng.randrange(width)] = rng.randint(0, 255)
        prev = bytes(row)
        rows.append(prev)
    return b"".join(rows)


def build_corpus() -> list[tuple[str, bytes]]:
    rng = random.Random(SEED)
    c: list[tuple[str, bytes]] = []
    add = lambda n, b: c.append((n, b))

    # --- degenerate / edge cases ---
    add("empty", b"")
    add("byte_00", b"\x00")
    add("byte_ff", b"\xff")
    add("byte_41", b"A")
    add("two_bytes", b"\x00\x01")

    # --- short literals, first opcode (special stream start) ---
    # lengths of the first literal run around thresholds 4 / 17 / 18 / 19
    for L in (3, 4, 5, 16, 17, 18, 19, 20):
        add(f"first_literals_{L}", bytes(rng.randrange(256) for _ in range(L)))

    # --- incompressible (random) at run-length extension boundaries ---
    # run-length == 0 -> we read 0x00 bytes (after +255) until a non-zero
    for L in (18, 255, 256, 273, 274, 512, 600, 1000, 5000, 65536):
        add(f"random_{L}", bytes(rng.randrange(256) for _ in range(L)))

    # --- RLE / distance 1 (long matches, match-length extension) ---
    for L in (2, 3, 4, 5, 17, 18, 255, 256, 264, 512, 1000, 4096):
        add(f"rle00_{L}", b"\x00" * L)
    add("rle_ff_1000", b"\xff" * 1000)

    # --- small periods (distance 2..8): various M1/M2 opcodes + trailing-lit states
    for period in (2, 3, 4, 5, 7, 8):
        add(f"period_{period}", (bytes(range(period)) * (1200 // period)))

    # --- repeated rows/tiles: distance = width (M3/M4) ---
    for w in (16, 64, 320, 640):
        scan = bytes(rng.randrange(256) for _ in range(w))
        add(f"row_repeat_w{w}", scan * 32)

    # --- gradients (semi-compressible) ---
    add("gradient_ramp", bytes(i & 0xFF for i in range(4096)))
    add("gradient_tri", bytes(abs((i % 510) - 255) for i in range(4096)))

    # --- machine state: short-match + 0/1/2/3 trailing literals, alternating ---
    sm = bytearray()
    pat = b"QWERTY"
    for trail in (0, 1, 2, 3, 2, 1, 0, 3):
        sm += pat                       # can be matched (match)
        sm += bytes(rng.randrange(256) for _ in range(trail))  # trailing literals
    add("state_machine_mix", bytes(sm) * 40)

    # --- realistic graphics (main use-case) ---
    add("img_16x16", synth_image(16, 16, rng))
    add("img_64x64", synth_image(64, 64, rng))
    add("img_320x200", synth_image(320, 200, rng))
    add("img_640x480", synth_image(640, 480, rng))

    # --- mixture: literals + matches + incompressible tail (near-EOS) ---
    mixed = b"HEADER\x00\x00" + b"\xab\xcd" * 300 + bytes(rng.randrange(256) for _ in range(37))
    add("mixed_tail", mixed)

    # --- some fuzz variety (different seeds) ---
    for s in range(5):
        r = random.Random(SEED + 1 + s)
        L = r.randint(1, 3000)
        add(f"fuzz_{s}_{L}", bytes(r.randrange(256) for _ in range(L)))

    return c


def main() -> None:
    os.makedirs(OUT_DIR, exist_ok=True)
    corpus = build_corpus()

if __name__ == "__main__":
    main()
