# MOUSE

The built-in object representing mouse state. Available under the global name `MOUSE` from any context (see [Built-in objects](../engine/globals.md#built-in-objects)). Exposes the cursor position, button state, and reactive click, move, and double-click events.

## Fields

### RAW

```
BOOL RAW
```

Controls whether the object reads raw mouse data (bypassing system acceleration and calibration).

## Methods

### DISABLE

```
void DISABLE()
```

Disables mouse input — the cursor stops updating its position, and no signals are emitted.

**Examples**

```
MOUSE^DISABLE();
```

**Compatibility:** `DISABLE` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### DISABLESIGNAL

```
void DISABLESIGNAL()
```

Disables mouse signal emission. Unlike [`DISABLE`](#disable), the cursor's position is still tracked, but signal handlers ([`ONMOVE`](#onmove), [`ONCLICK`](#onclick), …) are not invoked.

**Examples**

```
MOUSE^DISABLESIGNAL();
```

**Compatibility:** `DISABLESIGNAL` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### ENABLE

```
void ENABLE()
```

Enables mouse input.

**Examples**

```
MOUSE^ENABLE();
```

**Compatibility:** `ENABLE` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### ENABLESIGNAL

```
void ENABLESIGNAL()
```

Enables mouse signal emission.

**Examples**

```
MOUSE^ENABLESIGNAL();
```

**Compatibility:** `ENABLESIGNAL` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### GETPOSX

```
INTEGER GETPOSX()
```

Returns the current X coordinate of the cursor.

**Returns**: the cursor's X coordinate.

**Examples**

```
MOUSE^GETPOSX();
```

**Compatibility:** `GETPOSX` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### GETPOSY

```
INTEGER GETPOSY()
```

Returns the current Y coordinate of the cursor.

**Returns**: the cursor's Y coordinate.

**Examples**

```
MOUSE^GETPOSY();
```

**Compatibility:** `GETPOSY` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### HIDE

```
void HIDE()
```

Hides the mouse cursor.

**Examples**

```
MOUSE^HIDE();
```

**Compatibility:** `HIDE` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### ISLBUTTONDOWN

```
BOOL ISLBUTTONDOWN()
```

Checks whether the left mouse button is currently pressed.

**Returns**: [`BOOL`](BOOL.md) — `TRUE` if the button is held down.

**Examples**

```
MOUSE^ISLBUTTONDOWN();
```

**Compatibility:** `ISLBUTTONDOWN` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ❌, `PIKLIB8.DLL` ❌, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ⚠️ (4/5).

### SET

```
void SET(STRING cursorStyle)
```

Sets the cursor's style.

**Parameters**

- `cursorStyle` — the name of the cursor style (e.g. `"ACTIVE"`, `"ARROW"`).

**Examples**

```
MOUSE^SET("ACTIVE");
MOUSE^SET("ARROW");
```

**Compatibility:** `SET` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### SETPOSITION

```
void SETPOSITION(INTEGER posX, INTEGER posY)
```

Sets the cursor's position on the screen. If the position actually changes, the [`ONMOVE`](#onmove) signal is also fired.

**Parameters**

- `posX` — the new X coordinate of the cursor.
- `posY` — the new Y coordinate of the cursor.

**Examples**

```
MOUSE^SETPOSITION(400, 300);
MOUSE^SETPOSITION(MOUSE^GETPOSX(), VARINT0);
MOUSE^SETPOSITION(ANNMUCHA0^GETCENTERX(), ANNMUCHA0^GETCENTERY());
```

**Compatibility:** `SETPOSITION` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### SHOW

```
void SHOW()
```

Shows the mouse cursor.

**Compatibility:** `SHOW` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

## Signals

### ONCLICK

Fired when a mouse button is clicked. The signal is [parameterised](../engine/events.md#parameterised-signals) by the button name (`LEFT`, `RIGHT`), so separate handlers can be attached for each:

```
OBJECT_NAME:ONCLICK^LEFT=BEHLEFTCLICK
OBJECT_NAME:ONCLICK^RIGHT=BEHRIGHTCLICK
```

### ONDBLCLICK

Fired when a mouse button is double-clicked.

### ONINIT

Fired when the object is initialised.

### ONMOVE

Fired when the cursor's position changes.

### ONRELEASE

Fired when a mouse button is released.
