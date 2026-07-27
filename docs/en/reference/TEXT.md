# TEXT

A text element rendered on screen. Uses a font ([`FONT`](FONT.md)) referenced through the [`FONT`](#font) field; content, position, and alignment are configured by the remaining fields.

## Fields

### FONT

```
STRING FONT
```

Name of the [`FONT`](FONT.md) variable from which character textures are taken.

### HJUSTIFY

```
STRING HJUSTIFY
```

Horizontal alignment inside the `RECT` rectangle. Accepted values: `LEFT`, `RIGHT`, `CENTER`.

### PRIORITY

```
INTEGER PRIORITY
```

The text's rendering priority (`Z`) relative to other scene objects.

### RECT

```
INTEGER,INTEGER,INTEGER,INTEGER RECT
```

The rectangle in which the text is drawn — four comma-separated integers:
`xLeft, yTop, xRight, yBottom`. In a script, the field can also reference a
variable of type [`ANIMO`](index.md) or [`IMAGE`](IMAGE.md), in which case its
bounds are taken from that object.

### TEXT

```
STRING TEXT
```

The displayed text. Modified through [`SETTEXT`](#settext).

The `|` character and CR start a new line, while a bare LF is ignored. Text
wraps at word boundaries to the width of [`RECT`](#rect). Metrics and pair
adjustments come directly from the [FNT format](../formats/FNT.md).

Piklib can change the color of a text span with a `<COLORn>` code, where `n`
is the decimal value of an RGB555 or RGB565 color. For example,
`<COLOR63488>` is red in RGB565. Values containing commas inside a `SETTEXT`
argument are split by the script parser, so the numeric form should be used
there.

### TOCANVAS

```
BOOL TOCANVAS
```

Whether the text is rendered on the scene's main canvas. If `FALSE`, the text is not visible regardless of `VISIBLE`.

### VISIBLE

```
BOOL VISIBLE
```

The text's visibility. Modified through [`SHOW`](#show) and [`HIDE`](#hide).

### VJUSTIFY

```
STRING VJUSTIFY
```

Vertical alignment inside the `RECT` rectangle. Accepted values: `TOP`, `BOTTOM`, `CENTER`.

## Methods

### HIDE

```
void HIDE()
```

Hides the text (sets [`VISIBLE`](#visible) to `FALSE`).

**Compatibility:** `HIDE` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### SETCOLOR

```
void SETCOLOR(INTEGER red, INTEGER green, INTEGER blue)
```

Sets the base text color. Components range from 0 to 255.

```
LABEL^SETCOLOR(255, 0, 0);
```

**Compatibility:** `PIKLIB8.DLL` ✅.

### SETJUSTIFY

```
void SETJUSTIFY(INTEGER xLeft, INTEGER yTop, INTEGER xRight, INTEGER yBottom, STRING hJustify, STRING vJustify)
```

Sets the drawing rectangle ([`RECT`](#rect)) and the horizontal ([`HJUSTIFY`](#hjustify)) and vertical ([`VJUSTIFY`](#vjustify)) alignment in a single call.

**Parameters**

- `xLeft, yTop, xRight, yBottom` — rectangle coordinates.
- `hJustify` — horizontal alignment (`LEFT`, `RIGHT`, `CENTER`).
- `vJustify` — vertical alignment (`TOP`, `BOTTOM`, `CENTER`).

**Compatibility:** `SETJUSTIFY` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### SETPRIORITY

```
void SETPRIORITY(INTEGER priority)
```

Sets the text's rendering priority.

**Parameters**

- `priority` — the new value of the [`PRIORITY`](#priority) field.

**Compatibility:** `SETPRIORITY` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### SETTEXT

```
void SETTEXT(STRING text)
```

Changes the displayed text.

**Parameters**

- `text` — the new value of the [`TEXT`](#text) field.

**Examples**

```
TXTDEBUG^SETTEXT(ARRPX^GETSIZE());
TXTDEBUG^SETTEXT("SAVED");
```

**Compatibility:** `SETTEXT` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### SHOW

```
void SHOW()
```

Shows the text (sets [`VISIBLE`](#visible) to `TRUE`).

**Compatibility:** `SHOW` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

## Signals

### ONINIT

Fired when the object is initialised.
