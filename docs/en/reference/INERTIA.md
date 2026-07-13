# INERTIA

Interface to the built-in 2D physics engine of the same name (Inertia). Manages rigid bodies: creating objects, linking them to animations, gravity, velocities, damping, and applying forces. Used in *Reksio i Kretes w Akcji*.

Every physics body has an `objectId` — an integer used by most methods to identify the body. The world is loaded from an `.INE` file (see [Engine overview](../engine/index.md)) via [`LOAD`](#load).

## Methods

### ADDFORCE

```
void ADDFORCE(INTEGER objectId, INTEGER forceX, INTEGER forceY)
```

Applies a force to the object along the X and Y axes.

**Parameters**

- `objectId` — object identifier.
- `forceX`, `forceY` — force components.

**Examples**

```
EXTWORLD^ADDFORCE(1,-500,0);
EXTWORLD^ADDFORCE(1,0,-50);
```

**Compatibility:** `ADDFORCE` - type from a dynamically loaded library, outside the scope of the current `compat.json` export.

### CREATESPHERE

```
void CREATESPHERE(INTEGER objectId, INTEGER posX, INTEGER posY, INTEGER radius)
```

Creates a sphere of the given centre position and radius in the physics world, assigning the given identifier.

**Parameters**

- `objectId` — identifier for the new body.
- `posX`, `posY` — sphere centre position.
- `radius` — sphere radius.

**Examples**

```
EXTWORLD^CREATESPHERE(5,10,10,10);
```

**Compatibility:** `CREATESPHERE` - type from a dynamically loaded library, outside the scope of the current `compat.json` export.

### DELETEBODY

```
void DELETEBODY(INTEGER objectId)
```

Removes a body from the physics engine.

**Parameters**

- `objectId` — identifier of the body to remove.

**Examples**

```
EXTWORLD^DELETEBODY(IHANDLEDEL);
EXTWORLD^DELETEBODY(IRAKIETAOBJ);
```

**Compatibility:** `DELETEBODY` - type from a dynamically loaded library, outside the scope of the current `compat.json` export.

### GETPOSITIONX

```
INTEGER GETPOSITIONX(INTEGER objectId)
```

Returns the body's current X position.

**Parameters**

- `objectId` — body identifier.

**Returns**: [`INTEGER`](INTEGER.md) — X coordinate.

**Compatibility:** `GETPOSITIONX` - type from a dynamically loaded library, outside the scope of the current `compat.json` export.

### GETPOSITIONY

```
INTEGER GETPOSITIONY(INTEGER objectId)
```

Returns the body's current Y position.

**Parameters**

- `objectId` — body identifier.

**Returns**: [`INTEGER`](INTEGER.md) — Y coordinate.

**Compatibility:** `GETPOSITIONY` - type from a dynamically loaded library, outside the scope of the current `compat.json` export.

### GETSPEED

```
DOUBLE GETSPEED(INTEGER objectId)
```

Returns the body's speed (linear velocity magnitude).

**Parameters**

- `objectId` — body identifier.

**Returns**: [`DOUBLE`](DOUBLE.md) — speed value.

**Compatibility:** `GETSPEED` - type from a dynamically loaded library, outside the scope of the current `compat.json` export.

### LINK

```
void LINK(INTEGER objectId, STRING animoName, BOOL flag1, BOOL flag2)
```

Binds a physics body to an [`ANIMO`](ANIMO.md) animation — the animation's position is updated from the physics simulation. The meaning of the two boolean flags has not been established (shipping games always pass `TRUE` for both).

**Parameters**

- `objectId` — physics body identifier.
- `animoName` — name of the [`ANIMO`](ANIMO.md) variable.
- `flag1`, `flag2` — configuration flags (purpose not established).

**Examples**

```
EXTWORLD^LINK(1,"ANNSZCZUREK",TRUE,TRUE);
EXTWORLD^LINK(IOBIEKT,["ANNSTRZAL_"+ISTRZAL],TRUE,TRUE);
```

**Compatibility:** `LINK` - type from a dynamically loaded library, outside the scope of the current `compat.json` export.

### LOAD

```
void LOAD(STRING path)
```

Loads a physics-world definition from an `.INE` file.

**Parameters**

- `path` — path to the `.INE` file.

**Examples**

```
EXTWORLD^LOAD("WORLD.INE");
```

**Compatibility:** `LOAD` - type from a dynamically loaded library, outside the scope of the current `compat.json` export.

### RESETTIMER

```
void RESETTIMER()
```

Resets the simulation's internal timer.

**Examples**

```
EXTWORLD^RESETTIMER();
```

**Compatibility:** `RESETTIMER` - type from a dynamically loaded library, outside the scope of the current `compat.json` export.

### SETGRAVITY

```
void SETGRAVITY(DOUBLE gravityX, DOUBLE gravityY)
```

Sets the global gravity vector. A value of `(0, 0)` disables gravity.

**Parameters**

- `gravityX`, `gravityY` — gravity components.

**Examples**

```
EXTWORLD^SETGRAVITY(0,0);
```

**Compatibility:** `SETGRAVITY` - type from a dynamically loaded library, outside the scope of the current `compat.json` export.

### SETLINEARDAMPING

```
void SETLINEARDAMPING(INTEGER objectId, INTEGER linearDamping)
```

Sets linear damping (gradual reduction of linear velocity) for a body.

**Parameters**

- `objectId` — body identifier.
- `linearDamping` — damping value.

**Examples**

```
EXTWORLD^SETLINEARDAMPING(1,300);
```

**Compatibility:** `SETLINEARDAMPING` - type from a dynamically loaded library, outside the scope of the current `compat.json` export.

### SETMATERIAL

```
void SETMATERIAL(INTEGER objectId, STRING material)
```

Sets the body's material. Materials control how bodies respond to contact (rigidity, elasticity, friction). One name encountered in shipping scripts is `"TRIGGER"`, for which the engine fires the `ONSIGNAL^TRIGGER` signal on the linked animation.

**Parameters**

- `objectId` — body identifier.
- `material` — material name.

**Examples**

```
EXTWORLD^SETMATERIAL(IOBIEKT,"TRIGGER");
```

**Compatibility:** `SETMATERIAL` - type from a dynamically loaded library, outside the scope of the current `compat.json` export.

### SETPOSITION

```
void SETPOSITION(INTEGER objectId, INTEGER posX, INTEGER posY)
```

Sets the body's absolute position in the physics world.

**Parameters**

- `objectId` — body identifier.
- `posX`, `posY` — new position.

**Examples**

```
EXTWORLD^SETPOSITION(IOBIEKT,[ANNSZCZUREK^GETCENTERX()+70],[ANNSZCZUREK^GETCENTERY()-1]);
EXTWORLD^SETPOSITION(IRAKIETAOBJ,ANNDODATKI_7^GETPOSITIONX(),ANNDODATKI_7^GETPOSITIONY());
```

**Compatibility:** `SETPOSITION` - type from a dynamically loaded library, outside the scope of the current `compat.json` export.

### SETVELOCITY

```
void SETVELOCITY(INTEGER objectId, INTEGER speedX, INTEGER speedY)
```

Sets a body's velocity along the X and Y axes.

**Parameters**

- `objectId` — body identifier.
- `speedX`, `speedY` — velocity components.

**Examples**

```
EXTWORLD^SETVELOCITY(1,0,0);
EXTWORLD^SETVELOCITY(IOBIEKT,8,0);
```

**Compatibility:** `SETVELOCITY` - type from a dynamically loaded library, outside the scope of the current `compat.json` export.

### TICK

```
void TICK()
```

Advances the simulation by one step. Without `TICK` the physics world stays frozen — typically called from a [`TIMER`](TIMER.md)'s [`ONTICK`](TIMER.md#ontick) signal.

**Examples**

```
EXTWORLD^TICK();
```

**Compatibility:** `TICK` - type from a dynamically loaded library, outside the scope of the current `compat.json` export.

### UNLINK

```
void UNLINK(INTEGER objectId)
```

Breaks an animation binding established with [`LINK`](#link).

**Parameters**

- `objectId` — body identifier.

**Examples**

```
EXTWORLD^UNLINK(IID);
EXTWORLD^UNLINK(1);
```

**Compatibility:** `UNLINK` - type from a dynamically loaded library, outside the scope of the current `compat.json` export.

## Signals

### ONINIT

Fired when the object is initialised.

### ONSIGNAL

Fired when a signal arrives (see [Events and signals](../engine/events.md#onsignal)).
