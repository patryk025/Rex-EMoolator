# INTEGER

Signed integer number.

## Fields

### TOINI

```
BOOL TOINI
```

Controls whether the field's value is persisted to an INI file and restored on the next run.

### VALUE

```
INTEGER VALUE
```

The current value of the variable.

## Methods

### ABS

```
INTEGER ABS(INTEGER value)
```

Stores the absolute value of the argument in the variable and returns it.

**Parameters**

- `value` — the number whose absolute value will be stored.

**Returns**: the new value of the variable.

**Examples**

```
VARINT0^ABS(VARINT0);
I_7^ABS(ARRFLDCLONES^GET(I_FIELD_INDEX));
```

**Compatibility:** `ABS` - `PIKLIB61.DLL` ⚠️ (2/4), `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### ADD

```
INTEGER ADD(INTEGER addend)
```

Adds the argument to the variable's current value, stores the result, and returns it.

**Parameters**

- `addend` — the value to add.

**Returns**: the new value of the variable.

**Examples**

```
VARIRADIUS^ADD([VARIMENUVISIBLE*16]);
VARIKRETSTARTX^ADD(50);
VARITEMP0^ADD(VARIRADIUS);
```

**Compatibility:** `ADD` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### AND

```
INTEGER AND(INTEGER value)
```

Performs a bitwise AND between the variable's current value and the argument, stores the result, and returns it.

**Parameters**

- `value` — the value to combine with.

**Returns**: the new value of the variable.

**Examples**

```
VARITEMP2^AND(1);
VARITEMP1^AND(ARRMASK^GET(ARRENEMYMASK^GET(VARENEMY)));
```

**Compatibility:** `AND` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### CLAMP

```
INTEGER CLAMP(INTEGER rangeMin, INTEGER rangeMax)
```

Clamps the variable's current value to the inclusive range `[rangeMin, rangeMax]`. Values outside the range are pinned to its bounds.

**Parameters**

- `rangeMin` — lower bound of the range (inclusive).
- `rangeMax` — upper bound of the range (inclusive).

**Returns**: the new value of the variable.

**Examples**

```
VARIMENUX^CLAMP(92, 708);
I1^CLAMP(0, IMIECZMAX);
IFRAMER^CLAMP(IFRAMECENTER, IFRAMEMAX);
```

**Compatibility:** `CLAMP` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### CLEAR

```
INTEGER CLEAR()
```

Sets the variable's value to `0` and returns it.

**Returns**: `0`.

**Compatibility:** `CLEAR` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### DEC

```
INTEGER DEC()
```

Decrements the variable's value by `1`.

**Returns**: the new value of the variable.

**Examples**

```
VARITIMETOEXIT^DEC();
VARIWAIT^DEC();
```

**Compatibility:** `DEC` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### DIV

```
INTEGER DIV(INTEGER divisor)
```

Divides the variable's current value (integer division) by the argument, stores the result, and returns it. Division by zero leaves the variable unchanged.

**Parameters**

- `divisor` — the divisor.

**Returns**: the new value of the variable (or the unchanged value if `divisor` was `0`).

**Examples**

```
VARITEMP0^DIV(2);
VARMOUSEDX^DIV(VARMOUSESPEED);
```

**Compatibility:** `DIV` - `PIKLIB61.DLL` ⚠️ (3/4), `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### GET

```
INTEGER GET()
```

Returns the current value of the variable.

**Returns**: the current value of the `VALUE` field.

**Compatibility:** `GET` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### INC

```
INTEGER INC()
```

Increments the variable's value by `1`.

**Returns**: the new value of the variable.

**Examples**

```
VARINUMITEMS^INC();
VARITUTCOUNT^INC();
```

**Compatibility:** `INC` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### LENGTH

```
INTEGER LENGTH(INTEGER x, INTEGER y)
```

Computes the Euclidean length of the vector `(x, y)` as `sqrt(x² + y²)`, truncates the result to an integer, stores it, and returns it.

**Parameters**

- `x` — the first vector component.
- `y` — the second vector component.

**Returns**: the vector length truncated to an integer.

**Examples**

```
VARI_TMP1^LENGTH([VARI_TMPX-VARI_CARX], [VARI_TMPY-VARI_CARY]);
I3^LENGTH(I3, 600);
```

**Compatibility:** `LENGTH` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### MOD

```
INTEGER MOD(INTEGER divisor)
```

