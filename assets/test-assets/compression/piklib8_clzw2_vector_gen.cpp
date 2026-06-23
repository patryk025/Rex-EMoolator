// piklib8_clzw2_vector_gen.cpp  —  build as x86 (32-bit)
//
// Takes files from directory, compresses with native CLZWCompression2 from PIKLIB8.dll
// and writes ready CLZW2 block (LE32 raw_size, LE32 payload_size, payload LZO1X-1).
//
//   gen.exe <PIKLIB8.dll> <input_dir> <output_dir>
//
// Each input file -> <output_dir>/<name>.clzw2

#include <windows.h>

#include <cstring>
#include <filesystem>
#include <fstream>
#include <iostream>
#include <stdexcept>
#include <vector>

namespace fs = std::filesystem;

using Ctor       = void  (__thiscall*)(void*, char*, int);
using Dtor       = void  (__thiscall*)(void*);
using Compress   = char* (__thiscall*)(void*, int&);
using Decompress = char* (__thiscall*)(void*);

static constexpr int OBJ_SIZE = 32;   // size of CLZWCompression2 object
static constexpr int HEADER   = 8;    // LE32 raw_size + LE32 payload_size

// Export by name, if not available — by ordinal.
static FARPROC need(HMODULE dll, const char* name, WORD ordinal) {
    if (FARPROC p = GetProcAddress(dll, name)) return p;
    if (FARPROC p = GetProcAddress(dll, MAKEINTRESOURCEA(ordinal))) return p;
    throw std::runtime_error(std::string("brak eksportu: ") + name);
}

static std::vector<char> readFile(const fs::path& p) {
    std::ifstream f(p, std::ios::binary | std::ios::ate);
    if (!f) throw std::runtime_error("cannot open " + p.string());
    std::vector<char> buf(static_cast<size_t>(f.tellg()));
    f.seekg(0);
    if (!buf.empty()) f.read(buf.data(), static_cast<std::streamsize>(buf.size()));
    return buf;
}

static void writeFile(const fs::path& p, const char* data, size_t n) {
    std::ofstream f(p, std::ios::binary);
    if (!f) throw std::runtime_error("cannot write " + p.string());
    f.write(data, static_cast<std::streamsize>(n));
}

int main(int argc, char** argv) {
    if (argc < 4) {
        std::cerr << "usage: " << argv[0] << " <PIKLIB8.dll> <input_dir> <output_dir>\n";
        return 1;
    }
    const fs::path dllPath = argv[1], inDir = argv[2], outDir = argv[3];

    HMODULE dll = LoadLibraryA(dllPath.string().c_str());
    if (!dll) {
        std::cerr << "cannot load " << dllPath.string() << " (err " << GetLastError() << ")\n";
        return 2;
    }

    auto ctor       = reinterpret_cast<Ctor>      (need(dll, "??0CLZWCompression2@@QAE@PADH@Z",        112));
    auto dtor       = reinterpret_cast<Dtor>      (need(dll, "??1CLZWCompression2@@UAE@XZ",            551));
    auto compress   = reinterpret_cast<Compress>  (need(dll, "?compress@CLZWCompression2@@QAEPADAAH@Z", 2123));
    auto decompress = reinterpret_cast<Decompress>(need(dll, "?decompress@CLZWCompression2@@QAEPADXZ",  2182));

    fs::create_directories(outDir);

    int ok = 0, fail = 0;
    char dummy = 0;  // constructor won't accept nullptr for empty input

    for (const auto& entry : fs::directory_iterator(inDir)) {
        if (!entry.is_regular_file()) continue;
        const fs::path in = entry.path();
        const std::string fname = in.filename().string();
        try {
            std::vector<char> raw = readFile(in);
            char* rawPtr = raw.empty() ? &dummy : raw.data();

            alignas(16) unsigned char comp[OBJ_SIZE] = {};
            ctor(comp, rawPtr, static_cast<int>(raw.size()));

            int payload = 0;
            char* block = compress(comp, payload);
            if (!block || payload < 0) { dtor(comp); throw std::runtime_error("compress() failed"); }
            const int total = payload + HEADER;

            // round-trip with native decoder
            alignas(16) unsigned char dec[OBJ_SIZE] = {};
            ctor(dec, block, total);
            char* back = decompress(dec);
            const bool good = back && (raw.empty() || std::memcmp(raw.data(), back, raw.size()) == 0);

            if (good) {
                writeFile(outDir / (in.stem().string() + ".clzw2"), block, static_cast<size_t>(total));
                std::cout << "[OK]   " << fname << "  " << raw.size() << " -> " << total << "\n";
                ++ok;
            } else {
                std::cerr << "[FAIL] " << fname << ": round-trip doesn't match\n";
                ++fail;
            }

            dtor(dec);
            dtor(comp);
            // Buffers from compress()/decompress() intentionally leak —
            // this is a one-time generator, process terminates soon, and foreign heap is not our heap.
        } catch (const std::exception& ex) {
            std::cerr << "[FAIL] " << fname << ": " << ex.what() << "\n";
            ++fail;
        }
    }

    FreeLibrary(dll);
    std::cout << "\ndone: " << ok << " ok, " << fail << " fail -> " << outDir.string() << "\n";
    return fail ? 3 : 0;
}