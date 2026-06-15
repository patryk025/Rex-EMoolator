# SOUND

A short sound effect loaded from a `.WAV` file. Supports playback control and sample-rate change, which can be used to dynamically alter the pitch and speed of the played sound.

## Fields

### FILENAME

```
STRING FILENAME
```

Name of the `.WAV` file with the sound. If the path does not start with `$`, the engine prepends `$WAVS\`. The field is read at variable initialisation; it can also be changed at runtime via [`LOAD`](#load).

### PRELOAD

```
BOOL PRELOAD
```

Whether the sound data is loaded eagerly at initialisation or lazily before the first playback.

## Methods

### ISPLAYING

```
BOOL ISPLAYING()
```

Checks whether the sound is currently being played.

**Returns**: [`BOOL`](BOOL.md) — `TRUE` if the sound is playing.

**Examples**

```
SNDATGOAL^ISPLAYING();
SNDREX0^ISPLAYING();
```

### LOAD

```
void LOAD(STRING path)
```

Loads a sound file into the variable, replacing any previously loaded sound. Any ongoing playback is stopped. If the path does not start with `$`, the prefix `$WAVS\` is added.

**Parameters**

- `path` — path to the `.WAV` file in the game's VFS.

**Examples**

```
SNDATGOAL^LOAD(VARSTEMP0);
SNDWAV^LOAD("$WAVS\NAR_I000.WAV");
SNDANSWER^LOAD(ARRSEQ^GET(0));
```

### PAUSE

```
void PAUSE()
```

Pauses the sound's playback.

**Examples**

```
SND_SHIP_GAZ2^PAUSE();
```

### PLAY

```
void PLAY()
```

Starts playing the sound. The [`ONSTARTED`](#onstarted) signal is fired on start, and [`ONFINISHED`](#onfinished) on completion. If the sound is already playing, it is stopped and restarted from the beginning.

**Examples**

```
SNDTAKE^PLAY();
SNDATGOAL^PLAY();
```

### RESUME

```
void RESUME()
```

Resumes playback paused earlier with [`PAUSE`](#pause).

**Examples**

```
SND_SHIP_GAZ2^RESUME();
```

### SETFREQ

```
void SETFREQ(INTEGER sampleRate)
```

Sets the current playback sample rate (in hertz). A value different from the source file's sample rate scales the playback pitch and speed proportionally to the ratio of the two. The engine assumes a default source sample rate of `22050` Hz.

**Parameters**

- `sampleRate` — the target sample rate in Hz.

**Examples**

```
SNDENGINE0^SETFREQ(10025);
```

### STOP

```
void STOP([BOOL emitSignal])
```

Stops the sound's playback.

**Parameters**

- `emitSignal` — (optional) if `FALSE`, the [`ONFINISHED`](#onfinished) signal is suppressed. By default, the signal is fired.

**Examples**

```
SNDATGOAL^STOP(FALSE);
SNDIDLEREX^STOP();
```

## Signals

### ONSTARTED

Fired when playback starts via [`PLAY`](#play).

### ONFINISHED

Fired when playback finishes (naturally or through a [`STOP`](#stop) call that does not suppress the signal).
