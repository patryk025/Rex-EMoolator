# INE format — Inertia physics

!!! note "Reverse-engineered, no loader yet"
    The structure below is a complete reconstruction of the `.INE` format — understood more fully than [`.SEK`](SEK.md). Unlike the other format pages, though, it is **not** yet reconciled against parser code: the emulator does not ship an `INE` loader at the moment. Numbers are **little-endian**.

The `.INE` file stores data for the built-in 2D physics engine **Inertia**, used in *Reksio i Kretes w Akcji* ([`INERTIA`](../reference/INERTIA.md)). It is a flat list of physics **bodies** — each a collision shape with a material and a placement in the scene.

## Header

| Field | Type | Description |
|---|---|---|
| magic | `char[4]` | `INE1` |
| count | `uint32` | number of body blocks that follow |

## Bodies

Each body begins with a common header:

| Field | Type | Description |
|---|---|---|
| object id | `int32` | linked scene object; `-1` = assigned automatically |
| body type | `int32` | shape kind (see [Body types](#body-types)) |
| unused | `uint32` | read but ignored |
| group / material | `int32` | collision material (see [Materials](#materials)) |
| X | `float` | position in pixels; world `X = (X − 400) × 0.01` (100 px per metre) |
| Y | `float` | position in pixels; world `Y = (300 − Y) × 0.01` (reversed Y axis, 100 px per metre) |
| angle | `float` | rotation (exact mapping unconfirmed) |

What follows the header depends on the body type: solid primitives carry mass and dimensions, while hull/level bodies carry a vertex array.

### Body types

| Value | Name | Shape |
|---|---|---|
| 1 | `CBodyBox` | box |
| 2 | `CBodySphere` | sphere |
| 3 | `CBodyCone` | cone |
| 4 | `CBodyCCylinder` | capped cylinder (capsule) |
| 5 | `CBodyCylinder` | cylinder |
| 6 | `CBodyHull` | convex hull extruded from a polyline |
| 7 | `CBodyLevel` | static level geometry extruded from a polyline |

### Materials

| Value | Name | Notes |
|---|---|---|
| 0 | `DEFAULT` | |
| 1 | `BOUNCY` | |
| 2 | `RIGID` | |
| 5 | `TRIGGER` | dedicated callbacks |
| 6 | `GHOST` | |
| 7 | `REACTIVE` | mixed callbacks |

### Shape parameters

After the header, solid primitives store their mass and dimensions. Which fields are present is keyed off the body type:

| Body type | `mass` | `dim1` | `dim2` | `dim3` |
|---|:--:|:--:|:--:|:--:|
| `CBodyBox` (1) | ✓ | ✓ | ✓ | ✓ |
| `CBodySphere` (2) | ✓ | ✓ | ✓ | ✓ |
| `CBodyCone` (3) | ✓ | ✓ | ✓ | |
| `CBodyCCylinder` (4) | ✓ | ✓ | ✓ | |
| `CBodyCylinder` (5) | ✓ | ✓ | ✓ | |
| `CBodyHull` (6) | ✓ | | | |
| `CBodyLevel` (7) | | | | |

Each present field is a `float`. In short: `CBodyLevel` has neither mass nor dimensions, `CBodyHull` has only mass, the cone/cylinder family adds two dimensions, and box/sphere add a third.

### Vertex array (hull and level)

For `CBodyHull` (6) and `CBodyLevel` (7), a polyline follows the header instead of dimensions:

| Field | Type | Description |
|---|---|---|
| vertex count | `uint32` | number of points |
| vertices | `Point2D[]` | each: `float` x, `float` y (in pixels) |

The polyline is **extruded** into collision walls — **10 m** deep for `CBodyHull`, **2 m** for `CBodyLevel`. Vertices are later promoted to `vec4(x, y, 0, 1)`.

## See also

- [`INERTIA`](../reference/INERTIA.md) — the scripting object based on `.INE`.
- [SEK format](SEK.md) — the analogous format for 3D physics ([`WORLD`](../reference/WORLD.md)).
