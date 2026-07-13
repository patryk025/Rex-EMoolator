# CNVLOADER

Dynamic loader for `.CNV` files at runtime. Unlike [`CLASS`](CLASS.md), which defines an isolated per-instance context, `CNVLOADER` merges variables from the loaded file directly into the current context — they behave as if they had been defined there from the start.

A single `CNVLOADER` can hold several `.CNV` files loaded at once. Each [`RELEASE`](#release) call frees one specific file.

## Methods

### LOAD

```
void LOAD(STRING cnvFile)
```

Loads the given `.CNV` file. Variables defined in the file are added to the current context. Re-loading an already-loaded file is a no-op.

**Parameters**

- `cnvFile` — path to the `.CNV` file (resolved through the engine VFS).

**Examples**

```
CNVLOADER^LOAD(VARSTEMP0);
CNVLOADER^LOAD([G_SCUTSCENE+".CNV"]);
```

**Compatibility:** `LOAD` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### RELEASE

```
void RELEASE(STRING cnvFile)
```

Unloads a previously loaded file — removes from the current context every variable that came from it. Calling this on a file that has not been loaded is a no-op.

**Parameters**

- `cnvFile` — path to a previously loaded file.

**Examples**

```
CNVLOADER^RELEASE([G_SCUTSCENE+".CNV"]);
CNVLOADER^RELEASE("WYNURZENIE.CNV");
```

**Compatibility:** `RELEASE` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

## Signals

### ONINIT

Fired when the object is initialised.

### ONSIGNAL

Fired when a signal arrives (see [Events and signals](../engine/events.md#onsignal)).
