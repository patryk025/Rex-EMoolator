# STATICFILTER

A graphical filter — an effect applied to [`ANIMO`](ANIMO.md) or [`IMAGE`](IMAGE.md) variables. Each filter instance has one effect kind ([`ACTION`](#action)) and a set of properties set via [`SETPROPERTY`](#setproperty). A filter is attached to a specific graphic with [`LINK`](#link) — from then on property changes take effect in real time.

A single `STATICFILTER` can be attached to multiple graphics at once.

## Fields

### ACTION

```
STRING ACTION
```

Filter effect type. Accepted values (per `sub_100A4490` in the Piklib8 library):

| Value | Effect |
| --- | --- |
| `COLORCHANNEL` | RGB channel manipulation |
| `GRAYSCALE` | convert to grayscale |
| `BLUR` | blur |
| `ROTATE` | rotate |
| `SCALE` | scale |
| `NEGATIVE` | invert colours |
| `RANDOMJITTER` | random per-pixel jitter |
| `WAVES` | wave distortion |

## Methods

### LINK

```
void LINK(STRING objectName)
```

Attaches the filter to an [`ANIMO`](ANIMO.md) or [`IMAGE`](IMAGE.md) variable. The current property values are forwarded to the freshly created effect, which then takes effect immediately.

**Parameters**

- `objectName` — graphics variable name.

**Examples**

```
FJITTER^LINK("IMGZOOM");
FROTATE^LINK(ARRCARS^GET(0));
```

**Compatibility:** `LINK` - type visible in the export, but with no resolved C++ class - no method data.

### SETPROPERTY

```
void SETPROPERTY(STRING propertyName, mixed value)
```

Sets a filter property. Accepted names depend on the chosen [`ACTION`](#action):

| Property | Type | Filters |
| --- | --- | --- |
| `CANUNDO` | [`BOOL`](BOOL.md) | all |
| `CURRENTFRAME` | [`BOOL`](BOOL.md) | all |
| `CHANNELS` | [`STRING`](STRING.md) | `COLORCHANNEL` |
| `MAXJITTER` | [`INTEGER`](INTEGER.md) | `RANDOMJITTER` |
| `BLUR` | [`INTEGER`](INTEGER.md) | `BLUR` |
| `ANGLE` | [`INTEGER`](INTEGER.md) | `ROTATE` |
| `BYCENTER` | [`BOOL`](BOOL.md) | `ROTATE`, `SCALE` |
| `FACTORX` | [`INTEGER`](INTEGER.md) | `SCALE` |
| `FACTORY` | [`INTEGER`](INTEGER.md) | `SCALE` |

**Parameters**

- `propertyName` — name of the property to set.
- `value` — new property value.

**Examples**

```
FCOLOR^SETPROPERTY("CANUNDO","TRUE");
FCOLOR^SETPROPERTY("CHANNELS","B");
FJITTER^SETPROPERTY("MAXJITTER",7);
FROTATE^SETPROPERTY("ANGLE",IKONANGLE);
```

**Compatibility:** `SETPROPERTY` - type visible in the export, but with no resolved C++ class - no method data.

### UNLINK

```
void UNLINK(STRING objectName)
```

Detaches the filter from a graphics variable — removes the effect.

**Parameters**

- `objectName` — graphics variable name.

**Examples**

```
FROTATE^UNLINK("ANNKON");
FROTATE^UNLINK(ARRCARS^GET(VARPLAYER));
```

**Compatibility:** `UNLINK` - type visible in the export, but with no resolved C++ class - no method data.

## Signals

### ONINIT

Fired when the object is initialised.

### ONSIGNAL

Fired when a signal arrives (see [Events and signals](../engine/events.md#onsignal)).
