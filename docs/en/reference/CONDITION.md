# CONDITION

An object that describes a comparison of two operands. Configured by three fields in the script and invoked through [`CHECK`](#check) or one of the control-flow methods ([`BREAK`](#break), [`ONE_BREAK`](#one_break)).

## Fields

### OPERAND1

```
STRING OPERAND1
```

The left-hand operand of the comparison. The field holds the operand's textual form, which is parsed on every evaluation. Accepted forms:

- a quoted string literal (`"..."` or `'...'`),
- a boolean literal (`TRUE`, `FALSE`),
- a numeric literal (`5`, `-3.14`),
- a variable name (its value is fetched; for variables of type [`EXPRESSION`](index.md), `CONDITION`, or [`COMPLEXCONDITION`](COMPLEXCONDITION.md), the variable is recursively evaluated),
- a script fragment — text starting with `[`, `*`, or containing the `^` or `|` operators.

### OPERAND2

```
STRING OPERAND2
```

The right-hand operand of the comparison. The interpretation rules are identical to those of `OPERAND1`.

### OPERATOR

```
STRING OPERATOR
```

The comparison operator. Defaults to `EQUAL`. Accepted values:

| Value | Meaning |
|---|---|
| `EQUAL` | equal to |
| `NOTEQUAL` | not equal to |
| `LESS` | less than |
| `GREATER` | greater than |
| `LESSEQUAL` | less than or equal |
| `GREATEREQUAL` | greater than or equal |

## Methods

### BREAK

```
void BREAK([BOOL emitSignals])
```

Evaluates the condition. If the result is `TRUE`, aborts the entire current call tree (the same effect as [`@BREAK`](../engine/scripts.md#jump-operators)). If the result is `FALSE`, the method has no effect.

**Parameters**

- `emitSignals` — (optional) if `TRUE`, also fires [`ONRUNTIMESUCCESS`](#onruntimesuccess) or [`ONRUNTIMEFAILED`](#onruntimefailed) depending on the result. Defaults to `FALSE`.

**Examples**

```
COND1^BREAK(TRUE);
CONDKONTROLA^BREAK(TRUE);
```

**Compatibility:** `BREAK` - `PIKLIB61.DLL` ⚠️ (2/4), `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### CHECK

```
BOOL CHECK([BOOL emitSignals])
```

Evaluates the condition and returns the comparison result.

**Parameters**

- `emitSignals` — (optional) if `TRUE`, also fires [`ONRUNTIMESUCCESS`](#onruntimesuccess) or [`ONRUNTIMEFAILED`](#onruntimefailed) depending on the result. Defaults to `FALSE`.

**Returns**: [`BOOL`](BOOL.md) — the comparison result.

**Examples**

```
CONPR1^CHECK(TRUE);
CONPR2^CHECK(TRUE);
```

**Compatibility:** `CHECK` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### ONE_BREAK

```
void ONE_BREAK([BOOL emitSignals])
```

Evaluates the condition. If the result is `TRUE`, aborts only the current procedure (the same effect as [`@ONEBREAK`](../engine/scripts.md#jump-operators)). If the result is `FALSE`, the method has no effect.

**Parameters**

- `emitSignals` — (optional) same as for [`BREAK`](#break).

**Examples**

```
COND1^ONE_BREAK(TRUE);
CONDREMOVEMENUITEM^ONE_BREAK(TRUE);
```

**Compatibility:** `ONE_BREAK` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

## Signals

### ONRUNTIMESUCCESS

Fired when the condition evaluated to `TRUE` and `emitSignals` was `TRUE`.

### ONRUNTIMEFAILED

Fired when the condition evaluated to `FALSE` and `emitSignals` was `TRUE`.
