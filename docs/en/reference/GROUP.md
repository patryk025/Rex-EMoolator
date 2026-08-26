# GROUP

A group of variables that can receive batched method calls. Any method invoked on a `GROUP` instance that is not part of the group's own API is delegated to each member in turn. Members that do not implement the called method are skipped silently (without error).

The group keeps an internal **marker** pointing at one of its elements. The marker is updated by [`NEXT`](#next), [`PREV`](#prev), and [`RESETMARKER`](#resetmarker). It can be used to iterate over the group sequentially.

The variable's value is the number of elements in the group.

## Methods

### \[method name\]

```
void <methodName>(mixed param1, ..., mixed paramN)
```

Any method outside the group's own API is forwarded to every member with the same arguments. Members that do not implement the method are skipped.

**Examples**

```
GRPHIDE^HIDE();
GRPMOVE^SETPOSITION(VARX,VARY);
```



### ADD

```
void ADD(STRING varName1, [STRING varName2, ...])
```

Adds one or more elements to the group by variable name. Re-adding an element that is already in the group is a no-op.

**Parameters**

- `varName1, varName2, …` — successive variable names to add.

**Examples**

```
GRPHIDE^ADD("ANNREX");
GRPMOVE^ADD("ANNBODY1","ANNWAND1","ANNHEAD1");
GALL^ADD(["ANNPOLA_"+ICLONENO]);
```

**Compatibility:** `ADD` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### ADDCLONES

```
void ADDCLONES(STRING varName, INTEGER firstCloneIndex, INTEGER lastCloneIndex)
```

Adds an inclusive range of variable clones — from `firstCloneIndex` up to `lastCloneIndex`. Clones are referenced by name using the engine's clone-naming pattern (index suffix).

**Parameters**

- `varName` — base variable name.
- `firstCloneIndex` — first clone index.
- `lastCloneIndex` — last clone index.

**Examples**

```
GBKG^ADDCLONES("ANNPLANNAK",0,[I1-1]);
GTRASA^ADDCLONES("ANNSKRZYNIA",1,ITMPCLONENO);
GRPLANS^ADDCLONES("IMGPLAN1",1,10);
```

**Compatibility:** `ADDCLONES` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### GETSIZE

```
INTEGER GETSIZE()
```

Returns the number of elements in the group.

**Returns**: [`INTEGER`](INTEGER.md) — the group size.

**Examples**

```
GRPHIDE^GETSIZE();
```

**Compatibility:** `GETSIZE` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### GETNAMEATMARKER

```
STRING GETNAMEATMARKER()
```

Returns the name of the member at the current marker. The result can be stored in a `STRING` and used for indirect calls through `*name`. For an invalid marker position the emulator safely returns `"NULL"`.

### GETMARKERPOS

```
INTEGER GETMARKERPOS()
```

Returns the current marker position. `-1` denotes the position before the first element.

### NEXT

```
STRING NEXT([INTEGER step])
```

Moves the marker right by one element or by the requested step. Going past the end wraps around to the beginning.

**Returns**: the name of the element at the new marker position.

**Examples**

```
GENEMIES^NEXT();
GBAZUK^NEXT();
```

**Compatibility:** `NEXT` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### PREV

```
STRING PREV([INTEGER step])
```

Moves the marker left by one element or by the requested step. Going past the beginning wraps around to the end.

**Returns**: the name of the element at the new marker position.

**Compatibility:** `PREV` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### REMOVE

```
void REMOVE(STRING varName)
```

Removes the named element from the group. The marker position is not adjusted automatically.

**Parameters**

- `varName` — variable name to remove.

**Examples**

```
GOBJ^REMOVE(S1);
GOBJ^REMOVE("ANNTNTR");
```

**Compatibility:** `REMOVE` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### REMOVEALL

```
void REMOVEALL()
```

Drops every element from the group. The marker position is not adjusted automatically; use `RESETMARKER()` to reset it.

**Examples**

```
GRPHIDE^REMOVEALL();
```

**Compatibility:** `REMOVEALL` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### RESETMARKER

```
void RESETMARKER()
```

Sets the marker to `-1`, the position before the first element. The next `NEXT()` call selects index `0`.

**Examples**

```
GENEMIES^RESETMARKER();
```

**Compatibility:** `RESETMARKER` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### SETMARKERPOS

```
void SETMARKERPOS(INTEGER position)
```

Sets the marker to the requested position. The value is clamped from `-1` through the last element index; for an empty group the marker becomes `-1`.

## Signals

### ONINIT

Fired when the object is initialised.

### ONSIGNAL

Fired when a signal arrives (see [Events and signals](../engine/events.md#onsignal)).
