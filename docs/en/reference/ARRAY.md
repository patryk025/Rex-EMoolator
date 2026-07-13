# ARRAY

Zero-indexed array that stores values of any type. Full support for serialisation and arithmetic operations is provided for the four primitive types: [`INTEGER`](INTEGER.md), [`DOUBLE`](DOUBLE.md), [`STRING`](STRING.md), and [`BOOL`](BOOL.md). Mixed-type arrays are allowed, but some methods interpret elements in non-obvious ways (see the notes on [`FIND`](#find), [`CONTAINS`](#contains), and [`GETSUMVALUE`](#getsumvalue)).

## Fields

### TOINI

```
BOOL TOINI
```

Controls whether the array's contents are serialised to an INI file and restored on the next run.

## Methods

### ADD

```
void ADD(mixed value1, [mixed value2, ..., mixed valueN])
```

Appends the given values to the end of the array. The argument types do not have to match.

**Parameters**

- `value1, …, valueN` — values to append.

**Examples**

```
G_ARRSETTINGS^ADD(0, 600);
G_ARRDATAS^ADD("PODWIECZOREK1", "PODWIECZOREK2", "PODWIECZOREK3");
ARRFLAMESDIR^ADD("R", "R", "R", "L", "L");
ARR_JOINTS^ADD(FALSE, 6, "B", 10, "A", 4);
```

**Compatibility:** `ADD` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### ADDAT

```
void ADDAT(INTEGER index, mixed value)
```

Adds the argument to the element at position `index`. Internally, the element and the argument are converted to `DOUBLE`, summed, and stored as [`DOUBLE`](DOUBLE.md) — after this call the element at that position is always of type `DOUBLE`. Calling with `index` out of range leaves the array unchanged.

**Parameters**

- `index` — element position (`0`-based).
- `value` — value to add.

**Examples**

```
ARRIDLETIME^ADDAT(0, 1);
ARRENEMYY^ADDAT(VARITMPINDEX, VARITMP1);
ARRSPEED^ADDAT(0, 2.0);
```

**Compatibility:** `ADDAT` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### CHANGEAT

```
void CHANGEAT(INTEGER index, mixed value)
```

Replaces the element at position `index` with the given value. The type of the new element is preserved exactly as passed. Calling with `index` out of range leaves the array unchanged.

**Parameters**

- `index` — element position (`0`-based).
- `value` — the new value.

**Examples**

```
ARRIDLETIME^CHANGEAT(0, 0);
G_ARRREXSPELLS^CHANGEAT(VARIREPEATSPELL, 1);
ARRAYPLAYERSSTATE^CHANGEAT([VARCLONE-1], "NULL");
```

**Compatibility:** `CHANGEAT` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### CLAMPAT

```
void CLAMPAT(INTEGER index, mixed rangeMin, mixed rangeMax)
```

Clamps the value at position `index` to the inclusive range `[rangeMin, rangeMax]`. The element's type is preserved — `INTEGER` stays `INTEGER`, `DOUBLE` stays `DOUBLE`. For elements of any other type (and for `index` out of range) the call leaves the array unchanged.

**Parameters**

- `index` — element position (`0`-based).
- `rangeMin` — lower bound (inclusive).
- `rangeMax` — upper bound (inclusive).

**Examples**

```
ARRSPEED^CLAMPAT(VARPLAYER, 0.0, 100.0);
ARRSPEED^CLAMPAT(0, 0.0, 17.0);
```

**Compatibility:** `CLAMPAT` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### CONTAINS

```
BOOL CONTAINS(mixed needle)
```

Checks whether the array contains an element matching the given value. The comparison is done on the textual representation — `needle` is cast to [`STRING`](STRING.md) and compared with the `toDisplayString` result of each element. This differs from `FIND`, which compares values according to the [engine's typing rules](../engine/arithmetic.md#typing-rule).

**Parameters**

- `needle` — the value to search for.

**Returns**: [`BOOL`](BOOL.md) — `TRUE` if a matching element was found.

**Examples**

```
ART0^CONTAINS(ICIK);
ARRAYWARSZTATPRZEDMIOTY^CONTAINS(ARRAYTEMP^GET($1));
```

**Compatibility:** `CONTAINS` - `PIKLIB61.DLL` ⚠️ (3/4), `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### COPYTO

```
void COPYTO(STRING arrayVarName)
```

Appends the contents of this array to the end of the array named in the argument. The target array must already exist and be of type `ARRAY`. The target array is **not** cleared before copying.

**Parameters**

- `arrayVarName` — the name of the destination array variable.

**Examples**

```
ARAG^COPYTO("ARTMP");
```

**Compatibility:** `COPYTO` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ❌, `PIKLIB8.DLL` ⚠️ (5/10), `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### FIND

```
INTEGER FIND(mixed needle)
```

Searches the array for the first element equal to the given value. The comparison follows the [engine's typing rules](../engine/arithmetic.md#typing-rule) — the type of the array element in each iteration determines what type `needle` is cast to. This yields counterintuitive results in mixed-type arrays: for example, searching for `240` in an array containing `TRUE` will return the index of `TRUE`, because `240` is cast to `BOOL` (a non-zero value, which becomes `TRUE`).

**Parameters**

- `needle` — the value to search for.

**Returns**: the index of the first matching element, or `-1` if no match was found.

**Examples**

```
G_ARRCUTSCENES^FIND(G_SCUTSCENE);
ARRSTARTNAME0^FIND("NULL");
ARRCLONES^FIND(-1);
```

**Compatibility:** `FIND` - `PIKLIB61.DLL` ⚠️ (3/4), `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### GET

```
mixed GET(INTEGER index)
```

Returns the element at position `index`. For `index` out of range, returns `NULL`.

**Parameters**

- `index` — element position (`0`-based).

**Returns**: the element's value or `NULL`.

**Examples**

```
ARRIDLETIME^GET(0);
ARRACTIVESPELLS^GET(_I_);
ARRAYPLAYERSSTATE^GET([VARCLONE-1]);
```

**Compatibility:** `GET` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### GETSIZE

```
INTEGER GETSIZE()
```

Returns the number of elements in the array.

**Returns**: the array's size.

**Examples**

```
G_ARRSETTINGS^GETSIZE();
ARRENEMYROUTEX^GETSIZE();
```

**Compatibility:** `GETSIZE` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### GETSUMVALUE

```
DOUBLE GETSUMVALUE()
```

Returns the sum of all element values. Each element is cast to `DOUBLE` per the [conversion rules](../engine/arithmetic.md#type-conversions); non-numeric elements can contribute unexpected values (`BOOL` → `1.0` or `0.0`, a non-numeric [`STRING`](STRING.md) → `0.0`).

**Returns**: the sum as a [`DOUBLE`](DOUBLE.md).

**Examples**

```
ARCONTAINER^GETSUMVALUE();
```

**Compatibility:** `GETSUMVALUE` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### INSERTAT

```
void INSERTAT(INTEGER index, mixed value)
```

Inserts the value at position `index`, shifting existing elements to the right. Valid values of `index` are in `[0, size]` — inserting at the array's size appends the element at the end. Calling outside this range leaves the array unchanged.

**Parameters**

- `index` — insertion position.
- `value` — the value to insert.

**Examples**

```
ARRTURNIEJ^INSERTAT(I3, I1);
ARRTURNIEJ^INSERTAT(4, I1);
```

**Compatibility:** `INSERTAT` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### LOAD

```
void LOAD(STRING path)
```

Replaces the array's contents with data read from a binary `.ARR` file. The file is little-endian: a 4-byte element count, followed by, for each element, a 4-byte type tag (`1`=`INTEGER`, `2`=`STRING`, `3`=`BOOL`, `4`=`DOUBLE`) and the corresponding value representation.

**Parameters**

- `path` — the `.ARR` file path in the game's VFS.

**Examples**

```
G_ARRSETTINGS^LOAD("$COMMON\SETTINGS.ARR");
ARRPATH^LOAD(["MAPA"+ILEVEL+".ARR"]);
```

**Compatibility:** `LOAD` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### LOADINI

```
void LOADINI()
```

Replaces the array's contents with data deserialised from the game's INI file under the key matching this variable's name. The INI format is a comma-separated list of values:

```
ARRAY_NAME=value1,value2,value3,...
```

Each element is interpreted in order as [`INTEGER`](INTEGER.md), [`DOUBLE`](DOUBLE.md), [`BOOL`](BOOL.md), or [`STRING`](STRING.md) — the first matching type is used.

**Examples**

```
ARRAYWARSZTATMENUPRZEDMIOTY^LOADINI();
```

**Compatibility:** `LOADINI` - `PIKLIB61.DLL` ⚠️ (2/4), `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### MODAT

```
void MODAT(INTEGER index, mixed divisor)
```

Stores the remainder of dividing the element at position `index` by the argument. Internally, the element and the argument are converted to `DOUBLE`, modulo is computed, and the result is stored as [`DOUBLE`](DOUBLE.md). Division by zero, or calling with `index` out of range, leaves the array unchanged.

**Parameters**

- `index` — element position.
- `divisor` — the divisor.

**Examples**

```
ARRANGLE^MODAT(VARPLAYER, 360);
```

**Compatibility:** `MODAT` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### MULAT

```
void MULAT(INTEGER index, mixed multiplier)
```

Multiplies the element at position `index` by the argument. Internally, the element and the argument are converted to `DOUBLE`, multiplied, and the result is stored as [`DOUBLE`](DOUBLE.md). Calling with `index` out of range leaves the array unchanged.

**Parameters**

- `index` — element position.
- `multiplier` — the multiplier.

**Examples**

```
ARRDIRY^MULAT(VARPLAYER, -1.0);
ARRDIRX^MULAT(VARPLAYER, -1);
```

**Compatibility:** `MULAT` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### REMOVEALL

```
void REMOVEALL()
```

Removes all elements from the array.

**Examples**

```
G_ARRSETTINGS^REMOVEALL();
ARRTEMP^REMOVEALL();
```

**Compatibility:** `REMOVEALL` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### REMOVEAT

```
void REMOVEAT(INTEGER index)
```

Removes the element at position `index`, shifting the remaining elements to the left. Calling with `index` out of range leaves the array unchanged.

**Parameters**

- `index` — position of the element to remove.

**Examples**

```
ARRTEMP^REMOVEAT(VARITEMP2);
ARRENEMYROUTEX^REMOVEAT(0);
```

**Compatibility:** `REMOVEAT` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### REVERSEFIND

```
INTEGER REVERSEFIND(mixed needle)
```

Works like [`FIND`](#find), but scans the array from the end. The same type-dependent comparison rules apply.

**Parameters**

- `needle` — the value to search for.

**Returns**: the index of the last matching element, or `-1` if no match was found.

**Examples**

```
ARRAYKURNIKFREESLOTS^REVERSEFIND(0);
```

**Compatibility:** `REVERSEFIND` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### SAVE

```
void SAVE(STRING path)
```

Writes the array's contents to a binary `.ARR` file in the format described in [`LOAD`](#load).

**Parameters**

- `path` — the destination `.ARR` file path in the game's VFS.

**Examples**

```
G_ARRSETTINGS^SAVE("$COMMON\SETTINGS.ARR");
ARRPATH^SAVE(["MAPA"+ILEVEL+".ARR"]);
```

**Compatibility:** `SAVE` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### SAVEINI

```
void SAVEINI()
```

Serialises the array's contents to the game's INI file under the key matching this variable's name, as a comma-separated list of values (the format described in [`LOADINI`](#loadini)).

**Examples**

```
ARRAYPLATFORMOWKAPRZEDMIOTY^SAVEINI();
```

**Compatibility:** `SAVEINI` - `PIKLIB61.DLL` ⚠️ (2/4), `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### SUB

```
void SUB(mixed value)
```

Subtracts the argument from every element of the array. Each element is converted to `DOUBLE` before the subtraction; all elements after the call are of type `DOUBLE`.

**Parameters**

- `value` — the value to subtract.

**Examples**

```
ARRAYBKGA^SUB([0-VARINT2]);
```

**Compatibility:** `SUB` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### SUBAT

```
void SUBAT(INTEGER index, mixed value)
```

Subtracts the argument from the element at position `index`. Internally, the element and the argument are converted to `DOUBLE`, subtracted, and the result is stored as [`DOUBLE`](DOUBLE.md). Calling with `index` out of range leaves the array unchanged.

**Parameters**

- `index` — element position.
- `value` — the value to subtract.

**Examples**

```
ARRBUTTONPRESSED^SUBAT(IBUTTONNR, 1);
ARRSPEED^SUBAT(VARPLAYER, 0.15);
```

**Compatibility:** `SUBAT` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### SUM

```
void SUM(mixed value)
```

Adds the argument to every element of the array. Each element is converted to `DOUBLE` before the addition; all elements after the call are of type `DOUBLE`.

**Parameters**

- `value` — the value to add.

**Examples**

```
ARRCHICKENX^SUM(-60);
ARRCHICKENY^SUM(-110);
```

**Compatibility:** `SUM` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

## Signals

### ONCHANGE

Fired after a modification has been made to the array.

### ONINIT

Fired when the variable is initialised.

### ONDONE

Fired when leaving the scene that owns the variable.

### ONSIGNAL

Fired when a signal arrives (sent via the `SEND` method — see [Events and signals](../engine/events.md#onsignal)).
