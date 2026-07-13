# DOUBLE

Double-precision floating-point number.

## Fields

### TOINI

```
BOOL TOINI
```

Controls whether the field's value is persisted to an INI file and restored on the next run.

### VALUE

```
DOUBLE VALUE
```

The current value of the variable. Accepted notations include standard decimal (e.g. `1.234`) and scientific with the letter `e` or `d` (e.g. `1.23e4`, `1.23d4`).

## Methods

### ABS

```
DOUBLE ABS(DOUBLE value)
```

Stores the absolute value of the argument in the variable and returns it.

**Parameters**

- `value` — the number whose absolute value will be stored.

**Returns**: the new value of the variable.

**Examples**

```
VARDTMP2^ABS(VARDTMP2);
DKIERUNEKY^ABS(DKIERUNEKY);
```

**Compatibility:** `ABS` - `PIKLIB61.DLL` ⚠️ (2/4), `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### ADD

```
DOUBLE ADD(DOUBLE addend)
```

Adds the argument to the variable's current value, stores the result, and returns it.

**Parameters**

- `addend` — the value to add.

**Returns**: the new value of the variable.

**Examples**

```
VARDMENUOPACITY^ADD([42.5*VARIMENUVISIBLE]);
VARDTIME^ADD(1.0);
STREX|DPOSX^ADD(STREX|FORCEX);
```

**Compatibility:** `ADD` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### ARCTAN

```
DOUBLE ARCTAN(DOUBLE value)
```

Stores the arctangent of the argument, expressed in degrees, in the variable and returns it. The argument is treated as a number (a tangent value), not as an angle.

**Parameters**

- `value` — the number whose arctangent is computed.

**Returns**: the new value of the variable (in degrees).

**Examples**

```
VARDTMP1^ARCTAN(VARDTMP1);
```

**Compatibility:** `ARCTAN` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### ARCTANEX

```
DOUBLE ARCTANEX(DOUBLE y, DOUBLE x)
```

Stores the value of `atan2(y, x)` expressed in degrees in the variable and returns it. This is the angle of the vector `(x, y)` measured from the positive `OX` axis.

**Parameters**

- `y` — the first vector component.
- `x` — the second vector component.

**Returns**: the new value of the variable (in degrees).

**Examples**

```
VARDTEMP1^ARCTANEX(VARIDIRY, VARIDIRX);
VARDTEMP2^ARCTANEX(VREFLECT^GET(1), VREFLECT^GET(0));
```

**Compatibility:** `ARCTANEX` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### CLAMP

```
DOUBLE CLAMP(DOUBLE rangeMin, DOUBLE rangeMax)
```

Clamps the variable's current value to the inclusive range `[rangeMin, rangeMax]`. Values outside the range are pinned to its bounds.

**Parameters**

- `rangeMin` — lower bound of the range (inclusive).
- `rangeMax` — upper bound of the range (inclusive).

**Returns**: the new value of the variable.

**Examples**

```
D3^CLAMP(0.5, 2.5);
VARDTMP1^CLAMP(-15.0, 15.0);
DKONSPEED^CLAMP(0.0, DKONSPEEDMAX);
```

**Compatibility:** `CLAMP` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### CLEAR

```
DOUBLE CLEAR()
```

Sets the variable's value to `0.0` and returns it.

**Returns**: `0.0`.

**Compatibility:** `CLEAR` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### COSINUS

```
DOUBLE COSINUS(DOUBLE angle)
```

Stores the cosine of the given angle in the variable and returns it. The angle is given in degrees.

**Parameters**

- `angle` — the angle in degrees.

**Returns**: the new value of the variable.

**Examples**

```
VARDTEMP0^COSINUS(VARDANGLE);
VARDTEMP1^COSINUS(ARRANGLE^GET(VARPLAYER));
```

**Compatibility:** `COSINUS` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### DEC

```
DOUBLE DEC()
```

Decrements the variable's value by `1.0`.

**Returns**: the new value of the variable.

**Compatibility:** `DEC` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### DIV

```
DOUBLE DIV(DOUBLE divisor)
```

Divides the variable's current value by the argument, stores the result, and returns it. Division by zero leaves the variable unchanged.

**Parameters**

- `divisor` — the divisor.

**Returns**: the new value of the variable (or the unchanged value if `divisor` was `0.0`).

**Examples**

```
VARDTEMP0^DIV(ARRSPEEDFACTOR^GET(0));
DKONSPEED^DIV(6.0);
VARDTMP2^DIV(15);
```

**Compatibility:** `DIV` - `PIKLIB61.DLL` ⚠️ (3/4), `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### GET

```
DOUBLE GET()
```

Returns the current value of the variable.

**Returns**: the current value of the `VALUE` field.

**Compatibility:** `GET` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### INC

```
DOUBLE INC()
```

Increments the variable's value by `1.0`.

**Returns**: the new value of the variable.

**Compatibility:** `INC` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### LENGTH

```
DOUBLE LENGTH(DOUBLE x, DOUBLE y)
```

Computes the length of the vector `(x, y)` as `sqrt(x² + y²)`, stores it, and returns it.

**Parameters**

- `x` — the first vector component.
- `y` — the second vector component.

**Returns**: the vector length.

**Examples**

```
VARDTEMP0^LENGTH(VARIDIRX, VARIDIRY);
```

**Compatibility:** `LENGTH` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### LOG

