# SYSTEM

The built-in object exposing operating-system information. Available under the global name `SYSTEM` from any context (see [Built-in objects](../engine/globals.md#built-in-objects)).

## Methods

### GETDATE

```
INTEGER GETDATE()
```

Returns the current date encoded as an integer in the format `(year-2000)*10000 + month*100 + day`. For example, `26 March 2026` becomes `260326`.

**Returns**: the encoded date.

**Compatibility:** `GETDATE` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### GETMHZ

```
INTEGER GETMHZ()
```

Returns the processor's clock frequency in megahertz.

**Returns**: the CPU frequency in MHz.

**Compatibility:** `GETMHZ` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ❌, `PIKLIB8.DLL` ⚠️ (8/10), `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### GETSYSTEMTIME

```
INTEGER GETSYSTEMTIME()
```

Returns the operating system's uptime in milliseconds.

**Returns**: the uptime in milliseconds.

**Compatibility:** `GETSYSTEMTIME` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.
