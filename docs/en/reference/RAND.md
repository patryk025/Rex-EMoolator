# RAND

The built-in pseudo-random number generator. Available under the global name `RAND` from any context, and also under the alias `RANDOM` (see [Built-in objects](../engine/globals.md#built-in-objects)).

## Methods

### GET

```
INTEGER GET(INTEGER range)
INTEGER GET(INTEGER offset, INTEGER range)
```

Returns a pseudo-random number.

- The one-argument form returns a value from `[0, range)`.
- The two-argument form returns a value from `[offset, offset + range)`.

If `range` is less than or equal to `0`, `offset` is returned (or `0` in the one-argument form).

**Parameters**

- `offset` — start of the range (inclusive), default `0`.
- `range` — size of the range (the upper bound is exclusive).

**Returns**: the generated random value.

**Examples**

```
RANDOM^GET(100);
RANDOM^GET(VARI_TMP3);
RANDOM^GET(0, 3);
```

### GETPLENTY

```
void GETPLENTY(STRING arrayName, INTEGER count, INTEGER offset, INTEGER range, BOOL onlyUnique)
```

Generates `count` pseudo-random integers from the range `[offset, offset + range)` and appends them to the array named `arrayName`. The array is not cleared before appending.

If `onlyUnique` is `TRUE`, every generated value must be different from the previously generated ones; if the requested number of unique values exceeds the range size (`count > range`), the method leaves the array unchanged. For `count` less than `1`, the method also has no effect.

**Parameters**

- `arrayName` — name of the target [`ARRAY`](ARRAY.md) variable.
- `count` — number of elements to generate.
- `offset` — start of the range (inclusive).
- `range` — size of the range (the upper bound is exclusive).
- `onlyUnique` — `TRUE` to enforce uniqueness among the generated values.