```
DOUBLE LOG(DOUBLE value)
```

Stores the natural logarithm of the argument in the variable and returns it.

**Parameters**

- `value` — the number whose logarithm is computed.

**Returns**: the new value of the variable.

**Compatibility:** `LOG` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ❌, `PIKLIB8.DLL` ❌, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ⚠️ (4/5).

### MAXA

```
DOUBLE MAXA(DOUBLE value1, [DOUBLE value2, ..., DOUBLE valueN])
```

Picks the maximum of the given arguments, stores it, and returns it. Requires at least one argument.

**Parameters**

- `value1, …, valueN` — the values to choose from.

**Returns**: the largest of the given values.

**Examples**

```
VARDPOWER^MAXA(0.0, VARDPOWER);
```

**Compatibility:** `MAXA` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### MINA

```
DOUBLE MINA(DOUBLE value1, [DOUBLE value2, ..., DOUBLE valueN])
```

Picks the minimum of the given arguments, stores it, and returns it. Requires at least one argument.

**Parameters**

- `value1, …, valueN` — the values to choose from.

**Returns**: the smallest of the given values.

**Examples**

```
VARDPOWER^MINA(VARDPOWER, 9.0);
```

**Compatibility:** `MINA` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### MOD

```
DOUBLE MOD(DOUBLE divisor)
```

Computes the remainder of dividing the variable's current value by the argument, truncates the fractional part to an integer, stores it, and returns it. Division by zero leaves the variable unchanged.

**Parameters**

- `divisor` — the divisor.

**Returns**: the new value of the variable (or the unchanged value if `divisor` was `0.0`).

**Compatibility:** `MOD` - `PIKLIB61.DLL` ⚠️ (3/4), `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### MUL

```
DOUBLE MUL(DOUBLE multiplier)
```

Multiplies the variable's current value by the argument, stores the result, and returns it.

**Parameters**

- `multiplier` — the multiplier.

**Returns**: the new value of the variable.

**Examples**

```
STPLAYER|FORCEX^MUL(0.75);
VARCATFORCEX^MUL(1000000);
STREX|FORCEX^MUL(STREX|DEFIANCE);
```

**Compatibility:** `MUL` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### RESETINI

```
DOUBLE RESETINI()
```

Resets the variable's value to the reset value defined in the object's script attributes. The engine looks up the value in the following order: `DEFAULT` → `INIT_VALUE` → `VALUE`; the first one found is used. If none of them is set, the value is reset to `0.0`.

**Returns**: the new value of the variable.

**Compatibility:** `RESETINI` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### SET

```
DOUBLE SET(DOUBLE value)
```

Sets the variable's value.

**Parameters**

- `value` — the new value.

**Returns**: the new value of the variable.

**Examples**

```
VARDMAXVEL^SET(300.0);
VARDMAXVELKRET^SET([0.6*VARDMAXVEL]);
VARD_KRETSPEED^SET($1);
```

**Compatibility:** `SET` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### SGN

```
INTEGER SGN()
```

Returns the sign of the variable's current value: `-1` for negative values, `1` for positive, `0` for zero. This method does not modify the variable, and is the only method on this type that returns an [`INTEGER`](INTEGER.md) rather than a `DOUBLE`.

**Returns**: the sign of the variable's value (`-1`, `0`, or `1`).

**Compatibility:** `SGN` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ❌, `PIKLIB8.DLL` ⚠️ (8/10), `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### SINUS

```
DOUBLE SINUS(DOUBLE angle)
```

Stores the sine of the given angle in the variable and returns it. The angle is given in degrees.

**Parameters**

- `angle` — the angle in degrees.

**Returns**: the new value of the variable.

**Examples**

```
VARDTEMP1^SINUS(VARDANGLE);
VARDTEMP2^SINUS(ARRANGLE^GET(VARPLAYER));
```

**Compatibility:** `SINUS` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### SQRT

```
DOUBLE SQRT()
DOUBLE SQRT(DOUBLE value)
```

Stores the square root in the variable and returns it.

- Without an argument, the square root of the variable's current value is taken.
- With an argument, the square root of the argument is taken.

**Parameters**

- `value` — (optional) the number whose square root is computed.

**Returns**: the new value of the variable.

**Examples**

```
VARDODLEGLOSC^SQRT(VARDODLEGLOSC);
```

**Compatibility:** `SQRT` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### SUB

```
DOUBLE SUB(DOUBLE subtrahend)
```

Subtracts the argument from the variable's current value, stores the result, and returns it.

**Parameters**

- `subtrahend` — the value to subtract.

**Returns**: the new value of the variable.

**Examples**

```
VARDANGLE^SUB(VARDTEMP2);
DKONSPEED^SUB([DKONACCELERATION*D3]);
```

**Compatibility:** `SUB` - `PIKLIB61.DLL` ⚠️ (3/4), `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### SWITCH

```
DOUBLE SWITCH(DOUBLE valueA, DOUBLE valueB)
```

If the variable's current value equals `valueA`, assigns `valueB` to it; otherwise assigns `valueA`. Useful for alternating between two values.

**Parameters**

- `valueA` — the first value.
- `valueB` — the second value.

**Returns**: the new value of the variable.

**Compatibility:** `SWITCH` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

## Signals

### ONCHANGED

Fired when the variable's value is changed to one different from the previous one.

### ONBRUTALCHANGED

Fired on every call that sets the value, regardless of whether the new value differs from the previous one.
