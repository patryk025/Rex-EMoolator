# SEQUENCE

An animation sequence. The `.SEQ` file contains **sequence events** — descriptions of [`ANIMO`](ANIMO.md) animation runs played in sync with accompanying [`SOUND`](SOUND.md) effects. Sequences let you treat picture and audio as a single, script-controlled unit.

## Fields

### FILENAME

```
STRING FILENAME
```

Path to the `.SEQ` file holding the sequence definition.

## Methods

### GETEVENTNAME

```
STRING GETEVENTNAME()
```

Returns the name of the sequence event currently being played.

**Returns**: event name.

**Examples**

```
SEQSFX^GETEVENTNAME();
```

### GETPLAYING

```
STRING GETPLAYING()
```

Returns the name of the [`ANIMO`](ANIMO.md) variable being played as part of the currently active event. If no event is active, an empty string is returned.

**Returns**: the animation name or `""`.

### HIDE

```
void HIDE()
```

Hides every animation belonging to the sequence.

**Examples**

```
SEQJEAN^HIDE();
SEQKRET^HIDE();
```

### ISPLAYING

```
BOOL ISPLAYING()
```

Checks whether the sequence is currently playing.

**Returns**: [`BOOL`](BOOL.md) — `TRUE` if the sequence is in playback.

**Examples**

```
SEQBLANK^ISPLAYING();
SEQMANDOLINA^ISPLAYING();
```

### PAUSE

```
void PAUSE()
```

Pauses the sequence's playback.

**Examples**

```
SEQCS^PAUSE();
```

### PLAY

```
void PLAY(STRING eventName)
```

Starts playing the sequence event with the given name. On start, the [`ONSTARTED`](#onstarted) signal is fired with the event name as its argument.

**Parameters**

- `eventName` — name of the event from the `.SEQ` file.

**Examples**

```
GADAJA2^PLAY("KOGF2");
SEQNARRATOR^PLAY(VARSTRING0);
SEQLAB^PLAY(["PLAYER"+VARINT0]);
SEQREKSIO^PLAY($1);
```

### RESUME

```
void RESUME()
```

Resumes a sequence paused with [`PAUSE`](#pause).

**Examples**

```
SEQCS^RESUME();
```

### SETFREQ

```
void SETFREQ(INTEGER sampleRate)
```

Sets the sample rate used by the sound attached to the currently active sequence event. Equivalent to calling [`SETFREQ`](SOUND.md#setfreq) on the [`SOUND`](SOUND.md) object of that event.

**Parameters**

- `sampleRate` — the target sample rate in Hz.

### SETPAN

```
void SETPAN(INTEGER pan)
```

Sets the stereo panning (left/right balance) of the active event's sound. A value of `400` corresponds to a centred mix, `0` to fully left, and `800` to fully right.

**Parameters**

- `pan` — panning value in the `0–800` range.

### SETVOLUME

```
void SETVOLUME(INTEGER volume)
```

Sets the active event sound's volume. A value of `1600` corresponds to maximum volume; `0` mutes the sound.

**Parameters**

- `volume` — volume value in the `0–1600` range.

### SHOW

```
void SHOW()
```

Shows every animation belonging to the sequence.

### STOP

```
void STOP([BOOL emitSignal])
```

Stops the sequence's playback.

**Parameters**

- `emitSignal` — (optional) if `FALSE`, the [`ONFINISHED`](#onfinished) signal is suppressed. By default, the signal is fired.

**Examples**

```
SEQBLANK^STOP(FALSE);
SEQMENU^STOP(TRUE);
SEQZMIANAWAGIREX^STOP();
```

## Signals

### ONINIT

Fired when the object is initialised.

### ONSTARTED

Fired when a sequence event starts playing. The argument (`$1`) is the name of the started event.

### ONFINISHED

Fired when a sequence event finishes (naturally or via a [`STOP`](#stop) call that does not suppress the signal). The argument (`$1`) is the name of the finished event. The signal is [parameterised](../engine/events.md#parameterised-signals) by that name, so a handler can target a specific event:

```
SEQUENCE:ONFINISHED^IDLE=BEHAFTERIDLE
```

### ONSIGNAL

Fired when a signal arrives (see [Events and signals](../engine/events.md#onsignal)).
