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

**Compatibility:** `ADDBODY` - type from a dynamically loaded library, outside the scope of the current `compat.json` export.

### ADDFORCE

```
void ADDFORCE(INTEGER objectId, DOUBLE forceX, DOUBLE forceY, [DOUBLE forceZ])
void ADDFORCE(INTEGER objectId, DOUBLE forceX, DOUBLE forceY, DOUBLE forceZ,
              DOUBLE pointX, DOUBLE pointY, DOUBLE pointZ)
```

Applies a force to the body along three axes. Omitting `forceZ` is equivalent to passing `0.0`. The seven-argument form applies the force at `(pointX, pointY, pointZ)`, so it can also rotate the body.

**Parameters**

- `objectId` — body identifier.
- `forceX`, `forceY`, `forceZ` — force components.
- `pointX`, `pointY`, `pointZ` — world-space application point (seven-argument form only).

**Examples**

```
WORLD^ADDFORCE(100,VARFORCEX,VARFORCEY,0);
WTEST^ADDFORCE(501,0,VARD_TMP1,0);
```

**Compatibility:** `ADDFORCE` - type from a dynamically loaded library, outside the scope of the current `compat.json` export.

### ADDGRAVITYEX

```
void ADDGRAVITYEX(INTEGER objectId, INTEGER secondObjectId, BOOL gravityEx)
```

