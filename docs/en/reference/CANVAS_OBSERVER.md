# CANVAS_OBSERVER

Entry point for canvas operations — the shared rendering area where the engine draws every visible graphic. Provides methods for adding and removing graphics from the screen, looking up the object under a point, setting the background, taking screenshots, and emitting signals on game-window focus changes.

A scene typically has a single `CANVAS_OBSERVER` instance that scripts treat as a global object.

## Methods

### ADD

```
void ADD(STRING varName)
void ADD(STRING varName, INTEGER priority)
```

Adds an [`ANIMO`](ANIMO.md) or [`IMAGE`](IMAGE.md) variable to the canvas — makes it visible and optionally assigns a render priority (default `1000`).

**Parameters**

- `varName` — name of the graphics variable.
- `priority` — (optional) render priority (Z position).

**Examples**

```
CANVASOBSERVER^ADD("ANNMKORBA2");
CANVASOBSERVER^ADD("ANNMPRZEGRYZA");
```

**Compatibility:** `ADD` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### ENABLENOTIFY

```
void ENABLENOTIFY(BOOL enable)
```

Enables or disables emission of game-window focus signals ([`ONWINDOWFOCUSON`](#onwindowfocuson), [`ONWINDOWFOCUSOFF`](#onwindowfocusoff)).

**Parameters**

- `enable` — `TRUE` enables notifications, `FALSE` disables them.

**Examples**

```
CANVASOBSERVER^ENABLENOTIFY(TRUE);
```

**Compatibility:** `ENABLENOTIFY` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### GETBPP

```
INTEGER GETBPP()
```

Returns the canvas colour depth in bits per pixel. The original BlooMoo engine runs in 16 bpp (RGB565) — this method always returns `16`.

**Returns**: [`INTEGER`](INTEGER.md) — colour depth in bits (`16`).

**Compatibility:** `GETBPP` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ❌, `PIKLIB8.DLL` ⚠️ (4/10), `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### GETGRAPHICSAT

```
STRING GETGRAPHICSAT(INTEGER posX, INTEGER posY, BOOL onlyVisible, INTEGER minZ, INTEGER maxZ)
STRING GETGRAPHICSAT(INTEGER posX, INTEGER posY, BOOL onlyVisible, INTEGER minZ, INTEGER maxZ, BOOL ignoreAlpha)
```

Returns the name of the graphics variable at the point `(posX, posY)`. Only the current scene is searched. The lookup starts from the highest-priority graphic and walks down. Returns `"NULL"` if no graphic matches.

**Parameters**

- `posX`, `posY` — query point coordinates.
- `onlyVisible` — if `TRUE`, only currently visible graphics are considered.
- `minZ`, `maxZ` — priority (Z) range restricting the search.
- `ignoreAlpha` — (optional) if `TRUE`, only the graphic's rectangle is tested; if `FALSE` (or omitted), the pixel's alpha channel is checked too.

**Returns**: [`STRING`](STRING.md) — the matched object's name or `"NULL"`.

**Examples**

```
CANVASOBSERVER^GETGRAPHICSAT(MOUSE^GETPOSX(),MOUSE^GETPOSY(),TRUE,2998,2998,FALSE);
CANVASOBSERVER^GETGRAPHICSAT(VARICURSORX,VARICURSORY,TRUE,40,40,TRUE);
```

**Compatibility:** `GETGRAPHICSAT` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### GETGRAPHICSAT2

```
STRING GETGRAPHICSAT2(INTEGER posX, INTEGER posY, BOOL onlyVisible, INTEGER minZ, INTEGER maxZ)
STRING GETGRAPHICSAT2(INTEGER posX, INTEGER posY, BOOL onlyVisible, INTEGER minZ, INTEGER maxZ, BOOL ignoreAlpha)
```

Variant of [`GETGRAPHICSAT`](#getgraphicsat) that walks up the container hierarchy (scene → episode → root) instead of searching only the current scene.

**Compatibility:** `GETGRAPHICSAT2` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ❌, `PIKLIB8.DLL` ❌, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ⚠️ (4/5).

### MOVEBKG

```
void MOVEBKG(INTEGER deltaX, INTEGER deltaY)
```

Moves the background by the given X/Y deltas (relative to its current position).

**Parameters**

- `deltaX`, `deltaY` — translation vector in pixels.

**Examples**

```
CANVASOBSERVER^MOVEBKG(0,ARRAYDY^GET(0));
CANVASOBSERVER^MOVEBKG(ISCROLLMOVEX,ISCROLLMOVEY);
```

**Compatibility:** `MOVEBKG` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### PASTE

```
void PASTE(STRING varName, INTEGER posX, INTEGER posY)
```

Pastes a snapshot of the current contents of an [`ANIMO`](ANIMO.md) or [`IMAGE`](IMAGE.md) graphic onto the canvas as a static, non-modifiable texture at `(posX, posY)`. The source variable itself is not modified.

**Parameters**

- `varName` — name of the graphics variable.
- `posX`, `posY` — paste position.

**Examples**

```
CANVASOBSERVER^PASTE("ANNBUM",[I1-IPLANPOSX],[I2-IPLANPOSY]);
CANVASOBSERVER^PASTE("IMG1",0,0);
```

**Compatibility:** `PASTE` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ❌, `PIKLIB8.DLL` ❌, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ⚠️ (4/5).

### REDRAW

```
void REDRAW()
```

In the original engine this marks the canvas as needing repaint. In practice the engine already redraws the whole canvas every frame, so this method behaves as a no-op.

**Compatibility:** `REDRAW` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ❌, `PIKLIB8.DLL` ⚠️ (8/10), `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### REFRESH

```
void REFRESH()
```

Forces a redraw of every [`IMAGE`](IMAGE.md) in the current scene — internally calls [`INVALIDATE`](IMAGE.md#invalidate) on each.

**Compatibility:** `REFRESH` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### REMOVE

```
void REMOVE(STRING varName1, [STRING varName2, ...])
```

Hides the listed graphics on the canvas (sets their visibility to `FALSE`). Accepts any number of arguments.

**Parameters**

- `varName1, varName2, …` — successive graphics-variable names to hide.

**Examples**

```
CANVASOBSERVER^REMOVE("ZLY");
CANVASOBSERVER^REMOVE("ANNAUTOR","ANNAUTOL","ANNAUTORMASK","ANNAUTOLMASK");
```

**Compatibility:** `REMOVE` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### SAVE

```
void SAVE(STRING imgFileName, DOUBLE xScaleFactor, DOUBLE yScaleFactor)
void SAVE(STRING imgFileName, DOUBLE xScaleFactor, DOUBLE yScaleFactor, INTEGER xLeft, INTEGER yTop, INTEGER xRight, INTEGER yBottom)
```

Saves the current canvas view to an `.IMG` file. The seven-argument form crops a rectangle from the canvas before scaling.

**Parameters**

- `imgFileName` — destination `.IMG` path.
- `xScaleFactor`, `yScaleFactor` — X/Y scaling factors.
- `xLeft`, `yTop`, `xRight`, `yBottom` — (optional) rectangle to crop before scaling.

**Examples**

```
CANVASOBSERVER^SAVE("$COMMON\PAGE.IMG",1,1);
CANVASOBSERVER^SAVE("$COMMON\ZOOM.IMG",2,2,$1,$2,$3,$4);
CANVASOBSERVER^SAVE(["$COMMON\SAVE_BD\BD_SCR"+VARISLOTNO+".IMG"],0.5,0.5);
```

**Compatibility:** `SAVE` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### SETBACKGROUND

```
void SETBACKGROUND(STRING imageName)
```

Sets the canvas background. The argument may name an existing [`IMAGE`](IMAGE.md) variable or a path to an `.IMG` file — in the latter case the engine creates a hidden [`IMAGE`](IMAGE.md) variable backing that file.

**Parameters**

- `imageName` — [`IMAGE`](IMAGE.md) variable name or `.IMG` file path.

**Examples**

```
CANVASOBSERVER^SETBACKGROUND(SOBJECT|NAME);
CANVASOBSERVER^SETBACKGROUND("LOGO.IMG");
```

**Compatibility:** `SETBACKGROUND` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### SETBKGPOS

```
void SETBKGPOS(INTEGER posX, INTEGER posY)
```

Sets the absolute X/Y position of the background.

**Parameters**

- `posX`, `posY` — new background position.

**Examples**

```
CANVASOBSERVER^SETBKGPOS([VARI_BKGX-VARI_BKGXOFFSET],[VARI_BKGY-VARI_BKGYOFFSET]);
CANVASOBSERVER^SETBKGPOS(VARI_TMPX,0);
```

**Compatibility:** `SETBKGPOS` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

## Signals

### ONINIT

Fired when the object is initialised.

### ONWINDOWFOCUSON

Fired when the game window gains focus (e.g. after un-minimising). Emission requires notifications enabled via [`ENABLENOTIFY`](#enablenotify).

### ONWINDOWFOCUSOFF

Fired when the game window loses focus (e.g. when switched to another application). Emission requires notifications enabled.

### ONSIGNAL

Fired when a signal arrives (see [Events and signals](../engine/events.md#onsignal)).
