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

The rectangle in which the text is drawn — four comma-separated integers: `xLeft, yBottom, xRight, yTop`. In a script, the field can also reference a variable of type [`ANIMO`](index.md) or [`IMAGE`](IMAGE.md), in which case its bounds are taken from that object.

### TEXT

```
STRING TEXT
```

The displayed text. Modified through [`SETTEXT`](#settext).

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

### SETJUSTIFY

```
void SETJUSTIFY(INTEGER xLeft, INTEGER yBottom, INTEGER xRight, INTEGER yTop, STRING hJustify, STRING vJustify)
```

Sets the drawing rectangle ([`RECT`](#rect)) and the horizontal ([`HJUSTIFY`](#hjustify)) and vertical ([`VJUSTIFY`](#vjustify)) alignment in a single call.

**Parameters**

- `xLeft, yBottom, xRight, yTop` — rectangle coordinates.
- `hJustify` — horizontal alignment (`LEFT`, `RIGHT`, `CENTER`).
- `vJustify` — vertical alignment (`TOP`, `BOTTOM`, `CENTER`).

### SETPRIORITY

```
void SETPRIORITY(INTEGER priority)
```

Sets the text's rendering priority.

**Parameters**

- `priority` — the new value of the [`PRIORITY`](#priority) field.

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

### SHOW

```
void SHOW()
```

Shows the text (sets [`VISIBLE`](#visible) to `TRUE`).

## Signals

### ONINIT

Fired when the object is initialised.
