# BEHAVIOUR

A procedure. Executes the code stored in the `CODE` field, optionally guarded by the condition named in the `CONDITION` field. Call-site arguments are available inside the code as `$1`, `$2`, …; see [Procedure arguments](../engine/scripts.md#procedure-arguments) for details.

## Fields

### CODE

```
STRING CODE
```

The body of the procedure — a code block in curly braces following the syntax described in [Scripts](../engine/scripts.md#code-blocks).

Example:

```
BEHGOTOTITLE:CODE={BEHSETSCENE^RUN();G_SARCADESCENE^SET("EGIPTLEJ");BEHCSSTART^RUN();}
```

### CONDITION

```
STRING CONDITION
```

The name of a [`CONDITION`](CONDITION.md) or [`COMPLEXCONDITION`](COMPLEXCONDITION.md) variable, used by [`RUNC`](#runc) as a gate and by [`RUNLOOPED`](#runlooped) as a per-iteration loop guard. If the field is not set, the methods run unconditionally.

## Methods

### RUN

```
mixed RUN([mixed param1, ..., mixed paramN])
```

Executes the procedure's code. The arguments are exposed inside the body as `$1`, `$2`, …. The return value is whatever [`@RETURN`](../engine/scripts.md#jump-operators) sets in the body, or `NULL` if `@RETURN` is not invoked.

**Parameters**

- `param1, …, paramN` — procedure arguments (optional, of any type).

**Returns**: the value returned by the procedure or `NULL`.

**Examples**

```
__LOAD_SETTINGS__^RUN();
BEHSELECTOBJ^RUN(VARITER);
BEHADDITEM^RUN(SOBJECT|SPARAM0, VARITER);
BEHENTERRABBIT^RUN("ANNHILL0", -1);
```

### RUNC

```
mixed RUNC([mixed param1, ..., mixed paramN])
```

Calls the procedure only when the condition named in `CONDITION` is satisfied. If `CONDITION` is not set, behaves like [`RUN`](#run). Arguments and return value are the same as in `RUN`.

**Parameters**

- `param1, …, paramN` — procedure arguments.

**Returns**: the value returned by the procedure, or `NULL` (including the case when the condition was not satisfied).

**Examples**

```
BEHREMOVEMENUITEM^RUNC("CHOMIK");
BEHREMOVEMENUITEM^RUNC(VARSTRINGTEMP);
BEH_HERO_FINISHED_0^RUNC();
```

### RUNLOOPED

```
void RUNLOOPED(INTEGER start, INTEGER length)
void RUNLOOPED(INTEGER start, INTEGER length, INTEGER step, [mixed extraArg1, ..., mixed extraArgN])
```

Runs the procedure in a `for` loop with the counter passed as `$1`. Extra arguments (from the fourth argument onwards) are forwarded to the procedure as `$2`, `$3`, … . If the `CONDITION` field is set, its condition is checked before every iteration — when it is not satisfied, the loop ends.

The loop is equivalent to the following pseudocode:

```
for (int i = start; i < start + length; i += step) {
    // call procedure with $1 = i, $2..$N = extraArgs
}
```

If `step` is omitted or passed as `0`, the value `1` is used. The [`@BREAK`](../engine/scripts.md#jump-operators) operator inside the procedure terminates the `RUNLOOPED` loop (but not the calling procedure).

**Parameters**

- `start` — initial counter value.
- `length` — number of iterations (`startVal < endVal` equals `startVal + length`).
- `step` — (optional) counter step. Defaults to `1`.
- `extraArg1, …, extraArgN` — (optional) extra arguments forwarded to the procedure.

**Examples**

```
BEHSHOWMENU^RUNLOOPED(0, ARRAYWARSZTATMENUPRZEDMIOTY^GETSIZE());
BEHSHOWPIONEK^RUNLOOPED(1, 9);
BEHINITZASLONAX^RUNLOOPED(0, 7, 1, "[80*$1]");
```

## Signals

### ONINIT

Fired when the procedure is initialised.

### ONDONE

Fired after a procedure invocation completes.

### ONSIGNAL

Fired when a signal arrives (see [Events and signals](../engine/events.md#onsignal)).
