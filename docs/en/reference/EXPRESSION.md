# EXPRESSION

A named two-operand arithmetic expression. Reading the variable's value recomputes `OPERAND1 OPERATOR OPERAND2` in the current context every time, so the result tracks live changes to the input variables.

Operands may be numeric literals, variable names, or bracketed sub-expressions (see [Arithmetic](../engine/arithmetic.md)). `EXPRESSION` exposes no script methods.

## Fields

### OPERAND1

```
STRING OPERAND1
```

Left-hand operand of the expression.

### OPERAND2

```
STRING OPERAND2
```

Right-hand operand of the expression.

### OPERATOR

```
STRING OPERATOR
```

Binary operator applied to the operands. Accepted values:

| Value | Operation |
| --- | --- |
| `ADD` | addition |
| `SUB` | subtraction |
| `MUL` | multiplication |
| `DIV` | division |
| `MOD` | modulo |

The result-type rules (integer vs floating-point) mirror those of ordinary script arithmetic — see [Arithmetic — typing rule](../engine/arithmetic.md#typing-rule).

## Signals

### ONINIT

Fired when the object is initialised.

### ONSIGNAL

Fired when a signal arrives (see [Events and signals](../engine/events.md#onsignal)).
