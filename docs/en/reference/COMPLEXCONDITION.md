# COMPLEXCONDITION

An object that combines two conditions ([`CONDITION`](CONDITION.md) or nested `COMPLEXCONDITION`) with a logical `AND` or `OR` operator. Configured by three fields in the script and invoked similarly to a `CONDITION`.

## Fields

### CONDITION1

```
STRING CONDITION1
```

The name of the variable holding the left-hand sub-condition. The referenced variable should be of type [`CONDITION`](CONDITION.md) or `COMPLEXCONDITION` — in either case it is evaluated recursively.

### CONDITION2

```
STRING CONDITION2
```

The name of the variable holding the right-hand sub-condition; the rules are identical to those of `CONDITION1`.

### OPERATOR

```
STRING OPERATOR
```

The logical operator joining the two sub-conditions. Defaults to `AND`. Accepted values:

| Value | Meaning |
|---|---|
| `AND` | conjunction — the whole is true when both sub-conditions are true |
| `OR` | disjunction — the whole is true when at least one sub-condition is true |

## Methods

### BREAK

```
void BREAK([BOOL emitSignals])
```

Evaluates the compound condition. If the result is `TRUE`, aborts the entire current call tree (the same effect as [`@BREAK`](../engine/scripts.md#jump-operators)).

**Parameters**

- `emitSignals` — (optional) if `TRUE`, [`ONRUNTIMESUCCESS`](#onruntimesuccess)/[`ONRUNTIMEFAILED`](#onruntimefailed) signals are fired by both this object and each sub-condition. Defaults to `FALSE`.

**Examples**

```
COC_END^BREAK(TRUE);
CCONDISATPOS^BREAK(TRUE);
```

**Compatibility:** `BREAK` - `PIKLIB61.DLL` ⚠️ (2/4), `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### CHECK

```
BOOL CHECK([BOOL emitSignals])
```

Evaluates the compound condition and returns the result.

**Parameters**

- `emitSignals` — (optional) same as for [`BREAK`](#break).

**Returns**: [`BOOL`](BOOL.md) — the combined result.

**Examples**

```
CCONDTESTEND^CHECK(TRUE);
```

**Compatibility:** `CHECK` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### ONE_BREAK

```
void ONE_BREAK([BOOL emitSignals])
```

Evaluates the compound condition. If the result is `TRUE`, aborts only the current procedure (the same effect as [`@ONEBREAK`](../engine/scripts.md#jump-operators)).

**Parameters**

- `emitSignals` — (optional) same as for [`BREAK`](#break).

**Examples**

```
COC_END^ONE_BREAK(TRUE);
CCONDISATPOS^ONE_BREAK(TRUE);
```

**Compatibility:** `ONE_BREAK` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

## Signals

### ONRUNTIMESUCCESS

Fired when the compound condition evaluated to `TRUE` and `emitSignals` was `TRUE`.

### ONRUNTIMEFAILED

Fired when the compound condition evaluated to `FALSE` and `emitSignals` was `TRUE`.
