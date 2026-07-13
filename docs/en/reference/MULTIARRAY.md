# MULTIARRAY

A zero-indexed multi-dimensional array. Created by default as a two-dimensional `16 × 16` array; the number of dimensions can be changed in the script through the `DIMENSIONS` field. Each dimension grows automatically (doubling its size) whenever a write targets a position outside its current range.

## Fields

### DIMENSIONS

```
INTEGER DIMENSIONS
```

The number of dimensions of the array. The field is read during variable initialisation; the default is `2`. Each dimension starts with size `16`.

## Methods

### GET

```
mixed GET(INTEGER index1, [INTEGER index2, ..., INTEGER indexN])
```

Returns the value at the cell with the given coordinates. The number of arguments must equal the dimension count declared by `DIMENSIONS`. For a cell that has not been written, or with coordinates out of range, `NULL` is returned.

**Parameters**

- `index1, …, indexN` — cell coordinates (`0`-based), one per dimension.

**Returns**: the cell's value or `NULL`.

**Examples**

```
ARRMAPA^GET(IKRETMOVEONMAPAX, IKRETPOSMAPAY);
ARRMAPA^GET([IPLAYERPOSX-1], IPLAYERPOSY);
ARRMAPA^GET(_I_, I1);
```

**Compatibility:** `GET` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ❌, `PIKLIB8.DLL` ⚠️ (16/10), `BlooMooWEB.dll` ⚠️ (2/1), `BlooMooDLL.dll` ⚠️ (10/5).

### SET

```
void SET(INTEGER index1, [INTEGER index2, ..., INTEGER indexN], mixed value)
```

Stores a value at the cell with the given coordinates. The number of arguments must equal the dimension count + 1; the last argument is the value to store. If any coordinate exceeds the current size of its dimension, the array is grown automatically (the dimension's size is doubled until it covers the coordinate).

**Parameters**

- `index1, …, indexN` — cell coordinates.
- `value` — the value to store.

**Examples**

```
ARRMAPA^SET(ITMPX, ITMPY, 0);
ARRMAPA^SET(IX, IY, SPOLE);
ARRMAPA^SET(IPLAYERGOONX, IPLAYERGOONY, "PLAYER");
ARRMAPA^SET([IPLAYERPOSX+ILASTDIRX], [IPLAYERPOSY+ILASTDIRY], IPLAYER);
```

**Compatibility:** `SET` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ❌, `PIKLIB8.DLL` ⚠️ (16/10), `BlooMooWEB.dll` ⚠️ (2/1), `BlooMooDLL.dll` ⚠️ (10/5).

### GETSIZE

```
INTEGER GETSIZE(INTEGER dimension)
```

Returns the size of the given dimension of the array.

**Parameters**

- `dimension` — dimension index (`0`-based).

**Returns**: the dimension's size, or `0` for an invalid index.

**Compatibility:** `GETSIZE` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ❌, `PIKLIB8.DLL` ⚠️ (16/10), `BlooMooWEB.dll` ⚠️ (2/1), `BlooMooDLL.dll` ⚠️ (10/5).

### LOAD

```
void LOAD(STRING path)
```

Replaces the array's contents with data read from a binary file. The format includes the array's dimensions and cell values.

**Parameters**

- `path` — file path in the game's VFS.

**Compatibility:** `LOAD` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ❌, `PIKLIB8.DLL` ⚠️ (16/10), `BlooMooWEB.dll` ⚠️ (2/1), `BlooMooDLL.dll` ⚠️ (10/5).

### SAVE

```
void SAVE(STRING path)
```

Writes the array's contents to a binary file.

**Parameters**

- `path` — destination file path in the game's VFS.

**Compatibility:** `SAVE` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ❌, `PIKLIB8.DLL` ⚠️ (16/10), `BlooMooWEB.dll` ⚠️ (2/1), `BlooMooDLL.dll` ⚠️ (10/5).

## Signals

### ONINIT

Fired when the variable is initialised; the `DIMENSIONS` field is read at this moment and the array's dimensions are allocated.

### ONSIGNAL

Fired when a signal arrives (see [Events and signals](../engine/events.md#onsignal)).
