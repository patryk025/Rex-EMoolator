# VIRTUALGRAPHICSOBJECT

A virtual graphics object — acts as a proxy or composite over another graphic referenced by the [`SOURCE`](#source) field. Lets scripts treat an existing graphical element as a separate entity with its own position, priority, mask, and collision flags.

This type appears only sporadically in shipping scripts — primarily in *Reksio i Czarodzieje* (`common\classes\SinglePuzzle.class`).

## Fields

### ASBUTTON

```
BOOL ASBUTTON
```

Whether the object behaves as a clickable button.

### MASK

```
STRING MASK
```

Name of the graphics variable used as a clipping mask during rendering.

### MONITORCOLLISION

```
BOOL MONITORCOLLISION
```

Enables collision monitoring against other graphical objects.

### MONITORCOLLISIONALPHA

```
BOOL MONITORCOLLISIONALPHA
```

Enables collision monitoring with alpha-channel awareness — a collision is reported only when the overlapping pixels are opaque.

### PRIORITY

```
INTEGER PRIORITY
```

Render priority (Z position).

### SOURCE

```
STRING SOURCE
```

Name of the graphics variable whose contents are rendered through this virtual object.

### TOCANVAS

```
BOOL TOCANVAS
```

Whether the object is drawn on the canvas.

### VISIBLE

```
BOOL VISIBLE
```

Whether the object is visible.

## Methods

### GETHEIGHT

```
INTEGER GETHEIGHT()
```

Returns the object's height in pixels.

**Returns**: [`INTEGER`](INTEGER.md) — height.

**Compatibility:** `GETHEIGHT` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ❌, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### GETPOSITIONX

```
INTEGER GETPOSITIONX()
```

Returns the object's X position.

**Returns**: [`INTEGER`](INTEGER.md) — X coordinate.

**Compatibility:** `GETPOSITIONX` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ❌, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### GETPOSITIONY

```
INTEGER GETPOSITIONY()
```

Returns the object's Y position.

**Returns**: [`INTEGER`](INTEGER.md) — Y coordinate.

**Compatibility:** `GETPOSITIONY` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ❌, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### GETWIDTH

```
INTEGER GETWIDTH()
```

Returns the object's width in pixels.

**Returns**: [`INTEGER`](INTEGER.md) — width.

**Compatibility:** `GETWIDTH` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ❌, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### MOVE

```
void MOVE(INTEGER offsetX, INTEGER offsetY)
```

Moves the object by the given offsets, relative to its current position.

**Parameters**

- `offsetX`, `offsetY` — translation vector.

**Examples**

```
VGO^MOVE($1,$2);
```

**Compatibility:** `MOVE` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ❌, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### SETMASK

```
void SETMASK(STRING maskName)
```

Sets the clipping mask — equivalent to the [`MASK`](#mask) field.

**Parameters**

- `maskName` — name of the graphics variable used as the mask.

**Examples**

```
VGO^SETMASK(MSK);
```

**Compatibility:** `SETMASK` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ❌, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### SETPOSITION

```
void SETPOSITION(INTEGER posX, INTEGER posY)
```

Sets the object's absolute position.

**Parameters**

- `posX`, `posY` — new position.

**Examples**

```
VGO^SETPOSITION($1,$2);
```

**Compatibility:** `SETPOSITION` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ❌, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### SETPRIORITY

```
void SETPRIORITY(INTEGER priority)
```

Sets the render priority (Z position) — equivalent to the [`PRIORITY`](#priority) field.

**Parameters**

- `priority` — new priority value.

**Examples**

```
VGO^SETPRIORITY(1000);
```

**Compatibility:** `SETPRIORITY` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ❌, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### SETSOURCE

```
void SETSOURCE(STRING sourceName)
```

Sets the graphics variable referenced by [`SOURCE`](#source).

**Parameters**

- `sourceName` — graphics variable name.

**Examples**

```
VGO^SETSOURCE($2);
```

**Compatibility:** `SETSOURCE` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ❌, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

## Signals

### ONINIT

Fired when the object is initialised.

### ONSIGNAL

Fired when a signal arrives (see [Events and signals](../engine/events.md#onsignal)).
