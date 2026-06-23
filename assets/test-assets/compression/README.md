# CLZW2 test vectors

The corpus is split by responsibility:

- `raw/` contains the expected uncompressed data shared by all vector sets.
- `vectors/piklib8/` contains complete CLZW2 blocks produced by the native
  `PIKLIB8.dll` compressor. These are compatibility vectors for game data.
- `vectors/liblzo2/` contains headerless LZO1X-1 blocks produced by liblzo2.
  These are decoder regression vectors and exercise valid streams that the
  PIKLIB8 encoder does not necessarily emit.
- `tools/` contains corpus generators.

`CLZWCompression2Test` reconstructs the eight-byte CLZW2 header for liblzo2
vectors and compares both vector sets against the same files in `raw/`.

The native generator must be built as a 32-bit Windows executable. Run it from
the `tools/` directory:

```text
piklib8_clzw2_vector_gen.exe <PIKLIB8.dll> ../raw ../vectors/piklib8
```

It intentionally processes only files with the `.raw` extension.
