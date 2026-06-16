# Scripts

The Piklib/BlooMoo engine drives game logic by interpreting text-based scripts. This chapter describes the syntax of those scripts, how the engine loads them, and the order in which objects are initialised.

## File formats

Scripts are stored in files with the `.CNV`, `.DEF`, `.CLASS`, and `.SEQ` extensions. They all share the same basic textual structure and differ only in their intended use.

The engine treats all script content as uppercase and is case-insensitive. By convention, scripts are written in uppercase.

### Encryption

Files shipped with the game are encrypted by default using a transposition cipher with a variable offset. An encrypted file begins with a header of the form:

```
{<X:N>}
```

where `X` is a letter indicating the direction of the offset (`D` means a negative offset) and `N` is the offset value. The engine detects this header automatically and decrypts the rest of the file before parsing. Files without this header are read as plain text.

## Object declaration

An object begins with a line containing the `OBJECT` keyword:

```
OBJECT=OBJECT_NAME
```

Lines between consecutive `OBJECT=` lines define properties of the current object. The definition lasts until the end of the file or the next `OBJECT=` line.

If the same object is declared more than once in a single file, its properties are merged — later entries override earlier ones.

## Object properties

Properties are written as `objectName:property=value`:

```
OBJECT_NAME:PROPERTY=VALUE
```

Signals can take an additional parameter after a caret (`^`):

```
OBJECT_NAME:ONBRUTALCHANGED^3=PROCEDURE_NAME
```

In both cases, the engine accepts both `KEY=VALUE` and `KEY = VALUE` (with spaces around the equals sign).

## Variable type

The type is essential — without it, the engine does not know how to handle the object, and the result is usually a hard crash. The type is declared with the `TYPE` property:

```
OBJECT_NAME:TYPE=STRING
```

The full list of available types is in the [Type reference](../reference/index.md).

## Literals and strings

How a literal is interpreted depends on its context:

- Text in double quotes (`"..."`) is always treated as a [`STRING`](../reference/STRING.md).
- Unquoted text is first checked against the names of existing variables — if a variable with that name exists, its value is used. Otherwise the text is taken literally.

Floating-point numbers accept both standard notation (`1.234`) and scientific notation with the letter `e` or `d` (`1.23e4`, `1.23d4`).

## Code blocks

Code blocks — used as the value of a signal or as the body of a procedure — are written inside curly braces. Statements are separated by semicolons; the final statement must also end with a semicolon, otherwise it may not be executed.

```
OBJECT_NAME:ONCHANGED={VARIABLE2^PLAY("TADA");}
```

The entire code block must fit on a single line — the engine does not support multi-line blocks directly inside a script file.

## Comments

The engine recognises two forms of comments:

- **Line comment** — a line starting with `#` is skipped entirely.
- **Statement comment** — a single statement preceded by `!` is treated as commented out, up to the next semicolon.

## Method calls

Methods are called using the caret (`^`):

```
OBJECT_NAME^METHOD(arg1, arg2);
```

## Arithmetic expressions

Computational expressions are written inside square brackets:

```
VARIABLE_NAME^SET([VARIABLE_NAME^GET()+"2"]);
```

Operators and typing rules are described in the [Arithmetic](arithmetic.md) chapter.

## String pointers

A `*` before a variable name or an expression means that the value is to be used as the name of another variable. This allows dynamic references built from text:

```
*VARIABLE_NAME^PLAY();
*["ANIMO_"+_I_]^PLAY();
```

In the first form, `VARIABLE_NAME` should be a [`STRING`](../reference/STRING.md) containing the actual object's name. In the second, the object name is constructed from an arithmetic expression.

## Procedure arguments

Inside the body of a procedure, arguments are accessed with a dollar sign followed by a number (numbered from `1`):

```
PROCEDURE:CODE={VARIABLE_NAME^SET($1);}
```

## The THIS variable

Inside a block that handles a signal, an implicit `THIS` variable is available, set to a reference to the object that fired the signal. `THIS` is also accessible from procedures called from within such a block.

`THIS` behaves unusually: calling `GETNAME` on it returns the string `"temp"`, suggesting that under the hood it is a temporary wrapper. The following work reliably on `THIS`:

- `GET` and `SET` for primitive types,
- `SHOW`, `HIDE`, `PLAY`, `PAUSE`, `STOP`, and `RESUME` for graphical objects ([`ANIMO`](../reference/index.md)).

Calling another type-specific method (e.g. `GETCFRAMEINEVENT` on [`ANIMO`](../reference/index.md)) usually crashes the engine. To work around this, AidemMedia scripts use the following pattern: store the object's name in a [`STRING`](../reference/STRING.md) variable first, then call `^RUN(string_variable, method_name)`, which internally resolves the string pointer to the actual object.

## Loops

### @LOOP

