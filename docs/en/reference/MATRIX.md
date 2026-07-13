# MATRIX

A rectangular grid of cells with a small built-in stone-falling physics system and primitive enemy pathfinding. Designed around a single use case — the digging minigame (Kretes sections in *Reksio i Ufo*: the wall on Indor and the prison-tunnel on Kuran).

Grid data lives in a one-dimensional array of integers — a cell's address is computed as `index = y * width + x`. Hence the family of methods that converts between linear index and 2D position.

## Field codes

Values used by [`GET`](#get), [`GETCELLSNO`](#getcellsno), [`SET`](#set), [`SETROW`](#setrow) and the internal physics system:

| Code | Meaning |
| --- | --- |
| `0` | empty cell |
| `1` | ground |
| `2` | stone |
| `3` | dynamite stick |
| `4` | weak (destructible) wall |
| `5` | enemy |
| `6` | strong (indestructible) wall |
| `7` | lit dynamite stick |
| `8` | cell within blast radius |
| `9` | exit |
| `99` | Kretes' position |

## Movement direction codes

Values used by [`TICK`](#tick), [`NEXT`](#next), and [`CALCENEMYMOVEDIR`](#calcenemymovedir):

| Code | Direction |
| --- | --- |
| `0` | left |
| `1` | up |
| `2` | right |
| `3` | down |

The physics system uses additional codes returned by [`TICK`](#tick) and consumed by [`NEXT`](#next):

| Code | Operation |
| --- | --- |
| `1` | stone falls down |
| `2` | stone slides diagonally to the left |
| `3` | stone slides diagonally to the right |
| `4` | stone collides with an enemy and explodes |

## Fields

### BASEPOS

```
INTEGER, INTEGER BASEPOS
```

Pixel position of the grid's top-left corner on screen (`X,Y`).

### CELLHEIGHT

```
INTEGER CELLHEIGHT
```

Height of a single cell in pixels.

### CELLWIDTH

```
INTEGER CELLWIDTH
```

Width of a single cell in pixels.

### SIZE

```
INTEGER, INTEGER SIZE
```

Grid dimensions in cells (`width,height`). The value also dictates the size of the internal field-code array.

## Methods

### CALCENEMYMOVEDEST

```
INTEGER CALCENEMYMOVEDEST(INTEGER oldCell, INTEGER direction)
```

Computes the destination cell for an enemy moving from `oldCell` in the given `direction`. If the target cell is out of bounds or not empty, `oldCell` is returned unchanged.

**Parameters**

- `oldCell` — the enemy's current cell index.
- `direction` — movement direction (`0`–`3`, see [Movement direction codes](#movement-direction-codes)).

**Returns**: [`INTEGER`](INTEGER.md) — destination cell index.

**Examples**

```
MAT^CALCENEMYMOVEDEST(IENOLDCELL,IENNEWDIR);
```

**Compatibility:** `CALCENEMYMOVEDEST` - type from a dynamically loaded library, outside the scope of the current `compat.json` export.

### CALCENEMYMOVEDIR

```
INTEGER CALCENEMYMOVEDIR(INTEGER oldCell, INTEGER oldDir)
```

Computes the next movement direction for an enemy. The algorithm prefers turning left: it first checks `(oldDir+3) MOD 4`, then `oldDir`, then right, and finally backwards. The first direction whose target cell is empty is returned; if none is available, the leftward direction is returned anyway.

**Parameters**

- `oldCell` — the enemy's current cell index.
- `oldDir` — the previous-step movement direction.

**Returns**: [`INTEGER`](INTEGER.md) — the new movement direction.

**Examples**

```
MAT^CALCENEMYMOVEDIR(IENOLDCELL,IENOLDDIR);
```

**Compatibility:** `CALCENEMYMOVEDIR` - type from a dynamically loaded library, outside the scope of the current `compat.json` export.

### CANHEROGOTO

```
BOOL CANHEROGOTO(INTEGER cellNo)
```

Checks whether the protagonist can step onto the given cell. A cell is walkable if it does not contain a weak wall, strong wall, enemy or stone, and is not part of a gate area set with [`SETGATE`](#setgate).

**Parameters**

- `cellNo` — index of the cell to check.

**Returns**: [`BOOL`](BOOL.md) — `TRUE` if the cell is walkable.

**Examples**

```
MAT^CANHEROGOTO(I_0);
```

**Compatibility:** `CANHEROGOTO` - type from a dynamically loaded library, outside the scope of the current `compat.json` export.

### GET

```
INTEGER GET(INTEGER cellNo)
```

Returns the field code of the cell at the given index. For out-of-range indices returns `0`.

**Parameters**

- `cellNo` — cell index.

**Returns**: [`INTEGER`](INTEGER.md) — field code (see [Field codes](#field-codes)).

**Examples**

```
MAT^GET(I_ITERATOR);
MAT^GET(I_MOLE_FIELD_CURR);
```

**Compatibility:** `GET` - type from a dynamically loaded library, outside the scope of the current `compat.json` export.

### GETCELLOFFSET

```
INTEGER GETCELLOFFSET(INTEGER x, INTEGER y)
```

Returns the cell index for the given `(x, y)` grid coordinates. Returns `-1` if the coordinates fall outside the grid.

**Parameters**

- `x`, `y` — grid coordinates.

**Returns**: [`INTEGER`](INTEGER.md) — the cell index or `-1`.

**Examples**

```
MAT^GETCELLOFFSET(ISSRCX,ISSRCY);
MAT^GETCELLOFFSET(ISTRGX,ISTRGY);
```

**Compatibility:** `GETCELLOFFSET` - type from a dynamically loaded library, outside the scope of the current `compat.json` export.

### GETCELLPOSX

```
INTEGER GETCELLPOSX(INTEGER cellNo)
```

Returns the cell's X position in pixels (`BASEPOS.X + col * CELLWIDTH`).

**Parameters**

- `cellNo` — cell index.

**Returns**: [`INTEGER`](INTEGER.md) — X coordinate in pixels.

**Compatibility:** `GETCELLPOSX` - type from a dynamically loaded library, outside the scope of the current `compat.json` export.

### GETCELLPOSY

```
INTEGER GETCELLPOSY(INTEGER cellNo)
```

Returns the cell's Y position in pixels (`BASEPOS.Y + row * CELLHEIGHT`).

**Parameters**

- `cellNo` — cell index.

**Returns**: [`INTEGER`](INTEGER.md) — Y coordinate in pixels.

**Compatibility:** `GETCELLPOSY` - type from a dynamically loaded library, outside the scope of the current `compat.json` export.

### GETCELLSNO

```
INTEGER GETCELLSNO(INTEGER cellCode)
```

Returns the number of cells with the given field code.

**Parameters**

- `cellCode` — the field code to count.

**Returns**: [`INTEGER`](INTEGER.md) — number of matching cells.

**Examples**

```
MAT^GETCELLSNO(IC_FIELD_CODE_EXIT);
MAT^GETCELLSNO(IC_FIELD_CODE_ENEMY);
```

**Compatibility:** `GETCELLSNO` - type from a dynamically loaded library, outside the scope of the current `compat.json` export.

### GETFIELDPOSX

```
INTEGER GETFIELDPOSX(INTEGER cellNo)
```

Alias of [`GETCELLPOSX`](#getcellposx), used in *Reksio i Ufo* with the Piklib 7.1 engine. Piklib 8 no longer ships this method — calling it crashes the engine.

**Parameters**

- `cellNo` — cell index.

**Returns**: [`INTEGER`](INTEGER.md) — X coordinate in pixels.

**Compatibility:** `GETFIELDPOSX` - type from a dynamically loaded library, outside the scope of the current `compat.json` export.

### GETFIELDPOSY

```
INTEGER GETFIELDPOSY(INTEGER cellNo)
```

Alias of [`GETCELLPOSY`](#getcellposy), used in *Reksio i Ufo* with the Piklib 7.1 engine. Piklib 8 no longer ships this method — calling it crashes the engine.

**Parameters**

- `cellNo` — cell index.

**Returns**: [`INTEGER`](INTEGER.md) — Y coordinate in pixels.

**Compatibility:** `GETFIELDPOSY` - type from a dynamically loaded library, outside the scope of the current `compat.json` export.

### GETOFFSET

```
INTEGER GETOFFSET(INTEGER x, INTEGER y)
```

Alias of [`GETCELLOFFSET`](#getcelloffset), used in *Reksio i Ufo* with the Piklib 7.1 engine. Piklib 8 replaces it with `GETCELLOFFSET`.

**Parameters**

- `x`, `y` — grid coordinates.

**Returns**: [`INTEGER`](INTEGER.md) — cell index or `-1`.

**Compatibility:** `GETOFFSET` - type from a dynamically loaded library, outside the scope of the current `compat.json` export.

### ISGATEEMPTY

```
BOOL ISGATEEMPTY()
```

Checks whether all cells in the gate area set with [`SETGATE`](#setgate) have field code `0` (empty). Returns `FALSE` if no gate has been set.

**Returns**: [`BOOL`](BOOL.md) — `TRUE` if all gate cells are empty.

**Examples**

```
MAT^ISGATEEMPTY();
```

**Compatibility:** `ISGATEEMPTY` - type from a dynamically loaded library, outside the scope of the current `compat.json` export.

### ISINGATE

```
BOOL ISINGATE(INTEGER cellNo)
```

Checks whether the cell at the given index belongs to the gate area set with [`SETGATE`](#setgate).

**Parameters**

- `cellNo` — cell index.

**Returns**: [`BOOL`](BOOL.md) — `TRUE` if the cell is inside the gate area.

**Compatibility:** `ISINGATE` - type from a dynamically loaded library, outside the scope of the current `compat.json` export.

### MOVE

```
void MOVE(INTEGER oldCell, INTEGER newCell)
```

Moves the contents of the source cell to the destination cell; the source cell becomes empty (code `0`).

**Parameters**

- `oldCell` — source cell index.
- `newCell` — destination cell index.

**Examples**

```
MAT^MOVE(I_0,I_1);
```

**Compatibility:** `MOVE` - type from a dynamically loaded library, outside the scope of the current `compat.json` export.

### NEXT

```
INTEGER NEXT()
```

Executes the next queued move generated by [`TICK`](#tick). The source cell is cleared and the destination cell receives the stone code. After the move the [`ONNEXT`](#onnext) signal is emitted for every move except the last one in the queue, which fires [`ONLATEST`](#onlatest) instead.

**Returns**: [`INTEGER`](INTEGER.md) — `0` if the queue is empty; `1` for an ordinary stone move; `2` if the stone has come to rest two cells above Kretes (collision).

**Examples**

```
MAT^NEXT();
```

**Compatibility:** `NEXT` - type from a dynamically loaded library, outside the scope of the current `compat.json` export.

### SET

```
void SET(INTEGER cellNo, INTEGER cellCode)
void SET(INTEGER x, INTEGER y, INTEGER cellCode)
```

Sets a cell's field code. Two forms are accepted: the first takes a precomputed cell index, the second takes `(x, y)` coordinates.

**Parameters**

- `cellNo` — cell index **(form 1)**.
- `x`, `y` — cell coordinates **(form 2)**.
- `cellCode` — new field code (see [Field codes](#field-codes)).

**Examples**

```
MAT^SET(I_MOLE_FIELD_CURR,IC_FIELD_CODE_EMPTY);
MAT^SET([I_FIELD_INDEX%I_LEVEL_WIDTH],[I_FIELD_INDEX@I_LEVEL_WIDTH],IC_FIELD_CODE_EMPTY);
```

**Compatibility:** `SET` - type from a dynamically loaded library, outside the scope of the current `compat.json` export.

### SETGATE

```
void SETGATE(INTEGER startX, INTEGER startY, INTEGER endX, INTEGER endY)
```

Defines a rectangular gate area, inclusive of both corners. The gate affects [`CANHEROGOTO`](#canherogoto), [`ISGATEEMPTY`](#isgateempty), and [`ISINGATE`](#isingate).

**Parameters**

- `startX`, `startY` — coordinates of the gate's top-left corner.
- `endX`, `endY` — coordinates of the gate's bottom-right corner.

**Examples**

```
MAT^SETGATE(3,1,16,4);
```

**Compatibility:** `SETGATE` - type from a dynamically loaded library, outside the scope of the current `compat.json` export.

### SETROW

```
void SETROW(INTEGER row, INTEGER cellCode1, [INTEGER cellCode2, ...])
```

Sets field codes for all cells in the given row, starting from column `0`. Excess arguments (beyond the grid's width) are ignored.

**Parameters**

- `row` — row index.
- `cellCode1`, `cellCode2`, … — field codes for successive cells in the row.

**Examples**

```
MAT^SETROW(0,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6);
MAT^SETROW(1,6,6,6,2,2,2,2,2,2,2,2,2,2,2,2,2,2,6,6,6);
```

**Compatibility:** `SETROW` - type from a dynamically loaded library, outside the scope of the current `compat.json` export.

### TICK

```
void TICK()
```

Executes one physics tick. The grid is scanned bottom-to-top, left-to-right. For each stone the following cases are checked in order:

1. Is the cell directly below empty — schedule a downward move.
2. Is the cell below an enemy — schedule an explosion.
3. Are the diagonally adjacent cells (and the lateral neighbour) empty — schedule a diagonal slide.

Scheduled moves are appended to an internal queue and executed one-by-one by [`NEXT`](#next). Each entry records the source cell's X and Y, plus the operation code.

**Examples**

```
MAT^TICK();
```

**Compatibility:** `TICK` - type from a dynamically loaded library, outside the scope of the current `compat.json` export.

## Signals

### ONINIT

Fired when the object is initialised.

### ONNEXT

Fired by [`NEXT`](#next) after a move that was not the last in the current queue. Signal arguments are the source cell's X (`$1`), Y (`$2`) and operation code (`$3`).

### ONLATEST

Fired by [`NEXT`](#next) after the last queued move. Arguments are identical to [`ONNEXT`](#onnext).

### ONSIGNAL

Fired when a signal arrives (see [Events and signals](../engine/events.md#onsignal)).
