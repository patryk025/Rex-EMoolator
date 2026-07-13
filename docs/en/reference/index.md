# Type reference

List of data types available in scripts for the Piklib/BlooMoo engine, grouped by topic.

## Compatibility

Every method description includes the libraries in which the automated API comparison found it. `PIKLIB8.DLL (5/10 variants)` means that the method occurs in five of ten identified variants of that library. APIs dynamically loaded through `CMC_ExternObject`, such as `WORLD` and `INERTIA`, are not visible in this export and are labelled separately. See [`compatibility-audit.md`](../../compatibility-audit.md) for the full list of gaps and limitations.

## Types used in scripts

### Primitives

- [BOOL](BOOL.md) — boolean value.
- [DOUBLE](DOUBLE.md) — double-precision floating-point number.
- [INTEGER](INTEGER.md) — signed integer number.
- [STRING](STRING.md) — character string.

### Collections

- [ARRAY](ARRAY.md) — one-dimensional array.
- [MULTIARRAY](MULTIARRAY.md) — multi-dimensional array with automatic resizing.

### Logical conditions

- [CONDITION](CONDITION.md) — comparison of two operands.
- [COMPLEXCONDITION](COMPLEXCONDITION.md) — combination of two conditions with `AND`/`OR`.

### Code structure

- [BEHAVIOUR](BEHAVIOUR.md) — procedure.
- [CLASS](CLASS.md) — class definition.

### Scene hierarchy

- [APPLICATION](APPLICATION.md) — top of the script hierarchy.
- [EPISODE](EPISODE.md) — logical segment of the game.
- [SCENE](SCENE.md) — a single scene.

### Interaction and composition

- [BUTTON](BUTTON.md) — interactive button with three visual states.
- [CANVAS_OBSERVER](CANVAS_OBSERVER.md) — canvas and background operations.
- [CNVLOADER](CNVLOADER.md) — dynamic `.CNV` file loading.
- [GROUP](GROUP.md) — variable group with delegated method calls.
- [PATTERN](PATTERN.md) — multi-layer tile board.
- [STATICFILTER](STATICFILTER.md) — graphical filter (rotate, scale, blur).
- [VIRTUALGRAPHICSOBJECT](VIRTUALGRAPHICSOBJECT.md) — virtual graphics proxy object.

### Data

- [DATABASE](DATABASE.md) — tabular database with cursor.

### 3D physics

- [WORLD](WORLD.md) — interface to the ODE-based 3D physics engine.

### Built-in I/O objects

- [KEYBOARD](KEYBOARD.md) — keyboard state.
- [MOUSE](MOUSE.md) — mouse state.
- [RAND](RAND.md) — pseudo-random number generator.
- [SYSTEM](SYSTEM.md) — system information.

### Media

- [ANIMO](ANIMO.md) — animation from an `.ANN` file.
- [FONT](FONT.md) — bitmap font definition.
- [IMAGE](IMAGE.md) — static image.
- [SEQUENCE](SEQUENCE.md) — animation sequence with synchronised audio.
- [SOUND](SOUND.md) — short sound effect.
- [TEXT](TEXT.md) — on-screen text element.

### Math and utility

- [EXPRESSION](EXPRESSION.md) — two-operand arithmetic expression.
- [INERTIA](INERTIA.md) — interface to the built-in 2D physics engine.
- [MATRIX](MATRIX.md) — grid of cells with a stone-falling physics system.
- [STRUCT](STRUCT.md) — data structure with named fields.
- [TIMER](TIMER.md) — cyclic time counter.
- [VECTOR](VECTOR.md) — N-dimensional vector of floating-point numbers.
