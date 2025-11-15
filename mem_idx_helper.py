#!/usr/bin/env python3
import struct
import sys
import os

MAGIC = 0x584D454D  # 'MEMX'
HEADER_FMT = "<IIII"
REGION_FMT = "<IIIIQ"
HEADER_SIZE = struct.calcsize(HEADER_FMT)
REGION_SIZE = struct.calcsize(REGION_FMT)

class RegionIdx:
    __slots__ = ("base", "size", "protect", "type", "file_offset")

    def __init__(self, base, size, protect, rtype, file_offset):
        self.base = base
        self.size = size
        self.protect = protect
        self.type = rtype
        self.file_offset = file_offset

    def __repr__(self):
        return (f"Region(base=0x{self.base:08X}, size=0x{self.size:X}, "
                f"protect=0x{self.protect:X}, type=0x{self.type:X}, "
                f"file_offset=0x{self.file_offset:X})")

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
    # TODO: maybe use binsearch, but for about 600 regions it's working fine
    for r in regions:
        if r.base <= va < r.base + r.size:
            return r
    return None

def parse_int(s):
    s = s.strip()
    return int(s, 0)

def main():
    if len(sys.argv) < 2:
        print("Użycie: python mem_idx_helper.py Sekai_xxx.idx")
        sys.exit(1)

    idx_path = sys.argv[1]
    if not os.path.isfile(idx_path):
        print(f"Brak pliku: {idx_path}")
        sys.exit(1)

    print(f"[+] Wczytywanie indeksu z: {idx_path}")
    regions = load_index(idx_path)
    print(f"[+] Załadowano {len(regions)} regionów")

    mem_guess = idx_path.rsplit(".", 1)[0] + ".mem"
    print(f"[i] Zakładany plik .mem: {mem_guess}")
    print()
    print("Tryb interaktywny. Komendy:")
    print("  va <addr>             - adres wirtualny → offset w .mem")
    print("  mr <base> <rva>       - base modułu + RVA → offset w .mem")
    print("  info                  - podstawowe info")
    print("  region <addr>         - pokaż tylko info o regionie")
    print("  q / quit / exit       - wyjście")
    print()
    print("Przykłady:")
    print("  va 0x14F80BD0")
    print("  mr 0x0FFA0000 0xD354    # vtable Sekai: base + RVA")
    print()

    while True:
        try:
            line = input("> ").strip()
        except (EOFError, KeyboardInterrupt):
            print()
            break

        if not line:
            continue

        parts = line.split()
        cmd = parts[0].lower()

        if cmd in ("q", "quit", "exit"):
            break

        if cmd == "info":
            total_size = sum(r.size for r in regions)
            print(f"Regionów: {len(regions)}, łączny rozmiar: 0x{total_size:X} ({total_size} bajtów)")
            continue

        if cmd == "va":
            if len(parts) != 2:
                print("Użycie: va <addr>")
                continue
            try:
                va = parse_int(parts[1])
            except ValueError as e:
                print(f"Błąd parsowania adresu: {e}")
                continue

            r = find_region_for_va(va, regions)
            if not r:
                print(f"VA 0x{va:08X} nie należy do żadnego regionu")
                continue

            offset = r.file_offset + (va - r.base)
            print(f"VA 0x{va:08X} -> region base=0x{r.base:08X}, size=0x{r.size:X}")
            print(f"              file_offset=0x{r.file_offset:X} -> offset w .mem = 0x{offset:X} ({offset} dec)")
            continue

        if cmd == "mr":
            if len(parts) != 3:
                print("Użycie: mr <base> <rva>")
                continue
            try:
                base = parse_int(parts[1])
                rva  = parse_int(parts[2])
            except ValueError as e:
                print(f"Błąd parsowania: {e}")
                continue

            va = base + rva
            r = find_region_for_va(va, regions)
            if not r:
                print(f"VA 0x{va:08X} (base+RVA) nie należy do żadnego regionu")
                continue

            offset = r.file_offset + (va - r.base)
            print(f"BASE 0x{base:08X} + RVA 0x{rva:X} = VA 0x{va:08X}")
            print(f"VA 0x{va:08X} -> region base=0x{r.base:08X}, size=0x{r.size:X}")
            print(f"              file_offset=0x{r.file_offset:X} -> offset w .mem = 0x{offset:X} ({offset} dec)")
            continue

        if cmd == "region":
            if len(parts) != 2:
                print("Użycie: region <addr>")
                continue
            try:
                va = parse_int(parts[1])
            except ValueError as e:
                print(f"Błąd parsowania adresu: {e}")
                continue
            r = find_region_for_va(va, regions)
            if not r:
                print(f"VA 0x{va:08X} nie należy do żadnego regionu")
                continue
            print(r)
            continue

        print("Nieznana komenda. Dostępne: va, mr, info, region, quit")


if __name__ == "__main__":
    main()
