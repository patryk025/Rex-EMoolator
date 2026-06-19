# DTA format — database

The `.DTA` file is a **text** database stand-in, used by [`DATABASE`](../reference/DATABASE.md) objects. It stores rows and columns separated by simple delimiters.

## Structure

- **Rows** are separated by the Windows-style line ending: `\r\n` (CRLF).
- **Columns** within a row are separated by the pipe character: `|`.

```
value11|value12|value13␍␊
value21|value22|value23␍␊
```

## Schema (MODEL)

The database structure — the number of columns and their types — is not stored in the `.DTA` file itself, but in the `MODEL` field of the [`DATABASE`](../reference/DATABASE.md) object. `MODEL` is a [`STRUCT`](../reference/STRUCT.md) variable, and it is what gives the raw values from the file their concrete column types.

!!! note "The file is just data"
    The `.DTA` itself contains no header and no types — it's a "dumb" text store. Without the associated `MODEL` the columns cannot be interpreted unambiguously.

## Related formats

In the *Poznaj Mity* series, the role of `.DTA` files in minigames is played by `.TXT` files with an analogous text structure.

## See also

- [`DATABASE`](../reference/DATABASE.md) — the scripting object that operates on `.DTA`.
- [`STRUCT`](../reference/STRUCT.md) — the schema definition in the `MODEL` field.
