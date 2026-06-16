# File formats

The Piklib/BlooMoo engine keeps game assets in a dozen-odd file formats — some textual (scripts, data), some binary (graphics, sound, physics data). This section describes their byte-level structure, based on an analysis **reconciled with Rex-EMoolator's parsers** (`AnimoLoader`, `ImageLoader`, `FontLoader`, and others).

!!! note "Where this documentation comes from"
    The descriptions combine earlier format reverse-engineering with how the emulator actually reads them. Where the code understands a format more precisely than the old notes (e.g. the event fields in [`.ANN`](ANN.md)), the implementation takes precedence. Fields that are still uncertain are clearly marked.

## Binary formats

<div class="grid cards" markdown>

-   :material-animation-play:{ .lg .middle } __[ANN](ANN.md)__ — animations

    ---

    Events, frames, and a shared image pool. Magic: `NVP\0`.

-   :material-image:{ .lg .middle } __[IMG](IMG.md)__ — images

    ---

    A single hi-color bitmap with an optional alpha channel. Magic: `PIK\0`.

-   :material-format-font:{ .lg .middle } __[FNT](FNT.md)__ — fonts

    ---

    A bitmap font with all glyphs in one long bitmap. Magic: `FNT\0`.

-   :material-table:{ .lg .middle } __[ARR](ARR.md)__ — arrays

    ---

    A dump of an [`ARRAY`](../reference/ARRAY.md): type + value per element.

-   :material-table-large:{ .lg .middle } __[MAR](MAR.md)__ — multi-dimensional arrays

    ---

    A sparse dump of a [`MULTIARRAY`](../reference/MULTIARRAY.md): dimensions + `(index, value)` entries.

-   :material-cube-outline:{ .lg .middle } __[SEK](SEK.md)__ — 3D physics :material-progress-wrench:

    ---

    Waypoints, physics objects, and path points for [`WORLD`](../reference/WORLD.md). Magic: `SEKAI…`. *Work in progress.*

-   :material-cog-outline:{ .lg .middle } __[INE](INE.md)__ — Inertia physics

    ---

    Physics bodies — collision shapes for the [`INERTIA`](../reference/INERTIA.md) engine. Magic: `INE1`.

</div>

## Text formats

| Format | Role | Description |
|---|---|---|
| `CNV` | scene object scripts | the main object-definition format — see [Scripts](../engine/scripts.md) |
| `DEF` | entry point (`Application.def`) | `APPLICATION`/`EPISODE`/`SCENE` definitions — see [loading order](../engine/scripts.md#script-loading-order) |
| `CLASS` | class definitions | templates for [`CLASS`](../reference/CLASS.md) objects |
| `SEQ` | sequences | animation scenarios with sound synchronisation ([`SEQUENCE`](../reference/SEQUENCE.md)) |
| [`DTA`](DTA.md) | database | a database stand-in for [`DATABASE`](../reference/DATABASE.md) |
| `INI` | configuration | game settings |

The script formats (`CNV`/`DEF`/`CLASS`/`SEQ`) share a common text syntax described in the [Scripts](../engine/scripts.md) chapter.

## Magic bytes

Most binary formats start with a constant signature — the first thing a parser checks:

| Format | Bytes | ASCII |
|---|---|---|
| ANN | `4E 56 50 00` | `NVP\0` |
| IMG | `50 49 4B 00` | `PIK\0` |
| FNT | `46 4E 54 00` | `FNT\0` |
| SEK | `53 45 4B 41 49 …` | `SEKAI81080701915004` |
| INE | `49 4E 45 31` | `INE1` |

The [`ARR`](ARR.md), [`MAR`](MAR.md), and [`DTA`](DTA.md) files have no signature — `ARR` starts with the element count, `MAR` with the dimension count, and `DTA` is plain text.

## Pixel encoding

The engine's graphics are **hi-color** without a palette:

- **RGB565** (16 bits: 5R-6G-5B) — the most common,
- **RGB555** (15 bits: 5R-5G-5B).

The alpha channel, if present, is stored **separately** from the colour data (one byte per pixel) and merged on decode. Image data may additionally be compressed.

Data may be further compressed (**CRLE**, **CLZW2**), and script files may be encrypted. These cross-cutting mechanisms have their own chapters:

<div class="grid cards" markdown>

-   :material-zip-box-outline:{ .lg .middle } __[Compression](compression.md)__

    ---

    CRLE and CLZW2, combined compression, and pixel decoding RGB565/555 → RGBA.

-   :material-lock-outline:{ .lg .middle } __[Script encryption](encryption.md)__

    ---

    A variable-shift transposition cipher and the `{<C:6>}` header.

</div>

## See also

- [Animation system](../internals/animation.md) — how the engine interprets data from [`.ANN`](ANN.md).
- [Rendering](../internals/rendering.md) — what happens to a bitmap after decoding.
- [Type reference](../reference/index.md) — the scripting objects that use these files.
