# SYSTEM

The built-in object exposing operating-system information. Available under the global name `SYSTEM` from any context (see [Built-in objects](../engine/globals.md#built-in-objects)).

## Methods

### GETDATE

```
INTEGER GETDATE()
```

Returns the current date encoded as an integer in the format `(year-2000)*10000 + month*100 + day`. For example, `26 March 2026` becomes `260326`.

**Returns**: the encoded date.

### GETMHZ

```
INTEGER GETMHZ()
```

Returns the processor's clock frequency in megahertz.

**Returns**: the CPU frequency in MHz.

### GETSYSTEMTIME

```
INTEGER GETSYSTEMTIME()
```

Returns the operating system's uptime in milliseconds.

**Returns**: the uptime in milliseconds.
