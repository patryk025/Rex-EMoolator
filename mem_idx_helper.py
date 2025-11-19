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

def find_region_for_offset(offset, regions):
    # Find region that contains the given file offset
    for r in regions:
        if r.file_offset <= offset < r.file_offset + r.size:
            return r
    return None

def parse_int(s):
    s = s.strip()
    return int(s, 0)

def diff_dumps(regions1, regions2, memdump1, memdump2):
    # Create maps for quick lookup
    regions1_map = {r.base: r for r in regions1}
    regions2_map = {r.base: r for r in regions2}

    # Find region differences
    bases1 = set(regions1_map.keys())
    bases2 = set(regions2_map.keys())

    only_in_1 = bases1 - bases2
    only_in_2 = bases2 - bases1
    common = bases1 & bases2

    print(f"[*] Analiza struktury regionów:")
    print(f"    Tylko w zrzucie 1: {len(only_in_1)} regionów")
    print(f"    Tylko w zrzucie 2: {len(only_in_2)} regionów")
    print(f"    Wspólnych regionów: {len(common)}")
    print()

    if only_in_1:
        print("[+] Regiony usunięte (tylko w zrzucie 1):")
        for base in sorted(only_in_1)[:10]:  # Show max 10
            r = regions1_map[base]
            print(f"    0x{r.base:08X} - 0x{r.base + r.size:08X} (size=0x{r.size:X})")
        if len(only_in_1) > 10:
            print(f"    ... i {len(only_in_1) - 10} więcej")
        print()

    if only_in_2:
        print("[+] Regiony dodane (tylko w zrzucie 2):")
        for base in sorted(only_in_2)[:10]:
            r = regions2_map[base]
            print(f"    0x{r.base:08X} - 0x{r.base + r.size:08X} (size=0x{r.size:X})")
        if len(only_in_2) > 10:
            print(f"    ... i {len(only_in_2) - 10} więcej")
        print()

    # Continue only if there are common regions
    if not common:
        print("[!] Brak wspólnych regionów do porównania")
        return

    # Check if memory dump files exist
    if not os.path.isfile(memdump1):
        print(f"[!] Brak pliku zrzutu pamięci: {memdump1}")
        return
    if not os.path.isfile(memdump2):
        print(f"[!] Brak pliku zrzutu pamięci: {memdump2}")
        return

    print("[*] Porównywanie zawartości wspólnych regionów...")

    changed_regions = []

    with open(memdump1, "rb") as f1, open(memdump2, "rb") as f2:
        for base in sorted(common):
            r1 = regions1_map[base]
            r2 = regions2_map[base]

            # Check region size
            if r1.size != r2.size:
                changed_regions.append({
                    'base': base,
                    'type': 'size_changed',
                    'old_size': r1.size,
                    'new_size': r2.size
                })
                continue

            # Read region data from both dumps
            f1.seek(r1.file_offset)
            data1 = f1.read(r1.size)

            f2.seek(r2.file_offset)
            data2 = f2.read(r2.size)

            if len(data1) != r1.size or len(data2) != r2.size:
                print(f"[!] Błąd odczytu dla regionu 0x{base:08X}")
                continue

            # Compare byte by byte
            if data1 != data2:
                # Find differing byte ranges
                diffs = []
                diff_start = None

                for i in range(len(data1)):
                    if data1[i] != data2[i]:
                        if diff_start is None:
                            diff_start = i
                    else:
                        if diff_start is not None:
                            diffs.append((diff_start, i - 1))
                            diff_start = None

                # Handle case where difference goes to the end
                if diff_start is not None:
                    diffs.append((diff_start, len(data1) - 1))

                changed_regions.append({
                    'base': base,
                    'type': 'content_changed',
                    'size': r1.size,
                    'diffs': diffs,
                    'total_changed': sum(end - start + 1 for start, end in diffs)
                })

    if not changed_regions:
        print("[+] Brak różnic w zawartości wspólnych regionów!")
        return

    print(f"[+] Znaleziono {len(changed_regions)} zmienionych regionów:")
    print()

    for change in changed_regions[:20]:  # Show max 20
        base = change['base']
        if change['type'] == 'size_changed':
            print(f"  Region 0x{base:08X}:")
            print(f"    Zmiana rozmiaru: 0x{change['old_size']:X} -> 0x{change['new_size']:X}")
        else:
            print(f"  Region 0x{base:08X} (size=0x{change['size']:X}):")
            print(f"    Zmienione bajty: {change['total_changed']} / {change['size']} ({100.0 * change['total_changed'] / change['size']:.2f}%)")
            print(f"    Zakresów różnic: {len(change['diffs'])}")

            # Show first 5 diff ranges
            for start, end in change['diffs'][:5]:
                va_start = base + start
                va_end = base + end
                print(f"      VA 0x{va_start:08X} - 0x{va_end:08X} (offset +0x{start:X}, {end - start + 1} bajtów)")

            if len(change['diffs']) > 5:
                print(f"      ... i {len(change['diffs']) - 5} więcej zakresów")
        print()

    if len(changed_regions) > 20:
        print(f"... i {len(changed_regions) - 20} więcej zmienionych regionów")
        print()

    total_bytes_changed = sum(c.get('total_changed', 0) for c in changed_regions if c['type'] == 'content_changed')
    print(f"[=] Podsumowanie: łącznie ~{total_bytes_changed} bajtów zmienionych")

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
    print("  offset <offset>       - offset w .mem → adres wirtualny")
    print("  mr <base> <rva>       - base modułu + RVA → offset w .mem")
    print("  info                  - podstawowe info")
    print("  region <addr>         - pokaż tylko info o regionie")
    print("  diff <idx_path>       - pokaż różnice pomiędzy dwoma zrzutami pamięci")
    print("  q / quit / exit       - wyjście")
    print()
    print("Przykłady:")
    print("  va 0x14F80BD0")
    print("  offset 0x1A3F4B0       # offset w .mem → VA")
    print("  mr 0x0FFA0000 0xD354   # vtable Sekai: base + RVA")
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

        if cmd == "offset" or cmd == "off":
            if len(parts) != 2:
                print("Użycie: offset <offset>")
                continue
            try:
                offset = parse_int(parts[1])
            except ValueError as e:
                print(f"Błąd parsowania offsetu: {e}")
                continue

            r = find_region_for_offset(offset, regions)
            if not r:
                print(f"Offset 0x{offset:X} nie należy do żadnego regionu")
                continue

            va = r.base + (offset - r.file_offset)
            rva = va - r.base
            print(f"Offset w .mem 0x{offset:X} ({offset} dec)")
            print(f"  -> region base=0x{r.base:08X}, size=0x{r.size:X}")
            print(f"  -> VA = 0x{va:08X}")
            print(f"  -> RVA (offset w regionie) = 0x{rva:X}")
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

        if cmd == "diff":
            if len(parts) != 2:
                print("Użycie: diff <idx_path>")
                continue

            try:
                other_regions = load_index(parts[1])
            except RuntimeError as e:
                print(f"Błąd wczytywania indexu: {e}")
                continue
            
            other_mem_guessed = parts[1].rsplit(".", 1)[0] + ".mem"

            print(f"Analiza różnic między zrzutami")
            print(f"  {idx_path} -> {parts[1]}")
            print(f"  {mem_guess} -> {other_mem_guessed}")
            print()

            diff_dumps(regions, other_regions, mem_guess, other_mem_guessed)
            continue

        print("Nieznana komenda. Dostępne: va, offset, mr, info, region, diff, quit")


if __name__ == "__main__":
    main()
