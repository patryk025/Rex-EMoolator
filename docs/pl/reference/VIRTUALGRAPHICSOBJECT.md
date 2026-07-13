# VIRTUALGRAPHICSOBJECT

Wirtualny obiekt graficzny — pełni rolę proxy lub kompozytu dla rzeczywistego obiektu wskazanego polem [`SOURCE`](#source). Pozwala traktować inny element graficzny jak osobną encję z własną pozycją, priorytetem, maską i flagami kolizji.

W skryptach gier ten typ pojawia się punktowo — głównie w *Reksio i Czarodzieje* (`common\classes\SinglePuzzle.class`).

## Pola

### ASBUTTON

```
BOOL ASBUTTON
```

Określa, czy obiekt ma być traktowany jako klikalny przycisk.

### MASK

```
STRING MASK
```

Nazwa zmiennej graficznej używanej jako maska wycinająca przy renderowaniu obiektu.

### MONITORCOLLISION

```
BOOL MONITORCOLLISION
```

Włącza monitorowanie kolizji z innymi obiektami graficznymi.

### MONITORCOLLISIONALPHA

```
BOOL MONITORCOLLISIONALPHA
```

Włącza monitorowanie kolizji z uwzględnieniem kanału przezroczystości — kolizja jest wykrywana tylko, gdy nakładające się piksele są nieprzezroczyste.

### PRIORITY

```
INTEGER PRIORITY
```

Priorytet rysowania (pozycja w osi Z).

### SOURCE

```
STRING SOURCE
```

Nazwa zmiennej graficznej, której zawartość jest renderowana przez obiekt wirtualny.

### TOCANVAS

```
BOOL TOCANVAS
```

Określa, czy obiekt jest rysowany na kanwie.

### VISIBLE

```
BOOL VISIBLE
```

Określa, czy obiekt jest widoczny.

## Metody

### GETHEIGHT

```
INTEGER GETHEIGHT()
```

Zwraca wysokość obiektu w pikselach.

**Zwraca**: [`INTEGER`](INTEGER.md) — wysokość.

**Kompatybilność:** `GETHEIGHT` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ❌, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### GETPOSITIONX

```
INTEGER GETPOSITIONX()
```

Zwraca pozycję X obiektu.

**Zwraca**: [`INTEGER`](INTEGER.md) — koordynata X.

**Kompatybilność:** `GETPOSITIONX` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ❌, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### GETPOSITIONY

```
INTEGER GETPOSITIONY()
```

Zwraca pozycję Y obiektu.

**Zwraca**: [`INTEGER`](INTEGER.md) — koordynata Y.

**Kompatybilność:** `GETPOSITIONY` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ❌, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### GETWIDTH

```
INTEGER GETWIDTH()
```

Zwraca szerokość obiektu w pikselach.

**Zwraca**: [`INTEGER`](INTEGER.md) — szerokość.

**Kompatybilność:** `GETWIDTH` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ❌, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### MOVE

```
void MOVE(INTEGER offsetX, INTEGER offsetY)
```

Przesuwa obiekt o zadane wartości względem aktualnej pozycji.

**Parametry**

- `offsetX`, `offsetY` — wektor przesunięcia.

**Przykłady**

```
VGO^MOVE($1,$2);
```

**Kompatybilność:** `MOVE` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ❌, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### SETMASK

```
void SETMASK(STRING maskName)
```

Ustawia maskę wycinającą — odpowiednik pola [`MASK`](#mask).

**Parametry**

- `maskName` — nazwa zmiennej graficznej pełniącej rolę maski.

**Przykłady**

```
VGO^SETMASK(MSK);
```

**Kompatybilność:** `SETMASK` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ❌, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### SETPOSITION

```
void SETPOSITION(INTEGER posX, INTEGER posY)
```

Ustawia bezwzględną pozycję obiektu.

**Parametry**

- `posX`, `posY` — nowa pozycja.

**Przykłady**

```
VGO^SETPOSITION($1,$2);
```

**Kompatybilność:** `SETPOSITION` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ❌, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### SETPRIORITY

```
void SETPRIORITY(INTEGER priority)
```

Ustawia priorytet rysowania (pozycję w osi Z) — odpowiednik pola [`PRIORITY`](#priority).

**Parametry**

- `priority` — nowa wartość priorytetu.

**Przykłady**

```
VGO^SETPRIORITY(1000);
```

**Kompatybilność:** `SETPRIORITY` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ❌, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### SETSOURCE

```
void SETSOURCE(STRING sourceName)
```

Ustawia zmienną graficzną wskazywaną przez pole [`SOURCE`](#source).

**Parametry**

- `sourceName` — nazwa zmiennej graficznej.

**Przykłady**

```
VGO^SETSOURCE($2);
```

**Kompatybilność:** `SETSOURCE` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ❌, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

## Sygnały

### ONINIT

Wywoływany w momencie inicjalizacji obiektu.

### ONSIGNAL

Wywoływany po otrzymaniu sygnału (zobacz [Zdarzenia i sygnały](../engine/events.md#onsignal)).