```
@LOOP(BEHAVIOUR code, INTEGER start, INTEGER delta, INTEGER increment)
```

Executes `code` for counter values `_I_` in the range `[start, start + delta)` with step `increment`. In pseudocode:

```
for (int _I_ = start; _I_ < start + delta; _I_ += increment) {
    code;
}
```

### @FOR (BlooMoo)

```
@FOR(INTEGER counter, BEHAVIOUR code, INTEGER start, INTEGER delta, INTEGER increment)
```

Identical to `@LOOP`, except that the first argument selects a custom variable to act as the counter instead of the default `_I_`.

### @WHILE

```
@WHILE(mixed value1, STRING comparator, mixed value2, BEHAVIOUR code)
```

Executes `code` as long as the condition `value1 comparator value2` holds. The list of comparators is described below in [Conditional](#conditional).

## Conditional

The engine provides two forms of `@IF`.

### Simple condition

```
@IF(mixed value1, STRING comparator, mixed value2, BEHAVIOUR codeTrue, BEHAVIOUR codeFalse)
```

Available comparators:

| Comparator | Meaning |
|---|---|
| `_` | equal to |
| `!_` | not equal to |
| `<` | less than |
| `<_` | less than or equal |
| `>` | greater than |
| `>_` | greater than or equal |

### Compound condition

```
@IF(STRING condition, BEHAVIOUR codeTrue, BEHAVIOUR codeFalse)
```

Compound conditions add logical operators:

- `&&` — conjunction (and)
- `||` — disjunction (or)

In a compound condition, the equals sign is written as an apostrophe (`'`) rather than an underscore (`_`):

| Comparator | Meaning |
|---|---|
| `'` | equal to |
| `!'` | not equal to |
| `<` | less than |
| `<'` | less than or equal |
| `>` | greater than |
| `>'` | greater than or equal |

## Dynamic variable creation

Variables can be created on the fly inside a code block:

```
@INT(STRING name, INTEGER value)
@DOUBLE(STRING name, DOUBLE value)
@STRING(STRING name, STRING value)
@BOOL(STRING name, BOOL value)
```

Each statement creates a variable of the matching type with the given name and initial value.

## Jump operators

Inside loops and procedures, control flow can be redirected with:

- `@CONTINUE()` — skips the remaining statements in the current loop iteration and moves to the next one.
- `@BREAK()` — aborts the entire call tree started by the current signal or invocation.
- `@ONEBREAK()` — aborts the current procedure only.
- `@RETURN(mixed value)` — sets the value returned by the procedure but does not stop it from executing.

## Script loading order

Scripts in the engine are organised hierarchically: scripts at lower levels can see variables from their own scope and from all ancestors, but not the other way around.

### Entry point

The engine starts from `Application.def` in the `dane` subdirectory. This file defines objects of types [`APPLICATION`](../reference/index.md), [`EPISODE`](../reference/index.md), and [`SCENE`](../reference/index.md) — other types in this file are ignored.

Example contents:

```
OBJECT=GAME
GAME:TYPE=APPLICATION
GAME:PATH=GAME
GAME:EPISODES=PRZYGODA
GAME:STARTWITH=PRZYGODA

OBJECT=PRZYGODA
PRZYGODA:TYPE=EPISODE
PRZYGODA:PATH=GAME\PRZYGODA
PRZYGODA:SCENES=START,CREDITS,LEBIODKA
PRZYGODA:STARTWITH=START

OBJECT=START
START:TYPE=SCENE
START:PATH=GAME\PRZYGODA\START
```

### Loading subsequent files

After `Application.def` is read, the engine loads a `.CNV` file for each defined object. The file path is built from the object's `PATH` attribute (relative to the `dane` directory), the object's name, and the `.CNV` extension. If the file does not exist, loading is silently skipped.

The loading order is:

1. The file bound to the `APPLICATION` object.
2. The file of the first episode (`STARTWITH` attribute of `APPLICATION`).
3. The file of the first scene of that episode (`STARTWITH` attribute of `EPISODE`).

When locating files, the engine also takes the currently selected language into account (see [`APPLICATION.SETLANGUAGE`](../reference/APPLICATION.md#setlanguage)) — the chosen language identifier points to a subfolder of localised assets that is consulted while loading game files.

### Variable initialisation

Within each file, variables are created and initialised in a fixed order by type:

1. Procedures.
2. Primitive types ([`STRING`](../reference/STRING.md), [`DOUBLE`](../reference/DOUBLE.md), [`INTEGER`](../reference/INTEGER.md), [`BOOL`](../reference/BOOL.md)).
3. Arrays and conditions.
4. Animations, images, sounds, and fonts.
5. Buttons, text fields, sequences, mouse, keyboard, canvas observer.

For each variable in this phase the `ONINIT` signal is fired. Once all variables are initialised, the engine calls the `__INIT__` procedure (applies to the BlooMoo) if one is defined.
