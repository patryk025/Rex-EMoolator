# BOOL

Boolean type. Stores one of two values: `TRUE` or `FALSE`.

## Fields

### TOINI

```
BOOL TOINI
```

Controls whether the field's value is persisted to an INI file and restored on the next run.

### VALUE

```
BOOL VALUE
```

The current value of the variable.

## Methods

### GET

```
BOOL GET()
```

Returns the current value of the variable.

**Returns**

- `BOOL` — the current value of the `VALUE` field.

### RESETINI

```
void RESETINI()
```

Resets the variable's value to the reset value defined in the object's script attributes. The engine looks up the value in the following order: `DEFAULT` → `INIT_VALUE` → `VALUE`; the first one found is used.

### SET

```
void SET(BOOL value)
```

Sets the variable's value.

**Parameters**

- `value` — the new `BOOL` value.

**Examples**

```
VARBLOCKSCENE^SET(FALSE);
__KEYB__^SET(KEYBOARD^ISENABLED());
VARBTEMP1^SET($2);
```

### SWITCH

```
void SWITCH(BOOL value1, BOOL value2)
```

Toggles the variable's value between the two values passed as arguments. The method accepts two parameters in order to keep its signature consistent with `SWITCH` on the [`INTEGER`](INTEGER.md) and [`DOUBLE`](DOUBLE.md) types, even though for `BOOL` the full information could be expressed with a single argument.

**Parameters**

- `value1` — the first value.
- `value2` — the second value.

**Examples**

```
B_0^SWITCH(TRUE, FALSE);
```

## Signals

### ONCHANGED

Fired when the variable's value is changed to one different from the previous one.

### ONBRUTALCHANGED

Fired on every call that sets the value, regardless of whether the new value differs from the previous one.
