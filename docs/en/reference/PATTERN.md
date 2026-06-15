# PATTERN

A board built from a grid of fixed-size tiles arranged in multiple layers. Used in flying minigames such as the dragon-rider on Smokręt (*Reksio i Wehikuł Czasu*) and the broomstick (*Reksio i Czarodzieje*) — most likely to store the scrolling level map. Analysis of this type is still ongoing.

## Fields

### GRIDX

```
INTEGER GRIDX
```

Width of a single tile in pixels.

### GRIDY

```
INTEGER GRIDY
```

Height of a single tile in pixels.

### HEIGHT

```
INTEGER HEIGHT
```

Board height.

### LAYERS

```
INTEGER LAYERS
```

Number of layers.

### PRIORITY

```
INTEGER PRIORITY
```

Board render priority (Z position).

### TOCANVAS

```
BOOL TOCANVAS
```

Whether the board is drawn on the canvas.

### VISIBLE

```
BOOL VISIBLE
```

Whether the board is visible.

### WIDTH

```
INTEGER WIDTH
```

Board width.

## Methods

### ADD

```
void ADD(STRING tileId, INTEGER posX, INTEGER posY, STRING animoName, [INTEGER layer])
```

Places a graphic onto the board.

**Parameters**

- `tileId` — tile identifier.
- `posX`, `posY` — tile coordinates.
- `animoName` — name of the [`ANIMO`](ANIMO.md) variable to display in the tile.
- `layer` — (optional) layer number.

**Examples**

```
PATBOARD^ADD(["E"+_I_],VARX,VARY,VARSTRING0,0);
PATTERNFOREST^ADD([VARSTRING0+"_"+_I_],VARINT1,VARINT2,VARSTRING1,VARINT3);
```

### GETGRAPHICSAT

```
STRING GETGRAPHICSAT(INTEGER posX, INTEGER posY, BOOL onlyVisible, BOOL ignoreAlpha, INTEGER minZ, [INTEGER maxZ])
```

Returns the name of the tile at the given point. Behaves analogously to [`CANVAS_OBSERVER.GETGRAPHICSAT`](CANVAS_OBSERVER.md#getgraphicsat), but limited to this board's tiles.

**Parameters**

- `posX`, `posY` — query point coordinates.
- `onlyVisible` — if `TRUE`, only visible tiles are considered.
- `ignoreAlpha` — if `TRUE`, only the tile's rectangle is tested; if `FALSE`, the pixel's alpha channel is also checked.
- `minZ` — minimum layer.
- `maxZ` — (optional) maximum layer.

**Returns**: [`STRING`](STRING.md) — tile name or `"NULL"`.

**Examples**

```
PATTERNMASKKRET^GETGRAPHICSAT([ANNKRET^GETCENTERX()+20],ANNKRET^GETCENTERY(),TRUE,FALSE,0,1);
PATTERNFIRE^GETGRAPHICSAT(ANNCOLLUP^GETCENTERX(),ANNCOLLUP^GETCENTERY(),FALSE,FALSE,0);
```

### MOVE

```
void MOVE(INTEGER offsetX, INTEGER offsetY)
```

Moves the board by the given X/Y offsets.

**Parameters**

- `offsetX`, `offsetY` — translation vector.

**Examples**

```
PATBOARD^MOVE(0,-3000);
PATTERNBKG^MOVE(VARINT0,0);
```

## Signals

### ONINIT

Fired when the object is initialised.

### ONSIGNAL

Fired when a signal arrives (see [Events and signals](../engine/events.md#onsignal)).
