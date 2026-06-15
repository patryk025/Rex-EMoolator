# KEYBOARD

The built-in object representing keyboard state. Available under the global name `KEYBOARD` from any context (see [Built-in objects](../engine/globals.md#built-in-objects)). Handles key-down and key-up events, including auto-repeat mode.

## Methods

### DISABLE

```
void DISABLE()
```

Disables keyboard event handling — key signals stop being fired.

**Examples**

```
KEYBOARD^DISABLE();
```

### ENABLE

```
void ENABLE()
```

Enables keyboard event handling.

**Examples**

```
KEYBOARD^ENABLE();
```

### GETLATESTKEY

```
STRING GETLATESTKEY()
```

Returns the name of the most recently pressed key.

**Returns**: the key name in the format accepted by [`ISKEYDOWN`](#iskeydown) (see [Supported keys](#supported-keys)).

**Examples**

```
KEYBOARD^GETLATESTKEY();
```

### ISENABLED

```
BOOL ISENABLED()
```

Checks whether keyboard handling is enabled.

**Returns**: [`BOOL`](BOOL.md) — `TRUE` if the keyboard is responding to events.

**Examples**

```
KEYBOARD^ISENABLED();
```

### ISKEYDOWN

```
BOOL ISKEYDOWN(STRING keyName)
```

Checks whether the given key is currently held down.

**Parameters**

- `keyName` — the key name (see [Supported keys](#supported-keys)).

**Returns**: [`BOOL`](BOOL.md) — `TRUE` if the key is held down. For an unknown key name, `FALSE` is returned.

**Examples**

```
KEYBOARD^ISKEYDOWN("UP");
KEYBOARD^ISKEYDOWN("LEFT");
KEYBOARD^ISKEYDOWN(ARRAYKEYBOARD^GET(0));
```

### SETAUTOREPEAT

```
void SETAUTOREPEAT(BOOL autorepeat)
```

Sets whether the [`ONKEYDOWN`](#onkeydown) signal is fired repeatedly as long as the key remains held down. Disabled by default.

**Parameters**

- `autorepeat` — `TRUE` to enable repeat firing.

**Examples**

```
KEYBOARD^SETAUTOREPEAT(FALSE);
```

## Signals

### ONKEYDOWN

Fired when a key is pressed. The signal is [parameterised](../engine/events.md#parameterised-signals) by the key name — separate handlers can be attached for each:

```
KEYBOARD:ONKEYDOWN^UP=BEHGOUP
KEYBOARD:ONKEYDOWN^DOWN=BEHGODOWN
```

When auto-repeat is enabled ([`SETAUTOREPEAT(TRUE)`](#setautorepeat)), the signal is fired on every frame in which the key remains held down.

### ONKEYUP

Fired when a key is released. The signal is parameterised by the key name, analogously to [`ONKEYDOWN`](#onkeydown).

### ONCHAR

Fired for every character produced by a keypress. The signal is parameterised by the key name.

## Supported keys

The engine's keyboard recognises the following key names:

- **Function keys**: `F1`, `F2`, `F3`, `F4`, `F5`, `F6`, `F7`, `F8`, `F9`, `F10`, `F11`, `F12`
- **Arrows**: `UP`, `DOWN`, `LEFT`, `RIGHT`
- **Modifiers**: `LSHIFT`, `RSHIFT`, `LCTRL`, `RCTRL`, `LALT`, `RALT`, `CAPSLOCK`
- **Special**: `ESC`, `ENTER`, `SPACE`, `TAB`, `INSERT`, `PGUP`, `PGDN`, `HOME`
- **Letters**: `Q`, `W`, `E`, `R`, `T`, `U`, `I`, `O`, `P`, `A`, `S`, `D`, `F`, `G`, `H`, `J`, `K`, `L`, `C`, `V`, `B`, `N`, `M`
- **Digits**: `0`, `1`, `2`, `3`, `4`, `5`, `6`, `7`, `8`, `9`
