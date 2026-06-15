# ANIMO

An animation loaded from an `.ANN` file. The most complex visual type in the engine — supports multiple events (frame sequences), variable FPS, an anchor point, opacity, collision monitoring, and a button mode.

An animation is made up of **events**, each of which is a sequence of **frames**. The frame index inside an event starts at `0`, and the global frame index across the whole animation is also tracked separately from `0`.

## Fields

### ASBUTTON

```
BOOL ASBUTTON
```

Treats the animation as a clickable button. Modified through [`SETASBUTTON`](#setasbutton).

### FILENAME

```
STRING FILENAME
```

Name of the `.ANN` file with the animation. Read at variable initialisation; can be overwritten via [`LOAD`](#load).

### FPS

```
INTEGER FPS
```

Number of animation frames per second. Modified through [`SETFPS`](#setfps).

### MONITORCOLLISION

```
BOOL MONITORCOLLISION
```

Whether the animation participates in collision detection. Modified through [`MONITORCOLLISION`](#monitorcollision-1) and [`REMOVEMONITORCOLLISION`](#removemonitorcollision).

### MONITORCOLLISIONALPHA

```
BOOL MONITORCOLLISIONALPHA
```

Whether collision detection takes the alpha channel into account.

### PRELOAD

```
BOOL PRELOAD
```

Whether the animation data is loaded eagerly at initialisation.

### PRIORITY

```
INTEGER PRIORITY
```

Rendering priority (`Z`) relative to other scene objects.

### TOCANVAS

```
BOOL TOCANVAS
```

Whether the animation is drawn on the scene's main canvas. Setting this to `FALSE` hides the animation visually, but the engine keeps playing it and firing the associated events.

### VISIBLE

```
BOOL VISIBLE
```

Animation visibility. Modified through [`SHOW`](#show) and [`HIDE`](#hide).

## Methods

### GETANCHOR

```
STRING GETANCHOR()
```

Returns the currently configured anchor, in the form passed to [`SETANCHOR`](#setanchor).

**Returns**: the anchor name or its coordinates.

### GETCENTERX

```
INTEGER GETCENTERX()
```

Returns the X coordinate of the centre of the current frame's bounding box.

**Returns**: centre X.

**Examples**

```
ANNREX^GETCENTERX();
```

### GETCENTERY

```
INTEGER GETCENTERY()
```

Returns the Y coordinate of the centre of the current frame's bounding box.

**Returns**: centre Y.

### GETCFRAMEINEVENT

```
INTEGER GETCFRAMEINEVENT()
```

Returns the index of the current frame inside the currently playing event (counted from `0`).

**Returns**: the frame's index in the event.

**Examples**

```
ANNREX^GETCFRAMEINEVENT();
```

### GETCURRFRAMEPOSX

```
INTEGER GETCURRFRAMEPOSX()
```

Returns the X offset of the currently displayed frame image (per-frame, defined in the animation file).

**Returns**: frame X offset.

### GETCURRFRAMEPOSY

```
INTEGER GETCURRFRAMEPOSY()
```

Returns the Y offset of the currently displayed frame image.

**Returns**: frame Y offset.

### GETENDX

```
INTEGER GETENDX()
```

Returns the right edge of the current frame's bounding box.

**Returns**: right edge X.

### GETENDY

```
INTEGER GETENDY()
```

Returns the bottom edge of the current frame's bounding box.

**Returns**: bottom edge Y.

### GETEVENTNAME

```
STRING GETEVENTNAME()
```

Returns the name of the currently playing event.

**Returns**: event name.

**Examples**

```
ANNREX^GETEVENTNAME();
```

### GETFRAME

```
INTEGER GETFRAME()
```

Returns the global index of the currently displayed frame (independent of the event subdivision).

**Returns**: the global frame index.

### GETFRAMENAME

```
STRING GETFRAMENAME()
```

Returns the name of the currently displayed frame (the frame image file name).

**Returns**: the frame name.

### GETHEIGHT

```
INTEGER GETHEIGHT()
```

Returns the height of the current animation frame.

**Returns**: height in pixels.

### GETMAXHEIGHT

```
INTEGER GETMAXHEIGHT()
```

Returns the maximum height across all of the animation's frames.

**Returns**: the largest height in pixels.

### GETMAXWIDTH

```
INTEGER GETMAXWIDTH()
```

Returns the maximum width across all of the animation's frames.

**Returns**: the largest width in pixels.

**Examples**

```
ANN_STATEK^GETMAXWIDTH();
```

### GETNAME

```
STRING GETNAME()
```

Returns the animation variable's name.

**Returns**: the variable's name.

### GETNOE

```
INTEGER GETNOE()
```

Returns the number of events in the animation (short for *Number Of Events*).

**Returns**: event count.

**Examples**

```
ANNLISCIESLOTY^GETNOE();
ANNBTN^GETNOE();
```

### GETNOF

```
INTEGER GETNOF()
```

Returns the total number of frames in the animation (short for *Number Of Frames*).

**Returns**: frame count.

### GETNOFINEVENT

```
INTEGER GETNOFINEVENT(INTEGER eventId)
INTEGER GETNOFINEVENT(STRING eventName)
```

Returns the number of frames in the given event. The event can be identified by its number (from `0`) or by its name (case-insensitive). For a non-existent event, `0` is returned.

**Parameters**

- `eventId` / `eventName` — event identifier.

**Returns**: frame count in the event.

**Examples**

```
ANNREX^GETNOFINEVENT(VARSTEMP0);
ANNUKLAD^GETNOFINEVENT(0);
ANNPLANNAK^GETNOFINEVENT("IDLE");
```

### GETPOSITIONX

```
INTEGER GETPOSITIONX([BOOL absolute])
```

Returns the X coordinate of the current frame's top-left corner on the canvas. The variant with a `BOOL` argument returns the absolute position, ignoring per-frame offsets defined in the animation file.

**Parameters**

- `absolute` — (optional) `TRUE` to skip per-frame offsets.

**Returns**: position X.

### GETPOSITIONY

```
INTEGER GETPOSITIONY([BOOL absolute])
```

Returns the Y coordinate of the current frame's top-left corner on the canvas. The variant with a `BOOL` argument returns the absolute position.

**Parameters**

- `absolute` — (optional) `TRUE` to skip per-frame offsets.

**Returns**: position Y.

### GETPRIORITY

```
INTEGER GETPRIORITY()
```

Returns the animation's rendering priority (`Z`).

**Returns**: the value of the [`PRIORITY`](#priority) field.

### GETWIDTH

```
INTEGER GETWIDTH()
```

Returns the width of the current animation frame.

**Returns**: width in pixels.

### HIDE

```
void HIDE()
```

Hides the animation visually without stopping its playback. Calling [`PLAY`](#play) automatically restores visibility.

### ISAT

```
BOOL ISAT(INTEGER posX, INTEGER posY)
```

Checks whether the point at the given coordinates lies inside the current frame's bounding box.

**Parameters**

- `posX` — point X coordinate.
- `posY` — point Y coordinate.

**Returns**: [`BOOL`](BOOL.md) — `TRUE` if the point is inside the bounding box.

### ISNEAR

```
BOOL ISNEAR(STRING animoName, INTEGER iouThresholdPercent)
```

Checks whether this animation is close to another animation. Internally, the Jaccard index (*Intersection over Union*, IoU) of the two animations' bounding boxes is computed; if the IoU exceeds the given threshold (in percent), `TRUE` is returned.

**Parameters**

- `animoName` — name of the other animation.
- `iouThresholdPercent` — IoU threshold in percent.

**Returns**: [`BOOL`](BOOL.md) — `TRUE` if the IoU exceeds the threshold.

**Examples**

```
ENEMY^ISNEAR("HERO", 1);
ANNORKA^ISNEAR("ANNLODKA", 12);
```

### ISPLAYING

```
BOOL ISPLAYING()
BOOL ISPLAYING(STRING eventName)
```

Checks whether the animation is currently playing. The no-argument variant returns whether any event is currently playing; with a name, the check is limited to that specific event.

**Parameters**

- `eventName` — (optional) name of the event to check.

**Returns**: [`BOOL`](BOOL.md) — `TRUE` if the animation (or the specific event) is playing.

**Examples**

```
ANNREX^ISPLAYING();
ANNREXGLOWA^ISPLAYING("SPI");
```

### ISVISIBLE

```
BOOL ISVISIBLE()
```

Checks whether the animation is visible ([`VISIBLE`](#visible) = `TRUE` and [`TOCANVAS`](#tocanvas) = `TRUE`).

**Returns**: [`BOOL`](BOOL.md) — `TRUE` if the animation is visible.

### LOAD

```
void LOAD(STRING path)
```

Loads an animation from an `.ANN` file, replacing the previous contents.

**Parameters**

- `path` — `.ANN` file path in the game's VFS.

**Examples**

```
ANNBKG^LOAD(SOBJECT|NAME);
ANNCHARACTER^LOAD("PIXEL.ANN");
ANNMINIMAPA^LOAD([""+ILEVEL+"_MINIMAPA.ANN"]);
```

### MONITORCOLLISION {#monitorcollision-1}

```
void MONITORCOLLISION()
```

Enables collision monitoring between this animation and other objects.

### MONITORCOLLISIONALPHA {#monitorcollisionalpha-1}

```
void MONITORCOLLISIONALPHA()
```

Enables alpha-channel awareness in collision detection.

### MOVE

```
void MOVE(INTEGER offsetX, INTEGER offsetY)
```

Moves the animation by the given offsets relative to its current position.

**Parameters**

- `offsetX` — X offset.
- `offsetY` — Y offset.

**Examples**

```
ANNELEMENT^MOVE(-200, 0);
ANNPLAYER^MOVE(VARDX, VARDY);
ANNITEMDRAGGING^MOVE([IMOUSEX-IMOUSELASTX], [IMOUSEY-IMOUSELASTY]);
```

### NEXTFRAME

```
void NEXTFRAME()
```

Advances the animation to the next frame of the current event.

### NPLAY

```
void NPLAY(INTEGER eventId)
```

Starts playing the event with the given index (counted from `0`).

**Parameters**

- `eventId` — event index.

**Examples**

```
ANNDARK0^NPLAY(VARITEMP2);
CZAS^NPLAY(0);
```

### PAUSE

```
void PAUSE()
```

Pauses the animation on the current frame.

### PLAY

```
void PLAY([STRING eventName])
```

Starts playing an event. The no-argument variant restarts the previously played event from the beginning.

**Parameters**

- `eventName` — (optional) name of the event to play (case-insensitive).

**Examples**

```
G_STLPAGE^PLAY("ELAPSE");
ANNREX^PLAY(VARITEMP0);
ANNKRET^PLAY(["IDLE_"+ANNKRET^GETEVENTNAME()]);
ANIMOREKSIO^PLAY($1);
```

### PREVFRAME

```
void PREVFRAME()
```

Moves the animation to the previous frame of the current event.

### REMOVEMONITORCOLLISION

```
void REMOVEMONITORCOLLISION()
```

Disables collision monitoring previously enabled by [`MONITORCOLLISION`](#monitorcollision-1).

### REMOVEMONITORCOLLISIONALPHA

```
void REMOVEMONITORCOLLISIONALPHA()
```

Disables alpha-channel awareness in collision detection, previously enabled by [`MONITORCOLLISIONALPHA`](#monitorcollisionalpha-1).

### RESUME

```
void RESUME()
```

Resumes playback paused with [`PAUSE`](#pause).

### SETANCHOR

```
void SETANCHOR(STRING anchor)
void SETANCHOR(INTEGER offsetX, INTEGER offsetY)
```

Sets the animation's anchor — the offset that is subtracted from coordinates passed to [`SETPOSITION`](#setposition).

The `STRING` variant accepts a named position derived from the bounding box: `CENTER`, `LEFTUPPER`, `RIGHTUPPER`, `LEFTLOWER`, `RIGHTLOWER`, `LEFT`, `RIGHT`, `TOP`, `BOTTOM`.

The two-`INTEGER` variant accepts the anchor coordinates directly.

**Parameters**

- `anchor` — bounding-box position name.
- `offsetX, offsetY` — anchor coordinates.

**Examples**

```
ANNSELECT^SETANCHOR("CENTER");
ANNREX^SETANCHOR("LEFTLOWER");
ANNREX^SETANCHOR(0, -100);
```

### SETASBUTTON

```
void SETASBUTTON(BOOL enabled, BOOL changeCursor)
```

Configures the animation as a clickable button. Regardless of the argument values, the call makes the animation visible.

**Parameters**

- `enabled` — `TRUE` to activate click handling.
- `changeCursor` — `TRUE` for the cursor to change on hover.

**Examples**

```
ANNEXIT^SETASBUTTON(TRUE, TRUE);
ANIMOPOWROT^SETASBUTTON(FALSE, FALSE);
```

### SETBACKWARD

```
void SETBACKWARD()
```

Sets the playback direction to backwards.

### SETFORWARD

```
void SETFORWARD()
```

Sets the playback direction to forward (the natural direction).

### SETFPS

```
void SETFPS(INTEGER fps)
```

Changes the animation's playback speed.

**Parameters**

- `fps` — frames per second.

**Examples**

```
STLMAGIC^SETFPS(5);
ANNMUCHA1^SETFPS(30);
ANNKON^SETFPS([IKONFPS*8]);
```

### SETFRAME

```
void SETFRAME(INTEGER frameNumber)
void SETFRAME(STRING eventName, INTEGER frameNumber)
void SETFRAME(STRING eventName, STRING frameName)
```

Sets the animation to a specific frame. The one-argument variant selects a frame by its global index. The two-argument variant selects an event and then a position in it (by frame number or frame name).

**Parameters**

- `eventName` — event name.
- `frameNumber` — frame index within an event (from `0`) or a global frame index.
- `frameName` — specific frame name within the event.

**Examples**

```
ANNREX^SETFRAME(VARSTEMP0, [VARITEMP2-1]);
ANNSCIAGA^SETFRAME("PLAY", VARIREPEATSPELL);
OFERTA^SETFRAME(3);
ANN_H_PIECYK^SETFRAME("ROT", "PIECYK4");
```

### SETFRAMENAME

```
void SETFRAMENAME(INTEGER eventId, INTEGER frameNumber, STRING name)
```

Changes the name of a specific frame inside the given event.

**Parameters**

- `eventId` — event index (from `0`).
- `frameNumber` — frame index within the event (from `0`).
- `name` — the new frame name.

**Examples**

```
ANNKALAREPA^SETFRAMENAME(0, 0, "200");
ANNKALAREPA^SETFRAMENAME(1, 0, "300");
```

### SETOPACITY

```
void SETOPACITY(INTEGER opacity)
```

Sets the animation's opacity in the `0–255` scale (`0` — fully transparent, `255` — fully opaque).

**Parameters**

- `opacity` — alpha-channel value.

**Examples**

```
ANNPLAYER0^SETOPACITY(255);
ANNPLAYER^SETOPACITY(100);
```

### SETPOSITION

```
void SETPOSITION(INTEGER posX, INTEGER posY)
```

Sets the animation's absolute position. If an anchor has been set via [`SETANCHOR`](#setanchor), its coordinates are subtracted from the arguments.

**Parameters**

- `posX` — X coordinate.
- `posY` — Y coordinate.

**Examples**

```
ANNREX^SETPOSITION(400, 300);
ANNEXIT^SETPOSITION(-700, -450);
ANNBKG^SETPOSITION([VARIBKGOFFSETX-VARDTEMP0], [VARIBKGOFFSETY-VARDTEMP1]);
```

### SETPRIORITY

```
void SETPRIORITY(INTEGER priority)
```

Sets the rendering priority.

**Parameters**

- `priority` — the new value of the [`PRIORITY`](#priority) field.

**Examples**

```
ANNREX^SETPRIORITY(VARIPRIORITY);
ANNHEAD1^SETPRIORITY(15);
```

### SHOW

```
void SHOW()
```

Shows the animation (sets [`VISIBLE`](#visible) to `TRUE`).

### STOP

```
void STOP([BOOL emitSignal])
```

Stops the animation's playback.

**Parameters**

- `emitSignal` — (optional) if `FALSE`, the [`ONFINISHED`](#onfinished) signal is suppressed. By default, the signal is fired.

**Examples**

```
G_STLPAGE^STOP(FALSE);
ANNREX^STOP(FALSE);
ANNBLANK^STOP();
```

### TOP

```
void TOP([BOOL flag])
```

Modifies how the animation is rendered relative to the scene's top layer. The exact behaviour depends on the current scene composition.

**Parameters**

- `flag` — (optional) a flag changing the rendering mode.

**Examples**

```
ANNWAND0^TOP(FALSE);
ANNHEAD0^TOP(FALSE);
```

## Signals

### ONCLICK

Fired when the animation is clicked, provided it is configured as a button via [`SETASBUTTON`](#setasbutton).

### ONCOLLISION

Fired when a collision is detected.

### ONCOLLISIONFINISHED

Fired when an ongoing collision ends.

### ONDONE

Fired after all of the animation's events have finished.

### ONFINISHED

Fired when an event finishes playing. The signal is [parameterised](../engine/events.md#parameterised-signals) by the event name, so a handler can target a specific event:

```
ANIMATION:ONFINISHED^IDLE=BEHAFTERIDLE
```

### ONFIRSTFRAME

Fired when the event's first frame is displayed.

### ONFOCUSON

Fired when the cursor moves onto the animation, provided it is configured as a button.

### ONFOCUSOFF

Fired when the cursor moves off the animation, provided it is configured as a button.

### ONFRAMECHANGED

Fired when the animation's frame changes.

### ONINIT

Fired when the object is initialised.

### ONPAUSED

Fired when the animation is paused via [`PAUSE`](#pause).

### ONRELEASE

Fired when a mouse button is released over an animation configured as a button.

### ONRESUMED

Fired when the animation is resumed via [`RESUME`](#resume).

### ONSIGNAL

Fired when a signal arrives (see [Events and signals](../engine/events.md#onsignal)).

### ONSTARTED

Fired when an event starts playing. Note: this signal arrives after [`ONFRAMECHANGED`](#onframechanged) for the event's first frame.
