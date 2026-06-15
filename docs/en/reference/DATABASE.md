# DATABASE

A tabular database — a collection of rows sharing a single schema. The schema is defined by a paired [`STRUCT`](STRUCT.md) variable: its [`FIELDS`](STRUCT.md#fields) entry specifies the table's columns and per-column data types.

Rows are accessed sequentially via a cursor. The cursor position is updated by [`SELECT`](#select) (move to a specific index), [`NEXT`](#next) (advance one row), and [`FIND`](#find) (move to the first row matching a column value). Data is persisted in `.DTA` files using a `|`-separated text format — loadable via [`LOAD`](#load) and writable via [`SAVE`](#save).

## Fields

### MODEL

```
STRING MODEL
```

Name of the [`STRUCT`](STRUCT.md) variable that defines the database's schema. Required — [`LOAD`](#load) refuses to run if the schema has not been synchronised with the [`STRUCT`](STRUCT.md) beforehand.

## Methods

### FIND

```
INTEGER FIND(STRING columnName, mixed columnValue, INTEGER defaultIndex)
```

Returns the index of the first row whose `columnName` column equals `columnValue`. Falls back to `defaultIndex` if no row matches.

**Parameters**

- `columnName` — name of the column to search.
- `columnValue` — value to match.
- `defaultIndex` — index returned on no match.

**Returns**: [`INTEGER`](INTEGER.md) — matching row index or `defaultIndex`.

**Examples**

```
DBOBJECTS^FIND("IDNAME",VARSTAKENAME,0);
DBOBJECTS^FIND("TYPE",102,0);
DBDIALOGI^FIND("ID",SDIALOGNAME,IDIALOGINDEKS);
```

### GETROWSNO

```
INTEGER GETROWSNO()
```

Returns the number of rows in the database.

**Returns**: [`INTEGER`](INTEGER.md) — the row count.

**Examples**

```
DBOBJECTS^GETROWSNO();
```

### LOAD

```
void LOAD(STRING dtaName)
```

Loads database contents from a `.DTA` file. Each line is one row; columns within a row are separated by `|`. The call aborts with an error if the schema ([`MODEL`](#model)) has not been set.

**Parameters**

- `dtaName` — path to the `.DTA` file.

**Examples**

```
DBOBJECTS^LOAD(VARSCURRARCADE);
DBITEMS^LOAD("$COMMON\ITEMS0.DTA");
```

### NEXT

```
void NEXT()
```

Advances the cursor to the next row.

**Examples**

```
DBSCENE^NEXT();
```

### REMOVEALL

```
void REMOVEALL()
```

Drops every row from the database. The schema ([`MODEL`](#model)) is preserved.

**Examples**

```
DBITEMS^REMOVEALL();
```

### SAVE

```
void SAVE(STRING dtaName)
```

Writes the current contents to a `.DTA` file using the same format that [`LOAD`](#load) accepts (rows separated by newlines, columns by `|`).

**Parameters**

- `dtaName` — destination `.DTA` path.

**Examples**

```
DBOBJECTS^SAVE(VARSCURRARCADE);
DBLEVEL^SAVE(["$COMMON\SAVE_BD\BD_CLEV"+VARIACTIVESLOT+".FLD"]);
```

### SELECT

```
void SELECT(INTEGER rowIndex)
```

Moves the cursor to the row at the given (zero-based) index.

**Parameters**

- `rowIndex` — target row index.

**Examples**

```
DBOBJECTS^SELECT(0);
DBOBJECTS^SELECT(VARITER);
```

## Signals

### ONINIT

Fired when the object is initialised.

### ONSIGNAL

Fired when a signal arrives (see [Events and signals](../engine/events.md#onsignal)).
