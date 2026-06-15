# VECTOR

An N-dimensional vector of floating-point numbers. In practice, used for two- or three-dimensional coordinates — engine games use vectors mostly in physics-flavoured minigames (reflection, movement-direction normalisation).

The variable's value is the vector's Euclidean length, i.e. `sqrt(x1² + x2² + … + xN²)`.

## Fields

### SIZE

```
INTEGER SIZE
```

The number of vector components. Typical values in shipping games are `2` or `3`.

### VALUE

```
DOUBLE, DOUBLE, [DOUBLE...] VALUE
```

Initial values of each component. The number of entries should match [`SIZE`](#size).

## Methods

### ADD

```
void ADD(STRING|VECTOR vectorName)
```

Adds another vector to this one component-wise. The result is stored in the vector the method was called on.

**Parameters**

- `vectorName` — the vector to add; either a [`STRING`](STRING.md) (variable name) or a `VECTOR`.

**Examples**

```
VTEMP2^ADD("VTOCENTER");
VTEMP2^ADD(VTOCENTER);
```

### ASSIGN

```
void ASSIGN(DOUBLE x1, DOUBLE x2, [DOUBLE...])
```

Assigns new component values. The number of arguments dictates how many components are overwritten — any beyond that retain their previous values. If more arguments are supplied than the vector has components, the vector is extended.

**Parameters**

- `x1, x2, …` — the new component values.

**Examples**

```
VTEMP1^ASSIGN(0.0,0.0);
VTEMP1^ASSIGN(ARRDIRX^GET(VARPLAYER),ARRDIRY^GET(VARPLAYER));
VNORMAL^ASSIGN([ARRPOSX^GET(VARPLAYER)+ARRHWIDTH^GET(VARPLAYER)],[ARRPOSY^GET(VARPLAYER)+ARRHHEIGHT^GET(VARPLAYER)]);
```

### GET

```
DOUBLE GET(INTEGER index)
```

Returns the value of the component at the given (zero-based) index. Returns `0.0` for out-of-range indices.

**Parameters**

- `index` — component index, zero-based.

**Returns**: [`DOUBLE`](DOUBLE.md) — the component value or `0.0`.

**Examples**

```
VTEMP1^GET(0);
VTEMP1^GET(1);
```

### LEN

```
DOUBLE LEN()
```

Returns the vector's Euclidean length.

**Returns**: [`DOUBLE`](DOUBLE.md) — the vector length.

### MUL

```
void MUL(DOUBLE scalar)
```

Multiplies every component by the given scalar.

**Parameters**

- `scalar` — the multiplier.

**Examples**

```
VTEMP1^MUL(10.0);
VTEMP1^MUL(ARRSPEED^GET(VARPLAYER));
VTEMP2^MUL(-1);
```

### NORMALIZE

```
void NORMALIZE()
```

Normalises the vector to length `1` (divides each component by the current length). Calling this on a zero-length vector leaves the vector unchanged.

**Examples**

```
VNORMAL^NORMALIZE();
VTEMP1^NORMALIZE();
```

### REFLECT

```
void REFLECT(STRING|VECTOR normalVector, STRING|VECTOR resultVector)
```

Reflects this vector about the given normal vector and writes the result into the result vector. The original vector is unchanged.

**Parameters**

- `normalVector` — normal vector of the surface to reflect against.
- `resultVector` — vector that will receive the result.

**Examples**

```
VINCIDENT^REFLECT("VNORMAL","VREFLECT");
VINCIDENT^REFLECT(VNORMAL,VREFLECT);
```

## Signals

### ONINIT

Fired when the object is initialised.

### ONSIGNAL

Fired when a signal arrives (see [Events and signals](../engine/events.md#onsignal)).
