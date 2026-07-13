# STRING

Character string.

## Fields

### TOINI

```
BOOL TOINI
```

Controls whether the field's value is persisted to an INI file and restored on the next run.

### VALUE

```
STRING VALUE
```

The current value of the variable.

## Methods

### ADD

```
STRING ADD(STRING text)
```

Appends the argument to the variable's current value (string concatenation), stores the result, and returns it.

**Parameters**

- `text` — the text to append.

**Returns**: the new value of the variable.

**Examples**

```
G_SLASTOBJECTS^ADD(VARSTEMP0);
VARSMUSIC^ADD(".WAV");
S_0^ADD($1);
```

**Compatibility:** `ADD` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### CHANGEAT

```
STRING CHANGEAT(INTEGER index, STRING replacement)
```

Replaces the single character at position `index` with the `replacement` string. If `replacement` is not exactly one character long, the resulting string length changes accordingly. Calling with an `index` outside the string's range leaves the variable unchanged.

**Parameters**

- `index` — position of the character to replace (`0`-based).
- `replacement` — the string to insert in place of the character.

**Returns**: the new value of the variable (or the unchanged value if `index` was out of range).

**Examples**

```
*VARARRAYNAME^CHANGEAT([VARCLONE-1], "NULL");
*VARENEMYSTATE^CHANGEAT([VARINT1-1], VARSTRING1);
```

**Compatibility:** `CHANGEAT` - not present in the libraries analysed in `compat.json`.

### COPYFILE

```
BOOL COPYFILE(STRING source, STRING destination)
```

Copies a file inside the game's virtual file system (VFS) from `source` to `destination`. The method does not use the receiver variable's value — only the arguments matter.

**Parameters**

- `source` — path of the source file in the VFS.
- `destination` — path of the destination file in the VFS.

**Returns**: [`BOOL`](BOOL.md) — `TRUE` if the copy succeeded; `FALSE` otherwise (e.g. the source file does not exist or an I/O error occurred).

**Examples**

```
VARSTEMP0^COPYFILE("$COMMON\ITEMS_DEF.DTA", "$COMMON\ITEMS0.DTA");
S1^COPYFILE(S1, S2);
```

**Compatibility:** `COPYFILE` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ❌, `PIKLIB8.DLL` ⚠️ (6/10), `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### CUT

```
STRING CUT(INTEGER index, INTEGER length)
```

Replaces the variable's value with a substring of the current value, starting at position `index` and of `length` characters. If the requested length exceeds the available characters, the substring stops at the end of the string. If `index` is out of range, the variable becomes an empty string.

**Parameters**

- `index` — starting position of the substring (`0`-based).
- `length` — length of the substring.

**Returns**: the new value of the variable.

**Examples**

```
VARSTRING0^CUT(0, VARSTRING0^FIND("_"));
```

**Compatibility:** `CUT` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### FIND

```
INTEGER FIND(STRING needle)
INTEGER FIND(STRING needle, INTEGER offset)
```

Searches the variable's current value for the first occurrence of the given string. This method does not modify the variable.

**Parameters**

- `needle` — the string to search for.
- `offset` — (optional) position from which the search starts. Defaults to `0`.

**Returns**: the position of the first occurrence (`0`-based), or `-1` if the string was not found.

**Examples**

```
VARSTEMP0^FIND("IDLE");
SWYRAZ^FIND(S1);
```

**Compatibility:** `FIND` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### GET

```
STRING GET()
STRING GET(INTEGER index)
STRING GET(INTEGER index, INTEGER length)
```

Returns a fragment of the variable's current value. This method does not modify the variable.

- Without arguments, returns the full value of the `VALUE` field.
- With arguments, returns a substring starting at `index` and of `length` characters (default `1`).

If `index` is out of range, an empty string is returned. If the requested length exceeds the available characters, the substring stops at the end of the string.

**Parameters**

- `index` — starting position of the substring (`0`-based).
- `length` — (optional) length of the substring. Defaults to `1`.

**Returns**: the extracted substring (or the full value in the no-argument form).

**Examples**

```
VARSTEMP3^GET(7);
VARENEMYNAME^GET(0, VARENEMYNAME^FIND("_"));
SOBJNAME^GET(0, 1);
```

**Compatibility:** `GET` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### LENGTH

```
INTEGER LENGTH()
```

Returns the length of the variable's current value. This method does not modify the variable.

**Returns**: [`INTEGER`](INTEGER.md) — the number of characters in the string.

**Examples**

```
VARSTEMP0^LENGTH();
```

**Compatibility:** `LENGTH` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### LOWER

```
STRING LOWER()
```

Converts all letters in the variable's current value to lowercase.

**Returns**: the new value of the variable.

**Compatibility:** `LOWER` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ❌, `PIKLIB8.DLL` ❌, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ⚠️ (3/5).

### REPLACEAT

```
STRING REPLACEAT(INTEGER index, INTEGER length, STRING replacement)
```

Replaces a fragment of the current string of `length` characters, starting at position `index`, with the `replacement` string. If the requested length exceeds the available characters, the rest of the string is replaced. Calling with an `index` outside the string's range leaves the variable unchanged.

**Parameters**

- `index` — starting position of the replaced fragment (`0`-based).
- `length` — length of the replaced fragment.
- `replacement` — the string that will replace the fragment.

**Returns**: the new value of the variable (or the unchanged value if `index` was out of range).

**Examples**

```
S3^REPLACEAT(0, 1, S1);
VARSTMP2^REPLACEAT(8, 2, ["00"+VARIKRAINANO]);
```

**Compatibility:** `REPLACEAT` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### RESETINI

```
STRING RESETINI()
```

Resets the variable's value to the reset value defined in the object's script attributes. The engine looks up the value in the following order: `DEFAULT` → `INIT_VALUE` → `VALUE`; the first one found is used. If none of them is set, the value is reset to an empty string.

**Returns**: the new value of the variable.

**Compatibility:** `RESETINI` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### SET

```
STRING SET(STRING value)
```

Sets the variable's value.

**Parameters**

- `value` — the new value.

**Returns**: the new value of the variable.

**Examples**

```
SCENENAME^SET(PRZYGODA^GETCURRENTSCENE());
VARSTEMP0^SET(["BEHCLICK_"+SOBJECT|IDNAME]);
VARSTEMP0^SET($1);
```

**Compatibility:** `SET` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### SUB

```
STRING SUB(INTEGER index, INTEGER length)
```

Removes from the variable's current value a fragment of `length` characters, starting at position `index`. The remaining parts (before and after the removed fragment) are concatenated. Calling with an `index` outside the string's range leaves the variable unchanged.

**Parameters**

- `index` — starting position of the removed fragment (`0`-based).
- `length` — length of the removed fragment.

**Returns**: the new value of the variable (or the unchanged value if `index` was out of range).

**Examples**

```
VARSTEMP0^SUB(0, 5);
VARSTEMP0^SUB(VARITEMP0, [VARSTEMP0^LENGTH()-VARITEMP0]);
```

**Compatibility:** `SUB` - `PIKLIB61.DLL` ⚠️ (3/4), `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### UPPER

```
STRING UPPER()
```

Converts all letters in the variable's current value to uppercase.

**Returns**: the new value of the variable.

**Examples**

```
SDIALOGWAVENAME^UPPER();
SDIALOGPERSON^UPPER();
```

**Compatibility:** `UPPER` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

## Signals

### ONCHANGED

Fired when the variable's value is changed to one different from the previous one.

### ONBRUTALCHANGED

Fired on every call that sets the value, regardless of whether the new value differs from the previous one.

### ONINIT

Fired when the variable is initialised.
