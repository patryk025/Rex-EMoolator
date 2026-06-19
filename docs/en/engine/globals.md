# Global and built-in variables

The engine exposes a handful of variables and names that can be referenced from anywhere in the scripts, regardless of context depth. This chapter covers the built-in objects, the implicit variables, the special procedures, and the variable-lookup hierarchy.

## Context hierarchy

Each loaded script creates its own variable context. Contexts are nested: a scene's context inherits from its episode's context, which inherits from the application's context, which in turn inherits from the engine's global context. Variable lookup walks from the lowest context upwards — a variable in a lower context shadows one with the same name in a higher context, but higher-context variables remain visible from lower ones.

## Built-in objects

The following objects are created lazily by the engine on first access and are available from any context under fixed names:

| Name | Type | Description |
|---|---|---|
| `MOUSE` | [`MOUSE`](../reference/index.md) | Mouse state (position, buttons). |
| `KEYBOARD` | [`KEYBOARD`](../reference/index.md) | Keyboard state. |
| `RAND` | [`RAND`](../reference/index.md) | Pseudo-random number generator. Also available under the alias `RANDOM`. |
| `SYSTEM` | [`SYSTEM`](../reference/index.md) | Interface to system functions (time, environment). |

All four objects are singletons in the engine's global context — every script reference resolves to the same instance.

## Objects from `Application.def`

Objects defined in `Application.def` — of type [`APPLICATION`](../reference/index.md), [`EPISODE`](../reference/index.md), and [`SCENE`](../reference/index.md) — are loaded into the engine's global context and remain visible to every script in the game. Other types in that file are ignored (see [Entry point](scripts.md#entry-point)).

## Implicit variables

The engine injects a few variables that are not declared explicitly in scripts.

### `_I_`

The loop counter set up by the [`@LOOP`](scripts.md#loop) statement. It is an [`INTEGER`](../reference/INTEGER.md) variable created locally in the current iteration's context. Its value updates automatically as the loop progresses.

Inside a `@LOOP`, `_I_` can be read in arithmetic expressions and passed as a method argument:

```
@LOOP({*["ANIMO_"+_I_]^PLAY();}, 0, 10, 1);
```

The [`@FOR`](scripts.md#for-bloomoo) loop allows a custom counter name to be supplied; in that case `_I_` is not set.

### `THIS`

A reference to the object that emitted the signal currently being handled. Only available inside a signal-handling block and procedures called from it. Its behaviour is detailed in [The THIS variable](scripts.md#the-this-variable).

### `$1`, `$2`, … `$N`

The arguments of a procedure or signal handler (numbered from `1`). Available only inside the body of a procedure or handler block:

```
PROCEDURE:CODE={VARIABLE_NAME^SET($1);}
```

This syntax is also covered in [Procedure arguments](scripts.md#procedure-arguments).

## Special procedures

Certain procedure names have conventional meaning — the engine invokes them automatically at fixed points in the lifecycle.

### `__ONINIT__`

Called once the initialisation of every variable in a loaded file has finished. See [Variable initialisation](scripts.md#variable-initialisation). A common use is to set the scene's initial state once all of its objects are available.

### `__INIT__`

Called after the scene has been loaded and variable initialisation has completed — just before control is handed off to the game's logic. Used to set scene-specific state that depends on the current episode or application.

## Naming convention

Names surrounded by double underscores (`__NAME__`) are reserved for the engine and for global conventional names (e.g. `__KEYB__`, `__INIT__`, `__ONINIT__`). AidemMedia scripts also use this format for their own globally significant variables to avoid being shadowed in local contexts.
