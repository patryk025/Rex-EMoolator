# Arithmetic

The Piklib/BlooMoo engine performs computations exclusively inside **arithmetic expressions** enclosed in square brackets. Round parentheses are reserved for method-call argument lists and do not act as grouping inside expressions. This chapter describes the available operators, the typing rules, and the conversions between primitive types.

## Syntax

An arithmetic expression is written between square brackets. It may appear anywhere a value is expected — including as a method argument or as the value assigned to a field:

```
VARIABLE_NAME^SET([VAL1+VAL2]);
*["ANIMO_"+_I_]^PLAY();
```

Expressions can be nested using additional pairs of square brackets:

```
[[VAL1+VAL2]*VAL3]
```

## Order of operations

Operators have no precedence: an expression is evaluated strictly from left to right, and only square brackets can change that.

```
[7+3*2]    →  20   # (7+3)*2
[7+[3*2]]  →  13
```

!!! warning "Piklib 6.1 and 7.1"
    These engines support only one operator in an expression: the text is split at the first operator character, and the right side is treated as a single operand. That is why `[7+3*2]` yields `10` and `[1+2+3]` yields `3`. A bracket on the right works (`[7+[3*2]]` is `13`), but a bracket on the left (`[[7+3]*2]`) crashes the engine.

## Typing rule

All binary operations in expressions follow a single rule:

> **The result's type and the right operand's type are both determined by the type of the left operand.**

The right operand is cast to the left operand's type before the operation is performed, and the result also has that type. Examples:

```
"Value" + 2.5  →  "Value2.50000"   # DOUBLE cast to STRING
2 + "3"        →  5                 # STRING cast to INTEGER
```

A consequence: operand order matters not only for non-commutative operators, but also for the type of the result.

## Type conversions

Within the typing rule, the right operand is cast according to the rules below.

### From `STRING`

| Target | Rule |
|---|---|
| [`INTEGER`](../reference/INTEGER.md) | An integer is extracted from the start of the text (similar to `parseInt`). If the text does not start with a number, the result is `0`. |
| [`DOUBLE`](../reference/DOUBLE.md) | Same as `INTEGER`, but the fractional part is preserved. The decimal separator is a dot. |
| [`BOOL`](../reference/BOOL.md) | Text matching a truthful value (`TRUE` or a non-zero number) yields `TRUE`; otherwise `FALSE`. |

```
"5"     →  5
"Test"  →  0
```

### From `INTEGER`

| Target | Rule |
|---|---|
| [`STRING`](../reference/STRING.md) | The decimal representation of the number. |
| [`DOUBLE`](../reference/DOUBLE.md) | The number with a zero fractional part (five zeros after the dot). |
| [`BOOL`](../reference/BOOL.md) | A non-zero value yields `TRUE`; `0` yields `FALSE`. |

```
5   →  "5"
3   →  3.00000
-2  →  TRUE
0   →  FALSE
```

### From `DOUBLE`

