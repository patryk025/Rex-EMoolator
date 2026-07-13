# TIMER

A cyclic time counter that emits the [`ONTICK`](#ontick) signal every `ELAPSE` milliseconds. Lets scripts run logic at regular intervals — nothing involving the passage of time (animated values, delays, regeneration) can be done in the engine without one.

The variable's value is the number of ticks emitted so far.

## Fields

### ELAPSE

```
INTEGER ELAPSE
```

Tick interval in milliseconds. For values of `0` or less the timer does not tick even when enabled.

### ENABLED

```
BOOL ENABLED
```

Whether the timer is active after initialisation. Defaults to `TRUE`.

### TICKS

```
INTEGER TICKS
```

Tick limit — once that many ticks have been emitted, the timer disables itself (`ENABLED = FALSE`). A value of `0` removes the limit (the timer ticks indefinitely).

## Methods

### DISABLE

```
void DISABLE()
```

Disables the timer. [`ONTICK`](#ontick) is no longer emitted; the current tick count is preserved.

**Examples**

```
TIMER1^DISABLE();
```

**Compatibility:** `DISABLE` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### ENABLE

```
void ENABLE()
```

Enables the timer and resets the reference time to *now*. The first tick after the call fires only after a full `ELAPSE` has passed, regardless of how long the timer was disabled.

**Examples**

```
TIMCNV^ENABLE();
TIMER2^ENABLE();
```

**Compatibility:** `ENABLE` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### GETTICKS

```
INTEGER GETTICKS()
```

Returns the number of ticks emitted so far (counted from the last [`RESET`](#reset) or initialisation).

**Returns**: [`INTEGER`](INTEGER.md) — the tick count.

**Examples**

```
TIMER1^GETTICKS();
```

**Compatibility:** `GETTICKS` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### RESET

```
void RESET()
```

Zeros the tick count and resets the reference time to *now*.

**Examples**

```
TIMER1^RESET();
```

**Compatibility:** `RESET` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### SET

```
void SET(INTEGER ticks)
```

Sets the [`TICKS`](#ticks) limit. A value of `0` removes the limit.

**Parameters**

- `ticks` — the new tick limit.

**Compatibility:** `SET` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### SETELAPSE

```
void SETELAPSE(INTEGER timeMs)
```

Sets the tick interval in milliseconds and resets the reference time to *now*.

**Parameters**

- `timeMs` — the new tick interval (in milliseconds).

**Examples**

```
TIMERSEQ^SETELAPSE(RANDOM^GET(30000,10000));
TIMERPIECHUR^SETELAPSE(ARRAYPIECHURZYPARAM^GET(0));
TIMER1^SETELAPSE(VARTIMEUFO);
```

**Compatibility:** `SETELAPSE` - `PIKLIB61.DLL` ⚠️ (2/4), `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

## Signals

### ONINIT

Fired when the object is initialised.

### ONTICK

Fired after each full [`ELAPSE`](#elapse) cycle, provided the timer is enabled. The argument (`$1`) is the current tick count (`currentTickCount`).

### ONSIGNAL

Fired when a signal arrives (see [Events and signals](../engine/events.md#onsignal)).
