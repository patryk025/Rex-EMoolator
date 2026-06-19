# Events and signals

The Piklib/BlooMoo engine drives game logic with a reactive, signal-based model. Every object can emit named signals, and scripts can attach procedures or code blocks to react to them. This chapter describes how the mechanism works and lists the most common signals.

## Attaching a signal handler

A signal is attached like any other property of an object — the value is either a procedure name or a code block:

```
OBJECT_NAME:ONCHANGED=PROCEDURE_NAME
OBJECT_NAME:ONCHANGED={OTHER_OBJECT^PLAY("TADA");}
```

Each signal name can have at most one handler attached to it on a given object.

## Parameterised signals

Some signals — notably [`ONCHANGED`](#onchanged) and [`ONBRUTALCHANGED`](#onbrutalchanged) — are emitted together with a value. That value can be used to filter the handler: the engine first looks up a handler matching the pair `signal + value`, and only falls back to a generic handler for the signal name if no specific one is registered.

The discriminator follows the signal name after a caret `^`:

```
OBJECT_NAME:ONBRUTALCHANGED^3=HANDLER_FOR_THREE
OBJECT_NAME:ONBRUTALCHANGED=GENERIC_HANDLER
```

In the example above, `OBJECT^SET(3)` runs `HANDLER_FOR_THREE`; any other value runs `GENERIC_HANDLER`.

## Handler execution

The signal system is synchronous and single-threaded. Emitting a signal immediately transfers control to its handler: the currently executing procedure is paused, the handler runs, and once it returns, control resumes where the signal was emitted. Signals are not queued.

A chain of nested signal–procedure–signal–procedure invocations forms a call tree. The jump operators affect that tree as follows:

- [`@ONEBREAK`](scripts.md#jump-operators) — aborts only the current procedure and returns to its caller.
- [`@BREAK`](scripts.md#jump-operators) — aborts the entire call tree started by the original signal emission.

## Signal arguments

Some signals are emitted with extra arguments. Inside a handler, those arguments are available exactly like procedure arguments — through `$1`, `$2`, … (numbered from `1`). This lets a handler react to the specific value that triggered the signal.

## Universal signals

### ONINIT

Fired during the variable's initialisation as its file is loaded. The order in which `ONINIT` is fired across the variables of a single file is fixed and goes by type; the details are in [Variable initialisation](scripts.md#variable-initialisation).

Typical uses: filling in [arrays](../reference/index.md), setting up the initial state of animations, loading data from external files.

### ONSIGNAL

A general-purpose signal, emitted by calling the global `SEND` method on an object:

```
OBJECT_NAME^SEND("MY_SIGNAL");
```

The string passed in is available inside the handler as the first argument (`$1`). This mechanism allows custom events to be defined without relying on value changes or animation events.

## Value-change signals

Fired by [primitive types](../reference/index.md) (and any other type with a `VALUE` field) whenever the value is modified.

### ONCHANGED

Fired only when the new value differs from the previous one. The argument (`$1`) is the new value.

### ONBRUTALCHANGED

Fired on every modification, even if the new value is identical to the previous one. The argument (`$1`) is the new value.

In practice, `ONBRUTALCHANGED` is useful for detecting that a value-setting method (`SET`, `INC`, `SWITCH`, …) was called at all, regardless of whether it actually changed the value.

## Signals from graphical objects and sequences

### ONSTARTED

Fired when an animation or sequence starts playing.

- For a **sequence** — fired once, with the current event name from the `.SEQ` file as its argument.
- For an **animation** — fired with the event name from the `.ANN` file. If the animation is driven by a sequence, this signal can fire multiple times as the animation loops.

### ONFINISHED

Fired when playback finishes:

- for an **animation** — after the last frame is displayed and playback stops,
- for a **sequence** — after the last event finishes (or for each individual event played in turn, depending on configuration).

In both cases the signal is also fired in response to a manual `STOP` call (with no argument or with `TRUE`).
