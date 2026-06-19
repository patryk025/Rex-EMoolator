# Engine quirks

The Piklib/BlooMoo engine has a number of non-standard behaviours that often surprise programmers used to mainstream scripting languages. This chapter collects the most common ones in a single place.

## Expressions and operators

### Only square brackets group computations

Inside arithmetic expressions, grouping is done exclusively with `[ ]`. Round parentheses `( )` are reserved for method-call argument lists and have no grouping role (see [Arithmetic](arithmetic.md#syntax)).

### `@` instead of `/` for division

The division operator is the at-sign `@`, not `/`. Using `/` in an expression is not interpreted as division.

### The result type follows the left operand

All binary operations cast the right operand to the type of the left. The result also takes the left operand's type. The order of operands has non-trivial consequences (see [Typing rule](arithmetic.md#typing-rule)):

```
"Value" + 2.5  →  "Value2.50000"
2 + "3"        →  5
```

### Operators on `BOOL` have inverted logic

The `+` operator on [`BOOL`](../reference/BOOL.md) behaves as a logical conjunction (`AND`), and `*` as a disjunction (`OR`). The operators `-`, `@`, and `%` on `BOOL` have no effect (see [Arithmetic operators](arithmetic.md#arithmetic-operators)).

### `%` on `DOUBLE` loses the fractional part

The remainder operator on [`DOUBLE`](../reference/DOUBLE.md) first computes the result and then truncates it to an integer before casting it back to `DOUBLE`. As a result, `1.5 % 2` yields `1.00000`, not `1.50000`.

### `&&` and `||` accept only `BOOL`

The logical operators perform no implicit casting, even when the operand could be converted to `BOOL`. Passing an operand of any other type crashes the engine.

### Division by `0` in expressions crashes

Unlike the numeric-type methods (where `DIV` and `MOD` leave the value unchanged), the `@` and `%` operators in an arithmetic expression crash the engine when the right operand is `0`.

## Script syntax

### Code blocks must fit on one line

The entire block between `{` and `}` must fit on a single line of the script file. Multi-line blocks are not supported.

### Every statement must end with a semicolon

Including the last statement in a code block. Omitting the final semicolon can cause that statement to be skipped.

### Comments: `#` for lines, `!` for statements

A line starting with `#` is skipped entirely. A single statement preceded by `!` is commented out up to the next semicolon.

### The equality comparator changes in compound conditions

In a [simple `@IF` condition](scripts.md#simple-condition), equality is written as `_`. In a [compound `@IF` condition](scripts.md#compound-condition), the same comparator is written as an apostrophe `'`. The remaining comparators (`<`, `>`, `<_`/`<'`, `>_`/`>'`) follow the same pattern.

### Unquoted text is first looked up as a variable name

A value written without quotes is first interpreted as a variable name. If a variable with that name exists, its value is used; otherwise the text is treated as a [`STRING`](../reference/STRING.md) literal. This can lead to hard-to-spot collisions when a literal accidentally matches a variable name.

### Repeated object declarations merge properties

A repeated `OBJECT=NAME` line in the same file does not overwrite the previous definition — the engine merges the new properties into the existing object and overrides entries with the same keys.

### `Application.def` should end with an empty line

Missing a trailing empty line in `Application.def` can leave the last scene incorrectly parsed, which manifests as a black screen at startup.

## Numbers

### `DOUBLE` accepts `d` as the exponent marker

In scientific notation, both `e` and `d` are recognised as exponent separators: `1.23e4` and `1.23d4` are equivalent.

### `DOUBLE → INTEGER` rounds, it does not truncate

When casting [`DOUBLE`](../reference/DOUBLE.md) to [`INTEGER`](../reference/INTEGER.md), the value is rounded to the nearest integer rather than truncated. For positive values, `.5` rounds up; for negative values, it rounds down — so `-0.5 → -1` and `0.5 → 1`.

### `STRING → INTEGER` returns `0` instead of erroring

Casting a non-numeric string to [`INTEGER`](../reference/INTEGER.md) (or [`DOUBLE`](../reference/DOUBLE.md)) returns `0` (`0.00000`) silently. This can mask bugs in expressions mixing textual literals with numeric ones.

## Type methods

### `DOUBLE.MOD` drops the fractional part

Like the `%` operator, [`MOD`](../reference/DOUBLE.md#mod) on `DOUBLE` returns the integer part of the remainder.

### `DOUBLE.SGN` returns `INTEGER`, not `DOUBLE`

The [`SGN`](../reference/DOUBLE.md#sgn) method is the only `DOUBLE` method that does not modify the variable, and its return value is an [`INTEGER`](../reference/INTEGER.md).

### `INTEGER.RANDOM(min, max)` is inclusive on both ends

The two-argument form of [`RANDOM`](../reference/INTEGER.md#random) returns a value from `[min, max]`, including both endpoints. The one-argument form returns `[0, bound)`.

### `INTEGER.DIV` and `INTEGER.MOD` with `0` leave the value unchanged

Unlike division with the `@`/`%` operators in an expression, the [`DIV`](../reference/INTEGER.md#div) and [`MOD`](../reference/INTEGER.md#mod) methods on `INTEGER` return the unchanged current value when given a `0` divisor instead of crashing. The same applies to the `DOUBLE` variants.

### `STRING.COPYFILE` ignores its receiver

The [`COPYFILE`](../reference/STRING.md#copyfile) method on [`STRING`](../reference/STRING.md) does not use the value of the variable on which it is called — it operates purely on its two path arguments.

### `STRING.CHANGEAT` always replaces exactly one character

Regardless of the length of the replacement string, [`CHANGEAT`](../reference/STRING.md#changeat) removes a single character from the current value at the given position and inserts the entire replacement string in its place. The string length after the operation may differ from before.

## `THIS` in signal handlers

### `THIS.GETNAME` returns `"temp"`

Even though `THIS` references the object that emitted the signal, retrieving its name with `GETNAME` returns the string `"temp"`, hinting at an internal temporary-wrapper representation.

### Not every type-specific method works on `THIS`

Calling `GET`/`SET` (for primitive types) or `SHOW`/`HIDE`/`PLAY`/`PAUSE`/`STOP`/`RESUME` (for graphical objects) works reliably. Calling a type-specific method such as [`GETCFRAMEINEVENT`](../reference/index.md) on `ANIMO` typically crashes the engine. See [The THIS variable](scripts.md#the-this-variable) for details.
