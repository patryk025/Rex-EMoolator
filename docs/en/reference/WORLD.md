# WORLD

Interface to the 3D physics engine built on the Sekai library — a thin wrapper around Open Dynamics Engine (ODE). The world manages rigid bodies, joints, and navigation paths, and applies forces, gravity, and damping. The 3D counterpart used in Piklib-era games. Conceptually equivalent to [`INERTIA`](INERTIA.md), but with access to the third axis and a substantially wider API.

Each body in the world is identified by an integer (`objectId`). The world is loaded from a `.SEK` file holding the body definitions, physical parameters, collision meshes, the per-scene list of navigation points, and their connections.

## Geometry types

Values accepted by the `geomType` argument of [`ADDBODY`](#addbody):

| Value | Geometry |
| --- | --- |
| `0` | box |
| `1` | cylinder |
| `2` | sphere |
| `3` | trimesh (only used while loading from `.SEK`) |
| `4` | car (four wheels + box) |

## Fields

### FILENAME

```
STRING FILENAME
```

Path to the `.SEK` file with the physics-world definition.

## Methods

### ADDBODY

```
void ADDBODY(INTEGER objectId, DOUBLE mass, DOUBLE mu, DOUBLE mu2,
             DOUBLE bounce, DOUBLE bounceVelocity, DOUBLE maxVelocity,
             INTEGER bodyType, INTEGER geomType,
             DOUBLE dim1, DOUBLE dim2, DOUBLE dim3)
```

Creates a new physics body in the world. `mass`, `mu`, `mu2`, `bounce`, and `bounceVelocity` map directly onto their ODE counterparts: mass, friction, secondary-direction friction, bounciness, and minimum velocity required for a bounce. `maxVelocity` caps the body's speed.

The dimensions `dim1`, `dim2`, `dim3` depend on `geomType`:

- **box** — X, Y, Z lengths.
- **cylinder** — `dim1` is the radius, `dim2` is the height; `dim3` is ignored.
- **sphere** — `dim1` is the radius; `dim2` and `dim3` are ignored.

**Parameters**

- `objectId` — identifier of the new body.
- `mass` — body mass.
- `mu`, `mu2` — friction coefficients.
- `bounce` — bounciness factor.
- `bounceVelocity` — minimum velocity required for a bounce.
- `maxVelocity` — speed cap.
- `bodyType` — body type (ODE-reserved meaning).
- `geomType` — geometry type (see [Geometry types](#geometry-types)).
- `dim1`, `dim2`, `dim3` — dimensions interpreted per geometry.

**Examples**

```
WORLD^ADDBODY(100,10,0.0,10000.0,0.0,0.0,40000,1,2,30,16,16);
WORLD^ADDBODY(VARINT0,0.1,0.5,0.5,0.0,0.0,100000,1,2,16,16,16);
```

### ADDFORCE

```
void ADDFORCE(INTEGER objectId, DOUBLE forceX, DOUBLE forceY, [DOUBLE forceZ])
```

Applies a force to the body along three axes. Omitting `forceZ` is equivalent to passing `0.0`.

**Parameters**

- `objectId` — body identifier.
- `forceX`, `forceY`, `forceZ` — force components.

**Examples**

```
WORLD^ADDFORCE(100,VARFORCEX,VARFORCEY,0);
WTEST^ADDFORCE(501,0,VARD_TMP1,0);
```

### ADDGRAVITYEX

```
void ADDGRAVITYEX(INTEGER objectId, INTEGER secondObjectId, BOOL gravityEx)
```

Adds an extended gravity interaction between two bodies. The exact behaviour has not been established.

**Examples**

```
WTEST^ADDGRAVITYEX(VARI_ID,_I_,TRUE);
```

### FINDPATH

```
void FINDPATH(INTEGER objectId, INTEGER pointObjectId,
              INTEGER targetX, INTEGER targetY, INTEGER targetZ,
              BOOL saveIntermediates, [BOOL flag])
```

Computes a path for an object from its current position to a target point using the navigation graph loaded from the `.SEK` file. The result is cached by the physics engine and used by subsequent [`FOLLOWPATH`](#followpath) calls.

**Parameters**

- `objectId` — identifier of the body to navigate.
- `pointObjectId` — identifier of the navigation anchor point.
- `targetX`, `targetY`, `targetZ` — target coordinates.
- `saveIntermediates` — if `TRUE`, intermediate path points are kept.
- `flag` — (optional) configuration flag (meaning unknown).

**Examples**

```
WPATH^FINDPATH(100,VARIPATHID,$3,$4,0,TRUE,FALSE);
WPATH^FINDPATH(101,VARIPATHID,VARIKRETGOX,VARIKRETGOY,0,FALSE);
```

### FOLLOWPATH

```
DOUBLE FOLLOWPATH(INTEGER objectId, INTEGER arrivalRadius, DOUBLE turnClamp, DOUBLE speed)
```

Advances the body along the path previously computed by [`FINDPATH`](#findpath). Returns the remaining distance to the goal.

**Parameters**

- `objectId` — body identifier.
- `arrivalRadius` — radius within which the body is considered to have arrived.
- `turnClamp` — per-step turn limit.
- `speed` — movement speed.

**Returns**: [`DOUBLE`](DOUBLE.md) — remaining distance.

**Examples**

```
WPATH^FOLLOWPATH(100,20,0.5,VARDMAXVEL);
WPATH^FOLLOWPATH(101,20,0.5,VARD_KRETSPEED);
```

### GETANGLE

```
DOUBLE GETANGLE(INTEGER objectId)
```

Returns the angle derived from the body's velocity vector (in degrees).

**Parameters**

- `objectId` — body identifier.

**Returns**: [`DOUBLE`](DOUBLE.md) — angle in degrees.

### GETBKGPOSX

```
INTEGER GETBKGPOSX()
```

Returns the X position of the background associated with the physics world.

### GETBKGPOSY

```
INTEGER GETBKGPOSY()
```

Returns the Y position of the background associated with the physics world.

### GETMOVEDISTANCE

```
DOUBLE GETMOVEDISTANCE(INTEGER objectId)
```

Returns the distance the body has covered since the measurement was last reset.

**Parameters**

- `objectId` — body identifier.

### GETPOSITIONX

```
INTEGER GETPOSITIONX(INTEGER objectId)
```

Returns the body's X position in screen-space (offset by `+400` from the physics origin).

### GETPOSITIONY

```
INTEGER GETPOSITIONY(INTEGER objectId)
```

Returns the body's Y position in screen-space (axis flipped — `300 - Y`).

### GETPOSITIONZ

```
INTEGER GETPOSITIONZ(INTEGER objectId)
```

Returns the body's Z position.

### GETROTATIONZ

```
DOUBLE GETROTATIONZ(INTEGER objectId)
```

Returns the body's rotation around the Z axis (in degrees).

### GETSPEED

```
DOUBLE GETSPEED(INTEGER objectId)
```

Returns the body's speed (linear velocity magnitude).

### JOIN

```
void JOIN(INTEGER firstId, INTEGER secondId,
          DOUBLE anchorX, DOUBLE anchorY, DOUBLE anchorZ,
          DOUBLE limitMotor, DOUBLE lowStop, DOUBLE highStop,
          [DOUBLE hingeAxisX, DOUBLE hingeAxisY, DOUBLE hingeAxisZ])
```

Creates a hinge joint between two bodies. The optional axis arguments specify the rotation axis — defaults to `(0, 0, 1)`.

**Parameters**

- `firstId`, `secondId` — body identifiers to join.
- `anchorX`, `anchorY`, `anchorZ` — joint anchor point.
- `limitMotor` — force required to break the joint.
- `lowStop`, `highStop` — rotation limits.
- `hingeAxisX`, `hingeAxisY`, `hingeAxisZ` — (optional) rotation axis.

**Examples**

```
WORLD^JOIN(199,200,400,300,0,0,-180,180);
WTEST^JOIN(VARI_ID,VARI_TMP1,VARI_X,VARI_TMP2,0,0,-180,180,0,1,0);
```

### LINK

```
void LINK(INTEGER objectId, STRING objectName)
```

Binds a physics body to an [`ANIMO`](ANIMO.md) or [`IMAGE`](IMAGE.md) variable — the graphic's position is updated from the body's physics state.

**Parameters**

- `objectId` — body identifier.
- `objectName` — graphics variable name.

**Examples**

```
WPATH^LINK(100,"ANNREX");
WORLD^LINK(VARINT0,VARSTRING0);
```

### LOAD

```
void LOAD(STRING filename)
```

Resets the physics engine and loads the world from a `.SEK` file.

**Parameters**

- `filename` — path to the `.SEK` file.

**Examples**

```
WPATH^LOAD(SOBJECT|NAME);
```

### MOVEOBJECTS

```
DOUBLE MOVEOBJECTS()
```

Steps the simulation forward by one frame and moves every body according to the physics. Returns the simulated time that elapsed during this step.

**Returns**: [`DOUBLE`](DOUBLE.md) — simulation step duration.

**Examples**

```
WORLD^MOVEOBJECTS();
```

### REMOVEOBJECT

```
void REMOVEOBJECT(INTEGER objectId)
```

Removes a body from the physics engine.

**Examples**

```
WTEST^REMOVEOBJECT(60);
```

### SETACTIVE

```
void SETACTIVE(INTEGER objectId, BOOL active, BOOL collidable)
```

Sets a body's activity state.

**Parameters**

- `objectId` — body identifier.
- `active` — whether the body participates in the simulation.
- `collidable` — whether the body participates in collision detection.

**Examples**

```
WPATH^SETACTIVE(VARI_3DPATHID,$1,$2);
```

### SETBKGSIZE

```
void SETBKGSIZE(INTEGER leftX, INTEGER rightX, INTEGER topY, INTEGER bottomY)
```

Sets the size of the background rectangle associated with the world.

### SETG

```
void SETG(INTEGER objectId, DOUBLE g)
```

Sets a per-body gravitational constant (used to model magnets). Defaults to `0`, i.e. no attraction.

**Parameters**

- `objectId` — body identifier.
- `g` — gravitational constant.

**Examples**

```
WTEST^SETG(VARI_ID,VARD_MAGNESREACT);
WTEST^SETG(501,-7000000);
```

### SETGRAVITY

```
void SETGRAVITY(DOUBLE gravityX, DOUBLE gravityY, DOUBLE gravityZ)
```

Sets the world's global gravity vector.

**Parameters**

- `gravityX`, `gravityY`, `gravityZ` — gravity components.

**Examples**

```
WORLD^SETGRAVITY(0,0,-1000);
WORLD^SETGRAVITY(0,-15,0);
```

### SETGRAVITYCENTER

```
void SETGRAVITYCENTER(INTEGER objectId, BOOL gravityCenter)
```

Toggles whether a body is treated as the source of a central gravitational field.

**Parameters**

- `objectId` — body identifier.
- `gravityCenter` — toggle flag.

### SETLIMIT

```
void SETLIMIT(INTEGER objectId, INTEGER minX, INTEGER minY, INTEGER minZ,
              INTEGER maxX, INTEGER maxY, INTEGER maxZ)
```

Limits the body's position to a box-shaped region.

**Parameters**

- `objectId` — body identifier.
- `minX`, `minY`, `minZ` — lower bounds.
- `maxX`, `maxY`, `maxZ` — upper bounds.

**Examples**

```
WORLD^SETLIMIT(100,0,0,0,800,600,999999);
```

### SETMAXSPEED

```
void SETMAXSPEED(INTEGER objectId, INTEGER maxSpeed)
```

Caps the body's speed.

**Examples**

```
WORLD^SETMAXSPEED(100,200);
```

### SETMOVEFLAGS

```
void SETMOVEFLAGS(BOOL moveX, BOOL moveY)
```

Enables or disables movement along the X and Y axes. Full meaning and context of use have not been established.

**Examples**

```
WPATH^SETMOVEFLAGS(VARITEMP0,VARITEMP1);
```

### SETPOSITION

```
void SETPOSITION(INTEGER objectId, DOUBLE x, DOUBLE y, DOUBLE z)
```

Sets the body's absolute position (screen-space coordinates — the engine converts them to physics space).

**Examples**

```
WORLD^SETPOSITION(100,150,400,0);
WTEST^SETPOSITION(VARI_ID,VARI_X,VARI_Y,0);
```

### SETREFOBJECT

```
void SETREFOBJECT(INTEGER objectId)
```

Marks a body as the reference object — used when computing positions relative to it.

**Examples**

```
WPATH^SETREFOBJECT(100);
```

### SETVELOCITY

```
void SETVELOCITY(INTEGER objectId, DOUBLE speedX, DOUBLE speedY, DOUBLE speedZ)
```

Sets the body's velocity along three axes.

**Examples**

```
WORLD^SETVELOCITY(207,5000000,0,0);
WORLD^SETVELOCITY(VARPLAYERID,0,0,0);
```

### START

```
void START()
```

Starts the simulation (enables the physics engine's timer).

**Examples**

```
WORLD^START();
```

### STOP

```
void STOP()
```

Stops the simulation (disables the physics engine's timer). Body state is preserved.

**Examples**

```
WORLD^STOP();
```

### UNLINK

```
void UNLINK(INTEGER objectId)
```

Breaks the animation binding established by [`LINK`](#link).

**Examples**

```
WTEST^UNLINK(VARI_TMP2);
```

## Signals

### ONINIT

Fired when the object is initialised.

### ONSIGNAL

Fired when a signal arrives (see [Events and signals](../engine/events.md#onsignal)).
