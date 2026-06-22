from pathlib import Path
import random
import struct
import zlib
import json

OUT = Path(".")
OUT.mkdir(exist_ok=True)

def write(name, data):
    raw_path = OUT / f"{name}.raw"
    meta_path = OUT / f"{name}.json"

    raw_path.write_bytes(data)

    meta = {
        "name": name,
        "size": len(data),
        "crc32": f"{zlib.crc32(data) & 0xffffffff:08x}",
    }
    meta_path.write_text(json.dumps(meta, indent=2), encoding="utf-8")


write("empty", b"")
write("one_byte", b"\x42")
write("small_literals", b"ABCDEF")
write("zeros_4k", b"\x00" * 4096)
write("ff_4k", b"\xff" * 4096)
write("random_4k", bytes(random.randrange(256) for _ in range(4096)))

write("long_run_64k", b"\x7f" * 65536)
write("repeated_phrase", (b"REKSIO_KRETES_BLOOMOO_" * 2048))
data_with_noise = bytearray(b"REKSIO_KRETES_BLOOMOO_" * 2048)

for i in range(0, len(data_with_noise), 256):
    data_with_noise[i] ^= 0x55

write("almost_repeated_with_noise", data_with_noise)

# RGB565 gradient
w, h = 128, 128
rgb565 = bytearray()
for y in range(h):
    for x in range(w):
        r = (x * 31) // (w - 1)
        g = (y * 63) // (h - 1)
        b = ((x + y) * 31) // (w + h - 2)
        px = (r << 11) | (g << 5) | b
        rgb565 += struct.pack("<H", px)
write("gradient_rgb565", rgb565)

# Checkerboard RGB565
checker = bytearray()
for y in range(h):
    for x in range(w):
        px = 0xffff if ((x // 8) ^ (y // 8)) & 1 else 0x0000
        checker += struct.pack("<H", px)
write("checkerboard_rgb565", checker)

# Tilemap-like data
tile = bytes([0, 1, 2, 3, 3, 2, 1, 0] * 8)
tilemap = tile * 1024
write("tilemap_repeating_8x8", tilemap)

print("Generated raw test corpus.")