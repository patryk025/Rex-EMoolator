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

Returns to the scene that was active immediately before the current one.

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

Returns the name of the scene that was active immediately before the current one — the scene that [`BACK`](#back) would return to.

**Returns**: the previous scene's name.

**Compatibility:** `GETLATESTSCENE` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### GOTO

```
void GOTO(STRING sceneName)
```

Switches the game to the given scene.

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
