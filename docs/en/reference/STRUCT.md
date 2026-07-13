# STRUCT

A data structure with named, typed fields. In engine scripts it is used exclusively together with [`DATABASE`](DATABASE.md) — it describes a database row's schema and stores the values of the currently pointed-to record after a call to [`SET`](#set).

## Fields

### FIELDS

```
STRING FIELDS
```

The struct's schema, written as a comma-separated list. Each entry has the form `NAME<TYPE>`, where `NAME` is the field name and `TYPE` is the data type. Accepted types: `STRING`, `INTEGER`, `DOUBLE`, `BOOLEAN`. Type names are case-insensitive; any unrecognised name is treated as `STRING`.

## Methods

### GETFIELD

```
<type> GETFIELD(INTEGER fieldIndex)
```

Returns the value of the field at the given (zero-based) index. The return type follows the schema — `<INTEGER>` fields return [`INTEGER`](INTEGER.md), `<DOUBLE>` returns [`DOUBLE`](DOUBLE.md), `<BOOLEAN>` returns [`BOOL`](BOOL.md), and everything else returns [`STRING`](STRING.md). Out-of-range indices return an empty value. If the struct has not yet been synchronised with a [`DATABASE`](DATABASE.md), every field is empty.

**Parameters**

- `fieldIndex` — field index (zero-based).

**Returns**: the field value, typed according to the schema.

**Examples**

```
STLEVEL^GETFIELD(0);
```

**Compatibility:** `GETFIELD` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### SET

```
void SET(STRING cursorName)
```

Synchronises the struct with the row currently pointed to by a [`DATABASE`](DATABASE.md) cursor. Raw cursor values are converted to the types declared in the [`FIELDS`](#fields) schema.

**Parameters**

- `cursorName` — name of the cursor variable associated with a database.

**Examples**

```
SOBJECT^SET("DBOBJECTS_CURSOR");
```

**Compatibility:** `SET` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

## Signals

### ONINIT

Fired when the object is initialised.

### ONSIGNAL

Fired when a signal arrives (see [Events and signals](../engine/events.md#onsignal)).
