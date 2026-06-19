# SCENE

A single scene — one board, screen, or minigame. Belongs to an [`EPISODE`](EPISODE.md). Defines the background, music, and hotspot priority range, and exposes methods that control the scene's contents.

## Fields

### BACKGROUND

```
STRING BACKGROUND
```

Path to the `.IMG` file with the scene's background image.

### DLLS

```
STRING DLLS
```

List of DLL libraries attached to the scene (extensions of the BlooMoo library, e.g. `INERTIA`).

### MUSIC

```
STRING MUSIC
```

Path to the file holding the scene's background music.

### MUSICVOLUME

```
INTEGER MUSICVOLUME
```

The scene music's volume. A value of `1000` corresponds to 100%. Modified by [`SETMUSICVOLUME`](#setmusicvolume).

### MINHSPRIORITY

```
INTEGER MINHSPRIORITY
```

Minimum priority (`Z`) of the hotspots active in the scene. Modified by [`SETMINHSPRIORITY`](#setminhspriority).

### MAXHSPRIORITY

```
INTEGER MAXHSPRIORITY
```

Maximum priority (`Z`) of the hotspots active in the scene. Modified by [`SETMAXHSPRIORITY`](#setmaxhspriority).

### PATH

```
STRING PATH
```

Path relative to the `dane` directory containing the scene's files.

### Metadata

The following fields are stored as metadata and do not directly affect engine behaviour:

- `AUTHOR` — file author.
- `CREATIONTIME` — file creation date.
- `LASTMODIFYTIME` — file last-modification date.
- `VERSION` — scene version.

## Methods

### GETMAXHSPRIORITY

```
INTEGER GETMAXHSPRIORITY()
```

Returns the maximum priority of the scene's active hotspots.

**Returns**: the current value of the [`MAXHSPRIORITY`](#maxhspriority) field.

### GETMINHSPRIORITY

```
INTEGER GETMINHSPRIORITY()
```

Returns the minimum priority of the scene's active hotspots.

**Returns**: the current value of the [`MINHSPRIORITY`](#minhspriority) field.

### GETPLAYINGANIMO

```
void GETPLAYINGANIMO(STRING groupName)
```

Fills the [`GROUP`](index.md) variable named `groupName` with the names of every [`ANIMO`](index.md) currently playing in the scene. Existing contents of the group are overwritten.

**Parameters**

- `groupName` — name of the `GROUP` variable to populate.

### PAUSE

```
void PAUSE()
```

Pauses the scene's music and every playing [`ANIMO`](index.md).

**Examples**

```
BARANDALF^PAUSE();
```

### REMOVECLONES

```
void REMOVECLONES(STRING varName, INTEGER firstId, INTEGER lastId)
```

Removes clones of the variable `varName` with numbers in the range `[firstId, lastId]`. A value of `-1` in `lastId` means "up to the last clone". Clones are named according to the pattern `varName_N`, with `N` starting at `1`.

**Parameters**

- `varName` — base name of the cloned variable.
- `firstId` — number of the first clone to remove (minimum `1`).
- `lastId` — number of the last clone to remove, or `-1` for all clones to the end.

**Examples**

```
ARCADE^REMOVECLONES(VARSCURRENTITEMOBJECT, -1, -1);
CUTSCENKI^REMOVECLONES(SANN, -1, -1);
```

### RESUME

```
void RESUME()
```

Resumes the scene's music (with the volume from the [`MUSICVOLUME`](#musicvolume) field) and every paused [`ANIMO`](index.md).

**Examples**

```
BARANDALF^RESUME();
```

### RESUMEONLY

```
void RESUMEONLY(STRING groupName)
```

Resumes only those paused animations whose names appear in the [`GROUP`](index.md) variable `groupName`.

**Parameters**

- `groupName` — name of the `GROUP` variable holding the animations to resume.

### RUN

```
mixed RUN(STRING varName, STRING methodName, [mixed param1, ..., mixed paramN])
```

Dynamically invokes the method `methodName` on the variable `varName`. Behaves the same as [`APPLICATION.RUN`](APPLICATION.md#run); the scene-level variant exists for scripts that hold a reference to the scene rather than the application.

**Parameters**

- `varName` — name of the target variable.
- `methodName` — name of the method to invoke.
- `param1, …, paramN` — (optional) arguments.

**Returns**: the value returned by the invoked method.

**Examples**

```
S16_SPACEINVADERS^RUN(VARNAME, "SETPOSITION", 0, 0);
S16_SPACEINVADERS^RUN(VARNAME, "PLAY", VARTEMPSTRING);
S16_SPACEINVADERS^RUN(VARUFOHIT, "PLAY", ["TRAFIONY"+RANDOM^GET(0,2)]);
```

### RUNCLONES

```
void RUNCLONES(STRING varName, INTEGER firstId, INTEGER lastId, STRING behaviourName)
```

Invokes the procedure `behaviourName` for each clone of the variable `varName` in the range `[firstId, lastId]`. A value of `-1` in `lastId` means "up to the last clone". The procedure receives the clone's name as its first argument (`$1`).

**Parameters**

- `varName` — base name of the cloned variable.
- `firstId` — number of the first clone (minimum `1`).
- `lastId` — number of the last clone, or `-1`.
- `behaviourName` — name of the procedure to invoke.

**Examples**

```
S16_SPACEINVADERS^RUNCLONES("ANIMOUFO", -1, -1, "BEHINITUFO");
S71_DROGA^RUNCLONES("ANNKURA", -1, -1, "BEHINITCLONES");
```

### SETMAXHSPRIORITY

```
void SETMAXHSPRIORITY(INTEGER maxHSPriority)
```

Sets the maximum priority of the scene's active hotspots.

**Parameters**

- `maxHSPriority` — the new maximum priority.

### SETMINHSPRIORITY

```
void SETMINHSPRIORITY(INTEGER minHSPriority)
```

Sets the minimum priority of the scene's active hotspots.

**Parameters**

- `minHSPriority` — the new minimum priority.

**Examples**

```
MENUGLOWNE^SETMINHSPRIORITY(999);
MENUGLOWNE^SETMINHSPRIORITY(0);
```

### SETMUSICVOLUME

```
void SETMUSICVOLUME(INTEGER volume)
```

Sets the scene music's volume. A value of `1000` corresponds to 100%. The change is applied immediately if the music is currently playing.

**Parameters**

- `volume` — the new volume.

**Examples**

```
ARCADE^SETMUSICVOLUME(G_ARRSETTINGS^GET(1));
INTRO_2^SETMUSICVOLUME(500);
DIALOGS^SETMUSICVOLUME([0.8*G_ARRSETTINGS^GET(1)]);
```

### STARTMUSIC

```
void STARTMUSIC(STRING filename)
```

Stores the path of the music file in the [`MUSIC`](#music) field; the engine plays it as the scene's background music.

**Parameters**

- `filename` — path to the music file.

**Examples**

```
ARCADE^STARTMUSIC(VARSMUSIC);
MAGIC^STARTMUSIC("POJEDYNEK.WAV");
DIALOGS^STARTMUSIC("GABINETY.WAV");
```
