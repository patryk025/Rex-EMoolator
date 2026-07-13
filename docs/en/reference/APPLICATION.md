# APPLICATION

The application object — the top of the script hierarchy. Declared in [`Application.def`](../engine/scripts.md#entry-point) as the first object; lists the episodes and points to the one to start with.

## Fields

### EPISODES

```
STRING EPISODES
```

The list of episode names ([`EPISODE`](EPISODE.md)) that make up the application, separated by commas. The analysed games typically contain a single entry.

### PATH

```
STRING PATH
```

Path relative to the `dane` directory where the application's files live. Used by the engine when locating the `.CNV` file bound to the application (see [Loading subsequent files](../engine/scripts.md#loading-subsequent-files)).

### STARTWITH

```
STRING STARTWITH
```

Name of the episode the engine will start the game with.

### Metadata

The following fields are stored in the script as metadata and do not directly affect engine behaviour:

- `AUTHOR` — file author.
- `BLOOMOO_VERSION` — BlooMoo engine version.
- `CREATIONTIME` — file creation date.
- `DESCRIPTION` — application description.
- `LASTMODIFYTIME` — file last-modification date.
- `VERSION` — application version.

## Methods

### EXIT

```
void EXIT()
```

Terminates the application.

**Examples**

```
GAME^EXIT();
```

**Compatibility:** `EXIT` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### GETLANGUAGE

```
STRING GETLANGUAGE()
```

Returns the application's currently selected language. Defaults to `"POL"`.

**Returns**: the language code.

**Examples**

```
UFO^GETLANGUAGE();
```

**Compatibility:** `GETLANGUAGE` - `PIKLIB61.DLL` ⚠️ (3/4), `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### RUN

```
mixed RUN(STRING varName, STRING methodName, [mixed param1, ..., mixed paramN])
```

Invokes the method `methodName` on the variable `varName`, forwarding the remaining arguments. The return value is whatever the called method returns. This is the engine's dynamic-dispatch mechanism — both the target variable and the method can be selected at runtime.

**Parameters**

- `varName` — name of the target variable.
- `methodName` — name of the method to invoke.
- `param1, …, paramN` — (optional) arguments forwarded to the call.

**Returns**: the value returned by the invoked method.

**Examples**

```
UFO^RUN(VARSTRINGTEMP, "SETASBUTTON", FALSE, FALSE);
UFO^RUN(VARSTRINGTEMP, "HIDE");
UFO^RUN(["ANIMO"+$1], "HIDE");
UFO^RUN($1, "PLAY", $2);
UFO^RUN(ARRCARS^GET(VARPLAYER), "SETPRIORITY", ARRPRIORITY^GET(VARPLAYER));
```

**Compatibility:** `RUN` - `PIKLIB61.DLL` ⚠️ (2/4), `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### RUNENV

```
mixed RUNENV(STRING sceneName, STRING behaviourName)
```

Calls the procedure `behaviourName`, but only when the currently active scene has the name `sceneName`. Otherwise the method has no effect. Useful for procedures that only make sense in a particular scene context.

**Parameters**

- `sceneName` — name of the scene in which the procedure must run.
- `behaviourName` — name of the procedure to call.

**Returns**: the value returned by the procedure, or `NULL` if the scene guard was not satisfied.

**Examples**

```
GAME^RUNENV(SCENENAME, "__HELPSTART__");
GAME^RUNENV(SCENENAME, "B_PAUSE_START");
GAME^RUNENV(SCENENAME, "__CUTINIT__");
```

**Compatibility:** `RUNENV` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### SETLANGUAGE

```
void SETLANGUAGE(STRING languageCode)
```

Sets the application's language code. The engine maps the passed Windows LCID code to an internal language identifier per the table below:

| LCID   | Language  | Internal ID | Subfolder |
|--------|-----------|-------------|-----------|
| `0415` | Polish    | `1`         | `POL`     |
| `0405` | Czech     | `2`         | `CZE`     |
| `0402` | Bulgarian | `3`         | `BUL`     |
| `0418` | Romanian  | `4`         | `ROM`     |
| `0419` | Russian   | `5`         | `RUS`     |
| `040E` | Hungarian | `6`         | `HUN`     |
| `041B` | Slovak    | `7`         | `SLO`     |
| `0422` | Ukrainian | `8`         | `UKR`     |
| `0407` | German    | `9`         | `NIEM`    |
| `0c07` | German    | `10`        | `NIEM`    |
| `0807` | German    | `11`        | `NIEM`    |

The selected identifier determines the localized-assets subfolder the engine consults when loading game files (see [Loading subsequent files](../engine/scripts.md#loading-subsequent-files)). Identifiers `9`, `10`, and `11` (during my ongoing research, I've only seen them in *Reksio i Kretes w Akcji*) all map to the `NIEM` subfolder — the German-language build. Any identifier outside the listed range yields an empty subfolder. Setting the language also re-initialises the keyboard layout.

**Parameters**

- `languageCode` — the LCID as a four-digit hexadecimal number.

**Examples**

```
UFO^SETLANGUAGE("0415");
UFO^SETLANGUAGE("040E");
UFO^SETLANGUAGE("0419");
```

**Compatibility:** `SETLANGUAGE` - `PIKLIB61.DLL` ⚠️ (3/4), `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.
