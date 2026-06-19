# CLASS

A class definition. The definition file has the `.class` extension and uses syntax similar to `.CNV` files. Instances (`INSTANCE`) are created from a class definition with [`NEW`](#new) and removed with [`DELETE`](#delete).

## Fields

### DEF

```
STRING DEF
```

Path to the class definition file. If the path does not start with `$`, it is prefixed with `$COMMON/classes/`.

### BASE

```
STRING BASE
```

The base class for inheritance. In the current emulator implementation the field is read but not used.

## Methods

### NEW

```
mixed NEW(STRING varName, [mixed param1, ..., mixed paramN])
```

Creates a new instance of the class with the name `varName`. The new variable is registered in the context where the class is declared (not in the caller's context) — the instance therefore survives scene changes when the class is declared at the application level.

After the instance is created, if the class definition file contains a procedure called `CONSTRUCTOR`, it is invoked with the arguments passed to `NEW` (with `varName` as `$1`).

**Parameters**

- `varName` — name of the new instance variable.
- `param1, …, paramN` — (optional) arguments forwarded to the `CONSTRUCTOR` procedure.

**Returns**: the value returned by `CONSTRUCTOR` or `NULL`.

**Examples**

```
MM^NEW("G_MENU");
CLSLOGOBJ^NEW("LOG", FALSE);
CLSEIFELENEMYOBJ^NEW("ENEMY0", "1_ENEMY0.ANN", 2, 5, 16, 4, 0, 2, 18);
CLSBDENEMYOBJ^NEW(["BDENEMY"+I2], _I_, I1, I2, IBDKRAINA);
```

### DELETE

```
mixed DELETE(STRING varName, [mixed param1, ..., mixed paramN])
```

Deletes the instance named `varName`. If the class definition contains a procedure called `DESTRUCTOR`, it is invoked with the arguments passed to `DELETE` (with `varName` as `$1`) before the variable is removed from its context.

**Parameters**

- `varName` — name of the instance to delete.
- `param1, …, paramN` — (optional) arguments forwarded to the `DESTRUCTOR` procedure.

**Returns**: the value returned by `DESTRUCTOR` or `NULL`.

## Signals

### ONINIT

Fired when the class variable is initialised.
