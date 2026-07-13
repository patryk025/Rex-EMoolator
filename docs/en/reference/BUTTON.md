# BUTTON

An interactive button with three visual states (normal, hovered, pressed) and separate graphics and sounds for each. A button can also be disabled — either completely (`DISABLE`) or while remaining visible (`DISABLEBUTVISIBLE`).

Internally the button is a small state machine. Every state transition automatically shows or hides the appropriate graphics, plays the linked sound, and may emit a signal.

## Button states

| State | Meaning |
| --- | --- |
| `STANDARD` | at rest — the `GFXSTANDARD` graphic is shown. |
| `HOVERED` | the cursor is over the button — the `GFXONMOVE` graphic is shown (or `GFXSTANDARD` if unset). |
| `PRESSED` | the button is being pressed — the `GFXONCLICK` graphic is shown (or `GFXSTANDARD` if unset). |
| `DISABLED` | the button is disabled and hidden. |
| `DISABLED_BUT_VISIBLE` | the button is disabled but the `GFXSTANDARD` graphic is shown. |

## Fields

### DRAG

```
STRING DRAG
```

Name of the [`ANIMO`](ANIMO.md) or [`IMAGE`](IMAGE.md) variable moved while dragging. This field is optional. If it does not resolve to a graphics object, the engine falls back to [`GFXSTANDARD`](#gfxstandard), then [`GFXONMOVE`](#gfxonmove).

### DRAGGABLE

```
BOOL DRAGGABLE
```

Whether the button can be dragged.

### ENABLE

```
BOOL ENABLE
```

Whether the button is enabled after initialisation. A value of `FALSE` puts the button into the [`DISABLED`](#button-states) state.

### GFXONCLICK

```
STRING GFXONCLICK
```

Name of the [`ANIMO`](ANIMO.md) or [`IMAGE`](IMAGE.md) variable shown in the [`PRESSED`](#button-states) state. Optional — if unset, `GFXSTANDARD` keeps being shown while pressed.

### GFXONMOVE

```
STRING GFXONMOVE
```

Name of the [`ANIMO`](ANIMO.md) or [`IMAGE`](IMAGE.md) variable shown in the [`HOVERED`](#button-states) state. Optional.

### GFXSTANDARD

```
STRING GFXSTANDARD
```

Name of the [`ANIMO`](ANIMO.md) or [`IMAGE`](IMAGE.md) variable shown at rest. If [`ENABLE`](#enable) is `FALSE`, the graphic is hidden after initialisation — with one exception: if the linked animation is already playing, it stays visible (example: the torch in `16BLAWA` from *Reksio i Skarb Piratów*). In that case the linked graphic's [`TOCANVAS`](ANIMO.md#tocanvas) setting is ignored.

### RECT

```
INTEGER, INTEGER, INTEGER, INTEGER RECT
STRING RECT
```

The cursor hit area. Two forms are accepted:

- Four integers `xLeft,yBottom,xRight,yTop` defining a rectangle on screen.
- The name of an [`ANIMO`](ANIMO.md) or [`IMAGE`](IMAGE.md) variable whose current graphic rectangle will be used.

If unset, the rectangle of the [`GFXSTANDARD`](#gfxstandard) variable is used.

### SNDONCLICK

```
STRING SNDONCLICK
```

Name of the [`SOUND`](SOUND.md) variable played on entering the [`PRESSED`](#button-states) state.

### SNDONMOVE

```
STRING SNDONMOVE
```

Name of the [`SOUND`](SOUND.md) variable played on entering the [`HOVERED`](#button-states) state.

### SNDSTANDARD

```
STRING SNDSTANDARD
```

Name of the [`SOUND`](SOUND.md) variable played on returning to the [`STANDARD`](#button-states) state.

### VISIBLE

```
BOOL VISIBLE
```

Persisted in script data but not observed by the engine — testing shows this field has no effect on button behaviour.

## Methods

### DISABLE

```
void DISABLE()
```

Disables the button and hides all linked graphics ([`GFXSTANDARD`](#gfxstandard), [`GFXONMOVE`](#gfxonmove), [`GFXONCLICK`](#gfxonclick)). Graphics linked via [`RECT`](#rect) are unaffected.

**Examples**

```
B_GLOBAL_PAUSE^DISABLE();
```

**Compatibility:** `DISABLE` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### DISABLEBUTVISIBLE

```
void DISABLEBUTVISIBLE()
```

Disables the button but keeps the [`GFXSTANDARD`](#gfxstandard) graphic visible (like `DISABLE`, but the final state is [`DISABLED_BUT_VISIBLE`](#button-states)).

**Examples**

```
BTNBONE^DISABLEBUTVISIBLE();
BTNFORGOT^DISABLEBUTVISIBLE();
```

**Compatibility:** `DISABLEBUTVISIBLE` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### ENABLE

```
void ENABLE()
```

Enables the button and restores visibility of the [`GFXSTANDARD`](#gfxstandard) graphic. Calling this on an already-enabled button has no effect.

**Examples**

```
B_GLOBAL_PAUSE^ENABLE();
BTNEXIT^ENABLE();
```

**Compatibility:** `ENABLE` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### ENABLEDRAGGING

```
void ENABLEDRAGGING()
```

Enables dragging, equivalent to setting [`DRAGGABLE`](#draggable) to `TRUE`.

**Compatibility:** `ENABLEDRAGGING` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### DISABLEDRAGGING

```
void DISABLEDRAGGING()
```

Prevents new drag operations from starting.

**Compatibility:** `DISABLEDRAGGING` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### SETPRIORITY

```
void SETPRIORITY(INTEGER posZ)
```

Sets the rendering priority (Z position) of all three linked graphics ([`GFXSTANDARD`](#gfxstandard), [`GFXONMOVE`](#gfxonmove), [`GFXONCLICK`](#gfxonclick)). Higher values are drawn on top.

**Parameters**

- `posZ` — new priority value.

**Examples**

```
B_GLOBAL_PAUSE^SETPRIORITY(5001);
BTNNULLFADE^SETPRIORITY(40000);
```

**Compatibility:** `SETPRIORITY` - `PIKLIB61.DLL` ⚠️ (2/4), `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### SETRECT

```
void SETRECT(STRING varName)
void SETRECT(INTEGER xLeft, INTEGER yBottom, INTEGER xRight, INTEGER yTop)
```

Sets the cursor hit area. The first form copies the rectangle from a graphics variable; the second specifies it directly.

**Parameters**

- `varName` — name of the graphics variable **(form 1)**.
- `xLeft`, `yBottom`, `xRight`, `yTop` — rectangle coordinates **(form 2)**.

**Examples**

```
EXITPROGAM^SETRECT("ANNEXIT");
*STMP0^SETRECT([ITMPX+[ITMPDX*_I_]],[ITMPY+[ITMPDY*_I_]],[ITMPX+15+[ITMPDX*_I_]],[ITMPY+15+[ITMPDY*_I_]]);
```

**Compatibility:** `SETRECT` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### SETSTD

```
void SETSTD(STRING varName)
void SETSTD(STRING varName, BOOL flag)
```

Sets the button's standard graphic ([`GFXSTANDARD`](#gfxstandard)) and zeroes its priority. Shipping scripts also use a two-argument form whose boolean flag is always `FALSE`; its meaning has not been established.

**Parameters**

- `varName` — name of the new standard graphic variable (an empty string `""` clears the link).
- `flag` — configuration flag **(form 2, meaning unknown)**.

**Examples**

```
BTNEXIT^SETSTD("ANNOBJECT2");
B_GLOBAL_PAUSE^SETSTD("",FALSE);
BTNBAD^SETSTD("ZLY",FALSE);
```

**Compatibility:** `SETSTD` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

## Signals

### ONINIT

Fired when the object is initialised.

### ONFOCUSON

Fired on the transition from [`STANDARD`](#button-states) to [`HOVERED`](#button-states) — i.e. when the cursor enters the button area.

### ONFOCUSOFF

Fired on the transition from [`HOVERED`](#button-states) to [`STANDARD`](#button-states) — when the cursor leaves the button.

### ONCLICKED

Fired on the transition into [`PRESSED`](#button-states) (the mouse button has been pressed).

### ONRELEASED

Fired on the transition from [`PRESSED`](#button-states) to [`HOVERED`](#button-states) — when the mouse button is released over the button area.

### ONACTION

Fired together with [`ONRELEASED`](#onreleased) — confirms that a full click (press and release on the button area) has happened.

### ONSTARTDRAGGING

Fired when dragging begins (only for buttons with [`DRAGGABLE`](#draggable) set to `TRUE`). At this point [`SCENE^GETDRAGGEDNAME()`](SCENE.md#getdraggedname) returns the moved graphics object's name.

### ONDRAGGING

Fired while the graphics object is moving whenever the cursor position changes.

### ONENDDRAGGING

Fired when dragging ends.

### ONSIGNAL

Fired when a signal arrives (see [Events and signals](../engine/events.md#onsignal)).