Stores in the variable the remainder of dividing its current value by the argument. Division by zero leaves the variable unchanged.

**Parameters**

- `divisor` — the divisor.

**Returns**: the new value of the variable (or the unchanged value if `divisor` was `0`).

**Examples**

```
VARITEMP4^MOD(8);
IGC^MOD(ARLEVG^GETSIZE());
```

**Compatibility:** `MOD` - `PIKLIB61.DLL` ⚠️ (3/4), `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### MUL

```
INTEGER MUL(INTEGER multiplier)
```

Multiplies the variable's current value by the argument, stores the result, and returns it.

**Parameters**

- `multiplier` — the multiplier.

**Returns**: the new value of the variable.

**Examples**

```
VARITEMP0^MUL(34);
I1^MUL(IGRID);
```

**Compatibility:** `MUL` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### NOT

```
INTEGER NOT()
```

Performs a bitwise NOT (complement) on the variable's current value, stores the result, and returns it.

**Returns**: the new value of the variable.

**Compatibility:** `NOT` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### OR

```
INTEGER OR(INTEGER value)
```

Performs a bitwise OR between the variable's current value and the argument, stores the result, and returns it.

**Parameters**

- `value` — the value to combine with.

**Returns**: the new value of the variable.

**Compatibility:** `OR` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### POWER

```
INTEGER POWER(INTEGER exponent)
```

Raises the variable's current value to the given power, rounds the result to an integer, stores it, and returns it.

**Parameters**

- `exponent` — the exponent.

**Returns**: the new value of the variable.

**Compatibility:** `POWER` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### RANDOM

```
INTEGER RANDOM(INTEGER bound)
INTEGER RANDOM(INTEGER min, INTEGER max)
```

Stores a pseudo-random number in the variable and returns it.

- The one-argument form returns a value from `[0, bound)`.
- The two-argument form returns a value from `[min, max]` (both ends inclusive).

**Parameters**

- `bound` — upper bound (exclusive).
- `min`, `max` — range bounds (inclusive).

**Returns**: the generated random value.

**Examples**

```
VARITEMP0^RANDOM(0, 100);
VARI_TMP3^RANDOM(VARI_TMP3);
```

**Compatibility:** `RANDOM` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ❌, `PIKLIB8.DLL` ⚠️ (6/10), `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### RESETINI

```
INTEGER RESETINI()
```

Resets the variable's value to the reset value defined in the object's script attributes. The engine looks up the value in the following order: `DEFAULT` → `INIT_VALUE` → `VALUE`; the first one found is used. If none of them is set, the value is reset to `0`.

**Returns**: the new value of the variable.

**Compatibility:** `RESETINI` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### SET

```
INTEGER SET(INTEGER value)
```

Sets the variable's value.

**Parameters**

- `value` — the new value.

**Returns**: the new value of the variable.

**Examples**

```
G_IPAGE^SET(800);
VARITEMP1^SET($2);
ITEMP^SET(STCBAZA|SRODEK);
```

**Compatibility:** `SET` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### SUB

```
INTEGER SUB(INTEGER subtrahend)
```

Subtracts the argument from the variable's current value, stores the result, and returns it.

**Parameters**

- `subtrahend` — the value to subtract.

**Returns**: the new value of the variable.

**Examples**

```
G_IPAGE^SUB(100);
VARIPRIORITY^SUB(VARIBKGOFFSETY);
```

**Compatibility:** `SUB` - `PIKLIB61.DLL` ⚠️ (3/4), `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### SWITCH

```
INTEGER SWITCH(INTEGER valueA, INTEGER valueB)
```

If the variable's current value equals `valueA`, assigns `valueB` to it; otherwise assigns `valueA`. Useful for alternating between two values.

**Parameters**

- `valueA` — the first value.
- `valueB` — the second value.

**Returns**: the new value of the variable.

**Compatibility:** `SWITCH` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### XOR

```
INTEGER XOR(INTEGER value)
```

Performs a bitwise XOR between the variable's current value and the argument, stores the result, and returns it.

**Parameters**

- `value` — the value to combine with.

**Returns**: the new value of the variable.

**Compatibility:** `XOR` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

## Signals

### ONCHANGED

Fired when the variable's value is changed to one different from the previous one.

### ONBRUTALCHANGED

Fired on every call that sets the value, regardless of whether the new value differs from the previous one.

### ONINIT

Fired when the variable is initialised.

### ONSIGNAL

Fired upon receiving a signal.
