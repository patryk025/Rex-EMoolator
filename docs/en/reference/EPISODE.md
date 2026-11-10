# EPISODE

A logical segment of the game — a container of scenes ([`SCENE`](SCENE.md)) inside an [`APPLICATION`](APPLICATION.md). In practice, AidemMedia games used a single episode for the whole game.

## Fields

### SCENES

```
STRING SCENES
```

The list of scene names that make up the episode, separated by commas.

### PATH

```
STRING PATH
```

Path relative to the `dane` directory containing the episode's files. Used by the engine when locating the scenes' `.CNV` files.

### STARTWITH

```
STRING STARTWITH
```

The name of the scene that starts the episode.

### Metadata

The following fields are stored as metadata and do not directly affect engine behaviour:

- `AUTHOR` — file author.
- `CREATIONTIME` — file creation date.
- `DESCRIPTION` — episode description.
- `LASTMODIFYTIME` — file last-modification date.
- `VERSION` — episode version.

## Methods

### BACK

```
void BACK()
```

Returns to the scene that was active immediately before the current one. Like [`GOTO`](#goto), it only requests the switch, and the scene changes once the current frame ends.

The episode keeps a scene history and appends every scene it enters. `BACK` picks the second-to-last entry and drops the last one, and does nothing when the history has a single entry. The scene it returns to is appended again, so a second `BACK` in a row does not go further back: it re-enters the same scene. Example: with history `A, B, C`, the first `BACK` goes to `B` (history `A, B, B`), and the second goes to `B` again.

!!! note "Verification"
    History and `BACK` behaviour checked in `BlooMooDLL.dll` (Reksio i Wehikuł Czasu), in `CMC_Episode::back`.

**Examples**

```
PRZYGODA^BACK();
```

**Compatibility:** `BACK` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### GETCURRENTSCENE

```
STRING GETCURRENTSCENE()
```

Returns the name of the currently active scene.

**Returns**: the scene name.

**Examples**

```
PRZYGODA^GETCURRENTSCENE();
```

**Compatibility:** `GETCURRENTSCENE` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### GETLATESTSCENE

```
STRING GETLATESTSCENE()
```

Returns the name of the scene the episode unloaded last, which is usually the scene active before the current one. The value changes only when a switch actually happens, so a call right after [`GOTO`](#goto) in the same frame still returns the old value. It is not always the scene [`BACK`](#back) leads to: after `BACK` from `C` to `B`, it returns `C`.

**Returns**: the name of the scene unloaded last, or an empty string when there was none.

**Compatibility:** `GETLATESTSCENE` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### GOTO

```
void GOTO(STRING sceneName)
```

Switches the game to the given scene.

The switch is **deferred**. `GOTO` only stores the target scene, and the main loop performs the switch once the frame's input, timers, animations, sounds and collisions have run. The rest of the script that called `GOTO`, and every handler around it, therefore finishes in the **old** scene, on its objects. `@BREAK()` after `GOTO` only leaves the current handler, not the ones that called it.

- Several `GOTO` calls in one frame overwrite each other: the last one wins. A `GOTO` with an unknown name cancels an earlier request.
- A `GOTO` made while the target scene is loading (from its `__INIT__` or `ONINIT`) is lost, because the loop clears the request only after the switch has finished.
- The exception is the episode's start scene. It is entered directly, at game start or on an episode change, so a `GOTO` from its `__INIT__` is kept and runs right after. Redirecting scenes rely on this, for example `START` in Reksio i Kapitan Nemo.

!!! note "Verification"
    Deferral checked in `PIKLIB61.DLL`, `PIKLIB71.DLL`, `PIKLIB8.DLL` and `BlooMooDLL.dll` (`CMC_Scene::_goto` and `CMC_Scene::domodal`). Frame order and the lost `GOTO` from initialisation checked in `BlooMooDLL.dll`.

**Parameters**

- `sceneName` — target scene name.

**Examples**

```
PRZYGODA^GOTO("CREDITS");
PRZYGODA^GOTO("MAGIC");
PRZYGODA^GOTO(G_SARCADEOBJECTS);
PRZYGODA^GOTO(UFO^RUN(["VARLEVEL"+VARNR], "GET"));
```

**Compatibility:** `GOTO` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.
