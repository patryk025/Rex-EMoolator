# IMG format — images

The `.IMG` file holds a single hi-color bitmap with an optional alpha channel. It is the format of [`IMAGE`](../reference/IMAGE.md) objects and of scene backgrounds. All numbers are **little-endian**. The layout matches the `ImageLoader` parser.

## File structure

```mermaid
flowchart LR
    A["Signature PIK\0"] --> B[Header 36 B]
    B --> C["Colour data<br/>(image data size)"]
    C --> D["Alpha data<br/>(alpha data size, optional)"]
```

## Header

The signature `PIK\0` (4 bytes), followed by a 36-byte block:

| Offset | Field | Type | Description |
|---:|---|---|---|
| 0 | magic | `char[4]` | `50 49 4B 00` (`PIK\0`) |
| 4 | width | `uint32` | in pixels |
| 8 | height | `uint32` | in pixels |
| 12 | colour depth | `uint32` | usually `16` (RGB565) |
| 16 | image data size | `uint32` | length of the colour block |
| 20 | — | 4 B | unused / padding |
| 24 | compression type | `uint32` | see the table |
| 28 | alpha data size | `uint32` | `0` = no alpha channel |
| 32 | offset X | `uint32` | the image's start position |
| 36 | offset Y | `uint32` | the image's start position |

After the header: a colour data block (`image data size` bytes), and if `alpha data size > 0` — an alpha data block.

!!! note "The offset is the start position"
    Unlike in [animation](ANN.md), where offsets act per frame, in `IMG` the offset from the header becomes the image's **absolute start position** on the canvas (`ImageLoader` sets `posX`/`posY` from it). See [coordinates](../internals/rendering.md#coordinate-system-and-y-axis-flip).

## Compression types

| Value | Compression |
|---:|---|
| `0` | none |
| `2` | CLZW2 |
| `4` | none (treated as `0`) |
| `5` | JPEG / CLZW2 |

!!! tip "Quirk: `4` means `0`"
    `ImageLoader` normalises compression type `4` to `0` — so both mean uncompressed data. This is a code-level compatibility detail for the original's files.

## See also

- [`IMAGE`](../reference/IMAGE.md) — the scripting object based on `.IMG`.
- [ANN format](ANN.md) — animations use the same pixel encoding.
- [Compression](compression.md) — CLZW2 and pixel decoding RGB565/555.
- [Rendering](../internals/rendering.md) — what happens to a bitmap after decoding.
