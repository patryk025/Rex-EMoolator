# IMAGE

A static image rendered on a scene. Supports positioning, opacity, clipping, runtime file loading, and collision monitoring against other objects.

## Fields

### FILENAME

```
STRING FILENAME
```

Name of the `.IMG` file with the image. Read at variable initialisation; can also be overwritten at runtime via [`LOAD`](#load).

### MONITORCOLLISION

```
BOOL MONITORCOLLISION
```

Whether the image participates in collision detection with other objects. Modified through [`MONITORCOLLISION`](#monitorcollision-1) and [`REMOVEMONITORCOLLISION`](#removemonitorcollision).

### MONITORCOLLISIONALPHA

```
BOOL MONITORCOLLISIONALPHA
```

Whether collision detection takes the image's alpha channel into account — collisions do not occur on fully transparent pixels.

### PRIORITY

```
INTEGER PRIORITY
```

Rendering priority (`Z`) relative to other scene objects.

### VISIBLE

```
BOOL VISIBLE
```

Image visibility. Modified through [`SHOW`](#show) and [`HIDE`](#hide).

## Methods

### GETALPHA

```
INTEGER GETALPHA(INTEGER posX, INTEGER posY)
```

Returns the alpha-channel value of the pixel at the given coordinates (in the `0–255` scale, where `255` is fully opaque).

**Parameters**

- `posX` — pixel X coordinate.
- `posY` — pixel Y coordinate.

**Returns**: the pixel's alpha value.

**Examples**

```
IMGLEVEL^GETALPHA(VARX, VARY);
IMGALPHA^GETALPHA(EXPMASKX, EXPMASKY);
```

**Compatibility:** `GETALPHA` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### GETCENTERX

```
INTEGER GETCENTERX()
```

Returns the X coordinate of the centre of the image's bounding rectangle.

**Returns**: centre X.

**Compatibility:** `GETCENTERX` - `PIKLIB61.DLL` ⚠️ (2/4), `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### GETCENTERY

```
INTEGER GETCENTERY()
```

Returns the Y coordinate of the centre of the image's bounding rectangle.

**Returns**: centre Y.

**Compatibility:** `GETCENTERY` - `PIKLIB61.DLL` ⚠️ (2/4), `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### GETHEIGHT

```
INTEGER GETHEIGHT()
```

Returns the image's height in pixels.

**Returns**: image height.

**Examples**

```
IMGLINA^GETHEIGHT();
```

**Compatibility:** `GETHEIGHT` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### GETPIXEL

```
INTEGER GETPIXEL(INTEGER posX, INTEGER posY)
```

Returns the pixel value at the given coordinates as an integer encoded per the image's colour depth: RGB565 for 16-bit images, RGB555 for 15-bit images.

**Parameters**

- `posX` — pixel X coordinate.
- `posY` — pixel Y coordinate.

**Returns**: the encoded pixel colour value.

**Examples**

```
IMGMASK^GETPIXEL(IKONNEWX, IKONNEWY);
```

**Compatibility:** `GETPIXEL` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### GETPOSITIONX

```
INTEGER GETPOSITIONX()
```

Returns the X coordinate of the image's top-left corner.

**Returns**: position X.

**Compatibility:** `GETPOSITIONX` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### GETPOSITIONY

```
INTEGER GETPOSITIONY()
```

Returns the Y coordinate of the image's top-left corner.

**Returns**: position Y.

**Compatibility:** `GETPOSITIONY` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### GETWIDTH

```
INTEGER GETWIDTH()
```

Returns the image's width in pixels.

**Returns**: image width.

**Examples**

```
IMGPASEK^GETWIDTH();
```

**Compatibility:** `GETWIDTH` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### HIDE

```
void HIDE()
```

Hides the image (sets [`VISIBLE`](#visible) to `FALSE`).

**Examples**

```
G_IMGPAGE^HIDE();
```

**Compatibility:** `HIDE` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### INVALIDATE

```
void INVALIDATE()
```

Applies the pending opacity value previously set with [`SETOPACITY`](#setopacity). Without `INVALIDATE`, the opacity change does not become visible.

**Examples**

```
G_IMGPAGE^INVALIDATE();
```

**Compatibility:** `INVALIDATE` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### ISAT

```
BOOL ISAT(INTEGER posX, INTEGER posY)
```

Checks whether the point at the given coordinates lies inside the image's bounding rectangle.

**Parameters**

- `posX` — point X coordinate.
- `posY` — point Y coordinate.

**Returns**: [`BOOL`](BOOL.md) — `TRUE` if the point is inside the image's rectangle.

**Compatibility:** `ISAT` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ❌, `PIKLIB8.DLL` ⚠️ (8/10), `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### LOAD

```
void LOAD(STRING path)
```

Loads an image from a file, replacing the previous contents. After loading, the image is automatically shown ([`VISIBLE`](#visible) is set to `TRUE`).

**Parameters**

- `path` — `.IMG` file path in the game's VFS.

**Examples**

```
G_IMGPAGE^LOAD("$COMMON\PAGE.IMG");
IMGTLO^LOAD(VARSTEMP0);
IMGSCR^LOAD(["$COMMON\SAVE_BD\BD_SCR"+VARIACTIVESLOT+".IMG"]);
```

**Compatibility:** `LOAD` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### MERGEALPHA

```
void MERGEALPHA(INTEGER posX, INTEGER posY, STRING maskName)
```

Binds an alpha mask sourced from another image to this image. The mask is positioned at `(posX, posY)` and modifies the resulting transparency of the current image in the overlapping area.

**Parameters**

- `posX` — mask X position.
- `posY` — mask Y position.
- `maskName` — name of the `IMAGE` variable whose alpha channel will be used as the mask.

**Examples**

```
IMGDARK^MERGEALPHA([ANNPLAYER0^GETCENTERX()-75], [ANNPLAYER0^GETCENTERY()-75], "IMGKOLKO");
IMG_WODA^MERGEALPHA(800, VARI_Y, "IMG_LIGHT");
```

**Compatibility:** `MERGEALPHA` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### MONITORCOLLISION {#monitorcollision-1}

```
void MONITORCOLLISION()
```

Enables collision monitoring between this image and other objects. After the call, the [`MONITORCOLLISION`](#monitorcollision) field is `TRUE` and the image is registered with the collision-detection mechanism.

**Compatibility:** `MONITORCOLLISION` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### MONITORCOLLISIONALPHA {#monitorcollisionalpha-1}

```
void MONITORCOLLISIONALPHA()
```

Enables alpha-channel awareness in collision detection. After the call, the [`MONITORCOLLISIONALPHA`](#monitorcollisionalpha) field is `TRUE`.

**Compatibility:** `MONITORCOLLISIONALPHA` - not present in the libraries analysed in `compat.json`.

### MOVE

```
void MOVE(INTEGER offsetX, INTEGER offsetY)
```

Moves the image by the given offsets relative to its current position.

**Parameters**

- `offsetX` — X offset.
- `offsetY` — Y offset.

**Examples**

```
IMGBKGA^MOVE(0, 800);
IMGLINA^MOVE(0, IMOVDY);
IMRECT^MOVE(IGSX, 0);
```

**Compatibility:** `MOVE` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### REMOVEMONITORCOLLISION

```
void REMOVEMONITORCOLLISION()
```

Disables collision monitoring previously enabled by [`MONITORCOLLISION`](#monitorcollision-1).

**Compatibility:** `REMOVEMONITORCOLLISION` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### REMOVEMONITORCOLLISIONALPHA

```
void REMOVEMONITORCOLLISIONALPHA()
```

Disables alpha-channel awareness in collision detection, previously enabled by [`MONITORCOLLISIONALPHA`](#monitorcollisionalpha-1).

**Compatibility:** `REMOVEMONITORCOLLISIONALPHA` - not present in the libraries analysed in `compat.json`.

### SETCLIPPING

```
void SETCLIPPING(INTEGER xLeft, INTEGER yBottom, INTEGER xRight, INTEGER yTop)
```

Restricts the image's drawing area to the rectangle defined by the given edges. Pixels outside the rectangle are not drawn.

**Parameters**

- `xLeft, yBottom, xRight, yTop` — coordinates of the clipping rectangle.

**Examples**

```
G_IMGPAGE^SETCLIPPING(0, 0, G_IPAGE, 600);
```

**Compatibility:** `SETCLIPPING` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### SETOPACITY

```
void SETOPACITY(INTEGER opacity)
```

Sets the pending opacity value in the `0–255` scale (`0` — fully transparent, `255` — fully opaque). Out-of-range values are clamped. **The change does not become visible until [`INVALIDATE`](#invalidate) is called.**

**Parameters**

- `opacity` — alpha-channel value (`0–255`).

**Examples**

```
AIDEMMEDIA^SETOPACITY(VARNR);
IMGBRIDGE^SETOPACITY(200);
```

**Compatibility:** `SETOPACITY` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### SETPOSITION

```
void SETPOSITION(INTEGER posX, INTEGER posY)
```

Sets the absolute position of the image's top-left corner.

**Parameters**

- `posX` — X coordinate.
- `posY` — Y coordinate.

**Examples**

```
IMGENERGIA^SETPOSITION([795-VARENERGIA], 78);
IMGKOLKO^SETPOSITION(-500, -500);
IMGNAKLADKA^SETPOSITION(VARIPOSX, 0);
```

**Compatibility:** `SETPOSITION` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### SETPRIORITY

```
void SETPRIORITY(INTEGER priority)
```

Sets the image's rendering priority.

**Parameters**

- `priority` — the new value of the [`PRIORITY`](#priority) field.

**Examples**

```
G_IMGPAGE^SETPRIORITY(3000);
AIDEMMEDIA^SETPRIORITY(3);
```

**Compatibility:** `SETPRIORITY` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### SHOW

```
void SHOW()
```

Shows the image (sets [`VISIBLE`](#visible) to `TRUE`).

**Examples**

```
G_IMGPAGE^SHOW();
REX^SHOW();
```

**Compatibility:** `SHOW` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

## Signals

### ONCLICK

Fired when the image is clicked.

### ONFOCUSON

Fired when the cursor moves onto the image.

### ONFOCUSOFF

Fired when the cursor moves off the image.

### ONINIT

Fired when the object is initialised.