| Target | Rule |
|---|---|
| [`STRING`](../reference/STRING.md) | Decimal representation with a dot and five fractional digits; details depend on the engine (see the table below). |
| [`INTEGER`](../reference/INTEGER.md) | BlooMoo from "Reksio i Kapitan Nemo" on: rounded to the nearest integer; ties round up for positive values and down for negative values. Piklib and the BlooMoo of "Reksio i Wehikuł Czasu": the fractional part is truncated ([details](quirks.md#double-integer-depends-on-the-engine-version)). |
| [`BOOL`](../reference/BOOL.md) | Indirect: first cast to `INTEGER` (with the rounding above), then to `BOOL`. Values in the open interval `(-0.5, 0.5)` give `FALSE`, all others `TRUE`. |

```
3.5      →  "3.50000",   4,    TRUE
0.0      →  "0",         0,    FALSE
0.45362  →  "0.45362",   0,    FALSE
1.00001  →  "1.00001",   1,    TRUE
-0.5     →  "-0.50000", -1,    TRUE
```

The examples apply to BlooMoo from "Reksio i Kapitan Nemo" on. The text form looks different on older engines:

| Engine | `3.25` | `0.0` | `-0.5` |
|---|---|---|---|
| BlooMoo from "Reksio i Kapitan Nemo" on | `3.25000` | `0` | `-0.50000` |
| Piklib 7.2 and 8, the BlooMoo of "Reksio i Wehikuł Czasu" | `3.25000` | `00000` | `0.-50000` |
| Piklib 7.1 | `3.25` | `00` | `-50` |

In Piklib 6.1 every `DOUBLE` read from text is `0`, and a number is written as raw digits without a dot (`2` → `200`).

### From `BOOL`

| Target | Rule |
|---|---|
| [`STRING`](../reference/STRING.md) | `TRUE` → `"TRUE"`, `FALSE` → `"FALSE"`. |
| [`INTEGER`](../reference/INTEGER.md) | `TRUE` → `1`, `FALSE` → `0`. |
| [`DOUBLE`](../reference/DOUBLE.md) | `TRUE` → `1.00000`, `FALSE` → `0.00000`. |

## Arithmetic operators

Expressions support the following binary operators:

| Operator | Meaning |
|---|---|
| `+` | addition / concatenation |
| `-` | subtraction |
| `*` | multiplication |
| `@` | division |
| `%` | remainder |

### Addition (`+`)

| Left operand type | Behaviour |
|---|---|
| [`STRING`](../reference/STRING.md) | Concatenation of the right operand (after casting) onto the left. |
| [`INTEGER`](../reference/INTEGER.md) | Numeric sum. |
| [`DOUBLE`](../reference/DOUBLE.md) | Numeric sum. |
| [`BOOL`](../reference/BOOL.md) | Logical conjunction (`AND`). `TRUE + FALSE` yields `FALSE`. |

### Subtraction (`-`)

| Left operand type | Behaviour |
|---|---|
| [`STRING`](../reference/STRING.md) | No effect; the result is the left operand. |
| [`INTEGER`](../reference/INTEGER.md) | Numeric difference. |
| [`DOUBLE`](../reference/DOUBLE.md) | Numeric difference. |
| [`BOOL`](../reference/BOOL.md) | No effect; the result is the left operand. |

### Multiplication (`*`)

| Left operand type | Behaviour |
|---|---|
| [`STRING`](../reference/STRING.md) | No effect; the result is the left operand. |
| [`INTEGER`](../reference/INTEGER.md) | Numeric product. |
| [`DOUBLE`](../reference/DOUBLE.md) | Numeric product. |
| [`BOOL`](../reference/BOOL.md) | Logical disjunction (`OR`). `FALSE * TRUE` yields `TRUE`. |

### Division (`@`)

| Left operand type | Behaviour |
|---|---|
| [`STRING`](../reference/STRING.md) | No effect; the result is the left operand. |
| [`INTEGER`](../reference/INTEGER.md) | Integer quotient. |
| [`DOUBLE`](../reference/DOUBLE.md) | Floating-point quotient. |
| [`BOOL`](../reference/BOOL.md) | No effect; the result is the left operand. |

Dividing by `0` depends on the left operand's type: `INTEGER` crashes the engine, `DOUBLE` yields infinity (`1.#INF0`), and `STRING` and `BOOL` stay unchanged. The divisor can also become zero through casting, for example when it is text that is not a number.

### Remainder (`%`)

| Left operand type | Behaviour |
|---|---|
| [`STRING`](../reference/STRING.md) | No effect; the result is the left operand. |
| [`INTEGER`](../reference/INTEGER.md) | Remainder. |
| [`DOUBLE`](../reference/DOUBLE.md) | Remainder truncated to an integer and then cast back to `DOUBLE`. The fractional part is lost — for example, `1.5 % 2` yields `1.00000`, not `1.50000`. |
| [`BOOL`](../reference/BOOL.md) | No effect; the result is the left operand. |

A remainder by `0` crashes the engine for `INTEGER` and `DOUBLE`, while `STRING` and `BOOL` stay unchanged.

!!! bug "Difference in the emulator"
    Crosstests detected a difference to check: in the emulator, `@` and `%` by `0` always yield the text `NULL`. See [Differences from the original](../emulator/divergences.md).

## Comparison operators

Expressions in `[ ]` only know the five arithmetic operators. Comparisons are part of conditions: [`@IF`](scripts.md#conditional), [`@WHILE`](scripts.md#while), and the [`CONDITION`](../reference/CONDITION.md) and [`COMPLEXCONDITION`](../reference/COMPLEXCONDITION.md) objects, and the [Scripts](scripts.md#conditional) chapter describes how they are written.

The right operand is first cast to the left operand's type (per the [typing rule](#typing-rule)), and the comparison is then performed.

### Equality and inequality

Equality holds if both operands (after casting) are equal, and inequality is its opposite. For [`STRING`](../reference/STRING.md) the comparison is character-by-character; for numeric types, it is value-based.

### Less / greater

| Left operand type | `<` returns `TRUE` if |
|---|---|
| [`STRING`](../reference/STRING.md) | The left operand is lexicographically smaller than the right (character-by-character in CP1250 encoding). |
| [`INTEGER`](../reference/INTEGER.md) | The left operand's value is smaller. |
| [`DOUBLE`](../reference/DOUBLE.md) | The left operand's value is smaller. |
| [`BOOL`](../reference/BOOL.md) | The left operand is `FALSE` and the right is `TRUE` (`FALSE < TRUE`). |

`>` returns `TRUE` in the symmetric cases. "Less or equal" and "greater or equal" are equivalent to `<` or equality, and `>` or equality, respectively.

## Logical operators

`&&` (conjunction) and `||` (disjunction) are not part of arithmetic expressions. They exist only in the [compound `@IF` condition](scripts.md#compound-condition), as part of the condition string.

| Operator | Result |
|---|---|
| `&&` | `TRUE` only if both terms are true. |
| `||` | `TRUE` if at least one term is true. |
