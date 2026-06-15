# SEK format — 3D physics

!!! warning "Work in progress"
    The `.SEK` format is complex and still being analysed. The description below matches the `SEKLoader` parser (version `004`) and contains fields whose purpose remains **undetermined** (marked "unknown"). Treat it as a working reconstruction, not a complete specification.

The `.SEK` file describes the physical world of a [`WORLD`](../reference/WORLD.md) object: objects with collision geometry plus points and paths for route-finding. Numbers are **little-endian**.

## Header

| Field | Type | Description |
|---|---|---|
| magic | `char[16]` | `SEKAI81080701915` |
| version | `char[3]` | e.g. `004` (supported); `002`/`003` unsupported |
| entity count | `int32` | how many entity blocks follow |

## Entities

Each entity starts with a header:

| Field | Type | Description |
|---|---|---|
| entity type | `int32` | `1` = scene object, `4` = path points; others skipped |
| length | `int32` | size of the entity data (lets unknown types be skipped) |

### Object header (common to types 1 and 4)

| Field | Type | Description |
|---|---|---|
| entity id | `int32` | identifier |
| flags | `int32` | `3` = movable object, position read from file; otherwise static at `(0,0,0)` |
| position X/Y/Z | `float × 3` | coordinates |
| unknown | `float` | `0` or `1` |
| rotation Z | `float` | unusual angle mapping |
| unknown | `float` | usually `0` |
| body dimensions X/Y/Z | `float × 3` | radius/length (cylinder, sphere) or `lx`/`ly`/`lz` (box) |
| property count | `int32` | how many name–value pairs |
| properties | — | one pair each: `string` name, `4 B` padding, `string` value |

A property named `entityDef` contains a text block (lines separated by `\r\n`) with physics parameters in the `parameter(values);` syntax: `geomType`, `mass`, `mu`, `friction`, `bounce`, `bounceVel`, `maxVel`, `limit`.

### Type 1 — scene object

After the common header:

| Field | Type | Description |
|---|---|---|
| geometry type | `int32` | |
| triangle count | `int32` | |
| triangles | — | one block per triangle (see below) |

Each triangle: a `string` material, then **3 vertices** (each: position `float × 3`, normal `float × 3`, coordinates `u`, `v`), and finally 3 unknown `float`s.

### Type 4 — path points

After the common header:

| Field | Type | Description |
|---|---|---|
| point count | `int32` | |
| path count | `int32` | |
| points | — | one each: `float × 3` (X/Y/Z) + `4 B` padding |
| paths | — | one each: `int32` first point, `int32` second point, `int32` unknown (always `3` in tests) |

A graph is built from the points and paths, on which route-finding ([A*](../reference/WORLD.md)) operates.

## See also

- [`WORLD`](../reference/WORLD.md) — the scripting object based on `.SEK`.
- [INE format](INE.md) — the related format for the 2D Inertia physics engine.
