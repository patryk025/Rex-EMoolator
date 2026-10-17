# SND format — installer sound archive

`wavs/wav.snd` on the **Reksio i Kapitan Nemo** disc is never read by the
BlooMoo engine. It is an archive that the **installer** (`setup.exe`) unpacks
into individual `wavs/*.wav` files during installation. A game run straight
from the disc or an image therefore lacks those sounds — the emulator performs
the installer's step itself.

## Layout

The file is a sequence of records with no header and no table of contents.
Numbers are little-endian.

| Field | Type | Description |
|---|---|---|
| type | `uint32` | `0` — Ogg Vorbis, `1` — Speex |
| size | `uint32` | payload length in bytes |
| name length | `uint32` | name length **including** the terminating `\0` |
| name | `char[name length]` | file name without a directory, e.g. `Babc_W074.wav` |
| payload | `uint8[size]` | Ogg Vorbis (or Speex) stream |

The next record starts right after the previous payload. The known archive
holds 3274 records, all of type `0`.

## Unpacking

For each record the installer:

1. writes the payload to a temporary file,
2. decodes it to 16-bit PCM (`ov_read`: sample × 32768, round half to even,
   clamp to the `int16` range),
3. stores the result as `<archive directory>\<name>` in the DirectX SDK
   `CWaveFile` layout: `RIFF`/`WAVE`, `fmt ` (16-byte PCM), `fact` (4 bytes,
   value `0`), `data` — a 56-byte header.

A record with a repeated name overwrites the earlier file.

## In the emulator

`SndArchiveFileSystem` looks for `*.snd` files in the game root and its
immediate subdirectories and mounts them in the VFS **below** the game source —
files that exist (e.g. from an installed copy) take precedence. Records are
decoded lazily on first read (`OggVorbisDecoder` built on JOrbis, `WaveSaver`).

Limitations:

- Speex records are not exposed (no decoder; none occur in the known archive),
- JOrbis is not bit-exact with libvorbis — individual samples may differ by 1.

Analysis details: `docs/reverse-engineering/nemo-setup-wav-snd.md` in the repository.
