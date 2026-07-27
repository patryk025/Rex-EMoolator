# FNT format — fonts

An `.FNT` file stores a Piklib bitmap font: character identifiers, metrics, a
pair-adjustment matrix, and one shared color/alpha atlas. It is used by
[`FONT`](../reference/FONT.md) and [`TEXT`](../reference/TEXT.md) objects.
All multi-byte numbers are little-endian.

## File layout

|      Section Name      |  Section Length  |
|:----------------------:|:----------------:|
|         header         |       20 B       |
| character identifiers  |       N B        |
| pair-adjustment matrix |      N*N B       |
|       left trims       |       N B        |
|      right trims       |       N B        |
|  RGB555/RGB565 atlas   |     W*H*2 B      |
|      alpha atlas       |      W*H B       |

The exact file size is:

```text
20 + N*N + 3*N + 3*W*H
```

## Header

|  Offset | Field         | Type      | Meaning                        |
|--------:|---------------|-----------|--------------------------------|
|  `0x00` | `magic`       | `char[4]` | `46 4E 54 00`, or `FNT\0`      |
|  `0x04` | `atlasWidth`  | `uint32`  | full atlas width `W`           |
|  `0x08` | `atlasHeight` | `uint32`  | atlas and glyph-row height `H` |
|  `0x0C` | `pixelFormat` | `uint32`  | `15` = RGB555, `16` = RGB565   |
|  `0x10` | `glyphCount`  | `uint32`  | glyph count `N`                |

The uniform atlas cell width is calculated as:

```text
cellWidth = atlasWidth / glyphCount
```

The atlas width must be divisible by the glyph count.

## Characters and encoding

Each glyph has a one-byte identifier. FNT does not record an encoding name;
the encoding must match the game's text encoding. `arial14.fnt` use Windows-1250.

The renderer has several special cases:

- space is not drawn from the atlas and uses the width of lowercase `l`;
- `~` is not drawn and has a width of 1 px;
- NUL terminates the text;
- an unknown character is not drawn, but the text loop still adds 2 px.

## Trims and glyph regions

Left and right trims are two separate `N`-byte blocks:

```text
cellStart(i) = i * cellWidth
sourceX(i)   = cellStart(i) + leftTrim[i]
inkWidth(i)  = cellWidth - leftTrim[i] - rightTrim[i]
```

The renderer extracts the atlas region at `sourceX` with width `inkWidth`.

## Pair-adjustment matrix

The matrix contains `N*N` **signed int8** values in row-major order:

```text
K(previous, current) = matrix[previousIndex * N + currentIndex]
```

For the current glyph:

```text
drawX   = penX - K(previous, current)
advance = inkWidth(current) - K(previous, current) + 2
```

A positive value moves the glyph left and shortens the advance; a negative
value moves it right and increases the advance. `K=0` is used for the first
character or an unknown previous character.

The generator in `Piklib8.dll` is defective and produces almost exclusively
`+1`; all `118*118` entries in `arial14.fnt` have that value. The newer
`BlooMooDLL.dll` fixes the pair-dependent calculation without changing the
file format.

## Color and alpha

The color atlas stores one `uint16` per pixel:

- `pixelFormat=15`: RGB555;
- `pixelFormat=16`: RGB565.

It is followed by an eight-bit alpha atlas with the same dimensions. `0` is
transparent, `255` is opaque, and intermediate values are blended. Both
planes are stored row by row for the entire atlas.

The stored RGB plane is not necessarily the color seen on screen.
`CSimpleFont6` and `CText6` default to `0xFFFF` (white), and
`CSimpleFont6::setColor` replaces every 16-bit value in the color plane while
leaving alpha unchanged. For example, `arial14.fnt` stores black glyph RGB but
is normally rendered as a white alpha mask. Rex-EMoolator applies the same
default-white colorisation when loading an FNT.

## `arial14.fnt` example

| Field      |          Value |
|------------|---------------:|
| atlas      | `2124 × 22 px` |
| format     |  `16` (RGB565) |
| glyphs     |          `118` |
| cell width |        `18 px` |
| file size  |     `154482 B` |

## See also

- [`FONT`](../reference/FONT.md) — a collection of `.FNT` variants;
- [`TEXT`](../reference/TEXT.md) — text layout and display.