Controls an exception to central gravity. When `gravityEx` is `TRUE`, `objectId` is **not attracted** by the centre `secondObjectId`; `FALSE` removes that exception and re-enables its influence. This method does not create a gravity field: the centre still needs [`SETGRAVITYCENTER`](#setgravitycenter) enabled and a non-zero [`SETG`](#setg).

Despite its name, this is effectively an "add gravity exclusion" call.

**Parameters**

- `objectId` — body affected by the exception.
- `secondObjectId` — gravity-centre identifier to exclude.
- `gravityEx` — `TRUE` adds the exclusion, `FALSE` removes it; an omitted argument behaves as `TRUE`.

**Examples**

```
WTEST^ADDGRAVITYEX(VARI_ID,_I_,TRUE);
```

**Compatibility:** `ADDGRAVITYEX` - type from a dynamically loaded library, outside the scope of the current `compat.json` export.

### ADDOBJECT

```
void ADDOBJECT(INTEGER objectId, DOUBLE x, DOUBLE y, DOUBLE z,
               DOUBLE dim1, DOUBLE dim2, DOUBLE dim3, DOUBLE mass,
               INTEGER rigidFlag, DOUBLE maxSpeed)
```

Legacy shorthand for creating a dynamic sphere and placing it. `rigidFlag` is accepted for compatibility but not used by the emulator. New code should prefer [`ADDBODY`](#addbody), which makes geometry and contact settings explicit.

**Compatibility:** `ADDOBJECT` - type from a dynamically loaded library, outside the scope of the current `compat.json` export.

### SETFORCE / SETOBJECTFORCE

```
void SETFORCE(INTEGER objectId, DOUBLE forceX, DOUBLE forceY, [DOUBLE forceZ])
void SETOBJECTFORCE(INTEGER objectId, DOUBLE forceX, DOUBLE forceY, [DOUBLE forceZ])
```

Both names are aliases for the regular [`ADDFORCE`](#addforce) form. They do not set a persistent force; they add force for the current simulation step.

**Compatibility:** `SETFORCE` - type from a dynamically loaded library, outside the scope of the current `compat.json` export.
**Compatibility:** `SETOBJECTFORCE` - type from a dynamically loaded library, outside the scope of the current `compat.json` export.

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

**Compatibility:** `FINDPATH` - type from a dynamically loaded library, outside the scope of the current `compat.json` export.

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

**Compatibility:** `FOLLOWPATH` - type from a dynamically loaded library, outside the scope of the current `compat.json` export.

### GETANGLE

```
DOUBLE GETANGLE(INTEGER objectId)
```

Returns the angle derived from the body's velocity vector (in degrees).

**Parameters**

- `objectId` — body identifier.

**Returns**: [`DOUBLE`](DOUBLE.md) — angle in degrees.

**Compatibility:** `GETANGLE` - type from a dynamically loaded library, outside the scope of the current `compat.json` export.

### GETBKGPOSX

```
INTEGER GETBKGPOSX()
```

Returns the X position of the background associated with the physics world.

**Compatibility:** `GETBKGPOSX` - type from a dynamically loaded library, outside the scope of the current `compat.json` export.

### GETBKGPOSY

```
INTEGER GETBKGPOSY()
```

Returns the Y position of the background associated with the physics world.

**Compatibility:** `GETBKGPOSY` - type from a dynamically loaded library, outside the scope of the current `compat.json` export.

### GETCOLLISION

```
BOOL GETCOLLISION(INTEGER objectId, [INTEGER otherObjectId])
```

Returns `TRUE` when the body collided during the current simulation step. With `otherObjectId`, it checks specifically for a collision with that body. The result is reported only for bodies whose collision monitoring is enabled with [`SETACTIVE`](#setactive).

**Compatibility:** `GETCOLLISION` - type from a dynamically loaded library, outside the scope of the current `compat.json` export.

### GETMOVEDISTANCE

```
DOUBLE GETMOVEDISTANCE(INTEGER objectId)
```

Returns the distance the body has covered since the measurement was last reset.

**Parameters**

- `objectId` — body identifier.

**Compatibility:** `GETMOVEDISTANCE` - type from a dynamically loaded library, outside the scope of the current `compat.json` export.

### GETPOSITIONX

```
DOUBLE GETPOSITIONX(INTEGER objectId)
```

Returns the body's X position in screen-space (offset by `+400` from the physics origin).

**Compatibility:** `GETPOSITIONX` - type from a dynamically loaded library, outside the scope of the current `compat.json` export.

### GETPOSITIONY

```
DOUBLE GETPOSITIONY(INTEGER objectId)
```

Returns the body's Y position in screen-space (axis flipped — `300 - Y`).

**Compatibility:** `GETPOSITIONY` - type from a dynamically loaded library, outside the scope of the current `compat.json` export.

### GETPOSITIONZ

```
DOUBLE GETPOSITIONZ(INTEGER objectId)
```

Returns the body's Z position.

**Compatibility:** `GETPOSITIONZ` - type from a dynamically loaded library, outside the scope of the current `compat.json` export.

### GETROTATIONZ

```
DOUBLE GETROTATIONZ(INTEGER objectId)
```

Returns the body's rotation around the Z axis (in degrees).

**Compatibility:** `GETROTATIONZ` - type from a dynamically loaded library, outside the scope of the current `compat.json` export.

### GETROTATIONX / GETROTATIONY

```
DOUBLE GETROTATIONX(INTEGER objectId)
DOUBLE GETROTATIONY(INTEGER objectId)
```

Return the body's rotation around the X or Y axis, respectively, in degrees.

**Compatibility:** `GETROTATIONX` - type from a dynamically loaded library, outside the scope of the current `compat.json` export.
**Compatibility:** `GETROTATIONY` - type from a dynamically loaded library, outside the scope of the current `compat.json` export.

### GETSPEED

```
DOUBLE GETSPEED(INTEGER objectId)
```

Returns the body's speed (linear velocity magnitude).

**Compatibility:** `GETSPEED` - type from a dynamically loaded library, outside the scope of the current `compat.json` export.

### JOIN

```
void JOIN(INTEGER firstId, INTEGER secondId,
          DOUBLE anchorX, DOUBLE anchorY, DOUBLE anchorZ, DOUBLE limitMotor,
          [DOUBLE lowStop, DOUBLE highStop,
           DOUBLE hingeAxisX, DOUBLE hingeAxisY, DOUBLE hingeAxisZ])
```

Creates a hinge joint between two bodies. Valid variants have 6, 8, or 11 arguments. Without stops, the rotation range is `-90` to `90` degrees; the default axis is Y, `(0, 1, 0)`.

**Parameters**

- `firstId`, `secondId` — body identifiers to join.
- `anchorX`, `anchorY`, `anchorZ` — joint anchor point.
- `limitMotor` — force required to break the joint.
- `lowStop`, `highStop` — optional rotation limits, in degrees.
- `hingeAxisX`, `hingeAxisY`, `hingeAxisZ` — (optional) rotation axis.

**Examples**

```
WORLD^JOIN(199,200,400,300,0,0,-180,180);
WTEST^JOIN(VARI_ID,VARI_TMP1,VARI_X,VARI_TMP2,0,0,-180,180,0,1,0);
```

**Compatibility:** `JOIN` - type from a dynamically loaded library, outside the scope of the current `compat.json` export.

### JOIN2, JOINTSTEER, JOINTSPEED, and BREAK

```
void JOIN2(INTEGER firstId, INTEGER secondId,
           DOUBLE anchorX, DOUBLE anchorY, DOUBLE anchorZ,
           DOUBLE axis1X, DOUBLE axis1Y, DOUBLE axis1Z,
           DOUBLE axis2X, DOUBLE axis2Y, DOUBLE axis2Z)
void JOINTSTEER(INTEGER objectId, DOUBLE angle)
void JOINTSPEED(INTEGER objectId, DOUBLE speed)
void BREAK(INTEGER objectId)
```

`JOIN2` creates a two-axis (`hinge2`) joint, used for example by wheels. `JOINTSTEER` controls its steering and `JOINTSPEED` its motor speed; `BREAK` destroys the joint assigned to the body. Steering and speed have no effect on an ordinary `JOIN` hinge.

**Compatibility:** `JOIN2` - type from a dynamically loaded library, outside the scope of the current `compat.json` export.
**Compatibility:** `JOINTSTEER` - type from a dynamically loaded library, outside the scope of the current `compat.json` export.
**Compatibility:** `JOINTSPEED` - type from a dynamically loaded library, outside the scope of the current `compat.json` export.
**Compatibility:** `BREAK` - type from a dynamically loaded library, outside the scope of the current `compat.json` export.

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

**Compatibility:** `LINK` - type from a dynamically loaded library, outside the scope of the current `compat.json` export.

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

**Compatibility:** `LOAD` - type from a dynamically loaded library, outside the scope of the current `compat.json` export.

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

**Compatibility:** `MOVEOBJECTS` - type from a dynamically loaded library, outside the scope of the current `compat.json` export.

### PAUSELINK / RESUMELINK

```
void PAUSELINK(INTEGER objectId)
void RESUMELINK(INTEGER objectId)
```

Pause or resume updates of the graphic bound through [`LINK`](#link). The body's physics continues to run; only propagation of its position to `ANIMO` or `IMAGE` is paused.

**Compatibility:** `PAUSELINK` - type from a dynamically loaded library, outside the scope of the current `compat.json` export.
**Compatibility:** `RESUMELINK` - type from a dynamically loaded library, outside the scope of the current `compat.json` export.

### REMOVEOBJECT

```
void REMOVEOBJECT(INTEGER objectId)
```

Removes a body from the physics engine.

**Examples**

```
WTEST^REMOVEOBJECT(60);
```

**Compatibility:** `REMOVEOBJECT` - type from a dynamically loaded library, outside the scope of the current `compat.json` export.

### SETACTIVE

```
void SETACTIVE(INTEGER objectId, BOOL active, [BOOL monitorCollisions])
```

Sets the body's active state and, separately, collision reporting through [`GETCOLLISION`](#getcollision). The second flag does not disable physical contacts; it disables their reporting to the script. When omitted, it defaults to `active`.

**Parameters**

- `objectId` — body identifier.
- `active` — whether the body participates in the simulation.
- `monitorCollisions` — whether collisions are recorded and reported to the script.

**Examples**

```
WPATH^SETACTIVE(VARI_3DPATHID,$1,$2);
```

**Compatibility:** `SETACTIVE` - type from a dynamically loaded library, outside the scope of the current `compat.json` export.

### SETBKGSIZE

```
void SETBKGSIZE(INTEGER leftX, INTEGER rightX, INTEGER topY, INTEGER bottomY)
```

Sets the size of the background rectangle associated with the world.

**Compatibility:** `SETBKGSIZE` - type from a dynamically loaded library, outside the scope of the current `compat.json` export.

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

**Compatibility:** `SETG` - type from a dynamically loaded library, outside the scope of the current `compat.json` export.

### SETMASS, SETBODYPROPERTIES, and SETBODYDYNAMICS

```
void SETMASS(INTEGER objectId, DOUBLE mass)
void SETBODYPROPERTIES(INTEGER objectId, DOUBLE mass,
                       DOUBLE sizeX, DOUBLE sizeY, DOUBLE sizeZ)
void SETBODYDYNAMICS(INTEGER objectId, DOUBLE mu, DOUBLE mu2,
                     DOUBLE bounce, DOUBLE bounceVelocity, DOUBLE maxVelocity)
```

`SETMASS` changes only mass. `SETBODYPROPERTIES` changes mass and geometry dimensions together, while `SETBODYDYNAMICS` changes contact settings and the speed cap. These calls let scripts adjust an existing body without recreating it.

**Compatibility:** `SETMASS` - type from a dynamically loaded library, outside the scope of the current `compat.json` export.
**Compatibility:** `SETBODYPROPERTIES` - type from a dynamically loaded library, outside the scope of the current `compat.json` export.
**Compatibility:** `SETBODYDYNAMICS` - type from a dynamically loaded library, outside the scope of the current `compat.json` export.

### SETCOLLISIONTYPE

```
void SETCOLLISIONTYPE(INTEGER objectId, INTEGER collisionType)
```

Assigns a numeric collision type. The emulator retains this value for compatibility but does not yet use it to filter contacts.

**Compatibility:** `SETCOLLISIONTYPE` - type from a dynamically loaded library, outside the scope of the current `compat.json` export.

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

**Compatibility:** `SETGRAVITY` - type from a dynamically loaded library, outside the scope of the current `compat.json` export.

### SETGRAVITYCENTER

```
void SETGRAVITYCENTER(INTEGER objectId, BOOL gravityCenter)
```

Toggles whether a body is treated as the source of a central gravitational field.

**Parameters**

- `objectId` — body identifier.
- `gravityCenter` — toggle flag.

**Compatibility:** `SETGRAVITYCENTER` - type from a dynamically loaded library, outside the scope of the current `compat.json` export.

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

**Compatibility:** `SETLIMIT` - type from a dynamically loaded library, outside the scope of the current `compat.json` export.

### SETMAXSPEED

```
void SETMAXSPEED(INTEGER objectId, INTEGER maxSpeed)
```

Caps the body's speed.

**Examples**

```
WORLD^SETMAXSPEED(100,200);
```

**Compatibility:** `SETMAXSPEED` - type from a dynamically loaded library, outside the scope of the current `compat.json` export.

### SETMOVEFLAGS

```
void SETMOVEFLAGS(DOUBLE moveX, DOUBLE moveY)
```

Controls camera tracking on the X and Y axes for the body selected by [`SETREFOBJECT`](#setrefobject). A non-negative value enables tracking on an axis; a negative value disables it. It does not affect physical movement.

**Examples**

```
WPATH^SETMOVEFLAGS(VARITEMP0,VARITEMP1);
```

**Compatibility:** `SETMOVEFLAGS` - type from a dynamically loaded library, outside the scope of the current `compat.json` export.

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

**Compatibility:** `SETPOSITION` - type from a dynamically loaded library, outside the scope of the current `compat.json` export.

### SETPOSITIONCOORD

```
void SETPOSITIONCOORD(INTEGER objectId, INTEGER coordIndex, DOUBLE value)
```

Sets one position component: `coordIndex = 0` is X and `1` is Y. Other indices are ignored. X and Y use screen coordinates, just like `SETPOSITION`.

**Compatibility:** `SETPOSITIONCOORD` - type from a dynamically loaded library, outside the scope of the current `compat.json` export.

### ROTATE and ZEROALL

```
void ROTATE(INTEGER objectId, [DOUBLE ignored], DOUBLE angle)
void ZEROALL(INTEGER objectId)
```

`ROTATE` sets an absolute rotation around Z in degrees. With three arguments, the middle argument is ignored; with two, the second is the angle. `ZEROALL` clears linear and angular velocity, accumulated force, and torque.

**Compatibility:** `ROTATE` - type from a dynamically loaded library, outside the scope of the current `compat.json` export.
**Compatibility:** `ZEROALL` - type from a dynamically loaded library, outside the scope of the current `compat.json` export.

### SETREFOBJECT

```
void SETREFOBJECT(INTEGER objectId)
```

Marks a body as the reference object — used when computing positions relative to it.

**Examples**

```
WPATH^SETREFOBJECT(100);
```

**Compatibility:** `SETREFOBJECT` - type from a dynamically loaded library, outside the scope of the current `compat.json` export.

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

**Compatibility:** `SETVELOCITY` - type from a dynamically loaded library, outside the scope of the current `compat.json` export.

### START

```
void START()
```

Starts the simulation (enables the physics engine's timer).

**Examples**

```
WORLD^START();
```

**Compatibility:** `START` - type from a dynamically loaded library, outside the scope of the current `compat.json` export.

### STOP

```
void STOP()
```

Stops the simulation (disables the physics engine's timer). Body state is preserved.

**Examples**

```
WORLD^STOP();
```

**Compatibility:** `STOP` - type from a dynamically loaded library, outside the scope of the current `compat.json` export.

### UNLINK

```
void UNLINK(INTEGER objectId)
```

Breaks the animation binding established by [`LINK`](#link).

**Examples**

```
WTEST^UNLINK(VARI_TMP2);
```

**Compatibility:** `UNLINK` - type from a dynamically loaded library, outside the scope of the current `compat.json` export.

## Compatibility stubs

The following calls are recognised so scripts do not fail on an unknown method, but currently do nothing in the emulator: `SETANGLE`, `SETSPEED`, `SETDISPERSION`, `SETALWAYSACTIVE`, `ENABLEMESH`, `COLLIDEMESH`, `USEFF`, and `ADDMESHWAYPOINT`. `ISMESHENABLED` and `ISMESHCOLLIDING` always return `FALSE`.

The navigation calls `SETOBJECTPROPERTIES`, `FOLLOW`, `MOVETO`, `MOVETOPATH`, `GOTOPATH`, `ADDTOPATH`, `ADDWAYPOINT`, `REMOVEWAYPOINT`, `REMOVEROUTE`, and `FOLLOWROUTE` are likewise accepted but not implemented yet. For working graph navigation, use [`FINDPATH`](#findpath) and [`FOLLOWPATH`](#followpath).

## Signals

### ONINIT

Fired when the object is initialised.

### ONSIGNAL

Fired when a signal arrives (see [Events and signals](../engine/events.md#onsignal)).
